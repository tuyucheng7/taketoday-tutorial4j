## 1. 概述

在本教程中，我们介绍单一职责原则，它是面向对象编程的[SOLID原则](SOLID原则的可靠指南.md)之一。

总的来说，我们深入了解这个原则是什么以及如何在设计我们的软件时实施它。此外，我们会解释此原则何时可能具有误导性。另外在本文中，SRP即单一职责原则。

## 2. 单一职责原则

顾名思义，这一原则指出**每个类都应该只有一个责任，一个单一的目的**。这意味着一个类只会做一项工作，因此我们得出总结，**它应该只有一个改变的理由**。

我们不希望知道太多并且具有不相关行为的对象，这些类更难维护。例如，如果我们有一个类，并且出于不同的原因我们做了很多更改，那么这个类应该被分解成更多的类，每个类处理一个问题。当然，**如果发生错误，它会更容易找到**。

让我们考虑一个包含以某种方式更改文本的代码的类，这个类的唯一工作应该是处理文本。

```java
public class TextManipulator {
    private String text;

    public TextManipulator(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void appendText(String newText) {
        text = text.concat(newText);
    }

    public String findWordAndReplace(String word, String replacementWord) {
        if (text.contains(word)) {
            text = text.replace(word, replacementWord);
        }
        return text;
    }

    public String findWordAndDelete(String word) {
        if (text.contains(word)) {
            text = text.replace(word, "");
        }
        return text;
    }

    public void printText() {
        System.out.println(textManipulator.getText());
    }
}
```

尽管这看起来不错，但它并不是SRP的一个很好的例子。在这里，我们**有两个职责：操作和打印文本**。

在此类中使用打印文本的方法违反了单一职责原则，为此，我们应该创建另一个类，它只处理打印文本：

```java
public class TextPrinter {
    TextManipulator textManipulator;

    public TextPrinter(TextManipulator textManipulator) {
        this.textManipulator = textManipulator;
    }

    public void printText() {
        System.out.println(textManipulator.getText());
    }

    public void printOutEachWordOfText() {
        System.out.println(Arrays.toString(textManipulator.getText().split(" ")));
    }

    public void printRangeOfCharacters(int startingIndex, int endIndex) {
        System.out.println(textManipulator.getText().substring(startingIndex, endIndex));
    }
}
```

现在，在这个类中，我们可以根据需要为打印文本的多种变体创建方法，因为这是它的工作。

## 3. 这个原则怎么会误导人？

**在我们的软件中实施SRP的诀窍是了解每个类的职责**。但是，**每个开发人员对类目的都有自己的看法**，这使得事情变得棘手。由于我们没有关于如何实施此原则的严格说明，因此我们只能对责任是什么做出解释。这意味着有时只有我们作为应用程序的设计者才能决定某些东西是否在类的范围内。

在按照SRP原则编写类的时候，我们要考虑问题领域、业务需求、应用架构。这是非常主观的，这使得实施这一原则比看起来更难，它不会像我们在本教程中的示例那样简单。

## 4. 内聚

遵循SRP原则，我们的类将坚持一种功能。他们的方法和数据将关注一个明确的目的，这意味着[高内聚]()性以及健壮性，它们共同减少了错误。

当基于SRP原则设计软件时，内聚性是必不可少的，因为它可以帮助我们找到类的单一职责。这个概念还可以帮助我们找到具有多个职责的类。

让我们回到我们的TextManipulator类方法：

```java
// ...

public void appendText(String newText) {
    text = text.concat(newText);
}

public String findWordAndReplace(String word, String replacementWord) {
    if (text.contains(word)) {
        text = text.replace(word, replacementWord);
    }
    return text;
}

public String findWordAndDelete(String word) {
    if (text.contains(word)) {
        text = text.replace(word, "");
    }
    return text;
}

// ...
```

在这里，我们清楚地表示了这个类的作用：文本操作。但是，如果我们不考虑内聚性并且我们没有明确定义这个类的职责是什么，我们可以说编写和更新文本是两个不同且独立的工作。在这种想法的引导下，我们可以得出总结，这些应该是两个独立的类：WriteText和UpdateText。

实际上，我们会得到**两个紧密耦合和松散内聚的类**，它们应该几乎总是一起使用。**这三种方法可能执行不同的操作，但它们本质上服务于一个目的**：文本操作。关键是不要想太多。

LCOM是一种有助于在方法中实现高内聚的工具。从本质上讲，L**COM测量类组件之间的联系以及它们之间的关系**。

Martin Hitz和Behzad Montazeri介绍了[LCOM4](https://www.aivosto.com/project/help/pm-oo-cohesion.html)，[Sonarqube](https://www.baeldung.com/sonar-qube)曾对其进行过计量，但此后已弃用。

## 5. 总结

尽管该原则的名称是不言自明的，但错误的原则实施也是很容易的。开发项目时一定要区分各个类的职责，特别注意内聚性。