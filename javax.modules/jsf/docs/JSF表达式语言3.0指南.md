## 1. 概述

在本文中，我们将了解表达式语言 3.0 版 (EL 3.0) 的最新功能、改进和兼容性问题。

这是撰写本文时的最新版本，并随更新的 JavaEE 应用程序服务器一起发布(JBoss EAP 7 和 Glassfish 4 是实现了对它的支持的好例子)。

本文仅关注 EL 3.0 的发展——要了解更多关于表达式语言的一般知识，请先阅读[EL 2.2 版](https://www.baeldung.com/intro-to-jsf-expression-language)文章。

## 2.先决条件

本文中显示的示例也已针对 Tomcat 8 进行了测试。要使用 EL3.0，必须添加以下依赖项：

```xml
<dependency>
    <groupId>javax.el</groupId>
    <artifactId>javax.el-api</artifactId>
    <version>3.0.0</version>
</dependency>
```

始终可以通过此[链接](https://search.maven.org/classic/#search|gav|1|g%3A"javax.el" AND a%3A"javax.el-api")检查 Maven 存储库中的最新依赖项。

## 3. Lambda 表达式

最新的 EL 迭代为 lambda 表达式提供了非常强大的支持。Lambda 表达式被引入到JavaSE 8 中，但 EL 中对它的支持随JavaEE 7 一起提供。

这里的实现是全功能的，允许在 EL 使用和评估中有很大的灵活性(和一些隐含的风险)。

### 3.1. Lambda EL 值表达式

此功能的基本用法允许我们将 lambda 表达式指定为 EL 值表达式中的值类型：

```xml
<h:outputText id="valueOutput" 
  value="#{(x->xxx);(ELBean.pageCounter)}"/>
```

由此扩展，可以在 EL 中命名 lambda 函数以便在复合语句中重用，就像在JavaSE 中的 lambda 表达式中一样。复合 lambda 表达式可以用分号 ( ; ) 分隔：

```xml
<h:outputText id="valueOutput" 
  value="#{cube=(x->xxx);cube(ELBean.pageCounter)}"/>

```

此代码段将函数分配给多维数据集标识符，然后可以立即重用。

### 3.2. 将 Lambda 表达式传递给支持 Bean

让我们更进一步：通过在 EL 表达式(作为 lambda)中封装逻辑并将其传递给 JSF 支持 bean，我们可以获得很大的灵活性：

```xml
<h:outputText id="valueOutput" 
  value="#{ELBean.multiplyValue(x->xxx)}"/>

```

这现在允许我们将整个 lambda 表达式作为javax.el.LambdaExpression的实例进行处理：

```java
public String multiplyValue(LambdaExpression expr){
    return (String) expr.invoke( 
      FacesContext.getCurrentInstance().getELContext(), pageCounter);
}

```

这是一个引人注目的功能，它允许：

-   一种干净的逻辑封装方式，提供非常灵活的函数式编程范例。上面的支持 bean 逻辑可以根据从不同来源提取的值进行条件处理。
-   一种在可能尚未准备好升级的 JDK 8 之前的代码库中引入 lambda 支持的简单方法。
-   使用新的 Streams/Collections API 的强大工具。

## 4. 集合 API 增强

早期版本的 EL 对 Collections API 的支持有些欠缺。EL 3.0 在其对Java集合的支持方面引入了主要的 API 改进，就像 lambda 表达式一样，EL 3.0 在JavaEE 7 中提供了 JDK 8 Streaming 支持。

### 4.1. 动态集合定义

3.0 中的新功能，我们现在可以在 EL 中动态定义临时数据结构：

-   清单：

```xml
   <h:dataTable var="listItem" value="#{['1','2','3']}">
       <h:column id="nameCol">
           <h:outputText id="name" value="#{listItem}"/>
       </h:column>
   </h:dataTable>

```

-   套：

```xml
   <h:dataTable var="setResult" value="#{{'1','2','3'}}">
    ....
   </h:dataTable>

```

注意：与普通的JavaSets 一样，元素的顺序在列出时是不可预测的

-   地图：

```xml
   <h:dataTable var="mapResult" 
     value="#{{'one':'1','two':'2','three':'3'}}">
 

```

提示：在定义动态地图时，教科书中的一个常见错误是使用双引号 (“) 而不是地图键的单引号——这将导致 EL 编译错误。

### 4.2. 高级收集操作

EL3.0 支持高级查询语义，它结合了 lambda 表达式的强大功能、新的流式 API 和类似 SQL 的操作(如连接和分组)。我们不会在本文中介绍这些内容，因为它们是高级主题。让我们看一个示例来展示它的强大功能：

```xml
<h:dataTable var="streamResult" 
  value="#{['1','2','3'].stream().filter(x-> x>1).toList()}">
    <h:column id="nameCol">
        <h:outputText id="name" value="#{streamResult}"/>
    </h:column>
</h:dataTable>

```

上表将使用传递的 lambda 表达式过滤后备列表

```xml
 <h:outputLabel id="avgLabel" for="avg" 
   value="Average of integer list value"/>
 <h:outputText id="avg" 
   value="#{['1','2','3'].stream().average().get()}"/>
```

输出文本avg将计算列表中数字的平均值。通过新的[Optional API](https://www.baeldung.com/java-8-new-features)(对先前版本的另一项改进)，这两个操作都是 null 安全的。

请记住，对此的支持不需要 JDK 8，只需要 JavaEE 7/EL3.0。这意味着可以在 EL 中执行大部分 JDK 8 Stream操作，但不能在支持 beanJava代码中执行。

提示： 可以使用 JSTL <c:set/>标记将的数据结构声明为页面级变量并在整个 JSF 页面中对其进行操作：

```xml
 <c:set var='pageLevelNumberList' value="#{[1,2,3]}"/>

```

现在可以在整个页面中引用“#{pageLevelNumberList}”，就像它是真正的 JSF 组件或 bean 一样。这允许在整个页面中进行大量重用

```xml
<h:outputText id="avg" 
  value="#{pageLevelNumberList.stream().average().get()}"/>
```

## 5.静态字段和方法

在以前的 EL 版本中不支持静态字段、方法或枚举访问。事情变了。

首先，我们必须手动将包含常量的类导入到 EL 上下文中。这最好尽早完成。在这里，我们在JSF 托管 bean的@PostConstruct初始值设定项中执行此操作( ServletContextListener也是一个可行的候选者)：

```java
 @PostConstruct
 public void init() {
     FacesContext.getCurrentInstance()
       .getApplication().addELContextListener(new ELContextListener() {
         @Override
         public void contextCreated(ELContextEvent evt) {
             evt.getELContext().getImportHandler()
              .importClass("com.baeldung.el.controllers.ELSampleBean");
         }
     });
 }

```

然后我们在所需的类中定义一个String常量字段(或者一个Enum ，如果你选择的话)：

```java
public static final String constantField 
  = "THIS_IS_NOT_CHANGING_ANYTIME_SOON";

```

之后我们现在可以访问 EL 中的变量：

```xml
 <h:outputLabel id="staticLabel" 
   for="staticFieldOutput" value="Constant field access: "/>
 <h:outputText id="staticFieldOutput" 
   value="#{ELSampleBean.constantField}"/>

```

根据 EL 3.0 规范， java.lang.之外的任何类都需要手动导入，如图所示。只有这样做之后，类中定义的常量才能在 EL 中使用。导入最好作为 JSF 运行时初始化的一部分完成。

这里需要注意几点：

-   语法要求字段和方法是公共的、静态的(在方法的情况下是最终的)

-   语法在 EL 3.0 规范的初始草案和发布版本之间发生了变化。所以在一些教科书中，你可能仍然会找到类似这样的东西：

    ```xml
    T(YourClass).yourStaticVariableOrMethod
    ```

    这在实践中行不通(简化语法的设计更改是在实施周期后期决定的)

-   发布的最终语法仍然存在错误——运行这些语法的最新版本很重要。

## 六. 总结

我们已经检查了最新 EL 实现中的一些亮点。进行了重大改进，为 API 带来了很酷的新功能，例如 lambda 和流灵活性。

鉴于我们现在在 EL 中拥有的灵活性，记住 JSF 框架的设计目标之一很重要：使用 MVC 模式将关注点完全分离。

因此，值得注意的是，API 的最新改进可能使我们对 JSF 中的反模式敞开心扉，因为 EL 现在有能力执行真正的业务逻辑——比以前更多。因此，在现实世界的实施过程中牢记这一点非常重要，以确保责任被整齐地分开。