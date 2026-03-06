package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.entity.renderer.HelmBlockEntityRenderer;
import com.quintonc.vs_sails.client.ClientWindManager;
import com.quintonc.vs_sails.networking.PacketHandler;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ValkyrienSailsClient {

    //private static KeyBinding testKeyBinding;

    public static void clientInit() {
        BlockEntityRenderers.register(ValkyrienSails.HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);
        BlockEntityRenderers.register(ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);

        //System.out.println("Client init");
        com.quintonc.vs_sails.client.ClientWindManager.InitializeWind();

//        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((altasTexture, registry) -> {
//            registry.register(new Identifier(MOD_ID, PARTICLE));
//        }));

        PacketHandler.register();

        if (!ValkyrienSails.weather2) {
            //ClientTickEvents.END_CLIENT_TICK.register(ModSounds::windSoundHandler);
            ClientTickEvent.CLIENT_POST.register(ModSounds::windSoundHandler);
        }

        ClientTickEvent.CLIENT_POST.register(ClientWindManager::handleClientTick);

    }

}
