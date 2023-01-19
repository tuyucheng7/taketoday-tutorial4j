## 一、概述

在本快速教程中，让我们探讨如何 在 Java 中将*String*数组转换为*int数组。*

## 二、问题简介

首先，让我们看一个*String*数组示例：

```java
String[] stringArray = new String[] { "1", "2", "3", "4", "5", "6", "42" };复制
```

我们创建了包含七个字符串的*stringArray 。*现在，我们需要将*stringArray*转换为整数数组：

```java
int[] expected = new int[] { 1, 2, 3, 4, 5, 6, 42 };复制
```

如上面的示例所示，需求非常简单。然而，在现实世界中，字符串数组可能来自不同的来源，例如用户输入或另一个系统。因此，输入数组可能包含一些不是有效数字格式的值，例如：

```java
String[] stringArrayWithInvalidNum = new String[] { "1", "2", "hello", "4", "world", "6", "42" };复制
```

“ *hello”*和*“world”*元素不是有效数字，但其他元素是。通常，当在实际项目中检测到这些类型的值时，我们会遵循特殊的错误处理规则——例如，中止数组转换、将特定整数作为回退等。

在本教程中，**我们将使用 Java 的最小整数作为无效字符串元素的回退**：

```java
int[] expectedWithInvalidInput = new int[] { 1, 2, Integer.MIN_VALUE, 4, Integer.MIN_VALUE, 6, 42 };复制
```

接下来，让我们从包含所有有效元素的字符串数组开始，然后使用错误处理逻辑扩展解决方案。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

## 3.使用流API

[让我们首先使用Stream API](https://www.baeldung.com/java-8-streams)转换包含所有有效元素的字符串数组：

```java
int[] result = Arrays.stream(stringArray).mapToInt(Integer::parseInt).toArray();
assertArrayEquals(expected, result);复制
```

如我们所见，*Arrays.stream()*方法将输入字符串数组转换为*Stream*。然后，**mapToInt \*()\*中间操作将我们的流转换为\*IntStream\* 对象**。

我们使用*Integer.parseInt()*将字符串转换为整数。最后，*toArray()*将*IntStream*对象转换回数组。

那么，接下来，让我们看看无效数字格式场景中的元素。

**假设输入字符串的格式不是有效数字，在这种情况下\*Integer.parseInt()\*方法会抛出*****NumberFormatException**。*

因此，我们需要将*mapToInt()*方法中的[方法引用](https://www.baeldung.com/java-method-references) *Integer::parseInt*替换为lambda表达式，并[处理lambda表达式中的](https://www.baeldung.com/java-lambda-exceptions)[*NumberFormatException*](https://www.baeldung.com/java-lambda-exceptions)[异常](https://www.baeldung.com/java-lambda-exceptions)：

```java
int[] result = Arrays.stream(stringArrayWithInvalidNum).mapToInt(s -> {
    try {
        return Integer.parseInt(s);
    } catch (NumberFormatException ex) {
        // logging ...
        return Integer.MIN_VALUE;
    }
}).toArray();

assertArrayEquals(expectedWithInvalidInput, result);复制
```

然后，如果我们运行测试，它就会通过。

如上面的代码所示，我们只更改了 *mapToInt()*方法中的实现。

值得一提的是，**Java Stream API 在 Java 8 及更高版本上可用**。

## 4. 在循环中实现转换

我们已经了解了 Stream API 如何解决这个问题。但是，如果我们使用的是较旧的 Java 版本，则需要以不同的方式解决问题。

现在我们了解*Integer.parseInt()*完成主要的转换工作，**我们可以遍历数组中的元素并在每个字符串元素上调用\*Integer.parseInt()\***方法：

```java
int[] result = new int[stringArray.length];
for (int i = 0; i < stringArray.length; i++) {
    result[i] = Integer.parseInt(stringArray[i]);
}

assertArrayEquals(expected, result);复制
```

正如我们在上面的实现中看到的，我们首先创建一个与输入字符串数组长度相同的整数数组。然后，我们执行转换并在*for*循环中填充结果数组。

接下来，让我们扩展实现以添加错误处理逻辑。与 Stream API 方法类似，只需**将转换行包裹在\*try-catch\*块中即可解决问题**：

```java
int[] result = new int[stringArrayWithInvalidNum.length];
for (int i = 0; i < stringArrayWithInvalidNum.length; i++) {
    try {
        result[i] = Integer.parseInt(stringArrayWithInvalidNum[i]);
    } catch (NumberFormatException exception) {
        // logging ...
        result[i] = Integer.MIN_VALUE;
    }
}

assertArrayEquals(expectedWithInvalidInput, result);复制
```

如果我们试一试，测试就会通过。

## 5.结论

在本文中，我们通过示例学习了两种将字符串数组转换为整数数组的方法。此外，我们还讨论了在字符串数组包含无效数字格式时处理转换。

如果我们的 Java 版本是 8 或更高版本，Stream API 将是最直接的解决问题的方法。否则，我们可以遍历字符串数组并将每个字符串元素转换为整数。