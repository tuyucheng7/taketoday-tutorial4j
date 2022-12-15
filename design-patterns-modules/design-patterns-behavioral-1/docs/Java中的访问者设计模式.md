## 1. 概述

在本教程中，我们介绍GoF行为型设计模式之一：访问者。

首先，我们解释它的目的和它试图解决的问题。接下来，我们看一下访问者模式的UML图和实际示例的实现。

## 2. 访问者设计模式

访问者模式的目的是在不对现有对象结构进行修改的情况下定义新操作。

想象一下，我们有一个由组件组成的[复合]()对象，对象的结构是固定的-我们要么不能改变它，要么不打算向结构中添加新类型的元素。

现在，我们如何在不修改现有类的情况下向我们的代码添加新功能？访问者设计模式可能是一个答案。简单地说，**我们要做的就是为结构的每个元素添加一个接收访问者类的函数**。

这样我们的组件将允许访问者实现“访问”它们并对该元素执行任何所需的操作。

换句话说，我们将从类中提取将应用于对象结构的算法。

因此，**我们可以充分利用开放/封闭原则**，因为我们不会修改代码，但我们仍然能够通过提供新的访问者实现来扩展功能。

## 3. UML图

<img src="../assets/img.png">

在上面的UML图中，我们有两个实现层次结构、专用访问者和具体元素。

首先，客户端使用Visitor实现并将其应用于对象结构，复合对象遍历其组件并将访问者应用于每个组件。

现在，**特别相关的是具体元素(ConcreteElementA和ConcreteElementB)正在接收一个访问者，只是允许它访问它们**。

最后，这个方法对于结构中的所有元素都是一样的，它通过将自身(通过this关键字)传递给访问者的访问方法来执行[双重分派](https://en.wikipedia.org/wiki/Double_dispatch)。

## 4. 实现

我们的例子是由JSON和XML具体元素组成的自定义Document对象；这些元素有一个共同的抽象超类，即Element。

文档类：

```java
public class Document extends Element {

    List<Element> elements = new ArrayList<>();

    // ...

    @Override
    public void accept(Visitor v) {
        for (Element e : this.elements) {
            e.accept(v);
        }
    }
}
```

Element类有一个接收Visitor接口的抽象方法 ：

```java
public abstract void accept(Visitor v);
```

因此，在创建新元素时，将其命名为JsonElement，我们必须提供此方法的实现。

然而，由于访问者模式的性质，实现是相同的，所以在大多数情况下，我们需要从其他已经存在的元素中粘贴样板代码：

```java
public class JsonElement extends Element {

    // ...

    public void accept(Visitor v) {
        v.visit(this);
    }
}
```

由于我们的元素允许任何访问者访问它们，假设我们想要处理我们的Document元素，但每个元素都以不同的方式处理，具体取决于其类类型。

因此，我们的访问者将有一个针对给定类型的单独方法：

```java
public class ElementVisitor implements Visitor {

    @Override
    public void visit(XmlElement xe) {
        System.out.println("processing an XML element with uuid: " + xe.uuid);
    }

    @Override
    public void visit(JsonElement je) {
        System.out.println("processing a JSON element with uuid: " + je.uuid);
    }
}
```

在这里，我们的具体访问者实现了两种方法，每种类型的Element对应一种方法。

这使我们能够访问结构的特定对象，我们可以在该对象上执行必要的操作。

## 5. 测试

出于测试目的，让我们看一下VisitorDemo类：

```java
public class VisitorDemo {

    public static void main(String[] args) {
        Visitor v = new ElementVisitor();

        Document d = new Document(generateUuid());
        d.elements.add(new JsonElement(generateUuid()));
        d.elements.add(new JsonElement(generateUuid()));
        d.elements.add(new XmlElement(generateUuid()));

        d.accept(v);
    }

    // ...
}
```

首先，我们创建一个ElementVisitor，它包含我们将应用于元素的算法。

接下来，我们使用适当的组件设置我们的文档，并应用将被对象结构的每个元素接收的访问者。

输出将如下所示：

```shell
processing a JSON element with uuid: fdbc75d0-5067-49df-9567-239f38f01b04
processing a JSON element with uuid: 81e6c856-ddaf-43d5-aec5-8ef977d3745e
processing an XML element with uuid: 091bfcb8-2c68-491a-9308-4ada2687e203
```

它表明访问者已经访问了我们结构的每个元素，根据元素类型，它将处理分派到适当的方法，并且可以从每个底层对象中检索数据。

## 6. 缺点

作为每一种设计模式，即使是访问者也有其缺点，特别是，**如果我们需要向对象的结构中添加新元素，它的使用使得维护代码变得更加困难**。

例如，如果我们添加新的YamlElement，那么我们需要使用处理此元素所需的新方法更新所有现有访问者。更进一步，如果我们有十个或更多的具体访问者，那么更新所有访问者可能会很麻烦。

除此之外，在使用此模式时，与一个特定对象相关的业务逻辑会分散到所有访问者实现中。

## 7. 总结

访问者模式非常适合将算法与其操作的类分开，除此之外，它使添加新操作变得更加容易，只需提供一个新的访问者实现即可。

此外，我们不依赖于组件接口，如果它们不同，那很好，因为我们有一个单独的算法来处理每个具体元素。

此外，访问者最终可以根据它遍历的元素聚合数据。

要查看访问者设计模式的更专业版本，请查看[Java NIO](https://www.baeldung.com/java-nio2-file-visitor)中的访问者模式，这是JDK中对访问者模式实现的典型用例。