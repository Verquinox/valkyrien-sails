package com.quintonc.vs_sails.ship;

import org.jetbrains.annotations.NotNull;
import org.joml.*;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

public class SailsShipControl implements ShipForcesInducer {

    @Override
    public void applyForces(@NotNull PhysShip physShip) {
        //eureka's turning: physShip.applyInvariantTorque(moiTensor.transform(Vector3d(0.0, idealAlphaY, 0.0)))
        //double turn = 0.0; //represents helm block's value
        //should somehow get this to the helmblockentity to modify its value
        //physShip.applyInvariantTorque(new Vector3d(0.0, turn, 0.0));
    }
}
