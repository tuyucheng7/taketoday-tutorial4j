## 1. 概述

在本文中，我们将使用[Apache JMeter](https://jmeter.apache.org/)来配置和运行性能测试。

## 2.设置JMeter

让我们[下载 JMeter](https://jmeter.apache.org/download_jmeter.cgi)，解压缩，转到bin文件夹并运行可执行文件( .bat适用于 Windows， .sh适用于 Linux/Unix)。

接下来，我们只需要将此文件夹添加到路径环境变量中，以便可以从命令行访问它。

在撰写本文时最稳定的版本是 3.3——我们可以在[此处](https://archive.apache.org/dist/jmeter/binaries/)查看最新版本。

## 3. 创建 JMeter 脚本

现在让我们编写第一个 JMeter 脚本(一个包含测试技术描述的文件)。

此 API 是一个公开 REST API的简单[SpringBoot应用程序。](https://www.baeldung.com/spring-boot-start)

让我们先更新测试计划并更改其名称，然后添加一个线程组。

线程组让我们了解用户流程并模拟他们如何与应用程序交互，在 GUI 上右键单击我们的脚本名称并按照所选菜单操作：

[![线程组菜单模糊](https://www.baeldung.com/wp-content/uploads/2017/12/thread-group-menu-blur-1-1.png)](https://www.baeldung.com/wp-content/uploads/2017/12/thread-group-menu-blur-1-1.png)

 

现在我们转到Thread Group的配置部分，我们在其中指定并行向我们的应用程序发出请求的用户数量：

[![创建线程组模糊](https://www.baeldung.com/wp-content/uploads/2017/12/create-thread-group-blur.png)](https://www.baeldung.com/wp-content/uploads/2017/12/create-thread-group-blur.png)

在这里，我们指定了如下参数：

– 名称：我们要给线程组起的名字

– The number of Threads (users) : 并发用户数

–启动时间：从 0 到声明的用户数所需的时间

–循环计数：重复次数

添加一个HTTP 请求，因为我们将模拟它来自 5 个用户中的每一个。

让我们填写信息以解决上面描述的 API，如下图所示：

[![http请求模糊](https://www.baeldung.com/wp-content/uploads/2017/12/http-request-blur.png)](https://www.baeldung.com/wp-content/uploads/2017/12/http-request-blur.png)

我们只要填写网站地址，端口，具体路径即可。

接下来，让我们通过菜单“添加 > 监听器”添加查看结果树(如果结果是记录列表，则使用在表中查看结果)后模拟用户的请求。

点击顶部的绿色箭头右按钮运行测试并查看响应数据：

[![http请求查看结果模糊](https://www.baeldung.com/wp-content/uploads/2017/12/http-request-view-result-blur.png)](https://www.baeldung.com/wp-content/uploads/2017/12/http-request-view-result-blur.png)

我们可以在采样器结果选项卡上看到更详细的响应表示。

让我们在HTTP Request中添加一个Duration Assertion来结束，因此每个持续时间超过十毫秒的请求都将被视为失败的测试：

[![持续时间断言模糊](https://www.baeldung.com/wp-content/uploads/2017/12/duration-assertion-blur.png)](https://www.baeldung.com/wp-content/uploads/2017/12/duration-assertion-blur.png)

重新运行测试后，我们看到有一些(这里是 3 个)用户无法在不到 10 毫秒的时间内获取学生列表：

[![持续时间断言失败模糊](https://www.baeldung.com/wp-content/uploads/2017/12/duration-assertion-failed-blur.png)](https://www.baeldung.com/wp-content/uploads/2017/12/duration-assertion-failed-blur.png)

现在，让我们将扩展名为.jmx的测试保存在 API 的资源文件夹中。

更多元素可用于配置我们的测试文件：

-   [JDBC 请求](https://jmeter.apache.org/usermanual/component_reference.html#JDBC_Request)：用于向数据库发送 JDBC 请求(SQL 查询)，在使用它之前我们需要设置一个[JDBC 连接配置](https://jmeter.apache.org/usermanual/component_reference.html#JDBC_Connection_Configuration)元素
-   [XML 断言](https://jmeter.apache.org/usermanual/component_reference.html#XML_Assertion)：测试响应数据是否属于正确的 XML 文档
-   [大小断言](https://jmeter.apache.org/usermanual/component_reference.html#Size_Assertion)：断言响应中包含正确的字节数
-   [JMS 发布](https://jmeter.apache.org/usermanual/component_reference.html#JMS_Publisher)者：按照 J2EE 消息传递规范将消息发布到给定目标(主题/队列)

[用户手册](https://jmeter.apache.org/usermanual/component_reference.html)中详细介绍了所有可用组件。

## 4. 运行 JMeter 测试

有两种运行 JMeter 测试的方法，一种是使用可用的 Maven 插件，另一种是在非 GUI 模式下使用独立的 JMeter 应用程序。

无论如何，两者都需要知道从哪里访问我们之前配置的 JMeter 脚本。

### 4.1. JMeter Maven 插件

[JMeter Maven Plugin](http://jmeter.lazerycode.com/) 是一个 Maven 插件，它带来了运行 JMeter 测试的功能，作为我们构建的一部分；他现在的最后一个版本[是 2.6.0](https://search.maven.org/classic/#search|ga|1|jmeter-maven-plugin)，与 Apache JMeter 3.3 兼容。

让我们将它添加到我们项目的pom.xml中：

```xml
<plugin>
    <groupId>com.lazerycode.jmeter</groupId>
    <artifactId>jmeter-maven-plugin</artifactId>
    <version>2.6.0</version>
    <executions>
        <execution>
            <id>jmeter-tests</id>
            <goals>
                <goal>jmeter</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <testFilesDirectory>${project.basedir}/src/main/resources</testFilesDirectory>
        <resultsDirectory>${project.basedir}/src/main/resources</resultsDirectory>
    </configuration>
</plugin>
```

在此之后，我们可以使用mvn verify运行所有测试，或者使用mvn jmeter:jmeter运行 JMeter 测试；这是命令的控制台输出：

[![运行 jemeter 日志](https://www.baeldung.com/wp-content/uploads/2017/12/run-jemeter-log.png)](https://www.baeldung.com/wp-content/uploads/2017/12/run-jemeter-log.png)

注意：这里我们指定了我们的测试在项目中所在的目录，要么选择默认目录(${project.basedir}/src/test/jmeter)；同样配置了结果目录，否则默认目录为${project.basedir}/target/jmeter/results。

完整的插件文档可[在此处](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin/wiki)访问。

### 4.2. Non-GUI Mode

另一种方法是通过 JMeter 可执行文件，假设它可以通过命令行使用，我们可以这样做：

jmeter -Jjmeter.save.saveservice.output_format=xml

-n -t src/main/resources/JMeter.jmx -l src/main/resources/JMeter.jtl

我们将 XML 设置为输出格式，其中填充了确切的测试文件和结果文件。

注意：建议不要使用GUI模式进行负载测试，仅用于测试创建和测试调试。

## 5.总结

在本快速教程中，我们在 SpringBoot 应用程序上设置了 Apache JMeter，以使用 Maven 插件运行性能测试，同时实际了解如何设计基本性能测试。