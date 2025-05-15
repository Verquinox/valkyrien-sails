package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;

public class HelmBlock extends BlockWithEntity {
    public static final DirectionProperty FACING;
    public static final IntProperty WHEEL_ANGLE = IntProperty.of("wheel_angle", 0, 720);
    public static final Logger LOGGER = LoggerFactory.getLogger("helm_block");
    //private static final VoxelShape SHAPE = Block.createCuboidShape(1,0,5,15,13,11);

    public HelmBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WHEEL_ANGLE, 360));
    }

//    @Override
//    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        return SHAPE;
//    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(WHEEL_ANGLE, 360);
    }

    @SuppressWarnings("deprecation")
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) {
            return;
        }
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            if (ship != null) {
                SailsShipControl controller = SailsShipControl.getOrCreate(ship);

                controller.numHelms++;

                //if the ship's direction is opposite the helm (helm faces backwards), set it to its opposite
                if (Objects.equals(controller.shipDirection, state.get(FACING))) {
                    controller.shipDirection = controller.shipDirection.getOpposite();
                }
            } //TODO add else for template placing (see corresponding spot in SailBlock)
        }
    }

    @SuppressWarnings("deprecation")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof HelmBlockEntity) {
                //((HelmBlockEntity) be).sit(player);

                if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                    //LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
                    //assert ship != null;
                    //SailsShipControl controller = SailsShipControl.getOrCreate(ship);

                    //old code for adding ShipMountingEntity
//                    ShipMountingEntity mounter = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(world);
//                    //BlockPos newPos = pos.offset(state.get(Properties.FACING));
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
                    if (player.isSneaking() && state.get(WHEEL_ANGLE) > 0) {
                        state = state.with(WHEEL_ANGLE, state.get(WHEEL_ANGLE)-6);
                        world.setBlockState(pos, state, 10);
                    } else if (state.get(WHEEL_ANGLE) < 720) {
                        state = state.with(WHEEL_ANGLE, state.get(WHEEL_ANGLE)+6);
                        world.setBlockState(pos, state, 10);
                    }
                    player.sendMessage(Text.of("Angle: "+ (state.get(WHEEL_ANGLE) - 360)), true);
                }

                return ActionResult.success(world.isClient);
            }
        }

        return ActionResult.success(world.isClient);
    }

    @SuppressWarnings({"deprecation","UnstableApiUsage"})
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!world.isClient) {
            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
                if (ship != null) {
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    assert controller != null;
                    controller.numHelms--;
                }
            }
        }

        if (state.hasBlockEntity() && !state.isOf(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HelmBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ValkyrienSailsJava.HELM_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> HelmBlockEntity.tick(world1, pos, state1));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(WHEEL_ANGLE);
    }

    static {
        FACING = Properties.HORIZONTAL_FACING;
    }
}
