## 1. 简介

尽管是最著名的漏洞之一，[SQL 注入仍然在臭名昭著的](https://www.baeldung.com/cs/sql-injection)[OWASP 前 10 名列表中](https://owasp.org/www-project-top-ten/)名列前茅 ——现在是更通用的 注入类的一部分。

在本教程中，我们将探讨Java 中导致易受攻击的应用程序的常见编码错误，以及如何使用 JVM 标准运行时库中可用的 API 来避免这些错误。我们还将介绍我们可以从 JPA、Hibernate 和其他 ORM 中获得哪些保护，以及我们仍然需要担心哪些盲点。

## 2. 应用程序如何变得易受 SQL 注入攻击？

注入攻击之所以有效，是因为对于许多应用程序而言，执行给定计算的唯一方法是动态生成代码，然后由另一个系统或组件运行。如果在生成此代码的过程中我们使用未经适当清理的不受信任的数据，我们就会为黑客利用敞开大门。

这个说法可能听起来有点抽象，那么让我们通过一个教科书的例子来看看这在实践中是如何发生的：

```java
public List<AccountDTO>
  unsafeFindAccountsByCustomerId(String customerId)
  throws SQLException {
    // UNSAFE !!! DON'T DO THIS !!!
    String sql = "select "
      + "customer_id,acc_number,branch_id,balance "
      + "from Accounts where customer_id = '"
      + customerId 
      + "'";
    Connection c = dataSource.getConnection();
    ResultSet rs = c.createStatement().executeQuery(sql);
    // ...
}
```

这段代码的问题很明显：我们 在根本没有验证的情况下将 customerId的值放入查询中。如果我们确定此值仅来自可信来源，就不会发生任何坏事，但我们可以吗？

假设此函数用于 帐户 资源的 REST API 实现中。利用这段代码很简单：我们所要做的就是发送一个值，当它与查询的固定部分连接时，改变它的预期行为：

```bash
curl -X GET 
  'http://localhost:8080/accounts?customerId=abc%27%20or%20%271%27=%271' 
```

假设 customerId参数值在到达我们的函数之前未经检查，这就是我们收到的内容：

```bash
abc' or '1' = '1
```

当我们将这个值与固定部分连接起来时，我们得到最终将要执行的 SQL 语句：

```bash
select customer_id, acc_number,branch_id, balance
  from Accounts where customerId = 'abc' or '1' = '1'
```

可能不是我们想要的……

聪明的开发人员(我们不都是这样吗？)现在会想：“这太傻了！我 永远不会使用字符串连接来构建这样的查询”。

没那么快……这个典型的例子确实很愚蠢，但在某些情况下我们可能仍然需要这样做：

-   具有动态搜索条件的复杂查询：根据用户提供的条件添加 UNION 子句
-   动态分组或排序：用作 GUI 数据表后端的 REST API

### 2.1. 我正在使用 JPA。我很安全，对吧？

这是一个常见的误解。JPA 和其他 ORM 使我们免于创建手工编码的 SQL 语句，但它们不会阻止我们编写易受攻击的代码。

让我们看看上一个示例的 JPA 版本是什么样子的：

```java
public List<AccountDTO> unsafeJpaFindAccountsByCustomerId(String customerId) {    
    String jql = "from Account where customerId = '" + customerId + "'";        
    TypedQuery<Account> q = em.createQuery(jql, Account.class);        
    return q.getResultList()
      .stream()
      .map(this::toAccountDTO)
      .collect(Collectors.toList());        
}

```

我们之前指出的相同问题也出现在这里：我们正在使用未经验证的输入来创建 JPA 查询，因此我们在这里暴露于相同类型的漏洞利用。

## 三、预防技术

现在我们知道什么是 SQL 注入，让我们看看如何保护我们的代码免受这种攻击。在这里，我们重点介绍Java和其他 JVM 语言中可用的一些非常有效的技术，但类似的概念也适用于其他环境，例如 PHP、.Net、Ruby 等等。

对于那些正在寻找可用技术的完整列表(包括特定于数据库的技术)的人来说，[OWASP 项目](https://www.owasp.org/)维护了一个 [SQL 注入预防备忘单](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html)，这是了解有关该主题的更多信息的好地方。

### 3.1. 参数化查询

这种技术包括在我们需要插入用户提供的值时，在我们的查询中使用带有问号占位符(“？”)的准备好的语句。这是非常有效的，并且除非 JDBC 驱动程序的实现中存在错误，否则不会受到攻击。

让我们重写示例函数以使用此技术：

```java
public List<AccountDTO> safeFindAccountsByCustomerId(String customerId)
  throws Exception {
    
    String sql = "select "
      + "customer_id, acc_number, branch_id, balance from Accounts"
      + "where customer_id = ?";
    
    Connection c = dataSource.getConnection();
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1, customerId);
    ResultSet rs = p.executeQuery(sql)); 
    // omitted - process rows and return an account list
}
```

在这里，我们使用了 Connection实例中可用的prepareStatement()方法 来获取 PreparedStatement。该接口使用几个方法扩展了常规的 Statement 接口，这些方法允许我们在执行查询之前安全地将用户提供的值插入查询中。

对于 JPA，我们有一个类似的特性：

```java
String jql = "from Account where customerId = :customerId";
TypedQuery<Account> q = em.createQuery(jql, Account.class)
  .setParameter("customerId", customerId);
// Execute query and return mapped results (omitted)
```

在Spring Boot下运行此代码时，我们可以将属性logging.level.sql设置为 DEBUG 并查看实际构建的查询以执行此操作：

```plaintext
// Note: Output formatted to fit screen
[DEBUG][SQL] select
  account0_.id as id1_0_,
  account0_.acc_number as acc_numb2_0_,
  account0_.balance as balance3_0_,
  account0_.branch_id as branch_i4_0_,
  account0_.customer_id as customer5_0_ 
from accounts account0_ 
where account0_.customer_id=?
```

正如预期的那样，ORM 层使用 customerId参数的占位符创建了一个准备好的语句。这与我们在普通 JDBC 案例中所做的相同——但少了一些语句，这很好。

作为奖励，这种方法通常会产生更好的查询性能，因为大多数数据库可以缓存与准备好的语句相关联的查询计划。

请注意，此方法仅适用于用作 值的占位符。例如，我们不能使用占位符来动态更改表的名称：

```java
// This WILL NOT WORK !!!
PreparedStatement p = c.prepareStatement("select count() from ?");
p.setString(1, tableName);
```

在这里，JPA 也无济于事：

```java
// This WILL NOT WORK EITHER !!!
String jql = "select count() from :tableName";
TypedQuery q = em.createQuery(jql,Long.class)
  .setParameter("tableName", tableName);
return q.getSingleResult();

```

在这两种情况下，我们都会收到运行时错误。

这背后的主要原因是准备好的语句的本质：数据库服务器使用它们来缓存拉取结果集所需的查询计划，这对于任何可能的值通常都是相同的。对于表名和 SQL 语言中可用的其他结构(例如 order by子句中使用的列)，情况并非如此。

### 3.2. JPA 标准 API

由于显式 JQL 查询构建是 SQL 注入的主要来源，因此我们应该尽可能使用 JPA 的查询 API。

有关此 API 的快速入门，请[参阅有关 Hibernate Criteria 查询的文章](https://www.baeldung.com/hibernate-criteria-queries)。同样值得一读的是我们[关于 JPA 元模型的文章](https://www.baeldung.com/hibernate-criteria-queries-metamodel)，它展示了如何生成元模型类，这将帮助我们摆脱用于列名的字符串常量——以及当它们发生变化时出现的运行时错误。

让我们重写 JPA 查询方法以使用 Criteria API：

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Account> cq = cb.createQuery(Account.class);
Root<Account> root = cq.from(Account.class);
cq.select(root).where(cb.equal(root.get(Account_.customerId), customerId));

TypedQuery<Account> q = em.createQuery(cq);
// Execute query and return mapped results (omitted)
```

在这里，我们使用了更多的代码行来获得相同的结果，但好处是现在我们不必担心 JQL 语法。

另一个要点：尽管Criteria API冗长，但它使创建复杂的查询服务更加直接和安全。有关显示如何在实践中执行此操作的完整示例，请查看[JHipster](https://www.jhipster.tech/)生成的应用程序使用的方法。

### 3.3. 用户数据清理

数据清理是一种对用户提供的数据应用过滤器的技术，因此我们应用程序的其他部分可以安全地使用它。过滤器的实现可能有很大差异，但我们通常可以将它们分为两种类型：白名单和黑名单。

黑名单由试图识别无效模式的过滤器组成，在 SQL 注入预防的上下文中通常价值不大——但对于检测而言则不是！稍后会详细介绍。

另一方面，当我们可以准确定义什么是有效输入时，白名单特别有效。

让我们增强我们的safeFindAccountsByCustomerId 方法，这样调用者现在还可以指定用于对结果集进行排序的列。由于我们知道可能列的集合，我们可以使用一个简单的集合来实现白名单，并用它来清理接收到的参数：

```java
private static final Set<String> VALID_COLUMNS_FOR_ORDER_BY
  = Collections.unmodifiableSet(Stream
      .of("acc_number","branch_id","balance")
      .collect(Collectors.toCollection(HashSet::new)));

public List<AccountDTO> safeFindAccountsByCustomerId(
  String customerId,
  String orderBy) throws Exception { 
    String sql = "select "
      + "customer_id,acc_number,branch_id,balance from Accounts"
      + "where customer_id = ? ";
    if (VALID_COLUMNS_FOR_ORDER_BY.contains(orderBy)) {
        sql = sql + " order by " + orderBy;
    } else {
        throw new IllegalArgumentException("Nice try!");
    }
    Connection c = dataSource.getConnection();
    PreparedStatement p = c.prepareStatement(sql);
    p.setString(1,customerId);
    // ... result set processing omitted
}
```

在这里，我们结合了准备好的语句方法和用于清理 orderBy参数的白名单。最终结果是带有最终 SQL 语句的安全字符串。在这个简单的示例中，我们使用的是静态集，但我们也可以使用数据库元数据函数来创建它。

我们可以对 JPA 使用相同的方法，同时利用 Criteria API 和元数据来避免在我们的代码中使用字符串常量：

```java
// Map of valid JPA columns for sorting
final Map<String,SingularAttribute<Account,?>> VALID_JPA_COLUMNS_FOR_ORDER_BY = Stream.of(
  new AbstractMap.SimpleEntry<>(Account_.ACC_NUMBER, Account_.accNumber),
  new AbstractMap.SimpleEntry<>(Account_.BRANCH_ID, Account_.branchId),
  new AbstractMap.SimpleEntry<>(Account_.BALANCE, Account_.balance))
  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

SingularAttribute<Account,?> orderByAttribute = VALID_JPA_COLUMNS_FOR_ORDER_BY.get(orderBy);
if (orderByAttribute == null) {
    throw new IllegalArgumentException("Nice try!");
}

CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Account> cq = cb.createQuery(Account.class);
Root<Account> root = cq.from(Account.class);
cq.select(root)
  .where(cb.equal(root.get(Account_.customerId), customerId))
  .orderBy(cb.asc(root.get(orderByAttribute)));

TypedQuery<Account> q = em.createQuery(cq);
// Execute query and return mapped results (omitted)
```

此代码与普通 JDBC 中的基本结构相同。首先，我们使用白名单来清理列名，然后我们继续创建 CriteriaQuery以从数据库中获取记录。

### 3.4. 我们现在安全吗？

假设我们到处都使用了参数化查询和/或白名单。我们现在可以去找我们的经理并保证我们安全吗？

好吧……没那么快。甚至不考虑[图灵的停机问题](https://en.wikipedia.org/wiki/Halting_problem)，我们还必须考虑其他方面：

1.  存储过程：这些也容易出现 SQL 注入问题；只要有可能，请甚至对将通过准备好的语句发送到数据库的值应用卫生
2.  触发器：与过程调用相同的问题，但更加隐蔽，因为有时我们不知道它们在那里……
3.  不安全的直接对象引用：即使我们的应用程序没有 SQL 注入，仍然存在与此漏洞类别相关的风险 - 这里的要点与攻击者可以欺骗应用程序的不同方式有关，因此它返回他或她的记录不应该访问——[在 OWASP 的 GitHub 存储库中有一个很好的关于这个主题的备忘单](https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Insecure_Direct_Object_Reference_Prevention_Cheat_Sheet.md)

简而言之，我们最好的选择是谨慎。如今，许多组织正是为此使用“红队”。让他们做他们的工作，就是找到任何剩余的漏洞。

## 4. 损害控制技术

作为一种良好的安全实践，我们应该始终实施多个防御层——一个称为 纵深防御的概念。主要思想是，即使我们无法在我们的代码中找到所有可能的漏洞——处理遗留系统时的常见情况——我们至少应该尝试限制攻击可能造成的损害。

当然，这将是整篇文章甚至一本书的主题，但让我们举几个措施：

1.  应用最小权限原则：尽可能限制用于访问数据库的帐户的权限
2.  使用可用的特定于数据库的方法来添加额外的保护层；例如，H2 数据库有一个会话级别的选项，可以禁用 SQL 查询中的所有文字值
3.  使用短期凭据：使应用程序经常轮换数据库凭据； 实现这一点的一个好方法是使用[Spring Cloud Vault](https://www.baeldung.com/spring-cloud-vault)
4.  记录一切：如果应用程序存储客户数据，这是必须的； 有许多解决方案可以直接集成到数据库或作为代理工作，因此在发生攻击时我们至少可以评估损失
5.  使用[WAF](https://owasp.org/www-community/Web_Application_Firewall)或类似的入侵检测解决方案：这些是典型的 黑名单示例——通常，它们带有一个庞大的已知攻击特征数据库，并会在检测到时触发可编程操作。有些还包括 JVM 内的代理，可以通过应用一些检测来检测入侵——这种方法的主要优点是最终的漏洞变得更容易修复，因为我们将有一个完整的堆栈跟踪可用。

## 5.总结

在本文中，我们介绍了Java应用程序中的 SQL 注入漏洞——对任何依赖数据开展业务的组织的严重威胁——以及如何使用简单的技术来防止它们。