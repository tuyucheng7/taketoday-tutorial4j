## 1. 概述

在我们之前的[@ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot)指南中，我们学习了如何在Spring Boot中设置和使用@ConfigurationProperties注解来处理外部配置。

在本教程中，**我们将讨论如何测试依赖于@ConfigurationProperties注解的配置类**，以确保我们的配置数据被加载并正确绑定到其相应的字段。

## 2. 依赖项

在我们的Maven项目中，我们将使用spring-boot-starter和spring-boot-starter-test依赖项来启用核心spring API和Spring的测试API。此外，我们将使用spring-boot-starter-validation作为bean validation依赖：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.6.1</version>
</parent>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## 3. 属性绑定到用户定义的POJO

在使用外部化配置时，**我们通常会创建包含与匹配配置属性相对应字段的POJO**。正如我们已经知道的，Spring会自动将配置属性绑定到我们创建的Java类中。

首先，假设我们在src/test/resources/server-config-test.properties的属性文件中有一些服务器配置：

```properties
server.address.ip=192.168.0.1
server.resources_path.imgs=/root/imgs
```

我们将定义一个与上面的属性相对应的简单配置类：

```java
@Configuration
@ConfigurationProperties(prefix = "server")
public class ServerConfig {
    private Address address;
    private Map<String, String> resourcesPath;

    // getters and setters ...

    public static class Address {
        private String ip;
        // getters and setters ...
    }
}
```

以及相应的Address类型：

```java
public class Address {

    private String ip;

    // getters and setters
}
```

最后，我们将ServerConfig POJO注入到我们的测试类中，并验证它的所有字段是否都设置正确：

```java
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ServerConfig.class)
@TestPropertySource("classpath:server-config-test.properties")
class BindingPropertiesToUserDefinedPOJOUnitTest {

    @Autowired
    private ServerConfig serverConfig;

    @Test
    void givenUserDefinedPojo_whenBindingPropertiesFile_thenAllFieldAreSet() {
        assertEquals("192.168.0.1", serverConfig.getAddress().getIp());
        
        Map<String, String> expectedResourcesPath = new HashMap<>();
        expectedResourcesPath.put("imgs", "/root/imgs");
        
        assertEquals(expectedResourcesPath, serverConfig.getResourcesPath());
    }
}
```

在以上测试类中，我们使用了以下注解：

+ @ExtendWith：将Spring的TestContext框架与JUnit 5集成
+ @EnableConfigurationProperties：启用对@ConfigurationProperties bean(在本例中为ServerConfig bean)的支持
+ @TestPropertySource：指定一个覆盖默认application.properties的属性文件

## 4. @ConfigurationProperties使用在@Bean方法上

**创建配置bean的另一种方法是在@Bean方法上使用@ConfigurationProperties注解**。

例如，以下getDefaultConfigs()方法创建一个ServerConfig配置bean：

```java
@Configuration
public class ServerConfigFactory {

    @Bean(name = "default_bean")
    @ConfigurationProperties(prefix = "server.default")
    public ServerConfig getDefaultConfig() {
        return new ServerConfig();
    }
}
```

正如我们所看到的，我们能够在getDefaultConfigs()方法上使用@ConfigurationProperties来配置ServerConfig实例，而无需编辑ServerConfig类本身，**这在使用具有受限访问权限的外部第三方类时特别有用**。

接下来，我们需要为server.default前缀定义属性：

```properties
# server-config-test.properties
server.default.address.ip=192.168.0.2
server.default.resources_path.imgs=/root/def/imgs
```

最后，为了告诉Spring在加载ApplicationContext时使用ServerConfigFactory类(从而创建我们的配置bean)，我们将@ContextConfiguration注解添加到测试类上：

```java
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ServerConfig.class)
@ContextConfiguration(classes = ServerConfigFactory.class)
@TestPropertySource("classpath:server-config-test.properties")
class BindingPropertiesToBeanMethodsUnitTest {
    
    @Autowired
    @Qualifier("default_bean")
    private ServerConfig serverConfig;

    @Test
    void givenBeanAnnotatedMethod_whenBindingProperties_thenAllFieldAreSet() {
        assertEquals("192.168.0.2", serverConfig.getAddress().getIp());
        
        Map<String, String> expectedResourcesPath = new HashMap<>();
        expectedResourcesPath.put("imgs", "/root/def/imgs");
        
        assertEquals(expectedResourcesPath, serverConfig.getResourcesPath());
    }
}
```

