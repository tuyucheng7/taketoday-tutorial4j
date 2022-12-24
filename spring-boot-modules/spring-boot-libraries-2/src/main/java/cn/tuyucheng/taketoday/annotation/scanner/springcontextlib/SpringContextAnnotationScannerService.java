package cn.tuyucheng.taketoday.annotation.scanner.springcontextlib;

import cn.tuyucheng.taketoday.annotation.scanner.SampleAnnotation;
import cn.tuyucheng.taketoday.annotation.scanner.SampleAnnotationScanner;
import cn.tuyucheng.taketoday.annotation.scanner.ScanNotSupportedException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SpringContextAnnotationScannerService implements SampleAnnotationScanner {
	@Override
	public List<String> scanAnnotatedMethods() {
		throw new ScanNotSupportedException();
	}

	@Override
	public List<String> scanAnnotatedClasses() {
		ClassPathScanningCandidateComponentProvider provider =
			new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(SampleAnnotation.class));
		Set<BeanDefinition> beanDefs = provider
			.findCandidateComponents("cn.tuyucheng.taketoday.annotation.scanner");
		List<String> annotatedBeans = new ArrayList<>();
		for (BeanDefinition bd : beanDefs) {
			if (bd instanceof AnnotatedBeanDefinition) {
				Map<String, Object> annotAttributeMap = ((AnnotatedBeanDefinition) bd)
					.getMetadata()
					.getAnnotationAttributes(SampleAnnotation.class.getCanonicalName());
				annotatedBeans.add(annotAttributeMap.get("name")
					.toString());
			}
		}
		return Collections.unmodifiableList(annotatedBeans);
	}

	@Override
	public boolean supportsMethodScan() {
		return false;
	}

	@Override
	public boolean supportsClassScan() {
		return true;
	}
}