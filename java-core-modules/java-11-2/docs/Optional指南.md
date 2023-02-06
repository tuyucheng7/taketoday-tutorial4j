## 1. 概述

在本教程中，我们将展示Java 8中引入的Optional类。

该类的目的是为表示可选值而不是空引用提供类型级别的解决方案。

要更深入地了解为什么我们应该关注Optional类，请查看[Oracle官方文章](http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html)。

## 2. 创建Optional对象

有几种方法可以创建Optional对象。

要创建一个空的Optional对象，我们只需要使用它的empty()静态方法：

```java
@Test
public void whenCreatesEmptyOptional_thenCorrect() {
    Optional<String> empty = Optional.empty();
    assertFalse(empty.isPresent());
}
```

请注意，我们使用isPresent()方法来检查Optional对象中是否有值。仅当我们使用非空值创建Optional时才存在值。我们将在下一节中介绍isPresent()方法。

我们还可以使用静态方法of()创建一个Optional对象：

```java
@Test
public void givenNonNull_whenCreatesNonNullable_thenCorrect() {
    String name = "tuyucheng";
    Optional<String> opt = Optional.of(name);
    assertTrue(opt.isPresent());
}
```

但是，传递给of()方法的参数不能为空。否则，我们会得到一个NullPointerException：

```java
@Test(expected = NullPointerException.class)
public void givenNull_whenThrowsErrorOnCreate_thenCorrect() {
    String name = null;
    Optional.of(name);
}
```

但是如果我们期望一些空值，我们可以使用ofNullable()方法：

```java
@Test
public void givenNonNull_whenCreatesNullable_thenCorrect() {
    String name = "tuyucheng";
    Optional<String> opt = Optional.ofNullable(name);
    assertTrue(opt.isPresent());
}
```

通过这样做，如果我们传入一个空引用，它不会抛出异常，而是返回一个空的Optional对象：

```java
@Test
public void givenNull_whenCreatesNullable_thenCorrect() {
    String name = null;
    Optional<String> opt = Optional.ofNullable(name);
    assertFalse(opt.isPresent());
}
```

## 3. 检查值是否存在：isPresent()和isEmpty()

当我们有一个从方法返回或由我们创建的Optional对象时，我们可以使用isPresent()方法检查其中是否有值：

```java
@Test
public void givenOptional_whenIsPresentWorks_thenCorrect() {
    Optional<String> opt = Optional.of("Baeldung");
    assertTrue(opt.isPresent());

    opt = Optional.ofNullable(null);
    assertFalse(opt.isPresent());
}
```

如果包装的值不为null，则此方法返回true。

此外，从Java 11开始，我们可以使用isEmpty方法做相反的事情：

```java
@Test
public void givenAnEmptyOptional_thenIsEmptyBehavesAsExpected() {
    Optional<String> opt = Optional.of("Baeldung");
    assertFalse(opt.isEmpty());

    opt = Optional.ofNullable(null);
    assertTrue(opt.isEmpty());
}
```

## 4. ifPresent()的条件操作

ifPresent()方法使我们能够在包装的值上运行一些代码，如果它被发现是非null。在Optional之前，我们会这样做：

```java
if(name != null) {
    System.out.println(name.length());
}
```

此代码在继续执行一些代码之前检查name变量是否为空。这种方法很冗长，而且这不是唯一的问题-它也容易出错。

**确实，是什么保证在打印该变量之后，我们不会再次使用它然后忘记执行空值检查**？

**如果空值进入该代码，这可能会在运行时导致NullPointerException**。当程序由于输入问题而失败时，通常是由于编程实践不佳造成的。

Optional使我们能够明确地处理可为空的值，以此作为执行良好编程实践的一种方式。

现在让我们看看如何在Java 8中重构上述代码。

在典型的函数式编程风格中，我们可以对实际存在的对象执行操作：

```java
@Test
public void givenOptional_whenIfPresentWorks_thenCorrect() {
    Optional<String> opt = Optional.of("tuyucheng");
    opt.ifPresent(name -> System.out.println(name.length()));
}
```

