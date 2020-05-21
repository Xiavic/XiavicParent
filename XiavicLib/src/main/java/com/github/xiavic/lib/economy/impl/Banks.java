package com.github.xiavic.lib.economy.impl;

import com.github.xiavic.lib.Utils;
import com.github.xiavic.lib.economy.api.Currency;
import com.github.xiavic.lib.economy.api.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public enum Banks implements Bank {

    /**
     * Represents the instance of the central bank.
     */
    CENTRAL(Currencies.CENTRAL) {
        @Override @NotNull
        public BankAccount createAccount(@NotNull final String name, @NotNull final UUID player) {
            final Banks banks = this;
            final int id = banks.getOrCreateIDFor(player);
            final BankAccount account = new AbstractBankAccount(name, banks, player) {
            };
            banks.accountMap.computeIfAbsent(id, (unused) -> new BankAccountData())
                .addAccount(account);
            return account;
        }

        @Override public boolean supportsCreditCards() {
            return false;
        }

        @Override @NotNull
        public CreditCard getOrCreateCardFor(@NotNull final String name, @NotNull final UUID owner)
            throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Central Bank Cannot Create Credit Cards!");
        }
    };

    @NotNull private final Currency primaryCurrency;
    @NotNull private final Map<UUID, Integer> idMap = new HashMap<>();
    @NotNull private final Map<Integer, BankAccountData> accountMap = new HashMap<>();
    @NotNull private final Map<Currency, Double> currencyConversion = new HashMap<>();
    @NotNull private final Map<UUID, Collection<CreditCard>> creditCards = new HashMap<>();

    Banks(@NotNull final Currency currency) {
        this.primaryCurrency = currency;
    }

    public boolean isEnabled() {
        return true;
    }


    private int getOrCreateIDFor(@NotNull final UUID player) {

        if (idMap.containsKey(player)) {
            return idMap.get(player);
        }
        final Iterator<Integer> iterator =
            idMap.values().stream().sorted(Integer::compareTo).iterator();
        final int last = Integer.MIN_VALUE;
        while (iterator.hasNext()) {
            final int num = iterator.next();
            if (last + 1 != num) {
                break;
            }
        }
        return last + 1;
    }

    @Override @NotNull public Currency getPrimaryCurrency() {
        return primaryCurrency;
    }

    @Override public boolean isCurrencyConversionSupported(@NotNull final Currency currency) {
        return currencyConversion.containsKey(currency);
    }

    @Override public double convertToPrimary(final double sum, @NotNull final Currency original) {
        if (isCurrencyConversionSupported(original)) {
            throw new IllegalArgumentException("Currency conversion not supported!");
        }
        return getConversionRateFor(original) * sum;
    }

    @Override public double getConversionRateFor(@NotNull final Currency currency)
        throws IllegalArgumentException {
        if (isCurrencyConversionSupported(currency)) {
            throw new IllegalArgumentException("Currency conversion not supported!");
        }
        return currencyConversion.get(currency);
    }

    @Override @NotNull public String getName() {
        return Utils.capitalise(name().toLowerCase());
    }



    @Override @NotNull public Optional<BankAccountData> getAccountById(final int Id) {
        return Optional.ofNullable(accountMap.get(Id));
    }

    @Override @NotNull
    public Optional<Integer> getIdByAccount(@NotNull final BankAccountData bankAccount) {
        for (final Map.Entry<Integer, BankAccountData> entry : accountMap.entrySet()) {
            if (entry.getValue().equals(bankAccount))
                return Optional.of(entry.getKey());
        }
        return Optional.empty();
    }

    @Override @NotNull public Optional<BankAccount> getAccountFor(@NotNull final String name,
        @NotNull final UUID player) {
        for (final Map.Entry<Integer, BankAccountData> entry : accountMap.entrySet()) {
            final Optional<BankAccount> optionalBankAccount =
                entry.getValue().getAccount(name, player);
            if (optionalBankAccount.isPresent()) {
                return optionalBankAccount;
            }
        }
        return Optional.empty();
    }

    @Override @NotNull public BankAccountData getAccountsFor(@NotNull final UUID player) {
        if (!idMap.containsKey(player)) {
            return new BankAccountData();
        }
        final int id = idMap.get(player);
        return accountMap.get(id);
    }


    @Override @NotNull public Transaction createTransaction(final UUID invoker,
        final CreditHolder invokingCreditHolder, final CreditHolder targetCreditHolder) {
        if (!invokingCreditHolder.getBackingExecutor().equals(this)) {
            throw new IllegalArgumentException();
        }
        return new Transaction(invoker, invokingCreditHolder, targetCreditHolder);
    }

    @Override public boolean handleTransaction(@NotNull final Transaction transaction) {
        final double sum = transaction.getSum();
        if (sum < 0) {
            return false;
        }
        if (transaction.getInvokingCreditHolder().getBackingExecutor().equals(this)) {
            return transaction.getInvokingCreditHolder().withdraw(sum);
        } else if (transaction.getTargetCreditHolder().getBackingExecutor().equals(this)) {
            return transaction.getTargetCreditHolder().deposit(sum);
        }
        return false;
    }

    @Override public boolean canCallBack(@NotNull final Transaction transaction) {
        if (transaction.getInvokingCreditHolder().getBackingExecutor().equals(this)) {
            return !transaction.getInvokingCreditHolder().isFrozen();
        } else if (transaction.getTargetCreditHolder().getBackingExecutor().equals(this)) {
            return transaction.getTargetCreditHolder().has(transaction.getSum()) && !transaction
                .getTargetCreditHolder().isFrozen();
        }
        return false;
    }

    @Override public void callBack(@NotNull final Transaction transaction) {
        if (!canCallBack(transaction)) {
            return;
        }
        final double sum = transaction.getSum();
        if (sum < 0) {
            return;
        }
        if (transaction.getInvokingCreditHolder().getBackingExecutor().equals(this)) {
            assert transaction.getInvokingCreditHolder().deposit(sum);
        } else {
            assert !transaction.getTargetCreditHolder().getBackingExecutor().equals(this)
                || transaction.getTargetCreditHolder().withdraw(sum);
        }
    }

    /**
     * Basic implementaion of a {@link CreditCard} object.
     */
    public static class CreditCardImpl implements CreditCard {

        @Nullable private final Supplier<ItemStack> supplier;
        @NotNull private final String name;
        @NotNull private final UUID owner;
        private final int numericalID;
        private final double interest;
        @NotNull private final Bank bank;
        private boolean enabled;

        public CreditCardImpl(final @NotNull String name, final @NotNull Bank bank,
            final @NotNull UUID owner, final int numericalID, final double interest,
            final @Nullable Supplier<ItemStack> toItem) {
            this.name = name;
            this.bank = bank;
            this.owner = owner;
            this.numericalID = numericalID;
            this.interest = interest;
            this.supplier = toItem;
        }

        @Override public int getNumericalID() {
            return numericalID;
        }

        @Override @NotNull public String getName() {
            return name;
        }

        @Override @NotNull public UUID getOwner() {
            return owner;
        }

        @Override @NotNull public Bank getBackingBank() {
            return bank;
        }

        @Override public double getInterest() {
            return interest;
        }

        @Override public boolean isEnabled() {
            return enabled;
        }

        @Override public void setEnabled(final boolean enabled) {
            this.enabled = enabled;
        }

        @Override @Nullable public ItemStack getAsItem() {
            return supplier.get();
        }

        @Override @NotNull public Transaction createTransaction(@NotNull final UUID invoker,
            @NotNull final CreditHolder invokingCreditHolder,
            @NotNull final CreditHolder targetCreditHolder) {
            return bank.createTransaction(invoker, invokingCreditHolder, targetCreditHolder);
        }

        @Override public boolean handleTransaction(@NotNull final Transaction transaction) {
            final double sum = transaction.getSum();
            if (sum < 0) {
                return false;
            }
            if (transaction.getInvokingCreditHolder().getBackingExecutor().equals(this)) {
                return transaction.getInvokingCreditHolder().withdraw(sum);
            } else if (transaction.getTargetCreditHolder().getBackingExecutor().equals(this)) {
                return transaction.getTargetCreditHolder().deposit(sum);
            }
            return false;
        }

        @Override public void callBack(@NotNull final Transaction transaction)
            throws IllegalArgumentException {
            bank.callBack(transaction);
        }

        @Override public boolean canCallBack(@NotNull final Transaction transaction) {
            return bank.canCallBack(transaction);
        }
    }
}
