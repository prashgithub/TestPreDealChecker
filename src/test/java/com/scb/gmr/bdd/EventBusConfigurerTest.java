package com.scb.gmr.bdd;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.scb.gmr.event.PreAuthTradeAmountBreached;
import com.scb.gmr.eventhandler.PreDealCheckerEventHandler;
import com.scb.gmr.util.EventBusConfigurer;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.scb.gmr.bdd.TestUtil.COUNTERPARTY;
import static com.scb.gmr.bdd.TestUtil.DAILY_LIMIT;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by root on 22/7/16.
 */
public class EventBusConfigurerTest {
    @Test
    public void testWithExceptionHandling(){
        final AtomicBoolean isExceptionThrown = new AtomicBoolean(false);
        final SubscriberExceptionHandler exceptionHandler = (throwable, subscriberExceptionContext) -> {
            isExceptionThrown.set(true);
        };
        final PreDealCheckerEventHandler dealCheckerEventHandler = PreDealCheckerEventHandler.create(false);
        final EventBus eventBus = EventBusConfigurer.configure()
                .with(exceptionHandler)
                .with(dealCheckerEventHandler)
                .create();

        assertThat(isExceptionThrown.get(), equalTo(false));
        eventBus.post(PreAuthTradeAmountBreached.create(COUNTERPARTY, DAILY_LIMIT));
        assertThat(isExceptionThrown.get(), equalTo(true));
    }

}