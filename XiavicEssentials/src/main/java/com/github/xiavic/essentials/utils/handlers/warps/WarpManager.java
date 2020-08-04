package com.github.xiavic.essentials.utils.handlers.warps;

import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.utils.Utils;
import de.leonhard.storage.Json;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class WarpManager {

    public static Collection<Warp> warps = ConcurrentHashMap.newKeySet();
    private static Json warpsFile = Main.warps;

    public static boolean isWarp(Location location, boolean useBlockLoc) {
        for (Warp warp : warps) {
            Location loc = warp.getLocation();
            if (useBlockLoc && Utils.areCordsEqual(loc, location) || loc.equals(location)) return true;
        }
        return false;
    }

    public static WarpState registerWarp(Warp warp) {
        if (getAllWarpNames().contains(warp.getName())) return WarpState.ALREADY_EXISTS;
        warps.add(warp);
        return WarpState.REGISTERED;
    }

    public static WarpState unregisterWarp(Warp warp) {
        if (!warps.contains(warp)) return WarpState.FAILED;
        warps.remove(warp);
        return WarpState.UNREGISTERED;
    }

    public static Warp getWarp (String warpName) {
        for (Warp warp : warps) {
            if (warp.getName().equals(warpName)) return warp;
        }
        return null;
    }

    public static List<String> getAllWarpNames() {
        List<String> warpNames = new ArrayList<>();
        for (Warp warp : warps) {
            warpNames.add(warp.getName());
        }
        return warpNames;
    }


    public static String getAllWarps() {
        StringBuilder warpList = new StringBuilder();

        warpList.append("#2be3dd");
        for (Warp warp : warps) {
            warpList.append(warp.getName()).append("\n");
        }
        return warpList.toString();
    }

    public static List<String> getEnabledWarps() {
        ArrayList<String> enabledWarps = new ArrayList<>();
        for (Warp warp : warps) {
            if (warp.isEnabled()) {
                enabledWarps.add(warp.getName());
            }
        }
        return enabledWarps;
    }

    public static void saveAllWarps() {
        Main.warps.clear();

        for (Warp warp : warps) {
            warpsFile.setPathPrefix("publicwarps." + warp.getUniqueID());
            warpsFile.set("name", warp.getName());
            warpsFile.set("enabled", warp.isEnabled());
            warpsFile.set("perm", warp.getPermission());

            // Save Location to File Here
            warpsFile.set("location.world", warp.getLocation().getWorld().getName());
            warpsFile.set("location.x", warp.getLocation().getX());
            warpsFile.set("location.y", warp.getLocation().getY());
            warpsFile.set("location.Z", warp.getLocation().getZ());
            warpsFile.set("location.yaw", warp.getLocation().getYaw());
            warpsFile.set("location.pitch", warp.getLocation().getPitch());

        }
    }

    public static void loadWarpsFromFile() {
        warps.clear();
        for (String key : Main.warps.singleLayerKeySet("publicwarps")) {
            Main.warps.setPathPrefix("publicwarps." + key);

            // Create the Warp and Register it.
            Warp tempObject =  new Warp(
                    warpsFile.getString("name"),
                    warpsFile.getString("perm"),
                    warpsFile.getBoolean("enabled"),
                    new Location(
                            Bukkit.getWorld(warpsFile.getString("location.world")),
                            warpsFile.getDouble("location.x"),
                            warpsFile.getDouble("location.y"),
                            warpsFile.getDouble("location.z"),
                            warpsFile.getFloat("location.yaw"),
                            warpsFile.getFloat("location.pitch")
                    )
            );

            registerWarp(tempObject);

        }
    }





}
