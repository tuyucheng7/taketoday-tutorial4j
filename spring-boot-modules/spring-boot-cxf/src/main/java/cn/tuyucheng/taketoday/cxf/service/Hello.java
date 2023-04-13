package cn.tuyucheng.taketoday.cxf.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;

/**
 * Examples code for spring boot with CXF services. Hello is the interface for
 * sayHello interface.
 */
@WebService(targetNamespace = "http://service.cxf.taketoday.tuyucheng.cn/", name = "Hello")
public interface Hello {

	@WebResult(name = "return", targetNamespace = "")
	@RequestWrapper(localName = "sayHello",
		targetNamespace = "http://service.cxf.taketoday.tuyucheng.cn/",
		className = "cn.tuyucheng.taketoday.cxf.service.SayHello")
	@WebMethod(action = "urn:SayHello")
	@ResponseWrapper(localName = "sayHelloResponse",
		targetNamespace = "http://service.cxf.taketoday.tuyucheng.cn/",
		className = "cn.tuyucheng.taketoday.cxf.service.SayHelloResponse")
	String sayHello(@WebParam(name = "myname", targetNamespace = "") String myname);
}