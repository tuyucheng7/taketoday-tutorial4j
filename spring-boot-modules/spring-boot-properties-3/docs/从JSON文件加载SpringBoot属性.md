## 1. 简介

使用外部配置属性是一种非常常见的模式，而且，最常见的问题之一是能够在多个环境(例如开发、测试和生产)中更改我们的应用程序的行为，而无需更改部署工件。

在本教程中，我们将**重点介绍如何在Spring Boot应用程序中从JSON文件加载属性**。

## 2. 在Spring Boot中加载属性

Spring和Spring Boot对加载外部配置提供了强大的支持-你可以在[本文]()中找到对基础知识的全面概述。

由于**此支持主要集中在.properties和.yml文件上，因此使用JSON通常需要额外的配置**。

我们将假设基本特性是众所周知的-并将在这里重点关注JSON特定方面。

## 3. 通过命令行加载属性

我们可以在命令行中以三种预定义格式提供JSON数据。

首先，我们可以在UNIX shell中设置环境变量SPRING_APPLICATION_JSON：

```bash
$ SPRING_APPLICATION_JSON='{"environment":{"name":"production"}}' java -jar app.jar
```

提供的数据将填充到Spring Environment中，在此示例中，我们将获得值为“production”的属性environment.name。

此外，我们可以将JSON作为系统属性加载，例如：

```bash
$ java -Dspring.application.json='{"environment":{"name":"production"}}' -jar app.jar
```

最后一个选项是使用一个简单的命令行参数：

```bash
$ java -jar app.jar --spring.application.json='{"environment":{"name":"production"}}'
```

使用最后两种方法，spring.application.json属性将使用给定数据作为未解析的字符串填充。

这些是将JSON数据加载到我们的应用程序中的最简单的选项，**这种简约方法的缺点是缺乏可扩展性**。

在命令行中加载大量数据可能很麻烦且容易出错。

## 4. 通过@PropertySource注解加载属性

Spring Boot提供了一个强大的生态系统来通过注解创建配置类。

首先，我们定义一个带有一些简单成员变量的配置类：

```java
public class JsonProperties {

	private int port;

	private boolean resend;

	private String host;

	// getters and setters
}
```

我们可以在外部文件中提供标准JSON格式的数据(我们将其命名为configprops.json)：

```json
{
	"host": "mailer@mail.com",
	"port": 9090,
	"resend": true
}
```

现在我们必须将我们的JSON文件连接到配置类：

```java
@Component
@PropertySource(value = "classpath:configprops.json")
@ConfigurationProperties
public class JsonProperties {
	// same code as before
}
```

我们在类和JSON文件之间有一个松耦合，此连接基于字符串和变量名称，因此我们没有编译时检查，但我们可以通过测试来验证绑定。

由于字段应该由框架填充，所以我们需要使用集成测试。

对于简约的设置，我们可以定义应用程序的主要入口点：

```java
@SpringBootApplication
@ComponentScan(basePackageClasses = {JsonProperties.class})
public class ConfigPropertiesDemoApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(ConfigPropertiesDemoApplication.class).run();
	}
}
```

现在可以创建我们的集成测试：

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConfigPropertiesDemoApplication.class)
public class JsonPropertiesIntegrationTest {

	@Autowired
	private JsonProperties jsonProperties;

	@Test
	public void whenPropertiesLoadedViaJsonPropertySource_thenLoadFlatValues() {
		assertEquals("mailer@mail.com", jsonProperties.getHost());
		assertEquals(9090, jsonProperties.getPort());
		assertTrue(jsonProperties.isResend());
	}
}
```

因此，该测试将产生一个错误，即使加载ApplicationContext也会失败，原因如下：

```shell
ConversionFailedException: 
Failed to convert from type [java.lang.String] 
to type [boolean] for value 'true,'
```

加载机制通过@PropertySource注解成功地将类与JSON文件关联起来，但是resend属性的值被评估为“true”(带逗号)，无法转换为布尔值。

**因此，我们必须在加载机制中注入一个JSON解析器**；幸运的是，Spring Boot自带了Jackson库，我们可以通过PropertySourceFactory使用它。

## 5. 使用PropertySourceFactory解析JSON

**我们必须提供一个具有解析JSON数据能力的自定义PropertySourceFactory**：

```java
public class JsonPropertySourceFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
		Map readValue = new ObjectMapper().readValue(resource.getInputStream(), Map.class);
		return new MapPropertySource("json-property", readValue);
	}
}
```

我们可以提供这个工厂来加载我们的配置类，为此，我们必须从@PropertySource注解中引用工厂：

```java
@Configuration
@PropertySource(value = "classpath:configprops.json", factory = JsonPropertySourceFactory.class)
@ConfigurationProperties
public class JsonProperties {

