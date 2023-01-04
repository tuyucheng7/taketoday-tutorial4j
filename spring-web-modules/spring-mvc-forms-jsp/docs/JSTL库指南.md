## 1. 概述

JavaServer Pages标签库(JSTL)是一组标签，可用于实现一些常见操作，如循环、条件格式等。

在本教程中，我们将讨论如何设置JSTL以及如何使用它的众多标签。

## 2. 设置

要启用JSTL功能，我们必须将库添加到我们的项目中。对于Maven项目，我们在pom.xml文件中添加依赖项：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
```

将库添加到我们的项目后，最终设置将是使用taglib指令将核心JSTL标签和任何其他标签的命名空间文件添加到我们的JSP，如下所示：

```html
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
```

接下来，我们将看一下这些大致分为五类的标签。

## 3. 核心标签

JSTL核心标签库包含用于执行基本操作的标签，例如打印值、变量声明、异常处理、执行迭代和声明条件语句等。

让我们看一下核心标签。

### 3.1 \<c:out\>标签

\<c:out\>用于显示变量中包含的值或隐式表达式的结果。

它具有三个属性：value、default和escapeXML，escapeXML属性输出包含在value属性或其附件中的原始XML标签。

\<c:out\>标签的一个例子是：

```html
<c:out value="${pageTitle}"/>
```

### 3.2 \<c:set\>标签

\<c:set\>标签用于在JSP中声明作用域变量。我们还可以分别在var和value属性中声明变量的名称及其值。

一个例子将是以下形式：

```html
<c:set value="JSTL Core Tags Example" var="pageTitle"/>
```

### 3.3 \<c:remove\>标签

\<c:remove\>标签删除范围内的变量，这相当于将null分配给变量。它采用var和scope属性，其范围具有所有范围的默认值。

下面，我们展示了\<c:remove\>标签的示例用法：

```html
<c:remove var="pageTitle"/>
```

### 3.4 \<c:catch\>标签

\<c:catch\>标签捕获在其外壳内抛出的任何异常。如果抛出异常，它的值将存储在此标签的var属性中。

典型用法可能如下所示：

```html
<c:catch var ="exceptionThrown">
    <% int x = Integer.valueOf("a");%>
</c:catch>
```

并检查是否抛出异常，我们使用\<c:if\>标签，如下所示：

```html
<c:if test = "${exceptionThrown != null}">
    <p>The exception is : ${exceptionThrown} <br />
      There is an exception: ${exceptionThrown.message}
    </p>
</c:if>
```

### 3.5 \<c:if\>标签

\<c:if\>是一个条件标签，仅当其测试属性评估为真时才显示或执行它包含的scriptlet，评估的结果可以存储在它的var属性中。

### 3.6 \<c:choose\>、\<c:when\>和\<c:otherwise\>标签

\<c:choose\>是用于执行类似开关或if-else表达式的父标签。它有两个子标签；\<c:when\>和\<c:otherwise\>分别代表if/else-if和else。

\<c:when\>采用一个测试属性，该属性包含要评估的表达式。下面，我们展示了这些标签的示例用法：

```html
<c:set value="<%= Calendar.getInstance().get(Calendar.SECOND)%>" var="seconds"/>
<c:choose>
    <c:when test="${seconds le 30 }">
        <c:out value="${seconds} is less than 30"/>
    </c:when>
    <c:when test="${seconds eq 30 }">
        <c:out value="${seconds} is equal to 30"/>
    </c:when>
    <c:otherwise>
        <c:out value="${seconds} is greater than 30"/>
    </c:otherwise>
</c:choose>
```

### 3.7 \<c:import\>标签

\<c:import\>标签处理从绝对或相对URL获取和公开内容。

我们可以使用url和var属性来保存URL，以及分别从URL中获取的内容。例如，我们可以通过以下方式从URL导入内容：

```html
<c:import var = "data" url = "http://www.example.com"/>
```

### 3.8 \<c:forEach\>标签

\<c:forEach\>标签类似于Java的for、while或do-while语法。items属性包含要迭代的项目列表，而begin和end属性分别包含开始和结束索引(零索引)。

\<c:forEach\>标签还有一个step属性，控制每次迭代后索引增量的大小。下面，我们展示了一个示例用法：

```html
<c:forEach var = "i" items="1,4,5,6,7,8,9">
    Item <c:out value = "No. ${i}"/><p>
</c:forEach>
```

### 3.9 \<c:forTokens\>标签

\<c:forTokens\>标签用于将字符串拆分为标签并遍历它们。

类似于\<c:forEach\>标签，它有一个items属性和一个额外的delim属性，它是字符串的分隔符，如下所示：

```html
<c:forTokens 
  items = "Patrick:Wilson:Ibrahima:Chris" 
  delims = ":" var = "name">
    <c:out value = "Name: ${name}"/><p>
