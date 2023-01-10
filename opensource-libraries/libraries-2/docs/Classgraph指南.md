## 1. 概述

在这个简短的教程中，我们将讨论[Classgraph](https://github.com/classgraph/classgraph)库——它有什么帮助以及我们如何使用它。

Classgraph 帮助我们在Java类路径中找到目标资源，构建关于找到的资源的元数据，并提供方便的 API 来处理元数据。

这个用例在基于 Spring 的应用程序中非常流行，其中标有构造型注解的组件会自动注册到应用程序上下文中。但是，我们也可以将这种方法用于自定义任务。例如，我们可能想要查找所有具有特定注解的类，或者具有特定名称的所有资源文件。

很棒的是Classgraph 速度很快，因为它在字节码级别工作，这意味着检查的类不会加载到 JVM，并且它不会使用反射进行处理。

## 2.Maven依赖

首先，让我们将类图库添加[到](https://search.maven.org/search?q=g:io.github.classgraph AND a:classgraph)我们的pom.xml中：

```xml
<dependency>
    <groupId>io.github.classgraph</groupId>
    <artifactId>classgraph</artifactId>
    <version>4.8.28</version>
</dependency>
```

在接下来的部分中，我们将使用库的 API 研究几个实际示例。

## 3. 基本用法

使用库的三个基本步骤：

1.  设置扫描选项——例如，目标包
2.  执行扫描
3.  使用扫描结果

让我们为示例设置创建以下域：

```java
@Target({TYPE, METHOD, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation {

    String value() default "";
}
@TestAnnotation
public class ClassWithAnnotation {
}
```

现在让我们看看上面的 3 个步骤，一个使用@TestAnnotation查找类的示例：

```java
try (ScanResult result = new ClassGraph().enableClassInfo().enableAnnotationInfo()
  .whitelistPackages(getClass().getPackage().getName()).scan()) {
    
    ClassInfoList classInfos = result.getClassesWithAnnotation(TestAnnotation.class.getName());
    
    assertThat(classInfos).extracting(ClassInfo::getName).contains(ClassWithAnnotation.class.getName());
}
```

让我们分解上面的例子：

-   我们首先设置扫描选项(我们已将扫描器配置为仅解析类和注解信息，并指示它仅解析目标包中的文件)
-   我们使用ClassGraph.scan()方法执行扫描
-   我们使用ScanResult通过调用getClassWithAnnotation()方法来查找带注解的类

正如我们还将在下一个示例中看到的那样，ScanResult对象可以包含有关我们要检查的 API 的大量信息，例如ClassInfoList。

## 4.通过方法注解过滤

让我们将示例扩展到方法注解：

```java
public class MethodWithAnnotation {

    @TestAnnotation
    public void service() {
    }
}
```

我们可以使用类似的方法 - getClassesWithMethodAnnotations()找到所有具有由目标注解标记的方法的类：

```java
try (ScanResult result = new ClassGraph().enableAllInfo()
  .whitelistPackages(getClass().getPackage().getName()).scan()) {
    
    ClassInfoList classInfos = result.getClassesWithMethodAnnotation(TestAnnotation.class.getName());
    
    assertThat(classInfos).extracting(ClassInfo::getName).contains(MethodWithAnnotation.class.getName());
}
```

该方法返回一个ClassInfoList对象，其中包含有关与扫描匹配的类的信息。

## 5.按注解参数过滤

让我们也看看我们如何找到所有带有目标注解标记的方法和目标注解参数值的类。

首先，让我们定义包含带有@TestAnnotation 的方法的类，具有 2 个不同的参数值：

```java
public class MethodWithAnnotationParameterDao {

    @TestAnnotation("dao")
    public void service() {
    }
}
public class MethodWithAnnotationParameterWeb {

    @TestAnnotation("web")
    public void service() {
    }
}
```

现在，让我们遍历ClassInfoList结果，并验证每个方法的注解：

```java
try (ScanResult result = new ClassGraph().enableAllInfo()
  .whitelistPackages(getClass().getPackage().getName()).scan()) {

    ClassInfoList classInfos = result.getClassesWithMethodAnnotation(TestAnnotation.class.getName());
    ClassInfoList webClassInfos = classInfos.filter(classInfo -> {
        return classInfo.getMethodInfo().stream().anyMatch(methodInfo -> {
            AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(TestAnnotation.class.getName());
            if (annotationInfo == null) {
                return false;
            }
            return "web".equals(annotationInfo.getParameterValues().getValue("value"));
        });
    });

    assertThat(webClassInfos).extracting(ClassInfo::getName)
      .contains(MethodWithAnnotationParameterWeb.class.getName());
}
```

在这里，我们使用了AnnotationInfo和MethodInfo元数据类来查找有关我们要检查的方法和注解的元数据。

## 6. 按字段注解过滤

我们还可以使用getClassesWithFieldAnnotation()方法根据字段注解过滤ClassInfoList结果：

```java
public class FieldWithAnnotation {

    @TestAnnotation
    private String s;
}
try (ScanResult result = new ClassGraph().enableAllInfo()
  .whitelistPackages(getClass().getPackage().getName()).scan()) {

    ClassInfoList classInfos = result.getClassesWithFieldAnnotation(TestAnnotation.class.getName());
 
    assertThat(classInfos).extracting(ClassInfo::getName).contains(FieldWithAnnotation.class.getName());
}
```

## 7.寻找资源

最后，我们将看看如何找到有关类路径资源的信息。

让我们在类图类路径根目录中创建一个资源文件——例如，src/test/resources/classgraph/my.config——并为其提供一些内容：

```java
my data
```

我们现在可以找到资源并获取其内容：

```java
try (ScanResult result = new ClassGraph().whitelistPaths("classgraph").scan()) {
    ResourceList resources = result.getResourcesWithExtension("config");
    assertThat(resources).extracting(Resource::getPath).containsOnly("classgraph/my.config");
    assertThat(resources.get(0).getContentAsString()).isEqualTo("my data");
}
```

我们可以在这里看到我们使用了ScanResult的getResourcesWithExtension()方法来查找我们的特定文件。该类还有其他一些与资源相关的有用方法，例如getAllResources()、getResourcesWithPath()和 getResourcesMatchingPattern()。

这些方法返回一个ResourceList对象，该对象可进一步用于遍历和操作Resource对象。

## 8.实例化

当我们想要实例化找到的类时，不要通过Class.forName而是通过使用库方法ClassInfo.loadClass来实现这一点非常重要。

原因是 Classgraph 使用自己的类加载器从一些 JAR 文件中加载类。因此，如果我们使用Class.forName，同一个类可能会被不同的类加载器加载多次，这可能会导致严重的错误。

## 9.总结

在本文中，我们学习了如何有效地查找类路径资源并使用 Classgraph 库检查它们的内容。