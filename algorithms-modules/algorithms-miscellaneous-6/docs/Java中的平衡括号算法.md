## 1. 概述

Balanced Brackets，也称为Balanced Parentheses，是一个常见的编程问题。

在本教程中，我们将验证给定字符串中的括号是否平衡。

这种类型的字符串是所谓的[Dyck 语言](https://en.wikipedia.org/wiki/Dyck_language)的一部分。

## 2.问题陈述

括号被认为是以下任何字符 - “(“, “)”, “[“, “]”, “{“, “}”。

如果左括号“(”、“[”和“{”出现在相应的右括号“)”、“]”和“}”的左侧，则认为一组括号是匹配对“， 分别。

但是，如果包含的括号组不匹配，则包含括号对的字符串是不平衡的。

同样，包含非方括号字符如 az、AZ、0-9 或其他特殊字符如#、$、@的字符串也被认为是不平衡的。

例如，如果输入是“{[(])}”，则方括号对“[]”包含一个不平衡的左圆括号“(”。类似地，圆括号对“() ”，包含一个不平衡的右方括号“]”。因此，输入字符串“{[(])}”是不平衡的。

因此，如果满足以下条件，则称包含括号字符的字符串是平衡的：

1.  每个相应的右括号的左侧都会出现一个匹配的左括号
2.  平衡括号内的括号也是平衡的
3.  它不包含任何非括号字符

有几个特殊情况需要记住：null被认为是不平衡的，而空字符串被认为是平衡的。

为了进一步说明我们对平衡括号的定义，让我们看一些平衡括号的例子：

```plaintext
()
[()]
{[()]}
([{{[(())]}}])
```

还有一些不平衡的：

```plaintext
abc[](){}
{{[]()}}}}
{[(])}
```

现在我们对问题有了更好的理解，让我们看看如何解决它！

## 三、解决方法

有不同的方法来解决这个问题。在本教程中，我们将研究两种方法：

1.  使用String类的方法
2.  使用双端队列实现

## 4. 基本设置和验证

让我们首先创建一个方法，如果输入平衡则返回true，如果输入不平衡则返回false：

```java
public boolean isBalanced(String str)
```

让我们考虑输入字符串的基本验证：

1.  如果传递了一个空输入，那么它是不平衡的。
2.  对于要平衡的字符串，成对的左括号和右括号应该匹配。因此，可以肯定地说，长度为奇数的输入字符串不会被平衡，因为它至少包含一个不匹配的括号。
3.  根据问题陈述，应在括号之间检查平衡行为。因此，任何包含非括号字符的输入字符串都是不平衡字符串。

鉴于这些规则，我们可以实施验证：

```java
if (null == str || ((str.length() % 2) != 0)) {
    return false;
} else {
    char[] ch = str.toCharArray();
    for (char c : ch) {
        if (!(c == '{' || c == '[' || c == '(' || c == '}' || c == ']' || c == ')')) {
            return false;
        }
    }
}
```

现在输入字符串已经过验证，我们可以继续解决这个问题。

## 5. 使用String.replaceAll方法

在这种方法中，我们将遍历输入字符串，使用[String.replaceAll](https://www.baeldung.com/java-remove-replace-string-part#string-api)从字符串中删除出现的“()”、“[]”和“{}” 。我们继续这个过程，直到在输入字符串中找不到更多的匹配项。

一旦这个过程完成，如果我们的字符串的长度为零，那么所有匹配的括号对都被删除并且输入字符串是平衡的。但是，如果长度不为零，则字符串中仍然存在一些不匹配的左括号或右括号。因此，输入字符串是不平衡的。

让我们看看完整的实现：

```java
while (str.contains("()") || str.contains("[]") || str.contains("{}")) {
    str = str.replaceAll("()", "")
      .replaceAll("[]", "")
      .replaceAll("{}", "");
}
return (str.length() == 0);
```

## 6. 使用双端队列

[Deque](https://www.baeldung.com/java-queue#3-deques)是 Queue 的一种形式，它在队列的两端提供添加、检索和查看操作。我们将利用[此数据结构的后进先出 (LIFO)](https://www.baeldung.com/java-lifo-thread-safe)顺序功能来检查输入字符串中的余额。

首先，让我们构建我们的双端队列：

```java
Deque<Character> deque = new LinkedList<>();
```

请注意，我们在这里使用了[LinkedList](https://www.baeldung.com/java-linkedlist)，因为它提供了Deque接口的实现。

现在我们的双端队列已经构造好了，我们将一个一个地遍历输入字符串的每个字符。如果字符是左括号，那么我们将把它添加为Deque中的第一个元素：

```java
if (ch == '{' || ch == '[' || ch == '(') { 
    deque.addFirst(ch); 
}
```

但是，如果字符是右括号，那么我们将对LinkedList执行一些检查。

首先，我们检查LinkedList是否为空。空列表意味着右括号不匹配。因此，输入字符串是不平衡的。所以我们返回false。

但是，如果LinkedList不为空，则我们使用peekFirst方法查看其最后一个字符。如果它可以与右括号配对，那么我们使用removeFirst方法从列表中删除这个最上面的字符，然后继续循环的下一次迭代：

```java
if (!deque.isEmpty() 
    && ((deque.peekFirst() == '{' && ch == '}') 
    || (deque.peekFirst() == '[' && ch == ']') 
    || (deque.peekFirst() == '(' && ch == ')'))) { 
    deque.removeFirst(); 
} else { 
    return false; 
}
```

在循环结束时，所有字符都经过余额检查，因此我们可以返回true。下面是基于双端队列的方法的完整实现：

```java
Deque<Character> deque = new LinkedList<>();
for (char ch: str.toCharArray()) {
    if (ch == '{' || ch == '[' || ch == '(') {
        deque.addFirst(ch);
    } else {
        if (!deque.isEmpty()
            && ((deque.peekFirst() == '{' && ch == '}')
            || (deque.peekFirst() == '[' && ch == ']')
            || (deque.peekFirst() == '(' && ch == ')'))) {
            deque.removeFirst();
        } else {
            return false;
        }
    }
}
return deque.isEmpty();
```

## 七. 总结

在本教程中，我们讨论了 Balanced Brackets 的问题陈述并使用两种不同的方法解决了它。