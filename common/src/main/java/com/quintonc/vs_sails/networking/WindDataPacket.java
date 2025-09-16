package com.quintonc.vs_sails.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class WindDataPacket {
    public final float windStrength;
    public final float windDirection;

    public WindDataPacket(FriendlyByteBuf buf) {
        // Decode data into a message
        this(buf.readFloat(), buf.readFloat());
    }

    public WindDataPacket(float windStrength, float windDirection) {
        // Message creation
        this.windStrength = windStrength;
        this.windDirection = windDirection;

    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeFloat(windStrength);
        buf.writeFloat(windDirection);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive

    }
}
