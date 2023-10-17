## 1. 概述

在本文中，我们将重点介绍使用Vavr进行模式匹配。如果你不了解Vavr，请先阅读Vavr的概述。

模式匹配是Java本身不具备的功能。可以将其视为switch-case语句的高级形式。

Vavr模式匹配的优势在于，它使我们免于编写大量switch case或if-then-else语句。因此，它减少了代码量并以人类可读的方式表示条件逻辑。

我们可以通过进行以下导入来使用模式匹配API：

```java
import static io.vavr.API.*;
```

## 2. 模式匹配的工作原理

正如我们在上一篇文章中看到的，模式匹配可用于替换开关块：

```java
@Test
public void whenSwitchWorksAsMatcher_thenCorrect() {
    int input = 2;
    String output;
    switch (input) {
    case 0:
        output = "zero";
        break;
    case 1:
        output = "one";
        break;
    case 2:
        output = "two";
        break;
    case 3:
        output = "three";
        break;
    default:
        output = "unknown";
        break;
    }

    assertEquals("two", output);
}
```

或者多个if语句：

```java
@Test
public void whenIfWorksAsMatcher_thenCorrect() {
    int input = 3;
    String output;
    if (input == 0) {
        output = "zero";
    }
    else if (input == 1) {
        output = "one";
    }
    else if (input == 2) {
        output = "two";
    }
    else if (input == 3) {
        output = "three";
    } else {
        output = "unknown";
    }

    assertEquals("three", output);
}
```

到目前为止，我们看到的片段很冗长，因此容易出错。使用模式匹配时，我们使用三个主要构建块：两个静态方法Match、Case和原子模式。

原子模式表示应该评估以返回布尔值的条件：

-   $()：类似于switch语句中默认情况的通配符模式它处理找不到匹配项的情况
-   $(value)：这是等于模式，其中一个值与输入简单地相等比较。
-   $(predicate)：这是条件模式，其中将谓词函数应用于输入，并使用结果布尔值做出决定。

switch和if方法可以用更短更简洁的代码代替，如下所示：

```java
@Test
public void whenMatchworks_thenCorrect() {
    int input = 2;
    String output = Match(input).of(
      	Case($(1), "one"), 
      	Case($(2), "two"), 
      	Case($(3), "three"), 
      	Case($(), "?"));
        
    assertEquals("two", output);
}
```

如果输入没有匹配，则评估通配符模式：

```java
@Test
public void whenMatchesDefault_thenCorrect() {
    int input = 5;
    String output = Match(input).of(
      	Case($(1), "one"), 
      	Case($(), "unknown"));

    assertEquals("unknown", output);
}
```

如果没有通配符模式并且输入没有匹配，我们将得到一个匹配错误：

```java
@Test(expected = MatchError.class)
public void givenNoMatchAndNoDefault_whenThrows_thenCorrect() {
    int input = 5;
    Match(input).of(
      	Case($(1), "one"), 
      	Case($(2), "two"));
}
```

在本节中，我们介绍了Vavr模式匹配的基础知识，接下来的部分将介绍处理我们可能在代码中遇到的不同情况的各种方法。

## 3. 匹配选项

正如我们在上一节中看到的，通配符模式$()匹配未找到输入匹配项的默认情况。

但是，包含通配符模式的另一种替代方法是将匹配操作的返回值包装在Option实例中：

```java
@Test
public void whenMatchWorksWithOption_thenCorrect() {
    int i = 10;
    Option<String> s = Match(i)
      	.option(Case($(0), "zero"));

    assertTrue(s.isEmpty());
    assertEquals("None", s.toString());
}
```

想要更好的了解Vavr中的Option，可以参考介绍文章。

## 4. 匹配内置谓词

Vavr附带了一些内置谓词，使我们的代码更易于阅读。因此，我们可以使用谓词进一步改进我们的初始示例：

```java
@Test
public void whenMatchWorksWithPredicate_thenCorrect() {
    int i = 3;
    String s = Match(i).of(
      	Case($(is(1)), "one"), 
      	Case($(is(2)), "two"), 
      	Case($(is(3)), "three"),
      	Case($(), "?"));

    assertEquals("three", s);
}
```

Vavr提供了比这更多的谓词。例如，我们可以让我们的条件检查输入的类别：

```java
@Test
public void givenInput_whenMatchesClass_thenCorrect() {
    Object obj=5;
    String s = Match(obj).of(
      	Case($(instanceOf(String.class)), "string matched"), 
      	Case($(), "not string"));

    assertEquals("not string", s);
}
```

或者输入是否为空：

```java
@Test
public void givenInput_whenMatchesNull_thenCorrect() {
    Object obj=5;
    String s = Match(obj).of(
      	Case($(isNull()), "no value"), 
      	Case($(isNotNull()), "value found"));

    assertEquals("value found", s);
}
```

