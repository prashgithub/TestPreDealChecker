package com.scb.gmr;

import java.util.concurrent.atomic.AtomicInteger;

public final class CounterPartyTradeBean {
    private final CounterPartyLimits counterPartyLimits;
    private final AtomicInteger utilizedDailyLimit;

    public CounterPartyTradeBean(CounterPartyLimits counterPartyLimits) {
        this.counterPartyLimits = counterPartyLimits;
        this.utilizedDailyLimit = new AtomicInteger(0);
    }

    public boolean addTradeAmountIfUnderDailyTradeLimit(int tradeAmount) {
        boolean isSuccessful;
        do {
            final int utilizedDailyLimitValue = getCurrentUtilizedDailyLimit();
            final int newTotalTradeAmount = tradeAmount + utilizedDailyLimitValue;
            if (!isTradeAmountUnderDailyLimit(newTotalTradeAmount)) {
                return false;
            }
            isSuccessful = utilizedDailyLimit.compareAndSet(utilizedDailyLimitValue, newTotalTradeAmount);
        } while (!isSuccessful);
        return true;
    }

    public CounterPartyLimits getCounterPartyLimits() {
        return counterPartyLimits;
    }

    public int getCurrentUtilizedDailyLimit() {
        return utilizedDailyLimit.get();
    }

    public boolean isDailyLimitReached() {
        return getCurrentUtilizedDailyLimit() >= counterPartyLimits.getDailyLimit();
    }

    public boolean isTradeAmountUnderDailyLimitAvailable(int tradeAmount) {
        return tradeAmount + getCurrentUtilizedDailyLimit() <= counterPartyLimits.getDailyLimit();
    }

    public boolean isTradeAmountUnderDailyLimit(int tradeAmount) {
        return tradeAmount <= counterPartyLimits.getDailyLimit();
    }

    public boolean isTradeAmountUnderPreAuthTradeLimit(int tradeAmount) {
        return tradeAmount <= counterPartyLimits.getPreAuthTradeLimit();
    }

    public void resetUtilizedDailyLimit() {
        utilizedDailyLimit.set(0);
    }
}