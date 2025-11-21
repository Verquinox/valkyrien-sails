package com.quintonc.vs_sails.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class WheelMessage {
    public final ItemStack wheel;
    public final BlockPos pos;

    public WheelMessage(FriendlyByteBuf buf) {
        // Decode data into a message
        this(buf.readItem(), buf.readBlockPos());
    }

    public WheelMessage(ItemStack wheel, BlockPos pos) {
        // Message creation
        this.wheel = wheel;
        this.pos = pos;

    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeItem(wheel);
        buf.writeBlockPos(pos);
    }

    public void apply(Supplier<NetworkManager.PacketContext> contextSupplier) {
        // On receive
    }
}
