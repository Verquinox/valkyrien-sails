package com.quintonc.vs_sails.wind;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.compat.Weather2Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WindManager {
    protected static float windGustiness;
    protected static float windShear;
    private static final Map<ResourceLocation, Float> WIND_STRENGTH_BY_DIMENSION = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Float> WIND_DIRECTION_BY_DIMENSION = new ConcurrentHashMap<>();
    
    public enum WindType {
        DEFAULT,
        RADIAL,
        FIXED;

        public static final Codec<WindType> CODEC = Codec.STRING.comapFlatMap(
                WindType::parse,
                WindType::name
        );

        private static DataResult<WindType> parse(String value) {
            try {
                return DataResult.success(WindType.valueOf(value.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                return DataResult.error(() ->
                        "Invalid wind type '" + value + "'. Expected DEFAULT, RADIAL, or FIXED");
            }
        }
    }

    public record WindDirectionSpec(WindType type) {}
    public record WindRuleEffects(
            boolean weather,
            boolean dayNight,
            boolean moonPhase,
            boolean randomStrengthVariation,
            boolean randomDirectionVariation
    ) {}
    public record WindRuleWind(double dimensionMultiplier, WindDirectionSpec direction, double fixedDirection, int windInterval, WindRuleEffects effects) {}
    public record WindRuleTargets(List<String> dimensions, List<String> dimensionTags) {}
    public record WindRuleData(int priority, WindRuleTargets targets, WindRuleWind wind) {}

    public interface WindDirectionResolver {
        float resolve(Level level, Vec3 samplePos, float defaultDirection, WindDirectionSpec spec);
    }

    private static final Codec<List<String>> DIMENSIONS_CODEC = Codec.STRING.listOf().comapFlatMap(
            ids -> validateTargetIds(ids, "targets.dimensions"),
            ids -> ids
    );

    private static final Codec<List<String>> DIMENSION_TAGS_CODEC = Codec.STRING.listOf().comapFlatMap(
            ids -> validateTargetIds(ids, "targets.dimension_tags"),
            ids -> ids
    );

    public static final Codec<WindDirectionSpec> WIND_DIRECTION_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindDirectionSpec> instance) -> instance.group(
                    WindType.CODEC.fieldOf("type").forGetter(spec -> spec.type())
            ).apply(instance, type -> new WindDirectionSpec(type))
    );

    public static final Codec<WindRuleEffects> WIND_EFFECTS_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleEffects> instance) -> instance.group(
                    Codec.BOOL.fieldOf("weather").forGetter(effects -> effects.weather()),
                    Codec.BOOL.fieldOf("day_night").forGetter(effects -> effects.dayNight()),
                    Codec.BOOL.fieldOf("moon_phase").forGetter(effects -> effects.moonPhase()),
                    Codec.BOOL.fieldOf("random_strength_variation").forGetter(effects -> effects.randomStrengthVariation()),
                    Codec.BOOL.fieldOf("random_direction_variation").forGetter(effects -> effects.randomDirectionVariation())
            ).apply(instance, (weather, dayNight, moonPhase, randomStrengthVariation, randomDirectionVariation) ->
                    new WindRuleEffects(
                            weather,
                            dayNight,
                            moonPhase,
                            randomStrengthVariation,
                            randomDirectionVariation
                    )
            )
    );

    public static final Codec<WindRuleWind> WIND_RULE_WIND_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleWind> instance) -> instance.group(
                    Codec.DOUBLE.fieldOf("dimension_multiplier").forGetter(wind -> wind.dimensionMultiplier()),
                    WIND_DIRECTION_CODEC.fieldOf("direction").forGetter(wind -> wind.direction()),
                    Codec.DOUBLE.fieldOf("fixed_direction").forGetter(wind -> wind.fixedDirection()),
                    Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("wind_interval", 300).forGetter(wind -> wind.windInterval()),
                    WIND_EFFECTS_CODEC.fieldOf("effects").forGetter(wind -> wind.effects())
            ).apply(instance, (dimensionMultiplier, direction, fixedDirection, windInterval, effects) ->
                    new WindRuleWind(dimensionMultiplier, direction, fixedDirection, windInterval, effects)
            )
    ).comapFlatMap(WindManager::validateWind, wind -> wind);

    public static final Codec<WindRuleTargets> WIND_RULE_TARGETS_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleTargets> instance) -> instance.group(
                    DIMENSIONS_CODEC.fieldOf("dimensions").forGetter(targets -> targets.dimensions()),
                    DIMENSION_TAGS_CODEC.fieldOf("dimension_tags").forGetter(targets -> targets.dimensionTags())
            ).apply(instance, (dimensions, dimensionTags) ->
                    new WindRuleTargets(dimensions, dimensionTags)
            )
    ).comapFlatMap(WindManager::validateTargets, targets -> targets);

    public static final Codec<WindRuleData> WIND_RULE_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleData> instance) -> instance.group(
                    Codec.INT.fieldOf("priority").forGetter(data -> data.priority()),
                    WIND_RULE_TARGETS_CODEC.fieldOf("targets").forGetter(data -> data.targets()),
                    WIND_RULE_WIND_CODEC.fieldOf("wind").forGetter(data -> data.wind())
            ).apply(instance, (priority, targets, wind) ->
                    new WindRuleData(priority, targets, wind)
            )
    );

    public static DataResult<WindRuleData> parseWindRule(JsonElement json) {
        return WIND_RULE_CODEC.parse(JsonOps.INSTANCE, json);
    }

    public static WindRuleData sanitizeLoadedRule(
            ResourceLocation sourceId,
            WindRuleData rule,
            Set<ResourceLocation> knownDimensionIds
    ) {
        for (String dimensionId : rule.targets().dimensions()) {
            ResourceLocation parsedId = ResourceLocation.tryParse(dimensionId);
            if (parsedId != null && !knownDimensionIds.contains(parsedId)) {
                ValkyrienSails.LOGGER.warn(
                        "Wind rule '{}' targets unknown dimension id '{}'; keeping rule target as-is",
                        sourceId,
                        dimensionId
                );
            }
        }
        return rule;
    }

    public static void setWindForLevel(Level world, float strength, float direction) {
        if (world != null) {
            ResourceLocation id = world.dimension().location();
            WIND_STRENGTH_BY_DIMENSION.put(id, strength);
            WIND_DIRECTION_BY_DIMENSION.put(id, direction);
        }
    }

    public static void clearWindData() {
        WIND_STRENGTH_BY_DIMENSION.clear();
        WIND_DIRECTION_BY_DIMENSION.clear();
    }

    private static DataResult<WindRuleWind> validateWind(WindRuleWind wind) {
        if (!Double.isFinite(wind.dimensionMultiplier())) {
            return DataResult.error(() -> "'dimension_multiplier' must be a finite number");
        }
        if (!Double.isFinite(wind.fixedDirection())) {
            return DataResult.error(() -> "'fixed_direction' must be a finite number");
        }

        double clampedMultiplier = Math.max(0.0d, wind.dimensionMultiplier());
        int clampedWindInterval = Math.max(1, wind.windInterval());

        double normalized = normalizeDegrees(wind.fixedDirection());
        double sanitizedFixedDirection = wind.direction().type() == WindType.FIXED ? normalized : 0.0d;

        return DataResult.success(new WindRuleWind(
                clampedMultiplier,
                wind.direction(),
                sanitizedFixedDirection,
                clampedWindInterval,
                wind.effects()
        ));
    }

    private static DataResult<List<String>> validateTargetIds(List<String> ids, String fieldName) {
        Set<String> seen = new HashSet<>();
        for (String id : ids) {
            if (ResourceLocation.tryParse(id) == null) {
                return DataResult.error(() -> "Invalid id in " + fieldName + ": '" + id + "'");
            }
            if (!seen.add(id)) {
                return DataResult.error(() -> "Duplicate id in " + fieldName + ": '" + id + "'");
            }
        }
        return DataResult.success(ids);
    }

    private static DataResult<WindRuleTargets> validateTargets(WindRuleTargets targets) {
        if (targets.dimensions().isEmpty() && targets.dimensionTags().isEmpty()) {
            return DataResult.error(() ->
                    "At least one target is required in 'dimensions' or 'dimension_tags'");
        }
        return DataResult.success(targets);
    }

    private static double normalizeDegrees(double degrees) {
        return Mth.positiveModulo((float) degrees, 360.0f);
    }

    public static float getWindDirection(Level world, Vec3 pos) {
        if (ValkyrienSails.weather2 && world != null) {
            return (float) Weather2Compat.getWindDirection(world, pos);
        }
        if (world instanceof ServerLevel serverLevel) {
            WindRuleWind rule = WindRuleRegistry.getWind(serverLevel);
            if (rule.direction().type() == WindType.FIXED) {
                return (float) rule.fixedDirection();
            }
            if (rule.direction().type() == WindType.RADIAL && pos != null) {
                float offset = ServerWindManager.getCachedDirection((serverLevel));
                return (float) normalizeDegrees(Math.toDegrees(Math.atan2(pos.z, pos.x)) + offset);
            }
            return ServerWindManager.getCachedDirection(serverLevel);
        }

        if (world != null) {
            return WIND_DIRECTION_BY_DIMENSION.getOrDefault(world.dimension().location(), 0.0f);
        }

        return 0.0f;
    }

    public static float getWindStrength(Level world, BlockPos pos) {
        if (ValkyrienSails.weather2 && world != null) {
            return (float) Weather2Compat.getWindStrength(world, pos);
        }
        if (world instanceof ServerLevel serverLevel) {
            return ServerWindManager.getCachedStrength(serverLevel);
        }

        if (world != null) {
            return WIND_STRENGTH_BY_DIMENSION.getOrDefault(world.dimension().location(), 0.0f);
        }

        return 0.0f;
    }
}
