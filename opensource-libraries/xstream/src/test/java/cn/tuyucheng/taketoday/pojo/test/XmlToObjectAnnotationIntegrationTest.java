package cn.tuyucheng.taketoday.pojo.test;

import cn.tuyucheng.taketoday.annotation.pojo.Customer;
import cn.tuyucheng.taketoday.initializer.SimpleXstreamInitializer;
import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class XmlToObjectAnnotationIntegrationTest {

	private XStream xstream = null;

	@Before
	public void dataSetup() {
		SimpleXstreamInitializer simpleXstreamInitializer = new SimpleXstreamInitializer();
		xstream = simpleXstreamInitializer.getXstreamInstance();
		xstream.processAnnotations(Customer.class);
	}

	@Test
	public void convertXmlToObjectFromFile() throws FileNotFoundException {
		ClassLoader classLoader = getClass().getClassLoader();
		FileReader reader = new FileReader(classLoader.getResource("data-file-alias-field.xml").getFile());
		Customer customer = (Customer) xstream.fromXML(reader);
		Assert.assertNotNull(customer);
		Assert.assertNotNull(customer.getFirstName());
	}

}
