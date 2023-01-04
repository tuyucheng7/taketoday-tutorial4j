## 1. 简介

在本快速教程中，我们将了解如何在Thymeleaf中使用布尔值。

在我们深入细节之前，可以在[这篇](https://www.baeldung.com/thymeleaf-in-spring-mvc)文章中找到Thymeleaf基础知识。

## 2. 将表达式作为布尔值求值

在Thymeleaf中，任何值都可以计算为布尔值。我们有一些值被解释为false：

-   null
-   布尔值false
-   数字0
-   字符\0
-   字符串“false”、“off”和“no”

任何其他值都被评估为true。

## 3. 使用布尔值作为渲染条件

要有条件地呈现HTML元素，我们有两个选项：th:if和th:unless属性。

它们的效果恰恰相反，Thymeleaf将渲染一个带有th:if属性的元素，只有当该属性的值为true时，才会渲染带有th:unless属性的元素，只有当它的值为false时：

```html
<span th:if="${true}">will be rendered</span>
<span th:unless="${true}">won't be rendered</span>
<span th:if="${false}">won't be rendered</span>
<span th:unless="${false}">will be rendered</span>
```

## 4. 逻辑和条件运算符

此外，我们还可以使用Thymeleaf中的三个经典逻辑运算符：

-   and
-   or
-   用关键字not或“!”否定象征

我们可以在变量表达式中使用这些运算符或将多个变量表达式与它们组合：

```html
<span th:if="${isRaining or isCold}">The weather is bad</span>
<span th:if="${isRaining} or ${isCold}">The weather is bad</span>
<span th:if="${isSunny and isWarm}">The weather is good</span>
<span th:if="${isSunny} and ${isWarm}">The weather is good</span>
<span th:if="${not isCold}">It's warm</span>
<span th:if="${!isCold}">It's warm</span>
<span th:if="not ${isCold}">It's warm</span>
<span th:if="!${isCold}">It's warm</span>
```

我们还可以使用条件运算符：if-then、if-then-else和默认运算符。

if-then-else运算符是通常的三元运算符，或者?:运算符：

```html
It's <span th:text="${isCold} ? 'cold' : 'warm'"></span>
```

此外，if-then 运算符是我们没有else部分的简化版本：

```html
<span th:text="${isRaining or isCold} ? 'The weather is bad'"></span>
```

如果第一个操作数不为空，则默认运算符返回第二个操作数，否则返回第二个操作数：

```html
<span th:text="'foo' ?: 'bar'"></span> <!-- foo -->
<span th:text="null ?: 'bar'"></span> <!-- bar -->
<span th:text="0 ?: 'bar'"></span> <!-- 0 -->
<span th:text="1 ?: 'bar'"></span> <!-- 1 -->
```

默认运算符也称为猫王运算符，因为它与猫王的发型非常相似。

请注意，Elvis运算符仅执行空检查，不会将第一个操作数评估为布尔值。

## 5. #bools实用对象

#bools是一个实用对象，默认情况下在表达式中可用，并且有一些方便的方法： 

-   #bools.isTrue(obj)返回参数是否被评估为true
-   #bools.isFalse(obj)返回参数是否被评估为false
-   #bools.xxxIsTrue(collection)使用#bools.isTrue()将参数的元素转换为布尔值，然后将它们收集到相同类型的集合中
-   #bools.xxxIsFalse(collection)使用#bools.isFalse()将参数的元素转换为布尔值，然后将它们收集到相同类型的集合中
-   #bools.xxxAnd(collection)如果参数中的所有元素都被评估为true，则返回true
-   #bools.xxxOr(collection)如果参数中的任何元素被评估为true，则返回true

在上述方法中，xxx可以是array、list或set，具体取决于方法的参数(以及xxxIsTrue()和xxxIsFalse()情况下的返回值)。

## 6. 总结

在本文中，我们了解了Thymeleaf如何将值解释为布尔值，以及我们如何有条件地呈现元素并使用布尔表达式。