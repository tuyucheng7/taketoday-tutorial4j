## 1. 概述

在本教程中，我们将讨论在Java中使用通配符导入的优点和缺点。

## 2.Java中的导入

Java import语句声明代码中使用的名称(类名、静态变量和方法名)的来源。 

例如，让我们看一个Book类：

```java
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Book {

    private UUID id;

    private String name;

    private Date datePublished;

    private List<String> authors;
}
```

在这里，我们需要将Date和UUID这两种数据类型与List接口一起导入，因为它们默认不可用。因此，我们编写了三个导入语句以使这些数据类型可用于我们的类。我们将这些类型的导入称为特定导入。

## 3.Java通配符导入

通配符导入是指导入[包](http://baeldung.com/java-packages)而不是声明包中使用的特定类名。

使用通配符，我们可以将前面示例中的三个 import 语句替换为一个语句： 

```java
import java.util.;

public class Book {

    private UUID id;

    private String name;

    private Date datePublished;

    private List<String> authors;
}
```

这个通配符导入语句将整个java.util包添加到搜索路径，可以在其中找到所需的UUID、Date和List名称。

## 4.通配符导入的优势

与Java中的特定导入相比，通配符导入自然有一些优势。让我们在下面的小节中讨论通配符导入的主要优点。

### 4.1. 清洁代码

通配符导入帮助我们避免代码中的一长串导入。因此，这会影响代码的可读性，因为读者在到达显示逻辑的代码之前可能必须在每个源代码文件中滚动很多次。毫无疑问，更具可读性的代码也是[干净的代码](http://baeldung.com/java-clean-code)。 

这个想法在Robert C. Martin的 Clean Code 一书中也得到了支持。事实上，本书建议在使用同一来源的多个类时使用通配符导入。换句话说，当我们导入从一个包中导入的两个或多个类时，最好导入整个包。

### 4.2. 重构容易

使用通配符导入，重构更容易。例如，在重命名一个类时，我们不需要删除其所有特定的导入声明。

此外，如果我们将一个类从我们的一个包移动到另一个我们自己的包，如果通配符导入已经存在于两个包的文件中，我们就不需要重构任何代码。

### 4.3. 松耦合

通配符导入在现代软件开发中强制执行[松耦合方法。](http://baeldung.com/cs/cohesion-vs-coupling)

根据 Robert C. Martin 的说法，使用通配符导入的想法加强了松散耦合。对于特定的导入，类必须存在于包中。但是，使用通配符导入时，包中不需要存在特定的类。实际上，通配符导入将指定的包添加到搜索路径中，可以在其中搜索所需的类名。

因此，通配符样式的导入不会向包添加真正的依赖性。 

## 5.通配符导入的缺点

通配符导入也有其缺点。接下来，让我们看看通配符导入是如何导致一些问题的。

### 5.1. 类名冲突

不幸的是，当在通过通配符导入的多个包中找到一个类名时，就会发生冲突。

在这种情况下，编译器注意到有两个Date类并给出错误，因为在java.sql和java.util包中都找到了Date类：

```java
import java.util.;
import java.sql.;

public class Library {

    private UUID id;

    private String name;

    private Time openingTime;

    private Time closingTime;

    private List<Date> datesClosed;
}
```

为防止此类错误，我们可以指定冲突类的所需来源。

为了防止上面示例中的错误，我们可以添加第三行来指定冲突Date类的源到两个现有的导入：

```java
import java.util.;
import java.sql.;
import java.sql.Date;

```

### 5.2. 不可预见的类名冲突

有趣的是，随着时间的推移，冲突也会浮出水面，例如当一个类被添加到我们正在使用的另一个包的较新版本时。

例如，在Java1.1 中，List类只能在[java.awt](https://www.baeldung.com/java-images)包中找到。然而，在Java1.2 中，一个名为List的接口被添加到java.util包中。

让我们看一个例子：

```java
import java.awt.;
import java.util.;

public class BookView extends Frame {

    private UUID id;

    private String name;

    private Date datePublished;

    private List<String> authors;
}

```

最终，当java.awt和java.util包作为通配符导入时，这种情况可能会导致冲突。因此，在将代码迁移到较新的Java 版本时，我们可能会遇到问题。 

## 六，总结

在本文中，我们讨论了Java中的导入语句以及什么是通配符导入。我们了解了在我们的程序中使用通配符导入的优点和缺点。

通配符导入与特定导入的使用仍然是Java社区中的热门争论。简而言之， 我们可以说通配符导入方法具有优势，但在某些情况下使用它会导致问题。