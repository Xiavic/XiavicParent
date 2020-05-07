package com.github.prypurity.xiaviccore.Utils.signedit;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * Base SignEditor implementation, used to track dynamic sign editing.
 */
public abstract class AbstractSignEditor implements ISignEditor {

    protected Collection<UUID> editing = new HashSet<>(), toggled = new HashSet<>();

    @Override @NotNull public Collection<UUID> getEditingPlayers() {
        return new HashSet<>(editing);
    }

    @Override public boolean isDynamicEditingEnabled(@NotNull UUID player) {
        return toggled.contains(player);
    }

    @Override public void toggleDynamicEditing(final @NotNull UUID player, final boolean enabled) {
        this.toggled.remove(player);
        if (enabled) {
            this.toggled.add(player);
        }
    }

    @Override public boolean isEditingSign(final @NotNull UUID player) {
        return this.editing.contains(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) @Override
    public void onRightClickSign(final @NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (isDynamicEditingEnabled(
                event.getPlayer().getUniqueId())) { //If player is editing dynamically.
                final Block block = event.getClickedBlock();
                if (block != null) {
                    final BlockState state = block.getState();
                    if (state instanceof Sign) {
                        openUI(event.getPlayer(), (Sign) state);
                    }
                }
            }
        }
    }
}
