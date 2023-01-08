## 1. 概述

序列化是将对象的状态转换为字节流；反序列化则相反。换句话说，序列化是将Java对象转换为静态字节流(序列)，然后我们可以将其保存到数据库或通过网络传输。

## 2.序列化与反序列化

序列化过程是实例无关的；例如，我们可以在一个平台上序列化对象并在另一个平台上反序列化它们。符合序列化条件的类需要实现一个特殊的标记接口， Serializable。 

ObjectInputStream和ObjectOutputStream都是高级类，分别扩展了java.io.InputStream和java.io.OutputStream。ObjectOutputStream可以将原始类型和对象图作为字节流写入OutputStream 。然后我们可以使用ObjectInputStream读取这些流。

ObjectOutputStream中最重要的方法是：

```java
public final void writeObject(Object o) throws IOException;
```

此方法采用可序列化对象并将其转换为字节序列(流)。同样， ObjectInputStream中最重要的方法是：

```java
public final Object readObject() 
  throws IOException, ClassNotFoundException;
```

This method can read a stream of bytes and convert it back into aJavaobject. It can then be cast back to the original object.

Let's illustrate serialization with a Person class. Note that static fields belong to a class (as opposed to an object) and are not serialized. Also, note that we can use the keyword transient to ignore class fields during serialization:

```java
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    static String country = "ITALY";
    private int age;
    private String name;
    transient int height;

    // getters and setters
}
```

下面的测试显示了一个将Person类型的对象保存到本地文件，然后读回值的示例：

```java
@Test 
public void whenSerializingAndDeserializing_ThenObjectIsTheSame() () 
  throws IOException, ClassNotFoundException { 
    Person person = new Person();
    person.setAge(20);
    person.setName("Joe");
    
    FileOutputStream fileOutputStream
      = new FileOutputStream("yourfile.txt");
    ObjectOutputStream objectOutputStream 
      = new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(person);
    objectOutputStream.flush();
    objectOutputStream.close();
    
    FileInputStream fileInputStream
      = new FileInputStream("yourfile.txt");
    ObjectInputStream objectInputStream
      = new ObjectInputStream(fileInputStream);
    Person p2 = (Person) objectInputStream.readObject();
    objectInputStream.close(); 
 
    assertTrue(p2.getAge() == person.getAge());
    assertTrue(p2.getName().equals(person.getName()));
}
```

我们使用ObjectOutputStream将此对象的状态保存到使用FileOutputStream 的文件中。在项目目录中创建文件“yourfile.txt” 。然后使用 FileInputStream 加载该文件。 ObjectInputStream拾取此流并将其转换为名为p2的新对象。

最后，我们将测试加载对象的状态，并确保它与原始对象的状态相匹配。

请注意，我们必须将加载的对象显式转换为Person类型。

## 3.Java序列化注意事项

有一些注意事项与Java中的序列化有关。

### 3.1. 继承与组合

当一个类实现java.io.Serializable接口时，它的所有子类也都是可序列化的。相反，当一个对象引用了另一个对象时，这些对象必须单独实现Serializable接口，否则将抛出NotSerializableException ：

```java
public class Person implements Serializable {
    private int age;
    private String name;
    private Address country; // must be serializable too
}

```

如果可序列化对象中的某个字段由对象数组组成，则所有这些对象也必须是可序列化的，否则将抛出NotSerializableException 。

### 3.2. 串行版本 UID

JVM 将版本(长)号与每个可序列化类相关联。我们用它来验证保存和加载的对象是否具有相同的属性，因此在序列化上是兼容的。

大多数 IDE 可以自动生成这个数字，它基于类名、属性和相关的访问修饰符。任何更改都会导致不同的数字，并可能导致InvalidClassException。

如果可序列化类没有声明 serialVersionUID ， JVM 将在运行时自动生成一个。但是，强烈建议每个类都声明其serialVersionUID，因为生成的类依赖于编译器，因此可能会导致意外的InvalidClassExceptions。

### 3.3.Java中的自定义序列化

Java 指定了序列化对象的默认方式，但Java类可以覆盖此默认行为。尝试序列化具有某些不可序列化属性的对象时，自定义序列化特别有用。我们可以通过在我们想要序列化的类中提供两个方法来做到这一点：

```java
private void writeObject(ObjectOutputStream out) throws IOException;
```

和

```java
private void readObject(ObjectInputStream in) 
  throws IOException, ClassNotFoundException;
```

使用这些方法，我们可以将不可序列化的属性序列化为我们可以序列化的其他形式：

```java
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Address address;
    private Person person;

    // setters and getters

    private void writeObject(ObjectOutputStream oos) 
      throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(address.getHouseNumber());
    }

    private void readObject(ObjectInputStream ois) 
      throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        Integer houseNumber = (Integer) ois.readObject();
        Address a = new Address();
        a.setHouseNumber(houseNumber);
        this.setAddress(a);
    }
}
public class Address {
    private int houseNumber;

    // setters and getters
}
```

我们可以运行以下单元测试来测试这个自定义序列化：

```java
@Test
public void whenCustomSerializingAndDeserializing_ThenObjectIsTheSame() 
  throws IOException, ClassNotFoundException {
    Person p = new Person();
    p.setAge(20);
    p.setName("Joe");

    Address a = new Address();
    a.setHouseNumber(1);

    Employee e = new Employee();
    e.setPerson(p);
    e.setAddress(a);

    FileOutputStream fileOutputStream
      = new FileOutputStream("yourfile2.txt");
    ObjectOutputStream objectOutputStream 
      = new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(e);
    objectOutputStream.flush();
    objectOutputStream.close();

    FileInputStream fileInputStream 
      = new FileInputStream("yourfile2.txt");
    ObjectInputStream objectInputStream 
      = new ObjectInputStream(fileInputStream);
    Employee e2 = (Employee) objectInputStream.readObject();
    objectInputStream.close();

    assertTrue(
      e2.getPerson().getAge() == e.getPerson().getAge());
    assertTrue(
      e2.getAddress().getHouseNumber() == e.getAddress().getHouseNumber());
}
```

在这段代码中，我们可以看到如何通过使用自定义序列化序列化Address来保存一些不可序列化的属性。请注意，我们必须将不可序列化的属性标记为瞬态以避免NotSerializableException。

## 4。总结

在这篇简短的文章中，我们回顾了Java序列化，讨论了注意事项，并学习了如何进行自定义序列化。