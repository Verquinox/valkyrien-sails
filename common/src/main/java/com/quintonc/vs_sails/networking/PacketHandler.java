package com.quintonc.vs_sails.networking;

import com.quintonc.vs_sails.blocks.entity.BaseHelmBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.quintonc.vs_sails.ValkyrienSails.MOD_ID;

public class PacketHandler {
    public static final ResourceLocation WHEEL_ANGLE_PACKET = new ResourceLocation(MOD_ID, "wheel_angle_packet");
    public static final ResourceLocation WHEEL_PACKET = new ResourceLocation(MOD_ID, "wheel_packet");
    public static final ResourceLocation WIND_DATA_PACKET = new ResourceLocation(MOD_ID, "wind_data_packet");

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(MOD_ID, "networking_channel"));

    //public static float serverTPS;

    public static void register() {

        CHANNEL.register(WheelAngleMessage.class, WheelAngleMessage::encode, WheelAngleMessage::new, WheelAngleMessage::apply);
        CHANNEL.register(WheelMessage.class, WheelMessage::encode, WheelMessage::new, WheelMessage::apply);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PacketHandler.WHEEL_ANGLE_PACKET, (buf, context) -> {
            //Player player = context.getPlayer();
            // Logic
            int wheelAngle = buf.readInt();
            BlockPos pos = buf.readBlockPos();
            //float tps = buf.readFloat();

            context.queue(() -> {
                if (context.getPlayer() == null) {
                    return;
                }
                BlockEntity be = context.getPlayer().level().getBlockEntity(pos);
                if (be instanceof BaseHelmBlockEntity blockEntity) {
                    blockEntity.wheelAngle = wheelAngle;
                    blockEntity.renderWheelAngleVel = (float) Minecraft.getInstance().getFps() / 20;
                    //serverTPS = tps;
                }
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PacketHandler.WHEEL_PACKET, (buf, context) -> {
            //Player player = context.getPlayer();
            // Logic
            ItemStack wheel = buf.readItem();
            BlockPos pos = buf.readBlockPos();

            context.queue(() -> {
                if (context.getPlayer() == null) {
                    return;
                }
                BlockEntity be = context.getPlayer().level().getBlockEntity(pos);
                if (be instanceof BaseHelmBlockEntity blockEntity) {
                    blockEntity.setItem(0, wheel);
                }
            });
        });


        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PacketHandler.WIND_DATA_PACKET, (buf, context) -> WindDataPacket.decode(buf).apply(context));

    }

}
