package com.quintonc.vs_sails;

import com.quintonc.vs_sails.networking.PacketHandler;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;


public class ServerWindManager extends WindManager {
    private static int tickCount = 0;
    private static double timeInfluence = 0.5;
    private static double randomFactor = 0.25;
    private static Random random;

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

    public static void InitializeWind(ServerLevel world) {
        windStrength = 0;
        windDirection = 0;
        windGustiness = 0.125f; //between 0 and 1: 1 is
        random = new Random();

        System.out.println("ServerWindManager Init");
        //ServerTickEvents.START_WORLD_TICK.register(ServerWindManager::onWorldTick);
        TickEvent.SERVER_LEVEL_PRE.register(ServerWindManager::onWorldTick);
    }

    private static void onWorldTick(ServerLevel world) {
        if(tickCount == 299) {
            tickCount = 0;
            if (random.nextBoolean()) {
                timeInfluence = min(timeInfluence+((abs(timeInfluence)*0.125))+0.01, 1);
            } else {
                timeInfluence = max(timeInfluence-((abs(timeInfluence)*0.125))-0.01, -1);
            }
            updateWind(world);
        } else {
            tickCount++;
        }
    }

    private static void updateWind(ServerLevel world) {
        //float gust = random.nextFloat(windGustiness) - (float)(windGustiness*0.5);
        double timeFactor = sin(((double)world.getDayTime() / 12000) * Math.PI);
        randomFactor = min(max(randomFactor + (random.nextDouble() - 0.5) * (1 - randomFactor), 0), 0.99);

        if (world.getServer().overworld().isRaining() || world.getServer().overworld().isThundering()) {
            timeInfluence = 0;
        }

        windStrength = (float) copySign((pow(abs(timeFactor),0.44) * timeInfluence + abs(randomFactor) * (1 - timeInfluence)),timeFactor);
        //windStrength = (float) sin(sin(((double)world.getTimeOfDay() / 12000) * Math.PI))

        windDirection = DIRECTIONS.get(world.getServer().overworld().getMoonPhase());
        windDirection += 12 * windStrength;
        windDirection += 12;

        if (world.getServer().overworld().isThundering()){
            windStrength *= 2f;
        } else if (world.getServer().overworld().isRaining()){
            windStrength *= 1.5f;
        }
        windStrength /= 2;

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        world.getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            if (serverPlayerEntity.serverLevel().dimensionTypeId() == BuiltinDimensionTypes.OVERWORLD) {

                buf.writeFloat(windStrength);
                buf.writeFloat(windDirection);
                NetworkManager.sendToPlayers(world.getServer().getPlayerList().getPlayers(), PacketHandler.WIND_DATA_PACKET, buf);

//                System.out.println("Sending packet to " + serverPlayerEntity.getScoreboardName());
//                System.out.println("windStrength: " + windStrength + " timeInfluence: " + timeInfluence);
//                System.out.println("timeFactor: " + timeFactor + " randomFactor: " + randomFactor);
//                System.out.println("windDirection: " + windDirection);
            }
        });
    }



}
