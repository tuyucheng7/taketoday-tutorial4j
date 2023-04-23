package cn.tuyucheng.taketoday.manytomany.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {

	@Id
	@Column(name = "id")
	private Long id;

	@ManyToMany(mappedBy = "likedCourses")
	private Set<Student> likes = new HashSet<>();

	@OneToMany(mappedBy = "course")
	private Set<CourseRating> ratings = new HashSet<>();

	@OneToMany(mappedBy = "course")
	private Set<CourseRegistration> registrations = new HashSet<>();

	// additional properties

	public Course() {
	}

	public Course(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Student> getLikes() {
		return likes;
	}

	public void setLikes(Set<Student> likes) {
		this.likes = likes;
	}

	public Set<CourseRating> getRatings() {
		return ratings;
	}

	public void setRatings(Set<CourseRating> ratings) {
		this.ratings = ratings;
	}

	public Set<CourseRegistration> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(Set<CourseRegistration> registrations) {
		this.registrations = registrations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}
}