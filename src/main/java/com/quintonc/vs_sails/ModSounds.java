package com.quintonc.vs_sails;

import com.quintonc.vs_sails.client.ClientWindManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionTypes;

public class ModSounds {
    private static int windSoundTick = 0;
    public static SoundEvent WIND_AMBIENCE = registerSoundEvent("wind_ambience");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(ValkyrienSailsJava.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        ValkyrienSailsJava.LOGGER.info("Registering sounds for" + ValkyrienSailsJava.MOD_ID);
    }

    @Environment(EnvType.CLIENT)
    public static void windSoundHandler (MinecraftClient client) {
        if (windSoundTick == 30) {

            if (client.player != null && !client.isPaused()) { // && client.world.getBiome(client.player.getBlockPos()) == BiomeKeys.DEEP_OCEAN
                ClientPlayerEntity player = client.player;
                if (
                        !player.isSubmergedInWater()
                        && player.clientWorld.getDimensionKey() == DimensionTypes.OVERWORLD
                        && player.clientWorld.getLightLevel(LightType.SKY, player.getBlockPos()) > 3
                        && Math.abs(ClientWindManager.getWindStrength()) > 0.35
                ) {
                    float windVolume = (player.clientWorld.getLightLevel(LightType.SKY, player.getBlockPos()) * Math.abs(ClientWindManager.getWindStrength()) / 15 - 0.35f);
                    player.clientWorld.playSound(player.getX(), player.getY(), player.getZ(), WIND_AMBIENCE, SoundCategory.AMBIENT, windVolume, 1.0F, false);
                }


                windSoundTick = 0;
            }
        } else windSoundTick++;
    }
}

