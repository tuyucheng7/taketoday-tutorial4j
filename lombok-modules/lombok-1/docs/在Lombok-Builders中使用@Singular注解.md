## 1. 概述

Lombok 库提供了一种简化数据对象的好方法。[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)的关键特性之一是[@Builder注解](https://www.baeldung.com/lombok-builder)，它会自动创建用于创建不可变对象的 Builder 类。但是，使用标准的 Lombok 生成的Builder类在我们的对象中填充集合可能很笨拙。

在本教程中，我们将了解@Singular注解，它可以帮助我们处理数据对象中的集合。正如我们将看到的，它还强制执行良好实践。

## 2. 建造者和收藏品

构建器类使用其简单、流畅的语法可以轻松构建不可变数据对象。让我们看一个用 Lombok 的@Builder注解注解的示例类：

```java
@Getter
@Builder
public class Person {
    private final String givenName;
    private final String additionalName;
    private final String familyName;
    private final List<String> tags;
}
```

我们现在可以使用构建器模式创建Person的实例。请注意，这里的tags属性是一个List。此外，标准的 Lombok @Builder将提供设置此属性的方法，就像设置非列表属性一样：

```java
Person person = Person.builder()
  .givenName("Aaron")
  .additionalName("A")
  .familyName("Aardvark")
  .tags(Arrays.asList("fictional","incidental"))
  .build();
```

这是一种可行但相当笨拙的语法。我们可以像上面那样创建内联集合。或者，我们可以提前申报。无论哪种方式，它都会破坏我们创建对象的流程。这就是@Singular注解派上用场的地方。

### 2.1. 将@Singular注解与List一起使用

让我们向我们的Person对象添加另一个List并用@Singular注解它。这将为我们提供一个带注解的字段和一个未带注解的字段的并排视图。除了一般的tags属性，我们还将向我们的Person添加一个兴趣列表：

```java
@Singular private final List<String> interests;
```

我们现在可以一次建立一个值列表：

```java
Person person = Person.builder()
  .givenName("Aaron")
  .additionalName("A")
  .familyName("Aardvark")
  .interest("history")
  .interest("sport")
  .build();
```

构建器将在内部将每个元素存储在一个列表中，并在我们调用build()时创建适当的集合。

### 2.2. 使用其他集合类型

我们在这里说明了@Singular使用java.util.List，但它也可以应用于其他JavaCollection类。让我们向Person添加更多成员：

```java
@Singular private final Set<String> skills;
@Singular private final Map<String, LocalDate> awards;
```

就Builder而言， Set的行为与List非常相似——我们可以一个接一个地添加元素：

```java
Person person = Person.builder()
  .givenName("Aaron")
  .skill("singing")
  .skill("dancing")
  .build();
```

因为Set不支持重复，所以我们需要注意多次添加相同的元素不会创建多个元素。Builder会从容处理这种情况。我们可以多次添加一个元素，但创建的Set只会出现该元素一次。

Map的处理方式略有不同，Builder公开采用适当类型的键和值的方法：

```java
Person person = Person.builder()
  .givenName("Aaron")
  .award("Singer of the Year", LocalDate.now().minusYears(5))
  .award("Best Dancer", LocalDate.now().minusYears(2))
  .build();
```

正如我们在Set中看到的那样，构建器对重复的Map键很宽容，如果多次分配同一个键，它将使用最后一个值。

## 3、@Singular方法的命名

到目前为止，我们一直依赖@Singular注解中的一点魔法，而没有引起注意。Builder本身提供了一种一次性分配整个集合的方法，该方法使用复数形式——例如“ awards ”。@Singular注解添加的额外方法使用单数形式——例如，“ award ”。

Lombok 足够聪明，可以识别英语中的简单复数词，它们遵循规则模式。到目前为止，在我们使用的所有示例中，它只是删除了最后一个“s”。

它还会知道，对于某些以“es”结尾的单词，要删除最后两个字母。例如，它知道“草”是“草”的单数，而“葡萄”而不是“grap”是“葡萄”的单数。但是，在某些情况下，我们必须给它一些帮助。

让我们构建一个简单的海洋模型，其中包含鱼和海草：

```java
@Getter
@Builder
public class Sea {
    @Singular private final List<String> grasses;
    @Singular private final List<String> fish;
}
```

龙目岛可以处理“草”这个词，但会迷失在“鱼”这个词上。在英语中，单数和复数形式是一样的，这很奇怪。这段代码不会编译，我们会得到一个错误：

```plaintext
Can't singularize this name; please specify the singular explicitly (i.e. @Singular("sheep"))
```

我们可以通过向注解添加一个值以用作单数方法名称来解决问题：

```java
@Singular("oneFish") private final List<String> fish;
```

我们现在可以编译我们的代码并使用Builder：

```java
Sea sea = Sea.builder()
  .grass("Dulse")
  .grass("Kelp")
  .oneFish("Cod")
  .oneFish("Mackerel")
  .build();
```

在这种情况下，我们选择了相当做作的oneFish()，但同样的方法可以用于具有不同复数形式的非标准词。例如，可以使用方法child()提供子列表。

## 4.不变性

我们已经了解了@Singular注解如何帮助我们在 Lombok 中处理集合。除了提供便利和表现力之外，它还可以帮助我们保持代码整洁。

不可变对象被定义为一旦创建就不能修改的对象。不可变性在反应式架构中很重要，例如，因为它允许我们将对象传递给方法并保证没有副作用。Builder 模式最常用作 POJO getter 和 setter 的替代方法，以支持不变性。

当我们的数据对象包含Collection类时，很容易让不可变性有所下滑。基本集合接口——List、Set和Map——都有可变和不可变的实现。如果我们依赖标准的 Lombok 构建器，我们可能会不小心传入一个可变集合，然后修改它：

```java
List<String> tags= new ArrayList();
tags.add("fictional");
tags.add("incidental");
Person person = Person.builder()
  .givenName("Aaron")
  .tags(tags)
  .build();
person.getTags().clear();
person.getTags().add("non-fictional");
person.getTags().add("important");
```

在这个简单的例子中，我们不得不非常努力地工作才犯了这个错误。例如，如果我们使用Arrays.asList()构造变量tags，我们将免费获得一个不可变列表，并且调用add()或clear()将抛出UnsupportedOperationException。

例如，在实际编码中，如果将集合作为参数传入，则更有可能发生错误。但是，很高兴知道使用@Singular，我们可以使用基本Collection接口并在调用build()时获得不可变实例。

## 5.总结

在本教程中，我们了解了 Lombok @Singular注解如何提供一种使用 Builder 模式处理List、Set和Map接口的便捷方式。Builder 模式支持不变性，@Singular为我们提供了一流的支持。