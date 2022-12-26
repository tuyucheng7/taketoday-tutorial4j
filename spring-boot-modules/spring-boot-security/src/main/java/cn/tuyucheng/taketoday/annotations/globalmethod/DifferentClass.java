package cn.tuyucheng.taketoday.annotations.globalmethod;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@ExcludeFromJacocoGeneratedReport
public class DifferentClass {
	@RolesAllowed("USER")
	public String differentJsr250Hello() {
		return "Hello Jsr250";
	}
}