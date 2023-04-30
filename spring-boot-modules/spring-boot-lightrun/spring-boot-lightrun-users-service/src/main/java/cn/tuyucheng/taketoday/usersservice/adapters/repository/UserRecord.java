package cn.tuyucheng.taketoday.usersservice.adapters.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserRecord {
	@Id
	@Column(name = "user_id")
	private String id;
	private String name;

	public UserRecord(final String id, final String name) {
		this.id = id;
		this.name = name;
	}

	private UserRecord() {
		// Needed for JPA
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}