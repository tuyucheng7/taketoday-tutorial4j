## 1. 概述

有时在开发过程中，我们可能会添加比我们使用的更多的依赖项。

在本教程中，我们将了解如何使用Gradle [Nebula Lint](https://github.com/nebula-plugins/gradle-lint-plugin)插件来识别和修复此类问题。

## 2. 设置和配置

我们在示例中使用了多模块 Gradle 5 设置。

此插件仅适用于基于 Groovy 的构建文件。

让我们在根项目构建文件中配置它：

```groovy
plugins {
    id "nebula.lint" version "16.9.0"
}

description = "Gradle 5 root project"

allprojects {
    apply plugin :"java"
    apply plugin :"nebula.lint"
    gradleLint {
        rules=['unused-dependency']
    }
    group = "com.baeldung"
    version = "0.0.1"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    repositories {
        jcenter()
    }
}
```

我们暂时只能这样配置多项目构建。这意味着我们不能在每个模块中单独应用它。

接下来，让我们配置我们的模块依赖项：

```groovy
description = "Gradle Unused Dependencies example"

dependencies {
    implementation('com.google.guava:guava:29.0-jre')
    testImplementation('junit:junit:4.12')
}
```

现在让我们在模块源代码中添加一个简单的主类：

```java
public class UnusedDependencies {

    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
```

我们稍后会在此基础上构建，看看插件是如何工作的。

## 3. 检测场景和报告

该插件搜索输出 jar 以检测是否使用了依赖项。

但是，根据[几个条件](https://github.com/nebula-plugins/gradle-lint-plugin/wiki/Unused-Dependency-Rule)，它可以给我们不同的结果。

我们将在下一节探讨更有趣的案例。

### 3.1。未使用的依赖项

现在我们已经完成了设置，让我们看看基本的用例。我们对未使用的依赖项感兴趣。

让我们运行lintGradle任务：

```bash
$ ./gradlew lintGradle

> Task :lintGradle FAILED
# failure output omitted

warning   unused-dependency                  this dependency is unused and can be removed
unused-dependencies/build.gradle:6
implementation('com.google.guava:guava:29.0-jre')

✖ 1 problem (0 errors, 1 warning)

To apply fixes automatically, run fixGradleLint, review, and commit the changes.
# some more failure output

```

让我们来看看发生了什么。我们的compileClasspath配置中有一个未使用的依赖项(guava) 。

如果我们按照插件的建议运行fixGradleLint任务，则依赖项会自动从我们的build.gradle中删除。

但是，让我们在依赖项中使用一些虚拟逻辑：

```java
public static void main(String[] args) {
    System.out.println("Hello world");
    useGuava();
}

private static void useGuava() {
    List<String> list = ImmutableList.of("Baledung", "is", "cool");
    System.out.println(list.stream().collect(Collectors.joining(" ")));
}
```

如果我们重新运行它，我们不会再收到错误：

```bash
$ ./gradlew lintGradle

BUILD SUCCESSFUL in 559ms
3 actionable tasks: 1 executed, 2 up-to-date
```

### 3.2. 使用传递依赖

现在让我们包含另一个依赖项：

```groovy
dependencies {
    implementation('com.google.guava:guava:29.0-jre')
    implementation('org.apache.httpcomponents:httpclient:4.5.12')
    testImplementation('junit:junit:4.12')
}
```

这一次，让我们使用传递依赖中的一些东西：

```java
public static void main(String[] args) {
    System.out.println("Hello world");
    useGuava();
    useHttpCore();
}

// other methods

private static void useHttpCore() {
    SSLContextBuilder.create();
}
```

让我们看看发生了什么：

```bash
$ ./gradlew lintGradle

> Task :lintGradle FAILED
# failure output omitted 

warning   unused-dependency                  one or more classes in org.apache.httpcomponents:httpcore:4.4.13 
are required by your code directly (no auto-fix available)
warning   unused-dependency                  this dependency is unused and can be removed 
unused-dependencies/build.gradle:8
implementation('org.apache.httpcomponents:httpclient:4.5.12')

✖ 2 problems (0 errors, 2 warnings)
```

我们得到两个错误。第一个错误大致说我们应该直接引用httpcore。

我们示例中的SSLContextBuilder实际上是其中的一部分。

第二个错误说我们没有使用来自httpclient 的任何东西。

如果我们使用传递依赖，插件会告诉我们将其设为直接依赖。

让我们看一下我们的依赖树：

```bash
$ ./gradlew unused-dependencies:dependencies --configuration compileClasspath

> Task :unused-dependencies:dependencies

------------------------------------------------------------
Project :unused-dependencies - Gradle Unused Dependencies example
------------------------------------------------------------

compileClasspath - Compile classpath for source set 'main'.
+--- com.google.guava:guava:29.0-jre
|    +--- com.google.guava:failureaccess:1.0.1
|    +--- com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
|    +--- com.google.code.findbugs:jsr305:3.0.2
|    +--- org.checkerframework:checker-qual:2.11.1
|    +--- com.google.errorprone:error_prone_annotations:2.3.4
|    --- com.google.j2objc:j2objc-annotations:1.3
--- org.apache.httpcomponents:httpclient:4.5.12
     +--- org.apache.httpcomponents:httpcore:4.4.13
     +--- commons-logging:commons-logging:1.2
     --- commons-codec:commons-codec:1.11
```

在这种情况下，我们可以看到httpcore是由httpclient引入的。

### 3.3. 将依赖项与反射一起使用

当我们使用反射的时候呢？

让我们稍微增强一下我们的示例：

```java
public static void main(String[] args) {
    System.out.println("Hello world");
    useGuava();
    useHttpCore();
    useHttpClientWithReflection();
}

// other methods

private static void useHttpClientWithReflection() {
    try {
        Class<?> httpBuilder = Class.forName("org.apache.http.impl.client.HttpClientBuilder");
        Method create = httpBuilder.getMethod("create", null);
        create.invoke(httpBuilder, null);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

现在让我们重新运行 Gradle 任务：

```bash
$ ./gradlew lintGradle

> Task :lintGradle FAILED
# failure output omitted

warning   unused-dependency                  one or more classes in org.apache.httpcomponents:httpcore:4.4.13 
are required by your code directly (no auto-fix available)

warning   unused-dependency                  this dependency is unused and can be removed
unused-dependencies/build.gradle:9
implementation('org.apache.httpcomponents:httpclient:4.5.12')

✖ 2 problems (0 errors, 2 warnings)

```

发生了什么？我们使用了依赖项(httpclient)中的HttpClientBuilder ，但仍然出现错误。

如果我们使用带反射的库，插件不会检测到它的使用情况。

结果，我们可以看到相同的两个错误。

一般来说，我们应该将依赖配置为runtimeOnly。

### 3.4. 生成报告

对于大型项目，终端中返回的错误数量变得难以处理。

让我们配置插件来给我们一个报告：

```groovy
allprojects {
    apply plugin :"java"
    apply plugin :"nebula.lint"
    gradleLint {
        rules=['unused-dependency']
        reportFormat = 'text'
    }
    // other  details omitted
}
```

让我们运行generateGradleLintReport任务并检查我们的构建输出：

```bash
$ ./gradlew generateGradleLintReport
# task output omitted

$ cat unused-dependencies/build/reports/gradleLint/unused-dependencies.txt

CodeNarc Report - Jun 20, 2020, 3:25:28 PM

Summary: TotalFiles=1 FilesWithViolations=1 P1=0 P2=3 P3=0

File: /home/user/tutorials/gradle-5/unused-dependencies/build.gradle
    Violation: Rule=unused-dependency P=2 Line=null Msg=[one or more classes in org.apache.httpcomponents:httpcore:4.4.13 
                                                         are required by your code directly]
    Violation: Rule=unused-dependency P=2 Line=9 Msg=[this dependency is unused and can be removed] 
                                                 Src=[implementation('org.apache.httpcomponents:httpclient:4.5.12')]
    Violation: Rule=unused-dependency P=2 Line=17 Msg=[this dependency is unused and can be removed] 
                                                  Src=[testImplementation('junit:junit:4.12')]

[CodeNarc (http://www.codenarc.org) v0.25.2]
```

现在它检测到testCompileClasspath配置上未使用的依赖项。

不幸的是，这是插件的不一致行为。结果，我们现在得到三个错误。

## 4. 总结

在本教程中，我们了解了如何在 Gradle 构建中查找未使用的依赖项。

首先，我们解释了一般设置。之后，我们探讨了不同依赖项报告的错误及其用法。

最后，我们看到了如何生成基于文本的报告。