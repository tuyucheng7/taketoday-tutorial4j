## 1. 概述

在本教程中，我们将了解Thymeleaf中可用的不同类型的条件。

有关Thymeleaf的快速介绍，请参阅这篇[文章](https://www.baeldung.com/thymeleaf-in-spring-mvc)。

## 2. Maven依赖

让我们从使用Thymeleaf和Spring所需的Maven依赖项开始：

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

对于其他Spring版本，我们应该使用匹配的thymeleaf-springX库，其中X代表Spring版本。另请注意，从3.0.8.RELEASE开始，Thymeleaf支持Spring 5。

所需依赖项的最新版本可在[此处](https://search.maven.org/classic/#search|ga|1|thymeleaf)找到。

## 3. Thymeleaf条件句

我们必须区分允许我们根据条件在HTML元素中呈现文本的条件语句和控制HTML元素本身实例化的条件语句。

让我们定义 我们将在整篇文章中使用的Teacher模型类：

```java
public class Teacher implements Serializable {
    private String gender;
    private boolean isActive;
    private List<String> courses = new ArrayList<>();
    private String additionalSkills;
}
```

### 3.1 Elvis运算符

Elvis运算符?:让我们根据变量的当前状态在HTML元素中呈现文本。

如果变量为null，我们可以使用默认表达式来提供默认文本：

```html
<td th:text="${teacher.additionalSkills} ?: 'UNKNOWN'" />
```

在这里，如果定义了teacher.additionalSkills变量，我们希望显示该变量的内容，否则我们希望呈现文本“UNKNOWN”。

也可以根据布尔表达式显示任意文本：

```html
<td th:text="${teacher.active} ? 'ACTIVE' : 'RETIRED'" />
```

我们可以像前面的示例一样查询一个简单的布尔变量，但也可以进行字符串比较和范围检查。

支持以下比较器及其文本表示：>(gt)、>=(ge)、<(lt)、<=(le)、==(eq)和!=(ne)。

### 3.2 if-unless

th:if和th:unless属性允许我们根据提供的条件呈现HTML元素：

```html
<td>
    <span th:if="${teacher.gender == 'F'}">Female</span>
    <span th:unless="${teacher.gender == 'F'}">Male</span>
</td>
```

如果teacher.gender变量的内容等于F，则呈现值为Female的span元素。否则，渲染带有Male的元素。

这种设置类似于大多数编程语言中存在的if-else子句。

### 3.3 switch-case

如果一个表达式有两个以上的可能结果，我们可以使用th:switch和th:case属性来对HTML元素进行条件渲染：

```html
<td th:switch="${#lists.size(teacher.courses)}">
    <span th:case="'0'">NO COURSES YET!</span>
    <span th:case="'1'" th:text="${teacher.courses[0]}"></span>
    <div th:case="*">
        <div th:each="course:${teacher.courses}" th:text="${course}"/>
    </div>
</td>
```

根据teacher.courses列表的大小，我们显示默认文本、单个课程或所有可用课程。我们使用星号(*)作为默认选项。

## 4. 总结

在这篇简短的文章中，我们研究了不同类型的Thymeleaf条件语句，并提供了一些显示各种选项的简化示例。