在上面的示例中，我们仅使用两行代码来替换第一个示例中的五行代码：第一行将对象包装到Optional对象中，下一行执行隐式验证以及执行代码。

## 5. orElse()与默认值

orElse()方法用于检索包装在Optional实例中的值。它接收一个参数，作为默认值。orElse()方法返回包装的值(如果存在)，否则返回其参数：

```java
@Test
public void whenOrElseWorks_thenCorrect() {
    String nullName = null;
    String name = Optional.ofNullable(nullName).orElse("john");
    assertEquals("john", name);
}
```

## 6. orElseGet()与默认值

orElseGet()方法类似于orElse()。但是，如果Optional值不存在，它不会采用返回值，而是采用Supplier函数式接口，该接口被调用并返回调用的值：

```java
@Test
public void whenOrElseGetWorks_thenCorrect() {
    String nullName = null;
    String name = Optional.ofNullable(nullName).orElseGet(() -> "john");
    assertEquals("john", name);
}
```

## 7. orElse和orElseGet()的区别

对于许多不熟悉Optional或Java 8的程序员来说，orElse()和orElseGet()之间的区别并不清楚。事实上，这两种方法给人的印象是它们在功能上相互重叠。

然而，两者之间有一个微妙但非常重要的区别，如果没有很好地理解，可能会极大地影响我们代码的性能。

让我们在测试类中创建一个名为getMyDefault()的方法，它不接收任何参数并返回一个默认值：

```java
public String getMyDefault() {
    System.out.println("Getting Default Value");
    return "Default Value";
}
```

让我们看两个测试并观察它们的副作用，以确定orElse()和orElseGet()重叠的地方以及它们的不同之处：

```java
@Test
public void whenOrElseGetAndOrElseOverlap_thenCorrect() {
    String text = null;

    String defaultText = Optional.ofNullable(text).orElseGet(this::getMyDefault);
    assertEquals("Default Value", defaultText);

    defaultText = Optional.ofNullable(text).orElse(getMyDefault());
    assertEquals("Default Value", defaultText);
}
```

在上面的示例中，我们在Optional对象中包装了一个空text，并尝试使用这两种方法中的每一种来获取包装的值。

副作用是：

```shell
Getting default value...
Getting default value...
```

在每种情况下都会调用getMyDefault()方法。碰巧的是，**当包装的值不存在时，orElse()和orElseGet()的工作方式完全相同**。

现在让我们运行另一个存在该值的测试，理想情况下，甚至不应该创建默认值：

```java
@Test
public void whenOrElseGetAndOrElseDiffer_thenCorrect() {
    String text = "Text present";

    System.out.println("Using orElseGet:");
    String defaultText = Optional.ofNullable(text).orElseGet(this::getMyDefault);
    assertEquals("Text present", defaultText);

    System.out.println("Using orElse:");
    defaultText = Optional.ofNullable(text).orElse(getMyDefault());
    assertEquals("Text present", defaultText);
}
```

在上面的示例中，我们不再包装空值，其余代码保持不变。

现在让我们看看运行这段代码的副作用：

```shell
Using orElseGet:
Using orElse:
Getting default value...
```

请注意，当使用orElseGet()检索包装的值时，甚至不会调用getMyDefault()方法，因为包含的值存在。

但是，当使用orElse()时，无论包装的值是否存在，都会创建默认对象。所以在这种情况下，我们只是创建了一个从未使用过的冗余对象。

在这个简单的示例中，创建默认对象并没有显著的成本，因为JVM知道如何处理这种情况。**但是，当getMyDefault()之类的方法必须进行Web服务调用甚至查询数据库时，成本变得非常明显**。

## 8. orElseThrow()与异常

orElseThrow()方法继承于orElse()和orElseGet()并添加了一种新方法来处理缺失值。

当包装的值不存在时，它不会返回默认值，而是抛出异常：

```java
@Test(expected = IllegalArgumentException.class)
public void whenOrElseThrowWorks_thenCorrect() {
    String nullName = null;
    String name = Optional.ofNullable(nullName).orElseThrow(IllegalArgumentException::new);
}
```

