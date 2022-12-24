## 1. 简介

众所周知，在Java世界中，注解是获取有关类和方法的元信息的一种非常有用的方式。

在本教程中，我们将讨论在运行时扫描[Java注解]()。

## 2. 定义自定义注解

让我们首先定义一个示例注解和一个使用我们的自定义注解的示例类：

```java
@Target({ METHOD, TYPE })
@Retention(RUNTIME)
public @interface SampleAnnotation {
    String name();
}

@SampleAnnotation(name = "annotatedClass")
public class SampleAnnotatedClass {

    @SampleAnnotation(name = "annotatedMethod")
    public void annotatedMethod() {
        // Do something
    }

    public void notAnnotatedMethod() {
        // Do something
    }
}
```

现在，我们准备**在基于类和基于方法的用法上解析此自定义注解的“name”属性**。

## 3. 使用Java反射扫描注解

在Java [Reflection]()的帮助下，**我们可以扫描特定的注解类或特定类的注解方法**。

为了实现这个目标，我们需要使用[ClassLoader加载类]()。因此，当我们知道要在哪个类中扫描注解时，此方法很有用：

```java
Class<?> clazz = ClassLoader.getSystemClassLoader()
      .loadClass("cn.tuyucheng.taketoday.annotation.scanner.SampleAnnotatedClass");
SampleAnnotation classAnnotation = clazz.getAnnotation(SampleAnnotation.class);
Assert.assertEquals("SampleAnnotatedClass", classAnnotation.name());
Method[] methods = clazz.getMethods();
List<String> annotatedMethods = new ArrayList<>();
for (Method method : methods) {
    SampleAnnotation annotation = method.getAnnotation(SampleAnnotation.class);
    if (annotation != null) {
        annotatedMethods.add(annotation.name());
    }
}

assertEquals(1, annotatedMethods.size());
assertEquals("annotatedMethod", annotatedMethods.get(0));
```

## 4. 使用Spring Context库扫描注解

扫描注解的另一种方法是使用ClassPathScanningCandidateComponentProvider类，它包含在Spring Context库中。

