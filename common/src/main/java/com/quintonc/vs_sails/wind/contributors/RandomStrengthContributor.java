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
        if (!ctx.rule().effects().randomStrengthVariation()) {
            ctx.setTimeInfluence(0.5d);
            ctx.setRandomStrengthFactor(0.25d);
            return;
        }

        if (ctx.random().nextBoolean()) {
            ctx.setTimeInfluence(min(ctx.timeInfluence() + (abs(ctx.timeInfluence()) * 0.125d) + 0.01d, 1.0d));
        } else {
            ctx.setTimeInfluence(max(ctx.timeInfluence() - (abs(ctx.timeInfluence()) * 0.125d) - 0.01d, -1.0d));
        }

        ctx.setRandomStrengthFactor(min(
                max(ctx.randomStrengthFactor() + (ctx.random().nextDouble() - 0.5d) * (1.0d - ctx.randomStrengthFactor()), 0.0d),
                0.99d
        ));
    }
}
