## 1. 概述

核心[Spring Boot]()开发人员为大多数流行的开源项目提供了[Starter]()，但我们不仅限于这些。

**我们也可以编写自己的自定义Starter**，如果我们有一个内部库供组织内部使用，那么如果要在Spring Boot上下文中使用它，那么最好也为它编写一个Starter。

这些Starter使开发人员能够避免冗长的配置并快速启动他们的开发。然而，由于后台发生了很多事情，有时很难理解注解或仅在pom.xml中包含依赖项如何启用如此多的功能。

在本文中，我们将揭开Spring Boot魔法的神秘面纱，看看幕后发生了什么，然后我们将使用这些概念为我们自己的自定义库创建一个Starter。

## 2. 揭秘Spring Boot的自动配置

### 2.1 自动配置类

当Spring Boot启动时，它会在类路径中查找名为spring.factories的文件，该文件位于META-INF目录中。让我们看一下[来自spring-boot-autoconfigure](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/resources/META-INF/spring.factories)项目的这个文件的片段：

```properties
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,
org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

该文件将名称映射到Spring Boot将尝试运行的不同配置类。因此，根据这些内容，Spring Boot将尝试运行RabbitMQ、Cassandra、MongoDB和Hibernate的所有配置类。

这些类是否实际运行将取决于类路径上是否存在依赖类。例如，如果在类路径中找到MongoDB的类，[MongoAutoConfiguration](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/mongo/MongoAutoConfiguration.java)将运行，所有与mongo相关的bean都将被初始化。

此条件初始化由[@ConditionalOnClass](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/condition/ConditionalOnClass.html)注解启用，让我们看一下MongoAutoConfiguration类的代码片段，看看它的用法：

```java
@Configuration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDbFactory")
public class MongoAutoConfiguration {
    // configuration code
}
```

现在如何-如果[MongoClient](https://mongodb.github.io/mongo-java-driver/)在类路径中可用，这个配置类将运行并使用默认配置设置初始化的MongoClient填充Spring bean工厂。

### 2.2 来自application.properties文件的自定义属性

Spring Boot使用一些预先配置的默认值来初始化bean，为了覆盖这些默认值，我们通常在application.properties文件中使用一些特定的名称声明它们，这些属性由Spring Boot容器自动获取。

让我们看看它是如何工作的。

在MongoAutoConfiguration的代码片段中，@EnableConfigurationProperties注解是使用[MongoProperties](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/mongo/MongoProperties.java)类声明的，该类充当自定义属性的容器：

```java
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoProperties {

    private String host;

    // other fields with standard getters and setters
}
```

前缀加上字段名称构成了application.properties文件中的属性名称。因此，要为MongoDB设置主机，我们只需要在属性文件中写入以下内容：

```properties
spring.data.mongodb.host=localhost
```

同样，可以使用属性文件设置类中其他字段的值。

## 3. 创建自定义Starter

基于第2节中的概念，要创建自定义Starter，我们需要编写以下组件：

1.  我们库的自动配置类以及用于自定义配置的属性类。
2.  一个Starter pom，用于引入库和自动配置项目的依赖项。

为了演示，我们创建了一个简单的问候语库，它将接收一天中不同时间的问候语消息作为配置参数并输出问候消息。我们还将创建一个示例Spring Boot应用程序来演示我们的自动配置和启动模块的用法。

### 3.1 自动配置模块

我们将自动配置模块命名为greeter-spring-boot-autoconfigure，该模块将有两个主要类，即GreeterProperties，它允许通过application.properties文件设置自定义属性，以及GreeterAutoConfiguration将为greeter库创建bean。

让我们看一下这两个类的代码：

```java
@ConfigurationProperties(prefix = "tuyucheng.greeter")
public class GreeterProperties {

    private String userName;
    private String morningMessage;
    private String afternoonMessage;
    private String eveningMessage;
    private String nightMessage;

    // standard getters and setters
}
```

```java
@Configuration
@ConditionalOnClass(Greeter.class)
@EnableConfigurationProperties(GreeterProperties.class)
public class GreeterAutoConfiguration {

    @Autowired
    private GreeterProperties greeterProperties;

    @Bean
    @ConditionalOnMissingBean
    public GreetingConfig greeterConfig() {

        String userName = greeterProperties.getUserName() == null
              ? System.getProperty("user.name")
              : greeterProperties.getUserName();

        // ..

        GreetingConfig greetingConfig = new GreetingConfig();
        greetingConfig.put(USER_NAME, userName);
        // ...
        return greetingConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public Greeter greeter(GreetingConfig greetingConfig) {
        return new Greeter(greetingConfig);
    }
}
```

我们还需要在src/main/resources/META-INF目录下添加一个spring.factories文件，内容如下：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=
  cn.tuyucheng.taketoday.greeter.autoconfigure.GreeterAutoConfiguration
```

在应用程序启动时，如果类路径中存在类Greeter，则GreeterAutoConfiguration类将运行。如果成功运行，它将通过GreeterProperties类读取属性，从而使用GreeterConfig和Greeter bean填充Spring应用程序上下文。

[@ConditionalOnMissingBean](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/condition/ConditionalOnMissingBean.html)注解将确保这些bean仅在它们不存在时才被创建，这使开发人员能够通过在其中一个@Configuration类中定义自己的bean来完全覆盖自动配置的bean。

### 3.2 创建pom.xml

现在让我们创建starter pom，它将引入自动配置模块和greeter库的依赖项。

根据命名约定，所有不由核心Spring Boot团队管理的Starter都应以库名称开头，后跟后缀-spring-boot-starter，因此我们将Starter称为greeter-spring-boot-starter：

```xml
<project ...>
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>greeter-spring-boot-starter</artifactId>
    <version>1.0.0</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <greeter.version>1.0.0</greeter.version>
        <spring-boot.version>2.2.6.RELEASE</spring-boot.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>cn.tuyucheng.taketoday</groupId>
            <artifactId>greeter-spring-boot-autoconfigure</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>cn.tuyucheng.taketoday</groupId>
            <artifactId>greeter</artifactId>
            <version>${greeter.version}</version>
        </dependency>
    </dependencies>
</project>
```

### 3.3 使用Starter

接下来我们创建一个使用Starter模块的greeter-spring-boot-sample-app，在pom.xml中，我们需要将其添加为依赖项：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>greeter-spring-boot-starter</artifactId>
    <version>${greeter-starter.version}</version>
</dependency>
```

Spring Boot将自动配置所有内容，我们将有一个准备好注入和使用的Greeter bean。

我们还通过在application.properties文件中使用tuyucheng.greeter前缀定义它们来更改GreeterProperties的一些默认值：

```properties
tuyucheng.greeter.userName=Tuyucheng
tuyucheng.greeter.afternoonMessage=Woha Afternoon
```

最后，在我们的应用程序中使用Greeter bean：

```java
@SpringBootApplication
public class GreeterSampleApplication implements CommandLineRunner {

    @Autowired
    private Greeter greeter;

    public static void main(String[] args) {
        SpringApplication.run(GreeterSampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String message = greeter.greet();
        System.out.println(message);
    }
}
```

## 4. 总结

在本快速教程中，我们重点演示了如何创建自定义Spring Boot Starter，以及这些Starter如何与自动配置机制一起在后台工作以消除大量手动配置。