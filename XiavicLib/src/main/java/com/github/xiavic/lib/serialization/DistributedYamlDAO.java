package com.github.xiavic.lib.serialization;

import com.github.xiavic.lib.XiavicLib;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * A distributed database powered by yaml. This implementation
 * is really ill-advised as yaml should not be used if such a large
 * database is required. In any case, this implementation caches
 * 5 fragments into memory at any given moment.
 * <p>
 * This implementation
 * generally attempts to avoid data-duplication by saving new data
 * to temporary cached files. When a certain threshold is met, the data
 * in these files are then synchronised to the master database. Queries will
 * always check in the following order: Temporary Files -> Cache -> Master.
 * <p>
 * This implementation uses {@link FlatFileYamlDAO}.
 */
public class DistributedYamlDAO implements DataAccessObject {

    //TODO flush temp files periodically.

    private final long maxFileSize;
    private final long syncThreshold;
    private final YamlConfiguration pool = new YamlConfiguration();
    private final Collection<Path> fragmentIndex = ConcurrentHashMap.newKeySet();
    private final Set<FlatFileYamlDAO> cached;
    private final Collection<FlatFileYamlDAO> temp = ConcurrentHashMap.newKeySet();
    private FlatFileYamlDAO index;
    private UUID uniqueID = UUID.randomUUID();

