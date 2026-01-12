package com.quintonc.vs_sails;


import com.quintonc.vs_sails.blocks.entity.renderer.HelmBlockEntityRenderer;
import com.quintonc.vs_sails.client.particles.WindParticle;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ValkyrienSailsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ValkyrienSails.HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);
        BlockEntityRenderers.register(ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);

        ValkyrienSailsClient.clientInit();
        ParticleFactoryRegistry.getInstance().register(ValkyrienSails.WIND_PARTICLE, WindParticle.Factory::new);
    }
}
