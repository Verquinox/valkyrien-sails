package com.quintonc.vs_sails.blocks;

//import com.quintonc.vs_sails.blocks.entity.SailBlockEntity;
import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class SailBlock extends Block {
    public static final BooleanProperty SET = BooleanProperty.of("set");
    public static final BooleanProperty INVISIBLE = BooleanProperty.of("invisible");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
//    public static final BooleanProperty NORTHUP = BooleanProperty.of("northup");
//    public static final BooleanProperty EASTUP = BooleanProperty.of("eastup");
//    public static final BooleanProperty SOUTHUP = BooleanProperty.of("southup");
//    public static final BooleanProperty WESTUP = BooleanProperty.of("westup");
//    public static final BooleanProperty NORTHDOWN = BooleanProperty.of("northdown");
//    public static final BooleanProperty EASTDOWN = BooleanProperty.of("eastdown");
//    public static final BooleanProperty SOUTHDOWN = BooleanProperty.of("southdown");
//    public static final BooleanProperty WESTDOWN = BooleanProperty.of("westdown");
//    protected static final Map<Direction, BooleanProperty> FACING_PROPERTIES;
    //public static final DirectionProperty FACING;
    public static final Logger LOGGER = LoggerFactory.getLogger("sail_block");
    public static final VoxelShape SET_SHAPE = Block.createCuboidShape(0,0,0,16,16,16);

    public char sailType = 'x';

    public SailBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(SET, true));
    }

//    @SuppressWarnings("deprecation")
//    public BlockState rotate(BlockState state, BlockRotation rotation) {
//        return state.with(FACING, rotation.rotate(state.get(FACING)));
//    }

//    @SuppressWarnings("deprecation")
//    public BlockState mirror(BlockState state, BlockMirror mirror) {
//        return state.rotate(mirror.getRotation(state.get(FACING)));
//    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                //.with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(SET, true)
                .with(INVISIBLE, false)
                .with(NORTH, ctx.getWorld().getBlockState(ctx.getBlockPos().north()).isOf(this))
                .with(EAST, ctx.getWorld().getBlockState(ctx.getBlockPos().east()).isOf(this))
                .with(SOUTH, ctx.getWorld().getBlockState(ctx.getBlockPos().south()).isOf(this))
                .with(WEST, ctx.getWorld().getBlockState(ctx.getBlockPos().west()).isOf(this))
                .with(UP, ctx.getWorld().getBlockState(ctx.getBlockPos().up()).isOf(this))
                .with(DOWN, ctx.getWorld().getBlockState(ctx.getBlockPos().down()).isOf(this))
