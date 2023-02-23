## 1. 概述

[Java枚举类型](https://www.baeldung.com/a-guide-to-java-enums)提供了一种语言支持的方式来创建和使用常量值。通过定义一组有限的值，枚举比String或int等常量文字变量更类型安全。

但是，枚举值必须是有效的标识符，我们鼓励按照惯例使用SCREAMING_SNAKE_CASE。

考虑到这些限制，单独的枚举值不适合人类可读的字符串或非字符串值。

在本教程中，我们将使用枚举功能作为Java类来附加我们想要的值。

##延伸阅读：

## [Java枚举指南](https://www.baeldung.com/a-guide-to-java-enums)

一个快速实用的JavaEnum实现使用指南，它是什么，它解决了什么问题以及如何使用它来实现常用的设计模式。

[阅读更多](https://www.baeldung.com/a-guide-to-java-enums)→

## [在Java中迭代枚举值](https://www.baeldung.com/java-enum-iteration)

了解迭代Java枚举的三种简单方法。

[阅读更多](https://www.baeldung.com/java-enum-iteration)→

## [Java构造函数指南](https://www.baeldung.com/java-constructors)

了解有关Java构造函数的基础知识以及一些高级技巧

[阅读更多](https://www.baeldung.com/java-constructors)→

## 2. 使用Java枚举作为类

我们经常创建一个枚举作为一个简单的值列表。例如，这里是元素周期表的前两行作为一个简单的枚举：

```java
public enum Element {
    H, HE, LI, BE, B, C, N, O, F, NE
}
```

使用上面的语法，我们创建了十个名为Element的枚举的静态最终实例。虽然这非常有效，但我们只捕获了元素符号。虽然大写形式适用于Java常量，但这不是我们通常编写符号的方式。

此外，我们还遗漏了元素周期表元素的其他属性，例如名称和原子量。

虽然枚举类型在Java中有特殊的行为，但我们可以像对其他类一样添加构造函数、字段和方法。因此，我们可以增强我们的枚举以包含我们需要的值。

##3.添加构造函数和最终字段

让我们从添加元素名称开始。

我们将使用构造函数将名称设置为最终变量：

```java
public enum Element {
    H("Hydrogen"),
    HE("Helium"),
    // ...
    NE("Neon");

    public final String label;

    private Element(String label) {
        this.label = label;
    }
}
```

首先，我们注意到声明列表中的特殊语法。这就是为枚举类型调用构造函数的方式。尽管对枚举使用new运算符是非法的，但我们可以在声明列表中传递构造函数参数。

然后我们声明一个实例变量label。有几点需要注意。

首先，我们选择了标签标识符而不是名称。尽管可以使用成员字段名称，但让我们选择标签以避免与预定义的Enum.name()方法混淆。

其次，我们的标签字段是最终的。虽然枚举的字段不必是最终的，但在大多数情况下我们不希望我们的标签发生变化。本着枚举值不变的精神，这是有道理的。

最后，标签字段是公共的，所以我们可以直接访问标签：

```java
System.out.println(BE.label);
```

另一方面，该字段可以是私有的，可以使用getLabel()方法访问。为了简洁起见，本文将继续使用public字段样式。

## 4. 定位Java枚举值

Java为所有枚举类型提供了valueOf(String)方法。

因此，我们总是可以根据声明的名称获得一个枚举值：

```java
assertSame(Element.LI, Element.valueOf("LI"));
```

但是，我们可能还想通过标签字段查找枚举值。

为此，我们可以添加一个静态方法：

```java
public static Element valueOfLabel(String label) {
    for (Element e : values()) {
        if (e.label.equals(label)) {
            return e;
        }
    }
    return null;
}
```

静态valueOfLabel()方法迭代元素值，直到找到匹配项。如果未找到匹配项，则返回null。相反，可以抛出异常而不是返回null。

让我们看一个使用我们的valueOfLabel()方法的简单示例：

```java
assertSame(Element.LI, Element.valueOfLabel("Lithium"));
```

## 5. 缓存查找值

我们可以通过使用Map缓存标签来避免迭代枚举值。

为此，我们定义了一个静态最终Map并在类加载时填充它：

```java
public enum Element {

    // ... enum values

    private static final Map<String, Element> BY_LABEL = new HashMap<>();
    
    static {
        for (Element e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

   // ... fields, constructor, methods

    public static Element valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
```

由于被缓存，枚举值只迭代一次，并且简化了valueOfLabel()方法。

作为替代方案，我们可以在valueOfLabel()方法中首次访问缓存时延迟构建缓存。在那种情况下，必须同步地图访问以防止并发问题。

## 6. 附加多个值

Enum构造函数可以接受多个值。

为了说明，让我们将原子序数添加为int并将原子量添加为float：

```java
public enum Element {
    H("Hydrogen", 1, 1.008f),
    HE("Helium", 2, 4.0026f),
    // ...
    NE("Neon", 10, 20.180f);

    private static final Map<String, Element> BY_LABEL = new HashMap<>();
    private static final Map<Integer, Element> BY_ATOMIC_NUMBER = new HashMap<>();
    private static final Map<Float, Element> BY_ATOMIC_WEIGHT = new HashMap<>();
    
    static {
        for (Element e : values()) {
            BY_LABEL.put(e.label, e);
            BY_ATOMIC_NUMBER.put(e.atomicNumber, e);
            BY_ATOMIC_WEIGHT.put(e.atomicWeight, e);
        }
    }

    public final String label;
    public final int atomicNumber;
    public final float atomicWeight;

    private Element(String label, int atomicNumber, float atomicWeight) {
        this.label = label;
        this.atomicNumber = atomicNumber;
        this.atomicWeight = atomicWeight;
    }

    public static Element valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    public static Element valueOfAtomicNumber(int number) {
        return BY_ATOMIC_NUMBER.get(number);
    }

    public static Element valueOfAtomicWeight(float weight) {
        return BY_ATOMIC_WEIGHT.get(weight);
    }
}
```

同样，我们可以向enum添加任何我们想要的值，例如正确的大小写符号，“He”、“Li”和“Be”。

此外，我们可以通过添加执行操作的方法来将计算值添加到我们的枚举中。

## 7. 控制接口

作为向我们的枚举添加字段和方法的结果，我们更改了它的公共接口。因此，我们使用核心Enum name()和valueOf()方法的代码将不知道我们的新字段。

Java语言已经为我们定义了静态valueOf()方法，因此我们无法提供自己的valueOf()实现。

同样，因为Enum.name()方法是最终的，我们也不能覆盖它。

因此，没有实际的方法可以使用标准枚举API来利用我们的额外字段。相反，让我们看看一些不同的方式来公开我们的领域。

### 7.1 覆盖toString()

覆盖toString()可能是覆盖name()的替代方法：

```java
@Override 
public String toString() { 
    return this.label; 
}
```

默认情况下，Enum.toString()返回与Enum.name()相同的值。

### 7.2 实现接口

Java中的枚举类型可以实现接口。虽然这种方法不像EnumAPI那样通用，但接口确实可以帮助我们进行概括。

让我们考虑这个接口：

```java
public interface Labeled {
    String label();
}
```

为了与Enum.name()方法保持一致，我们的label()方法没有get前缀。

并且因为valueOfLabel()方法是静态的，我们没有将它包含在我们的接口中。

最后，我们可以在我们的枚举中实现接口：

```java
public enum Element implements Labeled {

    // ...

    @Override
    public String label() {
        return label;
    }

    // ...
}
```

这种方法的一个好处是Labeled接口可以应用于任何类，而不仅仅是枚举类型。我们现在不再依赖通用的枚举API，而是拥有一个更特定于上下文的API。

## 8. 总结

在本文中，我们探讨了JavaEnum实现的许多特性。通过添加构造函数、字段和方法，我们看到枚举可以做的比文字常量多得多。