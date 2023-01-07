package cn.tuyucheng.taketoday.date;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateUnitTest {

	@Test
	public void whenUsingFullyQualifiedClassNames() {

		java.util.Date javaDate = new java.util.Date();
		cn.tuyucheng.taketoday.date.Date baeldungDate = new cn.tuyucheng.taketoday.date.Date(javaDate.getTime());

		Assert.assertEquals(javaDate.getTime(), baeldungDate.getTime());
	}

	@Test
	public void whenImportTheMostUsedOne() {

		Date javaDate = new Date();
		cn.tuyucheng.taketoday.date.Date baeldungDate = new cn.tuyucheng.taketoday.date.Date(javaDate.getTime());

		Assert.assertEquals(javaDate.getTime(), baeldungDate.getTime());
	}
}
