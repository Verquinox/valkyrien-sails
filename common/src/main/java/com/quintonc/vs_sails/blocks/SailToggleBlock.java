package com.quintonc.vs_sails.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class SailToggleBlock extends Block {

    public static final BooleanProperty SET = BooleanProperty.create("set");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public SailToggleBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(SET, true));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(SET, true)
                .setValue(NORTH, ctx.getLevel().getBlockState(ctx.getClickedPos().north()).is(this))
                .setValue(EAST, ctx.getLevel().getBlockState(ctx.getClickedPos().east()).is(this))
                .setValue(SOUTH, ctx.getLevel().getBlockState(ctx.getClickedPos().south()).is(this))
                .setValue(WEST, ctx.getLevel().getBlockState(ctx.getClickedPos().west()).is(this))
                .setValue(UP, ctx.getLevel().getBlockState(ctx.getClickedPos().above()).is(this))
                .setValue(DOWN, ctx.getLevel().getBlockState(ctx.getClickedPos().below()).is(this))
                ;

    }

    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation("sail_togglers"));
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(tag)) {
            if (!world.isClientSide) {
                //if the sail is set, stow the sail, else set it
                if (state.getValue(SET)) {
                    state = state.setValue(SET, false);
                } else {
                    state = state.setValue(SET, true);
                }
                world.setBlock(pos, state, 10);
                updateAdjacents(world, pos, this);
            } else {
                boolean bl = state.getValue(SET);
                world.playSound(player, pos, bl ? SoundEvents.LEASH_KNOT_PLACE : SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, world.getRandom().nextFloat() * 0.1F + 0.9F);

            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        //LOGGER.info("neighborUpdate called!");
        //LOGGER.info(" " + sourceBlock.getClass());

        //if source block is a sail and is not air, check if can toggle state
        if ((sourceBlock instanceof SailToggleBlock || sourceBlock instanceof SailBlock) && !world.getBlockState(sourcePos).isAir()) {
            //LOGGER.info(":)");

            //if this block's set state does not match the source block's, change it to match
            BlockState sourceState = world.getBlockState(sourcePos);
            if (sourceState.hasProperty(SET) && sourceState.getValue(SET) != state.getValue(SET)) {
                state = state.setValue(SET, sourceState.getValue(SET));
                world.setBlock(pos, state, 10);
                updateAdjacents(world, pos, this);
            }
        }
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

    protected abstract void updateAdjacents(Level world, BlockPos pos, Block sourceBlock);

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SET);
        builder.add(NORTH);
        builder.add(EAST);
        builder.add(SOUTH);
        builder.add(WEST);
        builder.add(UP);
        builder.add(DOWN);
    }
}
