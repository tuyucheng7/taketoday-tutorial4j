package cn.tuyucheng.taketoday.countingbeans.olderspring.factorybeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnnotatedBeansComponent {

	@Autowired
	GenericApplicationContext applicationContext;

	public List<String> getBeansWithAnnotation(Class<?> annotationClass) {
		return BeanUtils.getBeansWithAnnotation(applicationContext, annotationClass);
	}

}