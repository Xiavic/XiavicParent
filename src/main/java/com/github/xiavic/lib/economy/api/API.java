package com.github.xiavic.lib.economy.api;

import com.github.xiavic.lib.economy.api.banking.Bank;
import com.github.xiavic.lib.economy.api.banking.BankAccountData;
import com.github.xiavic.lib.economy.api.banking.BankDatabase;

import java.util.UUID;

public class API {

    public static void loadAll() {
        loadBanking();
    }

    public static void loadBanking() {
        BankDatabase.getInstance().loadBankAccounts(true);
    }

    public static void saveBankingData() {
        BankDatabase.getInstance().saveBankAccounts(true);
    }

    /**
     * Get the umbrella bank account data object for all
     * banks this player is registered to.
     *
     * @param player The UUID of the player.
     * @return Returns a {@link BankAccountData} object which contains all
     * bank account data for all banks this player has come in contact
     * with.
     */
    public static BankAccountData getAllDataFor(UUID player) {
        BankAccountData ret = null;
        for (Bank bank : BankDatabase.getInstance().getRegisteredBanks()) {
            BankAccountData data = bank.getAccountsFor(player);
            ret = ret == null ? data : ret.merge(data);
        }
        return ret;
    }

}
