## 1. 概述

有时，当我们编译Java源文件时，我们会看到Java编译器打印出“unchecked cast”警告消息。

在本教程中，我们将仔细研究警告消息。我们将讨论此警告的含义、我们为何收到警告以及如何解决问题。

默认情况下，某些Java编译器会禁止显示未经检查的警告。

在我们查看这个“unchecked cast”警告之前，让我们确保我们已经[启用了编译器的选项来打印“unchecked”](https://www.baeldung.com/java-unchecked-conversion#enabling-the-unchecked-warning-option)警告。

## 2. “unchecked cast”警告是什么意思？

**“unchecked cast”是一个编译时警告**。简而言之，**在将原始类型转换为参数化类型而不进行类型检查时，我们会看到此警告**。

一个例子可以直接解释它。假设我们有一个简单的方法来返回原始类型Map：

```java
public class UncheckedCast {
    public static Map getRawMap() {
        Map rawMap = new HashMap();
        rawMap.put("date 1", LocalDate.of(2021, Month.FEBRUARY, 10));
        rawMap.put("date 2", LocalDate.of(1992, Month.AUGUST, 8));
        rawMap.put("date 3", LocalDate.of(1976, Month.NOVEMBER, 18));
        return rawMap;
    }
    // ...
}
```

现在，让我们创建一个测试方法来调用上述方法并将结果转换为Map<String, LocalDate>：

```java
@Test
public void givenRawMap_whenCastToTypedMap_shouldHaveCompilerWarning() {
    Map<String, LocalDate> castFromRawMap = (Map<String, LocalDate>) UncheckedCast.getRawMap();
    Assert.assertEquals(3, castFromRawMap.size());
    Assert.assertEquals(castFromRawMap.get("date 2"), LocalDate.of(1992, Month.AUGUST, 8));
}
```

**编译器必须允许此强制转换以保持与不支持泛型的旧Java版本的向后兼容性**。

但是如果我们编译我们的Java源代码，编译器将打印警告消息。接下来，让我们使用Maven编译并运行我们的单元测试：

```shell
$ mvn clean test
...
[WARNING] .../src/test/java/cn/tuyucheng/taketoday/uncheckedcast/UncheckedCastUnitTest.java:[14,97] unchecked cast
  required: java.util.Map<java.lang.String,java.time.LocalDate>
  found:    java.util.Map
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
...
[INFO] Results:
[INFO] 
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
...
```

如Maven输出所示，我们已经成功重现了警告。

另一方面，即使我们看到“unchecked cast”编译器警告，我们的测试也没有任何问题。

我们知道编译器不会无缘无故地警告我们。当我们看到这个警告时，一定有一些潜在的问题。

让我们弄清楚。

## 3. 为什么Java编译器会警告我们？

我们的测试方法在上一节中运行良好，尽管我们看到了“unchecked cast”警告。这是因为当我们将原始类型Map强制转换为Map<String, LocalDate\>时，原始Map仅包含<String, LocalDate\>条目。也就是说，类型转换是安全的。

为了分析潜在的问题，让我们稍微改变一下getRawMap()方法，在原始类型Map中再添加一个条目：

```java
public static Map getRawMapWithMixedTypes() {
    Map rawMap = new HashMap();
    rawMap.put("date 1", LocalDate.of(2021, Month.FEBRUARY, 10));
    rawMap.put("date 2", LocalDate.of(1992, Month.AUGUST, 8));
    rawMap.put("date 3", LocalDate.of(1976, Month.NOVEMBER, 18));
    rawMap.put("date 4", new Date());
    return rawMap;
}
```

这一次，我们在上面的方法中向Map添加了一个类型为<String, Date\>的新条目。

现在，让我们编写一个新的测试方法来调用getRawMapWithMixedTypes()方法：

```java
@Test(expected = ClassCastException.class)
public void givenMixTypedRawMap_whenCastToTypedMap_shouldThrowClassCastException() {
    Map<String, LocalDate> castFromRawMap = (Map<String, LocalDate>) UncheckedCast.getRawMapWithMixedTypes();
    Assert.assertEquals(4, castFromRawMap.size());
    Assert.assertTrue(castFromRawMap.get("date 4").isAfter(castFromRawMap.get("date 3")));
}
```

如果我们编译并运行测试，则会再次打印“unchecked cast”警告消息。此外，我们的测试将通过。

然而，由于我们的测试有expected = ClassCastException.class参数，这意味着测试方法抛出了ClassCastException。

如果我们仔细观察它，**ClassCastException不会在将原始类型Map转换为Map<String, LocalDate\>的行中抛出**，尽管警告消息指向这一行。**相反，当我们通过键获取类型错误的数据时会发生异常：castFromRawMap.get("date 4")**。 

如果我们将包含错误类型数据的原始类型集合转换为参数化类型集合，则**在我们加载错误类型数据之前不会抛出ClassCastException**。

有时，我们可能得到异常为时已晚。

例如，我们通过调用我们的方法得到一个包含许多条目的原始类型Map，然后我们将其转换为具有参数化类型的Map：

```java
(Map<String, LocalDate>) UncheckedCast.getRawMapWithMixedTypes()
```

对于Map中的每个条目，我们需要将LocalDate对象发送到远程API。在我们遇到ClassCastException之前，很可能已经进行了大量的API调用。根据要求，可能涉及一些额外的还原或数据清理过程。

如果我们能早点得到异常就好了，这样我们就可以决定如何处理条目类型错误的情况。

当我们了解“unchecked cast”警告背后的潜在问题时，让我们看看我们可以做些什么来解决这个问题。

## 4. 我们应该如何处理警告？

### 4.1 避免使用原始类型

泛型从Java 5开始引入。如果我们的Java环境支持泛型，我们应该避免使用原始类型。这是因为**使用原始类型会使我们失去泛型的所有安全性和表现力优势**。

此外，我们应该搜索遗留代码并将那些原始类型用法重构为泛型。

但是，有时我们不得不使用一些旧库。来自那些旧外部库的方法可能会返回原始类型集合。

调用这些方法并强制转换为参数化类型将产生“unchecked cast”编译器警告。但是我们无法控制外部库。

接下来，让我们看看如何处理这种情况。

### 4.2 抑制“unchecked”警告

如果我们无法消除“unchecked cast”警告并且我们确定引发警告的代码是类型安全的，我们可以使用[SuppressWarnings("unchecked")](https://www.baeldung.com/java-unchecked-conversion#1-suppressing-the-warning)注解来抑制警告。

**当我们使用@SuppressWarning("unchecked")注解时，我们应该始终将其放在尽可能小的范围内**。

让我们以[ArrayList](https://www.baeldung.com/java-arraylist)类中的remove()方法为例：

```java
public E remove(int index) {
    Objects.checkIndex(index, size);
    final Object[] es = elementData;
                                                              
    @SuppressWarnings("unchecked") E oldValue = (E) es[index];
    fastRemove(es, index);
                                                              
    return oldValue;
}
```

### 4.3 在使用原始类型集合之前进行类型安全检查

正如我们所了解的，@SuppressWarning("unchecked")注解只是抑制警告消息，而没有实际检查转换是否是类型安全的。

如果我们不确定转换原始类型是否是类型安全的，**我们应该[在真正使用数据之前检查类型](https://www.baeldung.com/java-unchecked-conversion#2-checking-type-conversion-before-using-the-raw-type-collection)，以便我们可以更早地得到ClassCastException**。

## 5. 总结

在本文中，我们了解了“unchecked cast”编译器警告的含义。

此外，我们还解决了此警告的原因以及如何解决潜在问题。