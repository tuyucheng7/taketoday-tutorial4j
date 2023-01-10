package cn.tuyucheng.taketoday.commons.chain;

public class TenDenominationDispenser extends AbstractDenominationDispenser {
	@Override
	protected String getDenominationString() {
		return AtmConstants.NO_OF_TENS_DISPENSED;
	}

	@Override
	protected int getDenominationValue() {
		return 10;
	}
}