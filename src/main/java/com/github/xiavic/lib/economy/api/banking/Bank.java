package com.github.xiavic.lib.economy.api.banking;

import de.leonhard.storage.util.Valid;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Represents a bank, an entity which stores money.
 */
public interface Bank extends ICreditService, TransactionExecutor {

    @NotNull static Builder builder() {
        return new Builder();
    }

    /**
     * @return Returns the identifiable name of this bank.
     */
    @NotNull String getName();

    /**
     * Get the primary currency of this bank.
     *
     * @return Returns the instance of this bank's primary currency.
     */
    @NotNull Currency getPrimaryCurrency();

    /**
     * Certain banks may not be able to create credit cards,
     * such as the {@link com.github.xiavic.lib.economy.banking.Banks} CENTRAL bank.
     *
     * @return Returns whether or not this bank supports
     * the creation of {@link CreditCard}s.
     */
    boolean supportsCreditCards();

    /**
     * Checks whether a certain {@link Currency} can be converted to this bank's
     * primary currency.
     *
     * @param currency The target currency to convert.
     * @return Returns whether the currency can be converted.
     * @see #getPrimaryCurrency()
     */
    boolean isCurrencyConversionSupported(@NotNull Currency currency);

    /**
     * Get the ratio (exchange rate) of a currency against
     * this bank's primary {@link Currency}
     *
     * @param currency The currency to convert,
     * @return Returns a double value of the exchange rate for 1 unit.
     * @throws IllegalArgumentException Thrown if {@link #isCurrencyConversionSupported(Currency)} returned false.
     */
    double getConversionRateFor(@NotNull Currency currency) throws IllegalArgumentException;

    default double convertToPrimary(final double sum, @NotNull final Currency original)
        throws IllegalArgumentException {
        return getConversionRateFor(original) * sum;
    }

    /**
     * Get a {@link BankAccountData} object based off an internal int ID.
     *
     * @param Id The internal ID of the player.
     * @return Returns a populated optional if an account was found.
     */
    @NotNull Optional<BankAccountData> getAccountById(int Id);


    /**
     * Get the ID of a bank account data object.
     *
     * @param bankAccount The BankAccountData object.
     * @return Returns an populated integer if this BankAccountData
     * object was registered to this bank.
     */
    @NotNull Optional<Integer> getIdByAccount(@NotNull BankAccountData bankAccount);

    @NotNull default BankAccount getOrCreateAccountFor(@NotNull final String accountName,
        @NotNull final UUID player) {
        return getAccountFor(accountName, player).orElse(createAccount(accountName, player));
    }

    @NotNull BankAccountData getAccountsFor(@NotNull UUID player);

    @NotNull Optional<BankAccount> getAccountFor(@NotNull String name, @NotNull UUID player);

    @NotNull BankAccount createAccount(@NotNull String name, @NotNull UUID player);

    class Builder {

        private Currency primaryCurrency;
        private Map<UUID, Integer> idMap = new HashMap<>();
        private Map<Integer, BankAccountData> accountMap = new HashMap<>();
        private Map<UUID, Collection<CreditCard>> creditCards = new HashMap<>();
        private Map<Currency, Double> conversionMap = new HashMap<>();
        private String name;

        public Builder() {

        }

        public Builder(@NotNull final Builder other) {
            this.primaryCurrency = other.primaryCurrency;
            this.idMap = new HashMap<>(other.idMap);
            this.accountMap = new HashMap<>(other.accountMap);
            this.conversionMap = new HashMap<>(other.conversionMap);
            this.creditCards = new HashMap<>(other.creditCards);
            this.name = other.name;
        }

        public Builder setIdMap(@NotNull final Map<UUID, Integer> idMap) {
            this.idMap.clear();
            this.idMap.putAll(idMap);
            return this;
        }

        public Builder setAccountMap(@NotNull final Map<Integer, BankAccountData> accountMap) {
            this.accountMap.clear();
            this.accountMap.putAll(accountMap);
            return this;
        }

        public Builder setConversionMap(@NotNull final Map<Currency, Double> conversionMap) {
            this.conversionMap.clear();
            this.conversionMap.putAll(conversionMap);
            return this;
        }

        public Builder setCreditCards(
            @NotNull final Map<UUID, Collection<CreditCard>> creditCards) {
            this.creditCards = creditCards;
            return this;
        }

        public Builder setName(@NotNull final String name) {
            this.name = name;
            return this;
        }

        public Builder setPrimaryCurrency(@NotNull final Currency primaryCurrency) {
            this.primaryCurrency = primaryCurrency;
            return this;
        }

        public Builder clear() {
            this.primaryCurrency = null;
            this.name = null;
            this.conversionMap.clear();
            this.accountMap.clear();
            this.idMap.clear();
            return this;
        }

        public Bank buildAndClear() {
            final BankImpl bank = new BankImpl(name, primaryCurrency, conversionMap, creditCards);
            bank.idMap = new HashMap<>(idMap);
            bank.accountMap = new HashMap<>(accountMap);
            bank.conversionMap = new HashMap<>(conversionMap);
            return bank;
        }


        private class BankImpl implements Bank {

