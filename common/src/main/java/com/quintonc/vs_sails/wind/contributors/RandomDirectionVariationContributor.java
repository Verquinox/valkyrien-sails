package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class RandomDirectionVariationContributor implements WindEffectContributor {
    public static final RandomDirectionVariationContributor INSTANCE = new RandomDirectionVariationContributor();

    private RandomDirectionVariationContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        if (!ctx.rule().effects().randomDirectionVariation()) {
            ctx.setRandomDirectionOffset(0.0d);
            return;
        }

        ctx.setRandomDirectionOffset(min(
                max(ctx.randomDirectionOffset() + (ctx.random().nextDouble() - 0.5d) * 24.0d, -120.0d),
                120.0d
        ));
    }
}