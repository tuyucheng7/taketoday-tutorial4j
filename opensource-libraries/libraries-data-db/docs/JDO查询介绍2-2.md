## 1. 概述

在本系列的前一篇文章中，我们展示了如何将Java对象持久保存到不同的数据存储中。更多详细信息，请查看[Java 数据对象指南](https://www.baeldung.com/jdo)。

JDO 支持不同的查询语言，以便开发人员可以灵活地使用他最熟悉的查询语言。

## 2. JDO 查询语言

JDO 支持以下查询语言：

-   JDOQL——一种使用Java语法的查询语言
-   Typed JDOQL – 遵循 JDOQL 语法，但提供 API 以简化查询的使用。
-   SQL——仅用于 RDBMS。
-   JPQL——由 Datanucleus 提供，但不是 JDO 规范的一部分。

## 3.查询接口

### 3.1. 创建查询

要创建查询，我们需要指定语言以及查询字符串：

```java
Query query = pm.newQuery(
  "javax.jdo.query.SQL",
  "select  from product_item where price < 10");
```

如果我们不指定语言，它默认为 JDOQL：

```java
Query query = pm.newQuery(
  "SELECT FROM com.baeldung.jdo.query.ProductItem WHERE price < 10");
```

### 3.2. 创建命名查询

我们还可以定义查询并通过其保存的名称引用它。

为此，我们首先创建一个ProductItem类：

```java
@PersistenceCapable
public class ProductItem {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    int id;
    String name;
    String status;
    String description;
    double price;

    //standard getters, setters & constructors
}
```

接下来，我们在META-INF/package.jdo文件中添加一个类配置来定义一个查询并命名：

```xml
<jdo>
    <package name="com.baeldung.jdo.query">
        <class name="ProductItem" detachable="true" table="product_item">
            <query name="PriceBelow10" language="javax.jdo.query.SQL">
            <![CDATA[SELECT  FROM PRODUCT_ITEM WHERE PRICE < 10]]>
            </query>
        </class>
    </package>
</jdo>
```

我们定义了一个名为“ PriceBelow10”的查询。

我们可以在我们的代码中使用它：

```java
Query<ProductItem> query = pm.newNamedQuery(
  ProductItem.class, "PriceBelow10");
List<ProductItem> items = query.executeList();
```

### 3.3. 关闭查询

为了节省资源，我们可以关闭查询：

```java
query.close();
```

同样，我们可以通过将特定结果集作为参数传递给close()方法来关闭它：

```java
query.close(ResultSet);
```

### 3.4. 编译查询

如果我们想要验证一个查询，我们可以调用compile()方法：

```java
query.compile();
```

如果查询无效，则该方法将抛出JDOException。

## 4.JDOQL

JDOQL 是一种基于对象的查询语言，旨在提供 SQL 语言的强大功能并在应用程序模型中保留Java对象关系。

JDOQL 查询可以定义为单字符串形式。

在我们深入研究之前，让我们回顾一些基本概念：

### 4.1. 考生班

JDOQL 中的候选类必须是可持久类。我们使用完整的类名而不是 SQL 语言中的表名：

```java
Query query = pm.newQuery("SELECT FROM com.baeldung.jdo.query.ProductItem");
List<ProductItem> r = query.executeList();
```

正如我们在上面的例子中看到的，com.baeldung.jdo.query.ProductItem是这里的候选类。

### 4.2. 筛选

过滤器可以用Java编写，但必须评估为布尔值：

```java
Query query = pm.newQuery("SELECT FROM com.baeldung.jdo.query.ProductItem");
query.setFilter("status == 'SoldOut'");
List<ProductItem> result = query.executeList();
```

### 4.3. 方法

JDOQL 并不支持所有的Java方法，但是它支持我们可以从查询中调用它们的各种方法，并且可以使用的范围很广：

```java
query.setFilter("this.name.startsWith('supported')");
```

有关支持方法的更多详细信息，请查看此[链接](http://www.datanucleus.org/products/accessplatform_5_1/jdo/query.html#jdoql_methods)。

### 4.4. 参数

我们可以将值作为参数传递给查询。我们可以显式或隐式定义参数。

要显式定义参数：

```java
Query query = pm.newQuery(
  "SELECT FROM com.baeldung.jdo.query.ProductItem "
  + "WHERE price < threshold PARAMETERS double threshold");
List<ProductItem> result = (List<ProductItem>) query.execute(10);
```

这也可以使用setParameters方法来实现：

```java
Query query = pm.newQuery(
  "SELECT FROM com.baeldung.jdo.query.ProductItem "
  + "WHERE price < :threshold");
query.setParameters("double threshold");
List<ProductItem> result = (List<ProductItem>) query.execute(10);
```

我们可以通过不定义参数类型来隐式地做到这一点：

```java
Query query = pm.newQuery(
  "SELECT FROM com.baeldung.jdo.query.ProductItem "
  + "WHERE price < :threshold");
List<ProductItem> result = (List<ProductItem>) query.execute(10);

```

## 5. JDOQL类型化

要使用JDOQLTypedQueryAPI，我们需要准备环境。

### 5.1. Maven 设置

```xml
<dependency>
    <groupId>org.datanucleus</groupId>
    <artifactId>datanucleus-jdo-query</artifactId>
    <version>5.0.2</version>
</dependency>
...
<plugins>
    <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <source>1.8</source>
            <target>1.8</target>
        </configuration>
    </plugin>
</plugins>
```

这些依赖项的最新版本是[datanucleus-jdo-query](https://search.maven.org/classic/#search|gav|1|g%3A"org.datanucleus" AND a%3A"datanucleus-jdo-query")和[maven-compiler-plugin。](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-compiler-plugin")

### 5.2. 启用注解处理

对于Eclipse，我们可以按照下面的步骤来启用注解处理：

1.  转到Java Compiler并确保编译器合规级别为 1.8 或更高
2.  转到Java Compiler → Annotation Processing并启用项目特定设置并启用注解处理
3.  转到Java Compiler → Annotation Processing → Factory Path，启用项目特定设置，然后将以下 jar 添加到列表中：javax.jdo.jar、datanucleus-jdo-query.jar

上面的准备意味着每当我们编译可持久化类时，datanucleus-jdo-query.jar 中的注解处理器会为每个被@PersistenceCapable 注解的类生成一个查询类。

在我们的例子中，处理器生成一个QProductItem类。生成的类与持久化类的名称几乎相同，只是前缀为 Q。

### 5.3. 创建 JDOQL 类型化查询：

```java
JDOQLTypedQuery<ProductItem> tq = pm.newJDOQLTypedQuery(ProductItem.class);
QProductItem cand = QProductItem.candidate();
tq = tq.filter(cand.price.lt(10).and(cand.name.startsWith("pro")));
List<ProductItem> results = tq.executeList();
```

我们可以利用查询类来访问候选字段并使用其可用的Java方法。

## 6. SQL

如果我们使用 RDBMS，JDO 支持 SQL 语言。

让我们创建 SQL 查询：

```java
Query query = pm.newQuery("javax.jdo.query.SQL","select  from "
  + "product_item where price < ? and status = ?");
query.setClass(ProductItem.class);
query.setParameters(10,"InStock");
List<ProductItem> results = query.executeList();
```

当我们执行查询时，我们使用setClass()查询来检索ProductItem对象。否则，它会检索一个Object类型。

## 7.JPQL

JDO DataNucleus 提供了JPQL 语言。

让我们使用 JPQL 创建一个查询：

```java
Query query = pm.newQuery("JPQL","select i from "
  + "com.baeldung.jdo.query.ProductItem i where i.price < 10"
  + " and i.status = 'InStock'");
List<ProductItem> results = (List<ProductItem>) query.execute();
```

这里的实体名称是com.baeldung.jdo.query.ProductItem。我们不能只使用类名。这是因为 JDO 没有元数据来定义像 JPA 这样的实体名称。我们已经定义了一个ProductItem p，之后，我们可以使用p作为别名来引用ProductItem。

有关 JPQL 语法的更多详细信息，请查看此[链接](http://www.datanucleus.org/products/accessplatform_5_1/jpa/getting_started.html)。

## 八. 总结

在本文中，我们展示了 JDO 支持的不同查询语言。我们展示了如何保存命名查询以供重用，解释了 JDOQL 概念，并展示了如何将 SQL 和 JPQL 与 JDO 一起使用。