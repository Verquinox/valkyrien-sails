package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSails;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WindFlagBlockEntity extends BlockEntity {
    //Spring parameters for adjusting towards the wind
    private static final float SPRING_STIFFNESS = 32.0f;
    private static final float SPRING_DAMPING = 9.0f;
    private static final float MAX_SPRING_STEP_SECONDS = 0.25f;
    private static final float SPRING_TIME_SCALE_BASE = 0.42f;
    private static final float SPRING_TIME_SCALE_VARIATION_MIN = 0.90f;
    private static final float SPRING_TIME_SCALE_VARIATION_MAX = 1.10f;

    private boolean randomizationInitialized;
    private boolean springInitialized;
    private float springYawDegrees;
    private float springYawVelocityDegreesPerSecond;
    private float springLastRenderTimeSeconds;
    private float springTimeScale = SPRING_TIME_SCALE_BASE;
    private float swayPhaseOffsetRadians;

    public WindFlagBlockEntity(BlockPos pos, BlockState state) {
        super(ValkyrienSails.WIND_FLAG_BLOCK_ENTITY, pos, state);
    }

    public float updateYawSpring(float targetYawDegrees, float renderTimeSeconds) {
        if (!springInitialized) {
            springInitialized = true;
            springYawDegrees = Mth.wrapDegrees(targetYawDegrees);
            springYawVelocityDegreesPerSecond = 0.0f;
            springLastRenderTimeSeconds = renderTimeSeconds;
            return springYawDegrees;
        }

        float deltaTimeSeconds = renderTimeSeconds - springLastRenderTimeSeconds;
        springLastRenderTimeSeconds = renderTimeSeconds;
        if (deltaTimeSeconds <= 0.0f) {
            return springYawDegrees;
        }
        deltaTimeSeconds = Mth.clamp(deltaTimeSeconds, 0.0f, MAX_SPRING_STEP_SECONDS);
        ensureRandomizationInitialized();
        float scaledDeltaTimeSeconds = deltaTimeSeconds * springTimeScale;

        float yawErrorDegrees = Mth.wrapDegrees(targetYawDegrees - springYawDegrees);
        float springAcceleration =
                SPRING_STIFFNESS * yawErrorDegrees - SPRING_DAMPING * springYawVelocityDegreesPerSecond;

        springYawVelocityDegreesPerSecond += springAcceleration * scaledDeltaTimeSeconds;
        springYawDegrees = Mth.wrapDegrees(springYawDegrees + springYawVelocityDegreesPerSecond * scaledDeltaTimeSeconds);
        return springYawDegrees;
    }

    //I want adjacent flags to flap differently in the wind for a more natural look, so this makes
    //a random phase based off the current block position.
    public float getSwayPhaseOffsetRadians() {
        ensureRandomizationInitialized();
        return swayPhaseOffsetRadians;
    }

    private void ensureRandomizationInitialized() {
        if (randomizationInitialized) return;
        randomizationInitialized = true;

        long seed = getBlockPos().asLong();
        float unit = ((seed ^ (seed >>> 32)) & 0xFFFF) / 65535.0f;

        float variation = Mth.lerp(unit, SPRING_TIME_SCALE_VARIATION_MIN, SPRING_TIME_SCALE_VARIATION_MAX);
        springTimeScale = SPRING_TIME_SCALE_BASE * variation;
        swayPhaseOffsetRadians = unit * (float) (Math.PI * 2.0);
    }
}
