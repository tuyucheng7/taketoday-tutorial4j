package cn.tuyucheng.taketoday.commons.chain;

import static cn.tuyucheng.taketoday.commons.chain.AtmConstants.NO_OF_FIFTIES_DISPENSED;

public class FiftyDenominationDispenser extends AbstractDenominationDispenser {
	@Override
	protected String getDenominationString() {
		return NO_OF_FIFTIES_DISPENSED;
	}

	@Override
	protected int getDenominationValue() {
		return 50;
	}
}