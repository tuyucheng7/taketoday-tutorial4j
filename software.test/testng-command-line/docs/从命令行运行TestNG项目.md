## 一、概述

在这个简短的教程中，我们将看到如何从命令行启动[TestNG测试。](https://www.baeldung.com/testng)这对于构建或我们想在开发期间直接运行单个测试很有用。
我们可以使用像[Maven](https://www.baeldung.com/maven)这样的构建工具来执行我们的测试，或者我们可能希望直接通过java命令运行它们。
让我们看看这两种方法。

## 2. 示例项目概述

对于我们的示例，让我们使用一些代码，其中包含一项将日期格式化为字符串的服务：

```java
public class DateSerializerService {
    public String serializeDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}

```

对于测试，让我们进行一项测试，以检查在将空日期传递给服务时是否引发了NullPointerExeception ：

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

我们还将创建一个pom.xml，它定义了从命令行执行 TestNG 所需的依赖项。我们需要的第一个依赖项是[TestNG](https://mvnrepository.com/artifact/org.testng/testng)：

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.4.0</version>
    <scope>test</scope>
</dependency>
```

接下来，我们需要[JCommander](https://mvnrepository.com/artifact/com.beust/jcommander)。TestNG 使用它来解析命令行：

```xml
<dependency>
    <groupId>com.beust</groupId>
    <artifactId>jcommander</artifactId>
    <version>1.81</version>
    <scope>test</scope>
</dependency>
```

最后，如果我们想让TestNG编写HTML测试报告，我们需要添加[WebJar for JQuery](https://mvnrepository.com/artifact/org.webjars/jquery)依赖：

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.5.1</version>
    <scope>test</scope>
</dependency>
```

## 3. 设置运行 TestNG 命令

### 3.1。使用 Maven 下载依赖

由于我们有一个 Maven 项目，让我们构建它：

```powershell
c:> mvn test
```

此命令应输出：

```plaintext
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------< com.baeldung.testing_modules:testng_command_line >----------
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

现在，我们拥有了从命令行运行 TestNG 测试所需的一切。
所有依赖项都将下载到 Maven 本地存储库中，该存储库通常位于用户的.m2文件夹中。

### 3.2. 获取我们的类路径

要通过java命令执行命令，我们需要添加一个-classpath选项：

```powershell
$ java -cp "~/.m2/repository/org/testng/testng/7.4.0/testng-7.4.0.jar;~/.m2/repository/com/beust/jcommander/1.81/jcommander-1.81.jar;~/.m2/repository/org/webjars/jquery/3.5.1/jquery-3.5.1.jar;target/classes;target/test-classes" org.testng.TestNG ...
```

我们将在稍后的命令行示例中将其缩写为 -cp <CLASSPATH>。

## 4.检查TestNG命令行

让我们检查一下我们是否可以通过java访问 TestNG ：

```powershell
$ java -cp <CLASSPATH> org.testng.TestNG
```

如果一切正常，控制台将显示一条消息：

```bash
You need to specify at least one testng.xml, one class or one method
Usage: <main class> [options] The XML suite files to run
Options:
...
```

## 5. 启动TestNG Single Test

### 5.1。使用java命令运行单个测试

现在，我们可以快速运行单个测试，而无需配置单个测试套件文件，只需使用以下命令行：

```powershell
$ java -cp <CLASSPATH> org.testng.TestNG -testclass "testng.cn.tuyucheng.taketoday.DateSerializerServiceUnitTest"
```

### 5.2. 使用 Maven 运行单个测试

如果我们希望 Maven 只执行这个测试，我们可以在pom.xml文件中配置maven-surefire-plugin ：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>/DateSerializerServiceUnitTest.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

在示例中，我们有一个名为ExecuteSingleTest的配置文件被配置为执行DateSerializerServiceUnitTest.java。我们可以运行这个配置文件：

```bash
$ mvn -P ExecuteSingleTest test
```

正如我们所见，Maven 需要比简单的 TestNG 命令行执行更多的配置来执行单个测试。

## 6. 启动 TestNG 测试套件

### 6.1。使用java命令运行测试套件

测试套件文件定义了测试应该如何运行。我们可以拥有尽可能多的东西。而且，我们可以通过指向定义测试套件的XML文件来运行测试套件：

```powershell
$ java -cp <CLASSPATH> org.testng.TestNG testng.xml
```

### 6.2. 使用 Maven 运行测试套件

如果我们想使用 Maven 执行测试套件，我们应该配置插件 maven-surefire-plugin：

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

在这里，我们有一个名为 ExecuteTestSuite 的Maven 配置文件，它将配置maven-surefire 插件 以启动testng.xml 测试套件。我们可以使用以下命令运行此配置文件：

```bash
$ mvn -P ExecuteTestSuite test
```

## 7. 结论

在本文中，我们看到了 TestNG 命令行如何有助于运行单个测试文件，而Maven应该用于配置和启动全套测试。