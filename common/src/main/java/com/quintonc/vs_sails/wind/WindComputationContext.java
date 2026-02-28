package com.quintonc.vs_sails.wind;

import net.minecraft.server.level.ServerLevel;

import java.util.Random;

public final class WindComputationContext {
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

    public WindManager.WindRuleWind rule() {
        return rule;
    }

    public boolean raining() {
        return inputs.raining();
    }

    public boolean thundering() {
        return inputs.thundering();
    }

    public long dayTime() {
        return inputs.dayTime();
    }

    public Random random() {
        return random;
    }

    public double timeInfluence() {
        return timeInfluence;
    }

    public void setTimeInfluence(double timeInfluence) {
        this.timeInfluence = timeInfluence;
    }

    public double randomStrengthFactor() {
        return randomStrengthFactor;
    }

    public void setRandomStrengthFactor(double randomStrengthFactor) {
        this.randomStrengthFactor = randomStrengthFactor;
    }

    public int moonPhase() {
        return inputs.moonPhase();
    }

    public double direction() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void addDirection(double delta) {
        this.direction += delta;
    }

    public double randomDirectionOffset() {
        return randomDirectionOffset;
    }

    public void setRandomDirectionOffset(double randomDirectionOffset) {
        this.randomDirectionOffset = randomDirectionOffset;
    }

    public double strength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public void multiplyStrength(double factor) {
        this.strength *= factor;
    }

    public double dimensionMultiplier() {
        return rule.dimensionMultiplier();
    }

    public void resetVariationState() {
        setTimeInfluence(0.5d);
        setRandomStrengthFactor(0.25d);
        setRandomDirectionOffset(0.0d);
    }

    public void setNoWind() {
        resetVariationState();
        setStrength(0.0d);
        setDirection(0.0d);
    }

    void flushToState() {
        state.timeInfluence = timeInfluence;
        state.randomStrengthFactor = randomStrengthFactor;
        state.randomDirectionOffset = randomDirectionOffset;
        state.strength = (float) strength;
        state.direction = (float) direction;
    }
}
