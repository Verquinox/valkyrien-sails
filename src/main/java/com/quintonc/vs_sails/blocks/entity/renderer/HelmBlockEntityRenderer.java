package com.quintonc.vs_sails.blocks.entity.renderer;

import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.quintonc.vs_sails.blocks.HelmBlock.FACING;
import static com.quintonc.vs_sails.blocks.HelmBlock.WHEEL_ANGLE;

public class HelmBlockEntityRenderer implements BlockEntityRenderer<HelmBlockEntity> {

    public static final Logger LOGGER = LoggerFactory.getLogger("helm_block_renderer");

    public HelmBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super();
    }

    @Override
    public void render(HelmBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = entity.getRenderStack();

        matrices.push();
        //int i =((HelmBlock)entity.getCachedState().getBlock()).wheelAngle;

        if (entity.getCachedState().get(FACING) == Direction.NORTH) {

            matrices.translate(0.5f,0.5f,0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getCachedState().get(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        } else if (entity.getCachedState().get(FACING) == Direction.SOUTH) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            matrices.translate(-0.5f,0.5f,-0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getCachedState().get(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        } else if (entity.getCachedState().get(FACING) == Direction.EAST) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            matrices.translate(0.5f,0.5f,-0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getCachedState().get(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        } else if (entity.getCachedState().get(FACING) == Direction.WEST) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            matrices.translate(-0.5f,0.5f,0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getCachedState().get(WHEEL_ANGLE)%360), 0, 0.3125f, 0);

        }
        matrices.scale(1.6f, 1.6f, 1.6f);

        //rotate wheel towards direction it is facing

        itemRenderer.renderItem(stack, ModelTransformationMode.GUI, getLightLevel(entity.getWorld(),
                entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);
        //LOGGER.info("item rendered");

        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
