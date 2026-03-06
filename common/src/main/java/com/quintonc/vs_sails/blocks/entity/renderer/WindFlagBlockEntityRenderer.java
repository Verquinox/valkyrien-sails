package com.quintonc.vs_sails.blocks.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.quintonc.vs_sails.blocks.WindFlagBlock;
import com.quintonc.vs_sails.blocks.entity.WindFlagBlockEntity;
import com.quintonc.vs_sails.wind.WindManager;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WindFlagBlockEntityRenderer implements BlockEntityRenderer<WindFlagBlockEntity> {
    private static final float FLAG_PIVOT_X = 0.5f; //8.0f / 16.0f;
    private static final float FLAG_PIVOT_Y = 1.625f; //26.0f / 16.0f;
    private static final float FLAG_PIVOT_Z = 0.5f; //8.0f / 16.0f;

    private static final float SWAY_AMPLITUDE_MAX_DEGREES = 6.0f;
    private static final float SWAY_AMPLITUDE_MIN_DEGREES = 1.25f;
    private static final float SWAY_FREQUENCY_MIN_HZ = 0.35f;
    private static final float SWAY_FREQUENCY_MAX_HZ = 1.25f;

    private final BlockRenderDispatcher blockRenderDispatcher;
    private final Vector3d scratchWindDirection = new Vector3d();
    private final BlockPos.MutableBlockPos scratchWorldBlockCenterPos = new BlockPos.MutableBlockPos();

    public WindFlagBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    //Principle:
    //The only element the block entity renders is the actual flag
    //We render the base color once, then we render the overlay
    //with applied customization values
    //We do this with blockstate properties. Is there a more efficient solution? Maybe, but this works.
    @Override
    public void render(WindFlagBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = entity.getLevel();
        if (level == null) {
            return;
        }

        BlockState sourceState = entity.getBlockState();
        if (sourceState.getValue(WindFlagBlock.FURLED)) {
            return;
        }

        BlockPos blockPos = entity.getBlockPos();
        Vec3 shipBlockCenter = Vec3.atCenterOf(blockPos);
        Vec3 worldBlockCenter = VSGameUtilsKt.toWorldCoordinates(level, shipBlockCenter);
        BlockState baseState = sourceState
                .setValue(WindFlagBlock.FLAG_GROUP, true)
                .setValue(WindFlagBlock.OVERLAY_ONLY, false)
                .setValue(WindFlagBlock.PATTERN, 0);
        boolean hasOverlay = sourceState.getValue(WindFlagBlock.PATTERN) != 0;

        scratchWorldBlockCenterPos.set(Mth.floor(worldBlockCenter.x), Mth.floor(worldBlockCenter.y), Mth.floor(worldBlockCenter.z));
        float worldWindYaw = WindManager.getWindDirection(level, worldBlockCenter);
        float windStrength = WindManager.getWindStrength(level, scratchWorldBlockCenterPos);
        float effectiveWorldWindYaw = worldWindYaw;
        //negative wind strength is still possible, so this is still necessary
        if (windStrength < 0.0f) {
            effectiveWorldWindYaw = Mth.wrapDegrees(effectiveWorldWindYaw + 180.0f);
        }
        float targetYawDegrees = effectiveWorldWindYaw;
        var ship = VSGameUtilsKt.getLoadedShipManagingPos(level, blockPos);
        if (ship instanceof ClientShip clientShip) {
            Vector3d localWindDirection = worldWindYawToDirection(effectiveWorldWindYaw, scratchWindDirection);
            clientShip.getRenderTransform().getWorldToShip().transformDirection(localWindDirection);
            targetYawDegrees = toModelYawDegrees(localWindDirection);
        }

        float renderTimeSeconds = (level.getGameTime() + partialTick) / 20.0f;
        targetYawDegrees = Mth.wrapDegrees(targetYawDegrees + getSwayDegrees(renderTimeSeconds, entity, windStrength));
        float yawDegrees = entity.updateYawSpring(targetYawDegrees, renderTimeSeconds);

        poseStack.pushPose();
        poseStack.rotateAround(Axis.YP.rotationDegrees(-yawDegrees), FLAG_PIVOT_X, FLAG_PIVOT_Y, FLAG_PIVOT_Z);
        blockRenderDispatcher.renderSingleBlock(baseState, poseStack, bufferSource, packedLight, packedOverlay);
        if (hasOverlay) {
            BlockState overlayState = sourceState
                    .setValue(WindFlagBlock.FLAG_GROUP, true)
                    .setValue(WindFlagBlock.OVERLAY_ONLY, true);
            int overlayLight = sourceState.getValue(WindFlagBlock.EMISSIVE) ? LightTexture.FULL_BRIGHT : packedLight;
            blockRenderDispatcher.renderSingleBlock(overlayState, poseStack, bufferSource, overlayLight, packedOverlay);
        }
        poseStack.popPose();
    }

    //Technically, this is not good for performance.
    //The value of having distant flag visibility (i.e. identifying approaching ships)
    //supercedes the minimal perf benefit of a custom LOD solution.
    @Override
    public int getViewDistance() {
        return 320;
    }

    private static Vector3d worldWindYawToDirection(float yawDegrees, Vector3d target) {
        double yawRadians = Math.toRadians(yawDegrees);
        target.set(Math.cos(yawRadians), 0.0, Math.sin(yawRadians));
        return target;
    }

    private static float toModelYawDegrees(Vector3d localWindDirection) {
        double horizontalLengthSquared =
                localWindDirection.x * localWindDirection.x + localWindDirection.z * localWindDirection.z;
        if (horizontalLengthSquared < 1.0e-12) {
            return 0.0f;
        }

        double horizontalLength = Math.sqrt(horizontalLengthSquared);
        double dirX = localWindDirection.x / horizontalLength;
        double dirZ = localWindDirection.z / horizontalLength;

        return Mth.wrapDegrees((float) Math.toDegrees(Math.atan2(dirZ, dirX)));
    }

    private static float getSwayDegrees(float renderTimeSeconds, WindFlagBlockEntity entity, float windStrength) {
        float normalizedWindStrength = Mth.clamp(Math.abs(windStrength), 0.0f, 1.0f);
        float swayAmplitudeDegrees =
                Mth.lerp(normalizedWindStrength, SWAY_AMPLITUDE_MAX_DEGREES, SWAY_AMPLITUDE_MIN_DEGREES);
        float swayFrequencyHz =
                Mth.lerp(normalizedWindStrength, SWAY_FREQUENCY_MIN_HZ, SWAY_FREQUENCY_MAX_HZ);

        float angularFrequency = swayFrequencyHz * (float) (Math.PI * 2.0);
        float phase = entity.getSwayPhaseOffsetRadians();
        return swayAmplitudeDegrees * Mth.sin(angularFrequency * renderTimeSeconds + phase);
    }

}
