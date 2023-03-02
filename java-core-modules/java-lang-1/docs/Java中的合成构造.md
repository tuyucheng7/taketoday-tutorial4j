##1.概述

在本教程中，我们将了解Java的合成构造，编译器引入的代码，用于透明地处理对成员的访问，否则由于可见性不足或缺少引用而无法访问这些成员。

注意：从[JDK11](https://openjdk.java.net/jeps/181)开始，不再生成合成方法和构造函数，因为它们已被[基于嵌套的访问控制](https://www.baeldung.com/java-nest-based-access-control)所取代。

##2.Java中的综合

我们可能找到的合成的最佳定义直接来自Java语言规范([JLS13.1.7](https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html))：

Java编译器引入的任何在源代码中没有相应构造的构造都必须标记为合成的，但默认构造函数、类初始化方法以及Enum类的values和valueOf方法除外。

有不同种类的编译构造，即字段、构造函数和方法。另一方面，尽管编译器可以更改嵌套类(即匿名类)，但它们不被视为合成类。

事不宜迟，让我们深入研究其中的每一个。

##3.合成场

让我们从一个简单的嵌套类开始：

```java
public class SyntheticFieldDemo {
    class NestedClass {}
}
```

编译时，任何内部类都将包含一个引用顶级类的合成字段。巧合的是，这使得从嵌套类访问封闭类成员成为可能。

为了确保这是正在发生的事情，我们将实施一个测试，该测试通过反射获取嵌套类字段并使用isSynthetic()方法检查它们：

```java
public void givenSyntheticField_whenIsSynthetic_thenTrue() {
    Field[] fields = SyntheticFieldDemo.NestedClass.class
      .getDeclaredFields();
    assertEquals("This class should contain only one field",
      1, fields.length);

    for (Field f : fields) {
        System.out.println("Field: " + f.getName() + ", isSynthetic: " +
          f.isSynthetic());
        assertTrue("All the fields of this class should be synthetic", 
          f.isSynthetic());
    }
}
```

我们可以验证这一点的另一种方法是通过命令javap运行反汇编程序。在任何一种情况下，输出都会显示一个名为this$0的合成字段。

##4.合成方法

接下来，我们将向嵌套类添加一个私有字段：

```java
public class SyntheticMethodDemo {
    class NestedClass {
        private String nestedField;
    }

    public String getNestedField() {
        return new NestedClass().nestedField;
    }

    public void setNestedField(String nestedField) {
        new NestedClass().nestedField = nestedField;
    }
}
```

在这种情况下，编译将生成变量的访问器。没有这些方法，就不可能从封闭实例访问私有字段。

再一次，我们可以使用相同的技术来检查这一点，该技术显示了两个名为access$0和access$1的合成方法：

```java
public void givenSyntheticMethod_whenIsSynthetic_thenTrue() {
    Method[] methods = SyntheticMethodDemo.NestedClass.class
      .getDeclaredMethods();
    assertEquals("This class should contain only two methods",
      2, methods.length);

    for (Method m : methods) {
        System.out.println("Method: " + m.getName() + ", isSynthetic: " +
          m.isSynthetic());
        assertTrue("All the methods of this class should be synthetic",
          m.isSynthetic());
    }
}
```

请注意，为了生成代码，必须实际读取或写入字段，否则，方法将被优化掉。这就是为什么我们还添加了getter和setter的原因。

如上所述，从JDK11开始不再生成这些合成方法。

###4.1.桥接方法

合成方法的一个特例是桥接方法，它处理泛型的类型擦除。

例如，让我们考虑一个简单的Comparator：

```java
public class BridgeMethodDemo implements Comparator<Integer> {
    @Override
    public int compare(Integer o1, Integer o2) {
        return 0;
    }
}
```

尽管compare()在源代码中采用两个Integer参数，但由于类型擦除，一旦编译它就会采用两个Object参数。

为了管理这一点，编译器创建了一个合成桥，负责转换参数：

```java
public int compare(Object o1, Object o2) {
    return compare((Integer) o1, (Integer) o2);
}
```

除了我们之前的测试之外，这次我们还将从Method类中调用isBridge()：

```java
public void givenBridgeMethod_whenIsBridge_thenTrue() {
    int syntheticMethods = 0;
    Method[] methods = BridgeMethodDemo.class.getDeclaredMethods();
    for (Method m : methods) {
        System.out.println("Method: " + m.getName() + ", isSynthetic: " +
          m.isSynthetic() + ", isBridge: " + m.isBridge());
        if (m.isSynthetic()) {
            syntheticMethods++;
            assertTrue("The synthetic method in this class should also be a bridge method",
              m.isBridge());
        }
    }
    assertEquals("There should be exactly 1 synthetic bridge method in this class",
      1, syntheticMethods);
}
```

##5.合成构造函数

最后，我们将添加一个私有构造函数：

```java
public class SyntheticConstructorDemo {
    private NestedClass nestedClass = new NestedClass();

    class NestedClass {
        private NestedClass() {}
    }
}
```

这一次，一旦我们运行测试或反汇编程序，我们就会看到实际上有两个构造函数，其中一个是合成的：

```java
public void givenSyntheticConstructor_whenIsSynthetic_thenTrue() {
    int syntheticConstructors = 0;
    Constructor<?>[] constructors = SyntheticConstructorDemo.NestedClass
      .class.getDeclaredConstructors();
    assertEquals("This class should contain only two constructors",
      2, constructors.length);

    for (Constructor<?> c : constructors) {
        System.out.println("Constructor: " + c.getName() +
          ", isSynthetic: " + c.isSynthetic());

        if (c.isSynthetic()) {
            syntheticConstructors++;
        }
    }

    assertEquals(1, syntheticConstructors);
}
```

与合成字段类似，此生成的构造函数对于使用其封闭实例中的私有构造函数实例化嵌套类至关重要。

如上所述，从JDK11开始不再生成合成构造函数。

##六，总结

在本文中，我们讨论了由Java编译器生成的合成构造。为了测试它们，我们使用了反射，你可以[在此处](https://www.baeldung.com/java-reflection)了解更多信息。