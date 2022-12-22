## 1. 概述

[Groovy]()是一种功能强大的动态JVM语言，具有众多[特性]()。在Spring中使用Groovy可以极大地增强应用程序在灵活性和改进可读性方面的能力。Spring从版本4开始支持基于[Groovy]()的配置。

在本教程中，我们将研究**将Groovy与Spring结合使用的不同方式**。首先，我们将了解如何使用Spring提供的多个选项创建Groovy bean定义。接下来，我们讨论如何使用Groovy脚本引导应用程序上下文。最后，我们了解如何使用XML和GroovyScriptEngine类将Groovy作为脚本执行(无需编译)。

## 2. Maven依赖

首先我们在pom.xml中定义[Groovy依赖项](https://search.maven.org/search?q=g:org.codehaus.groovy a:groovy-all)：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy</artifactId>
    <version>3.0.12</version>
</dependency>
```

此外，我们需要添加[GMavenPlus](https://search.maven.org/search?q=gmavenplus-plugin)插件来编译Groovy文件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.gmavenplus</groupId>
            <artifactId>gmavenplus-plugin</artifactId>
            <version>1.9.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>addSources</goal>
                        <goal>addTestSources</goal>
                        <goal>generateStubs</goal>
                        <goal>compile</goal>
                        <goal>generateTestStubs</goal>
                        <goal>compileTests</goal>
                        <goal>removeStubs</goal>
                        <goal>removeTestStubs</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 3. Bean定义

传统上，开发人员过去常常通过XML配置来声明bean，这种风格后来被通过Java注解以编程方式定义beans所取代，另一种声明bean的方法是通过Groovy脚本。

由于我们使用的是[GMavenPlus]()插件，因此Groovy源文件可以与src/main/java源文件夹中的其他Java代码混合在一起。但是，**最好将Groovy文件放在专用的src/main/groovy源文件夹中，以避免在以后阶段混淆**。

### 3.1 使用Groovy Bean构建器

Groovy Bean Builder是Java的基于@Configuration注解的配置和基于XML的配置的强大替代方法，让我们看一下使用Groovy代码的一些基本bean定义：

```groovy
beans {

    // Declares a simple bean with a constructor argument
    company(Company, name: 'ABC Inc');

    // The same bean can be declared using a simpler syntax: beanName(type, constructor-args) 
    company String, 'ABC Inc'

    // Declares an employee object with setters referencing the previous bean
    employee(Employee) {
        firstName = 'Lakshmi'
        lastName = 'Priya'
        // References to other beans can be done in both the ways
        vendor = company // or vendor = ref('company')
    }
    // Allows import of other configuration files, both XML and Groovy
    importBeans('classpath:ApplicationContext.xml')
    importBeans('classpath:GroovyContext.groovy')
}
```

在这里，包装所有已声明bean的顶级beans构造是一个闭包，[GroovyBeanDefinitionReader](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/beans/factory/groovy/GroovyBeanDefinitionReader.html)将其作为DSL进行处理。

### 3.2 使用注解

或者，Groovy类可以是有效的Spring bean，并且可以使用Groovy代替Java进行基于注解的配置：

```groovy
@Configuration
class SpringGroovyConfiguration{

    @Bean
    List<String> fruits() {
        ['Apple', 'Orange', 'Banana', 'Grapes']
    }

    @Bean
    Map<Integer, String> rankings() {
        [1: 'Gold', 2: 'Silver', 3: 'Bronze']
    }
}
```

### 3.3 使用XML

当然，Groovy Bean Builder和基于注解的配置都更加灵活，但是，我们仍然可以使用XML来声明在Groovy脚本中定义的bean。Groovy是一种[动态语言](https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/dynamic-language.html)，Spring为其提供了全面的支持。因此，**我们需要在XML配置中使用一个特殊元素(<lang:groovy>)来指示我们正在定义动态语言支持的beans**。

例如，让我们看一个引用正确模式的示例 XML 配置，以便lang命名空间中的标签可用：

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:lang="http://www.springframework.org/schema/lang"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/lang https://www.springframework.org/schema/lang/spring-lang.xsd">
    <lang:groovy id="notification" script-source="file:NotificationServiceImpl.groovy" refresh-check-delay="10000" >
        <lang:property name="message" value="Hello" />
    </lang:groovy>
</beans>
```

在这里，我们通过script-source属性声明了引用Groovy脚本的notification bean，我们可以使用文件前缀指定脚本的确切位置。或者，我们可以使用classpath前缀直接从类路径访问资源。refresh-check-delay属性定义了脚本的刷新间隔，当脚本内容发生变化时可以自动刷新。

## 4. 引导应用程序上下文

Spring需要知道如何引导Groovy上下文文件以使beans对应用程序可用，我们可以通过在web.xml中配置它或以编程方式加载上下文来做到这一点。

### 4.1 将Groovy配置添加到web.xml

为了使事情变得更简单，Spring 4.1版本添加了对在[GroovyWebApplicationContext](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/context/support/GroovyWebApplicationContext.html)的帮助下通过web.xml加载Groovy配置文件的支持。

默认情况下，配置将从/WEB-INF/applicationContext.groovy加载。但是，可以通过contextConfigLocation servlet上下文参数覆盖此位置：

```xml
<web-app>
    ...
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <context-param>
        <param-name>contextClass</param-name>
        <param-value>org.springframework.web.context.support.GroovyWebApplicationContext</param-value>
    </context-param>
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>WEB-INF/applicationContext.groovy</param-value>
    </context-param>
    ...
</web-app>
```

### 4.2 使用GenericGroovyApplicationContext 

Spring提供[GenericGroovyApplicationContext](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/GenericGroovyApplicationContext.html)来引导Groovy bean定义。此外，上下文可以使用内联bean定义闭包加载：

```groovy
def context = new GenericGroovyApplicationContext()
context.reader.beans {
    department(Department) {
        name = 'Finance'
        floor = 3
    }
}
context.refresh()
```

**或者，我们可以外部化这个bean定义并从Groovy配置文件加载应用程序上下文**：

```groovy
GenericGroovyApplicationContext context = new GenericGroovyApplicationContext()
context.load("config/applicationContext.groovy")
context.refresh()
```

正如我们所看到的，加载Groovy上下文类似于[XmlWebApplicationContext]()或[ClassPathXmlApplicationContext]()的Java风格实例化。

在没有额外配置的情况下，代码可以更简洁：

```groovy
ApplicationContext context = new GenericGroovyApplicationContext("config/applicationContext.groovy")
String foo = context.getBean("foo", String.class)
```

此外，**GenericGroovyApplicationContext还可以理解XML bean定义文件，通过允许与Groovy bean定义文件无缝混合和匹配，这增加了更多的灵活性**。 

## 5. 执行Groovy脚本

除了Groovy bean定义之外，Spring还支持执行Groovy脚本，无需编译。这种执行可以作为一个独立的bean，也可以通过在bean中调用Groovy脚本，使脚本成为它的可执行部分。

### 5.1 作为内联脚本

正如我们之前看到的，我们可以使用Spring提供的动态语言支持将Groovy源文件直接嵌入到Spring bean定义中。因此，我们可以利用<lang:inline-script/>元素直接在Spring配置XML文件中定义Groovy源代码。

例如，我们可以使用内联脚本功能创建一个Notifier bean：

```xml
<lang:groovy id="notifier">
	<lang:inline-script>

		package cn.tuyucheng.taketoday.springgroovyconfig;

		import cn.tuyucheng.taketoday.springgroovyconfig.NotificationService;

		class Notifier implements NotificationService {
			String message
		}

	</lang:inline-script>
	<lang:property name="message" value="Have a nice day!"/>
</lang:groovy>
```

### 5.2 使用GroovyScriptEngine

或者，我们可以使用GroovyScriptEngine类来执行Groovy脚本，**GroovyScriptEngine由Groovy本身提供，使用它不依赖于Spring**。

**此类支持在发生更改时自动重新加载脚本**。此外，它还加载所有依赖于它的类。

有两种方法可以执行脚本，在第一种方法中，我们得到一个GroovyObject并通过调用invokeMethod()来执行脚本：

```groovy
GroovyScriptEngine engine = new GroovyScriptEngine(ResourceUtils.getFile("file:src/main/resources/")
.getAbsolutePath(), this.getClass().getClassLoader());
Class<GroovyObject> joinerClass = engine.loadScriptByName("StringJoiner.groovy")
GroovyObject joiner = joinerClass.newInstance()

Object result = joiner.invokeMethod("join", new Object[]{"Mr.", "Bob"})
assertEquals("Mr.Bob", result.toString())
```

在第二种方法中，我们可以直接调用Groovy脚本，我们使用Binding类将变量传递给Groovy脚本：

```java
Binding binding = new Binding();
binding.setVariable("arg1", "Mr.");
binding.setVariable("arg2", "Bob");
Object result = engine.run("StringJoinerScript.groovy", binding); 
assertEquals("Mr.Bob", result.toString());
```

## 6. 总结

Spring提供了许多选项来集成Groovy，除了脚本功能之外，在Spring应用程序中使用动态语言(例如Groovy)也非常强大，Spring的适应性和Groovy的灵活性给了我们一个绝妙的结合。

在本文中，我们了解了Spring框架如何为Groovy提供广泛的支持，以便我们可以使用不同的方法来定义有效的bean。此外，我们还了解了如何将Groovy脚本引导到有效的Spring bean中。最后，我们讨论了如何动态调用Groovy脚本。