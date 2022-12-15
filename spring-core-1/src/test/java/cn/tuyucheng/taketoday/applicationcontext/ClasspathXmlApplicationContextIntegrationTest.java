package cn.tuyucheng.taketoday.applicationcontext;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

class ClasspathXmlApplicationContextIntegrationTest {

	@Test
	void testBasicUsage() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
		Student student = (Student) context.getBean("student");
		assertThat(student.getNo(), equalTo(15));
		assertThat(student.getName(), equalTo("Tom"));

		Student sameStudent = context.getBean("student", Student.class);// do not need cast class
		assertThat(sameStudent.getNo(), equalTo(15));
		assertThat(sameStudent.getName(), equalTo("Tom"));
	}

	@Test
	void testRegisterShutdownHook() {
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
		context.registerShutdownHook();
	}

	@Test
	void testInternationalization() {
		MessageSource resources = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-internationalization.xml");

		String enHello = resources.getMessage("hello", null, "Default", Locale.ENGLISH);
		String enYou = resources.getMessage("you", null, Locale.ENGLISH);
		String enThanks = resources.getMessage("thanks", new Object[]{enYou}, Locale.ENGLISH);
		assertThat(enHello, equalTo("hello"));
		assertThat(enThanks, equalTo("thank you"));

		String chHello = resources.getMessage("hello", null, "Default", Locale.SIMPLIFIED_CHINESE);
		String chYou = resources.getMessage("you", null, Locale.SIMPLIFIED_CHINESE);
		String chThanks = resources.getMessage("thanks", new Object[]{chYou}, Locale.SIMPLIFIED_CHINESE);
		assertThat(chHello, equalTo("你好"));
		assertThat(chThanks, equalTo("谢谢你"));
	}

	@Test
	void testApplicationContextAware() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
		Teacher teacher = context.getBean("teacher", Teacher.class);
		List<Course> courses = teacher.getCourses();
		assertThat(courses.size(), equalTo(1));
		assertThat(courses.get(0).getName(), equalTo("math"));
	}
}