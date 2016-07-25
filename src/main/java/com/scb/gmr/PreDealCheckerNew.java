package com.scb.gmr;

import CreditCheckAPI.PreDealListener;
import com.scb.gmr.event.CounterPartyNotConfigured;
import com.scb.gmr.event.DailyTradeLimitReached;
import com.scb.gmr.event.PreAuthTradeAmountBreached;
import com.scb.gmr.event.TradeRejectedForDailyLimit;

import java.util.concurrent.ConcurrentHashMap;

import static com.scb.gmr.event.EventBuses.*;

public class PreDealCheckerNew implements PreDealListener {
    private final ConcurrentHashMap<String, CounterPartyTradeBean> counterPartyTradeBeanMap
            = new ConcurrentHashMap<>(); //initial capacity not predictable here, imp to avoid rehashing

    private PreDealCheckerNew(CounterPartyLimits... limits) {
        for (CounterPartyLimits cp : limits) {
            addBean(cp);
        }
    }
    public static PreDealCheckerNew create(CounterPartyLimits... limits) {
        return new PreDealCheckerNew(limits);
    }
    public CounterPartyTradeBean getBeanFor(String counterParty) {
        return counterPartyTradeBeanMap.get(counterParty);
    }
    public CounterPartyTradeBean getOrCreateBeanFor(CounterPartyLimits limits) {
        CounterPartyTradeBean bean = getBeanFor(limits.getCounterParty());
        if(bean == null){
            bean = addBean(limits);
        }
        return bean;
    }
    private CounterPartyTradeBean addBean(CounterPartyLimits cp) {
        CounterPartyTradeBean newBean = new CounterPartyTradeBean(cp);
        CounterPartyTradeBean oldBeanOrNull = counterPartyTradeBeanMap.putIfAbsent(cp.getCounterParty(), newBean);
        return oldBeanOrNull == null? newBean : oldBeanOrNull;
    }

    @Override
    public void handle(String counterParty, int tradeAmount) {
        executeTradeAmountForCounterParty(counterParty, tradeAmount);
    }

    private void executeTradeAmountForCounterParty(String counterParty, int tradeAmount) {
        final CounterPartyTradeBean bean = getBeanFor(counterParty);

        if (bean == null) {
            DEAL_CHECKER_BUS.publish(CounterPartyNotConfigured.create(counterParty));
            return;
        }
        if (bean.isDailyLimitReached()) {
            DEAL_CHECKER_BUS.publish(DailyTradeLimitReached.create(counterParty));
            return;
        }
        if (!bean.isTradeAmountUnderPreAuthTradeLimit(tradeAmount)) {
            DEAL_CHECKER_BUS.publish(PreAuthTradeAmountBreached.create(counterParty, tradeAmount));
        }
        if (!bean.addTradeAmountIfUnderDailyTradeLimit(tradeAmount)) {
            DEAL_CHECKER_BUS.publish(TradeRejectedForDailyLimit.create(counterParty, tradeAmount));
        }
    }
}