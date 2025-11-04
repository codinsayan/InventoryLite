
package com.ait.inventory;

import java.util.Map;

public class CompatibilityUtil {
    public static boolean fits(Map<String,String> rules, Map<String,String> build) {
        // Trivial sample: socket and chipset must match
        String cpuSocket = build.get("cpuSocket");
        String moboSocket = build.get("moboSocket");
        if (cpuSocket!=null && moboSocket!=null && !cpuSocket.equalsIgnoreCase(moboSocket)) return false;
        // PSU wattage check
        try {
            int psu = Integer.parseInt(build.getOrDefault("psuWatt","0"));
            int gpu = Integer.parseInt(build.getOrDefault("gpuWatt","0"));
            int cpu = Integer.parseInt(build.getOrDefault("cpuWatt","0"));
            if (psu < (gpu + cpu + 150)) return false;
        } catch (Exception ignored) {}
        return true;
    }
}
