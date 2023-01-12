## 1. 概述

在本文中，我们将学习如何将自定义映射器与[MapStruct 库](https://www.baeldung.com/mapstruct)一起使用。

MapStruct 库用于Javabean 类型之间的映射。通过将自定义映射器与 MapStruct 结合使用， 我们可以自定义默认映射方法。

## 2.Maven依赖

让我们将[mapstruct](https://search.maven.org/search?q=g:org.mapstruct a:mapstruct)库添加到我们的 Maven pom.xml中：

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.3.1.Final</version> 
</dependency>
```

要在项目的目标文件夹中查看自动生成的方法，我们必须将annotationProcessorPaths添加到maven-compiler-plugin插件中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.5.1</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>1.3.1.Final</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## 3.自定义映射器

自定义映射器用于解决特定的转换需求。为此，我们必须定义一个方法来进行转换。然后，我们必须将方法通知给 MapStruct。最后，MapStruct 将调用该方法进行从源到目标的转换。

例如，假设我们有一个计算用户体重指数 (BMI) 报告的应用程序。要计算 BMI，我们必须收集用户的身体值。要将英制单位转换为公制单位，我们可以使用自定义映射器方法。

有两种方法可以通过 MapStruct 使用自定义映射器。我们可以通过在@Mapping注解的qualifiedByName属性中键入来调用自定义方法，或者我们可以为它创建一个注解。

在我们开始之前，我们必须定义一个 DTO 类来保存英制值：

```java
public class UserBodyImperialValuesDTO {
    private int inch;
    private int pound;
    // constructor, getters, and setters
}
```

接下来，让我们定义一个 DTO 类来保存指标值：

```java
public class UserBodyValues {
    private double kilogram;
    private double centimeter;
    // constructor, getters, and setters
}
```

### 3.1. 带有方法的自定义映射器

要开始使用自定义映射器，让我们创建一个带有@Mapper注解的接口：

```java
@Mapper 
public interface UserBodyValuesMapper {
    //...
}
```

其次，让我们用我们想要的返回类型和我们需要转换的参数创建我们的自定义方法。我们必须使用带有 value 参数的 @Named 注解来通知MapStruct有关自定义映射器方法的信息：

```java
@Mapper
public interface UserBodyValuesMapper {

    @Named("inchToCentimeter")
    public static double inchToCentimeter(int inch) {
        return inch  2.54;
    }
 
    //...
}
```

最后，让我们使用@Mapping注解定义映射器接口方法。在此注解中，我们将告诉 MapStruct 有关源类型、目标类型和它将使用的方法：

```java
@Mapper
public interface UserBodyValuesMapper {
    UserBodyValuesMapper INSTANCE = Mappers.getMapper(UserBodyValuesMapper.class);
    
    @Mapping(source = "inch", target = "centimeter", qualifiedByName = "inchToCentimeter")
    public UserBodyValues userBodyValuesMapper(UserBodyImperialValuesDTO dto);
    
    @Named("inchToCentimeter") 
    public static double inchToCentimeter(int inch) { 
        return inch  2.54; 
    }
}
```

让我们测试我们的自定义映射器：

```java
UserBodyImperialValuesDTO dto = new UserBodyImperialValuesDTO();
dto.setInch(10);

UserBodyValues obj = UserBodyValuesMapper.INSTANCE.userBodyValuesMapper(dto);

assertNotNull(obj);
assertEquals(25.4, obj.getCentimeter(), 0);

```

### 3.2. 带有注解的自定义映射器

要使用带有注解的自定义映射器，我们必须定义一个注解而不是@Named注解。然后，我们必须通过指定 @Mapping 注解的 qualifiedByName 参数来通知 MapStruct 关于新创建的注解。

让我们看看我们如何定义注解：

```java
@Qualifier
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PoundToKilogramMapper {
}
```

让我们将@PoundToKilogramMapper注解添加到我们的poundToKilogram方法中：

```java
@PoundToKilogramMapper
public static double poundToKilogram(int pound) {
    return pound  0.4535;
}

```

现在，让我们使用@Mapping注解定义映射器接口方法。在映射注解中，我们将告诉 MapStruct 源类型、目标类型和它将使用的注解类：

```java
@Mapper
public interface UserBodyValuesMapper {
    UserBodyValuesMapper INSTANCE = Mappers.getMapper(UserBodyValuesMapper.class);

    @Mapping(source = "pound", target = "kilogram", qualifiedBy = PoundToKilogramMapper.class)
    public UserBodyValues userBodyValuesMapper(UserBodyImperialValuesDTO dto);

    @PoundToKilogramMapper
    public static double poundToKilogram(int pound) {
        return pound  0.4535;
    }
}
```

最后，让我们测试我们的自定义映射器：

```java
UserBodyImperialValuesDTO dto = new UserBodyImperialValuesDTO();
dto.setPound(100);

UserBodyValues obj = UserBodyValuesMapper.INSTANCE.userBodyValuesMapper(dto);

assertNotNull(obj);
assertEquals(45.35, obj.getKilogram(), 0);

```

## 4. 总结

在本文中，我们学习了如何将自定义映射器与 MapStruct 库一起使用。