package com.quintonc.vs_sails.networking;

import com.quintonc.vs_sails.blocks.entity.BaseHelmBlockEntity;
import com.quintonc.vs_sails.client.ClientWindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.quintonc.vs_sails.ValkyrienSails.MOD_ID;

public class PacketHandler {
    public static final ResourceLocation WHEEL_ANGLE_PACKET = new ResourceLocation(MOD_ID, "wheel_angle_packet");
    public static final ResourceLocation WIND_DATA_PACKET = new ResourceLocation(MOD_ID, "wind_data_packet");

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(MOD_ID, "networking_channel"));

    //public static float serverTPS;

    public static void register() {

        CHANNEL.register(WheelAngleMessage.class, WheelAngleMessage::encode, WheelAngleMessage::new, WheelAngleMessage::apply);
        CHANNEL.register(WindDataPacket.class, WindDataPacket::encode, WindDataPacket::new, WindDataPacket::apply);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PacketHandler.WHEEL_ANGLE_PACKET, (buf, context) -> {
            //Player player = context.getPlayer();
            // Logic
            int wheelAngle = buf.readInt();
            BlockPos pos = buf.readBlockPos();
            //float tps = buf.readFloat();

            BlockEntity be = context.getPlayer().level().getBlockEntity(pos);
            if (be instanceof BaseHelmBlockEntity blockEntity) {
                blockEntity.wheelAngle = wheelAngle;
                blockEntity.renderWheelAngleVel = (float) Minecraft.getInstance().getFps() / 20;
                //serverTPS = tps;
            }
        });


        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PacketHandler.WIND_DATA_PACKET, (buf, context) -> {
            // Logic
            float str = buf.readFloat();
            float dir = buf.readFloat();
            context.queue(() -> {

                ClientWindManager.windStrength = str;
                ClientWindManager.windDirection = dir;
            });

        });

    }

}
