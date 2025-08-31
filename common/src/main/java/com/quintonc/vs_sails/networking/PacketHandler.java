package com.quintonc.vs_sails.networking;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.entity.BaseHelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.renderer.HelmBlockEntityRenderer;
import dev.architectury.networking.simple.MessageType;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PacketHandler {
    public static final ResourceLocation WHEEL_ANGLE_PACKET = new ResourceLocation("vs_sails", "wheel_angle_packet");
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation("vs_sails", "networking_channel"));

    public static void register() {

        CHANNEL.register(WheelAngleMessage.class, WheelAngleMessage::encode, WheelAngleMessage::new, WheelAngleMessage::apply);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PacketHandler.WHEEL_ANGLE_PACKET, (buf, context) -> {
            //Player player = context.getPlayer();
            // Logic
            int wheelAngle = buf.readInt();
            BlockPos pos = buf.readBlockPos();
            BlockEntity be = context.getPlayer().level().getBlockEntity(pos);
            if (be instanceof BaseHelmBlockEntity blockEntity) {
                blockEntity.wheelAngle = wheelAngle;
            }
        });

    }

}
