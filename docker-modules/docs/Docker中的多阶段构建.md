减小docker[***镜像***](https://www.toolsqa.com/docker/docker-images/)大小是 Docker 中最具挑战性的事情之一，因为如果镜像大小增加，则很难维护镜像，最终也难以维护执行镜像的容器。当 Dockerfile 中的每条指令执行时，一个新层将添加到图像中。因此，为了优化 Dockerfiles 以便我们可以保持较小的图像大小，我们在 docker 中进行多阶段构建，使 Dockerfiles 易于阅读和维护。Docker 在其最新版本中引入了多阶段构建。在本文中，我们将详细讨论 Docker 中的多阶段构建，涵盖以下主题。

-   *Docker 中的单阶段构建场景有哪些问题？*
-   什么是 Docker 中的多阶段构建？
    -   *如何命名构建阶段？*
    -   *我们如何停止特定的构建阶段？*
    -   *使用外部图像作为“舞台”。*
    -   *如何在多阶段 Docker 构建中将前一个阶段用作新阶段？*
    -   *Docker 中多阶段构建的好处是什么？*
    -   *什么时候不使用多阶段构建？*

## Docker 中单阶段构建场景的问题

早些时候，应用程序通常将“[***构建器模式***](https://en.wikipedia.org/wiki/Builder_pattern)”与*Dockerfile*一起使用。我们将只有一个用于开发的*Dockerfile*，它包含构建应用程序所需的一切。然后我们精简了这个*Dockerfile*以用于生产，这样它只包含应用程序和运行它所需的东西。这种设计基于“*构建器模式*”。

使用*构建器模式*，*Dockerfile*如下所示：

```
FROM golang:1.7.3 
WORKDIR /go/src/github.com/alexellis/href-counter/ 
COPY app.go . 
RUN go get -d -v golang.org/x/net/html \ 
  && CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o app .
```

如果您注意到上面的*Dockerfile*，我们已经使用“ *&&* ”（*Bash 运算符）压缩了两个****RUN***命令。它帮助我们防止在图像中创建附加层。因此，保持图像尺寸较小。但这种设计容易出错且难以维护，尤其是当***RUN***命令数量增加时。我们也可能忘记包含“ ***\*** ”运算符，在这种情况下，每个图像都会占用本地计算机上的空间。

这种情况的补救方法是使用*多阶段构建*，我们将在接下来讨论。

## 什么是 Docker 中的多阶段构建？

*Docker中的****多阶段构建***是*我们在Dockerfile*中使用多个“ ***FROM*** ”语句的。*Dockerfile*中的每条 FROM 指令使用不同的基础并开始构建的新阶段。在多阶段构建中，我们可以有选择地将工件从一个阶段复制到另一个阶段，从而跳过最终图像中不需要的所有内容。让我们考虑以下*Dockerfile*。

```
Dockerfile :

FROM golang : 1 . 7 . 3

WORKDIR /go/src/github.com/alexellis/href-counter/

RUN go get -d -v golang.org/x/net/html

COPY app.go .

RUN CGO ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o app .




FROM alpine : latest

RUN apk - -no-ache  add  ca-certificates

WORKDIR /root/

COPY - -from=0  /g/src/github.com/alexellis/href-counter/app .

CMD ["./app"]
```

上面的文件是一个*多阶段构建*的例子。我们可以在这里看到我们有两个执行两个容器的*FROM*语句/指令，“ ***golang*** ”和“ ***alpine*** ”。因此我们只需要一个*Dockerfile*并运行“ docker ***build*** ”。

```
&  docker  build  -t  alexellis2/href-counter:latest .
```

在这里，我们不需要创建任何中间图像，也不需要将任何工件提取到本地机器。结果是一个微小的生产图像，复杂性显着降低。![Docker 多阶段构建](https://www.toolsqa.com/gallery/Docker/1-Docker%20Multi-Stage%20Builds.png)

那么上面的*Dockerfile*是如何工作的呢？

在上面的*Dockerfile*中，第二个“ ***FROM ”指令以“*** ***alpine:latest*** ”为基础开始一个新的构建阶段。语句“ ***COPY --from=0*** ”会将构建的工件从早期阶段复制到新阶段。请注意，我们没有在最终图像中包含 Go SDK 或任何其他中间工件。

### ***如何命名构建阶段？***

***默认情况下，阶段不会在多阶段构建中命名***。对于第一个“ *FROM ”指令，我们通常通过以 0（**零*）开头的整数来引用它们，依此类推。*但是，我们可以通过在FROM*指令中***添加“ AS<NAME>*** ”来命名每个阶段*然后我们可以在COPY*指令中使用这个阶段名称这样，即使我们稍后决定对*Dockerfile中的指令重新排序，* *COPY*也不会中断。

例如，在下面的代码中，我们将这个阶段命名为“ ***golang_builder*** ”。

```
FROM golang:1.7.3 AS golang_builder WORKDIR /go/src/github.com/alexellis/href-counter/ 
   RUN go get -d -v golang.org/x/net/html 
   COPY app.go . RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o app . 
FROM alpine:latest RUN apk --no-cache add ca-certificates WORKDIR /root/ 
  COPY --from=golang_builder /go/src/github.com/alexellis/href-counter/app . CMD ["./app"
]
```

因此，如上例所示，我们可以方便地命名多阶段构建中的各个阶段，并在以后使用这些阶段名称而不是阶段编号。

### ***我们如何停止特定的构建阶段？***

当一个*Dockerfile*有这么多阶段或语句时，我们可能不必构建一个包含所有阶段的完整*Dockerfile 。*我们还可以指定目标构建阶段并停止其他阶段。如以下命令所示。

```
$ docker build --target golang_builder -t alexellis2/href-counter:latest .
```

在这里，上面的命令使用我们之前展示的*Dockerfile*并*在名为 golang_builder 的阶段停止*。所以这里有几个场景，这个停止构建可能非常强大：

-   *调试特定的构建阶段。为此，我们在特定阶段停止构建。*
-   *我们可以在启用所有调试符号或工具的调试阶段和精益生产阶段使用它。*
-   *我们还可以在测试阶段使用它，在该阶段应用程序填充测试数据，并使用真实数据在不同阶段构建生产环境。*

### ***使用外部图像作为“舞台”。***

当涉及多阶段构建时，我们不仅限于从*Dockerfile中较早创建的阶段进行复制。*我们还可以从单独的图像复制，本地图像或本地可用的标签或全球 docker 注册表或使用标签 ID。我们使用指令“ ***COPY --from*** ”来实现它。如果需要，Docker 客户端然后拉取图像并从那里复制工件。例如，考虑以下命令：

```
COPY  - -from = nginx:latest  /etc/nginx.conf  /nginx.conf
```

在上面的命令中，我们从外部源复制图像。/etc/nginx/nginx.conf。

### ***如何在多阶段 Docker 构建中将前一个阶段用作新阶段？***

多阶段构建的另一个特点是我们可以从前一阶段停止的地方继续。***使用“ FROM*** ”指令时参考前一阶段完成。考虑以下多阶段构建*Dockerfile*：

```
FROM alpine:latest as builder
RUN apk --no-cache add build-base

FROM builder as build1
COPY source1.cpp source.cpp
RUN g++ -o /binary source.cpp

FROM builder as build2
COPY source2.cpp source.cpp
RUN g++ -o /binary source.cpp
```

在上面的*Dockerfile*中，我们有一个构建器阶段，这是第一个阶段。在后续的*FROM*指令中，我们从这个阶段开始，进一步使用*COPY*和*RUN*。

### ***Docker 中多阶段构建的好处是什么？***

在 Docker 中进行多阶段构建的一些好处如下：

1.  *多阶段构建是部署生产就绪应用程序的理想选择。*
2.  *多阶段构建仅适用于一个 Dockerfile。*
3.  *它允许我们构建更小的镜像，Dockerfile 将它们分成不同的构建阶段。*
4.  *我们有统一的语法要学习。*
5.  *多阶段构建在本地机器和 CI（持续集成）服务器上工作。*

### ***什么时候不使用多阶段构建？***

到目前为止，在本文中，我们已经讨论了使用多阶段构建的情况。虽然多阶段构建无疑为我们提供了跨构建和执行环境的一致性，但有一些方面我们应该考虑并决定“***不***”使用多阶段构建。我们在下面总结了其中的一些方面。

-   *假设我们想让我们的 Dockerfile 简单易读。在那种情况下，我们可能不会使用多阶段构建，尤其是在开发人员不习惯这种复杂性的开发环境中。这是因为 Docker 中的多阶段构建增加了 Dockerfile 的物理大小和逻辑组织。*
-   *当容器数量很少时，多阶段构建的优势微乎其微。多阶段构建只有在像 CI/CD 中那样有很多容器时才会有所不同。*

因此，考虑到我们使用的容器数量、Dockerfile 的复杂性等，我们应该尝试多阶段构建。

## 关键要点

-   *多阶段构建通常只使用一个 Dockerfile。在这个 Dockerfile 中，我们使用多个 FROM 语句来定义构建的各个阶段。*
-   *使用多阶段构建，我们可以限制我们创建的图像的大小。在单阶段构建中，随着每条指令的执行，一个新层被添加到图像中，使其变得庞大。*
-   *对于多阶段构建，我们可以命名特定阶段、在特定阶段停止构建、使用外部图像或在阶段之间切换。*