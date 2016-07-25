package com.scb.gmr.event;

/**
 * Created by root on 22/7/16.
 */
public class DailyTradeLimitReached implements CounterPartyEvent {
    private final String counterParty;

    private DailyTradeLimitReached(String counterParty) {
        this.counterParty = counterParty;
    }

    public String getCounterParty() {
        return counterParty;
    }

    public static DailyTradeLimitReached create(String counterParty) {
        return new DailyTradeLimitReached(counterParty);
    }
}
