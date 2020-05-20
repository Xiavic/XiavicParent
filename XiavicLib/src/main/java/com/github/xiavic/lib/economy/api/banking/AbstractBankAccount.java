package com.github.xiavic.lib.economy.api.banking;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Base Bank Account, has almost all functionality implemented.
 */
public abstract class AbstractBankAccount implements BankAccount {

    @NotNull private final String name;
    @NotNull private final Bank bank;
    @NotNull private final UUID opener;
    private double balance;
    private boolean frozen = false;

    private Map<UUID, AccessLevel> accessLevelMap = new HashMap<>();

    public AbstractBankAccount(@NotNull final AbstractBankAccount other) {
        this.name = other.name;
        this.bank = other.bank;
        this.opener = other.opener;
        this.balance = other.balance;
        this.frozen = other.frozen;
    }

    public AbstractBankAccount(@NotNull final String name, @NotNull final Bank bank,
        @NotNull final UUID opener) {
        this(name, bank, opener, 0D);
    }

    public AbstractBankAccount(@NotNull final String name, @NotNull final Bank bank,
        @NotNull final UUID opener, final double initialBalance) {
        super();
        this.name = name;
        this.bank = bank;
        this.opener = opener;
        setBalance(initialBalance);
    }

    /**
     * Force sets the balance for this Account's balance.
     * This method does not care about whether
     * the account is frozen.
     *
     * @param balance The non-negative balance.
     */
    @Override public void setBalance(final double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Invalid Balance, cannot be negative!");
        }
        this.balance = balance;
    }

    @Override public boolean isFrozen() {
        return frozen;
    }

    @Override public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    @Override @NotNull public UUID getOpener() {
        return opener;
    }

    @Override @NotNull public Bank getBackingExecutor() {
        return bank;
    }

    @Override public double getCurrentBalance() {
        return balance;
    }

    @Override public boolean deposit(final double targetSum) {
        if (isFrozen()) {
            return false;
        }
        if (targetSum < 0) {
            return withdraw(-targetSum);
        }
        setBalance(getCurrentBalance() + targetSum);
        return true;
    }

    @Override public boolean withdraw(final double targetSum) {
        if (isFrozen()) {
            return false;
        }
        if (targetSum < 0) {
            return deposit(-targetSum);
        }
        if (targetSum > getCurrentBalance()) {
            return false;
        }
        setBalance(getCurrentBalance() - targetSum);
        return true;
    }

    @Override @NotNull public String getName() {
        return name;
    }

    @Override @NotNull public AccessLevel getAccessOf(@NotNull final UUID player) {
        return accessLevelMap.get(player);
    }

    @Override
    public void setAccessOf(@NotNull final UUID player, @NotNull final AccessLevel newLevel) {
        accessLevelMap.remove(player);
        accessLevelMap.put(player, newLevel);
    }

    @Override @NotNull public Map<UUID, AccessLevel> getRegisteredPeers() {
        return accessLevelMap;
    }
}
