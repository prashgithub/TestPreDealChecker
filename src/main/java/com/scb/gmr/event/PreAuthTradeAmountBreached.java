package com.scb.gmr.event;

/**
 * Created by root on 22/7/16.
 */
public class PreAuthTradeAmountBreached implements PreDealCheckerEvent {
    private final String counterParty;
    private final int tradeAmount;

    private PreAuthTradeAmountBreached(String counterParty, int tradeAmount) {
        this.counterParty = counterParty;
        this.tradeAmount = tradeAmount;
    }

    public String getCounterParty() {
        return counterParty;
    }

    public int getTradeAmount() {
        return tradeAmount;
    }

    public static PreAuthTradeAmountBreached create(String counterParty, int tradeAmount) {
        return new PreAuthTradeAmountBreached(counterParty, tradeAmount);
    }
}
