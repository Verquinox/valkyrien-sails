package com.quintonc.vs_sails.mixin;

import com.quintonc.vs_sails.blocks.RiggingBlock;
import com.quintonc.vs_sails.blocks.SailBlock;
import com.quintonc.vs_sails.blocks.SailToggleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionMixin {

    private static final double BLOCK_SPLASH_RADIUS = 3.5D;

    @Inject(method = "applyWater", at = @At("TAIL"))
    private void vs_sails$cleanseMagmaCoatedBlocks(CallbackInfo ci) {
        ThrownPotion potion = (ThrownPotion) (Object) this;
        if (potion.level().isClientSide) {
            return;
        }

        applyConversionInRadius(
                potion.level(),
                potion.getBoundingBox().inflate(BLOCK_SPLASH_RADIUS, BLOCK_SPLASH_RADIUS, BLOCK_SPLASH_RADIUS),
                potion.getX(),
                potion.getZ(),
                false
        );
    }

    @Inject(method = "onHit", at = @At("TAIL"))
    private void vs_sails$magmaCoatFromFireResistance(HitResult result, CallbackInfo ci) {
        ThrownPotion potion = (ThrownPotion) (Object) this;
        Level level = potion.level();
        if (level.isClientSide) {
            return;
        }

        ItemStack potionStack = potion.getItem();
        if (potionStack.is(Items.LINGERING_POTION)) {
            return;
        }
        if (PotionUtils.getMobEffects(potionStack).stream()
                .noneMatch(effect -> effect.getEffect() == MobEffects.FIRE_RESISTANCE)) {
            return;
        }

        applyConversionInRadius(
                level,
                potion.getBoundingBox().inflate(BLOCK_SPLASH_RADIUS, BLOCK_SPLASH_RADIUS, BLOCK_SPLASH_RADIUS),
                potion.getX(),
                potion.getZ(),
                true
        );
    }

    private static void applyConversionInRadius(Level level, AABB splashBounds, double centerX, double centerZ, boolean magmaCoat) {
        applyConversionInAabb(level, splashBounds, centerX, centerZ, magmaCoat);
        VSGameUtilsKt.transformFromWorldToNearbyShipsAndWorld(level, splashBounds, nearbyAabb ->
                applyConversionInAabb(level, nearbyAabb, centerX, centerZ, magmaCoat));
    }

    private static void applyConversionInAabb(Level level, AABB splashBounds, double centerX, double centerZ,
                                              boolean magmaCoat) {
        BlockPos minPos = BlockPos.containing(splashBounds.minX, splashBounds.minY, splashBounds.minZ);
        BlockPos maxPos = BlockPos.containing(splashBounds.maxX, splashBounds.maxY, splashBounds.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            Vec3 worldPos = VSGameUtilsKt.toWorldCoordinates(level, pos);
            double dx = worldPos.x - centerX;
            double dz = worldPos.z - centerZ;
            double distanceSq = dx * dx + dz * dz;
            double maxDistanceSq = BLOCK_SPLASH_RADIUS * BLOCK_SPLASH_RADIUS;
            if (distanceSq > maxDistanceSq) {
                continue;
            }

            // Full strength at center, increasingly random near the edge.
            double normalizedDistance = Math.sqrt(distanceSq) / BLOCK_SPLASH_RADIUS;
            double chance = 1.0D - normalizedDistance;
            if (level.getRandom().nextDouble() > chance) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            BlockState convertedState = null;
            if (state.getBlock() instanceof SailBlock sailBlock) {
                convertedState = magmaCoat ? sailBlock.toMagmaCoatedState(state) : sailBlock.toRegularState(state);
            } else if (state.getBlock() instanceof SailToggleBlock sailToggleBlock) {
                convertedState = magmaCoat ? sailToggleBlock.toMagmaCoatedState(state) : sailToggleBlock.toRegularState(state);
            } else if (state.getBlock() instanceof RiggingBlock riggingBlock) {
                convertedState = magmaCoat ? riggingBlock.toMagmaCoatedState(state) : riggingBlock.toRegularState(state);
            }

            if (convertedState != null) {
                level.setBlock(pos, convertedState, 10);
            }
        }
    }
}
