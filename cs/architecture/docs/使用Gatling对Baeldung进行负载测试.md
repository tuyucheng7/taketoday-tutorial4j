## 1. 概述

在[之前的教程](https://www.baeldung.com/introduction-to-gatling)中，我们了解了如何使用 Gatling 对自定义 Web 应用程序进行负载测试。

在本文中，我们将使用 Gatling 压力工具来测量该网站的暂存环境的性能。

## 2.测试场景

让我们首先设置我们的主要使用场景——一个接近可能正在浏览网站的典型用户的场景：

1.  转到主页
2.  从主页打开文章
3.  转到指南/REST
4.  转到 REST 类别
5.  转到完整档案
6.  从存档中打开一篇文章

## 3.记录场景

现在，我们将使用 Gatling 记录器记录我们的场景——如下所示：

```bash
$GATLING_HOME/bin/recorder.sh复制
```

对于 Windows 用户：

```bash
%GATLING_HOME%\bin\recorder.bat复制
```

注意：GATLING_HOME是你的 Gatling 安装目录。

Gatling Recorder有两种模式：HTTP 代理和 HAR 转换器。

[我们在之前的教程](https://www.baeldung.com/introduction-to-gatling)中详细讨论了 HTTP 代理模式——所以现在让我们来看看 HAR 转换器选项。

### 3.1. HAR转换器

HAR 是 HTTP Archive 的缩写——这是一种基本上记录有关浏览会话的完整信息的格式。

我们可以从浏览器获取 HAR 文件，然后使用 Gatling Recorder 将其转换为 Simulation。

我们将在 Chrome 开发者工具的帮助下创建我们的 HAR 文件：

-   菜单 -> 更多工具 -> 开发者工具
-   转到网络选项卡
-   确保选中保留日志
-   浏览完网站后，右键单击要导出的请求
-   然后，选择全部复制为 HAR
-   将它们粘贴到一个文件中，然后从 Gatling 记录器中导入它

根据你的喜好调整加特林录音机后，单击开始。

请注意，输出文件夹默认为GATLING_HOME/user-files-simulations

## 4.模拟

生成的模拟文件同样是用 Scala 编写的。大体上还可以，但可读性不强，所以我们会做一些调整来清理。这是我们的最终模拟：

```scala
class RestSimulation extends Simulation {

    val httpProtocol = http.baseURL("http://staging.baeldung.com")

    val scn = scenario("RestSimulation")
      .exec(http("home").get("/"))
      .pause(23)
      .exec(http("article_1").get("/spring-rest-api-metrics"))
      .pause(39)
      .exec(http("rest_series").get("/rest-with-spring-series"))
      .pause(60)
      .exec(http("rest_category").get("/category/rest/"))
      .pause(26)
      .exec(http("archive").get("/full_archive"))
      .pause(70)
      .exec(http("article_2").get("/spring-data-rest-intro"))

    setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}复制
```

这里需要注意的是，完整的模拟文件要大得多；在这里，为简单起见，我们没有包括静态资源。

## 5. 运行负载测试

现在，我们可以运行我们的模拟——如下：

```bash
$GATLING_HOME/bin/gatling.sh复制
```

对于 Windows 用户：

```bash
%GATLING_HOME%\bin\gatling.bat复制
```

Gatling 工具将扫描GATLING_HOME/user-files-simulations并列出所有找到的模拟供我们选择。

运行模拟后，结果如下所示：

对于一个用户：

```bash
> request count                                304 (OK=304    KO=0)
> min response time                             75 (OK=75     KO=-)
> max response time                          13745 (OK=13745  KO=-)
> mean response time                          1102 (OK=1102   KO=-)
> std deviation                               1728 (OK=1728   KO=-)
> response time 50th percentile                660 (OK=660    KO=-)
> response time 75th percentile               1006 (OK=1006   KO=-)
> mean requests/sec                           0.53 (OK=0.53   KO=-)
---- Response Time Distribution ------------------------------------
> t < 800 ms                                           183 ( 60%)
> 800 ms < t < 1200 ms                                  54 ( 18%)
> t > 1200 ms                                           67 ( 22%)
> failed                                                 0 (  0%)复制
```

对于 5 个并发用户：

```bash
> request count                               1520 (OK=1520   KO=0)
> min response time                             70 (OK=70     KO=-)
> max response time                          30289 (OK=30289  KO=-)
> mean response time                          1248 (OK=1248   KO=-)
> std deviation                               2079 (OK=2079   KO=-)
> response time 50th percentile                504 (OK=504    KO=-)
> response time 75th percentile               1440 (OK=1440   KO=-)
> mean requests/sec                          2.411 (OK=2.411  KO=-)
---- Response Time Distribution ------------------------------------
> t < 800 ms                                           943 ( 62%)
> 800 ms < t < 1200 ms                                 138 (  9%)
> t > 1200 ms                                          439 ( 29%)
> failed                                                 0 (  0%)复制
```

对于 10 个并发用户：

```bash
> request count                               3058 (OK=3018   KO=40)
> min response time                              0 (OK=69     KO=0)
> max response time                          44916 (OK=44916  KO=30094)
> mean response time                          2193 (OK=2063   KO=11996)
> std deviation                               4185 (OK=3953   KO=7888)
> response time 50th percentile                506 (OK=494    KO=13670)
> response time 75th percentile               2035 (OK=1976   KO=15835)
> mean requests/sec                          3.208 (OK=3.166  KO=0.042)
---- Response Time Distribution ----------------------------------------
> t < 800 ms                                          1752 ( 57%)
> 800 ms < t < 1200 ms                                 220 (  7%)
> t > 1200 ms                                         1046 ( 34%)
> failed                                                40 (  1%)复制
```

请注意，当同时测试 10 个用户时，一些请求失败了——仅仅是因为暂存环境无法处理这种负载。

## 六，总结

在这篇简短的文章中，我们探讨了使用 Gatling 记录测试场景的 HAR 选项，并对 baeldung.com 进行了简单的初始测试。