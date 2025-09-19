package com.quintonc.vs_sails.compat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import weather2.weathersystem.WeatherManagerServer;
import weather2.util.WindReader;
import net.minecraft.world.level.Level;

public class Weather2Compat {

    public static double getWindStrength(Level world, BlockPos pos) {
        return WindReader.getWindSpeed(world, pos);
    }

    public static double getWindDirection(Level world, Vec3 pos) {
        return (WindReader.getWindAngle(world, pos)-270) % 360;
    }
}
