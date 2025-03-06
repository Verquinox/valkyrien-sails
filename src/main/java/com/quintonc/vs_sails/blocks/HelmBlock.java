package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;
import org.valkyrienskies.mod.common.util.VSServerLevel;

public class HelmBlock extends BlockWithEntity {
    public static final DirectionProperty FACING;
    public static final IntProperty WHEEL_ANGLE = IntProperty.of("wheel_angle", 0, 720);

    public HelmBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(WHEEL_ANGLE, 360));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing())
                .with(WHEEL_ANGLE, 360);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        if (state.get(WHEEL_ANGLE) > 0) {
//            state = (BlockState)state.with(WHEEL_ANGLE, state.get(WHEEL_ANGLE)+1);
//            world.setBlockState(pos, state, 10);
//        }

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof HelmBlockEntity) {
                //seat player
                player.sendMessage(Text.of("Angle: "+ (state.get(WHEEL_ANGLE) - 360)));
                if (player.isSneaking()) {
                    state = state.with(WHEEL_ANGLE, state.get(WHEEL_ANGLE)-10);
                    world.setBlockState(pos, state, 10);
                } else {
                    state = state.with(WHEEL_ANGLE, state.get(WHEEL_ANGLE)+10);
                    world.setBlockState(pos, state, 10);
                }

                //set position?
                //world.(be);
                //ShipMountingEntity mounter = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(world);

                //mounter.setPos(pos.getX(), pos.getY(), pos.getZ());
                //world.add
                //need to do something to set this as the controlling entity?
                //why is addfreshentity not existing
                //player.startRiding(mounter);
                //return ActionResult.SUCCESS;
            }
        }

        return ActionResult.success(world.isClient);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HelmBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
        builder.add(new Property[]{WHEEL_ANGLE});
    }

    static {
        FACING = Properties.HORIZONTAL_FACING;
    }
}
