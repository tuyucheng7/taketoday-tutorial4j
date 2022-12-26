## 1. 概述

在本教程中，我们将了解什么是同构应用程序，并讨论[Nashorn](https://www.oracle.com/technetwork/articles/java/jf14-nashorn-2126515.html)，它是与Java捆绑在一起的JavaScript引擎。

此外，我们将探讨如何将Nashorn与[React](https://reactjs.org/)等前端库一起使用来创建同构应用程序。

## 2. 一点点历史

传统上，客户端和服务器应用程序的编写方式在服务器端非常繁重，将PHP视为一个脚本引擎，主要生成静态HTML和呈现它们的Web浏览器。

Netscape早在**90年代中期就在其浏览器中支持JavaScript**，这开始将一些处理从服务器端转移到客户端浏览器。很长一段时间以来，开发人员一直在努力解决有关Web浏览器中JavaScript支持的不同问题。

随着对更快和交互式用户体验的需求不断增长，这个界限已经被推得越来越高。**最早改变游戏规则的框架之一是**[jQuery](https://jquery.com/)，它带来了几个用户友好的功能和对AJAX的大大增强的支持。

很快，许多用于前端开发的框架开始出现，这极大地改善了开发人员的体验。从Google的[AngularJS](https://angular.io/)、Facebook的React到后来的[Vue](https://vuejs.org/)，它们开始吸引开发人员的注意力。

随着现代浏览器的支持、卓越的框架和所需的工具，**潮流正在很大程度上转向客户端**。

在速度越来越快的手持设备上获得沉浸式体验需要更多的客户端处理。

## 3. 什么是同构应用程序？

因此，我们看到了前端框架如何帮助我们开发一个用户界面完全在客户端呈现的Web应用程序。

但是，**也可以在服务器端使用相同的框架并生成相同的用户界面**。

现在，我们不必坚持只使用客户端或只使用服务器端的解决方案，更好的方法是拥有一个**客户端和服务器都可以处理相同的前端代码并生成相同的用户界面的解决方案**。

这种方法有一些好处，我们将在稍后讨论。

<img src="../assets/img.png">

**此类Web应用程序称为同构或通用**，现在客户端语言是最完全的JavaScript。因此，要使同构应用程序正常工作，我们还必须在服务器端使用JavaScript。

Node.js是迄今为止构建服务器端呈现应用程序的最常见选择。

## 4. 什么是Nashorn？

那么，Nashorn适合什么地方，我们为什么要使用它？**Nashorn是默认与Java一起打包的JavaScript引擎**。因此，如果我们已经有一个Java的Web应用程序后端并且想要构建一个同构应用程序，[那么Nashorn非常方便]()！

Nashorn已作为Java 8的一部分发布，这主要侧重于允许在Java中嵌入JavaScript应用程序。

**Nashorn将内存中的JavaScript编译为Java字节码**，并将其传递给JVM执行。与早期的引擎Rhino相比，这提供了更好的性能。

## 5. 创建同构应用

我们现在已经了解了足够多的上下文，我们这里的应用程序将显示斐波那契数列并提供一个按钮来生成和显示数列中的下一个数字。现在让我们创建一个简单的同构应用程序，它有一个后端和一个前端：

-   前端：一个简单的基于React.js的前端
-   后端：一个简单的Spring Boot后端，使用Nashorn来处理JavaScript

## 6. 应用程序前端

我们将**使用React.js来创建我们的前端**，React是一个流行的JavaScript库，用于构建单页应用程序。它**帮助我们将复杂的用户界面分解为具有可选状态和单向数据绑定的分层组件**。

React解析这个层次结构并创建一个称为虚拟DOM的内存数据结构，这有助于React查找不同状态之间的变化，并对浏览器DOM进行最小的更改。

### 6.1 React组件

让我们创建我们的第一个React组件：

```javascript
var App = React.createClass({displayName: "App",
    handleSubmit: function() {
        var last = this.state.data[this.state.data.length-1];
        var secondLast = this.state.data[this.state.data.length-2];
        $.ajax({
            url: '/next/'+last+'/'+secondLast,
            dataType: 'text',
            success: function(msg) {
                var series = this.state.data;
                series.push(msg);
                this.setState({data: series});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error('/next', status, err.toString());
            }.bind(this)
        });
    },
    componentDidMount: function() {
        this.setState({data: this.props.data});
    },
    getInitialState: function() {
        return {data: []};
    },
    render: function() {
        return (
            React.createElement("div", {className: "app"},
                React.createElement("h2", null, "Fibonacci Generator"),
                React.createElement("h2", null, this.state.data.toString()),
                React.createElement("input", {type: "submit", value: "Next", onClick: this.handleSubmit})
            )
        );
    }
});
```

现在，让我们了解上面的代码在做什么：

-   首先，我们在React中定义了一个名为“App”的类组件
-   这个组件中**最重要的函数是“render”**，它负责生成用户界面
-   我们提供了一个组件可以使用的样式类名
-   我们在这里使用组件状态来存储和显示系列
-   当状态初始化为一个空列表时，它会在组件挂载时获取作为prop传递给组件的数据
-   最后，单击“添加”按钮，对REST服务进行jQuery调用
-   该调用获取序列中的下一个数字并将其附加到组件的状态
-   组件状态的改变会自动重新渲染组件

### 6.2 使用React组件

React在HTML页面中**寻找一个名为“div”的元素来锚定其内容**，我们所要做的就是提供一个带有这个“div”元素的HTML页面并加载JS文件：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hello React</title>
    <script type="text/javascript" src="js/react.js"></script>
    <script type="text/javascript" src="js/react-dom.js"></script>
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
</head>
<body>
<div id="root"></div>
<script type="text/javascript" src="app.js"></script>
<script type="text/javascript">
    ReactDOM.render(
            React.createElement(App, {data: [0, 1, 1]}),
            document.getElementById("root")
    );
</script>
</body>
</html>
```

那么，让我们看看我们在这里做了什么：

-   我们**导入了所需的JS库，react、react-dom和jQuery**
-   之后，我们定义了一个名为“root”的“div”元素
-   我们还使用React组件导入了JS文件
-   接下来，我们用一些种子数据调用React组件“App”，即前3个斐波那契数

## 7. 应用程序后端

现在，让我们看看如何为我们的应用程序创建合适的后端。我们已经决定使用[Spring Boot]()和Spring Web来构建这个应用程序，更重要的是，我们决定**使用Nashorn来处理我们在上一节中开发的基于JavaScript的前端**。

### 7.1 Maven依赖项

对于我们的简单应用程序，我们将结合使用JSP和Spring MVC，因此我们将在POM中添加一些依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <scope>provided</scope>
</dependency>
```

第一个是Web应用程序的[标准Spring Boot依赖项](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)，第二个是[编译JSP](https://search.maven.org/search?q=g:org.apache.tomcat.embed AND a:tomcat-embed-jasper)所需的依赖项。

### 7.2 Web Controller

现在让我们创建我们的Web控制器，它将处理我们的JavaScript文件并使用JSP返回HTML：

```java
@Controller
public class MyWebController {
    @RequestMapping("/")
    public String index(Map<String, Object> model) throws Exception {
        ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
        nashorn.eval(new FileReader("static/js/react.js"));
        nashorn.eval(new FileReader("static/js/react-dom-server.js"));
        nashorn.eval(new FileReader("static/app.js"));
        Object html = nashorn.eval(
              "ReactDOMServer.renderToString(" +
                    "React.createElement(App, {data: [0,1,1]})" +
                    ");");
        model.put("content", String.valueOf(html));
        return "index";
    }
}
```

那么，这里到底发生了什么：

-   我们从ScriptEngineManager获取Nashorn类型的ScriptEngine实例
-   然后，我们**将相关库加载到React、react.js和react-dom-server.js**
-   我们还加载了包含React组件“App”的JS文件
-   最后，我们评估一个JS片段，该片段使用组件“App”和一些种子数据创建React元素
-   这为我们提供了React的输出，一个作为Object的HTML片段
-   我们将这个HTML片段作为数据传递给相关视图-JSP

### 7.3 JSP

现在，我们如何在JSP中处理这个HTML片段？

回想一下，React会自动将其输出添加到命名为“div”的元素-在我们的例子中为“root”。但是，**我们将在我们的JSP中手动将服务器端生成的HTML片段添加到同一元素中**。

让我们看看JSP现在的样子：

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Hello React!</title>
    <script type="text/javascript" src="js/react.js"></script>
    <script type="text/javascript" src="js/react-dom.js"></script>
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.10.0.min.js"></script>
</head>
<body>
<div id="root">${content}</div>
<script type="text/javascript" src="app.js"></script>
<script type="text/javascript">
    ReactDOM.render(
            React.createElement(App, {data: [0, 1, 1]}),
            document.getElementById("root")
    );
</script>
</body>
</html>
```

这与我们之前创建的页面相同，除了我们将HTML片段添加到之前为空的“根”div中。

### 7.4 Rest Controller

最后，我们还需要一个服务器端REST端点，它为我们提供序列中的下一个斐波那契数：

```java
@RestController
public class MyRestController {
    @RequestMapping("/next/{last}/{secondLast}")
    public int index(@PathVariable("last") int last, 
                     @PathVariable("secondLast") int secondLast) throws Exception {
        return last + secondLast;
    }
}
```

这里没什么特别的，只是一个简单的Spring REST控制器。

## 8. 运行应用程序

现在，我们已经完成了前端和后端，是时候运行应用程序了。

我们应该正常启动Spring Boot应用程序，使用应用程序主类：

```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
```

当我们运行这个类时，**Spring Boot会编译我们的JSP，并使它们与Web应用程序的其余部分一起在嵌入式Tomcat上可用**。

现在，如果我们访问我们的网站，我们将看到：

<img src="../assets/img_1.png">

让我们了解事件的顺序：

-   浏览器请求这个页面
-   当此页面的请求到达时，Spring Web控制器会处理JS文件
-   Nashorn引擎生成一个HTML片段并将其传递给JSP
-   JSP将此HTML片段添加到“根”div元素，最终返回上述的HTML页面
-   浏览器渲染HTML，同时开始下载JS文件
-   最后，该页面已准备好进行客户端操作-我们可以在该系列中添加更多数字

这里需要理解的重要一点是，如果React在目标“div”元素中找到一个HTML片段，会发生什么。在这种情况下，**React会将这个片段与其拥有的片段进行比较**，如果它找到一个清晰的片段，则不会替换它。这正是服务器端渲染和同构应用程序的动力所在。

## 9. 还有什么可能？

在我们的简单示例中，我们只是触及了可能性的表面，使用基于JS的现代框架的前端应用程序变得越来越强大和复杂，随着这种增加的复杂性，我们需要处理很多事情：

-   我们在我们的应用程序中只创建了一个React组件，而实际上，这可以**是几个组件形成一个层次结构**，通过props传递数据
-   我们希望**为每个组件创建单独的JS文件**，以保持它们的可管理性，并通过“exports/require”或“export/import”来管理它们的依赖关系
-   此外，可能无法仅在组件内管理状态；我们可能希望使用像[Redux](https://redux.js.org/)这样的状态管理库
-   此外，作为操作的副作用，我们可能不得不与外部服务进行交互；这可能需要我们使用像[redux-thunk](https://github.com/reduxjs/redux-thunk)或[Redux-Saga](https://redux-saga.js.org/)这样的模式
-   最重要的是，我们希望**利用JSX，它是JS的语法扩展**，用于描述用户界面

虽然Nashorn与纯JS完全兼容，但它可能不支持上述所有功能。由于JS兼容性，其中许多都需要反式编译和polyfill。

在这种情况下，通常的做法是利用像[Webpack](https://webpack.js.org/)或[Rollup](https://rollupjs.org/guide/en/)这样的模块打包器，他们主要做的是处理所有的React源文件，并将它们与所有依赖项一起打包到一个JS文件中。这总是需要像[Babel](https://babeljs.io/)这样的现代JavaScript编译器来编译JavaScript以实现向后兼容。

最终的包只有很好的旧JS，浏览器可以理解，Nashorn也遵守。

## 10. 同构应用的好处

因此，我们已经讨论了很多关于同构应用程序的内容，现在甚至还创建了一个简单的应用程序。但我们究竟为什么要关心这个呢？让我们了解使用同构应用程序的一些主要好处。

### 10.1 首页渲染

同构应用程序最重要的好处之一是**首页的渲染速度更快**，在典型的客户端呈现的应用程序中，浏览器首先下载所有JS和CSS工件。

之后，他们加载并开始呈现首页，如果我们发送从服务器端呈现的第一个页面，这可能会快得多，从而提供增强的用户体验。

### 10.2 搜索引擎优化友好

**服务器端渲染经常提到的另一个好处与SEO相关**，人们认为搜索机器人无法处理JavaScript，因此看不到通过React等库在客户端呈现的index页面，因此，服务器端呈现的页面对SEO更友好。不过，值得注意的是，现代搜索引擎机器人声称可以处理JavaScript。

## 11. 总结

在本教程中，我们介绍了同构应用程序和Nashorn JavaScript引擎的基本概念，我们进一步探讨了如何使用Spring Boot、React和Nashorn构建同构应用程序。

然后，我们讨论了扩展前端应用程序的其他可能性以及使用同构应用程序的好处。