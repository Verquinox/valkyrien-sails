//package com.quintonc.vs_sails.blocks;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.item.context.BlockPlaceContext;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.BaseEntityBlock;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.RenderShape;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.IntegerProperty;
//import org.jetbrains.annotations.Nullable;
//
//public class FurledSailBlock extends BaseEntityBlock {
//
//    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 15);
//
//    public FurledSailBlock(Properties settings) {
//        super(settings);
//
//    }
//
//    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
//        return this.defaultBlockState()
//                ;
//
//    }
//
//
//
//    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return true;
//    }
//
//    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return 5;
//    }
//
//    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return 20;
//    }
//
//
//
//
//
//    @Override
//    @SuppressWarnings("deprecation")
//    public RenderShape getRenderShape(BlockState state) {
//        return RenderShape.MODEL;
//    }
//
//
//    @Override
//    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
//        return null;
//    }
//}
