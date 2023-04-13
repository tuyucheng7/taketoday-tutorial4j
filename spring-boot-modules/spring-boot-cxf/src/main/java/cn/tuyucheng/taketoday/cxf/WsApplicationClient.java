package cn.tuyucheng.taketoday.cxf;

import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.Service.Mode;
import org.apache.cxf.staxutils.StaxUtils;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.net.URL;

public class WsApplicationClient {

	public static void main(String[] args) throws Exception {
		String address = "http://localhost:8080/Service/Hello";
		String request = "<q0:sayHello xmlns:q0=\"http://service.cxf.taketoday.tuyucheng.cn/\"><myname>Elan</myname></q0:sayHello>";

		StreamSource source = new StreamSource(new StringReader(request));
		Service service = Service.create(new URL(address + "?wsdl"),
			new QName("http://service.cxf.taketoday.tuyucheng.cn/", "HelloService"));
		Dispatch<Source> disp = service.createDispatch(new QName("http://service.cxf.taketoday.tuyucheng.cn/", "HelloPort"),
			Source.class, Mode.PAYLOAD);

		Source result = disp.invoke(source);
		String resultAsString = StaxUtils.toString(result);
		System.out.println(resultAsString);
	}
}