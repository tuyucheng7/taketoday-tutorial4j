package cn.tuyucheng.taketoday.jackson.date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

public class EventWithSerializer {
	public String name;

	@JsonDeserialize(using = CustomDateDeserializer.class)
	@JsonSerialize(using = CustomDateSerializer.class)
	public Date eventDate;

	public EventWithSerializer() {
		super();
	}

	public EventWithSerializer(final String name, final Date eventDate) {
		this.name = name;
		this.eventDate = eventDate;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public String getName() {
		return name;
	}
}
