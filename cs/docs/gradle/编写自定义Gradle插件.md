## 1. 概述

Gradle是一个非常强大的构建工具，通常因其高度可定制的构建过程而受到赞赏。本文我们将介绍如何创建自定义Gradle插件，这允许我们修改构建过程，超出我们使用标准配置所能实现的范围。

## 2. 插件源位置

我们可以将代码放在几个不同的位置，它们都有一些优点和缺点。

### 2.1 构建脚本

我们可以简单地将插件的源代码放在构建脚本(build.gradle)中，这会自动编译和包含插件。))虽然这很简单，但是，我们的插件在构建脚本之外是不可见的))。因此，我们不能在其他构建脚本中重用它。

### 2.2 BuildSrc文件夹

我们可以使用的另一种方法是将插件的源代码放在buildSrc/src/main/java文件夹中。当你运行Gradle时，它会检查buildSrc文件夹是否存在。如果存在，Gradle将自动构建并包含我们的插件。

))这使我们能够在各种构建脚本之间共享我们的插件，但我们仍然无法在其他项目中使用它))。

### 2.3 独立项目

最后，我们可以将我们的插件创建为一个单独的项目，这使得该插件可以在各种项目中完全重用。但是，要在外部项目中使用它，我们需要将它捆绑在一个jar文件中并添加到项目中。

## 3. 我们的第一个插件

让我们从最基础的开始，))每个Gradle插件都必须实现com.gradle.api.Plugin接口))。该接口是泛型的，因此我们可以使用各种参数类型对其进行参数化；通常，参数类型为org.gradle.api.Project。

但是，我们可以使用不同的类型参数，以便在不同的生命周期阶段应用插件：

+ 使用org.gradle.api.Settings可以将插件应用于settings脚本。
+ 使用org.gradle.api.Gradle可以将插件应用于初始化脚本。

我们可以创建的最简单的插件是一个hello world应用程序：

```java
public class GreetingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.task("hello")
                .doLast(task -> System.out.println("Hello Gradle!"));
    }
}
```

我们现在可以通过在构建脚本中添加以下代码来应用此插件：

```groovy
apply plugin: GreetingPlugin
```

现在，在执行gradle hello之后，我们将在日志中看到“Hello Gradle!”消息。

## 4. 插件配置

大多数插件都需要从构建脚本访问外部配置，我们可以通过使用Extension对象来做到这一点：

```java
public class GreetingPluginExtension {
    private String greeter = "Tuyucheng";
    private String message = "Message from the plugin!";
    
    // getters and setters ...
}
```

现在让我们将新的Extension对象添加到我们的插件类中：

```java
public class GreetingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        GreetingPluginExtension extension = project.getExtensions().create("greeting", GreetingPluginExtension.class);

        project.task("hello")
                .doLast(task -> {
                            System.out.println("Hello, " + extension.getGreeter());
                            System.out.println("I have a message for You: " + extension.getMessage());
                        }
                );
    }
}
```

现在，当我们调用gradle hello时，我们将看到GreetingPluginExtension中定义的默认消息。但是，既然我们已经创建了Extension，我们可以在构建脚本中使用闭包来实现这一点：

```groovy
greeting {
    greeter = "Stranger"
    message = "Message from the build script"
}
```

## 5. 独立插件项目

为了创建一个独立的Gradle插件，我们需要做更多的事情。

### 5.1 构建

首先，我们需要导入Gradle API依赖项，这非常简单：

```groovy
dependencies {
    compile gradleApi()
}
```

请注意，在Maven中执行同样的操作需要gradle-tooling-api依赖项(来自Gradle仓库)：

```xml
<dependencies>
    <dependency>
        <groupId>org.gradle</groupId>
        <artifactId>gradle-tooling-api</artifactId>
        <version>3.0</version>
    </dependency>
    <dependency>
        <groupId>org.gradle</groupId>
        <artifactId>gradle-core</artifactId>
        <version>3.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>repo.gradle.org</id>
        <url>https://repo.gradle.org/gradle/libs-releases-local/</url>
    </repository>
</repositories>
```

### 5.2 连接插件

为了让Gradle找到独立插件的实现，我们需要在src/main/resources/META-INF/gradle-plugins中创建属性文件。

))资源文件的名称需要与插件ID匹配。因此，如果我们的插件id为org.tuyucheng.greeting，则文件的确切路径将是META-INF/gradle-plugins/org.tuyucheng.greeting.properties))。

接下来，我们可以定义插件的实现类：

```properties
implementation-class=org.gradle.GreetingPlugin
```

implementation-class属性值应该指定为我们插件类的完整包名。

### 5.3 创建插件ID

Gradle中的插件ID必须遵循一些规则和约定，其中大部分类似于Java中的包命名规则：

+ 它们只能包含字母数字字符“.”和”-”
+ id必须至少有一个“.”，将域名与插件名称分开
+ 命名空间org.gradle和com.gradleware受到限制
+ id不能以“.”开头或结尾
+ 不允许两个或多个连续的“.”字符

最后，有一个约定，即插件ID应该是遵循反向域名约定的小写名称。Java包名和Gradle插件名的主要区别在于包名通常比插件ID更详细。

### 5.4 发布插件

当我们想要发布我们的插件以便能够在外部项目中重用它时，我们有两种方法可以实现。

首先，))我们可以将插件JAR发布到Maven或Ivy等外部仓库))。

或者，我们可以使用Gradle插件门户，这将使我们的插件可以被广泛的Gradle社区访问。有关将项目发布到Gradle仓库的更多信息，请参阅[Gradle插件门户文档](https://plugins.gradle.org/docs/publish-plugin)。

### 5.5JavaGradle开发插件

当我们用Java编写插件时，我们可以从Java Gradle Development Plugin中受益。这会自动编译并添加gradleApi()依赖项，它还将执行插件元数据验证，作为gradle jar任务的一部分。

我们可以通过在构建脚本中添加以下代码块来添加插件：

```groovy
plugins {
    id 'java-gradle-plugin'
}
```

## 6. 测试插件

为了测试我们的插件是否正常工作并正确应用于项目，我们可以使用org.gradle.testfixtures.ProjectBuilder来创建Project的实例。然后我们可以检查是否应用了插件，以及Project实例中是否存在正确的任务。

我们可以使用标准的JUnit测试来做到这一点：

```java
@Test
void greetingTest(){
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("cn.tuyucheng.taketoday.greeting");
    
    assertTrue(project.getPluginManager().hasPlugin("cn.tuyucheng.taketoday.greeting"));
    assertNotNull(project.getTasks().getByName("hello"));
}
```

## 7. 总结

在本文中，我们演示了在Gradle中编写自定义插件的基础知识，要更深入地了解插件的创建，请查看[Gradle文档](https://docs.gradle.org/current/userguide/custom_plugins.html)。