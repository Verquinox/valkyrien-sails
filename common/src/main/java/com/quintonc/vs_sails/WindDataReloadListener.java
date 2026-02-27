package com.quintonc.vs_sails;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public class WindDataReloadListener extends SimpleJsonResourceReloadListener {
    public static final WindDataReloadListener INSTANCE = new WindDataReloadListener();
    private static final Gson GSON = new GsonBuilder().create();

    private WindDataReloadListener() {
        super(GSON, "vs_sails/wind");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, WindManager.WindRuleData> parsedRules = new HashMap<>();

        prepared.forEach((id, json) -> WindManager.parseWindRule(json)
                .result()
                .ifPresent(rule -> parsedRules.put(id, rule)));

        WindRuleRegistry.replaceAll(parsedRules);
    }
}
