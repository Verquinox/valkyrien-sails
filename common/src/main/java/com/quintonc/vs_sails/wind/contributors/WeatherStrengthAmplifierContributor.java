package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public final class WeatherStrengthAmplifierContributor implements WindEffectContributor {
    public static final WeatherStrengthAmplifierContributor INSTANCE = new WeatherStrengthAmplifierContributor();

    private WeatherStrengthAmplifierContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        if (ctx.rule().effects().weather() <= 0.0d) {
            return;
        }

        double influence = ctx.rule().effects().weather();
        double fullMultiplier = ctx.thundering() ? 2.0d : (ctx.raining() ? 1.5d : 1.0d);
        double multiplier = 1.0d + (fullMultiplier - 1.0d) * influence;

        ctx.multiplyStrength(multiplier);
    }
}