## 5. 属性校验

要在Spring Boot中启用bean validation，**我们必须使用@Validated注解标注顶层类**，然后我们添加所需的javax.validation约束注解：

```java
@Validated
@Configuration
@ConfigurationProperties(prefix = "validate")
@PropertySource("classpath:property-validation.properties")
public class MailServer {
    @NotNull
    @NotEmpty
    private HashMap<String, @NotBlank String> propertiesMap;

    @Valid
    private MailConfig mailConfig = new MailConfig();

    // getter and setter ...
}
```

同样，MailConfig类也有一些约束：

```java
public class MailConfig {
    
    @Email
    @NotBlank
    private String address;
    
    // getter and setter ...
}
```

然后在property-validation.properties文件中提供相应的数据集：

```properties
# property-validation-test.properties
validate.propertiesMap.first=prop1
validate.propertiesMap.second=prop2
validate.mail_config.address=user1@test
```

应用程序将正常启动，我们的单元测试将通过：

```java
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(MailServer.class)
@TestPropertySource("classpath:property-validation-test.properties")
class PropertyValidationUnitTest {
    @Autowired
    private MailServer mailServer;

    private static Validator propertyValidator;

    @BeforeAll
    public static void setup() {
        propertyValidator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void whenBindingPropertiesToValidateBeans_thenConstrainsAreChecked() {
        assertEquals(0, propertyValidator.validate(mailServer.getPropertiesMap()).size());
        assertEquals(0, propertyValidator.validate(mailServer.getMailConfig()).size());
    }
}
```

相反，**如果我们使用无效属性，Spring将在启动时抛出IllegalStateException**。

例如，使用以下任何无效的属性配置：

```properties
# property-validation-test.properties
validate.propertiesMap.second=
validate.mail_config.address=user1.test
```

将导致我们的应用程序失败并显示以下错误消息：

```shell
[validate.mail-config.address,address]; default message [不是一个合法的电子邮件地址]; 
origin "validate.mail_config.address" from property source "class path resource [property-validation-test.properties]"
```

请注意，**我们在mailConfig字段上使用了@Valid以确保检查MailConfig约束，即使validate.mailConfig.address未定义**。否则，Spring会将mailConfig设置为null并正常启动应用程序。

## 6. 属性转换

Spring Boot属性转换使我们能够将某些属性转换为特定类型。

在本节中，我们将从测试使用Spring内置转换的配置类开始，然后我们将测试我们自己创建的自定义转换器。

### 6.1 SpringBoot默认的转化器

让我们考虑以下DataSize和Duration属性：

```properties
# spring-conversion-test.properties
server.upload_speed=500MB
server.download_speed=10

server.backup_day=1d
server.backup_hour=8
```

**Spring Boot会自动将这些属性绑定到PropertyConversion配置类中定义的匹配的DataSize和Duration字段**：

```java
@Configuration
@ConfigurationProperties(prefix = "server")
public class PropertyConversion {

    private DataSize uploadSpeed;

    @DataSizeUnit(DataUnit.GIGABYTES)
    private DataSize downloadSpeed;

    private Duration backupDay;

    @DurationUnit(ChronoUnit.HOURS)
    private Duration backupHour;

    // getter and setter ...
}
```

现在让我们测试转换结果：

```java
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PropertyConversion.class)
@ContextConfiguration(classes = CustomCredentialsConverter.class)
@TestPropertySource("classpath:spring-conversion-test.properties")
class SpringPropertiesConversionUnitTest {
    
    @Autowired
    private PropertyConversion propertyConversion;

    @Test
    void whenUsingSpringDefaultSizeConversion_thenDataSizeObjectIsSet() {
        assertEquals(DataSize.ofMegabytes(500), propertyConversion.getUploadSpeed());
        assertEquals(DataSize.ofGigabytes(10), propertyConversion.getDownloadSpeed());
    }

    @Test
    void whenUsingSpringDefaultDurationConversion_thenDurationObjectIsSet() {
        assertEquals(Duration.ofDays(1), propertyConversion.getBackupDay());
        assertEquals(Duration.ofHours(8), propertyConversion.getBackupHour());
    }
}
```

