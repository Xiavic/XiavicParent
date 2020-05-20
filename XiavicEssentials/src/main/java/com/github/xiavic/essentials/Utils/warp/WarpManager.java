package com.github.xiavic.essentials.Utils.warp;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum WarpManager implements IWarpManager<Warp> {

    INSTANCE;

    private Collection<Warp> warps = new HashSet<>();

    @Override public boolean isWarp(@NotNull final Location location, boolean useBlockloc) {
        for (final Warp warp : warps) {
            final Location loc = warp.getLocation();
            if (useBlockloc && IWarpManager.areCoordinatesEquals(loc, location) || loc.equals(location)) {
                return true;
            }
        }
        return false;
    }

    @Override public Optional<Warp> getWarp(final String name) {
        return getWarps(warp -> warp.getName().equalsIgnoreCase(name)).findAny();
    }

    @Override @NotNull public Collection<Warp> getWarps() {
        return new HashSet<>(warps);
    }

    @Override @NotNull public Stream<Warp> getWarps(@NotNull final Predicate<Warp> filter) {
        return warps.stream().filter(filter);
    }

    @Override @NotNull
    public Collection<Warp> getFilteredWarps(@NotNull final Predicate<Warp> filter) {
        return getWarps(filter).collect(Collectors.toSet());
    }

    @Override @NotNull public Collection<Warp> getWarps(World world) {
        return getFilteredWarps(warp -> warp.getLocation().getWorld() == world);
    }

    @Override @NotNull
    public Collection<Warp> getAccessibleToPermissible(@NotNull final Permissible permissible) {
        return getFilteredWarps(warp -> warp.canBeAccessedBy(permissible));
    }

    @Override public void registerWarp(@NotNull final Warp warp) {
        warps.remove(warp);
        warps.add(warp);
    }

    @Override public void unregisterWarp(@NotNull final Warp warp) {
        warps.remove(warp);
    }
}
