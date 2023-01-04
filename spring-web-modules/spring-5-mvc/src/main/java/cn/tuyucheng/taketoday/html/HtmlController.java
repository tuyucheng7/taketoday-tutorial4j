package cn.tuyucheng.taketoday.html;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HtmlController {
	@GetMapping(value = "/welcome", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String welcomeAsHTML() {
		return "<html>\n" + "<header><title>Welcome</title></header>\n" +
			"<body>\n" + "Hello world\n" + "</body>\n" + "</html>";
	}
}