## 1. 概述

我们通常使用单个docker-compose.yml文件来引用Docker Compose，但是，我们可能需要使用多个YAML文件，并且仍然能够让正在运行的容器成为同一网络的一部分。

在这个简短的教程中，我们将通过一些docker-compose.yml示例了解如何使用网络连接多个Docker Compose项目。

## 2. 为多个Docker Compose项目使用一个网络

**由于[Docker Compose]()引入了[网络]()，我们可以让我们的容器知道现有网络并让它们加入其中**。

例如，假设我们希望我们的Redis缓存和Web应用程序成为同一网络的一部分，但它们是两个不同的YAML文件。

### 2.1 加入现有网络

首先，我们创建一个网络：

```shell
docker network create network-example
```

然后，我们可以在我们的YAML模板中定义对该现有网络的引用。

下面是我们的Redis定义：

```yaml
services:
    db:
        image: redis:latest
        container_name: redis
        ports:
            - '6379:6379'
        networks:
            - network-example

networks:
    network-example:
        external: true
```

接下来在另一个文件中为我们的Web应用程序定义它：

```yaml
services:
    my_app:
        image: "web-app:latest"
        container_name: web-app
        ports:
            - "8080:8080"
        networks:
            - network-example

networks:
    network-example:
        external: true
```

### 2.2 在YAML模板中定义网络

**同样，我们也可以在YAML中定义一个网络，例如我们的redis_network**：

```yaml
services:
    db:
        image: redis:latest
        container_name: redis
        ports:
            - '6379:6379'
        networks:
            - network

networks:
    network:
        driver: bridge
        name: redis_network
```

这一次，当我们在设置我们的Web应用模板的时候，需要引用Redis现有网络：

```yaml
services:
    my_app:
        image: "web-app:latest"
        container_name: web-app
        ports:
            - "8080:8080"
        networks:
            - my-app

networks:
    my-app:
        name: redis_network
        external: true
```

## 3. 运行和检查容器

最后，我们可以使用[up](https://docs.docker.com/engine/reference/commandline/compose_up/)和[down](https://docs.docker.com/engine/reference/commandline/compose_down/)命令启动或停止我们的服务。

如果我们在服务定义中创建一个网络，我们需要首先启动该服务，就像我们例子中的Redis一样：

```shell
docker-compose -f docker-compose-redis-service.yaml up -d && docker-compose -f docker-compose-my-app-service.yaml up -d
```

我们可以[检查](https://docs.docker.com/engine/reference/commandline/inspect/)一个正在运行的容器以查看网络定义，例如我们的Redis服务：

```bash
docker inspect 5c7f8b28480b
```

我们将看到redis_network的条目，当我们为web-app执行相同的检查时，会得到相同的输出：

```yaml
"Networks": {
    "redis_network": {
        "IPAMConfig": null,
        "Links": null,
        "Aliases": [
            "redis",
            "4d23d918eb2c"
        ],
        "NetworkID": "e122aa15d5ad150a66d87f3145084520bde540447a14a73f446ec6ea0603aba9",
        "EndpointID": "8904a3389d0b20c6785884c702cb6ae1101522af1f99c079067171cbc9ca97e5",
        "Gateway": "172.28.0.1",
        "IPAddress": "172.28.0.2",
        "IPPrefixLen": 16,
        "IPv6Gateway": "",
        "GlobalIPv6Address": "",
        "GlobalIPv6PrefixLen": 0,
        "MacAddress": "02:42:ac:1c:00:02",
        "DriverOpts": null
    }
}
```

类似地，我们可以检查redis_network：

```shell
docker network inspect redis_network
```

这两个服务都属于同一个网络：

```yaml
"Containers": {
    "5c7f8b28480ba638ce993c6714841265c0a98d746b27205a756936bbe1850ac2": {
        "Name": "redis",
        "EndpointID": "238bb136d634100eccb7677e87ba07eb43f33be1dc320c795685230f04b809f9",
        "MacAddress": "02:42:ac:1c:00:02",
        "IPv4Address": "172.28.0.2/16",
        "IPv6Address": ""
    },
    "8584ce3bb2ab1cd3d182346c67179a3aa5e40c71e806c35cc4ce7ea91cae7902": {
        "Name": "web-app",
        "EndpointID": "9cf0e484e5af1baf968249c312489a83f57a194098a51652c3f6eac19ed0d557",
        "MacAddress": "02:42:ac:1c:00:03",
        "IPv4Address": "172.28.0.3/16",
        "IPv6Address": ""
    }
}
```

## 4. 总结

在本文中，我们介绍了如何通过同一网络连接多个Docker Compose服务，我们可以让他们加入现有网络，或者根据服务定义创建网络。

与往常一样，我们可以[在GitHub上找到案例的源]()docker-compose.yml文件。