package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public final class DefaultDirectionFromStrengthContributor implements WindEffectContributor {
    public static final DefaultDirectionFromStrengthContributor INSTANCE = new DefaultDirectionFromStrengthContributor();

    private DefaultDirectionFromStrengthContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        ctx.addDirection(12.0d * ctx.strength());
        ctx.addDirection(12.0d);
        ctx.addDirection(ctx.randomDirectionOffset());
    }
}
