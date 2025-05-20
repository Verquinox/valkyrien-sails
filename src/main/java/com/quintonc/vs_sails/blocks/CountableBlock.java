package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public abstract class CountableBlock extends Block {
    public CountableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) {
            return;
        }

        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            if (ship != null) {
                SailsShipControl controller = SailsShipControl.getOrCreate(ship);
                addToShip(controller);
            } else {
                ship = VSGameUtilsKt.getShipManagingPos((ServerWorld) world, pos);
                if (ship != null) {
                    SailsShipControl controller = SailsShipControl.getOrCreate(ship);
                    addToShip(controller);
                }
            }
        }
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!player.getStackInHand(Hand.MAIN_HAND).isOf(ItemStack.EMPTY.getItem()) || !VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            return  ActionResult.PASS;
        }
        if (!world.isClient) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            assert ship != null;
            SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
            assert controller != null;
            sendMessage(player, controller);
        }

        return ActionResult.success(world.isClient);
    }

    abstract void addToShip(SailsShipControl controller);

    abstract void removeFromShip(SailsShipControl controller);

    abstract void sendMessage(PlayerEntity player, SailsShipControl controller);

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.isAir()) {
            if (!world.isClient) {
                if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                    LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
