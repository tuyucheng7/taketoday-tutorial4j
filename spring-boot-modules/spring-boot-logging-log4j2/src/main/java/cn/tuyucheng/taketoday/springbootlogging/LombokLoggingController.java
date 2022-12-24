package cn.tuyucheng.taketoday.springbootlogging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// import lombok.extern.log4j.Log4j2;
// import lombok.extern.apachecommons.CommonsLog;

@RestController("LombokLoggingController")
@Slf4j
// @CommonsLog (Comment any other Lombok logging annotation and uncomment this
// to work with Apache Commons Logging)
// @Log4j2 (Comment any other Lombok logging annotation and uncomment this to
// work directly with Log4j2)
public class LombokLoggingController {

	@GetMapping("/lombok")
	public String index() {
		LOGGER.trace("A TRACE Message");
		LOGGER.debug("A DEBUG Message");
		LOGGER.info("An INFO Message");
		LOGGER.warn("A WARN Message");
		LOGGER.error("An ERROR Message");
		return "Howdy! Check out the Logs to see the output...";
	}
}