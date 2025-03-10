package com.quintonc.vs_sails.blocks.entity.renderer;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import com.quintonc.vs_sails.blocks.HelmBlock;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static com.quintonc.vs_sails.blocks.HelmBlock.FACING;
import static com.quintonc.vs_sails.blocks.HelmBlock.WHEEL_ANGLE;

public class HelmBlockEntityRenderer implements BlockEntityRenderer<HelmBlockEntity> {
    public HelmBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}

    @Override
    public void render(HelmBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockModelRenderer blockRenderer = MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer();
        //BlockRenderView block = ???;

        matrices.push();
        matrices.translate(0.5,0.60,0.5);
        //rotate wheel towards direction it is facing
        Quaternionf bee = new Quaternionf(0, 1, 0, 1);
        Quaternionf boo = new Quaternionf(RotationAxis.POSITIVE_X.rotationDegrees(entity.getCachedState().get(WHEEL_ANGLE)));
        matrices.multiply(boo);

        LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) entity.getWorld(), entity.getPos());
        double rotation = 0.0;
        rotation = entity.getCachedState().get(WHEEL_ANGLE);



        //add offset of the base based on rotation
        //matrices.translate(0.0,0.0,0.19);

        //rotate wheel based on wheel angle (rotation)
        //Matrix4f rotTransform = ();
        //matrices.multiplyPositionMatrix();

        //render wheel (need to add WheelModels class)

        //blockRenderer.render(block, ModelTransformationMode.GROUND, , OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);

        matrices.pop();
    }
}
