## 1. 概述

在设计Spring Boot应用程序时，我们通常希望使用[外部配置](https://www.baeldung.com/properties-with-spring)来定义我们的应用程序属性。这让我们可以在不同的环境中使用相同的代码。在某些情况下，我们可能希望跨多个[YAML配置文件](https://www.baeldung.com/spring-yaml)定义我们的属性，即使对于同一环境也是如此。

在本教程中，**我们将学习两种在Spring Boot应用程序中加载多个YAML配置文件的方法**。

## 2. 使用Spring Profile

**在应用程序中包含多个YAML配置文件的一种方法是使用[Spring Profile](https://www.baeldung.com/spring-profiles)**。

这种方法利用了Spring自动加载与应用程序配置文件关联的YAML配置文件的优势。

接下来，我们来看一个包含两个.yml文件的示例。

### 2.1 YAML设置

我们的第一个文件列出了学生。我们将其命名为application-students.yml并将其放在我们的./src/main/resources目录中：

```yaml
students:
    - Jane
    - Michael
```

让我们将第二个文件命名为application-teachers.yml并放在同一个./src/main/resources目录中：

```yaml
teachers:
    - Margo
    - Javier
```

### 2.2 应用程序

现在，让我们设置示例应用程序。我们将在应用程序中使用[CommandLineRunner](https://www.baeldung.com/spring-boot-console-app#console-application)来检查我们的属性加载：

```java
@SpringBootApplication
public class MultipleYamlApplication implements CommandLineRunner {

    @Autowired
    private MultipleYamlConfiguration config;

    public static void main(String[] args) {
        SpringApplication springApp = new SpringApplication(MultipleYamlApplication.class);
        springApp.setAdditionalProfiles("students", "teachers");
        springApp.run(args);
    }

    public void run(String... args) throws Exception {
        System.out.println("Students: " + config.getStudents());
        System.out.println("Teachers: " + config.getTeachers());
    }
}
```

在此示例中，**我们使用setAdditionalProfiles()方法以编程方式设置额外的Spring Profile**。

**我们还可以在通用application.yml文件中使用spring.profiles.include参数**：

```yaml
spring:
    profiles:
        include:
            - teachers
            - students
```

这两种方法都可以设置配置文件，并且在应用程序启动期间，Spring会按照模式application-{profile}.yml加载任何YAML配置文件。

### 2.3 配置

为了完成我们的示例，让我们创建我们的配置类。这将从YAML文件加载属性：

```java
@Configuration
@ConfigurationProperties
public class MultipleYamlConfiguration {

    List<String> teachers;
    List<String> students;

    // standard setters and getters
}
```

让我们在运行我们的应用程序后检查日志：

```shell
c.t.t.p.m.MultipleYamlApplication : The following 2 profiles are active: "teachers", "students"
```

这是输出：

```shell
Students: [Jane, Michael]
Teachers: [Margo, Javier]
```

虽然此方法有效，但**缺点是它以一种很可能不是实现意图的方式使用Spring Profile功能**。

鉴于此，让我们看一下包含多个YAML文件的第二种更强大的方法。

## 3. 使用@PropertySources

我们可以通过[@PropertySources](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/PropertySources.html)注解结合[使用@PropertySource加载YAML](https://www.baeldung.com/spring-yaml-propertysource)来指定多个YAML配置文件。

### 3.1 应用程序

```java
@SpringBootApplication
public class MultipleYamlApplication implements CommandLineRunner {

    @Autowired
    private MultipleYamlConfiguration config;

    public static void main(String[] args) {
        SpringApplication.run(MultipleYamlApplication.class);
    }

    public void run(String... args) throws Exception {
        System.out.println("Students: " + config.getStudents());
        System.out.println("Teachers: " + config.getTeachers());
    }
}
```

请注意，在这个例子中，**我们没有设置Spring Profile**。

### 3.2 配置和PropertySourceFactory

现在，让我们实现我们的配置类：

```java
@Configuration
@ConfigurationProperties
@PropertySources({
      @PropertySource(value = "classpath:application-teachers.yml", factory = MultipleYamlPropertySourceFactory.class),
      @PropertySource(value = "classpath:application-students.yml", factory = MultipleYamlPropertySourceFactory.class)})
public class MultipleYamlConfiguration {

    List<String> teachers;
    List<String> students;

    // standard setters and getters
}
```

**@PropertySources注解包含我们要在应用程序中使用的每个YAML文件的@PropertySource**。factory是一个自定义的PropertySourceFactory，可以加载YAML文件：

```java
public class MultipleYamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) throws IOException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());

        Properties properties = factory.getObject();

        return new PropertiesPropertySource(encodedResource.getResource().getFilename(), properties);
    }
}
```

运行我们的MultipleYamlApplication，我们看到了预期的输出：

```shell
Students: [Jane, Michael]
Teachers: [Margo, Javier]
```

## 4. 总结

在本文中，我们研究了在Spring Boot应用程序中加载多个YAML配置文件的两种可能方法。