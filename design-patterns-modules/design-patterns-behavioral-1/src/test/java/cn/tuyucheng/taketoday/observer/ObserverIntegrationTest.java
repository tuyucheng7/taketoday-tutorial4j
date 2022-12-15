package cn.tuyucheng.taketoday.observer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ObserverIntegrationTest {

	@Test
	void whenChangingNewsAgencyState_thenNewsChannelNotified() {
		NewsAgency observable = new NewsAgency();
		NewsChannel observer = new NewsChannel();

		observable.addObserver(observer);

		observable.setNews("news");
		assertEquals("news", observer.getNews());
	}

	@Test
	void whenChangingONewsAgencyState_thenONewsChannelNotified() {
		ONewsAgency observable = new ONewsAgency();
		ONewsChannel observer = new ONewsChannel();

		observable.addObserver(observer);

		observable.setNews("news");
		assertEquals("news", observer.getNews());
	}

	@Test
	void whenChangingPCLNewsAgencyState_thenONewsChannelNotified() {
		PCLNewsAgency observable = new PCLNewsAgency();
		PCLNewsChannel observer = new PCLNewsChannel();

		observable.addPropertyChangeListener(observer);

		observable.setNews("news");
		assertEquals("news", observer.getNews());
	}
}