package cn.tuyucheng.taketoday.annotation.scanner.springcorelib;

import cn.tuyucheng.taketoday.annotation.scanner.SampleAnnotatedClass;
import cn.tuyucheng.taketoday.annotation.scanner.SampleAnnotation;
import cn.tuyucheng.taketoday.annotation.scanner.SampleAnnotationScanner;
import cn.tuyucheng.taketoday.annotation.scanner.ScanNotSupportedException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpringCoreAnnotationScannerService implements SampleAnnotationScanner {
	@Override
	public List<String> scanAnnotatedMethods() {
		Class<?> userClass = ClassUtils.getUserClass(SampleAnnotatedClass.class);
		List<String> annotatedMethods = Arrays.stream(userClass.getMethods())
			.filter(method -> AnnotationUtils
				.getAnnotation(method, SampleAnnotation.class) != null)
			.map(method -> method.getAnnotation(SampleAnnotation.class)
				.name())
			.collect(Collectors.toList());

		return annotatedMethods;
	}

	@Override
	public List<String> scanAnnotatedClasses() {
		throw new ScanNotSupportedException();
	}

	@Override
	public boolean supportsMethodScan() {
		return true;
	}

	@Override
	public boolean supportsClassScan() {
		return false;
	}
}