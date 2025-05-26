package com.quintonc.vs_sails.networking;

import net.minecraft.resources.ResourceLocation;

import static com.quintonc.vs_sails.ValkyrienSails.MOD_ID;

public class WindModNetworking {
    public static final ResourceLocation WINDSTRENGTHS2CPACKET = ResourceLocation.tryBuild(MOD_ID, "wind_strength_s2c_packet");
    public static final ResourceLocation WINDDIRECTIONS2CPACKET = ResourceLocation.tryBuild(MOD_ID, "wind_direction_s2c_packet");

    public static void networkingInit() {

    }
}
