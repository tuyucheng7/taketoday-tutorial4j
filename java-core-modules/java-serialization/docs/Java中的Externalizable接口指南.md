## 1. 概述

在本教程中，我们将快速浏览一下 java 的java.io.Externalizable接口。此接口的主要目标是促进自定义序列化和反序列化。

在我们继续之前，请务必查看[Java 中的序列化](https://www.baeldung.com/java-serialization)一文。下一章将介绍如何使用此接口序列化Java对象。

之后，我们将讨论与java.io.Serializable接口相比的主要区别。

## 2.可外部化接口

Externalizable扩展自java.io.Serializable标记接口。任何实现Externalizable接口的类都应该覆盖writeExternal()和readExternal()方法。这样我们就可以改变 JVM 的默认序列化行为。

### 2.1. 连载

让我们看一下这个简单的例子：

```java
public class Country implements Externalizable {
  
    private static final long serialVersionUID = 1L;
  
    private String name;
    private int code;
  
    // getters, setters
  
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(code);
    }
  
    @Override
    public void readExternal(ObjectInput in) 
      throws IOException, ClassNotFoundException {
        this.name = in.readUTF();
        this.code = in.readInt();
    }
}
```

在这里，我们定义了一个类Country，它实现了Externalizable接口并实现了上面提到的两个方法。

在writeExternal()方法中，我们将对象的属性添加到ObjectOutput流中。这具有标准方法，例如用于String 的writeUTF()和用于 int 值的writeInt() 。

接下来，为了反序列化对象，我们使用readUTF()、readInt()方法从ObjectInput流中读取，以按照写入属性的相同顺序读取属性。

手动添加 serialVersionUID 是一种很好的做法。如果没有，JVM 会自动添加一个。

自动生成的数字取决于编译器。这意味着它可能会导致不太可能的InvalidClassException。

让我们测试一下我们上面实现的行为：

```java
@Test
public void whenSerializing_thenUseExternalizable() 
  throws IOException, ClassNotFoundException {
       
    Country c = new Country();
    c.setCode(374);
    c.setName("Armenia");
   
    FileOutputStream fileOutputStream
     = new FileOutputStream(OUTPUT_FILE);
    ObjectOutputStream objectOutputStream
     = new ObjectOutputStream(fileOutputStream);
    c.writeExternal(objectOutputStream);
   
    objectOutputStream.flush();
    objectOutputStream.close();
    fileOutputStream.close();
   
    FileInputStream fileInputStream
     = new FileInputStream(OUTPUT_FILE);
    ObjectInputStream objectInputStream
     = new ObjectInputStream(fileInputStream);
   
    Country c2 = new Country();
    c2.readExternal(objectInputStream);
   
    objectInputStream.close();
    fileInputStream.close();
   
    assertTrue(c2.getCode() == c.getCode());
    assertTrue(c2.getName().equals(c.getName()));
}
```

在此示例中，我们首先创建一个Country对象并将其写入文件。然后，我们从文件中反序列化对象并验证值是否正确。

打印的c2对象的输出：

```java
Country{name='Armenia', code=374}
```

这表明我们已经成功反序列化了对象。

### 2.2. 遗产

当一个类继承自Serializable接口时，JVM 会自动从子类中收集所有字段并使它们可序列化。

请记住，我们也可以将其应用于Externalizable 。我们只需要为继承层次结构的每个子类实现读/写方法。

让我们看看下面的Region类，它扩展了上一节中的Country类：

```java
public class Region extends Country implements Externalizable {
 
    private static final long serialVersionUID = 1L;
 
    private String climate;
    private Double population;
 
    // getters, setters
 
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeUTF(climate);
    }
 
    @Override
    public void readExternal(ObjectInput in) 
      throws IOException, ClassNotFoundException {
 
        super.readExternal(in);
        this.climate = in.readUTF();
    }
}
```

在这里，我们添加了两个额外的属性并序列化了第一个。

请注意，我们还在序列化程序方法中调用了super.writeExternal(out)、super.readExternal(in)来保存/恢复父类字段。

让我们使用以下数据运行单元测试：

```plaintext
Region r = new Region();
r.setCode(374);
r.setName("Armenia");
r.setClimate("Mediterranean");
r.setPopulation(120.000);
```

这是反序列化的对象：

```plaintext
Region{
  country='Country{
    name='Armenia',
    code=374}'
  climate='Mediterranean', 
  population=null
}
```

请注意，由于我们没有序列化Region类中的population字段，因此该属性的值为null。

## 3.可外部化与可序列化

让我们来看看这两个接口之间的主要区别：

-   连载责任

这里的关键区别在于我们如何处理序列化过程。当一个类实现java.io.Serializable接口时，JVM 将全权负责序列化该类实例。在 Externalizable 的情况下，程序员应该负责整个序列化和反序列化过程。

-   用例

如果我们需要序列化整个对象，Serializable接口更合适。另一方面，对于自定义序列化，我们可以使用Externalizable来控制流程。

-   表现

java.io.Serializable接口使用反射和元数据，导致性能相对较慢。相比之下，Externalizable接口让你可以完全控制序列化过程。

-   阅读顺序

在使用Externalizable时，必须按照写入时的确切顺序读取所有字段状态。否则，我们会得到一个例外。

例如，如果我们更改Country类中code和name属性的读取顺序，则会抛出java.io.EOFException 。

同时，Serializable接口没有这个要求。

-   自定义序列化

我们可以通过使用transient关键字标记字段来使用Serializable接口实现自定义序列化。JVM 不会序列化特定字段，但会将该字段添加到具有默认值的文件存储中。这就是为什么在自定义序列化的情况下使用Externalizable是一个好习惯。

## 4。总结

在这个Externalizable接口的简短指南中，我们讨论了主要特性、优点和简单使用的演示示例。我们也和Serializable接口做了对比。