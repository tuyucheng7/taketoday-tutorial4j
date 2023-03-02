## 1. 概述

在本教程中，我们将展示如何在Spring Boot应用程序中使用Hashicorp的Vault来保护敏感配置数据。

**我们在这里假设有一些Vault知识，并且我们有一个已经启动并运行的测试设置**。如果不是这种情况，让我们花点时间阅读我们的[Vault介绍教程](https://www.baeldung.com/vault)，以便我们熟悉它的基础知识。

## 2. Spring Cloud Vault

Spring Cloud Vault是Spring Cloud堆栈的一个相对较新的补充，它**允许应用程序以透明的方式访问存储在Vault实例中的密钥**。

一般来说，迁移到Vault是一个非常简单的过程：只需添加所需的库并向我们的项目添加一些额外的配置属性，我们就可以开始了。无需更改代码！

这是可能的，因为它充当在当前Environment中注册的高优先级PropertySource。

因此，只要需要一个属性，Spring就会使用它。示例包括DataSource属性、ConfigurationProperties等。

## 3. 将Spring Cloud Vault添加到Spring Boot项目

为了在基于Maven的Spring Boot项目中包含spring-cloud-vault库，我们使用关联的启动器工件，它将拉取所有必需的依赖项。

除了主要的启动器，我们还将包括spring-vault-config-databases，它增加了对动态数据库凭证的支持：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-vault-config-databases</artifactId>
</dependency>
```

可以从Maven Central下载最新版本的[Spring Cloud Vault Starter](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-vault-config/4.0.0)。

### 3.1 基本配置

为了正常工作，Spring Cloud Vault需要一种方法来确定在哪里联系Vault服务器以及如何对其进行身份验证。

我们通过在application.yml或application.properties中提供必要的信息来做到这一点：

```yaml
spring:
    cloud:
        vault:
            uri: https://localhost:8200
            ssl:
                trust-store: classpath:/vault.jks
                trust-store-password: changeit
    config:
        import: vault:// 
```

spring.cloud.vault.uri属性指向Vault的API地址。由于我们的测试环境使用带有自签名证书的HTTPS，因此我们还需要提供一个包含其公钥的密钥库。

**请注意，此配置没有身份验证数据**。对于最简单的情况，我们使用固定令牌，我们可以通过系统属性spring.cloud.vault.token或环境变量传递它。这种方法可以很好地与标准云配置机制结合使用，例如Kubernetes的ConfigMaps或Docker secrets。

Spring Vault还需要为我们要在应用程序中使用的每种类型的密钥进行额外配置。以下部分描述了我们如何添加对两种常见密钥类型的支持：键/值和数据库凭证。

## 4. 使用通用密钥后端

**我们使用通用密钥后端来访问以键值对形式存储在Vault中的未版本控制的密钥**。

假设我们的classpath中已经有spring-cloud-starter-vault-config依赖项，我们所要做的就是向application.yml文件添加一些属性：

```yaml
spring:
    cloud:
        vault:
            # other vault properties omitted ...
            generic:
                enabled: true
                application-name: fakebank
```

在这种情况下，属性application-name是可选的。如果未指定，Spring将采用标准spring.application.name的值。

我们现在可以使用存储在secret/fakebank中的所有键/值对作为任何其他环境属性。以下代码片段显示了我们如何读取存储在该路径下的foo键的值：

```java
@Autowired Environment env;
public String getFoo() {
    return env.getProperty("foo");
}
```

**正如我们所看到的，代码本身对Vault一无所知**，这是一件好事！我们仍然可以在本地测试中使用固定属性，并通过在application.yml中启用单个属性来随意切换到Vault。

### 4.1 Spring Profile注意事项

**如果在当前环境中可用，Spring Cloud Vault将使用可用的Profile名称作为后缀附加到将搜索键/值对的指定基本路径**。

它还将在可配置的默认应用程序路径(有或没有配置文件后缀)下查找属性，以便我们可以在一个位置共享密钥。请谨慎使用此功能！

总而言之，如果fakebank应用程序的production Profile处于激活状态，则Spring Vault将查找存储在以下路径下的属性：

1.  secret/fakebank/production(高优先级)
2.  secret/fakebank
3.  secret/application/production
4.  secret/application(低优先级)

在前面的列表中，application是Spring用作密钥的默认附加位置的名称。我们可以使用spring.cloud.vault.generic.default-context属性对其进行修改。

存储在最具体路径下的属性将优先于其他路径。例如，如果同一属性foo在上述路径下可用，则优先顺序为：

## 5. 使用数据库密钥后端

**数据库后端模块允许Spring应用程序使用由Vault创建的动态生成的数据库凭据**。Spring Vault在标准的spring.datasource.username和spring.datasource.password属性下注入这些凭据，以便它们可以被常规的DataSource选择。

请注意，在使用此后端之前，我们必须按照我们[之前的教程](https://www.baeldung.com/vault)中所述在Vault中创建数据库配置和角色。

为了在我们的Spring应用程序中使用Vault生成的数据库凭据，spring-cloud-vault-config-databases必须与相应的JDBC驱动程序一起出现在项目的类路径中。

我们还需要通过向我们的application.yml添加一些属性来启用它在我们的应用程序中的使用：

```yaml
spring:
    cloud:
        vault:
            # ... other properties omitted
            database:
                enabled: true
                role: fakebank-accounts-rw
```

这里最重要的属性是role属性，它包含存储在Vault中的数据库角色名称。在启动期间，Spring将联系Vault并请求它创建具有相应权限的新凭据。

默认情况下，Vault库将在配置的生存时间后撤销与这些凭据关联的权限。

幸运的是，**Spring Vault会自动续订与获取的凭据关联的租约**。通过这样做，只要我们的应用程序正在运行，凭据就会保持有效。

现在，让我们看看这种集成的实际效果。以下代码片段从Spring管理的DataSource获取新的数据库连接：

```java
Connection c = datasource.getConnection();
```

**再一次，我们可以看到我们的代码中没有使用Vault的迹象**。所有集成都发生在Environment级别，因此我们的代码可以像往常一样轻松地进行单元测试。

## 6. 总结

在本教程中，我们展示了如何使用Spring Vault库将Vault与Spring Boot集成。我们介绍了两个常见用例：通用键/值对和动态数据库凭证。