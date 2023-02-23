## 1. 概述

Java 5中引入的[枚举](https://www.baeldung.com/a-guide-to-java-enums)类型是一种特殊的数据类型，表示一组常量。

使用枚举，我们可以以类型安全的方式定义和使用我们的常量。它为常量带来了编译时检查。此外，它允许我们在switch-case语句中使用常量。

在本教程中，我们将讨论在Java中扩展枚举，包括添加新常量值和新功能。

## 2. 枚举和继承

当我们想要扩展一个Java类时，我们通常会创建一个子类。在Java中，枚举也是类。

在本节中，我们将看看是否可以像继承常规Java类那样继承枚举。

### 2.1 扩展枚举类型

首先，让我们看一个例子，这样我们可以快速理解问题：

```java
public enum BasicStringOperation {
    TRIM("Removing leading and trailing spaces."),
    TO_UPPER("Changing all characters into upper case."),
    REVERSE("Reversing the given string.");

    private String description;

    // constructor and getter
}
```

如上面的代码所示，我们有一个枚举BasicStringOperation，它包含三个基本的字符串操作。

现在假设我们要向枚举添加一些扩展，例如MD5_ENCODE和BASE64_ENCODE。我们可能会想出这个简单的解决方案：

```java
public enum ExtendedStringOperation extends BasicStringOperation {
    MD5_ENCODE("Encoding the given string using the MD5 algorithm."),
    BASE64_ENCODE("Encoding the given string using the BASE64 algorithm.");

    private String description;

    // constructor and getter
}
```

但是，当我们尝试编译该类时，我们会看到编译器错误：

```bash
Cannot inherit from enum BasicStringOperation
```

### 2.2 枚举不允许继承

让我们弄清楚为什么会收到编译器错误。

当我们编译枚举时，Java编译器会对它施展魔法：

- 它将枚举变成抽象类java.lang.Enum的子类
- 它将枚举编译为最终类

例如，如果我们使用[javap](https://www.baeldung.com/java-class-view-bytecode#javap)反汇编已编译的BasicStringOperation枚举，我们将看到它表示为java.lang.Enum<BasicStringOperation>的子类：

```bash
$ javap BasicStringOperation  
public final class extendenum.enums.cn.tuyucheng.taketoday.BasicStringOperation 
    extends java.lang.Enum<extendenum.enums.cn.tuyucheng.taketoday.BasicStringOperation> {
  public static final extendenum.enums.cn.tuyucheng.taketoday.BasicStringOperation TRIM;
  public static final extendenum.enums.cn.tuyucheng.taketoday.BasicStringOperation TO_UPPER;
  public static final extendenum.enums.cn.tuyucheng.taketoday.BasicStringOperation REVERSE;
 ...
}
```

正如我们所知，我们不能继承Java中的最终类。此外，即使我们可以创建ExtendedStringOperation枚举来继承BasicStringOperation，我们的ExtendedStringOperation枚举也会扩展两个类：BasicStringOperation和java.lang.Enum。也就是说会变成多重继承的情况，Java不支持这种情况。

## 3. 用接口模拟可扩展枚举

我们了解到我们无法创建现有枚举的子类。但是，接口是可扩展的。因此，我们可以通过实现接口来模拟可扩展枚举。

### 3.1 模拟扩展常量

为了快速理解这项技术，让我们看看如何模拟扩展我们的BasicStringOperation枚举以进行MD5_ENCODE和BASE64_ENCODE操作。

首先，我们将创建一个接口，StringOperation：

```java
public interface StringOperation {
    String getDescription();
}

```

接下来，我们将使两个枚举都实现上面的接口：

```java
public enum BasicStringOperation implements StringOperation {
    TRIM("Removing leading and trailing spaces."),
    TO_UPPER("Changing all characters into upper case."),
    REVERSE("Reversing the given string.");

    private String description;
    // constructor and getter override
}

public enum ExtendedStringOperation implements StringOperation {
    MD5_ENCODE("Encoding the given string using the MD5 algorithm."),
    BASE64_ENCODE("Encoding the given string using the BASE64 algorithm.");

    private String description;

    // constructor and getter override
}
```

最后，我们将了解如何模拟可扩展的BasicStringOperation枚举。

假设我们的应用程序中有一个方法来获取BasicStringOperation枚举的描述：

```java
public class Application {
    public String getOperationDescription(BasicStringOperation stringOperation) {
        return stringOperation.getDescription();
    }
}

```

现在我们可以将参数类型BasicStringOperation更改为接口类型StringOperation，以使该方法接受来自两个枚举的实例：

```java
public String getOperationDescription(StringOperation stringOperation) {
    return stringOperation.getDescription();
}
```

### 3.2 扩展功能

我们已经演示了如何使用接口模拟枚举的扩展常量。我们还可以向接口添加方法来扩展枚举的功能。

例如，假设我们想要扩展我们的StringOperation枚举，以便每个常量实际上可以将操作应用于给定的字符串：

```java
public class Application {
    public String applyOperation(StringOperation operation, String input) {
        return operation.apply(input);
    }
    //...
}
```

为此，我们将向接口添加apply()方法：

```java
public interface StringOperation {
    String getDescription();
    String apply(String input);
}

```

接下来，我们将让每个StringOperation枚举实现这个方法：

```java
public enum BasicStringOperation implements StringOperation {
    TRIM("Removing leading and trailing spaces.") {
        @Override
        public String apply(String input) { 
            return input.trim(); 
        }
    },
    TO_UPPER("Changing all characters into upper case.") {
        @Override
        public String apply(String input) {
            return input.toUpperCase();
        }
    },
    REVERSE("Reversing the given string.") {
        @Override
        public String apply(String input) {
            return new StringBuilder(input).reverse().toString();
        }
    };

    //...
}

public enum ExtendedStringOperation implements StringOperation {
    MD5_ENCODE("Encoding the given string using the MD5 algorithm.") {
        @Override
        public String apply(String input) {
            return DigestUtils.md5Hex(input);
        }
    },
    BASE64_ENCODE("Encoding the given string using the BASE64 algorithm.") {
        @Override
        public String apply(String input) {
            return new String(new Base64().encode(input.getBytes()));
        }
    };

    //...
}
```

一个测试方法证明这种方法按预期工作：

```java
@Test
public void givenAStringAndOperation_whenApplyOperation_thenGetExpectedResult() {
    String input = " hello";
    String expectedToUpper = " HELLO";
    String expectedReverse = "olleh ";
    String expectedTrim = "hello";
    String expectedBase64 = "IGhlbGxv";
    String expectedMd5 = "292a5af68d31c10e31ad449bd8f51263";
    assertEquals(expectedTrim, app.applyOperation(BasicStringOperation.TRIM, input));
    assertEquals(expectedToUpper, app.applyOperation(BasicStringOperation.TO_UPPER, input));
    assertEquals(expectedReverse, app.applyOperation(BasicStringOperation.REVERSE, input));
    assertEquals(expectedBase64, app.applyOperation(ExtendedStringOperation.BASE64_ENCODE, input));
    assertEquals(expectedMd5, app.applyOperation(ExtendedStringOperation.MD5_ENCODE, input));
}
```

##4.在不改变代码的情况下扩展枚举

我们已经了解了如何通过实现接口来扩展枚举，但有时，我们希望在不修改枚举的情况下扩展其功能。例如，我们可能想要从第三方库扩展一个枚举。

### 4.1 关联枚举常量和接口实现

首先，我们来看一个枚举示例：

```java
public enum ImmutableOperation {
    REMOVE_WHITESPACES, TO_LOWER, INVERT_CASE
}

```

假设枚举来自外部库，因此我们无法更改代码。

现在，在我们的Application类中，我们希望有一个方法将给定的操作应用于输入字符串：

```java
public String applyImmutableOperation(ImmutableOperation operation, String input) {...}
```

由于我们无法更改枚举代码，因此我们可以使用[EnumMap](https://www.baeldung.com/java-enum-map)将枚举常量与所需的实现相关联。

首先，我们将创建一个接口：

```java
public interface Operator {
    String apply(String input);
}

```

然后我们将使用EnumMap<ImmutableOperation,Operator>创建枚举常量和Operator实现之间的映射：

```java
public class Application {
    private static final Map<ImmutableOperation, Operator> OPERATION_MAP;

    static {
        OPERATION_MAP = new EnumMap<>(ImmutableOperation.class);
        OPERATION_MAP.put(ImmutableOperation.TO_LOWER, String::toLowerCase);
        OPERATION_MAP.put(ImmutableOperation.INVERT_CASE, StringUtils::swapCase);
        OPERATION_MAP.put(ImmutableOperation.REMOVE_WHITESPACES, input -> input.replaceAll("s", ""));
    }

    public String applyImmutableOperation(ImmutableOperation operation, String input) {
        return operationMap.get(operation).apply(input);
    }
```

这样，我们的applyImmutableOperation()方法就可以对给定的输入字符串应用相应的操作：

```java
@Test
public void givenAStringAndImmutableOperation_whenApplyOperation_thenGetExpectedResult() {
    String input = " He ll O ";
    String expectedToLower = " he ll o ";
    String expectedRmWhitespace = "HellO";
    String expectedInvertCase = " hE LL o ";
    assertEquals(expectedToLower, app.applyImmutableOperation(ImmutableOperation.TO_LOWER, input));
    assertEquals(expectedRmWhitespace, app.applyImmutableOperation(ImmutableOperation.REMOVE_WHITESPACES, input));
    assertEquals(expectedInvertCase, app.applyImmutableOperation(ImmutableOperation.INVERT_CASE, input));
}
```

### 4.2 验证EnumMap对象

如果枚举来自外部库，我们将不知道它是否被更改，例如向枚举添加新常量。因此，如果我们不更改EnumMap的初始化以包含新的枚举值，那么如果将新添加的枚举常量传递给我们的应用程序，我们的EnumMap方法可能会遇到问题。

为避免这种情况，我们可以在初始化后验证EnumMap以检查它是否包含所有枚举常量：

```java
static {
    OPERATION_MAP = new EnumMap<>(ImmutableOperation.class);
    OPERATION_MAP.put(ImmutableOperation.TO_LOWER, String::toLowerCase);
    OPERATION_MAP.put(ImmutableOperation.INVERT_CASE, StringUtils::swapCase);
    // ImmutableOperation.REMOVE_WHITESPACES is not mapped

    if (Arrays.stream(ImmutableOperation.values()).anyMatch(it -> !OPERATION_MAP.containsKey(it))) {
        throw new IllegalStateException("Unmapped enum constant found!");
    }
}

```

如上面的代码所示，如果未映射来自ImmutableOperation的任何常量，则会抛出IllegalStateException。由于我们的验证是在静态块中进行的，因此IllegalStateException将是ExceptionInInitializerError的原因：

```java
@Test
public void givenUnmappedImmutableOperationValue_whenAppStarts_thenGetException() {
    Throwable throwable = assertThrows(ExceptionInInitializerError.class, () -> {
        ApplicationWithEx appEx = new ApplicationWithEx();
    });
    assertTrue(throwable.getCause() instanceof IllegalStateException);
}

```

因此，一旦应用程序因上述错误和原因无法启动，我们应该仔细检查ImmutableOperation以确保所有常量都已映射。

## 5. 总结

枚举是Java中的一种特殊数据类型。在本文中，我们讨论了为什么枚举不支持继承。然后我们解决了如何使用接口模拟可扩展枚举。我们还学习了如何在不更改枚举的情况下扩展其功能。