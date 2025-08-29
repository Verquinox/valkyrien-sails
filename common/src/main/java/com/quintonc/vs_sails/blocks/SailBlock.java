package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.registration.SailsBlocks;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.level.block.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class SailBlock extends Block {
    public static final BooleanProperty SET = BooleanProperty.create("set");
    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
//    protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES;
    //public static final DirectionProperty FACING;
    public static final Logger LOGGER = LoggerFactory.getLogger("sail_block");
    public static final VoxelShape SET_SHAPE = Block.box(0,0,0,16,16,16);

    public char sailType = 'x';

    public SailBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(SET, true));
    }

//    @SuppressWarnings("deprecation")
//    public BlockState rotate(BlockState state, BlockRotation rotation) {
//        return state.with(FACING, rotation.rotate(state.get(FACING)));
//    }

//    @SuppressWarnings("deprecation")
//    public BlockState mirror(BlockState state, BlockMirror mirror) {
//        return state.rotate(mirror.getRotation(state.get(FACING)));
//    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                //.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .setValue(SET, true)
                .setValue(INVISIBLE, false)
                .setValue(NORTH, ctx.getLevel().getBlockState(ctx.getClickedPos().north()).is(this))
                .setValue(EAST, ctx.getLevel().getBlockState(ctx.getClickedPos().east()).is(this))
                .setValue(SOUTH, ctx.getLevel().getBlockState(ctx.getClickedPos().south()).is(this))
                .setValue(WEST, ctx.getLevel().getBlockState(ctx.getClickedPos().west()).is(this))
                .setValue(UP, ctx.getLevel().getBlockState(ctx.getClickedPos().above()).is(this))
                .setValue(DOWN, ctx.getLevel().getBlockState(ctx.getClickedPos().below()).is(this))
                ;

    }

    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }

        //LOGGER.info("Sail block is added");
        if (state.getValue(SET)) {
            //state = state.with(INVISIBLE, false);
            //LOGGER.info("invis false");
            world.setBlock(pos, state, 10);

            //if sail is in shipyard and the set state is changing, add it to the ship
            if (VSGameUtilsKt.isBlockInShipyard(world, pos) && (!oldState.is(this) || state.getValue(SET) != oldState.getValue(SET))) {
                ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
                if (ship != null) {
                    SailsShipControl controller = SailsShipControl.getOrCreate(ship, world);
                    addSailToShip(world, pos, controller);

                } else { //ship is being loaded from template
                    ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
                    if (ship != null) {
                        SailsShipControl controller = SailsShipControl.getOrCreate(ship, world);
                        addSailToShip(world, pos, controller);
                    }
                }
            }

        } else {
            //LOGGER.info("sail is not set!");
            BlockState savedState = state;

            StateUpdater updater = new StateUpdater(state);
            updater.updateStateForDir(world.getBlockState(pos.above()), UP);
            updater.updateStateForDir(world.getBlockState(pos.below()), DOWN);
            updater.updateStateForDir(world.getBlockState(pos.north()), NORTH);
            updater.updateStateForDir(world.getBlockState(pos.south()), SOUTH);
            updater.updateStateForDir(world.getBlockState(pos.east()), EAST);
            updater.updateStateForDir(world.getBlockState(pos.west()), WEST);

            state = updater.state;

            if (updater.invisCounter > 3) {
                //LOGGER.info("invis set to true!");
                state = state.setValue(INVISIBLE, true);
            } else {
                state = state.setValue(INVISIBLE, false);
            }
            if (savedState != state) {
                world.setBlock(pos, state, 10);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(this.asItem())) {
            if (!world.isClientSide) {
                //if the sail is set, stow the sail, else set it
                if (state.getValue(SET)) {
                    state = state.setValue(SET, false);
                } else {
                    state = state.setValue(SET, true);
                }
                world.setBlock(pos, state, 10);
                world.blockUpdated(pos, this);
                updateDiagonals(world, pos, this);
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
        if (sourceBlock instanceof SailBlock && !world.getBlockState(sourcePos).isAir()) {
            //LOGGER.info(":)");

            //if this block's set state does not match the source block's, change it to match
            BlockState sourceState = world.getBlockState(sourcePos);
            if (sourceState.hasProperty(SET) && sourceState.getValue(SET) != state.getValue(SET)) {
                state = state.setValue(SET, sourceState.getValue(SET));
                world.setBlock(pos, state, 10);
                world.blockUpdated(pos, this);
                updateDiagonals(world, pos, this);
            }
        }
    }

    public void updateDiagonals(Level world, BlockPos sourcePos, Block sourceBlock) {
        world.neighborChanged(sourcePos.offset(1, 1, 0), sourceBlock, sourcePos);
            //pos.x+1 pos.y+1 pos.z+1
            //pos.x+1 pos.y+1 pos.z-1
        world.neighborChanged(sourcePos.offset(1, -1, 0), sourceBlock, sourcePos);
            //pos.x+1 pos.y-1 pos.z+1
            //pos.x+1 pos.y-1 pos.z-1
        world.neighborChanged(sourcePos.offset(1, 0, 1), sourceBlock, sourcePos);
        world.neighborChanged(sourcePos.offset(1, 0, -1), sourceBlock, sourcePos);

        world.neighborChanged(sourcePos.offset(-1, 1, 0), sourceBlock, sourcePos);
            //pos.x-1 pos.y+1 pos.z+1
            //pos.x-1 pos.y+1 pos.z-1
        world.neighborChanged(sourcePos.offset(-1, -1, 0), sourceBlock, sourcePos);
            //pos.x-1 pos.y-1 pos.z+1
            //pos.x-1 pos.y-1 pos.z-1
        world.neighborChanged(sourcePos.offset(-1, 0, 1), sourceBlock, sourcePos);
        world.neighborChanged(sourcePos.offset(-1, 0, -1), sourceBlock, sourcePos);

        world.neighborChanged(sourcePos.offset(0, 1, 1), sourceBlock, sourcePos);
        world.neighborChanged(sourcePos.offset(0, 1, -1), sourceBlock, sourcePos);

        world.neighborChanged(sourcePos.offset(0, -1, 1), sourceBlock, sourcePos);
        world.neighborChanged(sourcePos.offset(0, -1, -1), sourceBlock, sourcePos);
    }

    public char calculateSailType(Level world, BlockPos pos, SailsShipControl controller) {
        if (!world.getBlockState(pos.north()).isAir() || !world.getBlockState(pos.south()).isAir()) {
            if (world.getBlockState(pos.east()).isAir() && world.getBlockState(pos.west()).isAir()) {
                if (controller.shipDirection == Direction.EAST || controller.shipDirection == Direction.WEST) {
                    return 's'; //todo use static final strings to make this more readable
                } else {
                    return 'f';
                }
            } else {
                return 'x';
            }
        } else if (!world.getBlockState(pos.east()).isAir() || !world.getBlockState(pos.west()).isAir()) {
            if (world.getBlockState(pos.north()).isAir() && world.getBlockState(pos.south()).isAir()) {
                if (controller.shipDirection == Direction.EAST || controller.shipDirection == Direction.WEST) {
                    return 'f';
                } else {
                    return 's';
                }
            }
        }
        //sail is neither square nor fore&aft. Should only happen if sail has no horizontal adjacent blocks (or has more than one occupied side)
        return 'x';
    }

    private void addSailToShip(Level world, BlockPos pos, SailsShipControl controller) {
        //sails only add to one of the sail types when they have free opposite faces
        sailType = calculateSailType(world, pos, controller);
        if (sailType == 's') {
            controller.numSquareSails++;
            //LOGGER.info("(a) NUMSQ: " + controller.numSquareSails);
        } else if (sailType == 'f') {
            controller.numFnASails++;
            //LOGGER.info("(a) NUMFA: " + controller.numFnASails);
        }
        controller.numSails++;
        //LOGGER.info("(a) NUMSAILS: " + controller.numSails);
    }

    private void removeSailFromShip(Level world, BlockPos pos, SailsShipControl controller) {
        sailType = calculateSailType(world, pos, controller);
        if (sailType == 's') {
            controller.numSquareSails--;
            //LOGGER.info("(r) NUMSQ: " + controller.numSquareSails);
        }
        if (sailType == 'f') {
            controller.numFnASails--;
            //LOGGER.info("(r) NUMFA: " + controller.numFnASails);
        }
        controller.numSails--;
        //LOGGER.info("(r) NUMSAILS: " + controller.numSails);
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClientSide) {
            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
                if (ship != null) {
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    if (!newState.isAir() && !newState.getValue(SET) && state.getValue(SET) != newState.getValue(SET) && controller != null) {
                        removeSailFromShip(world, pos, controller);
                    }
                    if (newState.isAir() && state.getValue(SET) && controller != null) {
                        removeSailFromShip(world, pos, controller);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(SET)) {
            return SET_SHAPE;
        }
        return Shapes.empty();
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(SET)) {
            return SET_SHAPE;
        }
        return Shapes.empty();
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return !state.getValue(SET);
    }

    //fixme this method of face culling only works for faces aligned with the grid
    @SuppressWarnings("deprecation")
    @Override
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.is(this) || super.skipRendering(state, stateFrom, direction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SET);
        builder.add(INVISIBLE);
        builder.add(NORTH);
        builder.add(EAST);
        builder.add(SOUTH);
        builder.add(WEST);
        builder.add(UP);
        builder.add(DOWN);
        //FACING_PROPERTIES = (Map)ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter((entry) -> ((Direction)entry.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
    }

    private static class StateUpdater {
        BlockState state;
        int invisCounter = 0;

        public StateUpdater(BlockState state) {
            this.state = state;
        }

        public void updateStateForDir(BlockState neighborState, BooleanProperty direction) {
            //LOGGER.info("state update called");
            if (!neighborState.isAir()) {
                if (neighborState.getBlock() instanceof SailBlock) {
                    invisCounter++;
                    //LOGGER.info("invis="+invisCounter);
                    if (neighborState.getValue(INVISIBLE)) {
                        state = state.setValue(direction, false);
                    } else {
                        state = state.setValue(direction, true);
                    }
                } else {
                    state = state.setValue(direction, true);
                }
            } else {
                state = state.setValue(direction, false);
            }
        }
    }
}
