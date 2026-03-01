package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

import static java.lang.Math.min;

public final class WeatherStrengthPrepContributor implements WindEffectContributor {
    public static final WeatherStrengthPrepContributor INSTANCE = new WeatherStrengthPrepContributor();

    private WeatherStrengthPrepContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        if ((ctx.raining()  || ctx.thundering())) {
            double influence = min(ctx.rule().effects().weather(), 1.0d);
            double neutral = ctx.timeInfluence();
            double full = 0.0d;
            ctx.setTimeInfluence(neutral + (full - neutral) * influence);
        }
    }
}
