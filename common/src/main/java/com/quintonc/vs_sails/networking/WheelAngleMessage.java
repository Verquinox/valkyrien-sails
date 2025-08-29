package com.quintonc.vs_sails.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class WheelAngleMessage {
    public final int wheelAngle;
    public final BlockPos pos;

    public WheelAngleMessage(FriendlyByteBuf buf) {
        // Decode data into a message
        this(buf.readInt(), buf.readBlockPos());
    }

    public WheelAngleMessage(int wheelAngle, BlockPos pos) {
        // Message creation
        this.wheelAngle = wheelAngle;
        this.pos = pos;

    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeInt(wheelAngle);
        buf.writeBlockPos(pos);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
    }
}
