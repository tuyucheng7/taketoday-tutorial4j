## 1. 概述

在本教程中，我们将学习如何使用Maven命令检测pom.xml中的重复依赖项，我们还将了解如何使用Maven Enforcer插件在存在重复依赖项时使构建失败。

## 2. 为什么检测重复的依赖

在pom.xml中存在重复依赖项的风险是目标库的最新版本可能不适用于我们项目的构建路径。例如，让我们考虑以下pom.xml：

```xml
<project>
    [...]
    <dependencies>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.11</version>
        </dependency>
    </dependencies>
    [...]
</project>
```

正如我们所看到的，同一个库commons-lang3有两个依赖项，尽管这两个依赖项的版本不同。

接下来，让我们看看如何使用Maven命令来检测这些重复的依赖项。

## 3. dependency:tree命令

让我们从终端运行命令[mvn dependency:tree](https://maven.apache.org/plugins/maven-dependency-plugin/tree-mojo.html)并查看输出。

```powershell
$ mvn dependency:tree
[INFO] Scanning for projects...
[WARNING]
[WARNING] Some problems were encountered while building the effective model for cn.tuyucheng.taketoday:maven-duplicate-dependencies:jar:1.0.0
[WARNING] 'dependencies.dependency.(groupId:artifactId:type:classifier)' must be unique: org.apache.commons:commons-lang3:jar -
> version 3.12.0 vs 3.11 @ line 14, column 15
[WARNING]
[WARNING] It is highly recommended to fix these problems because they threaten the stability of your build.
[WARNING]
[WARNING] For this reason, future Maven versions might no longer support building such malformed projects.
[WARNING]
[INFO]
[INFO] -------------< cn.tuyucheng.taketoday:maven-duplicate-dependencies >--------------
[INFO] Building maven-duplicate-dependencies 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-dependency-plugin:2.8:tree (default-cli) @ maven-duplicate-dependencies ---
[WARNING] The artifact xml-apis:xml-apis:jar:2.0.2 has been relocated to xml-apis:xml-apis:jar:1.0.b2
[INFO] cn.tuyucheng.taketoday:maven-duplicate-dependencies:jar:1.0.0
[INFO] \- org.apache.commons:commons-lang3:jar:3.11:compile
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.136 s
[INFO] Finished at: 2023-01-03T09:45:20+05:30
[INFO] ------------------------------------------------------------------------
```

在这里，我们收到有关pom.xml中存在重复依赖项的警告，我们还注意到commons-lang3.jar的3.11版本已添加到项目中，尽管存在更高的版本3.12.0。**发生这种情况是因为Maven选择了后来出现在pom.xml中的依赖项**。

## 4. dependency:analyze-duplicate命令

现在让我们运行命令[mvn dependency:analyze-duplicate](https://maven.apache.org/plugins/maven-dependency-plugin/analyze-duplicate-mojo.html)并检查输出。

```powershell
$ mvn dependency:analyze-duplicate
[INFO] Scanning for projects...
[WARNING]
[WARNING] Some problems were encountered while building the effective model for cn.tuyucheng.taketoday:maven-duplicate-dependencies:jar:1.0.0
[WARNING] 'dependencies.dependency.(groupId:artifactId:type:classifier)' must be unique: org.apache.commons:commons-lang3:jar -
> version 3.12.0 vs 3.11 @ line 14, column 15
[WARNING]
[WARNING] It is highly recommended to fix these problems because they threaten the stability of your build.
[WARNING]
[WARNING] For this reason, future Maven versions might no longer support building such malformed projects.
[WARNING]
[INFO]
[INFO] -------------< cn.tuyucheng.taketoday:maven-duplicate-dependencies >--------------
[INFO] Building maven-duplicate-dependencies 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-dependency-plugin:2.8:analyze-duplicate (default-cli) @ maven-duplicate-dependencies ---
[WARNING] The artifact xml-apis:xml-apis:jar:2.0.2 has been relocated to xml-apis:xml-apis:jar:1.0.b2
[INFO] List of duplicate dependencies defined in <dependencies/> in your pom.xml:
        o org.apache.commons:commons-lang3:jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.835 s
[INFO] Finished at: 2023-01-03T09:54:02+05:30
[INFO] ------------------------------------------------------------------------
```

在这里，我们注意到WARNING和INFO日志语句都提到了重复依赖项的存在。

## 5. 如果存在重复的依赖项，则构建失败

在上面的示例中，我们看到了如何检测重复的依赖项，但构建仍然成功，这可能会导致使用的jar版本不正确。

**使用**[Maven Enforcer插件](https://maven.apache.org/enforcer/maven-enforcer-plugin/index.html)**，我们可以确保在存在重复依赖项时构建不成功**。

为此，我们需要将这个Maven插件添加到我们的pom.xml并添加规则banDuplicatePomDependencyVersions：

```xml
<project>
    [...]
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-enforcer-plugin</artifactId>
                <version>3.0.0</version>
                <executions>
                    <execution>
                        <id>no-duplicate-declared-dependencies</id>
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
            </plugin>
        </plugins>
    </build>
    [...]
</project>
```

现在，该规则绑定了我们的Maven构建：

```powershell
$ mvn verify
[INFO] Scanning for projects...
[WARNING]
[WARNING] Some problems were encountered while building the effective model for cn.tuyucheng.taketoday:maven-duplicate-dependencies:jar:1.0.0
[WARNING] 'dependencies.dependency.(groupId:artifactId:type:classifier)' must be unique: org.apache.commons:commons-lang3:jar -
> version 3.12.0 vs 3.11 @ line 14, column 14
[WARNING]
[INFO] -------------< cn.tuyucheng.taketoday:maven-duplicate-dependencies >--------------
[INFO] Building maven-duplicate-dependencies 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-enforcer-plugin:3.0.0:enforce (no-duplicate-declared-dependencies) @ maven-duplicate-dependencies ---
[WARNING] Rule 0: org.apache.maven.plugins.enforcer.BanDuplicatePomDependencyVersions failed with message:
Found 1 duplicate dependency declaration in this project:
 - dependencies.dependency[org.apache.commons:commons-lang3:jar] ( 2 times )

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.537 s
[INFO] Finished at: 2023-01-03T09:55:46+05:30
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-enforcer-plugin:3.0.0:enforce (no-duplicate-declared-dependencies) on project maven-duplicate-dependencie
s: Some Enforcer rules have failed. Look above for specific messages explaining why the rule failed.
```

## 6. 删除重复的依赖

一旦我们确定了我们的重复依赖项，删除它们的最简单方法是从pom.xml中删除它们并仅保留我们项目使用的那些唯一依赖项。

## 7. 总结

在本文中，我们学习了如何使用mvn dependency:tree和mvn dependency:analyze-duplicate命令检测Maven中的重复依赖项，我们还看到了如何使用Maven Enforcer插件通过应用内置规则使包含重复依赖项的构建失败。