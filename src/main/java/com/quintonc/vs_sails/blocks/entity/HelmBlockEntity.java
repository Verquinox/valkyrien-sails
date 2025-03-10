package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;

import java.util.Vector;

import static com.quintonc.vs_sails.blocks.HelmBlock.WHEEL_ANGLE;

public class HelmBlockEntity extends BlockEntity {

    public double turnval;
    private static final int turnspeed = 10000;

    public HelmBlockEntity(BlockPos pos, BlockState state) {
        super(ValkyrienSailsJava.HELM_BLOCK_ENTITY, pos, state);
        turnval = 0.0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, HelmBlockEntity be) {
        //do seated controlling player stuff and have their impulses affect the turnval
        //left = negative, right = positive
        // changes by some amount each tick, possibly configurable in the future, possibly based on ship mass

        if (!world.isClient) {

            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                ChunkPos chunkPos = world.getChunk(pos).getPos();
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, chunkPos);

                //Matrix3dc moiTensor = ship.getInertiaData().getMomentOfInertiaTensor();

                Vector3dc v3dc = ship.getInertiaData().getCenterOfMassInShip();
                Vector3d loc = new Vector3d(v3dc.x(),v3dc.y(),v3dc.z()+1);
                Vector3d loc2 = new Vector3d(v3dc.x(),v3dc.y(),v3dc.z()-1);
                double wheelpos = (state.get(WHEEL_ANGLE)-360) * Math.PI/180;

                SailsShipControl shipForceApplier = ship.getAttachment(SailsShipControl.class);
                //pee.applyForces();
                Vector3d turnvector = new Vector3d(-turnspeed*wheelpos, 0, 0);
                Vector3d turnvector2 = new Vector3d(turnspeed*wheelpos, 0, 0);
                if (shipForceApplier != null) {
                    shipForceApplier.applyRotDependentForceToPos(turnvector, loc.sub(ship.getTransform().getPositionInShip()));
                    //shipForceApplier.applyRotDependentTorque();
                    shipForceApplier.applyRotDependentForceToPos(turnvector2, loc2.sub(ship.getTransform().getPositionInShip()));
                }
            }
        }
    }

    @Override
    public void markDirty() {
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
