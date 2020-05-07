package com.github.prypurity.xiaviccore.Utils.signedit;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface ISignEditor extends Listener {

    void openUI(@NotNull Player player, @NotNull Sign sign);

    void onRightClickSign(@NotNull PlayerInteractEvent event);

    @NotNull Collection<UUID> getEditingPlayers();

    boolean isEditingSign(@NotNull UUID player);

    boolean isDynamicEditingEnabled(@NotNull UUID player);

    void toggleDynamicEditing(@NotNull UUID player, boolean enabled);
}
