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
                line = line.replaceAll("#.*", "");
                if(!line.isEmpty())
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
        defaults.add("enable-wind = true #[Default: true]");
        defaults.add("no-sail-zone = 90 #[Default: 90] Angular size in degrees of zone where sails lose most of their effectiveness");
        //defaults.add("min-wind-speed = 0.2");
        defaults.add("blow-vanilla-particles = false #[Default: false] Should the wind blow vanilla particles");
        defaults.add("\n#Sail Options");
        defaults.add("sail-power = 25000 #[Default: 25000]");
        defaults.add("forgiving-sails = false #[Default: false] Enable this if you want the wind direction to matter less to the sails");
        defaults.add("\n#Helm Options");
        defaults.add("rudder-power = 1.0 #[Default: 1.0]");
        defaults.add("realistic-rudder = true #[Default: true] Is the rudder turning force dependent on the ship's speed");
        defaults.add("keel-power = 4.0 #[Default: 4.0] How strongly ships resist drifting");
        defaults.add("wheel-interval = 6 #How many degrees the wheel will turn per tick. MUST BE A FACTOR OF 360!!!");
        defaults.add("");
        defaults.add("magic-ballast-righting-force = 0.25 #[Default: 0.25]");
        defaults.add("");
        defaults.add("ballast-float-strength = 0.0625 #[Default: 0.0625]");
        defaults.add("buoy-float-strength = 0.125 #[Default: 0.125]");

        return defaults;
    }
}
