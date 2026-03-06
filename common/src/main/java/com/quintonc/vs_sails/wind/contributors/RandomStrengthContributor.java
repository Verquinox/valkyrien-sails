package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public final class RandomStrengthContributor implements WindEffectContributor {
    public static final RandomStrengthContributor INSTANCE = new RandomStrengthContributor();

    private RandomStrengthContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        double influence = ctx.rule().effects().randomStrengthVariation();
        if (influence <= 0.0d) {
            ctx.setTimeInfluence(0.5d);
            ctx.setRandomStrengthFactor(0.25d);
            return;
        }

        double driftStep = ((abs(ctx.timeInfluence()) * 0.125d) + 0.01d) * influence;
        if (ctx.random().nextBoolean()) {
            ctx.setTimeInfluence(min(ctx.timeInfluence() + driftStep, 1.0d));
        } else {
            ctx.setTimeInfluence(max(ctx.timeInfluence() - driftStep, -1.0d));
        }

        double randStep = (ctx.random().nextDouble() - 0.5d) * (1.0d - ctx.randomStrengthFactor()) * influence;
        ctx.setRandomStrengthFactor(min(max(ctx.randomStrengthFactor() + randStep, 0.0d), 0.99d));
    }
}
