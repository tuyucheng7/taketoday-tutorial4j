## 1. 概述

[Thymeleaf](http://www.thymeleaf.org/)是一个用于处理和创建HTML的Java模板引擎。

在本快速教程中，我们将研究Thymeleaf的List实用程序对象以执行常见的基于List的操作。

## 2. 计算大小

首先， size方法返回List的长度。我们可以通过th:text属性包含它：

```html
size: <span th:text="${#lists.size(myList)}"/>
```

myList是我们自己的对象，我们会通过[控制器](https://www.baeldung.com/thymeleaf-in-spring-mvc)传递它：

```java
@GetMapping("/size")
public String usingSize(Model model) {
    model.addAttribute("myList", getColors());
    return "lists/size";
}
```

## 3. 检查List是否为空

如果给定List没有元素，则isEmpty方法返回true：

```html
<span th:text="${#lists.isEmpty(myList)}"/>
```

通常，此实用程序方法与条件一起使用-th:if和th:unless：

```xml
<span th:unless="${#lists.isEmpty(myList)}">List is not empty</span>
```

## 4. 检查成员

contains方法检查一个元素是否是给定List的成员：

```html
myList contains red: <span th:text="${#lists.contains(myList, 'red')}"/>
```

同样，我们可以使用containsAll方法检查多个元素的成员资格：

```html
myList contains red and green: <span th:text='${#lists.containsAll(myList, {"red", "green"})}'/>
```

## 5. 排序

sort方法使我们能够对List进行排序：

```html
sort: <span th:text="${#lists.sort(myList)}"/>

sort with Comparator: <span th:text="${#lists.sort(myList, reverse)}"/>
```

这里我们有两个重载的排序方法。首先，我们按自然顺序对List进行排序-${#lists.sort(myList)}。其次，我们传递了一个Comparator类型的附加参数。在我们的示例中，我们从模型中获取这个比较器。

## 6. 转换为List

最后，我们可以使用toList方法将Iterable和数组转换为List。

```html
<span th:with="convertedList=${#lists.toList(myArray)}">
    converted list size: <span th:text="${#lists.size(convertedList)}"/>
</span>
```

在这里，我们创建了一个新的List convertedList，然后使用#lists.size打印它的大小。

## 7. 总结

在本教程中，我们研究了Thymeleaf内置List实用程序对象以及如何有效地使用它。