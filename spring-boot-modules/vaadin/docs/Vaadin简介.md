## 1. 概述

Vaadin 是用于创建 Web 用户界面的服务器端Java框架。使用它，我们可以使用Java功能创建我们的前端。

## 2. Maven依赖和设置

让我们首先将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-server</artifactId>
</dependency>
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-client-compiled</artifactId>
</dependency>
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-themes</artifactId>
</dependency>

```

可以在此处找到最新版本的依赖项：[vaadin-server](https://search.maven.org/classic/#search|gav|1|g%3A"com.vaadin" AND a%3A"vaadin-server")、[vaadin-client-compiled](https://search.maven.org/classic/#search|gav|1|g%3A"com.vaadin" AND a%3A"vaadin-client-compiled")、[vaadin-themes](https://search.maven.org/classic/#search|gav|1|g%3A"com.vaadin" AND a%3A"vaadin-themes")。

-   vaadin-server包——包括用于处理所有服务器详细信息(如会话、客户端通信等)的类。
-   vaadin-client-compiled – 基于 GWT 并包含编译客户端所需的包
-   vaadin-themes – 包括一些预制主题和所有用于制作我们主题的实用程序

要编译我们的 Vaadin 小部件，我们需要配置[maven-war-plugin](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-war-plugin")、[vaadin-maven-plugin](https://search.maven.org/classic/#search|gav|1|g%3A"com.vaadin" AND a%3A"vaadin-maven-plugin")和[maven-clean-plugin](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-clean-plugin")。对于完整的 pom，请务必检查源代码中的 pom 文件——在本教程的最后。

此外，我们还需要添加 Vaadin 存储库和依赖管理：

```xml
<repositories>
    <repository>
        <id>vaadin-addons</id>
        <url>http://maven.vaadin.com/vaadin-addons</url>
    </repository>
</repositories>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.vaadin</groupId>
            <artifactId>vaadin-bom</artifactId>
            <version>13.0.9</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

DependencyManagement标签控制所有 Vaadin依赖项的版本。

为了快速运行应用程序，我们将使用 Jetty 插件：

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>9.3.9.v20160517</version>
    <configuration>
        <scanIntervalSeconds>2</scanIntervalSeconds>
        <skipTests>true</skipTests>
    </configuration>
</plugin>
```

最新版本的插件可以在这里找到：[jetty-maven-plugin](https://search.maven.org/classic/#search|gav|1|g%3A"org.eclipse.jetty" AND a%3A"jetty-maven-plugin")。

使用此插件，我们可以使用以下命令运行我们的项目：

```bash
mvn jetty:run
```

## 3. 什么是Vaadin？

简单地说，Vaadin 是一个用于创建用户界面的Java框架，具有主题和组件，以及许多可扩展性选项。

该框架也覆盖了服务器端，这意味着对用户界面所做的每一次更改都会立即发送到服务器——因此后端应用程序每时每刻都知道前端发生了什么。

Vaadin 由客户端和服务器端组成——客户端构建在著名的 Google Widget Toolkit 框架之上，服务器端由 VaadinServlet处理。

## 4. 小服务程序

通常，Vaadin 应用程序不使用web.xml文件；相反，它使用注解定义其servlet ：

```java
@WebServlet(urlPatterns = "/VAADIN/", name = "MyUIServlet", asyncSupported = true)
@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
public static class MyUIServlet extends VaadinServlet {}
```

在这种情况下，此 servlet 提供来自/VAADIN路径的内容。

## 5.主类

servlet 中引用的VaadinUI类必须从框架扩展 UI 类，并且必须重写init方法以完成启用 Vaadin 的应用程序的引导。

下一步是创建布局并将其添加到应用程序的主布局中：

```java
public class VaadinUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        verticalLayout.setMargin(true);
        setContent(verticalLayout);
}
```

## 6. Vaadin 布局管理器

该框架带有许多预定义的布局管理器。

### 6.1. 垂直布局

将组件堆叠在一列中，其中第一个添加的在顶部，最新的在底部：

```java
VerticalLayout verticalLayout = new VerticalLayout();
verticalLayout.setSpacing(true);
verticalLayout.setMargin(true);
setContent(verticalLayout);
```

注意这里的属性是如何松散地从典型的 CSS 术语中借用的。

### 6.2. 水平布局

这种布局将每个组件从左到右并排放置，类似于垂直布局：

```java
HorizontalLayout horizontalLayout = new HorizontalLayout();
```

### 6.3. 网格布局

此布局将每个小部件放在网格中，需要将网格的列和行作为参数传递：

```java
GridLayout gridLayout = new GridLayout(3, 2);
```

### 6.4. 表单布局

表单布局将标题和组件放在两个不同的列中，并且可以为必填字段提供可选指示符：

```java
FormLayout formLayout = new FormLayout();
```

## 7.Vaadin 组件

现在已经处理了布局，让我们看一下用于构建用户界面的一些更常见的组件。

### 7.1. 标签 

[![标签](https://www.baeldung.com/wp-content/uploads/2017/07/label.png)](https://www.baeldung.com/wp-content/uploads/2017/07/label.png)

当然，这个标签也是众所周知的——只是用来显示文本：

```java
Label label = new Label();
label.setId("LabelID");
label.setValue("Label Value");
label.setCaption("Label");
gridLayout.addComponent(label);
```

创建组件后，请注意将其添加到布局的关键步骤。

### 7.2. 关联 

[![关联](https://www.baeldung.com/wp-content/uploads/2017/07/link.png)](https://www.baeldung.com/wp-content/uploads/2017/07/link.png)

链接小部件本质上是一个基本的超链接：

```java
Link link = new Link("Baeldung",
  new ExternalResource("http://www.baeldung.com/"));