</c:forTokens>
```

### 3.10 \<c:url\>和\<c:param\>标签

\<c:url\>标签对于使用正确的请求编码格式化URL很有用，格式化的URL存储在var属性中。

\<c:url\>标签还有一个\<c:param\>子标签，用于指定URL参数。我们在下面展示一个例子：

```html
<c:url value = "/core_tags" var = "myURL">
    <c:param name = "parameter_1" value = "1234"/>
    <c:param name = "parameter_2" value = "abcd"/>
</c:url>
```

### 3.11 \<c:redirect\>标签

\<c:redirect\>标签执行URL重写并将用户重定向到其url属性中指定的页面。典型的用例如下所示：

```html
<c:redirect url="/core_tags"/>
```

## 4. 格式化标签

JSTL格式化标签库提供了一种方便的方式来格式化文本、数字、日期、时间和其他变量以更好地显示。

JSTL格式化标签也可用于增强网站的国际化。

在使用这些格式化标签之前，我们必须将标签库添加到我们的JSP中：

```html
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
```

让我们识别各种格式标签以及如何使用它们。

### 4.1 \<fmt:formatDate\>标签

\<fmt:formatDate\>标签在格式化日期或时间时很有用。value属性保存要格式化的日期，type属性取三个值之一；日期、时间或两者。

\<fmt:formatDate\>还有一个pattern属性，我们可以在其中指定所需的格式化模式。下面是其中一种模式的示例：

```java
<c:set var="now" value="<%= new java.util.Date()%>"/>
<fmt:formatDate type="time" value="${now}"/>
```

### 4.2 \<fmt:parseDate\>标签

\<fmt:parseDate\>标签类似于\<fmt:formatDate\>标签。

不同之处在于，使用\<fmt:parseDate\>标签，我们可以指定基础日期解析器应该期望日期值所在的格式化模式。

我们可以解析日期：

```bash
<c:set var="today" value="28-03-2018"/>
<fmt:parseDate value="${today}" var="parsedDate" pattern="dd-MM-yyyy"/>
```

### 4.3 \<fmt:formatNumber\>标签

\<fmt:formatNumber\>标签处理以特定模式或精度呈现数字，可以是其类型属性中指定的数字、货币或百分比之一。\<fmt:formatNumber\>的一个示例用法是：

```bash
<c:set var="fee" value="35050.1067"/>
<fmt:formatNumber value="${fee}" type="currency"/>
```

### 4.4 \<fmt:parseNumber\>标签

\<fmt:parseNumber\>标签类似于\<fmt:formatNumber\>标签。不同之处在于，使用\<fmt:parseNumber\>标签，我们可以指定底层数字解析器应该期望数字所在的格式化模式。

我们可以这样使用：

```bash
<fmt:parseNumber var="i" type="number" value="${fee}"/>
```

### 4.5 \<fmt:bundle\>标签

\<fmt:bundle\>标签是\<fmt:message\>标签的父标签。\<fmt:bundle\>将在其basename属性中指定的包添加到包含的\<fmt:message\>标签中。

\<fmt:bundle\>标签对于启用国际化很有用，因为我们可以指定特定于语言环境的对象。典型用法将是以下形式：

```bash
<fmt:bundle basename="cn.tuyucheng.taketoday.jstl.bundles.CustomMessage" prefix="verb.">
    <fmt:message key="go"/><br/>
    <fmt:message key="come"/><br/>
    <fmt:message key="sit"/><br/>
    <fmt:message key="stand"/><br/>
</fmt:bundle>
```

### 4.6 \<fmt:setBundle\>标签

\<fmt:setBundle\>标签用于在JSP中加载资源包并使其在整个页面中可用。加载的资源包存储在\<fmt:setBundle\>标签的var属性中。我们可以通过以下方式设置捆绑：

```java
<fmt:setBundle basename="cn.tuyucheng.taketoday.jstl.bundles.CustomMessage" var="lang"/>
```

### 4.7 \<fmt:setLocale\>标签

\<fmt:setLocale\>标签用于为JSP中放置在其声明之后的部分设置语言环境。通常我们会通过以下方式设置：

```html
<fmt:setLocale value="fr_FR"/>
```

fr_FR代表在这种情况下是法语的语言环境。

### 4.8 \<fmt:timeZone\>标签

\<fmt:timeZone\>标签是一个父标签，它指定由其附件中的标签进行的任何时间格式化或解析操作所使用的时区。

此时区参数由其值属性提供。示例用法如下所示：

```html
<fmt:timeZone value="${zone}">
    <fmt:formatDate value="${now}" timeZone="${zn}" 
      type="both"/>
