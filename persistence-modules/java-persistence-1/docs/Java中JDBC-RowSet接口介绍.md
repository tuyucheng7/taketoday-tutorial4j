## 1. 概述

在本文中，我们将回顾 JDBC RowSet接口。JDBC RowSet对象以一种使其比结果集更具适应性和更易于使用的样式保存表格数据。

Oracle为最常用的RowSet定义了五个RowSet接口：

-   Jdbc行集
-   缓存行集
-   Web行集
-   连接行集
-   过滤行集

在本教程中，我们将回顾如何使用这些RowSet接口。

## 2.Jdbc行集

让我们从JdbcRowSet开始——我们将通过将Connection对象传递给JdbcRowSetImpl来简单地创建一个：

```java
JdbcRowSet jdbcRS = new JdbcRowSetImpl(conn);
jdbcRS.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
String sql = "SELECT  FROM customers";
jdbcRS.setCommand(sql);
jdbcRS.execute();
jdbcRS.addRowSetListener(new ExampleListener());
while (jdbcRS.next()) {
    // each call to next, generates a cursorMoved event
    System.out.println("id = " + jdbcRS.getString(1));
    System.out.println("name = " + jdbcRS.getString(2));
}
```

在上面的示例中，jdbcRs不包含任何数据，直到我们使用方法setCommand定义 SQL 语句然后运行方法execute。

另请注意，为了执行事件处理，我们如何将RowSetListener添加到JdbcRowSet 中。

JdbcRowSet不同于其他四种RowSet实现——因为它始终连接到数据库，因此它与ResultSet对象最相似。

## 3.缓存行集

CachedRowSet对象是唯一的，因为它可以在不连接到其数据源的情况下运行。我们称其为“断开连接的RowSet对象”。

CachedRowSet之所以得名，是因为它将数据缓存在内存中，这样它就可以对自己的数据进行操作，而不是对存储在数据库中的数据进行操作。

由于CachedRowSet接口是所有断开连接的 RowSet 对象的超级接口，我们在下面查看的代码也适用于WebRowSe t、JoinRowSet或FilteredRowSe t：

```java
CachedRowSet crs = new CachedRowSetImpl();
crs.setUsername(username);
crs.setPassword(password);
crs.setUrl(url);
crs.setCommand(sql);
crs.execute();
crs.addRowSetListener(new ExampleListener());
while (crs.next()) {
    if (crs.getInt("id") == 1) {
        System.out.println("CRS found customer1 and will remove the record.");
        crs.deleteRow();
        break;
    }
}
```

## 4.Web行集

接下来，让我们看一下WebRowSet。

这也是独一无二的，因为除了提供CachedRowSet对象的功能外，它还可以将自己写入XML 文档，还可以读取该 XML 文档以将自己转换回WebRowSet：

```java
WebRowSet wrs = new WebRowSetImpl();
wrs.setUsername(username);
wrs.setPassword(password);
wrs.setUrl(url);
wrs.setCommand(sql);
wrs.execute();
FileOutputStream ostream = new FileOutputStream("customers.xml");
wrs.writeXml(ostream);
```

使用writeXml方法，我们将WebRowSet对象的当前状态写入XML 文档。

通过将OutputStream对象传递给writeXml方法，我们以字节而不是字符写入，这对于处理所有形式的数据非常有帮助。

## 5.JoinRowSet _

JoinRowSet允许我们在内存中的RowSet对象之间创建 SQL JOIN 。这很重要，因为它为我们节省了必须创建一个或多个连接的开销：

```java
CachedRowSetImpl customers = new CachedRowSetImpl();
// configuration of settings for CachedRowSet
CachedRowSetImpl associates = new CachedRowSetImpl();
// configuration of settings for this CachedRowSet            
JoinRowSet jrs = new JoinRowSetImpl();
jrs.addRowSet(customers,ID);
jrs.addRowSet(associates,ID);
```

因为添加到JoinRowSet对象的每个RowSet对象都需要一个匹配列，即 SQL JOIN所基于的列，所以我们在addRowSet方法中指定“id” 。

请注意，我们也可以使用列号，而不是使用列名。

## 6.过滤行集

最后，FilteredRowSet 让我们减少RowSet对象中可见的行数，这样我们就可以只处理与我们正在做的事情相关的数据。

我们决定如何使用Predicate接口的实现来“过滤”数据：

```java
public class FilterExample implements Predicate {
    
    private Pattern pattern;
    
    public FilterExample(String regexQuery) {
        if (regexQuery != null && !regexQuery.isEmpty()) {
            pattern = Pattern.compile(regexQuery);
        }
    }
 
    public boolean evaluate(RowSet rs) {
        try {
            if (!rs.isAfterLast()) {
                String name = rs.getString("name");
                System.out.println(String.format(
                  "Searching for pattern '%s' in %s", pattern.toString(),
                  name));
                Matcher matcher = pattern.matcher(name);
                return matcher.matches();
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // methods for handling errors
}
```

现在我们将该过滤器应用于FilteredRowSet对象：

```java
RowSetFactory rsf = RowSetProvider.newFactory();
FilteredRowSet frs = rsf.createFilteredRowSet();
frs.setCommand("select  from customers");
frs.execute(conn);
frs.setFilter(new FilterExample("^[A-C]."));
            
ResultSetMetaData rsmd = frs.getMetaData();
int columncount = rsmd.getColumnCount();
while (frs.next()) {
    for (int i = 1; i <= columncount; i++) {
        System.out.println(
          rsmd.getColumnLabel(i)
          + " = "
          + frs.getObject(i) + " ");
        }
    }
```

## 七. 总结

本快速教程涵盖了JDK 中可用的RowSet接口的五个标准实现。

我们讨论了每个实现的配置并提到了它们之间的差异。

正如我们所指出的，只有一个RowSet实现是连接的RowSet对象—— JdbcRowSet。其他四个是断开连接的RowSet对象。