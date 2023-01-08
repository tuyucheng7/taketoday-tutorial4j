package cn.tuyucheng.taketoday.lombok.intro;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserEvent implements Serializable {

	// This class is just for sample purposes.

	private @Id
	@Setter(AccessLevel.PROTECTED) Long id;

	@ManyToOne
	private User user;

	public UserEvent(User user) {
		this.user = user;
	}

}
