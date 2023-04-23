package cn.tuyucheng.taketoday.persistence.multiple.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(schema = "spring_jpa_user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	private int age;

	@OneToMany
	List<Possession> possessionList;

	public User() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(final int age) {
		this.age = age;
	}

	public List<Possession> getPossessionList() {
		return possessionList;
	}

	public void setPossessionList(List<Possession> possessionList) {
		this.possessionList = possessionList;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", id=" + id + "]";
	}
}