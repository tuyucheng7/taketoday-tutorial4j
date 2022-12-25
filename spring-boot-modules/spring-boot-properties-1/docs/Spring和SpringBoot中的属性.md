## 1. 概述

本教程将展示如何通过Java配置和@PropertySource在**Spring中设置和使用属性**。

**我们还将了解属性在Spring Boot中的工作方式**。

## 2. 通过注解注册一个属性文件

Spring 3.1还引入了**新的@PropertySource注解**，作为将属性源添加到环境中的便捷机制。

我们可以将此注解与@Configuration注解结合使用：

```java
@Configuration
@PropertySource("classpath:foo.properties")
public class PropertiesWithJavaConfig {
	// ...
}
```

注册新属性文件的另一种非常有用的方法是使用占位符，它允许我们在运行时**动态选择正确的文件**：

```java
@PropertySource({
	"classpath:persistence-${envTarget:mysql}.properties"
})
// ...
```

### 2.1 定义多个属性位置

根据[Java 8约定](https://docs.oracle.com/javase/tutorial/java/annotations/repeating.html)，@PropertySource注解是可重复的。因此，如果我们使用的是Java 8或更高版本，我们可以使用此注解来定义多个属性位置：

```java
@PropertySource("classpath:foo.properties")
@PropertySource("classpath:bar.properties")
public class PropertiesWithJavaConfig {
	// ...
}
```

当然，**我们也可以使用@PropertySources注解并指定一个@PropertySource的数组**，这适用于任何受支持的Java版本，而不仅仅是Java 8或更高版本：

```java
@PropertySources({
	@PropertySource("classpath:foo.properties"),
	@PropertySource("classpath:bar.properties")
})
public class PropertiesWithJavaConfig {
	// ...
}
```

在任何一种情况下，值得注意的是，如果发生属性名称冲突，最后读取的属性源优先。

## 3. 使用/注入属性

**使用**[@Value注解]()**注入一个属性很简单**：

```java
@Value("${jdbc.url}")
private String jdbcUrl;
```

**我们还可以为属性指定一个默认值**：

```java
@Value("${jdbc.url:aDefaultUrl}")
private String jdbcUrl;
```

Spring 3.1中添加的新**PropertySourcesPlaceholderConfigurer解析了bean定义属性值和@Value注解中的${...}占位符**。

最后，我们可以**使用Environment API获取属性的值**：

```java
@Autowired
private Environment env;
// ...
dataSource.setUrl(env.getProperty("jdbc.url"));
```

## 4. Spring Boot的属性

在我们进入更高级的属性配置选项之前，让我们花一些时间看看Spring Boot中的新属性支持。

一般来说，与标准Spring相比，**这种新得支持涉及的配置更少，这当然也是Boot的主要目标之一**。

### 4.1 application.properties：默认属性文件

Boot将其典型的约定优于配置方法应用于属性文件，这意味着**我们可以简单地将一个application.properties文件放在我们的src/main/resources目录中，它会被自动检测到**，然后我们可以像往常一样从它注入任何加载的属性。

因此，通过使用此默认文件，我们不必显式注册PropertySource，甚至不必提供属性文件的路径。

如果需要，我们还可以使用环境属性在运行时配置不同的文件：

```bash
java -jar app.jar --spring.config.location=classpath:/another-location.properties
```

从[Spring Boot 2.3](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.3-Release-Notes#support-of-wildcard-locations-for-configuration-files)开始，**我们还可以为配置文件指定通配符位置**。

例如，我们可以将spring.config.location属性设置为config/*/：

```bash
java -jar app.jar --spring.config.location=config/*/
```

这样，Spring Boot将在我们的jar文件之外查找与config/*/目录模式匹配的配置文件，当我们有多个配置属性源时，这会派上用场。

从版本2.4.0开始，**Spring Boot支持使用多文档属性文件**，类似于[YAML](https://yaml.org/spec/1.2/spec.html#id2760395)的设计：

```properties
tuyucheng.customProperty=defaultValue
#---
tuyucheng.customProperty=overriddenValue
```

请注意，对于属性文件，三破折号表示法前面有一个注释字符(#)。

### 4.2 特定于环境的属性文件

如果我们需要针对不同的环境，Boot中有一个内置机制。

**我们可以简单的在src/main/resources目录下定义一个application-environment.properties文件，然后设置一个具有相同环境名称的Spring配置文件**。

例如，如果我们定义一个“staging”环境，这意味着我们必须先定义一个staging配置文件，然后再定义application-staging.properties。

**此环境文件将被加载并优先于默认属性文件**；请注意，默认文件仍将被加载，只是当发生属性冲突时，特定于环境的属性文件优先。

### 4.3 特定于测试的属性文件

在测试应用程序时，我们可能还需要使用不同的属性值。

**Spring Boot通过在测试运行期间查看我们的src/test/resources目录来为我们处理这个问题**。同样，默认属性仍然可以正常注入，但如果发生冲突，则这些属性将被这些测试属性覆盖。

### 4.4 @TestPropertySource注解

如果我们需要对测试属性进行更精细的控制，那么我们可以使用[@TestPropertySource](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/test/context/TestPropertySource.html)注解。

**这允许我们为特定的测试上下文设置测试属性，优先于默认属性源**：

```java
@RunWith(SpringRunner.class)
@TestPropertySource("/foo.properties")
public class FilePropertyInjectionUnitTest {

	@Value("${foo}")
	private String foo;

	@Test
	public void whenFilePropertyProvided_thenProperlyInjected() {
		assertThat(foo).isEqualTo("bar");
	}
}
```

如果我们不想使用文件，我们可以直接指定属性名称和值：

```java
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"foo=bar"})
public class PropertyInjectionUnitTest {

