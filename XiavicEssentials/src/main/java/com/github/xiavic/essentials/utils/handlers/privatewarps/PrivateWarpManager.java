package com.github.xiavic.essentials.utils.handlers.privatewarps;

import com.github.xiavic.essentials.Main;
import com.github.xiavic.essentials.utils.handlers.warps.WarpState;
import de.leonhard.storage.Json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PrivateWarpManager {

    public static Collection<PrivateWarp> pwarps = ConcurrentHashMap.newKeySet();
    private static Json pwarpsFile = Main.pwarps;

    public static WarpState registerWarp(PrivateWarp warp) {
        return null;
    }

    public static WarpState unregisterWarp(PrivateWarp warp) { return null; }

    public static List<PrivateWarp> getAllWarpOfUser(UUID playerUID) {
        List<PrivateWarp> warps = new ArrayList<>();
        for (PrivateWarp pwarp : pwarps) {
            if (pwarp.getUID() == playerUID) {
                warps.add(pwarp);
            }
        }
        return warps;
    }

    public static List<String> getThislater() {
        return null;
    }





    


}
