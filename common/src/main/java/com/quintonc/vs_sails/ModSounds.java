package com.quintonc.vs_sails;

import com.quintonc.vs_sails.client.ClientWindManager;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

public class ModSounds {
    private static int windSoundTick = 0;

    public static DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ValkyrienSails.MOD_ID, Registries.SOUND_EVENT);
    public static RegistrySupplier<SoundEvent> WIND_AMBIENCE = registerSoundEvent("wind_ambience");

    private static RegistrySupplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(ValkyrienSails.MOD_ID, name);
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSounds() {
        ValkyrienSails.LOGGER.info("Registering sounds for " + ValkyrienSails.MOD_ID);
        SOUNDS.register();
    }

    @Environment(EnvType.CLIENT)
    public static void windSoundHandler (Minecraft client) {
        if (windSoundTick == 30) {

            if (client.player != null && !client.isPaused()) { // && client.world.getBiome(client.player.getBlockPos()) == BiomeKeys.DEEP_OCEAN
                LocalPlayer player = client.player;
                if (
                        !player.isUnderWater()
                        && player.clientLevel.dimensionTypeId() == BuiltinDimensionTypes.OVERWORLD
                        && player.clientLevel.getBrightness(LightLayer.SKY, player.blockPosition()) > 3
                        && Math.abs(ClientWindManager.getWindStrength(player.clientLevel, player.blockPosition())) > 0.35
                ) {
                    float windVolume = (player.clientLevel.getBrightness(LightLayer.SKY, player.blockPosition()) * Math.abs(ClientWindManager.getWindStrength(player.clientLevel, player.blockPosition())) / 15 - 0.35f);
                    player.clientLevel.playLocalSound(player.getX(), player.getY(), player.getZ(), WIND_AMBIENCE.get(), SoundSource.AMBIENT, windVolume, 1.0F, false);
                }


                windSoundTick = 0;
            }
        } else windSoundTick++;
    }
}