首先我们添加[spring-context](https://search.maven.org/search?q=g:org.springframework a:spring-context)依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.22</version>
</dependency>
```

让我们继续一个简单的例子：

```java
ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
provider.addIncludeFilter(new AnnotationTypeFilter(SampleAnnotation.class));

Set<BeanDefinition> beanDefs = provider
      .findCandidateComponents("cn.tuyucheng.taketoday.annotation.scanner");
List<String> annotatedBeans = new ArrayList<>();
for (BeanDefinition bd : beanDefs) {
    if (bd instanceof AnnotatedBeanDefinition) {
        Map<String, Object> annotAttributeMap = ((AnnotatedBeanDefinition) bd)
          .getMetadata()
          .getAnnotationAttributes(SampleAnnotation.class.getCanonicalName());
        annotatedBeans.add(annotAttributeMap.get("name").toString());
    }
}

assertEquals(1, annotatedBeans.size());
assertEquals("SampleAnnotatedClass", annotatedBeans.get(0));
```

与Java Reflections完全不同，**我们可以扫描所有类而不需要知道具体的类名**。

## 5. Spring核心库扫描注解

虽然Spring Core没有直接提供对我们代码中所有注解的全量扫描，但是**我们仍然可以通过使用这个库的一些工具类来开发自己的全量注解扫描器**。

首先，我们需要添加[spring-core](https://search.maven.org/search?q=g:org.springframework a:spring-core)依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.3.22</version>
</dependency>
```

这是一个简单的例子：

```java
Class<?> userClass = ClassUtils.getUserClass(SampleAnnotatedClass.class);

List<String> annotatedMethods = Arrays.stream(userClass.getMethods())
      .filter(method -> AnnotationUtils
      .getAnnotation(method, SampleAnnotation.class) != null)
      .map(method -> method.getAnnotation(SampleAnnotation.class)
      .name())
      .collect(Collectors.toList());

assertEquals(1, annotatedMethods.size());
assertEquals("annotatedMethod", annotatedMethods.get(0));
```

在AnnotationUtils和ClassUtils的帮助下，可以找到使用特定注解标注的方法和类。

## 6. 使用Reflections库扫描注解

[Reflections]()是一个据说是本着Scannotations库的精神编写的库，**它扫描项目的类路径元数据并为其编制索引**。

首先我们添加[reflections](https://search.maven.org/search?q=g:org.reflections a:reflections)依赖：

```java
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.10.2</version>
</dependency>
```

现在我们准备使用该库来搜索带注解的类、方法、字段和类型：

```java
Reflections reflections = new Reflections("cn.tuyucheng.taketoday.annotation.scanner");

Set<Method> methods = reflections
      .getMethodsAnnotatedWith(SampleAnnotation.class);
List<String> annotatedMethods = methods.stream()
      .map(method -> method.getAnnotation(SampleAnnotation.class)
      .name())
      .collect(Collectors.toList());

assertEquals(1, annotatedMethods.size());
assertEquals("annotatedMethod", annotatedMethods.get(0));

Set<Class<?>> types = reflections
      .getTypesAnnotatedWith(SampleAnnotation.class);
List<String> annotatedClasses = types.stream()
      .map(clazz -> clazz.getAnnotation(SampleAnnotation.class)
      .name())
      .collect(Collectors.toList());

assertEquals(1, annotatedClasses.size());
assertEquals("SampleAnnotatedClass", annotatedClasses.get(0));
```

正如我们所见，Reflections库提供了一种灵活的方式来扫描所有带注解的类和方法，所以我们不需要从SampleAnnotatedClass开始。

## 7. 使用Jandex库扫描注解

现在让我们看一下一个名为Jandex的不同库，**我们可以使用它在运行时通过读取代码生成的Jandex文件来扫描注解**。

这个库引入了一个[Maven插件](https://search.maven.org/search?q=g:org.jboss.jandex a:jandex-maven-plugin)来生成一个Jandex文件，其中包含与我们项目相关的元信息：

```xml
<plugin>
    <groupId>org.jboss.jandex</groupId>
    <artifactId>jandex-maven-plugin</artifactId>
    <version>1.2.3</version>
    <executions>
        <execution>
            <phase>compile</phase>
            <id>make-index</id>
            <goals>
                <goal>jandex</goal>
            </goals>
            <configuration>
                <fileSets>
                    <fileSet>
                        <directory>${project.build.outputDirectory}</directory>
                    </fileSet>
                </fileSets>
            </configuration>
        </execution>
    </executions>
</plugin>
```

可以看到，运行maven-install命令后，在target目录中META-INF目录下生成了名为jandex.idx的文件。如果需要，我们还可以使用Maven的重命名插件修改文件的名称。

现在我们准备扫描任何类型的注解。首先，我们需要添加[jandex](https://search.maven.org/search?q=g:org.jboss a:jandex)依赖：

```xml
<dependency>
    <groupId>org.jboss</groupId>
    <artifactId>jandex</artifactId>
    <version>2.4.3.Final</version>
</dependency>
```

下面是一个具体的例子：

```java
IndexReader reader = new IndexReader(appFile.getInputStream());
Index jandexFile = reader.read();
List<AnnotationInstance> appAnnotationList = jandexFile
      .getAnnotations(DotName.createSimple("cn.tuyucheng.taketoday.annotation.scanner.SampleAnnotation"));

List<String> annotatedMethods = new ArrayList<>();
List<String> annotatedClasses = new ArrayList<>();
for (AnnotationInstance annotationInstance : appAnnotationList) {
    if (annotationInstance.target().kind() == AnnotationTarget.Kind.METHOD) {
        annotatedMethods.add(annotationInstance.value("name")
          .value()
          .toString());
    }
    if (annotationInstance.target().kind() == AnnotationTarget.Kind.CLASS) {
        annotatedClasses.add(annotationInstance.value("name")
          .value()
          .toString());
    }
}

assertEquals(1, annotatedMethods.size()); 
assertEquals("annotatedMethod", annotatedMethods.get(0));
assertEquals(1, annotatedClasses.size());
assertEquals("SampleAnnotatedClass", annotatedClasses.get(0));
```

## 8. 总结

根据我们的要求；有多种方法可以在运行时扫描注解，这些方法中的每一种都有其优点和缺点，我们可以决定考虑我们需要什么。