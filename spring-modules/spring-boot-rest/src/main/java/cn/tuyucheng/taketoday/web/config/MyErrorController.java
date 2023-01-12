package cn.tuyucheng.taketoday.web.config;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class MyErrorController extends BasicErrorController {

	public MyErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
		super(errorAttributes, serverProperties.getError());
	}

	@RequestMapping(produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Map<String, Object>> xmlError(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.APPLICATION_XML));
		body.put("xmlkey", "the XML response is different!");
		HttpStatus status = getStatus(request);
		return new ResponseEntity<>(body, status);
	}
}
