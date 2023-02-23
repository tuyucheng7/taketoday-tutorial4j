## 1. 概述

简单地说，rxjava-jdbc是一个用于与关系数据库交互的API，它允许流式的方法调用。在这个快速教程中，我们将了解该库以及如何使用它的一些常用功能。

如果你想了解RxJava的基础知识，请查看[这篇](https://www.baeldung.com/rx-java)文章。

## 2. Maven依赖

让我们从需要添加到pom.xml的Maven依赖项开始：

```xml
<dependency>
    <groupId>com.github.davidmoten</groupId>
    <artifactId>rxjava-jdbc</artifactId>
    <version>0.7.11</version>
</dependency>
```

我们可以在[Maven Central](https://search.maven.org/search?q=a:rxjava-jdbc)上找到最新版本的API。

## 3. 主要部件

**Database类是运行所有常见类型的数据库交互的主要入口点**。要创建一个Database对象，我们可以将ConnectionProvider接口的实现实例传递给from()静态方法：

```java
public static ConnectionProvider connectionProvider = new ConnectionProviderFromUrl(DB_CONNECTION, DB_USER, DB_PASSWORD);
Database db = Database.from(connectionProvider);
```

ConnectionProvider有几个值得关注的实现-例如ConnectionProviderFromContext、ConnectionProviderFromDataSource、ConnectionProviderFromUrl和ConnectionProviderPooled。

为了进行基本操作，我们可以使用数据库的以下API：

-   select()：用于SQL选择查询
-   update()：用于DDL语句，例如创建和删除，以及插入、更新和删除

## 4. 快速开始

在下一个快速示例中，我们将展示如何执行所有基本的数据库操作：

```java
public class BasicQueryTypesTest {

    Observable<Integer> create,
          insert1,
          insert2,
          insert3,
          update,
          delete = null;

    @Test
    public void whenCreateTableAndInsertRecords_thenCorrect() {
        create = db.update("CREATE TABLE IF NOT EXISTS EMPLOYEE(" + "id int primary key, name varchar(255))")
              .count();
        insert1 = db.update("INSERT INTO EMPLOYEE(id, name) VALUES(1, 'John')")
              .dependsOn(create)
              .count();
        update = db.update("UPDATE EMPLOYEE SET name = 'Alan' WHERE id = 1")
              .dependsOn(create)
              .count();
        insert2 = db.update("INSERT INTO EMPLOYEE(id, name) VALUES(2, 'Sarah')")
              .dependsOn(create)
              .count();
        insert3 = db.update("INSERT INTO EMPLOYEE(id, name) VALUES(3, 'Mike')")
              .dependsOn(create)
              .count();
        delete = db.update("DELETE FROM EMPLOYEE WHERE id = 2")
              .dependsOn(create)
              .count();
        List<String> names = db.select("select name from EMPLOYEE where id < ?")
              .parameter(3)
              .dependsOn(create)
              .dependsOn(insert1)
              .dependsOn(insert2)
              .dependsOn(insert3)
              .dependsOn(update)
              .dependsOn(delete)
              .getAs(String.class)
              .toList()
              .toBlocking()
              .single();

        assertEquals(Arrays.asList("Alan"), names);
    }
}
```

这里有一个简短的说明-我们正在调用dependsOn()来确定查询的运行顺序。

否则，代码将失败或产生不可预知的结果，除非我们指定我们希望以何种顺序执行查询。

## 5. 自动映射

自动映射功能允许我们将选择的数据库记录映射到对象。

让我们看一下自动映射数据库记录的两种方法。

### 5.1 使用接口自动映射

我们可以使用带注解的接口将automap()数据库记录到对象。为此，我们可以创建一个带注解的接口：

```java
public interface Employee {

    @Column("id")
    int id();

    @Column("name")
    String name();
}
```

然后，我们可以运行我们的测试：

```java
@Test
public void whenSelectFromTableAndAutomap_thenCorrect() {
    List<Employee> employees = db.select("select id, name from EMPLOYEE")
        .dependsOn(create)
        .dependsOn(insert1)
        .dependsOn(insert2)
        .autoMap(Employee.class)
        .toList()
        .toBlocking()
        .single();
    
    assertThat(employees.get(0).id()).isEqualTo(1);
    assertThat(employees.get(0).name()).isEqualTo("Alan");
    assertThat(employees.get(1).id()).isEqualTo(2);
    assertThat(employees.get(1).name()).isEqualTo("Sarah");
}
```

### 5.2 使用类自动映射

我们还可以使用具体的类将数据库记录自动映射到对象。让我们看看这个类看起来像什么：

```java
public class Manager {

    private int id;
    private String name;

    // standard constructors, getters, and setters
}
```

现在，我们可以运行我们的测试：

```java
@Test
public void whenSelectManagersAndAutomap_thenCorrect() {
    List<Manager> managers = db.select("select id, name from MANAGER")
        .dependsOn(create)
        .dependsOn(insert1)
        .dependsOn(insert2)
        .autoMap(Manager.class)
        .toList()
        .toBlocking()
        .single();
    
    assertThat(managers.get(0).getId()).isEqualTo(1);
    assertThat(managers.get(0).getName()).isEqualTo("Alan");
    assertThat(managers.get(1).getId()).isEqualTo(2);
    assertThat(managers.get(1).getName()).isEqualTo("Sarah");
}
```

这里有一些注意事项：

-   create、insert1和insert2是对创建Manager表并向其中插入记录所返回的Observables的引用
-   我们查询中所选列的数量必须与Manager类构造函数中的参数数量相匹配
-   列的类型必须可以自动映射到构造函数中的类型

有关自动映射的更多信息，请访问GitHub上的[rxjava-jdbc仓库](https://github.com/davidmoten/rxjava-jdbc)

## 6. 处理大对象

该API支持处理大对象，如CLOB和BLOBS。在接下来的小节中，我们将了解如何使用此功能。

### 6.1 CLOB

让我们看看如何插入和选择CLOB：

```java
@Before
public void setup() throws IOException {
    create = db.update("CREATE TABLE IF NOT EXISTS " + "SERVERLOG (id int primary key, document CLOB)")
        .count();
    
    InputStream actualInputStream = new FileInputStream("src/test/resources/actual_clob");
    actualDocument = getStringFromInputStream(actualInputStream);

    InputStream expectedInputStream = new FileInputStream("src/test/resources/expected_clob");
 
    expectedDocument = getStringFromInputStream(expectedInputStream);
    insert = db.update("insert into SERVERLOG(id,document) values(?,?)")
        	.parameter(1)
        	.parameter(Database.toSentinelIfNull(actualDocument))
        .dependsOn(create)
        .count();
}

@Test
public void whenSelectCLOB_thenCorrect() throws IOException {
    db.select("select document from SERVERLOG where id = 1")
        .dependsOn(create)
        .dependsOn(insert)
        .getAs(String.class)
        .toList()
        .toBlocking()
        .single();
    
    assertEquals(expectedDocument, actualDocument);
}
```

请注意，getStringFromInputStream()是一种将InputStream的内容转换为String的方法。

### 6.2 BLOB

我们可以使用API以非常相似的方式处理BLOB。唯一的区别是，我们必须传递一个字节数组，而不是将一个字符串传递给toSentinelIfNull()方法。

我们可以这样做：

```java
@Before
public void setup() throws IOException {
    create = db.update("CREATE TABLE IF NOT EXISTS " + "SERVERLOG (id int primary key, document BLOB)")
        .count();
    
    InputStream actualInputStream = new FileInputStream("src/test/resources/actual_clob");
    actualDocument = getStringFromInputStream(actualInputStream);
    byte[] bytes = this.actualDocument.getBytes(StandardCharsets.UTF_8);
    
    InputStream expectedInputStream = new FileInputStream("src/test/resources/expected_clob");
    expectedDocument = getStringFromInputStream(expectedInputStream);
    insert = db.update("insert into SERVERLOG(id,document) values(?,?)")
        .parameter(1)
        .parameter(Database.toSentinelIfNull(bytes))
        .dependsOn(create)
        .count();
}
```

然后，我们可以重用上一个示例中的相同测试。

## 7. 事务

接下来，让我们看一下对事务的支持。

事务管理允许我们处理用于将多个数据库操作分组在单个事务中的事务，以便它们可以全部提交-永久保存到数据库，或一起回滚。

让我们看一个简单的例子：

```java
@Test
public void whenCommitTransaction_thenRecordUpdated() {
    Observable<Boolean> begin = db.beginTransaction();
    Observable<Integer> createStatement = db.update("CREATE TABLE IF NOT EXISTS EMPLOYEE(id int primary key, name varchar(255))")
        .dependsOn(begin)
        .count();
    Observable<Integer> insertStatement = db.update("INSERT INTO EMPLOYEE(id, name) VALUES(1, 'John')")
        .dependsOn(createStatement)
        .count();
    Observable<Integer> updateStatement = db.update("UPDATE EMPLOYEE SET name = 'Tom' WHERE id = 1")
        .dependsOn(insertStatement)
        .count();
    Observable<Boolean> commit = db.commit(updateStatement);
    String name = db.select("select name from EMPLOYEE WHERE id = 1")
        .dependsOn(commit)
        .getAs(String.class)
        .toBlocking()
        .single();
    
    assertEquals("Tom", name);
}
```

为了开始事务，我们调用方法beginTransaction()。调用此方法后，每个数据库操作都在同一事务中运行，直到调用任何方法commit()或rollback()为止。

我们可以在捕获异常时使用rollback()方法来回滚整个事务，以防代码因任何原因失败。我们可以对所有Exceptions或特定的预期Exceptions执行此操作。

## 8. 返回生成的ID

如果我们在我们正在处理的表中设置auto_increment字段，我们可能需要检索生成的值。我们可以通过调用returnGeneratedKeys()方法来做到这一点。

让我们看一个简单的例子：

```java
@Test
public void whenInsertAndReturnGeneratedKey_thenCorrect() {
    Integer key = db.update("INSERT INTO EMPLOYEE(name) VALUES('John')")
        .dependsOn(createStatement)
        .returnGeneratedKeys()
        .getAs(Integer.class)
        .count()
        .toBlocking()
        .single();
 
    assertThat(key).isEqualTo(1);
}
```

## 9. 总结

在本教程中，我们了解了如何使用rxjava–jdbc的流式方法。我们还介绍了它提供的一些功能，例如自动映射、处理大对象和事务。