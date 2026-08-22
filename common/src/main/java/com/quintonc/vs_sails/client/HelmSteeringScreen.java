package com.quintonc.vs_sails.client;

import com.quintonc.vs_sails.blocks.entity.BaseHelmBlockEntity;
import com.quintonc.vs_sails.networking.PacketHandler;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class HelmSteeringScreen extends Screen {
    private static final int BAR_WIDTH = 220;
    private static final int BAR_HEIGHT = 12;
    private static final int HANDLE_WIDTH = 7;

    private final BlockPos helmPos;
    private int wheelAngle;
    private int lastSentAngle;
    private int dragStartMouseX = Integer.MIN_VALUE;
    private final int dragStartWheelAngle;

    public HelmSteeringScreen(BlockPos helmPos, int wheelAngle) {
        super(Component.translatable("screen.vs_sails.helm_steering"));
        this.helmPos = helmPos;
        this.wheelAngle = wheelAngle;
        this.lastSentAngle = wheelAngle;
        this.dragStartWheelAngle = wheelAngle;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (minecraft == null || GLFW.glfwGetMouseButton(minecraft.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_RIGHT)
                != GLFW.GLFW_PRESS) {
            onClose();
            return;
        }

        updateAngle(mouseX);

        int left = (width - BAR_WIDTH) / 2;
        int top = height / 2 + 28;
        int center = left + BAR_WIDTH / 2;
        int handleX = left + Math.round(((BaseHelmBlockEntity.maxAngle - wheelAngle)
                / (float) BaseHelmBlockEntity.maxAngle) * BAR_WIDTH);

        graphics.fill(left - 2, top - 2, left + BAR_WIDTH + 2, top + BAR_HEIGHT + 2, 0xCC111820);
        graphics.fill(left, top, left + BAR_WIDTH, top + BAR_HEIGHT, 0xCC506070);
        graphics.fill(center - 1, top - 5, center + 1, top + BAR_HEIGHT + 5, 0xFFFFFFFF);
        graphics.fill(handleX - HANDLE_WIDTH / 2, top - 4,
                handleX + HANDLE_WIDTH / 2 + 1, top + BAR_HEIGHT + 4, 0xFFFFB43C);

        double rudderDegrees = (wheelAngle - 360) / 2.0;
        String direction = rudderDegrees > 0.0 ? " left" : rudderDegrees < 0.0 ? " right" : "";
        Component angleText = Component.literal(String.format(Locale.ROOT, "%.1f°%s",
                Math.abs(rudderDegrees), direction));
        graphics.drawCenteredString(font, title, width / 2, top - 28, 0xFFFFFFFF);
        graphics.drawCenteredString(font, angleText, width / 2, top - 16, 0xFFFFD080);
        graphics.drawString(font, Component.translatable("screen.vs_sails.helm_left"),
                left, top + 20, 0xFFBFC8D0, false);
        Component right = Component.translatable("screen.vs_sails.helm_right");
        graphics.drawString(font, right, left + BAR_WIDTH - font.width(right),
                top + 20, 0xFFBFC8D0, false);
    }

    private void updateAngle(int mouseX) {
        if (dragStartMouseX == Integer.MIN_VALUE) {
            dragStartMouseX = mouseX;
            return;
        }

        int interval = Math.max(1, BaseHelmBlockEntity.wheelInterval);
        double anglePerPixel = BaseHelmBlockEntity.maxAngle / (double) BAR_WIDTH;
        int rawAngle = (int) Math.round(dragStartWheelAngle - (mouseX - dragStartMouseX) * anglePerPixel);
        int requested = (int) Math.round(rawAngle / (double) interval) * interval;
        wheelAngle = Math.max(0, Math.min(BaseHelmBlockEntity.maxAngle, requested));

        if (wheelAngle != lastSentAngle) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBlockPos(helmPos);
            buf.writeInt(wheelAngle);
            NetworkManager.sendToServer(PacketHandler.SET_HELM_ANGLE_PACKET, buf);
            lastSentAngle = wheelAngle;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}