package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

import static java.lang.Math.abs;
import static java.lang.Math.copySign;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

public final class BaseDayNightStrengthContributor implements WindEffectContributor {
    public static final BaseDayNightStrengthContributor INSTANCE = new BaseDayNightStrengthContributor();

    private BaseDayNightStrengthContributor() {}

    @Override
    public void apply(WindComputationContext ctx) {
        double influence = ctx.rule().effects().dayNight();
        double full = sin(((double) ctx.dayTime() / 12000.0d) * Math.PI);
        double neutral = 1.0d;
        double timeFactor = neutral + (full - neutral) * influence;

        ctx.setStrength(copySign(
                (pow(abs(timeFactor), 0.44d) * ctx.timeInfluence()
                        + abs(ctx.randomStrengthFactor()) * (1.0d - ctx.timeInfluence())),
                timeFactor
        ));
    }
}
