package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public final class RadialDirectionContributor implements WindEffectContributor {
    public static final RadialDirectionContributor INSTANCE = new RadialDirectionContributor();

    private RadialDirectionContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        ctx.setDirection(ctx.rule().baseDirection() + ctx.randomDirectionOffset());
    }
}
