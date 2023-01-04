## 1. 简介

[Thymeleaf](http://www.thymeleaf.org/)是一个能够直接与Spring一起工作的Java模板引擎。有关Thymeleaf和Spring的介绍，请查看[这篇](https://www.baeldung.com/thymeleaf-in-spring-mvc)文章。

除了这些基本功能外，Thymeleaf还为我们提供了一组实用对象，可帮助我们在应用程序中执行常见任务。

在本教程中，我们将使用Thymeleaf 3.0的一些功能讨论新旧Java Date类的处理和格式化。

## 2. Maven依赖

首先，让我们创建配置以将Thymeleaf与Spring集成到我们的pom.xml中：

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

可以在Maven Central上找到最新版本的[thymeleaf](https://search.maven.org/search?q=a:thymeleaf)和[thymeleaf-spring5](https://search.maven.org/search?q=a:thymeleaf-spring5)。请注意，对于Spring 4项目，必须使用thymeleaf-spring4库而不是thymeleaf-spring5。

此外，为了使用新的Java 8 Date类，我们需要向pom.xml添加另一个依赖项：

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-java8time</artifactId>
    <version>3.0.4.RELEASE</version>
</dependency>
```

[thymeleaf extras](https://search.maven.org/classic/#search|gav|1|a%3A"thymeleaf-extras-java8time")是一个可选模块，由Thymeleaf官方团队完全支持，它是为与Java 8 Time API兼容而创建的。它在表达式评估期间将一个#tempals对象添加到Context作为实用对象处理器。这意味着它可用于计算对象图导航语言(OGNL)和Spring表达式语言(Spring EL)中的表达式。

## 3. 新旧：java.util和java.time

Time包是用于JavaSE平台的新日期、时间和日历API。这个新API与旧的遗留日期API之间的主要区别在于，新API区分机器和人类对时间线的看法。机器视图揭示了一系列与纪元相关的整数值，而人类视图揭示了一组字段(例如，年、月和日)。

要使用新的Time包，我们需要配置模板引擎以使用新的Java8TimeDialect：

```java
private ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
    SpringTemplateEngine engine = new SpringTemplateEngine();
    engine.addDialect(new Java8TimeDialect());
    engine.setTemplateResolver(templateResolver);
    return engine;
}
```

这将添加类似于标准方言中的#temporal对象，允许从Thymeleaf模板格式化和创建Temporal对象。

为了测试新旧类的处理，我们将创建以下变量并将它们作为模型对象添加到我们的控制器类中：

```java
model.addAttribute("standardDate", new Date());
model.addAttribute("localDateTime", LocalDateTime.now());
model.addAttribute("localDate", LocalDate.now());
model.addAttribute("timestamp", Instant.now());
```

现在，我们已准备好使用Thymeleaf的Expression和Temporals实用程序对象。

### 3.1 格式化日期

我们要介绍的第一个功能是Date对象的格式(添加到Spring模型参数中)。我们将使用ISO8601格式：

```html
<h1>Format ISO</h1>
<p th:text="${#dates.formatISO(standardDate)}"></p>
<p th:text="${#temporals.formatISO(localDateTime)}"></p>
<p th:text="${#temporals.formatISO(localDate)}"></p>
<p th:text="${#temporals.formatISO(timestamp)}"></p>
```

无论我们的Date在后端怎么设置，都会按照选择的标准显示在Thymeleaf中。standardDate将由#dates实用程序处理。新的LocalDateTime、LocalDate和Instant类将由#temporals实用程序处理。

此外，如果我们想手动设置格式，我们可以使用：

```html
<h1>Format manually</h1>
<p th:text="${#dates.format(standardDate, 'dd-MM-yyyy HH:mm')}"></p>
<p th:text="${#temporals.format(localDateTime, 'dd-MM-yyyy HH:mm')}"></p>
<p th:text="${#temporals.format(localDate, 'MM-yyyy')}"></p>
```

正如我们所观察到的，我们无法使用#temporals.format(...)处理Instant类——它会导致UnsupportedTemporalTypeException。此外，仅当我们仅指定特定日期字段而跳过时间字段时，才有可能格式化LocalDate 。

让我们看看最终结果：

<img src="../assets/img.png">

### 3.2 获取特定日期字段

为了获取java.util.Date类的特定字段，我们应该使用以下实用程序对象：

```plaintext
${#dates.day(date)}
${#dates.month(date)}
${#dates.monthName(date)}
${#dates.monthNameShort(date)}
${#dates.year(date)}
${#dates.dayOfWeek(date)}
${#dates.dayOfWeekName(date)}
${#dates.dayOfWeekNameShort(date)}
${#dates.hour(date)}
${#dates.minute(date)}
${#dates.second(date)}
${#dates.millisecond(date)}
```

对于新的java.time包，我们应该坚持使用#temporals utilities：

```html
${#temporals.day(date)}
${#temporals.month(date)}
${#temporals.monthName(date)}
${#temporals.monthNameShort(date)}
${#temporals.year(date)}
${#temporals.dayOfWeek(date)}
${#temporals.dayOfWeekName(date)}
${#temporals.dayOfWeekNameShort(date)}
${#temporals.hour(date)}
${#temporals.minute(date)}
${#temporals.second(date)}
${#temporals.millisecond(date)}
```

让我们看几个例子。首先，让我们显示今天是星期几：

```html
<h1>Show only which day of a week</h1>
<p th:text="${#dates.day(standardDate)}"></p>
<p th:text="${#temporals.day(localDateTime)}"></p>
<p th:text="${#temporals.day(localDate)}"></p>
```

接下来，让我们显示工作日的名称：

```html
<h1>Show the name of the week day</h1>
<p th:text="${#dates.dayOfWeekName(standardDate)}"></p>
<p th:text="${#temporals.dayOfWeekName(localDateTime)}"></p>
<p th:text="${#temporals.dayOfWeekName(localDate)}"></p>
```

最后，让我们显示当天的当前秒数：

```html
<h1>Show the second of the day</h1>
<p th:text="${#dates.second(standardDate)}"></p>
<p th:text="${#temporals.second(localDateTime)}"></p>
```

请注意，为了使用时间部分，我们需要使用LocalDateTime，因为LocalDate会抛出错误。

## 4. 如何在表单中使用日期选择器

让我们看看如何使用日期选择器从Thymeleaf表单提交日期值。

首先，让我们创建一个带有Date属性的Student类：

```java
public class Student implements Serializable {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
}
```

@DateTimeFormat注解声明birthDate字段应格式化为Date。

接下来，我们将创建一个Thymeleaf表单来提交日期输入：

```xml
<form th:action="@{/saveStudent}" method="post" th:object="${student}">
    <div>
        <label for="student-birth-date">Date of birth:</label>
        <input type="date" th:field="${student.birthDate}" id="student-birth-date"/>
    </div>
    <div>
        <button type="submit" class="button">Submit</button>
    </div>
</form>
```

当我们提交表单时，控制器将拦截映射到表单中具有th:object属性的Student对象。此外，th:field属性将输入值与birthDate字段绑定。

现在，让我们创建一个控制器来拦截POST请求：

```java
@RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
public String saveStudent(Model model, @ModelAttribute("student") Student student) {
    model.addAttribute("student", student);
    return "datePicker/displayDate.html";
}
```

提交表单后，我们将 在另一个页面上使用dd/MM/yyyy模式显示birthDate值：

```xml
<h1>Student birth date</h1>
<p th:text="${#dates.format(student.birthDate, 'dd/MM/yyyy')}"></p>
```

结果显示了带有日期选择器的表单：

<img src="../assets/img_1.png">

提交表单后，我们将看到选定的日期：

<img src="../assets/img_2.png">

## 5. 总结

在本快速教程中，我们讨论了在Thymeleaf框架3.0版本中实现的Java日期处理功能。

如何测试？我们的建议是先在浏览器中运行代码，然后再检查我们现有的JUnit测试。

请注意，我们的示例并未涵盖Thymeleaf中的所有可用选项。如果你想了解所有类型的实用程序，请查看我们关于[Spring和Thymeleaf表达式](https://www.baeldung.com/spring-thymeleaf-3-expressions)的文章。