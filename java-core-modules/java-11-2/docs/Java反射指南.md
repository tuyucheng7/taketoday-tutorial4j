## 1. 概述

在本教程中，我们将探索Java反射，它允许我们检查和/或修改类、接口、字段和方法的运行时属性。当我们在编译时不知道它们的名字时，这特别有用。

此外，我们可以使用反射实例化新对象、调用方法以及获取或设置字段值。

## 2. 项目设置

**要使用Java反射，我们不需要包含任何特殊的jars、任何特殊的配置或Maven依赖项**。JDK附带了一组专门为此目的捆绑在[java.lang.reflect](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/package-summary.html)包中的类。

因此，我们需要做的就是将以下导入到我们的代码中：

```java
import java.lang.reflect.*;
```

为了访问实例的类、方法和字段信息，我们调用getClass方法，该方法返回对象的运行时Class表示形式。返回的Class对象提供了访问类信息的方法。

## 3. 简单例子

我们将看一个非常基本的示例，该示例在运行时检查一个简单Java对象的字段。

让我们创建一个简单的Person类，它只有name和age字段，没有任何方法。

这是Person类：

```java
public class Person {
    private String name;
    private int age;
}
```

现在，我们将使用Java反射来查找这个类的所有字段的名称。

为了体会反射的力量，让我们构造一个Person对象并使用Object作为引用类型：

```java
@Test
public void givenObject_whenGetsFieldNamesAtRuntime_thenCorrect() {
    Object person = new Person();
    Field[] fields = person.getClass().getDeclaredFields();

    List<String> actualFieldNames = getFieldNames(fields);

    assertTrue(Arrays.asList("name", "age")
        .containsAll(actualFieldNames));
}
```

这个测试向我们展示了我们能够从我们的person对象中获取一个Field对象数组，即使对对象的引用是该对象的父类型。

在上面的示例中，我们只对这些字段的名称感兴趣。但是还有很多事情可以做，我们将在下一节中看到这方面的例子。

请注意我们如何使用工具方法来提取实际的字段名称。

这是一个非常基本的代码：

```java
private static List<String> getFieldNames(Field[] fields) {
    List<String> fieldNames = new ArrayList<>();
    for (Field field : fields)
        fieldNames.add(field.getName());
    return fieldNames;
}
```

## 4. Java反射用例

在我们继续讨论Java反射的不同特性之前，我们将讨论一些我们可能会发现的常见用途。Java反射非常强大，可以通过多种方式派上用场。

例如，在许多情况下，我们对数据库表有一个命名约定。我们可以选择通过在表名前加上tbl_来增加一致性，这样一个包含学生数据的表就称为tbl_student_data。

在这种情况下，我们可以将保存学生数据的Java对象命名为Student或StudentData。然后使用CRUD范例，我们为每个操作提供一个入口点，因此Create操作只接收一个Object参数。

然后我们使用反射来检索对象名称和字段名称。此时，我们可以将此数据映射到数据库表，并将对象字段值分配给适当的数据库字段名称。

## 5. 检查Java类

在本节中，我们将探讨Java反射API中最基本的组件。正如我们前面提到的，Java类对象使我们能够访问任何对象的内部细节。

我们将检查内部细节，例如对象的类名、修饰符、字段、方法、实现的接口等。

### 5.1 准备

为了牢牢掌握应用于Java类的反射API并提供各种示例，让我们创建一个实现Eating接口的抽象Animal类。该接口定义了我们创建的任何具体Animal对象的进食行为。

首先，这里是吃的接口：

```java
public interface Eating {
    String eats();
}
```

下面是Eating接口的具体Animal实现：

```java
public abstract class Animal implements Eating {

    public static String CATEGORY = "domestic";
    private String name;

    protected abstract String getSound();

    // constructor, standard getters and setters omitted 
}
```

让我们还创建另一个名为Locomotion的接口来描述动物如何移动：

```java
public interface Locomotion {
    String getLocomotion();
}
```

我们现在将创建一个名为Goat的具体类，它扩展了Animal并实现了Locomotion。

由于超类实现了Eating，因此Goat也必须实现该接口的方法：

```java
public class Goat extends Animal implements Locomotion {

    @Override
    protected String getSound() {
        return "bleat";
    }

    @Override
    public String getLocomotion() {
        return "walks";
    }

    @Override
    public String eats() {
        return "grass";
    }

    // constructor omitted
}
```

