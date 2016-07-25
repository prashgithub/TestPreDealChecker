package com.scb.gmr.event;

import CreditCheckAPI.CreditCheckException;
import CreditCheckAPI.CreditLimitBreach;
import com.google.common.eventbus.Subscribe;

/**
 * Created by root on 22/7/16.
 */
public class DefaultHandlers {
    private static final CreditLimitBreach CREDIT_LIMIT_BREACH = (counterParty, tradeAmount) -> {
        throw new CreditCheckException("CreditLimitBreach for counterParty:" + counterParty
                + ", amount:" + tradeAmount);
    };

    @Subscribe
    public void counterPartyNotConfigured(CounterPartyNotConfigured event){
//        System.out.println("CounterParty is not configured:" + event.getCounterParty());
    }
    @Subscribe
    public void dailyTradeLimitReached(DailyTradeLimitReached event){
//        System.out.println("DailyTradeLimitReached for counterParty:" + event.getCounterParty());
    }
    @Subscribe
    public void preAuthTradeAmountBreached(PreAuthTradeAmountBreached event) throws CreditCheckException {
        try {
            CREDIT_LIMIT_BREACH.validate(event.getCounterParty(), event.getTradeAmount());
        } catch (CreditCheckException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    @Subscribe
    public void tradeRejectedForDailyLimit(TradeRejectedForDailyLimit event){
//        System.out.println("TradeRejectedForDailyLimit for counterParty:" + event.getCounterParty()
//                + ", amount:" + event.getTradeAmount() );
    }
}
