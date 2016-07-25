Pre Deal Checker Exercise Task Description
========================================

Implement a Pre Deal Checker Agent
------------------------------

The business require an PreDealChecker agent that will autonomously validate trades against predefined trade limits and daily trading limits
for a specified counter party.

 *  The PreDealChecker agent receives trades by implementing the PreDealListener interface.
 *  The PreDealChecker needs to be able to validate a given trade for a given counter party against given trading limits and daily limits.
 *  If one trade is above the trading limit, notify CreditLimitBreach(Its implementation is CreditCheck).
 *  CreditLimitBreach may throw CreditCheckException when error happen.
 *  Once the Daily Limit is reached, no more trades can be placed.
 *  Partial trades cannot be executed however in order to satisfy the business
    we must utilise as much of the daily limit as possible. For example, if the trade amount is already 900 and daily limit is 1000, a trade with 101 will be rejected,
	but a trade with 50 will be accepted.
 *  PreDealChecker should be thread safe.
 *  The agent should be able to notify information like reach the daily limit or error to other components through listener
 *  TDD is required.
 *  There is a BDD feature file PreDealChecker.feature, complete the glue code and add more cases as you think is necessary.
 *  Make the code as clean as possible.
 *  You can find PreDealListener,CreditLimitBreach and CreditCheck in CreditCheckAPI-1.0.0.jar

