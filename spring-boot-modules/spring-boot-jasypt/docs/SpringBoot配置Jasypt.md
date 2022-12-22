## 1. 简介

**Jasypt(Java Simplified Encryption) Spring Boot提供了用于加密Boot应用程序中的属性源的实用程序**。

在本文中，我们将讨论如何添加[jasypt-spring-boot](https://github.com/ulisesbocchio/jasypt-spring-boot)的支持并使用它。

有关使用Jasypt作为加密框架的更多信息，请在[此处]()查看我们的Jasypt简介。

## 2. 为什么选择Jasypt？

每当我们需要在配置文件中存储敏感信息时-这意味着我们实质上是在使这些信息容易受到攻击；这包括任何类型的敏感信息，例如凭据，但肯定远不止于此。

**通过使用Jasypt，我们可以为属性文件属性提供加密**，我们的应用程序将完成解密它并检索原始值的工作。

## 3. 在Spring Boot中使用Jasypt的方法

### 3.1 使用jasypt-spring-boot-starter

我们需要向项目添加一个依赖项：

```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```

Maven Central有最新版本的[jasypt-spring-boot-starter](https://search.maven.org/classic/#search|gav|1|g%3A"com.github.ulisesbocchio" AND a%3A"jasypt-spring-boot-starter")。

现在让我们用密钥“password”加密文本“Password@1”并将其添加到encrypted.properties：

```properties
encrypted.property=ENC(uTSqb9grs1+vUv3iN8lItC0kl65lMG+8)
```

我们定义一个配置类AppConfigForJasyptStarter，将encrypted.properties文件指定为PropertySource：

```java
@Configuration
@PropertySource("encrypted.properties")
public class AppConfigForJasyptStarter {
}
```

现在，我们将编写一个Service bean PropertyServiceForJasyptStarter来从encrypted.properties中检索值，**可以使用@Value注解或Environment类的getProperty()方法检索解密后的值**：

```java
@Service
public class PropertyServiceForJasyptStarter {

    @Value("${encrypted.property}")
    private String property;

    public String getProperty() {
        return property;
    }

    public String getPasswordUsingEnvironment(Environment environment) {
        return environment.getProperty("encrypted.property");
    }
}
```

最后，使用上面的Service类并设置我们用于加密的密钥，我们可以轻松地检索解密的密码并在我们的应用程序中使用：

```java
@Test
void whenDecryptedPasswordNeeded_GetFromService() {
    System.setProperty("jasypt.encryptor.password", "password");
    PropertyServiceForJasyptStarter service = appCtx.getBean(PropertyServiceForJasyptStarter.class);
 
    assertEquals("Password@1", service.getProperty());
 
    Environment environment = appCtx.getBean(Environment.class);
 
    assertEquals("Password@1",service.getPasswordUsingEnvironment(environment));
}
```

### 3.2 使用jasypt-spring-boot

**对于没有使用@SpringBootApplication或@EnableAutoConfiguration的项目，我们可以直接使用[jasypt-spring-boot](https://search.maven.org/classic/#search|gav|1|g%3A"com.github.ulisesbocchio" AND a%3A"jasypt-spring-boot")依赖**：

```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot</artifactId>
    <version>2.0.0</version>
</dependency>
```

同样，让我们用密钥“password”加密文本“Password@2”，并将其添加到encryptedv2.properties中：

```properties
encryptedv2.property=ENC(dQWokHUXXFe+OqXRZYWu22BpXoRZ0Drt)
```

我们为jasypt-spring-boot依赖项创建一个新的配置类。

在这里，我们需要添加注解@EncryptablePropertySource：

```java
@Configuration
@EncryptablePropertySource("encryptedv2.properties")
public class AppConfigForJasyptSimple {
}
```

此外，还定义了一个返回encryptedv2.properties的新PropertyServiceForJasyptSimple bean：

```java
@Service
public class PropertyServiceForJasyptSimple {

    @Value("${encryptedv2.property}")
    private String property;

    public String getProperty() {
        return property;
    }
}
```

最后，使用上面的Service类并设置我们用于加密的密钥，我们可以轻松地检索encryptedv2.property：

```java
@Test
void whenDecryptedPasswordNeeded_GetFromService() {
    System.setProperty("jasypt.encryptor.password", "password");
    PropertyServiceForJasyptSimple service = appCtx.getBean(PropertyServiceForJasyptSimple.class);
 
    assertEquals("Password@2", service.getProperty());
}
```

### 3.3 使用自定义Jasypt加密器

3.1节中定义的加密器和3.2使用默认配置值构建。

但是，让我们定义我们自己的Jasypt加密器并尝试将其用于我们的应用程序。

自定义加密器bean将如下所示：

```java
@Bean(name = "encryptorBean")
public StringEncryptor stringEncryptor() {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword("password");
    config.setAlgorithm("PBEWithMD5AndDES");
    config.setKeyObtentionIterations("1000");
    config.setPoolSize("1");
    config.setProviderName("SunJCE");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);
    return encryptor;
}
```

此外，我们可以修改SimpleStringPBEConfig的所有属性。

此外，**我们需要将属性“jasypt.encryptor.bean”添加到我们的application.properties中，以便Spring Boot知道它应该使用哪个自定义加密器**。

例如，我们在application.properties中添加使用密钥“password”加密的自定义文本“Password@3”：

```properties
jasypt.encryptor.bean=encryptorBean
encryptedv3.property=ENC(askygdq8PHapYFnlX6WsTwZZOxWInq+i)
```

一旦我们设置了它，我们就可以很容易地从Spring的Environment中获取encryptedv3.property：

```java
@Test
void whenConfiguredExcryptorUsed_ReturnCustomEncryptor() {
    Environment environment = appCtx.getBean(Environment.class);
 
    assertEquals("Password@3",environment.getProperty("encryptedv3.property"));
}
```

## 4. 总结

**通过使用Jasypt，我们可以为应用程序处理的数据提供额外的安全性**。

它使我们能够更多地关注我们应用程序的核心，并且还可以在需要时用于提供自定义加密。