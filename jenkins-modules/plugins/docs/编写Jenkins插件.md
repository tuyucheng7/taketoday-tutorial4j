## 1. 概述

Jenkins 是一个开源持续集成服务器，可以为特定任务/环境创建自定义插件。

在本文中，我们将完成创建扩展的整个过程，该扩展将统计信息添加到构建输出中，即类数和代码行数。

## 2.设置

首先要做的是设置项目。幸运的是，Jenkins 为此提供了方便的 Maven 原型。

只需从 shell 运行以下命令：

```bash
mvn archetype:generate -Dfilter=io.jenkins.archetypes:plugin
```

我们将得到以下输出：

```bash
[INFO] Generating project in Interactive mode
[INFO] No archetype defined. Using maven-archetype-quickstart
  (org.apache.maven.archetypes:maven-archetype-quickstart:1.0)
Choose archetype:
1: remote -> io.jenkins.archetypes:empty-plugin (Skeleton of
  a Jenkins plugin with a POM and an empty source tree.)
2: remote -> io.jenkins.archetypes:global-configuration-plugin
  (Skeleton of a Jenkins plugin with a POM and an example piece
  of global configuration.)
3: remote -> io.jenkins.archetypes:hello-world-plugin
  (Skeleton of a Jenkins plugin with a POM and an example build step.)
```

现在，选择第一个选项并在交互模式下定义 group/artifact/package。之后，有必要对pom.xml进行改进——因为它包含诸如<name>TODO Plugin</name>之类的条目。

## 3. Jenkins 插件设计

### 3.1. 扩展点

Jenkins 提供了许多扩展点。这些是为特定用例定义契约并允许其他插件实现它们的接口或抽象类。

