package cn.tuyucheng.taketoday.cxf.service;

import jakarta.jws.WebService;

import java.util.logging.Logger;

/**
 * Examples code for spring boot with CXF services. HelloPortImpl is the
 * implementation for Hello interface.
 */
@WebService(serviceName = "HelloService", portName = "HelloPort",
	targetNamespace = "http://service.cxf.taketoday.tuyucheng.cn/",
	endpointInterface = "cn.tuyucheng.taketoday.cxf.service.Hello")
public class HelloPortImpl implements Hello {

	private static final Logger LOG = Logger.getLogger(HelloPortImpl.class.getName());

	public java.lang.String sayHello(String myname) {
		LOG.info("Executing operation sayHello" + myname);
		try {
			return "Hello, Welcome to CXF Spring boot " + myname + "!!!";

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
}