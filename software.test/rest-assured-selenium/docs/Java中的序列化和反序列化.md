## Java中的序列化和反序列化

***Java中的序列化***和***反***序列化 是一个重要的编程概念。它适用于所有主要的编程语言。在本章中，我们将尝试在 Java 语言的上下文中理解这个概念。在本章结束时，我们将能够

-   ***从概念上了解序列化和反序列化的含义***
-   ***了解Java代码的序列化和反序列化***

让我们首先以通用形式定义这两个术语，而不考虑任何编程语言。

### ***什么是序列化？***

序列化是将类的实例（类的*对象*）转换为字节流的过程。然后可以将此字节流作为文件存储在磁盘上，也可以通过网络发送到另一台计算机。当程序关闭或休眠时，还可以使用序列化来保存 Object 的状态。使用序列化将状态保存在磁盘上后，我们可以通过从磁盘中对类进行反序列化来恢复状态。

让我们尝试使用一个小图表来可视化这一点。在此图中，我们将创建一个名为 Rectangle 的小类，它表示一个真实的矩形。这是该课程的代码

```java
public class Rectangle {

	private double height;
	private double width;

	public Rectangle(double height, double width)
	{
		this.height = height;
		this.width = width;
	}

	public double Area()
	{
		return height * width;
	}

	public double Perimeter()
	{
		return 2 * (height + width);
	}
}
```

***注意：**根据 Java 标准，这个类还不是可序列化的，让我们暂时忽略它。*

Rectangle 类的序列化过程如下所示。

![Java中的序列化和反序列化](https://toolsqa.com/gallery/Rest%20Assured/1.Serialization%20and%20Deserialization%20in%20Java.png)

用于从类 Rectangle 对象转换为字节流的编码方案由[***此处***](https://docs.oracle.com/javase/8/docs/technotes/guides/serialization/index.html)提到的序列化编码标准管理。

### ***可序列化接口***

在 Java 中，**Serializable**对象是继承自两个接口中的任何一个的对象

-   [***java.io.Serializable***](https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html)
-   [***java.io.Externalizable***](https://docs.oracle.com/javase/6/docs/api/java/io/Externalizable.html)

Serializable 接口是一个标记接口。这意味着如果您的类派生自此接口，则您不必实现任何方法。这只是一个标记，Java 运行时在尝试序列化类时，只会检查类中是否存在此接口。如果类继承层次结构中存在 Serializable 接口，Java 运行时将负责类的序列化。

另一方面，Externalizable 接口不是标记接口。如果您从 Externalizable 接口派生，则必须实现这两种方法

-   ***读取外部（对象输入输入）***
-   ***writeExternal（ObjectOutput 输出）***

只有当我们想要超越 Java 的默认序列化机制时，我们才应该从 Externalizable 接口继承。如果你想使用默认的 Java 序列化机制，那么你应该只从 Serializable 接口继承。

有了这个理解，我们的 Rectangle 类现在将继承自 Serializable 接口。

```java
public class Rectangle implements Serializable{

	private double height;
	private double width;

	public Rectangle(double height, double width)
	{
		this.height = height;
		this.width = width;
	}

	public double Area()
	{
		return height * width;
	}

	public double Perimeter()
	{
		return 2 * (height + width);
	}
}
```

### ***在 Java 中序列化对象***

让我们快速看一下Java中的序列化过程。在这个过程中，我们将执行这四个步骤

1.  *我们将创建一个新的**FileOutputStream**我们想要序列化类的文件*
2.  *然后我们将在步骤 1 中创建的**FileOutputStream**上的**ObjectOutputStream***
3.  *然后我们将对象写入**ObjectOutputStream***
4.  *最后，我们将关闭所有流对象以正确保存写入并终止所有流。*

下面是执行任务的代码。注意每一行代码上面提到的注释。

```java
public static void SerializeToFile(Object classObject, String fileName)
{
	try {

		// Step 1: Open a file output stream to create a file object on disk.
		// This file object will be used to write the serialized bytes of an object
		FileOutputStream fileStream = new FileOutputStream(fileName);

		// Step 2: Create a ObjectOutputStream, this class takes a files stream.
		// This class is responsible for converting the Object of any type into
		// a byte stream
		ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

		// Step 3: ObjectOutputStream.writeObject method takes an Object and 
		// converts it into a ByteStream. Then it writes the Byte stream into
		// the file using the File stream that we created in step 1.
		objectStream.writeObject(classObject);

		// Step 4: Gracefully close the streams
		objectStream.close();
		fileStream.close();

	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public static void main(String[] args)
{
	Rectangle rect = new Rectangle(18, 78);
	SerializeToFile(rect, "rectSerialized");

}
```

 当我们运行这个程序时，它会在项目的根文件夹中创建一个名为***“rectSerialized”的文件。***只需浏览到该位置并尝试使用记事本打开文件。下图显示

![序列化类打开](https://toolsqa.com/gallery/Rest%20Assured/2.SerializedClassOpen.png)

当您打开此文件时，您可以看到它包含乱码。内容经过编码，不是人类可读的格式。如下图所示。

![编码序列化内容](https://toolsqa.com/gallery/Rest%20Assured/3.EncodedSerializedContent.png)

这显示了序列化的确切含义以及我们如何在 Java 中序列化对象。这也应该让您实际了解序列化过程中涉及的不同步骤，包括不同步骤的输入和输出结果。

### ***反序列化为Java中的对象***

在上一节中，我们学习了序列化将类实例转换为字节流，然后将其存储在磁盘上的文件中。让我们快速看一下Java中与Serialization相反的Deserialization过程。在此过程中，我们将从文件中读取序列化字节流并将其转换回 Class 实例表示。以下是我们将遵循的步骤。

1.  *我们将创建一个新的**FileInputStream**来读取包含目标类的序列化字节流的文件。在我们的例子中是 Rectangle 类。*
2.  *然后，我们将 在步骤 1 中创建的**FileInputStream上创建一个****ObjectInputStream***
3.  *然后，我们将使用 **ObjectInputStream**读取对象并将其存储在 Rectangle 类型的变量中。*
4.  *最后，我们将关闭所有流对象以正确保存写入并终止所有流。*

下面是执行任务的代码。注意每一行代码上面提到的注释。

```java
public static Object DeSerializeFromFileToObject(String fileName)
{
	try {

		// Step 1: Create a file input stream to read the serialized content
		// of rectangle class from the file
		FileInputStream fileStream = new FileInputStream(new File(fileName));

		// Step 2: Create an object stream from the file stream. So that the content
		// of the file is converted to the Rectangle Object instance
		ObjectInputStream objectStream = new ObjectInputStream(fileStream);

		// Step 3: Read the content of the stream and convert it into object
		Object deserializeObject = objectStream.readObject();

		// Step 4: Close all the resources
		objectStream.close();
		fileStream.close();

		// return the deserialized object
		return deserializeObject;

	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}

public static void main(String[] args)
{
	Rectangle rect = new Rectangle(18, 78);
	SerializeToFile(rect, "rectSerialized");

	Rectangle deSerializedRect = (Rectangle) DeSerializeFromFileToObject("rectSerialized");
	System.out.println("Rect area is " + deSerializedRect.Area());
}
```

为了验证 Rectangle 类的原始状态是否已恢复，我们将调试代码并检查 ***deSerializedRect*** 变量。下图显示（*高度：18 和宽度：78*）矩形类的原始状态已恢复。

![反序列化](https://toolsqa.com/gallery/Rest%20Assured/4.Deserialization.png)