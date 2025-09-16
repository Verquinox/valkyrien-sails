package com.quintonc.vs_sails.client;

import com.quintonc.vs_sails.WindManager;

public class ClientWindManager extends WindManager {


    public static void InitializeWind() {

        windStrength = 0;
        windDirection = 0;
        windGustiness = 0.125f;
        windShear = 10;

    }
}
