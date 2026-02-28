package com.quintonc.vs_sails.wind;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.networking.PacketHandler;
import com.quintonc.vs_sails.networking.WindDataPacket;
import com.quintonc.vs_sails.wind.contributors.*;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ServerWindManager extends WindManager {

    private static final Map<ResourceKey<Level>, DimensionWindState> WIND_STATE_BY_DIMENSION = new HashMap<>();

    private static boolean tickHookRegistered;
    private static Random random;

    private static int lastPruneServerTick = 0;
    private static final int PRUNE_INTERVAL_TICKS = 300;

    private static final List<WindEffectContributor> WIND_STRENGTH_PIPELINE = List.of(
            RandomStrengthContributor.INSTANCE,
            WeatherStrengthPrepContributor.INSTANCE,
            BaseDayNightStrengthContributor.INSTANCE,
            WeatherStrengthAmplifierContributor.INSTANCE,
            StrengthFinalizationContributor.INSTANCE
    );

    private static final Map<WindType, List<WindEffectContributor>> WIND_DIRECTION_PIPELINE = Map.of(
            WindType.FIXED, List.of(
                    FixedDirectionContributor.INSTANCE,
                    DirectionFinalizationContributor.INSTANCE
            ),
            WindType.DEFAULT, List.of(
                    RandomDirectionVariationContributor.INSTANCE,
                    MoonDirectionContributor.INSTANCE,
                    DefaultDirectionFromStrengthContributor.INSTANCE,
                    DirectionFinalizationContributor.INSTANCE
            ),
            WindType.RADIAL, List.of(
                    RandomDirectionVariationContributor.INSTANCE,
                    RadialDirectionContributor.INSTANCE,
                    DirectionFinalizationContributor.INSTANCE
            )
    );

    //private static final float minWindSpeed = Float.parseFloat(ConfigUtils.config.getOrDefault("min-wind-speed","0.2"));

    //private static int gustiness = 10; //fixme finish
    //private static int shear = 10; //fixme finish

    public static void InitializeWind() {
        clearAllState();
        lastPruneServerTick = 0;
        random = new Random();

        if (!tickHookRegistered) {
            TickEvent.SERVER_LEVEL_PRE.register(ServerWindManager::onWorldTick);
            tickHookRegistered = true;
        }
    }

    public static float getWindDirectionAtPosition(ServerLevel level, Vec3 pos) {
        DimensionWindState state = getStateIfPresent(level);
        if (state == null) return 0.0f;

        return (float) resolveDirectionForPosition(
                state.windType,
                state.baseDirection,
                pos,
                state.direction
        );
    }

    public static float getCachedStrength(ServerLevel level) {
        DimensionWindState state = getStateIfPresent(level);
        return state != null ? state.strength : 0.0f;
    }

    private static void onWorldTick(ServerLevel world) {
        DimensionWindState state = getOrCreateState(world);
        WindRuleWind rule = WindRuleRegistry.getWind(world);

        state.windType = rule.direction().type();
        state.baseDirection = rule.baseDirection();

        tryPruneStaleState(world);

        if (state.tickCounter >= rule.windInterval() - 1) {
            state.tickCounter = 0;
            updateWind(world, rule, state);
        } else {
            state.tickCounter++;
        }
    }

    private static void updateWind(ServerLevel world, WindRuleWind rule, DimensionWindState state) {
        WindComputationContext ctx = new WindComputationContext(world, rule, state, random);

        runEffectPipeline(ctx);

        ctx.flushToState();

        boolean windEnabled = rule.dimensionMultiplier() > 0.0d && rule.direction().type() != WindType.NO_WIND;

        setWindForDimension(world.dimension().location(), state.strength, state.direction, state.windType, windEnabled);
        sendWindPackets(world, state.windType, state.strength, state.direction, windEnabled);

        if (Boolean.parseBoolean(ConfigUtils.config.getOrDefault("enable-aerodynamic-wind", "true"))) {
            for (LoadedServerShip ship : VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips()) {
                if (ship.getDragController() != null) {
                    Vec3 shipPos = new Vec3(
                            ship.getTransform().getPositionInWorld().x(),
                            ship.getTransform().getPositionInWorld().y(),
                            ship.getTransform().getPositionInWorld().z()
                    );
                    double effectiveDirection = resolveDirectionForPosition(state.windType, state.baseDirection, shipPos, state.direction);
                    ship.getDragController().setWindDirection(
                            new Vector3d(0,0,-1).rotateY(Math.toRadians(effectiveDirection)),
                            ValkyrienSails.MOD_ID
                    );
                    ship.getDragController().setWindSpeed(state.strength, ValkyrienSails.MOD_ID);
                }
            }
        }
    }

    private static void runEffectPipeline(WindComputationContext ctx) {
        if (ctx.rule().direction().type() == WindType.NO_WIND || ctx.dimensionMultiplier() <= 0.0d) {
            ctx.setNoWind();
            return;
        }

        applyContributors(WIND_STRENGTH_PIPELINE, ctx);

        List<WindEffectContributor> directionPipeline = WIND_DIRECTION_PIPELINE.getOrDefault(
                ctx.rule().direction().type(),
                WIND_DIRECTION_PIPELINE.get(WindType.DEFAULT)
        );
        applyContributors(directionPipeline, ctx);
    }

    private static void applyContributors(List<WindEffectContributor> contributors, WindComputationContext ctx) {
        for (WindEffectContributor contributor : contributors) {
            contributor.apply(ctx);
        }
    }

    private static DimensionWindState getOrCreateState(ServerLevel level) {
        return WIND_STATE_BY_DIMENSION.computeIfAbsent(level.dimension(), key -> new DimensionWindState());
    }

    private static DimensionWindState getStateIfPresent(ServerLevel level) {
        return WIND_STATE_BY_DIMENSION.get(level.dimension());
    }

    private static void clearAllState() {
        WIND_STATE_BY_DIMENSION.clear();
    }

    private static void tryPruneStaleState(ServerLevel world) {
        int serverTick = world.getServer().getTickCount(); // or equivalent in your mappings
        if (serverTick - lastPruneServerTick >= PRUNE_INTERVAL_TICKS) {
            pruneStaleState(world);
            lastPruneServerTick = serverTick;
        }
    }

    private static void pruneStaleState(ServerLevel world) {
        WIND_STATE_BY_DIMENSION.keySet().removeIf(key -> world.getServer().getLevel(key) == null);
    }

    private static void sendWindPackets(ServerLevel world, WindType windType, float strength, float defaultDirection, boolean windEnabled) {
        ResourceLocation dimensionId = world.dimension().location();
        for (ServerPlayer player : world.players()) {
            WindDataPacket packet = new WindDataPacket(dimensionId, windType, strength, defaultDirection, windEnabled);
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.encode(buf);

            NetworkManager.sendToPlayer(player, PacketHandler.WIND_DATA_PACKET, buf);
        }
    }

    private static double resolveDirectionForPosition(WindType windType, double baseDirection, Vec3 position, double defaultDirection) {
        if (windType == WindType.NO_WIND) return 0.0d;
        if (windType == WindType.FIXED) return baseDirection;
        if (windType == WindType.RADIAL && position != null) {
            return normalizeDegrees(Math.toDegrees(Math.atan2(position.z, position.x)) + defaultDirection);
        }
        return defaultDirection;
    }
}