</fmt:timeZone>
```

### 4.9 \<fmt:setTimeZone\>标签

\<fmt:setTimeZone\>标签可用于将其value属性中指定的时区到其var属性中指定的作用域变量。我们通过以下方式定义它：

```html
<fmt:setTimeZone value="GMT+9"/>
```

### 4.10 \<fmt:message\>标签

\<fmt:message\>标签用于显示国际化消息。要检索的消息的唯一标识符应传递给其键属性。

用于查找消息的特定包，也可以通过bundle属性指定。

这可能看起来像这样：

```html
<fmt:setBundle basename = "cn.tuyucheng.taketoday.jstl.bundles.CustomMessage" var = "lang"/>
<fmt:message key="verb.go" bundle="${lang}"/>
```

### 4.11 \<fmt:requestEncoding\>标签

\<fmt:requestEncoding\>标签在为具有post操作类型的表单指定编码类型时很有用。

要使用的字符编码的名称通过\<fmt:requestEncoding\>标签的key属性提供。

让我们看下面的例子：

```html
<fmt:requestEncoding value = "UTF-8" />
```

## 5. XML标签

JSTL XML标签库提供了在JSP中与XML数据交互的便捷方式。

为了能够访问这些XML标签，我们将通过以下方式将标签库添加到我们的JSP：

```java
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
```

让我们看看JSTL XML标签库中的不同标签。

### 5.1 \<x:out\>标签

\<x:out\>标签类似于JSP中的<%= %> scriptlet标签，但\<x:out\>专门用于XPath表达式。

\<x:out\>标签有一个select和escapeXML属性，分别用于指定XPath表达式来评估String和启用特殊XML字符的转义。

一个简单的例子是：

```html
<x:out select="$output/items/item[1]/name"/>
```

上面的$output指的是预加载的XSL文件。

### 5.2 \<x:parse\>标签

\<x:parse\>标签用于解析在其xml或doc属性或附件中指定的XML数据。一个典型的例子是：

```html
<x:parse xml="${xmltext}" var="output"/>
```

### 5.3 \<x:set\>标签

\<x:set\>标签将在其var属性中指定的变量设置为传递给其select属性的已计算XPath表达式。一个典型的例子是：

```html
<x:set var="fragment" select="$output//item"/>
```

### 5.4 \<x:if\>标签

如果提供给它的select属性的XPath表达式的计算结果为真，则\<x:if\>标签处理它的主体。

评估的结果可以存储在它的var属性中。

一个简单的用例如下所示：

```html
<x:if select="$output//item">
    Document has at least one <item> element.
</x:if>
```

### 5.5 \<x:forEach\>标签

\<x:forEach\>标签用于遍历XML文档中的节点。XML 文档通过\<x:forEach\>标签的select属性提供。

就像\<c:forEach\>核心标签一样，\<x:forEach\>标签也有begin、end和step属性。

因此，我们有：

```html
<ul class="items">
    <x:forEach select="$output/items/item/name" var="item">
        <li>Item Name: <x:out select="$item"/></li>
    </x:forEach>
</ul>
```

### 5.6 \<x:choose\>、\<x:when\>和\<x:otherwise\>标签

\<x:choose\>标签是一个父标签，用于执行switch-like或if/else-if/else表达式，没有属性，但包含\<x:when\>和\<x:otherwise\>标签。

\<x:when\>标签类似于if/else-if并且带有一个select属性，该属性包含要评估的表达式。

\<x:otherwise\>标签类似于else/default子句，没有属性。

下面，我们展示了一个示例用例：

```html
<x:choose>
    <x:when select="$output//item/category = 'Sneakers'">
        Item category is Sneakers
    </x:when>
    <x:when select="$output//item/category = 'Heels'">
        Item category is Heels
    </x:when>
    <x:otherwise>
       Unknown category.
    </x:otherwise>
</x:choose>
```

### 5.7 \<x:transform\>和\<x:param\>标签

\<x:transform\>标签通过应用可扩展样式表语言(XSL)在JSP中转换XML文档。

要转换的XML文档或字符串被提供给doc属性，而要应用的XSL被传递给\<x:transform\>标签的xslt属性。

\<x:param\>标签是\<x:transform\>标签的子标签，用于在转换样式表中设置参数。

一个简单的用例将是以下形式：

```html
<c:import url="/items_xml" var="xslt"/>
<x:transform xml="${xmltext}" xslt="${xslt}">
    <x:param name="bgColor" value="blue"/>
