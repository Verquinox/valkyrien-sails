package com.quintonc.vs_sails.blocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.Objects;

public class RiggingBlock extends CrossCollisionBlock {
    private final VoxelShape[] cullingShapes;

    public RiggingBlock(Properties settings) {
        super(2.0F, 2.0F, 16.0F, 16.0F, 16.0F, settings);
        this.registerDefaultState(this.defaultBlockState().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
        this.cullingShapes = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.cullingShapes[this.getAABBIndex(state)];
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
        boolean bl = this.canConnectToRigging(state);
        return !isExceptionForConnection(state) && neighborIsFullSquare || bl && !(state.getBlock() instanceof SailBlock);
    }

    private boolean canConnectToRigging(BlockState state) {
        TagKey<Block> tag = TagKey.create(Registries.BLOCK, new ResourceLocation("rigging"));
        return state.is(tag)  == this.defaultBlockState().is(tag);
    }

    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            ItemStack itemStack = player.getItemInHand(hand);
            return itemStack.is(Items.LEAD) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        } else {
            return LeadItem.bindPlayerMobs(player, world, pos);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockGetter blockView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        return ((((Objects.requireNonNull(super.getStateForPlacement(ctx))
                .setValue(NORTH, this.canConnect(blockState, blockState.isFaceSturdy(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH)))
                .setValue(EAST, this.canConnect(blockState2, blockState2.isFaceSturdy(blockView, blockPos3, Direction.WEST), Direction.WEST)))
                .setValue(SOUTH, this.canConnect(blockState3, blockState3.isFaceSturdy(blockView, blockPos4, Direction.NORTH), Direction.NORTH)))
                .setValue(WEST, this.canConnect(blockState4, blockState4.isFaceSturdy(blockView, blockPos5, Direction.EAST), Direction.EAST)))
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return direction.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.canConnect(neighborState, neighborState.isFaceSturdy(world, neighborPos, direction.getOpposite()), direction.getOpposite())) : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

}
