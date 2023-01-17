## 1. 概述

[Bazel](https://bazel.build/)是一个用于构建和测试源代码的开源工具，类似于 Maven 和 Gradle。它支持多种语言的项目并为多个平台构建输出。

在本教程中，我们将完成使用 Bazel 构建简单Java应用程序所需的步骤。为了便于说明，我们将从一个多模块 Maven 项目开始，然后使用 Bazel 构建源代码。

我们将从[安装 Bazel](https://docs.bazel.build/versions/master/install.html)开始。

## 2.项目结构

让我们创建一个多模块 Maven 项目：

```plaintext
bazel (root)
    pom.xml
    WORKSPACE (bazel workspace)
    |— bazelapp
        pom.xml
        BUILD (bazel build file)
        |— src
            |— main
                |— java
            |— test
                |— java
    |— bazelgreeting
        pom.xml
        BUILD (bazel build file)
        |— src
            |— main
                |— java
            |— test
                |— java
```

WORKSPACE文件的存在为 Bazel 设置了工作区。一个项目中可能有一个或多个。对于我们的示例，我们将在顶级项目目录中只保留一个文件。

下一个重要文件是BUILD文件，其中包含构建规则。它用唯一的目标 名称标识每个规则。

Bazel提供了灵活性，可以根据需要配置任意数量的BUILD文件，并将其配置为任何粒度级别。这意味着我们可以通过相应地配置BUILD规则来构建较少数量的Java类。为了简单起见，我们将在示例中保留最少的BUILD文件。

由于 Bazel BUILD配置的输出通常是一个 jar 文件，我们将把包含BUILD文件的每个目录称为构建包。

## 3.构建文件

### 3.1. 规则配置

是时候配置我们的第一个构建规则来构建Java二进制文件了。让我们在属于bazelapp模块的BUILD文件中配置一个：

```plaintext
java_binary (
    name = "BazelApp",
    srcs = glob(["src/main/java/com/baeldung/.java"]),
    main_class = "com.baeldung.BazelApp"
)

```

让我们一一了解配置设置：

-   java_binary——规则的名称；它需要额外的属性来构建二进制文件
-   name – 构建目标的名称
-   srcs – 一组文件位置模式，用于指示要构建的Java文件
-   main_class – 应用程序主类的名称(可选)

### 3.2. 构建执行

我们现在可以构建应用程序了。从包含WORKSPACE文件的目录中，让我们在 shell 中执行bazel build命令来构建我们的目标：

```bash
$ bazel build //bazelapp:BazelApp
```

最后一个参数是在其中一个BUILD文件中配置的目标名称。它具有模式“ //<path_to_build>:<target_name> ”。

模式的第一部分“//”表示我们从工作区目录开始。接下来，“bazelapp” ，是从工作区目录到BUILD文件的相对路径。最后，“BazelApp”是要构建的目标名称。

### 3.3. 构建输出

我们现在应该注意到上一步的两个二进制输出文件：

```plaintext
bazel-bin/bazelapp/BazelApp.jar
bazel-bin/bazelapp/BazelApp
```

BazelApp.jar包含所有类，而BazelApp是执行 jar 文件的包装脚本。

### 3.4. 可部署的 JAR

我们可能需要将 jar 及其依赖项运送到不同的位置进行部署。

上一节中的包装器脚本将所有依赖项(jar 文件)指定为BazelApp.jar的启动命令的一部分。

然而，我们也可以制作一个包含所有依赖项的胖罐子：

```shell
$ bazel build //bazelapp:BazelApp_deploy.jar
```

在目标名称后加上“_deploy”后缀 指示 Bazel 将所有依赖项打包到 jar 中并使其准备好进行部署。

## 4.依赖关系

到目前为止，我们只使用bazelapp 中的文件进行构建。但是，大多数应用程序都有依赖性。

在本节中，我们将看到如何将依赖项与 jar 文件一起打包。

### 4.1. 建立图书馆

不过，在我们这样做之前，我们需要一个bazelapp可以使用的依赖项。

让我们创建另一个名为bazelgreeting的 Maven 模块，并使用java_library规则为新模块 配置BUILD文件。我们将这个目标命名为“ greeter”：

```plaintext
java_library (
    name = "greeter",
    srcs = glob(["src/main/java/com/baeldung/.java"])
)
```

在这里，我们使用了java_library规则来创建库。构建此目标后，我们将获得libgreetings.jar文件：

```plaintext
INFO: Found 1 target...
Target //bazelgreeting:greetings up-to-date:
  bazel-bin/bazelgreeting/libgreetings.jar
```

### 4.2. 配置依赖项

要在 bazelapp 中使用 greeter ，我们需要一些额外的配置。首先，我们需要让包对bazelapp可见。我们可以通过在greeter 包的java_library规则中添加visibility属性来实现：

```plaintext
java_library (
    name = "greeter",
    srcs = glob(["src/main/java/com/baeldung/.java"]),
    visibility = ["//bazelapp:__pkg__"]
)
```

visibility属性使当前包对数组中列出的包可见。

现在在bazelapp包中，我们必须配置对greeter包的依赖 。让我们用deps属性来做到这一点：

```plaintext
java_binary (
    name = "BazelApp",
    srcs = glob(["src/main/java/com/baeldung/.java"]),
    main_class = "com.baeldung.BazelApp",
    deps = ["//bazelgreeting:greeter"]
)
```

deps属性使当前包依赖于数组中列出的包。

## 5. 外部依赖

我们可以处理具有多个工作区且相互依赖的项目。或者，我们可以从远程位置导入库。我们可以将此类外部依赖项分类为：

-   本地依赖项：我们在上一节中看到的同一工作区内管理它们，或者跨越多个工作区
-   HTTP 档案：我们通过 HTTP 从远程位置导入库

有许多 Bazel 规则可用于管理[外部依赖](https://docs.bazel.build/versions/master/external.html)项。我们将在后续部分中了解如何从远程位置导入 jar 文件。

### 5.1. HTTP 网址位置

对于我们的示例，让我们将[Apache Commons Lang](https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.9/commons-lang3-3.9.jar)导入到我们的应用程序中。由于我们必须从 HTTP 位置导入此 jar，因此我们将使用[http_jar](https://docs.bazel.build/versions/master/repo/http.html#http_jar)规则。我们将首先从 Bazel HTTP 构建定义加载规则，并使用 Apache Commons 的位置在WORKSPACE文件中配置它：

```plaintext
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")

http_jar (
    name = "apache-commons-lang",
    url = "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar"
)
```

我们还要在“ bazelapp”包的BUILD文件中进一步添加依赖：

```plaintext
deps = ["//bazelgreeting:greeter", "@apache-commons-lang//jar"]
```

请注意，我们需要从WORKSPACE文件中指定与http_jar规则中使用的名称相同的名称。

### 5.2. Maven 依赖项

管理单个 jar 文件成为一项繁琐的任务。或者，我们可以使用WORKSPACE文件中的[rules_jvm_external](https://github.com/bazelbuild/rules_jvm_external)规则配置 Maven 存储库。这将使我们能够从存储库中获取项目中所需的任意数量的依赖项。

首先，我们必须使用WORKSPACE文件中的http_archive规则从远程位置导入rules_jvm_external规则：

```plaintext
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

RULES_JVM_EXTERNAL_TAG = "2.0.1"
RULES_JVM_EXTERNAL_SHA = "55e8d3951647ae3dffde22b4f7f8dee11b3f70f3f89424713debd7076197eaca"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)
```

接下来，我们将使用maven_install规则并配置 Maven 存储库 URL 和所需的工件：

```plaintext
load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "org.apache.commons:commons-lang3:3.12.0" ], 
    repositories = [ 
        "https://repo1.maven.org/maven2", 
    ] )
```

最后，我们将在BUILD文件中添加依赖项：

```plaintext
deps = ["//bazelgreeting:greeter", "@maven//:org_apache_commons_commons_lang3"]
```

它使用下划线 (_) 字符解析工件的名称。

## 六. 总结

在本教程中，我们学习了使用 Bazel 构建工具构建 Maven 风格Java项目的基本配置。

源代码也可以通过[GitHub](https://github.com/eugenp/tutorials/tree/master/bazel)获得。它作为 Maven 项目存在，并且还配置了 Bazel WORKSPACE和BUILD文件。