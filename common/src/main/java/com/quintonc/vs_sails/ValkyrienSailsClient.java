package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.entity.renderer.HelmBlockEntityRenderer;
import com.quintonc.vs_sails.client.particles.WindParticle;
import com.quintonc.vs_sails.networking.PacketHandler;
import com.quintonc.vs_sails.networking.WindModNetworking;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Player;

public class ValkyrienSailsClient {

    //private static KeyBinding testKeyBinding;

    public static void clientInit() {
        BlockEntityRenderers.register(ValkyrienSails.HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);

        //System.out.println("Client init");
        com.quintonc.vs_sails.client.ClientWindManager.InitializeWind();

//        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((altasTexture, registry) -> {
//            registry.register(new Identifier(MOD_ID, PARTICLE));
//        }));

        PacketHandler.register();

        if (!ValkyrienSails.weather2) {
            ClientTickEvents.END_CLIENT_TICK.register(ModSounds::windSoundHandler);
        }


    }
}
