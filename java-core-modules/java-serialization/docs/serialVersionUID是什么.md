## 1. 概述

serialVersionUID属性是用于序列化/反序列化[可序列化](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/io/Serializable.html)类的对象的标识符。

在本快速教程中，我们将通过示例讨论什么是serialVersionUID以及如何使用它。

## 2. 串口版UID

简单地说，我们使用serialVersionUID属性来记住Serializable类的版本，以验证加载的类和序列化对象是否兼容。

不同类的serialVersionUID属性是独立的。因此，不同的类没有必要具有唯一的值。

下面我们 通过一些例子来学习如何使用serialVersionUID 。

让我们首先创建一个可序列化的类并声明一个serialVersionUID标识符：

```java
public class AppleProduct implements Serializable {

    private static final long serialVersionUID = 1234567L;

    public String headphonePort;
    public String thunderboltPort;
}
```

接下来，我们需要两个实用程序类：一个将AppleProduct对象序列化为一个字符串，另一个将对象从该字符串反序列化：

```java
public class SerializationUtility {

    public static void main(String[] args) {
        AppleProduct macBook = new AppleProduct();
        macBook.headphonePort = "headphonePort2020";
        macBook.thunderboltPort = "thunderboltPort2020";

        String serializedObj = serializeObjectToString(macBook);
 
        System.out.println("Serialized AppleProduct object to string:");
        System.out.println(serializedObj);
    }

    public static String serializeObjectToString(Serializable o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
public class DeserializationUtility {
 
    public static void main(String[] args) {
 
        String serializedObj = ... // ommited for clarity
        System.out.println(
          "Deserializing AppleProduct...");
 
        AppleProduct deserializedObj = (AppleProduct) deSerializeObjectFromString(
          serializedObj);
 
        System.out.println(
          "Headphone port of AppleProduct:"
            + deserializedObj.getHeadphonePort());
        System.out.println(
          "Thunderbolt port of AppleProduct:"
           + deserializedObj.getThunderboltPort());
    }
 
    public static Object deSerializeObjectFromString(String s)
      throws IOException, ClassNotFoundException {
  
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
          new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}
```

我们首先运行SerializationUtility.java，它将 AppleProduct对象保存(序列化)到一个String实例中，使用[Base64](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Base64.html)对字节进行编码。

然后，使用该字符串作为反序列化方法的参数，我们运行DeserializationUtility.java，它根据给定的字符串重新组装(反序列化) AppleProduct对象。

生成的输出应与此类似：

```plaintext
Serialized AppleProduct object to string:
rO0ABXNyACljb20uYmFlbGR1bmcuZGVzZXJpYWxpemF0aW9uLkFwcGxlUHJvZHVjdAAAAAAAEta
HAgADTAANaGVhZHBob25lUG9ydHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wADmxpZ2h0ZW5pbmdQb3
J0cQB+AAFMAA90aHVuZGVyYm9sdFBvcnRxAH4AAXhwdAARaGVhZHBob25lUG9ydDIwMjBwdAATd
Gh1bmRlcmJvbHRQb3J0MjAyMA==
Deserializing AppleProduct...
Headphone port of AppleProduct:headphonePort2020
Thunderbolt port of AppleProduct:thunderboltPort2020
```

现在，让我们修改 AppleProduct.java 中的 serialVersionUID 常量，并重新尝试从之前生成的相同字符串中反序列化AppleProduct对象。重新运行DeserializationUtility.java应该会生成此输出。

```plaintext
Deserializing AppleProduct...
Exception in thread "main" java.io.InvalidClassException: deserialization.cn.tuyucheng.taketoday.AppleProduct; local class incompatible: stream classdesc serialVersionUID = 1234567, local class serialVersionUID = 7654321
	at java.io.ObjectStreamClass.initNonProxy(ObjectStreamClass.java:616)
	at java.io.ObjectInputStream.readNonProxyDesc(ObjectInputStream.java:1630)
	at java.io.ObjectInputStream.readClassDesc(ObjectInputStream.java:1521)
	at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:1781)
	at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1353)
	at java.io.ObjectInputStream.readObject(ObjectInputStream.java:373)
	at deserialization.cn.tuyucheng.taketoday.DeserializationUtility.deSerializeObjectFromString(DeserializationUtility.java:24)
	at deserialization.cn.tuyucheng.taketoday.DeserializationUtility.main(DeserializationUtility.java:15)
```

通过更改类的serialVersionUID ，我们修改了它的版本/状态。结果在反序列化的时候没有找到兼容的类，抛出了InvalidClassException。

如果Serializable类中没有提供serialVersionUID，JVM 将自动生成一个。但是，最好提供serialVersionUID值并在类更改后更新它，以便我们可以控制序列化/反序列化过程。我们将在后面的部分中仔细研究它。

## 3.兼容的变化

假设我们需要向现有的AppleProduct类添加一个新字段lightningPort：

```java
public class AppleProduct implements Serializable {
//...
    public String lightningPort;
}
```

由于我们只是添加一个新字段，因此不需要更改serialVersionUID。这是因为，在反序列化过程中，空将被分配为lightningPort字段的默认值。

让我们修改我们的DeserializationUtility类来打印这个新字段的值：

```java
System.out.println("LightningPort port of AppleProduct:"
  + deserializedObj.getLightningPort());
```

现在，当我们重新运行DeserializationUtility类时，我们将看到类似于以下的输出：

```plaintext
Deserializing AppleProduct...
Headphone port of AppleProduct:headphonePort2020
Thunderbolt port of AppleProduct:thunderboltPort2020
Lightning port of AppleProduct:null
```

## 4. 默认串口版本

如果我们不 为 Serializable 类定义serialVersionUID 状态，那么Java将根据类本身的一些属性(例如类名、实例字段等)来定义一个。

让我们定义一个简单的 可序列化 类：

```java
public class DefaultSerial implements Serializable {
}
```

如果我们像下面这样序列化这个类的一个实例：

```java
DefaultSerial instance = new DefaultSerial();
System.out.println(SerializationUtility.serializeObjectToString(instance));
```

这将打印序列化二进制文件的 Base64 摘要：

```plaintext
rO0ABXNyACpjb20uYmFlbGR1bmcuZGVzZXJpYWxpemF0aW9uLkRlZmF1bHRTZXJpYWx9iVz3Lz/mdAIAAHhw
```

就像以前一样，我们应该能够从摘要中反序列化这个实例：

```java
String digest = "rO0ABXNyACpjb20uYmFlbGR1bmcuZGVzZXJpY" 
  + "WxpemF0aW9uLkRlZmF1bHRTZXJpYWx9iVz3Lz/mdAIAAHhw";
DefaultSerial instance = (DefaultSerial) DeserializationUtility.deSerializeObjectFromString(digest);
```

但是，对此类的一些更改可能会破坏序列化兼容性。例如，如果我们 向此类添加一个私有 字段：

```java
public class DefaultSerial implements Serializable {
    private String name;
}
```

然后尝试将相同的 Base64 摘要反序列化为类实例，我们将得到一个InvalidClassException：

```java
Exception in thread "main" java.io.InvalidClassException: 
  deserialization.cn.tuyucheng.taketoday.DefaultSerial; local class incompatible: 
  stream classdesc serialVersionUID = 9045863543269746292, 
  local class serialVersionUID = -2692722436255640434
```

由于这种不需要的不兼容性，在Serializable类中声明serialVersionUID 始终是个好主意。 这样我们就可以随着类本身的发展而保留或发展版本。

## 5.总结

在这篇快速文章中，我们演示了使用serialVersionUID常量来促进序列化数据的版本控制。