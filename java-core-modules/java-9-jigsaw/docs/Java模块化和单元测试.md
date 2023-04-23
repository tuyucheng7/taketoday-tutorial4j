## 1. 概述

在本教程中，我们将探讨Java模块化及其如何影响Java应用程序中的测试。我们将从JPMS的简要介绍开始，然后深入探讨测试如何与模块协同工作。

## 2. Java平台模块化系统

**Java Platform Modularity System，也称为[JPMS](https://www.baeldung.com/java-9-modularity)，在[Java 9](https://www.baeldung.com/new-java-9#Modular)中引入，旨在改进大型应用程序的组织和可维护性**。它提供了一种机制来更有效地定义和管理组件之间的依赖关系。

模块是独立的代码单元，封装了它们的实现并公开了[定义良好的API](https://www.baeldung.com/java-9-module-api)。它们显式声明依赖关系，使理解不同系统部分之间的关系变得更容易。

Java模块化的一些主要好处是：

-   封装：模块隐藏了它们的内部实现细节，并通过定义良好的API仅公开需要的内容
-   提高可维护性：通过明确分离关注点，管理和维护复杂的应用程序变得更加容易
-   增强的性能：模块可以在运行时加载和卸载，允许JVM优化应用程序的内存占用和启动时间

## 3. 简单的类路径测试

在使用模块进行测试之前，让我们考虑在非模块化Java应用程序中进行测试。假设我们有一个包含两个类的简单应用程序，Book和Library：

```powershell
library-core
└── src
    ├── main
    │   └── java
    │       └── cn
    │           └── tuyucheng
                    └── taketoday
    │                   └── library
    │                       └── core
    │                           ├── Book.java
    │                           └── Library.java
    └── test
        └── java
            └── cn
                └── tuyucheng
                    └── taketoday
                        └── library
                            └── core
                                └── LibraryUnitTest.java
```

Library类有一个名为addBook的方法，它接收一个Book并将其添加到内部books列表中。让我们为addBook方法编写一个测试：

```java
class LibraryUnitTest {

    @Test
    void givenEmptyLibrary_whenAddABook_thenLibraryHasOneBook() {
        Library library = new Library();
        Book theLordOfTheRings = new Book("The Lord of the Rings", "J.R.R. Tolkien");
        library.addBook(theLordOfTheRings);
        int expected = 1;
        int actual = library.getBooks().size();
        assertEquals(expected, actual);
    }
}
```

此测试检查将一本书添加到图书馆是否会增加图书数量以及这本书是否在图书馆中。测试的导入非常简单：

```java
package cn.tuyucheng.taketoday.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
```

我们不需要导入Library和Book类，因为JVM将它们视为在同一个包中。**发生这种情况是因为[类路径](https://www.baeldung.com/java-could-not-find-load-main-class#classpath)和类发现过程**。但是，这可能会导致难以调试和修复的问题。在大项目中，它甚至会导致[JAR地狱](https://www.baeldung.com/project-jigsaw-java-modularity#2-jar-hell)。

## 4. 模块化测试

让我们将图书馆管理应用程序拆分为模块化结构。我们将创建一个cn.tuyucheng.taketoday.library.core和一个cn.tuyucheng.taketoday.library.test模块。cn.tuyucheng.taketoday.library.core模块将包含应用程序代码：

```powershell
library-core
└── src
    └── main
        └── java
            ├── cn
            │   └── tuyucheng
            │       └── taketoday
            │           └── library
            │               └── core
            │                   ├── Book.java
            │                   └── Library.java
            └── module-info.java

```

cn.tuyucheng.taketoday.library.test将包含测试代码：

```powershell
library-test
└── src
    └── test
        └── java
            ├── cn
            │   └── tuyucheng
            │       └── taketoday
            │           └── library
            │               └── test
            │                   └── LibraryUnitTest.java
            └── module-info.java
```

为了简单起见，该结构反映了Maven项目的结构，但是在模块化应用程序中，我们只需要遵循JPMS的指南即可。

cn.tuyucheng.taketoday.library.core模块的module-info.java文件如下所示：

```java
module cn.tuyucheng.taketoday.library.core {
    exports cn.tuyucheng.taketoday.library.core;
}
```

cn.tuyucheng.taketoday.library.test的模块描述符将包含一些额外的指令：

```java
module cn.tuyucheng.taketoday.library.test {
    requires cn.tuyucheng.taketoday.library.core;
    requires org.junit.jupiter.api;
    opens cn.tuyucheng.taketoday.library.test to org.junit.platform.commons;
}
```

我们声明cn.tuyucheng.taketoday.library.test模块需要cn.tuyucheng.taketoday.library.core和org.junit.jupiter.api模块进行测试。**此外，我们将cn.tuyucheng.taketoday.library.test包打开到org.junit.platform.commons模块，JUnit需要通过[反射](https://www.baeldung.com/java-illegal-reflective-access#how-to-fix-reflection-illegal-access)访问我们的测试类**。

### 4.1 测试公共方法

现在，让我们使用模块化结构重写上一节的测试：

```java
class LibraryUnitTest {

    @Test
    void givenEmptyLibrary_whenAddABook_thenLibraryHasOneBook() {
        Library library = new Library();
        Book theLordOfTheRings = new Book("The Lord of the Rings", "J.R.R. Tolkien");
        library.addBook(theLordOfTheRings);
        int expected = 1;
        int actual = library.getBooks().size();
        assertEquals(expected, actual);
    }
}
```

我们的测试代码没有改变，但是我们项目的结构改变了。与第一个示例的主要区别在于导入：

```java
package cn.tuyucheng.taketoday.library.test;

import cn.tuyucheng.taketoday.library.core.Book;
import cn.tuyucheng.taketoday.library.core.Library;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
```

我们不使用类路径来发现测试和应用程序代码，并且需要显式导入要在测试中使用的类Book和Library。**从不同的模块导出相同的包是[不可能的](https://openjdk.org/projects/jigsaw/spec/reqs/02)**。这就是为什么测试代码和应用程序核心驻留在不同名称的包中的原因。

### 4.2 测试受保护的方法

**将应用程序和测试代码分离到模块中可能会违反“[不要重复自己](https://www.baeldung.com/cs/dry-software-design-principle)”(DRY)原则**。我们可能需要在测试模块中创建子类或包装类来测试[受保护的成员](https://www.baeldung.com/java-access-modifiers#protected)。这种代码结构复制会导致维护挑战并增加开发时间，因为应用程序代码的更改也必须反映在测试模块中。

让我们考虑一个受保护的方法：

```java
protected void removeBookByAuthor(String author) {
    books.removeIf(book -> book.getAuthor().equals(author));
}
```

我们可以使用继承来扩大对该方法的访问：

```java
public class TestLibrary extends Library {
    @Override
    public void removeBookByAuthor(final String author) {
        super.removeBookByAuthor(author);
    }
}
```

现在，我们可以在我们的测试中使用这个类：

```java
@Test
void givenTheLibraryWithSeveralBook_whenRemoveABookByAuthor_thenLibraryHasNoBooksByTheAuthor() {
    TestLibrary library = new TestLibrary();
    Book theLordOfTheRings = new Book("The Lord of the Rings", "J.R.R. Tolkien");
    Book theHobbit = new Book("The Hobbit", "J.R.R. Tolkien");
    Book theSilmarillion = new Book("The Silmarillion", "J.R.R. Tolkien");
    Book theHungerGames = new Book("The Hunger Games", "Suzanne Collins");
    library.addBook(theLordOfTheRings);
    library.addBook(theHobbit);
    library.addBook(theSilmarillion);
    library.addBook(theHungerGames);
    library.removeBookByAuthor("J.R.R. Tolkien");
    int expected = 1;
    int actual = library.getBooks().size();
    assertEquals(expected, actual);
}

```

### 4.3 测试包私有方法

要访问[包私有](https://www.baeldung.com/java-access-modifiers#default)成员进行测试，我们可能需要通过公共API公开内部实现细节或修改模块描述符以允许访问。

让我们考虑对Library类的removeBook方法的另一个测试：

```java
void removeBook(Book book) {
    books.remove(book);
}
```

该方法是包私有的，只能由同一包中的类访问。当我们在同一个模块中进行测试时，我们不会有任何问题：

```java
package cn.tuyucheng.taketoday.library.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LibraryUnitTest {
    // ...
    @Test
    void givenTheLibraryWithABook_whenRemoveABook_thenLibraryIsEmpty() {
        Library library = new Library();
        Book theLordOfTheRings = new Book("The Lord of the Rings", "J.R.R. Tolkien");
        library.addBook(theLordOfTheRings);
        library.removeBook(theLordOfTheRings);
        int expected = 0;
        int actual = library.getBooks().size();
        assertEquals(expected, actual);
    }
    // ...
}
```

**但是，由于模块系统的可访问性限制，我们可能需要在位于单独模块中的测试中使用反射**。我们应该打开核心模块进行测试才能这样做。这可以通过[两种方式](https://www.baeldung.com/java-illegal-reflective-access#how-to-fix-reflection-illegal-access)完成：在我们的模块描述符中添加一个指令，或者在执行测试时使用–add-opens命令：

```java
module cn.tuyucheng.taketoday.library.core {
    exports cn.tuyucheng.taketoday.library.core;
    opens cn.tuyucheng.taketoday.library.core to cn.tuyucheng.taketoday.library.test;
}
```

当我们使用cn.tuyucheng.taketoday.library.core时，模块描述符中的这些更改始终需要模块路径上的cn.tuyucheng.taketoday.library.test，这并不方便。在执行测试时打开模块的更好解决方案：

```shell
--add-opens cn.tuyucheng.taketoday.library.core/cn.tuyucheng.taketoday.library.core=cn.tuyucheng.taketoday.library.test
```

之后，我们可以编写测试：

```java
package cn.tuyucheng.taketoday.library.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import cn.tuyucheng.taketoday.library.core.Book;
import cn.tuyucheng.taketoday.library.core.Library;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class LibraryUnitTest {

    // ...
    @Test
    void givenTheLibraryWithABook_whenRemoveABook_thenLibraryIsEmpty() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Library library = new Library();
        Book theLordOfTheRings = new Book("The Lord of the Rings", "J.R.R. Tolkien");
        library.addBook(theLordOfTheRings);
        Method removeBook = Library.class.getDeclaredMethod("removeBook", Book.class);
        removeBook.setAccessible(true);
        removeBook.invoke(library, theLordOfTheRings);
        int expected = 0;
        int actual = library.getBooks().size();
        assertEquals(expected, actual);
    }
    // ...
}
```

但是，请记住，这可能会损害系统预期的模块化，并引入其他模块意外使用内部API的风险。

### 4.4 运行JUnit测试

将应用程序和测试代码放在单独的模块中需要额外的工作来设置测试。这很简单，但管理所有参数可能会造成混淆。让我们使用[org.junit.platform.console.ConsoleLauncher](https://www.baeldung.com/junit-run-from-command-line)运行我们的测试：

```shell
java --module-path mods \
--add-modules cn.tuyucheng.taketoday.library.test \
--add-opens cn.tuyucheng.taketoday.library.core/cn.tuyucheng.taketoday.library.core=cn.tuyucheng.taketoday.library.test \
org.junit.platform.console.ConsoleLauncher --select-class cn.tuyucheng.taketoday.library.test.LibraryUnitTest
```

–module-path显示了我们可以在哪里找到我们的测试和核心模块。**但是，这些模块在模块路径上的事实不会将它们添加到模块解析图中**。要包含它，我们应该使用以下命令：

```shell
--add-modules cn.tuyucheng.taketoday.library.test
```

正如我们所讨论的，cn.tuyucheng.taketoday.library.test没有对cn.tuyucheng.taketoday.library.core模块的反射访问权限。我们可以通过模块描述符或在运行测试时直接授予它。在测试的情况下，最好通过命令而不是指令来完成：

```shell
--add-opens cn.tuyucheng.taketoday.library.core/cn.tuyucheng.taketoday.library.core=cn.tuyucheng.taketoday.library.test
```

最后一行标识了包含我们要运行的测试的类：

```shell
org.junit.platform.console.ConsoleLauncher --select-class cn.tuyucheng.taketoday.library.test.LibraryUnitTest
```

这不是我们运行测试的唯一方式， ConsoleLauncher的文档包含有关我们可以使用的标志和参数的信息。

## 5. 在模块外部进行测试

通常，将应用程序代码放在一个模块中并将测试放在外面而不将其放在单独的模块中会很方便。让我们检查一下如何在我们的应用程序中实现这一点。

### 5.1 从类路径运行

最简单的方法是完全忽略module-info.java并使用“好”的旧类路径来运行测试：

```shell
javac --class-path libs/junit-jupiter-engine-5.9.2.jar:\
libs/junit-platform-engine-1.9.2.jar:\
libs/apiguardian-api-1.1.2.jar:\
libs/junit-jupiter-params-5.9.2.jar:\
libs/junit-jupiter-api-5.9.2.jar:\
libs/opentest4j-1.2.0.jar:\
libs/junit-platform-commons-1.9.2.jar \
-d outDir/library-core \
library-core/src/main/java/cn/tuyucheng/taketoday/library/core/Book.java \
library-core/src/main/java/cn/tuyucheng/taketoday/library/core/Library.java \
library-core/src/main/java/cn/tuyucheng/taketoday/library/core/Main.java \
library-core/src/test/java/cn/tuyucheng/taketoday/library/core/LibraryUnitTest.java
```

libs文件夹应包含所需的JUnit依赖项。示例源代码提供了一个脚本来自动下载它们。

然后，我们可以使用ConsoleLauncher从类路径运行测试：

```shell
java --module-path libs \
org.junit.platform.console.ConsoleLauncher \
--classpath ./outDir/library-core \
--select-class cn.tuyucheng.taketoday.library.core.LibraryUnitTest
```

这样，我们完全忽略模块并从类路径运行测试。**虽然对于简单的项目，这种方法工作得很好，但对于模块之间具有复杂关系的应用程序，这种方法可能效果不佳**。此外，这可能会隐藏使用模块运行应用程序时可能出现的问题。

### 5.2 修补模块

解决该问题的更好方法是在运行测试之前用附加类修补模块。这样，我们就可以避免测试和应用程序模块之间交互的复杂设置。

可以在运行模块之前将测试类添加到模块中。此外，我们可以使用相同名称的包进行测试，因此我们可以开箱即用地访问受保护和包私有的成员：

```shell
java --module-path mods:/libs \
--add-modules cn.tuyucheng.taketoday.library.core \
--add-opens cn.tuyucheng.taketoday.library.core/cn.tuyucheng.taketoday.library.core=org.junit.platform.commons \
--add-reads cn.tuyucheng.taketoday.library.core=org.junit.jupiter.api \
--patch-module cn.tuyucheng.taketoday.library.core=outDir/library-test \
--module org.junit.platform.console --select-class cn.tuyucheng.taketoday.library.core.LibraryUnitTest
```

我们之前已经回顾了–add-modules和–add-opens命令。但是，我们在这里使用–add-opens来允许对JUnit进行反射访问。当我们有一个专用的测试模块时，这是通过opens指令完成的。

**指令–add-reads是必需的，因为我们在测试中使用org.junit.jupiter.api模块中的类，并且需要明确声明我们正在使用它**：

```shell
--add-reads cn.tuyucheng.taketoday.library.core=org.junit.jupiter.api
```

关键部分是命令–patch-module：

```shell
--patch-module cn.tuyucheng.taketoday.library.core=outDir/library-test
```

**此命令将已编译的测试类cn.tuyucheng.taketoday.library.core.LibraryUnitTest放入我们的模块中**。在此之后，我们可以直接运行测试而无需额外的模块配置。

## 6. 总结

我们可以使用Java模块化实现更好的组织和可维护性，使我们能够更有效地管理复杂的应用程序。此外，模块之间依赖关系的显式声明有助于我们理解不同系统部分之间的关系，进一步提高代码的整体质量。

但是，**创建单独的测试模块需要额外的设置并且可能会复杂化甚至违反应用程序模块的边界**。这就是为什么通常更容易将测试放在模块外部并使用–patch-module在运行测试时将测试类注入内部。