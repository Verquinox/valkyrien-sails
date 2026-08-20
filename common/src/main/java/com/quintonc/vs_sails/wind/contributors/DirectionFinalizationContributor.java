package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;
import net.minecraft.util.Mth;

public final class DirectionFinalizationContributor implements WindEffectContributor {
    public static final DirectionFinalizationContributor INSTANCE = new DirectionFinalizationContributor();

    private DirectionFinalizationContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        ctx.setDirection(Mth.positiveModulo((float) ctx.direction(), 360.0f));
    }
}
