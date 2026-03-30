package com.quintonc.vs_sails.compat;

import net.Gabou.projectatmosphere.manager.ForecastOrchestrator;
import net.Gabou.projectatmosphere.modules.core.WindVector;
import net.Gabou.projectatmosphere.modules.wind.RegionWindForecastApi;
import net.Gabou.projectatmosphere.modules.wind.WindForecastApi;
import net.Gabou.projectatmosphere.util.RegionInstanceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import weather2.util.WindReader;

public class ProjectAtmosphereCompat {

    public static double getWindStrength(Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return 0.0;
        }

        WindVector wind = ForecastOrchestrator.getWind(serverLevel, pos, serverLevel.getGameTime());
        return wind.baseSpeed() / 6;
    }

    public static double getWindDirection(Level world, Vec3 pos) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return 0.0;
        }

        BlockPos blockPos = BlockPos.containing(pos);
        WindVector wind = ForecastOrchestrator.getWind(serverLevel, blockPos, serverLevel.getGameTime());
        return Mth.wrapDegrees((float) Math.toDegrees(wind.angleRadians()) - 270);
    }
}
