package com.quintonc.vs_sails.wind;

import net.minecraft.server.level.ServerLevel;

import java.util.Random;

final class WindComputationContext {
    final ServerLevel world;
    final WindManager.WindRuleWind rule;
    final DimensionWindState state;
    final WindInputs inputs;
    final Random random;

    double strength = 0.0d;
    double direction = 0.0d;

    double timeInfluence;
    double randomStrengthFactor;
    double randomDirectionOffset;

    WindComputationContext(ServerLevel world, WindManager.WindRuleWind rule, DimensionWindState state, Random random) {
        this.world = world;
        this.rule = rule;
        this.state = state;
        this.inputs = new WindInputs(world.isRaining(), world.isThundering(), world.getDayTime(), world.getMoonPhase());
        this.random = random;

        this.timeInfluence = state.timeInfluence;
        this.randomStrengthFactor = state.randomStrengthFactor;
        this.randomDirectionOffset = state.randomDirectionOffset;
    }

    boolean isFixedDirectionType() {
        return rule.direction().type() == WindManager.WindType.FIXED;
    }

    void flushToState() {
        state.timeInfluence = timeInfluence;
        state.randomStrengthFactor = randomStrengthFactor;
        state.randomDirectionOffset = randomDirectionOffset;
        state.strength = (float) strength;
        state.direction = (float) direction;
    }
}