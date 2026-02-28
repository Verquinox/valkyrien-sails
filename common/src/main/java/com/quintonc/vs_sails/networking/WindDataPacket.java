package com.quintonc.vs_sails.networking;

import com.quintonc.vs_sails.client.ClientWindManager;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class WindDataPacket {
    public final float windStrength;
    public final float windDirection;

    public WindDataPacket(float windStrength, float windDirection) {
        // Message creation
        this.windStrength = windStrength;
        this.windDirection = windDirection;

    }

    public static WindDataPacket decode(FriendlyByteBuf buf) {
        return new WindDataPacket(buf.readFloat(), buf.readFloat());
    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeFloat(windStrength);
        buf.writeFloat(windDirection);
    }

    public void apply(NetworkManager.PacketContext context) {
        // On receive
        context.queue(() -> {
            if (context.getPlayer() != null && context.getPlayer().level() != null) {
                ClientWindManager.setWindForLevel(context.getPlayer().level(), windStrength, windDirection);
            }
        });
    }
}
