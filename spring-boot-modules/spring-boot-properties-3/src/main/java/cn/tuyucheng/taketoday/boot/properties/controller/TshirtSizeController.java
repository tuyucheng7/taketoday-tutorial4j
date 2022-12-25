package cn.tuyucheng.taketoday.boot.properties.controller;

import cn.tuyucheng.taketoday.boot.properties.service.SizeConverterService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class TshirtSizeController {

	private final SizeConverterService service;

	public TshirtSizeController(SizeConverterService service) {
		this.service = service;
	}

	@RequestMapping(value = "convertSize", method = RequestMethod.GET)
	public int convertSize(@RequestParam(value = "label") final String label, @RequestParam(value = "countryCode", required = false) final String countryCode) {
		return service.convertSize(label, countryCode);
	}
}