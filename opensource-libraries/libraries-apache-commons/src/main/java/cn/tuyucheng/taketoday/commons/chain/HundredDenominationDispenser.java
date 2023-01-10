package cn.tuyucheng.taketoday.commons.chain;

import static cn.tuyucheng.taketoday.commons.chain.AtmConstants.NO_OF_HUNDREDS_DISPENSED;

public class HundredDenominationDispenser extends AbstractDenominationDispenser {
	@Override
	protected String getDenominationString() {
		return NO_OF_HUNDREDS_DISPENSED;
	}

	@Override
	protected int getDenominationValue() {
		return 100;
	}
}