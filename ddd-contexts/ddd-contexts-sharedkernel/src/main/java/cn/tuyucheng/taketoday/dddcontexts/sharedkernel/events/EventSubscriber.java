package cn.tuyucheng.taketoday.dddcontexts.sharedkernel.events;

public interface EventSubscriber {
	<E extends ApplicationEvent> void onEvent(E event);
}