Java 8中的方法引用在这里派上用场，可以传入异常构造函数。

**Java 10引入了orElseThrow()方法的简化无参数版本**。如果是空Optional，它会抛出NoSuchElementException：

```java
@Test(expected = NoSuchElementException.class)
public void whenNoArgOrElseThrowWorks_thenCorrect() {
    String nullName = null;
    String name = Optional.ofNullable(nullName).orElseThrow();
}
```

## 9. 使用get()返回值

检索包装值的最后一种方法是get()方法：

```java
@Test
public void givenOptional_whenGetsValue_thenCorrect() {
    Optional<String> opt = Optional.of("tuyucheng");
    String name = opt.get();
    assertEquals("tuyucheng", name);
}
```

但是，与前三种方法不同，get()只能在包装对象不为null时返回值；否则，它会抛出NoSuchElementException：

```java
@Test(expected = NoSuchElementException.class)
public void givenOptionalWithNull_whenGetThrowsException_thenCorrect() {
    Optional<String> opt = Optional.ofNullable(null);
    String name = opt.get();
}
```

这是get()方法的主要缺陷。理想情况下，Optional应该帮助我们避免这种不可预见的异常。因此，这种方法与Optional的目标背道而驰，并且可能会在未来的版本中被弃用。

因此，建议使用使我们能够准备并显式处理null情况的其他变体。

## 10. 使用filter()条件返回

我们可以使用filter方法对我们的包装值运行内联测试。它将谓词作为参数并返回一个Optional对象，如果包装的值通过了谓词的测试，则Optional按原样返回。

但是，如果谓词返回false，那么它将返回一个空的Optional：

```java
@Test
public void whenOptionalFilterWorks_thenCorrect() {
    Integer year = 2016;
    Optional<Integer> yearOptional = Optional.of(year);
    boolean is2016 = yearOptional.filter(y -> y == 2016).isPresent();
    assertTrue(is2016);
    boolean is2017 = yearOptional.filter(y -> y == 2017).isPresent();
    assertFalse(is2017);
}
```

filter方法通常以这种方式用于根据预定义的规则拒绝包装的值。我们可以使用它来拒绝错误的电子邮件格式或不够强的密码。

让我们看另一个有意义的例子。假设我们想购买一个调制解调器，我们只关心它的价格。

我们从某个站点接收有关调制解调器价格的推送通知，并将其存储在对象中：

```java
public class Modem {
    private Double price;

    public Modem(Double price) {
        this.price = price;
    }
    // standard getters and setters
}
```

然后我们将这些对象提供给一些代码，这些代码的唯一目的是检查调制解调器的价格是否在我们的预算范围内。

现在让我们看一下不使用Optional的代码：

```java
public boolean priceIsInRange1(Modem modem) {
    boolean isInRange = false;

    if (modem != null && 
        modem.getPrice() != null && 
        (modem.getPrice() >= 10 && 
        modem.getPrice() <= 15)) {

        isInRange = true;
    }
    return isInRange;
}
```

注意我们必须编写多少代码来实现这一点，尤其是在if条件下。对应用程序至关重要的if条件的唯一部分是最后的价格范围检查；其余的检查是防御性的：

```java
@Test
public void whenFiltersWithoutOptional_thenCorrect() {
    assertTrue(priceIsInRange1(new Modem(10.0)));
    assertFalse(priceIsInRange1(new Modem(9.9)));
    assertFalse(priceIsInRange1(new Modem(null)));
    assertFalse(priceIsInRange1(new Modem(15.5)));
    assertFalse(priceIsInRange1(null));
}
```

除此之外，有可能在漫长的一天中忘记空检查，而不会出现任何编译时错误。

现在让我们看一个使用Optional#filter的变体：

```java
public boolean priceIsInRange2(Modem modem2) {
     return Optional.ofNullable(modem2)
         .map(Modem::getPrice)
         .filter(p -> p >= 10)
         .filter(p -> p <= 15)
         .isPresent();
 }
```

