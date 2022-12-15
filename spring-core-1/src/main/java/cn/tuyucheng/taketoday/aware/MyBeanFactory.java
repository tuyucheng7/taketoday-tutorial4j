package cn.tuyucheng.taketoday.aware;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

@ExcludeFromJacocoGeneratedReport
public class MyBeanFactory implements BeanFactoryAware {

	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void getMyBeanName() {
		MyBeanName myBeanName = beanFactory.getBean(MyBeanName.class);
		System.out.println(beanFactory.isSingleton("myCustomBeanName"));
	}
}