package com.github.xiavic.essentials.utils.acf;

import co.aikar.commands.BukkitCommandManager;
import com.github.xiavic.essentials.utils.handlers.warps.WarpManager;

public class TabCompletions {

    public static void init (BukkitCommandManager manager) {
        manager.getCommandCompletions().registerCompletion("publicwarps", c -> {
            return WarpManager.getAllWarpNames();
        });
    }

}
