## JMeter

This module contains articles about JMeter.
It contains the code of a simple API for some CRUD operations built using Spring Boot.

### Requirements

- Maven
- JDK 8
- MongoDB (Note: for the Write Extracted Data to a File Using JMeter example MongoDB is not required)

### Running

To build and start the server simply type

```bash
$ mvn clean install
$ mvn spring-boot:run -Dserver.port=8989
```

### Available CRUD

You can see what crud operation are available using curl:

```bash
$ curl localhost:8080
```

You can view existing student objects with this command:

```bash
$ curl localhost:8080/students
```

Or create a new one via a POST:

```bash
$ curl -X POST -H "Content-Type:application/json" -d '{ "firstName" : "Dassi", "lastName" : "Orleando", "phoneNumber": "+237 545454545", "email": "mymail@yahoo.fr" }' localhost:8080/students
```

### Available UUID API

You can view the test response using curl:

```bash
$ curl localhost:8080/api/uuid
```

Now with default configurations it will be available at: [http://localhost:8080](http://localhost:8080)

Enjoy it :)

## 相关文章

+ [使用JMeter进行性能测试简介](http://tu-yucheng.github.io/load/2023/05/12/jmeter.html)
+ [配置Jenkins以运行和显示JMeter测试](http://tu-yucheng.github.io/load/2023/05/12/jenkins-and-jmeter.html)
+ [使用JMeter将提取的数据写入文件](http://tu-yucheng.github.io/load/2023/05/12/jmeter-write-to-file.html)
+ [JMeter中的基本身份验证](http://tu-yucheng.github.io/load/2023/05/12/jmeter-basic-auth.html)
+ [JMeter延迟与加载时间](http://tu-yucheng.github.io/load/2023/05/12/java-jmeter-latency-vs-load-time.html)
+ [如何在JMeter中生成仪表板报告](http://tu-yucheng.github.io/load/2023/05/12/jmeter-dashboard-report.html)
+ [在Apache JMeter中的请求之间插入延迟](http://tu-yucheng.github.io/load/2023/05/12/jmeter-delays-between-requests.html)
+ [使用JMeter进行分布式性能测试](http://tu-yucheng.github.io/load/2023/05/12/jmeter-distributed-testing.html)
+ [从命令行运行JMeter.jmx文件并配置报告文件](http://tu-yucheng.github.io/load/2023/06/09/java-jmeter-command-line.html)