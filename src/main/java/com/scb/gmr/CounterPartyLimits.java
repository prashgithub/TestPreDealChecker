package com.scb.gmr;

import org.assertj.core.util.Strings;

public final class CounterPartyLimits {
    private final String counterParty;
    private final int preAuthTradeLimit;
    private final int dailyLimit;

    private CounterPartyLimits(String counterParty, int preAuthTradeLimit, int dailyLimit) {
        //validate args
        if (Strings.isNullOrEmpty(counterParty) || preAuthTradeLimit < 0 || dailyLimit < 0) {
            throw new IllegalArgumentException("Incomplete / incorrect values to initialize.");
        }
        //assign
        this.counterParty = counterParty;
        this.preAuthTradeLimit = preAuthTradeLimit;
        this.dailyLimit = dailyLimit;
    }

    public String getCounterParty() {
        return counterParty;
    }

    public int getPreAuthTradeLimit() {
        return preAuthTradeLimit;
    }

    public int getDailyLimit() {
        return dailyLimit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String counterParty;
        private int preAuthTradeLimit;
        private int dailyLimit;

        public Builder setCounterParty(String counterParty) {
            this.counterParty = counterParty;
            return this;
        }

        public Builder setPreAuthTradeLimit(int preAuthTradeLimit) {
            this.preAuthTradeLimit = preAuthTradeLimit;
            return this;
        }

        public Builder setDailyLimit(int dailyLimit) {
            this.dailyLimit = dailyLimit;
            return this;
        }

        public String getCounterParty() {
            return counterParty;
        }

        public int getPreAuthTradeLimit() {
            return preAuthTradeLimit;
        }

        public int getDailyLimit() {
            return dailyLimit;
        }

        public CounterPartyLimits build() {
            return new CounterPartyLimits(counterParty, preAuthTradeLimit, dailyLimit);
        }
    }

    @Override
    public String toString() {
        return "CounterPartyLimits{" + "counterParty='" + counterParty + ", preAuthTradeLimit=" + preAuthTradeLimit + ", dailyLimit=" + dailyLimit + '}';
    }
}