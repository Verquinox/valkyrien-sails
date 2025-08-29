package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Objects;

public class HelmBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    public static final Logger LOGGER = LoggerFactory.getLogger("helm_block");
    //private static final VoxelShape SHAPE = Block.createCuboidShape(1,0,5,15,13,11);
    public static final int wheelInterval = Integer.parseInt(ConfigUtils.config.getOrDefault("wheel-interval","6"));

    public HelmBlock(Properties settings) {
        super(settings);
    }

//    @Override
//    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
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
                .setValue(FACING, ctx.getHorizontalDirection());
    }

    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                SailsShipControl controller = SailsShipControl.getOrCreate(ship, world);

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
            if (be instanceof HelmBlockEntity blockEntity) {
                if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                    blockEntity.sit(player);

                } else {
                    //todo make this properly work with non 360 factors (the two methods)
                    if (player.isShiftKeyDown()) {
                        blockEntity.rotateWheelLeft(state, (ServerLevel)world, pos);
                    } else {
                        blockEntity.rotateWheelRight(state, (ServerLevel)world, pos);
                    }
                    //player.displayClientMessage(Component.literal("Angle: "+ (state.getValue(WHEEL_ANGLE) + (blockEntity.rotations - 1) * 360) + " Rotations: " + blockEntity.rotations), true);
                    player.displayClientMessage(Component.literal("Angle: "+ blockEntity.wheelAngle), true);
                }
                return InteractionResult.sidedSuccess(false);
            }
        }
        return InteractionResult.sidedSuccess(false);
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
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
    }
}
