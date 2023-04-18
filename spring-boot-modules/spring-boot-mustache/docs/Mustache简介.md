## 1. 概述

在本文中，我们将重点介绍[Mustache](https://mustache.github.io/)模板并使用其[Java API](https://github.com/spullara/mustache.java)之一来生成动态HTML内容。

**Mustache是一个无逻辑的模板引擎，用于创建动态内容**，如HTML、配置文件等。

## 2. 简介

简而言之，该引擎被归类为无逻辑引擎，因为它没有支持if-else语句和for循环的结构。

Mustache模板由{{}}包围的标签名称组成(类似于胡须-因此得名)，并由包含模板数据的模型对象支持。

## 3. Maven依赖

多种语言(客户端和服务器端)都支持模板的编译和执行。

为了能够处理来自Java的模板，我们使用了它的Java库，该库可以作为Maven依赖项添加。

Java 8+：

```xml
<dependency>
    <groupId>com.github.spullara.mustache.java</groupId>
    <artifactId>compiler</artifactId>
    <version>0.9.4</version>
</dependency>
```

Java 6/7：

```xml
<dependency>
    <groupId>com.github.spullara.mustache.java</groupId>
    <artifactId>compiler</artifactId>
    <version>0.8.18</version>
</dependency>
```

我们可以在[中央Maven仓库](https://central.sonatype.com/artifact/com.github.spullara.mustache.java/compiler/0.9.10)中检查最新版本的库。

## 4. 用法

让我们看一个简单的场景，它展示了如何：

1.  编写一个简单的模板
2.  使用Java API编译模板
3.  通过提供必要的数据来执行它

### 4.1 一个简单的Mustache模板

我们将创建一个简单的模板来显示待办事项的详细信息：

```html
<h2>{{title}}</h2>
<small>Created on {{createdOn}}</small>
<p>{{text}}</p>
```

在上面的模板中，大括号({{}})中的字段可以是：

-   Java类的方法和属性
-   Map对象的键

### 4.2 编译Mustache模板

我们可以按如下方式编译模板：

```java
MustacheFactory mf = new DefaultMustacheFactory();
Mustache m = mf.compile("todo.mustache");
```

MustacheFactory在类路径中搜索给定的模板。在我们的示例中，我们将todo.mustache放在src/main/resources下。

### 4.3 执行Mustache模板

提供给模板的数据将是Todo类的一个实例，其定义是：

```java
public class Todo {
    private String title;
    private String text;
    private boolean done;
    private Date createdOn;
    private Date completedOn;

    // constructors, getters and setters
}
```

可以执行编译的模板来获取HTML，如下所示：

```java
Todo todo = new Todo("Todo 1", "Description");
StringWriter writer = new StringWriter();
m.execute(writer, todo).flush();
String html = writer.toString();
```

## 5. Mustache部分和迭代

现在让我们看看如何列出待办事项。**为了遍历列表数据，我们使用了Mustache部分**。

一个部分是一段代码，根据当前上下文中键的值重复一次或多次。

它看起来像：

```html
{{#todo}}
<!-- Other code -->
{{/todo}}
```

一个部分以井号(#)开头，以斜杠(/)结束，其中每个符号后跟一个键，键的值用作呈现该部分的基础。

以下是根据键的值可能发生的情况：

### 5.1 具有非空列表或非假值的部分

让我们创建一个使用部分的模板todo-section.mustache：

```html
{{#todo}}
<h2>{{title}}</h2>
<small>Created on {{createdOn}}</small>
<p>{{text}}</p>
{{/todo}}
```

让我们看看这个模板的实际效果：

```java
@Test
public void givenTodoObject_whenGetHtml_thenSuccess() throws IOException {
    Todo todo = new Todo("Todo 1", "Todo description");
    Mustache m = MustacheUtil.getMustacheFactory().compile("todo.mustache");
    Map<String, Object> context = new HashMap<>();
    context.put("todo", todo);
 
    String expected = "<h2>Todo 1</h2>";
    assertThat(executeTemplate(m, todo)).contains(expected);
}
```

让我们创建另一个模板todos.mustache来列出待办事项：

```html
{{#todos}}
<h2>{{title}}</h2>
{{/todos}}
```

并使用它创建待办事项列表：

```java
@Test
public void givenTodoList_whenGetHtml_thenSuccess() throws IOException {
    Mustache m = MustacheUtil.getMustacheFactory().compile("todos.mustache");
 
    List<Todo> todos = Arrays.asList(
        new Todo("Todo 1", "Todo description"),
        new Todo("Todo 2", "Todo description another"),
        new Todo("Todo 3", "Todo description another")
    );
    Map<String, Object> context = new HashMap<>();
    context.put("todos", todos);
 
    assertThat(executeTemplate(m, context))
        .contains("<h2>Todo 1</h2>")
        .contains("<h2>Todo 2</h2>")
        .contains("<h2>Todo 3</h2>");
}
```

### 5.2 具有空列表或错误或空值的部分

让我们用空值测试todo-section.mustache：

```java
@Test
public void givenNullTodoObject_whenGetHtml_thenEmptyHtml() throws IOException {
    Mustache m = MustacheUtil.getMustacheFactory()
        .compile("todo-section.mustache");
    Map<String, Object> context = new HashMap<>();
    assertThat(executeTemplate(m, context)).isEmpty();
}
```

同样，使用空列表测试todos.mustache：

```java
@Test
public void givenEmptyList_whenGetHtml_thenEmptyHtml() throws IOException {
    Mustache m = MustacheUtil.getMustacheFactory()
        .compile("todos.mustache");
 
    Map<String, Object> context = new HashMap<>();
    assertThat(executeTemplate(m, context)).isEmpty();;
}
```

## 6. 倒置部分

**倒置部分是基于不存在键或假或空值或空列表而仅呈现一次的部分**。换句话说，这些是在未呈现部分时呈现的。

它们以插入符号(^)开头并以斜杠(/)结尾，如下所示：

```html
{{#todos}}
<h2>{{title}}</h2>
{{/todos}}
{{^todos}}
<p>No todos!</p>
{{/todos}}
```

提供空列表时的上述模板：

```java
@Test
public void givenEmptyList_whenGetHtmlUsingInvertedSection_thenHtml() throws IOException {
    Mustache m = MustacheUtil.getMustacheFactory()
        .compile("todos-inverted-section.mustache");
  
    Map<String, Object> context = new HashMap<>();
    assertThat(executeTemplate(m, context).trim()).isEqualTo("<p>No todos!</p>");
}
```

## 7. Lambda

**Mustache部分的键值可以是函数或lambda表达式**。在这种情况下，通过将部分内的文本作为参数传递给lambda表达式来调用完整的lambda表达式。

让我们看一个模板todos-lambda.mustache：

```html
{{#todos}}
<h2>{{title}}{{#handleDone}}{{doneSince}}{{/handleDone}}</h2>
{{/todos}}
```

handleDone键解析为Java 8 lambda表达式，如下所示：

```java
public Function<Object, Object> handleDone() {
    return (obj) -> done ? String.format("<small>Done %s minutes ago<small>", obj) : "";
}
```

执行上述模板生成的HTML为：

```html
<h2>Todo 1</h2>
<h2>Todo 2</h2>
<h2>Todo 3<small>Done 5 minutes ago<small></h2>
```

## 8. 总结

在这篇介绍性文章中，我们研究了如何创建带有部分、反转部分和lambda的Mustache模板。并且我们通过提供相关数据使用Java API来编译和执行模板。

Mustache有一些更高级的功能值得探索-例如：

-   提供可调用值作为并发评估的值
-   使用DecoratedCollection获取集合元素的第一个、最后一个和索引
-   invert API，它给出给定文本和模板的数据