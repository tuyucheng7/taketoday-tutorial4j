## **一、简介**

调试是编写软件最重要的工具之一。

在本教程中，我们将回顾一些调试 Spring 应用程序的方法。

我们还将演示 Spring Boot、传统应用程序服务器和 IDE 如何简化这一过程。

## **2. Java 调试参数**

首先，让我们看看 Java 开箱即用地为我们提供了什么。

**默认情况下，JVM 不启用调试**。这是因为调试会在 JVM 内部产生额外的开销。对于可公开访问的应用程序，它也可能是一个安全问题。

因此，**调试应该只在开发过程中进行，**绝不能在生产系统上进行。

在我们附加调试器之前，我们必须首先配置 JVM 以允许调试。我们将通过为 JVM 设置命令行参数来完成此操作：

```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000复制
```

让我们分解一下每个值的含义：

***-agentlib:jdwp\***

这会在 JVM 中启用 Java 调试有线协议 (JDWP) 代理。**这是启用调试的主要命令行参数。**

***传输=dt_socket\***

这使用网络套接字进行调试连接。其他选项包括 Unix 套接字和共享内存。

***服务器=y\***

这会侦听传入的调试器连接。当设置为*n*时，进程将尝试连接到调试器而不是等待传入连接。*当它设置为n*时，需要额外的参数。

***暂停=n\***

这意味着不要在启动时等待调试连接。应用程序将启动并正常运行，直到附加调试器。当设置为*y*时，在附加调试器之前进程不会启动。

***地址=8000\***

这是 JVM 将侦听调试连接的网络端口。

