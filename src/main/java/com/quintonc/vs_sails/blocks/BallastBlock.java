package com.quintonc.vs_sails.blocks;

//import com.quintonc.vs_sails.blocks.entity.BallastBlockEntity;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class BallastBlock extends Block {
    public BallastBlock(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) {
            return;
        }

        //LOGGER.info("Ballast block is added");
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            if (ship != null) {
                SailsShipControl controller = SailsShipControl.getOrCreate(ship);
                controller.numBallast++;
            }
        }
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.isClient) {
            if (player.getStackInHand(Hand.MAIN_HAND) == ItemStack.EMPTY) {
                return ActionResult.SUCCESS;
            } else {
                return  ActionResult.PASS;
            }
        } else {
            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                if (player.getStackInHand(Hand.MAIN_HAND) == ItemStack.EMPTY) {
                    LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
                    assert ship != null;
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    assert controller != null;
                    player.sendMessage(Text.of("Ballast: "+ (controller.numBallast)));
                } else {
                    return ActionResult.PASS;
                }
            }
        }

        return ActionResult.success(world.isClient);
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.isAir()) {
            if (!world.isClient) {
                if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                    LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
                    assert ship != null;
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    assert controller != null;
                    controller.numBallast--;
                }
            }
        }

        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
    }

//    @Override
//    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
//        return new BallastBlockEntity(pos, state);
//    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
