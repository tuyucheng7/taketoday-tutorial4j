## 1. 概述

在本教程中，我们将熟悉超类型标记并了解它们如何帮助我们在运行时保留[泛型类型信息](https://www.baeldung.com/java-generics)。

## 2.擦除

有时我们需要将特定的类型信息传递给方法。例如，这里我们期望 Jackson 将 JSON 字节数组转换为字符串：

```java
byte[] data = // fetch json from somewhere
String json = objectMapper.readValue(data, String.class);
```

我们通过文字类标记传达这种期望，在本例中为String.class。 

但是，我们不能轻易地为泛型类型设置相同的期望：

```java
Map<String, String> json = objectMapper.readValue(data, Map<String, String>.class); // won't compile
```

Java 在编译期间擦除泛型类型信息。因此，泛型类型参数只是源代码的产物，在运行时将不存在。

### 2.1. 具体化

从技术上讲，泛型类型在Java中没有具体化。在编程语言的术语中，当类型在运行时出现时，我们说该类型已具体化。

Java中具体化的类型如下：

-   简单的基本类型，例如 long
-   非泛型抽象，例如 String 或Runnable
-   原始类型，例如 List 或 HashMap
-   所有类型都是无限通配符的通用类型，例如 List<?> 或HashMap<?, ?>
-   其他具体化类型的数组，例如 String[]、int[]、List[]或Map<?, ?>[]

因此，我们不能使用像Map<String, String>.class 这样的东西，因为 Map<String, String> 不是具体化的类型。

## 3. 超级代币

事实证明，我们可以利用Java中[匿名内部类](https://www.baeldung.com/java-anonymous-classes)的强大功能在编译期间保留类型信息：

```java
public abstract class TypeReference<T> {

    private final Type type;

    public TypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }
}
```

这个类是抽象的，所以我们只能从它派生子类。

例如，我们可以创建一个匿名内部：

```java
TypeReference<Map<String, Integer>> token = new TypeReference<Map<String, String>>() {};
```

构造函数执行以下步骤来保留类型信息：

-   首先，它获取此特定实例的通用超类元数据——在本例中，通用超类是TypeReference<Map<String, Integer>>
-   然后，它获取并存储泛型超类的实际类型参数——在本例中，它将是Map<String, Integer>

这种保留通用类型信息的方法通常称为超类型标记：

```java
TypeReference<Map<String, Integer>> token = new TypeReference<Map<String, Integer>>() {};
Type type = token.getType();

assertEquals("java.util.Map<java.lang.String, java.lang.Integer>", type.getTypeName());

Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
assertEquals("java.lang.String", typeArguments[0].getTypeName());
assertEquals("java.lang.Integer", typeArguments[1].getTypeName());
```

使用超类型标记，我们知道容器类型是 Map， 而且它的类型参数是 String 和 Integer。 

这种模式非常有名，以至于像 Jackson 这样的库和像 Spring 这样的框架都有自己的实现。将 JSON 对象解析为Map<String, String>可以通过使用超类型标记定义该类型来完成：

```java
TypeReference<Map<String, String>> token = new TypeReference<Map<String, String>>() {};
Map<String, String> json = objectMapper.readValue(data, token);
```

## 4。总结

在本教程中，我们了解了如何使用超类型标记在运行时保留泛型类型信息。