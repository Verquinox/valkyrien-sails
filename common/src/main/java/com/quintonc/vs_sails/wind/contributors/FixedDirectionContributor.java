package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public final class FixedDirectionContributor implements WindEffectContributor {
    public static final FixedDirectionContributor INSTANCE = new FixedDirectionContributor();

    private FixedDirectionContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        ctx.setDirection(ctx.rule().baseDirection());
    }
}
