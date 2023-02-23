## 1. 概述

在本快速教程中，我们将介绍Java包的基础知识。我们将了解如何创建包并访问我们放置在其中的类。

我们还将讨论命名约定以及它与底层目录结构的关系。

最后，我们将编译并运行我们打包的Java类。

## 2. Java包概述

在Java中，我们使用包来对相关的类、接口和子包进行分组。

这样做的主要好处是：

-   使相关类更容易查找：包通常包含逻辑上相关的类
-   避免命名冲突：包将帮助我们唯一标识一个类；例如，我们可以有一个cn.tuyucheng.taketoday.Application以及com.example.Application类
-   控制访问：我们可以通过组合包和[访问修饰符](https://www.baeldung.com/java-access-modifiers)来对类的可见性和访问

接下来，让我们看看如何创建和使用Java包。

## 3. 创建包

要创建包，我们必须使用package语句，将其添加为文件中的第一行代码。

让我们在名为cn.tuyucheng.taketoday.packages的包中放置一个类：

```java
package cn.tuyucheng.taketoday.packages;
```

强烈建议将每个新类放在一个包中。如果我们定义类但不将它们放在包中，它们将放在默认或未命名的包中。使用默认包有一些缺点：

-   我们失去了包结构的好处，我们不能有子包
-   我们无法从其他包中导入默认包中的类
-   [protected和package-private](https://www.baeldung.com/java-access-modifiers)访问范围将毫无意义 

正如[Java语言规范](https://docs.oracle.com/javase/specs/jls/se14/html/jls-7.html#jls-7.4.2)所述，Java SE平台提供未命名的包主要是为了在开发小型或临时应用程序或刚开始开发时提供方便。

因此，我们应该避免在实际应用中使用未命名或默认的包。

### 3.1 命名约定

为了避免包重名，我们遵循一些命名约定：

-   我们用所有小写字母定义我们的包名称
-   包名称以句点分隔
-   名称也由创建它们的公司或组织决定

要根据组织确定包名称，我们通常会从反转公司URL开始。之后，命名约定由公司定义，可能包括部门名称和项目名称。

例如，要从www.tuyucheng.cn创建一个包，让我们反转它：

```java
cn.tuyucheng
```

然后我们可以进一步定义它的子包，比如cn.tuyucheng.taketoday.packages或cn.tuyucheng.taketoday.packages.domain。

### 3.2 目录结构

Java中的包对应于一个目录结构。

每个包和子包都有自己的目录。因此，对于包cn.tuyucheng.taketoday.packages，我们应该有一个目录结构cn -> tuyucheng -> taketoday -> packages。

大多数IDE将根据我们的包名称帮助创建此目录结构，因此我们不必手动创建这些目录结构。

## 4. 使用包成员

让我们首先在名为domain的子包中定义一个类TodoItem：

```java
package cn.tuyucheng.taketoday.packages.domain;

public class TodoItem {
    private Long id;
    private String description;
    
    // standard getters and setters
}
```

### 4.1 导入

为了从另一个包中的类中使用我们的TodoItem类，我们需要导入它。导入后，我们可以通过名称访问它。

我们可以从包中导入单个类，也可以使用星号导入包中的所有类。

让我们导入整个domain子包：

```java
import cn.tuyucheng.taketoday.packages.domain.*;
```

现在，让我们只导入TodoItem类：

```java
import cn.tuyucheng.taketoday.packages.domain.TodoItem;
```

JDK和其他Java库也有自己的包。我们可以以相同的方式导入我们想要在我们的项目中使用的预先存在的类。

例如，让我们导入Java核心List接口和ArrayList类：

```java
import java.util.ArrayList;
import java.util.List;
```

然后我们可以通过简单地使用它们的名称在我们的应用程序中使用这些类型：

```java
public class TodoList {
    private List<TodoItem> todoItems;

    public void addTodoItem(TodoItem todoItem) {
        if (todoItems == null) {
            todoItems = new ArrayList<TodoItem>();
        }
        todoItems.add(todoItem);
    }
}
```

在这里，我们使用我们的新类和Java核心类来创建ToDoItems列表。

### 4.2 完全限定名称

有时，我们可能会使用来自不同包的两个具有相同名称的类。例如，我们可能同时使用java.sql.Date和java.util.Date。当我们遇到命名冲突时，我们需要为至少一个类使用完全限定的类名。

让我们使用具有完全限定名称的TodoItem ：

```java
public class TodoList {
    private List<cn.tuyucheng.taketoday.packages.domain.TodoItem> todoItems;

    public void addTodoItem(cn.tuyucheng.taketoday.packages.domain.TodoItem todoItem) {
        if (todoItems == null) {
            todoItems = new ArrayList<cn.tuyucheng.taketoday.packages.domain.TodoItem>();
        }
        todoItems.add(todoItem);
    }

    // standard getters and setters
}
```

## 5. 用javac编译

当需要编译我们的打包类时，我们需要记住我们的目录结构。从源文件夹开始，我们需要告诉javac在哪里可以找到我们的文件。

我们需要先编译我们的TodoItem类，因为我们的TodoList类依赖于它。

让我们首先打开命令行或终端并导航到我们的源目录。

现在，让我们编译我们的cn.tuyucheng.taketoday.packages.domain.TodoItem类：

```bash
> javac cn/tuyucheng/taketoday/packages/domain/TodoItem.java
```

如果我们的类编译干净，我们将看不到任何错误消息，并且TodoItem.class文件应该出现在我们的cn/tuyucheng/taketoday/packages/domain目录中。

对于引用其他包中类型的类型，我们应该使用-classpath标志来告诉javac命令在哪里可以找到其他已编译的类。

现在我们的TodoItem类已经编译，我们可以编译我们的 TodoList和TodoApp类：

```bash
>javac -classpath . cn/tuyucheng/taketoday/packages/.java
```

同样，我们应该看不到任何错误消息，并且应该在我们的cn/tuyucheng/taketoday/packages目录中找到两个类文件。

让我们使用TodoApp类的完全限定名称运行我们的应用程序：

```bash
>java cn.tuyucheng.taketoday.packages.TodoApp
```

我们的输出应该是这样的：

![包裹](https://www.baeldung.com/wp-content/uploads/2018/12/packages.jpg)

## 6. 总结

在这篇简短的文章中，我们了解了包是什么以及为什么要使用它们。

我们讨论了命名约定以及包与目录结构的关系。我们还看到了如何创建和使用包。

最后，我们讨论了如何使用javac和java命令编译和运行带有包的应用程序。