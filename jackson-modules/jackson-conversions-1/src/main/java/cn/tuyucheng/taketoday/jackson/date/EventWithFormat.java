package cn.tuyucheng.taketoday.jackson.date;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class EventWithFormat {
	public String name;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	public Date eventDate;

	public EventWithFormat() {
		super();
	}

	public EventWithFormat(final String name, final Date eventDate) {
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