从现在开始，我们将使用Java反射来检查出现在上述类和接口中的Java对象的各个方面。

### 5.2 类名

让我们首先从Class获取对象的名称：

```java
@Test
public void givenObject_whenGetsClassName_thenCorrect() {
    Object goat = new Goat("goat");
    Class<?> clazz = goat.getClass();

    assertEquals("Goat", clazz.getSimpleName());
    assertEquals("cn.tuyucheng.taketoday.reflection.Goat", clazz.getName());
    assertEquals("cn.tuyucheng.taketoday.reflection.Goat", clazz.getCanonicalName());
}
```

请注意，Class的getSimpleName方法返回对象的基本名称，因为它会出现在其声明中。然后其他两个方法返回完全限定的类名，包括包声明。

如果我们只知道它的完全限定类名，让我们也看看如何创建Goat类的对象：

```java
@Test
public void givenClassName_whenCreatesObject_thenCorrect(){
    Class<?> clazz = Class.forName("cn.tuyucheng.taketoday.reflection.Goat");

    assertEquals("Goat", clazz.getSimpleName());
    assertEquals("cn.tuyucheng.taketoday.reflection.Goat", clazz.getName());
    assertEquals("cn.tuyucheng.taketoday.reflection.Goat", clazz.getCanonicalName()); 
}
```

请注意，我们传递给静态forName方法的名称应该包含包信息。否则，我们将得到一个ClassNotFoundException。

### 5.3 类修饰符

我们可以通过调用getModifiers方法来确定类中使用的修饰符，该方法返回一个Integer。每个修饰符都是一个设置或清除的标志位。

[java.lang.reflect.Modifier](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Modifier.html)类提供静态方法来分析返回的Integer是否存在特定修饰符。

让我们确认一下我们上面定义的一些类的修饰符：

```java
@Test
public void givenClass_whenRecognisesModifiers_thenCorrect() {
    Class<?> goatClass = Class.forName("cn.tuyucheng.taketoday.reflection.Goat");
    Class<?> animalClass = Class.forName("cn.tuyucheng.taketoday.reflection.Animal");

    int goatMods = goatClass.getModifiers();
    int animalMods = animalClass.getModifiers();

    assertTrue(Modifier.isPublic(goatMods));
    assertTrue(Modifier.isAbstract(animalMods));
    assertTrue(Modifier.isPublic(animalMods));
}
```

我们能够检查位于我们正在导入项目的库jar中的任何类的修饰符。

在大多数情况下，我们可能需要使用forName方法而不是完整的实例化，因为在内存繁重的类的情况下这将是一个昂贵的过程。

### 5.4 包信息

通过使用Java反射，我们还能够获取有关任何类或对象的包的信息。此数据捆绑在[Package](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Package.html)类中，该类通过调用Class对象的getPackage方法返回。

让我们运行一个测试来检索包名称：

```java
@Test
public void givenClass_whenGetsPackageInfo_thenCorrect() {
    Goat goat = new Goat("goat");
    Class<?> goatClass = goat.getClass();
    Package pkg = goatClass.getPackage();

    assertEquals("cn.tuyucheng.taketoday.reflection", pkg.getName());
}
```

### 5.5 超类

我们还可以通过使用Java反射来获取任何Java类的超类。

在许多情况下，尤其是在使用库类或Java的内置类时，我们可能事先不知道我们正在使用的对象的超类。本小节将说明如何获取此信息。

让我们继续确定Goat的超类。

此外，我们还展示了java.lang.String类是java.lang.Object类的子类：

```java
@Test
public void givenClass_whenGetsSuperClass_thenCorrect() {
    Goat goat = new Goat("goat");
    String str = "any string";

    Class<?> goatClass = goat.getClass();
    Class<?> goatSuperClass = goatClass.getSuperclass();

    assertEquals("Animal", goatSuperClass.getSimpleName());
    assertEquals("Object", str.getClass().getSuperclass().getSimpleName());
}
```

### 5.6 实现的接口

使用Java反射，我们还能够获取给定类实现的接口列表。

让我们检索Goat类和Animal抽象类实现的接口的Class类型：

```java
@Test
public void givenClass_whenGetsImplementedInterfaces_thenCorrect(){
    Class<?> goatClass = Class.forName("cn.tuyucheng.taketoday.reflection.Goat");
    Class<?> animalClass = Class.forName("cn.tuyucheng.taketoday.reflection.Animal");

    Class<?>[] goatInterfaces = goatClass.getInterfaces();
    Class<?>[] animalInterfaces = animalClass.getInterfaces();

    assertEquals(1, goatInterfaces.length);
    assertEquals(1, animalInterfaces.length);
    assertEquals("Locomotion", goatInterfaces[0].getSimpleName());
    assertEquals("Eating", animalInterfaces[0].getSimpleName());
}
```

