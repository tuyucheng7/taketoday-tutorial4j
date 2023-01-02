## 1. 概述

在本教程中，我们将了解[Maven Enforcer插件](https://maven.apache.org/enforcer/maven-enforcer-plugin/)以及我们如何使用它来保证项目的合规性级别。

当我们拥有分散在全球各地的分布式团队时，该插件特别方便。

## 2. 依赖

为了在我们的项目中使用该插件，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.0.0-M2</version>
</plugin>
```

[Maven Central](https://search.maven.org/search?q=a:maven-enforcer-plugin)上提供了最新版本的插件。

## 3. 插件配置和目标

Maven Enforcer有两个目标：enforcer:enforce和enforcer:display-info。

enforce目标在项目构建期间运行以执行配置中指定的规则，而display-info目标显示有关项目pom.xml中存在的内置规则的当前信息。

让我们在<executions\>标签中定义enforce目标，此外，我们将添加包含项目规则定义的<configuration\>标签：

```xml
...
<executions>
    <execution>
        <id>enforce</id>
        <goals>
            <goal>enforce</goal>
        </goals>
        <configuration>
            <rules>
                <banDuplicatePomDependencyVersions/>
            </rules>
        </configuration>
    </execution>
</executions>
...
```

## 4. Maven Enforcer规则

关键字enforce给出了一个微妙的暗示，即存在要遵守的规则。这就是Maven Enforcer插件的工作原理，我们使用一些在项目构建阶段强制执行的规则来配置它。

在本节中，我们将查看可以应用于我们的项目以提高其质量的可用规则。

### 4.1 禁止重复依赖

在多模块项目中，POM之间存在父子关系，确保项目的有效最终POM中没有重复的依赖项可能是一项棘手的任务。但是，使用banDuplicatePomDependencyVersions规则，我们可以轻松地确保我们的项目没有此类故障。

我们需要做的就是将<banDuplicatePomDependencyVersions\>标签添加到插件配置的<rules\>部分：

```xml
...
<rules>
    <banDuplicatePomDependencyVersions/>
</rules>
...
```

为了检查规则的行为，我们可以在pom.xml中重复指定一个依赖项并运行`mvn clean compile`，它会在控制台上产生以下错误行：

```bash
...
[WARNING] Rule 0: org.apache.maven.plugins.enforcer.BanDuplicatePomDependencyVersions failed with message:
Found 1 duplicate dependency declaration in this project:
 - dependencies.dependency[io.vavr:vavr:jar] ( 2 times )

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.370 s
[INFO] Finished at: 2019-02-19T10:17:57+01:00
...
```

### 4.2 需要Maven和Java版本

requireMavenVersion和requireJavaVersion规则分别在项目范围内锁定所需的Maven和Java版本，这将有助于消除在开发环境中使用不同版本的Maven和JDK可能产生的差异。

让我们更新插件配置的<rules\>部分：

```xml
<requireMavenVersion>
    <version>3.0</version>
</requireMavenVersion>
<requireJavaVersion>
    <version>1.8</version>
</requireJavaVersion>
```

这些允许我们以灵活的方式指定版本号，只要它们符合插件的[版本范围规范模式](https://maven.apache.org/enforcer/enforcer-rules/versionRanges.html)。

此外，这两个规则还接受用于指定自定义消息的message参数：

```xml
...
<requireMavenVersion>
    <version>3.0</version>
    <message>Invalid Maven version. It should, at least, be 3.0</message>
</requireMavenVersion>
...
```

### 4.3 需要环境变量

通过requireEnvironmentVariable规则，我们可以确保在执行环境中设置了某个环境变量。

该规则可以重复指定以容纳多个必需的变量：

```xml
<requireEnvironmentVariable>
    <variableName>ui</variableName>
</requireEnvironmentVariable>
<requireEnvironmentVariable>
    <variableName>cook</variableName>
</requireEnvironmentVariable>
```

### 4.4 需要激活的Profile

Maven中的Profile帮助我们配置在我们的应用程序部署到不同环境时将处于活动状态的属性。

因此，当我们需要确保一个或多个指定的Profile处于激活状态时，我们可以使用requireActiveProfile规则，从而保证我们的应用程序的成功执行：

```xml
<requireActiveProfile>
    <profiles>local,base</profiles>
    <message>Missing active profiles</message>
</requireActiveProfile>
```

在上面的代码片段中，我们使用message属性提供自定义消息以显示规则检查是否失败。

### 4.5 其他规则

Maven Enforcer插件有许多[其他规则](https://maven.apache.org/enforcer/enforcer-rules/index.html)来提高项目质量和一致性，而不管开发环境如何。

此外，该插件还有一个命令来显示有关某些当前配置地规则的信息：

```bash
mvn enforcer:display-info
```

## 5. 自定义规则

到目前为止，我们一直在探索插件的内置规则，现在，是时候看看创建我们自己的自定义规则了。

首先，我们需要创建一个包含自定义规则的新Java项目。**自定义规则是一个类对象，它实现了EnforceRule接口并重写了execute()方法**：

```java
public void execute(EnforcerRuleHelper enforcerRuleHelper) throws EnforcerRuleException {
    try {
        String groupId = (String) enforcerRuleHelper.evaluate("${project.groupId}");
        if (groupId == null || !groupId.startsWith("cn.tuyucheng.taketoday")) {
            throw new EnforcerRuleException("Project group id does not start with cn.tuyucheng.taketoday");
        }
    }
    catch (ExpressionEvaluationException ex) {
        throw new EnforcerRuleException( "Unable to lookup an expression " + ex.getLocalizedMessage(), ex );
    }
}
```

我们的自定义规则只是检查目标项目的groupId是否以cn.tuyucheng.taketoday开头。

请注意我们如何不必返回布尔值或任何类似的东西来指示不满足规则，我们只是抛出一个带有错误描述的EnforcerRuleException。

**我们可以通过将其添加为Maven Enforcer插件的依赖项来使用我们的自定义规则**：

```xml
...
<rules>
    <myCustomRule implementation="cn.tuyucheng.taketoday.enforcer.MyCustomRule"/>
</rules>
...
```

请注意，如果自定义规则项目不是Maven Central上已发布的工件，我们可以通过运行mvn clean install将其安装到本地Maven仓库中。

这将使它在编译具有Maven Enforcer插件的目标项目时可用，请参阅[自定义规则的插件文档](https://maven.apache.org/enforcer/enforcer-api/writing-a-custom-rule.html)以了解更多信息。

**要查看它的实际效果，我们可以将带有Enforcer插件的项目的groupId属性设置为“cn.tuyucheng.taketoday”以外的任何值，然后运行**`mvn clean compile`。

## 6. 总结

在这个快速教程中，我们了解了Maven Enforcer插件如何成为我们现有插件箱的有用补充，编写自定义规则的能力扩大了它的应用范围。

请注意，我们需要在[GitHub](https://github.com/eugenp/tutorials/tree/master/maven-modules/maven-plugins)上提供的完整示例源代码中取消注释自定义规则示例的依赖项和规则。