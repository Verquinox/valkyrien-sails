package com.quintonc.vs_sails.networking;

import net.minecraft.util.Identifier;

import static com.quintonc.vs_sails.ValkyrienSails.MOD_ID;

public class WindModNetworking {
    public static final Identifier WINDSTRENGTHS2CPACKET = Identifier.of(MOD_ID, "wind_strength_s2c_packet");
    public static final Identifier WINDDIRECTIONS2CPACKET = Identifier.of(MOD_ID, "wind_direction_s2c_packet");

    public static void networkingInit() {

    }
}
