package com.github.xiavic.essentials.Utils.handlers.warps;

import com.github.xiavic.essentials.Utils.Utils;
import org.bukkit.Location;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class WarpManager {

    public static Collection<Warp> warps = ConcurrentHashMap.newKeySet();

    public static boolean isWarp(Location location, boolean useBlockLoc) {
        for (Warp warp : warps) {
            Location loc = warp.getLocation();
            if (useBlockLoc && Utils.areCordsEqual(loc, location) || loc.equals(location)) return true;
        }
        return false;
    }

    public static WarpState registerWarp(Warp warp) {
        if (warps.contains(warp)) return WarpState.ALREADY_EXISTS;
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

    public static String getAllWarps() {
        StringBuilder warpList = new StringBuilder();

        warpList.append("`2be3dd");
        for (Warp warp : warps) {
            warpList.append(warp.getName()).append("\n");
        }
        return warpList.toString();
    }




}
