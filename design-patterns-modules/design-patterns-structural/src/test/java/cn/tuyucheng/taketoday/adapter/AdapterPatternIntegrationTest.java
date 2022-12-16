package cn.tuyucheng.taketoday.adapter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdapterPatternIntegrationTest {

	@Test
	void givenMovableAdapter_WhenConvertingMPHToKMPH_thenSuccessfullyConverted() {
		Movable bugattiVeyron = new BugattiVeyron();
		MovableAdapter bugattiVeyronAdapter = new MovableAdapterImpl(bugattiVeyron);
		assertEquals(431.30312, bugattiVeyronAdapter.getSpeed(), 0.00001);

		Movable mcLaren = new McLaren();
		MovableAdapter mcLarenAdapter = new MovableAdapterImpl(mcLaren);
		assertEquals(387.85094, mcLarenAdapter.getSpeed(), 0.00001);

		Movable astonMartin = new AstonMartin();
		MovableAdapter astonMartinAdapter = new MovableAdapterImpl(astonMartin);
		assertEquals(354.0548, astonMartinAdapter.getSpeed(), 0.00001);
	}
}