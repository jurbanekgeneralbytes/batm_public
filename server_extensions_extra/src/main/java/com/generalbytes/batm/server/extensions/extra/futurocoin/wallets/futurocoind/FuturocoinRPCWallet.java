package com.generalbytes.batm.server.extensions.extra.futurocoin.wallets.futurocoind;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FuturocoinRPCWallet implements IWallet {

    private static final Logger log = LoggerFactory.getLogger(FuturocoinRPCWallet.class);
    private static final String CRYPTO_CURRENCY = Currencies.FTO;

    public FuturocoinRPCWallet(String rpcURL, String accountName) {
        this.rpcURL = rpcURL;
        this.accountName = accountName;
    }

    private String rpcURL;
    private String accountName;

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CRYPTO_CURRENCY);
        return result;

    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CRYPTO_CURRENCY;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Futurocoind wallet error: unknown cryptocurrency.");
            return null;
        }

        log.info("Futurocoind sending coins from " + accountName + " to: " + destinationAddress + " " + amount);
        try {
            String result = getClient(rpcURL).sendFrom(accountName, destinationAddress,amount.doubleValue());
            log.debug("result = " + result);
            return result;
        } catch (BitcoinException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Futurocoind wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            List<String> addressesByAccount = getClient(rpcURL).getAddressesByAccount(accountName);
            if (addressesByAccount == null || addressesByAccount.size() == 0) {
                return null;
            }else{
                return addressesByAccount.get(0);
            }
        } catch (BitcoinException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Futurocoind wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            double balance = getClient(rpcURL).getBalance(accountName);
            return BigDecimal.valueOf(balance);
        } catch (BitcoinException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BitcoinJSONRPCClient getClient(String rpcURL) {
        try {
            return new BitcoinJSONRPCClient(rpcURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
