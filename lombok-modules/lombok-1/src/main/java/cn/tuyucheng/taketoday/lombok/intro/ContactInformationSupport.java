package cn.tuyucheng.taketoday.lombok.intro;

import lombok.Data;

@Data
public class ContactInformationSupport implements HasContactInformation {

	private String firstName;
	private String lastName;
	private String phoneNr;

	@Override
	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

}
