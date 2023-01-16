## 1. 概述

Flyway 迁移并不总是按计划进行。在本教程中，我们将探索从失败的迁移中恢复的选项。

## 2.设置

让我们从一个基本的 Flyway 配置的Spring Boot项目开始。它具有[flyway-core](https://search.maven.org/search?q=g:org.flywaydb AND a:flyway-core)、[spring-boot-starter-jdbc](https://search.maven.org/search?q=a:spring-boot-starter-jdbc)和flyway [-maven-plugin](https://search.maven.org/search?q=g:org.flywaydb AND a:flyway-maven-plugin) 依赖项。

更多配置细节请参考我们[介绍 Flyway](https://www.baeldung.com/database-migrations-with-flyway)的文章。

### 2.1. 配置

首先，让我们添加两个不同的配置文件。这将使我们能够轻松地针对不同的数据库引擎运行迁移：

```xml
<profile>
    <id>h2</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <dependencies>
        <dependency>
            <groupId>com.h2database</groupId>
	    <artifactId>h2</artifactId>
        </dependency>
    </dependencies>
</profile>
<profile>
    <id>postgre</id>
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
    </dependencies>
</profile>
```

我们还为每个配置文件添加 Flyway 数据库配置文件。

首先，我们创建application-h2.properties：

```markdown
flyway.url=jdbc:h2:file:./testdb;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE;MODE=MySQL;DATABASE_TO_UPPER=false;
flyway.user=testuser
flyway.password=password
```

之后，让我们创建 PostgreSQL application-postgre.properties：

```markdown
flyway.url=jdbc:postgresql://127.0.0.1:5431/testdb
flyway.user=testuser
flyway.password=password
```

注意：我们可以调整 PostgreSQL 配置以匹配现有数据库，也可以使用代码示例中[的docker-compose文件](https://github.com/eugenp/tutorials/tree/master/persistence-modules/flyway)。

### 2.2. 迁移

让我们添加第一个迁移文件V1_0__add_table.sql：

```sql
create table table_one (
  id numeric primary key
);
```

现在让我们添加第二个包含错误的迁移文件V1_1__add_table.sql：

```sql
create table <span style="color: #ff0000">table_one</span> (
  id numeric primary key
);
```

我们故意犯了一个错误，使用了相同的表名。这应该会导致 Flyway 迁移错误。

## 3.运行迁移

现在，让我们运行应用程序并尝试应用迁移。

首先是默认的h2配置文件：

```bash
mvn spring-boot:run
```

然后对于postgre配置文件：

```bash
mvn spring-boot:run -Ppostgre
```

不出所料，第一次迁移成功，而第二次失败：

```bash
Migration V1_1__add_table.sql failed
...
Message    : Table "TABLE_ONE" already exists; SQL statement:
```

### 3.1. 检查状态

在继续修复数据库之前，让我们通过运行来检查 Flyway 迁移状态：

```bash
mvn flyway:info -Ph2
```

如预期的那样返回：

```bash
+-----------+---------+-------------+------+---------------------+---------+
| Category  | Version | Description | Type | Installed On        | State   |
+-----------+---------+-------------+------+---------------------+---------+
| Versioned | 1.0     | add table   | SQL  | 2020-07-17 12:57:35 | Success |
| Versioned | 1.1     | add table   | SQL  | 2020-07-17 12:57:35 | <span style="color: #ff0000">Failed</span>  |
+-----------+---------+-------------+------+---------------------+---------+

```

但是当我们检查 PostgreSQL 的状态时：

```bash
mvn flyway:info -Ppostgre
```

我们注意到第二次迁移的状态是Pending而不是Failed：

```bash
+-----------+---------+-------------+------+---------------------+---------+
| Category  | Version | Description | Type | Installed On        | State   |
+-----------+---------+-------------+------+---------------------+---------+
| Versioned | 1.0     | add table   | SQL  | 2020-07-17 12:57:48 | Success |
| Versioned | 1.1     | add table   | SQL  |                     | <span style="color: #339966">Pending</span> |
+-----------+---------+-------------+------+---------------------+---------+
```

不同之处在于PostgreSQL 支持 DDL 事务，而其他类似 H2 或 MySQL 则不支持。结果，PostgreSQL能够回滚失败迁移的事务。当我们尝试修复数据库时，让我们看看这种差异如何影响事情。

### 3.2. 更正错误并重新运行迁移

让我们通过将表名从table_one更正为 table_two来修复迁移文件V1_1__add_table.sql 。

现在，让我们再次尝试运行该应用程序：

```bash
mvn spring-boot:run -Ph2
```

我们现在注意到 H2 迁移失败并显示：

```bash
Validate failed: 
Detected failed migration to version 1.1 (add table)
```

只要该版本存在已经失败的迁移，Flyway 就不会重新运行1.1 版迁移。

另一方面，postgre配置文件成功运行。如前所述，由于回滚，状态是干净的并准备好应用更正的迁移。

实际上，通过运行mvn flyway:info -Ppostgre我们可以看到两个迁移都成功应用了。因此，总而言之，对于 PostgreSQL，我们所要做的就是更正我们的迁移脚本并重新触发迁移。

## 4.手动修复数据库状态

修复数据库状态的第一种方法是从flyway_schema_history表中手动删除 Flyway 条目。

让我们简单地对数据库运行这个 SQL 语句：

```sql
delete from flyway_schema_history where version = '1.1';
```

现在，当我们再次运行mvn spring-boot:run时，我们看到迁移已成功应用。

但是，直接操作数据库可能并不理想。那么，让我们看看我们还有哪些其他选择。

## 5.飞路修复

### 5.1. 修复失败的迁移

让我们继续添加另一个损坏的迁移V1_2__add_table.sql文件，运行应用程序并返回到迁移失败的状态。

修复数据库状态的另一种方法是使用[flyway:repair](https://flywaydb.org/documentation/command/repair)工具。更正 SQL 文件后，我们可以运行：

```bash
mvn flyway:repair
```

这将导致：

```bash
Successfully repaired schema history table "PUBLIC"."flyway_schema_history"
```

在幕后，Flyway 只是从flyway_schema_history表中删除失败的迁移条目。

现在，我们可以再次运行flyway:info并看到上次迁移的状态从Failed变为Pending。

让我们再次运行该应用程序。如我们所见，现在已成功应用更正后的迁移。

### 5.2. 重新对齐校验和

通常建议永远不要更改成功应用的迁移。但可能存在无法解决的情况。

因此，在这种情况下，让我们通过在文件开头添加注解来更改迁移V1_1__add_table.sql 。

现在运行应用程序，我们看到“迁移校验和不匹配”错误消息，如：

```markdown
Migration checksum mismatch for migration version 1.1
-> Applied to database : 314944264
-> Resolved locally    : 1304013179
```

发生这种情况是因为我们更改了一个已经应用的迁移并且 Flyway 检测到不一致。

为了重新对齐校验和，我们可以使用相同的flyway:repair命令。但是，这次不会执行任何迁移。只有flyway_schema_history表中版本 1.1条目的校验和将被更新以反映更新的迁移文件。

修复后再次运行该应用程序，我们注意到该应用程序现在已成功启动。

请注意，在这种情况下，我们通过 Maven使用了flyway:repair 。另一种方法是安装 Flyway 命令行工具并运行[flyway repair](https://flywaydb.org/documentation/command/repair)。效果是一样的：flyway repair将从flyway_schema_history表中删除失败的迁移，并重新对齐已应用迁移的校验和。

## 6.飞路回调

如果我们不想手动干预，我们可以考虑一种方法，在迁移失败后自动清除flyway_schema_history中的失败条目。为此，我们可以使用afterMigrateError Flyway [回调](https://www.baeldung.com/flyway-callbacks)。

我们首先创建 SQL 回调文件db/callback/afterMigrateError__repair.sql：

```sql
DELETE FROM flyway_schema_history WHERE success=false;
```

每当发生迁移错误时，这将自动从 Flyway 状态历史记录中删除任何失败的条目。

让我们创建一个application-callbacks.properties配置文件配置，它将在 Flyway 位置列表中包含db/callback文件夹：

```bash
spring.flyway.locations=classpath:db/migration,classpath:db/callback
```

现在，在添加另一个损坏的迁移V1_3__add_table.sql 之后，我们运行包含回调配置文件的应用程序：

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2,callbacks
...
Migrating schema "PUBLIC" to version 1.3 - add table
Migration of schema "PUBLIC" to version 1.3 - add table failed!
...
Executing SQL callback: afterMigrateError - repair

```

正如预期的那样，迁移失败但afterMigrateError回调运行并清理了flyway_schema_history。

只需更正V1_3__add_table.sql迁移文件并再次运行应用程序就足以应用于已更正的迁移。

## 七. 总结

在本文中，我们研究了从失败的 Flyway 迁移中恢复的不同方法。

我们看到像 PostgreSQL 这样的数据库——即支持 DDL 事务的数据库——如何不需要额外的努力来修复 Flyway 数据库状态。

另一方面，对于像 H2 这样没有这种支持的数据库，我们看到了如何使用 Flyway 修复来清理 Flyway 历史并最终应用更正的迁移。