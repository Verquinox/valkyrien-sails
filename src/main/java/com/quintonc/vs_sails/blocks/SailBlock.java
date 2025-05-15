package com.quintonc.vs_sails.blocks;

//import com.quintonc.vs_sails.blocks.entity.SailBlockEntity;
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
                .with(INVISIBLE, false);

    }

    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) {
            return;
        }

        //LOGGER.info("Sail block is added");
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            if (ship != null) {
                SailsShipControl controller = SailsShipControl.getOrCreate(ship);
                if (state.get(SET)) {
                    state = state.with(INVISIBLE, false);
                    world.setBlockState(pos, state, 10);
                    //sails only add to numsails when they have free opposite faces
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
                } else {
                    //LOGGER.info("sail is not set!");
                    //up down north south east west
                    int i = 0;
                    if (world.getBlockState(pos.up()).isOf(this)) {
                        i++;
                    }
                    if (world.getBlockState(pos.down()).isOf(this)) {
                        i++;
                    }
                    if (world.getBlockState(pos.north()).isOf(this)) {
                        i++;
                    }
                    if (world.getBlockState(pos.south()).isOf(this)) {
                        i++;
                    }
                    if (world.getBlockState(pos.east()).isOf(this)) {
                        i++;
                    }
                    if (world.getBlockState(pos.west()).isOf(this)) {
                        i++;
                    }

                    if (i > 3) {
                        LOGGER.info("invis set to true!");
                        state = state.with(INVISIBLE, true);
                        world.setBlockState(pos, state, 10);
                    }
                }
            } //TODO add else to make sure sails add correctly on template load (need some refactoring to make it good)
        }
    }

    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
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
        }

        boolean bl = state.get(SET);
        world.playSound(player, pos, bl ? SoundEvents.ENTITY_LEASH_KNOT_PLACE : SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.75F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        return ActionResult.success(world.isClient);
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

        //logic for only providing force when faces are clear
//        if (!world.isClient) {
//            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
//                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
//                assert ship != null;
//                SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
//                char newSailType = calculateSailType(world, pos, controller);
//                if (sailType != newSailType && state.get(SET) && controller != null) {
//                    if (sailType == 's') {
//                        controller.numSquareSails--;
//                        LOGGER.info("(u) NUMSQ: " + controller.numSquareSails);
//                        if (newSailType == 'f') {
//                            controller.numFnASails++;
//                            LOGGER.info("(u) NUMFA: " + controller.numFnASails);
//                        }
//                    } else if (sailType == 'f') {
//                        controller.numFnASails--;
//                        LOGGER.info("(u) NUMFA: " + controller.numFnASails);
//                        if (newSailType == 's') {
//                            controller.numSquareSails++;
//                            LOGGER.info("(u) NUMSQ: " + controller.numSquareSails);
//                        }
//                    }
//                    sailType = newSailType;
//                }
//            }
//        }
        //if state is set fixme logic is not perfect; might try to indicate sail activation with a blockstate
            //if sourceblock is solid && sourceblock.pos == adjacent to sail face
                //            if (!world.getBlockState(pos.north()).isAir() && state.get(FACING) == Direction.NORTH) {
                //                controller.numSails--;
                //            }
                //numsails--;
            //if nonsolid block or air is found at both faces
                //numsails++;
    }

    public void updateDiagonals(World world, BlockPos sourcePos, Block sourceBlock) {
        //make this only apply to sails facing the same direction
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
                    return 's';
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

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient) {
            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
                if (ship != null) {
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    if (!newState.isAir() && !newState.get(SET) && controller != null) {
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
                    if (newState.isAir() && state.get(SET) && controller != null) {
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
                }
            }
        }

//        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) { //fixme remove
//            world.removeBlockEntity(pos);
//        }
    }

//    @Nullable
//    @Override
//    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
//        return new SailBlockEntity(pos, state);
//    }

//    @Nullable
//    @Override
//    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
//        return checkType(type, ValkyrienSailsJava.SAIL_BLOCK_ENTITY, SailBlockEntity::tick);
//    }

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
    }
}
