package cn.tuyucheng.taketoday.caching.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
// to run any module like ehcache,caching or multiplecachemanager in local machine
// add it's package for ComponentScan like below
//@ComponentScan("cn.tuyucheng.taketoday.multiplecachemanager")
public class CacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(CacheApplication.class, args);
	}
}
