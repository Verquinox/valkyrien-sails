package com.quintonc.vs_sails.blocks;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class RopeBlock extends SailToggleBlock {

    public static final Logger LOGGER = LoggerFactory.getLogger("rope_block");

    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;

    public RopeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockGetter blockView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        BlockPos northPos = blockPos.north();
        BlockPos eastPos = blockPos.east();
        BlockPos southPos = blockPos.south();
        BlockPos westPos = blockPos.west();
        BlockPos upPos = blockPos.above();
        BlockPos downPos = blockPos.below();
        BlockState northState = blockView.getBlockState(northPos);
        BlockState eastState = blockView.getBlockState(eastPos);
        BlockState southState = blockView.getBlockState(southPos);
        BlockState westState = blockView.getBlockState(westPos);
        BlockState upState = blockView.getBlockState(upPos);
        BlockState downState = blockView.getBlockState(downPos);
        return ((((((Objects.requireNonNull(this.defaultBlockState()).setValue(SET, true)
                .setValue(NORTH, this.canConnect(northState, northState.isFaceSturdy(blockView, northPos, Direction.SOUTH))))
                .setValue(EAST, this.canConnect(eastState, eastState.isFaceSturdy(blockView, eastPos, Direction.WEST))))
                .setValue(SOUTH, this.canConnect(southState, southState.isFaceSturdy(blockView, southPos, Direction.NORTH))))
                .setValue(WEST, this.canConnect(westState, westState.isFaceSturdy(blockView, westPos, Direction.EAST))))
                .setValue(UP, this.canConnect(upState, upState.isFaceSturdy(blockView, upPos, Direction.DOWN))))
                .setValue(DOWN, this.canConnect(downState, downState.isFaceSturdy(blockView, downPos, Direction.UP))))
                ;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape finalShape = Block.box(6,6,6,10,10,10);
        if (state.getValue(NORTH)) {
            finalShape = Shapes.or(finalShape, Block.box(6,6,0,10,10,10));
        }
        if (state.getValue(SOUTH)) {
            finalShape = Shapes.or(finalShape, Block.box(6,6,6,10,10,16));
        }
        if (state.getValue(EAST)) {
            finalShape = Shapes.or(finalShape, Block.box(6,6,6,16,10,10));
        }
        if (state.getValue(WEST)) {
            finalShape = Shapes.or(finalShape, Block.box(0,6,6,10,10,10));
        }
        if (state.getValue(UP)) {
            finalShape = Shapes.or(finalShape, Block.box(6,6,6,10,16,10));
        }
        if (state.getValue(DOWN)) {
            finalShape = Shapes.or(finalShape, Block.box(6,0,6,10,10,10));
        }
        return finalShape;
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

//    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
//
//        if (world.isClientSide) {
//            return;
//        }
//
//
//    }

        @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        //if (!(sourceBlock instanceof SailBlock)) { //todo add config for allowing ropes to connect isolated sails
            super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
        //}
    }

    @Override
    protected void updateAdjacents(Level world, BlockPos pos, Block sourceBlock) {
        world.blockUpdated(pos, sourceBlock);
    }

    public boolean canConnect(BlockState state, boolean neighborIsFullSquare) {
        boolean bl = this.canConnectToRope(state);
        return !isExceptionForConnection(state) && neighborIsFullSquare || bl;
    }

    private boolean canConnectToRope(BlockState state) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation("sail_togglers"));
        return state.getBlock().asItem().getDefaultInstance().is(tag) == this.asItem().getDefaultInstance().is(tag);
    }

    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.canConnect(neighborState, neighborState.isFaceSturdy(world, neighborPos, direction.getOpposite())));
    }

//    public void updateStateForDir(Level world, BlockState state, BlockState neighborState, BooleanProperty dirVal, Direction sourceDir) {
//        //LOGGER.info("state update called");
//        if (!neighborState.isFaceSturdy(world, sourceDir)) {
//            state.setValue(dirVal, true);
//        } else {
//            state.setValue(dirVal, false);
//        }
//    }
    static {
        PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().collect(Util.toMap());
    }
}
