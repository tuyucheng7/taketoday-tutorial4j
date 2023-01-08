## 1. 概述

正如我们所知，[toString()](https://www.baeldung.com/java-tostring)方法用于获取Java对象的字符串表示形式。

[Lombok 项目](https://www.baeldung.com/intro-to-project-lombok)可以帮助我们生成一致的字符串表示，而无需样板文件和混乱的源代码。它还可以提高可维护性，尤其是在类可能包含大量字段的情况下。

在本教程中，我们将了解如何自动生成此方法以及可用于进一步微调结果输出的各种配置选项。

## 2.设置

让我们首先在示例项目中包含[Project Lombok](https://search.maven.org/artifact/org.projectlombok/lombok)依赖项：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.22</version>
    <scope>provided</scope>
</dependency>
```

在我们的示例中，我们将使用带有几个字段的简单帐户POJO 类来演示功能和各种配置选项。

## 3. 基本用法

我们可以使用 Lombok @ToString注解来注解任何类。这会修改生成的字节码并创建toString()方法的实现。

让我们将这个注解应用到我们简单的Account POJO：

```java
@ToString
public class Account {

    private String id;

    private String name;

    // standard getters and setters
}
```

默认情况下，@ ToString注解打印类名，以及每个非静态字段名及其通过调用 getter(如果已声明)获得的值。这些字段也根据源类中的声明顺序出现。逗号分隔不同的字段值对。

现在，在此类的实例上调用 toString()方法会生成以下输出：

```bash
Account(id=12345, name=An account)

```

在大多数情况下，这足以生成Java对象的标准且有用的字符串表示形式。

## 4.配置选项

有几个可用的配置选项允许我们修改和调整生成的toString()方法。这些在某些用例中可能会有所帮助。让我们更详细地看一下这些。

### 4.1. 超类toString()

默认情况下，输出不包含来自 toString()方法的超类实现的数据。但是，我们可以通过将callSuper 属性值设置为true来修改它：

```java
@ToString(callSuper = true)
public class SavingAccount extends Account {
    
    private String savingAccountId;

    // standard getters and setters
}
```

这会产生以下输出，超类信息后跟子类字段和值：

```bash
SavingAccount(super=Account(id=12345, name=An account), savingAccountId=6789)
```

重要的是，这只有在我们扩展java.lang.Object以外的类时才真正有用。toString()的Object实现没有提供太多有用的信息。换句话说，包含此数据只会增加冗余信息并增加输出的冗长程度。

### 4.2. 省略字段名称

正如我们之前看到的，默认输出包含字段名称，后跟值。但是，我们可以通过 在@ToString注解中将includeFieldNames属性设置为false来从输出中省略字段名称：

```java
@ToString(includeFieldNames = false)
public class Account {

    private String id;

    private String name;

    // standard getters and setters
}
```

因此，输出现在显示所有字段值的逗号分隔列表，不带字段名称：

```bash
Account(12345, An account)
```

### 4.3. 使用字段而不是获取器

正如我们已经看到的，getter 方法提供用于打印的字段值。此外，如果该类不包含特定字段的 getter 方法，则 Lombok 会直接访问该字段并获取其值。

但是，我们可以通过将doNotUseGetters属性设置为true来将 Lombok 配置为始终使用直接字段值而不是 getter：

```java
@ToString(doNotUseGetters = true)
public class Account {

    private String id;

    private String name;

    // ignored getter
    public String getId() {
        return "this is the id:" + id;
    }

    // standard getters and setters
}
```

如果没有此属性，我们将通过调用 getter 获得输出：

```bash
Account(id=this is the id:12345, name=An account)
```

相反，使用doNotUseGetters属性，输出实际上显示id字段的值，而不调用 getter：

```bash
Account(id=12345, name=An account)
```

### 4.4. 字段包含和排除

假设我们想从字符串表示中排除某些字段，例如密码、其他敏感信息或大型 JSON 结构。我们可以简单地通过使用@ToString.Exclude annotation对它们进行注解来省略这些字段。

让我们从表示中排除名称字段：

```java
@ToString
public class Account {

    private String id;

    @ToString.Exclude
    private String name;

    // standard getters and setters
}
```

或者，我们可以仅指定输出中需要的字段。让我们通过在类级别使用@ToString(onlyExplicitlyIncluded = true) 然后用@ToString.Include注解每个必填字段来完成此操作：

```java
@ToString(onlyExplicitlyIncluded = true)
public class Account {

    @ToString.Include
    private String id;

    private String name;

    // standard getters and setters
}
```

上述两种方法都会产生以下仅包含id字段的输出：

```bash
Account(id=12345)
```

此外， Lombok 输出会自动排除任何以$符号开头的变量。但是，我们可以覆盖此行为并通过在字段级别添加@ToString.Include注解来包含它们。

### 4.5. 排序输出

默认情况下，输出包含根据类中的声明顺序的字段。但是，我们可以简单地通过将 rank 属性添加到@ToString.Include注解来调整顺序。

让我们修改我们的Account类，以便id字段在任何其他字段之前呈现，而不管类定义中的声明位置如何。我们可以通过将@ToString.Include(rank = 1) 注解添加到id字段来实现：

```java
@ToString
public class Account {

    private String name;

    @ToString.Include(rank = 1)
    private String id;

    // standard getters and setters
}
```

现在，id字段首先呈现在输出中，尽管它在name字段之后声明：

```bash
Account(id=12345, name=An account)
```

输出首先包含较高等级的成员，然后是较低等级的成员。没有rank属性的成员默认rank值为0，相同rank的成员按照声明顺序打印。

### 4.6. 方法输出

除了字段之外，还可以包含不带参数的实例方法的输出。我们可以通过使用@ToString.Include标记无参数实例方法来做到这一点：

```java
@ToString
public class Account {

    private String id;

    private String name;

    @ToString.Include
    String description() {
        return "Account description";
    }

    // standard getters and setters
}
```

这会将描述作为键添加，并将其输出作为值添加到帐户表示中：

```bash
Account(id=12345, name=An account, description=Account description)
```

如果指定的方法名称与字段名称匹配，则该方法优先于该字段。换句话说，输出包含方法调用的结果而不是匹配的字段值。

### 4.7. 修改字段名称

我们可以通过在@ToString.Include注解的name属性中指定不同的值来更改任何字段名称：

```java
@ToString
public class Account {

    @ToString.Include(name = "identification")
    private String id;

    private String name;

    // standard getters and setters
}
```

现在，输出包含注解属性中的替代字段名称而不是实际字段名称：

```bash
Account(identification=12345, name=An account)
```

## 5.打印数组

使用[Arrays.deepToString()](https://www.baeldung.com/java-util-arrays#1tostring-anddeeptostring)方法打印数组。这会将数组元素转换为其相应的字符串表示形式。但是，数组可能包含直接引用或间接循环引用。

为了避免无限递归及其相关的运行时错误，此方法将自身内部对数组的任何循环引用呈现为“[[…]]”。

让我们通过向我们的Account类添加一个Object数组字段来查看：

```java
@ToString
public class Account {

    private String id;

    private Object[] relatedAccounts;

    // standard getters and setters
}
```

relatedAccounts数组现在包含在输出中：

```bash
Account(id=12345, relatedAccounts=[54321, [...]])
```

重要的是，循环引用由deepToString() 方法检测并由 Lombok 适当地呈现，而不会导致任何[StackOverflowError](https://www.baeldung.com/java-stack-overflow-error)。

## 6. 要记住的要点

有几个细节值得一提，它们对于避免意外结果很重要。

如果类中存在任何名为toString()的方法(无论返回类型如何)，Lombok 不会生成其 toString()方法。

不同版本的 Lombok 可能会更改生成方法的输出格式。无论如何，我们应该避免依赖于解析toString()方法输出的代码。所以这应该不是问题。

最后，我们还可以在枚举s上添加此注解。这会产生一种表示，其中枚举值跟在枚举类名称之后，例如AccounType.SAVING。

## 七. 总结

在本文中，我们了解了如何使用 Lombok 注解以最少的工作量和样板生成Java对象的String表示形式。

最初，我们查看了基本用法，这通常足以满足大多数情况。然后，我们介绍了可用于调整和调整生成的输出的各种选项。