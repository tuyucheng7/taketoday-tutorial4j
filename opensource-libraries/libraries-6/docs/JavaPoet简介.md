## 1. 概述

在本教程中，我们将探讨 [JavaPoet](https://github.com/square/javapoet)库的基本功能。

JavaPoet由[Square](https://square.github.io/)开发，提供 API 来生成Java源代码。它可以生成原始类型、引用类型及其变体(如类、接口、枚举类型、匿名内部类)、字段、方法、参数、注解和 Javadoc。

JavaPoet 自动管理依赖类的导入。它还使用 Builder 模式来指定生成Java代码的逻辑。

## 2.Maven依赖

为了使用 JavaPoet，我们可以直接下载最新的[JAR 文件，或者在我们的](https://search.maven.org/artifact/com.squareup/javapoet/1.11.1/jar)pom.xml中定义如下依赖：

```xml
<dependency>
    <groupId>com.squareup</groupId>
    <artifactId>javapoet</artifactId>
    <version>1.10.0</version>
</dependency>
```

## 三、方法说明

首先，让我们看一下方法规范。要生成方法，我们只需调用MethodSpec类的methodBuilder()方法即可。我们将生成的方法名称指定为methodBuilder() 方法的字符串参数 。

我们可以使用 addStatement() 方法生成任何以分号结尾的单个逻辑语句。同时，我们可以在一个控制流中定义一个用大括号括起来的控制流，比如if-else块，或者for循环。

这是一个简单的例子——生成sumOfTen()方法，该方法将计算从 0 到 10 的数字的总和：

```java
MethodSpec sumOfTen = MethodSpec
  .methodBuilder("sumOfTen")
  .addStatement("int sum = 0")
  .beginControlFlow("for (int i = 0; i <= 10; i++)")
  .addStatement("sum += i")
  .endControlFlow()
  .build();
```

这将产生以下输出：

```java
void sumOfTen() {
    int sum = 0;
    for (int i = 0; i <= 10; i++) {
        sum += i;
    }
}
```

## 4.代码块

我们还可以将一个或多个控制流和逻辑语句包装到一个代码块中：

```java
CodeBlock sumOfTenImpl = CodeBlock
  .builder()
  .addStatement("int sum = 0")
  .beginControlFlow("for (int i = 0; i <= 10; i++)")
  .addStatement("sum += i")
  .endControlFlow()
  .build();
```

其中产生：

```java
int sum = 0;
for (int i = 0; i <= 10; i++) {
    sum += i;
}
```

我们可以通过调用addCode()并提供sumOfTenImpl对象来简化MethodSpec中的早期逻辑：

```java
MethodSpec sumOfTen = MethodSpec
  .methodBuilder("sumOfTen")
  .addCode(sumOfTenImpl)
  .build();
```

代码块也适用于其他规范，例如类型和 Javadoc。

## 五、现场说明

接下来——让我们探索字段规范逻辑。

为了生成一个字段，我们使用FieldSpec类的builder()方法：

```java
FieldSpec name = FieldSpec
  .builder(String.class, "name")
  .addModifiers(Modifier.PRIVATE)
  .build();
```

这将生成以下字段：

```java
private String name;
```

我们还可以通过调用 initializer()方法来初始化字段的默认值：

```java
FieldSpec defaultName = FieldSpec
  .builder(String.class, "DEFAULT_NAME")
  .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
  .initializer(""Alice"")
  .build();
```

其中产生：

```java
private static final String DEFAULT_NAME = "Alice";
```

## 六、参数说明

现在让我们探讨参数规范逻辑。

如果我们想向方法添加参数，我们可以 在构建器的函数调用链中调用addParameter() 。

对于更复杂的参数类型，我们可以使用ParameterSpec构建器：

```java
ParameterSpec strings = ParameterSpec
  .builder(
    ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(String.class)), 
    "strings")
  .build();
```

我们还可以添加方法的修饰符，例如public和/或 static：

```java
MethodSpec sumOfTen = MethodSpec
  .methodBuilder("sumOfTen")
  .addParameter(int.class, "number")
  .addParameter(strings)
  .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
  .addCode(sumOfTenImpl)
  .build();
```

生成的Java代码如下所示：

```java
public static void sumOfTen(int number, List<String> strings) {
    int sum = 0;
    for (int i = 0; i <= 10; i++) {
        sum += i;
    }
}
```

## 7. 类型说明

探索完方法、字段和参数的生成方式，现在我们来看看类型规范。

要声明一个类型，我们可以使用 可以构建类、接口和枚举类型的TypeSpec。

### 7.1. 生成类

为了生成一个类，我们可以使用 TypeSpec 类的classBuilder ()方法。

我们还可以指定其修饰符，例如 public 和final访问修饰符。除了类修饰符之外，我们还可以使用已经提到的 FieldSpec 和 MethodSpec 类来指定 字段和方法。

请注意， 在生成接口或匿名内部类时， addField()和addMethod()方法也可用。

让我们看一下下面的类构建器示例：

```java
TypeSpec person = TypeSpec
  .classBuilder("Person")
  .addModifiers(Modifier.PUBLIC)
  .addField(name)
  .addMethod(MethodSpec
    .methodBuilder("getName")
    .addModifiers(Modifier.PUBLIC)
    .returns(String.class)
    .addStatement("return this.name")
    .build())
  .addMethod(MethodSpec
    .methodBuilder("setName")
    .addParameter(String.class, "name")
    .addModifiers(Modifier.PUBLIC)
    .returns(String.class)
    .addStatement("this.name = name")
    .build())
  .addMethod(sumOfTen)
  .build();
```

生成的代码如下所示：

```java
public class Person {
    private String name;

    public String getName() {
        return this.name;
    }

    public String setName(String name) {
        this.name = name;
    }

    public static void sumOfTen(int number, List<String> strings) {
        int sum = 0;
        for (int i = 0; i <= 10; i++) {
            sum += i;
        }
    }
}
```

### 7.2. 生成接口

要生成Java接口，我们使用TypeSpec的interfaceBuilder()方法。

我们还可以通过在addModifiers()中指定DEFAULT修饰符值来定义默认方法：

```java
TypeSpec person = TypeSpec
  .interfaceBuilder("Person")
  .addModifiers(Modifier.PUBLIC)
  .addField(defaultName)
  .addMethod(MethodSpec
    .methodBuilder("getName")
    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
    .build())
  .addMethod(MethodSpec
    .methodBuilder("getDefaultName")
    .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
    .addCode(CodeBlock
      .builder()
      .addStatement("return DEFAULT_NAME")
      .build())
    .build())
  .build();
```

它将生成以下Java代码：

```java
public interface Person {
    private static final String DEFAULT_NAME = "Alice";

    void getName();

    default void getDefaultName() {
        return DEFAULT_NAME;
    }
}
```

### 7.3. 生成枚举

要生成枚举类型，我们可以使用 TypeSpec 的enumBuilder ()方法。要指定每个枚举值，我们可以调用 addEnumConstant()方法：

```java
TypeSpec gender = TypeSpec
  .enumBuilder("Gender")
  .addModifiers(Modifier.PUBLIC)
  .addEnumConstant("MALE")
  .addEnumConstant("FEMALE")
  .addEnumConstant("UNSPECIFIED")
  .build();
```

上述enumBuilder()逻辑的输出是：

```java
public enum Gender {
    MALE,
    FEMALE,
    UNSPECIFIED
}
```

### 7.4. 生成匿名内部类

要生成匿名内部类，我们可以使用TypeSpec类的anonymousClassBuilder()方法。请注意，我们必须在addSuperinterface()方法中指定父类。否则，它将使用默认的父类，即Object：

```java
TypeSpec comparator = TypeSpec
  .anonymousClassBuilder("")
  .addSuperinterface(ParameterizedTypeName.get(Comparator.class, String.class))
  .addMethod(MethodSpec
    .methodBuilder("compare")
    .addModifiers(Modifier.PUBLIC)
    .addParameter(String.class, "a")
    .addParameter(String.class, "b")
    .returns(int.class)
    .addStatement("return a.length() - b.length()")
    .build())
  .build();
```

这将生成以下Java代码：

```java
new Comparator<String>() {
    public int compare(String a, String b) {
        return a.length() - b.length();
    }
});
```

## 8.注解规范

要向生成的代码添加注解，我们可以 在MethodSpec或FieldSpec构建器类中调用addAnnotation()方法：

```java
MethodSpec sumOfTen = MethodSpec
  .methodBuilder("sumOfTen")
  .addAnnotation(Override.class)
  .addParameter(int.class, "number")
  .addParameter(strings)
  .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
  .addCode(sumOfTenImpl)
  .build();
```

其中产生：

```java
@Override
public static void sumOfTen(int number, List<String> strings) {
    int sum = 0;
    for (int i = 0; i <= 10; i++) {
        sum += i;
    }
}
```

如果我们需要指定成员值，我们可以调用 AnnotationSpec类的addMember()方法：

```java
AnnotationSpec toString = AnnotationSpec
  .builder(ToString.class)
  .addMember("exclude", ""name"")
  .build();
```

这将生成以下注解：

```java
@ToString(
    exclude = "name"
)
```

## 9. 生成 Javadoc

可以使用CodeBlock 生成 Javadoc，或者直接指定值：

```java
MethodSpec sumOfTen = MethodSpec
  .methodBuilder("sumOfTen")
  .addJavadoc(CodeBlock
    .builder()
    .add("Sum of all integers from 0 to 10")
    .build())
  .addAnnotation(Override.class)
  .addParameter(int.class, "number")
  .addParameter(strings)
  .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
  .addCode(sumOfTenImpl)
  .build();
```

这将生成以下Java代码：

```java
/
  Sum of all integers from 0 to 10
 /
@Override
public static void sumOfTen(int number, List<String> strings) {
    int sum = 0;
    for (int i = 0; i <= 10; i++) {
        sum += i;
    }
}
```

## 10.格式化

让我们重新检查[第 5 节中](https://www.baeldung.com/java-poet#fieldspec)FieldSpec 初始值设定项的示例，其中包含用于转义“Alice”字符串值的转义字符：

```java
initializer(""Alice"")
```

[我们在第 8 节](https://www.baeldung.com/java-poet#annotationspec)定义注解的排除成员时也有类似的例子：

```java
addMember("exclude", ""name"")
```

当我们的 JavaPoet 代码增长并且有很多类似的字符串转义或字符串连接语句时，它会变得笨拙。

JavaPoet 中的字符串格式化功能使beginControlFlow()、 addStatement() 或initializer()方法中的字符串格式化更容易。语法类似于Java 中的String.format()功能。它可以帮助格式化文字、字符串、类型和名称。

### 10.1. 文字格式

JavaPoet在输出中将$L替换为文字值。我们可以在参数中指定任何原始类型和字符串值：

```java
private MethodSpec generateSumMethod(String name, int from, int to, String operator) {
    return MethodSpec
      .methodBuilder(name)
      .returns(int.class)
      .addStatement("int sum = 0")
      .beginControlFlow("for (int i = $L; i <= $L; i++)", from, to)
      .addStatement("sum = sum $L i", operator)
      .endControlFlow()
      .addStatement("return sum")
      .build();
}
```

如果我们使用指定的以下值调用generateSumMethod() ：

```java
generateSumMethod("sumOfOneHundred", 0, 100, "+");
```

JavaPoet 将生成以下输出：

```java
int sumOfOneHundred() {
    int sum = 0;
    for (int i = 0; i <= 100; i++) {
        sum = sum + i;
    }
    return sum;
}
```

### 10.2. 字符串 格式

字符串格式化生成一个带引号的值，在Java中专指String类型。JavaPoet在输出中将$S替换为字符串值：

```java
private static MethodSpec generateStringSupplier(String methodName, String fieldName) {
    return MethodSpec
      .methodBuilder(methodName)
      .returns(String.class)
      .addStatement("return $S", fieldName)
      .build();
}
```

如果我们调用generateGetter() 方法并提供这些值：

```java
generateStringSupplier("getDefaultName", "Bob");
```

我们将得到以下生成的Java代码：

```java
String getDefaultName() {
    return "Bob";
}
```

### 10.3. 类型格式化

JavaPoet在生成的Java代码中将$T替换为一个类型。JavaPoet 自动处理导入语句中的类型。如果我们以文字形式提供类型，JavaPoet 将不会处理导入。

```java
MethodSpec getCurrentDateMethod = MethodSpec
  .methodBuilder("getCurrentDate")
  .returns(Date.class)
  .addStatement("return new $T()", Date.class)
  .build();
```

JavaPoet 将生成以下输出：

```java
Date getCurrentDate() {
    return new Date();
}
```

### 10.4. 名称格式

如果我们需要引用变量/参数、字段或方法的名称，我们可以 在 JavaPoet 的字符串格式化程序中使用$N 。

我们可以将之前的 getCurrentDateMethod()添加到新的引用方法中：

```java
MethodSpec dateToString = MethodSpec
  .methodBuilder("getCurrentDateAsString")
  .returns(String.class)
  .addStatement(
    "$T formatter = new $T($S)", 
    DateFormat.class, 
    SimpleDateFormat.class, 
    "MM/dd/yyyy HH:mm:ss")
  .addStatement("return formatter.format($N())", getCurrentDateMethod)
  .build();
```

其中产生：

```java
String getCurrentDateAsString() {
    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    return formatter.format(getCurrentDate());
}
```

## 11. 生成 Lambda 表达式

我们可以利用我们已经探索过的功能来生成 Lambda 表达式。例如， 多次打印名称字段或变量的代码块：

```java
CodeBlock printNameMultipleTimes = CodeBlock
  .builder()
  .addStatement("$T<$T> names = new $T<>()", List.class, String.class, ArrayList.class)
  .addStatement("$T.range($L, $L).forEach(i -> names.add(name))", IntStream.class, 0, 10)
  .addStatement("names.forEach(System.out::println)")
  .build();
```

该逻辑生成以下输出：

```java
List<String> names = new ArrayList<>();
IntStream.range(0, 10).forEach(i -> names.add(name));
names.forEach(System.out::println);
```

## 12. 使用JavaFile生成输出

JavaFile类有助于配置和生成所生成代码的输出 。要生成Java代码，我们只需构建JavaFile， 提供包名称和TypeSpec对象的实例。

### 12.1. 代码缩进

默认情况下，JavaPoet 使用两个空格进行缩进。为了保持一致性，本教程中的所有示例都使用 4 个空格缩进，这是通过indent()方法配置的：

```java
JavaFile javaFile = JavaFile
  .builder("com.baeldung.javapoet.person", person)
  .indent("    ")
  .build();
```

### 12.2. 静态导入

如果我们需要添加静态导入，我们可以通过调用addStaticImport()方法在JavaFile中定义类型和具体方法名称：

```java
JavaFile javaFile = JavaFile
  .builder("com.baeldung.javapoet.person", person)
  .indent("    ")
  .addStaticImport(Date.class, "UTC")
  .addStaticImport(ClassName.get("java.time", "ZonedDateTime"), "")
  .build();
```

生成以下静态导入语句：

```java
import static java.util.Date.UTC;
import static java.time.ZonedDateTime.;
```

### 12.3. 输出

writeTo ()方法提供将代码写入多个目标的功能，例如标准输出流 ( System.out ) 和File。

要将Java代码写入标准输出流，我们只需调用 writeTo() 方法，并提供System.out作为参数：

```java
JavaFile javaFile = JavaFile
  .builder("com.baeldung.javapoet.person", person)
  .indent("    ")
  .addStaticImport(Date.class, "UTC")
  .addStaticImport(ClassName.get("java.time", "ZonedDateTime"), "")
  .build();

javaFile.writeTo(System.out);
```

writeTo ()方法还接受java.nio.file.Path和java.io.File。我们可以提供相应的 Path或 File对象，以便将Java源代码文件生成到目标文件夹/路径中：

```java
Path path = Paths.get(destinationPath);
javaFile.writeTo(path);
```

有关JavaFile的更多详细信息，请参阅[Javadoc](https://square.github.io/javapoet/javadoc/javapoet/com/squareup/javapoet/JavaFile.html)。

## 13.总结

本文介绍了 JavaPoet 的功能，例如生成方法、字段、参数、类型、注解和 Javadoc。

JavaPoet 专为代码生成而设计。如果我们想用Java进行元编程，JavaPoet 从 1.10.0 版本开始不支持代码编译和运行。