我们可以使用contains风格，而不是equals风格的匹配值。这样，我们可以使用isIn谓词检查输入是否存在于值列表中：

```java
@Test
public void givenInput_whenContainsWorks_thenCorrect() {
    int i = 5;
    String s = Match(i).of(
      	Case($(isIn(2, 4, 6, 8)), "Even Single Digit"), 
      	Case($(isIn(1, 3, 5, 7, 9)), "Odd Single Digit"), 
      	Case($(), "Out of range"));

    assertEquals("Odd Single Digit", s);
}
```

我们可以用谓词做更多的事情，比如将多个谓词组合为一个匹配案例。要仅在输入通过给定的一组谓词时才匹配，我们可以使用allOf谓词对谓词进行与运算。

一个实际的例子是我们想要检查一个数字是否包含在列表中，就像我们在前面的例子中所做的那样。问题是该列表也包含空值。所以，我们想要应用一个过滤器，除了拒绝不在列表中的数字外，还会拒绝空值：

```java
@Test
public void givenInput_whenMatchAllWorks_thenCorrect() {
    Integer i = null;
    String s = Match(i).of(
      	Case($(allOf(isNotNull(),isIn(1,2,3,null))), "Number found"), 
      	Case($(), "Not found"));

    assertEquals("Not found", s);
}
```

要在输入匹配任何给定组时进行匹配，我们可以使用anyOf谓词对谓词进行OR。

假设我们按出生年份筛选候选人，并且我们只想要出生于1990、1991或1992年的候选人。

如果找不到这样的候选人，那么我们只能接受1986年出生的人，我们也想在我们的代码中明确这一点：

```java
@Test
public void givenInput_whenMatchesAnyOfWorks_thenCorrect() {
    Integer year = 1990;
    String s = Match(year).of(
      	Case($(anyOf(isIn(1990, 1991, 1992), is(1986))), "Age match"), 
      	Case($(), "No age match"));
    assertEquals("Age match", s);
}
```

最后，我们可以使用noneOf方法确保没有提供的谓词匹配。

为了证明这一点，我们可以否定前面示例中的条件，这样我们就可以得到不在上述年龄组中的候选人：

```java
@Test
public void givenInput_whenMatchesNoneOfWorks_thenCorrect() {
    Integer year = 1990;
    String s = Match(year).of(
      	Case($(noneOf(isIn(1990, 1991, 1992), is(1986))), "Age match"), 
      	Case($(), "No age match"));

    assertEquals("No age match", s);
}
```

## 5. 匹配自定义谓词

在上一节中，我们探讨了Vavr的内置谓词。但Vavr并不止于此。有了lambda的知识，我们可以构建和使用我们自己的谓词，甚至只是将它们写成内联。

有了这些新知识，我们可以在上一节的第一个例子中内联一个谓词，然后像这样重写它：

```java
@Test
public void whenMatchWorksWithCustomPredicate_thenCorrect() {
    int i = 3;
    String s = Match(i).of(
      	Case($(n -> n == 1), "one"), 
      	Case($(n -> n == 2), "two"), 
      	Case($(n -> n == 3), "three"), 
      	Case($(), "?"));
    assertEquals("three", s);
}
```

如果我们需要更多参数，我们还可以应用功能接口代替谓词。contains示例可以像这样重写，虽然有点冗长，但它使我们对谓词的作用有更多的权力：

```java
@Test
public void givenInput_whenContainsWorks_thenCorrect2() {
    int i = 5;
    BiFunction<Integer, List<Integer>, Boolean> contains = (t, u) -> u.contains(t);

    String s = Match(i).of(
      	Case($(o -> contains
        	.apply(i, Arrays.asList(2, 4, 6, 8))), "Even Single Digit"), 
      	Case($(o -> contains
        	.apply(i, Arrays.asList(1, 3, 5, 7, 9))), "Odd Single Digit"), 
      	Case($(), "Out of range"));

    assertEquals("Odd Single Digit", s);
}
```

在上面的示例中，我们创建了一个Java 8 BiFunction，它只检查两个参数之间的isIn关系。

你也可以为此使用Vavr的FunctionN。因此，如果内置谓词不太符合你的要求，或者你想控制整个评估，那么请使用自定义谓词。

## 6. 对象分解

对象分解是将Java对象分解成其组成部分的过程。例如，考虑将员工的生物数据与就业信息一起提取的情况：

```java
public class Employee {

    private String name;
    private String id;

    //standard constructor, getters and setters
}
```

我们可以将Employee的记录分解为其组成部分：name和id。这在Java中非常明显：

```java
@Test
public void givenObject_whenDecomposesJavaWay_thenCorrect() {
    Employee person = new Employee("Carl", "EMP01");

    String result = "not found";
    if (person != null && "Carl".equals(person.getName())) {
        String id = person.getId();
        result="Carl has employee id "+id;
    }

    assertEquals("Carl has employee id EMP01", result);
}
```

