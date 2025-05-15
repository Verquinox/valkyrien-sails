package com.quintonc.vs_sails;

public class WindManager {
    protected static float windDirection;
    protected static float windStrength;
    protected static float windGustiness;
    protected static float windShear;

    public static float getWindDirection() {
        return windDirection;
    }

    public static float getWindStrength() {
        return windStrength;
    }
}