从断言中注意到，每个类只实现一个接口。检查这些接口的名称，我们发现Goat实现了Locomotion和Animal实现了Eating，就像它出现在我们的代码中一样。

我们可以看到Goat是抽象类Animal的子类，实现了接口方法eats()。然后，Goat也实现了Eating接口。

因此值得注意的是，只有类显式声明为使用implements关键字实现的那些接口才会出现在返回的数组中。

因此，即使一个类实现了接口方法，因为它的超类实现了该接口，但子类没有直接使用implements关键字声明该接口，该接口也不会出现在接口数组中。

### 5.7 构造函数、方法和字段

使用Java反射，我们能够检查**任何对象类的构造函数以及方法和字段**。

稍后，我们将能够对类的每个组件进行更深入的检查。但就目前而言，只需获取它们的名称并将它们与我们的预期进行比较就足够了。

让我们看看如何获取Goat类的构造函数：

```java
@Test
public void givenClass_whenGetsConstructor_thenCorrect(){
    Class<?> goatClass = Class.forName("cn.tuyucheng.taketoday.reflection.Goat");

    Constructor<?>[] constructors = goatClass.getConstructors();

    assertEquals(1, constructors.length);
    assertEquals("cn.tuyucheng.taketoday.reflection.Goat", constructors[0].getName());
}
```

我们还可以检查Animal类的字段：

```java
@Test
public void givenClass_whenGetsFields_thenCorrect(){
    Class<?> animalClass = Class.forName("cn.tuyucheng.taketoday.reflection.Animal");
    Field[] fields = animalClass.getDeclaredFields();

    List<String> actualFields = getFieldNames(fields);

    assertEquals(2, actualFields.size());
    assertTrue(actualFields.containsAll(Arrays.asList("name", "CATEGORY")));
}
```

我们可以类似地检查Animal类的方法：

```java
@Test
public void givenClass_whenGetsMethods_thenCorrect(){
    Class<?> animalClass = Class.forName("cn.tuyucheng.taketoday.reflection.Animal");
    Method[] methods = animalClass.getDeclaredMethods();
    List<String> actualMethods = getMethodNames(methods);

    assertEquals(4, actualMethods.size());
    assertTrue(actualMethods.containsAll(Arrays.asList("getName",
      "setName", "getSound")));
}
```

就像getFieldNames一样，我们添加了一个辅助方法来从Method对象数组中检索方法名称：

```java
private static List<String> getMethodNames(Method[] methods) {
    List<String> methodNames = new ArrayList<>();
    for (Method method : methods)
        methodNames.add(method.getName());
    return methodNames;
}
```

## 6. 检查构造函数

使用Java反射，我们可以检查任何类的构造函数，甚至可以**在运行时创建类对象**。[java.lang.reflect.Constructor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Constructor.html)类使这成为可能。

之前，我们只研究了如何获取Constructor对象的数组，我们可以从中获取构造函数的名称。

在本节中，我们将重点介绍如何检索特定的构造函数。

正如我们所知，在Java中，没有两个类的构造函数共享完全相同的方法签名。因此，我们将使用这种唯一性从多个构造函数中获取一个构造函数。

为了了解这个类的特性，我们将创建一个带有三个构造函数的Animal的Bird子类。

我们不会实现Locomotion，以便我们可以使用构造函数参数指定该行为，以增加更多变化：

```java
public class Bird extends Animal {
    private boolean walks;

    public Bird() {
        super("bird");
    }

    public Bird(String name, boolean walks) {
        super(name);
        setWalks(walks);
    }

    public Bird(String name) {
        super(name);
    }

    public boolean walks() {
        return walks;
    }

    // standard setters and overridden methods
}
```

让我们通过反射来确认这个类有三个构造函数：

```java
@Test
public void givenClass_whenGetsAllConstructors_thenCorrect() {
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Constructor<?>[] constructors = birdClass.getConstructors();

    assertEquals(3, constructors.length);
}
```

接下来，我们将通过按声明顺序传递构造函数的参数类类型来检索Bird类的每个构造函数：

