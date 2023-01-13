## 1. 概述

在本教程中，我们将探索 Genie 引擎，它是 Netflix 为以抽象方式向集群提交作业而开发的。

本教程推荐有关大数据处理的基本知识，例如[Hadoop](https://hadoop.apache.org/)和[Spark 。](https://www.baeldung.com/apache-spark)

## 2. 为什么选择精灵？

假设我们有许多用户需要将各种任务提交到包含大量不同大小的 Hadoop 集群的云中。虽然创建一些集群是为了在特定时间处理数据，但其他集群的中心是关闭服务以释放资源。因此，用户会遇到为他们的工作找到合适集群的问题。

假设许多用户甚至不想创建集群或不知道配置，这可能是一个真正的问题。如何为用户提供一种方式来简单地提交他们的工作并取回结果而无需处理基础设施设置？

## 3.什么是精灵？

Netflix 将 Genie 构建为分布式引擎，正是为了解决上述问题。提供统一的 RESTful API 的引擎，用于自主提交作业 。Genie 将启动作业与配置分开，允许灵活扩展集群。

但是，Genie 不会自行扩展集群——它只是通过在满足用户工作需求的集群上启动他们的工作来完成用户任务。

调度也不是 Genie 的初衷。主要目的是在单个作业级别上进行作业管理。

对于工作流调度， 应使用[Apache Oozie等其他工具。](https://oozie.apache.org/)事实上，Netflix 明确表示：

>   Genie 不是工作流调度程序，例如 Oozie。Genie 的执行单元是单个 Hadoop、Hive 或 Pig 作业。Genie 不安排或运行工作流——事实上，我们在 Netflix 使用企业调度程序 (UC4) 来运行我们的 ETL。

尽管如此，Genie 还是提供了用于管理集群、应用程序和命令等资源的 API。注册资源后，用户可以发现这些资源并开始提交他们的工作。

最后，值得注意的是，Genie 是一个或多或少针对 Netflix 的特定需求量身定制的开源项目。它在 Netflix 快速变化的云环境中扮演着重要的角色，并与 Netflix 的技术堆栈集成。

## 4. 精灵在行动

现在，让我们看看 Genie 的实际应用，以便更好地了解我们如何使用它来提交作业。作为介绍，我们将通过GitHub 上提供[的 Genie 演示来完成我们的工作。](https://netflix.github.io/genie/docs/3.3.9/demo/)

### 4.1. 先决条件

这个例子需要：

-   最新的 Docker 和 Docker Compose 版本(或 Docker Desktop，两者都包含)
-   以下自由港：8080、8088、19888、19070、19075、8089、19889、19071 和 19076
-   一个相对强大的机器，至少有 8GB RAM，其中 4GB 应该分配给 docker
-   至少 4GB 的磁盘空间

### 4.2. 集群设置

首先，我们必须通过将[docker-compose.yml](https://netflix.github.io/genie/docs/3.3.9/demo/docker-compose.yml)下载到我们选择的文件夹来使用 Docker Compose 来设置我们的集群。为此，让我们创建一个名为demoDir的新目录。compose 文件定义了我们将一一探索的五种服务。

首先，让我们看一下将在名为genie_demo_app_3.3.9的容器中运行的 Genie 服务器，该容器将端口 8080 映射到 Genie UI：

```bash
genie:
    image: netflixoss/genie-app:3.3.9
    ports:
      - "8080:8080"
    depends_on:
      - genie-hadoop-prod
      - genie-hadoop-test
      - genie-apache
    tty: true
    container_name: genie_demo_app_3.3.9
```

第二个服务是genie_demo_apache_3.3.9，用于下载demo需要的文件：

```bash
genie-apache:
    image: netflixoss/genie-demo-apache:3.3.9
    tty: true
    container_name: genie_demo_apache_3.3.9
```

接下来是 Genie 客户端，其中包含使用 Genie 提交作业的示例脚本。它的容器名称是genie_demo_client_3.3.9：

```bash
genie-client:
    image: netflixoss/genie-demo-client:3.3.9
    depends_on:
      - genie
    tty: true
    container_name: genie_demo_client_3.3.9
```

接下来是我们的生产 (SLA) Hadoop 集群。该服务将接收我们提交的作业。集群资源管理器映射到端口 8088，而历史服务器获得 19888。

我们将在这里做一个小调整，将命名节点和数据节点分别映射到端口 19070 和 19075：

```bash
genie-hadoop-prod:
    image: sequenceiq/hadoop-docker:2.7.1
    command: /bin/bash -c "/usr/local/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver 
               && /etc/bootstrap.sh -bash"
    ports:
      - "19888:19888"
      - "19070:50070"
      - "19075:50075"
      - "8088:8088"
    tty: true
    container_name: genie_demo_hadoop_prod_3.3.9
```

最后，让我们探索代表测试集群的测试 Hadoop 容器。与生产集群类似，它分配了端口 8089(资源管理器)、19889(历史服务器)、19071(命名节点)和 19076(数据节点)：

```bash
genie-hadoop-test:
    image: sequenceiq/hadoop-docker:2.7.1
    command: /bin/bash -c "/usr/local/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver 
               && /etc/bootstrap.sh -bash"
    ports:
      - "19889:19888"
      - "19071:50070"
      - "19076:50075"
      - "8089:8088"
    tty: true
    container_name: genie_demo_hadoop_test_3.3.9
```

让我们通过从demoDir 运行 docker -compose来启动上述容器。第一次运行时执行时间会更长，因为它必须下载演示图像：

```bash
cd demoDir
docker-compose up -d
```

我们可以通过检查以下内容来验证集群是否已启动并准备就绪：

-   精灵用户界面：[http://localhost:8080](http://localhost:8080/)
-   SLA 集群资源管理器 UI：[http://localhost:8088](http://localhost:8088/)
-   TEST 集群资源管理器 UI：[http://localhost:8089](http://localhost:8089/)

### 4.3. 初始化演示

现在，在演示容器运行后，我们可以使用docker exec命令登录到客户端容器 ：

```bash
docker exec -it genie_demo_client_3.3.9 /bin/bash
```

现在在客户端容器中，我们执行一个初始化脚本来准备集群来接受我们的工作：

```bash
./init_demo.py
```

如果演示成功运行，Genie UI 将在集群、命令和应用程序选项卡中显示数据。

### 4.4. 职位提交

再举一个例子，让我们提交一个 Spark 作业来计算 π 的前 10 位小数。我们可以通过将相应的文字作为参数传递给脚本来将作业提交给测试或 SLA：

```bash
./run_spark_submit_job.py sla 2.0.1
./run_spark_submit_job.py test 2.0.1
```

在 Genie UI 的“作业”选项卡中，我们可以单击每个作业描述中的文件夹图标以导航到其输出文件夹。从那里，成功完成后，我们可以在标准输出下找到计算值。

[![天才的工作](https://www.baeldung.com/wp-content/uploads/2019/11/Genie-Job-1024x142.png)](https://www.baeldung.com/wp-content/uploads/2019/11/Genie-Job-1024x142.png)

Hadoop 资源管理器 UI 也显示集群作业。

[![Hadoop 资源管理器](https://www.baeldung.com/wp-content/uploads/2019/11/Hadoop-Resource-Manager-1024x266.png)](https://www.baeldung.com/wp-content/uploads/2019/11/Hadoop-Resource-Manager-1024x266.png)

最后，我们退出客户端容器，通过运行停止并删除所有演示容器：

```bash
docker-compose down
```

图像仍将在磁盘上可用，我们可以随时再次启动演示容器。

## 5.总结

在本教程中，我们介绍了 Netflix 开发的作业管理工具 Genie。

然后我们运行了一个演示，它为我们提供了一个实际示例，说明我们如何在现实生活场景中使用 Genie。