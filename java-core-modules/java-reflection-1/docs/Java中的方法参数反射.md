## 1. 概述

Java 8 中加入了 Method Parameter Reflection 支持，简单来说就是提供了在运行时获取参数名称的支持。

在本快速教程中，我们将了解如何在运行时访问构造函数和方法的参数名称——使用反射。

## 2. 编译参数 

为了能够访问方法名称信息，我们必须明确选择加入。

为此，我们在编译期间指定参数选项。

对于 Maven 项目，我们可以在pom.xml中声明此选项：

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.1</version>
  <configuration>
    <source>1.8</source>
    <target>1.8</target>
    <compilerArgument>-parameters</compilerArgument>
  </configuration>
</plugin>

```

## 3.示例类

我们将使用一个人为的Person类和一个名为fullName的属性 来演示：

```java
public class Person {

    private String fullName;

    public Person(String fullName) {
        this.fullName = fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // other methods
}
```

## 4.用法

Parameter类是Java8 中新增的，具有多种有趣的方法。如果提供了-parameters编译器选项，则isNamePresent()方法将返回 true。

要访问参数的名称，我们可以简单地调用 getName() ：

```java
@Test
public void whenGetConstructorParams_thenOk() 
  throws NoSuchMethodException, SecurityException {
 
    List<Parameter> parameters 
        = Arrays.asList(Person.class.getConstructor(String.class).getParameters());
    Optional<Parameter> parameter 
        = parameters.stream().filter(Parameter::isNamePresent).findFirst();
    assertThat(parameter.get().getName()).isEqualTo("fullName");
}

@Test
public void whenGetMethodParams_thenOk() 
  throws NoSuchMethodException, SecurityException {
 
    List<Parameter> parameters = Arrays.asList(
      Person.class.getMethod("setFullName", String.class).getParameters());
    Optional<Parameter> parameter= parameters.stream()
      .filter(Parameter::isNamePresent)
      .findFirst();
 
    assertThat(parameter.get().getName()).isEqualTo("fullName");
}
```

## 5.总结

在这篇快速文章中，我们了解了Java8 中对参数名称的新反射支持。

此信息最明显的用例是帮助在编辑器中实现自动完成支持。