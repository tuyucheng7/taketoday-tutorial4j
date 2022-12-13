## 1. 概述

JUnit 5是不同于JUnit 4的全新版本，它有助于编写具有全新特性的开发人员测试。

其中一个功能是参数化测试，该功能使我们**能够用不同的参数多次执行单个测试方法**。

## 2. 依赖

为了使用JUnit 5的参数化测试，我们需要导入junit-jupiter-params依赖：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

## 3. 快速上手

假设我们有一个现有的工具类：

```java
public class Numbers {

    public static boolean isOdd(int number) {
        return number % 2 != 0;
    }
}
```

参数化测试与其他测试类似，只是我们使用的是@ParameterizedTest注解：

```java
class NumbersUnitTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, -3, 15, Integer.MAX_VALUE})
    void isOdd_shouldReturnTrueForOddNumbers(int number) {
        assertTrue(Numbers.isOdd(number));
    }
}
```

JUnit 5测试Runner将执行上述测试，isOdd()方法会被调用六次。并且每次它都会从@ValueSource中指定的数组为测试方法的number参数分配一个不同的值。

因此，这个例子演示了参数化测试所需要的两个必要条件：

+ **数据的来源**：在本例中为@ValueSource中的ints数组
+ **访问数据的方式**：在本例中为测试方法的形参number

## 4. 参数源

参数化测试使用不同的参数多次执行相同的测试，接下来我们一一介绍不同类型的参数值。

### 4.1 简单值

**使用@ValueSource注解，我们可以向测试方法传递一个字面值数组**。

假设我们要测试以下的isBlank()方法：

```java
public class Strings {

    public static boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }
}
```

对于空字符，我们也期望从这个方法返回true。因此，我们可以编写一个参数化测试来断言这种行为：

```java
class StringsUnitTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void shouldReturnTrueForNullOrBlankStrings(String input) {
        assertTrue(Strings.isBlank(input));
    }
}
```

如我们所见，JUnit将运行此测试两次，每次将strings数组中的一个参数分配给测试方法形参input。

@ValueSource仅支持以下类型的数据类型：

+ short(shorts属性)
+ byte(bytes属性)
+ int(ints属性)
+ long(longs属性)
+ float(floats属性)
+ double(doubles属性)
+ char(chars属性)
+ java.lang.String(strings属性)
+ java.lang.Class(classes属性)

此外，**我们每次只能向测试方法传递一个参数**。

**注意：我们没有将null作为参数传递，这是另一个限制-我们不能通过@ValueSource传递null，即使对于String和Class也是如此**。

### 4.2 Null和Empty值

**从JUnit 5.4开始，我们可以使用@NullSource将单个null值传递给参数化测试方法**：

```java
@ParameterizedTest
@NullSource
void isBlank_shouldReturnTrueForNullInputs(String input) {
    assertTrue(Strings.isBlank(input));
}
```

由于基本数据类型不能接收null值，因此不能将@NullSource用于基本类型参数。

类似地，我们可以使用@EmptySource注解传递empty值(即空字符串)：

```java
@ParameterizedTest
@EmptySource
void isBlank_shouldReturnTrueFroEmptyStrings(String input) {
    assertTrue(Strings.isBlank(input));
}
```

**@EmptySource将一个空参数传递给带注解的方法**。对于String参数，传递的值是空字符串。此外，该参数源可以为Collection类型和数组提供空元素集合或数组。

为了同时传递null和空值，我们可以使用组合注解@NullAndEmptySource，下面是该注解的源码定义：

```java
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = STABLE, since = "5.7")
@NullSource
@EmptySource
public @interface NullAndEmptySource {
}
```

以及它的使用方式：

```java
@ParameterizedTest
@NullAndEmptySource
void isBlank_shouldReturnTrueForNullAndEmptyStrings(String input) {
    assertTrue(Strings.isBlank(input));
}
```

与@EmptySource一样，组合注解适用于字符串、集合和数组。

