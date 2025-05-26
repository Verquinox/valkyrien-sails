package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;

public class HelmBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final IntegerProperty WHEEL_ANGLE = IntegerProperty.create("wheel_angle", 0, 720);
    public static final Logger LOGGER = LoggerFactory.getLogger("helm_block");
    //private static final VoxelShape SHAPE = Block.createCuboidShape(1,0,5,15,13,11);
    public static final int wheelInterval = Integer.parseInt(ConfigUtils.config.getOrDefault("wheel-interval","6"));

    public HelmBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(WHEEL_ANGLE, 360));
    }

//    @Override
//    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        return SHAPE;
//    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(WHEEL_ANGLE, 360);
    }

    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                SailsShipControl controller = SailsShipControl.getOrCreate(ship);

                controller.numHelms++;

                //if the ship's direction is opposite the helm (helm faces backwards), set it to its opposite
                if (Objects.equals(controller.shipDirection, state.getValue(FACING))) {
                    controller.shipDirection = controller.shipDirection.getOpposite();
                }
            } //TODO add else for template placing (see corresponding spot in SailBlock)
        }
    }

    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof HelmBlockEntity) {
                //((HelmBlockEntity) be).sit(player);

                if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                    //LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
                    //assert ship != null;
                    //SailsShipControl controller = SailsShipControl.getOrCreate(ship);

                    //old code for adding ShipMountingEntity
//                    ShipMountingEntity mounter = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(world);
//                    //BlockPos newPos = pos.offset(state.get(BlockStateProperties.FACING));
//
//                    Vector3dc mounterPos;
//                    if (state.get(FACING) == Direction.NORTH) {
//                        mounterPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() + 1.3125);
//                    } else if (state.get(FACING) == Direction.SOUTH) {
//                        mounterPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() - 0.3125);
//                    } else if (state.get(FACING) == Direction.EAST) {
//                        mounterPos = new Vector3d(pos.getX() - 0.3125, pos.getY() + 0.125, pos.getZ() + .5);
//                    } else {
//                        mounterPos = new Vector3d(pos.getX() + 1.3125, pos.getY() + 0.125, pos.getZ() + .5);
//                    }
//
//                    //LOGGER.info("posx="+mounterPos.x()+"posy="+mounterPos.y()+"posz="+mounterPos.z());
//
//                    assert mounter != null;
//                    mounter.setPos(mounterPos.x(), mounterPos.y(), mounterPos.z());
//                    Vec3d blah2 = new Vec3d(0, 0, 0);
//                    mounter.move(MovementType.SELF, blah2);
//                    Vec3i facingVec1 = state.get(FACING).getOpposite().getVector();
//                    Vector3d beep = ship.getShipToWorld().transformPosition(new Vector3d((facingVec1.getX()+pos.getX()), pos.getY(), facingVec1.getZ()+pos.getZ()));
//                    Vec3d beep2 = new Vec3d(beep.x,beep.y,beep.z);
//                    mounter.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, beep2);
////                    Vec3d blah = new Vec3d(state.get(FACING).getVector().getX(), state.get(FACING).getVector().getY(), state.get(FACING).getVector().getZ());
////                    mounter.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, blah);
//                    mounter.setController(true);
//                    world.spawnEntity(mounter);
//
//                    player.startRiding(mounter, false);
//                    //player.setMainArm(Arm.LEFT);

                    HelmBlockEntity blockEntity = (HelmBlockEntity) world.getBlockEntity(pos);
                    assert blockEntity != null;
                    blockEntity.sit(player);


//                    SeatedControllingPlayer playerControl;
//                    if (ship.getAttachment(SeatedControllingPlayer.class) == null) {
//                        playerControl = new SeatedControllingPlayer(world.getBlockState(pos).get(FACING));
//                    } else {
//                        playerControl = ship.getAttachment(SeatedControllingPlayer.class);
//                    }

                    //LOGGER.info("player="+mounter.getControllingPassenger());

                } else {
                    //seat player
                    if (player.isShiftKeyDown() && state.getValue(WHEEL_ANGLE) > 0) {
                        state = state.setValue(WHEEL_ANGLE, state.getValue(WHEEL_ANGLE)-wheelInterval);
                        world.setBlock(pos, state, 10);
                    } else if (state.getValue(WHEEL_ANGLE) < 720) {
                        state = state.setValue(WHEEL_ANGLE, state.getValue(WHEEL_ANGLE)+wheelInterval);
                        world.setBlock(pos, state, 10);
                    }
                    player.displayClientMessage(Component.literal("Angle: "+ (state.getValue(WHEEL_ANGLE) - 360)), true);
                }

                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClientSide) {
            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
                if (ship != null) {
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    assert controller != null;
                    controller.numHelms--;
                }
            }
        }

        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HelmBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ValkyrienSails.HELM_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> HelmBlockEntity.tick(world1, pos, state1));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(WHEEL_ANGLE);
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
    }
}
