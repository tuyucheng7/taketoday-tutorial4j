## 1. 概述

属性是Spring Boot提供的最有用的机制之一，它们可以从不同的地方提供，例如专用属性文件、环境变量等。因此，查找和记录特定属性有时很有用，例如在调试时。

在这个简短的教程中，我们将介绍几种在Spring Boot应用程序中查找和记录属性的不同方法。首先，我们将创建一个我们将使用的简单测试应用程序。然后，我们将尝试三种不同的方式来记录特定的属性。

## 2. 创建测试应用程序

让我们创建一个具有3个自定义属性的简单应用程序。

我们可以使用[Spring Initializr](https://start.spring.io/)创建Spring Boot应用程序模板，我们使用Java作为语言，其他的选项可以自由选择，例如Java版本、项目元数据等。

下一步是将自定义属性添加到我们的应用程序，我们会将这些属性添加到src/main/resources中的新application.properties文件中：

```properties
app.name=MyApp
app.description=${app.name} is a Spring Boot application
tuyucheng.property=stagingValue
```

## 3. 使用上下文刷新事件记录属性

在Spring Boot应用程序中记录属性的第一种方法是使用[Spring Events]()，尤其是org.springframework.context.event.ContextRefreshedEvent类和相应的EventListener，我们将演示如何记录所有可用的属性以及仅从特定文件打印属性的更详细版本。

### 3.1 记录所有属性

让我们从创建一个bean和事件监听器方法开始：

```java
@Component
public class AppContextRefreshedEventPropertiesPrinter {

	@EventListener
	public void handleContextRefreshed(ContextRefreshedEvent event) {
		// event handling logic
	}
}
```

我们使用org.springframework.context.event.EventListener注解来标注事件监听器方法，当ContextRefreshedEvent事件发生时，Spring调用带注解的方法。

下一步是从触发的事件中获取org.springframework.core.env.ConfigurableEnvironment接口的实例，**ConfigurableEnvironment接口提供了一个有用的方法getPropertySources()，我们将使用它来获取所有属性源的列表，例如环境、JVM或属性文件变量**：

```java
ConfigurableEnvironment env = (ConfigurableEnvironment) event.getApplicationContext().getEnvironment();
```

现在让我们看看如何使用它来打印所有属性，不仅是来自application.properties文件的属性，还来自环境、JVM变量等等：

```java
env.getPropertySources()
	.stream()
    .filter(ps -> ps instanceof MapPropertySource)
    .map(ps -> ((MapPropertySource) ps).getSource().keySet())
    .flatMap(Collection::stream)
    .distinct()
    .sorted()
    .forEach(key -> LOGGER.info("{}={}", key, env.getProperty(key)));
```

首先，我们从可用的属性源创建一个[Stream]()，然后，我们使用它的filter()方法迭代作为org.springframework.core.env.MapPropertySource类实例的属性源。

顾名思义，该属性源中的属性存储在Map结构中，我们在下一步中使用它，其中我们使用流的map()方法来获取属性键集合。

接下来，我们使用Stream的flatMap()方法，因为我们想要迭代单个属性键，而不是一组键，并且我们希望按字母顺序打印唯一的、不重复的属性。

最后一步是记录属性键及其值。

当我们启动应用程序时，我们应该会看到从各种源获取的大量属性列表：

```properties
COMMAND_MODE=unix2003
CONSOLE_LOG_CHARSET=UTF-8
#...
tuyucheng.property=defaultValue
app.name=MyApp
app.description=MyApp is a Spring Boot application
#...
java.class.version=52.0
ava.runtime.name=OpenJDK Runtime Environment
```

### 3.2 仅从application.properties文件记录属性

如果我们想记录仅在application.properties文件中找到的属性，我们可以重用之前的几乎所有代码，只需要更改传递给filter()方法的lambda函数：

```java
env.getPropertySources()
	.stream()
    .filter(ps -> ps instanceof MapPropertySource && ps.getName().contains("application.properties"))
    // ...
```

现在，当我们启动应用程序时，我们应该会看到以下日志：

```bash
tuyucheng.property=defaultValue
app.name=MyApp
app.description=MyApp is a Spring Boot application
```

## 4. 使用Environment接口记录属性

记录属性的另一种方法是使用org.springframework.core.env.Environment接口：

```java
@Component
public class EnvironmentPropertiesPrinter {
	@Autowired
	private Environment env;

	@PostConstruct
	public void logApplicationProperties() {
		LOGGER.info("{}={}", "tuyucheng.property", env.getProperty("tuyucheng.property"));
		LOGGER.info("{}={}", "app.name", env.getProperty("app.name"));
		LOGGER.info("{}={}", "app.description", env.getProperty("app.description"));
	}
}
```

与上下文刷新事件方法相比，**唯一的限制是我们需要知道属性名称才能获取其值**，Environment接口不提供列出所有属性的方法。另一方面，它绝对是一种更短、更容易的技术。

当我们启动应用程序时，我们应该看到与之前相同的输出：

```bash
tuyucheng.property=defaultValue 
app.name=MyApp 
app.description=MyApp is a Spring Boot application
```

## 5. 使用Spring Actuator记录属性

[Spring Actuator]()是一个非常有用的库，它为我们的应用程序带来了生产就绪的特性，**其中/env REST端点可以返回当前环境属性**。

首先，让我们将[Spring Actuator库](https://search.maven.org/artifact/org.springframework.boot/spring-boot-starter-actuator/2.7.3/jar)添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.7.3</version>
</dependency>
```

接下来，我们需要启用/env端点，因为默认情况下它是禁用的，因此我们修改application.properties并添加以下属性：

```properties
management.endpoints.web.exposure.include=env
```

现在，我们所要做的就是启动应用程序并访问/env端点，在我们的例子中，URL地址是http://localhost:8080/actuator/env，然后我们应该看到一个包含所有环境变量的大型JSON响应，包括我们的属性：

```json
{
	"activeProfiles": [],
	"propertySources": [
		...
		{
			"name": "Config resource 'class path resource [application.properties]' via location 'optional:classpath:/' (document #0)",
			"properties": {
				"app.name": {
					"value": "MyApp",
					"origin": "class path resource [application.properties] - 10:10"
				},
				"app.description": {
					"value": "MyApp is a Spring Boot application",
					"origin": "class path resource [application.properties] - 11:17"
				},
				"tuyucheng.property": {
					"value": "defaultValue",
					"origin": "class path resource [application.properties] - 13:15"
				}
			}
		}
		...
	]
}
```

## 6. 总结

在本文中，我们学习了如何在Spring Boot应用程序中记录属性。首先，我们创建了一个具有3个自定义属性的测试应用程序；然后，我们演示了三种不同的方法来检索和记录所需的属性。