package com.quintonc.vs_sails;

import com.quintonc.vs_sails.compat.Weather2Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WindManager {
    public static float windDirection;
    public static float windStrength;
    protected static float windGustiness;
    protected static float windShear;

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
