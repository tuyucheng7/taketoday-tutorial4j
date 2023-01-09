## 1. 概述

[Velocity](https://velocity.apache.org/)是一个基于Java的模板引擎。

它是一个开源 Web 框架，旨在用作 MVC 架构中的视图组件，它提供了一些现有技术(如 JSP)的替代方案。

Velocity 可用于生成 XML 文件、SQL、PostScript 和大多数其他基于文本的格式。

在本文中，我们将探讨如何使用它来创建动态网页。

## 2. 速度如何运作

Velocity 的核心类是VelocityEngine。

它使用数据模型和速度模板协调读取、解析和生成内容的整个过程。

简而言之，对于任何典型的速度应用，我们需要遵循以下步骤：

-   初始化速度引擎
-   阅读模板
-   将数据模型放在上下文对象中
-   将模板与上下文数据合并并呈现视图

让我们按照这些简单的步骤来看一个例子：

```java
VelocityEngine velocityEngine = new VelocityEngine();
velocityEngine.init();
   
Template t = velocityEngine.getTemplate("index.vm");
    
VelocityContext context = new VelocityContext();
context.put("name", "World");
    
StringWriter writer = new StringWriter();
t.merge( context, writer );
```

## 3.Maven依赖

要使用 Velocity，我们需要将以下依赖项添加到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>org.apache.velocity</groupId>
    <artifactId>velocity</artifactId>
    <version>1.7</version>
    </dependency>
<dependency>
     <groupId>org.apache.velocity</groupId>
     <artifactId>velocity-tools</artifactId>
     <version>2.0</version>
</dependency>
```

这两个依赖项的最新版本可以在这里：[velocity](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.velocity" AND a%3A"velocity")和[velocity-tools](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.velocity" AND a%3A"velocity-tools")。

## 4.速度模板语言

Velocity Template Language (VTL) 通过使用 VTL 引用提供了将动态内容合并到网页中的最简单和最干净的方法。

Velocity 模板中的 VTL 引用以$开头，用于获取与该引用关联的值。VTL 还提供了一组指令，可用于操作Java代码的输出。这些指令以# 开头。

### 4.1. 参考

Velocity中的引用分为三种，变量、属性和方法：

-   变量——使用

    

    #set

    指令或从Java对象字段返回的值 在页面中定义：

    ```plaintext
    #set ($message="Hello World")
    ```

-   属性

    ——引用对象中的字段；他们还可以引用属性的

    getter

    方法：

    ```plaintext
    $customer.name
    ```

-   方法

    ——指的是Java对象上的方法：

    ```plaintext
    $customer.getName()
    ```

每个引用产生的最终值在呈现为最终输出时都会转换为字符串。

### 4.2. 指令

VTL 提供了一组丰富的指令：

-   set

     – 可用于设置引用的值；该值可以分配给变量或属性引用：

    ```plaintext
    #set ($message = "Hello World")
    #set ($customer.name = "Brian Mcdonald")
    ```

-   条件

    —— 

    #if、#elseif

    和

    #else

    指令提供了一种根据条件检查生成内容的方法：

    ```html
    #if($employee.designation == "Manager")
        <h3> Manager </h3>
    #elseif($employee.designation == "Senior Developer")
        <h3> Senior Software Engineer </h3>
    #else
        <h3> Trainee </h3>
    #end
    ```

-   循环

    —— 

    #foreach

    指令允许循环对象集合：

    ```html
    <ul>
        #foreach($product in $productList)
            <li> $product </li>
        #end
    </ul>
    ```

-   include

     – 

    #include

    元素提供了将文件导入模板的能力：

    ```plaintext
    #include("one.gif","two.txt","three.html"...)
    ```

-   parse

     – 

    #parse

    语句允许模板设计者导入另一个包含 VTL 的本地文件；然后 Velocity 将解析内容并呈现它：

    ```plaintext
    #parse (Template)
    ```

-   evaluate

     – 

    #evaluate

    指令可用于动态评估 VTL；这允许模板在呈现时评估

    字符串

    ，例如使模板国际化：

    ```plaintext
    #set($firstName = "David")
    #set($lastName = "Johnson")
    
    #set($dynamicsource = "$firstName$lastName")
    
    #evaluate($dynamicsource)
    ```

-   break – #break指令停止当前执行范围的任何进一步呈现(即#foreach、#parse)

-   stop – #stop指令停止模板的任何进一步渲染和执行。

-   velocimacros

     – 

    #macro

    指令允许模板设计者定义重复的 VTL 段：

    ```html
    #macro(tablerows)
        <tr>
            <td>
            </td>
        </tr>
    #end
    ```

    这个宏现在可以放在模板中的任何地方，如#tablerows()：

    ```html
    #macro(tablerows $color $productList)
        #foreach($product in $productList)
            <tr>
                <td bgcolor=$color>$product.name</td>
            </tr>
        #end
    #end
    ```

### 4.3. 其它功能

-   数学

    ——一些内置的数学函数，可以在模板中使用：

    ```plaintext
    #set($percent = $number / 100)
    #set($remainder = $dividend % $divisor)
    ```

-   范围运算符

    ——可以与

    #set

    和

    #foreach 结合使用：

    ```plaintext
    #set($array = [0..10])
    
    #foreach($elem in $arr)
        $elem
    #end
    ```

## 5. 速度小服务程序

Velocity Engine 的主要工作是根据模板生成内容。

该引擎本身不包含任何与网络相关的功能。要实现 Web 应用程序，我们需要使用 servlet 或基于 servlet 的框架。

Velocity 提供了一个开箱即用的实现VelocityViewServlet，它是 velocity-tools 子项目的一部分。

要使用 VelocityViewServlet 提供的内置功能，我们可以从VelocityViewServlet扩展我们的 servlet并覆盖handleRequest()方法：

```java
public class ProductServlet extends VelocityViewServlet {

    ProductService service = new ProductService();

    @Override
    public Template handleRequest(
      HttpServletRequest request, 
      HttpServletResponse response,
      Context context) throws Exception {
      
        List<Product> products = service.getProducts();
        context.put("products", products);

        return getTemplate("index.vm");
    }
}
```

## 六、配置

### 6.1. 网页配置

现在让我们看看如何在web.xml中配置VelocityViewServlet。

我们需要指定可选的初始化参数，包括velocity.properties和toolbox.xml：

```xml
<web-app>
    <display-name>apache-velocity</display-name>
      //...
       
    <servlet>
        <servlet-name>velocity</servlet-name>
        <servlet-class>org.apache.velocity.tools.view.VelocityViewServlet</servlet-class>

        <init-param>
            <param-name>org.apache.velocity.properties</param-name>
            <param-value>/WEB-INF/velocity.properties</param-value>
        </init-param>
    </servlet>
        //...
</web-app>

```

我们还需要指定此 servlet 的映射。所有对速度模板 ( .vm ) 的请求都需要由速度 servlet 提供服务：

```xml
<servlet-mapping>
    <servlet-name>velocityLayout</servlet-name>
    <url-pattern>.vm</url-pattern>
</servlet-mapping>
```

### 6.2. 资源加载器

Velocity 提供灵活的资源加载系统。它允许一个或多个资源加载器同时运行：

-   文件资源加载器
-   JarResourceLoader
-   类路径资源加载器
-   URL资源加载器
-   数据源资源加载器
-   WebappResourceLoader

这些资源加载器在velocity.properties 中配置：

```plaintext
resource.loader=webapp
webapp.resource.loader.class=org.apache.velocity.tools.view.WebappResourceLoader
webapp.resource.loader.path = 
webapp.resource.loader.cache = true
```

## 7.速度模板

Velocity 模板是编写所有视图生成逻辑的地方。这些页面是使用 Velocity 模板语言 (VTL) 编写的：

```html
<html>
    ...
    <body>
        <center>
        ...
        <h2>$products.size() Products on Sale!</h2>
        <br/>
            We are proud to offer these fine products
            at these amazing prices.
        ...
        #set( $count = 1 )
        <table class="gridtable">
            <tr>
                <th>Serial #</th>
                <th>Product Name</th>
                <th>Price</th>
            </tr>
            #foreach( $product in $products )
            <tr>
                <td>$count)</td>
                <td>$product.getName()</td>
                <td>$product.getPrice()</td>
            </tr>
            #set( $count = $count + 1 )
            #end
        </table>
        <br/>
        </center>
    </body>
