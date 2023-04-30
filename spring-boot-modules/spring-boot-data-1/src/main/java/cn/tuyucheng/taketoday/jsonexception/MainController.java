package cn.tuyucheng.taketoday.jsonexception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/")
	public void index() throws CustomException {
		throw new CustomException();
	}

}