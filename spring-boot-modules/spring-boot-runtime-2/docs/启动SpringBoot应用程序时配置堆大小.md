## 1. 简介

在本教程中，我们将学习如何在启动Spring Boot应用程序时配置[堆大小]()。我们将配置-Xms和-Xmx设置，它们对应于起始堆大小和最大堆大小。

然后，我们将首先使用Maven在命令行上使用mvn启动应用程序时配置堆大小，并了解如何使用Maven插件设置这些值。接下来，我们将我们的应用程序打包到一个jar文件中，并使用提供给java -jar命令的[JVM 参数]()运行它。

最后，我们将创建一个设置JAVA_OPTS的.conf文件，并使用Linux System V Init技术将[我们的应用程序作为服务运行](https://www.baeldung.com/spring-boot-app-as-a-service)。

## 2. 从Maven运行

### 2.1 传递JVM参数

让我们首先创建一个简单的REST控制器，该控制器返回一些基本的内存信息，我们可以使用这些信息来验证我们的设置：

```java
@GetMapping("memory-status")
public MemoryStats getMemoryStatistics() {
    MemoryStats stats = new MemoryStats();
    stats.setHeapSize(Runtime.getRuntime().totalMemory());
    stats.setHeapMaxSize(Runtime.getRuntime().maxMemory());
    stats.setHeapFreeSize(Runtime.getRuntime().freeMemory());
    return stats;
}
```

让我们使用mvn spring-boot：run按原样运行它来获取基线，一旦我们的应用程序启动，我们就可以使用[curl]()来调用我们的REST控制器：

```bash
curl http://localhost:8080/memory-status
```

我们的结果会因我们的机器而异，但看起来像这样：

```plaintext
{"heapSize":333447168,"heapMaxSize":5316280320,"heapFreeSize":271148080}
```

对于Spring Boot 2.x，我们可以使用-Dspring-boot.run将[参数传递]()给我们的应用程序。

让我们使用-Dspring-boot.run.jvmArguments将起始堆大小和最大堆大小传递给我们的应用程序：

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xms2048m -Xmx4096m"
```

现在，当我们调用端点时，我们应该看到指定的堆设置：

```bash
{"heapSize":2147483648,"heapMaxSize":4294967296,"heapFreeSize":2042379008}
```

### 2.2 使用Maven插件

通过在pom.xml文件中配置spring-boot-maven-plugin，我们可以避免每次运行应用程序时都必须提供参数：

让我们配置插件来设置我们想要的堆大小：

```xml
<plugins>
	<plugin>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-maven-plugin</artifactId>
		<executions>
			<execution>
				<configuration>
					<mainClass>cn.tuyucheng.taketoday.heap.HeapSizeDemoApplication</mainClass>
				</configuration>
			</execution>
		</executions>
		<configuration>
			<executable>true</executable>
			<jvmArguments>
				-Xms256m
				-Xmx1g
			</jvmArguments>
		</configuration>
	</plugin>
</plugins>
```

现在，我们可以只使用mvn spring-boot:run运行我们的应用程序，并在ping端点时查看正在使用的指定JVM参数：

```shell
{"heapSize":259588096,"heapMaxSize":1037959168,"heapFreeSize":226205152}
```

**我们在插件中配置的任何JVM参数将优先于使用-Dspring-boot.run.jvmArguments从Maven运行时提供的任何参数**。

## 3. 使用java -jar运行

如果我们从jar文件运行我们的应用程序，我们可以为java命令提供JVM参数。

首先，我们必须在我们的Maven文件中指定<packaging\>为jar：

```xml
<packaging>jar</packaging>
```

然后，我们可以将我们的应用程序打包成jar文件：

```bash
mvn clean package
```

现在我们有了jar文件，我们可以使用java -jar运行它并覆盖堆配置：

```bash
java -Xms512m -Xmx1024m -jar target/spring-boot-runtime-2.jar
```

然后调用我们的端点来检查内存值：

```shell
{"heapSize":536870912,"heapMaxSize":1073741824,"heapFreeSize":491597032}
```

## 4. 使用.conf文件

最后，我们将学习如何使用.conf文件来设置作为Linux服务运行的应用程序的堆大小。

让我们首先创建一个与我们的应用程序jar文件同名且扩展名为.conf的文件：spring-boot-runtime-2.conf。

我们现在可以将它放在resources下的文件夹中，并将我们的堆配置添加到JAVA_OPTS：

```plaintext
JAVA_OPTS="-Xms512m -Xmx1024m"
```

接下来，修改我们的Maven构建以将spring-boot-runtime-2.conf文件复制到我们target文件夹的jar文件中：

```xml
<build>
	<finalName>${project.artifactId}</finalName>
	<resources>
		<resource>
			<directory>src/main/resources/heap</directory>
			<targetPath>${project.build.directory}</targetPath>
			<filtering>true</filtering>
			<includes>
				<include>${project.name}.conf</include>
			</includes>
		</resource>
	</resources>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
			<executions>
				<execution>
					<configuration>
						<mainClass>cn.tuyucheng.taketoday.heap.HeapSizeDemoApplication</mainClass>
					</configuration>
				</execution>
			</executions>
			<configuration>
				<executable>true</executable>
			</configuration>
		</plugin>
	</plugins>
</build>
```

**我们还需要将executable设置为true才能将我们的应用程序作为服务运行**。

我们可以打包我们的jar文件并使用Maven复制我们的.conf文件：

```bash
mvn clean package spring-boot:repackage
```

接下来创建我们的init.d服务：

```bash
sudo ln -s /path/to/spring-boot-runtime-2.jar /etc/init.d/spring-boot-runtime-2
```

现在，开始我们的应用程序：

```bash
sudo /etc/init.d/spring-boot-runtime-2 start
```

然后，当我们调用端点时，我们应该看到我们在.conf文件中指定的JAVA_OPT值得到了配置：

```bash
{"heapSize":538968064,"heapMaxSize":1073741824,"heapFreeSize":445879544}
```

## 5. 总结

在这个简短的教程中，我们研究了如何为运行Spring Boot应用程序的三种常见方式覆盖Java堆设置。我们从Maven开始，既在命令行修改值，也通过在Spring Boot Maven插件中设置它们。

接下来，我们使用java -jar并传入JVM参数来运行我们的应用程序jar文件。

最后，我们通过设置一个.conf文件，并创建一个System V init服务来运行我们的应用程序。

还有其他解决方案可以从Spring Boot fat jar创建服务和守护进程，其中许多提供了覆盖JVM参数的特定方法。