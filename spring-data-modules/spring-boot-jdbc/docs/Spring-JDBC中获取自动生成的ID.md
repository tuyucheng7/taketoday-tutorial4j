## 1. 简介

在本快速教程中，我们将探讨在使用Spring JDBC时插入实体后获取自动生成的密钥的可能性。

## 2.Maven依赖

首先，我们需要在pom.xml中定义spring-boot-starter-jdbc 和 H2依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

我们可以在 Maven Central 上查看这两个依赖项的最新版本：[spring-boot-starter-jdbc](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-jdbc")和 [h2](https://search.maven.org/classic/#search|gav|1|g%3A"com.h2database" AND a%3A"h2")。

## 3.获取自动生成的密钥

### 3.1. 场景

让我们定义一个包含 2 列的sys_message表： id(自动生成的键)和message：

```sql
CREATE TABLE IF NOT EXISTS sys_message (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    message varchar(100) NOT NULL,
    PRIMARY KEY (id)
);

```

### 3.2. 使用Jdbc 模板

现在，让我们实现一个方法，该方法将使用JDBCTemplate插入新记录并返回自动生成的ID。 

因此，我们将使用 支持检索数据库生成的主键的JDBCTemplate update()方法。此方法将 PrepareStatementCreator接口的实例作为第一个参数，另一个参数是KeyHolder。 

由于 PrepareStatementCreator接口是一个FunctionalInterface ，其方法接受 java.sql.Connection的实例并返回 java.sql.PreparedStatement对象，为简单起见，我们可以使用 lambda 表达式：

```java
String INSERT_MESSAGE_SQL 
  = "insert into sys_message (message) values(?) ";
    
public long insertMessage(String message) {    
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection
          .prepareStatement(INSERT_MESSAGE_SQL);
          ps.setString(1, message);
          return ps;
        }, keyHolder);

        return (long) keyHolder.getKey();
    }
}

```

值得注意的是，keyHolder对象将包含从JDBCTemplate update()方法返回的自动生成的密钥。

我们可以通过调用 keyHolder.getKey() 来检索该密钥。

此外，我们可以验证方法：

```java
@Test
public void 
  insertJDBC_whenLoadMessageByKey_thenGetTheSameMessage() {
    long key = messageRepositoryJDBCTemplate.insert(MESSAGE_CONTENT);
    String loadedMessage = messageRepositoryJDBCTemplate
      .getMessageById(key);

    assertEquals(MESSAGE_CONTENT, loadedMessage);
}
```

### 3.3. 使用SimpleJdbcInsert

除了 JDBCTemplate 之外，我们还可以使用SimpleJdbcInsert来实现相同的结果。

因此，我们需要初始化 SimpleJdbcInsert的实例：

```java
@Repository
public class MessageRepositorySimpleJDBCInsert {

    SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public MessageRepositorySimpleJDBCInsert(DataSource dataSource) {
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
          .withTableName("sys_message").usingGeneratedKeyColumns("id");
    }
    
    //...
}

```

因此，我们可以调用SimpleJdbcInsert的executeAndReturnKey方法 将新记录插入sys_message表并取回自动生成的键：

```java
public long insert(String message) {
    Map<String, Object> parameters = new HashMap<>(1);
    parameters.put("message", message);
    Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);
    return (long) newId;
}

```

此外，我们可以非常简单地验证该方法：

```java
@Test
public void 
  insertSimpleInsert_whenLoadMessageKey_thenGetTheSameMessage() {
    long key = messageRepositorySimpleJDBCInsert.insert(MESSAGE_CONTENT);
    String loadedMessage = messageRepositoryJDBCTemplate.getMessageById(key);

    assertEquals(MESSAGE_CONTENT, loadedMessage);
}
```

## 4. 总结

我们探索了使用JDBCTemplate和 SimpleJdbcInsert插入新记录并取回自动生成的键的可能性。