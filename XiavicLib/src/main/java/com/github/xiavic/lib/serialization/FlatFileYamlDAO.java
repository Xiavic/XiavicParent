package com.github.xiavic.lib.serialization;

import com.github.xiavic.lib.XiavicLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;

public class FlatFileYamlDAO implements DataAccessObject {

    @NotNull private final YamlConfiguration configuration;
    @NotNull private final Path file;

    public FlatFileYamlDAO(@NotNull final Path file) {
        this.configuration = new YamlConfiguration();
        this.file = file;
        if (!loadFromDisk(file)) {
            throw new IllegalStateException("Unable to load from disk!");
        }
    }

    public FlatFileYamlDAO(@Nullable final YamlConfiguration configuration,
        @NotNull final Path file) {
        this(configuration, file, -1);
    }

    public FlatFileYamlDAO(@Nullable final YamlConfiguration configuration,
        @NotNull final Path file, final long maxSize) {
        this.configuration = configuration == null ? new YamlConfiguration() : configuration;
        this.file = file;
    }

    @Override public @NotNull Path getBackingFile() {
        return file;
    }

    public YamlConfiguration getDataCopy() {
        final String data;
        synchronized (configuration) {
            data = configuration.saveToString();
        }
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.loadFromString(data);
        } catch (InvalidConfigurationException ex) {
            throw new IllegalStateException("Database corrupted!", ex);
        }
        return yaml;
    }

    @Override public boolean containsKey(final String key) {
        return configuration.contains(key);
    }

    @Override @NotNull public Path getFile() {
        return file;
    }

    @Override public boolean loadFromDisk(@NotNull final Path file) {
        synchronized (configuration) {
            try {
                configuration.load(file.toFile());
                return true;
            } catch (final InvalidConfigurationException | IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    @Override public @NotNull Optional<Map<String, Object>> getRaw(final @NotNull String key) {
        synchronized (configuration) {
            final ConfigurationSection section = configuration.getConfigurationSection(key);
            if (section == null) {
                return Optional.empty();
            }
            Map<String, Object> map = new HashMap<>();
            loadMapFromSection(section, map);
            return Optional.of(map);
        }
    }

    @Override @NotNull
    public <T extends ConfigurationSerializable> Optional<T> get(@NotNull final String key,
        @NotNull final Class<T> def) {
        return getRaw(key).map((Map<String, Object> result) -> {
            try {
                final Constructor<T> constructor = def.getDeclaredConstructor(Map.class);
                return constructor.newInstance(result);
            } catch (final ReflectiveOperationException ignored) {
            }
            try {
                final Method method = def.getDeclaredMethod("deserialize", Map.class);
                if (method.getReturnType() == def) {
                    return def.cast(method.invoke(null, result));
                }
            } catch (ReflectiveOperationException ignored) {
            }
            try {
                final Method method = def.getDeclaredMethod("valueOf", Map.class);
                if (method.getReturnType() == def) {
                    return def.cast(method.invoke(null, result));
                }
            } catch (ReflectiveOperationException ignored) {
            }
            return null;
        });
    }

    @Override @Nullable public String getString(@NotNull final String key, final String def) {
        return configuration.getString(key, def);
    }

    @Override public int getInt(@NotNull final String key, final int def) {
        return configuration.getInt(key, def);
    }

    @Override public long getLong(@NotNull final String key, final long def) {
        return configuration.getLong(key, def);
    }

    @Override public double getDouble(@NotNull final String key, final double def) {
        return configuration.getDouble(key, def);
    }

    @Override public float getFloat(@NotNull final String key, final float def) {
        return (float) configuration.getDouble(key, def);
    }

    @Override public @NotNull Set<String> keySet() {
        synchronized (configuration) {
            return configuration.getKeys(false);
        }
    }

    @Override public @NotNull Collection<Object> values(@NotNull final String key) {
        ConfigurationSection section = configuration.getConfigurationSection(key);
        if (section == null) {
            return new HashSet<>();
        }
        return section.getValues(false).values();
    }

    @Override
    public void save(@NotNull final String key, @Nullable final ConfigurationSerializable object) {
        synchronized (configuration) {
            configuration.set(key, object);
        }
        Bukkit.getScheduler()
            .runTaskAsynchronously(XiavicLib.getPlugin(XiavicLib.class), this::writeToDisk);
    }

    @Override public void save(@NotNull final String key, final int value) {
        configuration.set(key, value);
    }

    @Override public void save(@NotNull final String key, final long value) {
        configuration.set(key, value);
    }

    @Override public void save(@NotNull final String key, final double value) {
        configuration.set(key, value);
    }

    @Override public void save(@NotNull final String key, final float value) {
        configuration.set(key, value);
    }

    @Override public void save(@NotNull final String key, @Nullable final String value) {
        configuration.set(key, value);
    }

    public static void loadMapFromSection(@Nullable final ConfigurationSection section,
        @NotNull Map<String, Object> existing) {
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            if (!section.isConfigurationSection(key)) {
                Map<String, Object> map = new HashMap<>();
                loadMapFromSection(section.getConfigurationSection(key), map);
                existing.put(key, map);
            } else {
                loadMapFromSection(section.getConfigurationSection(key), existing);
            }
        }
    }

    @Override public boolean writeToDisk() {
        synchronized (configuration) {
            try {
                configuration.save(file.toFile());
            } catch (final IOException ex) {
                return false;
            }
            return true;
        }
    }
}
