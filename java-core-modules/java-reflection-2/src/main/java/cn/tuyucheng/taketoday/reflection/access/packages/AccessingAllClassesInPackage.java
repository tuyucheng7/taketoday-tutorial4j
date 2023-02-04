package cn.tuyucheng.taketoday.reflection.access.packages;

import com.google.common.reflect.ClassPath;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class AccessingAllClassesInPackage {

	private static final Logger LOG = LoggerFactory.getLogger(AccessingAllClassesInPackage.class);

	public Set<Class> findAllClassesUsingClassLoader(String packageName) {
		InputStream stream = ClassLoader.getSystemClassLoader()
			.getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines()
			.filter(line -> line.endsWith(".class"))
			.map(line -> getClass(line, packageName))
			.collect(Collectors.toSet());
	}

	private Class getClass(String className, String packageName) {
		try {
			return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
		} catch (ClassNotFoundException e) {
			LOG.error("<<Class not found>>");
		}
		return null;
	}

	public Set<Class> findAllClassesUsingReflectionsLibrary(String packageName) {
		Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
		return new HashSet<>(reflections.getSubTypesOf(Object.class));
	}

	public Set<Class> findAllClassesUsingGoogleGuice(String packageName) throws IOException {
		return ClassPath.from(ClassLoader.getSystemClassLoader())
			.getAllClasses()
			.stream()
			.filter(clazz -> clazz.getPackageName()
				.equalsIgnoreCase(packageName))
			.map(ClassPath.ClassInfo::load)
			.collect(Collectors.toSet());
	}
}