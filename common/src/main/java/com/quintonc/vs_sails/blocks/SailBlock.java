package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.registration.SailsBlocks;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.item.Item;
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

public class SailBlock extends SailToggleBlock {

    public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");

    public static final Logger LOGGER = LoggerFactory.getLogger("sail_block");
    public static final VoxelShape SET_SHAPE = Block.box(0,0,0,16,16,16);

    public char sailType = 'x';

    public SailBlock(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(INVISIBLE, true);
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

    public void updateAdjacents(Level world, BlockPos sourcePos, Block sourceBlock) {
        world.blockUpdated(sourcePos, sourceBlock);

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

    public Item getSailItem() {
        return this.asItem();
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(INVISIBLE);
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
