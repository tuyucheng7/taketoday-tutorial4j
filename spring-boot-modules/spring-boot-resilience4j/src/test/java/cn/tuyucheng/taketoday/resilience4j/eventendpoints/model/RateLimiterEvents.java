package cn.tuyucheng.taketoday.resilience4j.eventendpoints.model;

import java.util.List;

public class RateLimiterEvents {

	private List<RateLimiterEvent> rateLimiterEvents;

	public List<RateLimiterEvent> getRateLimiterEvents() {
		return rateLimiterEvents;
	}

	public void setRateLimiterEvents(List<RateLimiterEvent> rateLimiterEvents) {
		this.rateLimiterEvents = rateLimiterEvents;
	}
}