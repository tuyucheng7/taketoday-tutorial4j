## 1. 简介

当然，我们永远不会假设我们可以 在Java中将String转换为 String 数组：

```plaintext
java.lang.String cannot be cast to [Ljava.lang.String;
```

但是，事实证明这是一个常见的 JPA 错误。

在这个快速教程中，我们将展示它是如何出现的以及如何解决它。

## 2. JPA常见错误案例

在 JPA 中，当我们使用本机查询并使用 EntityManager的createNativeQuery方法时，遇到此错误并不少见。

它的 [Javadoc](https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html#createNativeQuery-java.lang.String-) 实际上警告我们， 此方法将返回Object[]的列表，或者如果查询仅返回一列，则仅返回一个Object。

让我们看一个例子。首先，让我们创建一个查询执行器，我们希望重用它来执行我们所有的查询：

```java
public class QueryExecutor {
    public static List<String[]> executeNativeQueryNoCastCheck(String statement, EntityManager em) {
        Query query = em.createNativeQuery(statement);
        return query.getResultList();
    }
}
```

如上所示，我们正在使用 createNativeQuery()方法，并且我们始终期望包含String数组的结果集。

之后，让我们创建一个简单的实体以在我们的示例中使用：

```java
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    // getters and setters

}
```

最后，让我们创建一个测试类，在运行测试之前插入一条消息：

```java
public class SpringCastUnitTest {

    private static EntityManager em;
    private static EntityManagerFactory emFactory;

    @BeforeClass
    public static void setup() {
        emFactory = Persistence.createEntityManagerFactory("jpa-h2");
        em = emFactory.createEntityManager();

        // insert an object into the db
        Message message = new Message();
        message.setText("text");

        EntityTransaction tr = em.getTransaction();
        tr.begin();
        em.persist(message);
        tr.commit();
    }
}
```

现在，我们可以使用我们的 QueryExecutor 来执行一个查询来检索我们实体的文本字段：

```java
@Test(expected = ClassCastException.class)
public void givenExecutorNoCastCheck_whenQueryReturnsOneColumn_thenClassCastThrown() {
    List<String[]> results = QueryExecutor.executeNativeQueryNoCastCheck("select text from message", em);

    // fails
    for (String[] row : results) {
        // do nothing
    }
}
```

正如我们所见，因为查询中只有一列，JPA 实际上会返回一个字符串列表，而不是字符串数组列表。 我们得到一个ClassCastException，因为查询返回单个列，而我们期待一个数组。

## 3.手动铸造修复

修复此错误的最简单方法是检查结果集对象的类型以避免ClassCastException。让我们在QueryExecutor中实现一个方法来做到这一点：

```java
public static List<String[]> executeNativeQueryWithCastCheck(String statement, EntityManager em) {
    Query query = em.createNativeQuery(statement);
    List results = query.getResultList();

    if (results.isEmpty()) {
        return new ArrayList<>();
    }

    if (results.get(0) instanceof String) {
        return ((List<String>) results)
          .stream()
          .map(s -> new String[] { s })
          .collect(Collectors.toList());
    } else {
        return (List<String[]>) results;
    }
}
```

然后，我们可以使用这个方法来执行我们的查询而不会出现异常：

```java
@Test
public void givenExecutorWithCastCheck_whenQueryReturnsOneColumn_thenNoClassCastThrown() {
    List<String[]> results = QueryExecutor.executeNativeQueryWithCastCheck("select text from message", em);
    assertEquals("text", results.get(0)[0]);
}
```

这不是一个理想的解决方案，因为我们必须将结果转换为数组，以防查询仅返回一列。

## 4. JPA 实体映射修复

修复此错误的另一种方法是将结果集映射到实体。这样，我们就可以决定如何提前映射我们的查询结果并避免不必要的转换。

让我们向我们的执行器添加另一个方法来支持自定义实体映射的使用：

```java
public static <T> List<T> executeNativeQueryGeneric(String statement, String mapping, EntityManager em) {
    Query query = em.createNativeQuery(statement, mapping);
    return query.getResultList();
}
```

之后，让我们创建一个自定义的 SqlResultSetMapping 来将我们之前查询的结果集映射到Message实体：

```java
@SqlResultSetMapping(
  name="textQueryMapping",
  classes={
    @ConstructorResult(
      targetClass=Message.class,
      columns={
        @ColumnResult(name="text")
      }
    )
  }
)
@Entity
public class Message {
    // ...
}
```

在这种情况下，我们还必须添加一个与我们新创建的 SqlResultSetMapping相匹配的构造函数：

```java
public class Message {

    // ... fields and default constructor

    public Message(String text) {
        this.text = text;
    }

    // ... getters and setters

}
```

最后，我们可以使用我们的新执行器方法来运行我们的测试查询并获取Message列表：

```java
@Test
public void givenExecutorGeneric_whenQueryReturnsOneColumn_thenNoClassCastThrown() {
    List<Message> results = QueryExecutor.executeNativeQueryGeneric(
      "select text from message", "textQueryMapping", em);
    assertEquals("text", results.get(0).getText());
}
```

由于我们将结果集映射委托给 JPA，因此该解决方案更加简洁。

## 5.总结

在本文中，我们展示了本机查询是获取此ClassCastException的常见位置。我们还研究了自己进行类型检查以及通过将查询结果映射到传输对象来解决它。