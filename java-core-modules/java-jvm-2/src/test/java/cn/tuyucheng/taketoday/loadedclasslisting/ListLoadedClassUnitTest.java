package cn.tuyucheng.taketoday.loadedclasslisting;

import com.google.common.reflect.ClassPath.ClassInfo;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Set;

public class ListLoadedClassUnitTest {

	private static final String PACKAGE_NAME = "cn.tuyucheng.taketoday.loadedclasslisting";

	@Test
	public void when_findAllClassesUsingReflectionsLibrary_thenSuccess() {
		ListLoadedClass instance = new ListLoadedClass();
		Set<Class> classes = instance.findAllClassesUsingReflectionsLibrary(PACKAGE_NAME);

		Assertions.assertEquals(4, classes.size());
	}

	@Test
	public void when_findAllClassesUsingGuavaLibrary_InPackage_thenSuccess() throws IOException {
		ListLoadedClass instance = new ListLoadedClass();
		Set<Class> classes = instance.listClassLoaded(PACKAGE_NAME);

		Assertions.assertEquals(4, classes.size());
	}

	@Test
	public void when_findAllClassesUsingGuavaLibrary_thenSuccess() throws IOException {
		ListLoadedClass instance = new ListLoadedClass();
		Set<ClassInfo> classes = instance.listClassLoaded();

		Assertions.assertTrue(4 < classes.size());
	}
}
