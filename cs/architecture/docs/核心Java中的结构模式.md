## 1. 概述

结构[设计模式](https://www.baeldung.com/design-patterns-series)是那些通过识别大型对象结构之间的关系来简化大型对象结构设计的模式。它们描述了组合类和对象的常用方法，以便它们作为解决方案变得可重复。

四人帮描述了七种这样的结构方式或模式[。](https://www.pearson.com/us/higher-education/program/Gamma-Design-Patterns-Elements-of-Reusable-Object-Oriented-Software/PGM14333.html)在本快速教程中，我们将看到一些示例，说明 Java 中的一些核心库如何采用它们中的每一个。

## 2.[适配器](https://www.baeldung.com/java-adapter-pattern)

顾名思义，适配器充当中介，将原本不兼容的接口转换为客户期望的接口。

这在我们想要获取源代码无法修改的现有类并使其与另一个类一起工作的情况下很有用。

JDK 的集合框架提供了很多适配器模式的例子：

```java
List<String> musketeers = Arrays.asList("Athos", "Aramis", "Porthos");复制
```

在这里，[Arrays#asList](https://www.baeldung.com/java-arrays-aslist-vs-new-arraylist)帮助我们将Array适配为List。

I/O 框架也广泛使用了这种模式。作为示例，让我们考虑一下将[InputStream映射到Reader](https://www.baeldung.com/java-convert-inputstream-to-reader)对象的代码片段：

```java
InputStreamReader input = new InputStreamReader(new FileInputStream("input.txt"));复制
```

## 3.[桥梁](https://www.baeldung.com/java-structural-design-patterns#bridge)

桥接模式允许抽象和实现之间的分离，以便它们可以彼此独立开发，但仍然有一种方式或桥梁来共存和交互。

Java 中的一个例子是[JDBC API](https://www.baeldung.com/java-jdbc)。它充当 Oracle、MySQL 和 PostgreSQL 等数据库及其特定实现之间的链接。

JDBC API 是一组标准接口，例如Driver、Connection和ResultSet 等等。这使不同的数据库供应商可以有各自的实现。

例如，要创建到数据库的连接，我们会说：

```java
Connection connection = DriverManager.getConnection(url);复制
```

在这里，url是一个字符串，可以代表任何数据库供应商。

例如，对于 PostgreSQL，我们可能有：

```java
String url = "jdbc:postgresql://localhost/demo";复制
```

对于 MySQL：

```java
String url = "jdbc:mysql://localhost/demo";复制
```

## 4.[复合](https://www.baeldung.com/java-composite-pattern)

此模式处理对象的树状结构。在这棵树中，单个对象，甚至整个层次结构，都以相同的方式处理。简而言之，此模式以分层方式排列对象，以便客户端可以与整体的任何一部分无缝协作。

AWT/Swing 中的嵌套容器是在核心 Java 中使用复合模式的很好的例子。java.awt.Container对象基本上是一个根组件，可以包含其他组件，形成嵌套组件的树结构。

考虑这个代码片段：

```java
JTabbedPane pane = new JTabbedPane();
pane.addTab("1", new Container());
pane.addTab("2", new JButton());
pane.addTab("3", new JCheckBox());复制
```

此处使用的所有类——即JTabbedPane、JButton、JCheckBox和JFrame—— 都是Container的后代。正如我们所见，此代码片段在第二行中处理树的根或Container 的方式与其处理其子节点的方式相同。

## 5.[装饰器](https://www.baeldung.com/java-decorator-pattern)

当我们想要在不修改原始对象本身的情况下增强对象的行为时，这种模式就会发挥作用。这是通过向对象添加相同类型的包装器以向其附加额外责任来实现的。

这种模式最普遍的用法之一可以在[java.io](https://www.baeldung.com/java-download-file#using-java-io)包中找到：

```java
BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File("test.txt")));
while (bis.available() > 0) {
    char c = (char) bis.read();
    System.out.println("Char: " + c);
}复制
```

在这里，BufferedInputStream正在修饰FileInputStream以添加缓冲输入的功能。值得注意的是，这两个类都将InputStream作为共同的祖先。这意味着装饰的对象和被装饰的对象属于同一类型。这是装饰者模式的明确指标。

## 6.[立面](https://www.baeldung.com/java-facade-pattern)

根据定义，外观一词是指物体的人造或虚假外观。应用于编程，它同样意味着为一组复杂的对象提供另一个面孔——或者更确切地说，接口。

当我们想要简化或隐藏子系统或框架的复杂性时，这种模式就会发挥作用。

Faces API 的ExternalContext是外观模式的一个很好的例子。它在内部使用HttpServletRequest、HttpServletResponse和HttpSession等类。基本上，它是一个允许 Faces API 幸福地不知道其底层应用程序环境的类。

让我们看看[Primefaces](https://www.primefaces.org/docs/api/5.3/org/primefaces/component/export/PDFExporter.html#writePDFToResponse(javax.faces.context.ExternalContext, java.io.ByteArrayOutputStream, java.lang.String))如何使用它来编写一个HttpResponse，而实际上并不知道它：

```java
protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName)
  throws IOException, DocumentException {
    externalContext.setResponseContentType("application/pdf");
    externalContext.setResponseHeader("Expires", "0");
    // set more relevant headers
    externalContext.setResponseContentLength(baos.size());
    externalContext.addResponseCookie(
      Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
    OutputStream out = externalContext.getResponseOutputStream();
    baos.writeTo(out);
    // do cleanup
}复制
```

正如我们在这里看到的，我们直接使用ExternalContext作为外观来设置响应标头、实际响应和 cookie 。HTTPResponse不在图中。

## 7.[享元](https://www.baeldung.com/java-flyweight)

享元模式通过回收对象来减轻对象的重量或内存占用。换句话说，如果我们有可以共享状态的不可变对象，按照这种模式，我们可以缓存它们以提高系统性能。

[在 Java 的Number](https://www.baeldung.com/java-number-class)类中随处可见享元。

用于创建任何数据类型的包装类对象的 valueOf方法旨在缓存值并在需要时返回它们。

例如，[Integer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html)有一个静态类IntegerCache，它有助于其[valueOf](https://www.baeldung.com/java-convert-string-to-int-or-integer#integervalueof)方法始终缓存 -128 到 127 范围内的值：

```java
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high) {
        return IntegerCache.cache[i + (-IntegerCache.low)];
    }
    return new Integer(i);
}复制
```

## 8.[代理](https://www.baeldung.com/java-proxy-pattern)

此模式为另一个复杂对象提供代理或替代。虽然它听起来与外观相似，但实际上有所不同，因为外观为客户端提供了不同的交互界面。在代理的情况下，接口与其隐藏的对象的接口相同。

使用此模式，可以轻松地在原始对象创建之前或之后对其执行任何操作。

JDK为代理实现提供了一个开箱即用的[java.lang.reflect.Proxy类：](https://www.baeldung.com/java-dynamic-proxies)

```java
Foo proxyFoo = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(),
  new Class<?>[] { Foo.class }, handler);复制
```

上面的代码片段为接口Foo创建了一个代理proxyFoo。

## 9.总结

在这个简短的教程中，我们看到了在核心 Java 中实现的结构设计模式的实际用法。

总而言之，我们简要定义了七种模式分别代表什么，然后通过代码片段一一理解。