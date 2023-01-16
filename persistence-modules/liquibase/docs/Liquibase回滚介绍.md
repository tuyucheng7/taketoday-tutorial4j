## 1. 概述

在我们[之前的文章中](https://www.baeldung.com/liquibase-refactor-schema-of-java-app)，我们展示了 Liquibase 作为管理数据库模式和数据的工具。

在本文中，我们将更多地研究回滚功能——以及我们如何撤消 Liquibase 操作。

自然，这是任何生产级系统的关键特性。

## 2. Liquibase迁移的分类

有两类 Liquibase 操作，导致生成不同的回滚语句：

-   自动的，其中迁移可以确定性地生成回滚所需的步骤
-   manual，我们需要发出回滚命令，因为迁移指令不能用于确定性地识别语句

例如，“创建表”语句的回滚将是“删除”已创建的表。这可以毫无疑问地确定，因此可以自动生成回滚语句。

另一方面，无法确定“drop table”命令的回滚语句。无法确定表的最后状态，因此无法自动生成回滚语句。这些类型的迁移语句需要手动回滚指令。

## 3. 写一个简单的回滚语句

让我们编写一个简单的变更集，它将在执行时创建一个表并向变更集添加回滚语句：

```xml
<changeSet id="testRollback" author="baeldung">
    <createTable tableName="baeldung_turorial">
        <column name="id" type="int"/>
        <column name="heading" type="varchar(36)"/>
        <column name="author" type="varchar(36)"/>
    </createTable>
    <rollback>
        <dropTable tableName="baeldung_test"/>
    </rollback>
</changeSet>
```

上面的例子属于上面提到的第一类。如果我们不添加，它将自动创建一个回滚语句。但是我们可以通过创建回滚语句来覆盖默认行为。

我们可以使用以下命令运行迁移：

```bash
mvn liquibase:update
```

执行后我们可以使用以下方法回滚操作：

```bash
mvn liquibase:rollback
```

这将执行变更集的回滚段，并且应该恢复在更新阶段完成的任务。但是如果我们单独发出这个命令，构建就会失败。

原因是——我们没有指定回滚的限制；通过回滚到初始阶段，数据库将被完全清除。因此，当条件满足时，必须定义以下三个约束之一来限制回滚操作：

-   回滚标签
-   回滚计数
-   回滚日期

### 3.1. 回滚到标签

我们可以将数据库的特定状态定义为标签。因此，我们可以参考那个状态。回滚到标签名称“1.0”看起来像：

```bash
mvn liquibase:rollback -Dliquibase.rollbackTag=1.0
```

这将执行在标签“1.0”之后执行的所有变更集的回滚语句。

### 3.2. 按计数回滚

在这里，我们定义需要回滚多少个变更集。如果我们将它定义为一个，最后执行的变更集将被回滚：

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

### 3.3. 回滚到现在

我们可以将回滚目标设置为日期，因此，在该日期之后执行的任何变更集都将回滚：

```bash
mvn liquibase:rollback "-Dliquibase.rollbackDate=Jun 03, 2017"
```

日期格式必须是 ISO 数据格式或应匹配执行平台的DateFormat.getDateInstance()的值。

## 4.回滚变更集选项

让我们探讨一下回滚语句在变更集中的可能用法。

### 4.1. 多语句回滚

单个回滚标记可能包含多个要执行的指令：

```bash
<changeSet id="multiStatementRollback" author="baeldung">
    <createTable tableName="baeldung_tutorial2">
        <column name="id" type="int"/>
        <column name="heading" type="varchar(36)"/>
    </createTable>
    <createTable tableName="baeldung_tutorial3">
        <column name="id" type="int"/>
        <column name="heading" type="varchar(36)"/>
    </createTable>
    <rollback>
        <dropTable tableName="baeldung_tutorial2"/>
        <dropTable tableName="baeldung_tutorial3"/>
    </rollback>
</changeSet>
```

在这里，我们将两个表放在同一个回滚标记中。我们也可以将任务拆分为多个语句。

### 4.2. 多个回滚标签

在一个变更集中，我们可以有多个回滚标签。它们按照变更集中出现的顺序执行：

```bash
<changeSet id="multipleRollbackTags" author="baeldung">
    <createTable tableName="baeldung_tutorial4">
        <column name="id" type="int"/>
        <column name="heading" type="varchar(36)"/>
    </createTable>
    <createTable tableName="baeldung_tutorial5">
        <column name="id" type="int"/>
        <column name="heading" type="varchar(36)"/>
    </createTable>
    <rollback>
        <dropTable tableName="baeldung_tutorial4"/>
    </rollback>
    <rollback>
        <dropTable tableName="baeldung_tutorial5"/>
    </rollback>
</changeSet>

```

### 4.3. 引用另一个变更集进行回滚

如果我们要更改数据库的某些细节，我们可以引用另一个变更集，可能是原始变更集。这将减少代码重复并可以正确还原已完成的更改：

```bash
<changeSet id="referChangeSetForRollback" author="baeldung">
    <dropTable tableName="baeldung_tutorial2"/>
    <dropTable tableName="baeldung_tutorial3"/>
    <rollback changeSetId="multiStatementRollback" changeSetAuthor="baeldung"/>
</changeSet>
```

### 4.4. 空回滚标记

默认情况下，如果我们没有提供，Liquibase 会尝试生成回滚脚本。如果我们需要打破这个特性，我们可以有空的回滚标签，这样回滚操作就不会被恢复：

```bash
<changeSet id="emptyRollback" author="baeldung">
    <createTable tableName="baeldung_tutorial">
        <column name="id" type="int"/>
        <column name="heading" type="varchar(36)"/>
        <column name="author" type="varchar(36)"/>
    </createTable>
    <rollback/>
</changeSet>
```

## 5.回滚命令选项

除了将数据库回滚到以前的状态之外，Liquibase 还可以以多种不同的方式使用。这些是生成回滚 SQL，创建未来的回滚脚本，最后，我们可以一步测试迁移和回滚。

### 5.1. 生成回滚脚本

与回滚一样，我们手头有三个选项来生成回滚 SQL。那些是：

-   rollbackSQL <tag> – 用于将数据库回滚到上述标记的脚本
-   rollbackToDateSQL <date/time> – 一个 SQL 脚本，用于将数据库回滚到上述日期/时间的状态
-   rollbackCountSQL <value> – 一个 SQL 脚本，用于将数据库回滚到之前步骤数所提到的状态

让我们看一个实际的例子：

```bash
mvn liquibase:rollbackCountSQL 2
```

### 5.2. 生成未来回滚脚本

此命令生成所需的回滚 SQL 命令，以将数据库从状态(此时有资格运行的变更集已完成)恢复到当前状态。如果需要为我们即将执行的更改提供回滚脚本，这将非常有用：

如果需要为我们即将执行的更改提供回滚脚本，这将非常有用：

```bash
mvn liquibase:futureRollbackSQL

```

### 5.3. 运行更新测试回滚

此命令执行数据库更新，然后回滚变更集以使数据库恢复到当前状态。未来回滚和更新测试回滚都不会在执行完成后改变当前数据库。但是更新测试回滚执行实际迁移，然后将其回滚。

这可用于在不永久更改数据库的情况下测试更新更改的执行：

```bash
mvn liquibase:updateTestingRollback
```

## 六. 总结

在本快速教程中，我们探讨了 Liquibase 回滚功能的一些命令行和变更集特性。