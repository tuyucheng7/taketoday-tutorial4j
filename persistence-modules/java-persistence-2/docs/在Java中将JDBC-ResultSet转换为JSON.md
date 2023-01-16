## 1. 概述

在某些场景中，我们可能需要通过 API 调用将数据库查询的结果发送到另一个系统或消息平台。对于这种情况，我们通常使用 JSON 作为数据交换格式。 

在本教程中，我们将看到将[JDBC ](https://www.baeldung.com/java-jdbc)ResultSet对象转换为[JSON](https://www.baeldung.com/java-json)格式的多种方法。

## 2.代码示例

我们将在代码示例中使用[H2](https://www.baeldung.com/spring-boot-h2-database)数据库。我们有一个示例 CSV 文件，我们已使用 JDBC将其读入表words 。以下是示例 CSV 文件中的三行，第一行是标题：

```apache
Username,Id,First name,Last name
doe1,7173,John,Doe
smith3,3722,Dana,Smith
john22,5490,John,Wang
```

形成ResultSet的代码行如下所示：

```java
ResultSet resultSet = stmt.executeQuery("SELECT  FROM words");
```

对于 JSON 处理，我们使用[JSON-Java](https://www.baeldung.com/java-org-json) ( org.json ) 库。首先，我们将[其对应的依赖](https://mvnrepository.com/artifact/org.json/json)添加到我们的POM文件中：

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20220320</version>
</dependency>

```

## 3.不使用外部依赖

JDBC API 早于现代Java集合框架。因此，我们不能使用 for-each 迭代和 Stream 方法之类的东西。

相反，我们必须依赖迭代器。[此外，我们需要从ResultSet](https://www.baeldung.com/jdbc-database-metadata)[的元数据中](https://www.baeldung.com/jdbc-database-metadata)提取列名的数量和列表。

这导致了一个基本循环，包括每行形成一个 JSON 对象，将对象添加到List，最后将该List转换为JSON数组。所有这些功能都在org.json包中可用：

```java
ResultSetMetaData md = resultSet.getMetaData();
int numCols = md.getColumnCount();
List<String> colNames = IntStream.range(0, numCols)
  .mapToObj(i -> {
      try {
          return md.getColumnName(i + 1);
      } catch (SQLException e) {
          e.printStackTrace();
          return "?";
      }
  })
  .collect(Collectors.toList());

JSONArray result = new JSONArray();
while (resultSet.next()) {
    JSONObject row = new JSONObject();
    colNames.forEach(cn -> {
        try {
            row.put(cn, resultSet.getObject(cn));
        } catch (JSONException | SQLException e) {
            e.printStackTrace();
        }
    });
    result.add(row);
}
```

在这里，我们首先运行一个循环来提取每一列的名称。我们稍后会使用这些列名来形成生成的 JSON 对象。 

在第二个循环中，我们遍历实际结果并将每个结果转换为 JSON 对象，使用我们在上一步中计算的列名。然后我们将所有这些对象添加到一个 JSON 数组中。 

我们已将列名和列计数的提取排除在循环之外。这有助于加快执行速度。

生成的 JSON 如下所示：

```json
[
   {
      "Username":"doe1",
      "First name":"John",
      "Id":"7173",
      "Last name":"Doe"
   },
   {
      "Username":"smith3",
      "First name":"Dana",
      "Id":"3722",
      "Last name":"Smith"
   },
   {
      "Username":"john22",
      "First name":"John",
      "Id":"5490",
      "Last name":"Wang"
   }
]
```

## 4. 使用默认设置的 jOOQ

[jOOQ](https://www.baeldung.com/jooq-intro)框架(Java 面向对象查询)提供了一组方便的实用函数来处理 JDBC 和ResultSet 对象。首先，我们需要将[jOOQ 依赖](https://mvnrepository.com/artifact/org.jooq/jooq)项添加到我们的 POM 文件中：

```perl
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq</artifactId>
    <version>3.11.11</version>
</dependency>
```

 添加依赖后，我们实际上可以使用单行解决方案将ResultSet转换为 JSON 对象：

```java
JSONObject result = new JSONObject(DSL.using(dbConnection)
  .fetch(resultSet)
  .formatJSON());
```

生成的 JSON 元素是一个对象，由两个字段组成，分别称为fields和records，其中字段具有列的名称和类型，而records包含实际数据。这与之前的 JSON 对象略有不同，在我们的示例表中看起来像这样：

```json
{
   "records":[
      [
         "doe1",
         "7173",
         "John",
         "Doe"
      ],
      [
         "smith3",
         "3722",
         "Dana",
         "Smith"
      ],
      [
         "john22",
         "5490",
         "John",
         "Wang"
      ]
   ],
   "fields":[
      {
         "schema":"PUBLIC",
         "name":"Username",
         "type":"VARCHAR",
         "table":"WORDS"
      },
      {
         "schema":"PUBLIC",
         "name":"Id",
         "type":"VARCHAR",
         "table":"WORDS"
      },
      {
         "schema":"PUBLIC",
         "name":"First name",
         "type":"VARCHAR",
         "table":"WORDS"
      },
      {
         "schema":"PUBLIC",
         "name":"Last name",
         "type":"VARCHAR",
         "table":"WORDS"
      }
   ]
}
```

## 5. 使用自定义设置的 jOOQ

如果我们不喜欢 jOOQ 生成的 JSON 对象的默认结构，可以自定义它。

我们将通过实现RecordMapper接口来做到这一点。该接口有一个map()方法，该方法接收Record作为输入并返回所需的任意类型的对象。

然后我们将RecordMapper作为输入提供给jOOQ 结果类的map()方法：

```java
List json = DSL.using(dbConnection)
  .fetch(resultSet)
  .map(new RecordMapper() {
      @Override
      public JSONObject map(Record r) {
          JSONObject obj = new JSONObject();
          colNames.forEach(cn -> obj.put(cn, r.get(cn)));
          return obj;
      }
  });
return new JSONArray(json);

```

在这里，我们从map()方法返回了一个JSONObject 。

生成的 JSON 看起来像这样，类似于第 3 节：

```json
[
   {
      "Username":"doe1",
      "First name":"John",
      "Id":"7173",
      "Last name":"Doe"
   },
   {
      "Username":"smith3",
      "First name":"Dana",
      "Id":"3722",
      "Last name":"Smith"
   },
   {
      "Username":"john22",
      "First name":"John",
      "Id":"5490",
      "Last name":"Wang"
   }
]
```

## 六. 总结

在本文中，我们探索了三种将 JDBC ResultSet转换为 JSON 对象的不同方法。

每种方法都有自己的用途。例如，我们选择什么取决于输出 JSON 对象所需的结构以及对依赖项大小的可能限制。