package com.github.xiavic.lib.serialization;

import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import co.aikar.idb.PooledDatabaseOptions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

public class SqlDAO implements DataAccessObject{

    @NotNull private final Database database;
    @NotNull private final String tableName;

    public SqlDAO(@NotNull final DatabaseOptions databaseOptions, @NotNull final String table) {
        this(PooledDatabaseOptions.builder().options(databaseOptions).createHikariDatabase(), table);
    }

    public SqlDAO(@NotNull final Database database, @NotNull final String table) {
        this.database = database;
        this.tableName = table;
    }

    @Override
    public void close() {
        database.close();
    }

    @Override public boolean containsKey(final String key) throws IOException {
         try {
             DbRow row = database.createStatement().executeQueryGetFirstRow("SELECT * FROM " + tableName + " WHERE " + "KEY = " + key);
             return row != null && !row.isEmpty();
         } catch (SQLException ex) {
             ex.printStackTrace();
             return false;
         }
    }

    @Override public @NotNull Optional<Map<String, Object>> getRaw(@NotNull final String key) {
        try {
            DbRow row = database.createStatement().executeQueryGetFirstRow("SELECT * FROM " + tableName + " WHERE " + "KEY = " + key);
            if (row == null) {
                return Optional.empty();
            }
            //row.key
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
        throw new UnsupportedOperationException();
    }

    @Override public @Nullable Object getObject(@NotNull final String key) {
        return null;
    }

    @Override public @NotNull <T extends ConfigurationSerializable> Optional<T> getSerializable(
        @NotNull final String key, @NotNull final Class<T> type) {
        return Optional.empty();
    }

    @Override public @NotNull Path getBackingFile() {
        return null;
    }

    @Override
    public @Nullable String getString(@NotNull final String key, @Nullable final String def) {
        return null;
    }

    @Override public int getInt(@NotNull final String key, final int def) {
        return 0;
    }

    @Override public double getDouble(@NotNull final String key, final double def) {
        return 0;
    }

    @Override public float getFloat(@NotNull final String key, final float def) {
        return 0;
    }

    @Override public long getLong(@NotNull final String key, final long def) {
        return 0;
    }

    @Override public @NotNull byte[] getByteArray(@NotNull final String key) {
        return new byte[0];
    }

    @Override public @NotNull Set<String> keySet() {
        return null;
    }

    @Override public @NotNull Collection<?> values(@NotNull final String key) {
        return null;
    }

    @Override public void save(@NotNull final String key, @Nullable final Object object)
        throws IllegalArgumentException {

    }

    @Override public boolean writeToDisk() {
        return false;
    }

    @Override public boolean loadFromDisk(@NotNull final Path file) {
        return false;
    }
}
