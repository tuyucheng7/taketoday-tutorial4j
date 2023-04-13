## 1. 简介

Spring Boot具有许多有用的特性，包括**外部化配置和对属性文件中定义的属性的轻松访问**，前面的[教程](https://www.baeldung.com/properties-with-spring)描述了完成此操作的各种方法。

我们现在将更详细地探讨@ConfigurationProperties注解。

## 延伸阅读

### [Spring @Value快速指南](https://www.baeldung.com/spring-value-annotation)

学习使用Spring @Value注解来配置来自属性文件、系统属性等的字段。

[阅读更多](https://www.baeldung.com/spring-value-annotation)→

### [Spring和Spring Boot的属性](https://www.baeldung.com/properties-with-spring)

有关如何在Spring中使用属性文件和属性值的教程。

[阅读更多](https://www.baeldung.com/properties-with-spring)→

## 2. 设置

本教程使用相当标准的设置。我们首先在pom.xml中添加[spring-boot-starter-parent](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.5)作为父级：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.0.0</version>
    <relativePath/>
</parent>
```

为了能够验证文件中定义的属性，我们还需要JSR-303的实现，而[hibernate-validator](https://central.sonatype.com/artifact/org.hibernate.validator/hibernate-validator/8.0.0.Final)就是其中之一，由spring-boot-starter-validation依赖项提供。

让我们也将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

[Hibernate验证器入门](http://hibernate.org/validator/documentation/getting-started/)一文提供了更多详细信息。

## 3. 简单属性

**官方文档建议我们将配置属性隔离到单独的POJO中**。

因此，让我们从这样做开始：

```java
@Configuration
@ConfigurationProperties(prefix = "mail")
public class ConfigProperties {

    private String hostName;
    private int port;
    private String from;

    // standard getters and setters
}
```

我们使用@Configuration以便Spring在应用程序上下文中创建一个Spring bean。

**@ConfigurationProperties最适合所有具有相同前缀的分层属性**；因此，我们添加一个mail前缀。

Spring框架使用标准的Java bean setter，因此我们必须为每个属性声明setter方法。

注意：如果我们在POJO中不使用@Configuration，那么我们需要在Spring应用程序主类中添加@EnableConfigurationProperties(ConfigProperties.class)来将属性绑定到POJO中：

```java
@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class EnableConfigurationDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(EnableConfigurationDemoApplication.class, args);
	}
}
```

就是这样！**Spring将自动绑定在我们的属性文件中定义的任何属性，这些属性具有前缀mail并且与ConfigProperties类中的字段之一同名**。

Spring使用一些宽松的规则来绑定属性，因此，以下变体都绑定到属性hostName：

```properties
mail.hostName
mail.hostname
mail.host_name
mail.host-name
mail.HOST_NAME
```

因此，我们可以使用以下属性文件来设置所有字段：

```properties
#Simple properties
mail.hostname=host@mail.com
mail.port=9000
mail.from=mailer@mail.com
```

### 3.1 Spring Boot 2.2

**从[Spring Boot 2.2](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.2-Release-Notes#configurationproperties-scanning)开始，Spring通过类路径扫描查找并注册@ConfigurationProperties类**。扫描@ConfigurationProperties需要通过添加**@ConfigurationPropertiesScan**注解来明确选择加入。因此，**我们不必使用@Component(和其他元注解，如@Configuration)来标注这些类，甚至不必使用@EnableConfigurationProperties**：

```java
@ConfigurationProperties(prefix = "mail")
@ConfigurationPropertiesScan
public class ConfigProperties {

	private String hostName;
	private int port;
	private String from;

	// standard getters and setters 
}
```

@SpringBootApplication启用的类路径扫描程序会找到ConfigProperties类，即使我们没有用@Component标注这个类。

**此外，我们可以使用[@ConfigurationPropertiesScan](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/api/org/springframework/boot/context/properties/ConfigurationPropertiesScan.html)注解来扫描配置属性类的自定义位置**：

```java
@SpringBootApplication
@ConfigurationPropertiesScan("cn.tuyucheng.taketoday.configurationproperties")
public class EnableConfigurationDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnableConfigurationDemoApplication.class, args);
	}
}
```

这样Spring将只在cn.tuyucheng.taketoday.properties包中查找配置属性类。

## 4. 嵌套属性

**我们可以在List、Map和类中嵌套属性**。

让我们创建一个新的Credentials类以用于一些嵌套属性：

```java
public class Credentials {
	private String authMethod;
	private String username;
	private String password;

