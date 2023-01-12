## 1. 概述

很容易误用[bean 验证](https://www.baeldung.com/javax-validation)约束。例如，我们可能不小心用@Future约束修饰了一个String属性。此类错误会导致运行时出现不可预知的错误。

幸运的是，[Hibernate Validator Annotation Processor](https://hibernate.org/validator/tooling/)有助于在编译时检测这些问题。由于它抛出的错误，我们可以更早地发现这些错误。

在本教程中，我们将探索如何配置处理器，并查看它可以为我们找到的一些常见问题。

## 2.配置

### 2.1. 安装

让我们从将[注解处理器依赖](https://search.maven.org/artifact/org.hibernate.validator/hibernate-validator-annotation-processor)项添加到我们的 pom.xml 开始：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.6.1</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgs>
            <arg>-Averbose=true</arg>
            <arg>-AmethodConstraintsSupported=true</arg>
            <arg>-AdiagnosticKind=ERROR</arg>
        </compilerArgs>
        <annotationProcessorPaths>
            <path>
                <groupId>org.hibernate.validator</groupId>
                <artifactId>hibernate-validator-annotation-processor</artifactId>
                <version>6.2.0.Final</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

我们应该注意，此工具的版本 7 仅与[jakarta.validation](https://search.maven.org/search?q=g:jakarta.validation AND a:jakarta.validation-api)约束兼容：

```xml
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.1</version>
</dependency>
```

该处理器还提供[有关](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/?v=7.0#validator-annotationprocessor-ide)如何针对主要JavaIDE 进行设置的指南。

### 2.2. 编译器选项

让我们设置我们的处理器编译器选项：

```xml
<compilerArgs>
    <arg>-Averbose=true</arg>
    <arg>-AmethodConstraintsSupported=true</arg>
    <arg>-AdiagnosticKind=ERROR</arg>
</compilerArgs>

```

首先，diagnosticKind选项针对日志记录级别。最好保留默认的ERROR值，以便在编译时发现问题。[Diagnostic.Kind](https://docs.oracle.com/en/java/javase/12/docs//api/java.compiler/javax/tools/Diagnostic.Kind.html)枚举中引用了所有允许的值。

接下来，如果我们只想将注解验证限制为 getter，我们应该将methodConstraintsSupported选项设置为false。

在这里，我们将verbose设置为true以获得更多输出，但如果我们不想要大量日志输出，我们可以将其设置为false 。

## 3. 常见的约束问题

注解处理器带有一组要检查的预定义错误。让我们以一个简单的Message类为例，仔细看看其中的三个：

```kotlin
public class Message {
    // constructor omitted
}
```

### 3.1. 只能注解 getter

首先，处理器的默认选项不应该存在这个问题。顾名思义，当我们注解一个非getter方法时，它就会弹出。我们需要将methodConstraintsSupported选项设置为true以允许这样做。

让我们向Message类添加三个带注解的方法：

```typescript
@Min(3)
public boolean broadcast() {
    return true;
}

@NotNull
public void archive() {
}

@AssertTrue
public boolean delete() {
    return false;
}

```

接下来，我们在配置中将 methodConstraintsSupported选项设置为false：

```xml
<compilerArgs>
    <arg>AmethodConstraintsSupported=false</arg>
</compilerArgs>
```

最后，这三种方法将使处理器检测到我们的问题：

```vim
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalmethodvalidationmodelReservationManagement.java:[25,4] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[55,4] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[38,5] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[47,4] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.
[INFO] 4 errors
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.457 s
[INFO] Finished at: 2022-01-20T21:42:47Z
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.6.1:compile (default-compile) on project javaxval: Compilation failure: Compilation failure:
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalmethodvalidationmodelReservationManagement.java:[25,4] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[55,4] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[38,5] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[47,4] error: Constraint annotations must not be specified at methods, which are no valid JavaBeans getter methods.

```

有趣的是，删除方法受到该问题的影响，尽管从技术上讲，它已被正确注解。

对于接下来的部分，我们将把methodConstraintsSupported选项设置回true。

### 3.2. 只能注解非 Void 方法

这个问题表明我们不应该用约束验证来修饰void方法。我们可以通过在Message类中注解存档方法来查看它的实际效果：

```typescript
@NotNull
public void archive() {
}

```

它会导致处理器引发错误：

```csharp
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[45,4] error: Void methods may not be annotated with constraint annotations.
[INFO] 1 error
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.078 s
[INFO] Finished at: 2022-01-20T21:35:08Z
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.6.1:compile (default-compile) on project javaxval: Compilation failure
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[45,4] error: Void methods may not be annotated with constraint annotations.

```

### 3.3. 不支持的注解类型

最后一个问题是最常见的。它发生在注解目标数据类型与目标属性不匹配时。为了在我们的Message类中看到它的作用，让我们向我们的Message类添加一个错误注解的String属性：

```typescript
@Past 
private String createdAt;

```

@Past注解会导致错误。事实上，只有日期类型可以使用这个约束：

```csharp
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] ${home}baeldungtutorialsjavaxvalhibernatevalidatorapMessage.java:[20,5] error: The annotation @Past is disallowed for this data type.
[INFO] 1 error
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.892 s
[INFO] Finished at: 2022-01-20T21:29:15Z
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.6.1:compile (default-compile) on project javaxval: Compilation failure
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[20,5] error: The annotation @Past is disallowed for this data type.

```

如果我们将错误的注解应用于具有不受支持的返回类型的方法，我们将得到类似的错误：

```java
@Min(3)
public boolean broadcast() { 
    return true;
}

```

处理器错误消息与上一个相同：

```csharp
[ERROR] COMPILATION ERROR :
[INFO] -------------------------------------------------------------
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[37,5] error: The annotation @Min is disallowed for the return type of this method.
[INFO] 1 error
[INFO] -------------------------------------------------------------
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.761 s
[INFO] Finished at: 2022-01-20T21:38:28Z
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.6.1:compile (default-compile) on project javaxval: Compilation failure
[ERROR] ${home}baeldungtutorialsjavaxvalsrcmainjavacombaeldungjavaxvalhibernatevalidatorapMessage.java:[37,5] error: The annotation @Min is disallowed for the return type of this method.

```

## 4. 总结

在本文中，我们尝试了 Hibernate Validator Annotation Processor。

首先，我们安装它并配置它的选项。然后，我们通过三个常见的约束问题探索了它的行为。