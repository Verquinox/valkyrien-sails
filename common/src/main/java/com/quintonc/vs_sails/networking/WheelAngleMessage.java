package com.quintonc.vs_sails.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class WheelAngleMessage {
    public final int wheelAngle;
    public final BlockPos pos;
    //public final float tps;

    public WheelAngleMessage(FriendlyByteBuf buf) {
        // Decode data into a message
        this(buf.readInt(), buf.readBlockPos()/*, buf.readFloat()*/);
    }

    public WheelAngleMessage(int wheelAngle, BlockPos pos/*, float tps*/) {
        // Message creation
        this.wheelAngle = wheelAngle;
        this.pos = pos;
        //this.tps = tps;

    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeInt(wheelAngle);
        buf.writeBlockPos(pos);
        //buf.writeFloat(tps);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
    }
}
