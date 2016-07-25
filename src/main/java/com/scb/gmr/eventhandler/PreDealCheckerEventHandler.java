package com.scb.gmr.eventhandler;

import CreditCheckAPI.CreditCheckException;
import CreditCheckAPI.CreditLimitBreach;
import com.google.common.eventbus.Subscribe;
import com.scb.gmr.event.CounterPartyNotConfigured;
import com.scb.gmr.event.DailyTradeLimitReached;
import com.scb.gmr.event.PreAuthTradeAmountBreached;
import com.scb.gmr.event.TradeRejectedForDailyLimit;

/**
 * Created by root on 22/7/16.
 */
public class PreDealCheckerEventHandler {
    private static final CreditLimitBreach CREDIT_LIMIT_BREACH = (counterParty, tradeAmount) -> {
        throw new CreditCheckException("CreditLimitBreach for counterParty:" + counterParty
                + ", amount:" + tradeAmount);
    };
    public static PreDealCheckerEventHandler create(boolean logEnable){
        return new PreDealCheckerEventHandler(logEnable);
    }
    private final boolean logEnabled;
    private PreDealCheckerEventHandler(boolean logEnabled){
        this.logEnabled = logEnabled;
    }

    private void logMsg(String msg) {
        if(logEnabled) System.out.println(msg);
    }
    @Subscribe
    public void counterPartyNotConfigured(CounterPartyNotConfigured event){
        logMsg("CounterParty is not configured:" + event.getCounterParty());
    }
    @Subscribe
    public void dailyTradeLimitReached(DailyTradeLimitReached event){
        logMsg("DailyTradeLimitReached for counterParty:" + event.getCounterParty());
    }
    @Subscribe
    public void preAuthTradeAmountBreached(PreAuthTradeAmountBreached event) throws CreditCheckException {
        try {
            CREDIT_LIMIT_BREACH.validate(event.getCounterParty(), event.getTradeAmount());
        } catch (CreditCheckException e) {
            logMsg(e.getMessage());
            throw e;
        }
    }
    @Subscribe
    public void tradeRejectedForDailyLimit(TradeRejectedForDailyLimit event){
        logMsg("TradeRejectedForDailyLimit for counterParty:" + event.getCounterParty()
                + ", amount:" + event.getTradeAmount() );
    }
}
