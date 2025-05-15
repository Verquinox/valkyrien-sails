package com.quintonc.vs_sails.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {
    public PlayerModelMixin(final ModelPart model) {
        super(model);
    }

    @Inject(method = "setAngles*", at = @At(value = "HEAD"))
    public void setupAnim(final T livingEntity,
                          final float swing,
                          final float g,
                          final float tick,
                          final float i,
                          final float j,
                          final CallbackInfo info) {
        final Entity vehicle = livingEntity.getVehicle();
        if (vehicle instanceof ShipMountingEntity) {
            if (vehicle.getWorld().getBlockState(vehicle.getBlockPos()).isAir()) {
                this.riding = false;
            }
        }
    }
}
