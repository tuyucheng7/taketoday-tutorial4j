## 1. 概述

在本快速教程中，我们将学习几种在Thymeleaf中有条件地添加CSS类的不同方法。

如果你不熟悉Thymeleaf，我们建议你查看[我们的介绍](https://www.baeldung.com/thymeleaf-in-spring-mvc)。

## 2. 使用th:if

假设我们的目标是生成一个<span\>，其类由服务器确定：

```html
<span class="base condition-true">
   I have two classes: "base" and either "condition-true" or "condition-false" depending on a server-side condition.
</span>
```

在提供此HTML之前，我们需要一种让服务器评估条件的好方法，包括condition-true类或condition-false类，以及一个基类。

在模板化HTML时，需要为动态行为添加一些条件逻辑是很常见的。

首先，让我们使用th:if来演示最简单的条件逻辑形式：

```html
<span th:if="${condition}" class="base condition-true">
   ThisHTMLis duplicated. We probably want a better solution.
</span>
<span th:if="${!condition}" class="base condition-false">
   ThisHTMLis duplicated. We probably want a better solution.
</span>
```

我们可以在这里看到，这将在逻辑上导致将正确的CSS类附加到我们的HTML元素，但该解决方案违反了[DRY原则](https://www.baeldung.com/java-clean-code)，因为它需要整个代码块。

使用th:if在某些情况下当然有用，但我们应该寻找另一种动态附加CSS类的方法。

## 3. 使用th:attr

Thymeleaf为我们提供了一个属性，可以让我们定义其他属性，称为th:attr。

让我们用它来解决我们的问题：

```html
<span th:attr="class=${condition ? 'base condition-true' : 'base condition-false'}">
   ThisHTMLis consolidated, which is good, but the Thymeleaf attribute still has some redundancy in it.
</span>
```

你可能已经注意到基类仍然是重复的。此外，还有一个更具体的Thymeleaf属性，我们可以在定义类时使用。

## 4. 使用th:class

th:class属性是th:attr="class=..."的快捷方式，所以让我们使用它，同时将基类从条件结果中分离出来：

```html
<span th:class="'base '+${condition ? 'condition-true' : 'condition-false'}">
   The base CSS class still has to be appended with String concatenation. We can do a little bit better.
</span>
```

这个解决方案非常好，因为它满足了我们的需求并且是DRY的。然而，我们仍然可以从Thymeleaf的另一个属性中获益。

## 5. 使用th:classappend

将我们的基类与条件逻辑完全分离不是很好吗？我们可以静态定义我们的基类并将条件逻辑减少到仅相关部分：

```html
<span class="base" th:classappend="${condition ? 'condition-true' : 'condition-false'}">
   This HTML is consolidated, and the conditional class is appended separately from the static base class.
</span>
```

## 6. 总结

通过我们的Thymeleaf代码的每次迭代，我们了解了一种有用的条件技术，以后可能会派上用场。最终，我们发现使用th:classappend为我们提供了DRY代码和关注点分离的最佳组合，同时也满足了我们的目标。