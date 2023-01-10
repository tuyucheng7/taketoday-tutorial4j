## 1. 概述

[Apache Commons Lang 3 库](https://commons.apache.org/proper/commons-lang/) 是一个流行的、功能齐全的实用程序类包，旨在扩展JavaAPI 的功能。

该库的库非常丰富，从字符串、数组和数字操作、反射和并发，到几个有序数据结构的实现，如对和三元组(通常称为[元组](https://en.wikipedia.org/wiki/Tuple))。

在本教程中，我们将深入探讨库中最有用的实用程序类。

## 2. Maven 依赖

像往常一样，要开始使用 Apache Commons Lang 3，我们首先需要添加[Maven 依赖](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3&core=gav)项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

## 3. StringUtils类

我们将在本介绍性综述中介绍的第一个实用程序类是[StringUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/StringUtils.html)。

顾名思义， StringUtils允许我们执行一堆 null-safe字符串操作，以补充/扩展[java.lang.String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html)提供的开箱即用的操作。

让我们开始展示对给定字符串执行多项检查的实用方法集，例如确定字符串是否为空白、空、小写、大写、字母数字等：

```java
@Test
public void whenCalledisBlank_thenCorrect() {
    assertThat(StringUtils.isBlank(" ")).isTrue();
}
    
@Test
public void whenCalledisEmpty_thenCorrect() {
    assertThat(StringUtils.isEmpty("")).isTrue();
}
    
@Test
public void whenCalledisAllLowerCase_thenCorrect() {
    assertThat(StringUtils.isAllLowerCase("abd")).isTrue();
}
    
@Test
public void whenCalledisAllUpperCase_thenCorrect() {
    assertThat(StringUtils.isAllUpperCase("ABC")).isTrue();
}
    
@Test
public void whenCalledisMixedCase_thenCorrect() {
    assertThat(StringUtils.isMixedCase("abC")).isTrue();
}
    
@Test
public void whenCalledisAlpha_thenCorrect() {
    assertThat(StringUtils.isAlpha("abc")).isTrue();
}
    
@Test
public void whenCalledisAlphanumeric_thenCorrect() {
    assertThat(StringUtils.isAlphanumeric("abc123")).isTrue();
}

```

当然，StringUtils类还实现了许多其他方法，为简单起见，我们在此省略了这些方法。

对于检查或将某种类型的转换算法应用于给定字符串的其他一些其他方法，请[查看本教程](https://www.baeldung.com/string-processing-commons-lang)。

我们上面介绍的那些非常简单，因此单元测试应该是不言自明的。

## 4. ArrayUtils类

ArrayUtils[类](https://commons.apache.org/proper/commons-lang/javadocs/api-3.6/index.html?org/apache/commons/lang3/ArrayUtils.html)实现了一批实用方法，使我们能够处理和检查许多不同形状和形式的数组。

让我们从toString()方法的两个重载实现开始，它返回给定数组的字符串表示形式，并在数组为空时返回特定字符串：

```java
@Test
public void whenCalledtoString_thenCorrect() {
    String[] array = {"a", "b", "c"};
    assertThat(ArrayUtils.toString(array))
      .isEqualTo("{a,b,c}");
}

@Test
public void whenCalledtoStringIfArrayisNull_thenCorrect() {
    assertThat(ArrayUtils.toString(null, "Array is null"))
      .isEqualTo("Array is null");
}

```

接下来，我们有hasCode()和toMap()方法。

前者为数组生成自定义[hashCode](https://www.baeldung.com/java-hashcode)实现，而后者将数组转换为[Map](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html)：

```java
@Test
public void whenCalledhashCode_thenCorrect() {
    String[] array = {"a", "b", "c"};
    assertThat(ArrayUtils.hashCode(array))
      .isEqualTo(997619);
}
    
@Test
public void whenCalledtoMap_thenCorrect() {
    String[][] array = {{"1", "one", }, {"2", "two", }, {"3", "three"}};
    Map map = new HashMap();
    map.put("1", "one");
    map.put("2", "two");
    map.put("3", "three");
    assertThat(ArrayUtils.toMap(array))
      .isEqualTo(map);
}
```

最后，让我们看看isSameLength()和indexOf()方法。

前者用于检查两个数组的长度是否相同，后者用于获取给定元素的索引：

```java
@Test
public void whenCalledisSameLength_thenCorrect() {
    int[] array1 = {1, 2, 3};
    int[] array2 = {1, 2, 3};
    assertThat(ArrayUtils.isSameLength(array1, array2))
      .isTrue();
}

@Test
public void whenCalledIndexOf_thenCorrect() {
    int[] array = {1, 2, 3};
    assertThat(ArrayUtils.indexOf(array, 1, 0))
      .isEqualTo(0);
}

```

与StringUtils类一样，ArrayUtils实现了更多的附加方法。[可以在本教程](https://www.baeldung.com/array-processing-commons-lang)中了解有关它们的更多信息。

在这种情况下，我们只展示了最具代表性的。

## 5. NumberUtils类

Apache Commons Lang 3 的另一个关键组件是[NumberUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/math/NumberUtils.html)类。

正如预期的那样，该类提供了大量实用方法，旨在处理和操作数字类型。

让我们看一下compare()方法的重载实现，它比较不同基元的相等性，例如int和long：

```java
@Test
public void whenCalledcompareWithIntegers_thenCorrect() {
    assertThat(NumberUtils.compare(1, 1))
      .isEqualTo(0);
}
    
@Test
public void whenCalledcompareWithLongs_thenCorrect() {
    assertThat(NumberUtils.compare(1L, 1L))
      .isEqualTo(0);
}
```

此外，存在对byte和short进行操作的compare()实现，其工作方式与上述示例非常相似。

接下来是createNumber()和isDigit()方法。

第一个允许我们创建string的数字表示，而第二个检查字符串是否仅由数字组成：

```java
@Test
public void whenCalledcreateNumber_thenCorrect() {
    assertThat(NumberUtils.createNumber("123456"))
      .isEqualTo(123456);
}
    
@Test
public void whenCalledisDigits_thenCorrect() {
    assertThat(NumberUtils.isDigits("123456")).isTrue();
}

```

在查找提供的数组的混合值和最大值时，NumberUtils类通过min()和max()方法的重载实现为这些操作提供了强大的支持：

```java
@Test
public void whenCalledmaxwithIntegerArray_thenCorrect() {
    int[] array = {1, 2, 3, 4, 5, 6};
    assertThat(NumberUtils.max(array))
      .isEqualTo(6);
}
    
@Test
public void whenCalledminwithIntegerArray_thenCorrect() {
    int[] array = {1, 2, 3, 4, 5, 6};
    assertThat(NumberUtils.min(array)).isEqualTo(1);
}
    
@Test
public void whenCalledminwithByteArray_thenCorrect() {
    byte[] array = {1, 2, 3, 4, 5, 6};
    assertThat(NumberUtils.min(array))
      .isEqualTo((byte) 1);
}
```

## 6.分数类_

当我们使用一支笔和一张纸时，使用分数就可以了。但是，我们在编写代码时是否必须经历这个过程的复杂性？并不真地。

[Fraction](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/math/Fraction.html)类使分数的加、减和乘变得轻而易举：

```java
@Test
public void whenCalledgetFraction_thenCorrect() {
    assertThat(Fraction.getFraction(5, 6)).isInstanceOf(Fraction.class);
}
    
@Test
public void givenTwoFractionInstances_whenCalledadd_thenCorrect() {
    Fraction fraction1 = Fraction.getFraction(1, 4);
    Fraction fraction2 = Fraction.getFraction(3, 4);
    assertThat(fraction1.add(fraction2).toString()).isEqualTo("1/1");
}
    
@Test
public void givenTwoFractionInstances_whenCalledsubstract_thenCorrect() {
    Fraction fraction1 = Fraction.getFraction(3, 4);
    Fraction fraction2 = Fraction.getFraction(1, 4);
    assertThat(fraction1.subtract(fraction2).toString()).isEqualTo("1/2");
}
    
@Test
public void givenTwoFractionInstances_whenCalledmultiply_thenCorrect() {
    Fraction fraction1 = Fraction.getFraction(3, 4);
    Fraction fraction2 = Fraction.getFraction(1, 4);
    assertThat(fraction1.multiplyBy(fraction2).toString()).isEqualTo("3/16");
}
```

虽然分数运算肯定不是我们在日常开发工作中必须处理的最常见任务，但Fraction类为以直接方式执行这些运算提供了宝贵的支持。

## 7.SystemUtils类_

有时，我们需要获取底层Java平台或操作系统的不同属性和变量的一些动态信息。

Apache Commons Lang 3 提供了[SystemUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/SystemUtils.html)类以轻松完成此操作。

例如，让我们考虑getJavaHome()、getUserHome()和isJavaVersionAtLeast()方法：

```java
@Test
public void whenCalledgetJavaHome_thenCorrect() {
    assertThat(SystemUtils.getJavaHome())
      .isEqualTo(new File("path/to/java/jdk"));
}

@Test
public void whenCalledgetUserHome_thenCorrect() {
    assertThat(SystemUtils.getUserHome())
      .isEqualTo(new File("path/to/user/home"));
}

@Test
public void whenCalledisJavaVersionAtLeast_thenCorrect() {
    assertThat(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_RECENT)).isTrue();
}
```

SystemUtils类实现了一些额外的实用方法。我们省略了它们以保持示例简短。

## 8.惰性初始化和构建器类

Apache Commons Lang 3 最吸引人的方面之一是一些众所周知的设计模式的实现，包括[惰性初始化](https://en.wikipedia.org/wiki/Lazy_initialization)和[构建器](https://en.wikipedia.org/wiki/Builder_pattern)模式。

例如，假设我们创建了一个昂贵的User类(为简洁起见未显示)，并希望将其实例化推迟到真正需要时再进行。

在这种情况下，我们需要做的就是扩展参数化的[LazyInitializer](https://commons.apache.org/proper/commons-lang/apidocs/index.html?org/apache/commons/lang3/concurrent/LazyInitializer.html)抽象类并覆盖其initialize()方法：

```java
public class UserInitializer extends LazyInitializer<User> {

    @Override
    protected User initialize() {
        return new User("John", "john@domain.com");
    }
}
```

现在，如果我们想在需要时获取我们的昂贵的User对象，我们只需调用UserInitializer 的 get()方法：

```java
@Test 
public void whenCalledget_thenCorrect() 
  throws ConcurrentException { 
    UserInitializer userInitializer = new UserInitializer(); 
    assertThat(userInitializer.get()).isInstanceOf(User.class); 
}
```

get()方法是实例字段的双重检查习惯用法(线程安全)的实现，如Joshua [Bloch 的“Effective Java”，第 71 项中](https://www.pearson.com/us/higher-education/program/Bloch-Effective-Java-3rd-Edition/PGM1763855.html)所述：

```java
private volatile User instance;
 
User get() { 
    if (instance == null) { 
        synchronized(this) { 
            if (instance == null) 
                instance = new User("John", "john@domain.com"); 
            }
        } 
    } 
    return instance; 
}
```

此外，Apache Commons Lang 3 实现了[HashCodeBuilder](https://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/builder/HashCodeBuilder.html)类，它允许我们基于典型的流畅 API，通过为构建器提供不同的参数来生成hashCode()实现：

```java
@Test
public void whenCalledtoHashCode_thenCorrect() {
    int hashcode = new HashCodeBuilder(17, 37)
      .append("John")
      .append("john@domain.com")
      .toHashCode();
    assertThat(hashcode).isEqualTo(1269178828);
}
```

我们可以用[BasicThreadFactory](https://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/concurrent/BasicThreadFactory.html)类做一些类似的事情，并创建具有命名模式和优先级的守护线程：

```java
@Test
public void whenCalledBuilder_thenCorrect() {
    BasicThreadFactory factory = new BasicThreadFactory.Builder()
      .namingPattern("workerthread-%d")
      .daemon(true)
      .priority(Thread.MAX_PRIORITY)
      .build();
    assertThat(factory).isInstanceOf(BasicThreadFactory.class);
}
```

## 9.ConstructorUtils类_

Reflection 是 Apache Commons Lang 3 中的一等公民。

该库包含几个反射类，它们允许我们反射性地访问和操作类字段和方法。

例如，假设我们已经实现了一个简单的User域类：

```java
public class User {

    private String name;
    private String email;
    
    // standard constructors / getters / setters / toString
}
```

假设它的参数化构造函数是public的，我们可以使用[ConstructorUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/reflect/ConstructorUtils.html)类轻松访问它：

```java
@Test
public void whenCalledgetAccessibleConstructor_thenCorrect() {
    assertThat(ConstructorUtils
      .getAccessibleConstructor(User.class, String.class, String.class))
      .isInstanceOf(Constructor.class);
}

```

除了通过构造函数进行标准类实例化之外，我们还可以通过调用invokeConstructor()和invokeExactConstructor()方法来反射性地创建User实例：

```java
@Test
public void whenCalledinvokeConstructor_thenCorrect() 
  throws Exception {
      assertThat(ConstructorUtils.invokeConstructor(User.class, "name", "email"))
        .isInstanceOf(User.class);
}

@Test
public void whenCalledinvokeExactConstructor_thenCorrect() 
  throws Exception {
      String[] args = {"name", "email"};
      Class[] parameterTypes= {String.class, String.class};
      assertThat(ConstructorUtils.invokeExactConstructor(User.class, args, parameterTypes))
        .isInstanceOf(User.class);
}

```

## 10. FieldUtils类

同样，我们可以使用[FieldUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/reflect/FieldUtils.html)类的方法来反射性地读/写类字段。

假设我们想要获取User类的字段，或者最终是该类从超类继承的字段。

在这种情况下，我们可以调用getField()方法：

```java
@Test
public void whenCalledgetField_thenCorrect() {
    assertThat(FieldUtils.getField(User.class, "name", true).getName())
      .isEqualTo("name");
}

```

或者，如果我们想使用更严格的反射范围，并且只获取在User类中声明的字段，而不是从超类继承的，我们只需使用getDeclaredField()方法：

```java
@Test
public void whenCalledgetDeclaredFieldForceAccess_thenCorrect() {
    assertThat(FieldUtils.getDeclaredField(User.class, "name", true).getName())
      .isEqualTo("name");
}
```

此外，我们可以使用getAllFields()方法获取反射类的字段数，并使用writeField()和writeDeclaredField()方法将值写入声明的字段或在层次结构中定义的字段：

```java
@Test
public void whenCalledgetAllFields_thenCorrect() {
    assertThat(FieldUtils.getAllFields(User.class).length)
      .isEqualTo(2);  
}

@Test
public void whenCalledwriteField_thenCorrect() 
  throws IllegalAccessException {
    FieldUtils.writeField(user, "name", "Julie", true);
    assertThat(FieldUtils.readField(user, "name", true))
      .isEqualTo("Julie");     
}
    
@Test
public void givenFieldUtilsClass_whenCalledwriteDeclaredField_thenCorrect() throws IllegalAccessException {
    FieldUtils.writeDeclaredField(user, "name", "Julie", true);
    assertThat(FieldUtils.readField(user, "name", true))
      .isEqualTo("Julie");    
}
```

## 11. MethodUtils类

同样，我们可以对[MethodUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/reflect/MethodUtils.html)类的类方法使用反射。

在这种情况下，User类的getName()方法的可见性是public。因此，我们可以使用getAccessibleMethod()方法访问它：

```java
@Test
public void whenCalledgetAccessibleMethod_thenCorrect() {
    assertThat(MethodUtils.getAccessibleMethod(User.class, "getName"))
      .isInstanceOf(Method.class);
}

```

当谈到反射调用方法时，我们可以使用invokeExactMethod()和invokeMethod()方法：

```java
@Test
public 
  void whenCalledinvokeExactMethod_thenCorrect() 
  throws Exception {
    assertThat(MethodUtils.invokeExactMethod(new User("John", "john@domain.com"), "getName"))
     .isEqualTo("John");
}

@Test
public void whenCalledinvokeMethod_thenCorrect() 
  throws Exception {
    User user = new User("John", "john@domain.com");
    Object method = MethodUtils.invokeMethod(user, true, "setName", "John");
    assertThat(user.getName()).isEqualTo("John");
}
```

## 12.可变对象类

虽然[不可变性](https://en.wikipedia.org/wiki/Immutable_object)是优秀的面向对象软件的一个关键特性，我们在所有可能的情况下都应该默认它，但不幸的是，有时我们需要处理可变对象。

此外，创建可变类需要大量样板代码，大多数 IDE 都可以通过自动生成的 setter 生成这些代码。

为此，Apache Commons Lang 3 提供了[MutableObject](https://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/mutable/MutableObject.html)类，这是一个简单的包装类，用于以最少的麻烦创建可变对象：

```java
@BeforeClass
public static void setUpMutableObject() {
    mutableObject = new MutableObject("Initial value");
}
    
@Test
public void whenCalledgetValue_thenCorrect() {
    assertThat(mutableObject.getValue()).isInstanceOf(String.class);
}
    
@Test
public void whenCalledsetValue_thenCorrect() {
    mutableObject.setValue("Another value");
    assertThat(mutableObject.getValue()).isEqualTo("Another value");
}
    
@Test
public void whenCalledtoString_thenCorrect() {
    assertThat(mutableObject.toString()).isEqualTo("Another value");    
}

```

当然，这只是一个如何使用MutableObject类的例子。

根据经验，我们应该始终努力创建不可变类，或者在最坏的情况下，只提供所需级别的可变性。

## 13. MutablePair类

有趣的是，Apache Commons Lang 3 以成对和三元组的形式为元组提供了强大的支持。

所以，假设我们需要创建一对可变的有序元素。

在这种情况下，我们将使用[MutablePair](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/tuple/MutablePair.html)类：

```java
private static MutablePair<String, String> mutablePair;

@BeforeClass
public static void setUpMutablePairInstance() {
    mutablePair = new MutablePair<>("leftElement", "rightElement");
}
    
@Test
public void whenCalledgetLeft_thenCorrect() {
    assertThat(mutablePair.getLeft()).isEqualTo("leftElement");
}
    
@Test
public void whenCalledgetRight_thenCorrect() {
    assertThat(mutablePair.getRight()).isEqualTo("rightElement");
}
    
@Test
public void whenCalledsetLeft_thenCorrect() {
    mutablePair.setLeft("newLeftElement");
    assertThat(mutablePair.getLeft()).isEqualTo("newLeftElement");
}

```

此处值得强调的最相关的细节是该类的干净 API。

它允许我们通过标准的 setter/getter 来设置和访问由 pair 包裹的左右对象。

## 14. ImmutablePair类

不出所料， MutablePair类还有一个不可变的对应实现，称为[ImmutablePair](https://commons.apache.org/proper/commons-lang/javadocs/api-3.1/org/apache/commons/lang3/tuple/ImmutablePair.html)：

```java
private static ImmutablePair<String, String> immutablePair = new ImmutablePair<>("leftElement", "rightElement");
    
@Test
public void whenCalledgetLeft_thenCorrect() {
    assertThat(immutablePair.getLeft()).isEqualTo("leftElement");
}
    
@Test
public void whenCalledgetRight_thenCorrect() {
    assertThat(immutablePair.getRight()).isEqualTo("rightElement");
}
    
@Test
public void whenCalledof_thenCorrect() {
    assertThat(ImmutablePair.of("leftElement", "rightElement"))
      .isInstanceOf(ImmutablePair.class);
}
    
@Test(expected = UnsupportedOperationException.class)
public void whenCalledSetValue_thenThrowUnsupportedOperationException() {
    immutablePair.setValue("newValue");
}

```

正如我们对不可变类所期望的那样，任何通过setValue()方法更改对的内部状态的尝试都将导致抛出UnsupportedOperationException异常。

## 15.三班_ _

此处要查看的最后一个实用程序类是[Triple](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/tuple/Triple.html)。

由于类是抽象的，我们可以使用[of()](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/tuple/Triple.html#of-L-M-R-)静态工厂方法创建Triple实例：

```java
@BeforeClass
public static void setUpTripleInstance() {
    triple = Triple.of("leftElement", "middleElement", "rightElement");
}
    
@Test
public void whenCalledgetLeft_thenCorrect() {
    assertThat(triple.getLeft()).isEqualTo("leftElement");
}
    
@Test
public void whenCalledgetMiddle_thenCorrect() {
    assertThat(triple.getMiddle()).isEqualTo("middleElement");
}
    
@Test
public void whenCalledgetRight_thenCorrect() {
    assertThat(triple.getRight()).isEqualTo("rightElement");
}
```

[通过MutableTriple](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/tuple/ImmutableTriple.html)和[ImmutableTriple](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/tuple/ImmutableTriple.html)类，还有可变和不可变三元组的具体实现。

我们可以通过参数化构造函数创建它们的实例，而不是使用静态工厂方法。

在这种情况下，我们将跳过它们，因为它们的 API 看起来与MutablePair和ImmutablePair类的 API 非常相似。

## 16.总结

在本教程中，我们深入了解了 Apache Commons Lang 3 现成提供的一些最有用的实用程序类。

该库实现了许多其他值得关注的实用程序类。在这里，我们只是根据一个非常自以为是的标准展示了最有用的。

有关完整的库 API，请查看[官方 Javadocs](https://commons.apache.org/proper/commons-lang/javadocs/api-release/index.html)。