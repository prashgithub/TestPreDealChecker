package com.scb.gmr.bdd;

import com.google.common.eventbus.EventBus;
import com.scb.gmr.CounterPartyLimits;
import com.scb.gmr.CounterPartyTradeBean;
import com.scb.gmr.PreDealChecker;
import com.scb.gmr.eventhandler.PreDealCheckerEventHandler;
import com.scb.gmr.util.EventBusConfigurer;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static org.junit.Assert.assertTrue;

public class PreDealCheckerStepDefinitions {
    private static final EventBus EVENT_BUS = EventBusConfigurer
            .configure()
            .with(PreDealCheckerEventHandler.create(false))
            .create();

    //static to preserve state between scenario examples
    private final static PreDealChecker DEAL_CHECKER_1 = PreDealChecker.create(EVENT_BUS);
    private final static PreDealChecker DEAL_CHECKER_2 = PreDealChecker.create(EVENT_BUS);

    //instance variables configure reset with every scenario examples
    private PreDealChecker dealChecker;
    private CounterPartyLimits.Builder cpLimitsBuilder = CounterPartyLimits.builder();
    private long startTimestamp;

    @Given("^a counterParty (.*)$")
    public void given_A_Counterparty(String counterParty) throws Throwable {
        cpLimitsBuilder.setCounterParty(counterParty);
    }

    @And("^pre authorised trading limit of (.*)$")
    public void and_A_PreAuthorised_Trading_Limit_Of(int preAuthorised) throws Throwable {
        cpLimitsBuilder.setPreAuthTradeLimit(preAuthorised);
    }

    @And("^a daily trading limit of (.*)$")
    public void and_A_Daily_Trading_Limit_Of(int dailyLimit) throws Throwable {
        cpLimitsBuilder.setDailyLimit(dailyLimit);
    }

    @When("^I place the order for (.*) with a (.*)$")
    public void when_I_Place_the_Following_Order(String counterParty, int notional) throws Throwable {
        switchDealChecker(DEAL_CHECKER_1);
        dealChecker.handle(counterParty, notional);
    }

    @When("^I execute (.*) for (.*) with a (.*)$")
    public void when_I_Execute_the_Following_Order(int numberOfExecutions, final String counterParty, final int notional) throws Throwable {
        switchDealChecker(DEAL_CHECKER_2);

        if(startTimestamp == 0){
            startTimestamp = System.currentTimeMillis();
        }
        for(int i = 0; i < numberOfExecutions; i++){
            dealChecker.handle(counterParty, notional);
        }
    }

    private void switchDealChecker(PreDealChecker scenario) {
        if(dealChecker != scenario){
            dealChecker = scenario;
        }
        dealChecker.getOrCreateBeanFor(cpLimitsBuilder.build());
    }


    @Then("^the trade should be successfully (.*)$")
    public void then_I_have_shared_at_hand(boolean expectedValue) throws Throwable {
    }

    @And("^the utilised daily limit should be (.*)$")
    public void and_The_Utilised_Daily_Limit_Should_Be(int utilisedDailyLimit) throws Throwable {
        CounterPartyTradeBean bean = dealChecker.getBeanFor(cpLimitsBuilder.getCounterParty());
        assertTrue(bean.getCurrentUtilizedDailyLimit() == utilisedDailyLimit);
    }

    @Then("^I should finish within (.*)$")
    public void then_I_Should_Finish_Within(int secondsToExecute) throws Throwable {
        long difference = (System.currentTimeMillis() - startTimestamp) / 1000; //ms to seconds
        assertTrue("Actual difference:"+difference + " whereas expected secondsToExecute:" + secondsToExecute, difference <= secondsToExecute);
    }
}
