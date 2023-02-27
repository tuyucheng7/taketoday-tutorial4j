package cn.tuyucheng.taketoday.cxf.introduction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.SOAPBinding;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StudentLiveTest {
	private static QName SERVICE_NAME = new QName("http://introduction.cxf.tuyucheng.com/", "Tuyucheng");
	private static QName PORT_NAME = new QName("http://introduction.cxf.tuyucheng.com/", "TuyuchengPort");

	private Service service;
	private Tuyucheng tuyuchengProxy;
	private TuyuchengImpl tuyuchengImpl;

	{
		service = Service.create(SERVICE_NAME);
		final String endpointAddress = "http://localhost:8080/tuyucheng";
		service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
	}

	@BeforeEach
	void reinstantiateTuyuchengInstances() {
		tuyuchengImpl = new TuyuchengImpl();
		tuyuchengProxy = service.getPort(PORT_NAME, Tuyucheng.class);
	}

	@Test
	void whenUsingHelloMethod_thenCorrect() {
		final String endpointResponse = tuyuchengProxy.hello("Tuyucheng");
		final String localResponse = tuyuchengImpl.hello("Tuyucheng");
		assertEquals(localResponse, endpointResponse);
	}

	@Test
	void whenUsingHelloStudentMethod_thenCorrect() {
		final Student student = new StudentImpl("John Doe");
		final String endpointResponse = tuyuchengProxy.helloStudent(student);
		final String localResponse = tuyuchengImpl.helloStudent(student);
		assertEquals(localResponse, endpointResponse);
	}

	@Test
	void usingGetStudentsMethod_thenCorrect() {
		final Student student1 = new StudentImpl("Adam");
		tuyuchengProxy.helloStudent(student1);

		final Student student2 = new StudentImpl("Eve");
		tuyuchengProxy.helloStudent(student2);

		final Map<Integer, Student> students = tuyuchengProxy.getStudents();
		assertEquals("Adam", students.get(1).getName());
		assertEquals("Eve", students.get(2).getName());
	}
}