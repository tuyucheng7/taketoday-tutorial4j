package cn.tuyucheng.taketoday.aware;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.beans.factory.BeanNameAware;

@ExcludeFromJacocoGeneratedReport
public class MyBeanName implements BeanNameAware {

	@Override
	public void setBeanName(String beanName) {
		System.out.println(beanName);
	}
}