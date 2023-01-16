## 1. 简介

在这个简短的教程中，我们将探索几种使用 [Flyway 回滚迁移的方法。](https://www.baeldung.com/database-migrations-with-flyway)

## 2. 用迁移模拟回滚

在本节中，我们将使用标准迁移文件回滚我们的数据库。

在我们的示例中，我们将使用 Flyway 的命令行版本。但是，核心原则同样适用于其他格式，例如核心 API、Maven 插件等。

### 2.1. 创建迁移

首先，让我们向数据库中添加一个新的book表。为此，我们将创建一个名为 V1_0__create_book_table.sql的迁移文件：

```sql
create table book (
  id numeric,
  title varchar(128),
  author varchar(256),
  constraint pk_book primary key (id)
);
```

其次，让我们应用迁移：

```bash
./flyway migrate
```

### 2.2. 模拟回滚

然后，在某个时候，说我们需要撤销上次迁移。

为了将数据库恢复到创建book表之前的状态，让我们创建名为 V2_0__drop_table_book.sql的迁移：

```sql
drop table book;
```

接下来，让我们应用迁移：

```bash
./flyway migrate
```

最后，我们可以使用以下方法检查所有迁移的历史记录：

```bash
./flyway info
```

这给了我们以下输出：

```bash
+-----------+---------+-------------------+------+---------------------+---------+
| Category  | Version | Description       | Type | Installed On        | State   |
+-----------+---------+-------------------+------+---------------------+---------+
| Versioned | 1.0     | create book table | SQL  | 2020-08-29 16:07:43 | Success |
| Versioned | 2.0     | drop table book   | SQL  | 2020-08-29 16:08:15 | Success |
+-----------+---------+-------------------+------+---------------------+---------+
```

请注意，我们的第二次迁移已成功运行。

就 Flyway 而言，第二个迁移文件只是另一个标准迁移。实际将数据库恢复到以前的版本完全是通过 SQL 完成的。例如，在我们的例子中，删除表的 SQL 与创建表的第一次迁移相反。

使用此方法，审计跟踪不会向我们显示第二次迁移与第一次迁移相关，因为它们具有不同的版本号。为了获得这样的审计线索，我们需要使用 Flyway Undo。

## 3.使用Flyway撤消

首先，需要注意的是，Flyway Undo 是 Flyway 的商业功能，在社区版中不可用。因此，我们需要专业版或企业版才能使用此功能。

### 3.1. 创建迁移文件

首先，让我们创建一个名为 V1_0__create_book_table.sql的迁移文件：

```sql
create table book (
  id numeric,
  title varchar(128),
  author varchar(256),
  constraint pk_book primary key (id)
);
```

其次，我们创建对应的撤销迁移文件U1_0__create_book_table.sql：

```sql
drop table book;
```

在我们的撤消迁移中，请注意文件名前缀是“U”，而正常的迁移前缀是“V”。另外，在我们的undo迁移文件中，我们写了反转对应迁移文件的变化的SQL。在我们的例子中，我们将删除由正常迁移创建的表。

### 3.2. 应用迁移

接下来，让我们检查迁移的当前状态：

```bash
./flyway -pro info
```

这为我们提供了以下输出：

```bash
+-----------+---------+-------------------+------+--------------+---------+----------+
| Category  | Version | Description       | Type | Installed On | State   | Undoable |
+-----------+---------+-------------------+------+--------------+---------+----------+
| Versioned | 1.0     | create book table | SQL  |              | Pending | Yes      |
+-----------+---------+-------------------+------+--------------+---------+----------+

```

请注意最后一列Undoable，它表示 Flyway 检测到一个伴随我们正常迁移文件的撤消迁移文件。

接下来，让我们应用我们的迁移：

```bash
./flyway migrate
```

当它完成时，我们的迁移就完成了，我们的模式有一个新的书表：

```bash
                List of relations
 Schema |         Name          | Type  |  Owner   
--------+-----------------------+-------+----------
 public | book                  | table | baeldung
 public | flyway_schema_history | table | baeldung
(2 rows)

```

### 3.3. 回滚上次迁移

最后，让我们使用命令行撤销上次的迁移：

```bash
./flyway -pro undo
```

命令成功运行后，我们可以再次检查迁移的状态：

```bash
./flyway -pro info
```

这给了我们以下输出：

```bash
+-----------+---------+-------------------+----------+---------------------+---------+----------+
| Category  | Version | Description       | Type     | Installed On        | State   | Undoable |
+-----------+---------+-------------------+----------+---------------------+---------+----------+
| Versioned | 1.0     | create book table | SQL      | 2020-08-22 15:48:00 | Undone  |          |
| Undo      | 1.0     | create book table | UNDO_SQL | 2020-08-22 15:49:47 | Success |          |
| Versioned | 1.0     | create book table | SQL      |                     | Pending | Yes      |
+-----------+---------+-------------------+----------+---------------------+---------+----------+
```

请注意撤消是如何成功的，并且第一次迁移返回到挂起状态。此外，与第一种方法相比，审计跟踪清楚地显示了被回滚的迁移。

尽管 Flyway Undo 很有用，[但它假定整个迁移已成功。](https://flywaydb.org/documentation/command/undo)例如，如果迁移中途失败，它可能无法按预期工作。

## 4. 总结

在这个简短的教程中，我们研究了使用标准迁移来恢复我们的数据库。我们还查看了使用 Flyway Undo 回滚迁移的官方方法。