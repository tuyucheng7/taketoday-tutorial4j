## 一、概述

在本教程中，我们将学习如何解决为不同目的生成模拟数据的问题。我们将学习如何使用[Datafaker](http://www.datafaker.net/)并查看几个示例。

## 2. 历史

Datafaker 是 Javafaker 的现代[分支](https://github.com/DiUS/java-faker)。它被转移到 Java 8 并进行了改进，提高了库的[性能](http://www.datafaker.net/documentation/performance/)。但是，当前的 API 或多或少保持不变。因此，以前使用的 Javafaker 迁移到 Datafaker 不会有任何问题。[Java Faker 文章](https://www.baeldung.com/java-faker)中提供的所有示例 都适用于 1.6.0 版本的 Datafaker。

当前的Datafaker API 与 Javafaker 兼容。因此，本文将只关注差异和改进。

首先，让我们在项目中添加 [datafaker](https://search.maven.org/search?q=g:net.datafaker AND a:datafaker AND v:1.6.0) Maven 依赖项：

```xml
<dependency>
    <groupId>net.datafaker</groupId>
    <artifactId>datafaker</artifactId>
    <version>1.6.0</version>
</dependency>
```

## 3. 提供者

Datafaker 最重要的部分之一是[提供者](http://www.datafaker.net/documentation/providers/)。这是一组使数据生成更加方便的特殊类。重要的是要注意这些类由 具有正确数据 的[yml 文件备份。](https://github.com/datafaker-net/datafaker/tree/main/src/main/resources)Faker方法和表达式直接或间接使用这些文件来生成数据。在接下来的部分中，我们将更加熟悉这些方法和指令的工作。

## 4. 数据生成的其他模式

Datafaker 和 Javafaker 支持基于提供的模式生成值。Datafaker 引入了带有 模板化、示例化、选项、日期、csv 和 json指令的附加功能。

### 4.1。模板化

模板化指令有几个参数。第一个是基础 S字符串。第二个是将在给定字符串中替换的字符。其余是随机选择的替换选项：

```java
public class Templatify {
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println("Expression: " + getExpression());
        System.out.println("Expression with a placeholder: " + getExpressionWithPlaceholder());
    }

    static String getExpression() {
        return faker.expression("#{templatify 'test','t','j','r'}");
    }

    static String getExpressionWithPlaceholder() {
        return faker.expression("#{templatify '#ight', '#', 'f', 'l', 'm', 'n'}");
    }
}
```

尽管我们可以使用不带占位符的基本字符串，但它可能会产生不良结果，因为它将替换给定字符串中的所有出现。我们可以引入一个占位符，一个只出现在基本字符串特定位置的字符。在上述情况下，结果是：

```bash
Expression: resj
Expression with a placeholder: night
```

如果有多个位置可以放置随机字符，则每次都会随机。可以使用字符串进行替换，但文档没有明确提及这一点。因此最好谨慎使用。

### 4.2. 举例

该指令根据提供的示例生成一个随机值。它将用尊重的值替换小写或大写字符。数字也是如此。特殊字符未被触及，这有助于创建格式化字符串：

```java
public class Examplify {
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println("Expression: " + getExpression());
        System.out.println("Number expression: " + getNumberExpression());
    }

    static String getExpression() {
        return faker.expression("#{examplify 'Cat in the Hat'}");
    }

    static String getNumberExpression() {
        return faker.expression("#{examplify '123-123-123'}");
    }
}
```

输出示例：

```bash
Expression: Lvo lw ero Qkd
Number expression: 707-657-434
```

### 4.3. 正则表达式

这是一种更灵活的创建格式化 S字符串值的方法。我们可以使用regexify指令作为表达式，或者直接在Faker 对象上调用regexify方法：

```java
public class Regexify {
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println("Expression: " + getExpression());
        System.out.println("Regexify with a method: " + getMethodExpression());
    }

    static String getExpression() {
        return faker.expression("#{regexify '(hello|bye|hey)'}");
    }

    static String getMethodExpression() {
        return faker.regexify("[A-D]{4,10}");
    }
}
```

可能的输出：

```bash
Expression: bye
Regexify with a method: DCCC

```

### 4.4. 选项

options.option指令允许从提供的列表中随机选择一个选项。此功能可以通过regexify实现，但通常情况下，单独的指令是有意义的：

```java
public class Option {
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println("First expression: " + getFirstExpression());
        System.out.println("Second expression: " + getSecondExpression());
        System.out.println("Third expression: " + getThirdExpression());
    }

    static String getFirstExpression() {
        return faker.expression("#{options.option 'Hi','Hello','Hey'}");
    }

    static String getSecondExpression() {
        return faker.expression("#{options.option '1','2','3','4',''}");
    }

    static String getThirdExpression() {
        return faker.expression("#{regexify '(Hi|Hello|Hey)'}");
    }
}
```

上面代码的输出：

```bash
First expression: Hey
Second expression: 4
Third expression: Hello
```

如果选项的数量太大，为随机值创建自定义提供程序是有意义的。

### 4.5. CSV

该指令根据其名称创建 CSV 格式的数据。但是，使用此指令可能会造成混淆。因为，在底层，两个具有完全不同签名的重载方法处理这个指令：

```java
public class Csv {
    private static final Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println("First expression:\n" + getFirstExpression());
        System.out.println("Second expression:\n" + getSecondExpression());
    }

    static String getSecondExpression() {
        final String secondExpressionString
              = "#{csv ',','\"','true','4','name_column','#{Name.first_name}','last_name_column','#{Name.last_name}'}";
        return faker.expression(secondExpressionString);
    }

    static String getFirstExpression() {
        final String firstExpressionString
              = "#{csv '4','name_column','#{Name.first_name}','last_name_column','#{Name.last_name}'}";
        return faker.expression(firstExpressionString);
    }
}
```

上面的指令使用表达式#{Name.first_name} 和 #{Name.last_name}。下一节将解释这些表达式的用法。

表达式中csv指令之后的值映射到上述方法的参数。这些方法的文档提供了附加信息。但是，有时解析这些指令可能会出现问题，在这种情况下，最好直接使用这些方法。上面的代码将产生以下输出：

```sql
First expression:
"name_column","last_name_column"
"Riley","Spinka"
"Lindsay","O'Conner"
"Sid","Rogahn"
"Prince","Wiegand"

Second expression:
"name_column","last_name_column"
"Jen","Schinner"
"Valeria","Walter"
"Mikki","Effertz"
"Deon","Bergnaum"
```

这是一种以编程方式生成模拟数据以供在应用程序外部使用的好方法。

### 4.6. JSON

另一种流行且经常使用的格式是 JSON。Datafaker 允许使用表达式生成 JSON 格式的数据：

```java
public class Json {
    private static final Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println(getExpression());
    }

    static String getExpression() {
        return faker.expression(
              "#{json 'person'," + "'#{json ''first_name'',''#{Name.first_name}'',''last_name'',''#{Name.last_name}''}'," +
                    "'address'," + "'#{json ''country'',''#{Address.country}'',''city'',''#{Address.city}''}'}");
    }
}
```

上面的代码产生以下输出：

```bash
{"person": {"first_name": "Dorian", "last_name": "Simonis"}, "address": {"country": "Cameroon", "city": "South Ernestine"}}
```

### 4.7. 方法调用

事实上，所有的表达式都只是方法调用，方法名称和参数作为字符串传递。 因此，上面的所有指令都反映了具有相同名称的方法。但是，有时使用纯文本创建模拟数据更方便：

```java
public class MethodInvocation {
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println("Name from a method: " + getNameFromMethod());
        System.out.println("Name from an expression: " + getNameFromExpression());
    }

    static String getNameFromMethod() {
        return faker.name().firstName();
    }

    static String getNameFromExpression() {
        return faker.expression("#{Name.first_Name}");
    }
}
```

现在很明显，带有csv 和 json 指令的表达式在内部使用了方法调用。这样，我们就可以调用任何方法在Faker对象上生成数据。虽然方法名称不区分大小写并且允许格式变化，但最好参考所用版本的文档来验证它。

此外，可以将参数传递给带有表达式的方法。我们在regexify和templatify指令的格式中部分地看到了这一点。尽管在某些情况下它可能有点麻烦且容易出错，但有时这是与Faker 交互的最方便的方式：

```java
public class MethodInvocationWithParams {
    public static int MIN = 1;
    public static int MAX = 10;
    public static String UNIT = "SECONDS";
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println("Duration from the method :" + getDurationFromMethod());
        System.out.println("Duration from the expression: " + getDurationFromExpression());
    }
    static Duration getDurationFromMethod() {
        return faker.date().duration(MIN, MAX, UNIT);
    }

    static String getDurationFromExpression() {
        return faker.expression("#{date.duration '1', '10', 'SECONDS'}");
    }
}
```

表达式的缺点之一是它们返回一个String对象。结果，这减少了我们可以对返回的对象进行的操作数量。上面的代码产生这个输出：

```bash
Duration from the method: PT6S
Duration from the expression: PT4S
```

## 5. 收藏

集合允许使用模拟数据创建列表。在这种情况下，元素可以是不同的类型。集合由最具体的类型参数化：集合中所有类的父类。让我们稍微了解一下并生成“星球大战”和“开始迷航”中的角色列表：

```java
public class Collection {
    public static int MIN = 1;
    public static int MAX = 100;
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println(getFictionalCharacters());
    }

    static List<String> getFictionalCharacters() {
        return faker.collection(
                    () -> faker.starWars().character(),
                    () -> faker.starTrek().character())
              .len(MIN, MAX)
              .generate();
    }
}
```

结果，我们得到了以下列表：

```
[Luke Skywalker, Wesley Crusher, Jean-Luc Picard, Greedo, Hikaru Sulu, William T. Riker]
```

因为我们集合中的两个供应商都返回 String 类型的值，所以结果列表将由String 参数化。 让我们检查一下我们混合不同类型数据的情况：

```java
public class MixedCollection {
    public static int MIN = 1;
    public static int MAX = 20;
    private static Faker faker = new Faker();

    public static void main(String[] args) {
        System.out.println(getMixedCollection());
    }

    static List<? extends Serializable> getMixedCollection() {
        return faker.collection(
                    () -> faker.date().birthday(),
                    () -> faker.name().fullName())
              .len(MIN, MAX)
              .generate();
    }
}
```

在这种情况下，最具体的String和 Timestamp 类是 Serializable。 输出将如下：

```bash
[1964-11-09 15:16:43.0, Devora Stamm DVM, 1980-01-11 15:18:00.0, 1989-04-28 05:13:54.0,
  2004-09-06 17:11:49.0, Irving Turcotte, Sherita Durgan I, 2004-03-08 00:45:57.0, 1979-08-25 22:48:50.0,
  Manda Hane, Latanya Hegmann, 1991-05-29 12:07:23.0, 1989-06-26 12:40:44.0, Kevin Quigley]
```

## 六，结论

Datafaker 是 Javafaker 的一个新的改进版本。本文介绍了 Datafaker 1.6.0 中引入的新功能，它提供了生成数据的新方法。不过，关于这个库还有更多需要了解，最好参考[官方文档](http://www.datafaker.net/documentation/getting-started/)和[GitHub 存储库](https://github.com/datafaker-net/datafaker/)，以获取有关 Datafaker 功能和特性的更多信息。