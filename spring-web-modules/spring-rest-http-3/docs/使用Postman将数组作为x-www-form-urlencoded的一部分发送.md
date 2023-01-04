## 1. 概述

在本教程中，我们将探索使用 Postman将数组作为x-www-form-urlencoded的一部分发送的方法。

[W3C委员会](https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.1)定义了多种格式，我们可以使用这些格式通过网络层发送数据。这些格式包括表单数据、原始数据和x-www-form-urlencoded数据。默认情况下，我们使用后一种格式发送数据。

## 2. x-www-form-urlencoded

列出的格式描述了在HTTP消息正文中作为一个块发送的表单数据，它发送一个编码的表单数据集以提交给服务器，编码数据具有键值对的格式，服务器必须支持内容类型。

使用此内容类型提交的表单匹配以下编码模式：

-   控件名称和值被转义。
-   ','符号将分隔多个值。
-   '+'符号将替换所有空格字符。
-   保留字符应遵循RFC 17.38符号。
-   所有非字母数字字符都使用百分比编码进行编码。
-   键和值之间用等号('=')分隔，键值对之间用和号('&')分隔。

此外，没有指定数据的长度。但是，使用x-www-form-urlencoded数据类型有数据限制。因此，媒体服务器将拒绝超过配置中指定大小的请求。

此外，在发送包含非字母数字字符的二进制数据或值时，它变得效率低下。包含非字母数字字符的键和值采用[百分比编码](https://en.wikipedia.org/wiki/Percent-encoding#The_application/x-www-form-urlencoded_type)(也称为URL编码)，因此这种类型不适用于二进制数据。因此，我们应该考虑改用表单数据内容类型。

此外，我们不能用它来编码文件，它只能对URL参数或请求正文中的数据进行编码。

## 3. 发送数组

要在Postman中使用x-www-form-urlencoded类型，我们需要在请求的正文选项卡中选择具有相同名称的单选按钮。

如前所述，请求由键值对组成。Postman在将数据发送到服务器之前会对数据进行编码。此外，它将对键和值进行编码。

现在，让我们看看如何在Postman中发送一个数组。

### 3.1 发送简单数组对象

我们将从展示如何发送包含简单对象类型(例如，String元素)的简单数组对象开始。

首先，让我们创建一个Student类，它将一个数组作为实例变量：

```java
class StudentSimple {

   private String firstName;
   private String lastName;
   private String[] courses;

   // getters and setters
}
```

其次，我们将定义一个将公开REST端点的控制器类：

```java
@PostMapping(
      path = "/simple", 
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
public ResponseEntity<StudentSimple> createStudentSimple(StudentSimple student) {
    return ResponseEntity.ok(student);
}
```

当我们使用consume属性时，我们需要将x-www-form-urlencoded定义为控制器将从客户端接受的媒体类型。否则，我们将收到[415 Unsupported Media Type](https://www.baeldung.com/spring-415-unsupported-mediatype)错误。此外，我们需要省略@RequestBody注解，因为该注解不支持x-www-form-urlencoded内容类型。

最后，让我们在Postman中创建一个请求，最简单的方法是使用逗号(',')分隔值：

<img src="../assets/img.png">

但是，如果值包含逗号本身，则此方法可能会导致问题。我们可以通过分别设置每个值来解决问题。要将元素设置为课程数组，我们需要使用相同的键提供键值对：

<img src="../assets/img_1.png">

数组中元素的顺序将遵循请求中提供的顺序。

此外，方括号是可选的。另一方面，如果我们想将一个元素添加到数组中的特定索引，我们可以通过在方括号中指定索引来实现：

<img src="../assets/img_2.png">

我们可以使用[cURL](https://www.baeldung.com/curl-rest)请求测试我们的应用程序：

```bash
curl -X POST \
  http://localhost:8080/students/simple \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'firstName=Jane&lastName=Doe&
        courses[2]=Programming+in+Java&
        courses[1]=Data+Structures&
        courses[0]=Linux+Basics'
```

### 3.2 发送复杂数组对象

现在，让我们看一下发送包含复杂对象的数组的方式。

首先，让我们定义代表单个课程的Course类：

```java
class Course {

    private String name;
    private int hours;
    
    // getters and setters
}
```

接下来，我们将创建一个代表学生的类：

```java
class StudentComplex {

    private String firstName;
    private String lastName;
    private Course[] courses;

    // getters and setters
}
```

让我们在控制器类中添加一个新端点：

```java
@PostMapping(
    path = "/complex",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
public ResponseEntity<StudentComplex> createStudentComplex(StudentComplex student) {
    return ResponseEntity.ok(student);
}
```

最后，让我们在Postman中创建一个请求。与前面的示例一样，要向数组添加元素，我们需要使用相同的键提供键值对：

<img src="../assets/img_3.png">

这里，带索引的方括号是强制性的。要为每个实例变量设置值，我们需要使用点('.')运算符后跟变量名称。

同样，我们可以使用cURL请求测试端点：

```bash
curl -X POST \
  http://localhost:8080/students/complex \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'firstName=Jane&lastName=Doe&
        courses[0].name=Programming+in+Java&
        courses[0].hours=40&
        courses[1].name=Data+Structures&
        courses[1].hours=35'
```

## 4. 总结

在本文中，我们学习了如何在服务器端充分设置Content-Type以避免Unsupported Media Type错误。此外，我们还解释了如何在Postman中使用x-www-form-urlencoded内容类型发送简单数组和复杂数组。