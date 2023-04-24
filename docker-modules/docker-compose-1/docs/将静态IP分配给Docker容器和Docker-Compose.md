## 1. 概述

当我们运行Docker容器时，它会使用IP地址连接到虚拟网络，出于这个原因，我们希望服务能够动态获取配置。但是，我们可能希望使用静态IP而不是自动IP分配。

在本教程中，我们将了解内置配置与手动为容器分配IP之间的区别。最后，我们演示一些带有测试的DockerCompose示例。

## 2. DHCP和DNS

让我们看看使用DHCP和DNS解析主机名称的[Docker]()内置IP分配给容器。

### 2.1 Docker如何分配IP

**Docker首先为每个容器分配一个[IP](https://docs.docker.com/config/containers/container-networking/#ip-address-and-hostname)，充当DHCP服务器。此外，还有多个DNS服务器**。

然后容器使用[dockerd](https://docs.docker.com/engine/reference/commandline/dockerd/)中的服务器处理DNS请求，该服务器识别同一内部网络上其他容器的名称。这样，容器可以在不知道其内部IP地址的情况下进行通信。尽管每次应用程序启动时内部IP地址可能不同，但由于dockerd中的内部 DNS服务器，容器仍然可以轻松地使用人类可读的名称进行连接。

**然后，dockerd将名称查找发送到[CoreDNS](https://coredns.io/)(来自[CNCF](https://www.cncf.io/))。最后，请求根据域名移动到主机**。

域docker.internal有一个副作用，它包括解析为当前主机的有效IP地址的DNS名称host.docker.internal。它允许容器联系那些主机服务，而不必担心硬编码IP地址。虽然不推荐，但它可以方便地用于开发目的。

### 2.2 网络示例

例如，我们可以为MySQL服务运行一个容器，下面是Docker Compose YAML文件定义：

```yaml
services:
    db:
        image: mysql:latest
        environment:
            - MYSQL_ROOT_PASSWORD=password
            - MYSQL_ROOT_HOST=localhost
        ports:
            - 3306:3306
        volumes:
            - db:/var/lib/mysql
        networks:
            - network

volumes:
    db:
        driver: local

networks:
    network:
        driver: bridge
```

像往常一样，我们运行我们的容器：

```shell
docker-compose up -d
```

下面使用[jq]()的格式语法从容器的角度检查网络来获取JSON输出：

```bash
docker inspect --format='{{json .NetworkSettings.Networks}}' 2d3f4c69a213 | jq .
```

Docker Compose根据当前目录分配网络名称。例如，如果我们在project目录中，我们可以看到类似下面的输出：

```json
{
    "project_network": {
        "IPAMConfig": null,
        "Links": null,
        "Aliases": [
            "project-db-1",
            "db",
            "2d3f4c69a213"
        ],
        "NetworkID": "39ffbd8155d11ba03d0b548307f549f06790fe045e121a6d862b070d4fb67fa7",
        "EndpointID": "0eba235239b06f7e0cb5065b7f2ebd83e7d227f8cfad4df8de73260472737500",
        "Gateway": "172.19.0.1",
        "IPAddress": "172.19.0.2",
        "IPPrefixLen": 16,
        "IPv6Gateway": "",
        "GlobalIPv6Address": "",
        "GlobalIPv6PrefixLen": 0,
        "MacAddress": "02:42:ac:13:00:02",
        "DriverOpts": null
    }
}
```

容器从网络创建的子网中获取私有172.19.0.2IP地址。

**最重要的是，我们可以看到有关IPAMConfig的信息，即IP地址管理。当我们静态分配IP时，这将是相关的**。

现在，我们可以检查网络：

```shell
docker inspect network project_network
```

这一次，我们对网络有了更好的了解：

```json
[
    {
        "Name": "project_network",
        "Id": "39ffbd8155d11ba03d0b548307f549f06790fe045e121a6d862b070d4fb67fa7",
        "Created": "2022-09-09T16:19:26.27396468+02:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.19.0.0/16",
                    "Gateway": "172.19.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "2d3f4c69a2139dea9089a6d42907fdc085282c5df176b39bf7c20f5d0780179d": {
                "Name": "project-db-1",
                "EndpointID": "7447fe2550afb3f980f36449673724e9ed6dd16f41a085cc20ada3074a0d8e54",
                "MacAddress": "02:42:ac:13:00:02",
                "IPv4Address": "172.19.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {
            "com.docker.compose.network": "network",
            "com.docker.compose.project": "project",
            "com.docker.compose.version": "2.10.2"
        }
    }
]
```

值得注意的是，Docker Compose[网络](https://docs.docker.com/compose/networking/)从版本2开始可用。

## 3. 静态IP

了解更多有关自动IP分配的信息后，我们现在将创建网络的子网，然后我们可以为我们的服务分配我们喜欢的IP。

### 3.1 分配静态IP

如果我们使用DockerCLI，首先通过创建子网来实现此目的：

```bash
docker network create --subnet=10.5.0.0/16 mynet
```

然后，我们使用静态IP运行容器，同时使用MySQL服务：

```bash
docker run --net mynet --ip 10.5.0.1 -p 3306:3306 --mount source=db,target=/var/lib/mysql -e MYSQL_ROOT_PASSWORD=password mysql:latest
```

下面是一个使用Docker Compose的完整示例：

```yaml
services:
    db:
        container_name: mysql_db
        image: mysql:latest
        environment:
            - MYSQL_ROOT_PASSWORD=password
            - MYSQL_ROOT_HOST=10.5.0.1
        ports:
            - 3306:3306
        volumes:
            - db:/var/lib/mysql
            - ./init.sql:/docker-entrypoint-initdb.d/init.sql
        networks:
            network:
                ipv4_address: 10.5.0.5

volumes:
    db:
        driver: local

networks:
    network:
        driver: bridge
        ipam:
            config:
                -   subnet: 10.5.0.0/16
                    gateway: 10.5.0.1
```

我们已经通过ipam关键字定义了[网络](https://docs.docker.com/compose/compose-file/compose-file-v3/#networks)的子网，并为服务分配了一个IPv4地址。为了进行更改，我们使用了 10.5.0.5。172.\*和 10.\*IP地址通常用于专用网络。我们还可以使用[IPv6](https://docs.docker.com/config/daemon/ipv6/)地址，它的地址长度为128位，由于效率更高，它将取代IPv4。

按照建议，我们将网关地址分配给数据库主机MYSQL_ROOT_HOST。

最后，我们添加一个SQL脚本来创建用户、数据库和表：

```sql
CREATE DATABASE IF NOT EXISTS test;
CREATE USER 'db_user'@'10.5.0.1' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'db_user'@'10.5.0.1' WITH GRANT OPTION;
FLUSH PRIVILEGES;

use test;

CREATE TABLE IF NOT EXISTS TEST_TABLE(id int, name varchar(255));

INSERT INTO TEST_TABLE
VALUES (1, 'TEST_1');
INSERT INTO TEST_TABLE
VALUES (2, 'TEST_2');
INSERT INTO TEST_TABLE
VALUES (3, 'TEST_3');
```

我们希望只允许用户访问该特定地址的数据库。

容器启动后，我们可以使用[docker ps](https://docs.docker.com/engine/reference/commandline/ps/)查看它的定义：

```shell
CONTAINER ID   IMAGE          COMMAND                  CREATED         STATUS         PORTS                                                  NAMES
97812e199512   mysql:latest   "docker-entrypoint.s…"   7 minutes ago   Up 7 minutes   0.0.0.0:3306->3306/tcp, :::3306->3306/tcp, 33060/tcp   mysql_db
```

现在可以通过输入密码连接到数据库，我们使用容器名称或ID，因为它们解析为DNS的别名：

```shell
mysql --host=mysql_db -u db_user -p
```

现在，使用status命令，我们可以测试我们的MySQL主机是否解析为容器ID：

```shell
Connection id:          10
Current database:       test
Current user:           db_user@10.5.0.1
SSL:                    Not in use
Current pager:          stdout
Using outfile:          ''
Using delimiter:        ;
Server:                 MySQL
Server version:         8.0.30 MySQL Community Server - GPL
Protocol version:       10
Connection:             97812e199512 via TCP/IP
Server characterset:    utf8mb4
Db     characterset:    utf8mb4
Client characterset:    utf8mb3
Conn.  characterset:    utf8mb3
TCP port:               3306
```

### 3.2 与内置DockerIP管理的区别

让我们检查一下容器，在静态IP的情况下，我们可以看到IPAM配置现在有一个IPv4地址：

```json
{
    "project_network": {
        "IPAMConfig": {
            "IPv4Address": "10.5.0.5"
        },
        "Links": null,
        "Aliases": [
            "project_db",
            "db",
            "122c0c6bfcf9"
        ],
        "NetworkID": "7ac7a1d9e33dffc65bc867aee4db04b9b8fecaeb3bbb91c74c2f72e4611c6955",
        "EndpointID": "84145191a0327b777b6a31bacb2a0260d9a31e8c22cbfca1923775b3649b1d7e",
        "Gateway": "10.5.0.1",
        "IPAddress": "10.5.0.5",
        "IPPrefixLen": 16,
        "IPv6Gateway": "",
        "GlobalIPv6Address": "",
        "GlobalIPv6PrefixLen": 0,
        "MacAddress": "02:42:0a:05:00:05",
        "DriverOpts": null
    }
}
```

从容器的角度来看，这是主要的区别。**如果我们需要一个静态的私有IP地址，我们应该考虑是否需要使用它**。大多数时候，我们希望一个静态IP从另一个容器或主机与一个容器通信，Docker的内置网络已经可以处理这个问题。

但是，我们可能希望手动指定一个私有IP地址，例如，用于直接从主机访问容器。

值得注意的是使用[Docker Swarm](https://docs.docker.com/engine/swarm/networking/)自定义网络的可能性。

## 4. 总结

在本文中，我们介绍了Docker如何管理IP分配以及如何向容器添加静态地址，并演示了使用或不使用静态IP运行 MySQL服务的Docker Compose配置示例。