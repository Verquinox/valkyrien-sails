package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.registration.SailsBlocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;

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
        return (!isExceptionForConnection(state) && neighborIsFullSquare || bl) && !(state.getBlock() instanceof SailBlock);
    }

    private boolean canConnectToRigging(BlockState state) {
        TagKey<Block> tag = TagKey.create(Registries.BLOCK, new ResourceLocation("rigging"));
        return state.is(tag)  == this.defaultBlockState().is(tag);
    }

    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.is(Items.POTION) && PotionUtils.getPotion(heldItem) == Potions.WATER) {
            BlockState regularState = toRegularState(state);
            if (regularState != null) {
                if (!world.isClientSide) {
                    world.setBlock(pos, regularState, 10);
                    world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (world instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SPLASH,
                                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                12, 0.3D, 0.3D, 0.3D, 0.02D);
                    }
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        if (heldItem.is(Items.MAGMA_CREAM)) {
            BlockState magmaState = toMagmaCoatedState(state);
            if (magmaState != null) {
                if (!world.isClientSide) {
                    world.setBlock(pos, magmaState, 10);
                    world.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (world instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.MAGMA_CREAM)),
                                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                                10, 0.3D, 0.3D, 0.3D, 0.02D);
                    }
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        if (world.isClientSide) {
            return heldItem.is(Items.LEAD) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        } else {
            return LeadItem.bindPlayerMobs(player, world, pos);
        }
    }

    public boolean isMagmaCoated() {
        return false;
    }

    public BlockState toMagmaCoatedState(BlockState state) {
        if (isMagmaCoated()) {
            return null;
        }

        return SailsBlocks.MAGMA_RIGGING_BLOCK.get().defaultBlockState()
                .setValue(NORTH, state.getValue(NORTH))
                .setValue(EAST, state.getValue(EAST))
                .setValue(SOUTH, state.getValue(SOUTH))
                .setValue(WEST, state.getValue(WEST))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
    }

    public BlockState toRegularState(BlockState state) {
        if (!isMagmaCoated() || state.getBlock() != SailsBlocks.MAGMA_RIGGING_BLOCK.get()) {
            return null;
        }

        return SailsBlocks.RIGGING_BLOCK.get().defaultBlockState()
                .setValue(NORTH, state.getValue(NORTH))
                .setValue(EAST, state.getValue(EAST))
                .setValue(SOUTH, state.getValue(SOUTH))
                .setValue(WEST, state.getValue(WEST))
                .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
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

    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }

}
