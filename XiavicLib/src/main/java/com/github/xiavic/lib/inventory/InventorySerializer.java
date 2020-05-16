package com.github.xiavic.lib.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public interface InventorySerializer {

    byte[] serialize(PlayerInventory playerInventory);

    void applyInventoryOnto(final Player player, final byte[] bytes);
}