            Map<UUID, Integer> idMap = new HashMap<>();
            Map<Integer, BankAccountData> accountMap = new HashMap<>();
            Map<Currency, Double> conversionMap;
            Map<UUID, Collection<CreditCard>> creditCardData;
            private final Currency primaryCurrency;
            private final String name;

            BankImpl(@NotNull final String name, @NotNull final Currency primaryCurrency,
                @NotNull final Map<Currency, Double> conversionMap,
                @NotNull final Map<UUID, Collection<CreditCard>> creditCardData) {
                this.name = name;
                this.primaryCurrency = primaryCurrency;
                this.conversionMap = new HashMap<>(conversionMap);
                this.creditCardData = creditCardData;
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


            @Override @NotNull public String getName() {
                return name;
            }

            @Override @NotNull public Currency getPrimaryCurrency() {
                return primaryCurrency;
            }

            @Override
            public boolean isCurrencyConversionSupported(@NotNull final Currency currency) {
                return conversionMap.containsKey(currency);
            }

            @Override public double getConversionRateFor(@NotNull final Currency currency)
                throws IllegalArgumentException {
                Valid.checkBoolean(isCurrencyConversionSupported(currency),
                    "Currency conversion unsupported");
                return conversionMap.get(currency);
            }

            @Override
            public double convertToPrimary(final double sum, @NotNull final Currency original)
                throws IllegalArgumentException {
                return getConversionRateFor(original) * sum;
            }

            @Override public @NotNull Optional<BankAccountData> getAccountById(final int Id) {
                if (!accountMap.containsKey(Id)) {
                    return Optional.empty();
                }
                return Optional.of(accountMap.get(Id));
            }

            @Override public @NotNull Optional<Integer> getIdByAccount(
                @NotNull final BankAccountData bankAccount) {
                for (final Map.Entry<Integer, BankAccountData> entry : accountMap.entrySet()) {
                    if (bankAccount.equals(entry.getValue())) {
                        return Optional.of(entry.getKey());
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

            @Override @NotNull
            public Optional<BankAccount> getAccountFor(@NotNull final String name,
                @NotNull final UUID player) {
                if (!idMap.containsKey(player)) {
                    return Optional.empty();
                }
                final BankAccountData data = getAccountById(idMap.get(player)).orElse(null);
                if (data == null) {
                    return Optional.empty();
                }
                return data.getAccount(name, player);
            }


            @Override @NotNull
            public BankAccount createAccount(final @NotNull String name, final @NotNull UUID player) {
                return new AbstractBankAccount(name, this, player) {
                };
            }

            @Override @NotNull public Transaction createTransaction(final UUID invoker,
                final CreditHolder invokingCreditHolder, final CreditHolder targetCreditHolder) {
                return new Transaction(invoker, invokingCreditHolder, targetCreditHolder);
            }

            @Override public boolean handleTransaction(final Transaction transaction) {
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

            @Override public boolean canCallBack(final Transaction transaction) {
                if (transaction.getInvokingCreditHolder().getBackingExecutor().equals(this)) {
                    return !transaction.getInvokingCreditHolder().isFrozen();
                } else if (transaction.getTargetCreditHolder().getBackingExecutor().equals(this)) {
                    return transaction.getTargetCreditHolder().has(transaction.getSum())
                        && !transaction.getTargetCreditHolder().isFrozen();
                }
                return false;
            }

            @Override public void callBack(final Transaction transaction) {
                final double sum = transaction.getSum();
                if (sum < 0) {
                    return;
                }
                if (transaction.getInvokingCreditHolder().getBackingExecutor().equals(this)) {
                    transaction.getInvokingCreditHolder().deposit(sum);
                } else if (transaction.getTargetCreditHolder().getBackingExecutor().equals(this)) {
                    if (!transaction.getTargetCreditHolder().withdraw(sum)) {
                        throw new IllegalStateException("Unable to call back transaction.");
                    }
                }
            }

            @Override public boolean supportsCreditCards() {
                return false;
            }

            @Override @NotNull
            public CreditCard getOrCreateCardFor(final String name, final UUID owner) {
                throw new UnsupportedOperationException(
                    "Bank does not support credit-card creation.");
            }

            @Override public boolean equals(final Object o) {
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;

                final BankImpl bank = (BankImpl) o;

                if (!Objects.equals(idMap, bank.idMap))
                    return false;
                if (!Objects.equals(accountMap, bank.accountMap))
                    return false;
                if (!Objects.equals(conversionMap, bank.conversionMap))
                    return false;
                if (!Objects.equals(creditCardData, bank.creditCardData))
                    return false;
                if (!Objects.equals(primaryCurrency, bank.primaryCurrency))
                    return false;
                return Objects.equals(name, bank.name);
            }

            @Override public int hashCode() {
                int result = idMap != null ? idMap.hashCode() : 0;
                result = 31 * result + (accountMap != null ? accountMap.hashCode() : 0);
                result = 31 * result + (conversionMap != null ? conversionMap.hashCode() : 0);
                result = 31 * result + (creditCardData != null ? creditCardData.hashCode() : 0);
                result = 31 * result + primaryCurrency.hashCode();
                result = 31 * result + name.hashCode();
                return result;
            }
        }
    }

}
