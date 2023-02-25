## 一、概述

在本教程中，我们将学习如何通过正确转义列名中的数据库关键字来避免神秘的[Hibernate](https://www.baeldung.com/learn-jpa-hibernate)异常。

## 2.设置

在我们深入之前，让我们定义一个简单的*BrokenPhoneOrder*实体，在列映射中使用保留关键字：

```java
@Entity
public class BrokenPhoneOrder implements Serializable {
    @Id
    @Column(name = "order")
    String order;
    @Column(name = "where")
    String where;

    <code class="language-sql">// getters and setters }复制
```

一旦我们了解了该实体导致的语法异常的原因，我们就会将它变成一个没有使用有缺陷的数据库关键字的*PhoneOrder*。

## 2. Hibernate 异常——隐藏的根本原因

让我们想象一下，从我们基于 ORM 的应用程序接收到以下异常：

```sql
You have an error <span class="hljs-keyword">in</span> your <span class="hljs-keyword">SQL</span> syntax; <span class="hljs-keyword">check</span> the manual that corresponds <span class="hljs-keyword">to</span> your MySQL server version <span class="hljs-keyword">for</span> the <span class="hljs-keyword">right</span> syntax
复制
```

*当我们尝试将BrokenPhoneOrder*持久化到数据库时，它总是被抛出 ：

```java
@Test
void givenBrokenPhoneOrderWithReservedKeywords_whenNewObjectIsPersisted_thenItFails() {
    BrokenPhoneOrder order = new BrokenPhoneOrder(randomUUID().toString(), "My House");

    assertThatExceptionOfType(PersistenceException.class).isThrownBy(() -> {
        session.persist(order);
        session.flush();
    });
}复制
```

异常本身没有用，所以让我们启用额外的 SQL 语句日志记录来访问实际查询。

我们需要修改*hibernate.cfg.xml*以包含*show_sql*属性：

```xml
<property name="show_sql">true</property>复制
```

对于 *hibernate.properties，*语法略有不同：

```properties
show_sql = true复制
```

现在我们可以看到在异常日志之前记录了以下 SQL 语句：

```
Hibernate：插入 broken_phone_order（where，order）值（？，？）
```

问号只是*订单*的值以及 我们试图创建的*BrokenPhoneOrder*实体 的*位置。*

## 3.实际的SQL错误

我们可能已经能够找出异常的根本原因。但即使没有，让我们选择我们最喜欢的 SQL 编辑器并将捕获的 SQL 与示例值粘贴在一起：

```sql
INSERT INTO broken_phone_order (order, where) VALUES ('some-1', 'somewhere');复制
```

在大多数情况下，我们会看到已经用红色标记的*位置*和*顺序*。如果没有，一旦我们执行它，数据库引擎（在我们的例子中是 MySQL）返回一个错误：

```
ERROR 1064 (42000): 你的 SQL 语法有错误；检查与您的 MySQL 服务器版本对应的手册，了解在第 1 行的“order, where) values ('a', 'b')' 附近使用的正确语法
```

## 4. 数据库关键词

根据 SQL 标准，*ORDER*和 WHERE *都是保留字。*因此，**它们在语言语法中具有特殊的含义**。每个数据库引擎都有相同的基本单词列表，但也可以对其进行扩展。

***我们可以简单地通过用“\*字符包裹它们来删除特殊含义，例如，\*`where`\***。

让我们将它应用到*INSERT*语句并确认这次它通过了：

```sql
INSERT INTO broken_phone_order (`order`, `where`) VALUES ('some-1', 'somewhere');复制
```

## 5. Hibernate 中的保留字

问题是：如果 Hibernate 查询是自动生成的，我们将如何更改它？

在 Hibernate 中，就像在 SQL 中一样，我们可以简单地在 *@Column*注释中使用*“* 转义” 。

让我们尝试使用 *PhoneOrder*实体：

```sql
@Entity
@Table(name = "phone_order")
public class PhoneOrder implements Serializable {
    @Id
    @Column(name = "`order`")
    String order;
    @Column(name = "`where`")
    String where;

    // getters and setters
}复制
```

通过适当的关键字转义，MySQL 不再抱怨语法，实体成功持久化：

```java
@Test
void givenPhoneOrderWithEscapedKeywords_whenNewObjectIsPersisted_thenItSucceeds() {
    PhoneOrder order = new PhoneOrder(randomUUID().toString(), "here");

    session.persist(order);
    session.flush();
}复制
```

## 六，结论

在本文中，我们学习了如何正确转义*@Column*注释中的保留字。*通过在 order*和*where*术语周围添加额外的转义字符，我们影响了 Hibernate 生成的查询并解决了异常。