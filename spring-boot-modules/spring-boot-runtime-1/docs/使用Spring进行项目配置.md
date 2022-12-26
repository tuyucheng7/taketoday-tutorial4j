## 1. 配置必须是环境特定的

配置必须是特定于环境的-这只是生活中的一个事实，如果不是这种情况，那么它就不是配置，我们只会在代码中硬编码值。

**对于Spring应用程序，你可以使用多种解决方案**，从简单的解决方案一直到超级灵活、高度复杂的替代方案。

一种更常见和直接的解决方案是灵活使用属性文件和[Spring提供的一流属性支持]()。

作为概念证明，出于本文的目的，我们将看一下一种特定类型的属性-数据库配置。将一种类型的数据库配置用于生产，另一种用于测试，另一种用于开发环境是非常有意义的。

## 2. 每个环境的.properties文件

让我们开始我们的概念验证-通过定义我们想要定位的环境：

-   开发
-   暂存
-   生产

接下来让我们创建3个属性文件，每个环境对应一个：

-   persistence-dev.properties
-   persistence-staging.properties
-   persistence-production.properties

在典型的Maven应用程序中，这些可以驻留在src/main/resources中，但是无论它们在哪里，在部署应用程序时它们都**需要在类路径上可用**。

一个重要的旁注-**将所有属性文件置于版本控制之下可以使配置更加透明和可重现**，这与将配置放在磁盘上的某个地方并简单地将Spring指向它们是相反的。

## 3. Spring配置

在Spring中，我们将根据环境包含正确的文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	   xmlns:context="http://www.springframework.org/schema/context"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
      http://www.springframework.org/schema/context
      http://www.springframework.org/schema/context/spring-context-4.0.xsd">

	<context:property-placeholder
		location="classpath*:*persistence-${envTarget}.properties"/>
</beans>
```

当然也可以用Java配置来做同样的事情：

```java
@PropertySource({"classpath:persistence-${envTarget:dev}.properties"})
```

这种方法允许为特定的、集中的目的灵活地拥有多个*.properties文件。例如在我们的例子中，持久层Spring配置导入了persistence属性，这是非常有意义的。安全配置将导入与安全相关的属性等。

## 4. 在每个环境中设置属性

最终的可部署war将包含所有属性文件-对于持久层，persistence-*.properties的三个变体。由于这些文件的名称实际上不同，因此不必担心不小心包含了错误的文件。我们将设置envTarget变量，从而从多个现有变体中选择我们想要的实例。

envTarget变量可以在操作系统/环境中设置或作为JVM命令行的参数：

```bash
-DenvTarget=dev
```

## 5. 测试和Maven

对于需要启用持久层的集成测试，我们只需在pom.xml中设置envTarget属性：

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
		<systemPropertyVariables>
			<envTarget>h2_test</envTarget>
		</systemPropertyVariables>
	</configuration>
</plugin>
```

相应的persistence-h2_test.properties文件可以放在src/test/resources中，这样它只会用于测试，而不会在运行时不必要地包含和部署在war中。

## 6. 更进一步

如果需要，可以通过多种方法为该解决方案构建额外的灵活性。

一种这样的方法是**对属性文件的名称使用更复杂的编码**，不仅指定要使用它们的环境，还指定更多信息(例如持久性提供程序)。例如，我们可能会使用以下类型的属性文件：persistence-h2.properties、persistence-mysql.properties或更具体的：persistence-dev_h2.properties、persistence-staging_mysql.properties、persistence-production_amazonRDS.properties。

这种命名约定的优点在于它只是一个约定，因为整体方法没有任何变化-就是透明度。现在，仅通过查看名称就可以更清楚地了解配置的作用：

-   **persistence-dev_h2.properties**：开发环境的持久性提供程序是一个轻量级的内存中H2数据库
-   **persistence-staging_mysql.properties**：暂存环境的持久性提供程序是一个MySQL实例
-   **persistence-production_amazon_rds.propertie**：生产环境的持久性提供程序是Amazon RDS

## 7. 总结

本文讨论了在Spring中进行特定于环境地配置的灵活解决方案，可以在[此处](https://www.javacodegeeks.com/2012/06/spring-31-profiles-and-tomcat.html)找到使用Profile的替代解决方案。