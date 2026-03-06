package com.quintonc.vs_sails.networking;

import com.quintonc.vs_sails.wind.WindManager;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WindDataPacket {
    public final ResourceLocation dimensionId;
    public final WindManager.WindType windType;
    public final float windStrength;
    public final float windDirection;
    public final boolean windEnabled;

    public WindDataPacket(
            ResourceLocation dimensionId,
            WindManager.WindType windType,
            float windStrength,
            float windDirection,
            boolean windEnabled
    ) {
        // Message creation
        this.dimensionId = dimensionId;
        this.windType = windType;
        this.windStrength = windStrength;
        this.windDirection = windDirection;
        this.windEnabled = windEnabled;

    }

    public static WindDataPacket decode(FriendlyByteBuf buf) {
        return new WindDataPacket(
                buf.readResourceLocation(),
                buf.readEnum(WindManager.WindType.class),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean()
        );
    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeResourceLocation(dimensionId);
        buf.writeEnum(windType);
        buf.writeFloat(windStrength);
        buf.writeFloat(windDirection);
        buf.writeBoolean(windEnabled);
    }

    public void apply(NetworkManager.PacketContext context) {
        // On receive
        context.queue(() -> {
            if (dimensionId == null || windType == null) return;
            if (!Float.isFinite(windStrength) || !Float.isFinite(windDirection)) return;
            float normalizedDirection = Mth.positiveModulo(windDirection, 360.0f);
            WindManager.setWindForDimension(dimensionId, windStrength, normalizedDirection, windType, windEnabled);
        });
    }
}