**map调用仅用于将值转换为其他值**。请记住，此操作不会修改原始值。

在我们的例子中，我们从Model类中获取一个价格对象。我们将在下一节详细介绍map()方法。

首先，如果将一个空对象传递给该方法，我们预计不会出现任何问题。

其次，我们在其主体中编写的唯一逻辑正是方法名称所描述的-价格范围检查。Optional负责其余的工作：

```java
@Test
public void whenFiltersWithOptional_thenCorrect() {
    assertTrue(priceIsInRange2(new Modem(10.0)));
    assertFalse(priceIsInRange2(new Modem(9.9)));
    assertFalse(priceIsInRange2(new Modem(null)));
    assertFalse(priceIsInRange2(new Modem(15.5)));
    assertFalse(priceIsInRange2(null));
}
```

以前的方法承诺检查价格范围，但必须做更多的事情来抵御其固有的脆弱性。因此，我们可以使用filter方法来替换不必要的if语句并拒绝不需要的值。

## 11. 使用map()转换值

在上一节中，我们了解了如何根据过滤器拒绝或接受一个值。

我们可以使用类似的语法通过map()方法转换Optional值：

```java
@Test
public void givenOptional_whenMapWorks_thenCorrect() {
    List<String> companyNames = Arrays.asList("paypal", "oracle", "", "microsoft", "", "apple");
    Optional<List<String>> listOptional = Optional.of(companyNames);

    int size = listOptional
        .map(List::size)
        .orElse(0);
    assertEquals(6, size);
}
```

在此示例中，我们将字符串列表包装在Optional对象中，并使用其map方法对包含的列表执行操作。我们执行的操作是检索列表的大小。

map方法返回包装在Optional中的计算结果。然后，我们必须在返回的Optional上调用适当的方法来检索其值。

请注意，filter方法只是对值执行检查，并且仅当此值与给定的谓词匹配时才返回描述该值的Optional，否则返回一个空的Optional。然而，map方法采用现有值，使用该值执行计算，并返回包装在Optional对象中的计算结果：

```java
@Test
public void givenOptional_whenMapWorks_thenCorrect2() {
    String name = "tuyucheng";
    Optional<String> nameOptional = Optional.of(name);

    int len = nameOptional
        .map(String::length)
        .orElse(0);
    assertEquals(8, len);
}
```

我们可以将map和filter链接在一起来做一些更强大的事情。

假设我们要检查用户输入的密码的正确性。我们可以使用map转换来清理密码并使用filter检查其正确性：

```java
@Test
public void givenOptional_whenMapWorksWithFilter_thenCorrect() {
    String password = " password ";
    Optional<String> passOpt = Optional.of(password);
    boolean correctPassword = passOpt.filter(pass -> pass.equals("password")).isPresent();
    assertFalse(correctPassword);

    correctPassword = passOpt
        .map(String::trim)
        .filter(pass -> pass.equals("password"))
        .isPresent();
    assertTrue(correctPassword);
}
```

正如我们所看到的，如果不首先清理输入，它将被过滤掉-但用户可能会理所当然地认为前导和尾随空格都构成输入。因此，我们在过滤掉不正确的密码之前，将脏密码转换为带有映射的干净密码。

## 12. 使用flatMap()转换值

就像map()方法一样，我们也有flatMap()方法作为转换值的替代方法。不同之处在于map仅在它们被解包时才转换值，而flatMap接收一个被包装的值并在转换之前解包它。

以前，我们创建了简单的String和Integer对象，用于包装在Optional实例中。但是，我们经常会从复杂对象的访问器中接收这些对象。

为了更清楚地了解差异，让我们看一个Person对象，它获取一个人的详细信息，例如姓名、年龄和密码：

```java
public class Person {
    private String name;
    private int age;
    private String password;

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<Integer> getAge() {
        return Optional.ofNullable(age);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    // normal constructors and setters
}
```

我们通常会创建这样一个对象并将其包装在Optional对象中，就像我们对String所做的那样。

