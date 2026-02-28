package com.quintonc.vs_sails.wind;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.networking.PacketHandler;
import com.quintonc.vs_sails.wind.contributors.*;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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

    private static DimensionWindState getOrCreateState(ServerLevel level) {
        return WIND_STATE_BY_DIMENSION.computeIfAbsent(level.dimension(), key -> new DimensionWindState());
    }

    private static DimensionWindState getStateIfPresent(ServerLevel level) {
        return WIND_STATE_BY_DIMENSION.get(level.dimension());
    }

    public static float getCachedStrength(ServerLevel level) {
        DimensionWindState state = getStateIfPresent(level);
        return state != null ? state.strength : 0.0f;
    }

    public static float getCachedDirection(ServerLevel level) {
        DimensionWindState state = getStateIfPresent(level);
        return state != null ? state.direction : 0.0f;
    }

    private static void clearAllState() {
        WIND_STATE_BY_DIMENSION.clear();
    }

    //private static final float minWindSpeed = Float.parseFloat(ConfigUtils.config.getOrDefault("min-wind-speed","0.2"));

    //private static int gustiness = 10; //fixme finish
    //private static int shear = 10; //fixme finish

    public static void InitializeWind() {
        clearAllState();
        windGustiness = 0.125f; //between 0 and 1: 1 is
        lastPruneServerTick = 0;
        random = new Random();

        if (!tickHookRegistered) {
            TickEvent.SERVER_LEVEL_PRE.register(ServerWindManager::onWorldTick);
            tickHookRegistered = true;
        }
    }

    private static void onWorldTick(ServerLevel world) {
        DimensionWindState state = getOrCreateState(world);
        WindRuleWind rule = WindRuleRegistry.getWind(world);
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
        setWindForLevel(world, state.strength, state.direction);
        sendWindPackets(world, rule, state.strength, state.direction);

        if (Boolean.parseBoolean(ConfigUtils.config.getOrDefault("enable-aerodynamic-wind", "true"))) {
            for (LoadedServerShip ship : VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips()) {
                if (ship.getDragController() != null) {
                    Vec3 shipPos = new Vec3(
                            ship.getTransform().getPositionInWorld().x(),
                            ship.getTransform().getPositionInWorld().y(),
                            ship.getTransform().getPositionInWorld().z()
                    );
                    double effectiveDirection = resolveDirectionForPosition(rule, shipPos, state.direction);
                    ship.getDragController().setWindDirection(
                            new Vector3d(0,0,-1).rotateY(Math.toRadians(effectiveDirection)),
                            ValkyrienSails.MOD_ID
                    );
                    ship.getDragController().setWindSpeed(state.strength, ValkyrienSails.MOD_ID);
                }
            }
        }
    }

    private static final List<WindEffectContributor> STRENGTH_CONTRIBUTORS = List.of(
            RandomStrengthContributor.INSTANCE,
            WeatherStrengthPrepContributor.INSTANCE,
            BaseDayNightStrengthContributor.INSTANCE,
            WeatherStrengthAmplifierContributor.INSTANCE
    );

    private static final List<WindEffectContributor> DEFAULT_DIRECTION_CONTRIBUTORS = List.of(
            RandomDirectionVariationContributor.INSTANCE,
            MoonDirectionContributor.INSTANCE,
            DefaultDirectionFromStrengthContributor.INSTANCE
    );

    private static final List<WindEffectContributor> RADIAL_DIRECTION_CONTRIBUTORS = List.of(
            RandomDirectionVariationContributor.INSTANCE,
            RadialDirectionContributor.INSTANCE
    );

    private static final List<WindEffectContributor> STRENGTH_FINALIZATION_CONTRIBUTORS = List.of(
            StrengthFinalizationContributor.INSTANCE
    );

    private static final List<WindEffectContributor> DIRECTION_FINALIZATION_CONTRIBUTORS = List.of(
            DirectionFinalizationContributor.INSTANCE
    );

    private static void applyDirectionContributors(WindComputationContext ctx) {
        if (ctx.rule().direction().type() == WindType.FIXED) {
            ctx.setDirection(ctx.rule().fixedDirection());
            return;
        }

        if (ctx.rule().direction().type() == WindType.DEFAULT) {
            applyContributors(DEFAULT_DIRECTION_CONTRIBUTORS, ctx);
            return;
        }

        applyContributors(RADIAL_DIRECTION_CONTRIBUTORS, ctx);
    }

    private static void applyContributors(List<WindEffectContributor> contributors, WindComputationContext ctx) {
        for (WindEffectContributor contributor : contributors) {
            contributor.apply(ctx);
        }
    }

    private static void runEffectPipeline(WindComputationContext ctx) {
        computeWindLegacy(ctx);
    }

    private static void computeWindLegacy(WindComputationContext ctx) {
        if (ctx.dimensionMultiplier() <= 0.0d) {
            ctx.setNoWind();
            return;
        }

        applyContributors(STRENGTH_CONTRIBUTORS, ctx);
        applyContributors(STRENGTH_FINALIZATION_CONTRIBUTORS, ctx);

        applyDirectionContributors(ctx);
        applyContributors(DIRECTION_FINALIZATION_CONTRIBUTORS, ctx);
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

    private static void sendWindPackets(ServerLevel world, WindRuleWind rule, float strength, float defaultDirection) {
        for (ServerPlayer player : world.players()) {
            double effectiveDirection = resolveDirectionForPosition(rule, player.position(), defaultDirection);
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeFloat(strength);
            buf.writeFloat((float) effectiveDirection);
            NetworkManager.sendToPlayer(player, PacketHandler.WIND_DATA_PACKET, buf);
        }
    }

    private static double resolveDirectionForPosition(WindRuleWind rule, Vec3 position, double defaultDirection) {
        if (rule.direction().type() == WindType.FIXED) {
            return rule.fixedDirection();
        }
        if (rule.direction().type() == WindType.RADIAL) {
            return normalizeDegrees(Math.toDegrees(Math.atan2(position.z, position.x)) + defaultDirection);
        }
        return defaultDirection;
    }

    private static double normalizeDegrees(double degrees) {
        return Mth.positiveModulo((float) degrees, 360.0f);
    }
}