为了向参数化测试传递更多的类似空字符串变体，我们可以将@ValueSource、@NullSource和@EmptySource组合在一起：

```java
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {" ", "\t", "\n"})
void isBlank_shouldReturnTrueForAllTypesOfBlankStrings(String input) {
    assertTrue(Strings.isBlank(input));
}
```

### 4.3 枚举

**为了使用枚举的不同值运行测试，我们可以使用@EnumSource注解**。

例如，我们可以断言所有月份的数字都在1到12之间：

```java
class EnumsUnitTest {

    @ParameterizedTest
    @EnumSource(Month.class)
    void getValueForAMonth_IsAlwaysBetweenOneAndTwelve(Month month) {
        int monthNumber = month.getValue();
        assertTrue(monthNumber >= 1 && monthNumber <= 12);
    }
}
```

或者，我们可以使用names属性过滤掉几个月份。

我们还可以断言4月、6月、9月和11月都只有30天：

```java
@ParameterizedTest(name = "{index} {0} is 30 days long")
@EnumSource(value = Month.class, names = {"APRIL", "JUNE", "SEPTEMBER", "NOVEMBER"})
void someMonths_Are30DaysLong(Month month) {
    final boolean isALeapYear = false;
    assertEquals(30, month.length(isALeapYear));
}
```

默认情况下，names将仅保留匹配的枚举值。

我们可以通过将mode属性设置为EnumSource.Mode.EXCLUDE来实现相反的效果：

```java
@ParameterizedTest
@EnumSource(
        value = Month.class, names = {"APRIL", "JUNE", "SEPTEMBER", "NOVEMBER", "FEBRUARY"},
        mode = EnumSource.Mode.EXCLUDE
)
void exceptFourMonths_othersAre31DaysLong(Month month) {
    final boolean isALeapYear = false;
    assertEquals(31, month.length(isALeapYear));
}
```

除了文本字符串，我们还可以将正则表达式传递给names属性：

```java
@ParameterizedTest
@EnumSource(value = Month.class, names = ".+BER", mode = EnumSource.Mode.MATCH_ANY)
void fourMonths_AreEndingWithBer(Month month) {
    EnumSet<Month> months = EnumSet.of(Month.NOVEMBER, Month.DECEMBER, Month.OCTOBER, Month.SEPTEMBER);
    assertTrue(months.contains(month));
}
```

与@ValueSource相似，@EnumSource仅适用于我们每次测试执行只传递一个参数的情况。

### 4.4 CSV文本

假设我们要确保String的toUpperCase()方法生成预期的大写字符串，@ValueSource注解是无法实现的。

要为此类场景编写参数化测试，我们必须：

+ 将**输入值**和**期望值**传递给测试方法。
+ **使用这些输入值计算实际结果**。
+ **用期望值断言实际值**。

因此，我们需要能够传递多个参数的参数源。

@CsvSource就是其中之一：

```java
@ParameterizedTest
@CsvSource({"test,TEST", "tEsT,TEST", "Java,JAVA"})
void toUpperCase_shouldGenerateTheExpectedUppercaseValue(String input, String expected) {
    String actualValue = input.toUpperCase();
    assertEquals(expected, actualValue);
}
```

@CsvSource可以接收逗号分隔值的数组，其中的每一对用逗号分割，对应于CSV文件中的一行数据。该数据源每次获取一个数组元素，根据逗号进行拆分，并将拆分后的两部分作为单独的参数传递给带注解的测试方法。

默认情况下分隔符是逗号，但我们可以使用delimiter属性对其进行自定义：

```java
@ParameterizedTest
@CsvSource(value = {"test:test", "TeSt:test", "Java:java"}, delimiter = ':')
void toLowerCase_shouldGenerateTheExpectedLowercaseValue(String input, String expected) {
    String actualValue = input.toLowerCase();
    assertEquals(expected, actualValue);
}
```

### 4.5 CSV文件

我们可以引用一个实际的CSV文件，而不是在代码中传递CSV文本值。

