package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
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

    public static final Logger LOGGER = LoggerFactory.getLogger("sail_block");
    public static final VoxelShape SET_SHAPE = Block.box(0,0,0,16,16,16);

    public SailBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(SET, true).setValue(INVISIBLE, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(SET, true);
    }

    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }

        //LOGGER.info("Sail block is added");
        if (state.getValue(SET)) {

            world.setBlock(pos, state, 10);

            //if sail is in shipyard and the set state is changing, add it to the ship
            if (VSGameUtilsKt.isBlockInShipyard(world, pos) && (!oldState.is(this) || state.getValue(SET) != oldState.getValue(SET))) {
                ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
                if (ship != null) {
                    SailsShipControl controller = SailsShipControl.getOrCreate((LoadedServerShip) ship, world);
                    addSailToShip(world, pos, controller);

                } else { //ship is being loaded from template
                    ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
                    if (ship instanceof LoadedServerShip) {
                        SailsShipControl controller = SailsShipControl.getOrCreate((LoadedServerShip) ship, world);
                        addSailToShip(world, pos, controller);
                    }
                }
            }

        }
//        else {
//            //LOGGER.info("sail is not set!");
//            BlockState savedState = state;
//
//            StateUpdater updater = new StateUpdater(state);
//            updater.updateStateForDir(world.getBlockState(pos.above()), UP);
//            updater.updateStateForDir(world.getBlockState(pos.below()), DOWN);
//            updater.updateStateForDir(world.getBlockState(pos.north()), NORTH);
//            updater.updateStateForDir(world.getBlockState(pos.south()), SOUTH);
//            updater.updateStateForDir(world.getBlockState(pos.east()), EAST);
//            updater.updateStateForDir(world.getBlockState(pos.west()), WEST);
//
//            state = updater.state;
//
//            if (updater.invisCounter > 3) {
//                //LOGGER.info("invis set to true!");
//                state = state.setValue(INVISIBLE, true);
//            } else {
//                state = state.setValue(INVISIBLE, false);
//            }
//            if (savedState != state) {
//                world.setBlock(pos, state, 10);
//            }
//        }
    }

    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        TagKey<Item> tag = TagKey.create(Registries.ITEM, new ResourceLocation("sail_togglers"));
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(tag)) {
            if (!world.isClientSide) {
                toggleFirstSail(state, world, pos);
            } else {
                boolean bl = state.getValue(SET);
                world.playSound(player, pos, bl ? SoundEvents.LEASH_KNOT_PLACE : SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.75F, world.getRandom().nextFloat() * 0.1F + 0.9F);

            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return InteractionResult.PASS;
    }

    public void toggleFirstSail(BlockState state, Level world, BlockPos pos) { //todo make this called by rope as well
        //if the sail is set, stow the sail, else set it
        if (state.getValue(SET)) {
            toggleOff(state, world, pos);
        } else {
            state = state.setValue(SET, true);
            state = state.setValue(INVISIBLE, false);
            world.setBlock(pos, state, 10);
            updateAdjacents(world, pos, this);
        }
        //BlockState state2 = SailsBlocks.HELM_WHEEL.get().defaultBlockState();


    }

    public void toggleOff(BlockState state, Level world, BlockPos pos) {

        if ( // a sail should become a "furled" sail if:
                // it has a non-sail block directly or diagonally adjacent to it
                //try horizontalFaceNeighbors.stream().anyMatch(offset -> isNotSailOrAir(world, pos.offset(offset)))
                isNotSailOrAir(world, pos.offset(1, 1, 0))
                        || isNotSailOrAir(world, pos.offset(-1, 1, 0))
                        || isNotSailOrAir(world, pos.offset(0, 1, 1))
                        || isNotSailOrAir(world, pos.offset(0, 1, -1))
                        || isNotSailOrAir(world, pos.offset(1, -1, 0))
                        || isNotSailOrAir(world, pos.offset(-1, -1, 0))
                        || isNotSailOrAir(world, pos.offset(0, -1, 1))
                        || isNotSailOrAir(world, pos.offset(0, -1, -1))
                        || isNotSailOrAir(world, pos.above())
                        || isNotSailOrAir(world, pos.below())
                // it has air directly above it and fewer than 2 sail blocks diagonally above it
                        //|| (world.getBlockState(pos.above()).isAir() && fewerThanXSailsHorizontally(world, pos.above(), 2))
                // it has a sail above it, fewer than 3 sail blocks diagonally above it, and at least 2 sail blocks below it
                        //|| (world.getBlockState(pos.above()).getBlock() instanceof SailBlock && fewerThanXSailsHorizontally(world, pos.above(), 3) && atLeastXSailsHorizontally(world, pos.below(), 2))
        ) {
            //fixme THE PROBLEM AREA
            //if (atLeastXSailsHorizontally(world, pos.above(), 1) && !world.getBlockState(pos.above()).isAir()/*has a solid block above*/ && hasMismatchedOppositeAir(world, pos)) { //also check if sail has above and below perpendicular to the spar
               // state = state.setValue(INVISIBLE, true);
            //} else {
            //if (atLeastXSailsHorizontally(world, pos.above(), 1) && atLeastXSailsHorizontally(world, pos.below(), 1)) {
                //if (noBlockHereOrAboveOrBelow(world, pos.north()) != noBlockHereOrAboveOrBelow(world, pos.south()) || noBlockHereOrAboveOrBelow(world, pos.east()) != noBlockHereOrAboveOrBelow(world, pos.west())) {
                //    state = state.setValue(INVISIBLE, true);
                //} else {
                //    state = state.setValue(INVISIBLE, false);
                //}
            //} else {
                state = state.setValue(INVISIBLE, false);
            //}

            //}
        } else {
            state = state.setValue(INVISIBLE, true);
        }

        state = state.setValue(SET, false);
        world.setBlock(pos, state, 10);
        updateAdjacents(world, pos, this);
    }

    private boolean isNotSailOrAir(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return !(state.getBlock() instanceof SailBlock || state.isAir() || !state.isCollisionShapeFullBlock(world, pos));
    }

    private boolean fewerThanXSailsHorizontally(Level world, BlockPos pos, int x) {
        return countHorizontallyAdjacentSails(world, pos) < x;
    }

    private boolean atLeastXSailsHorizontally(Level world, BlockPos pos, int x) {
        return countHorizontallyAdjacentSails(world, pos) >= x;
    }

    //private List<Vec3i> horizontalFaceNeighbors = List.of(Direction.NORTH.getNormal(), Direction.SOUTH.getNormal(), Direction.EAST.getNormal(), Direction.WEST.getNormal());
    //private List<Vec3i> horizontalEdgeNeighbors = List.of(new Vec3i(1, 0, 1), new Vec3i());

    private int countHorizontallyAdjacentSails(Level world, BlockPos pos) {
        int n = 0;
        if (world.getBlockState(pos).getBlock() instanceof SailBlock) {n++;}
//        for (var offset : horizontalFaceNeighbors) {
//            if (world.getBlockState(pos.offset(offset)).getBlock() instanceof SailBlock) {
//                n++;
//            }
//        }
        if (world.getBlockState(pos.north()).getBlock() instanceof SailBlock) {n++;}
        if (world.getBlockState(pos.south()).getBlock() instanceof SailBlock) {n++;}
        if (world.getBlockState(pos.east()).getBlock() instanceof SailBlock) {n++;}
        if (world.getBlockState(pos.west()).getBlock() instanceof SailBlock) {n++;}
        if (world.getBlockState(pos.east().north()).getBlock() instanceof SailBlock) {n++;}
        if (world.getBlockState(pos.east().south()).getBlock() instanceof SailBlock) {n++;}
        if (world.getBlockState(pos.west().north()).getBlock() instanceof SailBlock) {n++;}
        if (world.getBlockState(pos.west().south()).getBlock() instanceof SailBlock) {n++;}
        return n;
    }

    private boolean noBlockHereOrAboveOrBelow(Level world, BlockPos pos) {
        return isNotSailOrAir(world, pos) && isNotSailOrAir(world, pos.above()) && isNotSailOrAir(world, pos.below());
    }

    private int countHorizontallyAdjacentSolids(Level world, BlockPos pos) {
        int n = 0;
        if (!world.getBlockState(pos).isAir()) {n++;}
        if (!world.getBlockState(pos.north()).isAir()) {n++;}
        if (!world.getBlockState(pos.south()).isAir()) {n++;}
        if (!world.getBlockState(pos.east()).isAir()) {n++;}
        if (!world.getBlockState(pos.west()).isAir()) {n++;}
        if (!world.getBlockState(pos.east().north()).isAir()) {n++;}
        if (!world.getBlockState(pos.east().south()).isAir()) {n++;}
        if (!world.getBlockState(pos.west().north()).isAir()) {n++;}
        if (!world.getBlockState(pos.west().south()).isAir()) {n++;}
        return n;
    }

    //kill me
    private boolean hasMismatchedOppositeAir(Level world, BlockPos pos) {
        return ((world.getBlockState(pos.north()).isAir() != world.getBlockState(pos.south()).isAir()) && (world.getBlockState(pos.north()).getBlock() instanceof SailBlock || world.getBlockState(pos.south()).getBlock() instanceof SailBlock)) ||
                ((world.getBlockState(pos.east()).isAir() != world.getBlockState(pos.west()).isAir()) && (world.getBlockState(pos.east()).getBlock() instanceof SailBlock || world.getBlockState(pos.west()).getBlock() instanceof SailBlock)) ||
                ((world.getBlockState(pos.north().east()).isAir() != world.getBlockState(pos.south().west()).isAir()) && (world.getBlockState(pos.north().east()).getBlock() instanceof SailBlock || world.getBlockState(pos.south().west()).getBlock() instanceof SailBlock)) ||
                ((world.getBlockState(pos.north().west()).isAir() != world.getBlockState(pos.south().east()).isAir()) && (world.getBlockState(pos.north().west()).getBlock() instanceof SailBlock || world.getBlockState(pos.south().east()).getBlock() instanceof SailBlock));
    }

    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        //LOGGER.info("neighborUpdate called!");
        //LOGGER.info(" " + sourceBlock.getClass());

        //if source block is a sail and is not air, check if can toggle state
        if ((sourceBlock instanceof SailBlock || sourceBlock instanceof SailToggleBlock) && !world.getBlockState(sourcePos).isAir()) {
            //LOGGER.info(":)");

            //if this block's set state does not match the source block's, change it to match
            BlockState sourceState = world.getBlockState(sourcePos);
            if (sourceState.hasProperty(SET) && sourceState.getValue(SET) != state.getValue(SET)) {
                if (sourceState.getValue(SET)) {
                    //toggle on
                    state = state.setValue(SET, true);
                    state = state.setValue(INVISIBLE, false);
                    world.setBlock(pos, state, 10);
                    updateAdjacents(world, pos, this);
                } else {
                    toggleOff(state, world, pos);
                }
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
                    return 's'; //fixme use static final strings to make this more readable
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
        char sailType = calculateSailType(world, pos, controller);
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
        char sailType = calculateSailType(world, pos, controller);
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
                    if (!newState.isAir() && newState.hasProperty(SET) && !newState.getValue(SET) && state.getValue(SET) != newState.getValue(SET) && controller != null) {
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

    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(INVISIBLE)) {
            return Shapes.empty();
        }
        return SET_SHAPE;
    }

//    @SuppressWarnings("deprecation")
//    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
//        if (state.getValue(INVISIBLE)) {
//            return Shapes.empty();
//        }
//        return SET_SHAPE;
//    }

//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
//        if (state.getValue(INVISIBLE)) {
//            return Block.box(0,0,0,16,16,16);
//        } else {
//            return SET_SHAPE;
//        }
//    }

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
    }

//    private static class StateUpdater {
//        BlockState state;
//        int invisCounter = 0;
//
//        public StateUpdater(BlockState state) {
//            this.state = state;
//        }
//
//        public void updateStateForDir(BlockState neighborState, BooleanProperty direction) {
//            //LOGGER.info("state update called");
//            if (!neighborState.isAir()) {
//                if (neighborState.getBlock() instanceof SailBlock) {
//                    invisCounter++;
//                    //LOGGER.info("invis="+invisCounter);
//                    if (neighborState.getValue(INVISIBLE)) {
//                        state = state.setValue(direction, false);
//                    } else {
//                        state = state.setValue(direction, true);
//                    }
//                } else {
//                    state = state.setValue(direction, true);
//                }
//            } else {
//                state = state.setValue(direction, false);
//            }
//        }
//    }
}
