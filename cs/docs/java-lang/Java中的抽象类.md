## 1. 概述

在很多情况下，在实现合约时，我们希望推迟实现的某些部分稍后完成。我们可以通过抽象类在Java中轻松实现这一点。

**在本教程中，我们将学习Java中抽象类的基础知识，以及它们在哪些情况下会有所帮助**。

## 2. 抽象类的关键概念

在深入探讨何时使用抽象类之前，**让我们看看它们最相关的特征**：

-   **我们定义一个抽象类，在class关键字之前加上abstract修饰符**
-   抽象类可以被子类化，但不能被实例化
-   如果一个类定义了一个或多个抽象方法，那么这个类本身必须被声明为抽象的
-   **抽象类可以声明抽象方法和具体方法**
-   从抽象类派生的子类必须实现基类的所有抽象方法或本身是抽象的

为了更好地理解这些概念，我们将创建一个简单的示例。

让我们的基本抽象类定义棋盘游戏的抽象API：

```java
public abstract class BoardGame {

    // ... field declarations, constructors

    public abstract void play();

    // ... concrete methods
}
```

然后，我们可以创建一个实现play方法的子类：

```java
public class Checkers extends BoardGame {

    public void play() {
        //... implementation
    }
}
```

## 3. 何时使用抽象类

现在，**让我们分析几个典型的场景，我们应该更倾向抽象类而不是接口和具体类**：

-   我们希望将一些通用功能封装在一个地方(代码重用)，多个相关的子类将共享这些功能
-   我们需要部分定义一个API，我们的子类可以轻松地扩展和改进它
-   子类需要继承一个或多个具有protected访问修饰符的公共方法或字段

请记住，所有这些场景都是基于继承的完全遵守[开放/封闭原则](https://en.wikipedia.org/wiki/Open–closed_principle)的好例子。

此外，由于抽象类的使用隐式处理基类型和子类型，我们也利用了[多态性](https://www.baeldung.com/java-polymorphism)。

请注意，代码重用是使用抽象类的一个非常有说服力的理由，只要保留类层次结构中的“is-a”关系即可。

**[Java 8增加了默认方法的另一个缺陷](https://www.baeldung.com/java-static-default-methods)，它有时可以代替完全创建抽象类的需要**。

## 4. FileReader的示例层次结构

为了更清楚地理解抽象类带来的功能，让我们看另一个例子。

### 4.1 定义基抽象类

因此，如果我们想要多种类型的文件读取器，我们可能会创建一个抽象类来封装文件读取的常见内容：

```java
public abstract class BaseFileReader {

    protected Path filePath;

    protected BaseFileReader(Path filePath) {
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
    }

    public List<String> readFile() throws IOException {
        return Files.lines(filePath)
              .map(this::mapFileLine).collect(Collectors.toList());
    }

    protected abstract String mapFileLine(String line);
}
```

请注意，我们已经使filePath为受保护的，以便子类可以在需要时访问它。更重要的是，**我们还有一些未完成的事情：如何从文件内容中实际解析一行文本**。

我们的计划很简单：虽然我们的具体类并不是每个都有一种特殊的方式来存储文件路径或遍历文件，但它们每个都有一种特殊的方式来转换每一行。

乍一看，BaseFileReader似乎是不必要的。然而，它是干净、易于扩展的设计的基础。从中，**我们可以轻松实现不同版本的FileReader，这些FileReader可以专注于他们独特的业务逻辑**。

### 4.2 定义子类

一个自然的实现可能是将文件的内容转换为小写：

```java
public class LowercaseFileReader extends BaseFileReader {

    public LowercaseFileReader(Path filePath) {
        super(filePath);
    }

    @Override
    public String mapFileLine(String line) {
        return line.toLowerCase();
    }
}
```

或者另一个可能是将文件内容转换为大写：

```java
public class UppercaseFileReader extends BaseFileReader {

    public UppercaseFileReader(Path filePath) {
        super(filePath);
    }

    @Override
    public String mapFileLine(String line) {
        return line.toUpperCase();
    }
}
```

从这个简单的例子我们可以看出，**每个子类都可以专注于它独特的行为**，而不需要指定文件读取的其他方面。

### 4.3 使用子类

最后，使用从抽象类继承的类与任何其他具体类没有什么不同：

```java
@Test
public void givenLowercaseFileReaderInstance_whenCalledreadFile_thenCorrect() throws Exception {
    URL location = getClass().getClassLoader().getResource("files/test.txt")
    Path path = Paths.get(location.toURI());
    BaseFileReader lowercaseFileReader = new LowercaseFileReader(path);
        
    assertThat(lowercaseFileReader.readFile()).isInstanceOf(List.class);
}
```

为了简单起见，目标文件位于src/main/resources/files文件夹下。因此，我们使用应用程序类加载器来获取示例文件的路径。请随时查看我们[关于Java类加载器的教程](https://www.baeldung.com/java-classloaders)。

## 5. 总结

在这篇简短的文章中，**我们了解了Java中抽象类的基础知识，以及何时使用它们来实现抽象并将通用实现封装在一个地方**。