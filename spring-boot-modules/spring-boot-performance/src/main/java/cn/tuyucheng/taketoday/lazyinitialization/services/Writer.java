package cn.tuyucheng.taketoday.lazyinitialization.services;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public class Writer {

	private final String writerId;

	public Writer(String writerId) {
		this.writerId = writerId;
		System.out.println(writerId + " initialized!!!");
	}

	public void write(String message) {
		System.out.println(writerId + ": " + message);
	}
}