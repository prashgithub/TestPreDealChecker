package com.scb.gmr.event;

import com.google.common.eventbus.EventBus;


/**
 * Created by root on 22/7/16.
 */
public enum EventBuses {
    DEAL_CHECKER_BUS(new EventBus((throwable, context) -> {
        throw new RuntimeException(throwable.getMessage(), throwable);
    }));

    private final EventBus eventBus;

    EventBuses(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void register(Object methods){
        eventBus.register(methods);
    }
    public void unregister(Object methods){
        eventBus.unregister(methods);
    }
    public void publish(Object event){
        eventBus.post(event);
    }
    static {
        DEAL_CHECKER_BUS.register(new DefaultHandlers());
    }
}