	@Value("${foo}")
	private String foo;

	@Test
	public void whenPropertyProvided_thenProperlyInjected() {
		assertThat(foo).isEqualTo("bar");
	}
}
```

**我们还可以使用**[@SpringBootTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html)**注解的properties参数来实现类似的效果**：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"foo=bar"}, classes = SpringBootPropertiesTestApplication.class)
public class SpringBootPropertyInjectionIntegrationTest {

	@Value("${foo}")
	private String foo;

	@Test
	public void whenSpringBootPropertyProvided_thenProperlyInjected() {
		assertThat(foo).isEqualTo("bar");
	}
}
```

### 4.5 分层属性

如果我们有组合在一起的属性，我们可以使用[@ConfigurationProperties](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/properties/ConfigurationProperties.html)注解，它将这些属性层次结构映射到Java对象图中。

让我们使用一些用于配置数据库连接的属性：

```properties
database.url=jdbc:postgresql:/localhost:5432/instance
database.username=foo
database.password=bar
```

然后让我们使用注解将它们映射到Database对象：

```java
@ConfigurationProperties(prefix = "database")
public class Database {
	String url;
	String username;
	String password;

	// standard getters and setters
}
```

Spring Boot再次应用它的约定优于配置方法，自动在属性名称和它们对应的字段之间进行映射，我们需要提供的只是属性前缀。

如果你想更深入地了解配置属性，请查看我们[更深入的文章]()。

### 4.6 替代方案：YAML文件

Spring还支持YAML文件。

