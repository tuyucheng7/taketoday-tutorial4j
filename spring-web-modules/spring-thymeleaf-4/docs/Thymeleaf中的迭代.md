## 1. 概述

[Thymeleaf](https://www.thymeleaf.org/)是一个通用的Java模板引擎，用于处理XML、XHTML和HTML5文档。

在本快速教程中，我们将了解如何使用Thymeleaf执行迭代，以及该库提供的一些其他功能。

有关Thymeleaf的更多信息，请在[此处](https://www.baeldung.com/thymeleaf-in-spring-mvc)查看我们的介绍性文章。

## 2. Maven依赖

要创建此示例，我们将结合使用Spring Framework库和Thymeleaf库。

在这里我们可以看到我们的依赖项([thymeleaf](https://search.maven.org/search?q=a:thymeleaf)和[thymeleaf-spring](https://search.maven.org/search?q=thymeleaf-spring4))：

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

## 3. 示例设置

在我们进入视图层之前，让我们为示例创建MVC结构。

从模型层的代码片段开始：

```java
public class Student implements Serializable {
    private Integer id;
    private String name;
    // standard constructors, getters, and setters
}
```

我们还提供负责加载模型并将其返回到视图层的控制器方法：

```java
@GetMapping("/listStudents")
public String listStudent(Model model) {
    model.addAttribute("students", StudentUtils.buildStudents());
    return "listStudents.html";
}
```

在我们上面的示例中，buildStudents()方法只返回一个Student对象列表，然后我们将其添加到模型中。

## 4. th:each属性

在Thymeleaf中，迭代是通过使用th:each属性实现的。

关于这个属性的一个有趣的事情是它会接受和迭代一些不同的数据类型，例如：

-   实现java.util.Iterable的对象
-   实现java.util.Map 的对象
-   数组
-   任何其他对象都被视为包含一个元素的单值列表

现在让我们使用在上面示例中设置的数据调用th:each属性：

```html
<tr th:each="student: ${students}">
    <td th:text="${student.id}" />
    <td th:text="${student.name}" />
</tr>
```

代码片段显示了th:each遍历我们的Students列表。使用${}符号访问模型属性，列表中的每个元素都通过学生变量传递到循环体。

## 5. 状态变量

Thymeleaf还启用了一种有用的机制来通过状态变量跟踪迭代过程。

状态变量提供以下属性：

-   index：当前迭代索引，从0(零)开始
-   count：到目前为止处理的元素数
-   size：列表中元素的总数
-   even/odd：检查当前迭代索引是偶数还是奇数
-   first：检查当前迭代是否是第一个迭代
-   last：检查当前迭代是否是最后一个

让我们看看状态变量在我们的例子中是如何工作的：

```html
<tr 
  th:each="student, iStat : ${students}" 
  th:style="${iStat.odd}? 'font-weight: bold;'" 
  th:alt-title="${iStat.even}? 'even' : 'odd'">
    <td th:text="${student.id}" />
    <td th:text="${student.name}" />
</tr>
```

在这里，我们包含了iStat.odd属性来评估条件并为当前行设置粗体样式。下一次评估也是如此，但这次我们使用iStat.even 通过alt/titleHTML属性打印一个值。

如果我们省略显式创建状态变量(在我们的示例中显示为iStat)，我们可以通过简单地使用studentStat来调用我们的状态变量，它是带有后缀Stat的变量学生的聚合。

## 6. 总结

在本文中，我们探讨了Thymeleaf库提供的众多功能之一。

我们通过使用属性th:each及其开箱即用的属性在Thymeleaf中展示了迭代。