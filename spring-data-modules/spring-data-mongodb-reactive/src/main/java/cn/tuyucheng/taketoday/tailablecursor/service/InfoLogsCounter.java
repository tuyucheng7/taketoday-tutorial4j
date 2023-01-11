package cn.tuyucheng.taketoday.tailablecursor.service;

import cn.tuyucheng.taketoday.tailablecursor.domain.Log;
import cn.tuyucheng.taketoday.tailablecursor.domain.LogLevel;
import cn.tuyucheng.taketoday.tailablecursor.repository.LogsRepository;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class InfoLogsCounter implements LogsCounter {

	private final AtomicInteger counter = new AtomicInteger();
	private final Disposable subscription;

	public InfoLogsCounter(LogsRepository repository) {
		Flux<Log> stream = repository.findByLevel(LogLevel.INFO);
		this.subscription = stream.subscribe(logEntity -> {
			LOGGER.info("INFO log received: " + logEntity);
			counter.incrementAndGet();
		});
	}

	@Override
	public int count() {
		return this.counter.get();
	}

	@PreDestroy
	public void close() {
		this.subscription.dispose();
	}
}