例如，每个构建都包含许多步骤，例如“从 VCS 检出”、“编译”、“测试”、 “组装”等。Jenkins 定义了[hudson.tasks.BuildStep](https://javadoc.jenkins.io/hudson/tasks/BuildStep.html) 扩展点，因此我们可以实现它以提供可以配置的自定义步骤。

另一个例子是[hudson.tasks.BuildWrapper——](https://javadoc.jenkins.io/hudson/tasks/BuildWrapper.html) 这允许我们定义前/后操作。

我们还有一个非核心[电子邮件扩展](https://plugins.jenkins.io/email-ext)插件，它定义了hudson.plugins.emailext.plugins.RecipientProvider扩展点，它允许提供电子邮件收件人。此处提供了一个示例实现：[hudson.plugins.emailext.plugins.recipients.UpstreamComitterRecipientProvider](https://github.com/jenkinsci/email-ext-plugin/blob/64641f05edafd8157ed50fb64238aa22d9f342af/src/main/java/hudson/plugins/emailext/plugins/recipients/UpstreamComitterRecipientProvider.java)。

注意：有一种遗留方法，其中插件类需要扩展[hudson.Plugin](http://javadoc.jenkins-ci.org/hudson/Plugin.html)。但是，现在建议改用扩展点。

### 3.2. 插件初始化

有必要告诉 Jenkins 我们的扩展以及它应该如何被实例化。

首先，我们在插件中定义一个静态内部类，并使用hudson.Extension注解对其进行标记：

```java
class MyPlugin extends BuildWrapper {
    @Extension
    public static class DescriptorImpl 
      extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "name to show in UI";
        }
    }
}
```

其次，我们需要定义一个构造函数用于插件的对象实例化，并用org.kohsuke.stapler.DataBoundConstructor注解来标记它。

可以为其使用参数。它们显示在 UI 中，并由 Jenkins 自动交付。

例如考虑[Maven 插件](https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/Maven.java#L177)：

```java
@DataBoundConstructor
public Maven(
  String targets,
  String name,
  String pom,
  String properties,
  String jvmOptions,
  boolean usePrivateRepository,
  SettingsProvider settings,
  GlobalSettingsProvider globalSettings,
  boolean injectBuildVariables) { ... }
```

它映射到以下 UI：

[![Maven 设置](https://www.baeldung.com/wp-content/uploads/2018/01/maven-settings.png)](https://www.baeldung.com/wp-content/uploads/2018/01/maven-settings.png)

也可以将org.kohsuke.stapler.DataBoundSetter注解与设置器一起使用。

## 4. 插件实现

我们打算在构建期间收集基本的项目统计信息，因此，hudson.tasks.BuildWrapper是正确的方法。

让我们来实现它：

```java
class ProjectStatsBuildWrapper extends BuildWrapper {

    @DataBoundConstructor
    public ProjectStatsBuildWrapper() {}

    @Override
    public Environment setUp(
      AbstractBuild build,
      Launcher launcher,
      BuildListener listener) {}

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {

        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Construct project stats during build";
        }

    }
}
```

好的，现在我们需要实现实际的功能。

让我们为项目统计定义一个域类：

```java
class ProjectStats {

    private int classesNumber;
    private int linesNumber;

    // standard constructors/getters
}
```

并编写构建数据的代码：

```java
private ProjectStats buildStats(FilePath root)
  throws IOException, InterruptedException {
 
    int classesNumber = 0;
    int linesNumber = 0;
    Stack<FilePath> toProcess = new Stack<>();
    toProcess.push(root);
    while (!toProcess.isEmpty()) {
        FilePath path = toProcess.pop();
        if (path.isDirectory()) {
            toProcess.addAll(path.list());
        } else if (path.getName().endsWith(".java")) {
            classesNumber++;
            linesNumber += countLines(path);
        }
    }
    return new ProjectStats(classesNumber, linesNumber);
}
```

最后，我们需要向最终用户显示统计信息。让我们为此创建一个 HTML 模板：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>$PROJECT_NAME$</title>
</head>
<body>
Project $PROJECT_NAME$:
<table border="1">
    <tr>
        <th>Classes number</th>
        <th>Lines number</th>
    </tr>
    <tr>
        <td>$CLASSES_NUMBER$</td>
        <td>$LINES_NUMBER$</td>
    </tr>
</table>
</body>
</html>
```

并在构建期间填充它：

```java
public class ProjectStatsBuildWrapper extends BuildWrapper {
    @Override
    public Environment setUp(
      AbstractBuild build,
      Launcher launcher,
      BuildListener listener) {
        return new Environment() {
 
            @Override
            public boolean tearDown(
              AbstractBuild build, BuildListener listener)
              throws IOException, InterruptedException {
 
                ProjectStats stats = buildStats(build.getWorkspace());
                String report = generateReport(
                  build.getProject().getDisplayName(),
                  stats);
                File artifactsDir = build.getArtifactsDir();
                String path = artifactsDir.getCanonicalPath() + REPORT_TEMPLATE_PATH;
                File reportFile = new File("path");
                // write report's text to the report's file
            }
        };
    }
}
```

## 5.用法

是时候将我们迄今为止创建的所有内容结合起来，并在实际中查看它了。

假设 Jenkins 在本地环境中启动并运行。否则请参考[安装细节。](https://www.baeldung.com/jenkins-pipelines)

### 5.1. 将插件添加到 Jenkins

现在，让我们构建我们的插件：

```bash
mvn install
```

这将在目标目录中创建一个.hpi文件。我们需要将它到 Jenkins 插件目录(默认为~/.jenkins/plugin)：

```bash
cp ./target/jenkins-hello-world.hpi ~/.jenkins/plugins/
```

最后，让我们重新启动服务器并确保应用插件：

1.  在http://localhost:8080打开 CI 仪表板
2.  导航到管理 Jenkins | 管理插件| 已安装
3.  找到我们的插件

[![启用插件](https://www.baeldung.com/wp-content/uploads/2018/01/plugin-enabled.png)](https://www.baeldung.com/wp-content/uploads/2018/01/plugin-enabled.png)

### 5.2. 配置 Jenkins 作业

让我们为开源 Apache commons-lang 项目创建一个新作业，并在那里配置其 Git 存储库的路径：

[![通用语言 git](https://www.baeldung.com/wp-content/uploads/2018/01/common-lang-git.png)](https://www.baeldung.com/wp-content/uploads/2018/01/common-lang-git.png)

我们还需要为此启用我们的插件：

[![为项目启用](https://www.baeldung.com/wp-content/uploads/2018/01/enable-for-project.png)](https://www.baeldung.com/wp-content/uploads/2018/01/enable-for-project.png)

### 5.3. 检查结果

我们现在都准备好了，让我们检查一下它是如何工作的。

我们可以构建项目并导航到结果。我们可以看到这里有一个stats.html文件：

[![公共语言构建 1](https://www.baeldung.com/wp-content/uploads/2018/01/commons-lang-build-1.png)](https://www.baeldung.com/wp-content/uploads/2018/01/commons-lang-build-1.png)

让我们打开它：

[![公共语言结果](https://www.baeldung.com/wp-content/uploads/2018/01/commons-lang-result.png)](https://www.baeldung.com/wp-content/uploads/2018/01/commons-lang-result.png)

这正是我们所期望的——一个包含三行代码的类。

## 六. 总结

在本教程中，我们从头开始创建了一个Jenkins插件并确保它可以正常工作。

当然，我们并没有涵盖 CI 扩展开发的所有方面，我们只是提供了一个基本的概述、设计思路和初始设置。