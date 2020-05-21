package com.github.xiavic.lib.serialization;

import com.github.xiavic.lib.XiavicLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class YamlDAO implements DataAccessObject {

    private final UUID uniqueID = UUID.randomUUID();
    @NotNull private final YamlConfiguration configuration;
    @NotNull private final Path file;
    private final long maxFileSize;
    private AtomicLong currentSize = new AtomicLong(0);
    private YamlDAO temp;

    public YamlDAO(@Nullable final YamlConfiguration configuration, @NotNull final Path file) {
        this(configuration, file, -1);
    }

    public YamlDAO(@Nullable final YamlConfiguration configuration, @NotNull final Path file,
        final long maxSize) {
        this.configuration = configuration == null ? new YamlConfiguration() : configuration;
        this.file = file;
        this.maxFileSize = maxSize < -1 ? -1 : maxSize;
        currentSize.getAndSet(this.configuration.saveToString().getBytes().length * Byte.SIZE);
        this.temp = new YamlDAO(new File(file.toFile(), "temp_" + uniqueID.toString()), maxFileSize);
    }

    private YamlDAO(final File file, long maxFileSize) {
        this.file = Paths.get(file.getAbsolutePath());
        this.maxFileSize = maxFileSize;
        this.configuration = new YamlConfiguration();
    }

    public long getEstimatedSize() {
        return currentSize.get();
    }

    public boolean usesMultipleFiles() {
        return maxFileSize != -1;
    }

    public long getMaxFileSize() {
        return maxFileSize;
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

    @Override
    public void save(@NotNull final String key, @Nullable final ConfigurationSerializable object) {
        synchronized (configuration) {
            configuration.set(key, object);
        }
        Bukkit.getScheduler().runTaskAsynchronously(XiavicLib.getPlugin(XiavicLib.class), this::writeToDisk);
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
