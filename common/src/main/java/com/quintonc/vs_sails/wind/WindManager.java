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
    private static final Map<ResourceLocation, Float> WIND_STRENGTH_BY_DIMENSION = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Float> WIND_DIRECTION_BY_DIMENSION = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, WindType> WIND_TYPE_BY_DIMENSION = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, Boolean> WIND_ENABLED_BY_DIMENSION = new ConcurrentHashMap<>();

    public enum WindType {
        NO_WIND,
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
                        "Invalid wind type '" + value + "'. Expected DEFAULT, RADIAL, FIXED, or NO_WIND");
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
    public record WindRuleWind(double dimensionMultiplier, WindDirectionSpec direction, double baseDirection, int windInterval, WindRuleEffects effects) {}
    public record WindRuleTargets(List<String> dimensions, List<String> dimensionTags) {}
    public record WindRuleData(int priority, WindRuleTargets targets, WindRuleWind wind) {}

    public static void setWindForDimension(
            ResourceLocation dimensionId,
            float windStrength,
            float windDirection,
            WindType windType,
            boolean windEnabled
    ) {
        if (dimensionId != null) {
            WIND_STRENGTH_BY_DIMENSION.put(dimensionId, windStrength);
            WIND_DIRECTION_BY_DIMENSION.put(dimensionId, windDirection);
            WIND_TYPE_BY_DIMENSION.put(dimensionId, windType);
            WIND_ENABLED_BY_DIMENSION.put(dimensionId, windEnabled);
        }
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
                    WindType.CODEC.fieldOf("type").forGetter(WindDirectionSpec::type)
            ).apply(instance, WindDirectionSpec::new)
    );

    public static final Codec<WindRuleEffects> WIND_EFFECTS_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleEffects> instance) -> instance.group(
                    Codec.BOOL.fieldOf("weather").forGetter(WindRuleEffects::weather),
                    Codec.BOOL.fieldOf("day_night").forGetter(WindRuleEffects::dayNight),
                    Codec.BOOL.fieldOf("moon_phase").forGetter(WindRuleEffects::moonPhase),
                    Codec.BOOL.fieldOf("random_strength_variation").forGetter(WindRuleEffects::randomStrengthVariation),
                    Codec.BOOL.fieldOf("random_direction_variation").forGetter(WindRuleEffects::randomDirectionVariation)
            ).apply(instance, WindRuleEffects::new
            )
    );

    public static final Codec<WindRuleWind> WIND_RULE_WIND_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleWind> instance) -> instance.group(
                    Codec.DOUBLE.fieldOf("dimension_multiplier").forGetter(WindRuleWind::dimensionMultiplier),
                    WIND_DIRECTION_CODEC.fieldOf("direction").forGetter(WindRuleWind::direction),
                    Codec.DOUBLE.fieldOf("base_direction").forGetter(WindRuleWind::baseDirection),
                    Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("wind_interval", 300).forGetter(WindRuleWind::windInterval),
                    WIND_EFFECTS_CODEC.fieldOf("effects").forGetter(WindRuleWind::effects)
            ).apply(instance, WindRuleWind::new)
    ).comapFlatMap(WindManager::validateWind, wind -> wind);

    public static final Codec<WindRuleTargets> WIND_RULE_TARGETS_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleTargets> instance) -> instance.group(
                    DIMENSIONS_CODEC.fieldOf("dimensions").forGetter(WindRuleTargets::dimensions),
                    DIMENSION_TAGS_CODEC.fieldOf("dimension_tags").forGetter(WindRuleTargets::dimensionTags)
            ).apply(instance, WindRuleTargets::new
            )
    ).comapFlatMap(WindManager::validateTargets, targets -> targets);

    public static final Codec<WindRuleData> WIND_RULE_CODEC = RecordCodecBuilder.create(
            (RecordCodecBuilder.Instance<WindRuleData> instance) -> instance.group(
                    Codec.INT.fieldOf("priority").forGetter(WindRuleData::priority),
                    WIND_RULE_TARGETS_CODEC.fieldOf("targets").forGetter(WindRuleData::targets),
                    WIND_RULE_WIND_CODEC.fieldOf("wind").forGetter(WindRuleData::wind)
            ).apply(instance, WindRuleData::new
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

    public static void clearWindData() {
        WIND_STRENGTH_BY_DIMENSION.clear();
        WIND_DIRECTION_BY_DIMENSION.clear();
        WIND_TYPE_BY_DIMENSION.clear();
        WIND_ENABLED_BY_DIMENSION.clear();
    }

    private static DataResult<WindRuleWind> validateWind(WindRuleWind wind) {
        if (!Double.isFinite(wind.dimensionMultiplier())) {
            return DataResult.error(() -> "'dimension_multiplier' must be a finite number");
        }
        if (!Double.isFinite(wind.baseDirection())) {
            return DataResult.error(() -> "'base_direction' must be a finite number");
        }

        double clampedMultiplier = Math.max(0.0d, wind.dimensionMultiplier());
        int clampedWindInterval = Math.max(1, wind.windInterval());

        double normalizedBaseDirection = normalizeDegrees(wind.baseDirection());

        return DataResult.success(new WindRuleWind(
                clampedMultiplier,
                wind.direction(),
                normalizedBaseDirection,
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

    static double normalizeDegrees(double degrees) {
        return Mth.positiveModulo((float) degrees, 360.0f);
    }

    public static boolean isWindEnabled(Level world) {
        if (world instanceof ServerLevel serverLevel) {
            WindRuleWind rule = WindRuleRegistry.getWind(serverLevel);
            return rule.dimensionMultiplier() > 0.0d && rule.direction().type() != WindType.NO_WIND;
        }

        if (world != null) {
            ResourceLocation dimensionId = world.dimension().location();
            return WIND_ENABLED_BY_DIMENSION.getOrDefault(dimensionId, false);
        }

        return false;
    }

    public static boolean isWindEnabled(ResourceLocation dimensionId) {
        return dimensionId != null && WIND_ENABLED_BY_DIMENSION.getOrDefault(dimensionId, false);
    }

    public static float getWindDirection(Level world, Vec3 pos) {
        if (ValkyrienSails.weather2 && world != null) {
            return (float) Weather2Compat.getWindDirection(world, pos);
        }
        if (world instanceof ServerLevel serverLevel) {
            return ServerWindManager.getWindDirectionAtPosition(serverLevel, pos);
        }

        if (world != null) {
            ResourceLocation dimensionId = world.dimension().location();
            WindType windType = WIND_TYPE_BY_DIMENSION.getOrDefault(dimensionId, WindType.DEFAULT);

            float baseDirection = WIND_DIRECTION_BY_DIMENSION.getOrDefault(dimensionId, 0.0f);
            if (windType == WindType.NO_WIND) return 0.0f;
            if (windType == WindType.RADIAL && pos != null) {
                return (float) normalizeDegrees(Math.toDegrees(Math.atan2(pos.z, pos.x)) + baseDirection);
            }
            return baseDirection;
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
