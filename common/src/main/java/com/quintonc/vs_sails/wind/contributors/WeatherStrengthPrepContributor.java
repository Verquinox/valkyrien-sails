package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public final class WeatherStrengthPrepContributor implements WindEffectContributor {
    public static final WeatherStrengthPrepContributor INSTANCE = new WeatherStrengthPrepContributor();

    private WeatherStrengthPrepContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        if (ctx.rule().effects().weather() && (ctx.raining() || ctx.thundering())) {
            ctx.setTimeInfluence(0.0d);
        }
    }
}