	// standard getters and setters
}
```

我们还需要更新ConfigProperties类以使用List、Map和Credentials类：

```java
public class ConfigProperties {

	private String host;
	private int port;
	private String from;
	private List<String> defaultRecipients;
	private Map<String, String> additionalHeaders;
	private Credentials credentials;

	// standard getters and setters
}
```

以下属性文件将设置所有字段：

```properties
#Simple properties
mail.hostname=mailer@mail.com
mail.port=9000
mail.from=mailer@mail.com

#List properties
mail.defaultRecipients[0]=admin@mail.com
mail.defaultRecipients[1]=owner@mail.com

#Map Properties
mail.additionalHeaders.redelivery=true
mail.additionalHeaders.secure=true

#Object properties
mail.credentials.username=john
mail.credentials.password=password
mail.credentials.authMethod=SHA1
```

## 5. 在@Bean方法上使用@ConfigurationProperties

**我们还可以在@Bean标注的方法上使用@ConfigurationProperties注解**。

当我们想要将属性绑定到我们无法控制的第三方组件(外部库)时，这种方法可能特别有用。

让我们创建一个简单的Item类，我们将在下一个示例中使用该类：

```java
public class Item {
	private String name;
	private int size;

	// standard getters and setters
}
```

现在让我们看看如何在@Bean方法上使用@ConfigurationProperties将外部化属性绑定到Item实例：

```java
@Configuration
public class ConfigProperties {

	@Bean
	@ConfigurationProperties(prefix = "item")
	public Item item() {
		return new Item();
	}
}
```

因此，任何以item为前缀的属性都将映射到由Spring上下文管理的Item实例。

## 6. 属性验证

**@ConfigurationProperties使用JSR-303格式提供属性验证**，从而允许我们编写干净的校验代码。

例如，让我们使hostName属性成为必需的：

```java
@NotBlank
private String hostName;
```

接下来，将authMethod属性的长度限制为1到4个字符：

```java
@Length(max = 4, min = 1)
private String authMethod;
```

然后将port属性的值限制为从1025到65536：

```java
@Min(1025)
@Max(65536)
private int port;
```

最后，from属性必须匹配电子邮件地址格式：

```java
@Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$")
private String from;
```

这有助于我们减少代码中的大量if–else条件，并使其看起来更加简洁明了。

**如果这些验证中的任何一个失败，那么主应用程序将无法正常启动，并以IllegalStateException异常结束**。

Hibernate验证框架使用标准的Java bean getter和setter，因此为我们的每个属性声明getters和setter非常重要。

## 7. 属性转换

@ConfigurationProperties支持将属性绑定到其对应的bean的多种类型的转换。

### 7.1 Duration

我们将首先了解如何将属性转换为Duration对象。

这里我们有两个Duration类型的字段：

```java
@ConfigurationProperties(prefix = "conversion")
public class PropertyConversion {

