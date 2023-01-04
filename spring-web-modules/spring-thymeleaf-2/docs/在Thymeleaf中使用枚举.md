## 1. 简介

在本快速教程中，我们将学习如何在[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)中使用枚举。

我们将从在下拉列表中列出枚举值开始。之后，我们将研究在我们的模板中使用我们的枚举进行流量控制。

## 2. 设置

让我们首先将Thymeleaf的[Spring Boot Starter](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <versionId>2.2.2.RELEASE</versionId>
</dependency>
```

我们将使用具有多种颜色选择的小部件，所以让我们定义我们的Color枚举：

```java
public enum Color {
    BLACK, BLUE, RED, YELLOW, GREEN, ORANGE, PURPLE, WHITE
}
```

现在，让我们创建我们的Widget类：

```java
public class Widget {
    private String name;
    private Color color;

    // Standard getters/setters
}
```

## 3. 在下拉菜单中显示枚举

让我们使用[select和option](https://www.baeldung.com/thymeleaf-select-option)创建一个使用我们的Color枚举的下拉列表：

```html
<select name="color">
    <option th:each="colorOpt : ${T(cn.tuyucheng.taketoday.thymeleaf.model.Color).values()}" 
        th:value="${colorOpt}" th:text="${colorOpt}"></option>
</select>
```

T运算符是[Spring表达式语言](https://www.baeldung.com/spring-expression-language)的一部分，用于指定类的实例或访问静态方法。

如果我们运行我们的应用程序并导航到我们的小部件入口页面，我们将在颜色下拉列表中看到我们所有的颜色：

<img src="../assets/img.png">

当我们提交表单时，我们的Widget对象将填充选定的颜色：

<img src="../assets/img_1.png">

## 4. 使用显示名称

由于所有大写字母可能有点不和谐，让我们扩展我们的示例以提供更加用户友好的下拉标签。

我们将从修改Color枚举以提供显示名称开始：

```java
public enum Color {
    BLACK("Black"),
    BLUE("Blue"),
    RED("Red"),
    YELLOW("Yellow"),
    GREEN("Green"),
    ORANGE("Orange"),
    PURPLE("Purple"),
    WHITE("White");

    private final String displayValue;

    private Color(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
```

接下来，让我们转到我们的Thymeleaf模板并更新我们的下拉列表以使用新的displayValue：

```html
<select name="color">
    <option th:each="colorOpt : ${T(cn.tuyucheng.taketoday.thymeleaf.model.Color).values()}" 
        th:value="${colorOpt}" th:text="${colorOpt.displayValue}"></option>
</select>
```

我们的下拉菜单现在显示更易读的颜色名称：

<img src="../assets/img_2.png">

## 5. if语句

有时我们可能希望根据枚举的值来改变我们显示的内容。我们可以将Color枚举与[Thymeleaf条件语句](https://www.baeldung.com/spring-thymeleaf-conditionals)一起使用。

假设我们对一些可能的小部件颜色有意见。

我们可以使用带有Color枚举的Thymeleaf if语句来有条件地显示文本：

```html
<div th:if="${widget.color == T(cn.tuyucheng.taketoday.thymeleaf.model.Color).RED}">
    This color screams danger.
</div>
```

另一种选择是使用字符串比较：

```html
<div th:if="${widget.color.name() == 'GREEN'}">
    Green is for go.
</div>
```

## 6. Switch-Case语句

除了if语句之外，Thymeleaf还支持switch-case语句。

让我们对Color枚举使用switch-case语句：

```html
<div th:switch="${widget.color}">
    <span th:case="${T(cn.tuyucheng.taketoday.thymeleaf.model.Color).RED}"
          style="color: red;">Alert</span>
    <span th:case="${T(cn.tuyucheng.taketoday.thymeleaf.model.Color).ORANGE}"
          style="color: orange;">Warning</span>
    <span th:case="${T(cn.tuyucheng.taketoday.thymeleaf.model.Color).YELLOW}"
          style="color: yellow;">Caution</span>
    <span th:case="${T(cn.tuyucheng.taketoday.thymeleaf.model.Color).GREEN}"
          style="color: green;">All Good</span>
</div>
```

如果我们输入一个橙色的小部件，我们应该看到我们的警告声明：

<img src="../assets/img_3.png">

## 7. 总结

在本教程中，我们首先使用Thymeleaf使用我们在应用程序中定义的Color枚举创建下拉列表。从那里，我们学习了如何使下拉显示值更具可读性。

在使用下拉列表检查输入端之后，我们转到输出端并学习了如何在控制结构中使用枚举。我们使用if和switch-case语句来根据我们的Color枚举来调节某些元素。