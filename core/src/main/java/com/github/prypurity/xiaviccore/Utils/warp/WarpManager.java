package com.github.prypurity.xiaviccore.Utils.warp;

import com.github.prypurity.xiaviccore.Main;
import de.leonhard.storage.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WarpManager {

    private final Map<String, Warp> warpMap = new HashMap<>();
    private final Yaml dataFile;

    private static WarpManager instance;

    public static void setInstance(final WarpManager instance) {
        WarpManager.instance = instance;
    }

    public void setAsStaticInstance() {
        setInstance(this);
    }

    public WarpManager(@NotNull final Yaml dataFile) {
        this.dataFile = dataFile;
    }

    public Optional<Warp> getWarp(final String name) {
        if (warpMap.containsKey(ChatColor.stripColor(name))) {
            return Optional.of(warpMap.get(ChatColor.stripColor(name)));
        } else {
            return Optional.empty();
        }
    }

    public void registerWarp(@NotNull final Warp warp) {
        warpMap.remove(ChatColor.stripColor(warp.getName()));
        warpMap.put(ChatColor.stripColor(warp.getName()), warp);
    }

    public BukkitTask save() {
        final Set<Warp> warps = new HashSet<>(warpMap.values());
        final Yaml clone = new Yaml(dataFile); //Yaml Saving //TODO Replace with DAO
        return Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            for (final Warp warp : warps) {
                for (final Map.Entry<String, String> entry : warp.serialize().entrySet()) {
                    clone.set(entry.getKey(), entry.getValue());
                }
            }
        });
    }

}
