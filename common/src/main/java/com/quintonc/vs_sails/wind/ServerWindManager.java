package com.quintonc.vs_sails.wind;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.networking.PacketHandler;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.*;


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

    private static final List<Integer> DIRECTIONS = Arrays.asList(
            225, // south-west
            90,  // east
            270, // west
            0,   // north
            180, // south
            315, // north-west
            45,  // north-east
            135  // south-east
    );

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
        trypruneStaleState(world);

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
                    ship.getDragController().setWindSpeed((float) state.strength, ValkyrienSails.MOD_ID);
                }
            }
        }
    }

    private static void runEffectPipeline(WindComputationContext ctx) {
        computeWindLegacy(ctx);
    }

    private static void computeWindLegacy(WindComputationContext ctx) {
        if (ctx.rule.dimensionMultiplier() > 0.0d) {
            if (ctx.rule.effects().randomStrengthVariation()) {
                if (ctx.random.nextBoolean()) {
                    ctx.timeInfluence = min(ctx.timeInfluence + (abs(ctx.timeInfluence) * 0.125d) + 0.01d, 1.0d);
                } else {
                    ctx.timeInfluence = max(ctx.timeInfluence - (abs(ctx.timeInfluence) * 0.125d) - 0.01d, -1.0d);
                }
                ctx.randomStrengthFactor = min(
                        max(ctx.randomStrengthFactor + (ctx.random.nextDouble() - 0.5d) * (1.0d - ctx.randomStrengthFactor), 0.0d),
                        0.99d
                );
            } else {
                ctx.timeInfluence = 0.5d;
                ctx.randomStrengthFactor = 0.25d;
            }

            if (ctx.rule.effects().randomDirectionVariation()) {
                ctx.randomDirectionOffset = min(
                        max(ctx.randomDirectionOffset + (ctx.random.nextDouble() - 0.5d) * 24.0d, -120.0d),
                        120.0d
                );
            } else {
                ctx.randomDirectionOffset = 0.0d;
            }

            if (ctx.rule.effects().weather() && (ctx.inputs.raining() || ctx.inputs.thundering())) {
                ctx.timeInfluence = 0.0d;
            }

            double timeFactor = ctx.rule.effects().dayNight()
                    ? sin(((double) ctx.inputs.dayTime() / 12000.0d) * Math.PI)
                    : 1.0d;

            ctx.strength = copySign(
                    (pow(abs(timeFactor), 0.44d) * ctx.timeInfluence
                        + abs(ctx.randomStrengthFactor) * (1.0d - ctx.timeInfluence)),
                    timeFactor
            );

            if (ctx.rule.effects().weather()) {
                if (ctx.inputs.thundering()) {
                    ctx.strength *= 2.0d;
                } else if (ctx.inputs.raining()) {
                    ctx.strength *= 1.5d;
                }
            }

            ctx.strength /= 2.0d;
            ctx.strength *= ctx.rule.dimensionMultiplier();

            if (ctx.rule.direction().type() == WindType.FIXED) {
                ctx.direction = ctx.rule.fixedDirection();
            } else if (ctx.rule.direction().type() == WindType.DEFAULT) {
                if (ctx.rule.effects().moonPhase()) {
                    ctx.direction = DIRECTIONS.get(ctx.inputs.moonPhase());
                }
                ctx.direction += 12.0d * ctx.strength;
                ctx.direction += 12.0d;
                ctx.direction += ctx.randomDirectionOffset;
                ctx.direction = normalizeDegrees(ctx.direction);
            } else if (ctx.rule.direction().type() == WindType.RADIAL) {
                ctx.direction = normalizeDegrees(ctx.randomDirectionOffset);
            }
        } else {
            ctx.timeInfluence = 0.5d;
            ctx.randomStrengthFactor = 0.25d;
            ctx.randomDirectionOffset = 0.0d;
            ctx.strength = 0.0d;
            ctx.direction = 0.0d;
        }
    }

    private static void trypruneStaleState(ServerLevel world) {
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
