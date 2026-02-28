package com.quintonc.vs_sails.wind;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class WindRuleRegistry {
    private static final WindManager.WindRuleWind NO_WIND = new WindManager.WindRuleWind(
            0.0d,
            new WindManager.WindDirectionSpec(WindManager.WindType.NO_WIND),
            0.0d,
            300,
            new WindManager.WindRuleEffects(false, false, false, false, false)
    );

    private static volatile List<ResolvedRule> RULES = List.of();

    private WindRuleRegistry() {
    }

    public static void replaceAll(Map<ResourceLocation, WindManager.WindRuleData> parsedRules) {
        List<ResolvedRule> resolvedRules = parsedRules.entrySet().stream()
                .map(entry -> new ResolvedRule(
                        entry.getKey(),
                        entry.getValue(),
                        parseTargets(entry.getValue().targets().dimensions()),
                        parseTargets(entry.getValue().targets().dimensionTags())
                ))
                .sorted(Comparator
                        .comparingInt((ResolvedRule rule) -> rule.data().priority()).reversed()
                        .thenComparing(rule -> rule.id().toString()))
                .toList();

        RULES = resolvedRules;
    }

    public static Optional<WindManager.WindRuleData> getRule(ServerLevel level) {
        ResourceLocation dimensionId = level.dimension().location();

        for (ResolvedRule rule : RULES) {
            if (rule.dimensions().contains(dimensionId)) {
                return Optional.of(rule.data());
            }
            if (matchesAnyTag(level, rule.dimensionTags())) {
                return Optional.of(rule.data());
            }
        }

        return Optional.empty();
    }

    public static WindManager.WindRuleWind getWind(ServerLevel level) {
        return getRule(level)
                .map(WindManager.WindRuleData::wind)
                .orElse(NO_WIND);
    }

    private static Set<ResourceLocation> parseTargets(List<String> targets) {
        return targets.stream()
                .map(ResourceLocation::tryParse)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static boolean matchesAnyTag(ServerLevel level, Set<ResourceLocation> tagIds) {
        if (tagIds.isEmpty()) {
            return false;
        }

        ResourceKey<LevelStem> stemKey = ResourceKey.create(Registries.LEVEL_STEM, level.dimension().location());
        Optional<Holder.Reference<LevelStem>> stemHolder = level.getServer()
                .registryAccess()
                .registryOrThrow(Registries.LEVEL_STEM)
                .getHolder(stemKey);

        if (stemHolder.isEmpty()) {
            return false;
        }

        Holder.Reference<LevelStem> holder = stemHolder.get();
        for (ResourceLocation tagId : tagIds) {
            if (holder.is(TagKey.create(Registries.LEVEL_STEM, tagId))) {
                return true;
            }
        }

        return false;
    }

    private record ResolvedRule(
            ResourceLocation id,
            WindManager.WindRuleData data,
            Set<ResourceLocation> dimensions,
            Set<ResourceLocation> dimensionTags
    ) {
    }
}
