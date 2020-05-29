package com.github.xiavic.essentials.utils.Misc;

import org.bukkit.entity.Player;

public enum everythingElse {

    INSTANCE;

    public static boolean isFrozen(Player player) {
        return player.getWalkSpeed() == 0;
    }
}
