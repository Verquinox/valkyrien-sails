package com.quintonc.vs_sails;

import com.quintonc.vs_sails.compat.Weather2Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WindManager {
    public static float windDirection;
    public static float windStrength;
    protected static float windGustiness;
    protected static float windShear;
    public enum WindType { DEFAULT, RADIAL, FIXED }

    public record WindDirectionSpec(WindType type, Double degrees) {}
    public record WindRuleEffects(boolean weather, boolean dayNight, boolean moonPhase, boolean randomVariation) {}
    public record WindRuleWind(double dimensionMultiplier, WindDirectionSpec direction, WindRuleEffects effects) {}
    public record WindRuleTargets(List<String> dimensions, List<String> dimensionTags) {}
    public record WindRuleData(int priority, WindRuleTargets targets, WindRuleWind wind) {}

    public interface WindDirectionResolver {
        float resolve(Level level, Vec3 samplePos, float defaultDirection, WindDirectionSpec spec);
    }

    public static float getWindDirection(Level world, Vec3 pos) {
        if (ValkyrienSails.weather2 && world != null) {
            return (float) Weather2Compat.getWindDirection(world, pos);
        }
        return windDirection;
    }

    public static float getWindStrength(Level world, BlockPos pos) {
        if (ValkyrienSails.weather2 && world != null) {
            return (float) Weather2Compat.getWindStrength(world, pos);
        }
        return windStrength;
    }
}
