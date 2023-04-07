## 1. 什么是Project Amber

**[Project Amber](https://openjdk.java.net/projects/amber/)是Java和OpenJDK开发人员的最新倡议，旨在对JDK进行一些小但重要的更改，以使开发过程更好**。这自2017年以来一直在进行，并且已经对Java 10和11进行了一些更改，其他更改计划包含在Java 12中，但在未来的版本中还会有更多更改。

这些更新都以[JEP](https://en.wikipedia.org/wiki/JDK_Enhancement_Proposal)的形式打包-JDK增强建议方案。

## 2. 交付的更新

到目前为止，Project Amber已成功对当前发布的JDK版本进行了一些更改-[JEP-286](https://openjdk.java.net/jeps/286)和[JEP-323](https://openjdk.java.net/jeps/323)。

### 2.1 局部变量类型推断

**Java 7引入了[钻石运算符](https://www.baeldung.com/java-diamond-operator)作为一种使泛型更易于使用的方法**。此功能意味着我们在定义变量时不再需要在同一语句中多次写入泛型信息：

```java
List<String> strings = new ArrayList<String>(); // Java 6
List<String> strings = new ArrayList<>(); // Java 7
```

**Java 10包括JEP-286的完整工作，允许我们的Java代码定义局部变量，而无需在编译器已经可用的地方复制类型信息**。这在更广泛的社区中被称为var关键字，并为Java带来了与许多其他语言中可用的类似功能。

通过这项工作，**每当我们定义局部变量时，我们都可以使用var关键字而不是完整的类型定义**，编译器将自动计算出要使用的正确类型信息：

```java
var strings = new ArrayList<String>();
```

**在上面，变量strings被确定为ArrayList<String\>()类型**，但不需要在同一行上重复信息。

**我们可以在任何使用局部变量的地方使用它**，而不管值是如何确定的。这包括返回类型和表达式，以及像上面这样的简单赋值。

var这个词是一个特例，因为它不是保留字。相反，它是一个特殊的类型名称。这意味着可以将这个词用于代码的其他部分-包括变量名，但强烈建议不要这样做以避免混淆。

**仅当我们提供实际类型作为声明的一部分时，我们才能使用局部类型推断**。它被故意设计为当值显式为null时不工作，当根本没有提供值时，或者当提供的值无法确定确切类型时-例如，Lambda定义：

```java
var unknownType; // No value provided to infer type from
var nullType = null; // Explicit value provided but it's null
var lambdaType = () -> System.out.println("Lambda"); // Lambda without defining the interface
```

但是，**如果它是某个其他调用的返回值，则该值可以为null**，因为调用本身提供了类型信息：

```java
Optional<String> name = Optional.empty();
var nullName = name.orElse(null);
```

在这种情况下，nullName将推断为String类型，因为这是name.orElse()的返回类型。

**以这种方式定义的变量可以像任何其他变量一样具有任何其他修饰符**-transitive、synchronized和final。

### 2.2 Lambda的局部变量类型推断

上述工作允许我们声明局部变量而不需要类型信息。但是，这不适用于参数列表，尤其是不适用于lambda函数的参数，这似乎令人惊讶。

在Java 10中，我们可以通过以下两种方式之一定义Lambda函数-显式声明类型或完全省略它们：

```java
names.stream()
    .filter(String name -> name.length() > 5)
    .map(name -> name.toUpperCase());
```

在这里，第二行有一个显式类型声明String，而第三行完全省略它，编译器计算出正确的类型。**我们不能做的是在这里使用var类型**。

**Java 11允许这种情况发生**，因此我们可以改写为：

```java
names.stream()
    .filter(var name -> name.length() > 5)
    .map(var name -> name.toUpperCase());
```

**这与我们代码中其他地方使用var类型是一致的**。

Lambda总是限制我们为每个参数使用完整的类型名称，或者任何参数都不指定类型。这没有改变，并且**var的使用必须用于每个参数或不用于任何参数**：

```java
numbers.stream()
    .reduce(0, (var a, var b) -> a + b); // Valid

numbers.stream()
    .reduce(0, (var a, b) -> a + b); // Invalid

numbers.stream()
    .reduce(0, (var a, int b) -> a + b); // Invalid
```

在这里，第一个示例完全有效-因为两个lambda参数都使用了var。但是，**第二个和第三个是非法的，因为只有一个参数使用var**，即使在第三种情况下我们也有一个显式类型名称。

## 3. 即将更新

除了已发布的JDK中已经可用的更新之外，即将发布的JDK 12版本还包括一个更新-[JEP-325](https://openjdk.java.net/jeps/325)。

### 3.1 Switch表达式

**JEP-325支持简化switch语句的工作方式，并允许将它们用作表达式以进一步简化使用它们的代码**。

目前，switch语句的工作方式与C或C++等语言中的语句非常相似。**这些变化使它更类似于Kotlin中的when语句或Scala中的match语句**。

通过这些更改，**定义switch语句的语法看起来类似于lambdas的语法**，使用->符号。这位于case匹配和要执行的代码之间：

```java
switch (month) {
    case FEBRUARY -> System.out.println(28);
    case APRIL -> System.out.println(30);
    case JUNE -> System.out.println(30);
    case SEPTEMBER -> System.out.println(30);
    case NOVEMBER -> System.out.println(30);
    default -> System.out.println(31);
}
```

**注意break关键字不是必须的，而且我们这里也不能用**。它自动暗示每个匹配case都是不同的，并且fallthrough不是一个选项。相反，我们可以在需要时继续使用旧样式。

**箭头的右侧必须是表达式、块或throws语句**。其他任何内容都是错误的。这也解决了在switch语句内定义变量的问题-这只能发生在一个块内部，这意味着它们会自动限定在该块内：

```java
switch (month) {
    case FEBRUARY -> {
        int days = 28;
    }
    case APRIL -> {
        int days = 30;
    }
    // ....
}
```

**在旧式switch语句中，由于变量days重复，这将是一个错误**。使用块的要求避免了这种情况。

**箭头的左侧可以是任意数量的逗号分隔值**。这是为了允许一些与fallthrough相同的功能，但仅限于整个匹配并且绝不会偶然：

```java
switch (month) {
    case FEBRUARY -> System.out.println(28);
    case APRIL, JUNE, SEPTEMBER, NOVEMBER -> System.out.println(30);
    default -> System.out.println(31);
}
```

到目前为止，所有这一切都可以通过switch语句的当前工作方式实现，并使其更整洁。但是，**此更新还带来了将switch语句用作表达式的功能**。这对Java来说是一个重大变化，但它与许多其他语言(包括其他JVM语言)开始发挥作用是一致的。

**这允许switch表达式解析为一个值，然后在其他语句中使用该值**-例如，赋值：

```java
final var days = switch (month) {
    case FEBRUARY -> 28;
    case APRIL, JUNE, SEPTEMBER, NOVEMBER -> 30;
    default -> 31;
}
```

在这里，我们使用switch表达式生成一个数字，然后将该数字直接分配给一个变量。

**以前，这只能通过将变量days定义为null然后在switch cases中为其分配一个值来实现**。这意味着days不可能是最终的，如果我们错过了一个case，可能会被取消分配。

## 4. 即将发生的变化

到目前为止，所有这些更改要么已经可用，要么将在即将发布的版本中提供。**作为Project Amber的一部分，有一些提议的更改尚未计划发布**。

### 4.1 原始字符串文字

**目前，Java只有一种定义字符串字面量的方法-用双引号将内容括起来**。这很容易使用，但在更复杂的情况下会遇到问题。

具体来说，**很难编写包含某些字符的字符串——包括但不限于**：换行符、双引号和反斜杠字符。这在文件路径和正则表达式中尤其成问题，因为这些字符可能比典型字符更常见。

**[JEP-326](https://openjdk.java.net/jeps/326)引入了一种新的String字面量类型，称为Raw String Literals(原始字符串文字)**。它们用反引号而不是双引号括起来，并且可以在其中包含任何字符。

**这意味着可以编写跨越多行的字符串，以及包含引号或反斜杠的字符串，而无需对它们进行转义**。因此，它们变得更容易阅读。

例如：

```java
// File system path
"C:\\Dev\\file.txt"
`C:\Dev\file.txt`

// Regex
"\\d+\\.\\d\\d"
`\d+\.\d\d`

// Multi-Line
"Hello\nWorld"
`Hello
World`
```

在所有这三种情况下，**使用反引号更容易看出版本中发生了什么，输入out也更不容易出错**。

**新的Raw String Literals还允许我们在不复杂的情况下包含反引号本身**。用于开始和结束字符串的反引号的数量可以根据需要设置-它不必只有一个反引号。只有当我们达到相等长度的反引号时，字符串才会结束。所以，例如：

```java
``This string allows a single "`" because it's wrapped in two backticks``
```

这使我们能够完全按原样输入字符串，而不是需要特殊的序列来使某些字符起作用。

### 4.2 Lambda Leftovers

[JEP-302](https://openjdk.java.net/jeps/302)对lambda的工作方式进行了一些小的改进。

主要变化是处理参数的方式。首先，**此更改引入了对未使用的参数使用下划线的功能，这样我们就不会生成不需要的名称**。这在以前是可能的，但仅适用于单个参数，因为下划线是有效名称。

Java 8引入了一项更改，因此使用下划线作为名称是一种警告。然后Java 9将其发展为一个错误，完全阻止我们使用它们。这一即将发生的变化允许它们使用lambda参数而不会引起任何冲突。例如，这将允许以下代码：

```java
jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = 1", (rs, _) -> parseUser(rs))
```

在此增强功能下，**我们定义了带有两个参数的lambda，但只有第一个绑定到名称**。第二个是不可访问的，但同样，我们这样写是因为我们没有任何需要使用它。

**此增强功能的另一个主要变化是允许lambda参数从当前上下文中隐藏名称**。这是目前不允许的，这可能会导致我们编写一些不太理想的代码。例如：

```java
String key = computeSomeKey();
map.computeIfAbsent(key, key2 -> key2.length());
```

**除了编译器之外，没有真正需要为什么key和key2不能共享一个名称**。lambda从不需要引用变量key，强迫我们这样做会使代码更难看。

相反，此增强功能允许我们以更明显和更简单的方式编写它：

```java
String key = computeSomeKey();
map.computeIfAbsent(key, key -> key.length());
```

此外，**此增强功能中还有一个提议的更改，当重载方法具有lambda参数时，它可能会影响重载决策**。目前，在某些情况下，由于重载决议的工作规则，这可能会导致歧义，并且这个JEP可能会稍微调整这些规则以避免一些歧义。

例如，**目前编译器认为以下方法是不明确的**：

```java
m(Predicate<String> ps) { ... }
m(Function<String, String> fss) { ... }
```

这两种方法都接收具有单个String参数且具有非void返回类型的lambda。**对于开发人员来说，很明显它们是不同的-一个返回String，另一个返回boolean，但编译器会将它们视为不明确的**。

这个JEP可以解决这个缺点并允许显式处理这个重载。

### 4.3 模式匹配

**[JEP-305](https://openjdk.java.net/jeps/305)改进了我们可以使用instanceof运算符和自动类型强制转换的方式**。

目前，在Java中比较类型时，我们必须使用instanceof运算符来判断值的类型是否正确，然后再将值转换为正确的类型：

```java
if (obj instanceof String) {
    String s = (String) obj;
    // use s
}
```

这有效并且可以立即理解，但是它比必要的更复杂。**我们的代码中有一些非常明显的重复，因此存在允许错误潜入的风险**。

**此增强功能对instanceof运算符进行了类似的调整，就像之前在Java 7中的try-with-resources下所做的那样**。通过此更改，比较、强制转换和变量声明变为单个语句：

```java
if (obj instanceof String s) {
    // use s
}
```

**这为我们提供了一个单一的语句，没有重复，也没有错误蔓延的风险**，但执行与上述相同。

这也将跨分支正常工作，允许以下工作：

```java
if (obj instanceof String s) {
    // can use s here
} else {
    // can't use s here
}
```

**增强功能也可以在适当的不同作用域边界内正常工作**。正如预期的那样，由instanceof子句声明的变量将正确地隐藏在其外部定义的变量。不过，这只会发生在适当的块中：

```java
String s = "Hello";
if (obj instanceof String s) {
    // s refers to obj
} else {
    // s refers to the variable defined before the if statement
}
```

**这也适用于相同的if子句**，与我们依赖空检查的方式相同：

```java
if (obj instanceof String s && s.length() > 5) {
    // s is a String of greater than 5 characters
}
```

**目前，这仅计划用于if语句**，但未来的工作可能会将其扩展到与switch表达式一起使用。

### 4.4 简洁方法体

**[JEP草案8209434](https://openjdk.java.net/jeps/8209434)是一项支持简化方法定义的提案**，其方式类似于lambda定义的工作方式。

**目前，我们可以通过三种不同的方式定义Lambda**：使用主体、作为单个表达式或作为方法引用：

```java
ToIntFunction<String> lenFn = (String s) -> { return s.length(); };
ToIntFunction<String> lenFn = (String s) -> s.length();
ToIntFunction<String> lenFn = String::length;
```

但是，**在编写实际的类方法体时，我们目前必须将它们完整地写出来**。

**该提案也支持这些方法的表达式和方法引用形式**，在它们适用的情况下。这将有助于使某些方法比现在简单得多。

例如，getter方法不需要完整的方法体，而可以用单个表达式代替：

```java
String getName() -> name;
```

同样，我们可以用方法引用调用替换那些只是其他方法包装器的方法，包括传递参数：

```java
int length(String s) = String::length
```

**这些将允许在有意义的情况下使用更简单的方法**，这意味着它们不太可能掩盖类其余部分的真实业务逻辑。

请注意，这仍处于草稿状态，因此在交付前可能会发生重大变化。

## 5. 增强枚举

[JEP-301](https://openjdk.java.net/jeps/301)之前计划成为Project Amber的一部分。**这会给枚举带来一些改进，明确允许单个枚举元素具有不同的泛型类型信息**。

例如，它将允许：

```java
enum Primitive<X> {
    INT<Integer>(Integer.class, 0) {
        int mod(int x, int y) { return x % y; }
        int add(int x, int y) { return x + y; }
    },
    FLOAT<Float>(Float.class, 0f)  {
        long add(long x, long y) { return x + y; }
    }, ... ;

    final Class<X> boxClass;
    final X defaultValue;

    Primitive(Class<X> boxClass, X defaultValue) {
        this.boxClass = boxClass;
        this.defaultValue = defaultValue;
    }
}
```

不幸的是，**在Java编译器应用程序中进行的这种增强实验证明，它不如以前认为的那么可行**。将泛型类型信息添加到枚举元素使得无法将这些枚举用作其他类的泛型类型-例如，EnumSet。这大大降低了增强功能的实用性。

因此，**此增强功能目前处于搁置状态，直到可以制定出这些细节**。

## 6. 总结

我们在这里介绍了许多不同的功能。其中一些已经可用，其他将很快可用，还有更多计划在未来发布。