    private DistributedYamlDAO(final long maxFileSize, final long tempSizeThreshold) {
        final Cache<FlatFileYamlDAO, Object> cache =
            CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).maximumSize(5)
                .concurrencyLevel(2).build();
        this.cached = cache.asMap().keySet();
        this.maxFileSize = maxFileSize <= -1 ? -1 : maxFileSize;
        if (tempSizeThreshold < 0) {
            throw new IllegalArgumentException("Temp size threshold must be greater than 0!");
        }
        this.syncThreshold = tempSizeThreshold;

    }

    /**
     * Creates a new access object for a given Distributed Yaml Database.
     * If there was no
     *
     * @param index             The path to the index file.
     * @param maxFileSize       The max size for each file.
     * @param tempSizeThreshold The max size for the temporary files before they are synchronised to the main database.
     *                          This number must be greater than 0.
     * @throws IllegalArgumentException Thrown if the UniqueID gathered from the index file is invalid.
     * @throws IllegalStateException    Thrown if the index file could not be loaded into memory.
     */
    public DistributedYamlDAO(@NotNull final Path index, final long maxFileSize,
        final long tempSizeThreshold) throws IllegalArgumentException, IllegalStateException {
        this(maxFileSize, tempSizeThreshold);
        this.index = new FlatFileYamlDAO(new YamlConfiguration(), index);
        this.index.loadFromDisk(index);
        String rawUUID = this.index.getString("uniqueID", null);
        if (rawUUID != null) {
            this.uniqueID = UUID.fromString(rawUUID);
            this.index.save("uniqueID", uniqueID.toString());
        }
        buildFragmentIndex();
    }

    private static long estimateSizeFor(@NotNull final YamlConfiguration configuration) {
        return configuration.saveToString().getBytes().length * Byte.SIZE;
    }

    private void buildFragmentIndex() {
        for (final String key : index.keySet()) {
            final String filePath = index.getString(key, null);
            if (filePath != null && !filePath.isEmpty()) {
                fragmentIndex.add(Paths.get(filePath));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override public @NotNull Path getBackingFile() {
        return index.getBackingFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean containsKey(final String key) {
        return resolveKey(key).isPresent();
    }


    public Optional<FlatFileYamlDAO> resolveCached(String key) {
        for (final FlatFileYamlDAO dao : temp) {
            if (dao.containsKey(key)) {
                return Optional.of(dao);
            }
        }
        for (final FlatFileYamlDAO dao : cached) {
            if (dao.containsKey(key)) {
                return Optional.of(dao);
            }
        }
        return Optional.empty();
    }

    public Optional<FlatFileYamlDAO> resolveKey(String key) {
        final Optional<FlatFileYamlDAO> optional = resolveCached(key);
        if (optional.isPresent()) {
            return optional;
        }
        int index = 0;
        for (final Path fragment : fragmentIndex) {
            if (cached.stream()
                .anyMatch(flatFileYamlDAO -> flatFileYamlDAO.getBackingFile().equals(fragment))) {
                continue;
            }
            cache(fragment);
            if (index++ % 3 == 0) {
                Optional<FlatFileYamlDAO> dao = resolveCached(key);
                if (dao.isPresent()) {
                    return dao;
                }
            }
        }
        return Optional.empty();
    }

    private FlatFileYamlDAO cache(Path path) {
        final FlatFileYamlDAO dao = new FlatFileYamlDAO(path);
        cached.add(dao);
        return dao;
    }

    /**
     * Estimate the size adding this section would take up.
     *
     * @param key   The key.
     * @param value The value.
     * @return Returns a loosely estimated size based of the {@link YamlConfiguration#saveToString()}
     */
    private long estimateSizeFor(String key, Object value) {
        synchronized (pool) {
            pool.set(key, value);
            final long size = estimateSizeFor(pool);
            pool.set(key, null);
            return size;
        }
    }

    @Override @NotNull public Optional<Map<String, Object>> getRaw(@NotNull final String key) {
        return resolveKey(key).flatMap(flatFileYamlDAO -> flatFileYamlDAO.getRaw(key));
    }

    /**
     * {@inheritDoc}
     */
    @Override @NotNull public <T extends ConfigurationSerializable> Optional<T> getSerializable(
        @NotNull final String key, @NotNull final Class<T> type) {
        return resolveKey(key)
            .flatMap(flatFileYamlDAO -> flatFileYamlDAO.getSerializable(key, type));
    }

    /**
     * Create a new fragment. This method will not register to
     * the index file cached in MEMORY.
     *
     * @return Returns a new {@link FlatFileYamlDAO}.
     * @throws IOException Thrown if an IOException occurs when the file is created.
     */
    private FlatFileYamlDAO newTempFragment() throws IOException {
        final String indexName = index.getBackingFile().toFile().getName().split("\\.")[0];
        return new FlatFileYamlDAO(new YamlConfiguration(), Files.createTempFile(indexName, ".temp"));
    }

    private FlatFileYamlDAO newFragment() throws IOException{
        final String indexName = index.getBackingFile().toFile().getName().split("\\.")[0];
        Path file = Paths.get(new File(index.getBackingFile().toFile(), indexName + "_" + UUID.randomUUID().toString() + ".yml").getAbsolutePath());
        return new FlatFileYamlDAO(new YamlConfiguration(), Files.createFile(file));
    }

    public void registerFragment(FlatFileYamlDAO fragment) {
        if (!fragmentIndex.contains(fragment.getBackingFile())) {
            fragment.save(UUID.randomUUID().toString(), fragment.getBackingFile().toFile().getAbsolutePath());
            buildFragmentIndex();
        }
    }

    /**
     * Saves to any existing {@link FlatFileYamlDAO} in {@link #temp}, if none are free,
     * a new fragment is created.
     *
     * @return Returns the {@link FlatFileYamlDAO} the object was saved to.
     */
    public FlatFileYamlDAO createOrSave(String key, Object value, boolean isTemp) {
        long estimatedSize = estimateSizeFor(key, value);
        if (estimatedSize > maxFileSize) {
            throw new IllegalStateException("Value too large! Unable to save.");
        }
        long tempSize = 0;
        for (final FlatFileYamlDAO dao : temp) {
            tempSize += estimateSizeFor(dao.getDataCopy());
        }
        if (tempSize >= syncThreshold) {
            //Let this method finish, give ~ 25ms (or 5 ticks) since this method may be called async.
            Bukkit.getScheduler().runTaskLaterAsynchronously(XiavicLib.getPlugin(XiavicLib.class), this::writeToDisk, 5);
        }
        for (FlatFileYamlDAO fileYamlDAO : temp) {
            if (fileYamlDAO.getEstimatedSize() + estimatedSize < maxFileSize) {
                fileYamlDAO.save(key, value);
                return fileYamlDAO;
            }
        }
        try {
            final FlatFileYamlDAO object = isTemp ? newTempFragment() : newFragment();
            temp.add(object);
            object.save(key, value);
            return object;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create new YAML fragment!", ex);
        }
    }


    /**
     * Uses {@link #createOrSave(String, Object, boolean)} to obtain a fragment,
     * if the fragment is yet to be indexed,
     *
     * @return Returns the {@link FlatFileYamlDAO} the object was saved to.
     */
    private FlatFileYamlDAO createOrSaveMaster(String key, Object value) {
        FlatFileYamlDAO dao = createOrSave(key, value, false);
        if (fragmentIndex.contains(dao.getBackingFile())) {
            return dao;
        }
        registerFragment(dao);
        return dao;
    }


    /**
     * {@inheritDoc}
     */
    @Override @NotNull public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        for (DataAccessObject dao : temp) {
            keys.addAll(dao.keySet());
        }
        for (DataAccessObject dao : cached) {
            keys.addAll(dao.keySet());
        }
        for (final Path path : fragmentIndex) {
            keys.addAll(cache(path).keySet());
        }
        return keys;
    }

    /**
     * {@inheritDoc}
     */
    @Override public @NotNull Collection<?> values(@NotNull final String key) {
        return resolveKey(key).map(FlatFileYamlDAO::keySet).orElse(new HashSet<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override public void save(@NotNull final String key, @Nullable final Object object)
        throws IllegalArgumentException {
        createOrSave(key, object, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override public int getInt(@NotNull final String key, final int def) {
        return resolveKey(key).map(dao -> dao.getInt(key, def)).orElse(def);
    }

    /**
     * {@inheritDoc}
     */
    @Override public float getFloat(@NotNull final String key, final float def) {
        return resolveKey(key).map(dao -> dao.getFloat(key, def)).orElse(def);
    }

    /**
     * {@inheritDoc}
     */
    @Override public double getDouble(@NotNull final String key, final double def) {
        return resolveKey(key).map(dao -> dao.getDouble(key, def)).orElse(def);
    }

    /**
     * {@inheritDoc}
     */
    @Override public long getLong(@NotNull final String key, final long def) {
        return resolveKey(key).map(dao -> dao.getLong(key, def)).orElse(def);
    }

    /**
     * {@inheritDoc}
     */
    @Override @Nullable public String getString(@NotNull final String key,
        @Nullable final String def) {
        return resolveKey(key).map(dao -> dao.getString(key, def)).orElse(def);
    }

    /**
     * {@inheritDoc}
     */
    @Override @Nullable public Object getObject(@NotNull final String key) {
        return resolveKey(key).map(dao -> dao.getObject(key)).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override public boolean writeToDisk() {
        Map<String, Object> overflow = new HashMap<>();
        for (final FlatFileYamlDAO dao : temp) {
            for (String key : dao.keySet()) {
                Object value = dao.getObject(key);
                long size = estimateSizeFor(key, value);
                Optional<FlatFileYamlDAO> optional = resolveKey(key);
                if (!optional.isPresent()) {
                    continue;
                }
                FlatFileYamlDAO flatFileYamlDAO = optional.get();
                if (flatFileYamlDAO.containsKey(key)) {
                    flatFileYamlDAO.save(key, null); //Invalidate existing.
                    if (size + estimateSizeFor(flatFileYamlDAO.getDataCopy()) > maxFileSize) {
                        overflow.put(key, value); //Means the file will overflow.
                    } else {
                        flatFileYamlDAO.save(key, value); //Save to the file.
                    }
                }
            }
        }
        Collection<DataAccessObject> daos = new HashSet<>();
        for (Map.Entry<String, Object> entry : overflow.entrySet()) {
            daos.add(createOrSaveMaster(entry.getKey(), entry.getValue()));
        }
        boolean success = true;
        for (DataAccessObject object : daos) {
            if (success && !object.writeToDisk()) {
                success = false;
            }
        }
        return success;
    }

    /**
     * {@inheritDoc}
     * Only the index file for this database should be provided!
     * Loads the and builds the index based off the provided index file.
     *
     * @param file The index file for the database to load.
     * @return Return true if loading was a success.
     */
    @Override public boolean loadFromDisk(@NotNull final Path file) {
        try {
            FlatFileYamlDAO dao = new FlatFileYamlDAO(file);
            final String uniqueID = dao.getString("uniqueID", null);
            if (uniqueID == null) {
                throw new IllegalArgumentException("Invalid Index file!");
            }
            this.uniqueID = UUID.fromString(uniqueID);
            this.temp.clear();
            this.fragmentIndex.clear();
            this.cached.clear();
            this.index = dao;
            buildFragmentIndex();
            return true;
        } catch (Throwable unknown) {
            unknown.printStackTrace();
            return false;
        }
    }

    @Override public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DistributedYamlDAO that = (DistributedYamlDAO) o;

        if (maxFileSize != that.maxFileSize)
            return false;
        if (!Objects.equals(index, that.index))
            return false;
        if (!Objects.equals(uniqueID, that.uniqueID))
            return false;
        if (!Objects.equals(fragmentIndex, that.fragmentIndex))
            return false;
        if (!Objects.equals(cached, that.cached))
            return false;
        if (!Objects.equals(temp, that.temp))
            return false;
        return pool.equals(that.pool);
    }

    @Override public int hashCode() {
        int result = index != null ? index.hashCode() : 0;
        result = 31 * result + (uniqueID != null ? uniqueID.hashCode() : 0);
        result = 31 * result + (fragmentIndex != null ? fragmentIndex.hashCode() : 0);
        result = 31 * result + (cached != null ? cached.hashCode() : 0);
        result = 31 * result + (temp != null ? temp.hashCode() : 0);
        result = 31 * result + (int) (maxFileSize ^ (maxFileSize >>> 32));
        result = 31 * result + pool.hashCode();
        return result;
    }
}
