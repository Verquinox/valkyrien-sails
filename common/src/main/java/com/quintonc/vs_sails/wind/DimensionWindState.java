package com.quintonc.vs_sails.wind;

final class DimensionWindState {
    int tickCounter = 0;
    double timeInfluence = 0.5d;
    double randomStrengthFactor = 0.25d;
    double randomDirectionOffset = 0.0d;

    float strength = 0.0f;
    float direction = 0.0f;
}
