package cn.tuyucheng.taketoday;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Date;

public class MyPayloadClonePrepare implements Processor {

	public void process(Exchange exchange) throws Exception {
		MyPayload myPayload = exchange.getIn().getBody(MyPayload.class);
		exchange.getIn().setBody(myPayload.deepClone());
		exchange.getIn().setHeader("date", new Date());
	}
}