package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.entity.BaseHelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.RedstoneHelmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RedstoneHelmBlock extends BaseHelmBlock {

    public RedstoneHelmBlock(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult pog = super.use(state, world, pos, player, hand, hit);
        updateNeighbours(state, world, pos);
        return pog;
    }

        public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BaseHelmBlockEntity blockEntity) {
            int wheelAngle = blockEntity.wheelAngle;

            if (direction == state.getValue(FACING).getClockWise()) {
                return (wheelAngle-360)/24;
            } else if (direction == state.getValue(FACING).getCounterClockWise()) {
                return (360-wheelAngle)/24;
            }
        }
        return 0;
    }

//    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return (Boolean)state.getValue(POWERED) && getConnectedDirection(state) == direction ? 15 : 0;
//    }

    public void updateNeighbours(BlockState state, Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getClockWise()), this);
        level.updateNeighborsAt(pos.relative(state.getValue(FACING).getCounterClockWise()), this);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.is(newState.getBlock())) {
            this.updateNeighbours(state, world, pos);

            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneHelmBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> RedstoneHelmBlockEntity.tick(world1, pos, state1));
    }
}