我们创建了一个员工对象，然后我们首先检查它是否为null，然后再应用过滤器以确保我们最终得到名称为Carl的员工的记录。然后我们继续检索他的id。Java方式可行，但冗长且容易出错。

在上面的示例中，我们基本上所做的是将我们所知道的与传入的内容进行匹配。我们知道我们需要一个名为Carl的员工，因此我们尝试将此名称与传入对象进行匹配。

然后我们分解他的细节以获得人类可读的输出。空检查只是我们不需要的防御性开销。

使用Vavr的模式匹配API，我们可以忘记不必要的检查，只关注重要的事情，从而产生非常紧凑和可读的代码。

要使用此规定，我们必须在你的项目中安装额外的vavr-match依赖项。你可以通过[此链接](https://search.maven.org/classic/#search|ga|1|vavr-match)获得它。

上面的代码可以写成如下：

```java
@Test
public void givenObject_whenDecomposesVavrWay_thenCorrect() {
    Employee person = new Employee("Carl", "EMP01");

    String result = Match(person).of(
      	Case(Employee($("Carl"), $()),
        	(name, id) -> "Carl has employee id "+id),
      	Case($(),
        	() -> "not found"));
         
    assertEquals("Carl has employee id EMP01", result);
}
```

上面示例中的关键构造是原子模式$("Carl")和$()，值模式分别是通配符模式。我们在[Vavr介绍性文章](https://www.baeldung.com/vavr)中详细讨论了这些。

这两种模式都从匹配的对象中检索值并将它们存储到lambda参数中。值模式$("Carl")只有在检索到的值与其中的内容匹配时才能匹配，即carl。

另一方面，通配符模式$()匹配其位置上的任何值，并将该值检索到id lambda参数中。

为了使这种分解起作用，我们需要定义分解模式或正式称为不应用模式的东西。

这意味着我们必须教会模式匹配API如何分解我们的对象，从而为每个要分解的对象生成一个条目：

```java
@Patterns
class Demo {
	@Unapply
	static Tuple2<String, String> Employee(Employee Employee) {
		return Tuple.of(Employee.getName(), Employee.getId());
	}

	// other unapply patterns
}
```

注解处理工具将生成一个名为DemoPatterns.java的类，我们必须将其静态导入到我们想要应用这些模式的任何地方：

```java
import static cn.tuyucheng.taketoday.vavr.DemoPatterns.*;
```

我们还可以分解内置的Java对象。

例如，java.time.LocalDate可以分解为年月日。让我们将其unapply模式添加到Demo.java：

```java
@Unapply
static Tuple3<Integer, Integer, Integer> LocalDate(LocalDate date) {
    return Tuple.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
}
```

然后是测试：

```java
@Test
public void givenObject_whenDecomposesVavrWay_thenCorrect2() {
    LocalDate date = LocalDate.of(2017, 2, 13);

    String result = Match(date).of(
      	Case(LocalDate($(2016), $(3), $(13)), 
        	() -> "2016-02-13"),
      	Case(LocalDate($(2016), $(), $()),
        	(y, m, d) -> "month " + m + " in 2016"),
      	Case(LocalDate($(), $(), $()),  
        	(y, m, d) -> "month " + m + " in " + y),
		Case($(), 
        	() -> "(catch all)")
    );

    assertEquals("month 2 in 2017",result);
}
```

## 7. 模式匹配的副作用

默认情况下，Match就像一个表达式，这意味着它返回一个结果。但是，我们可以通过使用在lambda中运行的辅助函数来强制它产生副作用。

它采用方法引用或lambda表达式并返回Void。

考虑这样一个场景，当输入是一个偶数时我们想要打印一些东西，而当输入是一个奇数时我们想要打印另一个东西并且当输入不是这些时抛出异常。

偶数打印机：

```java
public void displayEven() {
    System.out.println("Input is even");
}
```

奇数打印机：

```java
public void displayOdd() {
    System.out.println("Input is odd");
}
```

和匹配功能：

```java
@Test
public void whenMatchCreatesSideEffects_thenCorrect() {
    int i = 4;
    Match(i).of(
      	Case($(isIn(2, 4, 6, 8)), o -> run(this::displayEven)), 
      	Case($(isIn(1, 3, 5, 7, 9)), o -> run(this::displayOdd)), 
      	Case($(), o -> run(() -> {
          	throw new IllegalArgumentException(String.valueOf(i));
      	})));
}
```

这会打印：

```bash
Input is even
```

## 8. 总结

在本文中，我们探索了Vavr中模式匹配API最重要的部分。事实上，我们现在可以编写更简单、更简洁的代码，而无需冗长的switch和if语句，这要感谢Vavr。