例如，假设我们有一个如下的CSV文件：

```csv
input,expected
test,TEST
tEst,TEST
Java,JAVA
```

**我们可以使用@CsvFileSource加载CSV文件并使用numLinesToSkip属性忽略标头**：

```java
@ParameterizedTest
@CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)
void toUpperCase_shouldGenerateTheExpectedUppercaseValueCSVFile(String input, String expected) {
    String actualValue = input.toUpperCase();
    assertEquals(expected, actualValue);
}
```

resources属性表示要读取的类路径上的CSV文件资源，该文件应该位于src/test/resources/文件夹中。而且它是String类型的数组，我们可以将多个文件传递给它。

numLinesToSkip属性表示读取CSV文件时要跳过的行数。**默认情况下，@CsvFileSource不会跳过任何行，但我们通常希望跳过第一行，也就是标头**。

与@CsvSource注解一样，分隔符可以使用delimiter属性进行自定义。

除了列分隔符之外，我们还有以下功能：

+ 可以使用lineSeparator属性自定义行分隔符：默认的行分隔符是换行("\n")
+ 文件编码可以使用encoding属性定义：默认值是UTF-8

### 4.6 方法

到目前为止，我们介绍的参数源都比较简单，并且有一个共同的局限性，也就是很难或者根本不可能使用它们传递复杂的对象。

**提供更复杂参数的一种方法是使用方法作为参数源**。

让我们用@MethodSource测试isBlank方法：

```java
@ParameterizedTest
@MethodSource("provideStringsForIsBlank")
void isBlank_shouldReturnTrueForNullOrBlankStrings(String input, boolean expected) {
    assertEquals(expected, Strings.isBlank(input));
}
```

@MethodSource只有唯一的一个属性，用来指定使用哪个方法作为参数源，我们提供给@MethodSource的方法名称需要与现有方法匹配。

因此我们需要编写一个provideStringsForIsBlank()方法，**这是一个返回Arguments Stream的静态方法**：

```java
private static Stream<Arguments> provideStringsForIsBlank() {
    return Stream.of(
            Arguments.of(null, true),
            Arguments.of("", true),
            Arguments.of(" ", true),
            Arguments.of("not blank", false)
    );
}
```

**在这里，我们实际上返回一个Arguments流，但这不是严格的要求。例如，我们可以返回任何其他类似集合的接口，如List**。

如果我们每次测试调用只提供一个参数，那么就没有必要使用Arguments：

```java
@ParameterizedTest
@MethodSource
void isBlank_shouldReturnTrueForNullOrBlankStringsOneArgument(String input) {
    assertTrue(Strings.isBlank(input));
}

private static Stream<String> isBlank_shouldReturnTrueForNullOrBlankStringsOneArgument() {
    return Stream.of(null, " ", "");
}
```

**当我们没有为@MethodSource提供方法名时，JUnit会搜索与测试方法同名的方法作为参数源**。

有时，我们可能需要在不同的类中共享一个方法作为参数源。在这些情况下，我们可以通过其完全限定名来引用当前类之外的方法：

```java
@ParameterizedTest
@MethodSource("cn.tuyucheng.taketoday.junit5.parameterized.StringParams#blankStrings")
void isBlank_shouldReturnTrueForNullOrBlankStringsExternalSource(String input) {
    assertTrue(Strings.isBlank(input));
}
```

```java
package cn.tuyucheng.taketoday.junit5.parameterized;

public class StringParams {

    public static Stream<String> blankStrings() {
        return Stream.of(null, "", "  ");
    }
}
```

使用全限定类名#方法名格式，我们可以引用外部静态方法。

### 4.7 自定义参数提供者

传递测试参数的另一种高级方法是使用ArgumentsProvider接口的自定义实现：

```java
public class BlankStringsArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of((String) null),
                Arguments.of(""),
                Arguments.of("   ")
        );
    }
}
```

