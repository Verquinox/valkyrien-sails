package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class RandomDirectionVariationContributor implements WindEffectContributor {
    public static final RandomDirectionVariationContributor INSTANCE = new RandomDirectionVariationContributor();

    private RandomDirectionVariationContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        double influence = ctx.rule().effects().randomDirectionVariation();
        if (influence <= 0.0d) {
            ctx.setRandomDirectionOffset(0.0d);
            return;
        }

        double delta = (ctx.random().nextDouble() - 0.5d) * 24.0d * influence;
        double limit = 120.0d * influence;
        ctx.setRandomDirectionOffset(max(-limit, Math.min(limit, ctx.randomDirectionOffset() + delta)));
    }
}