package com.github.xiavic.lib.NMSHandler;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum NMSVersion {
    V1_8_R1, V1_8_R2, V1_8_R3, V1_9_R1, v1_9_R2, v1_10_R1, v1_11_R1, V1_12_R1, v1_13_R1, V1_13_R2, V1_14_R1, V1_15_R1, V1_16_R1;

    NMSVersion() {
    }

    public static NMSVersion getCurrent() throws IllegalArgumentException {
        return NMSVersion.valueOf("V" + Bukkit.getServer().getClass().getPackage().getName()
            .replace("org.bukkit.craftbukkit", ""));
    }

    @NotNull public String getNumericalName() {
        return name().substring(1);
    }

    public int getMinorRevision() {
        return Integer.parseInt(name().substring(name().indexOf("R") + 1));
    }

    public int getMajorRevision() {
        return Integer.parseInt(name().substring(3, name().indexOf("R") - 2));
    }

    @NotNull public NMSVersion getNext() {
        if (this.ordinal() == values().length) {
            return this;
        }
        return values()[this.ordinal() + 1];
    }

    @NotNull public NMSVersion getPrevious() {
        if (this.ordinal() == 0) {
            return this;
        }
        return values()[this.ordinal() - 1];
    }

    public boolean isNewerThan(@NotNull final NMSVersion other) {
        return other.ordinal() > this.ordinal();
    }

    public boolean isOlderThan(@NotNull final NMSVersion other) {
        return other.ordinal() < this.ordinal();
    }
}
