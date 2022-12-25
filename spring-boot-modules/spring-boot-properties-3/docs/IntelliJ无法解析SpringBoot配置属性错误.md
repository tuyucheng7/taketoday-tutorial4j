## 1. 概述

当我们在Spring应用程序中注入运行时属性时，我们可以[为自定义属性组定义bean类]()。

IntelliJ为内置属性bean提供帮助和自动完成,但是，它需要一些帮助才能为自定义属性提供这些。

在这个简短的教程中，我们将了解如何向IntelliJ公开这些属性，以使开发过程更容易。

## 2. 自定义属性

让我们看一下IntelliJ可以为我们提供的有关应用程序属性的编码帮助：

<img src="../assets/img.png">

在这里，属性url和timeout-in-milliseconds都是自定义属性，我们可以看到描述、类型和可选的默认值。

但是，如果属性未知，IntelliJ会向我们显示警告：

<img src="../assets/img_1.png">

这是因为，**如果没有元数据，IntelliJ无法帮助我们**。

现在让我们看一下我们必须做些什么来解决这个问题。

## 3. 依赖关系

首先，我们需要将[spring-boot-configuration-processor](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-configuration-processor)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

每次我们构建项目时都会调用spring -boot-configuration-processor，它将在target/classes/META-INF/中创建元数据文件。 

依赖被标记为可选的，这意味着当有人使用我们的项目作为依赖时它不会被继承。

接下来，我们将看到spring-boot-configuration-processor在哪里获取用于创建元数据的信息。

## 4. 配置元数据@ConfigurationProperties

我们在一个用@ConfigurationProperties注解标注的类中定义我们的属性：

```java
@Configuration
@ConfigurationProperties(prefix = "cn.tuyucheng.taketoday")
public class CustomProperties {

	/**
	 * The url to connect to.
	 */
	String url;

	/**
	 * The time to wait for the connection.
	 */
	private int timeoutInMilliSeconds = 1000;

	// Getters and Setters
}
```

在这里，该类包含属性名称、它们的类型以及初始化列表中提供的任何默认值。此外，Javadoc还提供了每个属性的描述。

**在构建过程中，注解处理器搜索所有使用@ConfigurationProperties注解的类，它为类的每个实例变量生成自定义属性元数据**。

## 5. 配置元数据文件

### 5.1 元数据文件的格式

描述自定义属性的[元数据文件]()驱动IntelliJ中的上下文帮助，例如：

```json
{
	"groups": [
		{
			"name": "cn.tuyucheng.taketoday",
			"type": "cn.tuyucheng.taketoday.configuration.processor.CustomProperties",
			"sourceType": "cn.tuyucheng.taketoday.configuration.processor.CustomProperties"
		}
	],
	"properties": [
		{
			"name": "cn.tuyucheng.taketoday.url",
			"type": "java.lang.String",
			"description": "The url to connect to.",
			"sourceType": "cn.tuyucheng.taketoday.configuration.processor.CustomProperties"
		},
		{
			"name": "cn.tuyucheng.taketoday.timeout-in-milli-seconds",
			"type": "java.lang.Integer",
			"description": "The time to wait for the connection.",
			"sourceType": "cn.tuyucheng.taketoday.configuration.processor.CustomProperties",
			"defaultValue": 1000
		}
	],
	"hints": []
}
```

由于注解处理器从我们的代码中为我们生成了这个文件，所以**不需要直接查看或编辑这个文件**。

### 5.2 没有ConfigurationProperties Bean的元数据

如果我们有未由@ConfigurationProperties引入的现有属性，但仍需要它们的元数据文件，那么IntelliJ可以提供帮助。

让我们仔细看看之前的警告信息：

<img src="../assets/img_2.png">

在这里我们看到一个Define configuration key选项，我们可以使用它来创建一个additional-spring-configuration-metadata.json文件，创建的文件将如下所示：

```json
{
	"properties": [
		{
			"name": "cn.tuyucheng.taketoday.timeoutInMilliSeconds",
			"type": "java.lang.String",
			"description": "Description for cn.tuyucheng.taketoday.timeoutInMilliSeconds."
		}
	]
}
```

由于没有来自其他任何地方的关于该属性的信息，因此**我们必须手动编辑其中的元数据**，默认类型始终是字符串。

让我们在文件中添加一些额外的信息：

```json
{
	"properties": [
		{
			"name": "cn.tuyucheng.taketoday.timeout-in-milli-seconds",
			"type": "java.lang.Integer",
			"description": "The time to wait for the connection.",
			"sourceType": "cn.tuyucheng.taketoday.configuration.processor.CustomProperties",
			"defaultValue": 1000
		}
	]
}
```

**请注意，我们需要重新构建项目才能看到新属性出现在Intellij的自动完成提示窗口中**。

此外，我们应该注意，生成此元数据文件的选项也可以通过IntelliJ的Alt+ENTER快捷方式在未知属性上使用。

## 6. 总结

在本文中，我们了解了IntelliJ如何使用配置属性元数据来为我们的属性文件提供帮助。

我们看到了如何使用Spring的注解处理器从自定义类生成元数据。然后，我们看到了如何使用IntelliJ中的快捷方式来创建需要手动编辑的元数据文件。