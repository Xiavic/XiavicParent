package com.github.prypurity.xiaviccore.Utils.Listeners;

import com.github.prypurity.xiaviccore.Main;
import com.github.prypurity.xiaviccore.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public enum AFKHandler implements Listener {

    INSTANCE;

    private Map<UUID, Long> tickCount = new HashMap<>();
    private Collection<UUID> notAFK = new HashSet<>();
    private BukkitTask current;
    private long timeoutTicks;

    public void registerTicker() {
        if (current != null && !current.isCancelled()) {
            return;
        }
        current = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), this::tick, 1, 1);
    }

    public void clearTicker() {
        if (current != null && !current.isCancelled()) {
            tickCount.clear();
            notAFK.clear();
            current.cancel();
            current = null;
        }
    }

    public long getTimeoutTicks() {
        return timeoutTicks;
    }

    public void setTimeoutTicks(final long timeoutTicks) {
        if (timeoutTicks < 1) {
            throw new IllegalArgumentException("Ticks must be positive and > 1!");
        }
        this.timeoutTicks = timeoutTicks;
    }

    public void setTimeout(final long timeout, final @NotNull TimeUnit timeUnit) {
        setTimeoutTicks(Utils.toTicks(timeout, timeUnit));
    }

    public void tick() {
        tickCount.entrySet().removeIf(entry -> {
            entry.setValue(entry.getValue() - 1);
            if (entry.getValue() < 0) {
                setAFK(entry.getKey(), true);
                return true;
            }
            return false;
        });
    }

    public void reset(final UUID player) {
        setAFK(player, false);
    }

    public boolean isAFK(final UUID player) {
        return !notAFK.contains(player);
    }

    public void setAFK(final UUID player, final boolean afk) {
        final boolean currentlyAFK = isAFK(player);
        tickCount.remove(player);
        if (!currentlyAFK && afk) { //Becomes afk
            notAFK.remove(player);
        } else if (currentlyAFK && !afk) { //Become NOT afk
            notAFK.add(player);
            tickCount.put(player, timeoutTicks);
        }
    }

    private final void clearReferences(final UUID player) {
        notAFK.remove(player);
        tickCount.remove(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        Bukkit.getScheduler()
            .runTask(Main.getInstance(), () -> reset(event.getPlayer().getUniqueId()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(final PlayerInteractEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(final InventoryEvent event) {
        reset(event.getView().getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    //False because it shows the player attempted to do something.
    public void onCommandSender(final PlayerCommandPreprocessEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEat(final PlayerRespawnEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemUse(final PlayerItemConsumeEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(final PlayerRespawnEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(final PlayerJoinEvent event) {
        reset(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDisconnect(final PlayerQuitEvent event) {
        clearReferences(event.getPlayer().getUniqueId());
    }

}
