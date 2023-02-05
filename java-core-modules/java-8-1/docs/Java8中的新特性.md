## 1. 概述

在本教程中，我们将快速浏览Java 8中一些最重要的新特性。

我们将讨论接口默认和静态方法、方法引用和Optional。

我们已经介绍了Java 8发行版的一些特性-[Stream API](https://www.baeldung.com/java-8-streams-introduction)，[lambda表达式和函数接口](https://www.baeldung.com/java-8-lambda-expressions-tips)。因为它们是全面的主题，值得单独查看。

## 2. 接口中的默认和静态方法

在Java 8之前，接口只能包含公共抽象方法。如果不强制所有实现类创建新方法的实现，就不可能向现有接口添加新功能，也不可能创建带有实现的接口方法。

从Java 8开始，接口可以具有**默认**和**静态**方法，尽管这些方法是在接口中声明的，但它们已经具有定义的行为。

### 2.1 静态方法

考虑Vehicle接口中的以下方法定义：

```java
public interface Vehicle {

    static String producer() {
        return "N&F Vehicles";
    }
}
```

静态producer()方法只能通过接口和在接口内部使用，它不能被实现类重写。

要在接口外部调用它，应该使用静态方法的标准调用方式，即类名.方法名：

```java
@Test
void callDefaultInterfaceMethods_whenExpectedResults_thenCorrect() {
    String producer = Vehicle.producer();
    assertEquals(producer, "N&F Vehicles");
}
```

### 2.2 默认方法

默认方法是使用**default关键字**声明的。这些方法可以通过实现类的实例访问，并且可以被重写。

让我们为Vehicle接口添加一个默认方法，该方法也调用了静态方法producer()：

```java
default String getOverview() {
    return "ATV mode by " + producer();
}
```

假设VehicleImpl类实现了该接口。

为了执行getOverview()方法，应该创建这个类的一个实例：

```java
@Test
void callStaticInterfaceMethodsMethods_whenExpectedResults_thenCorrect() {
    Vehicle vehicle = new VehicleImpl();
    String overview = vehicle.getOverview();

    assertEquals(overview, "ATV made by N&F Vehicles");
}
```

## 3. 方法引用

方法引用可以用作仅调用现有方法的lambda表达式的更短且更易读的替代方法。方法引用有四种变体。

### 3.1 对静态方法的引用

对静态方法的引用语法为**ContainingClass::methodName**。

我们将尝试在Stream API的帮助下计算List<String\>中的所有空字符串：

```java
public class User {
    private String name;
    private Address address;

    // constructors getters and setters ...

    public static boolean isRealUser(User user) {
        return true;
    }
}
```

```java
@Test
void checkStaticMethodReferences_whenWork_thenCorrect() {
    List<User> users = new ArrayList<>();
    users.add(new User());
    users.add(new User());
    
    boolean isReal = users.stream().anyMatch(u -> User.isRealUser(u));
    
    assertTrue(isReal);
}
```

让我们仔细看看anyMatch()方法中的lambda表达式，它只是调用User类的静态方法isRealUser(User user)。

因此，它可以用对静态方法的引用代替：

```java
boolean isReal = list.stream().anyMatch(User::isRealUser);
```

这种类型的代码看起来信息量更大。

### 3.2 对实例方法的引用

对实例方法的引用语法为**containingInstance::methodName**。

以下代码调用User类的isLegalName(String string)方法，该方法校验输入参数：

```java
public boolean isLegalName(String name) {
    return name.length() > 3 && name.length() < 16;
}
```

```java
@Test
void checkInstanceMethodReferences_whenWork_thenCorrect() {
    User user = new User();
    boolean isLegalName = list.stream().anyMatch(user::isLegalName);
    
    assertTrue(isLegalName);
}
```

### 3.3 引用特定类型对象的实例方法

这种方法引用的语法为**ContainingType::methodName**。

让我们看一个例子：

```java
private List<String> list;

@BeforeEach
void init() {
    list = new ArrayList<>();
    list.add("One");
    list.add("OneAndOnly");
    list.add("Derek");
    list.add("Change");
    list.add("factory");
    list.add("justBefore");
    list.add("Italy");
    list.add("Italy");
    list.add("Thursday");
    list.add("");
    list.add("");
}

@Test
void checkParticularTypeReferences_whenWork_thenCorrect() {
    long count = list.stream().filter(String::isEmpty).count();
    
    assertEquals(count, 2);
}
```

### 3.4 对构造方法的引用

对构造方法的引用语法为**ClassName::new**。

由于Java中的构造函数是一种特殊的方法，因此方法引用也同样适用于构造方法，借助new作为方法名称：

```java
Stream<User> stream = list.stream().map(User::new);
```

## 4. Optional<T\>

在Java 8之前，开发人员必须仔细检查他们引用的值，因为随时可能抛出NullPointerException(NPE)。所有这些检查都需要非常繁琐且容易出错的样板代码。

Java 8 Optional<T\>类可以帮助处理有可能遇到NPE的情况。它用作T类型对象的容器，如果该值不为null，它可以返回此对象的引用。当该容器内的值为null时，它允许执行一些预定义的操作，而不是抛出NPE。

### 4.1 创建Optional<T\>

Optional类的实例可以通过它的静态方法创建。

让我们看看如何返回一个空的Optional：

```java
Optional<String> optional = Optional.empty();
```

接下来，我们返回一个包含非空值的Optional：

```java
String str = "value";
Optional<String> optional = Optional.of(str);
```

最后，下面介绍如何返回具有特定值的Optional或如果参数为null，则返回空Optional：

```java
Optional<String> optional = Optional.ofNullable(getString());
```

### 4.2 Optional<T\>的用法

假设我们希望得到一个List<String\>，在为null的情况下，我们想用一个新的ArrayList<String\>实例来替换它。

对于Java 8之前的代码，我们需要做这样的事情：

```java
List<String> list = getList();
List<String> listOpt = list != null ? list : new ArrayList<>();
```

在Java 8中，可以使用更简短的代码实现相同的功能：

```java
List<String> listOpt = getList().orElseGet(() -> new ArrayList<>());
```

当我们以原始方式访问某个对象的字段时，代码可能会变得更加冗长。

假设我们有一个User类型的对象，它有一个Address类型的字段，并且Address包含一个String类型的字段street，如果存在street的值不为空，我们需要返回street的值，如果street为空，我们需要返回一个默认值：

```java
User user = getUser();
if (user != null) {
    Address address = user.getAddress();
    if (address != null) {
        String street = address.getStreet();
        if (street != null) {
            return street;
        }
    }
}
return "not specified";
```

这可以通过Optional来简化：

```java
Optional<User> user = Optional.ofNullable(getUser());
String result = user
    .map(User::getAddress)
    .map(Address::getStreet)
    .orElse("not specified");
```

在这个例子中，我们使用map()方法将调用getAddress()的结果转换为Optional<Address\>，并将getStreet()的结果转换为Optional<String\>。如果这些方法中的任何一个返回null，map()方法将返回一个空的Optional。

现在想象一下，我们的getter返回Optional<T\>。

在这种情况下，我们应该使用flatMap()方法而不是map()：

```java
Optional<OptionalUser> optionalUser = Optional.ofNullable(getOptionalUser());
String result = optionalUser
    .flatMap(OptionalUser::getAddress)
    .flatMap(OptionalAddress::getStreet)
    .orElse("not specified");
```

Optional的另一个用例是更改NPE，但有另一个例外。

因此，就像我们之前所做的那样，让我们尝试以Java 8之前的风格执行此操作：

```java
String value = null;
String result = "";
try {
    result = value.toUpperCase();
} catch (NullPointerException exception) {
    throw new CustomException();
}
```

如果我们使用Optional<String\>，答案会更易读、更简单：

```java
String value = null;
Optional<String> valueOpt = Optional.ofNullable(value);
String result = valueOpt.orElseThrow(CustomException::new).toUpperCase();
```

请注意，如何在我们的应用程序中使用Optional以及出于什么目的是一个严肃且有争议的设计决策，对其所有优缺点的解释超出了本文的范围。但是有很多有趣的文章专门讨论这个问题，[这个](https://blog.joda.org/2014/11/optional-in-java-se-8.html)和[这个](https://blog.joda.org/2015/08/java-se-8-optional-pragmatic-approach.html)可能对深入挖掘非常有帮助。

## 5. 总结

在本文中，我们简要讨论了Java 8中一些有趣的新特性。

当然，还有许多其他添加和改进分布在许多Java 8 JDK包和类中。

但是，本文中说明的信息是探索和了解其中一些新功能的良好起点。