## 1. 简介

表达式语言 (EL) 是一种脚本语言，已在许多Java框架中得到采用，例如带有[SpEL](https://www.baeldung.com/spring-expression-language)的 Spring和带有 JBoss EL 的 JBoss。

在本文中，我们将重点关注 JSF 对该脚本语言的实现——Unified EL。

EL 目前处于 3.0 版本，这是一个重大升级，允许处理引擎以独立模式使用——例如，在JavaSE 平台上。以前的版本依赖于 Jakarta EE 兼容的应用程序服务器或 Web 容器。本文讨论 EL 2.2 版。

## 2. 立即和延期评估

JSF 中 EL 的主要功能是连接 JSF 视图(通常是 XHTML 标记)和基于 java 的后端。后端可以是用户创建的托管 bean，或容器管理的对象，如 HTTP 会话。

我们将关注 EL 2.2。JSF 中的 EL 有两种一般形式，立即语法 EL 和延迟语法 EL。

### 2.1. 立即语法 EL

也称为 JSP EL，这是一种脚本格式，是JavaWeb 应用程序开发的 JSP 时代的遗留物。

JSP EL 表达式以美元符号 ( $ ) 开头，然后是左大括号 ( { )，然后是实际表达式，最后以右大括号 ( } ) 结束：

```xml
${ELBean.value > 0}
```

这个语法：

1.  在页面的生命周期中仅评估一次(在开始时)。这意味着它的价值。被上面例子中的表达式读取必须在页面加载之前设置。
2.  提供对 bean 值的只读访问。
3.  因此，需要遵守 JavaBean 命名约定。

对于大多数用途，这种形式的 EL 不是很通用。

### 2.2. 延迟执行 EL

延迟执行 EL 是专为 JSF 设计的 EL。它与 JSP EL 在语法上的主要区别是它用“ #”而不是“ $ ”标记。

```xml
#{ELBean.value > 0}
```

延期他：

1.  与 JSF 生命周期同步。这意味着延迟 EL 中的 EL 表达式在 JSF 页面呈现的不同点(开始和结束)进行评估。
2.  提供对 bean 值的读写访问。这允许使用 EL 在 JSF backing-bean(或其他任何地方)中设置一个值。
3.  允许程序员在对象上调用任意方法，并根据 EL 的版本将参数传递给此类方法。

统一 EL 是统一延迟 EL 和 JSP EL 的规范，允许在同一页面中使用这两种语法。

## 3.统一EL

Unified EL 允许两种通用的表达式，值表达式和方法表达式。

快速说明 - 以下部分将展示一些示例，这些示例都可以在应用程序中找到(请参阅末尾的 Github 链接)，方法是导航至：

```bash
http://localhost:8080/jsf/el_intro.jsf
```

### 3.1. 值表达式

值表达式允许我们读取或设置托管 bean 属性，具体取决于它的放置位置。

以下表达式将托管 bean 属性读取到页面上：

```xml
Hello, #{ELBean.firstName}
```

但是，以下表达式允许我们在用户对象上设置一个值：

```xml
<h:inputText id="firstName" value="#{ELBean.firstName}" required="true"/>
```

变量必须遵循 JavaBean 命名约定才有资格进行这种处理。对于要提交的 bean 的值，只需要保存封闭的表单。

### 3.2. 方法表达式

统一 EL 提供了方法表达式来从 JSF 页面中执行公共的、非静态的方法。这些方法可能有也可能没有返回值。

这是一个简单的例子：

```xml
<h:commandButton value="Save" action="#{ELBean.save}"/>
```

所引用的save()方法是在名为ELBean 的支持 bean 上定义的。

从 EL 2.2 开始，还可以将参数传递给使用 EL 访问的方法。这可以让我们重写我们的例子：

```xml
<h:inputText id="firstName" binding="#{firstName}" required="true"/>
<h:commandButton value="Save"
  action="#{ELBean.saveFirstName(firstName.value.toString().concat('(passed)'))}"/>
```

我们在这里所做的是为inputText组件创建一个页面范围的绑定表达式，并将value属性直接传递给方法表达式。

请注意，变量在没有任何特殊符号、大括号或转义字符的情况下传递给方法。

### 3.3. 隐式 EL 对象

JSF EL 引擎提供对多个容器管理对象的访问。他们之中有一些是：

-   #{Application}：也可用作#{servletContext}，这是表示 Web 应用程序实例的对象
-   #{applicationScope}：可在 Web 应用程序范围内访问的变量映射
-   #{Cookie}：HTTP Cookie 变量的映射
-   #{facesContext} : FacesContext的当前实例
-   #{flash}：JSF Flash 作用域对象
-   #{header} : 当前请求中 HTTP 标头的映射
-   #{initParam} : Web 应用程序上下文初始化变量的映射
-   #{param} : HTTP 请求查询参数的映射
-   #{request} : HTTPServletRequest对象
-   #{requestScope}：请求范围的变量映射
-   #{sessionScope}：会话范围的变量映射
-   #{session} : HTTPSession对象
-   #{viewScope}：变量的视图(页面)范围映射

以下简单示例通过访问标头隐式对象列出所有请求标头和值：

```xml
<c:forEach items="#{header}" var="header">
   <tr>
       <td>#{header.key}</td>
       <td>#{header.value}</td>
   </tr>
</c:forEach>
```

## 4. 你可以在 EL 中做什么

由于其多功能性，EL 可以出现在Java代码、XHTML 标记、Javascript 中，甚至出现在 JSF 配置文件(如faces-config.xml文件)中。让我们检查一些具体的用例。

### 4.1. 在页面标记中使用 EL

EL 可以包含在标准 HTML 标签中：

```xml
<meta name="description" content="#{ELBean.pageDescription}"/>
```

### 4.2. 在 JavaScript 中使用 EL

在 Javascript 或 <script> 标签中遇到 EL 将被解释：

```javascript
<script type="text/javascript"> var theVar = #{ELBean.firstName};</script>
```

支持 bean 变量将在此处设置为 javascript 变量。

### 4.3. 使用运算符评估 EL 中的布尔逻辑

EL 支持相当高级的比较运算符：

-   eq相等运算符，相当于“ ==”。
-   lt小于运算符，等同于“<”。
-   le小于或等于运算符，相当于“<=”。
-   gt大于运算符，相当于“>”。
-   ge大于等于，相当于“ >=。“

### 4.4. 在支持 Bean 中评估 EL

在支持 bean 代码中，可以使用 JSF 应用程序评估 EL 表达式。这打开了一个充满可能性的世界，将 JSF 页面与支持 bean 连接起来。可以检索隐式 EL 对象，或从辅助 bean 轻松检索实际的 HTML 页面组件或它们的值：

```java
FacesContext ctx = FacesContext.getCurrentInstance(); 
Application app = ctx.getApplication(); 
String firstName = app.evaluateExpressionGet(ctx, "#{firstName.value}", String.class); 
HtmlInputText firstNameTextBox = app.evaluateExpressionGet(ctx, "#{firstName}", HtmlInputText.class);
```

这允许开发人员在与 JSF 页面交互时具有很大的灵活性。

## 5. 在 EL 中你不能做什么

EL < 3.0 确实有一些限制。以下各节讨论其中的一些。

### 5.1. 无超载

EL 不支持使用重载。因此，在具有以下方法的支持 bean 中：

```java
public void save(User theUser);
public void save(String username);
public void save(Integer uid);
```

JSF EL 将无法正确评估以下表达式

```xml
<h:commandButton value="Save" action="#{ELBean.save(firstName.value)}"/>
```

JSF ELResolver将自省bean的类定义，并选择java.lang.Class#getMethods返回的第一个方法(返回类中可用方法的方法)。无法保证返回方法的顺序，这将不可避免地导致未定义的行为。

### 5.2. 没有枚举或常量值

JSF EL < 3.0，不支持在脚本中使用常量值或枚举。所以，有以下任何一项

```java
public static final String USER_ERROR_MESS = "No, you can’t do that";
enum Days { Sat, Sun, Mon, Tue, Wed, Thu, Fri };
```

意味着将无法执行以下操作

```xml
<h:outputText id="message" value="#{ELBean.USER_ERROR_MESS}"/>
<h:commandButton id="saveButton" value="save" rendered="bean.offDay==Days.Sun"/>
```

### 5.3. 没有内置的空安全

JSF EL < v3.0 不提供隐式空安全访问，有些人可能会觉得现代脚本引擎很奇怪。

因此，如果下面表达式中的person为空，则整个表达式将失败并出现难看的 NPE

```xml
Hello Mr, #{ELBean.person.surname}"
```

## 六. 总结

我们已经研究了 JSF EL 的一些基础知识、优势和局限性。

这在很大程度上是一种通用的脚本语言，有一定的改进空间；它还是将 JSF 视图绑定到 JSF 模型和控制器的粘合剂。