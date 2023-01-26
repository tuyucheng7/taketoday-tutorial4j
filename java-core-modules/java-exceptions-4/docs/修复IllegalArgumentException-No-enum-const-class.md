## 一、概述

在这篇简短的文章中，我们将仔细研究异常“ *IllegalArgumentException: No enum const class* ”。

首先，我们将了解此异常背后的主要原因。然后，我们将看到如何使用实际示例重现它，最后学习如何修复它。

## 2. 原因

在深入细节之前，让我们先了解异常及其堆栈跟踪的含义。

通常，**当我们将非法或不适当的值传递给方法时，会发生****[\*IllegalArgumentException\*](https://www.baeldung.com/java-illegalargumentexception-or-nullpointerexception#illegalargumentexception)**。

“ *No enum const class ”告诉我们在指定的**枚举*类型中没有给定名称的常量。

因此，**此异常的最典型原因通常是使用无效的枚举常量作为方法参数**。

## 3.重现异常

现在我们知道了异常的含义，让我们看看如何使用实际示例重现它。

例如，让我们考虑*Priority*枚举：

```java
public enum Priority {

    HIGH("High"), MEDIUM("Medium"), LOW("Low");

    private String name;

    Priority(String name) {
        this.name = name;
    }

    public String getPriorityName() {
        return name;
    }

}复制
```

如我们所见，我们的枚举有一个私有字段*名称*，表示每个*优先级*常量的名称。

接下来，让我们创建一个[静态方法](https://www.baeldung.com/java-static-methods-use-cases)来帮助我们通过*名称获取**Priority*常量：

```java
public class PriorityUtils {

    public static Priority getByName(String name) {
        return Priority.valueOf(name);
    }

    public static void main(String[] args) {
        System.out.println(getByName("Low"));
    }

}
复制
```

现在，如果我们执行*PriorityUtils*类，我们会得到一个异常：

```xml
Exception in thread "main" java.lang.IllegalArgumentException: No enum constant com.baeldung.exception.noenumconst.Priority.Low
    at java.lang.Enum.valueOf(Enum.java:238)
    at com.baeldung.exception.noenumconst.Priority.valueOf(Priority.java:1)
....复制
```

查看堆栈跟踪，*getByName(String name)*失败并出现异常，因为内置方法[*Enum.valueOf()*](https://docs.oracle.com/javase/7/docs/api/java/lang/Enum.html#valueOf(java.lang.Class, java.lang.String))无法找到具有给定名称“ *Low ”的**优先级*常量。

***Enum.valueOf()\*只接受一个字符串，该字符串必须与用于在 enum 中声明常量的标识符完全匹配**。换句话说，它只接受*HIGH*、 *MEDIUM*和*LOW*作为参数。**由于它不知道\*name\*属性，当我们将值“ \*Low”传递给它时，它会抛出\**IllegalArgumentException\***。

因此，让我们使用测试用例来确认这一点：

```java
@Test
void givenCustomName_whenUsingGetByName_thenThrowIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> PriorityUtils.getByName("Low"));
}复制
```

## 4. **解决方案**

**最简单的解决方案是在将自定义\*名称\*传递给\*Enum.valueOf()\*方法**之前将其转换为大写。

这样，我们确保传递的字符串与大写的常量名称完全匹配。

现在，让我们看看它的实际效果：

```java
public static Priority getByUpperCaseName(String name) {
    if (name == null || name.isEmpty()) {
        return null;
    }

    return Priority.valueOf(name.toUpperCase());
}复制
```

为了避免任何[*NullPointerException*](https://www.baeldung.com/java-illegalargumentexception-or-nullpointerexception#nullpointerexception)或不需要的行为，我们添加了一个检查以确保给定的名称不为*空*且不为空。

最后，让我们添加一些测试用例来确认一切正常：

```java
@Test
void givenCustomName_whenUsingGetByUpperCaseName_thenReturnEnumConstant() {
    assertEquals(Priority.HIGH, PriorityUtils.getByUpperCaseName("High"));
}复制
```

如我们所见，我们使用自定义名称*High成功获得了**Priority.HIGH*。

现在，让我们检查一下当我们传递*null*或空值时会发生什么：

```java
@Test
void givenEmptyName_whenUsingGetByUpperCaseName_thenReturnNull() {
    assertNull(PriorityUtils.getByUpperCaseName(""));
}

@Test
void givenNull_whenUsingGetByUpperCaseName_thenReturnNull() {
    assertNull(PriorityUtils.getByUpperCaseName(null));
}复制
```

正如我们在上面看到的，该方法确实返回*null 。*

## 5.结论

在这个简短的教程中，我们详细讨论了导致 Java 抛出异常“ *IllegalArgumentException: No enum const class* ”的原因。

在此过程中，我们学习了如何产生异常以及如何使用实际示例修复它。