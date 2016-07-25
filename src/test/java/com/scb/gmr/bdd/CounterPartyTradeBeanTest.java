package com.scb.gmr.bdd;

import com.scb.gmr.CounterPartyLimits;
import com.scb.gmr.CounterPartyTradeBean;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static com.scb.gmr.bdd.TestUtil.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class CounterPartyTradeBeanTest {
    @Test
    public void testCounterPartyLimitsBasic() throws Exception {
        CounterPartyLimits counterPartyLimits = createCounterPartyLimits();
        assertThat(counterPartyLimits.getCounterParty(), is(equalTo(COUNTERPARTY)));
        assertThat(counterPartyLimits.getPreAuthTradeLimit(), is(equalTo(AUTH_LIMIT)));
        assertThat(counterPartyLimits.getDailyLimit(), is(equalTo(DAILY_LIMIT)));
        assertThat(counterPartyLimits.toString(), containsString(COUNTERPARTY));
    }

    @Test
    public void testCounterPartyTradeBeanBasic() throws Exception {
        CounterPartyTradeBean bean = createCounterPartyTradeBean();

        assertThat(bean.getCounterPartyLimits().getDailyLimit(), is(equalTo(DAILY_LIMIT)));
        assertThat(bean.getCounterPartyLimits().getPreAuthTradeLimit(), is(equalTo(AUTH_LIMIT)));

        //empty bean boundary conditions tests
        assertThat(bean.isTradeAmountUnderPreAuthTradeLimit(0), is(true));
        assertThat(bean.isTradeAmountUnderPreAuthTradeLimit(AUTH_LIMIT), is(true));
        assertThat(bean.isTradeAmountUnderPreAuthTradeLimit(AUTH_LIMIT + 1), is(false));

        assertThat(bean.isTradeAmountUnderDailyLimit(DAILY_LIMIT - 1), is(true));
        assertThat(bean.isTradeAmountUnderDailyLimit(DAILY_LIMIT), is(true));
        assertThat(bean.isTradeAmountUnderDailyLimit(DAILY_LIMIT + 1), is(false));

        assertThat(bean.isDailyLimitReached(), is(false));
        assertThat(bean.isTradeAmountUnderDailyLimitAvailable(1), is(true));

        //add till limit
        assertThat(bean.addTradeAmountIfUnderDailyTradeLimit(DAILY_LIMIT - 1), is(true));
        assertThat(bean.addTradeAmountIfUnderDailyTradeLimit(1), is(true));

        //limit reached
        assertThat(bean.isDailyLimitReached(), is(true));
        assertThat(bean.isTradeAmountUnderDailyLimitAvailable(1), is(false));

        //add not having any effect
        assertThat(bean.addTradeAmountIfUnderDailyTradeLimit(1), is(false));
        assertThat(bean.isDailyLimitReached(), is(true));

        //reset
        bean.resetUtilizedDailyLimit();
        assertThat(bean.isTradeAmountUnderDailyLimitAvailable(DAILY_LIMIT), is(true));
        assertThat(bean.isTradeAmountUnderDailyLimit(DAILY_LIMIT), is(true));
        assertThat(bean.isDailyLimitReached(), is(false));
    }

    @Test
    public void testCounterPartyTradeBean_MT() throws Exception {
        //init
        final ExecutorService service = createExecutorService();
        final CounterPartyTradeBean bean = createCounterPartyTradeBean();
        final Callable<Boolean> unitTask = () -> bean.addTradeAmountIfUnderDailyTradeLimit(1);
        final List<Callable<Boolean>> tasks = createTaskCopies(DAILY_LIMIT, unitTask);
        final List<Future<Boolean>> results = new ArrayList<>(DAILY_LIMIT);
        final Consumer<Callable<Boolean>> taskConsumer = t -> results.add(service.submit(t));
        final Consumer<? super Future<Boolean>> resultConsumer = future -> {
            try {
                assertThat(future.get(), is(true));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        };

        //invoke and assert
        try {
            tasks.parallelStream().forEach(taskConsumer);
            results.parallelStream().forEach(resultConsumer);
            assertThat(bean.isDailyLimitReached(), is(true));
            assertThat(bean.isTradeAmountUnderDailyLimit(DAILY_LIMIT + 1), is(false));
            assertThat(bean.isTradeAmountUnderDailyLimitAvailable(1), is(false));
            assertThat(bean.addTradeAmountIfUnderDailyTradeLimit(1), is(false));
        } finally {
            service.shutdown();
        }
    }
}