</html>
```

## 8.管理页面布局

Velocity 为基于 Velocity Tool 的应用程序提供了简单的布局控制和可定制的错误屏幕。

VelocityLayoutServlet封装了此功能以呈现指定的布局。VelocityLayoutServlet是 VelocityViewServlet 的扩展。

### 8.1. 网页配置

让我们看看如何配置VelocityLayoutServlet。servlet 被定义为拦截速度模板页面的请求，布局特定属性在velocity.properties文件中定义：

```xml
<web-app>
    // ...
    <servlet>
        <servlet-name>velocityLayout</servlet-name>
        <servlet-class>org.apache.velocity.tools.view.VelocityLayoutServlet</servlet-class>

        <init-param>
            <param-name>org.apache.velocity.properties</param-name>
            <param-value>/WEB-INF/velocity.properties</param-value>
        </init-param>
    </servlet>
    // ...
    <servlet-mapping>
        <servlet-name>velocityLayout</servlet-name>
        <url-pattern>.vm</url-pattern>
    </servlet-mapping>
    // ...
</web-app>
```

### 8.2. 布局模板

布局模板定义了速度页面的典型结构。默认情况下，VelocityLayoutServlet在布局文件夹下搜索Default.vm 。覆盖几个属性可以更改此位置：

```plaintext
tools.view.servlet.layout.directory = layout/
tools.view.servlet.layout.default.template = Default.vm

```

布局文件由页眉模板、页脚模板和速度变量$screen_content 组成，它呈现请求速度页面的内容：

```html
<html>
    <head>
        <title>Velocity</title>
    </head>
    <body>
        <div>
            #parse("/fragments/header.vm")
        </div>
        <div>
            <!-- View index.vm is inserted here -->
            $screen_content
        </div>
        <div>
            #parse("/fragments/footer.vm")
        </div>
    </body>
</html>
```

### 8.3. 请求屏幕中的布局规范

特定屏幕的布局可以定义为页面开头的速度变量。这是通过将这一行放在页面中来完成的：

```plaintext
#set($layout = "MyOtherLayout.vm")
```

### 8.4. 请求参数中的布局规范

我们可以在查询字符串layout=MyOtherLayout.vm中添加请求参数，VLS 将找到它并在该布局内渲染屏幕，而不是搜索默认布局。

### 8.5. 错误屏幕

可以使用速度布局来实现自定义错误屏幕。VelocityLayoutServlet提供了两个变量$error_cause和$stack_trace来呈现异常细节。

错误页面可以在velocity.properties文件中配置：

```plaintext
tools.view.servlet.error.template = Error.vm
```

## 9.总结

在本文中，我们了解了 Velocity 如何成为呈现动态网页的有用工具。此外，我们还看到了使用速度提供的 servlet 的不同方式。

[我们在 Baeldung](https://www.baeldung.com/spring-mvc-with-velocity)也有一篇文章重点介绍了使用 Spring MVC 进行 Velocity 配置的文章。