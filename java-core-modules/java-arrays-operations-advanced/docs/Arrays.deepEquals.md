## 1. 概述

在本教程中，我们将深入了解Arrays类中的deepEquals方法的细节。我们将看看什么时候应该使用这个方法，我们将通过一些简单的例子。

要了解有关java.util.Arrays类中不同方法的更多信息，请查看我们的[快速指南](https://www.baeldung.com/java-util-arrays)。

## 2. 目的

当我们想要检查两个嵌套或多维数组之间的相等性时，我们应该使用deepEquals方法。此外，当我们想要比较两个由用户定义的对象组成的数组时，我们将在后面看到，我们必须重写equals方法。

现在，让我们了解有关deepEquals方法的更多详细信息。

### 2.1. 句法

我们将从看一下方法签名开始：

```java
public static boolean deepEquals(Object[] a1, Object[] a2)
```

从方法签名中，我们注意到我们不能使用deepEquals来比较原始数据类型的两个一维数组。为此，我们必须将原始数组装箱到其相应的包装器或使用Arrays.equals方法，该方法具有用于原始数组的重载方法。

### 2.2. 执行

通过分析该方法的内部实现，我们可以看出该方法不仅检查了数组的顶层元素，还递归地检查了它的每个子元素。

因此，我们应该避免对具有自引用的数组使用deepEquals方法，因为这会导致java.lang.StackOverflowError。

接下来，让我们看看我们可以从这个方法中得到什么输出。

## 3、输出

Arrays.deepEquals方法返回：

-   如果两个参数是同一个对象(具有相同的引用)则为真
-   如果两个参数都为空则为真
-   如果两个参数中只有一个为null，则为 false 
-   如果数组长度不同则为false
-   如果两个数组都为空则为真
-   如果数组包含相同数量的元素并且每对子元素都非常相等，则为真
-   在其他情况下为假

在下一节中，我们将查看一些代码示例。

## 4.例子

现在是时候开始查看实际的deepEquals方法了。此外，我们将比较deepEquals方法和来自同一个Arrays类的equals方法。

### 4.1. 一维数组

首先，让我们从一个简单的例子开始，比较两个Object类型的一维数组：

```java
    Object[] anArray = new Object[] { "string1", "string2", "string3" };
    Object[] anotherArray = new Object[] { "string1", "string2", "string3" };

    assertTrue(Arrays.equals(anArray, anotherArray));
    assertTrue(Arrays.deepEquals(anArray, anotherArray));
```

我们看到equals和deepEquals方法都返回true。让我们看看如果数组的一个元素为null会发生什么：

```java
    Object[] anArray = new Object[] { "string1", null, "string3" };
    Object[] anotherArray = new Object[] { "string1", null, "string3" };

    assertTrue(Arrays.equals(anArray, anotherArray));
    assertTrue(Arrays.deepEquals(anArray, anotherArray));
```

我们看到两个断言都通过了。因此，我们可以得出总结，当使用deepEquals方法时，输入数组的任何深度都接受空值。

但是让我们再尝试一件事，让我们检查嵌套数组的行为：

```java
    Object[] anArray = new Object[] { "string1", null, new String[] {"nestedString1", "nestedString2" }};
    Object[] anotherArray = new Object[] { "string1", null, new String[] {"nestedString1", "nestedString2" } };

    assertFalse(Arrays.equals(anArray, anotherArray));
    assertTrue(Arrays.deepEquals(anArray, anotherArray));
```

在这里我们发现deepEquals返回true而equals返回false。这是因为deepEquals在遇到数组时会递归调用自己，而 equals 只是比较子数组的引用。

### 4.2. 基本类型的多维数组

接下来，让我们检查使用多维数组的行为。在下一个示例中，这两种方法有不同的输出，强调在比较多维数组时我们应该使用deepEquals而不是equals方法：

```java
    int[][] anArray = { { 1, 2, 3 }, { 4, 5, 6, 9 }, { 7 } };
    int[][] anotherArray = { { 1, 2, 3 }, { 4, 5, 6, 9 }, { 7 } };

    assertFalse(Arrays.equals(anArray, anotherArray));
    assertTrue(Arrays.deepEquals(anArray, anotherArray));
```

### 4.3. 用户定义对象的多维数组

最后，让我们检查deepEquals 和equals 方法在测试用户定义对象的两个多维数组是否相等时的行为：

让我们从创建一个简单的Person类开始：

```java
    class Person {
        private int id;
        private String name;
        private int age;

        // constructor & getters & setters

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Person))
                return false;
            Person person = (Person) obj;
            return id == person.id && name.equals(person.name) && age == person.age;
        }
    }
```

有必要为我们的Person类重写equals方法。否则，默认的equals方法将只比较对象的引用。

另外，让我们考虑到，即使它与我们的示例无关，我们也应该在重写equals方法时始终重写hashCode，这样我们就不会违反他们的[约定](https://www.baeldung.com/java-equals-hashcode-contracts)。

接下来，我们可以比较Person类的两个多维数组：

```java
    Person personArray1[][] = { { new Person(1, "John", 22), new Person(2, "Mike", 23) },
      { new Person(3, "Steve", 27), new Person(4, "Gary", 28) } };
    Person personArray2[][] = { { new Person(1, "John", 22), new Person(2, "Mike", 23) }, 
      { new Person(3, "Steve", 27), new Person(4, "Gary", 28) } };
        
    assertFalse(Arrays.equals(personArray1, personArray2));
    assertTrue(Arrays.deepEquals(personArray1, personArray2));
```

作为递归比较子元素的结果，这两种方法再次有不同的结果。

最后，值得一提的是，Objects.deepEquals方法在使用两个Object数组 调用时，会在内部执行Arrays.deepEquals方法：

```java
    assertTrue(Objects.deepEquals(personArray1, personArray2));
```

## 5.总结

在这个快速教程中，我们了解到当我们想要比较两个嵌套或多维对象数组或原始类型之间的相等性时，我们应该使用Arrays.deepEquals方法。