然后我们可以使用@ArgumentsSource注解来标注我们的测试，并指定我们自定义的BlankStringsArgumentsProvider：

```java
@ParameterizedTest
@ArgumentsSource(BlankStringsArgumentsProvider.class)
void isBlank_shouldReturnTrueForNullOrBlankStringsArgProvider(String input) {
    assertTrue(Strings.isBlank(input));
}
```

### 4.8 自定义注解

假设我们要从静态变量加载测试参数：

```java
static Stream<Arguments> arguments = Stream.of(
        Arguments.of(null, true),
        Arguments.of("", true),
        Arguments.of("  ", true),
        Arguments.of("not blank", false)
);

@ParameterizedTest
@VariableSource("arguments")
void isBlank_shouldReturnTrueForNullOrBlankStringsVariableSource(String input, boolean expected) {
    assertEquals(expected, Strings.isBlank(input));
}
```

**实际上，JUnit 5没有提供这个功能**。但是，我们可以实现自己的解决方案。

首先，我们可以创建一个注解：

```java
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(VariableArgumentsProvider.class)
public @interface VariableSource {
    String value();
}
```

然后我们需要以某种方式使用注解并提供测试参数，JUnit 5提供了两个抽象来实现这些：

+ AnnotationConsumer：消费注解细节
+ ArgumentsProvider：提供测试参数

因此，我们接下来需要让VariableArgumentsProvider类从指定的静态变量中读取，并将其值作为测试参数返回：

```java
public class VariableArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<VariableSource> {
    private String variableName;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return context.getTestClass()
                .map(this::getField)
                .map(this::getValue)
                .orElseThrow(() -> new IllegalArgumentException("Failed to load test arguments"));
    }

    @Override
    public void accept(VariableSource variableSource) {
        variableName = variableSource.value();
    }

    private Field getField(Class<?> clazz) {
        try {
            return clazz.getDeclaredField(variableName);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Stream<Arguments> getValue(Field field) {
        Object value = null;
        try {
            value = field.get(null);
        } catch (Exception ignored) {
        }
        return value == null ? null : (Stream<Arguments>) value;
    }
}
```

## 5. 参数转换

### 5.1 隐式转换

让我们用@CsvSource重新编写EnumsUnitTest中的一个测试：

```java
@ParameterizedTest
@CsvSource({"APRIL", "JUNE", "SEPTEMBER", "NOVEMBER"})
void someMonths_Are30DaysLongCsv(Month month) {
    final boolean isALeapYear = false;
    assertEquals(30, month.length(isALeapYear));
}
```

看起来这个测试存在问题，但实际上它是完全可行的。 

JUnit 5会将String参数转换为指定的枚举类型。为了支持这样的测试用例，JUnit Jupiter提供了许多内置的隐式类型转换器。

转换过程取决于每个方法参数的声明类型，隐式转换可以将String实例转换为如下类型：

+ UUID
+ Locale
+ LocalDate、LocalTime、LocalDateTime、Year、Month等
+ File和Path
+ URL和URI
+ 枚举

### 5.2 显示转换

我们甚至可以为参数提供自定义的显式转换器，假设我们要将yyyy/mm/dd格式的字符串转换为LocalDate实例。首先，我们需要实现ArgumentConverter接口：

```java
public class SlashyDateConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof String))
            throw new IllegalArgumentException("The argument should be a string: " + source);
        try {
            String[] parts = ((String) source).split("/");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert", e);
        }
    }
}
```

然后我们应该通过@ConvertWith注解标注方法的形参，引用转换器：

```java
@ParameterizedTest
@CsvSource({"2018/12/25,2018", "2019/02/11,2019"})
void getYear_shouldWorkAsExpected(@ConvertWith(SlashyDateConverter.class) LocalDate date, int expected) {
    assertEquals(expected, date.getYear());
}
```

## 6. 参数访问器

默认情况下，提供给参数化测试的每个参数都对应测试方法的一个形参。因此，当通过参数源传递大量参数时，测试方法签名会变得太过冗长。

