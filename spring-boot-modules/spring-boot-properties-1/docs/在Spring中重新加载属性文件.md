## 1. 概述

在本教程中，我们将学习如何在Spring应用程序中重新加载属性。

## 2. Spring中读取属性

我们有几个不同的选项来访问Spring中的属性：

1.  Environment：我们可以注入Environment然后使用Environment#getProperty来读取给定的属性，Environment包含不同的属性源，如系统属性、-D参数和application.properties(.yml)，还可以使用@PropertySource将额外的属性源添加到环境中。
2.  Properties：我们可以将属性文件加载到Properties实例中，然后通过调用properties.get("property")在bean中使用它。
3.  [@Value]()：我们可以使用@Value(${'property'})注解在bean中注入特定属性。
4.  [@ConfigurationProperties]()：我们可以使用@ConfigurationProperties在bean中加载分层属性。

## 3. 从外部文件重新加载属性

要在运行时更改文件中的属性，我们应该将该文件放在jar之外的某个位置，然后我们使用命令行参数–spring.config.location=file://{path to file}告诉Spring它在哪里。或者，我们可以将它放在application.properties中。

在基于文件的属性中，我们必须选择一种重新加载文件的方式。例如，我们可以开发一个端点或调度程序来读取文件并更新属性。

一个方便的重新加载文件的库是Apache的commons-configuration，我们可以将[PropertiesConfiguration](https://commons.apache.org/proper/commons-configuration/apidocs/org/apache/commons/configuration2/PropertiesConfiguration.html)与不同的ReloadingStrategy一起使用。

让我们将[commons-configuration](https://search.maven.org/search?q=g:commons-configuration a:commons-configuration)添加到我们的pom.xml：

```xml
<dependency>
    <groupId>commons-configuration</groupId>
    <artifactId>commons-configuration</artifactId>
    <version>1.10</version>
</dependency>
```

然后我们将添加一个方法来创建一个PropertiesConfiguration bean，稍后我们将使用它：

```java
@Bean
@ConditionalOnProperty(name = "spring.config.location", matchIfMissing = false)
public PropertiesConfiguration propertiesConfiguration(@Value("${spring.config.location}") String path) throws Exception {
    String filePath = new File(path.substring("file:".length())).getCanonicalPath();
    PropertiesConfiguration configuration = new PropertiesConfiguration(new File(filePath));
    configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
    return configuration;
}
```

在上面的代码中，我们将FileChangedReloadingStrategy设置为具有默认刷新延迟的重新加载策略，这意味着，**如果上次检查是在5000毫秒前，则PropertiesConfiguration会检查文件修改日期**。

我们可以使用FileChangedReloadingStrategy#setRefreshDelay自定义延迟。

### 3.1 重新加载环境属性

如果我们想重新加载通过Environment实例加载的属性，我们必须**扩展PropertySource，然后使用PropertiesConfiguration从外部属性文件返回新值**。

让我们从扩展PropertySource开始：

```java
public class ReloadablePropertySource extends PropertySource {

	PropertiesConfiguration propertiesConfiguration;

	public ReloadablePropertySource(String name, PropertiesConfiguration propertiesConfiguration) {
		super(name);
		this.propertiesConfiguration = propertiesConfiguration;
	}

	public ReloadablePropertySource(String name, String path) {
		super(StringUtils.hasText(name) ? path : name);
		try {
			this.propertiesConfiguration = new PropertiesConfiguration(path);
			this.propertiesConfiguration.setReloadingStrategy(new FileChangedReloadingStrategy());
		} catch (Exception e) {
			throw new PropertiesException(e);
		}
	}

	@Override
	public Object getProperty(String s) {
		return propertiesConfiguration.getProperty(s);
	}
}
```

我们已经重写了getProperty方法以将其委托给PropertiesConfiguration#getProperty。因此，它将根据我们的刷新延迟定期检查更新的值。

现在我们将我们的ReloadablePropertySource添加到Environment的属性源中：

```java
@Configuration
public class ReloadablePropertySourceConfig {

	private ConfigurableEnvironment env;

	public ReloadablePropertySourceConfig(@Autowired ConfigurableEnvironment env) {
		this.env = env;
	}

	@Bean
	@ConditionalOnProperty(name = "spring.config.location", matchIfMissing = false)
	public ReloadablePropertySource reloadablePropertySource(PropertiesConfiguration properties) {
		ReloadablePropertySource ret = new ReloadablePropertySource("dynamic", properties);
		MutablePropertySources sources = env.getPropertySources();
		sources.addFirst(ret);
		return ret;
	}
}
```

**我们将新的属性源添加为第一项**，因为我们希望它覆盖具有相同键的任何现有属性。

让我们创建一个bean来从Environment中读取一个属性：

```java
@Component
public class EnvironmentConfigBean {

	private Environment environment;

	public EnvironmentConfigBean(@Autowired Environment environment) {
		this.environment = environment;
	}

	public String getColor() {
		return environment.getProperty("application.theme.color");
	}
}
```

如果我们需要添加其他可重新加载的外部属性源，我们首先必须实现我们的自定义PropertySourceFactory：

```java
public class ReloadablePropertySourceFactory extends DefaultPropertySourceFactory {
	@Override
	public PropertySource<?> createPropertySource(String s, EncodedResource encodedResource) throws IOException {
		Resource internal = encodedResource.getResource();
		if (internal instanceof FileSystemResource)
			return new ReloadablePropertySource(s, ((FileSystemResource) internal).getPath());
		if (internal instanceof FileUrlResource)
			return new ReloadablePropertySource(s, ((FileUrlResource) internal)
				.getURL()
				.getPath());
		return super.createPropertySource(s, encodedResource);
	}
}
```

然后我们可以使用@PropertySource来标注组件的类：

```java
@PropertySource(value = "file:path-to-config", factory = ReloadablePropertySourceFactory.class)
```

### 3.2 重新加载属性实例

Environment是比Properties更好的选择，尤其是当我们需要从文件中重新加载属性时。但是，如果我们需要它，我们可以扩展java.util.Properties：

```java
public class ReloadableProperties extends Properties {
	private PropertiesConfiguration propertiesConfiguration;

	public ReloadableProperties(PropertiesConfiguration propertiesConfiguration) throws IOException {
		super.load(new FileReader(propertiesConfiguration.getFile()));
		this.propertiesConfiguration = propertiesConfiguration;
	}

	@Override
	public String getProperty(String key) {
		String val = propertiesConfiguration.getString(key);
		super.setProperty(key, val);
		return val;
	}

	// other overrides
}
```

我们重写了getProperty及其重载，然后将其委托给PropertiesConfiguration实例。现在我们可以创建一个此类的bean，并将其注入到我们的组件中。

### 3.3 使用@ConfigurationProperties重新加载Bean

为了获得与@ConfigurationProperties相同的效果，我们需要重建实例，但是Spring只会创建具有原型或请求作用域的组件的新实例。

因此，我们重新加载环境的技术也适用于它们，但对于单例，我们别无选择，只能实现一个端点来销毁和重新创建bean，或者在bean本身内部处理属性重新加载。

### 3.4 使用@Value重新加载Bean

@Value注解呈现与@ConfigurationProperties相同的[限制]()。

## 4. Actuator和Cloud重新加载属性

[Spring Actuator]()为健康、指标和配置提供了不同的端点，但没有为刷新bean提供任何端点。因此，我们需要Spring Cloud为其添加一个/refresh端点，此端点重新加载Environment的所有属性源，然后发布[EnvironmentChangeEvent](https://static.javadoc.io/org.springframework.cloud/spring-cloud-commons-parent/1.1.9.RELEASE/org/springframework/cloud/context/environment/EnvironmentChangeEvent.html)。

Spring Cloud也引入了[@RefreshScope](https://static.javadoc.io/org.springframework.cloud/spring-cloud-commons-parent/1.1.4.RELEASE/org/springframework/cloud/context/scope/refresh/RefreshScope.html)，我们可以将它用于配置类或者bean，因此，默认作用域将是refresh而不是[singleton]()。

使用refresh作用域，Spring将在EnvironmentChangeEvent上清除这些组件的内部缓存，然后，在下一次访问bean时，将创建一个新实例。

首先我们将spring-boot-starter-actuator依赖添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

```

然后我们导入[spring-cloud-dependencies](https://search.maven.org/search?q=g:org.springframework.cloud a:spring-cloud-dependencies)：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<properties>
    <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
</properties>
```

接下来，我们添加spring-cloud-starter：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter</artifactId>
</dependency>
```

最后，我们将启用刷新端点：

```properties
management.endpoints.web.exposure.include=refresh
```

当我们使用Spring Cloud时，我们可以设置一个[Config Server]()来管理属性，但我们也可以继续我们的外部文件。现在我们可以处理另外两种读取属性的方法：@Value和@ConfigurationProperties。

### 4.1 使用@ConfigurationProperties刷新Bean

让我们演示如何将@ConfigurationProperties与@RefreshScope一起使用：

```java
@Component
@ConfigurationProperties(prefix = "application.theme")
@RefreshScope
public class ConfigurationPropertiesRefreshConfigBean {
	private String color;

	public void setColor(String color) {
		this.color = color;
	}

	// getter and other stuffs
}
```

我们的bean正在从根“application.theme”属性中读取“color”属性，**请注意，根据Spring的文档，我们确实需要setter方法**。

在我们更改外部配置文件中“application.theme.color”的值后，我们可以调用/refresh以便我们可以在下次访问时从bean中获取新值。

### 4.2 使用@Value刷新Bean

让我们创建示例组件：

```java
@Component
@RefreshScope
public class ValueRefreshConfigBean {
	private String color;

	public ValueRefreshConfigBean(@Value("${application.theme.color}") String color) {
		this.color = color;
	}
	// put getter here 
}
```

刷新过程与上述相同，**但是，有必要注意/refresh不适用于具有显式单例作用域的bean**。

## 5. 总结

在本文中，我们学习了如何使用或不使用Spring Cloud功能重新加载属性，并说明了每种技术的缺陷和例外情况。