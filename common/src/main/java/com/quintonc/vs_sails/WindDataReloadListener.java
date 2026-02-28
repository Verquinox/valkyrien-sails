package com.quintonc.vs_sails;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class WindDataReloadListener {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String WIND_PATH = "vs_sails/wind";
    private static final String JSON_SUFFIX = ".json";

    private WindDataReloadListener() {
    }

    public static void loadFromServer(MinecraftServer server) {
        Map<ResourceLocation, WindManager.WindRuleData> parsedRules = new HashMap<>();
        Set<ResourceLocation> knownDimensionIds = server.levelKeys().stream()
                .map(ResourceKey::location)
                .collect(Collectors.toSet());

        Map<ResourceLocation, Resource> resources = server.getResourceManager()
                .listResources(WIND_PATH, id -> id.getPath().endsWith(JSON_SUFFIX));

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation fileId = entry.getKey();
            ResourceLocation ruleId = toRuleId(fileId);

            if (ruleId == null) {
                ValkyrienSails.LOGGER.warn("Skipping malformed wind file path '{}'", fileId);
                continue;
            }

            try (Reader reader = entry.getValue().openAsReader()) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                if (json == null) {
                    ValkyrienSails.LOGGER.warn("Skipping empty wind rule file '{}'", fileId);
                    continue;
                }

                WindManager.parseWindRule(json)
                        .resultOrPartial(error -> ValkyrienSails.LOGGER.warn(
                                "Failed to parse wind rule '{}': {}. Falling back to defaults for this file.",
                                fileId,
                                error
                        ))
                        .map(rule -> WindManager.sanitizeLoadedRule(ruleId, rule, knownDimensionIds))
                        .ifPresent(rule -> parsedRules.put(ruleId, rule));
            } catch (Exception e) {
                ValkyrienSails.LOGGER.warn(
                        "Failed reading wind rule '{}'. Falling back to defaults for this file.",
                        fileId,
                        e
                );
            }
        }

        if (parsedRules.isEmpty()) {
            ValkyrienSails.LOGGER.warn("No valid wind rules loaded; using default no-wind fallback");
        }

        WindRuleRegistry.replaceAll(parsedRules);
    }

    private static ResourceLocation toRuleId(ResourceLocation fileId) {
        String path = fileId.getPath();
        String prefix = WIND_PATH + "/";
        if (!path.startsWith(prefix) || !path.endsWith(JSON_SUFFIX)) {
            return null;
        }
        String trimmedPath = path.substring(prefix.length(), path.length() - JSON_SUFFIX.length());
        return new ResourceLocation(fileId.getNamespace(), trimmedPath);
    }
}
