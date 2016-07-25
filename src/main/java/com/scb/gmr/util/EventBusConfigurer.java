package com.scb.gmr.util;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * Created by root on 22/7/16.
 */
public class EventBusConfigurer {
    private static final SubscriberExceptionHandler DEF_EXCEPTION_HANDLER = (throwable, context) -> {
        throw new RuntimeException(throwable.getMessage(), throwable);
    };

    private SubscriberExceptionHandler exceptionHandler;
    private Object[] handlers;

    private EventBusConfigurer(){
    }

    public EventBus create(){
        EventBus eventBus = new EventBus(getExceptionHandlerOrDefault());
        for (Object handler : getHandlersOrBlank()){
            eventBus.register(handler);
        }
        return eventBus;
    }
    public EventBusConfigurer with(SubscriberExceptionHandler exceptionHandler){
        this.exceptionHandler = exceptionHandler;
        return this;
    }
    public EventBusConfigurer with(Object ... handlers){
        this.handlers = handlers;
        return this;
    }

    public SubscriberExceptionHandler getExceptionHandlerOrDefault() {
        return exceptionHandler == null? DEF_EXCEPTION_HANDLER : exceptionHandler;
    }

    public Object[] getHandlersOrBlank() {
        return handlers == null? new Object[0] : handlers;
    }

    public static EventBusConfigurer configure() {
        return new EventBusConfigurer();
    }
}
