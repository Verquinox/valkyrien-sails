package com.quintonc.vs_sails;

import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.networking.WindModNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionTypes;

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

    public static void InitializeWind(ServerWorld world) {
        windStrength = 0;
        windDirection = 0;
        windGustiness = 0.125f; //between 0 and 1: 1 is
        random = new Random();

        System.out.println("ServerWindManager Init");
        ServerTickEvents.START_WORLD_TICK.register(ServerWindManager::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        if(tickCount == 299) {
            tickCount = 0;
            if (random.nextBoolean()) {
                timeInfluence = min(timeInfluence+((timeInfluence*0.125))+0.001, 1);
            } else {
                timeInfluence = max(timeInfluence-((timeInfluence*0.125))-0.001, -1);
            }
            updateWind(world);
        } else {
            tickCount++;
        }
    }

    private static void updateWind(ServerWorld world) {
        //float gust = random.nextFloat(windGustiness) - (float)(windGustiness*0.5);
        double timeFactor = sin(((double)world.getTimeOfDay() / 12000) * Math.PI);
        randomFactor = min(max(randomFactor + (random.nextDouble() - 0.5) * (1 - randomFactor), 0), 1);

        if (world.getServer().getOverworld().isRaining() || world.getServer().getOverworld().isThundering()) {
            timeInfluence = 0;
        }

        windStrength = (float) copySign((pow(abs(timeFactor),0.44) * timeInfluence + abs(randomFactor) * (1 - timeInfluence)),timeFactor);
        //windStrength = (float) sin(sin(((double)world.getTimeOfDay() / 12000) * Math.PI))

        windDirection = DIRECTIONS.get(world.getServer().getOverworld().getMoonPhase());
        windDirection += 12 * windStrength;
        windDirection += 10;

        if (world.getServer().getOverworld().isThundering()){
            windStrength *= 2f;
        } else if (world.getServer().getOverworld().isRaining()){
            windStrength *= 1.5f;
        }
        windStrength /= 2;

//        if (abs(windStrength) < minWindSpeed) {
//            windStrength = minWindSpeed;
//        }

        PacketByteBuf buf1 = PacketByteBufs.create();
        buf1.writeFloat(windStrength);

        buf1.readerIndex(0);

        PacketByteBuf buf2 = PacketByteBufs.create();
        buf2.writeFloat(windDirection);

        buf2.readerIndex(0);

        world.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
            if (serverPlayerEntity.getServerWorld().getDimensionKey() == DimensionTypes.OVERWORLD) {
                assert WindModNetworking.WINDSTRENGTHS2CPACKET != null;
                ServerPlayNetworking.send(serverPlayerEntity, WindModNetworking.WINDSTRENGTHS2CPACKET, buf1);
                assert WindModNetworking.WINDDIRECTIONS2CPACKET != null;
                ServerPlayNetworking.send(serverPlayerEntity, WindModNetworking.WINDDIRECTIONS2CPACKET, buf2);
                //System.out.println("Sending packet to " + serverPlayerEntity.getEntityName()); //fixme comment this out
                //System.out.println("windStrength: " + windStrength + " timeInfluence: " + timeInfluence);
                //System.out.println("timeFactor: " + timeFactor + " randomFactor: " + randomFactor);
            }
        });
    }



}
