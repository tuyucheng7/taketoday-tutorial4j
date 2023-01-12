## 1. 概述

[HashiCorp 的 Vault](https://www.vaultproject.io/)是一种用于存储和保护秘密的工具。Vault，总的来说，解决的是如何管理秘密的软件开发安全问题。要了解更多信息，请在此处查看[我们的文章](https://www.baeldung.com/vault)。

[Spring Vault](https://spring.io/projects/spring-vault)为 HashiCorp 的 Vault 提供 Spring 抽象。

在本教程中，我们将通过一个示例来说明如何在 Vault 中存储和检索机密。

## 2.Maven依赖

首先，让我们看一下开始使用 Spring Vault 所需的依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.vault</groupId>
        <artifactId>spring-vault-core</artifactId>
        <version>2.3.2</version>
    </dependency>
</dependencies>

```

可以在 [Maven Central](https://search.maven.org/search?q=g:org.springframework.vault AND a:spring-vault-core&core=gav)上找到最新版本的spring-vault-core。

## 3.配置保管库

现在让我们完成配置 Vault 所需的步骤。

### 3.1. 创建VaultTemplate

为了保护我们的秘密，我们必须实例化一个VaultTemplate，为此我们需要VaultEndpoint和TokenAuthentication实例：

```java
VaultTemplate vaultTemplate = new VaultTemplate(new VaultEndpoint(), 
  new TokenAuthentication("00000000-0000-0000-0000-000000000000"));
```

### 3.2. 创建VaultEndpoint

有几种方法可以实例化VaultEndpoint。让我们来看看其中的一些。

第一个是使用默认构造函数简单地实例化它，这将创建一个指向http://localhost:8200 的默认端点：

```java
VaultEndpoint endpoint = new VaultEndpoint();
```

另一种方法是通过指定 Vault 的主机和端口来创建 VaultEndpoint ：

```java
VaultEndpoint endpoint = VaultEndpoint.create("host", port);
```

最后，我们还可以从 Vault URL 创建它：

```java
VaultEndpoint endpoint = VaultEndpoint.from(new URI("vault uri"));
```

这里有几点需要注意——Vault 将配置根令牌 00000000-0000-0000-0000-000000000000以运行此应用程序。

在我们的示例中，我们使用 了 TokenAuthentication，但也支持其他[身份验证方法](https://docs.spring.io/spring-vault/docs/current/reference/html/index.html#vault.core.authentication)。

## 4. 使用 Spring 配置 Vault Bean

使用 Spring，我们可以通过多种方式配置 Vault。一种是通过扩展 AbstractVaultConfiguration，另一种是使用 EnvironmentVaultConfiguration ，它利用了 Spring 的环境属性。

我们现在将讨论两种方式。

### 4.1. 使用AbstractVaultConfiguration

让我们创建一个扩展AbstractVaultConfiguration 的类，以配置 Spring Vault：

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

这种方法类似于我们在上一节中看到的方法。不同的是，我们使用 Spring Vault 通过扩展抽象类AbstractVaultConfiguration 来配置 Vault bean。

我们只需要提供配置VaultEndpoint和ClientAuthentication的实现。

### 4.2. 使用EnvironmentVaultConfiguration

我们还可以使用 EnviromentVaultConfiguration配置 Spring Vault ：

```java
@Configuration
@PropertySource(value = { "vault-config.properties" })
@Import(value = EnvironmentVaultConfiguration.class)
public class VaultEnvironmentConfig {
}
```

EnvironmentVaultConfiguration使用 Spring 的PropertySource来配置 Vault beans。我们只需要为属性文件提供一些可接受的条目。

有关所有预定义属性的更多信息，请参见 [官方文档](https://docs.spring.io/spring-vault/docs/current/reference/html/index.html#vault.core.environment-vault-configuration)。

要配置 Vault，我们至少需要几个属性：

```java
vault.uri=https://localhost:8200
vault.token=00000000-0000-0000-0000-000000000000
```

## 5. 保护秘密

我们将创建一个映射到用户名和密码的简单Credentials类：

```java
public class Credentials {

    private String username;
    private String password;
    
    // standard constructors, getters, setters
}
```

现在，让我们看看如何使用VaultTemplate保护我们的Credentials对象 ：

```java
Credentials credentials = new Credentials("username", "password");
vaultTemplate.write("secret/myapp", credentials);
```

完成这些行后，我们的秘密就被存储了。

接下来，我们将看看如何访问它们。

## 6. 访问秘密

我们可以使用 VaultTemplate 中的 read() 方法访问安全机密 ，该 方法返回 VaultResponseSupport作为响应：

```java
VaultResponseSupport<Credentials> response = vaultTemplate
  .read("secret/myapp", Credentials.class);
String username = response.getData().getUsername();
String password = response.getData().getPassword();
```

我们的秘密值现在准备好了。

## 7. 保险库存储库

Vault 存储库是 Spring Vault 2.0 附带的一个方便的功能。它在 Vault 之上应用了[Spring Data 的存储库](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)概念。

让我们深入了解如何在实践中使用这个新功能。

### 7.1. @Secret和@Id注解

Spring 提供了这两个注解来标记我们想要持久化到 Vault 中的对象。

所以首先，我们需要装饰我们的域类型Credentials：

```java
@Secret(backend = "credentials", value = "myapp")
public class Credentials {

    @Id
    private String username;
    // Same code
]
```

@Secret注解的值属性用于区分域类型。backend属性表示秘密后端挂载。

另一方面，@Id 只是简单地划分了我们对象的标识符。

### 7.2. 保险库存储库

现在，让我们定义一个使用我们的域对象Credentials的存储库接口：

```java
public interface CredentialsRepository extends CrudRepository<Credentials, String> {
}
```

如我们所见，我们的存储库扩展了CrudRepository，它提供了基本的 CRUD 和查询方法。

接下来，让我们将CredentialsRepository注入CredentialsService并实现一些 CRUD 方法：

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

## 八. 总结

在本文中，我们通过一个示例展示了 Spring Vault 在典型场景中的工作原理，了解了 Spring Vault 的基础知识。