package com.quintonc.vs_sails.client;

import com.quintonc.vs_sails.wind.WindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

public class ClientWindManager extends WindManager {

    private static ResourceLocation lastDimensionId;
    private static ClientLevel lastLevel;

    public static void InitializeWind() {
        lastDimensionId = null;
        lastLevel = null;
        clearWindData();
    }

    public static void handleClientTick(Minecraft client) {
        ClientLevel level = client.level;
        if (level == null) {
            clearWindData();
            lastLevel = null;
            lastDimensionId = null;
            return;
        }

        ResourceLocation currentDim = level.dimension().location();
        if (level != lastLevel || !currentDim.equals(lastDimensionId)) {
            clearWindData();
            lastLevel = level;
            lastDimensionId = currentDim;
        }
    }
}
