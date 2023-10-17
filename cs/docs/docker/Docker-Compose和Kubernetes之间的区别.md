---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在处理容器化应用程序时，我们可能想知道Docker Compose和Kubernetes在这方面扮演什么角色。

在本教程中，我们将讨论一些最常见的用例以了解两者之间的区别。

## 2. Docker Compose

[Docker Compose](https://www.baeldung.com/ops/docker-compose)是一个命令行工具，用于运行具有 YAML 模板定义的多个Docker容器。我们可以从现有镜像或特定上下文构建容器。

我们可以添加一个 版本的 compose 文件格式和至少一个服务。或者，我们可以添加卷和 网络。此外，我们可以定义依赖项和环境变量。

### 2.1.Docker组合模板

让我们为连接到PostgreSQL数据库的 API创建一个docker-compose.yml文件：

```yaml
version: '3.8'
services:
  db:
    image: postgres:latest
    restart: always
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - '5432:5432'
    volumes:
      - db:/var/lib/postgresql/data
    networks:
      - mynet

  api:
    container_name: my-api
    build:
      context: ../../../../docker-modules
    image: my-api
    depends_on:
      - db
    networks:
      - mynet
    ports:
      - 8080:8080
    environment:
      DB_HOST: db
      DB_PORT: 5432
      DB_USER: postgres
      DB_PASSWORD: postgres
      DB_NAME: postgres

networks:
  mynet:
    driver: bridge

volumes:
  db:
    driver: local
```

最后，我们可以通过运行以下命令开始在本地或生产环境中工作：

```shell
docker-compose up
```

### 2.2.Docker Compose常见用例

我们通常使用Docker Compose来创建微服务基础设施环境，通过网络链接不同的服务。

此外，Docker Compose被广泛用于为我们的测试套件创建和销毁隔离的测试环境。

此外，如果我们对可扩展性感兴趣，我们可以看看 [Docker Swarm——](https://docs.docker.com/engine/swarm/)一个由Docker创建的项目，用于像Kubernetes一样在编排级别工作。

然而，与Kubernetes相比，Docker Swarm 的产品有限。

## 3. 库伯内斯

借助[Kubernetes](https://kubernetes.io/docs/home/)(也称为 K8s)，我们可以在容器化和集群环境中自动部署和管理应用程序。谷歌在 K8s 上的最初工作从开源到捐赠给[Linux 基金会，并最终作为启动](https://www.linuxfoundation.org/)[云原生计算基金会 (CNCF) 的](https://www.cncf.io/)种子技术 。

在容器时代，Kubernetes受到了极大的关注，以至于成为现在最流行的分布式系统编排器。

一个完整的[API](https://kubernetes.io/docs/concepts/overview/kubernetes-api/)可用于描述Kubernetes对象的规范和状态。它还允许与第三方软件集成。

在Kubernetes中，不同的[组件](https://kubernetes.io/docs/concepts/overview/components/)是集群的一部分，该集群由一组称为节点的工作机器组成。节点在[Pod](https://kubernetes.io/docs/concepts/workloads/pods/)中运行我们的容器化应用程序。

Kubernetes就是管理部署在虚拟机或节点上的Pod中的工件。节点和它们运行的容器都被分组为一个集群，每个容器都有端点、DNS、存储和可扩展性。

Pod是非永久性资源。例如，部署可以动态地创建和销毁它们。通常，我们可以将应用程序公开为 [服务](https://kubernetes.io/docs/concepts/services-networking/)，以便始终在同一端点可用。

### 3.1.Kubernetes模板

Kubernetes提供了一种[声明式或命令式的](https://kubernetes.io/docs/tasks/manage-kubernetes-objects/)方法，因此我们可以使用模板来创建、更新、删除甚至缩放对象。例如，让我们为Deployment定义一个模板：

```yaml
-- Postgres Database
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgresql
  namespace: Database
spec:
  selector:
    matchLabels:
      app: postgresql
  replicas: 1
  template:
    metadata:
      labels:
        app: postgresql
    spec:
      containers:
        - name: postgresql
          image: postgresql:latest
          ports:
            - name: tcp
              containerPort: 5432
          env:
            - name: POSTGRES_USER
              value: postgres
            - name: POSTGRES_PASSWORD
              value: postgres
            - name: POSTGRES_DB
              value: postgres
          volumeMounts:
        - mountPath: /var/lib/postgresql/data
          name: postgredb
      volumes:
        - name: postgredb
          persistentVolumeClaim:
            claimName: postgres-data

-- My Api
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-api
  namespace: Api
spec:
  selector:
    matchLabels:
      app: my-api
  replicas: 1
  template:
    metadata:
      labels:
        app: my-api
    spec:
      containers:
        - name: my-api
          image: my-api:latest
          ports:
            - containerPort: 8080
              name: "http"
          volumeMounts:
            - mountPath: "/app"
              name: my-app-storage
          env:
            - name: POSTGRES_DB
              value: postgres
            - name: POSTGRES_USER
              value: postgres
            - name: POSTGRES_PASSWORD
              value: password
          resources:
            limits:
              memory: 2Gi
              cpu: "1"
      volumes:
        - name: my-app-storage
          persistentVolumeClaim:
            claimName: my-app-data
```

[然后我们可以使用kubectl](https://kubernetes.io/docs/reference/kubectl/)命令行通过网络使用对象。

### 3.2.Kubernetes和云提供商

Kubernetes本身并不是基础架构即代码 (IaC)。但是，它集成了云提供商的容器服务——例如，Amazon 的[ECS](https://aws.amazon.com/ecs/)或[EKS 、Google 的](https://aws.amazon.com/eks/)[GKE](https://cloud.google.com/kubernetes-engine)和RedHat 的[OpenShift](https://www.redhat.com/en/technologies/cloud-computing/openshift)。

或者我们可以使用它，例如，使用像[Helm](https://www.baeldung.com/ops/kubernetes-helm)这样的工具。

我们确实经常在公共云基础设施中看到Kubernetes。但是，我们可以设置[Minikube](https://www.baeldung.com/spring-boot-minikube)或本地[Kubeadm](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/)集群。

同样通过 CNCF 批准，我们可以查看[K3s](https://k3s.io/)，这是 K8s 的轻量级版本。

## 4.Kubernetes和Docker Compose的区别

Docker Compose是关于创建和启动一个或多个容器的，而Kubernetes更多的是作为一个平台来创建一个我们可以在其中编排容器的网络。

Kubernetes已经能够解决应用程序管理中的许多关键问题：

-   资源优化
-   容器的自愈
-   应用程序重新部署期间的停机时间
-   自动缩放

最后，Kubernetes将多个隔离的容器带到了一个资源始终可用的阶段，并具有潜在的最佳分布。

但是，当涉及到开发时，Docker Compose可以配置应用程序的所有服务依赖项以开始使用，例如我们的自动化测试。因此，它是本地开发的强大工具。

## 5.总结

在本文中，我们了解了Docker Compose和Kubernetes之间的区别。当我们需要定义和运行多容器Docker应用程序时，Docker Compose可以提供帮助。

Kubernetes是一个强大而复杂的框架，用于在集群环境中管理容器化应用程序。
