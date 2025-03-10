package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import com.quintonc.vs_sails.blocks.entity.SailBlockEntity;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class SailBlock extends BlockWithEntity {
    public static final BooleanProperty SET = BooleanProperty.of("set");
    public static final DirectionProperty FACING;
    public static final Logger LOGGER = LoggerFactory.getLogger("sail_block");

    public SailBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(SET, true));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(SET, true);
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) {
            return;
        }

        //LOGGER.info("Sail block is added");
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            assert ship != null;
            SailsShipControl controller = SailsShipControl.getOrCreate(ship);
            controller.numSails++;
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            //LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            //SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
            //I eventually want to make this method add and remove sails from the sail list
        }
        if ((Boolean)state.get(SET)) {
            state = (BlockState)state.with(SET, false);
            world.setBlockState(pos, state, 10);
        } else {
            state = (BlockState)state.with(SET, true);
            world.setBlockState(pos, state, 10);
        }

        boolean bl = (Boolean)state.get(SET);
        world.playSound(player, pos, bl ? WoodType.ACACIA.fenceGateOpen() : WoodType.ACACIA.fenceGateClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        return ActionResult.success(world.isClient);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        //if sourceBlock is a sail, then
        //      if state != sourceBlock.state
        //          state = source.state
//        if () {
//
//        }
//        if () { //fixme
//        } else {
//            state = state.with(SET, true);
//            world.setBlockState(pos, state, 10);
//        }
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
        SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
        controller.numSails--;
        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }

    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SailBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ValkyrienSailsJava.SAIL_BLOCK_ENTITY, SailBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
        builder.add(new Property[]{SET});
    }

    static {
        FACING = Properties.HORIZONTAL_FACING;
    }
}
