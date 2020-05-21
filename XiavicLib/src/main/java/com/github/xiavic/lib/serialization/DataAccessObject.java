package com.github.xiavic.lib.serialization;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DataAccessObject {

    boolean containsKey(String key);

    @NotNull Optional<Map<String, Object>> getRaw(@NotNull String key);

    @NotNull <T extends ConfigurationSerializable> Optional<T> get(@NotNull String key,
        @NotNull Class<T> type);

    @NotNull Path getBackingFile();

    @Nullable String getString(@NotNull String key, @Nullable String def);

    int getInt(@NotNull String key, int def);

    double getDouble(@NotNull String key, double def);

    float getFloat(@NotNull String key, float def);

    long getLong(@NotNull String key, long def);

    @NotNull Set<String> keySet();

    @NotNull Collection<Object> values(@NotNull String key);

    void save(@NotNull String key, @Nullable ConfigurationSerializable object);

    void save(@NotNull String key, int value);

    void save(@NotNull String key, long value);

    void save(@NotNull String key, double value);

    void save(@NotNull String key, float value);


    void save(@NotNull String key, @Nullable String value);

    boolean writeToDisk();

    boolean loadFromDisk(@NotNull Path file);

    @NotNull Path getFile();

}
