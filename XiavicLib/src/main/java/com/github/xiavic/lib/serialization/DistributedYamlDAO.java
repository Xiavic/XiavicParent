package com.github.xiavic.lib.serialization;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DistributedYamlDAO implements DataAccessObject {

    private FlatFileYamlDAO index;
    private Collection<Path> fragmentIndex = new HashSet<>();
    private Set<FlatFileYamlDAO> cached;
    private Collection<FlatFileYamlDAO> temp = new HashSet<>(5);
    private final long maxFileSize;

    private DistributedYamlDAO(final long maxFileSize) {
        final Cache<FlatFileYamlDAO, Object> cache =
            CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).maximumSize(5)
                .concurrencyLevel(2).build();
        this.cached = cache.asMap().keySet();
        this.maxFileSize = maxFileSize <= -1 ? -1 : maxFileSize;
    }

    public DistributedYamlDAO(@NotNull final Path index, final long maxFileSize) {
        this(maxFileSize);
        this.index = new FlatFileYamlDAO(new YamlConfiguration(), index, maxFileSize);
        this.index.loadFromDisk(index);
        buildFragmentIndex();
    }

    private void buildFragmentIndex() {
        for (final String key : index.keySet()) {
            final String filePath = index.getString(key, null);
            if (filePath != null) {
                fragmentIndex.add(Paths.get(filePath));
            }
        }
    }

    @Override public @NotNull Path getBackingFile() {
        return index.getBackingFile();
    }

    @Override public boolean containsKey(final String key) {
        return resolveKey(key).isPresent();
    }

    public Optional<FlatFileYamlDAO> resolveKey(String key) {
        for (final FlatFileYamlDAO dao : temp) {
            if (dao.containsKey(key)) {
                Optional.of(dao);
            }
        }
        for (final FlatFileYamlDAO dao : cached) {
            if (dao.containsKey(key)) {
                Optional.of(dao);
            }
        }
        for (final Path fragment : fragmentIndex) {
            if (cached.stream().anyMatch(flatFileYamlDAO -> flatFileYamlDAO.getBackingFile().equals(fragment)));
        } return Optional.empty();
    }

    public void cache(Path path) {
        cached.add(new FlatFileYamlDAO(path));
    }

    @Override public @NotNull Optional<Map<String, Object>> getRaw(@NotNull final String key) {
        return Optional.empty();
    }

    @Override
    public @NotNull <T extends ConfigurationSerializable> Optional<T> get(@NotNull final String key,
        @NotNull final Class<T> type) {
        return Optional.empty();
    }

    @Override
    public void save(@NotNull final String key, @Nullable final ConfigurationSerializable object) {

    }

    @Override public void save(@NotNull final String key, final int value) {

    }

    @Override public void save(@NotNull final String key, final long value) {

    }

    @Override public void save(@NotNull final String key, final double value) {

    }

    @Override public void save(@NotNull final String key, final float value) {

    }

    @Override public void save(@NotNull final String key, @Nullable final String value) {

    }

    @Override public long getLong(@NotNull final String key, final long def) {
        return 0;
    }

    @Override public @NotNull Set<String> keySet() {
        return null;
    }

    @Override public @NotNull Collection<Object> values(@NotNull final String key) {
        return null;
    }

    @Override public int getInt(@NotNull final String key, final int def) {
        return 0;
    }

    @Override public float getFloat(@NotNull final String key, final float def) {
        return 0;
    }

    @Override public double getDouble(@NotNull final String key, final double def) {
        return 0;
    }

    @Override
    public @Nullable String getString(@NotNull final String key, @Nullable final String def) {
        return null;
    }


    @Override public boolean writeToDisk() {
        return false;
    }

    @Override public boolean loadFromDisk(@NotNull final Path file) {
        return false;
    }

    @Override public @NotNull Path getFile() {
        return null;
    }

    public void flushTemp() {

    }
}
