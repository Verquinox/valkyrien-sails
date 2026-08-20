package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.RedstoneHelmBlock;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.registration.SailsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static com.quintonc.vs_sails.blocks.RedstoneHelmBlock.LEFT;
import static com.quintonc.vs_sails.blocks.RedstoneHelmBlock.RIGHT;
import static java.lang.Math.sqrt;

public class RedstoneHelmBlockEntity extends BaseHelmBlockEntity {

    public RedstoneHelmBlockEntity(BlockPos pos, BlockState blockState) {
        super(ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY, pos, blockState);
        wheelAngle = 360;
        maxAngle = 720;
        wheelInterval = Integer.parseInt(ConfigUtils.config.getOrDefault("wheel-interval","6"));
    }

    public static void tick(Level world, BlockPos pos, BlockState state) {
        //do seated controlling player stuff and have their impulses affect the turnval
        // changes by some amount each tick

        if (!world.isClientSide) {

            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                ChunkPos chunkPos = world.getChunk(pos).getPos();
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, chunkPos);

                if (ship != null) {
                    SeatedControllingPlayer playerControl = ship.getAttachment(SeatedControllingPlayer.class);

                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof RedstoneHelmBlockEntity blockEntity) {
                        if (playerControl != null) {
                            if (playerControl.getLeftImpulse() < 0) {
                                blockEntity.rotateWheelRight(state, (ServerLevel)world, pos);
                                Block block = state.getBlock();
                                if (block instanceof RedstoneHelmBlock redstoneHelmBlock) {
                                    redstoneHelmBlock.updateNeighbours(state, world, pos);
                                }
                            } else if (playerControl.getLeftImpulse() > 0) {
                                blockEntity.rotateWheelLeft(state, (ServerLevel)world, pos);
                                Block block = state.getBlock();
                                if (block instanceof RedstoneHelmBlock redstoneHelmBlock) {
                                    redstoneHelmBlock.updateNeighbours(state, world, pos);
                                }
                            }
                        }

                        //todo perform redstone helm logic here

                        setChanged(world, pos, state);
                    }
                }
            }
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof RedstoneHelmBlockEntity blockEntity) {
                if (state.is(SailsBlocks.REDSTONE_HELM_BLOCK.get())) {
                    BlockState oldState = state;
                    if (blockEntity.wheelAngle > 360) {
                        state = state.setValue(LEFT, true);
                        state = state.setValue(RIGHT, false);
                    } else if (blockEntity.wheelAngle < 360) {
                        state = state.setValue(LEFT, false);
                        state = state.setValue(RIGHT, true);
                    } else {
                        state = state.setValue(LEFT, false);
                        state = state.setValue(RIGHT, false);
                    }
                    if (!oldState.equals(state)) {
                        world.setBlock(pos, state, 10);
                    }
                }
            }
        }
    }
}
