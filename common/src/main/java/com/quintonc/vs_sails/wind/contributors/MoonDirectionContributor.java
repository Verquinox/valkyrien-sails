package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

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
        if (ctx.rule().effects().moonPhase()) {
            ctx.setDirection(DIRECTIONS[ctx.moonPhase()]);
        }
    }
}