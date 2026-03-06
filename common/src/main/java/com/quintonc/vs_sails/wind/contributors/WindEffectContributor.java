package com.quintonc.vs_sails.wind.contributors;

import com.quintonc.vs_sails.wind.WindComputationContext;

public interface WindEffectContributor {
    void apply(WindComputationContext ctx);
}
