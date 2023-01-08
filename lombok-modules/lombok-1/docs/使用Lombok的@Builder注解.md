## 1. 概述

Project Lombok 的[@Builder](https://projectlombok.org/features/Builder)是一种有用的机制，无需编写样板代码即可使用[构建器模式。](https://www.baeldung.com/creational-design-patterns#builder)我们可以将此注解应用于 类 或方法。

在本快速教程中，我们将了解@Builder的不同用例。

## 2.Maven依赖

首先，我们需要将[Project Lombok](https://projectlombok.org/)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
```

Maven Central 在[这里有最新版本的 Project Lombok。](https://search.maven.org/classic/#search|ga|1|g%3A"org.projectlombok")

## 3.在类上使用@Builder

在第一个用例中，我们只是实现一个Class，我们想使用一个构建器来创建我们类的实例。

第一步也是唯一的一步是将注解添加到类声明中：

```java
@Getter
@Builder
public class Widget {
    private final String name;
    private final int id;
}

```

Lombok 为我们做了所有的工作。我们现在可以构建一个Widget并对其进行测试：

```java
Widget testWidget = Widget.builder()
  .name("foo")
  .id(1)
  .build();

assertThat(testWidget.getName())
  .isEqualTo("foo");
assertThat(testWidget.getId())
  .isEqualTo(1);
```

如果我们想要创建对象的副本或近似副本，我们可以将属性 toBuilder = true添加到 @Builder注解中：

```java
@Builder(toBuilder = true)
public class Widget {
//...
}
```

这告诉 Lombok 将toBuilder()方法添加到我们的 Class中。当我们调用toBuilder()方法时，它会 返回一个构建器，该构建器使用调用它的实例的属性进行初始化：

```java
Widget testWidget = Widget.builder()
  .name("foo")
  .id(1)
  .build();

Widget.WidgetBuilder widgetBuilder = testWidget.toBuilder();

Widget newWidget = widgetBuilder.id(2).build();
assertThat(newWidget.getName())
  .isEqualTo("foo");
assertThat(newWidget.getId())
  .isEqualTo(2);
```

我们可以在测试代码中看到，Lombok 生成的构建器类的名称与我们的类一样，并在其后附加了“Builder” ，在本例中为WidgetBuilder 。然后我们可以修改我们想要的属性，并build()一个新实例。

如果我们需要指定必填字段，我们可以使用注解配置来创建一个辅助构建器：

```java
@Builder(builderMethodName = "internalBuilder")
public class RequiredFieldAnnotation {
    @NonNull
    private String name;
    private String description;

    public static RequiredFieldAnnotationBuilder builder(String name) {
        return internalBuilder().name(name);
    }
}
```

在这种情况下，我们将默认构建器隐藏为internalBuilder并创建我们自己的构建器。因此，当我们创建构建器时，我们必须提供所需的参数：

```java
RequiredField.builder("NameField").description("Field Description").build();
```

此外，为确保我们的字段存在，我们可以添加@NonNull注解。

## 4.在方法上使用@Builder

假设我们正在使用一个我们想用构建器构造的对象，但我们不能修改源或扩展Class。

[首先，让我们使用Lombok 的 @Value 注解](https://www.baeldung.com/intro-to-project-lombok)创建一个快速示例：

```java
@Value
final class ImmutableClient {
    private int id;
    private String name;
}
```

现在我们有一个带有两个不可变成员的最终 类，它们的 getter 和一个全参数构造函数。

我们介绍了如何在 Class上使用@Builder，但我们也可以在方法上使用它。我们将使用此功能解决无法修改或扩展ImmutableClient的问题。

然后我们将使用创建 ImmutableClients 的方法创建一个新类：

```java
class ClientBuilder {

    @Builder(builderMethodName = "builder")
    public static ImmutableClient newClient(int id, String name) {
        return new ImmutableClient(id, name);
    }
}
```

此注解创建一个名为builder()的方法，该方法返回一个用于创建ImmutableClients的Builder。

现在让我们构建一个ImmutableClient：

```java
ImmutableClient testImmutableClient = ClientBuilder.builder()
  .name("foo")
  .id(1)
  .build();
assertThat(testImmutableClient.getName())
  .isEqualTo("foo");
assertThat(testImmutableClient.getId())
  .isEqualTo(1);
```

## 5.总结

在这篇简短的文章中，我们在一个方法上使用了 Lombok 的@Builder注解来为最终 类创建一个构建器，并且我们学习了如何使一些类字段成为必需的。