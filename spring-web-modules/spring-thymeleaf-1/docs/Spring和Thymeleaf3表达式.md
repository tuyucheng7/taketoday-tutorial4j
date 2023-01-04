## 1. 简介

[Thymeleaf](http://www.thymeleaf.org/)是一个Java模板引擎，用于处理和创建HTML、XML、JavaScript、CSS和纯文本。有关Thymeleaf和Spring的介绍，请查看[这篇](https://www.baeldung.com/thymeleaf-in-spring-mvc)文章。

除了这些基本功能外，Thymeleaf还为我们提供了一组实用对象，可帮助我们在应用程序中执行常见任务。

在本文中，我们将讨论Thymeleaf 3.0的核心功能，Spring MVC应用程序中的表达式实用程序对象。更具体地说，我们将涵盖处理日期、日历、字符串、对象等主题。

## 2. Maven依赖

首先，让我们看看将Thymeleaf与Spring集成所需的配置。我们的依赖项中需要thymeleaf-spring库：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

请注意，对于Spring 4项目， 必须使用thymeleaf-spring4库而不是thymeleaf-spring5。可以在[此处](https://search.maven.org/search?q=a:thymeleaf-spring5)找到最新版本的依赖项。

## 3. 表达式实用对象

在查看本文的核心重点之前，如果你想退一步看看如何在你的Web应用程序项目中配置Thymeleaf 3.0，请查看本[教程](https://www.baeldung.com/spring-thymeleaf-3)。

出于当前文章的目的，我们创建了一个Spring控制器和HTML文件以测试我们将要讨论的所有功能。以下是可用帮助对象及其功能的完整列表：

-   #dates：java.util.Date对象的实用方法
-   #calendars：类似于#dates，用于java.util.Calendar对象
-   #numbers：用于格式化数字对象的实用方法
-   #strings：String对象的实用方法
-   #objects：Java Object类的一般实用方法
-   #bools：布尔值评估的实用方法
-   #arrays：数组的实用方法
-   #lists：列表的实用方法
-   #sets：集合的实用方法
-   #maps：Map的实用方法
-   #aggregates：用于在数组或集合上创建聚合的实用方法
-   #messages：用于在变量表达式中获取外部化消息的实用方法

### 3.1 日期对象

我们要讨论的第一个功能是处理java.util.Date对象。负责日期处理的表达式实用程序对象以#dates.functionName()开头。我们要介绍的第一个功能是格式化Date对象(添加到Spring模型参数中)。

假设我们要使用ISO8601格式：

```html
<p th:text="${#dates.formatISO(date)}"></p>
```

不管我们后台怎么设置日期，都需要按照这个标准来显示。更重要的是，如果我们想要具体格式，我们可以手动指定它：

```html
<p th:text="${#dates.format(date, 'dd-MM-yyyy HH:mm')}"></p>
```

该函数采用两个变量作为参数：日期及其格式。

最后，这里有一些我们可以使用的类似有用的函数：

```html
<p th:text="${#dates.dayOfWeekName(date)}"></p>
<p th:text="${#dates.createNow()}"></p>
<p th:text="${#dates.createToday()}"></p>
```

首先，我们将收到星期几的名称，第二个我们将创建一个新的日期对象，最后我们将创建一个时间设置为00:00的新日期。

### 3.2 日历对象

日历实用程序与日期处理非常相似，只是我们使用的是java.util.Calendar对象的实例：

```html
<p th:text="${#calendars.formatISO(calendar)}"></p>
<p th:text="${#calendars.format(calendar, 'dd-MM-yyyy HH:mm')}"></p>
<p th:text="${#calendars.dayOfWeekName(calendar)}"></p>
```

唯一的区别是当我们想要创建新的Calendar实例时：

```html
<p th:text="${#calendars.createNow().getTime()}"></p>
<p th:text="${#calendars.createToday().getFirstDayOfWeek()}"></p>
```

请注意，我们可以使用任何Calendar类方法来获取请求的数据。

### 3.3 号码处理

另一个非常少的功能是数字处理。让我们关注一个随机创建的双精度类型的num变量：

```html
<p th:text="${#numbers.formatDecimal(num,2,3)}"></p>
<p th:text="${#numbers.formatDecimal(num,2,3,'COMMA')}"></p>
```

在第一行中，我们通过设置最小整数位数和精确的小数位数来格式化十进制数。在第二个中，除了整数和小数位之外，我们还指定了小数点分隔符。选项是POINT、COMMA、WHITESPACE、NONE或DEFAULT(按语言环境)。

我们还想在本段中介绍一个功能。它是创建一个整数序列：

```html
<p th:each="number: ${#numbers.sequence(0,2)}">
    <span th:text="${number}"></span>
</p>
<p th:each="number: ${#numbers.sequence(0,4,2)}">
    <span th:text="${number}"></span>
</p>
```

在第一个示例中，我们让Thymeleaf生成一个从0-2的序列，而在第二个示例中，除了最小值和最大值之外，我们还提供了步长的定义(在此示例中，值将改变两个)。

请注意，间隔在两侧都是封闭的。

### 3.4 字符串操作

它是表达实用对象最全面的特征。

我们可以从检查空或null String对象的实用程序开始描述。通常，开发人员会在Thymeleaf标记内使用Java方法来执行此操作，这对于null对象可能不安全。

相反，我们可以这样做：

```html
<p th:text="${#strings.isEmpty(string)}"></p>
<p th:text="${#strings.isEmpty(nullString)}"></p>
<p th:text="${#strings.defaultString(emptyString,'Empty String')}"></p>
```

第一个String不为空，因此该方法将返回false。第二个String是null，所以我们会得到true。最后，我们可以使用#strings.defaultString(...)方法指定默认值，如果String将为空。

还有更多的方法。它们不仅适用于字符串，还适用于Java.Collections。例如使用子字符串相关的操作：

```html
<p th:text="${#strings.indexOf(name,frag)}"></p>
<p th:text="${#strings.substring(name,3,5)}"></p>
<p th:text="${#strings.substringAfter(name,prefix)}"></p>
<p th:text="${#strings.substringBefore(name,suffix)}"></p>
<p th:text="${#strings.replace(name,'las','ler')}"></p>
```

或者使用null-safe比较和连接：

```html
<p th:text="${#strings.equals(first, second)}"></p>
<p th:text="${#strings.equalsIgnoreCase(first, second)}"></p>
<p th:text="${#strings.concat(values...)}"></p>
<p th:text="${#strings.concatReplaceNulls(nullValue, values...)}"></p>
```

最后，还有与文本样式相关的功能，它们将保持语法始终相同：

```html
<p th:text="${#strings.abbreviate(string,5)} "></p>
<p th:text="${#strings.capitalizeWords(string)}"></p>
```

在第一种方法中，缩写文本将使其最大尺寸为n。如果文本较大，它将被裁剪并以“...”结束。

在第二种方法中，我们将大写单词。

### 3.5 聚合

我们要在这里讨论的最后但并非最不重要的功能是聚合。它们是null安全的，并提供实用程序来计算数组或任何其他集合的平均值或总和：

```html
<p th:text="${#aggregates.sum(array)}"></p>
<p th:text="${#aggregates.avg(array)}"></p>
<p th:text="${#aggregates.sum(set)}"></p>
<p th:text="${#aggregates.avg(set)}"></p>
```

## 4. 总结

在本文中，我们讨论了在Thymeleaf框架3.0版中实现的表达式实用程序对象功能。

本教程的完整实现可以在[GitHub项目](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-web-modules/spring-thymeleaf)中找到。

如何测试？我们的建议是先使用浏览器，然后再检查现有的JUnit测试。

请注意，示例并未涵盖所有可用的实用程序表达式。如果你想了解所有类型的实用程序，请查看[此处](http://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#appendix-b-expression-utility-objects)。