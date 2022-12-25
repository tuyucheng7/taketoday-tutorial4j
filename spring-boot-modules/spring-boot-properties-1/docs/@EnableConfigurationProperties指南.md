## 1. 简介

在本快速教程中，**我们将展示如何将@EnableConfigurationProperties注解与**[@ConfigurationProperties]()**注解类一起使用**。

## 2. @EnableConfigurationProperties注解的用途

**@EnableConfigurationProperties注解与@ConfiguratonProperties紧密相连**，它在我们的应用程序中启用了对@ConfigurationProperties注解类的支持。但是，值得指出的是，[Spring Boot文档](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config-typesafe-configuration-properties)**说，每个项目都会自动包含@EnableConfigurationProperties**。因此，@ConfigurationProperties支持在每个Spring Boot应用程序中隐式打开。

为了在我们的项目中使用配置类，我们需要将其注册为常规的Spring bean。

首先，我们可以用@Component注解来标注这样一个类。或者，我们可以使用@Bean工厂方法。

**但是，在某些情况下，我们可能更愿意将@ConfigurationProperties类保留为简单的POJO，这时候@EnableConfigurationProperties就派上用场了，我们可以直接在这个注解上指定所有的配置bean**。

**这是一种快速注册@ConfigurationProperties注解bean的便捷方式**。

## 3. 使用@EnableConfigurationProperties

现在，让我们看看如何在实践中使用@EnableConfigurationProperties。

首先，我们需要定义示例配置类：

```java
@ConfigurationProperties(prefix = "additional")
public class AdditionalProperties {

	private String unit;
	private int max;

	// standard getters and setters
}
```

**请注意，我们仅使用@ConfigurationProperties注解标注了AdditionalProperties，也就是说它还是一个简单的POJO**！

最后，让我们使用@EnableConfigurationProperties注册我们的配置bean：

```java
@Configuration
@EnableConfigurationProperties(AdditionalProperties.class)
public class AdditionalConfiguration {

	@Autowired
	private AdditionalProperties additionalProperties;

	// make use of the bound properties
}
```

就这样！**我们现在可以像使用任何其他Spring bean一样使用AdditionalProperties**。

## 4. 总结

在这个快速教程中，我们介绍了一种在Spring中快速注册@ConfigurationProperties注解类的便捷方法。