link.setTargetName("_blank");
```

请注意<a>元素的典型 HTML 值是如何全部显示在这里的。

### 7.3. 文本域 

[![文本域](https://www.baeldung.com/wp-content/uploads/2017/07/textfield.png)](https://www.baeldung.com/wp-content/uploads/2017/07/textfield.png)

此小部件用于输入文本：

```java
TextField textField = new TextField();
textField.setIcon(VaadinIcons.USER);
```

我们可以进一步自定义元素；例如，我们可以通过setIcon() API 快速将图像添加到小部件。

另外，请注意Font Awesome 是开箱即用的框架；它被定义为一个枚举，我们可以很容易地使用它。

### 7.4. 文本区 

[![文本区域](https://www.baeldung.com/wp-content/uploads/2017/07/textarea.png)](https://www.baeldung.com/wp-content/uploads/2017/07/textarea.png)

如所料，TextArea在其余传统 HTML 元素旁边可用：

```java
TextArea textArea = new TextArea();
```

### 7.5. 日期字段和内联日期字段 

[![日期字段](https://www.baeldung.com/wp-content/uploads/2017/07/datefield.png)](https://www.baeldung.com/wp-content/uploads/2017/07/datefield.png)

这个强大的组件用于选择日期；date 参数是要在小部件中选择的当前日期：

```java
DateField dateField = new DateField("DateField", LocalDate.ofEpochDay(0));
```

[![内联日期字段](https://www.baeldung.com/wp-content/uploads/2017/07/inlinedatefield.png)](https://www.baeldung.com/wp-content/uploads/2017/07/inlinedatefield.png)

我们可以更进一步，将它嵌套在组合框控件中以节省空间：

```java
InlineDateField inlineDateField = new InlineDateField();
```

### 7.6. 密码字段 

[![密码字段](https://www.baeldung.com/wp-content/uploads/2017/07/passwordfield.png)](https://www.baeldung.com/wp-content/uploads/2017/07/passwordfield.png)

这是标准的屏蔽密码输入：

```java
PasswordField passwordField = new PasswordField();
```

### 7.7. 富文本区 

[![富文本区域](https://www.baeldung.com/wp-content/uploads/2017/07/richtextarea.png)](https://www.baeldung.com/wp-content/uploads/2017/07/richtextarea.png)

有了这个组件，我们可以显示格式化的文本，它提供了一个界面来使用按钮来操作这些文本，以控制字体、大小、对齐等。是：

```java
RichTextArea richTextArea = new RichTextArea();
richTextArea.setCaption("Rich Text Area");
richTextArea.setValue("<h1>RichTextArea</h1>");
richTextArea.setSizeFull();
Panel richTextPanel = new Panel();
richTextPanel.setContent(richTextArea);
```

### 7.8. 按钮 

[![纽扣](https://www.baeldung.com/wp-content/uploads/2017/07/buttons.png)](https://www.baeldung.com/wp-content/uploads/2017/07/buttons.png)

按钮用于捕获用户输入并有多种尺寸和颜色。

要创建一个按钮，我们像往常一样实例化小部件类：

```java
Button normalButton = new Button("Normal Button");
```

改变样式我们可以有一些不同的按钮：

```java
tinyButton.addStyleName("tiny");
smallButton.addStyleName("small");
largeButton.addStyleName("large");
hugeButton.addStyleName("huge");
dangerButton.addStyleName("danger");
friendlyButton.addStyleName("friendly");
primaryButton.addStyleName("primary");
borderlessButton.addStyleName("borderless");
linkButton.addStyleName("link");
quietButton.addStyleName("quiet");
```

我们可以创建一个禁用按钮：

```java
Button disabledButton = new Button("Disabled Button");
disabledButton.setDescription("This button cannot be clicked");
disabledButton.setEnabled(false);
buttonLayout.addComponent(disabledButton);
```

使用浏览器外观的本机按钮：

```java
NativeButton nativeButton = new NativeButton("Native Button");
buttonLayout.addComponent(nativeButton);
```

还有一个带有图标的按钮：

```java
Button iconButton = new Button("Icon Button");
iconButton.setIcon(VaadinIcons.ALIGN_LEFT);
buttonLayout.addComponent(iconButton);
```

### 7.9. 复选框 

[![复选框](https://www.baeldung.com/wp-content/uploads/2017/07/checkbox.png)](https://www.baeldung.com/wp-content/uploads/2017/07/checkbox.png)

复选框是一个改变状态的元素，被选中或未被选中：

```java
CheckBox checkbox = new CheckBox("CheckBox");        
checkbox.setValue(true);
checkbox.addValueChangeListener(e ->
  checkbox.setValue(!checkbox.getValue()));
