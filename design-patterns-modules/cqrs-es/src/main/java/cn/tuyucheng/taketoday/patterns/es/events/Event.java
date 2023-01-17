package cn.tuyucheng.taketoday.patterns.es.events;

import java.util.Date;
import java.util.UUID;

import lombok.ToString;

@ToString
public abstract class Event {

    public final UUID id = UUID.randomUUID();

    public final Date created = new Date();

}
