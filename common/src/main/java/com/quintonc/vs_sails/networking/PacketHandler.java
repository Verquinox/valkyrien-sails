package com.quintonc.vs_sails.networking;

import com.quintonc.vs_sails.blocks.entity.BaseHelmBlockEntity;
import com.quintonc.vs_sails.client.ClientWindManager;
import com.quintonc.vs_sails.client.HelmSteeringScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerPlayer;

import static com.quintonc.vs_sails.ValkyrienSails.MOD_ID;

public class PacketHandler {
    public static final ResourceLocation WHEEL_ANGLE_PACKET = new ResourceLocation(MOD_ID, "wheel_angle_packet");
    public static final ResourceLocation WHEEL_PACKET = new ResourceLocation(MOD_ID, "wheel_packet");
    public static final ResourceLocation WIND_DATA_PACKET = new ResourceLocation(MOD_ID, "wind_data_packet");
    public static final ResourceLocation OPEN_HELM_SCREEN_PACKET = new ResourceLocation(MOD_ID, "open_helm_screen");
    public static final ResourceLocation SET_HELM_ANGLE_PACKET = new ResourceLocation(MOD_ID, "set_helm_angle");

    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(MOD_ID, "networking_channel"));

    //public static float serverTPS;

    public static void registerServer() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SET_HELM_ANGLE_PACKET, (buf, context) -> {
            BlockPos pos = buf.readBlockPos();
            int requestedAngle = buf.readInt();

            context.queue(() -> {
                if (!(context.getPlayer() instanceof ServerPlayer player)
                        || player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) {
                    return;
                }
                BlockEntity be = player.level().getBlockEntity(pos);
                if (be instanceof BaseHelmBlockEntity helm && !helm.getFirstItem().isEmpty()) {
                    helm.setWheelAngle(requestedAngle);
                }
            });
        });
    }

    public static void registerClient() {

        CHANNEL.register(WheelAngleMessage.class, WheelAngleMessage::encode, WheelAngleMessage::new, WheelAngleMessage::apply);
        CHANNEL.register(WheelMessage.class, WheelMessage::encode, WheelMessage::new, WheelMessage::apply);

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, OPEN_HELM_SCREEN_PACKET, (buf, context) -> {
            BlockPos pos = buf.readBlockPos();
            int wheelAngle = buf.readInt();
            context.queue(() -> Minecraft.getInstance().setScreen(new HelmSteeringScreen(pos, wheelAngle)));
        });

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
//                    Entity camera = Minecraft.getInstance().cameraEntity;
//                    if (!(camera == null || camera == Minecraft.getInstance().player)) {
//                        Minecraft.getInstance().player.displayClientMessage(Component.literal("Angle: "+wheelAngle), true);
//                    }
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
