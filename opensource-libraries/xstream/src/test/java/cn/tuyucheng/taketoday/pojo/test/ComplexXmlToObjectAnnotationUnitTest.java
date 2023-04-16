package cn.tuyucheng.taketoday.pojo.test;

import cn.tuyucheng.taketoday.complex.pojo.ContactDetails;
import cn.tuyucheng.taketoday.complex.pojo.Customer;
import cn.tuyucheng.taketoday.initializer.SimpleXstreamInitializer;
import com.thoughtworks.xstream.XStream;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;

import static org.junit.Assert.assertNotNull;

public class ComplexXmlToObjectAnnotationUnitTest {

	private XStream xstream = null;

	@Before
	public void dataSetup() {
		SimpleXstreamInitializer simpleXstreamInitializer = new SimpleXstreamInitializer();
		xstream = simpleXstreamInitializer.getXstreamInstance();
		xstream.processAnnotations(Customer.class);
	}

	@Test
	public void convertXmlToObjectFromFile() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		FileReader reader = new FileReader(classLoader
			.getResource("data-file-alias-field-complex.xml")
			.getFile());
		Customer customer = (Customer) xstream.fromXML(reader);

		assertNotNull(customer);
		assertNotNull(customer.getContactDetailsList());
	}

	@Test
	public void convertXmlToObjectAttributeFromFile() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		FileReader reader = new FileReader(classLoader
			.getResource("data-file-alias-field-complex.xml")
			.getFile());
		Customer customer = (Customer) xstream.fromXML(reader);

		assertNotNull(customer);
		assertNotNull(customer.getContactDetailsList());

		for (ContactDetails contactDetails : customer.getContactDetailsList()) {
			assertNotNull(contactDetails.getContactType());
		}
	}
}