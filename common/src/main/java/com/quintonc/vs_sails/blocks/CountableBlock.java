package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public abstract class CountableBlock extends Block {
    public CountableBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }

        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                SailsShipControl controller = SailsShipControl.getOrCreate(ship, world);
                addToShip(controller);
            } else {
                ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
                if (ship != null) {
                    SailsShipControl controller = SailsShipControl.getOrCreate(ship, world);
                    addToShip(controller);
                }
            }
        }
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(ItemStack.EMPTY.getItem()) || !VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            return  InteractionResult.PASS;
        }
        if (!world.isClientSide) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
            assert ship != null;
            SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
            assert controller != null;
            sendMessage(player, controller);
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    abstract void addToShip(SailsShipControl controller);

    abstract void removeFromShip(SailsShipControl controller);

    abstract void sendMessage(Player player, SailsShipControl controller);

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.isAir()) {
            if (!world.isClientSide) {
                if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                    LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
                    assert ship != null;
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    assert controller != null;
                    removeFromShip(controller);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
