package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.registration.SailsBlocks;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class HelmBlockEntity extends BaseHelmBlockEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger("helm_entity");

    private static final double rudderarea = Double.parseDouble(ConfigUtils.config.getOrDefault("rudder-power","1.0"));

    public HelmBlockEntity(BlockPos pos, BlockState state) {
        super(ValkyrienSails.HELM_BLOCK_ENTITY, pos, state);
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
                    if (be instanceof HelmBlockEntity blockEntity) {
                        if (playerControl != null) {
                            if (playerControl.getLeftImpulse() < 0) {
                                blockEntity.rotateWheelRight(state, (ServerLevel)world, pos);
                            } else if (playerControl.getLeftImpulse() > 0) {
                                blockEntity.rotateWheelLeft(state, (ServerLevel)world, pos);
                            }
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
                            //todo replace with a method in SailsShipControl that takes in a rudder angle and updates a field that is used for force application
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
}
