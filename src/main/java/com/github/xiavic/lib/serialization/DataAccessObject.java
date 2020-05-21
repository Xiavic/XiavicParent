package com.github.xiavic.lib.serialization;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents an access object for a given database implementation.
 * Implementations of this interface SHOULD be thread-safe, however,
 * subclasses should clearly document if the implementation is not thread-safe.
 *
 * @see StorageType
 */
public interface DataAccessObject {

    /**
     * Whether this database contains a given key.
     *
     * @param key The key to check for.
     * @return Returns whether this database contains the given key.
     */
    boolean containsKey(String key);


    /**
     * Get the never-null raw {@link ConfigurationSerializable#serialize()} map
     * representing the state of a serialized object.
     *
     * @param key The key.
     * @return Returns a never-null map representation of this object.
     */
    @NotNull Optional<Map<String, Object>> getRaw(@NotNull String key);

    /**
     * Get the nullable {@link Object} which is mapped to the provided key.
     *
     * @param key The key.
     * @return Returns the object if present which may be null. If no key was found, null is returned.
     */
    @Nullable Object getObject(@NotNull String key);

    /**
     * Get a never-null {@link Optional} {@link ConfigurationSerializable} from the given key.
     *
     * @param key  The key of the object.
     * @param type The class instance of the targeted type, required due to type-erasure.
     * @param <T>  The type of the serializable.
     * @return Returns a never-null optional wrapping the value mapped to this key.
     */
    @NotNull <T extends ConfigurationSerializable> Optional<T> getSerializable(@NotNull String key,
        @NotNull Class<T> type);

    /**
     * @return Returns a never-null {@link Path} which represents the
     * * physical location of the database on the disk.
     */
    @NotNull Path getBackingFile();

    /**
     * Get a mapped value from the given key.
     *
     * @param key The key, which cannot be null.
     * @param def The default value which may be null.
     * @return Returns the value mapped to the given key, or the default value
     * if the key was not found, or if the original value was <code>null</code>.
     */
    @Nullable String getString(@NotNull String key, @Nullable String def);

    /**
     * Get a mapped value from the given key.
     *
     * @param key The key, which cannot be null.
     * @param def The default value.
     * @return Returns the value mapped to the given key, or the default value
     * if the key was not found.
     */
    int getInt(@NotNull String key, int def);

    /**
     * Get a mapped value from the given key.
     *
     * @param key The key, which cannot be null.
     * @param def The default value.
     * @return Returns the value mapped to the given key, or the default value
     * if the key was not found.
     */
    double getDouble(@NotNull String key, double def);

    /**
     * Get a mapped value from the given key.
     *
     * @param key The key, which cannot be null.
     * @param def The default value.
     * @return Returns the value mapped to the given key, or the default value
     * if the key was not found.
     */
    float getFloat(@NotNull String key, float def);

    /**
     * Get a mapped value from the given key.
     *
     * @param key The key, which cannot be null.
     * @param def The default value.
     * @return Returns the value mapped to the given key, or the default value
     * if the key was not found.
     */
    long getLong(@NotNull String key, long def);

    /**
     *
     * @return Returns a never-null {@link Set<String>} which contains all the keys
     * this database contains. Modifications to the returned list will
     * not be reflected in the database.
     */
    @NotNull Set<String> keySet();

    /**
     * Get a never null {@link Collection} of values mapped to a given key.
     * @param key The key.
     * @return Returns a never-null collection of value mapped to the given key. Modifications to
     * this collection will not be reflected in the database.
     */
    @NotNull Collection<?> values(@NotNull String key);

    /**
     * Save an object to the database. This method does not guarantee
     * that the changes are immediately written to the disk.
     * @param key The key.
     * @param object The object to save.
     * @throws IllegalArgumentException Thrown if the given database does not
     * support the saving of such an object.
     */
    void save(@NotNull String key, @Nullable Object object) throws IllegalArgumentException;

    /**
     * Force all changes to be written to the disk.
     * @return Returns true if the operation was successful.
     */
    boolean writeToDisk();

    /**
     * Load a database from the disk. Implementations should guarantee
     * that the file from {@link #getFile()} can be loaded by this method.
     * @param file The physical file.
     * @return Returns true if the operation was successful.
     */
    boolean loadFromDisk(@NotNull Path file);
}
