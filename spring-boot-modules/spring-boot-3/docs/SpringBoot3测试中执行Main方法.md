## 1. 概述

**通常由@SpringBootTest发现的测试配置将是你的主@SpringBootApplication类**。在大多数结构良好的应用程序中，此配置类还将包括用于启动应用程序的main方法。

在以前，我们经常使用[Spring Profile](https://www.baeldung.com/spring-profiles)来隔离不同环境的配置和测试。但是从[Spring Boot 3](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Release-Notes)开始，我们可以[使用@SpringBootApplication main方法配置测试](https://docs.spring.io/spring-boot/docs/3.0.0/reference/html/features.html#features.testing.spring-boot-applications.using-main)。

在本文中，我们将介绍Spring Boot 3对@SpringBootTest注解的改进。

## 2. Maven依赖

对于我们的示例，我们只需要添加[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.6)和[spring-boot-starter-test](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-test/3.0.6)这两个简单的依赖项：

```xml
<dependencies>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
        <version>3.0.0</version>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
        <version>3.0.0</version>
	</dependency>
</dependencies>
```

## 3. 使用Main方法配置测试

对于大多数时候，以下是一个典型的Spring Boot应用程序非常常见的代码模式：

```java
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

在上面的示例中，main方法除了委托给SpringApplication.run之外不做任何事情。但是，**在调用SpringApplication.run之前，可以使用更复杂的main方法来应用自定义**。

例如，下面是一个更改Banner模式并设置其他Profile的应用程序：

```java
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MyApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setAdditionalProfiles("myprofile");
        application.run(args);
    }
}
```

由于main方法中的自定义可能会影响生成的ApplicationContext，因此你可能还希望使用main方法创建测试中使用的ApplicationContext。

默认情况下，**@SpringBootTest不会调用你的main方法，而是直接使用类本身来创建ApplicationContext**。

例如，假设我们有一个对应于myprofile的配置文件application-myprofile.properties，其中包含以下属性：

```properties
customProperty=customValue
```

接下来，让我们创建一个@SpringBootTest测试用例，在其中我们注入customProperty属性并在测试中断言它的值正确：

```java
@SpringBootTest
public class MainIntegrationTest {

    @Value("${customProperty}")
    private String customProperty;

    @Test
    void whenUseMainMethodSetToAlways_thenShouldLoadCustomProperty() {
        assertEquals("customValue", customProperty);
    }
}
```

但是，由于我们之前所说的@SpringBootTest不会调用main方法，因此**该测试将抛出异常而终止**：

```shell
Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'customProperty' in value "${customProperty}"
```

对于这种情况，我们很快想到的一种解决方法是**在测试中指定我们要使用myprofile**，例如：

```java
@ActiveProfiles("myprofile")
@SpringBootTest
public class MainIntegrationTest {
    // ...
}
```

如果我们再次运行，测试就会通过。但是，这种方法并没有真正解决@SpringBootTest不会执行main方法代码的问题。

## 4. Spring Boot 3中的改进

如果要更改此行为，我们可以**将@SpringBootTest的[useMainMethod](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html#useMainMethod())属性更改为UseMainMethod.ALWAYS或UseMainMethod.WHEN_AVAILABLE**，这是在Spring Boot 3新增的一个属性。

**当设置为ALWAYS时，如果找不到main方法，则测试将失败。设置为WHEN_AVAILABLE时，如果可用，将使用main方法，否则将使用标准加载机制**。

例如，下面的测试将调用MyApplication的main方法以创建ApplicationContext。如果main方法设置了其他Profile，则这些Profile将在应用程序上下文启动时处于活动状态：

```java
@SpringBootTest(useMainMethod = UseMainMethod.ALWAYS)
public class MainIntegrationTest {
    // ...
}
```

因此，通过该属性，我们可以不需要指定额外的@ActiveProfiles注解，该测试仍然通过。

## 5. 总结

在本文中，我们介绍了Spring Boot 3对@SpringBootTest注解的改进。我们看到了如何使用main方法配置测试，以及如何使用@SpringBootTest的useMainMethod属性来调用main方法。