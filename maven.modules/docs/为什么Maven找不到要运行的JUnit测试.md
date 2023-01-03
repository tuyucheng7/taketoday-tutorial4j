## 1. 概述

在处理[Maven](https://www.baeldung.com/category/maven/)项目时，开发人员经常会犯错误，导致JUnit测试在构建期间无法运行。Maven可能找不到要运行的JUnit测试的原因有多种，在本教程中，我们将查看这些特定案例并了解如何修复它们。

## 2. 命名约定

[Maven Surefire插件](https://www.baeldung.com/maven-surefire-plugin)在Maven构建生命周期的测试(test)阶段执行单元测试，重要的是，**每当执行test目标时，Surefire插件就会被Maven生命周期隐式调用**，例如，在运行“mvn test”或“mvn install”时。

默认情况下，Surefire插件会自动包含所有具有以下通配符模式的测试类：

-   \*\*/Test\*.java：包括它的所有子目录和所有以“Test”开头的Java文件名
-   \*\*/\*Test.java：包括它的所有子目录和所有以“Test”结尾的Java文件名
-   \*\*/\*Tests.java：包括它的所有子目录和所有以“Tests”结尾的Java文件名
-   \*\*/\*TestCase.java：包括它的所有子目录和所有以“TestCase”结尾的Java文件名

因此，如果我们的测试不遵循上述通配符模式，Maven将不会选择这些测试来运行。但是，在某些情况下，可能需要遵循特定于项目的命名模式而不是这些标准约定。在这些情况下，我们可以通过显式指定我们想要包含(或排除)的测试和其他模式来覆盖Surefire插件。

例如，让我们考虑以下pom.xml：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M7</version>
            <configuration>
                <includes>
                    <include>**/*_UT.java</include>
                </includes>
                <excludes>
                    <exclude>**/BaseTest.java</exclude>
                    <exclude>**/TestsUtil.java</exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

在这里，我们可以看到除了标准的命名约定之外，我们还包含了要运行的特定测试。此外，我们还排除了某些命名模式，这些模式指示插件在测试阶段不考虑这些测试。

## 3. 不正确的依赖

**使用**[JUnit 5平台](https://www.baeldung.com/junit-5)**时，我们至少需要添加一个TestEngine实现**。例如，如果我们想用JUnit Jupiter编写测试，我们需要将测试工件junit-jupiter-engine添加到pom.xml的依赖项中：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M7</version>
            <dependencies>
                <dependency>
                    <groupId>org.junit.jupiter</groupId>
                    <artifactId>junit-jupiter-engine</artifactId>
                    <version>5.4.0</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

如果我们想通过JUnit平台编写和执行JUnit 3或4测试，我们需要将Vintage Engine添加到依赖项部分：

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.vintage</groupId>
        <artifactId>junit-vintage-engine</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 4. 测试文件夹不正确

将测试放在错误的文件夹中是不考虑执行测试的另一个原因，出现这种情况主要是由于某些IDE(如Eclipse)中的默认类创建路径设置所致。

为了提供一些背景知识，Maven定义了一个标准的目录结构：

```powershell
- src
    - main
        - java
        - resources
        - webapp
    - test
        - java
        - resources

- target
```

main目录是与应用程序本身相关的源代码的根目录，而不是测试代码；test目录包含测试源代码。任何放置在src/main/java目录下的测试都将被跳过；相反，**所有的测试和测试资源应该分别放在src/test/java和src/test/resources文件夹下**。

例如，源类文件应该放在这里：

```bash
src/main/java/Calculator.java
```

并且，相应的测试类文件应该放在这里：

```bash
src/test/java/CalculatorTest.java
```

## 5. 测试方法不是public

Java中的默认访问修饰符是[包私有](https://www.baeldung.com/java-access-modifiers#default)的，这意味着所有在没有附加访问修饰符的情况下创建的类只对同一包中的类可见。一些IDE具有标准配置，因此在创建测试时，它们将被标记为包私有的。此外，测试方法可能被错误地标记为私有。

在JUnit 4之前，Maven只会运行标记为public的测试类。不过，我们应该注意，这不会成为JUnit 5+的问题。但是，**实际的测试方法应该始终标记为public**。

让我们看一个例子：

```java
public class MySampleTest {
    @Test
    private void givenTestCase1_thenPrintTest1() {
        // ...
    }
}
```

在这里，我们可以注意到测试方法被标记为private，因此，Maven不会考虑将此方法用于测试执行，将测试方法标记为public将使测试能够运行。

## 6. 打包

Maven提供了多种选项来[打包应用程序](https://www.baeldung.com/maven-packaging-types)，流行的打包类型是jar、war、ear和pom，如果我们不指定packaging值，它将默认为jar打包类型。每个打包类型都包含一个绑定到特定阶段的[目标列表](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#built-in-lifecycle-bindings)。

**将打包类型标记为pom只会将目标绑定到deploy和install阶段，在这种情况下将跳过test阶段**，这可能是测试未按预期运行的情况之一。

有时，由于复制粘贴错误，Maven打包类型可能被标记为pom：

```xml
<packaging>pom</packaging>
```

要解决此问题，我们需要指定正确的打包类型，例如：

```xml
<packaging>jar</packaging>
```

## 7. 总结

在本文中，我们研究了Maven找不到要运行的JUnit测试的特定情况。

首先，我们了解了命名约定如何影响测试执行。接下来，我们讨论了为在JUnit 5平台上执行的测试要添加的依赖项。此外，我们还注意到将测试不正确地放置在不同的文件夹中或使用私有测试方法可能会阻止测试运行。最后，我们看到了打包类型如何绑定到每个阶段的特定目标。