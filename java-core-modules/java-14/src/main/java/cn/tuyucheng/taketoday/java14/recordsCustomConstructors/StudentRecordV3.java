package cn.tuyucheng.taketoday.java14.recordsCustomConstructors;

import java.util.UUID;

record StudentRecordV3(String name, int rollNo, int marks, String id) {

	public StudentRecordV3(String name, int rollNo, int marks) {
		this(name, rollNo, marks, UUID.randomUUID().toString());
	}
}