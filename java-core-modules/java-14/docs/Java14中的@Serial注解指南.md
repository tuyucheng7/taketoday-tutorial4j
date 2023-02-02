## 1. 简介

在这个快速教程中，我们将看看Java 14中新引入的@Serial注解。

与@Override类似，此注解与serial lint标志结合使用，以对[类的序列化相关成员](https://www.baeldung.com/java-serialization)执行编译时检查。

**尽管注解已根据构建25提供，**[但lint检查尚未发布](https://bugs.openjdk.java.net/browse/JDK-8202056)。

## 2. 用法

让我们首先使用@Serial标注七个与序列化相关的方法和字段中的每一个：

```java
public class MySerialClass implements Serializable {

    @Serial
    private static final ObjectStreamField[] serialPersistentFields = null;

    @Serial
    private static final long serialVersionUID = 1;

    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
        // ...
    }

    @Serial
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        // ...
    }

    @Serial
    private void readObjectNoData() throws ObjectStreamException {
        // ...
    }

    @Serial
    private Object writeReplace() throws ObjectStreamException {
        // ...
        return null;
    }

    @Serial
    private Object readResolve() throws ObjectStreamException {
        // ...
        return null;
    }

}
```

之后，**我们需要使用serial lint标志编译我们的类**：

```shell
javac -Xlint:serial MySerialClass.java
```

**然后，编译器会检查方法签名和注解成员的类型，如果它们与预期的不匹配，则发出警告**。

此外，如果应用@Serial，编译器也会抛出错误：

-   当一个类没有实现Serializable接口时：

```java
public class MyNotSerialClass {
    @Serial
    private static final long serialVersionUID = 1; // Compilation error
}
```

-   如果它是无效的，例如枚举的任何序列化方法，[因为它们被忽略了](https://docs.oracle.com/en/java/javase/11/docs/specs/serialization/serial-arch.html#serialization-of-enum-constants)：

```java
public enum MyEnum {
    @Serial
    private void readObjectNoData() throws ObjectStreamException {
    } // Compilation error 
}
```

-   无法在[Externalizable](https://www.baeldung.com/java-externalizable)类中写入writeObject()、readObject()、readObjectNoData()和serialPersistentFields，因为这些类使用不同的序列化方法：

```java
public class MyExternalizableClass implements Externalizable {
    @Serial
    private void writeObject(ObjectOutputStream stream) throws IOException {
    } // Compilation error 
}
```

## 3. 总结

这篇简短的文章介绍了新的@Serial注解用法。