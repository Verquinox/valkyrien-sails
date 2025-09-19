package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.networking.PacketHandler;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

import java.util.ArrayList;
import java.util.List;

import static com.quintonc.vs_sails.blocks.HelmBlock.FACING;
import static java.lang.Math.sqrt;

public abstract class BaseHelmBlockEntity extends BlockEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger("base_helm_entity");

    public static int wheelInterval;
    private List<ShipMountingEntity> seats = new ArrayList<ShipMountingEntity>();

    public int wheelAngle;
    public float renderWheelAngle = 360f;
    public float renderWheelAngleVel = 0;
    public static int maxAngle;

    public BaseHelmBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public boolean startRiding(Player player, boolean force, BlockPos pos, BlockState state, ServerLevel world) {

        for (int i = seats.size()-1; i > 0; i--) {
            if (!seats.get(i).isPassenger()) {
                seats.get(i).kill();
                seats.remove(i);
            } else if (!seats.get(i).isAlive()) {
                seats.remove(i);
            }
        }

        ShipMountingEntity seat = spawnSeat(pos, state, world);
        boolean ride = player.startRiding(seat, force);

        if (ride) {
            //controller. = player
            seats.add(seat);
        }

        return ride;
    }

    ShipMountingEntity spawnSeat(BlockPos pos, BlockState state, ServerLevel world) {
        BlockPos newPos = pos.relative(state.getValue(FACING).getOpposite());

        Vector3dc mounterPos;
        if (state.getValue(FACING) == Direction.NORTH) {
            mounterPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() + 1.3125);
        } else if (state.getValue(FACING) == Direction.SOUTH) {
            mounterPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() - 0.3125);
        } else if (state.getValue(FACING) == Direction.EAST) {
            mounterPos = new Vector3d(pos.getX() - 0.3125, pos.getY() + 0.125, pos.getZ() + .5);
        } else {
            mounterPos = new Vector3d(pos.getX() + 1.3125, pos.getY() + 0.125, pos.getZ() + .5);
        }

        ShipMountingEntity entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(world);

        assert entity != null;
        entity.setPos(mounterPos.x(), mounterPos.y(), mounterPos.z());
        entity.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(pos.getX(),pos.getY(),pos.getZ()));
        entity.move(MoverType.SELF, new Vec3(0, 0, 0));
        entity.setController(true);
        world.addFreshEntityWithPassengers(entity);
        return entity;
    }

    public boolean sit(Player player) {
        boolean force = false;
//        if (!force && player.getVehicle().getType() == ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE && seats.contains((ShipMountingEntity) player.getVehicle())) {
//
//        }

        return startRiding(player, force, this.getBlockPos(), this.getBlockState(), (ServerLevel) level);
    }

    @Override
    public void setRemoved() {
        assert level != null;
        if (!level.isClientSide) {
            for (int i = seats.size()-1; i > 0; i--) {
                seats.get(i).kill();
            }
            seats.clear();
        }

        super.setRemoved();
    }

    public boolean rotateWheelRight(BlockState state, ServerLevel world, BlockPos pos) {
        boolean success = false;
        if (wheelAngle-wheelInterval >= 0) {
            wheelAngle-=wheelInterval;
            playWheelSounds(world, pos);
            success = true;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(wheelAngle);
        buf.writeBlockPos(pos);
        //buf.writeFloat(world.getServer().getAverageTickTime());
        NetworkManager.sendToPlayers(world.getServer().getPlayerList().getPlayers(), PacketHandler.WHEEL_ANGLE_PACKET, buf);

        return success;
    }

    public boolean rotateWheelLeft(BlockState state, ServerLevel world, BlockPos pos) {
        boolean success = false;
        if (wheelAngle+wheelInterval <= 720) {
            wheelAngle+=wheelInterval;
            playWheelSounds(world, pos);
            success = true;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(wheelAngle);
        buf.writeBlockPos(pos);
        //buf.writeFloat(world.getServer().getAverageTickTime());
        NetworkManager.sendToPlayers(world.getServer().getPlayerList().getPlayers(), PacketHandler.WHEEL_ANGLE_PACKET, buf);

        return success;
    }

    private void playWheelSounds(Level world, BlockPos pos) {
        if ((double)wheelAngle/ BaseHelmBlockEntity.maxAngle == 0.5) {
            world.playSound(null, pos.below(), SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON,
                    SoundSource.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
            world.playSound(null, pos.below(), SoundEvents.ARMOR_EQUIP_CHAIN,
                    SoundSource.BLOCKS, 0.1f, world.getRandom().nextFloat() * 0.1F + 0.9F);
        } else if (wheelAngle == BaseHelmBlockEntity.maxAngle || wheelAngle == 0) {
            world.playSound(null, pos.below(), SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON,
                    SoundSource.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("wheel_angle", wheelAngle);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        wheelAngle = pTag.getInt("wheel_angle");
    }

    public abstract ItemStack getRenderStack();

    public int getWheelAngle() {
        return wheelAngle;
    }

    @Override
    public void setChanged() {
        assert level != null;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        super.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

}
