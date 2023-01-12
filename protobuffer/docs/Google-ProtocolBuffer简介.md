## 1. 概述

在本文中，我们将研究[Google Protocol Buffer](https://developers.google.com/protocol-buffers/) (protobuf)——一种著名的与语言无关的二进制数据格式。我们可以定义一个带有协议的文件，接下来，使用该协议，我们可以用 Java、C++、C#、Go 或 Python 等语言生成代码。

这是格式本身的介绍性文章；如果想了解如何在 Spring Web 应用程序中使用该格式，请查看[这篇文章](https://www.baeldung.com/spring-rest-api-with-protocol-buffers)。

## 2.定义Maven依赖

要使用 protocol buffers 是 Java，我们需要将 Maven 依赖项添加到[protobuf-java](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.protobuf" AND a%3A"protobuf-java")：

```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>${protobuf.version}</version>
</dependency>

<properties>
    <protobuf.version>3.2.0</protobuf.version>
</properties>
```

## 3. 定义协议

让我们从一个例子开始。我们可以用 protobuf 格式定义一个非常简单的协议：

```java
message Person {
    required string name = 1;
}
```

这是一种简单的Person类型消息的协议，它只有一个必填字段——名称，它是一个字符串类型。

让我们看一下定义协议的更复杂示例。假设我们需要以 protobuf 格式存储个人详细信息：

包装协议缓冲区；

```xml
package protobuf;

option java_package = "com.baeldung.protobuf";
option java_outer_classname = "AddressBookProtos";

message Person {
    required string name = 1;
    required int32 id = 2;
    optional string email = 3;

    repeated string numbers = 4;
}

message AddressBook {
    repeated Person people = 1;
}
```

我们的协议包含两种类型的数据：Person和AddressBook。生成代码后(稍后部分将详细介绍)，这些类将成为AddressBookProtos类中的内部类。

当我们想要定义一个必需的字段时——这意味着创建一个没有该字段的对象将导致异常，我们需要使用required关键字。

使用optional关键字创建字段意味着不需要设置该字段。repeated关键字是可变大小的数组类型。

所有字段都被索引——用数字 1 表示的字段将被保存为二进制文件中的第一个字段。标记为 2 的字段将被保存，依此类推。这使我们可以更好地控制字段在内存中的布局方式。

## 4. 从 Protobuf 文件生成Java代码

一旦我们定义了一个文件，我们就可以从中生成代码。

首先，我们需要在我们的机器上[安装 protobuf 。](https://github.com/google/protobuf/releases)完成此操作后，我们可以通过执行protoc命令来生成代码：

```shell
protoc -I=. --java_out=. addressbook.proto
```

protoc命令将从我们的addressbook.proto文件生成Java输出文件。-I选项指定原型文件所在的目录。java-out指定将创建生成的类的目录。

生成的类将为我们定义的消息提供 setter、getter、构造函数和构建器。它还将有一些实用方法，用于保存 protobuf 文件并将它们从二进制格式反序列化为Java类。

## 5. 创建 Protobuf 定义消息的实例

我们可以轻松地使用生成的代码来创建Person类的Java实例：

```java
String email = "j@baeldung.com";
int id = new Random().nextInt();
String name = "Michael Program";
String number = "01234567890";
AddressBookProtos.Person person =
  AddressBookProtos.Person.newBuilder()
    .setId(id)
    .setName(name)
    .setEmail(email)
    .addNumbers(number)
    .build();

assertEquals(person.getEmail(), email);
assertEquals(person.getId(), id);
assertEquals(person.getName(), name);
assertEquals(person.getNumbers(0), number);
```

我们可以通过在所需的消息类型上使用newBuilder()方法来创建流畅的构建器。设置所有必填字段后，我们可以调用build()方法来创建Person类的实例。

## 6. 序列化和反序列化 Protobuf

一旦我们创建了Person类的实例，我们希望以与创建的协议兼容的二进制格式将其保存在磁盘上。假设我们要创建AddressBook类的实例并将一个人添加到该对象。

接下来，我们要将该文件保存在光盘上——我们可以使用自动生成的代码中的writeTo() util 方法：

```java
AddressBookProtos.AddressBook addressBook 
  = AddressBookProtos.AddressBook.newBuilder().addPeople(person).build();
FileOutputStream fos = new FileOutputStream(filePath);
addressBook.writeTo(fos);
```

执行该方法后，我们的对象将被序列化为二进制格式并保存在磁盘上。要从光盘加载该数据并将其反序列化回AddressBook对象，我们可以使用mergeFrom()方法：

```java
AddressBookProtos.AddressBook deserialized
  = AddressBookProtos.AddressBook.newBuilder()
    .mergeFrom(new FileInputStream(filePath)).build();
 
assertEquals(deserialized.getPeople(0).getEmail(), email);
assertEquals(deserialized.getPeople(0).getId(), id);
assertEquals(deserialized.getPeople(0).getName(), name);
assertEquals(deserialized.getPeople(0).getNumbers(0), number);
```

## 七. 总结

在这篇简短的文章中，我们介绍了一种以二进制格式描述和存储数据的标准——Google Protocol Buffer。

我们创建了一个简单的协议，创建了符合定义协议的Java实例。接下来，我们看到了如何使用 protobuf 序列化和反序列化对象。