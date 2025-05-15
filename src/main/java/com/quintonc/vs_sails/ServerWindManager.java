package com.quintonc.vs_sails;

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


public class ServerWindManager extends WindManager {
    private static int tickCount = 0;

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

        System.out.println("ServerWindManager Init");
        ServerTickEvents.START_WORLD_TICK.register(ServerWindManager::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        if(tickCount == 299) {
            tickCount = 0;
            updateWind(world);
        } else {
            tickCount++;
        }
    }

    private static void updateWind(ServerWorld world) {
        //Random random = new Random();
        //float gust = random.nextFloat(windGustiness) - (float)(windGustiness*0.5);
        windStrength = (float) Math.sin(((double)world.getTimeOfDay() / 12000) * Math.PI);

        windDirection = DIRECTIONS.get(world.getServer().getOverworld().getMoonPhase());
        windDirection += 12 * windStrength;
        windDirection += 10;

        if (world.getServer().getOverworld().isThundering()){
            windStrength *= 2f;
        } else if (world.getServer().getOverworld().isRaining()){
            windStrength *= 1.5f;
        }
        windStrength /= 2;
//        windStrength = windStrength * Math.abs(windStrength);


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
                System.out.println("Sending packet to " + serverPlayerEntity.getEntityName()); //fixme comment this out
            }
        });
    }



}