//                .with(NORTHUP, ctx.getWorld().getBlockState(ctx.getBlockPos().north().up()).isOf(this))
//                .with(EASTUP, ctx.getWorld().getBlockState(ctx.getBlockPos().east().up()).isOf(this))
//                .with(SOUTHUP, ctx.getWorld().getBlockState(ctx.getBlockPos().south().up()).isOf(this))
//                .with(WESTUP, ctx.getWorld().getBlockState(ctx.getBlockPos().west().up()).isOf(this))
//                .with(NORTHDOWN, ctx.getWorld().getBlockState(ctx.getBlockPos().north().down()).isOf(this))
//                .with(EASTDOWN, ctx.getWorld().getBlockState(ctx.getBlockPos().east().down()).isOf(this))
//                .with(SOUTHDOWN, ctx.getWorld().getBlockState(ctx.getBlockPos().south().down()).isOf(this))
//                .with(WESTDOWN, ctx.getWorld().getBlockState(ctx.getBlockPos().west().down()).isOf(this))
                ;

    }

    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) {
            return;
        }

        //LOGGER.info("Sail block is added");
        if (state.get(SET)) {
            //state = state.with(INVISIBLE, false);
            //LOGGER.info("invis false");
            world.setBlockState(pos, state, 10);

            //if sail is in shipyard and the set state is changing, add it to the ship
            if (VSGameUtilsKt.isBlockInShipyard(world, pos) && (!oldState.isOf(this) || state.get(SET) != oldState.get(SET))) {
                ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
                if (ship != null) {
                    SailsShipControl controller = SailsShipControl.getOrCreate(ship);
                    addSailToShip(world, pos, controller);

                } else { //ship is being loaded from template
                    ship = VSGameUtilsKt.getShipManagingPos((ServerWorld) world, pos);
                    if (ship != null) {
                        SailsShipControl controller = SailsShipControl.getOrCreate(ship);
                        addSailToShip(world, pos, controller);
                    }
                }
            }

        } else {
            //LOGGER.info("sail is not set!");
            BlockState savedState = state;

            StateUpdater updater = new StateUpdater(state);
            updater.updateStateForDir(world.getBlockState(pos.up()), UP);
            updater.updateStateForDir(world.getBlockState(pos.down()), DOWN);
            updater.updateStateForDir(world.getBlockState(pos.north()), NORTH);
            updater.updateStateForDir(world.getBlockState(pos.south()), SOUTH);
            updater.updateStateForDir(world.getBlockState(pos.east()), EAST);
            updater.updateStateForDir(world.getBlockState(pos.west()), WEST);
//            updater.updateStateForDir(world.getBlockState(pos.up().north()), NORTHUP);
//            updater.updateStateForDir(world.getBlockState(pos.up().east()), EASTUP);
//            updater.updateStateForDir(world.getBlockState(pos.up().south()), SOUTHUP);
//            updater.updateStateForDir(world.getBlockState(pos.up().west()), WESTUP);
//            updater.updateStateForDir(world.getBlockState(pos.down().north()), NORTHDOWN);
//            updater.updateStateForDir(world.getBlockState(pos.down().south()), SOUTHDOWN);
//            updater.updateStateForDir(world.getBlockState(pos.down().east()), EASTDOWN);
//            updater.updateStateForDir(world.getBlockState(pos.down().west()), WESTDOWN);

            state = updater.state;

            if (updater.invisCounter > 3) {
                //LOGGER.info("invis set to true!");
                state = state.with(INVISIBLE, true);
            } else {
                state = state.with(INVISIBLE, false);
            }
            if (savedState != state) {
                world.setBlockState(pos, state, 10);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(Hand.MAIN_HAND).isOf(this.asItem())) {
            if (!world.isClient) {
                //if the sail is set, stow the sail, else set it
                if (state.get(SET)) {
                    state = state.with(SET, false);
                } else {
                    state = state.with(SET, true);
                }
                world.setBlockState(pos, state, 10);
                world.updateNeighbors(pos, this);
                updateDiagonals(world, pos, this);
            } else {
                boolean bl = state.get(SET);
                world.playSound(player, pos, bl ? SoundEvents.ENTITY_LEASH_KNOT_PLACE : SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.75F, world.getRandom().nextFloat() * 0.1F + 0.9F);

            }
            return ActionResult.success(world.isClient);
        }

        return ActionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        //LOGGER.info("neighborUpdate called!");
        //LOGGER.info(" " + sourceBlock.getClass());

        //if source block is a sail and is not air, check if can toggle state
        if (sourceBlock instanceof SailBlock && !world.getBlockState(sourcePos).isAir()) {
            //LOGGER.info(":)");

            //if this block's set state does not match the source block's, change it to match
            if (world.getBlockState(sourcePos).get(SET) != state.get(SET)) {
                state = state.with(SET, world.getBlockState(sourcePos).get(SET));
                world.setBlockState(pos, state, 10);
                world.updateNeighbors(pos, this);
                updateDiagonals(world, pos, this);
            }
        }
    }

    public void updateDiagonals(World world, BlockPos sourcePos, Block sourceBlock) {
        world.updateNeighbor(sourcePos.add(1, 1, 0), sourceBlock, sourcePos);
            //pos.x+1 pos.y+1 pos.z+1
            //pos.x+1 pos.y+1 pos.z-1
        world.updateNeighbor(sourcePos.add(1, -1, 0), sourceBlock, sourcePos);
            //pos.x+1 pos.y-1 pos.z+1
            //pos.x+1 pos.y-1 pos.z-1
        world.updateNeighbor(sourcePos.add(1, 0, 1), sourceBlock, sourcePos);
        world.updateNeighbor(sourcePos.add(1, 0, -1), sourceBlock, sourcePos);

        world.updateNeighbor(sourcePos.add(-1, 1, 0), sourceBlock, sourcePos);
            //pos.x-1 pos.y+1 pos.z+1
            //pos.x-1 pos.y+1 pos.z-1
        world.updateNeighbor(sourcePos.add(-1, -1, 0), sourceBlock, sourcePos);
            //pos.x-1 pos.y-1 pos.z+1
            //pos.x-1 pos.y-1 pos.z-1
        world.updateNeighbor(sourcePos.add(-1, 0, 1), sourceBlock, sourcePos);
        world.updateNeighbor(sourcePos.add(-1, 0, -1), sourceBlock, sourcePos);

        world.updateNeighbor(sourcePos.add(0, 1, 1), sourceBlock, sourcePos);
        world.updateNeighbor(sourcePos.add(0, 1, -1), sourceBlock, sourcePos);

        world.updateNeighbor(sourcePos.add(0, -1, 1), sourceBlock, sourcePos);
        world.updateNeighbor(sourcePos.add(0, -1, -1), sourceBlock, sourcePos);
    }

    public char calculateSailType(World world, BlockPos pos, SailsShipControl controller) {
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

    private void addSailToShip(World world, BlockPos pos, SailsShipControl controller) {
        //sails only add to one of the sail types when they have free opposite faces
        sailType = calculateSailType(world, pos, controller);
        if (sailType == 's') {
            controller.numSquareSails++;
            LOGGER.info("(a) NUMSQ: " + controller.numSquareSails);
        } else if (sailType == 'f') {
            controller.numFnASails++;
            LOGGER.info("(a) NUMFA: " + controller.numFnASails);
        }
        controller.numSails++;
        LOGGER.info("(a) NUMSAILS: " + controller.numSails);
    }

    private void removeSailFromShip(World world, BlockPos pos, SailsShipControl controller) {
        sailType = calculateSailType(world, pos, controller);
        if (sailType == 's') {
            controller.numSquareSails--;
            LOGGER.info("(r) NUMSQ: " + controller.numSquareSails);
        }
        if (sailType == 'f') {
            controller.numFnASails--;
            LOGGER.info("(r) NUMFA: " + controller.numFnASails);
        }
        controller.numSails--;
        LOGGER.info("(r) NUMSAILS: " + controller.numSails);
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient) {
            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
                if (ship != null) {
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    if (!newState.isAir() && !newState.get(SET) && state.get(SET) != newState.get(SET) && controller != null) {
                        removeSailFromShip(world, pos, controller);
                    }
                    if (newState.isAir() && state.get(SET) && controller != null) {
                        removeSailFromShip(world, pos, controller);
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(SET)) {
            return SET_SHAPE;
        }
        return VoxelShapes.empty();
    }

    @SuppressWarnings("deprecation")
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(SET)) {
            return SET_SHAPE;
        }
        return VoxelShapes.empty();
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return !state.get(SET);
    }

    //fixme this method of face culling only works for faces aligned with the grid
    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this) || super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SET);
        builder.add(INVISIBLE);
        builder.add(NORTH);
        builder.add(EAST);
        builder.add(SOUTH);
        builder.add(WEST);
        builder.add(UP);
        builder.add(DOWN);
//        builder.add(NORTHUP);
//        builder.add(EASTUP);
//        builder.add(SOUTHUP);
//        builder.add(WESTUP);
//        builder.add(NORTHDOWN);
//        builder.add(EASTDOWN);
//        builder.add(SOUTHDOWN);
//        builder.add(WESTDOWN);
        //FACING_PROPERTIES = (Map)ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter((entry) -> ((Direction)entry.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
    }

    private static class StateUpdater {
        BlockState state;
        int invisCounter = 0;

        public StateUpdater(BlockState state) {
            this.state = state;
        }

        public void updateStateForDir(BlockState neighborState, BooleanProperty direction) {
            if (!neighborState.isAir()) {
                if (neighborState.isOf(ValkyrienSails.SAIL_BLOCK)) {
                    invisCounter++;
                    if (neighborState.get(INVISIBLE)) {
                        state = state.with(direction, false);
                    } else {
                        state = state.with(direction, true);
                    }
                } else {
                    state = state.with(direction, true);
                }
            } else {
                state = state.with(direction, false);
            }
        }
    }
}
