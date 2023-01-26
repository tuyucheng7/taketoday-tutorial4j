## **一、概述**

在本教程中，我们将学习使用 Flyway 进行数据库迁移的基础知识，并查看需要乱序运行迁移时的特定用例。

## **2.飞路介绍**

**Flyway 是一个帮助通过迁移进行数据库版本控制的工具。**我们可以创建改变数据库状态的脚本。他们被称为迁移。

有几种情况我们需要迁移。例如，我们可能需要从以前的数据源填充我们的数据库。或者我们有一个已经使用数据库的已发布应用程序，我们需要部署一个依赖修改后的数据库架构的新版本。在这两种情况下，我们都可以使用迁移来达到预期的结果。

**使用 Flyway，我们甚至可以将这些脚本上传到版本控制系统，以便我们能够跟踪何时以及为何需要引入特定的修改。**

**在此示例中，我们将使用[版本化迁移](https://flywaydb.org/documentation/concepts/migrations#versioned-migrations)。**换句话说，我们将为每个迁移脚本分配一个版本来确定它们的顺序。

## **3.示例迁移**

在我们的示例中，我们将以 Flyway 作为起点使用一个简单的 Spring Boot 应用程序。

### **3.1. Maven插件**

首先，让我们将[Flyway Maven 插件](https://mvnrepository.com/artifact/org.flywaydb/flyway-maven-plugin)添加到我们的*pom.xml*中：

```xml
<build>
    ...
    <plugin>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-maven-plugin</artifactId>
        <version>8.5.13</version>
    </plugin>
    ...
</build>复制
```

我们需要这个[插件](https://flywaydb.org/documentation/usage/maven/)来运行不同的 Flyway 目标。但是，我们需要先[配置插件](https://www.baeldung.com/database-migrations-with-flyway#1-plugin-configuration)才能使用它。这可以包括设置数据库 URL、用户名和密码等。

### **3.2. 迁移脚本**

***让我们在db/out-of-order-migration\*目录中的项目中创建两个 SQL 迁移。**我们必须遵循这些文件的[命名约定](https://www.baeldung.com/database-migrations-with-flyway#3-define-first-migration)。让我们将第一个脚本命名为*V1_0__create_city_table.sql*：

```sql
create table city (
  id numeric,
  name varchar(50),
  constraint pk_city primary key (id)
);复制
```

然后，创建另一个名为*V2_0__create_person_table.sql*的文件：

```sql
create table person (
  id numeric,
  name varchar(50),
  constraint pk_person primary key (id)
);复制
```

让我们执行这些迁移：

```bash
mvn -Dflyway.user=sa -Dflyway.url=jdbc:h2:file:./database -Dflyway.locations=filesystem:db/out-of-order-migration flyway:migrate复制
```

**在此命令中，我们使用了flyway 插件的[\*migrate\*目标](https://flywaydb.org/documentation/usage/maven/migrate)**以及与数据库相关的三个参数。首先，我们设置用户名，然后设置数据库所在的 URL，最后设置迁移脚本的位置。

作为此命令的结果，Flyway 成功运行了我们的两个脚本。我们甚至可以检查状态：

```bash
mvn -Dflyway.user=sa -Dflyway.url=jdbc:h2:file:./database -Dflyway.locations=filesystem:db/out-of-order-migration flyway:info复制
```

这将打印以下消息：

```plaintext
Schema version: 2.0
+-----------+---------+---------------------+------+---------------------+---------+
| Category  | Version | Description         | Type | Installed On        | State   |
+-----------+---------+---------------------+------+---------------------+---------+
| Versioned | 1.0     | create city table   | SQL  | 2023-01-02 21:08:45 | Success |
| Versioned | 2.0     | create person table | SQL  | 2023-01-02 21:08:45 | Success |
+-----------+---------+---------------------+------+---------------------+---------+
复制
```

## **4.乱序迁移**

当我们添加新的迁移时，Flyway 可以检测到更改并执行最新的迁移。**但是，当最新脚本的版本号不是最高时，就会出现问题。**换句话说，它不正常。

默认情况下，Flyway 会忽略我们最新的迁移。幸运的是，我们能够解决这个问题。**我们可以使用[\*outOfOrder\*配置参数](https://flywaydb.org/documentation/configuration/parameters/outOfOrder)来告诉 Flyway 运行这些脚本而不是跳过它们。**

让我们在我们的项目中尝试添加一个名为*V1_1__add_zipcode_to_city.sql*的新迁移：

```sql
alter table city add column (
  zip varchar(10)
);复制
```

这个脚本的版本是1.1，但是根据Flyway的说法，我们已经迁移到2.0版本了。这意味着该脚本将被忽略。我们甚至可以使用*info*命令检查它：

```bash
mvn -Dflyway.user=sa -Dflyway.url=jdbc:h2:file:./database -Dflyway.locations=filesystem:db/out-of-order-migration flyway:info复制
```

Flyway 识别脚本，但状态被忽略：

```plaintext
+-----------+---------+---------------------+------+---------------------+---------+
| Category  | Version | Description         | Type | Installed On        | State   |
+-----------+---------+---------------------+------+---------------------+---------+
| Versioned | 1.0     | create city table   | SQL  | 2023-01-02 21:08:45 | Success |
| Versioned | 2.0     | create person table | SQL  | 2023-01-02 21:08:45 | Success |
| Versioned | 1.1     | add zipcode to city | SQL  |                     | Ignored |
+-----------+---------+---------------------+------+---------------------+---------+
复制
```

**现在，如果我们再次获取状态但添加\*outOfOrder\*标志，结果会有所不同：**

```bash
mvn -Dflyway.user=sa -Dflyway.url=jdbc:h2:file:./database -Dflyway.locations=filesystem:db/out-of-order-migration -Dflyway.outOfOrder=true flyway:info复制
```

最新的迁移状态变为pending：

```plaintext
+-----------+---------+---------------------+------+---------------------+---------+
| Category  | Version | Description         | Type | Installed On        | State   |
+-----------+---------+---------------------+------+---------------------+---------+
| Versioned | 1.0     | create city table   | SQL  | 2023-01-02 21:08:45 | Success |
| Versioned | 2.0     | create person table | SQL  | 2023-01-02 21:08:45 | Success |
| Versioned | 1.1     | add zipcode to city | SQL  |                     | Pending |
+-----------+---------+---------------------+------+---------------------+---------+
复制
```

这意味着我们可以运行迁移命令并应用更改。**不过，我们还必须在此处添加\*outOfOrder\*标志**：

```bash
mvn -Dflyway.user=sa -Dflyway.url=jdbc:h2:file:./database -Dflyway.locations=filesystem:db/out-of-order-migration -Dflyway.outOfOrder=true flyway:migrate复制
```

我们成功执行了新的更改：

```plaintext
[INFO] Successfully validated 3 migrations (execution time 00:00.015s)
[INFO] Current version of schema "PUBLIC": 2.0
[WARNING] outOfOrder mode is active. Migration of schema "PUBLIC" may not be reproducible.
[INFO] Migrating schema "PUBLIC" to version "1.1 - add zipcode to city" [out of order]
[INFO] Successfully applied 1 migration to schema "PUBLIC", now at version v1.1 (execution time 00:00.019s)复制
```

**这些类型的迁移在被 Flyway 应用后具有不同的状态。**我们的前两个迁移处于“成功”状态，但第三个迁移是“乱序”，即使它是成功的：

```plaintext
+-----------+---------+---------------------+------+---------------------+--------------+
| Category  | Version | Description         | Type | Installed On        | State        |
+-----------+---------+---------------------+------+---------------------+--------------+
| Versioned | 1.0     | create city table   | SQL  | 2023-01-02 21:08:45 | Success      |
| Versioned | 2.0     | create person table | SQL  | 2023-01-02 21:08:45 | Success      |
| Versioned | 1.1     | add zipcode to city | SQL  | 2023-01-02 21:17:38 | Out of Order |
+-----------+---------+---------------------+------+---------------------+--------------+复制
```

## **5.结论**

在本教程中，我们看到了 Flyway 迁移的简要介绍，然后重点介绍了一个特定的用例。**我们找到了一种方法来运行根据版本号被认为是乱序的迁移。**然后，我们将这个解决方案应用到我们的项目中。