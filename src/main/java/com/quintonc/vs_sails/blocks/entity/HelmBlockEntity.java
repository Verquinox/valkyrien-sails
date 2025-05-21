package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import com.quintonc.vs_sails.blocks.HelmBlock;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
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
//import static com.quintonc.vs_sails.blocks.HelmBlock.WHEEL_ANGLE;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class HelmBlockEntity extends BlockEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger("helm_entity");

    public int wheelAngle;
    protected final PropertyDelegate propertyDelegate;
    public static final int wheelInterval = Integer.parseInt(ConfigUtils.config.getOrDefault("wheel-interval","6"));
    private static final double rudderarea = Double.parseDouble(ConfigUtils.config.getOrDefault("rudder-power","1.0"));
    private LoadedServerShip shipW = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld)this.world,this.getPos());
    //private SailsShipControl controller = SailsShipControl.getOrCreate(shipW);
    private List<ShipMountingEntity> seats = new ArrayList<ShipMountingEntity>();

    public HelmBlockEntity(BlockPos pos, BlockState state) {
        super(ValkyrienSailsJava.HELM_BLOCK_ENTITY, pos, state);
        wheelAngle = 360;
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0 -> {
                        return HelmBlockEntity.this.wheelAngle;
                    }
                    default -> {
                        return 2;
                    }
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> HelmBlockEntity.this.wheelAngle = value;
                }
            }
            public int size() {
                return 1;
            }
        };
    }

    public static void tick(World world, BlockPos pos, BlockState state, HelmBlockEntity entity) {
        //do seated controlling player stuff and have their impulses affect the turnval
        // changes by some amount each tick, possibly configurable in the future, possibly based on ship mass

        if (!world.isClient) {

            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                ChunkPos chunkPos = world.getChunk(pos).getPos();
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, chunkPos);

                if (ship != null) {
                    SeatedControllingPlayer playerControl = ship.getAttachment(SeatedControllingPlayer.class);
                    if (playerControl != null) {
                        if (playerControl.getLeftImpulse() < 0) {
                            if (entity.wheelAngle > 0) {
                                entity.wheelAngle -= wheelInterval;
                                if (entity.wheelAngle == 720 || entity.wheelAngle == 360 || entity.wheelAngle == 0) {
                                    world.playSound(null, pos.down(), SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON,
                                            SoundCategory.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
                                }
                            }
                        } else if (playerControl.getLeftImpulse() > 0) {
                            if (entity.wheelAngle < 720) {
                                entity.wheelAngle += wheelInterval;
                                if (entity.wheelAngle == 720 || entity.wheelAngle == 360 || entity.wheelAngle == 0) {
                                    world.playSound(null, pos.down(), SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON,
                                            SoundCategory.BLOCKS, 1.5f, world.getRandom().nextFloat() * 0.1F + 0.9F);
                                }
//                                else if (state.get(WHEEL_ANGLE) % 15 == 0) { fixme code for additional wheel sfx
//                                    world.playSound(null, pos.down(), SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF,
//                                            SoundCategory.BLOCKS, 0.25f, world.getRandom().nextFloat() * 0.1F + 0.9F);
//                                }
                            }
                        }
                    }

                    //Matrix3dc moiTensor = ship.getInertiaData().getMomentOfInertiaTensor();

                    //find rudder hinge point
                    Vector3dc com = ship.getInertiaData().getCenterOfMassInShip();
                    double rudderOffset = (ship.getShipAABB().maxX() - ship.getShipAABB().minX());
                    Vec3i shipDir = SailsShipControl.getOrCreate(ship).shipDirection.getOpposite().getVector();
                    Vector3d loc = new Vector3d(com.x()+(rudderOffset*shipDir.getX()), com.y(), com.z()+(rudderOffset*shipDir.getZ())+1); //fixme shipDir.getZ()?
                    Vector3d loc2 = new Vector3d(com.x()+(rudderOffset*shipDir.getX()),com.y(),com.z()+(rudderOffset*shipDir.getZ())-1);
                    Vector3d rudderPos = new Vector3d(com.x()+(rudderOffset*shipDir.getX()),com.y(),com.z()+(rudderOffset*shipDir.getZ())); //fixme new

                    //size of the ship's 'rudder'
                    AABBic shipDims = ship.getShipAABB();
                    double rudderSize = rudderarea * ((shipDims.maxX() - shipDims.minX()) * (shipDims.maxZ() - shipDims.minZ())) / 100.0;
                    //todo make method in SailsShipController for getting dimensions (length+width)

                    //angle of ship's 'rudder' (in radians) based on wheel position
                    double rudderAngle = (((double)entity.wheelAngle-360) / 10) * Math.PI/180;

                    //mass of ship
                    double mass = ship.getInertiaData().getMass() / 100;

                    //ship velocity
                    double vel = Math.sqrt(Math.pow(ship.getVelocity().x(), 2) + Math.pow(ship.getVelocity().z(), 2));

                    //rudder force to be applied to rudder hinge point
                    double rudderForce;
                    if (Boolean.parseBoolean(ConfigUtils.config.getOrDefault("realistic-rudder","true"))) {
                        rudderForce = (2 * Math.PI * rudderAngle) * 998 / 5 * sqrt(mass) * Math.pow(vel, 2) * rudderSize;
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
                    markDirty(world, pos, state);
                }

            }
        }
    }

    public boolean startRiding(PlayerEntity player, boolean force, BlockPos pos, BlockState state, ServerWorld world) {

        for (int i = seats.size()-1; i > 0; i--) {
            if (!seats.get(i).hasVehicle()) {
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

    ShipMountingEntity spawnSeat(BlockPos pos, BlockState state, ServerWorld world) {
        BlockPos newPos = pos.offset(state.get(FACING).getOpposite());

        Vector3dc mounterPos;
        if (state.get(FACING) == Direction.NORTH) {
            mounterPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() + 1.3125);
        } else if (state.get(FACING) == Direction.SOUTH) {
            mounterPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.125, pos.getZ() - 0.3125);
        } else if (state.get(FACING) == Direction.EAST) {
            mounterPos = new Vector3d(pos.getX() - 0.3125, pos.getY() + 0.125, pos.getZ() + .5);
        } else {
            mounterPos = new Vector3d(pos.getX() + 1.3125, pos.getY() + 0.125, pos.getZ() + .5);
        }

        ShipMountingEntity entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(world);

        assert entity != null;
        entity.setPos(mounterPos.x(), mounterPos.y(), mounterPos.z());
        entity.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(pos.getX(),pos.getY(),pos.getZ()));
        entity.move(MovementType.SELF, new Vec3d(0, 0, 0));
        entity.setController(true);
        world.spawnNewEntityAndPassengers(entity);
        return entity;
    }

    public boolean sit(PlayerEntity player) {
        boolean force = false;
//        if (!force && player.getVehicle().getType() == ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE && seats.contains((ShipMountingEntity) player.getVehicle())) {
//
//        }

        return startRiding(player, force, this.getPos(), this.getCachedState(), (ServerWorld) world);
    }

    @Override
    public void markRemoved() {
        assert world != null;
        if (!world.isClient) {
            for (int i = seats.size()-1; i > 0; i--) {
                seats.get(i).kill();
            }
            seats.clear();
        }

        super.markRemoved();
    }

    public ItemStack getRenderStack() {
        return new ItemStack(ValkyrienSailsJava.HELM_WHEEL.asItem());
    }

    @Override
    public Integer getRenderData() {
        return wheelAngle;
    }

    @Override
    public void markDirty() {
        assert world != null;
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        super.markDirty();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

}
