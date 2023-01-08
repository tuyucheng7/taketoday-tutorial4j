## 1. 概述

在本教程中，我们演示了如何在 Jackson 中使用@JsonFormat。

@JsonFormat是一个 Jackson 注解，我们用它来指定如何格式化 JSON 输出的字段和/或属性。

具体来说，此注解允许我们指定如何根据SimpleDateFormat格式设置日期和日历值的格式。

## 2.Maven依赖

@JsonFormat定义在[jackson-databind](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")包中，所以我们需要如下 Maven 依赖：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

## 3. 开始

### 3.1. 使用默认格式

我们将从演示将@JsonFormat注解与代表用户的类一起使用的概念开始。

由于我们要解释注解的细节，User对象将根据请求创建(而不是从数据库存储或加载)并序列化为 JSON：

```java
public class User {
    private String firstName;
    private String lastName;
    private Date createdDate = new Date();

    // standard constructor, setters and getters
}

```

构建并运行此代码示例会返回以下输出：

```javascript
{"firstName":"John","lastName":"Smith","createdDate":1482047026009}
```

如我们所见，createdDate字段显示为自纪元以来的毫秒数，这是日期字段使用的默认格式。

### 3.2. 在 Getter 上使用注解

我们现在将使用@JsonFormat来指定序列化createdDate字段的格式。

让我们看一下为此更改更新的 User 类。如图所示，我们对createdDate字段进行注解以指定日期格式。

用于模式参数的数据格式由[SimpleDateFormat](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/SimpleDateFormat.html)指定：

```java
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
private Date createdDate;
```

完成此更改后，我们再次构建项目并运行它。

这是输出：

```javascript
{"firstName":"John","lastName":"Smith","createdDate":"2016-12-18@07:53:34.740+0000"}
```

在这里，我们使用@JsonFormat注解使用指定的SimpleDateFormat格式格式化createdDate字段。

上面的示例演示了在字段上使用注解。我们也可以在 getter 方法(属性)中使用它。

例如，我们可能有一个在调用时计算的属性。在这种情况下，我们可以在 getter 方法上使用注解。

请注意，我们还更改了模式以仅返回即时的日期部分：

```java
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
public Date getCurrentDate() {
    return new Date();
}
```

这是输出：

```javascript
{ ... , "currentDate":"2016-12-18", ...}
```

### 3.3. 指定语言环境

除了指定日期格式外，我们还可以指定序列化的语言环境。

不指定此参数会导致使用默认语言环境执行序列化：

```java
@JsonFormat(
  shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ", locale = "en_GB")
public Date getCurrentDate() {
    return new Date();
}
```

### 3.4. 指定形状

使用@JsonFormat并将形状设置为JsonFormat.Shape.NUMBER会导致Date类型的默认输出 - 作为自纪元以来的毫秒数。

参数模式不适用于这种情况，将被忽略：

```java
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public Date getDateNum() {
    return new Date();
}
```

让我们看看输出：

```javascript
{ ..., "dateNum":1482054723876 }
```

## 4. 总结

综上所述，我们使用@JsonFormat来控制Date和Calendar类型的输出格式。