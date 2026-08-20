package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public final class StrengthFinalizationContributor implements WindEffectContributor {
    public static final StrengthFinalizationContributor INSTANCE = new StrengthFinalizationContributor();

    private StrengthFinalizationContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        ctx.setStrength((ctx.strength() / 2.0d) * ctx.dimensionMultiplier());
    }
}