```java
@Test
public void givenClass_whenGetsEachConstructorByParamTypes_thenCorrect(){
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");

    Constructor<?> cons1 = birdClass.getConstructor();
    Constructor<?> cons2 = birdClass.getConstructor(String.class);
    Constructor<?> cons3 = birdClass.getConstructor(String.class, boolean.class);
}
```

不需要断言，因为我们将得到NoSuchMethodException并且当具有给定参数类型的给定顺序的构造函数不存在时，测试将自动失败。

在最后一个测试中，我们将看到如何在运行时实例化对象，同时提供它们的参数：

```java
@Test
public void givenClass_whenInstantiatesObjectsAtRuntime_thenCorrect() {
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Constructor<?> cons1 = birdClass.getConstructor();
    Constructor<?> cons2 = birdClass.getConstructor(String.class);
    Constructor<?> cons3 = birdClass.getConstructor(String.class, boolean.class);

    Bird bird1 = (Bird) cons1.newInstance();
    Bird bird2 = (Bird) cons2.newInstance("Weaver bird");
    Bird bird3 = (Bird) cons3.newInstance("dove", true);

    assertEquals("bird", bird1.getName());
    assertEquals("Weaver bird", bird2.getName());
    assertEquals("dove", bird3.getName());

    assertFalse(bird1.walks());
    assertTrue(bird3.walks());
}
```

我们通过调用Constructor类的newInstance方法并按照声明的顺序传递所需的参数来实例化类对象。然后我们将结果转换为所需的类型。

也可以使用Class.newInstance()方法调用默认构造函数。但是，这种方法自Java 9以来已被弃用，我们不应该在现代Java项目中使用它。

对于bird1，我们使用默认构造函数自动将Bird代码中的名称设置为bird，并通过测试确认这一点。

然后我们只用一个名字实例化bird2并进行测试。请记住，当我们不设置运动行为时，它默认为false，如最后两个断言所示。

## 7. 检查字段

以前，我们只检查字段的名称。在本节中，**我们将展示如何在运行时获取和设置它们的值**。

有两种主要方法用于在运行时检查类的字段：getFields()和getField(fieldName)。

getFields()方法返回相关类的所有可访问公共字段，它将返回类和所有超类中的所有公共字段。

例如，当我们在Bird类上调用此方法时，我们只会获得其超类Animal的CATEGORY字段，因为Bird本身并没有声明任何公共字段：

```java
@Test
public void givenClass_whenGetsPublicFields_thenCorrect() {
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Field[] fields = birdClass.getFields();

    assertEquals(1, fields.length);
    assertEquals("CATEGORY", fields[0].getName());
}
```

此方法还有一个称为getField的变体，它通过获取字段名称仅返回一个Field对象：

```java
@Test
public void givenClass_whenGetsPublicFieldByName_thenCorrect() {
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Field field = birdClass.getField("CATEGORY");

    assertEquals("CATEGORY", field.getName());
}
```

我们无法访问在超类中声明且未在子类中声明的私有字段。这就是我们无法访问name字段的原因。

但是，我们可以通过调用getDeclaredFields方法来检查我们正在处理的类中声明的私有字段：

```java
@Test
public void givenClass_whenGetsDeclaredFields_thenCorrect(){
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Field[] fields = birdClass.getDeclaredFields();

    assertEquals(1, fields.length);
    assertEquals("walks", fields[0].getName());
}
```

如果我们知道字段的名称，我们还可以使用它的其他变体：

```java
@Test
public void givenClass_whenGetsFieldsByName_thenCorrect() {
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Field field = birdClass.getDeclaredField("walks");

    assertEquals("walks", field.getName());
}
```

如果我们输入错误的字段名称或输入不存在的字段，我们将收到NoSuchFieldException。

现在我们将获取字段类型：

```java
@Test
public void givenClassField_whenGetsType_thenCorrect() {
    Field field = Class.forName("cn.tuyucheng.taketoday.reflection.Bird")
        .getDeclaredField("walks");
    Class<?> fieldClass = field.getType();

    assertEquals("boolean", fieldClass.getSimpleName());
}
```

接下来，让我们看看如何访问字段值并对其进行修改。

要获取字段的值，更不用说设置它了，我们必须首先通过调用Field对象的setAccessible方法设置它是可访问的，并将布尔值true传递给它：

