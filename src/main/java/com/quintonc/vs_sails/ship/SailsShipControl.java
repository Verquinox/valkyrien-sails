package com.quintonc.vs_sails.ship;

import kotlin.jvm.Volatile;
import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ServerTickListener;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SailsShipControl implements ShipForcesInducer, ServerTickListener {

    //public static final Logger LOGGER = LoggerFactory.getLogger("ship_control");

    private ConcurrentLinkedQueue<Vector3dc> invForces = new ConcurrentLinkedQueue<Vector3dc>();
    private ConcurrentLinkedQueue<Vector3dc> rotForces = new ConcurrentLinkedQueue<Vector3dc>();
    private ConcurrentLinkedQueue<ForceAtPos> invPosForces = new ConcurrentLinkedQueue<ForceAtPos>();
    private ConcurrentLinkedQueue<ForceAtPos> rotPosForces = new ConcurrentLinkedQueue<ForceAtPos>();
    private ConcurrentLinkedQueue<Double> buoyForces = new ConcurrentLinkedQueue<Double>();

    @Volatile
    boolean toBeStatic = false;

    @Volatile
    boolean toBeStaticUpdated = false;

    public int numSails = 0;

    private static final int sailSpeed = 10000;

    public static SailsShipControl getOrCreate(ServerShip ship) {
        if (ship.getAttachment(SailsShipControl.class) == null) {
            ship.saveAttachment(SailsShipControl.class, new SailsShipControl());
        }
        return ship.getAttachment(SailsShipControl.class);
    }

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        //eureka's turning: physShip.applyInvariantTorque(moiTensor.transform(Vector3d(0.0, idealAlphaY, 0.0)))
        //double turn = 0.0; //represents helm block's value
        //should somehow get this to the helmblockentity to modify its value
        //physShip.applyInvariantTorque(new Vector3d(0.0, turn, 0.0));
        //LOGGER.info("forces applied");
        PhysShipImpl physShip1 = (PhysShipImpl) physShip;

        while (!invForces.isEmpty())
            physShip1.applyInvariantForce(Objects.requireNonNull(invForces.poll()));

        while (!rotForces.isEmpty())
            physShip1.applyRotDependentForce(Objects.requireNonNull(rotForces.poll()));

        while (!invPosForces.isEmpty()) {
            ForceAtPos invData = invPosForces.poll();
            assert invData != null;
            physShip1.applyInvariantForceToPos(invData.force, invData.pos);
        }
        while (!rotPosForces.isEmpty()) {
            ForceAtPos rotData = rotPosForces.poll();
            assert rotData != null;
            physShip1.applyRotDependentForceToPos(rotData.force, rotData.pos);
        }

//        while (!buoyForces.isEmpty()) {
//            physShip1.setBuoyantFactor(1.0 + buoyForces.poll() * 0.1);
//        }
        //experimental no-block-entity sail force implementation
        //Vector3d sailForce = new Vector3d(numSails*sailSpeed, 0, 0);
        //physShip1.applyRotDependentForce(sailForce);

        if (toBeStaticUpdated) {
            physShip1.setStatic(toBeStatic);
            toBeStaticUpdated = false;
        }
    }

    public void applyInvariantForce (Vector3dc force) {
        //LOGGER.info("inv force requested");
        invForces.add(force);
    }

    public void applyRotDependentForce(Vector3dc force) {
        rotForces.add(force);
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
    }

    public void addBuoyancy(double buoyancy) {
        buoyForces.add(buoyancy);
    }

    public void setStatic(boolean b) {
        toBeStatic = b;
        toBeStaticUpdated = true;
    }

    @Override
    public void onServerTick() {

    }

    private class ForceAtPos {
        Vector3dc force;
        Vector3dc pos;
    }
}
