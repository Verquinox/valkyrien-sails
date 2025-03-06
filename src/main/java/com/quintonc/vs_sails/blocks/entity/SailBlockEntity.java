package com.quintonc.vs_sails.blocks.entity;

import com.quintonc.vs_sails.ValkyrienSailsJava;
import com.quintonc.vs_sails.blocks.SailBlock;
//import com.quintonc.vs_sails.util.ConfigUtils;
//import com.quintonc.vs_sails.util.PatternProcessor;
import jdk.jfr.Category;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.SeatedControllingPlayer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;

import java.util.List;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;

public class SailBlockEntity extends BlockEntity {

    private static final int sailspeed = 10000;

    public SailBlockEntity(BlockPos pos, BlockState state) {
        super(ValkyrienSailsJava.SAIL_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, SailBlockEntity be) {

//        if (!(world.getBlockState(pos.up()).getBlock() instanceof SailBlock)) {
//            return;
//        }

        if (!world.isClient) {

            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {


                ChunkPos chunkPos = world.getChunk(pos).getPos();
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, chunkPos);

                if (ship != null) {
//                    SeatedControllingPlayer seatedControllingPlayer = ship.getAttachment(SeatedControllingPlayer.class);
//                    if (seatedControllingPlayer == null) {
//                        if (world.getBlockState(pos.up()).getBlock() instanceof SailBlock) {
//                            seatedControllingPlayer = new SeatedControllingPlayer(world.getBlockState(pos.up()).get(HORIZONTAL_FACING).getOpposite());
//                        } else {
//                            return;
//                        }
//                        ship.setAttachment(SeatedControllingPlayer.class, seatedControllingPlayer);
//                    }


                    be.moveShipForward(ship, state);


                }
            }
        }

    }



    /**
     * This method converts the Euler angles of the ship's rotation
     * to a vector that drives the ship forward
     * @param ship the ship the block is on
     * @param state blockstate of the sail block
     */
    private void moveShipForward(LoadedServerShip ship, BlockState state)
    {
        //double mass = ship.getInertiaData().getMass();
        // Y is Yaw from getEulerAnglesXYZ
        double shipYaw = ship.getTransform().getShipToWorldRotation().getEulerAnglesXYZ(new Vector3d()).y;
        System.out.println("Euler: "+ship.getTransform().getShipToWorldRotation().getEulerAnglesXYZ(new Vector3d()));
        Vector3d directionVectorXZ = new Vector3d(Math.cos(shipYaw)*sailspeed,0,Math.sin(shipYaw)*sailspeed);

        GameTickForceApplier shipForceApplier = ship.getAttachment(GameTickForceApplier.class);
        if (shipForceApplier != null) shipForceApplier.applyInvariantForce(directionVectorXZ);
    }
}