## 一、概述

在[Groovy](https://www.baeldung.com/groovy-language)中，我们可以像在[Java](https://www.baeldung.com/java-collections)中一样使用列表。但是，由于它对扩展方法的支持，它附带了更多的东西。

在本教程中，我们将了解 Groovy 在改变、过滤和排序列表方面的表现。

## 2. 创建 Groovy 列表

Groovy 在处理集合时提供了一些有趣的快捷方式，这利用了它对动态类型和文字语法的支持。

让我们首先使用速记语法创建一个包含一些值的列表：

```groovy
def list = [1,2,3]
```

同样，我们可以创建一个空列表：

```groovy
def emptyList = []
```

默认情况下，Groovy 创建java.util.ArrayList的一个实例。

但是，我们也可以指定要创建的列表类型：

```groovy
def linkedList = [1,2,3] as LinkedList
ArrayList arrList = [1,2,3]
```

接下来，列表可用于通过使用构造函数参数来创建其他列表：

```groovy
def copyList = new ArrayList(arrList)
```

我们也可以通过克隆来做到这一点：

```groovy
def cloneList = arrList.clone()
```

请注意，克隆会创建列表的浅表副本。

Groovy 使用“==”运算符比较两个列表中的元素是否相等。

继续前面的示例，将cloneList与arrlist进行比较，结果为true：

```groovy
assertTrue(cloneList == arrList)
```

现在让我们看看如何对列表执行一些常见的操作。

## 3. 从列表中检索项目

我们可以使用文字语法从列表中获取一个项目：

```groovy
def list = ["Hello", "World"]
assertTrue(list[1] == "World")
```

或者我们可以使用get()和getAt()方法：

```groovy
assertTrue(list.get(1) == "World")
assertTrue(list.getAt(1) == "World")
```

我们还可以使用正索引和负索引从列表中获取项目。

当使用负数索引时，列表从右到左读取：

```groovy
assertTrue(list[-1] == "World")
assertTrue(list.getAt(-2) == "Hello")
```

请注意，get()方法不支持负索引。

## 4. 将项目添加到列表

有多种将项目添加到列表的简写方法。

让我们定义一个空列表并向其中添加一些项目：

```groovy
def list = []

list << 1
list.add("Apple")
assertTrue(list == [1, "Apple"])
```

接下来，我们还可以指定放置项目的索引。

此外，如果列表的长度小于指定的索引，Groovy 会添加与差值一样多的空值：

```groovy
list[2] = "Box"
list[4] = true
assertTrue(list == [1, "Apple", "Box", null, true])
```

最后，我们可以使用“+=”运算符将新项目添加到列表中。

与其他方法相比，此运算符创建一个新的列表对象并将其分配给变量列表：

```groovy
def list2 = [1,2]
list += list2
list += 12        
assertTrue(list == [1, 6.0, "Apple", "Box", null, true, 1, 2, 12])
```

## 5.更新列表中的项目

我们可以使用文字语法或set()方法更新列表中的项目：

```groovy
def list =[1, "Apple", 80, "App"]
list[1] = "Box"
list.set(2,90)
assertTrue(list == [1, "Box", 90,  "App"])
```

在此示例中，索引 1 和 2 处的项目更新为新值。

## 6. 从列表中删除项目

我们可以使用remove()方法删除特定索引处的项目：

```groovy
def list = [1,2,3,4,5,5,6,6,7]
list.remove(3)
assertTrue(list == [1,2,3,5,5,6,6,7])
```

我们还可以使用removeElement()方法删除元素。

从列表中删除第一次出现的元素：

```groovy
list.removeElement(5)
assertTrue(list == [1,2,3,5,6,6,7])
```

此外，我们可以使用减号运算符从列表中删除所有出现的元素。

但是，此运算符不会改变基础列表——它返回一个新列表：

```groovy
assertTrue(list - 6 == [1,2,3,5,7])
```

## 7. 迭代列表

Groovy 向现有的 Java Collections API 添加了新方法。

这些方法通过封装样板代码简化了过滤、搜索、排序、聚合等操作。它们还支持广泛的输入，包括闭包和输出数据结构。

让我们首先看看迭代列表的两种方法。

each()方法接受一个闭包，与Java中的foreach()方法非常相似。

Groovy 在每次迭代中传递一个隐式参数it对应于当前元素：

```groovy
def list = [1,"App",3,4]
list.each {println it  2}
```

另一种方法eachWithIndex()提供当前元素之外的当前索引值：

```groovy
list.eachWithIndex{ it, i -> println "$i : $it" }
```

## 8.过滤

过滤是另一个经常在列表上执行的操作，Groovy 提供了许多不同的方法可供选择。

让我们定义一个列表来操作：

```groovy
def filterList = [2,1,3,4,5,6,76]
```

要找到第一个符合条件的对象，我们可以使用find：

```groovy
assertTrue(filterList.find {it > 3} == 4)
```

要查找与条件匹配的所有对象，我们可以使用findAll：

```groovy
assertTrue(filterList.findAll {it > 3} == [4,5,6,76])
```

让我们看另一个例子。

这里我们想要一个包含所有数字元素的列表：

```groovy
assertTrue(filterList.findAll {it instanceof Number} == [2,1,3,4,5,6,76])
```

或者，我们可以使用grep方法来做同样的事情：

```groovy
assertTrue(filterList.grep( Number ) == [2,1,3,4,5,6,76])
```

grep和find方法的区别在于grep可以接受对象或闭包作为参数。

因此，它允许进一步将条件语句减少到最低限度：

```groovy
assertTrue(filterList.grep {it > 6} == [76])
```

此外，grep使用Object#isCase(java.lang.Object)来评估列表中每个元素的条件。

有时，我们可能只对列表中的独特项目感兴趣。为此，我们可以使用两种重载方法。

unique()方法可选地接受一个闭包，并只在底层列表中保留与闭包条件匹配的元素，同时丢弃其他元素。

它默认使用自然排序来确定唯一性：

```groovy
def uniqueList = [1,3,3,4]
uniqueList.unique()
assertTrue(uniqueList == [1,3,4])
```

或者，如果要求不改变底层列表，我们可以使用toUnique()方法：

```groovy
assertTrue(["A", "B", "Ba", "Bat", "Cat"].toUnique {it.size()} == ["A", "Ba", "Bat"])
```

如果我们想检查列表中的某些或所有项目是否满足特定条件，我们可以使用every()和any()方法。

every()方法根据列表中的每个元素评估闭包中的条件。

然后它仅在列表中的所有元素都满足条件时才返回true ：

```groovy
def conditionList = [2,1,3,4,5,6,76]
assertFalse(conditionList.every {it < 6})
```

另一方面，如果列表中的任何元素满足条件，则any()方法返回true ：

```groovy
assertTrue(conditionList.any {it % 2 == 0})
```

## 9.排序

默认情况下，Groovy 根据自然顺序对列表中的项目进行排序：

```groovy
assertTrue([1,2,1,0].sort() == [0,1,1,2])
```

但我们也可以传递一个具有自定义排序逻辑的比较器：

```groovy
Comparator mc = {a,b -> a == b? 0: a < b? 1 : -1}
def list = [1,2,1,0]
list.sort(mc)
assertTrue(list == [2,1,1,0])
```

此外，我们可以使用min()或max()方法来查找最大值或最小值，而无需显式调用sort()：

```groovy
def strList = ["na", "ppp", "as"]
assertTrue(strList.max() == "ppp")
Comparator minc = {a,b -> a == b? 0: a < b? -1 : 1}
def numberList = [3, 2, 0, 7]
assertTrue(numberList.min(minc) == 0)
```

## 10.收集

有时，我们可能希望修改列表中的项目并返回具有更新值的另一个列表。

我们可以使用collect()方法来做到这一点：

```groovy
def list = ["Kay","Henry","Justin","Tom"]
assertTrue(list.collect{"Hi " + it} == ["Hi Kay","Hi Henry","Hi Justin","Hi Tom"])
```

## 11.加入

有时，我们可能需要加入列表中的项目。

为此，我们可以使用join()方法：

```groovy
assertTrue(["One","Two","Three"].join(",") == "One,Two,Three")
```

## 12.总结

在本文中，我们介绍了 Groovy 添加到 Java Collections API 的一些扩展。

我们首先查看字面语法，然后查看它在创建、更新、删除和检索列表中的项目中的用法。

最后，我们了解了 Groovy 对迭代、过滤、搜索、收集、连接和排序列表的支持。