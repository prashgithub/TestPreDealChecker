package com.scb.gmr.event;

/**
 * Created by root on 22/7/16.
 */
public class CounterPartyNotConfigured implements PreDealCheckerEvent {
    private final String counterParty;

    private CounterPartyNotConfigured(String counterParty) {
        this.counterParty = counterParty;
    }

    public String getCounterParty() {
        return counterParty;
    }

    public static CounterPartyNotConfigured create(String counterParty) {
        return new CounterPartyNotConfigured(counterParty);
    }
}