	// same code as before
}
```

结果，我们的测试将通过。此外，这个属性源工厂也可以很好地解析集合值。

所以现在我们可以使用集合成员(以及相应的getter和setter)来扩展我们的配置类：

```java
private List<String> topics;
// getter and setter
```

我们可以在JSON文件中提供输入值：

```json
{
    // same fields as before
    "topics" : ["spring", "boot"]
}
```

我们可以使用新的测试用例轻松测试集合值的绑定：

```java
@Test
public void whenPropertiesLoadedViaJsonPropertySource_thenLoadListValues() {
    assertThat(jsonProperties.getTopics(), Matchers.is(Arrays.asList("spring", "boot")));
}
```

### 5.1 嵌套结构

处理嵌套的JSON结构并不是一件容易的事，作为更健壮的解决方案，Jackson库的映射器会将嵌套数据映射到一个Map中。 

因此，我们可以使用getter和setter将Map成员添加到我们的JsonProperties类：

```java
private LinkedHashMap<String, ?> sender;
// getter and setter
```

在JSON文件中，我们可以为该字段提供嵌套数据结构：

```json
{
	// same fields as before
	"sender": {
		"name": "sender",
		"address": "street"
	}
}
```

现在我们可以通过Map访问嵌套数据：

```java
@Test
public void whenPropertiesLoadedViaJsonPropertySource_thenNestedLoadedAsMap() {
    assertEquals("sender", jsonProperties.getSender().get("name"));
    assertEquals("street", jsonProperties.getSender().get("address"));
}
```

## 6. 使用自定义ContextInitializer

**如果我们想更好地控制属性的加载，我们可以使用自定义的ContextInitializers**。这种手动方法比较乏味，但是我们能够完全控制数据的加载和解析。

我们将使用与之前相同的JSON数据，但我们将加载到不同的配置类中：

```java
@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomJsonProperties {

	private String host;

	private int port;

	private boolean resend;

	// getters and setters
}
```

请注意，我们不再使用@PropertySource注解，但是在@ConfigurationProperties注解中，我们定义了一个前缀。

在下一节中，我们将研究如何将属性加载到“custom”命名空间中。

### 6.1 将属性加载到自定义命名空间

为了为上面的属性类提供输入，我们将从JSON文件加载数据，解析后我们将使用MapPropertySources填充Spring Environment：

```java
public class JsonPropertyContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static String CUSTOM_PREFIX = "custom.";

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
		try {
			Resource resource = configurableApplicationContext.getResource("classpath:configpropscustom.json");
			Map readValue = new ObjectMapper()
				.readValue(resource.getInputStream(), Map.class);
			Set<Map.Entry> set = readValue.entrySet();
			List<MapPropertySource> propertySources = set.stream()
				.map(entry-> new MapPropertySource(
					CUSTOM_PREFIX + entry.getKey(), Collections.singletonMap(CUSTOM_PREFIX + entry.getKey(), entry.getValue()
				)))
				.collect(Collectors.toList());
			for (PropertySource propertySource : propertySources) {
				configurableApplicationContext.getEnvironment()
					.getPropertySources()
					.addFirst(propertySource);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
```

正如我们所看到的，它需要一些相当复杂的代码，但这是灵活性的代价。在上面的代码中，我们可以指定我们自己的解析器并决定如何处理每个条目。

**在此演示中，我们只是将属性放入custom命名空间中**。

要使用这个初始化器，我们必须将它连接到应用程序。对于生产使用，我们可以在SpringApplicationBuilder中添加以下内容：

```java
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {JsonProperties.class, CustomJsonProperties.class})
public class ConfigPropertiesDemoApplication {
	public static void main(String[] args) {
		new SpringApplicationBuilder(ConfigPropertiesDemoApplication.class)
			.initializers(new JsonPropertyContextInitializer())
			.run();
	}
}
```

另请注意，CustomJsonProperties类已添加到basePackageClasses属性中。

对于我们的测试环境，我们可以在@ContextConfiguration注解中提供我们的自定义初始化程序：

```java
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConfigPropertiesDemoApplication.class, initializers = JsonPropertyContextInitializer.class)
public class JsonPropertiesIntegrationTest {

