package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

import static net.minecraft.util.Mth.positiveModulo;
import static net.minecraft.util.Mth.wrapDegrees;

public final class MoonDirectionContributor implements WindEffectContributor {
    public static final MoonDirectionContributor INSTANCE = new MoonDirectionContributor();

    private static final int[] DIRECTIONS = {
            225, // south-west
            90,  // east
            270, // west
            0,   // north
            180, // south
            315, // north-west
            45,  // north-east
            135  // south-east
    };

    private MoonDirectionContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        double influence = ctx.rule().effects().moonPhase();
        if (influence <= 0.0d) return;

        double target = DIRECTIONS[ctx.moonPhase()];
        double current = ctx.direction();
        double delta = wrapDegrees((float) (target - current));
        double blended = current + delta * influence;
        ctx.setDirection(positiveModulo((float) blended, 360.0f));
    }
}