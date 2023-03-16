## 1. 概述

在这个简短的教程中，我们将了解如何从命令行启动[TestNG测试](https://www.baeldung.com/testng)。这对于构建或我们想在开发期间直接运行单个测试很有用。

我们可以使用像[Maven](https://www.baeldung.com/maven)这样的构建工具来执行我们的测试，或者我们可能希望直接通过java命令运行它们。让我们看看这两种方法。

## 2. 示例项目概述

对于我们的示例，让我们使用一些代码，其中包含一个将日期格式化为字符串的方法：

```java
public class DateSerializerService {
    public String serializeDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
```

对于测试，让我们进行一个测试来检查在将空日期传递给服务时是否抛出NullPointerException：

```java
@Test(testName = "Date Serializer")
public class DateSerializerServiceUnitTest {
    private DateSerializerService toTest;

    @BeforeClass
    public void beforeClass() {
        toTest = new DateSerializerService();
    }

    @Test(expectedExceptions = { NullPointerException.class })
    void givenNullDate_whenSerializeDate_thenThrowsException() {
        Date dateToTest = null;

        toTest.serializeDate(dateToTest, "yyyy/MM/dd HH:mm:ss.SSS");
    }
}
```

**我们还将添加一个pom.xml，它定义了从命令行执行TestNG所需的依赖项**。我们需要的第一个依赖项是[TestNG](https://central.sonatype.com/artifact/org.testng/testng/7.7.1)：

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.4.0</version>
    <scope>test</scope>
</dependency>
```

接下来，我们需要[JCommander](https://central.sonatype.com/artifact/com.beust/jcommander/1.82)。TestNG使用它来解析命令行：

```xml
<dependency>
    <groupId>com.beust</groupId>
    <artifactId>jcommander</artifactId>
    <version>1.81</version>
    <scope>test</scope>
</dependency>
```

最后，如果我们想让TestNG样式化HTML测试报告，我们需要添加[JQuery](https://central.sonatype.com/artifact/org.webjars/jquery/3.6.4)依赖：

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.5.1</version>
    <scope>test</scope>
</dependency>
```

## 3. 设置运行TestNG命令

### 3.1 使用Maven下载依赖

由于我们是一个Maven项目，因此让我们构建它：

```shell
c:\> mvn test
```

此命令应输出：

```shell
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------< cn.tuyucheng.taketoday.testing_modules:testng_command_line >----------
[INFO] Building testng_command_line 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.639 s
[INFO] Finished at: 2021-12-19T15:16:52+01:00
[INFO] ------------------------------------------------------------------------
```

现在，我们有了从命令行运行TestNG测试所需的一切。

所有依赖项都将下载到Maven本地仓库中，该仓库通常位于用户的.m2文件夹中，或者自定义的仓库文件夹。

### 3.2 获取我们的类路径

**要通过java命令执行命令，我们需要添加一个-classpath选项**：

```shell
$ java -cp "~/.m2/repository/org/testng/testng/7.4.0/testng-7.4.0.jar;~/.m2/repository/com/beust/jcommander/1.81/jcommander-1.81.jar;~/.m2/repository/org/webjars/jquery/3.5.1/jquery-3.5.1.jar;target/classes;target/test-classes" org.testng.TestNG ...
```

稍后我们将在命令行示例中将其缩写为-cp <CLASSPATH\>。

## 4. 检查TestNG命令行

让我们检查一下我们是否可以通过java访问TestNG：

```shell
$ java -cp <CLASSPATH> org.testng.TestNG
```

如果一切正常，控制台将显示一条消息：

```shell
You need to specify at least one testng.xml, one class or one method
Usage: <main class> [options] The XML suite files to run
Options:
...
```

## 5. 启动TestNG单测试

### 5.1 使用java命令运行单个测试

**现在，我们可以快速运行单个测试**，而无需配置单个测试套件文件，只需使用以下命令行：

```shell
$ java -cp <CLASSPATH> org.testng.TestNG -testclass "cn.tuyucheng.taketoday.testng.DateSerializerServiceUnitTest"
```

### 5.2 使用Maven运行单个测试

如果我们希望Maven只执行单个测试，我们可以在pom.xml文件中配置maven-surefire-plugin：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/DateSerializerServiceUnitTest.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

在示例中，我们有一个名为ExecuteSingleTest的Profile被配置为执行DateSerializerServiceUnitTest.java。我们可以运行这个Profile：

```bash
$ mvn -P ExecuteSingleTest test
```

正如我们所看到的，**Maven需要比简单的TestNG命令行执行更多的配置来执行单个测试**。

## 6. 启动TestNG测试套件

### 6.1 使用java命令运行测试套件

测试套件文件定义了测试应该如何运行。我们可以根据需要定义任意数量的套件。而且，**我们可以通过指向定义测试套件的XML文件来运行测试套件**：

```shell
$ java -cp <CLASSPATH> org.testng.TestNG testng.xml
```

### 6.2 使用Maven运行测试套件

如果我们想使用Maven执行测试套件，我们应该**配置插件maven-surefire-plugin**：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <suiteXmlFiles>
                        <suiteXmlFile>testng.xml</suiteXmlFile>
                    </suiteXmlFiles>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

在这里，我们有一个名为ExecuteTestSuite的Maven Profile，它将配置maven-surefire插件以启动testng.xml测试套件。我们可以使用以下命令运行此Profile：

```shell
$ mvn -P ExecuteTestSuite test
```

## 7. 总结

在本文中，我们看到了**TestNG命令行如何有助于运行单个测试文件，而Maven应该用于配置和启动测试套件**。