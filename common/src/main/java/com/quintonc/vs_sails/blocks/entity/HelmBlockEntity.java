package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.networking.PacketHandler;
import com.quintonc.vs_sails.registration.SailsBlocks;
import com.quintonc.vs_sails.ship.SailsShipControl;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

import java.util.ArrayList;
import java.util.List;

import static com.quintonc.vs_sails.blocks.HelmBlock.FACING;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static net.minecraft.world.level.block.Block.canSupportCenter;

public class HelmBlockEntity extends BlockEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger("helm_entity");

    public static final int wheelInterval = Integer.parseInt(ConfigUtils.config.getOrDefault("wheel-interval","6"));
    private static final double rudderarea = Double.parseDouble(ConfigUtils.config.getOrDefault("rudder-power","1.0"));
    public static final int maxRotations = 2; //todo make config for this
    private LoadedServerShip shipW = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel)this.level,this.getBlockPos());
    //private SailsShipControl controller = SailsShipControl.getOrCreate(shipW);
    private List<ShipMountingEntity> seats = new ArrayList<ShipMountingEntity>();

    public int rotations = 1;
    public int wheelAngle = 360;
    public static final int maxAngle = 720;

    public HelmBlockEntity(BlockPos pos, BlockState state) {
        super(ValkyrienSails.HELM_BLOCK_ENTITY, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state) {
        //do seated controlling player stuff and have their impulses affect the turnval
        // changes by some amount each tick, possibly configurable in the future, possibly based on ship mass

        if (!world.isClientSide) {

            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                ChunkPos chunkPos = world.getChunk(pos).getPos();
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, chunkPos);

                if (ship != null) {
                    SeatedControllingPlayer playerControl = ship.getAttachment(SeatedControllingPlayer.class);

                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof HelmBlockEntity blockEntity) {
                        if (playerControl != null) {
                            if (playerControl.getLeftImpulse() < 0) {
                                if (blockEntity.rotateWheelRight(state, (ServerLevel)world, pos)) {
                                    if (/*HelmBlockEntity.maxAngle/blockEntity.wheelAngle == 2 ||*/ blockEntity.wheelAngle/HelmBlockEntity.maxAngle == 1 || blockEntity.wheelAngle == 0) {
                                        world.playSound(null, pos.below(), SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON,
                                                SoundSource.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
                                        world.playSound(null, pos.below(), SoundEvents.ARMOR_EQUIP_CHAIN,
                                                SoundSource.BLOCKS, 0.75f, world.getRandom().nextFloat() * 0.1F + 0.9F);
                                    }
                                }
                            } else if (playerControl.getLeftImpulse() > 0) {
                                if (blockEntity.rotateWheelLeft(state, (ServerLevel)world, pos)) {
                                    if (/*HelmBlockEntity.maxAngle/blockEntity.wheelAngle == 2 ||*/ blockEntity.wheelAngle/HelmBlockEntity.maxAngle == 1 || blockEntity.wheelAngle == 0) {
                                        world.playSound(null, pos.below(), SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON, //fixme redundant code
                                                SoundSource.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
                                        world.playSound(null, pos.below(), SoundEvents.ARMOR_EQUIP_CHAIN,
                                                SoundSource.BLOCKS, 0.75f, world.getRandom().nextFloat() * 0.1F + 0.9F);
                                    }
                                }
                            }

//                            if (playerControl.getLeftImpulse() < 0) {
//                                if (state.getValue(WHEEL_ANGLE) > 0) {
//                                    state = state.setValue(WHEEL_ANGLE, state.getValue(WHEEL_ANGLE)-wheelInterval);
//                                    world.setBlock(pos, state, 10);
//                                    if (state.getValue(WHEEL_ANGLE) == 720 || state.getValue(WHEEL_ANGLE) == 360 || state.getValue(WHEEL_ANGLE) == 0) {
//                                        world.playSound(null, pos.below(), SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON,
//                                                SoundSource.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
//                                    }
//                                }
//                            } else if (playerControl.getLeftImpulse() > 0) {
//                                if (state.getValue(WHEEL_ANGLE) < 720) {
//                                    state = state.setValue(WHEEL_ANGLE, state.getValue(WHEEL_ANGLE)+wheelInterval);
//                                    world.setBlock(pos, state, 10);
//                                    if (state.getValue(WHEEL_ANGLE) == 720 || state.getValue(WHEEL_ANGLE) == 360 || state.getValue(WHEEL_ANGLE) == 0) {
//                                        world.playSound(null, pos.below(), SoundEvents.BAMBOO_WOOD_BUTTON_CLICK_ON,
//                                                SoundSource.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
//                                    }
//    //                                else if (state.get(WHEEL_ANGLE) % 15 == 0) { fixme code for additional wheel sfx
//    //                                    world.playSound(null, pos.down(), SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF,
//    //                                            SoundSource.BLOCKS, 0.25f, world.getRandom().nextFloat() * 0.1F + 0.9F);
//    //                                }
//                                }
//                            }
                        }

                        //Matrix3dc moiTensor = ship.getInertiaData().getMomentOfInertiaTensor();

                        //find rudder hinge point
                        Vector3dc com = ship.getInertiaData().getCenterOfMassInShip();
                        double rudderOffset = (ship.getShipAABB().maxX() - ship.getShipAABB().minX());
                        Vec3i shipDir = SailsShipControl.getOrCreate(ship, world).shipDirection.getOpposite().getNormal();
                        Vector3d loc = new Vector3d(com.x()+(rudderOffset*shipDir.getX()), com.y(), com.z()+(rudderOffset*shipDir.getZ())+1); //fixme shipDir.getZ()?
                        Vector3d loc2 = new Vector3d(com.x()+(rudderOffset*shipDir.getX()),com.y(),com.z()+(rudderOffset*shipDir.getZ())-1);
                        Vector3d rudderPos = new Vector3d(com.x()+(rudderOffset*shipDir.getX()),com.y(),com.z()+(rudderOffset*shipDir.getZ())); //fixme new

                        //size of the ship's 'rudder'
                        AABBic shipDims = ship.getShipAABB();
                        double rudderSize = rudderarea * ((shipDims.maxX() - shipDims.minX()) * (shipDims.maxZ() - shipDims.minZ())) / 100.0;
                        //todo make method in SailsShipController for getting dimensions (length+width)

                        //angle of ship's 'rudder' (in radians) based on wheel position
                        //double rudderAngle = (((double)state.getValue(WHEEL_ANGLE)-360) / 10) * Math.PI/180;
                        double rudderAngle = (((double)blockEntity.wheelAngle-360) / 10) * Math.PI/180;

                        //mass of ship
                        double mass = ship.getInertiaData().getMass() / 100;

                        //ship velocity
                        double vel = Math.sqrt(Math.pow(ship.getVelocity().x(), 2) + Math.pow(ship.getVelocity().z(), 2));

                        //rudder force to be applied to rudder hinge point
                        double rudderForce;
                        if (Boolean.parseBoolean(ConfigUtils.config.getOrDefault("realistic-rudder","true"))) {
                            rudderForce = (2 * Math.PI * rudderAngle) * 998 / 6 * sqrt(mass) * Math.pow(vel, 2) * rudderSize;
                        } else {
                            rudderForce = (2 * Math.PI * rudderAngle) * 998 * sqrt(mass) * rudderSize;
                        }

                        Vector3d turnvector = new Vector3d(rudderForce+mass, 0, 0);
                        Vector3d turnvector2 = new Vector3d(-rudderForce-mass, 0, 0);
                        Vector3d turnvector3 = new Vector3d(0, 0, rudderForce); //fixme new for lift-based rudder force (remove mass from turnvector)

                        //LOGGER.info("VEL:"+vel);

                        SailsShipControl shipForceApplier = ship.getAttachment(SailsShipControl.class);
                        if (shipForceApplier != null) {
                            shipForceApplier.applyRotDependentForceToPos(turnvector, loc.sub(ship.getTransform().getPositionInShip()));
                            shipForceApplier.applyRotDependentForceToPos(turnvector2, loc2.sub(ship.getTransform().getPositionInShip()));
                            //fixme wip lift-based rudder force
//                        if (shipForceApplier.shipDirection == Direction.NORTH || shipForceApplier.shipDirection == Direction.SOUTH) {
//                            shipForceApplier.applyRotDependentForceToPos(turnvector.mul(0.00001), rudderPos);
//                        } else {
//                            shipForceApplier.applyRotDependentForceToPos(turnvector3.mul(0.00001), rudderPos);
//                        }
                        }
                        setChanged(world, pos, state);
                    }
                }
            }
        }
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
            success = true;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(wheelAngle);
        buf.writeBlockPos(pos);
        NetworkManager.sendToPlayers(world.getServer().getPlayerList().getPlayers(), PacketHandler.WHEEL_ANGLE_PACKET, buf);

//        if (wheelAngle-wheelInterval == 0 && rotations > 1) {
//            rotations--;
//            state = state.setValue(WHEEL_ANGLE, 360);
//            world.setBlock(pos, state, 10);
//        } else if (wheelAngle-wheelInterval != wheelAngle-wheelInterval % 360 && rotations > 1) {
//            rotations--;
//            state = state.setValue(WHEEL_ANGLE, (wheelAngle-wheelInterval) % 360);
//            world.setBlock(pos, state, 10);
//        } else {
//            state = state.setValue(WHEEL_ANGLE, wheelAngle-wheelInterval);
//            world.setBlock(pos, state, 10);
//        }
        return success;
    }

    public boolean rotateWheelLeft(BlockState state, ServerLevel world, BlockPos pos) {
        boolean success = false;
        if (wheelAngle+wheelInterval <= 720) {
            wheelAngle+=wheelInterval;
            success = true;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(wheelAngle);
        buf.writeBlockPos(pos);
        NetworkManager.sendToPlayers(world.getServer().getPlayerList().getPlayers(), PacketHandler.WHEEL_ANGLE_PACKET, buf);

//        if (wheelAngle+wheelInterval <= 360) {
//            state = state.setValue(WHEEL_ANGLE, wheelAngle+wheelInterval);
//            world.setBlock(pos, state, 10);
//        } else if (rotations < maxRotations) {
//            rotations++;
//            state = state.setValue(WHEEL_ANGLE, (wheelAngle+wheelInterval) % 360);
//            world.setBlock(pos, state, 10);
//        }
        return success;
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

    public ItemStack getRenderStack() {
        return new ItemStack(SailsBlocks.HELM_WHEEL.get().asItem());
    }

    public int getWheelAngle() {
        return 0;
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