所有相同的命名规则都适用于特定于测试、特定于环境和默认的属性文件，唯一的区别是文件扩展名和对类路径上的[SnakeYAML](https://bitbucket.org/asomov/snakeyaml)库的依赖。

**YAML特别适用于分层属性存储**；例如以下属性文件：

```properties
database.url=jdbc:postgresql:/localhost:5432/instance
database.username=foo
database.password=bar
secret=foo
```

与以下YAML文件同义：

```yaml
database:
    url: jdbc:postgresql:/localhost:5432/instance
    username: foo
    password: bar
secret: foo
```

还值得一提的是YAML文件不支持@PropertySource注解，因此如果我们需要使用这个注解，它会限制我们使用属性文件。

另一个值得注意的地方是，在2.4.0版本中，Spring Boot改变了从多文档YAML文件加载属性的方式。以前，它们的添加顺序基于配置文件激活顺序；然而，对于新版本，框架遵循我们之前为.properties文件指出的相同排序规则；在文件中声明较低优先级的属性将简单地覆盖较高的属性。

此外，在此版本中，无法再从特定于配置文件的文档中激活配置文件，从而使结果更加清晰和可预测。

### 4.7 导入其他配置文件

在2.4.0版本之前，Spring Boot允许使用spring.config.location和spring.config.additional-location属性包含额外的配置文件，但它们有一定的限制。例如，它们必须在启动应用程序之前定义(作为环境或系统属性，或使用命令行参数)，因为它们在流程的早期使用。

在上述版本中，**我们可以在application.properties或application.yml文件中使用spring.config.import属性来轻松包含其他文件**，此属性支持一些有趣的功能：

-   添加多个文件或目录
-   这些文件可以从类路径或外部目录加载
-   指示如果找不到文件或者它是可选文件，启动过程是否应该失败
-   导入无扩展名文件

让我们看一个有效的例子：

```properties
spring.config.import=classpath:additional-application.properties,
  classpath:additional-application[.yml],
  optional:file:./external.properties,
  classpath:additional-application-properties/
```

注意：为了清楚起见，我们在这里使用换行符格式化此属性。

Spring会将导入视为一个新文档，插入到import声明的正下方。

### 4.8 来自命令行参数的属性

除了使用文件，我们还可以直接在命令行中传递属性：

```bash
java -jar app.jar --property="value"
```

我们也可以通过系统属性来做到这一点，这些属性在-jar命令之前而不是之后提供：

```bash
java -Dproperty.name="value" -jar app.jar
```

### 4.9 来自环境变量的属性

Spring Boot还将检测环境变量，将它们视为属性：

```bash
export name=value
java -jar app.jar
```

### 4.10 属性值的随机化

如果我们不想要确定性的属性值，我们可以使用[RandomValuePropertySource](https://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/api/org/springframework/boot/context/config/RandomValuePropertySource.html)来随机化属性值：

```properties
random.number=${random.int}
random.long=${random.long}
random.uuid=${random.uuid}
```

### 4.11 其他类型的属性源

Spring Boot支持多种属性源，实现了经过深思熟虑的排序以允许合理的覆盖，值得参考[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)，它超出了本文的范围。

## 5. 使用Beans的配置-PropertySourcesPlaceholderConfigurer

除了将属性获取到Spring中的便捷方法外，我们还可以手动定义和注册属性配置bean。

**使用PropertySourcesPlaceholderConfigurer可以让我们完全控制配置，缺点是更冗长，而且大多数时候是不必要的**。

让我们看看如何使用Java配置来定义这个bean：

```java
@Bean
public static PropertySourcesPlaceholderConfigurer properties(){
    PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
    Resource[] resources = new ClassPathResource[ ]{new ClassPathResource("foo.properties")};
    pspc.setLocations( resources );
    pspc.setIgnoreUnresolvablePlaceholders( true );
    return pspc;
}
```

## 6. 父子上下文中的属性

这个问题一次又一次地出现：当我们的Web应用程序具有一个父上下文和一个子上下文时会发生什么？父上下文可能有一些共同的核心功能和bean，然后是一个(或多个)子上下文，可能包含特定于Servlet的bean。

在这种情况下，定义属性文件并将它们包含在这些上下文中的最佳方法是什么？以及如何最好地从Spring检索这些属性？

我们将给出一个简单的细分。

如果文件是在**父上下文中定义**的：

-   @Value在子上下文中工作：是

-   @Value在父上下文中工作：是

-   子上下文中的environment.getProperty：是

-   父上下文中的environment.getProperty：是

如果文件是在**子上下文中定义**的：

-   @Value在子上下文中工作：是

-   @Value在父上下文中工作：否

-   子上下文中的environment.getProperty：是

-   父上下文中的environment.getProperty：否

## 7. 总结

本文演示了几个在Spring中使用属性和属性文件的示例。