package com.quintonc.vs_sails.config;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtils {
    public static Map<String,String> config = new HashMap<>();


    public static Map<String,String> loadConfigs()
    {
        File file = new File(FabricLoader.getInstance().getConfigDir().toString() + "/vs_sails/config.cfg");
        try {
            List<String> lines = FileUtils.readLines(file,"utf-8");
            lines.forEach(line->
            {
                if(!line.isEmpty() && line.charAt(0)!='#')
                {
                    String noSpace = line.replace(" ","");
                    String[] entry = noSpace.split("=");
                    config.put(entry[0],entry[1]);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    @SuppressWarnings("deprecation")
    public static void generateConfigs(List<String> input)
    {
        File file = new File(FabricLoader.getInstance().getConfigDirectory().getPath() + "/vs_sails/config.cfg");

        try {
            FileUtils.writeLines(file,input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static Map<String,String> checkConfigs()
    {
        if(new File(FabricLoader.getInstance().getConfigDirectory().getPath() + "/vs_sails/config.cfg").exists())
        {
            if (360 % Integer.parseInt(ConfigUtils.config.getOrDefault("wheel-interval","6")) != 0) {
                throw new RuntimeException("WHEEL INTERVAL MUST BE A FACTOR OF 360. It is: "+
                        ConfigUtils.config.getOrDefault("wheel-interval","6"));
            }
            return loadConfigs();
        }
        generateConfigs(makeDefaults());
        return loadConfigs();
    }

    private static List<String> makeDefaults()
    {
        List<String> defaults = new ArrayList<>();


        defaults.add("#Valkyrien Sails Configuration\n");
        defaults.add("#World Options");
        defaults.add("enable-wind = true");
        defaults.add("blow-vanilla-particles = false");
        defaults.add("");
        defaults.add("sail-power = 10000");
        defaults.add("\n#Helm Options");
        defaults.add("rudder-power = 50");
        defaults.add("#Should the rudder turning force be dependent on the ship's speed, as in reality");
        defaults.add("realistic-rudder = true");
        defaults.add("#How many degrees the wheel will turn per tick. MUST BE A FACTOR OF 360!!!");
        defaults.add("wheel-interval = 6");
        defaults.add("");
        defaults.add("magic-ballast-righting-force = 0.5");
        defaults.add("");
        defaults.add("ballast-float-strength = 0.0625");
        defaults.add("buoy-float-strength = 0.125");

        return defaults;
    }
}
