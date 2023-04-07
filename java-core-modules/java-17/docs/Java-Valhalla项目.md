## **一、概述**

在本文中，我们将了解[Project ](https://wiki.openjdk.java.net/display/valhalla/Main) [Valhalla——](https://wiki.openjdk.java.net/display/valhalla/Main) 它的历史原因、当前的开发状态以及它发布后为日常 Java 开发人员带来的好处。

## 2. Valhalla 项目的动机和原因

Oracle 的 Java 语言架构师 Brian Goetz在他的一次[演讲](https://www.youtube.com/watch?v=A-mxj2vhVAA)中说，Valhalla 项目的主要动机之一是希望使**Java 语言和运行时适应现代硬件**。在构思 Java 语言时（撰写本文时大约 25 年前），**内存获取和算术运算的成本大致相同。**

如今，情况发生了变化，内存提取操作的成本是算术操作的 200 到 1,000 倍。**就语言设计而言，这意味着导致指针提取的间接寻址会对整体性能产生不利影响。**

由于应用程序中的大多数 Java 数据结构都是对象，我们可以将 Java 视为一种指针密集型语言（尽管我们通常不会直接看到或操作它们）。这种基于指针的对象实现用于启用对象标识，对象标识本身用于多态性、可变性和锁定等语言特性。每个对象都默认具有这些功能，无论是否真的需要它们。

遵循导致指针和指向间接的指针的身份链，由于间接具有性能缺陷，一个合乎逻辑的结论是删除那些不需要它们的数据结构。这是值类型发挥作用的地方，但首先，让我们看一下常规类：

## 3. 普通班

目前，在 Java 中，常规类如下所示：

```java
class Point { 
   int x; 
   int y; 
}复制
```

内存布局为：

 

[![点类记忆](https://www.baeldung.com/wp-content/uploads/2019/02/point-class-memory.svg)](https://www.baeldung.com/wp-content/uploads/2019/02/point-class-memory.svg)

 

### 3.1. 使用 Regular 类时间接的负面影响

在下图中，我们演示了在数组中使用常规*Point类时间接寻址的负面影响。*

每个数组槽都有一个指向*Point*对象的堆引用，这在性能和内存方面是昂贵的。因此，当访问一个数组元素时，必须进行间接寻址，从而引入开销。此外， 如果数组很大，访问数组中的特定元素需要大量计算，并可能导致数组性能下降。_ _ _ _ _ 此外，间接离子可以导致内存泄漏，因为不再使用的对象仍然可以保留在数组中并占用内存空间。_ _

 

[![java点值类型数组](https://www.baeldung.com/wp-content/uploads/2019/02/java-point-vaue-type-array.svg)](https://www.baeldung.com/wp-content/uploads/2019/02/java-point-vaue-type-array.svg)

 

为此，Project Valhalla 引入了 Value 类。

## 4.价值类

值类仍然是引用类型，作为常规类并存储在堆空间中。它们隐含地是最终的，包括它们的字段。

不同之处在于 Value 类直接使用其字段值进行编码，从而最大限度地减少了内部对象标头、堆分配和间接寻址的成本。

**这允许 JVM 在一定程度上将值类型扁平化为数组和对象，以及其他值类型****。**

值类型的思想是**表示纯数据聚合**。这伴随着删除常规对象的特征。因此，我们拥有没有身份的纯数据。当然，这意味着我们也失去了可以使用对象标识实现的功能。因此，**平等比较只能基于状态进行。** 因此，我们不能使用表征多态性，也不能使用不可变或不可为空的对象。

*Value Point*类的代码和相应的内存布局如下：

```java
value class Point {
  int x;
  int y;
}复制
```

[![点类记忆](https://www.baeldung.com/wp-content/uploads/2019/02/point-class-memory.svg)](https://www.baeldung.com/wp-content/uploads/2019/02/point-class-memory.svg)

 

 

值类与我们上面看到的常规类具有相似的布局，但通过将一个对象嵌套在另一个对象中以允许在数组和内存中更有效地存储 的方式进行改进。此外，修改列表更有效，因为必须创建新实例，这与必须在同一实例中进行修改的常规对象形成对比。 

尽管如此，仍有改进的余地，因为值类是对象和基元之间的中间地带；当用作字段或数组时，它们存在一些缺点：

-   Value 类型的变量可能为 null，因此我们需要额外的位来编码 null。
-   作为堆引用，Value 对象必须以原子方式进行修改，因此内联 Value 对象是不切实际的。

Valhalla 项目旨在通过引入原始类来解决上述问题。

## 5. 原始类

原始类将提供一些优于值类的性能优势。由于它们像常规原语一样工作，因此它们也存储在堆栈内存中，并且每个原语都属于自己的线程。同样，多个线程不能访问同一个原语，因此后者不需要原子修改（性能密集型）。此外，我们可以将它们的实例视为具有额外实用方法的原始类型的组合。

这是一个原始类型*Point[]*：

[![java点值类型数组值](https://www.baeldung.com/wp-content/uploads/2019/02/java-point-vaue-type-array-values.svg)](https://www.baeldung.com/wp-content/uploads/2019/02/java-point-vaue-type-array-values.svg)

这也使 JVM 能够在堆栈上传递值类型，而不必在堆上分配它们。**最后，这意味着我们得到的数据聚合具有类似于 Java 原语的运行时行为，例如\*int\*或\*float\*。**

但与基元不同的是，值类型可以有方法和字段。我们还可以实现接口并将它们用作通用类型。所以我们可以从两个不同的角度来看值类型：

-   更快的对象
-   用户定义的原语

作为额外的锦上添花，我们可以使用值类型作为泛型类型而无需装箱。这直接将我们引向了 Project Valhalla 的另一个重要特性：专门的泛型，将在本文后面进行解释。

## 6. 基本原语类

目前，从 Java 5 开始，原始值可以存储在 Wrapper 类中，并通过装箱/拆箱呈现为对象。尽管如此，仍有改进的余地；例如，将原始值包装在对象中具有可衡量的运行时成本，而装箱相同的值可能会导致两个对象彼此不相等。

Valhalla 项目旨在将包装类（java.lang.Integer、java.lang.Double 等）迁移到原始类。

正如 JEP 401 中所述，这消除了使用类建模原始值的大部分开销。因此，现在将基本原语视为类类型、获得类的所有功能并将这些类型的许多细节委托给标准库是可行的。

## 7. 增强泛型

当我们想对语言原语使用泛型时，我们目前使用装箱类型，例如*Integer*用于*int*或*Float*用于*float*。这种装箱创建了一个额外的间接层，从而首先破坏了使用基元来提高性能的目的。

因此，我们在现有框架和库中看到许多针对原始类型的专门化，例如*IntStream<T>*或*ToIntFunction<T>*。这样做是为了保持使用基元的性能改进。

因此，增强泛型是为了消除对这些“黑客”的需求。相反，Java 语言努力为几乎所有事物启用泛型类型：对象引用、基元、值类型，甚至可能是*void*。

## 八、结论

我们已经瞥见了 Project Valhalla 将给 Java 语言带来的变化。**两个主要目标是增强性能和减少泄漏抽象。**

通过扁平化对象图和删除间接来解决性能增强问题。**这导致更有效的内存布局和更少的分配和垃圾收集。**

**当用作通用类型时，更好的抽象伴随着具有更相似行为的基元和对象。**

Valhalla 项目的早期原型将值类型引入现有类型系统，代号为[LW1](https://wiki.openjdk.java.net/display/valhalla/LW1)。

我们可以在相应的项目页面和 JEP 上找到有关 Project Valhalla 的更多信息：

-   [瓦尔哈拉计划](https://wiki.openjdk.java.net/display/valhalla/Main)
-   [JEP 401：值对象](https://openjdk.org/jeps/8277163)
-   [JEP 401：原始类](https://openjdk.org/jeps/401)
-   [JEP 402：基本原语类](https://openjdk.org/jeps/402)
-   [JEP 草案：通用泛型](https://openjdk.org/jeps/8261529)
-   [JEP 218：原始类型上的泛型](http://openjdk.java.net/jeps/218)