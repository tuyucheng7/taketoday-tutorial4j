## 1. 概述

在本快速教程中，我们将演示如何在Java中验证 Serializable 对象。

## 2.序列化与反序列化

[序列化是](https://www.baeldung.com/java-serialization)将对象的状态转换为字节流的过程。序列化对象主要用于 Hibernate、RMI、JPA、EJB 和 JMS 技术。

换个方向，反序列化是相反的过程，其中使用字节流在内存中重新创建实际的Java对象。这个过程经常被用来持久化对象。

## 3.序列化验证

我们可以使用多种方法验证序列化。让我们来看看一些。

### 3.1. 验证实现序列化

确定对象是否可序列化的最简单方法是检查该对象是否是java.io.Serializable或java.io.Externalizable的实例。但是，这种方法并不能保证我们可以序列化一个对象。

假设我们有一个没有实现Serializable接口的Address对象：

```java
public class Address {
    private int houseNumber;

    //getters and setters
}
```

尝试序列化Address对象时，可能会发生NotSerializableException：

```java
@Test(expected = NotSerializableException.class)
public void whenSerializing_ThenThrowsError() throws IOException {
    Address address = new Address();
    address.setHouseNumber(10);
    FileOutputStream fileOutputStream = new FileOutputStream("yofile.txt");
    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
        objectOutputStream.writeObject(address);
    }
}
```

现在，假设我们有一个实现了Serializable接口的Person对象：

```java
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private int age;
    private String name;

    // getters and setters
}
```

在这种情况下，我们将能够序列化和反序列化以重新创建对象：

```java
Person p = new Person();
p.setAge(20);
p.setName("Joe");
FileOutputStream fileOutputStream = new FileOutputStream("yofile.txt");
try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
    objectOutputStream.writeObject(p);
}

FileInputStream fileInputStream = new FileInputStream("yofile.txt");
try ( ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
    Person p2 = (Person) objectInputStream.readObject();
    assertEquals(p2.getAge(), p.getAge());
    assertEquals(p2.getName(), p.getName());;
}
```

### 3.2. Apache Commons SerializationUtils

验证对象序列化的另一种方法是使用Apache Commons [SerializationUtils](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/SerializationUtils.html)中的序列化方法。此方法将不接受不可序列化的对象。

如果我们尝试通过显式类型转换来编译代码来序列化不可序列化的Address对象会怎样？在运行时，我们会遇到ClassCastException：

```java
Address address = new Address();
address.setHouseNumber(10);
SerializationUtils.serialize((Serializable) address);
```

让我们使用上面的代码来验证可序列化的Person对象：

```java
Person p = new Person();
p.setAge(20);
p.setName("Joe");
byte[] serialize = SerializationUtils.serialize(p);
Person p2 = (Person)SerializationUtils.deserialize(serialize);
assertEquals(p2.getAge(), p.getAge());
assertEquals(p2.getName(), p.getName());
```

### 3.3. Spring Core SerializationUtils

我们现在将查看[spring-core中的](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework" AND a%3A"spring-core")[SerializationUtils](https://www.javadoc.io/doc/org.springframework/spring-core/5.0.8.RELEASE/org/springframework/util/SerializationUtils.html)方法，它类似于 Apache Commons 中的方法。此方法也不接受不可序列化的地址对象。

此类代码将在运行时抛出ClassCastException：

```java
Address address = new Address();
address.setHouseNumber(10);
org.springframework.util.SerializationUtils.serialize((Serializable) address);
```

让我们尝试使用可序列化的Person 对象：

```java
Person p = new Person();
p.setAge(20);
p.setName("Joe");
byte[] serialize = org.springframework.util.SerializationUtils.serialize(p);
Person p2 = (Person)org.springframework.util.SerializationUtils.deserialize(serialize);
assertEquals(p2.getAge(), p.getAge());
assertEquals(p2.getName(), p.getName());
```

### 3.4. 自定义序列化实用程序

作为第三种选择，我们将创建自己的自定义实用程序以根据我们的要求进行序列化或反序列化。为了证明这一点，我们将编写两个单独的序列化和反序列化方法。

第一个是序列化过程的对象验证示例：

```java
public static  byte[] serialize(T obj) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(obj);
    oos.close();
    return baos.toByteArray();
}
```

我们还将编写一个方法来执行反序列化过程：

```java
public static  T deserialize(byte[] b, Class cl) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(b);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object o = ois.readObject();
    return cl.cast(o);
}
```

此外，我们可以创建一个将Class作为参数并在对象可序列化时返回true的实用方法。此方法假设基元和接口是隐式可序列化的，同时验证输入类是否可以分配给可序列化。此外，我们在验证过程中排除了瞬态和静态字段。

让我们实现这个方法：

```java
public static boolean isSerializable(Class<?> it) {
    boolean serializable = it.isPrimitive() || it.isInterface() || Serializable.class.isAssignableFrom(it);
    if (!serializable) {
        return false;
    }
    Field[] declaredFields = it.getDeclaredFields();
    for (Field field : declaredFields) {
        if (Modifier.isVolatile(field.getModifiers()) || Modifier.isTransient(field.getModifiers()) || 
          Modifier.isStatic(field.getModifiers())) {
            continue;
        }
        Class<?> fieldType = field.getType();
        if (!isSerializable(fieldType)) {
            return false;
        }
    }
    return true;
}
```

现在让我们验证我们的实用程序方法：

```java
assertFalse(MySerializationUtils.isSerializable(Address.class));
assertTrue(MySerializationUtils.isSerializable(Person.class));
assertTrue(MySerializationUtils.isSerializable(Integer.class));
```

## 4。总结

在本文中，我们研究了几种确定对象是否可序列化的方法。我们还演示了一个自定义实现来完成相同的任务。