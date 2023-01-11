package cn.tuyucheng.taketoday.boot.ddd.event;

import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Aggregate3 extends AbstractAggregateRoot<Aggregate3> {
	@Id
	@GeneratedValue
	private long id;

	public void domainOperation() {
		// some domain operation
		registerEvent(new DomainEvent());
	}
}