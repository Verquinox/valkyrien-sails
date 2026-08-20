package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.registration.SailsBlocks;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;

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

        TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation("sail_togglers"));
        if (!heldItem.is(tag)) {
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

    public BlockState toMagmaCoatedState(BlockState state) {
        if (isMagmaCoated() || state.getBlock() != SailsBlocks.ROPE_BLOCK.get()) {
            return null;
        }

        return SailsBlocks.MAGMA_ROPE_BLOCK.get().defaultBlockState()
                .setValue(SET, state.getValue(SET))
                .setValue(NORTH, state.getValue(NORTH))
                .setValue(EAST, state.getValue(EAST))
                .setValue(SOUTH, state.getValue(SOUTH))
                .setValue(WEST, state.getValue(WEST))
                .setValue(UP, state.getValue(UP))
                .setValue(DOWN, state.getValue(DOWN));
    }

    public BlockState toRegularState(BlockState state) {
        if (!isMagmaCoated() || state.getBlock() != SailsBlocks.MAGMA_ROPE_BLOCK.get()) {
            return null;
        }

        return SailsBlocks.ROPE_BLOCK.get().defaultBlockState()
                .setValue(SET, state.getValue(SET))
                .setValue(NORTH, state.getValue(NORTH))
                .setValue(EAST, state.getValue(EAST))
                .setValue(SOUTH, state.getValue(SOUTH))
                .setValue(WEST, state.getValue(WEST))
                .setValue(UP, state.getValue(UP))
                .setValue(DOWN, state.getValue(DOWN));
    }

    public boolean isMagmaCoated() {
        return false;
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
