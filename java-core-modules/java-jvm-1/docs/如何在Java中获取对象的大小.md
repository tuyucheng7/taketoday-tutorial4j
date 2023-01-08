## 1. 概述

与我们可以使用 sizeof ()方法以字节为单位获取对象大小的 C/C++ 不同，在Java中没有真正等效的此类方法。

在本文中，我们将演示如何仍然可以获得特定对象的大小。

## 2. Java中的内存消耗

虽然Java中没有sizeof运算符，但实际上我们不需要。所有基本类型都有标准大小，通常没有填充字节或对齐字节。不过，这并不总是直截了当的。

虽然基元必须表现得好像它们具有官方大小一样，但 JVM 可以在内部以任何它喜欢的方式存储数据，具有任何数量的填充或开销。它可以选择将boolean[]存储在像BitSet这样的 64 位长块中，在堆栈上分配一些临时Object或优化一些完全不存在的变量或方法调用，用常量替换它们，等等......但是，只要程序给出相同的结果，完全没问题。

还要考虑到硬件和操作系统缓存的影响(我们的数据可能在每个缓存级别上重复)，这意味着我们只能粗略地预测 RAM 消耗。

### 2.1. 对象、引用和包装类

现代 64 位 JDK 的最小对象大小为 16 字节，因为对象具有 12 字节的标头，填充为 8 字节的倍数。在 32 位 JDK 中，开销为 8 字节，填充为 4 字节的倍数。

在堆边界小于 32Gb ( -Xmx32G )的 32 位平台和 64 位平台上，引用的典型大小为 4 字节，对于高于 32Gb 的边界，引用的大小为8 字节。

这意味着 64 位 JVM 通常需要多 30-50% 的堆空间。

特别需要注意的是，盒装类型、数组、String和其他容器(如多维数组)的内存消耗很大，因为它们会增加一定的开销。例如，当我们将int primitive(仅占用 4 个字节)与占用 16 个字节的 Integer对象进行比较时，我们看到有 300% 的内存开销。

## 3. 使用仪器估计对象大小

在Java中估算对象大小的一种方法是使用Java5 中引入的[Instrumentation](https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/Instrumentation.html)[接口的](https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/Instrumentation.html)getObjectSize(Object)方法 。

正如我们在 Javadoc 文档中看到的那样，该方法提供了指定对象大小的“特定于实现的近似值”。值得注意的是，在单个 JVM 调用期间，大小中可能包含开销，并且值可能不同。

这种方法只支持所考虑对象本身的大小估计，而不支持它引用的对象的大小。要估计对象的总大小，我们需要一个代码来遍历这些引用并计算估计的大小。

### 3.1. 创建检测代理

为了调用Instrumentation.getObjectSize(Object) 来获取对象的大小，我们需要首先能够访问 Instrumentation 的实例。我们需要使用检测代理，有两种方法可以做到这一点，如 [java.lang.instrument包的文档中所述。](https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/package-summary.html)

可以通过命令行指定 Instrumentation 代理，或者我们可以将其与已经运行的 JVM 一起使用。我们将专注于第一个。

要通过命令行指定检测代理，我们需要实现重载的premain方法，该方法将在使用检测时首先由 JVM 调用。除此之外，我们需要公开一个静态方法才能访问 Instrumentation.getObjectSize(Object)。

现在让我们创建InstrumentationAgent类：

```java
public class InstrumentationAgent {
    private static volatile Instrumentation globalInstrumentation;

    public static void premain(final String agentArgs, final Instrumentation inst) {
        globalInstrumentation = inst;
    }

    public static long getObjectSize(final Object object) {
        if (globalInstrumentation == null) {
            throw new IllegalStateException("Agent not initialized.");
        }
        return globalInstrumentation.getObjectSize(object);
    }
}
```

在我们为此代理创建 JAR 之前，我们需要确保其中包含一个简单的图元文件MANIFEST.MF：

