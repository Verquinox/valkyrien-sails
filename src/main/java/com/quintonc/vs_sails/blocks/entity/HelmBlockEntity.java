package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;

import java.util.Vector;

public class HelmBlockEntity extends BlockEntity {

    public double turnval;

    public HelmBlockEntity(BlockPos pos, BlockState state) {
        super(ValkyrienSailsJava.HELM_BLOCK_ENTITY, pos, state);
        turnval = 0.0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, HelmBlockEntity be) {
        //do seated controlling player stuff and have their impulses affect the turnval
        //left = negative, right = positive
        // changes by some amount each tick, possibly configurable in the future, possibly based on ship mass
        ChunkPos chunkPos = world.getChunk(pos).getPos();
        LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, chunkPos);
        //PhysShip ship2

        Matrix3dc moiTensor = ship.getInertiaData().getMomentOfInertiaTensor();



        GameTickForceApplier shipForceApplier = ship.getAttachment(GameTickForceApplier.class);
        Vector3d torqueval = new Vector3d(100000, 0, 0);
        if (shipForceApplier != null) shipForceApplier.applyRotDependentForce(torqueval);
    }


}
