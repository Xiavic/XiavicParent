package com.github.xiavic.lib.serialization;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public enum StorageType {

    FLAT_YAML, DISTRIBUTED_YAML, SQL, MONGO_DB;

    private final Supplier<DataAccessObject> supplier;

    StorageType() {
        supplier = null;
    }

    StorageType(@NotNull final Supplier<DataAccessObject> supplier) {
        this.supplier = supplier;
    }

    @NotNull
    public DataAccessObject createAccessor() {
        return Objects.requireNonNull(supplier.get());
    }
}
