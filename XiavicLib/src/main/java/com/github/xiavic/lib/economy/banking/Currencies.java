package com.github.xiavic.lib.economy.banking;

import com.github.xiavic.lib.economy.api.banking.Currency;
import org.jetbrains.annotations.NotNull;

/**
 * Enumerated representation of all hard-coded currencies.
 */
public enum Currencies implements Currency {

    /**
     * Represents a the central "universal" currency used to integrate wih vault,
     * and to pack-against when trading with other currencies.
     */
    CENTRAL("$", "Central Dollar", "Central Dollars");


    private final @NotNull String identifier, name, pluralName;

    Currencies(@NotNull final String identifier, @NotNull final String name,
        @NotNull final String pluralName) {
        this.identifier = identifier;
        this.name = name;
        this.pluralName = pluralName;
    }

    @NotNull public String getName() {
        return name;
    }

    @NotNull public String getIdentifier() {
        return identifier;
    }

    @NotNull public String getPluralName() {
        return pluralName;
    }
}
