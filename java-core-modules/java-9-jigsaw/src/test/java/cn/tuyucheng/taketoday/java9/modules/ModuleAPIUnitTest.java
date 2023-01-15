package cn.tuyucheng.taketoday.java9.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.*;
import java.sql.Date;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleAPIUnitTest {

	private static final String JAVA_BASE_MODULE_NAME = "java.base";

	private Module javaBaseModule;
	private Module javaSqlModule;
	private Module module;

	@BeforeEach
	void setUp() {
		javaBaseModule = HashMap.class.getModule();
		javaSqlModule = Date.class.getModule();
		module = Person.class.getModule();
	}

	@Test
	void whenCheckingIfNamed_thenModuleIsNamed() {
		assertThat(javaBaseModule.isNamed(), is(true));
		assertThat(javaBaseModule.getName(), is(JAVA_BASE_MODULE_NAME));
	}

	@Test
	void whenCheckingIfNamed_thenModuleIsUnnamed() {
		assertThat(module.isNamed(), is(false));
		assertThat(module.getName(), is(nullValue()));
	}

	@Test
	void whenExtractingPackagesContainedInAModule_thenModuleContainsOnlyFewOfThem() {
		assertTrue(javaBaseModule.getPackages().contains("java.lang.annotation"));
		assertFalse(javaBaseModule.getPackages().contains("java.sql"));
	}

	@Test
	void whenRetrievingClassLoader_thenClassLoaderIsReturned() {
		assertThat(
				module.getClassLoader().getClass().getName(),
				is("jdk.internal.loader.ClassLoaders$AppClassLoader")
		);
	}

	@Test
	void whenGettingAnnotationsPresentOnAModule_thenNoAnnotationsArePresent() {
		assertThat(javaBaseModule.getAnnotations().length, is(0));
	}

	@Test
	void whenGettingLayerOfAModule_thenModuleLayerInformationAreAvailable() {
		ModuleLayer javaBaseModuleLayer = javaBaseModule.getLayer();

		assertTrue(javaBaseModuleLayer.configuration().findModule(JAVA_BASE_MODULE_NAME).isPresent());
		assertTrue(javaBaseModuleLayer.parents().get(0).configuration().parents().isEmpty());
	}

	@Test
	void whenRetrievingModuleDescriptor_thenTypeOfModuleIsInferred() {
		ModuleDescriptor javaBaseModuleDescriptor = javaBaseModule.getDescriptor();
		ModuleDescriptor javaSqlModuleDescriptor = javaSqlModule.getDescriptor();

		assertFalse(javaBaseModuleDescriptor.isAutomatic());
		assertFalse(javaBaseModuleDescriptor.isOpen());
		assertFalse(javaSqlModuleDescriptor.isAutomatic());
		assertFalse(javaSqlModuleDescriptor.isOpen());
	}

	@Test
	void givenModuleName_whenBuildingModuleDescriptor_thenBuilt() {
		Builder moduleBuilder = ModuleDescriptor.newModule("tuyucheng.base");

		ModuleDescriptor moduleDescriptor = moduleBuilder.build();

		assertThat(moduleDescriptor.name(), is("tuyucheng.base"));
	}

	@Test
	void givenModules_whenAccessingModuleDescriptorRequires_thenRequiresAreReturned() {
		Set<Requires> javaBaseRequires = javaBaseModule.getDescriptor().requires();
		Set<Requires> javaSqlRequires = javaSqlModule.getDescriptor().requires();

		Set<String> javaSqlRequiresNames = javaSqlRequires.stream()
				.map(Requires::name)
				.collect(Collectors.toSet());

		assertThat(javaBaseRequires, empty());
		assertThat(javaSqlRequiresNames, hasItems("java.base", "java.xml", "java.logging"));
	}

	@Test
	void givenModules_whenAccessingModuleDescriptorProvides_thenProvidesAreReturned() {
		Set<Provides> javaBaseProvides = javaBaseModule.getDescriptor().provides();
		Set<Provides> javaSqlProvides = javaSqlModule.getDescriptor().provides();

		Set<String> javaBaseProvidesService = javaBaseProvides.stream()
				.map(Provides::service)
				.collect(Collectors.toSet());

		assertThat(javaBaseProvidesService, hasItem("java.nio.file.spi.FileSystemProvider"));
		assertThat(javaSqlProvides, empty());
	}

	@Test
	void givenModules_whenAccessingModuleDescriptorExports_thenExportsAreReturned() {
		Set<Exports> javaSqlExports = javaSqlModule.getDescriptor().exports();

		Set<String> javaSqlExportsSource = javaSqlExports.stream()
				.map(Exports::source)
				.collect(Collectors.toSet());

		assertThat(javaSqlExportsSource, hasItems("java.sql", "javax.sql"));
	}

	@Test
	void givenModules_whenAccessingModuleDescriptorUses_thenUsesAreReturned() {
		Set<String> javaSqlUses = javaSqlModule.getDescriptor().uses();

		assertThat(javaSqlUses, hasItem("java.sql.Driver"));
	}

	@Test
	void givenModules_whenAccessingModuleDescriptorOpen_thenOpenAreReturned() {
		Set<Opens> javaBaseUses = javaBaseModule.getDescriptor().opens();
		Set<Opens> javaSqlUses = javaSqlModule.getDescriptor().opens();

		assertThat(javaBaseUses, empty());
		assertThat(javaSqlUses, empty());
	}

	@Test
	void whenAddingReadsToAModule_thenModuleCanReadNewModule() {
		Module updatedModule = module.addReads(javaSqlModule);

		assertTrue(updatedModule.canRead(javaSqlModule));
	}

	@Test
	void whenExportingPackage_thenPackageIsExported() {
		Module updatedModule = module.addExports("cn.tuyucheng.taketoday.java9.modules", javaSqlModule);

		assertTrue(updatedModule.isExported("cn.tuyucheng.taketoday.java9.modules"));
	}

	@Test
	void whenOpeningAModulePackage_thenPackagedIsOpened() {
		Module updatedModule = module.addOpens("cn.tuyucheng.taketoday.java9.modules", javaSqlModule);

		assertTrue(updatedModule.isOpen("cn.tuyucheng.taketoday.java9.modules", javaSqlModule));
	}

	@Test
	void whenAddingUsesToModule_thenUsesIsAdded() {
		Module updatedModule = module.addUses(Driver.class);

		assertTrue(updatedModule.canUse(Driver.class));
	}

	private static class Person {
		private final String name;

		Person(String name) {
			this.name = name;
		}

		String getName() {
			return name;
		}
	}
}