## 一、概述

*使用ArrayList*时，在列表中搜索元素是标准操作。*[contains()](https://www.baeldung.com/find-list-element-java#1-contains)* 方法让我们知道列表对象是否包含我们要查找的元素。

在本教程中，我们将探讨如何在不区分大小写的情况下在*ArrayList<String>对象中搜索字符串。*

## 二、问题简介

在内部，*ArrayList.contains()*方法使用*equals()*方法来确定列表是否具有给定元素。*如果ArrayList*中的所有元素都是字符串，即**在使用\*ArrayList<String>\*时，\*contains()\*方法会区分大小写地搜索给定的字符串**。让我们通过一个例子来快速理解它。

假设我们有一个包含六个字符串的*List对象：*

```java
List<String> LANGUAGES = Arrays.asList("Java", "Python", "Kotlin", "Ruby", "Javascript", "Go");复制
```

当我们检查*LANGUAGES*是否包含*“jAvA”*时， *contains()*方法报告*false*，因为*“jAvA”*不等于*“Java”*：

```java
String searchStr = "jAvA";
boolean result = LANGUAGES.contains(searchStr);
assertFalse(result);复制
```

*在本教程中，让我们学习一些在ArrayList<String>*实例中搜索字符串而不关心大小写的方法。

为简单起见，我们将使用单元测试断言来验证解决方案是否按预期工作。

那么接下来，让我们看看他们的行动。

## 3.使用流API

Java [Stream API](https://www.baeldung.com/java-8-streams)提供了许多方便的接口，使我们能够轻松地将集合作为流处理。**它在 Java 8 及更高版本上可用**。

例如，**我们可以使用\*Stream\*的\*anyMatch()\*方法进行不区分大小写的字符串搜索：**

```java
String searchStr = "koTliN";
boolean result = LANGUAGES.stream().anyMatch(searchStr::equalsIgnoreCase);
assertTrue(result);复制
```

正如我们在上面的示例中看到的，我们在*LANGUAGES*列表中搜索字符串“ *koTliN* ” 。然后，如果我们运行它，测试就会通过。

值得一提的是，我们传递给 *anyMatch()方法的**searchStr::equalsIgnoreCase*是一个[方法引用](https://www.baeldung.com/java-method-references)。将为流中的每个字符串元素调用*searchStr.equalsIgnoreCase()*方法。

## 4. 创建实用方法

我们已经看到 Stream API 可以直接解决这个问题。但是，如果我们的 Java 版本低于 8，则无法使用 Stream API。在这种情况下，解决该问题的经典方法是创建一个实用方法：

```java
public class IgnoreCaseSearchUtil {
    public static boolean ignoreCaseContains(List<String> theList, String searchStr) {
        for (String s : theList) {
            if (searchStr.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}复制
```

如上面的代码所示，我们在[for 循环](https://www.baeldung.com/java-for-loop)中检查给定列表中的每个字符串元素。**一旦某个元素 不区分大小写地 等于\*searchStr ，该方法将立即返回\**true\*，而不检查列表中的其他元素。**

接下来，让我们创建一个测试来验证它是否按预期工作：

```java
String searchStr = "ruBY";
boolean result = IgnoreCaseSearchUtil.ignoreCaseContains(LANGUAGES, searchStr);
assertTrue(result);复制
```

这次，我们在列表中搜索字符串“ *ruBY* ”。同样，如果我们运行它，测试就会通过。

## 5. 创建*ArrayList<String>的子类*

到目前为止，我们已经学习了两种方法来确定*ArrayList<String>*对象是否包含忽略大小写的给定字符串。这两种解决方案都非常容易理解。但是，如果我们需要在项目中经常执行此操作，则必须多次调用实用程序方法或 Stream API 的*anyMatch()方法。*

如果是这种情况，我们可能想要创建一个特定的*ArrayList<String>*类型，它本身支持不区分大小写的*contains()*方法。

那么接下来，让我们创建一个*ArrayList<String>*的子类：

```java
public class IgnoreCaseStringList extends ArrayList<String> {

    public IgnoreCaseStringList() {

    }

    public IgnoreCaseStringList(Collection<? extends String> c) {
        super(c);
    }

    @Override
    public boolean contains(Object o) {
        String searchStr = (String) o;
        for (String s : this) {
            if (searchStr.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

}
复制
```

在上面的代码中我们可以看到，*IgnoreCaseStringList*类[继承了](https://www.baeldung.com/java-inheritance) *ArrayList<String>。* **我们创建了两个构造函数来更轻松地初始化\*IgnoreCaseStringList\*实例**。此外，为了使*IgnoreCaseStringList* 支持不区分大小写的*contains()*，我们[重写](https://www.baeldung.com/java-override)了 *contains()*方法。实施对我们来说并不陌生。它与我们学习的实用方法非常相似。

接下来，让我们测试*IgnoreCaseStringList*是否有效：

```java
String searchStr = "pYtHoN";
List<String> ignoreCaseList = new IgnoreCaseStringList(LANGUAGES);
boolean result = ignoreCaseList.contains(searchStr);
assertTrue(result);复制
```

如我们所见，在初始化*IgnoreCaseList*实例后，我们可以简单地调用*contains()*方法来搜索给定的字符串，不区分大小写。当我们执行上面的测试时，它通过了。因此，*IgnoreCaseStringList*巧妙地完成了这项工作。

值得一提的是，*IgnoreCaseList*方法带来了另一个好处。**它也使 \*containsAll()\*方法区分大小写**。这是因为 *containsAll()*方法是在*ArrayList*的超类型*AbstractCollection*类中实现的。此外，它在内部调用*contains()*方法：

```java
public boolean containsAll(Collection<?> c) {
    Iterator var2 = c.iterator();
    Object e;
    do {
        if (!var2.hasNext()) {
            return true;
        }
        e = var2.next();
    } while(this.contains(e));
    return false;
}复制
```

最后我们写一个测试来验证一下：

```java
boolean resultContainAll = ignoreCaseList.containsAll(Arrays.asList("pYtHon", "jAvA", "koTliN", "ruBY"));
assertTrue(resultContainAll);复制
```

另一方面，如果我们希望 Stream API 和实用方法方法也支持区分大小写的*containsAll()*特性，我们必须自己实现它，例如，通过添加另一个实用方法。

## 六，结论

*在本文中，我们探讨了如何在ArrayList<String>*中执行不区分大小写的搜索。我们已经通过示例学习了三种解决问题的方法。