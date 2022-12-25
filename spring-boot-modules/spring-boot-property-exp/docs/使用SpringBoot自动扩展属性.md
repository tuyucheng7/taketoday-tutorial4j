## 1. 概述

在本文中，我们将通过Maven和Gradle这两种构建工具来探讨Spring提供的属性扩展机制。

## 2. Maven

### 2.1 默认配置

对于使用spring-boot-starter-parent的Maven项目，不需要额外的配置来使用属性扩展：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.3.3.RELEASE</version>
</parent>
```

现在我们可以使用@...@占位符扩展项目的属性，下面是一个示例，演示了如何将从Maven获取的项目版本保存到我们的属性中：

```properties
expanded.project.version=@project.version@
expanded.project.property=@custom.property@
```

我们只能在与这些模式匹配的配置文件中使用这些扩展：

-   *\*/application\*.yml
-   *\*/application\*.yaml
-   *\*/application\*.properties

### 2.2 手动配置

在不使用spring-boot-starter-parent父级的情况下，我们需要手动配置此过滤和扩展，我们需要将<resources\>标签包含到pom.xml文件的<build\>部分：

```xml
<resources>
	<resource>
		<directory>${basedir}/src/main/resources</directory>
		<filtering>true</filtering>
		<includes>
			<include>**/application*.yml</include>
			<include>**/application*.yaml</include>
			<include>**/application*.properties</include>
		</includes>
	</resource>
</resources>
```

在<plugins\>中：

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-resources-plugin</artifactId>
	<version>2.7</version>
	<configuration>
		<delimiters>
			<delimiter>@</delimiter>
		</delimiters>
		<useDefaultDelimiters>false</useDefaultDelimiters>
	</configuration>
</plugin>
```

在需要使用${variable.name}类型的标准占位符的情况下，我们需要将useDefaultDelimeters设置为true，因此application.properties将如下所示：

```properties
expanded.project.version=${project.version}
expanded.project.property=${custom.property}
```

## 3. Gradle

### 3.1 标准Gradle解决方案

Spring Boot文档中的Gradle[解决方案](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html)并不是100%兼容Maven属性过滤和扩展。

为了允许我们使用属性扩展机制，我们需要将以下代码包含到build.gradle中：

```groovy
processResources {
    expand(project.properties)
}
```

这是一个有限的解决方案，与Maven默认配置有以下区别：

1.  不支持带点的属性(例如user.name)，Gradle将点理解为对象属性分隔符
2.  过滤所有资源文件，而不仅仅是一组特定的配置文件
3.  使用默认的美元符号占位符${...}，因此与标准的Spring占位符冲突

### 3.2 Maven兼容解决方案

为了复制标准的Maven解决方案并使用@...@样式占位符，我们需要将以下代码添加到我们的build.gradle中：

```groovy
import org.apache.tools.ant.filters.ReplaceTokens
processResources {
	with copySpec {
		from 'src/main/resources'
		include '**/application*.yml'
		include '**/application*.yaml'
		include '**/application*.properties'
		project.properties.findAll().each {
			prop ->
				if (prop.value != null) {
					filter(ReplaceTokens, tokens: [ (prop.key): prop.value])
					filter(ReplaceTokens, tokens: [ ('project.' + prop.key): prop.value])
				}
		}
	}
}
```

这将解决项目的所有属性，我们仍然不能在build.gradle中定义带点的属性(例如user.name)，但是现在我们可以使用gradle.properties文件以标准的Java属性格式定义属性，并且它也支持带有点的属性(例如database.url)。

此构建仅过滤项目配置文件而不过滤所有资源，并且它与Maven解决方案100%兼容。

## 4. 总结

在这个快速教程中，我们了解了如何使用Maven和Gradle这两种构建工具自动扩展Spring Boot属性，以及我们如何轻松地从一种方法迁移到另一种方法。