</x:transform>
```

## 6. SQL标签

JSTL SQL标签库提供了用于执行关系数据库操作的标签。

为了启用JSTL SQL标签，我们将标签库添加到我们的JSP中：

```html
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
```

JSTL SQL标签支持不同的数据库，包括MySQL、Oracle和Microsoft SQL Server。

接下来，我们将查看可用的不同SQL标签。

### 6.1 \<sql:setDataSource\>标签

\<sql:setDataSource\>标签用于定义JDBC配置变量。

这些配置变量保存在\<sql:setDataSource\>标签的driver、url、user、password和dataSource属性中，如下所示：

```html
<sql:setDataSource var="dataSource" driver="com.mysql.cj.jdbc.Driver"
  url="jdbc:mysql://localhost/test" user="root" password=""/>
```

在上面，var属性包含一个标识关联数据库的值。

### 6.2 \<sql:query\>标签

\<sql:query\>标签用于执行SQL SELECT语句，结果存储在其var属性中定义的作用域变量中。通常，我们将其定义为：

```html
<sql:query dataSource="${dataSource}" var="result">
    SELECT  from USERS;
</sql:query>
```

\<sql:query\>标签的sql属性包含要执行的 SQL 命令。其他属性包括maxRows、startRow和dataSource。

### 6.3 \<sql:update\>标签

\<sql:update\>标签类似于\<sql:query\>标签，但只执行不需要返回值的SQL INSERT、UPDATE或DELETE操作。

一个示例用法是：

```html
<sql:update dataSource="${dataSource}" var="count">
    INSERT INTO USERS(first_name, last_name, email) VALUES
      ('Grace', 'Adams', 'gracea@domain.com');
</sql:update>
```

\<sql:update\>标签的var属性保存受其sql属性中指定的SQL语句影响的行数。

### 6.4 \<sql:param\>标签

\<sql:param\>标签是一个子标签，可以在\<sql:query\>或\<sql:update\>标签中使用，为sql语句中的值占位符提供值，如下所示：

```html
<sql:update dataSource = "${dataSource}" var = "count">
    DELETE FROM USERS WHERE email = ?
    <sql:param value = "gracea@domain.com" />
</sql:update>
```

\<sql:param\>标签只有一个属性；value保存要提供的值。

### 6.5 \<sql:dateParam\>标签

\<sql:dateParam\>标签在\<sql:query\>或\<sql:update\>标签内使用，为sql语句中的值占位符提供日期和时间值。

我们可以在我们的JSP中这样定义它：

```html
<sql:update dataSource = "${dataSource}" var = "count">
    UPDATE Users SET registered = ? WHERE email = ?
    <sql:dateParam value = "<%=registered%>" type = "DATE" />
    <sql:param value = "<%=email%>" />
</sql:update>
```

与\<sql:param\>标签一样，\<sql:dateParam\>标签有一个value属性和一个附加的type属性，其值可以是date、time或timestamp(日期和时间)之一。

### 6.6 \<sql:transaction\>标签

\<sql:transaction\>标签用于通过将\<sql:query\>和\<sql:update\>标签组合在一起来创建类似JDBC事务的操作，如下所示：

```html
<sql:transaction dataSource = "${dataSource}">
    <sql:update var = "count">
        UPDATE Users SET first_name = 'Patrick-Ellis' WHERE
          email='patrick@tuyucheng.com'
    </sql:update>
    <sql:update var = "count">
        UPDATE Users SET last_name = 'Nelson' WHERE 
          email ='patrick@tuyucheng.com'
    </sql:update>
    <sql:update var = "count">
        INSERT INTO Users(first_name, last_name, email) 
          VALUES ('Grace', 'Adams', 'gracea@domain.com');
    </sql:update>
</sql:transaction>
```

\<sql:transaction\>标签确保所有数据库操作都被成功处理(提交)，或者如果在任何操作中发生错误则全部失败(回滚)。

## 7. JSTL函数

JSTL方法是用于在JSP中进行数据操作的实用程序。虽然一些函数采用不同的数据类型，但它们中的大多数专用于字符串操作。

为了在JSP中启用JSTL方法，我们将taglib添加到我们的页面：

```html
<%@ taglib prefix = "fn"
  uri = "http://java.sun.com/jsp/jstl/functions" %>
```

让我们看看这些功能以及如何使用它们。

### 7.1 fn:contains()和fn:containsIgnoreCase()

fn:contains()方法评估一个String以检查它是否包含给定的子字符串，如下所示：

```html
<c:set var = "string1" value = "This is first string"/>
<c:if test = "${fn:contains(string1, 'first')}">
    <p>Found 'first' in string<p>
