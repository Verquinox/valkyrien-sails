package com.quintonc.vs_sails.blocks.entity.renderer;

import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import com.mojang.math.Axis;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.quintonc.vs_sails.blocks.HelmBlock.FACING;
import static com.quintonc.vs_sails.blocks.HelmBlock.WHEEL_ANGLE;

public class HelmBlockEntityRenderer implements BlockEntityRenderer<HelmBlockEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger("helm_block_renderer");

    public HelmBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(HelmBlockEntity entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light, int overlay) {

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = entity.getRenderStack();

        matrices.pushPose();
        //int i =((HelmBlock)entity.getCachedState().getBlock()).wheelAngle;

        if (entity.getBlockState().getValue(FACING) == Direction.NORTH) {

            matrices.translate(0.5f,0.5f,0.5f);
            matrices.rotateAround(Axis.ZP.rotationDegrees(entity.getBlockState().getValue(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        } else if (entity.getBlockState().getValue(FACING) == Direction.SOUTH) {
            matrices.mulPose(Axis.YP.rotationDegrees(180));
            matrices.translate(-0.5f,0.5f,-0.5f);
            matrices.rotateAround(Axis.ZP.rotationDegrees(entity.getBlockState().getValue(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        } else if (entity.getBlockState().getValue(FACING) == Direction.EAST) {
            matrices.mulPose(Axis.YP.rotationDegrees(270));
            matrices.translate(0.5f,0.5f,-0.5f);
            matrices.rotateAround(Axis.ZP.rotationDegrees(entity.getBlockState().getValue(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        } else if (entity.getBlockState().getValue(FACING) == Direction.WEST) {
            matrices.mulPose(Axis.YP.rotationDegrees(90));
            matrices.translate(-0.5f,0.5f,0.5f);
            matrices.rotateAround(Axis.ZP.rotationDegrees(entity.getBlockState().getValue(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        }
        matrices.scale(1.6f, 1.6f, 1.6f);

        //rotate wheel towards direction it is facing

        itemRenderer.renderStatic(stack, ItemDisplayContext.GUI, getLightLevel(entity.getLevel(),
                entity.getBlockPos()), OverlayTexture.NO_OVERLAY, matrices, vertexConsumers, entity.getLevel(), 1);
        //LOGGER.info("item rendered");

        matrices.popPose();
    }

    private int getLightLevel(Level world, BlockPos pos) {
        int bLight = world.getBrightness(LightLayer.BLOCK, pos);
        int sLight = world.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
