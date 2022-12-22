package cn.tuyucheng.taketoday.jasypt.simple;

import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
import org.springframework.context.annotation.Configuration;

@Configuration
@EncryptablePropertySource(value = "encryptedv2.properties")
public class AppConfigForJasyptSimple {
}