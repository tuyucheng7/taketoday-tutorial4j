## 1. 概述

在本教程中，我们将研究[Handlebars.java](https://jknack.github.io/handlebars.java/)库以简化模板管理。

## 2.Maven依赖

让我们从添加[handlebars](https://search.maven.org/search?q=g:com.github.jknack%2Ba:handlebars)依赖项开始：

```xml
<dependency>
    <groupId>com.github.jknack</groupId>
    <artifactId>handlebars</artifactId>
    <version>4.1.2</version>
</dependency>
```

## 3. 一个简单的模板

Handlebars 模板可以是任何类型的文本文件。它由{{name}} 和 {{#each people}} 等标签组成。

然后我们通过传递上下文对象(如Map或其他对象)来填充这些标签。

### 3.1. 使用这个

要将单个字符串值传递给我们的模板，我们可以使用任何对象作为上下文。我们还必须在我们的模板中使用 {{ this}} t标签。

然后 Handlebars 在上下文对象上调用toString方法并用结果替换标签：

```java
@Test
public void whenThereIsNoTemplateFile_ThenCompilesInline() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("Hi {{this}}!");
    
    String templateString = template.apply("Baeldung");
    
    assertThat(templateString).isEqualTo("Hi Baeldung!");
}
```

在上面的示例中，我们首先创建了一个Handlebars 实例，这是我们的 API 入口点。

然后，我们给那个实例我们的模板。在这里，我们只是内联传递模板，但稍后我们会看到一些更强大的方法。

最后，我们为编译后的模板提供上下文。{{this}}将最终调用toString，这就是我们看到“Hi Baeldung！”的原因 .

### 3.2. 将地图作为上下文对象传递

我们刚刚看到了如何为我们的上下文发送一个字符串，现在让我们尝试一个Map：

```java
@Test
public void whenParameterMapIsSupplied_thenDisplays() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("Hi {{name}}!");
    Map<String, String> parameterMap = new HashMap<>();
    parameterMap.put("name", "Baeldung");
    
    String templateString = template.apply(parameterMap);
    
    assertThat(templateString).isEqualTo("Hi Baeldung!");
}
```

与前面的示例类似，我们正在编译我们的模板，然后传递上下文对象，但这次是作为Map传递。

另外，请注意我们使用的是{{name}}而不是{{this}}。这意味着我们的地图必须包含键name。

### 3.3. 将自定义对象作为上下文对象传递

我们还可以将自定义对象传递给我们的模板：

```java
public class Person {
    private String name;
    private boolean busy;
    private Address address = new Address();
    private List<Person> friends = new ArrayList<>();
 
    public static class Address {
        private String street;       
    }
}
```

使用Person类，我们将获得与前面示例相同的结果：

```java
@Test
public void whenParameterObjectIsSupplied_ThenDisplays() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compileInline("Hi {{name}}!");
    Person person = new Person();
    person.setName("Baeldung");
    
    String templateString = template.apply(person);
    
    assertThat(templateString).isEqualTo("Hi Baeldung!");
}
```

我们模板中的{{name}}将深入到我们的Person对象并获取name字段的值。

## 4.模板加载器

到目前为止，我们已经使用了在代码中定义的模板。但是，这不是唯一的选择。我们还可以从文本文件中读取模板。

Handlebars.java 为从类路径、文件系统或 servlet 上下文中读取模板提供特殊支持。默认情况下，Handlebars 扫描类路径以加载给定的模板：

```java
@Test
public void whenNoLoaderIsGiven_ThenSearchesClasspath() throws IOException {
    Handlebars handlebars = new Handlebars();
    Template template = handlebars.compile("greeting");
    Person person = getPerson("Baeldung");
    
    String templateString = template.apply(person);
    
    assertThat(templateString).isEqualTo("Hi Baeldung!");
}
```

因此，因为我们调用了compile而不是compileInline，所以这是对 Handlebars 在类路径上查找/greeting.hbs的提示。

但是，我们也可以使用ClassPathTemplateLoader配置这些属性：

```java
@Test
public void whenClasspathTemplateLoaderIsGiven_ThenSearchesClasspathWithPrefixSuffix() throws IOException {
    TemplateLoader loader = new ClassPathTemplateLoader("/handlebars", ".html");
    Handlebars handlebars = new Handlebars(loader);
    Template template = handlebars.compile("greeting");
    // ... same as before
}
```

在这种情况下，我们告诉Handlebars在 classpath中查找/handlebars/greeting.html。

最后，我们可以链接多个TemplateLoader实例：

```java
@Test
public void whenMultipleLoadersAreGiven_ThenSearchesSequentially() throws IOException {
    TemplateLoader firstLoader = new ClassPathTemplateLoader("/handlebars", ".html");
    TemplateLoader secondLoader = new ClassPathTemplateLoader("/templates", ".html");
    Handlebars handlebars = new Handlebars().with(firstLoader, secondLoader);
    // ... same as before
}
```

所以，在这里，我们有两个加载程序，这意味着 Handlebars 将在两个目录中搜索问候语模板。

## 5.内置助手

内置助手在编写模板时为我们提供了额外的功能。

### 5.1. 有帮手

with帮助程序更改当前上下文：

```html
{{#with address}}
<h4>I live in {{street}}</h4>
{{/with}}
```

在我们的示例模板中，{{#with address}}标记开始该部分，{{/with}}标记结束它。

本质上，我们正在深入了解当前的上下文对象——比方说人——并将地址设置为with部分的本地上下文。此后，此部分中的每个字段引用都将加上person.address。

因此，{{street}}标签将保存person.address.street的值：

```java
@Test
public void whenUsedWith_ThenContextChanges() throws IOException {
    Handlebars handlebars = new Handlebars(templateLoader);
    Template template = handlebars.compile("with");
    Person person = getPerson("Baeldung");
    person.getAddress().setStreet("World");
    
    String templateString = template.apply(person);
    
    assertThat(templateString).contains("<h4>I live in World</h4>");
}
```

我们正在编译我们的模板并将一个Person实例分配为上下文对象。请注意，Person类有一个Address字段。这是我们提供给with助手的字段。

虽然我们进入了上下文对象的一个级别，但如果上下文对象有多个嵌套级别，则可以更深入地进行。

### 5.2. 每个助手

每个助手迭代一个集合：

```html
{{#each friends}}
<span>{{name}} is my friend.</span>
{{/each}}
```

作为使用{{#each friends}}和{{/each}}标签启动和关闭迭代部分的结果，Handlebars 将迭代上下文对象的friends字段。

```java
@Test
public void whenUsedEach_ThenIterates() throws IOException {
    Handlebars handlebars = new Handlebars(templateLoader);
    Template template = handlebars.compile("each");
    Person person = getPerson("Baeldung");
    Person friend1 = getPerson("Java");
    Person friend2 = getPerson("Spring");
    person.getFriends().add(friend1);
    person.getFriends().add(friend2);
    
    String templateString = template.apply(person);
    
    assertThat(templateString)
      .contains("<span>Java is my friend.</span>", "<span>Spring is my friend.</span>");
}
```

在示例中，我们将两个Person实例分配给上下文对象的friends字段。因此，Handlebars 在最终输出中将 HTML 部分重复两次。

### 5.3. 如果帮手

最后，if助手提供条件渲染。

```html
{{#if busy}}
<h4>{{name}} is busy.</h4>
{{else}}
<h4>{{name}} is not busy.</h4>
{{/if}}
```

在我们的模板中，我们根据繁忙的字段提供不同的消息。

```java
@Test
public void whenUsedIf_ThenPutsCondition() throws IOException {
    Handlebars handlebars = new Handlebars(templateLoader);
    Template template = handlebars.compile("if");
    Person person = getPerson("Baeldung");
    person.setBusy(true);
    
    String templateString = template.apply(person);
    
    assertThat(templateString).contains("<h4>Baeldung is busy.</h4>");
}
```

编译模板后，我们设置上下文对象。由于busy字段为true，最终输出变为<h4>Baeldung is busy.</h4>。

## 6.自定义模板助手

我们还可以创建自己的自定义助手。

### 6.1. 帮手

Helper接口使我们能够创建模板助手。

作为第一步，我们必须提供Helper的实现：

```java
new Helper<Person>() {
    @Override
    public Object apply(Person context, Options options) throws IOException {
        String busyString = context.isBusy() ? "busy" : "available";
        return context.getName() + " - " + busyString;
    }
}
```

正如我们所见，Helper接口只有一个方法接受上下文和选项对象。出于我们的目的，我们将输出Person的姓名和忙碌字段。

创建助手后，我们还必须向 Handlebars 注册我们的自定义助手：

```java
@Test
public void whenHelperIsCreated_ThenCanRegister() throws IOException {
    Handlebars handlebars = new Handlebars(templateLoader);
    handlebars.registerHelper("isBusy", new Helper<Person>() {
        @Override
        public Object apply(Person context, Options options) throws IOException {
            String busyString = context.isBusy() ? "busy" : "available";
            return context.getName() + " - " + busyString;
        }
    });
    
    // implementation details
}
```

在我们的示例中，我们使用Handlebars.registerHelper()方法以isBusy的名称注册我们的助手。

作为最后一步，我们必须使用助手的名称在我们的模板中定义一个标签：

```html
{{#isBusy this}}{{/isBusy}}
```

请注意，每个助手都有一个开始和结束标记。

### 6.2. 辅助方法

当我们使用Helper接口时，我们只能创建一个 helper。相反，帮助器源类使我们能够定义多个模板帮助器。

此外，我们不需要实现任何特定的接口。我们只需在一个类中编写辅助方法，然后 HandleBars 使用反射提取辅助定义：

```java
public class HelperSource {

    public String isBusy(Person context) {
        String busyString = context.isBusy() ? "busy" : "available";
        return context.getName() + " - " + busyString;
    }

    // Other helper methods
}
```

由于一个 helper 源可以包含多个 helper 实现，注册不同于单个 helper 注册：

```java
@Test
public void whenHelperSourceIsCreated_ThenCanRegister() throws IOException {
    Handlebars handlebars = new Handlebars(templateLoader);
    handlebars.registerHelpers(new HelperSource());
    
    // Implementation details
}
```

我们正在使用Handlebars.registerHelpers()方法注册我们的助手。此外，辅助方法的名称成为辅助标记的名称。

## 7. 模板重用

Handlebars 库提供了多种方法来重用我们现有的模板。

### 7.1. 模板包含

模板包含是重用模板的方法之一。它有利于模板的组合。

```html
<h4>Hi {{name}}!</h4>
```

这是标题模板的内容——header.html。

为了在另一个模板中使用它，我们必须引用标题模板。

```html
{{>header}}
<p>This is the page {{name}}</p>
```

我们有页面模板- page.html -其中包括使用{{>header}}的页眉模板。

Handlebars.java 处理模板时，最终的输出也会包含header的内容：

```java
@Test
public void whenOtherTemplateIsReferenced_ThenCanReuse() throws IOException {
    Handlebars handlebars = new Handlebars(templateLoader);
    Template template = handlebars.compile("page");
    Person person = new Person();
    person.setName("Baeldung");
    
    String templateString = template.apply(person);
    
    assertThat(templateString)
      .contains("<h4>Hi Baeldung!</h4>", "<p>This is the page Baeldung</p>");
}
```

### 7.2. 模板继承

作为组合的替代方案，Handlebars 提供了模板继承。

我们可以使用{{#block}}和{{#partial}}标签实现继承关系：

```html
<html>
<body>
{{#block "intro"}}
  This is the intro
{{/block}}
{{#block "message"}}
{{/block}}
</body>
</html>
```

通过这样做，消息库模板有两个块——介绍和消息。

要应用继承，我们需要使用{{#partial}}覆盖其他模板中的这些块：

```html
{{#partial "message" }}
  Hi there!
{{/partial}}
{{> messagebase}}
```

这是简单的消息模板。请注意，我们包含了消息库模板并覆盖了消息块。

## 8.总结

在本教程中，我们查看了 Handlebars.java 以创建和管理模板。

我们从基本的标签使用开始，然后研究了加载 Handlebars 模板的不同选项。

我们还研究了提供大量功能的模板助手。最后，我们研究了重用模板的不同方法。