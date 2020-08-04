package com.github.xiavic.essentials.utils.acf;

import co.aikar.commands.BukkitCommandManager;
import com.github.xiavic.essentials.utils.handlers.warps.WarpManager;
import com.google.common.collect.ImmutableList;

public class TabCompletions {

    public static void init (BukkitCommandManager manager) {
        manager.getCommandCompletions().registerCompletion("publicwarps", c -> {
            return WarpManager.getAllWarpNames();
        });

        manager.getCommandCompletions().registerCompletion("enabledPublicWarps", c -> {
            return WarpManager.getEnabledWarps();
        });

        manager.getCommandCompletions().registerCompletion("permittedPWarps", c -> {
            return null;
        });

        manager.getCommandCompletions().registerCompletion("editTypes", c -> {
            return ImmutableList.of("NAME", "PERM", "ENABLED", "LOCATION");
        });


    }

}