### 6.2 自定义转换器

现在让我们假设我们要转换convert.credentials属性：

```properties
#spring-conversion-test.properties
convert.credentials=user,123
```

到以下Credentials类中：

```java
public class Credentials {
    private String username;
    private String password;
    // getter and setter ...
}
```

为了实现这一点，我们可以编写自定义转换器：

```java
@Component
@ConfigurationPropertiesBinding
public class CustomCredentialsConverter implements Converter<String, Credentials> {

    @Override
    public Credentials convert(String source) {
        String[] data = source.split(",");
        return new Credentials(data[0], data[1]);
    }
}
```

最后，我们向PropertyConversion类添加一个Credentials字段：

```java
public class PropertyConversion {
    private Credentials credentials;
    // ...
}
```

在我们的SpringPropertiesConversionUnitTest测试类中，我们还需要添加@ContextConfiguration来在Spring的上下文中注册自定义转换器：

```java
// other annotations
@ContextConfiguration(classes = CustomCredentialsConverter.class)
class SpringPropertiesConversionUnitTest {
    
    // ...

    @Test
    void whenResisteringCustomCredentialsConverter_thenCredentialsAreParsed() {
        assertEquals("user", propertyConversion.getCredentials().getUsername());
        assertEquals("123", propertyConversion.getCredentials().getPassword());
    }
}
```

正如上面的断言所示，**Spring使用我们的自定义转换器将convert.credentials属性解析为Credentials实例**。

## 7. YAML文件绑定

对于多层级配置数据，yaml配置可能更方便，yaml还支持在同一个文档中定义多个profile。

以下application.yml文件位于src/test/resources/目录下，为ServerConfig类定义了一个“test” profile：

```yaml
spring:
    config:
        activate:
            on-profile: test
server:
    address:
        ip: 192.168.0.4
    resources_path:
        imgs: /etc/test/imgs
---
# other profiles
```

因此，以下测试可以通过：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = ServerConfig.class)
@ActiveProfiles("test")
class BindingYMLPropertiesUnitTest {
    
    @Autowired
    private ServerConfig serverConfig;

    @Test
    void whenBindingYmlConfigFile_thenAllFieldsAreSet() {
        assertEquals("192.168.0.4", serverConfig.getAddress().getIp());
        
        Map<String, String> expectedResourcesPath = new HashMap<>();
        expectedResourcesPath.put("imgs", "/etc/test/imgs");
        
        assertEquals(expectedResourcesPath, serverConfig.getResourcesPath());
    }
}
```

关于以上配置类使用到的几个注解：

+ @ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)：加载application.yml文件
+ @ActiveProfiles("test")：指定在此测试期间将使用"test" profile

**最后，请记住@ProperySource和@TestPropertySource都不支持加载.yml文件，因此，我们应该始终将yaml配置放在application.yml文件中**。

## 8. 覆盖@ConfigurationProperties配置

有时，我们可能希望使用另一个属性数据源覆盖@ConfigurationProperties加载的配置属性，尤其是在测试时。

正如我们在前面的例子中看到的，我们可以使用@TestPropertySource("path_to_new_data_set")将整个原始配置(/src/main/resources)替换为新配置。

**或者，我们可以使用@TestPropertySource的properties属性有选择性地替换一些原始属性**。

假设我们想用另一个值覆盖之前定义的validate.mail_config.address属性，我们所要做的就是用@TestPropertySource标注我们的测试类，然后通过properties列表为相同的属性分配一个新值：

```java
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = MailServer.class)
@TestPropertySource(properties = {"validate.mail_config.address=new_user@test"})
class OverridingConfigurationPropertiesUnitTest {
    @Autowired
    private MailServer mailServer;

    @Test
    void givenUsingPropertiesAttribute_whenAssiginingNewValueToProperty_thenSpringUsesNewValue() {
        assertEquals("new_user@test", mailServer.getMailConfig().getAddress());
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("first", "prop1");
        expectedMap.put("second", "prop2");
        assertEquals(expectedMap, mailServer.getPropertiesMap());
    }
}
```

因此，Spring将使用新定义的值。

## 9. 总结

在本文中，我们学习了如何测试使用@ConfigurationProperties注解加载.properties和.yml配置文件的不同类型的配置类。