package cn.tuyucheng.taketoday.gradle.multiplerepositories;

import cn.tuyucheng.taketoday.gradle.publishPackage.User;

public class Student extends User {

	private String studentCode;

	private String lastInstitution;

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getLastInstitution() {
		return lastInstitution;
	}

	public void setLastInstitution(String lastInstitution) {
		this.lastInstitution = lastInstitution;
	}
}