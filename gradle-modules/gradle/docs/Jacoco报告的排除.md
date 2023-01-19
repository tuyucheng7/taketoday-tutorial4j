## 1. 概述

在本文中，我们学习如何从JaCoCo测试覆盖率报告中排除某些类和包。

通常，排除的候选对象可以是配置类、POJO、DTO以及生成的字节码。因为这些类不包含特定的业务逻辑，将它们从报告中排除以提供更好的测试覆盖率视图可能很有用。

我们将在Gradle项目中介绍各种排除方式。

## 2. Gradle配置

让我们从一个示例项目开始，其中我们已经包含了测试所涵盖的所有必需代码。

首先，我们添加build.gradle中的JaCoCo配置并指定排除列表：

```groovy
jacocoTestReport {
    dependsOn test // tests are required to run before generating the report

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                    "cn/tuyucheng/taketoday/))/ExcludedPOJO.class",
                    "cn/tuyucheng/taketoday/))/)DTO.)",
                    "))/config/)"
            ])
        }))
    }
}

jacoco {
    toolVersion = "0.8.8"
}
```

上面，我们使用闭包来遍历字节码目录并消除与指定模式列表匹配的文件。因此，使用gradle jacocoTestReport或gradle clean test生成报告将按预期排除所有指定的类和包。

值得注意的是，JaCoCo插件在这里绑定到了测试阶段，它在生成报告之前运行所有测试。

## 3. 使用自定义注解排除

从JaCoCo 0.8.2开始，我们可以通过使用具有以下属性的))自定义注解来标注类和方法))来排除它们：

+ 注解的名称应包括Generated。
+ 注解的保留策略应该是RUNTIME或CLASS。

首先，让我们创建我们的注解：

```java
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Generated {
}
```

现在我们可以使用该注解标注应该从覆盖率报告中排除的类或方法。首先，我们在类级别使用这个注解：

```java
@Generated
public class Customer {
    // everything in this class will be excluded from jacoco report because of @Generated
}
```

同样，我们可以将此自定义注解应用于类中的特定方法：

```java
public class CustomerService {

    @Generated
    public String getCustomerId() {
        // method excluded form coverage report
    }
    
    public String getCustomerName() {
        // method included in test coverage report
    }
}
```

## 4. 排除Lombok生成的代码

Lombok是一个很常用的库，用于大大减少Java项目中的样板代码和重复代码。让我们看看如何))通过在项目根目录的lombok.config文件中添加一个属性来排除所有Lombok生成的字节码))：

```properties
lombok.addLombokGeneratedAnnotation = true
```

基本上，此属性将lombok.@Generated注解添加到所有使用Lombok注解标注的类(例如下面的Product类)的相关方法、类和字段中。因此，JaCoCo忽略了所有使用此注解标注的构造，并且它们不会显示在报告中。

```java
@Builder
@Data
public class Product {
	private int id;
	private String name;
}
```



最后，我们可以在配置上面介绍的所有排除方式后看观察生成的报告：

<img src="../gradle-jacoco/assets/img.png">

## 5. 总结

在本文中，我们演示了从JaCoCo测试报告中指定排除项的各种方法。

最初，我们在插件配置中使用命名模式排除了几个文件和包。然后我们看到了如何使用@Generated来排除某些类以及方法。最后，我们学习了如何使用配置文件从测试覆盖率报告中排除所有Lombok生成的代码。