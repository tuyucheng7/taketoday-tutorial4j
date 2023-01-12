## 1. 简介

本文介绍了Java源代码级注解处理，并提供了使用此技术在编译期间生成其他源文件的示例。

## 2. 注解处理的应用

源代码级别的注解处理最早出现在Java5 中。它是一种在编译阶段生成额外源文件的便捷技术。

源文件不必是Java文件 — 可以根据源代码中的注解生成任何类型的描述、元数据、文档、资源或任何其他类型的文件。

在许多无处不在的Java库中积极使用注解处理，例如，在 QueryDSL 和 JPA 中生成元类，在 Lombok 库中使用样板代码扩充类。

需要注意的重要一点是注解处理 API 的局限性——它只能用于生成新文件，不能更改现有文件。

值得注意的例外是[Lombok](https://projectlombok.org/)库，它使用注解处理作为引导机制将自身包含在编译过程中并通过一些内部编译器 API 修改 AST。这种 hacky 技术与注解处理的预期目的无关，因此本文不予讨论。

## 3.注解处理API

注解处理分多轮完成。每一轮都从编译器在源文件中搜索注解并选择适合这些注解的注解处理器开始。每个注解处理器依次在相应的源上被调用。

如果在此过程中生成了任何文件，则将以生成的文件作为输入开始另一轮。这个过程一直持续到处理阶段没有新文件生成为止。

每个注解处理器依次在相应的源上被调用。如果在此过程中生成了任何文件，则将以生成的文件作为输入开始另一轮。这个过程一直持续到处理阶段没有新文件生成为止。

注解处理 API 位于javax.annotation.processing包中。必须实现的主要接口是Processor接口，它具有AbstractProcessor类形式的部分实现。此类是我们要扩展的类，以创建我们自己的注解处理器。

## 4. 设置项目

为了演示注解处理的可能性，我们将开发一个简单的处理器来为注解类生成流畅的对象构建器。

我们将把我们的项目分成两个 Maven 模块。其中之一，注解处理器模块，将包含处理器本身和注解，另一个注解用户模块，将包含注解类。这是注解处理的典型用例。

annotation-processor模块的设置如下。我们将使用 Google 的[自动服务](https://github.com/google/auto/tree/master/service)库生成处理器元数据文件(稍后将讨论)，以及针对Java8 源代码调整的maven-compiler-plugin 。这些依赖项的版本被提取到属性部分。

可以在 Maven 中央存储库中找到最新版本的[自动服务](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.auto.service" AND a%3A"auto-service")库和[maven-compiler-plugin ：](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-compiler-plugin")

```xml
<properties>
    <auto-service.version>1.0-rc2</auto-service.version>
    <maven-compiler-plugin.version>
      3.5.1
    </maven-compiler-plugin.version>
</properties>

<dependencies>

    <dependency>
        <groupId>com.google.auto.service</groupId>
        <artifactId>auto-service</artifactId>
        <version>${auto-service.version}</version>
        <scope>provided</scope>
    </dependency>

</dependencies>

<build>
    <plugins>

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven-compiler-plugin.version}</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
            </configuration>
        </plugin>

    </plugins>
</build>
```

带有注解源的注解用户Maven 模块不需要任何特殊调整，除了在 dependencies 部分添加对注解处理器模块的依赖：

```xml
<dependency>
    <groupId>com.baeldung</groupId>
    <artifactId>annotation-processing</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 5. 定义注解

假设我们的注解用户模块中有一个简单的 POJO 类，其中包含几个字段：

```java
public class Person {

    private int age;

    private String name;

    // getters and setters …

}
```

我们想创建一个构建器帮助类来更流畅地实例化Person类：

```java
Person person = new PersonBuilder()
  .setAge(25)
  .setName("John")
  .build();
```

这个PersonBuilder类是生成的明显选择，因为它的结构完全由Person setter 方法定义。

让我们在注解处理器模块中为 setter 方法创建一个@BuilderProperty注解。它将允许我们为每个具有 setter 方法注解的类生成Builder类：

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface BuilderProperty {
}
```

带有ElementType.METHOD参数的@Target注解确保此注解只能放在方法上。

SOURCE保留策略意味着此注解仅在源处理期间可用，而在运行时不可用。

带有@BuilderProperty注解的属性的Person类将如下所示：

```java
public class Person {

    private int age;

    private String name;

    @BuilderProperty
    public void setAge(int age) {
        this.age = age;
    }

    @BuilderProperty
    public void setName(String name) {
        this.name = name;
    }

    // getters …

}
```

## 6. 实现处理器

### 6.1. 创建一个AbstractProcessor子类

我们将从扩展注解处理器Maven 模块中的AbstractProcessor类开始。

首先，我们应该指定该处理器能够处理的注解，以及支持的源代码版本。这可以通过实现Processor接口的方法 getSupportedAnnotationTypes 和 getSupportedSourceVersion 或通过使用@SupportedAnnotationTypes和@SupportedSourceVersion注解来注解的类来完成。

@AutoService注解是自动服务库的一部分，允许生成处理器元数据，这将在以下部分中进行说明。

```java
@SupportedAnnotationTypes(
  "com.baeldung.annotation.processor.BuilderProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, 
      RoundEnvironment roundEnv) {
        return false;
    }
}
```

不仅可以指定具体的注解类名称，还可以指定通配符，例如“com.baeldung.annotation.”来处理com.baeldung.annotation包及其所有子包内的注解，甚至“”来处理所有注解.

我们必须实现的唯一方法是自己进行处理的process方法。编译器会为每个包含匹配注解的源文件调用它。

注解作为第一个Set<? extends TypeElement> annotations参数，有关当前处理轮次的信息作为RoundEnviroment roundEnv参数传递。

如果的注解处理器已经处理了所有传递的注解，并且不希望将它们传递给列表下方的其他注解处理器，则返回布尔值应该为真。

### 6.2. 收集数据

我们的处理器还没有真正做任何有用的事情，所以让我们用代码填充它。

首先，我们需要遍历在类中找到的所有注解类型——在我们的例子中，注解集将有一个元素对应于@BuilderProperty注解，即使该注解在源文件中出现多次。

尽管如此，为了完整起见，最好将process方法实现为迭代循环：

```java
@Override
public boolean process(Set<? extends TypeElement> annotations, 
  RoundEnvironment roundEnv) {

    for (TypeElement annotation : annotations) {
        Set<? extends Element> annotatedElements 
          = roundEnv.getElementsAnnotatedWith(annotation);
        
        // …
    }

    return true;
}
```

在此代码中，我们使用RoundEnvironment实例来接收所有使用@BuilderProperty注解进行注解的元素。对于Person类，这些元素对应于setName和setAge方法。

@BuilderProperty注解的用户可能会错误地注解实际上不是 setter 的方法。setter 方法名称应以set开头，并且该方法应接收单个参数。因此，让我们把小麦和谷壳分开。

在下面的代码中，我们使用Collectors.partitioningBy()收集器将带注解的方法分成两个集合：正确注解的 setter 和其他错误注解的方法：

```java
Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().collect(
  Collectors.partitioningBy(element ->
    ((ExecutableType) element.asType()).getParameterTypes().size() == 1
    && element.getSimpleName().toString().startsWith("set")));

List<Element> setters = annotatedMethods.get(true);
List<Element> otherMethods = annotatedMethods.get(false);
```

在这里，我们使用Element.asType()方法来接收TypeMirror类的实例，这使我们能够在某种程度上内省类型，即使我们仅处于源处理阶段。

我们应该警告用户有关错误注解的方法，因此让我们使用可从AbstractProcessor.processingEnv受保护字段访问的Messager实例。以下几行将在源处理阶段为每个错误注解的元素输出错误：

```java
otherMethods.forEach(element ->
  processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
    "@BuilderProperty must be applied to a setXxx method " 
      + "with a single argument", element));
```

当然，如果正确的 setters 集合为空，则没有必要继续当前类型元素集迭代：

```java
if (setters.isEmpty()) {
    continue;
}
```

如果 setter 集合至少有一个元素，我们将使用它从封闭元素中获取完全限定的类名，在 setter 方法的情况下，它似乎是源类本身：

```java
String className = ((TypeElement) setters.get(0)
  .getEnclosingElement()).getQualifiedName().toString();
```

生成构建器类所需的最后一点信息是设置器名称与其参数类型名称之间的映射：

```java
Map<String, String> setterMap = setters.stream().collect(Collectors.toMap(
    setter -> setter.getSimpleName().toString(),
    setter -> ((ExecutableType) setter.asType())
      .getParameterTypes().get(0).toString()
));
```

### 6.3. 生成输出文件

现在我们拥有了生成构建器类所需的所有信息：源类的名称、它的所有 setter 名称以及它们的参数类型。

要生成输出文件，我们将使用AbstractProcessor.processingEnv受保护属性中的对象再次提供的Filer实例：

```java
JavaFileObject builderFile = processingEnv.getFiler()
  .createSourceFile(builderClassName);
try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
    // writing generated file to out …
}
```

下面提供了writeBuilderFile方法的完整代码。我们只需要计算源类和构建器类的包名、完全限定的构建器类名和简单类名。其余代码非常简单。

```java
private void writeBuilderFile(
  String className, Map<String, String> setterMap) 
  throws IOException {

    String packageName = null;
    int lastDot = className.lastIndexOf('.');
    if (lastDot > 0) {
        packageName = className.substring(0, lastDot);
    }

    String simpleClassName = className.substring(lastDot + 1);
    String builderClassName = className + "Builder";
    String builderSimpleClassName = builderClassName
      .substring(lastDot + 1);

    JavaFileObject builderFile = processingEnv.getFiler()
      .createSourceFile(builderClassName);
    
    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

        if (packageName != null) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println();
        }

        out.print("public class ");
        out.print(builderSimpleClassName);
        out.println(" {");
        out.println();

        out.print("    private ");
        out.print(simpleClassName);
        out.print(" object = new ");
        out.print(simpleClassName);
        out.println("();");
        out.println();

        out.print("    public ");
        out.print(simpleClassName);
        out.println(" build() {");
        out.println("        return object;");
        out.println("    }");
        out.println();

        setterMap.entrySet().forEach(setter -> {
            String methodName = setter.getKey();
            String argumentType = setter.getValue();

            out.print("    public ");
            out.print(builderSimpleClassName);
            out.print(" ");
            out.print(methodName);

            out.print("(");

            out.print(argumentType);
            out.println(" value) {");
            out.print("        object.");
            out.print(methodName);
            out.println("(value);");
            out.println("        return this;");
            out.println("    }");
            out.println();
        });

        out.println("}");
    }
}
```

## 7. 运行示例

要查看实际的代码生成，应该从公共父根编译两个模块，或者先编译注解处理器模块，然后再编译注解用户模块。

生成的PersonBuilder类可以在annotation-user/target/generated-sources/annotations/com/baeldung/annotation/PersonBuilder.java文件中找到，应该如下所示：

```java
package com.baeldung.annotation;

public class PersonBuilder {

    private Person object = new Person();

    public Person build() {
        return object;
    }

    public PersonBuilder setName(java.lang.String value) {
        object.setName(value);
        return this;
    }

    public PersonBuilder setAge(int value) {
        object.setAge(value);
        return this;
    }
}
```

## 8. 注册处理器的替代方法

要在编译阶段使用注解处理器，还有其他几个选项，具体取决于的用例和使用的工具。

### 8.1. 使用注解处理器工具

apt工具是一个用于处理源文件的特殊命令行实用程序。它是Java5 的一部分，但自Java7 以来，它已被弃用，取而代之的是其他选项，并在Java8 中被完全删除。本文不会讨论它。

### 8.2. 使用编译器密钥

-processor编译器密钥是一个标准的 JDK 工具，用于使用自己的注解处理器来增强编译器的源代码处理阶段。

请注意，处理器本身和注解必须已经在单独的编译中编译为类并出现在类路径中，因此应该做的第一件事是：

```java
javac com/baeldung/annotation/processor/BuilderProcessor
javac com/baeldung/annotation/processor/BuilderProperty
```

然后，使用-processor键对的源代码进行实际编译，指定刚刚编译的注解处理器类：

```java
javac -processor com.baeldung.annotation.processor.MyProcessor Person.java
```

要一次指定多个注解处理器，可以用逗号分隔它们的类名，如下所示：

```bash
javac -processor package1.Processor1,package2.Processor2 SourceFile.java
```

### 8.3. 使用 Maven

maven-compiler-plugin允许指定注解处理器作为其配置的一部分。

下面是为编译器插件添加注解处理器的示例。还可以使用generatedSourcesDirectory配置参数指定将生成的源放入的目录。

请注意，BuilderProcessor类应该已经编译，例如，从构建依赖项中的另一个 jar 导入：

```xml
<build>
    <plugins>

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.5.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
                <generatedSourcesDirectory>${project.build.directory}
                  /generated-sources/</generatedSourcesDirectory>
                <annotationProcessors>
                    <annotationProcessor>
                        com.baeldung.annotation.processor.BuilderProcessor
                    </annotationProcessor>
                </annotationProcessors>
            </configuration>
        </plugin>

    </plugins>
</build>
```

### 8.4. 将处理器 Jar 添加到类路径

无需在编译器选项中指定注解处理器，可以简单地将带有处理器类的特殊结构的 jar 添加到编译器的类路径中。

要自动获取它，编译器必须知道处理器类的名称。因此，必须在META-INF/services/javax.annotation.processing.Processor文件中将其指定为处理器的完全限定类名：

```bash
com.baeldung.annotation.processor.BuilderProcessor
```

还可以指定此 jar 中的多个处理器通过用新行分隔它们来自动获取：

```bash
package1.Processor1
package2.Processor2
package3.Processor3
```

如果使用 Maven 构建此 jar 并尝试将此文件直接放入src/main/resources/META-INF/services目录，将遇到以下错误：

```bash
[ERROR] Bad service configuration file, or exception thrown while 
constructing Processor object: javax.annotation.processing.Processor: 
Provider com.baeldung.annotation.processor.BuilderProcessor not found
```

这是因为当BuilderProcessor文件尚未编译时，编译器会尝试在模块本身的源代码处理阶段使用此文件。该文件必须放在另一个资源目录中并在 Maven 构建的资源阶段到META-INF/services目录，或者(甚至更好)在构建期间生成。

下一节中讨论的 Google自动服务库允许使用简单的注解生成此文件。

### 8.5. 使用 Google自动服务库

要自动生成注册文件，可以使用Google 的自动服务库中的@AutoService注解，如下所示：

```java
@AutoService(Processor.class)
public BuilderProcessor extends AbstractProcessor {
    // …
}
```

此注解本身由自动服务库中的注解处理器处理。该处理器生成包含BuilderProcessor类名的META-INF/services/javax.annotation.processing.Processor文件。

## 9.总结

在本文中，我们使用为 POJO 生成 Builder 类的示例演示了源代码级别的注解处理。我们还提供了几种在的项目中注册注解处理器的替代方法。