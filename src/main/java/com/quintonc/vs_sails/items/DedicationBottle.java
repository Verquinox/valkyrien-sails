package com.quintonc.vs_sails.items;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import g_mungus.vlib.api.VLibGameUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;

public class DedicationBottle extends Item {

    public int mode;

    public DedicationBottle(Settings settings) {
        super(settings);
        mode = 0;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        if (!context.getWorld().isClient()) {
            if (!VSGameUtilsKt.isBlockInShipyard(context.getWorld(), context.getBlockPos())) {
                Objects.requireNonNull(context.getPlayer()).getStackInHand(context.getHand()).decrement(1);
                VLibGameUtils.INSTANCE.assembleByConnectivity((ServerWorld)context.getWorld(), context.getBlockPos());
                Random random = Random.create();
                ((ServerWorld) context.getWorld()).spawnParticles(
                        new ItemStackParticleEffect(ParticleTypes.ITEM, ValkyrienSailsJava.DEDICATION_BOTTLE.getDefaultStack()),
                        context.getBlockPos().getX(),
                        context.getBlockPos().getY(),
                        context.getBlockPos().getZ(),
                        10,
                        ((double)random.nextFloat() - (double)0.5F) * 0.08,
                        ((double)random.nextFloat() - (double)0.5F) * 0.08,
                        ((double)random.nextFloat() - (double)0.5F) * 0.08,
                        random.nextDouble() * 0.1
                );
                context.getWorld().playSound(null,
                        context.getBlockPos().getX(),
                        context.getBlockPos().getY(),
                        context.getBlockPos().getZ(),
                        SoundEvents.BLOCK_GLASS_BREAK,
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F
                );
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

}
