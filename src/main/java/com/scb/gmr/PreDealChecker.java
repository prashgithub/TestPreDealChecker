package com.scb.gmr;

import CreditCheckAPI.PreDealListener;
import com.google.common.eventbus.EventBus;
import com.scb.gmr.event.*;

import java.util.concurrent.ConcurrentHashMap;

public class PreDealChecker implements PreDealListener {
    private final ConcurrentHashMap<String, CounterPartyTradeBean> counterPartyTradeBeanMap
            = new ConcurrentHashMap<>(); //initial capacity not predictable here, imp to avoid rehashing

    private final EventBus dealCheckerEventBus;

    private PreDealChecker(EventBus eventBus, CounterPartyLimits... limits) {
        dealCheckerEventBus = eventBus;
        for (CounterPartyLimits cp : limits) {
            addBean(cp);
        }
    }

    public static PreDealChecker create(EventBus eventBus, CounterPartyLimits... limits) {
        return new PreDealChecker(eventBus, limits);
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
            dealCheckerEventBus.post(CounterPartyNotConfigured.create(counterParty));
            return;
        }
        if (bean.isDailyLimitReached()) {
            dealCheckerEventBus.post(DailyTradeLimitReached.create(counterParty));
            return;
        }
        if (!bean.isTradeAmountUnderPreAuthTradeLimit(tradeAmount)) {
            dealCheckerEventBus.post(PreAuthTradeAmountBreached.create(counterParty, tradeAmount));
        }
        if (!bean.addTradeAmountIfUnderDailyTradeLimit(tradeAmount)) {
            dealCheckerEventBus.post(TradeRejectedForDailyLimit.create(counterParty, tradeAmount));
        }
    }
}