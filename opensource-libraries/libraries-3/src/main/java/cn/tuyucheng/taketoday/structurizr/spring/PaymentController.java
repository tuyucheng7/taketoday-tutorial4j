package cn.tuyucheng.taketoday.structurizr.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PaymentController {
	@Autowired
	private PaymentRepository repository;

	@Autowired
	private GenericComponent component;
}