```java
@Test
public void givenClassField_whenSetsAndGetsValue_thenCorrect() {
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Bird bird = (Bird) birdClass.getConstructor().newInstance();
    Field field = birdClass.getDeclaredField("walks");
    field.setAccessible(true);

    assertFalse(field.getBoolean(bird));
    assertFalse(bird.walks());
    
    field.set(bird, true);
    
    assertTrue(field.getBoolean(bird));
    assertTrue(bird.walks());
}
```

在上面的测试中，我们确定walks字段的值在设置为true之前确实为false。

请注意我们如何使用Field对象来设置和获取值，方法是将我们正在处理的类的实例以及我们希望该字段在该对象中具有的新值传递给它。

关于Field对象需要注意的一件重要事情是，当它被声明为public static时，我们不需要包含它们的类的实例。

我们可以在它的位置传递null并仍然获得该字段的默认值：

```java
@Test
public void givenClassField_whenGetsAndSetsWithNull_thenCorrect(){
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Field field = birdClass.getField("CATEGORY");
    field.setAccessible(true);

    assertEquals("domestic", field.get(null));
}
```

## 8. 检查方法

在前面的示例中，我们仅使用反射来检查方法名称。但是，Java反射比这更强大。

使用Java反射，我们可以**在运行时调用方法**并将其所需的参数传递给它们，就像我们为构造函数所做的那样。同样，我们也可以通过指定每个方法的参数类型来调用重载方法。

就像字段一样，我们使用两种主要方法来检索类方法。getMethods方法返回类和超类的所有公共方法的数组。

这意味着通过这个方法，我们可以获得java.lang.Object类的公共方法，例如toString、hashCode和notifyAll：

```java
@Test
public void givenClass_whenGetsAllPublicMethods_thenCorrect(){
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Method[] methods = birdClass.getMethods();
    List<String> methodNames = getMethodNames(methods);

    assertTrue(methodNames.containsAll(Arrays
        .asList("equals", "notifyAll", "hashCode", "walks", "eats", "toString")));
}
```

要仅获取我们感兴趣的类的公共方法，我们必须使用getDeclaredMethods方法：

```java
@Test
public void givenClass_whenGetsOnlyDeclaredMethods_thenCorrect(){
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    List<String> actualMethodNames = getMethodNames(birdClass.getDeclaredMethods());

    List<String> expectedMethodNames = Arrays
        .asList("setWalks", "walks", "getSound", "eats");

    assertEquals(expectedMethodNames.size(), actualMethodNames.size());
    assertTrue(expectedMethodNames.containsAll(actualMethodNames));
    assertTrue(actualMethodNames.containsAll(expectedMethodNames));
}
```

这些方法中的每一个都有一个单一的变体，它返回一个我们知道其名称的Method对象：

```java
@Test
public void givenMethodName_whenGetsMethod_thenCorrect() throws Exception {
    Bird bird = new Bird();
    Method walksMethod = bird.getClass().getDeclaredMethod("walks");
    Method setWalksMethod = bird.getClass().getDeclaredMethod("setWalks", boolean.class);

    assertTrue(walksMethod.canAccess(bird));
    assertTrue(setWalksMethod.canAccess(bird));
}
```

请注意我们如何检索各个方法并指定它们采用的参数类型。那些不接受参数类型的会用一个空的变量参数来检索，只剩下一个参数，方法名。

接下来，我们将展示如何在运行时调用方法。

我们知道默认情况下Bird类的walks属性是false。

我们想调用它的setWalks方法并将其设置为true：

```java
@Test
public void givenMethod_whenInvokes_thenCorrect() {
    Class<?> birdClass = Class.forName("cn.tuyucheng.taketoday.reflection.Bird");
    Bird bird = (Bird) birdClass.getConstructor().newInstance();
    Method setWalksMethod = birdClass.getDeclaredMethod("setWalks", boolean.class);
    Method walksMethod = birdClass.getDeclaredMethod("walks");
    boolean walks = (boolean) walksMethod.invoke(bird);

    assertFalse(walks);
    assertFalse(bird.walks());

    setWalksMethod.invoke(bird, true);

    boolean walks2 = (boolean) walksMethod.invoke(bird);
    assertTrue(walks2);
    assertTrue(bird.walks());
}
```

请注意我们如何首先调用walks方法并将返回类型转换为适当的数据类型，然后检查其值。我们稍后调用setWalks方法来更改该值并再次测试。

## 9. 总结

在本文中，我们介绍了Java反射API，并研究了如何使用它在运行时检查类、接口、字段和方法，而无需在编译时事先了解它们的内部结构。