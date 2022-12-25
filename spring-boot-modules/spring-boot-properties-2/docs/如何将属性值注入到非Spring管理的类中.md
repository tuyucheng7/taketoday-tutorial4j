## 1. 概述

根据设计，使用@Repository、@Service、@Controller等注解标注的类由Spring管理，在这些类中注入配置简单自然，而不那么简单的是**将配置传递给不由Spring直接管理的类**。

在这种情况下，我们可以使用基于ClassLoader的配置加载或简单地在另一个bean中实例化我们的类并手动设置所需的参数-这是建议的选项，因为配置条目不需要专门存储在*.properties文件中。

在这篇简短的文章中，我们将讨论使用Java ClassLoader加载*.properties文件以及将[Spring已经加载的配置]()注入非托管类的类中。

## 2. 使用类加载器加载配置

简单地说，*.properties文件是包含一些配置信息的资源文件，我们可以使用Java ClassLoader来代替使用支持自动应用程序配置加载的第三方实现，例如在Spring中实现的实现。

我们将创建一个容器对象，该容器将保存在resourceFileName中定义的属性，为了用配置填充容器，我们将使用ClassLoader。

让我们定义实现包含loadProperties(String resourceFileName)方法的PropertiesLoader类：

```java
public class PropertiesLoader {

	public static Properties loadProperties(String resourceFileName) throws IOException {
		Properties configuration = new Properties();
		InputStream inputStream = PropertiesLoader.class
			.getClassLoader()
			.getResourceAsStream(resourceFileName);
		configuration.load(inputStream);
		inputStream.close();
		return configuration;
	}
}
```

每个Class对象都包含对实例化它的ClassLoader的引用；这是一个主要负责加载类的对象，但在本教程中，我们将使用它来加载资源文件，而不是普通的Java类。ClassLoader正在寻找类路径上的resourceFileName，之后，我们通过getResourceAsStream API将资源文件加载为InputStream。

在上面的例子中，我们定义了一个配置容器，它可以使用load(InputStream) API来解析resourceFileName。

load方法实现对*.properties文件的解析，支持“:”或“=”字符作为分隔符。此外，新行开头使用的“#”或“!”字符都是注释标记，会导致该行被忽略。

最后，让我们从配置文件中读取已定义配置条目的确切值：

```java
String property = configuration.getProperty(key);
```

## 3. 用Spring加载配置

第二种解决方案是利用Spring的Spring特性来处理一些低级的文件加载和处理。

让我们定义一个Initializer来保存初始化自定义类所需的配置，在Bean初始化期间，框架将从*.properties配置文件中加载所有使用@Value注解标注的字段：

```java
@Component
public class Initializer {

	private String someInitialValue;
	private String anotherManagedValue;

	public Initializer(
		@Value("someInitialValue") String someInitialValue,
		@Value("anotherValue") String anotherManagedValue) {

		this.someInitialValue = someInitialValue;
		this.anotherManagedValue = anotherManagedValue;
	}

	public ClassNotManagedBySpring initClass() {
		return new ClassNotManagedBySpring(this.someInitialValue, this.anotherManagedValue);
	}
}
```

Initializer现在可以负责实例化ClassNotManagedBySpring。

现在我们将简单地访问我们的Initializer实例并在其上运行initClass()方法来处理我们自定义ClassNotManagedBySpring的实例化：

```java
ClassNotManagedBySpring classNotManagedBySpring = initializer.initClass();
```

一旦我们有了对Initializer的引用，我们就能够实例化我们的自定义ClassNotManagedBySpring。

## 4. 总结

在这个快速教程中，我们重点介绍了如何将属性读入非Spring Java类。