上面的值是标准值，适用于大多数用例和操作系统。JPDA[连接指南](https://docs.oracle.com/en/java/javase/11/docs/specs/jpda/conninv.html)更详细地涵盖了所有可能的值。

### 2.1. JDK9+上的绑定地址

在 JDK8 及以下版本中，将*address*属性设置为仅端口号（上例中的 address=8000）意味着 JVM 会监听所有可用的 IP 地址。因此，远程连接是开箱即用的。

由于安全原因，这在 JDK9+ 中已更改。当前，默认设置仅允许本地主机连接。

这意味着如果我们想让远程连接可用，我们需要在端口号前加上主机名前缀，*address* =myhost:8000，或者使用星号来监听所有可用的 IP 地址，*address* =*:8000。

## **3. Spring Boot 应用**

[有几种方法](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html#using-boot-running-your-application)可以启动 Spring Boot 应用程序。最简单的方法是从命令行使用带有*-jar选项的**java*命令。

*要启用调试，我们只需使用-D*选项添加调试参数：

```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000 -jar myapp.jar复制
```

使用 Maven，我们可以使用提供的*运行*目标来启动我们的应用程序并启用调试：

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000"复制
```

同样，对于 Gradle，我们可以使用*bootRun*任务。首先，我们必须更新*build.gradle*文件以确保 Gradle 将命令行参数传递给 JVM：

```plaintext
bootRun {
   systemProperties = System.properties
}复制
```

现在我们可以执行*bootRun*任务：

```bash
gradle bootRun -Dagentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000复制
```

## **4. 应用服务器**

虽然 Spring Boot 近年来变得非常流行，但传统的[应用程序服务器](https://www.baeldung.com/java-servers)在现代软件架构中仍然相当普遍。在本节中，我们将了解如何为一些更流行的应用程序服务器启用调试。

大多数应用程序服务器都提供用于启动和停止应用程序的脚本。启用调试通常只是向该脚本添加额外参数和/或设置额外环境变量的问题。

### **4.1. 雄猫**

[Tomcat](https://www.baeldung.com/spring-boot-war-tomcat-deploy)的启动脚本名为*catalina.sh*（在 Windows 上为*catalina.bat*）。要在启用调试的情况下启动 Tomcat 服务器，我们可以将*jpda*添加到参数中：

```bash
catalina.sh jpda start复制
```

默认调试参数将使用网络套接字侦听端口 8000 和*suspend=n*。这些可以通过设置以下一个或多个环境变量来更改：*JPDA_TRANSPORT*、*JPDA_ADDRESS*和*JPDA_SUSPEND*。

*我们还可以通过设置JPDA_OPTS*来完全控制调试参数。设置此变量后，它优先于其他 JPDA 变量。因此，它必须是 JVM 的完整调试参数。

### **4.2. 野蝇**

[Wildfly](https://www.baeldung.com/jboss-start-stop)的启动脚本是*stand-alone.sh*。要在启用调试的情况下启动 Wildfly 服务器，我们可以添加*–debug*。

默认调试模式使用*suspend=n*端口 8787 上的网络侦听器。*我们可以通过在–debug*参数之后指定它来覆盖端口。

为了更好地控制调试参数，我们可以将完整的调试参数添加到*JAVA_OPTS*环境变量中。

### **4.3. 网络逻辑**

Weblogic 的启动脚本是*startWeblogic.sh*。要在启用调试的情况下启动 Weblogic 服务器，我们可以将环境变量*debugFlag*设置为*true*。

默认调试模式使用*suspend=n*端口 8453 上的网络侦听器。*我们可以通过设置DEBUG_PORT*环境变量来覆盖端口。

为了更好地控制调试参数，我们可以将完整的调试参数添加到*JAVA_OPTIONS*环境变量中。

最新版本的 Weblogic 还提供了一个 Maven 插件来启动和停止服务器。**此插件将采用与启动脚本相同的环境变量**。

### **4.4. 玻璃鱼**

Glassfish 的启动脚本是*asadmin*。要启动启用调试的 Glassfish 服务器，我们必须使用*–debug*：

```bash
asadmin start-domain --debug复制
```

默认调试模式使用*suspend=n*端口 9009 上的网络侦听器。

### **4.5. 码头**

Jetty 应用服务器没有附带启动脚本。相反，Jetty 服务器是使用*java*命令启动的。

因此，启用调试就像添加标准 JVM 命令行参数一样简单。

## **5. 从 IDE 调试**

现在我们已经了解了如何在各种应用程序类型中启用调试，让我们看看连接调试器。

每个现代 IDE 都提供调试支持。这包括在启用调试的情况下启动新进程的能力，以及调试已经运行的进程的能力。

### **5.1. IntelliJ**

[IntelliJ](https://www.baeldung.com/intellij-debugging-tricks)为 Spring 和 Spring Boot 应用程序提供一流的支持。*调试非常简单，只需导航到具有main*方法的类，右键单击三角形图标，然后选择“调试”：

[![intellij run gutter - 图标](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-run-gutter-icon.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-run-gutter-icon.jpg)

如果一个项目包含多个 Spring Boot 应用程序，IntelliJ 会提供一个 Run Dashboard 工具窗口。这个窗口让我们可以从一个地方调试多个 Spring Boot 应用程序：

[![intellij spring run仪表盘](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-spring-run-dashboard.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-spring-run-dashboard.jpg)

对于使用 Tomcat 或其他 Web 服务器的应用程序，我们可以创建自定义配置以进行调试。在*Run* > *Edit Configurations*下，有许多适用于最流行的应用程序服务器的模板：

[![intellij 运行调试模板](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-run-debug-templates.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-run-debug-templates.jpg)

最后，IntelliJ 使得连接到任何正在运行的进程并对其进行调试变得非常容易。**只要应用程序是使用正确的调试参数启动的**，IntelliJ 就可以连接到它，即使它在另一台主机上。

在*Run/Debug Configurations*屏幕上，*Remote*模板将让我们配置我们想要附加到已经运行的应用程序的方式：

[![intellij远程调试配置](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-remote-debug-configuration.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/intellij-remote-debug-configuration.jpg)

请注意，IntelliJ 只需要知道主机名和调试端口。为了方便起见，它告诉我们应该在我们要调试的应用程序上使用正确的 JVM 命令行参数。

### **5.2. 蚀**

*在 Eclipse 中调试 Spring Boot 应用程序的最快方法是在Package Explorer*或*Outline*窗口中右键单击 main 方法：

[![eclipse调试java应用程序](https://www.baeldung.com/wp-content/uploads/2018/12/eclipse-debug-java-application.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/eclipse-debug-java-application.jpg)

Eclipse 的默认安装不支持开箱即用的 Spring 或 Spring Boot。但是，Eclipse Marketplace 中有一个[Spring Tools 附加组件，它提供与 IntelliJ 相当的 Spring 支持。](https://www.eclipse.org/community/eclipse_newsletter/2018/february/springboot.php)

最值得注意的是，**该附加组件提供了一个引导仪表板，让我们可以从一个地方管理多个 Spring Boot 应用程序**：

[![日食春季启动仪表板](https://www.baeldung.com/wp-content/uploads/2018/12/eclipse-spring-boot-dashboard.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/eclipse-spring-boot-dashboard.jpg)

该附加组件还提供了一个*Spring Boot*运行/调试配置，允许我们自定义单个 Spring Boot 应用程序的调试。此自定义视图可从与标准*Java 应用程序*配置相同的所有位置获得。

要在本地或远程主机上调试已经运行的进程，我们可以使用*远程 Java 应用程序*配置：

[![eclipse ide远程调试配置](https://www.baeldung.com/wp-content/uploads/2018/12/eclipse-ide-remote-debug-configuration.jpg)](https://www.baeldung.com/wp-content/uploads/2018/12/eclipse-ide-remote-debug-configuration.jpg)

## **6. 使用 Docker 调试**

[在 Docker 容器中](https://www.baeldung.com/dockerizing-spring-boot-application)调试Spring 应用程序可能需要额外的配置。**如果容器在本地运行，并且未使用主机网络模式**，则无法在容器外部访问调试端口。

有几种方法可以在 Docker 中公开调试端口。

我们可以将*–expose*与*docker run*命令一起使用：

```bash
docker run --expose 8000 mydockerimage复制
```

我们还可以将*EXPOSE指令添加到**Dockerfile*中：

```plaintext
EXPOSE 8000复制
```

或者，如果我们使用 Docker Compose，我们可以将它添加到 YAML 中：

```plaintext
expose:
 - "8000"复制
```

## **七、结论**

在本文中，我们讨论了如何为任何 Java 应用程序启用调试。

通过简单地添加一个命令行参数，我们可以轻松调试任何 Java 应用程序。

我们还了解到，Maven 和 Gradle 以及最流行的 IDE 都有专门的附加组件，可以使调试 Spring 和 Spring Boot 应用程序更加容易。