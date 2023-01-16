## 1. 简介

在本教程中，我们将探索[Flyway](https://flywaydb.org/)的关键概念，以及我们如何使用该框架可靠、轻松地持续重构应用程序的数据库模式。此外，我们将展示一个使用 Maven Flyway 插件管理内存中 H2 数据库的示例。

Flyway 使用迁移将数据库从一个版本更新到下一个版本。我们可以使用特定于数据库的语法在 SQL 中编写迁移，也可以在高级数据库转换中使用Java编写迁移。

## 延伸阅读：

## [使用 Liquibase 安全地发展你的数据库架构](https://www.baeldung.com/liquibase-refactor-schema-of-java-app)

如何使用 Liquibase 安全、成熟地改进Java应用程序的数据库模式。

[阅读更多](https://www.baeldung.com/liquibase-refactor-schema-of-java-app)→

## [使用 Flyway 回滚迁移](https://www.baeldung.com/flyway-roll-back)

了解如何使用 Flyway 安全地回滚迁移。

[阅读更多](https://www.baeldung.com/flyway-roll-back)→

## [Flyway 回调指南](https://www.baeldung.com/flyway-callbacks)

在 Flyway 中实现 SQL 和Java命令回调的指南

[阅读更多](https://www.baeldung.com/flyway-callbacks)→

迁移可以是版本化的或可重复的。前者有一个独特的版本，并且只应用一次。后者没有版本。相反，每次校验和更改时都会(重新)应用它们。

在单个迁移运行中，可重复的迁移总是在执行挂起的版本化迁移之后最后应用。可重复迁移按其描述的顺序应用。对于单个迁移，所有语句都在单个数据库事务中运行。

在本教程中，我们将主要关注如何使用 Maven 插件执行数据库迁移。

## 2. Flyway Maven 插件

要安装 Flyway Maven 插件，让我们将以下插件定义添加到我们的pom.xml 中：

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>8.5.13</version> 
</plugin>
```

该插件的最新版本可在[Maven Central](https://mvnrepository.com/artifact/org.flywaydb/flyway-maven-plugin)获得。

我们可以用四种不同的方式配置这个 Maven 插件。在以下部分中，我们将逐一介绍这些选项。

请参阅[文档](https://flywaydb.org/documentation/usage/maven/migrate)以获取所有可配置属性的列表。

### 2.1. 插件配置

我们可以通过pom.xml的插件定义中的<configuration>标签直接配置插件：

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>8.5.13</version>
    <configuration>
        <user>databaseUser</user>
        <password>databasePassword</password>
        <schemas>
            <schema>schemaName</schema>
        </schemas>
        ...
    </configuration>
</plugin>
```

### 2.2. Maven 属性

我们还可以通过在 pom 中将可配置属性指定为 Maven属性来配置插件：

```xml
<project>
    ...
    <properties>
        <flyway.user>databaseUser</flyway.user>
        <flyway.password>databasePassword</flyway.password>
        <flyway.schemas>schemaName</flyway.schemas>
        ...
    </properties>
    ...
</project>
```

### 2.3. 外部配置文件

另一种选择是在单独的.conf文件中提供插件配置：

```plaintext
flyway.user=databaseUser
flyway.password=databasePassword
flyway.schemas=schemaName
...
```

默认配置文件名为flyway.conf，默认情况下它从以下位置加载配置文件：

-   安装目录/conf/flyway.conf
-   userhome/flyway.conf
-   工作目录/flyway.conf

编码由flyway.encoding属性指定(UTF-8是默认编码)。

如果我们使用任何其他名称(例如customConfig.conf)作为配置文件，那么我们必须在调用 Maven 命令时明确指定：

```plaintext
$ mvn -Dflyway.configFiles=customConfig.conf
```

### 2.4. 系统属性

最后，在命令行调用 Maven 时，所有配置属性也可以指定为系统属性：

```bash
$ mvn -Dflyway.user=databaseUser -Dflyway.password=databasePassword 
  -Dflyway.schemas=schemaName
```

当以多种方式指定配置时，以下是优先顺序：

1.  系统属性
2.  外部配置文件
3.  Maven 属性
4.  插件配置

## 3.示例迁移

在本节中，我们将逐步完成使用 Maven 插件将数据库架构迁移到内存中的 H2 数据库所需的步骤。我们使用外部文件来配置 Flyway。

### 3.1. 更新 POM

首先，让我们添加 H2 作为依赖项：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
</dependency>
```

同样，我们可以检查[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.h2database" AND a%3A"h2")上可用的最新版本的驱动程序。我们还添加了 Flyway 插件，如前所述。

### 3.2. 使用外部文件配置 Flyway

接下来，我们在$PROJECT_ROOT中创建myFlywayConfig.conf ，内容如下：

```xml
flyway.user=databaseUser
flyway.password=databasePassword
flyway.schemas=app-db
flyway.url=jdbc:h2:mem:DATABASE
flyway.locations=filesystem:db/migration
```

上面的配置指定我们的迁移脚本位于db/migration目录下。它使用databaseUser和databasePassword连接到内存中的 H2 实例。

应用程序数据库模式是app-db。

当然，我们将flyway.user、flyway.password、 flyway.url分别替换为自己的数据库用户名、数据库密码、数据库URL。

### 3.3. 定义第一次迁移

Flyway 遵守以下迁移脚本的命名约定：

<前缀><版本>__<描述>.sql

在哪里：

-   <Prefix> – 默认前缀是V，我们可以使用flyway.sqlMigrationPrefix属性在上面的配置文件中更改它。
-   <Version> – 迁移版本号。主要版本和次要版本可以用下划线分隔。迁移版本应始终以 1 开头。
-   <Description> – 迁移的文本描述。双下划线将描述与版本号分开。

示例：V1_1_0__my_first_migration.sql

因此，让我们使用名为V1_0__create_employee_schema.sql的迁移脚本在$PROJECT_ROOT中创建目录db/migration ，其中包含用于创建员工表的 SQL 指令：

```sql
CREATE TABLE IF NOT EXISTS `employee` (

    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(20),
    `email` varchar(50),
    `date_of_birth` timestamp

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;
```

### 3.4. 执行迁移

接下来，我们从$PROJECT_ROOT调用以下 Maven 命令来执行数据库迁移：

```bash
$ mvn clean flyway:migrate -Dflyway.configFiles=myFlywayConfig.conf
```

这应该导致我们第一次成功迁移。

数据库模式现在应该如下所示：

```plaintext
employee:
+----+------+-------+---------------+
| id | name | email | date_of_birth |
+----+------+-------+---------------+
```

我们可以重复定义和执行步骤来做更多的迁移。

### 3.5. 定义和执行第二次迁移

让我们通过创建包含以下两个查询的名为V2_0_create_department_schema.sql的第二个迁移文件来查看第二个迁移的样子：

```sql
CREATE TABLE IF NOT EXISTS `department` (

`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
`name` varchar(20)

)ENGINE=InnoDB DEFAULT CHARSET=UTF8; 

ALTER TABLE `employee` ADD `dept_id` int AFTER `email`;
```

然后我们将像第一次一样执行类似的迁移。

现在我们的数据库架构已经更改为向employee添加一个新列和一个新表：

```plaintext
employee:
+----+------+-------+---------+---------------+
| id | name | email | dept_id | date_of_birth |
+----+------+-------+---------+---------------+
department:
+----+------+
| id | name |
+----+------+
```

最后，我们可以通过调用以下 Maven 命令来验证两次迁移是否确实成功：

```plaintext
$ mvn flyway:info -Dflyway.configFiles=myFlywayConfig.conf
```

## 4. 在 IntelliJ IDEA 中生成版本化迁移

手动编写迁移会花费很多时间；相反，我们可以根据我们的 JPA 实体生成它们。我们可以通过使用名为[JPA Buddy](https://www.baeldung.com/jpa-buddy-post)的 IntelliJ IDEA 插件来实现这一点。

要生成差异版本迁移，只需安装插件并从 JPA Structure 面板调用操作。

我们只需选择要将哪个源(数据库或 JPA 实体)与哪个目标(数据库或数据模型快照)进行比较。然后JPA Buddy 将生成迁移，如动画所示：

[![flyway 迁移生成 jpa](https://www.baeldung.com/wp-content/uploads/2016/09/flyway-migration-generation-jpa.gif)](https://www.baeldung.com/wp-content/uploads/2016/09/flyway-migration-generation-jpa.gif)

JPA Buddy 的另一个优势是能够定义Java和数据库类型之间的映射。此外，它可以与 Hibernate 自定义类型和 JPA 转换器一起正常工作。

## 5. 在Spring Boot中禁用 Flyway

有时我们可能需要在某些情况下禁用 Flyway 迁移。

例如，通常的做法是在测试期间基于实体生成数据库模式。在这种情况下，我们可以在测试配置文件下禁用 Flyway。

让我们看看它在Spring Boot中是多么容易。

### 5.1. 春季启动 1.x

我们需要做的就是在我们的application-test.properties文件中设置flyway.enabled属性：

```plaintext
flyway.enabled=false
```

### 5.2. 春季启动 2.x

在较新版本的Spring Boot中，此属性已更改为spring.flyway.enabled：

```plaintext
spring.flyway.enabled=false
```

### 5.3. 空迁徙路线迁移策略

如果我们只想在启动时禁用自动 Flyway 迁移，但仍然能够手动触发迁移，那么使用上述属性并不是一个好的选择。

那是因为在这种情况下，Spring Boot 将不再自动配置Flyway bean。因此，我们必须自己提供它，这不是很方便。

因此，如果这是我们的用例，我们可以启用 Flyway 并实现一个空的[FlywayMigrationStrategy](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/flyway/FlywayMigrationStrategy.html)：

```java
@Configuration
public class EmptyMigrationStrategyConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // do nothing  
        };
    }
}
```

这将在应用程序启动时有效地禁用 Flyway 迁移。

但是，我们仍然可以手动触发迁移：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ManualFlywayMigrationIntegrationTest {

    @Autowired
    private Flyway flyway;

    @Test
    public void skipAutomaticAndTriggerManualFlywayMigration() {
        flyway.migrate();
    }
}
```

## 6.飞行路线如何运作

为了跟踪我们已经应用了哪些迁移以及何时应用，它向我们的模式添加了一个特殊的簿记表。此元数据表还跟踪迁移校验和，以及迁移是否成功。

该框架执行以下步骤以适应不断发展的数据库模式：

1.  它检查数据库模式以定位其元数据表(默认情况下为SCHEMA_VERSION)。如果元数据表不存在，它将创建一个。
2.  它扫描应用程序类路径以查找可用的迁移。
3.  它将迁移与元数据表进行比较。如果版本号低于或等于标记为当前的版本，它将被忽略。
4.  它将任何剩余的迁移标记为挂起的迁移。这些是根据版本号排序并按顺序执行的。
5.  在应用每次迁移时，元数据表会相应更新。

## 7. 命令

Flyway 支持以下基本命令来管理数据库迁移：

-   信息：打印数据库模式的当前状态/版本。它打印哪些迁移待处理、哪些迁移已应用、已应用迁移的状态以及应用迁移的时间。
-   迁移：将数据库模式迁移到当前版本。它扫描可用迁移的类路径并应用挂起的迁移。
-   Baseline：基于现有数据库，不包括所有迁移，包括baselineVersion。Baseline 有助于从现有数据库中的 Flyway 开始。然后可以正常应用较新的迁移。
-   验证：根据可用迁移验证当前数据库模式。
-   修复：修复元数据表。
-   Clean：删除已配置架构中的所有对象。当然，我们不应该在任何生产数据库上使用clean 。

## 八. 总结

在本文中，我们了解了 Flyway 的工作原理以及我们如何使用该框架可靠地重构我们的应用程序数据库。