</c:if>
```

fn:contains()函数有两个字符串参数；第一个是源字符串，第二个参数是子字符串。它根据评估结果返回一个布尔值。

fn:containsIgnoreCase()函数是fn:contains()方法的不区分大小写的变体，可以像这样使用：

```html
<c:if test = "${fn:containsIgnoreCase(string1, 'first')}">
    <p>Found 'first' string<p>
</c:if>
<c:if test = "${fn:containsIgnoreCase(string1, 'FIRST')}">
    <p>Found 'FIRST' string<p>
</c:if>
```

### 7.3 fn:endsWith()函数

fn:endsWith()函数计算字符串以检查其后缀是否与另一个子字符串匹配。它需要两个参数；第一个参数是要测试其后缀的字符串，而第二个参数是被测试的后缀。

我们可以这样定义：

```html
<c:if test = "${fn:endsWith(string1, 'string')}">
    <p>String ends with 'string'<p>
</c:if>
```

### 7.4 fn:escapeXml()函数

fn:escapeXML()函数用于转义输入字符串中的XML标签，如下所示：

```html
<p>${fn:escapeXml(string1)}</p>
```

### 7.5 fn:indexOf()函数

fn:indexOf()函数查看字符串并返回给定子字符串第一次出现的索引。

它需要两个参数；第一个是源字符串，第二个参数是要匹配并返回第一次出现的子字符串。

fn:indexOf()函数返回一个整数，可以像这样使用：

```html
<p>Index: ${fn:indexOf(string1, "first")}</p>
```

### 7.6 fn:join()函数

fn:join()函数将数组的所有元素连接成一个字符串，可以像这样使用：

```html
<c:set var = "string3" value = "${fn:split(string1, ' ')}" />
<c:set var = "string4" value = "${fn:join(string3, '-')}" />
```

### 7.7 fn:length()函数

fn:length()函数返回给定集合中的元素数或给定字符串中的字符数。

fn:length()函数接受一个对象，它可以是一个集合或一个字符串，并返回一个整数，如下所示：

```html
<p>Length: ${fn:length(string1)}</p>
```

### 7.8 fn:replace()函数

fn:replace()函数用另一个字符串替换一个字符串中出现的所有子字符串。

它需要三个参数；源字符串、要在源中查找的子字符串和要替换所有出现的子字符串的字符串，如下所示：

```html
<c:set var = "string3" value = "${fn:replace(string1, 'first', 'third')}" />
```

### 7.9 fn:split()函数

fn:split()函数使用指定的分隔符对字符串执行拆分操作。这是一个示例用法：

```html
<c:set var = "string3" value = "${fn:split(string1, ' ')}" />
```

### 7.10 fn:startsWith()函数

fn:startsWith()函数检查字符串的前缀，如果它与给定的子字符串匹配则返回true，如下所示：

```html
<c:if test = "${fn:startsWith(string1, 'This')}">
    <p>String starts with 'This'</p>
</c:if>
```

### 7.11 fn:substring()函数

fn:substring()函数在指定的起始索引和结束索引处从源字符串创建子字符串。我们会像这样使用它：

```html
<c:set var = "string3" value = "${fn:substring(string1, 5, 15)}" />
```

### 7.12 fn:substringAfter()函数

fn:substringAfter()函数检查给定子字符串的源字符串，并在第一次出现指定子字符串后立即返回该字符串。

我们会像这样使用它：

```html
<c:set var = "string3" value = "${fn:substringAfter(string1, 'is')}" />
```

### 7.13 fn:substringBefore()函数

fn:substringBefore()函数检查给定子字符串的源字符串，并返回指定子字符串第一次出现之前的字符串。

在我们的JSP页面中，它看起来像这样：

```html
<c:set var = "string3" value = "${fn:substringBefore(string1, 'is')}" />
```

### 7.14 fn:toLowerCase()函数

fn:toLowerCase()函数将字符串中的所有字符转换为小写，可以像这样使用：

```html
<c:set var = "string3" value = "${fn:toLowerCase(string1)}" />
```

### 7.15 fn:toUpperCase()函数

fn:toUpperCase()函数将字符串中的所有字符转换为大写：

```html
<c:set var = "string3" value = "${fn:toUpperCase(string1)}" />
```

### 7.16 fn:trim()函数

fn:trim()函数删除字符串中的前后空格：

```html
<c:set var = "string1" value = "This is first String    "/>
```

## 9. 总结

在这篇内容广泛的文章中，我们了解了各种JSTL标签以及如何使用它们。