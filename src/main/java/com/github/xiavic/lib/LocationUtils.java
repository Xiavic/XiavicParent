package com.github.xiavic.lib;

import de.leonhard.storage.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class LocationUtils {

    private static Yaml dataFile;

    public static void setDataFile(final @NotNull Yaml dataFile) {
        LocationUtils.dataFile = dataFile;
    }

    public static Location getLocation(String locName) {
        String test = dataFile.getString(locName);
        String[] list = test.split(",");
        World world = Bukkit.getWorld(list[0]);
        double x = Double.parseDouble(list[1]);
        double y = Double.parseDouble(list[2]);
        double z = Double.parseDouble(list[3]);

        float yaw = Float.parseFloat(list[4]);
        float pitch = Float.parseFloat(list[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

}
