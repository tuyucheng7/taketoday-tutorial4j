## 1. 概述

在本教程中，我们将Spring Boot应用程序与AWS Secrets Manager集成，以检索数据库凭证和其他类型的密钥，例如API密钥。

## 2. AWS Secrets Manager

**[AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html)是一项AWS服务，它使我们能够安全地存储、轮换和管理凭证**，例如，用于数据库、API 密钥、令牌或我们想要管理的任何其他密钥。

我们可以区分两种类型的密钥-一种用于严格的数据库凭证，另一种用于任何其他类型的密钥。

使用AWS Secrets Manager的一个很好的例子是为我们的应用程序提供一组凭证或API密钥。

推荐的密钥保存方式是JSON格式。此外，**如果我们想使用密钥轮换功能，我们必须使用[JSON结构](https://docs.aws.amazon.com/secretsmanager/latest/userguide/reference_secret_json_structure.html)**。

## 3. 与AWS Secrets Manager集成

AWS Secrets Manager可以轻松地与我们的Spring Boot应用程序集成。让我们尝试一下，通过AWS CLI在AWS中创建密钥，然后通过Spring Boot中的简单配置检索它们。

### 3.1 密钥创建

让我们在AWS Secrets Manager中创建一个密钥。为此，我们可以使用[AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-quickstart.html)和[aws secretsmanager create-secret](https://docs.aws.amazon.com/cli/latest/reference/secretsmanager/create-secret.html)命令。

在我们的例子中，让我们将密钥命名为test/secret/并创建两对API密钥-具有apiKeyValue1的api-key1和具有apiKeyValue2值的api-key2 ：

```shell
aws secretsmanager create-secret \
--name test/secret/ \
--secret-string "{\"api-key1\":\"apiKeyValue1\",\"api-key2\":\"apiKeyValue2\"}"
```

作为响应，我们应该获取创建的密钥的ARN、名称和版本ID：

```json
{
    "ARN": "arn:aws:secretsmanager:eu-central-1:111122223333:secret:my/secret/-gLK10U",
    "Name": "test/secret/",
    "VersionId": "a04f735e-3b5f-4194-be0d-719d5386b67b"
}
```

### 3.2 Spring Boot应用程序集成

为了检索我们的新密钥，我们必须添加[spring-cloud-starter-aws-secrets-manager-config](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-aws-secrets-manager-config)依赖项：

```xml
<dependency>
    <groupId>io.awspring.cloud</groupId>
    <artifactId>spring-cloud-starter-aws-secrets-manager-config</artifactId>
    <version>2.4.4</version>
</dependency>
```

下一步是在我们的application.properties文件中添加一个属性：

```properties
spring.config.import=aws-secretsmanager:test/secret/
```

我们在这里提供我们刚刚创建的密钥的名称。完成此设置后，让我们在应用程序中使用我们的新密钥并验证它们的值。

为此，我们可以通过@Value注解将我们的密钥注入到应用程序中。在注解中，我们指定了我们在密钥创建过程中提供的密钥字段的名称。在我们的例子中，它是api-key1和api-key2：

```java
@Value("${api-key1}")
private String apiKeyValue1;

@Value("${api-key2}")
private String apiKeyValue2;
```

为了验证此示例中的值，让我们在bean属性初始化之后在@PostConstruct中打印它们：

```java
@PostConstruct
private void postConstruct() {
    System.out.println(apiKeyValue1);
    System.out.println(apiKeyValue2);
}
```

我们应该注意，将密钥值输出到我们的控制台并不是好的做法。但是，我们可以在这个例子中看到，当我们运行应用程序时，它们的值已经被正确加载：

```shell
2023-03-26 12:40:24.376  INFO 33504 [main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
apiKeyValue1
apiKeyValue2
2023-03-26 12:40:25.306  INFO 33504 [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080 (http) with context path ''
```

## 4. 数据库凭证的特殊密钥

AWS Secrets Manager中有一种特殊类型的密钥用于存储数据库凭证。我们可以从AWS支持的数据库中选择一种，例如Amazon RDS、Amazon DocumentDB或Amazon Redshift。非Amazon数据库的另一种可能性是提供服务器地址、数据库名称和端口。

通过在Spring Boot应用程序中使用aws-secretsmanager-jdbc库，我们可以轻松地向我们的数据库提供这些凭证。此外，**如果我们在Secrets Manager中轮换凭证，AWS提供的库会在使用之前的凭证收到身份验证错误时自动检索一组新的凭证**。

### 4.1 数据库密钥创建

为了在AWS Secrets Manager中创建数据库类型密钥，我们将再次使用AWS CLI：

```shell
$ aws secretsmanager create-secret \
    --name rds/credentials \
    --secret-string file://mycredentials.json
```

在上面的命令中，我们使用了mycredentials.json文件，在其中为数据库指定了所有必要的属性：

```json
{
    "engine": "mysql",
    "host": "cwhgvgjbpqqa.eu-central-rds.amazonaws.com",
    "username": "admin",
    "password": "password",
    "dbname": "db-1",
    "port": "3306"
}
```

### 4.2 Spring Boot应用程序集成

创建密钥后，我们就可以在Spring Boot应用程序中使用它了。为此，我们需要添加一些依赖项，例如[aws-secretsmanager-jdbc](https://mvnrepository.com/artifact/com.amazonaws.secretsmanager/aws-secretsmanager-jdbc)和[mysql-connector-java](https://mvnrepository.com/artifact/mysql/mysql-connector-java)：

```xml
<dependency>
    <groupId>com.amazonaws.secretsmanager</groupId>
    <artifactId>aws-secretsmanager-jdbc</artifactId>
    <version>1.0.11</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.32</version>
</dependency>
```

最后，我们还需要在application.properties文件中设置一些属性：

```properties
spring.datasource.driver-class-name=com.amazonaws.secretsmanager.sql.AWSSecretsManagerMySQLDriver
spring.jpa.database-platform=org.hibernate.dialect.MySQL5Dialect
spring.datasource.url=jdbc-secretsmanager:mysql://db-1.cwhqvgjbpgfw.eu-central-1.rds.amazonaws.com:3306
spring.datasource.username=rds/credentials
```

在spring.datasource.driver-class-name中，我们指定要使用的[驱动程序](https://docs.aws.amazon.com/secretsmanager/latest/userguide/retrieving-secrets_jdbc.html)的名称。

下一个是spring.jpa.database-platform，我们在其中提供方言。

当我们在spring.datasource.url中指定数据库的URL时，我们必须在该URL之前添加jdbc-secretsmanager前缀。这是必需的，因为我们要与AWS Secrets Manager集成。在此示例中，我们的URL引用的是MySQL RDS实例，但它可以引用任何MySQL数据库。

在spring.datasource.username中，我们只需要提供之前设置的AWS Secrets Manager的密钥。基于这些属性，我们的应用程序将尝试连接到Secrets Manager并在连接到数据库之前检索用户名和密码。

在应用程序日志中，我们可以看到我们设法连接到数据库并且EntityManager已经初始化：

```shell
2023-03-26 12:40:22.648  INFO 33504 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: default]
2023-03-26 12:40:22.697  INFO 33504 --- [           main] org.hibernate.Version                    : HHH000412: Hibernate ORM core version 5.6.12.Final
2023-03-26 12:40:22.845  INFO 33504 --- [           main] o.hibernate.annotations.common.Version   : HCANN000001: Hibernate Commons Annotations {5.1.2.Final}
2023-03-26 12:40:22.951  INFO 33504 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2023-03-26 12:40:23.752  INFO 33504 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2023-03-26 12:40:23.783  INFO 33504 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.MySQL5Dialect
2023-03-26 12:40:24.363  INFO 33504 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
2023-03-26 12:40:24.376  INFO 33504 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
```

此外，还创建了一个简单的UserController，我们可以在其中创建、读取和删除用户。

我们可以使用[curl](https://www.baeldung.com/curl-rest)创建用户：

```shell
$ curl --location 'localhost:8080/users/' \
--header 'Content-Type: application/json' \
--data '{
    "name": "my-user-1"
}'
```

得到成功的响应：

```json
{
    "id": 1,
    "name": "my-user-1"
}
```

## 5. 总结

在本文中，我们了解了如何将Spring Boot应用程序与AWS Secrets Manager集成，以及如何为数据库凭证和其他类型的密钥检索密钥。