formLayout.addComponent(checkbox);
```

### 7.10. 列表 

Vaadin 有一些有用的小部件来处理列表。

首先，我们创建一个要放置在小部件中的项目列表：

```java
List<String> numbers = new ArrayList<>();
numbers.add("One");
numbers.add("Ten");
numbers.add("Eleven");
```

ComboBox是一个下拉列表：

[![组合框](https://www.baeldung.com/wp-content/uploads/2017/07/combobox.png)](https://www.baeldung.com/wp-content/uploads/2017/07/combobox.png)

```java
ComboBox comboBox = new ComboBox("ComboBox");
comboBox.addItems(numbers);
formLayout.addComponent(comboBox);
```

ListSelect垂直放置项目并在溢出时使用滚动条：

[![列表选择](https://www.baeldung.com/wp-content/uploads/2017/07/listselect.png)](https://www.baeldung.com/wp-content/uploads/2017/07/listselect.png)

```java
ListSelect listSelect = new ListSelect("ListSelect");
listSelect.addItems(numbers);
listSelect.setRows(2);
formLayout.addComponent(listSelect);
```

NativeSelect类似于ComboBox，但具有浏览器外观：

[![本机选择](https://www.baeldung.com/wp-content/uploads/2017/07/nativeselect.png)](https://www.baeldung.com/wp-content/uploads/2017/07/nativeselect.png)

```java
NativeSelect nativeSelect = new NativeSelect("NativeSelect");
nativeSelect.addItems(numbers);
formLayout.addComponent(nativeSelect);
```

TwinColSelect是一个双列表，我们可以在其中更改这两个窗格之间的项目；每个项目一次只能存在于一个窗格中：

[![双选](https://www.baeldung.com/wp-content/uploads/2017/07/twincolselect.png)](https://www.baeldung.com/wp-content/uploads/2017/07/twincolselect.png)

```java
TwinColSelect twinColSelect = new TwinColSelect("TwinColSelect");
twinColSelect.addItems(numbers);
```

### 7.11. 网格 

网格用于以矩形方式显示数据；有行和列，可以为数据定义页眉和页脚：

[![网格](https://www.baeldung.com/wp-content/uploads/2017/07/grid.png)](https://www.baeldung.com/wp-content/uploads/2017/07/grid.png)

```java
Grid<Row> grid = new Grid(Row.class);
grid.setColumns("column1", "column2", "column3");
Row row1 = new Row("Item1", "Item2", "Item3");
Row row2 = new Row("Item4", "Item5", "Item6");
List<Row> rows = new ArrayList();
rows.add(row1);
rows.add(row2);
grid.setItems(rows);
```

上面的Row类是我们添加的一个简单的 POJO 来表示一行：

```java
public class Row {
    private String column1;
    private String column2;
    private String column3;

