## 1. 简介

[Project Lombok](https://www.baeldung.com/intro-to-project-lombok) 是一个流行的Java库，可帮助减少开发人员需要编写的样板代码量。

在本教程中，我们将了解 Lombok 的[@Builder](https://projectlombok.org/features/Builder) 注解是如何工作的，以及我们如何根据我们的特定需求对其进行自定义。

## 2.Maven依赖

让我们从将[依赖](https://search.maven.org/search?q=a:lombok)项添加到我们的pom.xml开始：

```shell
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
```

## 3. Lombok Builder注解

在我们研究自定义 Lombok 生成的构建器类之前，让我们快速回顾一下 Lombok @Builder注解的工作原理。我们已经对[Lombok 的功能进行了全面介绍。](https://www.baeldung.com/intro-to-project-lombok)

@Builder[注解](https://www.baeldung.com/lombok-builder)可用于为我们的类自动生成构建[器](https://www.baeldung.com/lombok-builder)。对于我们的示例，我们将使用一个消息系统，一个用户可以在其中向另一个用户发送消息。该消息是一个简单的文本字符串或一个File。使用 Lombok，我们可以如下定义Message类：

```java
@Builder
@Data
public class Message {
    private String sender;
    private String recipient;
    private String text;
    private File file;
}
```

@Data生成通常与简单 POJO(普通旧Java对象)关联的所有样板：所有字段的 getter、所有非最终字段的 setter、适当的toString、equals和hashCode实现，以及构造函数。

使用生成的构建器，我们现在可以生成Message类的实例：

```java
Message message = Message.builder()
  .sender("user@somedomain.com")
  .recipient("someuser@otherdomain.com")
  .text("How are you today?")
  .build();
```

@Builder注解也支持属性的默认值，但我们现在不讨论它。从这个例子中应该可以清楚地看出@Builder注解是相当强大的，可以替代很多样板代码。

## 4.自定义Lombok Builders

上一节展示了我们如何使用 Lombok 生成构建器类。但是可能会出现生成的builder不够用的情况。在我们的示例中，我们有一个约束条件，即消息只能包含文本或文件。它不能兼而有之。Lombok 当然不知道，生成的构建器会很乐意让我们进入那个非法状态。

幸运的是，我们可以通过定制构建器来解决这个问题。

自定义 Lombok 构建器简单明了：我们编写构建器中我们想要自定义的部分，而 Lombok @Builder注解将不会生成这些部分。所以在我们的例子中，这将是：

```java
public static class MessageBuilder {
    private String text;
    private File file;

    public MessageBuilder text(String text) {
        this.text = text;
        verifyTextOrFile();
        return this;
    }

    public MessageBuilder file(File file) {
        this.file = file;
        verifyTextOrFile();
        return this;
    }

    private void verifyTextOrFile() {
        if (text != null && file != null) {
            throw new IllegalStateException("Cannot send 'text' and 'file'.");
        }
    }
}
```

请注意，我们不必声明发送者和接收者成员，或与它们关联的构建器方法。Lombok 仍然会为我们生成那些。

如果我们尝试使用以下代码生成包含文本和文件的Message实例：

```java
Message message = Message.builder()
  .sender("user@somedomain.com")
  .recipient("someuser@otherdomain.com")
  .text("How are you today?")
  .file(new File("/path/to/file"))
  .build();
```

它将导致以下异常：

```shell
Exception in thread "main" java.lang.IllegalStateException: Cannot send 'text' and 'file'.
```

## 5.总结

在这篇快速文章中，我们研究了如何自定义 Lombok 构建器。