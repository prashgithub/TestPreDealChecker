package com.scb.gmr.bdd;

import com.scb.gmr.CounterPartyLimits;
import com.scb.gmr.CounterPartyTradeBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by root on 21/7/16.
 */
public class TestUtil {
    public static final int DAILY_LIMIT = 1000;
    public static final int AUTH_LIMIT = 100;
    public static final String COUNTERPARTY = "COUNTERPARTY";

    public static ExecutorService createExecutorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    public static CounterPartyTradeBean createCounterPartyTradeBean() {
        return new CounterPartyTradeBean(createCounterPartyLimits());
    }
    public static CounterPartyLimits createCounterPartyLimits() {
        return CounterPartyLimits.builder()
                .setCounterParty(COUNTERPARTY)
                .setPreAuthTradeLimit(AUTH_LIMIT)
                .setDailyLimit(DAILY_LIMIT)
                .build();
    }
    public static <T> List<Callable<T>> createTaskCopies(int count, Callable<T> task) {
        List<Callable<T>> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            tasks.add(task);
        }
        return tasks;
    }
}