```plaintext
Premain-class: objectsize.cn.tuyucheng.taketoday.InstrumentationAgent
```

现在我们可以制作一个包含 MANIFEST.MF 文件的代理 JAR。一种方法是通过命令行：

```plaintext
javac InstrumentationAgent.java
jar cmf MANIFEST.MF InstrumentationAgent.jar InstrumentationAgent.class
```

### 3.2. 示例类

让我们通过创建一个包含将使用我们的代理类的示例对象的类来实际操作：

```java
public class InstrumentationExample {

    public static void printObjectSize(Object object) {
        System.out.println("Object type: " + object.getClass() +
          ", size: " + InstrumentationAgent.getObjectSize(object) + " bytes");
    }

    public static void main(String[] arguments) {
        String emptyString = "";
        String string = "Estimating Object Size Using Instrumentation";
        String[] stringArray = { emptyString, string, "com.baeldung" };
        String[] anotherStringArray = new String[100];
        List<String> stringList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder(100);
        int maxIntPrimitive = Integer.MAX_VALUE;
        int minIntPrimitive = Integer.MIN_VALUE;
        Integer maxInteger = Integer.MAX_VALUE;
        Integer minInteger = Integer.MIN_VALUE;
        long zeroLong = 0L;
        double zeroDouble = 0.0;
        boolean falseBoolean = false;
        Object object = new Object();

        class EmptyClass {
        }
        EmptyClass emptyClass = new EmptyClass();

        class StringClass {
            public String s;
        }
        StringClass stringClass = new StringClass();

        printObjectSize(emptyString);
        printObjectSize(string);
        printObjectSize(stringArray);
        printObjectSize(anotherStringArray);
        printObjectSize(stringList);
        printObjectSize(stringBuilder);
        printObjectSize(maxIntPrimitive);
        printObjectSize(minIntPrimitive);
        printObjectSize(maxInteger);
        printObjectSize(minInteger);
        printObjectSize(zeroLong);
        printObjectSize(zeroDouble);
        printObjectSize(falseBoolean);
        printObjectSize(Day.TUESDAY);
        printObjectSize(object);
        printObjectSize(emptyClass);
        printObjectSize(stringClass);
    }

    public enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }
}
```

为此，我们需要在运行我们的应用程序时包括 – javaagent 选项以及代理 JAR 的路径：

```plaintext
VM Options: -javaagent:"path_to_agent_directoryInstrumentationAgent.jar"
```

运行我们类的输出将向我们显示估计的对象大小：

```plaintext
Object type: class java.lang.String, size: 24 bytes
Object type: class java.lang.String, size: 24 bytes
Object type: class [Ljava.lang.String;, size: 32 bytes
Object type: class [Ljava.lang.String;, size: 416 bytes
Object type: class java.util.ArrayList, size: 24 bytes
Object type: class java.lang.StringBuilder, size: 24 bytes
Object type: class java.lang.Integer, size: 16 bytes
Object type: class java.lang.Integer, size: 16 bytes
Object type: class java.lang.Integer, size: 16 bytes
Object type: class java.lang.Integer, size: 16 bytes
Object type: class java.lang.Long, size: 24 bytes
Object type: class java.lang.Double, size: 24 bytes
Object type: class java.lang.Boolean, size: 16 bytes
Object type: class objectsize.cn.tuyucheng.taketoday.InstrumentationExample$Day, size: 24 bytes
Object type: class java.lang.Object, size: 16 bytes
Object type: class objectsize.cn.tuyucheng.taketoday.InstrumentationExample$1EmptyClass, size: 16 bytes
Object type: class objectsize.cn.tuyucheng.taketoday.InstrumentationExample$1StringClass, size: 16 bytes
```

## 4. 总结

在本文中，我们描述了Java中特定类型如何使用内存，JVM 如何存储数据，并强调了可能影响总内存消耗的因素。然后我们演示了如何在实践中获得Java对象的估计大小。