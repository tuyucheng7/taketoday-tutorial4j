package cn.tuyucheng.taketoday.dynamicvalidation.model;

import cn.tuyucheng.taketoday.dynamicvalidation.ContactInfo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ContactInfo
	@NotNull
	private String contactInfo;

	public Customer() {
	}

	public Customer(final long id, final String contactInfo) {
		this.id = id;
		this.contactInfo = contactInfo;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(final String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

}
