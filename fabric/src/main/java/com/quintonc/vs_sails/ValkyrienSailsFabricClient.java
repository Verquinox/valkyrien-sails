package com.quintonc.vs_sails;


import com.quintonc.vs_sails.blocks.WindFlagBlock;
import com.quintonc.vs_sails.blocks.entity.renderer.HelmBlockEntityRenderer;
import com.quintonc.vs_sails.blocks.entity.renderer.WindFlagBlockEntityRenderer;
import com.quintonc.vs_sails.client.particles.WindParticle;
import com.quintonc.vs_sails.registration.SailsBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ValkyrienSailsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ValkyrienSails.HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);
        BlockEntityRenderers.register(ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);
        BlockEntityRenderers.register(ValkyrienSails.WIND_FLAG_BLOCK_ENTITY, WindFlagBlockEntityRenderer::new);

        ValkyrienSailsClient.clientInit();
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderType.translucent(),
                SailsBlocks.WIND_FLAG.get(),
                SailsBlocks.BLACK_WIND_FLAG.get(),
                SailsBlocks.BROWN_WIND_FLAG.get(),
                SailsBlocks.CYAN_WIND_FLAG.get(),
                SailsBlocks.GRAY_WIND_FLAG.get(),
                SailsBlocks.GREEN_WIND_FLAG.get(),
                SailsBlocks.LIGHT_BLUE_WIND_FLAG.get(),
                SailsBlocks.BLUE_WIND_FLAG.get(),
                SailsBlocks.LIGHT_GRAY_WIND_FLAG.get(),
                SailsBlocks.LIME_WIND_FLAG.get(),
                SailsBlocks.MAGENTA_WIND_FLAG.get(),
                SailsBlocks.ORANGE_WIND_FLAG.get(),
                SailsBlocks.PINK_WIND_FLAG.get(),
                SailsBlocks.PURPLE_WIND_FLAG.get(),
                SailsBlocks.RED_WIND_FLAG.get(),
                SailsBlocks.WHITE_WIND_FLAG.get(),
                SailsBlocks.YELLOW_WIND_FLAG.get()
        );
        ColorProviderRegistry.BLOCK.register(
                (state, world, pos, tintIndex) -> WindFlagBlock.getOverlayTintColor(state, world, pos, tintIndex),
                SailsBlocks.WIND_FLAG.get(),
                SailsBlocks.BLACK_WIND_FLAG.get(),
                SailsBlocks.BROWN_WIND_FLAG.get(),
                SailsBlocks.CYAN_WIND_FLAG.get(),
                SailsBlocks.GRAY_WIND_FLAG.get(),
                SailsBlocks.GREEN_WIND_FLAG.get(),
                SailsBlocks.LIGHT_BLUE_WIND_FLAG.get(),
                SailsBlocks.BLUE_WIND_FLAG.get(),
                SailsBlocks.LIGHT_GRAY_WIND_FLAG.get(),
                SailsBlocks.LIME_WIND_FLAG.get(),
                SailsBlocks.MAGENTA_WIND_FLAG.get(),
                SailsBlocks.ORANGE_WIND_FLAG.get(),
                SailsBlocks.PINK_WIND_FLAG.get(),
                SailsBlocks.PURPLE_WIND_FLAG.get(),
                SailsBlocks.RED_WIND_FLAG.get(),
                SailsBlocks.WHITE_WIND_FLAG.get(),
                SailsBlocks.YELLOW_WIND_FLAG.get()
        );
        ColorProviderRegistry.ITEM.register(
                WindFlagBlock::getOverlayTintColor,
                SailsBlocks.WIND_FLAG.get(),
                SailsBlocks.BLACK_WIND_FLAG.get(),
                SailsBlocks.BROWN_WIND_FLAG.get(),
                SailsBlocks.CYAN_WIND_FLAG.get(),
                SailsBlocks.GRAY_WIND_FLAG.get(),
                SailsBlocks.GREEN_WIND_FLAG.get(),
                SailsBlocks.LIGHT_BLUE_WIND_FLAG.get(),
                SailsBlocks.BLUE_WIND_FLAG.get(),
                SailsBlocks.LIGHT_GRAY_WIND_FLAG.get(),
                SailsBlocks.LIME_WIND_FLAG.get(),
                SailsBlocks.MAGENTA_WIND_FLAG.get(),
                SailsBlocks.ORANGE_WIND_FLAG.get(),
                SailsBlocks.PINK_WIND_FLAG.get(),
                SailsBlocks.PURPLE_WIND_FLAG.get(),
                SailsBlocks.RED_WIND_FLAG.get(),
                SailsBlocks.WHITE_WIND_FLAG.get(),
                SailsBlocks.YELLOW_WIND_FLAG.get()
        );
        ParticleFactoryRegistry.getInstance().register(ValkyrienSails.WIND_PARTICLE, WindParticle.Factory::new);
    }
}
