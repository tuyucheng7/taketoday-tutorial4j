---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java-collection
copyright: java-collection
excerpt: Java Collection
---

## 1. 概述

使用ArrayList时，在列表中搜索元素是标准操作。[contains()](https://www.baeldung.com/find-list-element-java#1-contains)方法让我们知道列表对象是否包含我们要查找的元素。

在本教程中，我们将探讨如何在不区分大小写的情况下在ArrayList<String\>对象中搜索字符串。

## 2. 问题简介

在内部，ArrayList.contains()方法使用equals()方法来确定列表是否具有给定元素。如果ArrayList中的所有元素都是字符串，即在使用ArrayList<String\>时，contains()方法会区分大小写地搜索给定的字符串。让我们通过一个例子来快速理解它。

假设我们有一个包含六个字符串的List对象：

```java
List<String> LANGUAGES = Arrays.asList("Java", "Python", "Kotlin", "Ruby", "Javascript", "Go");
```

当我们检查LANGUAGES是否包含“jAvA”时，contains()方法报告false，因为“jAvA”不等于“Java”：

```java
String searchStr = "jAvA";
boolean result = LANGUAGES.contains(searchStr);
assertFalse(result);
```

在本教程中，让我们学习一些在ArrayList<String\>实例中搜索字符串而不关心大小写的方法。

为简单起见，我们将使用单元测试断言来验证解决方案是否按预期工作。

那么接下来，让我们看看他们的行动。

## 3. 使用Stream API

Java[Stream API](https://www.baeldung.com/java-8-streams)提供了许多方便的接口，使我们能够轻松地将集合作为流处理。它在Java 8及更高版本上可用。

例如，我们可以使用Stream的anyMatch()方法进行不区分大小写的字符串搜索：

```java
String searchStr = "koTliN";
boolean result = LANGUAGES.stream().anyMatch(searchStr::equalsIgnoreCase);
assertTrue(result);
```

正如我们在上面的示例中看到的，我们在LANGUAGES列表中搜索字符串“koTliN”。然后，如果我们运行它，测试就会通过。

值得一提的是，我们传递给anyMatch()方法的searchStr::equalsIgnoreCase是一个[方法引用](https://www.baeldung.com/java-method-references)。将为流中的每个字符串元素调用searchStr.equalsIgnoreCase()方法。

## 4. 创建实用方法

我们已经看到Stream API可以直接解决这个问题。但是，如果我们的Java版本低于8，则无法使用Stream API。在这种情况下，解决该问题的经典方法是创建一个实用方法：

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
}
```

如上面的代码所示，我们在[for循环](https://www.baeldung.com/java-for-loop)中检查给定列表中的每个字符串元素。一旦某个元素不区分大小写地等于searchStr，该方法将立即返回true，而不检查列表中的其他元素。

接下来，让我们创建一个测试来验证它是否按预期工作：

```java
String searchStr = "ruBY";
boolean result = IgnoreCaseSearchUtil.ignoreCaseContains(LANGUAGES, searchStr);
assertTrue(result);
```

这次，我们在列表中搜索字符串“ruBY”。同样，如果我们运行它，测试就会通过。

## 5. 创建ArrayList<String\>的子类

到目前为止，我们已经学习了两种方法来确定ArrayList<String\>对象是否包含忽略大小写的给定字符串。这两种解决方案都非常容易理解。但是，如果我们需要在项目中经常执行此操作，则必须多次调用实用程序方法或Stream API的anyMatch()方法。

如果是这种情况，我们可能想要创建一个特定的ArrayList<String\>类型，它本身支持不区分大小写的contains()方法。

那么接下来，让我们创建一个ArrayList<String\>的子类：

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

```

在上面的代码中我们可以看到，IgnoreCaseStringList类[继承了](https://www.baeldung.com/java-inheritance)ArrayList<String\>。我们创建了两个构造函数来更轻松地初始化IgnoreCaseStringList实例。此外，为了使IgnoreCaseStringList支持不区分大小写的contains()，我们[重写](https://www.baeldung.com/java-override)了contains()方法。实施对我们来说并不陌生。它与我们学习的实用方法非常相似。

接下来，让我们测试IgnoreCaseStringList是否有效：

```java
String searchStr = "pYtHoN";
List<String> ignoreCaseList = new IgnoreCaseStringList(LANGUAGES);
boolean result = ignoreCaseList.contains(searchStr);
assertTrue(result);
```

如我们所见，在初始化IgnoreCaseList实例后，我们可以简单地调用contains()方法来搜索给定的字符串，不区分大小写。当我们执行上面的测试时，它通过了。因此，IgnoreCaseStringList巧妙地完成了这项工作。

值得一提的是，IgnoreCaseList方法带来了另一个好处。它也使containsAll()方法区分大小写。这是因为containsAll()方法是在ArrayList的超类型AbstractCollection类中实现的。此外，它在内部调用contains()方法：

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
}
```

最后我们写一个测试来验证一下：

```java
boolean resultContainAll = ignoreCaseList.containsAll(Arrays.asList("pYtHon", "jAvA", "koTliN", "ruBY"));
assertTrue(resultContainAll);
```

另一方面，如果我们希望Stream API和实用方法方法也支持区分大小写的containsAll()特性，我们必须自己实现它，例如，通过添加另一个实用方法。

## 6.  总结

在本文中，我们探讨了如何在ArrayList<String\>中执行不区分大小写的搜索。我们已经通过示例学习了三种解决问题的方法。
