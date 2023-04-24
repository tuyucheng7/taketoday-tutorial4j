## 1. 概述

Docker容器在我们的系统中作为独立进程运行。然而，我们通常希望它们能够相互交流和传递信息。

在本教程中，我们通过一些使用Docker Compose的实际示例了解Docker链接和depends_on之间的区别。

## 2. Docker Compose depends_on

**[depends_on](https://docs.docker.com/compose/compose-file/compose-file-v3/#depends_on)是一个[Docker Compose]()关键字，用于设置服务必须启动和停止的顺序**。

例如，假设我们希望我们的Web应用程序(我们构建为wep-app镜像)在我们的[Postgres]()容器之后启动，下面是docker-compose.yml文件：

```yaml
services:
    db:
        image: postgres:latest
        environment:
            - POSTGRES_USER=postgres
            - POSTGRES_PASSWORD=postgres
        ports:
            - 5432:5432
    web-app:
        image: web-app:latest
        ports:
            - 8080:8080
        depends_on:
            - db
```

Docker将根据给定的依赖关系拉取镜像并运行容器。因此，在本例中，Postgres容器是队列中第一个运行的容器。

**但是存在一些限制，因为depends_on不会显式等待依赖项准备就绪**。

假设我们的Web应用程序需要在启动时运行一些数据库迁移脚本。如果数据库不接受连接，虽然Postgres服务已正确启动，但我们无法执行任何脚本。

但是，如果我们使用特定工具或我们自己的托管脚本来[控制启动或关闭顺序](https://docs.docker.com/compose/startup-order/)，则可以避免这种情况。

## 3. Docker Compose链接

**[links](https://docs.docker.com/network/links/)指示Docker通过网络链接容器，当我们链接容器时，Docker会创建环境变量并将容器添加到已知主机列表中，以便它们可以相互发现**。

我们将查看一个运行Postgres容器的简单Docker示例，并将其链接到我们的Web应用程序。

首先，我们运行Postgres容器：

```shell
docker run -d --name db -p 5342:5342 postgres:latest 
```

然后，我们将它链接到我们的Web应用程序：

```shell
docker run -d -p 8080:8080 --name web-app --link db 
```

下面通过Docker Compose完成该示例：

```yaml
services:
    db:
        image: postgres:latest
        environment:
            - POSTGRES_USER=postgres
            - POSTGRES_PASSWORD=postgres
        ports:
            - 5432:5432
    web-app:
        images: web-app:latest
        ports:
            - 8080:8080
        links:
            - db
```

## 4. Docker Compose网络

**我们可以发现Docker链接仍在使用，但是，由于[网络](https://docs.docker.com/network/)的引入，Docker Compose从版本2开始弃用它**。

通过这种方式，我们可以将应用程序与复杂的网络连接起来，例如[覆盖](https://docs.docker.com/network/overlay/)网络。

但是，在独立应用程序中，当我们不指定网络时，我们通常可以使用[网桥](https://docs.docker.com/network/bridge/)作为默认值。

让我们删除links并将其替换为networks，同时还为数据库添加数据卷和环境变量：

```yaml
services:
    db:
        image: postgres:latest
        restart: always
        environment:
            - POSTGRES_USER=postgres
            - POSTGRES_PASSWORD=postgres
        ports:
            - 5432:5432
        volumes:
            - db:/var/lib/postgresql/data
        networks:
            - mynet

    web-app:
        image:web-app: latest
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

## 5. Docker links和depends_on的区别

虽然它们都涉及表达依赖关系，但Docker links和depends_on具有不同的含义。**depends_on指示服务必须启动和停止的顺序，而links关键字处理网络上容器的通信**。

此外，depends_on是Docker Compose关键字，而我们也可以类似地使用links作为Docker的遗留功能。

## 6. 总结

在本文中，我们通过Docker Compose示例了解了Docker链接和depends_on之间的区别。depends_on指示Docker运行容器的顺序，而links或较新版本的Docker Compose中的网络通过网络为容器设置连接。

与往常一样，我们可以[在GitHub上找到案例的源]()docker-compose.yml文件。