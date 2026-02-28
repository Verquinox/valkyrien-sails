package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public final class WeatherStrengthAmplifierContributor implements WindEffectContributor {
    public static final WeatherStrengthAmplifierContributor INSTANCE = new WeatherStrengthAmplifierContributor();

    private WeatherStrengthAmplifierContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        if (!ctx.rule().effects().weather()) {
            return;
        }

        if (ctx.thundering()) {
            ctx.multiplyStrength(2.0d);
        } else if (ctx.raining()) {
            ctx.multiplyStrength(1.5d);
        }
    }
}
