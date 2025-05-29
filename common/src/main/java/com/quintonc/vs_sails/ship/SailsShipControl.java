package com.quintonc.vs_sails.ship;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quintonc.vs_sails.ServerWindManager;
import com.quintonc.vs_sails.config.ConfigUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.*;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import static java.lang.Math.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
@SuppressWarnings({"deprecation","UnstableApiUsage"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SailsShipControl implements ShipForcesInducer, ServerTickListener {

    @JsonIgnore
    public static final Logger LOGGER = LoggerFactory.getLogger("ship_control");

    private ConcurrentLinkedQueue<Vector3dc> invForces = new ConcurrentLinkedQueue<Vector3dc>();
    private ConcurrentLinkedQueue<Vector3dc> rotForces = new ConcurrentLinkedQueue<Vector3dc>();
    private ConcurrentLinkedQueue<ForceAtPos> invPosForces = new ConcurrentLinkedQueue<ForceAtPos>();
    private ConcurrentLinkedQueue<ForceAtPos> rotPosForces = new ConcurrentLinkedQueue<ForceAtPos>();
    private ConcurrentLinkedQueue<Double> buoyForces = new ConcurrentLinkedQueue<Double>();
    private ConcurrentLinkedQueue<Double> rotTorques = new ConcurrentLinkedQueue<Double>();

//    @Volatile
//    boolean toBeStatic = false;
//
//    @Volatile
//    boolean toBeStaticUpdated = false;

    @JsonIgnore
    private static final int sailSpeed = Integer.parseInt(ConfigUtils.config.getOrDefault("sail-power","25000"));
    @JsonIgnore
    private static final boolean forgivingSails = Boolean.parseBoolean(ConfigUtils.config.getOrDefault("forgiving-sails","false"));
    @JsonIgnore
    private static final double noSailZone = toRadians((double)Integer.parseInt(ConfigUtils.config.getOrDefault("no-sail-zone", "90"))/2);
    @JsonIgnore
    private static final double keelStrength = Double.parseDouble(ConfigUtils.config.getOrDefault(
            "keel-power","4.0"));
    @JsonIgnore
    private static final double magicBallastForce = Double.parseDouble(ConfigUtils.config.getOrDefault(
            "magic-ballast-righting-force","0.25"));
    @JsonIgnore
    private static final double ballastStrength = Double.parseDouble(ConfigUtils.config.getOrDefault(
            "ballast-float-strength","0.0625"));
    @JsonIgnore
    private static final double buoyStrength = Double.parseDouble(ConfigUtils.config.getOrDefault(
            "buoy-float-strength","0.125"));

    public double rudderMod = 0;

    public int numSails = 0;
    public int numFnASails = 0;
    public int numSquareSails = 0;
    public int numBallast = 0;
    public int numMagicBallast = 0;
    public int numBuoys = 0;
    public int numHelms = 0;

    public int boundx = 1;
    public int boundz = 1;

    @JsonIgnore
    public Component message;

    public Direction shipDirection = Direction.NORTH;
    @JsonIgnore
    public ServerShip ship = null;

    public static SailsShipControl getOrCreate(ServerShip ship) {
        if (ship != null) {
            if (ship.getAttachment(SailsShipControl.class) == null) {
                ship.saveAttachment(SailsShipControl.class, new SailsShipControl());
            }
            SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
            //controller.ship = ship;
            assert controller != null;
            controller.boundx = Objects.requireNonNull(ship.getShipAABB()).maxX() - ship.getShipAABB().minX();
            controller.boundz = ship.getShipAABB().maxZ() - ship.getShipAABB().minZ();
            if (controller.boundx > controller.boundz) {
                if (controller.shipDirection != Direction.WEST && controller.shipDirection != Direction.EAST) {
                    controller.shipDirection = Direction.EAST;
                    //swap square and fna sails
                    int x = controller.numSquareSails;
                    controller.numSquareSails = controller.numFnASails;
                    controller.numFnASails = x;
                    LOGGER.info("Sail types swapped! New ship dir: " + controller.shipDirection);
                }
            } else {
                if (controller.shipDirection != Direction.SOUTH && controller.shipDirection != Direction.NORTH) {
                    controller.shipDirection = Direction.NORTH;
                    //swap square and fna sails
                    int x = controller.numSquareSails;
                    controller.numSquareSails = controller.numFnASails;
                    controller.numFnASails = x;
                    LOGGER.info("Sail types swapped! New ship dir: " + controller.shipDirection);
                }
            }

            //LOGGER.info("Xbound=" + boundx + " Zbound=" + boundz);
            //LOGGER.info("dir=" + controller.shipDirection.toString());
            return controller;
        } else {
            return null;
        }
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        //LOGGER.info("forces applied");
        PhysShipImpl physShip1 = (PhysShipImpl) physShip;

        physShip1.setDoFluidDrag(true);

        while (!invForces.isEmpty()) {
            physShip1.applyInvariantForce(Objects.requireNonNull(invForces.poll()));
            //LOGGER.info("invForce applied");
        }

        while (!rotForces.isEmpty()) {
            physShip1.applyRotDependentForce(Objects.requireNonNull(rotForces.poll()));
            //LOGGER.info("rotDependentForce applied");
        }

        while (!invPosForces.isEmpty()) {
            ForceAtPos invData = invPosForces.poll();
            if (invData != null) { //fixme if you ever come across a physpipelinecrash(too many game frames)
                physShip1.applyInvariantForceToPos(invData.force, invData.pos);
            }
            //LOGGER.info("invForceTOPOS applied");
        }
        while (!rotPosForces.isEmpty()) {
            ForceAtPos rotData = rotPosForces.poll();
            if (rotData != null && rotData.force != null && rotData.pos != null) { //fixme if rotposforces are not applying
                physShip1.applyRotDependentForceToPos(rotData.force, rotData.pos);
            }
            //LOGGER.info("rotDependentForceTOPOS applied");
        }

//        while (!rotTorques.isEmpty()) {
//            physShip1.applyRotDependentTorque(rotTorques.poll());
//        }

        //KEEL BEHAVIOR
            Vector3dc linearVelocity = physShip1.getPoseVel().getVel();

            Vector3d acceleration = linearVelocity.negate(new Vector3d());
            Vector3d force = acceleration.mul(physShip1.getInertia().getShipMass());

            force = physShip1.getTransform().getWorldToShip().transformDirection(force);

            Vector3d keelForce; //todo perhaps make this based on length/width ratio?
            if (shipDirection == Direction.NORTH || shipDirection == Direction.SOUTH) {
                keelForce = new Vector3d(force.x()*keelStrength,0,0);
            } else {
                keelForce = new Vector3d(0,0,force.z()*4);
            }

            physShip.applyRotDependentForce(keelForce);

        //Ballast behavior
//        if (numBallast > 0) {
//            //physShip1.setBuoyantFactor(1.0 + numBallast * 0.0625); //0.375
//
//        }

        if (numMagicBallast > 0) {
            Vector3d shipUp = new Vector3d(0.0, 1.0, 0.0);
            Vector3d worldUp = new Vector3d(0.0, 1.0, 0.0);
            //todo possibly modify worldUp based on wind angle & numsails to make ship heel (should really do it separately)
            physShip1.getTransform().getShipToWorldRotation().transform(shipUp);

            double angleBetween = shipUp.angle(worldUp);
            Vector3d idealAngularAcceleration = new Vector3d(0,0,0);

            if (angleBetween > 0.01) {
                Vector3d stabilizationRotationAxisNormalized = shipUp.cross(worldUp, new Vector3d()).normalize();
                idealAngularAcceleration.add(stabilizationRotationAxisNormalized.mul(
                        angleBetween, stabilizationRotationAxisNormalized)
                );
            }

            Vector3dc omega = physShip1.getPoseVel().getOmega();
            idealAngularAcceleration.sub(omega.x(), omega.y(), omega.z());

            Vector3d stabilizationTorque = physShip1.getTransform().getShipToWorldRotation().transform(
                    physShip1.getInertia().getMomentOfInertiaTensor().transform(
                            physShip1.getTransform().getShipToWorldRotation().transformInverse(idealAngularAcceleration)
                    )
            );

            stabilizationTorque.mul(numMagicBallast * magicBallastForce);
            physShip1.applyInvariantTorque(stabilizationTorque);

        }

//        if (numBuoys > 0) {
//
//        }

        if (numBallast > 0 || numBuoys > 0) {
            physShip1.setBuoyantFactor(1.0 + numBuoys * buoyStrength + numBallast * ballastStrength);
        }


//        if (ship != null) { //fixme old code
//            Vector3d rightingpos = new Vector3d(ship.getInertiaData().getCenterOfMassInShip());
//            //rightingpos.y += ship.getShipAABB().maxY()-ship.getShipAABB().minY(); //oldcode
//            rightingpos.y += numBallast*4; //newcode
//            physShip1.applyInvariantForceToPos(new Vector3d(0, physShip1.getInertia().getShipMass(), 0), rightingpos);
//        }

        //sail force implementation
        if (numSails > 0) {
            Vector3d sailForce = new Vector3d(
                    shipDirection.getNormal().getX(), shipDirection.getNormal().getY(), shipDirection.getNormal().getZ()
            );

            if (Boolean.parseBoolean(ConfigUtils.config.getOrDefault("enable-wind","true"))) {
                double windDirection = ServerWindManager.getWindDirection(); //in degrees
                double windStrength = ServerWindManager.getWindStrength(); // -1.0 -- 1.0
                double shipAngle = getShipYaw(physShip1.getTransform().getShipToWorldRotation()); //in radians
                double windAngle;
                double squareAngleBetween;
                double fnaAngleBetween;
                //LOGGER.info("wind:"+windAngle+" ship:"+shipAngle);
                if (windStrength > 0) {
                    windAngle = windDirection + 90 % 360;
                } else {
                    windAngle = windDirection + 270 % 360;
                }
                if (shipDirection == Direction.WEST) {
                    windAngle = (windAngle-90) % 360;
                } else if (shipDirection == Direction.NORTH) {
                    windAngle = (windAngle+180) % 360;
                } else if (shipDirection == Direction.EAST) {
                    windAngle = (windAngle+90) % 360;
                }

                windAngle = toRadians(windAngle);
                squareAngleBetween = abs(min(abs(shipAngle-windAngle), 2*PI - abs(shipAngle-windAngle)));
                double fnaAngle1 = abs(shipAngle+PI/2-windAngle);
                double fnaAngle2 = abs(shipAngle-PI/2-windAngle);
                fnaAngleBetween = abs(min(min(fnaAngle1, 2*PI - fnaAngle1), min(fnaAngle2, 2*PI - fnaAngle2)));
                if (squareAngleBetween > PI/2) {
                    fnaAngleBetween *= 2;
                }

                //LOGGER.info(" wa: "+Math.toDegrees(windAngle)+" sa: "+Math.toDegrees(shipAngle)+" s-w: "+(shipAngle-windAngle));
                //LOGGER.info("sab:"+toDegrees(squareAngleBetween)+" fab:"+toDegrees(fnaAngleBetween));
                //DecimalFormat f = new DecimalFormat("000.000");
                //message = Component.literal("ship: "+f.format(toDegrees(shipAngle))+" wind: "+f.format(toDegrees(windAngle))+" sAngle: "+f.format(toDegrees(squareAngleBetween))+" fAngle: "+f.format(toDegrees(fnaAngleBetween)));
                //double shipw = physShip1.getTransform().getShipToWorldRotation().w();
                //double shipx = physShip1.getTransform().getShipToWorldRotation().x();
                //double shipy = physShip1.getTransform().getShipToWorldRotation().y();
                //double shipz = physShip1.getTransform().getShipToWorldRotation().z();
                //message = Component.literal("w: "+f.format(shipw)+" x: "+f.format(shipx)+" y: "+f.format(shipy)+" z: "+f.format(shipz));

                //message = Component.literal("angle: "+toDegrees(getShipYaw(physShip1.getTransform().getShipToWorldRotation())));

                double squareWindModifier = numSquareSails/calculateWindAngleModifier(squareAngleBetween, PI-noSailZone);
                double fnAWindModifier = numFnASails/calculateWindAngleModifier(fnaAngleBetween, PI-noSailZone);

                //LOGGER.info("sqm:"+squareWindModifier+" fwm:"+fnAWindModifier);

                sailForce.mul(-(squareWindModifier+fnAWindModifier)*sailSpeed*(windStrength*windStrength));
            } else {
                sailForce.mul(-(numSquareSails+numFnASails)*sailSpeed);
            }

            //LOGGER.info("vel:" + physShip1.getPoseVel().getVel());
            if (sailForce.x > 0) {
                sailForce.x -= rudderMod;
            } else if (sailForce.x < 0) {
                sailForce.x += rudderMod;
            }

//            //LOGGER.info("sailforce="+sailForce.toString()+" shipdir="+shipDirection.toString());
            physShip1.applyRotDependentForce(sailForce);

            //LOGGER.info("noentity foce applied");
        } else if (numSails < 0) {
            numSails = 0;
            LOGGER.info("forced numSails = 0");
        }
        if (numSquareSails < 0) {
            numSquareSails = 0;
            LOGGER.info("forced numSquareSails = 0");
        }
        if (numFnASails < 0) {
            numFnASails = 0;
            LOGGER.info("forced numFnASails = 0");
        }
        if (numBallast < 0) {
            numBallast = 0;
            LOGGER.info("forced numBallast = 0");
        }
        if (numMagicBallast < 0) {
            numMagicBallast = 0;
            LOGGER.info("forced numMagicBallast = 0");
        }
        if (numBuoys < 0) {
            numBuoys = 0;
            LOGGER.info("forced numBuoys = 0");
        }
        if (numHelms < 0) {
            numHelms = 0;
            LOGGER.info("forced numHelms = 0");
        }

//        if (toBeStaticUpdated) {
//            physShip1.setStatic(toBeStatic);
//            toBeStaticUpdated = false;
//        }
    }

    public void applyInvariantForce (Vector3dc force) {
        //LOGGER.info("inv force requested");
        invForces.add(force);
    }

    public void applyRotDependentForce(Vector3dc force) {
        rotForces.add(force);
        //LOGGER.info("applyrotforce called");
    }

    public void applyInvariantForceToPos(Vector3dc force, Vector3dc pos) {
        ForceAtPos data = new ForceAtPos();
        data.force = force;
        data.pos = pos;
        invPosForces.add(data);
    }

    public void applyRotDependentForceToPos(Vector3dc force, Vector3dc pos) {
        ForceAtPos data = new ForceAtPos();
        data.force = force;
        data.pos = pos;
        rotPosForces.add(data);
        //LOGGER.info("applyrotforceTOPOS called");
    }

    private double calculateWindAngleModifier(double windAngle, double noSail) {
        if (forgivingSails) {
            return pow(2, windAngle/noSail) + pow(2, -windAngle/noSail);
        }
        return pow(2, pow(windAngle, 2)/noSail) + pow(2, -pow(windAngle, 2)/noSail);
    }

    public static double getShipYaw(Quaterniondc shipRotation) {
        Vector3d worldForwardDirection = new Vector3d();
        Vector3d LOCAL_SHIP_FORWARD_NEGATIVE_Z = new Vector3d(0.0, 0.0, -1.0);
        shipRotation.transform(LOCAL_SHIP_FORWARD_NEGATIVE_Z, worldForwardDirection);

        if (worldForwardDirection.lengthSquared() < 1.0e-12) {
            return 0.0;
        }

        double horizontalDistance = sqrt(worldForwardDirection.x * worldForwardDirection.x + worldForwardDirection.z * worldForwardDirection.z);

        double yaw;
        if (horizontalDistance < 1.0e-9) {
            yaw = 0.0;
        } else {
            yaw = atan2(worldForwardDirection.x, -worldForwardDirection.z);
        }
        if (yaw < 0) {
            yaw = 2*PI + yaw;
        }

        return yaw;
    }

    public void addBuoyancy(double buoyancy) {
        buoyForces.add(buoyancy);
    }

//    public void convertToShipDirection(double force) {
//
//    }

//    //fixme ship is always null
//    public double getShipWidth() {
//        if (shipDirection == Direction.NORTH || shipDirection == Direction.SOUTH) { //fixme change to && with != for perf
//            return ship.getShipAABB().maxX() - ship.getShipAABB().minX();
//        } else {
//            return ship.getShipAABB().maxZ() - ship.getShipAABB().minZ();
//        }
//    }
//
//    //fixme ship is always null
//    public double getShipLength() {
//        if (shipDirection == Direction.NORTH || shipDirection == Direction.SOUTH) {
//            return ship.getShipAABB().maxZ() - ship.getShipAABB().minZ();
//        } else {
//            return ship.getShipAABB().maxX() - ship.getShipAABB().minX();
//        }
//    }

//    public void setStatic(boolean b) {
//        toBeStatic = b;
//        toBeStaticUpdated = true;
//    }

    public int getNumSails() {
        return numSails;
    }
    public int getNumBallast() {
        return numBallast;
    }

    private void deleteIfEmpty() { //fixme add call for this
        if (numBallast <= 0 && numSails <= 0 && numMagicBallast <= 0 && numBuoys <= 0 && numHelms == 0) {
            ship.saveAttachment(SailsShipControl.class, null);
        }
    }

    @Override
    public void onServerTick() {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ForceAtPos {
        Vector3dc force;
        Vector3dc pos;
    }
}
