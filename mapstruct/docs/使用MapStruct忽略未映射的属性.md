## 1. 概述

在Java应用程序中，我们可能希望将值从一种类型的Javabean 到另一种类型。为避免冗长且容易出错的代码，我们可以使用诸如[MapStruct 之](https://www.baeldung.com/mapstruct)类的 bean 映射器。

虽然用相同的字段名映射相同的字段非常简单，但我们经常会遇到不匹配的 bean。在本教程中，我们将了解 MapStruct 如何处理部分映射。

## 2.映射

MapStruct 是一个Java注解处理器。因此，我们需要做的就是定义映射器接口并声明映射方法。MapStruct 将在编译期间生成此接口的实现。

为简单起见，让我们从两个具有相同字段名称的类开始：

```java
public class CarDTO {
    private int id;
    private String name;
}
public class Car {
    private int id;
    private String name;
}
```

接下来，让我们创建一个映射器接口：

```java
@Mapper
public interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);
    CarDTO carToCarDTO(Car car);
}
```

最后，让我们测试我们的映射器：

```java
@Test
public void givenCarEntitytoCar_whenMaps_thenCorrect() {
    Car entity = new Car();
    entity.setId(1);
    entity.setName("Toyota");

    CarDTO carDto = CarMapper.INSTANCE.carToCarDTO(entity);

    assertThat(carDto.getId()).isEqualTo(entity.getId());
    assertThat(carDto.getName()).isEqualTo(entity.getName());
}
```

## 3.未映射的属性

由于 MapStruct 在编译时运行，因此它比动态映射框架更快。如果映射不完整，它也可以生成错误报告——也就是说，如果不是所有的目标属性都被映射：

```java
Warning:(X,X) java: Unmapped target property: "propertyName".
```

虽然这是在发生事故时有用的警告，但如果字段是故意缺失的，我们可能更愿意以不同的方式处理事情。

让我们用一个映射两个简单对象的例子来探讨这个问题：

```java
public class DocumentDTO {
    private int id;
    private String title;
    private String text;
    private List<String> comments;
    private String author;
}
public class Document {
    private int id;
    private String title;
    private String text;
    private Date modificationTime;
}
```

我们在两个类中都有不应该在映射期间填充的唯一字段。他们是：

-   DocumentDTO中的评论
-   DocumentDTO中的作者
-   文档中的修改时间

如果我们定义一个映射器接口，它会在构建过程中产生警告消息：

```java
@Mapper
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    DocumentDTO documentToDocumentDTO(Document entity);
    Document documentDTOToDocument(DocumentDTO dto);
}
```

由于我们不想映射这些字段，我们可以通过几种方式将它们排除在映射之外。

## 4.忽略特定字段

要跳过特定映射方法中的几个属性，我们可以在@Mapping注解中使用ignore属性：

```java
@Mapper
public interface DocumentMapperMappingIgnore {

    DocumentMapperMappingIgnore INSTANCE =
      Mappers.getMapper(DocumentMapperMappingIgnore.class);

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "author", ignore = true)
    DocumentDTO documentToDocumentDTO(Document entity);

    @Mapping(target = "modificationTime", ignore = true)
    Document documentDTOToDocument(DocumentDTO dto);
}
```

在这里，我们提供了字段名称作为目标，并将ignore设置为true以表明映射不需要它。

但是，这种技术在某些情况下并不方便。我们可能会发现它很难使用，例如，当使用具有大量字段的大模型时。

## 5.未映射的目标策略

为了让事情更清楚，代码更易读，我们可以指定未映射的目标策略。

为此，当没有映射的源字段时，我们使用 MapStruct unmappedTargetPolicy 来提供我们想要的行为：

-   错误：任何未映射的目标属性都将导致构建失败——这可以帮助我们避免意外未映射的字段
-   WARN：(默认)构建期间的警告消息
-   忽略：没有输出或错误

为了忽略未映射的属性并且没有输出警告，我们应该将IGNORE值分配给unmappedTargetPolicy。 根据目的，有几种方法可以做到这一点。

### 5.1. 为每个映射器设置策略

我们可以将unmappedTargetPolicy设置为@Mapper注解。因此，它的所有方法都将忽略未映射的属性：

```java
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapperUnmappedPolicy {
    // mapper methods
}
```

### 5.2. 使用共享的MapperConfig

我们可以忽略多个映射器中未映射的属性，方法是通过@MapperConfig设置unmappedTargetPolicy以在多个映射器之间共享一个设置。

首先我们创建一个带注解的接口：

```java
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IgnoreUnmappedMapperConfig {
}
```

然后我们将该共享配置应用于映射器：

```java
@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface DocumentMapperWithConfig { 
    // mapper methods 
}
```

我们应该注意，这是一个简单的示例，显示了@MapperConfig 的最小用法，这似乎并不比在每个映射器上设置策略好多少。当有多个设置要在多个映射器之间标准化时，共享配置会变得非常有用。

### 5.3. 配置选项

最后，我们可以配置 MapStruct 代码生成器的注解处理器选项。在使用[Maven](https://www.baeldung.com/maven-compiler-plugin)时，我们可以使用处理器插件的compilerArgs参数来传递处理器选项：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven-compiler-plugin.version}</version>
            <configuration>
                <source>${maven.compiler.source}</source>
                <target>${maven.compiler.target}</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${org.mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
                <compilerArgs>
                    <compilerArg>
                        -Amapstruct.unmappedTargetPolicy=IGNORE
                    </compilerArg>
                </compilerArgs>
            </configuration>
        </plugin>
    </plugins>
</build>
```

在这个例子中，我们忽略了整个项目中未映射的属性。

## 6. 优先顺序

我们已经研究了几种可以帮助我们处理部分映射并完全忽略未映射属性的方法。我们还看到了如何在映射器上独立应用它们，但我们也可以将它们组合起来。

假设我们有一个带有默认 MapStruct 配置的大型 bean 和映射器代码库。除了少数情况，我们不想允许部分映射。我们可能会很容易地向一个 bean 或其映射对应物添加更多字段，并在不知不觉中获得部分映射。

因此，通过 Maven 配置添加全局设置可能是一个好主意，以使构建在部分映射的情况下失败。

现在，为了在我们的一些映射器中允许未映射的属性并覆盖全局行为，我们可以结合这些技术，记住优先顺序(从最高到最低)：

-   在映射器方法级别忽略特定字段
-   映射器上的策略
-   共享的 MapperConfig
-   全局配置

## 七. 总结

在本教程中，我们研究了如何配置 MapStruct 以忽略未映射的属性。

首先，我们查看了未映射的属性对映射意味着什么。然后我们看到了如何以几种不同的方式允许部分映射没有错误。

最后，我们学习了如何组合这些技术，同时牢记它们的优先顺序。