或者，它可以通过另一个方法调用返回给我们：

```java
Person person = new Person("john", 26);
Optional<Person> personOptional = Optional.of(person);
```

现在请注意，当我们包装一个Person对象时，它将包含嵌套的Optional实例：

```java
@Test
public void givenOptional_whenFlatMapWorks_thenCorrect2() {
    Person person = new Person("john", 26);
    Optional<Person> personOptional = Optional.of(person);

    Optional<Optional<String>> nameOptionalWrapper = personOptional.map(Person::getName);
    Optional<String> nameOptional = nameOptionalWrapper.orElseThrow(IllegalArgumentException::new);
    String name1 = nameOptional.orElse("");
    assertEquals("john", name1);

    String name = personOptional
        .flatMap(Person::getName)
        .orElse("");
    assertEquals("john", name);
}
```

在这里，我们试图检索Person对象的name属性以执行断言。

请注意我们如何在第三条语句中使用map()方法来实现这一点，然后注意我们之后如何使用flatMap()方法做同样的事情。

Person::getName方法引用类似于我们在上一节中用于清理密码的String::trim调用。

唯一的区别是getName()返回一个Optional而不是String，就像trim()操作一样。再加上映射转换将结果包装在Optional对象中的事实，导致嵌套Optional。

因此，在使用map()方法时，我们需要在使用转换后的值之前添加一个额外的调用来检索该值。这样，将删除Optional包装器。此操作在使用flatMap时隐式执行。

## 13. 在Java 8中链接Optionals

有时，我们可能需要从多个Optional中获取第一个非空Optional对象。在这种情况下，使用类似orElseOptional()的方法会非常方便。遗憾的是，Java 8不直接支持此类操作。

让我们首先介绍一些我们将在本节中使用的方法：

```java
private Optional<String> getEmpty() {
    return Optional.empty();
}

private Optional<String> getHello() {
    return Optional.of("hello");
}

private Optional<String> getBye() {
    return Optional.of("bye");
}

private Optional<String> createOptional(String input) {
    if (input == null || "".equals(input) || "empty".equals(input)) {
        return Optional.empty();
    }
    return Optional.of(input);
}
```

为了在Java 8中链接多个Optional对象并获取第一个非空对象，我们可以使用Stream API：

```java
@Test
public void givenThreeOptionals_whenChaining_thenFirstNonEmptyIsReturned() {
    Optional<String> found = Stream.of(getEmpty(), getHello(), getBye())
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
    
    assertEquals(getHello(), found);
}
```

这种方法的缺点是我们所有的get方法总是被执行，无论非空Optional出现在Stream中的什么位置。

如果我们想惰性地评估传递给Stream.of()的方法，我们需要使用方法引用和Supplier接口：

```java
@Test
public void givenThreeOptionals_whenChaining_thenFirstNonEmptyIsReturnedAndRestNotEvaluated() {
    Optional<String> found =
        Stream.<Supplier<Optional<String>>>of(this::getEmpty, this::getHello, this::getBye)
            .map(Supplier::get)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

    assertEquals(getHello(), found);
}
```

如果我们需要使用接收参数的方法，我们必须求助于lambda表达式：

```java
@Test
public void givenTwoOptionalsReturnedByOneArgMethod_whenChaining_thenFirstNonEmptyIsReturned() {
    Optional<String> found = Stream.<Supplier<Optional<String>>>of(
        () -> createOptional("empty"),
        () -> createOptional("hello")
    )
        .map(Supplier::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();

    assertEquals(createOptional("hello"), found);
}
```

通常，我们希望返回一个默认值，以防所有链接的Optional都为空。我们可以通过添加对orElse()或orElseGet()的调用来做到这一点：

```java
@Test
public void givenTwoEmptyOptionals_whenChaining_thenDefaultIsReturned() {
    String found = Stream.<Supplier<Optional<String>>>of(
        () -> createOptional("empty"),
        () -> createOptional("empty")
    )
        .map(Supplier::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseGet(() -> "default");

    assertEquals("default", found);
}
```