    // constructors, getters, setters
}
```

## 8.服务器推送

另一个有趣的特性是能够将消息从服务器发送到 UI。

要使用服务器推送，我们需要将以下依赖项添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>com.vaadin</groupId>
    <artifactId>vaadin-push</artifactId>
    <versionId>8.8.5</versionId>
</dependency>
```

可以在此处找到最新版本的依赖项：[vaadin-push](https://search.maven.org/classic/#search|gav|1|g%3A"com.vaadin" AND a%3A"vaadin-push")。

此外，我们需要将@Push注解添加到表示 UI 的类中：

```java
@Push
@Theme("mytheme")
public class VaadinUI extends UI {...}
```

我们创建一个标签来捕获服务器推送消息：

```java
private Label currentTime;
```

然后我们创建一个ScheduledExecutorService将时间从服务器发送到标签：

```java
ScheduledExecutorService scheduleExecutor = Executors.newScheduledThreadPool(1);
Runnable task = () -> {
    currentTime.setValue("Current Time : " + Instant.now());
};
scheduleExecutor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
```

ScheduledExecutorService在应用程序的服务器端运行，每次运行时，用户界面都会更新。

## 9.数据绑定

我们可以将我们的用户界面绑定到我们的业务类。

首先，我们创建一个Java类：

```java
public class BindData {

    private String bindName;

    public BindData(String bindName){
        this.bindName = bindName;
    }
    
    // getter & setter
}
```

然后我们将具有单个字段的类绑定到用户界面中的TextField ：

```java
Binder<BindData> binder = new Binder<>();
BindData bindData = new BindData("BindData");
binder.readBean(bindData);
TextField bindedTextField = new TextField();
binder.forField(bindedTextField).bind(BindData::getBindName, BindData::setBindName);
```

首先，我们使用之前创建的类创建一个BindData对象，然后Binder将字段绑定到TextField。

## 10.验证者

我们可以创建验证器来验证输入字段中的数据。为此，我们将验证器附加到我们要验证的字段：

```java
BindData stringValidatorBindData = new BindData("");
TextField stringValidator = new TextField();
Binder<BindData> stringValidatorBinder = new Binder<>();
stringValidatorBinder.setBean(stringValidatorBindData);
stringValidatorBinder.forField(stringValidator)
  .withValidator(new StringLengthValidator("String must have 2-5 characters lenght", 2, 5))
  .bind(BindData::getBindName, BindData::setBindName);
```

然后我们在使用之前验证我们的数据：

```java
Button buttonStringValidator = new Button("Validate String");
buttonStringValidator.addClickListener(e -> stringValidatorBinder.validate());
```

在这种情况下，我们使用StringLengthValidator来验证字符串的长度，但 Vaadin 提供了其他有用的验证器，还允许我们创建自定义验证器。

## 11.总结

当然，这篇快速的文章仅仅触及了表面；该框架不仅仅是用户界面小部件，Vaadin 提供了使用Java创建现代 Web 应用程序所需的一切。