解决此问题的一种方法是将所有传递的参数封装到ArgumentsAccessor的实例中，并按索引和类型检索参数。

假设我们有一个以下的Person类定义：

```java
class Person {
    String firstName;
    String middleName;
    String lastName;
    // constructor ...

    public String fullName() {
        if (middleName == null || middleName.trim().isEmpty())
            return String.format("%s %s", firstName, lastName);
        return String.format("%s %s %s", firstName, middleName, lastName);
    }
}
```

为了测试fullName()方法，我们需要传递四个参数：firstName、middleName、lastName和预期的fullName。此时我们就可以使用ArgumentsAccessor来检索测试参数，而不是将它们每一个都声明为方法形参：

```java
@ParameterizedTest
@CsvSource({"Isaac,,Newton, Isaac Newton", "Charles,Robert,Darwin,Charles Robert Darwin"})
void fullName_shouldGenerateTheExpectedFullName(ArgumentsAccessor argumentsAccessor) {
    String firstName = argumentsAccessor.getString(0);
    String middleName = (String) argumentsAccessor.get(1);
    String lastName = argumentsAccessor.get(2, String.class);
    String expectedFullName = argumentsAccessor.getString(3);
    Person person = new Person(firstName, middleName, lastName);
    assertEquals(expectedFullName, person.fullName());
}
```

在这里，我们将所有传递的参数封装到一个ArgumentsAccessor实例中，然后在测试方法中检索每个传递的参数及其索引。除了作为访问器之外，还通过get*方法支持类型转换：

+ getString(index)检索特定索引处的元素并将其转换为String，对于基本数据类型也是如此
+ get(index)只是将特定索引处的元素作为Object获取
+ get(index, type)检索特定索引处的元素并将其转换为给定类型

## 7. 参数聚合器

直接使用ArgumentsAccessor检索每一个参数可能会降低测试代码的可读性或可重用性，为了解决这些问题，我们可以编写一个自定义且可重用的参数聚合器。

为此，我们需要实现ArgumentsAggregator接口：

```java
public class PersonAggregator implements ArgumentsAggregator {

    @Override
    public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
        return new Person(accessor.getString(1), accessor.getString(2), accessor.getString(3));
    }
}
```

然后我们通过@AggregateWith注解引用它：

```java
@ParameterizedTest
@CsvSource({"Isaac Newton,Isaac,,Newton", "Charles Robert Darwin,Charles,Robert,Darwin"})
void fullName_shouldGenerateTheExpectedFullName(String expectedFullName, @AggregateWith(PersonAggregator.class) Person person) {
    assertEquals(expectedFullName, person.fullName());
}
```

PersonAggregator根据获取的参数从中实例化一个Person对象。

## 8. 自定义测试名

默认情况下，参数化测试的显示名称包含当前测试的索引以及所有传递参数的字符串表示形式：

<img src="../assets/img_4.png">

但是，我们可以通过@ParameterizedTest注解的name属性自定义显示名称：

```java
@ParameterizedTest(name = "{index} {0} is 30 days long")
@EnumSource(value = Month.class, names = {"APRIL", "JUNE", "SEPTEMBER", "NOVEMBER"})
void someMonths_Are30DaysLong(Month month) {
    final boolean isALeapYear = false;
    assertEquals(30, month.length(isALeapYear));
}
```

在自定义显示名称时，我们可以使用以下占位符：

+ {index}将替换为当前测试索引。简单来说，第一次执行的测试索引为1，第二次执行的测试索引为2，以此类推
+ {arguments}是完整的、逗号分隔的参数列表的占位符
+ {0}、{1}、...是各个参数的占位符

## 9. 总结

在本文中，我们全面介绍了JUnit 5中参数化测试的具体细节。参数化测试使用@ParameterizedTest注解进行标注，并且需要为它们声明参数的来源。

此外，我们还介绍JUnit提供的一些工具来将参数转换为自定义目标类型，或者自定义测试名称。