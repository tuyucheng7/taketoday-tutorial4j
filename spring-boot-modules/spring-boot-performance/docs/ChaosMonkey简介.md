## 1. 简介

在本教程中，我们将讨论用于Spring Boot的[Chaos Monkey](https://codecentric.github.io/chaos-monkey-spring-boot/)。

该工具通过向REST端点添加延迟、抛出错误甚至终止应用程序，帮助我们**将**[混沌工程](https://principlesofchaos.org/)**的一些原则引入我们的Spring boot Web应用程序**。

## 2. 设置

要将Chaos Monkey添加到我们的应用程序中，我们的项目中需要有一个[Maven](https://search.maven.org/classic/#search|gav|1|a%3A"chaos-monkey-spring-boot")依赖项：

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>chaos-monkey-spring-boot</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 3. 配置

一旦在我们的项目中设置了依赖项，我们就需要配置并开始我们的混沌。

我们可以通过几种方式做到这一点：

-   在应用程序启动时，使用chaos-monkey spring profile(推荐)
-   使用chaos.monkey.enabled=true属性

通过使用chaos-monkey spring profile启动应用程序，**如果我们想在应用程序运行时启用或禁用它，我们不必停止和启动应用程序**：

```bash
java -jar your-app.jar --spring.profiles.active=chaos-monkey
```

另一个有用的属性是management.endpoint.chaosmonkey.enabled，将此属性设置为true将为我们的Chaos Monkey启用管理端点：

```bash
http://localhost:8080/chaosmonkey
```

从这个端点，我们可以看到我们库的状态，这是端点的完整[列表](https://github.com/codecentric/chaos-monkey-spring-boot/blob/main/chaos-monkey-docs/src/main/asciidoc/endpoints.adoc)及其描述，它们将有助于更改配置、启用或禁用Chaos Monkey以及其他更精细的控制。

使用所有可用的[属性](https://codecentric.github.io/chaos-monkey-spring-boot/latest/#_properties)，我们可以对生成的混沌中发生的事情进行更细粒度的控制。

## 4. 它是如何工作的

Chaos Monkey由Watchers和Assaults组成，Watcher是一个Spring Boot组件，它利用 [Spring AOP](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop-api)来查看何时在使用以下Spring注解标注的类中执行公共方法：

-   @Component
-   @Controller
-   @RestController
-   @Service
-   @Repository

根据我们的应用程序属性文件中的配置，**我们的公共方法将受到以下情况之一的攻击或不攻击**：

-   延迟攻击：向请求添加随机延迟
-   异常攻击：抛出随机运行时异常
-   AppKiller攻击：嗯，应用程序死了

让我们来看看我们如何配置我们的Watchers和Assaults以进行更可控的攻击。

## 5. Watcher

默认情况下，Watcher仅针对我们的Service启用，这意味着我们的攻击将仅针对我们用@Service注解的类中的公共方法执行。

但是我们可以通过配置属性轻松地更改它：

```properties
chaos.monkey.watcher.controller=false
chaos.monkey.watcher.restController=false
chaos.monkey.watcher.service=true
chaos.monkey.watcher.repository=false
chaos.monkey.watcher.component=false
```

请记住，一旦应用程序启动，**我们就无法使用我们之前谈到的用于Spring Boot管理端口的Chaos Monkey动态更改Watcher**。

## 6. Assaults

Assaults基本上是我们想要在我们的应用程序中测试的场景。让我们来看看每种类型的攻击，看看它做了什么以及我们如何配置它。

### 6.1 延迟攻击

这种类型的攻击会增加我们调用的延迟，**这样我们的应用程序响应速度就会变慢，并且我们可以监控它的行为，例如当数据库响应速度变慢时**。

我们可以使用我们应用程序的属性文件来配置和开启这种类型的攻击：

```properties
chaos.monkey.assaults.latencyActive=true
chaos.monkey.assaults.latencyRangeStart=3000
chaos.monkey.assaults.latencyRangeEnd=15000
```

配置和打开和关闭此类攻击的另一种方法是通过Chaos Monkey的管理端点。

让我们打开延迟攻击并添加2到5秒之间的延迟范围：

```bash
curl -X POST http://localhost:8080/chaosmonkey/assaults \
-H 'Content-Type: application/json' \
-d \
'
{
	"latencyRangeStart": 2000,
	"latencyRangeEnd": 5000,
	"latencyActive": true,
	"exceptionsActive": false,
	"killApplicationActive": false
}'
```

### 6.2 异常攻击

这测试了我们的应用程序处理异常的能力，**根据配置，一旦启用，它将抛出一个随机的运行时异常**。

我们可以使用类似于延迟攻击的curl调用来启用它：

```bash
curl -X POST http://localhost:8080/chaosmonkey/assaults \
-H 'Content-Type: application/json' \
-d \
'
{
	"latencyActive": false,
	"exceptionsActive": true,
	"killApplicationActive": false
}'
```

### 6.3 应用杀手攻击

这个，好吧，我们的应用程序会在某个随机点死掉。我们可以像前两种类型的攻击一样，通过简单的curl调用来启用或禁用它：

```bash
curl -X POST http://localhost:8080/chaosmonkey/assaults \
-H 'Content-Type: application/json' \
-d \
'
{
	"latencyActive": false,
	"exceptionsActive": false,
	"killApplicationActive": true
}'
```

## 7. 总结

在本文中，我们讨论了用于Spring Boot的Chaos Monkey，我们已经看到它采用[混沌工程](https://principlesofchaos.org/)的一些原则，并使我们能够将它们应用到[Spring Boot](https://spring.io/projects/spring-boot)应用程序中。