	// same code as before
}
```

自动连接我们的CustomJsonProperties类后，我们可以从custom命名空间测试数据绑定：

```java
@Test
public void whenLoadedIntoEnvironment_thenFlatValuesPopulated() {
    assertEquals("mailer@mail.com", customJsonProperties.getHost());
    assertEquals(9090, customJsonProperties.getPort());
    assertTrue(customJsonProperties.isResend());
}
```

### 6.2 展平嵌套结构

Spring框架提供了一种强大的机制，可以将属性绑定到对象成员中，此功能的基础是属性中的名称前缀。

如果我们扩展我们的自定义ApplicationInitializer以将Map值转换为命名空间结构，那么框架可以将我们的嵌套数据结构直接加载到相应的对象中。

下面是增强的CustomJsonProperties类：

```java
@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomJsonProperties {

	// same code as before

	private Person sender;

	public static class Person {

		private String name;
		private String address;

		// getters and setters for Person class
	}

	// getters and setters for sender member
}
```

以及改进后的ApplicationContextInitializer：

```java
public class JsonPropertyContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final static String CUSTOM_PREFIX = "custom.";

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
		try {
			Resource resource = configurableApplicationContext.getResource("classpath:configpropscustom.json");
			Map readValue = new ObjectMapper()
				.readValue(resource.getInputStream(), Map.class);
			Set<Map.Entry> set = readValue.entrySet();
			List<MapPropertySource> propertySources = convertEntrySet(set, Optional.empty());
			for (PropertySource propertySource : propertySources) {
				configurableApplicationContext.getEnvironment()
					.getPropertySources()
					.addFirst(propertySource);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<MapPropertySource> convertEntrySet(Set<Map.Entry> entrySet, Optional<String> parentKey) {
		return entrySet.stream()
			.map((Map.Entry e) -> convertToPropertySourceList(e, parentKey))
			.flatMap(Collection::stream)
			.collect(Collectors.toList());
	}

	private static List<MapPropertySource> convertToPropertySourceList(Map.Entry e, Optional<String> parentKey) {
		String key = parentKey
			.map(s -> s + ".")
			.orElse("") + (String) e.getKey();
		Object value = e.getValue();
		return covertToPropertySourceList(key, value);
	}

	@SuppressWarnings("unchecked")
	private static List<MapPropertySource> covertToPropertySourceList(String key, Object value) {
		if (value instanceof LinkedHashMap) {
			LinkedHashMap map = (LinkedHashMap) value;
			Set<Map.Entry> entrySet = map.entrySet();
			return convertEntrySet(entrySet, Optional.ofNullable(key));
		}
		String finalKey = CUSTOM_PREFIX + key;
		return Collections.singletonList(new MapPropertySource(finalKey, Collections.singletonMap(finalKey, value)));
	}
}
```

**因此，我们嵌套的JSON数据结构将被加载到一个配置对象中**：

```java
@Test
public void whenLoadedIntoEnvironment_thenValuesLoadedIntoClassObject() {
    assertNotNull(customJsonProperties.getSender());
    assertEquals("sender", customJsonProperties.getSender().getName());
    assertEquals("street", customJsonProperties.getSender().getAddress());
}
```

## 7. 总结

Spring Boot框架提供了一种通过命令行加载外部JSON数据的简单方法，如果需要，我们可以通过适当配置的PropertySourceFactory加载JSON数据。

虽然，加载嵌套属性是可以解决的，但需要格外小心。