	private Duration timeInDefaultUnit;
	private Duration timeInNano;
	// ...
}
```

这是我们的属性文件：

```properties
conversion.timeInDefaultUnit=10
conversion.timeInNano=9ns
```

因此，字段timeInDefaultUnit的值为10毫秒，而timeInNano的值为9纳秒。

**支持的单位是ns、us、ms、s、m、h和d，分别表示纳秒、微秒、毫秒、秒、分钟、小时和天**。

默认单位是毫秒，这意味着如果我们没有在数值旁边指定单位，Spring会将该值转换为毫秒。

我们还可以使用@DurationUnit覆盖默认单位：

```java
@DurationUnit(ChronoUnit.DAYS)
private Duration timeInDays;
```

这是相应的属性：

```properties
conversion.timeInDays=2
```

### 7.2 DataSize

**同样，Spring Boot @ConfigurationProperties支持DataSize类型转换**。

让我们添加三个数据类型为DataSize的字段：

```java
private DataSize sizeInDefaultUnit;

private DataSize sizeInGB;

@DataSizeUnit(DataUnit.TERABYTES)
private DataSize sizeInTB;
```

这些是相应的属性：

```properties
conversion.sizeInDefaultUnit=300
conversion.sizeInGB=2GB
conversion.sizeInTB=4
```

**在这种情况下，sizeInDefaultUnit值将为300字节，因为默认单位是字节**。

支持的单位有B、KB、MB、GB和TB，我们还可以使用@DataSizeUnit覆盖默认单位。

### 7.3 自定义转换器

我们还可以添加自己的自定义Converter以支持将属性转换为特定的类类型。

让我们添加一个简单的类Employee：

```java
public class Employee {
	private String name;
	private double salary;
}
```

然后我们将创建一个自定义转换器来转换此属性：

```properties
conversion.employee=john,2000
```

我们将其转换为Employee类型的对象：

```java
private Employee employee;
```

我们需要实现Converter接口，然后**使用@ConfigurationPropertiesBinding注解来注册我们的自定义转换器**：

```java
@Component
@ConfigurationPropertiesBinding
public class EmployeeConverter implements Converter<String, Employee> {

    @Override
    public Employee convert(String from) {
        String[] data = from.split(",");
        return new Employee(data[0], Double.parseDouble(data[1]));
    }
}
```

## 8. 不可变的@ConfigurationProperties绑定

**从Spring Boot 2.2开始，我们可以使用@ConstructorBinding注解来绑定我们的配置属性**。

这实质上意味着@ConfigurationProperties标注的类现在可能是[不可变](https://www.baeldung.com/java-immutable-object)的。

但是从Spring Boot 3开始，这个注解就不再需要了：

```java
@ConfigurationProperties(prefix = "mail.credentials")
public class ImmutableCredentials {

    private final String authMethod;
    private final String username;
    private final String password;

    public ImmutableCredentials(String authMethod, String username, String password) {
        this.authMethod = authMethod;
        this.username = username;
        this.password = password;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
```

如我们所见，在使用@ConstructorBinding时，我们需要为构造函数提供我们想要绑定的所有参数。

请注意，ImmutableCredentials的所有字段都是最终的。此外，类中没有setter方法。

此外，需要强调的是，**要使用构造函数绑定，我们需要使用@EnableConfigurationProperties或@ConfigurationPropertiesScan显式启用我们的配置类**。

## 9. Java 16记录

Java 16在[JEP 395](https://openjdk.java.net/jeps/395)中引入了记录类型。记录是充当不可变数据的透明载体的类，这使得它们成为配置持有者和DTO的完美候选者。事实上，**我们可以在Spring Boot中将Java记录定义为配置属性**。例如，前面的例子可以重写为：

```java
@ConstructorBinding
@ConfigurationProperties(prefix = "mail.credentials")
public record ImmutableCredentials(String authMethod, String username, String password) {
}
```

显然，与所有那些嘈杂的getter和setter相比，它更加简洁。

此外，从[Spring Boot 2.6](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6.0-M2-Release-Notes#records-and-configurationproperties)开始，**对于单构造函数记录，我们可以删除@ConstructorBinding注解**。但是，如果我们的记录有多个构造函数，则仍应使用@ConstructorBinding来标识要用于属性绑定的构造函数。

## 10. 总结

在本文中，我们探讨了@ConfigurationProperties注解并重点介绍了它提供的一些有用功能，例如宽松绑定和Bean验证。