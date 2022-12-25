## 1. 简介

通常，我们项目的构建配置包含大量关于我们应用程序的信息，应用程序本身可能需要其中的一些信息。因此，我们可以从现有的构建配置中使用它，而不是硬编码这些信息。

在本文中，我们将了解**如何在Spring Boot应用程序中使用来自项目构建配置的信息**。

## 2. 构建信息

假设我们要在我们网站的主页上显示应用程序描述和版本。

通常，此信息存在于pom.xml中：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<artifactId>spring-boot</artifactId>
	<name>spring-boot</name>
	<packaging>war</packaging>
	<description>This is simple boot application for Spring boot actuator test</description>
	<version>1.0.0</version>
	<!--...-->
</project>
```

## 3. 引用应用程序属性文件中的信息

现在，要在我们的应用程序中使用上述信息，我们必须首先在我们的应用程序属性文件之一中引用它：

```properties
application-description=@project.description@
application-version=@project.version@
```

在这里，我们使用构建属性project.description的值来设置应用程序属性application-description。同样，应用程序版本是使用project.version设置的。

**这里最重要的一点是在属性名称周围使用@字符，这告诉Spring从Maven项目扩展命名属性**。

现在，当我们构建项目时，这些属性将被替换为它们在pom.xml中的值。

这种扩展也称为资源过滤，**值得注意的是，这种过滤仅适用于生产配置**。因此，我们不能在src/test/resources下的文件中使用构建属性。

另一件需要注意的事情是，如果我们使用addResources标志，spring-boot:run目标会将src/main/resources直接添加到类路径中，尽管这对于热重载很有用，但它绕过了资源过滤，因此也绕过了此功能。

现在，**只有当我们使用spring-boot-starter-parent时，上述属性扩展才能开箱即用**。

### 3.1 在没有spring-boot-starter-parent的情况下扩展属性

让我们看看如何在不使用spring-boot-starter-parent依赖项的情况下启用此功能。

首先，我们必须在pom.xml的<build/\>元素内启用资源过滤：

```xml
<resources>
	<resource>
		<directory>src/main/resources</directory>
		<filtering>true</filtering>
	</resource>
</resources>
```

在这里，我们仅在src/main/resources下启用了资源过滤。

然后，我们可以为maven-resources-plugin添加分隔符配置：

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-resources-plugin</artifactId>
	<configuration>
		<delimiters>
			<delimiter>@</delimiter>
		</delimiters>
		<useDefaultDelimiters>false</useDefaultDelimiters>
	</configuration>
</plugin>
```

请注意，我们已经将useDefaultDelimiters属性指定为false，这可确保标准的Spring占位符如(${placeholder})不会被构建扩展。

## 4. 使用YAML文件中的构建信息

**如果我们使用YAML来存储应用程序属性，我们可能无法使用@来指定构建属性，这是因为@是YAML中的保留字符**。

但是，我们可以通过**在maven-resources-plugin中配置不同的分隔符**来克服这个问题：

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-resources-plugin</artifactId>
	<configuration>
		<delimiters>
			<delimiter>^</delimiter>
		</delimiters>
		<useDefaultDelimiters>false</useDefaultDelimiters>
	</configuration>
</plugin>
```

或者，只需**覆盖pom.xml的properties标签中的resource.delimiter属性**：

```xml
<properties>
    <resource.delimiter>^</resource.delimiter>
</properties>
```

然后，我们可以在我们的YAML文件中使用^：

```yaml
application-description: ^project.description^
application-version: ^project.version^
```

## 5. 总结

在本文中，我们了解了如何在我们的应用程序中使用Maven项目信息，这可以帮助我们避免在应用程序属性文件中对项目构建配置中已经存在的信息进行硬编码。