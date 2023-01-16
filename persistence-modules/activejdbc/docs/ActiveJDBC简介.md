## 1. 简介

ActiveJDBC 是一个轻量级 ORM，它遵循ActiveRecord的核心思想，是 Ruby on Rails 的主要 ORM。

它专注于通过删除典型持久性管理器的额外层来简化与数据库的交互，并专注于 SQL 的使用而不是创建新的查询语言。

此外，它还提供了自己的方式来通过DBSpec类为数据库交互编写单元测试。

让我们看看这个库与其他流行的JavaORM 有何不同以及如何使用它。

## 2. ActiveJDBC 与其他 ORM

与大多数其他JavaORM 相比，ActiveJDBC 有明显的不同。它从数据库中推断出 DB 模式参数，从而消除了将实体映射到基础表的需要。

没有会话，没有持久性管理器，不需要学习新的查询语言，没有 getters/setters。库本身在大小和依赖项数量方面都很轻。

此实现鼓励使用在执行测试后由框架清理的测试数据库，从而降低维护测试数据库的成本。

但是，每当我们创建或更新模型时，都需要一些额外的[检测步骤。](http://javalite.io/instrumentation)我们将在接下来的部分中对此进行讨论。

## 三、设计原则

-   从数据库推断元数据
-   基于约定的配置
-   没有会话，没有“附加、重新附加”
-   轻量级模型，简单的 POJO
-   没有代理
-   避免贫血领域模型
-   不需要 DAO 和 DTO

## 4. 设置图书馆

使用 MySQL 数据库的典型 Maven 设置包括：

```xml
<dependency>
    <groupId>org.javalite</groupId>
    <artifactId>activejdbc</artifactId>
    <version>1.4.13</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.34</version>
</dependency>
```

可以在 Maven 中央存储库中找到最新版本的[activejdbc](https://search.maven.org/classic/#search|gav|1|g%3A"org.javalite" AND a%3A"activejdbc")和[mysql 连接器工件。](https://search.maven.org/classic/#search|gav|1|g%3A"mysql" AND a%3A"mysql-connector-java")

[使用ActiveJDBC](http://javalite.io/instrumentation)项目时，检测是简化和需要的代价。

有一个检测插件需要在项目中配置：

```xml
<plugin>
    <groupId>org.javalite</groupId>
    <artifactId>activejdbc-instrumentation</artifactId>
    <version>1.4.13</version>
    <executions>
        <execution>
            <phase>process-classes</phase>
            <goals>
                <goal>instrument</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

最新的[activejdbc-instrumentation](https://search.maven.org/classic/#search|gav|1|g%3A"org.javalite" AND a%3A"activejdbc-instrumentation")插件也可以在 Maven Central 中找到。

现在，我们可以通过执行以下两个命令之一来处理检测：

```plaintext
mvn process-classes
mvn activejdbc-instrumentation:instrument
```

## 5. 使用 ActiveJDBC

### 5.1. 该模型

我们可以只用一行代码创建一个简单的模型——它涉及扩展Model类。

该库使用英语的词形[变化来实现名词的复数和单数形式的转换。](http://javalite.io/english_inflections)这可以使用@Table注解覆盖。

让我们看看一个简单的模型是怎样的：

```java
import org.javalite.activejdbc.Model;

public class Employee extends Model {}
```

### 5.2. 连接到数据库

提供了两个类——Base和DB——来连接数据库。

连接到数据库的最简单方法是：

```java
Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://host/organization", "user", "xxxxx");
```

当模型运行时，它们使用在当前线程中找到的连接。在任何 DB 操作之前，该连接由Base或DB类放在本地线程上。

上述方法允许更简洁的 API，消除了对 DB Session 或 Persistence 管理器的需求，就像在其他JavaORM 中一样。

让我们看看如何使用DB类连接到数据库：

```java
new DB("default").open(
  "com.mysql.jdbc.Driver", 
  "jdbc:mysql://localhost/dbname", 
  "root", 
  "XXXXXX");
```

如果我们看一下Base和DB在连接数据库时的使用方式有何不同，它可以帮助我们得出总结，如果在单个数据库上操作，则应使用Base ，而应在多个数据库上使用DB。

### 5.3. 插入记录

向数据库中添加一条记录非常简单。如前所述，不需要 setter 和 getter：

```java
Employee e = new Employee();
e.set("first_name", "Hugo");
e.set("last_name", "Choi");
e.saveIt();
```

或者，我们可以这样添加相同的记录：

```java
Employee employee = new Employee("Hugo","Choi");
employee.saveIt();
```

甚至，流利地说：

```java
new Employee()
 .set("first_name", "Hugo", "last_name", "Choi")
 .saveIt();
```

### 5.4. 更新记录

下面的代码片段显示了如何更新记录：

```java
Employee employee = Employee.findFirst("first_name = ?", "Hugo");
employee
  .set("last_name","Choi")
  .saveIt();
```

### 5.5. 删除记录

```java
Employee e = Employee.findFirst("first_name = ?", "Hugo");
e.delete();
```

如果需要删除所有记录：

```java
Employee.deleteAll();
```

如果我们想从级联到子表的主表中删除记录，请使用deleteCascade：

```java
Employee employee = Employee.findFirst("first_name = ?","Hugo");
employee.deleteCascade();
```

### 5.6. 获取记录

让我们从数据库中获取一条记录：

```java
Employee e = Employee.findFirst("first_name = ?", "Hugo");
```

如果我们想获取多条记录，我们可以使用where方法：

```java
List<Employee> employees = Employee.where("first_name = ?", "Hugo");
```

## 6.交易支持

在JavaORM 中，存在显式连接或管理器对象(JPA 中的 EntityManager、Hibernate 中的 SessionManager 等)。ActiveJDBC 中没有这样的东西。

调用Base.open()打开一个连接，将其附加到当前线程，因此所有模型的所有后续方法都会重用此连接。调用Base.close()关闭连接并将其从当前线程中删除。

要管理事务，有几个方便的调用：

开始交易：

```java
Base.openTransaction();
```

提交交易：

```java
Base.commitTransaction();
```

回滚事务：

```java
Base.rollbackTransaction();
```

## 7. 支持的数据库

最新版本支持SQLServer、MySQL、Oracle、PostgreSQL、H2、SQLite3、DB2等数据库。

## 八. 总结

在本快速教程中，我们关注并探索了 ActiveJDBC 的基础知识。