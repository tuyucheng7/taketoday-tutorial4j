package cn.tuyucheng.taketoday.manytomany.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "student")
public class Student {

	@Id
	@Column(name = "id")
	private Long id;

	@ManyToMany
	@JoinTable(name = "course_like", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
	private Set<Course> likedCourses = new HashSet<>();

	@OneToMany(mappedBy = "student")
	private Set<CourseRating> ratings = new HashSet<>();

	@OneToMany(mappedBy = "student")
	private Set<CourseRegistration> registrations = new HashSet<>();

	// additional properties

	public Student() {
	}

	public Student(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Course> getLikedCourses() {
		return likedCourses;
	}

	public void setLikedCourses(Set<Course> likedCourses) {
		this.likedCourses = likedCourses;
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
		Student other = (Student) obj;
		if (id == null) {
			return other.id == null;
		} else return id.equals(other.id);
	}
}