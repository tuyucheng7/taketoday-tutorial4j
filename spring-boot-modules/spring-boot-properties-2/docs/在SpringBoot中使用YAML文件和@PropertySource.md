## 1. 概述

在本快速教程中，我们将展示如何在Spring Boot中使用@PropertySource注解读取YAML属性文件。

## 2. @PropertySource和YAML格式

Spring Boot对外部化配置有很好的支持，此外，可以使用不同的方式和格式来读取开箱即用的Spring Boot应用程序中的属性。

但是，**默认情况下，@PropertySource不会加载YAML文件**，[官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config-yaml-shortcomings)中明确提到了这一事实。

因此，如果我们想在我们的应用程序中使用@PropertySource注解，我们需要坚持使用标准的properties文件，**或者我们可以自己实现缺失的拼图**！

## 3. 自定义PropertySourceFactory

从Spring 4.3开始，@PropertySource带有factory属性，我们可以利用它来**提供我们自定义的PropertySourceFactory实现，该PropertySourceFactory实现将处理YAML文件处理**。

这比听起来容易！让我们看看如何做到这一点：

```java
public class YamlPropertySourceFactory implements PropertySourceFactory {

	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(encodedResource.getResource());

		Properties properties = factory.getObject();

		return new PropertiesPropertySource(encodedResource.getResource().getFilename(), properties);
	}
}
```

正如我们所看到的，实现单个[createPropertySource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/support/PropertySourceFactory.html#createPropertySource-java.lang.String-org.springframework.core.io.support.EncodedResource-)方法就足够了。

在我们的自定义实现中，首先，**我们使用**[YamlPropertiesFactoryBean](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/YamlPropertiesFactoryBean.html)**将YAML格式的资源转换为java.util.Properties对象**。

然后，我们简单地返回了[PropertiesPropertySource](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/env/PropertiesPropertySource.html)的一个新实例，它是一个允许Spring读取已解析属性的包装器。

## 4. @PropertySource和YAML的实际应用

现在让我们将所有部分放在一起，看看如何在实践中使用它们。

首先，让我们创建一个简单的YAML文件foo.yml：

```yaml
yaml:
    name: foo
    aliases:
        - abc
        - xyz
```

接下来，让我们使用@ConfigurationProperties创建一个属性类，并使用我们自定义的YamlPropertySourceFactory：

```java
@Configuration
@ConfigurationProperties(prefix = "yaml")
@PropertySource(value = "classpath:foo.yml", factory = YamlPropertySourceFactory.class)
public class YamlFooProperties {

	private String name;

	private List<String> aliases;

	// standard getter and setters
}
```

最后，**我们验证属性是否已正确注入**：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class YamlFooPropertiesIntegrationTest {

	@Autowired
	private YamlFooProperties yamlFooProperties;

	@Test
	public void whenFactoryProvidedThenYamlPropertiesInjected() {
		assertThat(yamlFooProperties.getName()).isEqualTo("foo");
		assertThat(yamlFooProperties.getAliases()).containsExactly("abc", "xyz");
	}
}
```

## 5. 总结

总而言之，在这个快速教程中，我们首先看到了创建自定义的PropertySourceFactory是多么容易；之后，我们介绍了如何使用其factory属性将此自定义实现传递给@PropertySource。

因此，我们能够成功地将YAML属性文件加载到我们的Spring Boot应用程序中。