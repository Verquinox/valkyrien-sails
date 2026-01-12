package com.quintonc.vs_sails.items;

import com.quintonc.vs_sails.util.ConnectivityUtils;
import com.quintonc.vs_sails.registration.SailsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.assembly.ShipAssembler;
import java.util.Objects;
import java.util.Set;

public class DedicationBottle extends Item {


    public int mode;

    public DedicationBottle(Properties settings) {
        super(settings);
        mode = 0;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {

        if (!context.getLevel().isClientSide()) {
            if (!VSGameUtilsKt.isBlockInShipyard(context.getLevel(), context.getClickedPos())) {
                Objects.requireNonNull(context.getPlayer()).getItemInHand(context.getHand()).shrink(1);
                ServerLevel serverLevel = (ServerLevel) context.getLevel();

                Set<BlockPos> blocksToAssemble = ConnectivityUtils.tryFillByConnectivity(serverLevel, context.getClickedPos());
                if (blocksToAssemble != null) {
                    ServerShip ship = ShipAssembler.assembleToShip(serverLevel, blocksToAssemble, 1.0);

                    if (context.getItemInHand().hasCustomHoverName()) {

                        ship.setSlug(context.getItemInHand().getHoverName().getString()
                                .replace(' ', '-')
                                .replaceAll("[^a-zA-Z0-9-]", "")
                        );
                    }
                }

                RandomSource random = RandomSource.create();
                ((ServerLevel) context.getLevel()).sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, SailsItems.DEDICATION_BOTTLE.get().getDefaultInstance()),
                        context.getClickedPos().getX(),
                        context.getClickedPos().getY(),
                        context.getClickedPos().getZ(),
                        10,
                        ((double)random.nextFloat() - (double)0.5F) * 0.08,
                        ((double)random.nextFloat() - (double)0.5F) * 0.08,
                        ((double)random.nextFloat() - (double)0.5F) * 0.08,
                        random.nextDouble() * 0.1
                );
                context.getLevel().playSound(null,
                        context.getClickedPos().getX(),
                        context.getClickedPos().getY(),
                        context.getClickedPos().getZ(),
                        SoundEvents.GLASS_BREAK,
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F
                );
                context.getPlayer().getCooldowns().addCooldown(context.getItemInHand().getItem(), 2000);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        return InteractionResultHolder.consume(user.getItemInHand(hand));
    }

}
