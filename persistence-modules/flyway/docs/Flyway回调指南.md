## 1. 简介

Flyway 库允许我们通过跟踪存储为 SQL 源代码的更改来对数据库进行版本控制。每组更改称为迁移。

使用一组命令(包括migrate、clean 、 info 、 validate 、 baseline和repair )将单个迁移按顺序应用于数据库。它们根据目标数据库的当前版本以受控方式应用。

虽然迁移通常足以涵盖大多数用例，但仍有许多场景非常适合回调。

在本文中，我们将使用 Flyway 回调来挂钩它提供的各种命令的生命周期。

## 2.用例场景

我们可能有一个非常具体的要求，需要回调提供的那种灵活性。以下是一些可能的用例：

-   重建物化视图——每当我们应用影响这些视图基表的迁移时，我们可能希望重建物化视图。SQL 回调非常适合执行这种类型的逻辑
-   刷新缓存——也许我们有一个修改恰好被缓存的数据的迁移。我们可以使用回调来刷新缓存，确保我们的应用程序从数据库中提取新数据
-   调用外部系统——使用回调，我们可以使用任意技术调用外部系统。例如，我们可能想要发布事件、发送电子邮件或触发服务器重启

## 3. 支持的回调

每个可用的 Flyway 命令都有相应的前后回调事件。有关这些命令的更多信息，请参阅[我们的主要 Flyway 文章](https://www.baeldung.com/database-migrations-with-flyway)，或[官方文档](https://flywaydb.org/documentation/)。

-   BEFORE_ 事件在操作执行之前触发。

-   AFTER_ 事件在操作成功后触发。这些

    后续

    事件还有一些更细化的事件：

    -   操作失败后将触发 ERROR 等价物。
    -   OPERATION_FINISH 事件在操作完成后触发。

-   migrate和undo也有 _EACH 事件，它是为每个单独的迁移而触发的。migrate和 undo 命令具有这些额外的回调，因为运行这些命令通常会导致执行许多迁移。

回调事件的完整列表可以在[Event](https://flywaydb.org/documentation/usage/api/javadoc/org/flywaydb/core/api/callback/Event.html)类中找到。

例如，clean命令的回调事件是BEFORE_CLEAN和AFTER_CLEAN。Flyway 在clean命令执行之前和之后立即触发它们。

回顾一下我们在介绍中讨论的内容，可用的命令有：migrate、clean、info、validate、 baseline和repair。

Flyway 的作者提供了这些额外的钩子，使我们能够在 Flyway 所使用的最高粒度级别(即单个迁移)上控制自定义回调逻辑。

## 4.依赖关系

为了了解回调在实践中是如何工作的，让我们通过一个简单的例子来研究。我们可以通过在我们的pom.xml中将 flyway-core 声明为依赖项来开始我们的示例：

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>8.5.13</version>
</dependency>
```

我们可以在[Maven Central上找到最新版本的](https://search.maven.org/classic/#search|ga|1|g%3A"org.flywaydb" AND a%3A"flyway-core")flyway-core。

## 5.回调

Flyway 使我们能够使用两种不同的方法(Java 或 SQL)创建回调。前者是最灵活的。它为我们提供了执行任意代码的自由。

后者让我们直接与数据库交互。

### 5.1.Java回调

Java API 协定在回调接口中定义。

在最简单的情况下，要创建自定义回调，我们需要实现回调接口，如我们的ExampleFlywayCallback 所示：

```java
public class ExampleFlywayCallback implements Callback {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_EACH_MIGRATE;
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        if (event == Event.AFTER_EACH_MIGRATE) {
            log.info("> afterEachMigrate");
        }
    }

    @Override
    public String getCallbackName() {
        return ExampleFlywayCallback.class.getSimpleName();
    }
}
```

### 5.2. SQL 回调

SQL 回调协定是通过使用配置为位置的目录中包含的具有特定名称的文件来定义的。Flyway 将在其配置的位置查找 SQL 回调文件并相应地执行它们。

例如，配置为位置的目录中名为beforeEachMigrate.sql的文件将在执行迁移命令期间在每个迁移脚本之前运行。

## 6.配置与执行

在以下示例中，我们配置了Java回调并指定了两个 SQL 脚本位置：一个包含我们的迁移，另一个包含 SQL 回调。

没有必要为迁移和 SQL 回调配置单独的位置，但我们在示例中以这种方式设置它以演示如何将它们分开：

```java
@Test
public void migrateWithSqlAndJavaCallbacks() {
    Flyway flyway = Flyway.configure()
      .dataSource(dataSource)
      .locations("db/migration", "db/callbacks")
      .callbacks(new ExampleFlywayCallback())
      .load();
    flyway.migrate();
}
```

如果我们在Java和 SQL 中都定义了beforeEachMigrate，了解Java回调将首先执行并紧接着执行 SQL 回调会很有帮助。

这可以在上述测试的输出中看到：

```shell
21:50:45.677 [main] INFO  c.b.f.FlywayApplicationUnitTest - > migrateWithSqlAndJavaCallbacks
21:50:45.848 [main] INFO  o.f.c.i.license.VersionPrinter - Flyway Community Edition 8.0.0 by Redgate
21:50:45.849 [main] INFO  o.f.c.i.d.base.BaseDatabaseType - Database: jdbc:h2:mem:DATABASE (H2 1.4)
21:50:45.938 [main] INFO  o.f.core.internal.command.DbValidate - Successfully validated 2 migrations (execution time 00:00.021s)
21:50:45.951 [main] INFO  o.f.c.i.s.JdbcTableSchemaHistory - Creating Schema History table "PUBLIC"."flyway_schema_history" ...
21:50:46.003 [main] INFO  o.f.c.i.c.SqlScriptCallbackFactory - Executing SQL callback: beforeMigrate - 
21:50:46.015 [main] INFO  o.f.core.internal.command.DbMigrate - Current version of schema "PUBLIC": << Empty Schema >>
21:50:46.023 [main] INFO  o.f.c.i.c.SqlScriptCallbackFactory - Executing SQL callback: beforeEachMigrate - 
21:50:46.024 [main] INFO  o.f.core.internal.command.DbMigrate - Migrating schema "PUBLIC" to version "1.0 - add table one"
21:50:46.025 [main] INFO  c.b.f.ExampleFlywayCallback - > afterEachMigrate
21:50:46.046 [main] INFO  o.f.c.i.c.SqlScriptCallbackFactory - Executing SQL callback: beforeEachMigrate - 
21:50:46.046 [main] INFO  o.f.core.internal.command.DbMigrate - Migrating schema "PUBLIC" to version "1.1 - add table two"
21:50:46.047 [main] INFO  c.b.f.ExampleFlywayCallback - > afterEachMigrate
21:50:46.067 [main] INFO  o.f.core.internal.command.DbMigrate - Successfully applied 2 migrations to schema "PUBLIC", now at version v1.1 (execution time 00:00.060s)
```

## 七. 总结

在本文中，我们研究了如何在Java和 SQL 中使用 Flyway 回调机制。我们查看了可能的用例并详细说明了示例。