到目前为止，在我们关于[***Docker 容器***](https://www.toolsqa.com/docker/docker-containers/)的文章中，我们一次只处理***一个容器***。然而，[***Docker***](https://www.toolsqa.com/docker/introduction-to-docker-and-docker-architecture/)为我们提供了处理和运行多个容器的灵活性，以及使用名为***Docker Compose***的工具来处理它们。因此，在本文中，我们将详细讨论这一点。我们将涵盖以下主题：

-   什么是 Docker 组合？
    -   *安装 Docker Compose*
-   *它的主要特点是什么？*
-   Docker-Compose 命令
    -   *如何在 Docker-Compose 中设置环境变量？*
    -   *.env 文件的语法规则*
-   *如何使用 Docker 组合？*
-   我们应该在哪里使用 Docker Compose？
    -   *它应该在生产中使用吗？*
-   Docker-compose 配置：多个组合文件
    -   *多个组合文件*
    -   *使用“extends”扩展服务*
-   *Dockerfile 和 Docker Compose 有什么区别？*

## 什么是 Docker 组合？

它是一种定义和执行具有多个（*多个*）容器的 Docker 应用程序的工具。此外，e 使用配置文件“ [***YAML***](https://en.wikipedia.org/wiki/YAML) ”和 compose 来配置 Docker 应用程序的服务。此外，当我们使用 YAML 时，我们可以通过一条命令创建和启动所有应用程序服务。

***Docker Compose 将多个容器作为单一服务运行***。因此，例如，如果应用程序需要*MySQL*和*NUnit ，我们可以使用**YAML*文件将这两个容器作为服务同时启动此外，我们不需要单独启动每个容器。![1-Docker Compose概览.png](https://www.toolsqa.com/gallery/Docker/1-Docker%20Compose%20overview.png)

*因此，如果 Docker 运行单个容器应用程序，我们可以使用Docker Compose*将其扩展为包含多个容器，如上所示。值得注意的是，它在所有环境中都能有效工作，包括开发、生产、测试、暂存和[***CI（持续集成）***](https://www.toolsqa.com/software-testing/continuous-integration/)工作流程。

应用程序的运行分为三个步骤，总结如下：

1.  *首先，在 Dockerfile 中定义应用程序环境。*
2.  *其次，创建一个“ **docker-compose.yml** ”文件，定义要在隔离环境中一起运行的服务。*
3.  *最后，执行/运行 docker-compose up。因此，该工具启动并运行整个应用程序。*

### ***安装 Docker Compose***

我们必须先满足先决条件，然后才能将*其*安装到我们的机器上。因此，此先决条件如下所示：

1.  首先，我们应该在安装之前在任何机器上安装*Docker 引擎*，因为它依赖于*Docker 引擎*来执行任何有意义的任务。

***因此，一旦我们成功安装了Docker Engine，我们就可以继续安装compose工具了。***

对于本文的范围，我们将讨论在 Linux 操作系统上的安装。首先，在 Linux 机器上执行以下步骤来安装 Compose。

1.  首先，使用以下命令下载*Compose*的当前稳定版本。

```
docker curl -L "https://github.com/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

随后，以下/下面的屏幕截图显示了上述命令的执行。![2-下载compose.png](https://www.toolsqa.com/gallery/Docker/2-download%20compose.png)

请注意下载详细信息。此外，工具下载后，我们必须使用下一个命令提供“***执行***”权限。

1.  其次，更改二进制文件的权限以使其可执行。

```
docker chmod +x /usr/local/bin/docker-compose
```

此外，我们不必显式安装此工具。换句话说，一旦我们下载了该工具并赋予它执行权限，我们就可以使用它来执行各种任务。因此，一旦我们授予权限，我们就可以使用下面步骤 3 中给出的以下命令来验证安装。

1.  第三，使用以下命令验证安装：

```
$ docker-compose --version
```

此外，上述步骤的屏幕截图如下。![3-Docker Compose安装完成.png](https://www.toolsqa.com/gallery/Docker/3-Docker%20Compose%20install%20complete.png)

从上面的截图可以看出，一旦我们下载并赋予Compose工具执行权限，就可以使用了。因此，接下来，我们使用命令“ ***sudo docker-compose --version*** ”验证了安装。此外，此命令的输出显示了 Compose 工具的版本和内部版本号。因此，它表示安装成功。

## Docker Compose 的主要特点是什么？

以下是它的一些特点：

-   *首先，Compose 有助于在单个主机上拥有多个隔离的环境。因此，为此，Compose 使用项目名称。默认情况下，项目名称是基目录的名称。但是，我们可以使用 COMPOSE_PROJECT_NAME 环境变量或“ **-p** ”命令行选项将其设置为任何其他自定义名称。*
-   *此外，它在我们创建容器时会保留卷数据。如果 Compose 在之前的运行中找到了容器，它会将旧容器数据复制到新容器中。*
-   *此外，使用 Compose，我们仅重新创建那些已更改的容器。此外，它缓存配置以创建新容器，以便当我们重新启动未更改的服务时，它会重用现有容器。*
-   *此外，它还支持变量和在环境之间移动组合。Compose 支持 Compose 文件中的变量，我们可以使用它来自定义各种环境或不同用户的组合。*

最后，既然我们已经讨论了一些基本功能，那么现在让我们转到命令部分。

## Docker-Compose 命令

我们使用“ ***docker-compose*** ”命令在 Docker 中定义和运行具有多个容器的应用程序。此外，“ *docker-compose* ”命令的一般语法如下：

```
docker-compose [-f <arg>...] [options] [COMMAND] [ARGS...]
```

这里的***[options]***指定了可以与“ *docker-compose* ”一起使用的各种选项，以促进可读输出。此外，***[COMMAND]***指定了我们可以使用 Compose 执行或运行应用程序的各种命令。

下表描述了我们与*docker-compose*命令一起使用的*[options] 。*

| ***选项***                 | ***描述***                                                   |
| -------------------------- | ------------------------------------------------------------ |
| -f, --文件文件             | 如果我们有备用的撰写文件（*默认：docker-compose.yml*）       |
| -p, --project-name 名称    | 用于指定备用项目名称（*默认：目录名称*）                     |
| --冗长                     | 执行命令的更多输出                                           |
| --日志级别                 | 设置日志级别（*DEBUG、INFO、WARNING、ERROR、CRITICAL*）      |
| --no-ansi                  | 不要打印 ANSI 控制字符。                                     |
| -v, --版本                 | 显示版本并退出                                               |
| -H, --主机主机             | 要连接到的守护程序套接字                                     |
| --tls                      | 使用 TLS；（*由 –tlsverify 暗示*）                           |
| --tlscacert CA_PATH        | 信任仅由该 CA 签名的证书                                     |
| --tlscert CLIENT_CERT_PATH | TLS 证书文件路径                                             |
| --tlskey TLS_KEY_PATH      | TLS 密钥文件路径                                             |
| --tlsverify                | 使用 TLS 并验证远程                                          |
| --skip-hostname-check      | 跳过守护程序的主机名检查客户端证书中指定的名称               |
| --项目目录路径             | 用于指定备用工作目录（*默认：Compose 文件的路径*）           |
| - 兼容性                   | 当 set = Compose 尝试将 v3 文件中的部署密钥转换为其非 Swarm 等效项时。 |

同样，下表显示了与*docker-compose一起使用的各种**[commands]*。

| ***不*** | ***命令*** | ***描述***                    |
| -------- | ---------- | ----------------------------- |
| 1        | 建造       | 构建/重建服务                 |
| 2        | 捆         | 从 Compose 文件生成 Docker 包 |
| 3        | 配置       | 验证并查看 Compose 文件       |
| 4        | 创造       | 创建服务                      |
| 5        | 下         | 停止/删除容器、网络、图像和卷 |
| 6        | 事件       | 从容器接收实时事件            |
| 7        | 执行       | 在正在运行的容器中执行命令    |
| 8        | 帮助       | 获取指定命令的帮助            |
| 9        | 图片       | 列出图像                      |
| 10       | 杀         | 杀死容器                      |
| 11       | 日志       | 查看容器的输出                |
| 12       | 暂停       | 暂停服务                      |
| 13       | 港口       | 打印端口绑定的公共端口        |
| 14       | 附言       | 列出容器                      |
| 15       | 拉         | 拉取服务镜像                  |
| 16       | 推         | 推送服务图片                  |
| 17       | 重新开始   | 重启服务                      |
| 18       | R M        | 移除停止的容器                |
| 19       | 跑         | 运行一次性命令                |
| 20       | 规模       | 设置服务的容器数量            |
| 21       | 开始       | 启动服务                      |
| 22       | 停止       | 停止服务                      |
| 23       | 最佳       | 显示正在运行的进程            |
| 24       | 取消暂停   | 取消暂停服务                  |
| 25       | 向上       | 创建并启动容器                |
| 26       | 版本       | 显示 Docker-Compose 版本信息  |

例如，让我们执行“ ***--version*** ”命令，给出以下输出。

```
docker-compose version 1.25.5, build 8a1c60f6
```

### ***如何在 Docker Compose 中设置环境变量？***

Compose 还处理***环境变量***。因此，我们可以在 compose 文件中替换环境变量，甚至可以通过“ *docker-compose* ”命令使用环境变量。

例如，假设我们设置了以下环境变量。

```
$ cat .env
TAG=v1.7
```

环境变量为***TAG***，其值为 v1.7 与上述命令。

此外，我们可以在*YAML文件中使用这个**$TAG*环境变量，如下所示：

```
version: '2'
services:
  web:
    image: "webapp:${TAG}"
```

此外，这意味着当执行“ *docker-compose up ”命令时，将下载版本为 1.7（* *${TAG} 的值*）的 Web 服务定义的 Web 应用程序。

***注意**：Shell 中环境变量的值优先于 env 文件中该变量的值。*

*此外，我们还可以传递多个环境变量，通过带有“ env-file ”选项的“* *services* ”容器从外部文件一次组合一个文件，如下所示。

```
web:
  env_file:
    - webapp-variables.env
```

这里我们传递了文件“ *webapp-variables.env* ”，其中包含多个环境变量。

或者，我们也可以使用***-e 选项****通过“ docker-compose run* ”命令在容器上设置环境变量。

```
docker-compose run -e DEBUG=1 web python consoleapp.py
```

因此，当我们使用各种选项设置环境变量时，Compose 使用以下优先级来决定使用哪个值：

1.  *撰写档案*
2.  *外壳环境变量*
3.  *环境文件*
4.  *文件*
5.  *变量未定义*

除了上述之外，Compose 还有一些内置的环境变量来定义其命令行行为。因此，您可以参考[***Compose CLI 环境变量***](https://docs.docker.com/compose/reference/envvars/)。

### ***.env 文件的语法规则***

在*Docker compose中，我们可以在扩展名为“* ***.env*** ”的环境文件中声明默认环境变量，如上例所示。请注意，我们通常将此文件放在当前工作目录中。

使用“ *.env* ”文件时，我们应确保将以下语法规则应用于“ *.env* ”文件：

-   *“env”文件中的每一行都应采用 VAR=VAL 格式。*
-   *此外，以“#”开头的行将作为注释处理，因此会被忽略。*
-   *此外，我们忽略空行。*
-   *此外，不处理引号，这意味着它们是 VAL 的一部分。*

在 Compose 中，我们可以为 Compose 文件中的变量替换定义我们的环境变量。此外，我们还可以使用来定义以下 CLI 变量：

-   *COMPOSE_API_VERSION*
-   *COMPOSE_CONVERT_WINDOWS_PATHS*
-   *COMPOSE_FILE*
-   *COMPOSE_HTTP_TIMEOUT*
-   *COMPOSE_PROFILES*
-   *COMPOSE_PROJECT_NAME*
-   *COMPOSE_TLS_VERSION*
-   *DOCKER_CERT_PATH*
-   *DOCKER_HOST*
-   *DOCKER_TLS_VERIFY*

在运行时，环境中的值会覆盖“ *.env* ”文件中定义的值。此外，在命令执行期间通过命令行参数传递的值会覆盖“ *.env* ”文件值。

## 如何使用 Docker 组合？

现在安装已经完成，我们可以使用它来运行应用程序了。随后，这是一个概述如下的三步过程：

1.  *首先，创建一个定义应用程序环境的“Dockerfile”以在任何地方重现该环境。*
2.  *其次，创建一个“docker-compose.yml”文件来定义构成应用程序的服务。它有助于服务在隔离环境中一起运行。*
3.  *最后，执行“docker-compose up”命令启动 Compose 并立即运行整个应用程序。*

现在让我们举个例子来执行上面的步骤，这样我们就可以使用Docker Compose了。在这里，我们将使用 compose 来设置一个*带有 MySQL 数据库的 WordPress 应用程序*。因此，为此，我们将同时使用 Compose 启动容器*WordPress*和*MySQL*。此示例仅用于演示目的。因此，我们将只在本地环境中运行它。此外，我们将只编写***docker-compose.yml***文件，然后使用“ docker *-compose up* ”命令启动这些容器。

因此，让我们从示例开始。首先，我们将创建一个文件夹，比如*my_wordpressApp*，然后切换到该目录。

```
mkdir my_wordpressApp
cd my_wordpressApp
```

完成后，我们将使用任何编辑器（如“ *vi* ”或“ *vim ”）创建一个**docker-compose.yml*文件，并更新其中的以下内容。

```
version: '3.3'

services:
   db:
     image: mysql:5.7
     volumes:
       - db_data:/var/lib/mysql
     restart: always
     environment:
       MYSQL_ROOT_PASSWORD: somewordpress
       MYSQL_DATABASE: wordpress
       MYSQL_USER: wordpress
       MYSQL_PASSWORD: wordpress

   wordpress:
     depends_on:
       - db
     image: wordpress:latest
     ports:
       - "8000:80"
     restart: always
     environment:
       WORDPRESS_DB_HOST: db:3306
       WORDPRESS_DB_USER: wordpress
       WORDPRESS_DB_PASSWORD: wordpress
       WORDPRESS_DB_NAME: wordpress
volumes:
    db_data: {}
```

如上面的文件所示，我们有一个 MySQL 5.7 镜像和一个 WordPress 镜像（*最新版本*）的数据库服务。此外，我们使用环境变量来指定数据库字符串（*MYSQL_ROOT_PASSWORD、MYSQL_DATABASE 等*）以及指定 WordPress 容器详细信息（*WORDPRESS_DB_HOST、WORDPRESS_DB_USER 等*）。随后，现在我们将执行以下命令。

```
docker-compose up -d
```

此外，我们可以在下面的屏幕截图中看到此命令的输出。![4-docker-compose up output.png](https://www.toolsqa.com/gallery/Docker/4-docker-compose%20up%20output.png)

上图下载并启动了两个容器，如上图所示。因此，我们将执行以下命令来验证 WordPress 和 MySQL 容器是否已启动并正在运行。

```
docker-compose ps
```

执行此命令后，我们将获得以下输出。![5-运行容器.png](https://www.toolsqa.com/gallery/Docker/5-Running%20containers.png)

因此，使用 Docker Compose，我们可以一次下载并设置多个容器，而无需一个一个地进行。此外，有关使用 Compose 的更多应用程序示例，请参阅[***Awesome Compose Repository***](https://github.com/docker/awesome-compose)。

## 我们应该在哪里使用 Docker Compose？

***它通过 YAML 文件促进了多容器应用程序的使用***。在这里我们可以设置所需的容器数量、它们的构建、存储设计等。此外，我们可以使用一组命令配置、构建、运行所有这些容器。因此它减轻了 Docker 维护多个容器的负担。

因此，***当我们拥有多容器应用程序时，Compose 是***开发、测试、持续集成工作流、暂存环境等的绝佳替代方案。此外，我们可以将 compose 视为可以在这些应用程序中使用的“***自动化多容器工作流***”应用开发阶段。

### ***应该在生产中使用 Docker Compose 吗？***

与在开发环境中一样，使用单个服务器部署应用程序是最简单的方法。但是如果我们想要扩展应用程序，我们可以在***Docker Swarm***集群上使用 Docker Compose。此外，要在生产环境中使用 Compose，我们需要对应用程序配置进行特定更改。随后，这些变化包括：

1.  *我们必须删除应用程序代码的任何卷绑定，以便代码无法从外部更改并保留在容器内。*
2.  *此外，我们可能必须绑定到主机上的不同端口。*
3.  *此外，我们需要设置新的环境变量或以不同方式设置现有环境变量，例如减少日志记录的冗长程度、删除调试信息等。除此之外，我们还必须为应用程序使用的任何外部服务设置额外的环境变量。*
4.  *我们还需要避免应用程序停机。在这种情况下，我们可能必须指定像 restart: always 这样的策略。*
5.  *此外，有时我们可能必须专门为生产中的应用程序添加一些额外的服务。在这种情况下，我们必须更改我们的应用程序配置。*

要指定上述更改，除了*docker-compose.yml*文件之外，我们可能还需要一个单独的*production.yml 。*此外，文件*production.yml*文件将仅具有特定于生产的配置。因此我们可以执行这两个文件，以便我们可以在生产环境中设置所有配置。随后，要运行这两个 YML 文件，我们可以提供以下命令。

```
docker-compose -f docker-compose.yml -f production.yml up -d
```

以下部分讨论使用多个撰写文件和***extends***关键字。

## Docker-compose 配置：多个组合文件

上面，我们讨论了***docker-compose。yaml***文件。我们可以有多个组合文件并共享配置。此外，我们可以使用两种方法来共享配置。

1.  *我们可以通过使用多个 Compose 文件来扩展整个 Compose 文件。*
2.  *除了上述之外，我们还可以使用“ **extends** ”字段来扩展个别服务。（这仅适用于最高 2.1 的 Compose 文件版本）*

随后，让我们现在讨论这两种方法。

### ***多个撰写文件***

我们知道 compose 读取包含基本配置的文件“ *docker-compose.yml ”。*除此之外，还有一个可选的“ *docker-override.yml* ”文件，如果存在，Compose 也会读取该文件。覆盖文件包含要覆盖现有服务或新服务的配置。此外，我们可以通过使用多个 Compose 文件来为不同的工作流或环境定制一个 Compose 应用程序。

Compose 使用“ ***-f*** ”选项来使用多个文件，并将基本 Compose 文件作为第一个文件。此外，compose 会按照我们在命令行中指定的顺序合并这些不同的文件。此外，我们必须确保文件中的所有路径都是相对于基本组合文件的。覆盖文件的一个显着特征是它们不需要是有效的 Compose 文件。此外，它们通常包含定义配置的小片段。因此，我们必须指定所有相对于基础文件的路径。

如果有多个 compose 文件，即*docker-compose.yml*和*docker-compose.prod.yml*，那么我们可以使用“ docker *-compose command* ”，如下所示：

```
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

此外，它将部署这些文件中指定的所有服务。

***注意**：我们使用 docker-compose.prod.yml 主要是为了在生产环境中部署服务。*

### ***使用“extends”扩展服务***

我们可以使用“ ***extends*** ”关键字来启用在各种文件和/或项目之间共享配置。例如，当我们有多个使用一组通用配置的服务时，我们在 docker-compose 中使用“ ***extends ”关键字。***因此，使用“ *extends* ”关键字，我们在一个地方定义了一组通用的服务配置，以便从任何地方引用它们。

此外，请考虑以下*docker-compose.yml*。这里我们使用了“ *extends* ”关键字。

```
web :

  extends :

         file : common-services.yml

         service : webapp
```

在此文件中，extends 关键字指示 Compose应重用文件“ ***common-services.yml*** *”中为“ webapp* ”服务定义的配置。

***注意**：早期的 Compose 文件格式直到 Compose 文件版本 2.1 都支持“ **extends ”关键字。**但是，Compose 版本 3.x 不支持它。*

## Dockerfile 和 Docker Compose 有什么区别？

下表列出了两者之间的一些常见差异：

| ***文件***                                                   | ***码头工人组成***                                           |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 使用 Dockerfile 的 Docker 管理单个容器。                     | 一个 Compose 管理多个容器。                                  |
| Dockerfile 创建[***Docker 镜像***](https://www.toolsqa.com/docker/docker-images/)。 | Compose 用于一次创建应用程序、Web 和数据库容器。             |
| Dockerfile 是一个简单的文本文件，其中包含用户可以调用以组装图像的命令。 | Compose 是一个用于定义和运行具有多个容器的 Docker 应用程序的工具。 |
| 我们可以从 Dockerfile 中执行 run、from、entry point 等命令来操作镜像和容器。 | Compose 使用 docker-compose.yml 来定义构成我们应用程序的服务，以便在隔离环境中一起运行它们。 |

## 关键要点

-   *我们讨论过 Compose 是一个帮助我们处理多个容器的工具。此外，当我们想要扩展 Docker 应用程序以使用多个容器时，我们会使用 Compose。*
-   *此外，它还维护一个组合文件“docker-compose.yml”，用于定义多个容器。此外，我们使用“docker-compose up”执行此文件，它定义并创建在 compose 文件中定义的容器。*