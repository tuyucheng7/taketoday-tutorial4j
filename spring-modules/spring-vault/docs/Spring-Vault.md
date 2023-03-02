## 1. 概述

[HashiCorp的Vault](https://www.vaultproject.io/)是一种用于存储和保护机密的工具。总的来说，Vault解决的是如何管理机密的软件开发安全问题。要了解更多信息，请在[此处](https://www.baeldung.com/vault)查看我们的文章。

[Spring Vault](https://spring.io/projects/spring-vault)为HashiCorp的Vault提供Spring抽象。

在本教程中，我们将通过一个示例来说明如何在Vault中存储和检索机密。

## 2. Maven依赖

首先，让我们看一下开始使用Spring Vault所需的依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.vault</groupId>
        <artifactId>spring-vault-core</artifactId>
        <version>2.3.2</version>
    </dependency>
</dependencies>
```

可以在[Maven Central](https://central.sonatype.com/artifact/org.springframework.vault/spring-vault-core/3.0.1)上找到最新版本的spring-vault-core。

## 3. 配置Vault

现在让我们完成配置Vault所需的步骤。

### 3.1 创建VaultTemplate

为了保护我们的机密，我们必须实例化一个VaultTemplate，为此我们需要VaultEndpoint和TokenAuthentication实例：

```java
VaultTemplate vaultTemplate = new VaultTemplate(new VaultEndpoint(), new TokenAuthentication("00000000-0000-0000-0000-000000000000"));
```

### 3.2 创建VaultEndpoint

有几种方法可以实例化VaultEndpoint。让我们来看看其中的一些。

第一个是使用默认构造函数简单地实例化它，这将创建一个指向http://localhost:8200的默认端点：

```java
VaultEndpoint endpoint = new VaultEndpoint();
```

另一种方法是通过指定Vault的主机和端口来创建VaultEndpoint：

```java
VaultEndpoint endpoint = VaultEndpoint.create("host", port);
```

最后，我们还可以从Vault URL创建它：

```java
VaultEndpoint endpoint = VaultEndpoint.from(new URI("vault uri"));
```

这里有几点需要注意-Vault将配置根令牌00000000-0000-0000-0000-000000000000以运行此应用程序。

在我们的示例中，我们使用了TokenAuthentication，但也支持其他[身份验证方法](https://docs.spring.io/spring-vault/docs/current/reference/html/index.html#vault.core.authentication)。

## 4. 使用Spring配置Vault Bean

使用Spring，我们可以通过多种方式配置Vault。一种是通过扩展AbstractVaultConfiguration，另一种是使用EnvironmentVaultConfiguration，它利用了Spring的环境属性。

我们现在将讨论这两种方式。

### 4.1 使用AbstractVaultConfiguration

让我们创建一个扩展AbstractVaultConfiguration的类来以配置Spring Vault：

```java
@Configuration
public class VaultConfig extends AbstractVaultConfiguration {

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication("00000000-0000-0000-0000-000000000000");
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("host", 8020);
    }
}
```

这种方法类似于我们在上一节中看到的方法。不同的是，我们使用Spring Vault通过扩展抽象类AbstractVaultConfiguration来配置Vault bean。

我们只需要提供配置VaultEndpoint和ClientAuthentication的实现。

### 4.2 使用EnvironmentVaultConfiguration

我们还可以使用EnvironmentVaultConfiguration配置Spring Vault：

```java
@Configuration
@PropertySource(value = { "vault-config.properties" })
@Import(value = EnvironmentVaultConfiguration.class)
public class VaultEnvironmentConfig {
}
```

EnvironmentVaultConfiguration使用Spring的PropertySource来配置Vault beans。我们只需要为属性文件提供一些可接收的条目。

有关所有预定义属性的更多信息，请参见[官方文档](https://docs.spring.io/spring-vault/docs/current/reference/html/index.html#vault.core.environment-vault-configuration)。

要配置Vault，我们至少需要这几个属性：

```properties
vault.uri=https://localhost:8200
vault.token=00000000-0000-0000-0000-000000000000
```

## 5. 保护机密

我们将创建一个映射到用户名和密码的简单Credentials类：

```java
public class Credentials {

    private String username;
    private String password;

    // standard constructors, getters, setters
}
```

现在，让我们看看如何使用VaultTemplate保护我们的Credentials对象：

```java
Credentials credentials = new Credentials("username", "password");
vaultTemplate.write("secret/myapp", credentials);
```

完成这些行后，我们的机密就被存储了。

接下来，我们将了解如何访问它们。

## 6. 访问机密

我们可以使用VaultTemplate中的read()方法访问安全机密，该方法返回VaultResponseSupport作为响应：

```java
VaultResponseSupport<Credentials> response = vaultTemplate
    .read("secret/myapp", Credentials.class);
String username = response.getData().getUsername();
String password = response.getData().getPassword();
```

我们的机密值现在准备好了。

## 7. Vault Repository

Vault Repository是Spring Vault 2.0附带的一个方便的功能。**它在Vault之上应用了[Spring Data的Repository](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)概念**。

让我们深入了解如何在实践中使用这个新功能。

### 7.1 @Secret和@Id注解

Spring提供了这两个注解来标记我们想要持久化到Vault中的对象。

所以首先，我们需要标注我们的域类Credentials：

```java
@Secret(backend = "credentials", value = "myapp")
public class Credentials {

    @Id
    private String username;
    // Same code
}
```

@Secret注解的value属性用于区分域类型。backend属性表示机密后端挂载。

另一方面，@Id只是简单地划分了我们对象的标识符。

### 7.2 Vault Repository

现在，让我们定义一个使用我们的域对象Credentials的Repository接口：

```java
public interface CredentialsRepository extends CrudRepository<Credentials, String> {
}
```

正如我们所看到的，**我们的Repository扩展了CrudRepository，它提供了基本的CRUD和查询方法**。

接下来，让我们将CredentialsRepository注入CredentialsService并实现一些CRUD方法：

```java
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    public Credentials saveCredentials(Credentials credentials) {
        return credentialsRepository.save(credentials);
    }

    public Optional<Credentials> findById(String username) {
        return credentialsRepository.findById(username);
    }
}
```

现在我们已经添加了所有缺失的拼图，让我们使用测试用例确认一切正常。

首先，让我们从save()方法的测试用例开始：

```java
@Test
public void givenCredentials_whenSave_thenReturnCredentials() {
    // Given
    Credentials credentials = new Credentials("login", "password");
    Mockito.when(credentialsRepository.save(credentials))
        .thenReturn(credentials);

    // When
    Credentials savedCredentials = credentialsService.saveCredentials(credentials);

    // Then
    assertNotNull(savedCredentials);
    assertEquals(savedCredentials.getUsername(), credentials.getUsername());
    assertEquals(savedCredentials.getPassword(), credentials.getPassword());
}
```

最后，让我们用一个测试用例来确认一下findById()方法：

```java
@Test
public void givenId_whenFindById_thenReturnCredentials() {
    // Given
    Credentials credentials = new Credentials("login", "p@ssw@rd");
    Mockito.when(credentialsRepository.findById("login"))
        .thenReturn(Optional.of(credentials));

    // When
    Optional<Credentials> returnedCredentials = credentialsService.findById("login");

    // Then
    assertNotNull(returnedCredentials);
    assertNotNull(returnedCredentials.get());
    assertEquals(returnedCredentials.get().getUsername(), credentials.getUsername());
    assertEquals(returnedCredentials.get().getPassword(), credentials.getPassword());
}
```

## 8. 总结

在本文中，我们通过一个示例展示了Spring Vault在典型场景中的工作原理，了解了Spring Vault的基础知识。