package cn.tuyucheng.taketoday.optionalreturntype;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Optional;

@Entity
public class UserOptional implements Serializable {
	@Id
	private long userId;

	@Column(nullable = true)
	private String firstName;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Optional<String> getFirstName() {
		return Optional.ofNullable(firstName);
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
		Optional.ofNullable(firstName);
	}

}
