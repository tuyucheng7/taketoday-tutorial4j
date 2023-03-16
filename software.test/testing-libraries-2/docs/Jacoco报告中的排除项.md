## 1. 简介

在本教程中，我们将学习如何从[JaCoCo](https://www.baeldung.com/jacoco)测试覆盖率报告中排除某些类和包。

通常，排除的候选对象可以是配置类、POJO、DTO以及生成的字节码。这些不包含特定的业务逻辑，将它们从报告中排除以提供更好的测试覆盖率视图可能很有用。

我们将探索Maven和Gradle项目中的各种排除方式。

## 2. 示例

让我们从一个示例项目开始，其中我们已经包含了测试所涵盖的所有必需代码。

接下来，我们将通过运行mvn clean package或mvn jacoco:report生成覆盖率报告：

<img src="../assets/img_3.png">

这份报告显示我们已经有了所需的覆盖率，错过的指令应该从JaCoCo报告指标中排除。

## 3. 使用插件配置排除

可以在插件配置中**使用标准*和?排除类和包的通配符语法**：

-    *匹配零个或多个字符
-    **匹配零个或多个目录
-   ?匹配单个字符

### 3.1 Maven配置

让我们更新Maven插件以添加几个排除模式：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>cn/tuyucheng/taketoday/**/ExcludedPOJO.class</exclude>
            <exclude>cn/tuyucheng/taketoday/**/*DTO.*</exclude>
            <exclude>**/config/*</exclude>
        </excludes>
    </configuration>
    <!--...-->
</plugin>
```

在这里，我们指定了以下排除项：

-   cn.tuyucheng.taketoday包下任何子包中的ExcludedPOJO类
-   cn.tuyucheng.taketoday包下任何子包中名称以DTO结尾的所有类
-   在根或子包中的任何位置声明的config包

### 3.2 Gradle配置

我们也可以在Gradle项目中应用相同的排除项。

首先，我们将更新build.gradle中的JaCoCo配置并指定排除列表，使用与之前相同的模式：

```groovy
jacocoTestReport {
    dependsOn test // tests are required to run before generating the report

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                    "cn/tuyucheng/taketoday/**/ExcludedPOJO.class",
                    "cn/tuyucheng/taketoday/**/*DTO.*",
                    "**/config/*"
            ])
        }))
    }
}
```

我们使用闭包来遍历类目录并消除与指定模式列表匹配的文件。因此，使用./gradlew jacocoTestReport或./gradlew clean test生成报告将按预期排除所有指定的类和包。

值得注意的是，JaCoCo插件在这里绑定到test阶段，该阶段在生成报告之前运行所有测试。

## 4. 使用自定义注解排除

从JaCoCo 0.8.2开始，我们可以**通过使用具有以下属性的[自定义注解](https://www.baeldung.com/java-custom-annotation)标注类和方法来排除它们**：

-   注解的名称应包括Generated
-   注解的保留策略应该是RUNTIME或CLASS

首先，我们将创建我们的注解：

```java
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD, CONSTRUCTOR})
public @interface Generated {
}
```

现在我们可以标注应该从覆盖率报告中排除的类或方法或构造函数。

让我们首先在类级别使用此注解：

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

最后，让我们将注解应用于构造函数：

```java
public class CustomerService {

    @Generated
    public CustomerService(){
        // constructor excluded from coverage report
    }
}
```

## 5. 排除Lombok生成的代码

[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)是一个流行的库，用于大大减少Java项目中的样板和重复代码。

让我们看看如何通过**在项目根目录的lombok.config文件中添加一个属性来排除所有Lombok生成的字节码**：

```lombok.config
lombok.addLombokGeneratedAnnotation=true
```

基本上，此属性将lombok.@Generated注解添加到所有使用Lombok注解标注的类(例如Product类)的相关方法、类和字段。因此，JaCoCo会忽略所有使用此注解标注的构造，并且它们不会显示在报告中。

最后，我们可以在应用上面显示的所有排除技术后看到报告：

<img src="../assets/img_4.png">

## 6. 总结

在本文中，我们演示了从JaCoCo测试报告中指定排除项的各种方法。

最初，我们在插件配置中使用命名模式排除了几个文件和包。然后我们看到了如何使用@Generated来排除某些类以及方法。最后，我们学习了如何使用配置文件从测试覆盖率报告中排除所有Lombok生成的代码。