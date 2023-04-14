module cn.tuyucheng.taketoday.library.test {
	requires cn.tuyucheng.taketoday.library.core;
	requires org.junit.jupiter.api;
	opens cn.tuyucheng.taketoday.library.test to org.junit.platform.commons;
}