## 14. JDK 9 Optional API

Java 9的发布为Optional API添加了更多新方法：

-   or()方法，用于提供创建Optional替代选项的Supplier
-   ifPresentOrElse()方法，如果Optional存在则允许执行一个操作，如果不存在则允许执行另一个操作
-   用于将Optional转换为Stream的stream()方法

这是完整的文章以供[进一步阅读](https://www.baeldung.com/java-9-optional)。

## 15. Optional的误用

最后，让我们看看一种使用Optional的诱人但危险的方法：将Optional参数传递给方法。

想象一下，我们有一个Person列表，我们想要一种方法来搜索该列表中具有给定姓名的人。此外，如果指定了该方法，我们希望该方法能够匹配至少具有一定年龄的条目。

由于这个参数是Optional，因此我们附带了此方法：

```java
public static List<Person> search(List<Person> people, String name, Optional<Integer> age) {
    // Null checks for people and name
    return people.stream()
        .filter(p -> p.getName().equals(name))
        .filter(p -> p.getAge().get() >= age.orElse(0))
        .collect(Collectors.toList());
}
```

然后我们发布我们的方法，另一个开发人员尝试使用它：

```java
someObject.search(people, "Peter", null);
```

现在，开发人员执行其代码并得到NullPointerException。**我们必须对我们的可选参数进行空值检查，这违背了我们想要避免这种情况的最初目的**。

以下是我们本可以更好地处理它的一些可能性：

```java
public static List<Person> search(List<Person> people, String name, Integer age) {
    // Null checks for people and name
    final Integer ageFilter = age != null ? age : 0;

    return people.stream()
        .filter(p -> p.getName().equals(name))
        .filter(p -> p.getAge().get() >= ageFilter)
        .collect(Collectors.toList());
}
```

在那里，参数仍然是Optional，但我们只在一次检查中处理它。

另一种可能性是**创建两个重载方法**：

```java
public static List<Person> search(List<Person> people, String name) {
    return doSearch(people, name, 0);
}

public static List<Person> search(List<Person> people, String name, int age) {
    return doSearch(people, name, age);
}

private static List<Person> doSearch(List<Person> people, String name, int age) {
    // Null checks for people and name
    return people.stream()
        .filter(p -> p.getName().equals(name))
        .filter(p -> p.getAge().get().intValue() >= age)
        .collect(Collectors.toList());
}
```

通过这种方式，我们提供了一个清晰的API，其中有两种方法可以做不同的事情(尽管它们共享实现)。

因此，有一些解决方案可以避免使用Optional作为方法参数。**Java在发布Optional时的意图是将其用作返回类型**，从而表明方法可以返回空值。事实上，一些[代码检查器](https://rules.sonarsource.com/java/RSPEC-3553)甚至不鼓励使用Optional作为方法参数的做法。

## 16. Optional和序列化

如上所述，Optional旨在用作返回类型。不建议尝试将其用作字段类型。

此外，**在可序列化类中使用Optional将导致NotSerializableException**。我们的文章[Java Optional作为返回类型](https://www.baeldung.com/java-optional-return)进一步解决了序列化问题。

并且，在[将Optional与Jackson一起使用](https://www.baeldung.com/jackson-optional)中，我们解释了当Optional字段被序列化时会发生什么，以及一些解决方法来实现所需的结果。

## 17. 总结

在本文中，我们介绍了Java 8 Optional类的大部分重要特性。

我们简要探讨了为什么我们会选择使用Optional而不是显式的null检查和输入验证的一些原因。

我们还学习了如何使用get()、orElse()和orElseGet()方法获取Optional的值，或者如果为空则获取默认值(并看到了[最后两个之间的重要区别](https://www.baeldung.com/java-filter-stream-of-optional))。

然后我们看到了如何使用map()、flatMap()和filter()转换或过滤我们的Optional。我们讨论了流式的API Optional提供了什么，因为它允许我们轻松地链接不同的方法。 

最后，我们看到了为什么使用Optional作为方法参数是一个坏主意以及如何避免它。