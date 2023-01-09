## 1. 概述

CDI(上下文和依赖注入)是Java EE 6 及更高版本中包含的标准[依赖注入框架。](https://en.wikipedia.org/wiki/Dependency_injection)

它允许我们通过特定于域的生命周期上下文来管理有状态组件的生命周期，并以类型安全的方式将组件(服务)注入到客户端对象中。

在本教程中，我们将深入了解 CDI 最相关的功能，并实施在客户端类中注入依赖项的不同方法。

## 2. DYDI(自己动手做依赖注入)

简而言之，无需借助任何框架即可实现 DI。

这种方法通常被称为 DYDI(自己动手做依赖注入)。

使用 DYDI，我们通过普通的旧工厂/构建器将所需的依赖项传递到客户端类，从而将应用程序代码与对象创建隔离开来。

下面是一个基本的 DYDI 实现的样子：

```java
public interface TextService {
    String doSomethingWithText(String text);
    String doSomethingElseWithText(String text);    
}
public class SpecializedTextService implements TextService { ... }
public class TextClass {
    private TextService textService;
    
    // constructor
}
public class TextClassFactory {
      
    public TextClass getTextClass() {
        return new TextClass(new SpecializedTextService(); 
    }    
}
```

当然，DYDI 适用于一些比较简单的用例。

如果我们的示例应用程序的大小和复杂性增加，实现更大的互连对象网络，我们最终会用大量的对象图工厂污染它。

这将需要大量用于创建对象图的样板代码。这不是一个完全可扩展的解决方案。

我们可以做得更好吗？我们当然可以。这正是 CDI 发挥作用的地方。

## 3. 一个简单的例子

CDI 将 DI 变成了一个无需动脑筋的过程，归结为只需用一些简单的注解装饰服务类，并在客户端类中定义相应的注入点。

为了展示 CDI 如何在最基本的层面上实现 DI，假设我们要开发一个简单的图像文件编辑应用程序。能够打开、编辑、写入、保存图像文件等。

### 3.1. “ beans.xml”文件

首先，我们必须在“src/main/resources/META-INF/”文件夹中放置一个“beans.xml”文件。即使此文件根本不包含任何特定的 DI 指令，它也是启动和运行 CDI 所必需的：

```xml
<beans xmlns="http://java.sun.com/xml/ns/javaee" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
  http://java.sun.com/xml/ns/javaee/beans_1_0.xsd">
</beans>
```

### 3.2. 服务等级

接下来，让我们创建对 GIF、JPG 和 PNG 文件执行上述文件操作的服务类：

```java
public interface ImageFileEditor {
    String openFile(String fileName);
    String editFile(String fileName);
    String writeFile(String fileName);
    String saveFile(String fileName);
}
public class GifFileEditor implements ImageFileEditor {
    
    @Override
    public String openFile(String fileName) {
        return "Opening GIF file " + fileName;
    }
    
    @Override
    public String editFile(String fileName) {
      return "Editing GIF file " + fileName;
    }
    
    @Override
    public String writeFile(String fileName) {
        return "Writing GIF file " + fileName;
    }

    @Override
    public String saveFile(String fileName) {
        return "Saving GIF file " + fileName;
    }
}
public class JpgFileEditor implements ImageFileEditor {
    // JPG-specific implementations for openFile() / editFile() / writeFile() / saveFile()
    ...
}
public class PngFileEditor implements ImageFileEditor {
    // PNG-specific implementations for openFile() / editFile() / writeFile() / saveFile()
    ...
}
```

### 3.3. 客户端类

最后，让我们实现一个在构造函数中采用ImageFileEditor实现的客户端类，并使用@Inject注解定义一个注入点：

```java
public class ImageFileProcessor {
    
    private ImageFileEditor imageFileEditor;
    
    @Inject
    public ImageFileProcessor(ImageFileEditor imageFileEditor) {
        this.imageFileEditor = imageFileEditor;
    }
}
```

简而言之，@Inject注解是 CDI 的实际主力。它允许我们在客户端类中定义注入点。

在这种情况下，@Inject指示 CDI 在构造函数中注入ImageFileEditor实现。

此外，还可以通过在字段(字段注入)和设置器(setter 注入)中使用@Inject注解来注入服务。稍后我们将查看这些选项。

### 3.4. 使用 Weld构建ImageFileProcessor对象图

当然，我们需要确保 CDI 将正确的ImageFileEditor实现注入到ImageFileProcessor类构造函数中。

为此，首先，我们应该获得该类的一个实例。

由于我们不会依赖任何JavaEE 应用程序服务器来使用 CDI，因此我们将使用[Weld](http://weld.cdi-spec.org/)来执行此操作，这是JavaSE 中的 CDI 参考实现：

```java
public static void main(String[] args) {
    Weld weld = new Weld();
    WeldContainer container = weld.initialize();
    ImageFileProcessor imageFileProcessor = container.select(ImageFileProcessor.class).get();
 
    System.out.println(imageFileProcessor.openFile("file1.png"));
 
    container.shutdown();
}

```

在这里，我们创建一个WeldContainer对象，然后获取一个ImageFileProcessor对象，最后调用它的openFile()方法。

正如预期的那样，如果我们运行该应用程序，CDI 将通过抛出DeploymentException大声抱怨：

```plaintext
Unsatisfied dependencies for type ImageFileEditor with qualifiers @Default at injection point...
```

我们收到此异常是因为 CDI 不知道将什么ImageFileEditor实现注入到ImageFileProcessor构造函数中。

在 CDI 的术语中，这称为模糊注入异常。

### 3.5. @Default和@Alternative注解_

解决这种歧义很容易。默认情况下，CDI 使用@Default注解来注解接口的所有实现。

因此，我们应该明确地告诉它应该将哪个实现注入到客户端类中：

```java
@Alternative
public class GifFileEditor implements ImageFileEditor { ... }
@Alternative
public class JpgFileEditor implements ImageFileEditor { ... }

public class PngFileEditor implements ImageFileEditor { ... }
```

在本例中，我们使用@Alternative 注解对GifFileEditor和JpgFileEditor进行了注解，因此 CDI 现在知道PngFileEditor(默认使用@Default注解进行注解)是我们要注入的实现。

如果我们重新运行该应用程序，这次它将按预期执行：

```plaintext
Opening PNG file file1.png

```

此外，使用@Default注解对PngFileEditor进行注解并将其他实现保留为替代项将产生与上述相同的结果。

简而言之，这表明我们如何通过简单地切换服务类中的@Alternative注解来非常轻松地交换实现的运行时注入。

## 4. 字段注入

CDI 支持开箱即用的字段和设置器注入。

以下是执行字段注入的方法(使用@Default和@Alternative注解限定服务的规则保持不变)：

```java
@Inject
private final ImageFileEditor imageFileEditor;
```

## 5.二传手注射

同样，这里是如何进行 setter 注入：

```java
@Inject 
public void setImageFileEditor(ImageFileEditor imageFileEditor) { ... }
```

## 6. @Named注解

到目前为止，我们已经学习了如何在客户端类中定义注入点并使用@Inject、@Default 和@Alternative 注解注入服务，这些注解涵盖了大部分用例。

尽管如此，CDI 还允许我们使用@Named注解执行服务注入。

此方法通过将有意义的名称绑定到实现，提供了一种更语义化的服务注入方式：

```java
@Named("GifFileEditor")
public class GifFileEditor implements ImageFileEditor { ... }

@Named("JpgFileEditor")
public class JpgFileEditor implements ImageFileEditor { ... }

@Named("PngFileEditor")
public class PngFileEditor implements ImageFileEditor { ... }
```

现在，我们应该重构ImageFileProcessor类中的注入点以匹配命名实现：

```java
@Inject 
public ImageFileProcessor(@Named("PngFileEditor") ImageFileEditor imageFileEditor) { ... }
```

也可以使用命名实现执行字段和 setter 注入，这看起来与使用@Default和@Alternative注解非常相似：

```java
@Inject 
private final @Named("PngFileEditor") ImageFileEditor imageFileEditor;

@Inject 
public void setImageFileEditor(@Named("PngFileEditor") ImageFileEditor imageFileEditor) { ... }
```

## 7. @Produces注解

有时，服务需要一些配置在被注入以处理额外的依赖关系之前被完全初始化。

CDI 通过@Produces注解为这些情况提供支持。

@Produces允许我们实现工厂类，其职责是创建完全初始化的服务。

要了解@Produces注解的工作原理，让我们重构ImageFileProcessor类，以便它可以在构造函数中使用额外的TimeLogger服务。

该服务将用于记录执行某个图像文件操作的时间：

```java
@Inject
public ImageFileProcessor(ImageFileEditor imageFileEditor, TimeLogger timeLogger) { ... } 
    
public String openFile(String fileName) {
    return imageFileEditor.openFile(fileName) + " at: " + timeLogger.getTime();
}
    
// additional image file methods

```

在这种情况下，TimeLogger类采用两个附加服务，SimpleDateFormat和Calendar：

```java
public class TimeLogger {
    
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    
    // constructors
    
    public String getTime() {
        return dateFormat.format(calendar.getTime());
    }
}
```

我们如何告诉 CDI 在何处查看以获取完全初始化的TimeLogger对象？

我们只是创建一个TimeLogger工厂类，并使用@Produces注解来注解它的工厂方法：

```java
public class TimeLoggerFactory {
    
    @Produces
    public TimeLogger getTimeLogger() {
        return new TimeLogger(new SimpleDateFormat("HH:mm"), Calendar.getInstance());
    }
}
```

每当我们获得ImageFileProcessor实例时，CDI 将扫描TimeLoggerFactory类，然后调用getTimeLogger()方法(因为它使用@Produces注解进行注解)，最后注入Time Logger服务。

如果我们使用Weld运行重构的示例应用程序，它将输出以下内容：

```java
Opening PNG file file1.png at: 17:46
```

## 8.自定义限定符

CDI 支持使用自定义限定符来限定依赖项和解决不明确的注入点。

自定义限定符是一个非常强大的特性。它们不仅将语义名称绑定到服务，而且还绑定注入元数据。[RetentionPolicy](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/annotation/RetentionPolicy.html)和合法注解目标 ( [ElementType](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/annotation/ElementType.html) )等元数据。

让我们看看如何在我们的应用程序中使用自定义限定符：

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface GifFileEditorQualifier {}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface JpgFileEditorQualifier {}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface PngFileEditorQualifier {}

```

现在，让我们将自定义限定符绑定到ImageFileEditor实现：

```java
@GifFileEditorQualifier
public class GifFileEditor implements ImageFileEditor { ... }

@JpgFileEditorQualifier
public class JpgFileEditor implements ImageFileEditor { ... }
@PngFileEditorQualifier
public class PngFileEditor implements ImageFileEditor { ... }

```

最后，让我们重构ImageFileProcessor类中的注入点：

```java
@Inject
public ImageFileProcessor(@PngFileEditorQualifier ImageFileEditor imageFileEditor, TimeLogger timeLogger) { ... }

```

如果我们再次运行我们的应用程序，它应该会生成如上所示的相同输出。

自定义限定符为将名称和注解元数据绑定到实现提供了一种简洁的语义方法。

此外，自定义限定符允许我们定义更具限制性的类型安全注入点(优于@Default 和@Alternative 注解的功能)。

如果在类型层次结构中只有一个子类型被限定，那么 CDI 将只注入子类型，而不是基类型。

## 9.总结

毫无疑问，CDI 使依赖注入变得轻而易举，额外注解的成本对于获得有组织的依赖注入来说是非常小的努力。

有时 DYDI 确实仍然比 CDI 占有一席之地。就像在开发仅包含简单对象图的相当简单的应用程序时一样。