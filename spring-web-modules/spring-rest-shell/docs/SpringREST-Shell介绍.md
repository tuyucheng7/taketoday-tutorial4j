## 1. 概述

在本文中，我们将了解Spring REST Shell及其一些特性。

这是一个Spring Shell扩展，所以我们建议先阅读[它](https://www.baeldung.com/spring-shell-cli)。

## 2. 简介

Spring REST Shell是一个命令行shell，旨在促进使用符合Spring HATEOAS的REST资源。

我们不再需要使用curl等工具在 bash中操作URL。Spring REST Shell提供了一种更方便的与REST资源交互的方式。

## 3. 安装

如果我们使用带有Homebrew的macOS机器，我们可以简单地执行下一个命令：

```bash
brew install rest-shell
```

对于其他操作系统的用户，我们需要从[官方GitHub项目页面](https://github.com/spring-projects/rest-shell)下载一个二进制包，解压并找到一个可执行文件来运行：

```bash
tar -zxvf rest-shell-1.2.0.RELEASE.tar.gz
cd rest-shell-1.2.0.RELEASE
bin/rest-shell
```

另一种选择是下载源代码并执行Gradle任务：

```bash
git clone git://github.com/spring-projects/rest-shell.git
cd rest-shell
./gradlew installApp
cd build/install/rest-shell-1.2.0.RELEASE
bin/rest-shell
```

如果一切设置正确，我们将看到以下问候语：

```bash
 ___ ___  __ _____  __  _  _     _ _  __    
| _ \ __/' _/_   _/' _/| || |   / / | \ \   
| v / _|`._`. | | `._`.| >< |  / / /   > >  
|_|_\___|___/ |_| |___/|_||_| |_/_/   /_/   
1.2.1.RELEASE

Welcome to the REST shell. For assistance hit TAB or type "help".
http://localhost:8080:>
```

## 4. 开始

我们将使用已经为[另一篇文章开发](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-rest-shell)的API，localhost:8080用作基本URL。

以下是公开端点的列表：

-   GET /articles：获取所有Articles
-   GET /articles/{id}：通过id获取文章
-   GET /articles/search/findByTitle?title={title}：按标题获取文章
-   GET /profile/articles：获取文章资源的配置文件数据
-   POST /articles：创建一个新的文章，提供正文

Article类具有三个字段：id、title和content。

### 4.1 创建新资源

让我们添加一篇新文章，我们将使用post命令传递带有data参数的JSON字符串。

首先，我们需要遵循与我们要添加的资源关联的URL。以下命令采用相对URI，将其与baseUri连接并将结果设置为当前位置：

```bash
http://localhost:8080:> follow articles
http://localhost:8080/articles:> post --data "{title: "First Article"}"
```

命令的执行结果将是：

```bash
< 201 CREATED
< Location: http://localhost:8080/articles/1
< Content-Type: application/hal+json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Sun, 29 Oct 2017 23:04:43 GMT
< 
{
    "title" : "First Article",
    "content" : null,
    "_links" : {
        "self" : {
            "href" : "http://localhost:8080/articles/1"
        },
        "article" : {
            "href" : "http://localhost:8080/articles/1"
        }
    }
}
```

### 4.2 发现资源

现在，当我们获得一些资源时，让我们找出它们。我们将使用discover命令显示当前URI的所有可用资源：

```bash
http://localhost:8080/articles:> discover

rel        href                                  
=================================================
self       http://localhost:8080/articles/       
profile    http://localhost:8080/profile/articles
article    http://localhost:8080/articles/1
```

了解资源URI，我们可以使用get命令获取它：

```bash
http://localhost:8080/articles:> get 1

> GET http://localhost:8080/articles/1

< 200 OK
< Content-Type: application/hal+json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Sun, 29 Oct 2017 23:25:36 GMT
< 
{
    "title" : "First Article",
    "content" : null,
    "_links" : {
        "self" : {
            "href" : "http://localhost:8080/articles/1"
        },
        "article" : {
            "href" : "http://localhost:8080/articles/1"
        }
    }
}
```

### 4.3 添加查询参数

我们可以使用–params参数将查询参数指定为JSON片段。

让我们按给定的标题获取一篇文章：

```bash
http://localhost:8080/articles:> get search/findByTitle \
> --params "{title: "First Article"}"

> GET http://localhost:8080/articles/search/findByTitle?title=First+Article

< 200 OK
< Content-Type: application/hal+json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Sun, 29 Oct 2017 23:39:39 GMT
< 
{
    "title" : "First Article",
    "content" : null,
    "_links" : {
        "self" : {
            "href" : "http://localhost:8080/articles/1"
        },
        "article" : {
            "href" : "http://localhost:8080/articles/1"
        }
    }
}
```

### 4.4 设置标题

名为headers的命令允许在会话范围内管理标头——每个请求都将使用这些标头发送。标头集采用–name和–value参数来确定标头。

我们将添加一些标头并发出包含这些标头的请求：

```bash
http://localhost:8080/articles:>
  headers set --name Accept --value application/json

{
    "Accept" : "application/json"
}

http://localhost:8080/articles:>
  headers set --name Content-Type --value application/json

{
    "Accept" : "application/json",
    "Content-Type" : "application/json"
}

http://localhost:8080/articles:> get 1

> GET http://localhost:8080/articles/1
> Accept: application/json
> Content-Type: application/json
```

### 4.5 将结果写入文件

将HTTP请求的结果打印到屏幕上并不总是可取的。有时，我们需要将结果保存在文件中以供进一步分析。

–output参数允许执行这样的操作：

```bash
http://localhost:8080/articles:> get search/findByTitle \
> --params "{title: "First Article"}" \
> --output first_article.txt

>> first_article.txt
```

### 4.6 从文件中读取JSON

通常，JSON数据太大或太复杂，无法使用–data参数通过控制台输入。

此外，我们可以直接在命令行中输入的JSON数据的格式也有一些限制。

–from参数提供了从文件或目录中读取数据的可能性。

如果该值是一个目录，shell将读取每个以“.json”结尾的文件，并使用该文件的内容执行POST或PUT。

如果参数是文件，则shell将从该文件加载文件和POST/PUT数据。

让我们从文件second_article.txt创建下一篇文章：

```bash
http://localhost:8080/articles:> post --from second_article.txt

1 files uploaded to the server using POST
```

### 4.7 设置上下文变量

我们还可以在当前会话上下文中定义变量。命令var分别定义了获取和设置变量的get和set参数。

与headers类比，参数–name和–value用于给出新变量的名称和值：

```bash
http://localhost:8080:> var set --name articlesURI --value articles
http://localhost:8080/articles:> var get --name articlesURI

articles
```

现在，我们将打印出上下文中当前可用变量的列表：

```bash
http://localhost:8080:> var list

{
    "articlesURI" : "articles"
}
```

确保我们的变量已保存后，我们将使用它和以下命令切换到给定的URI：

```bash
http://localhost:8080:> follow #{articlesURI}
http://localhost:8080/articles:> 
```

### 4.8 查看历史

我们访问的所有路径都被记录下来。命令历史按时间顺序显示这些路径：

```bash
http://localhost:8080:> history list

1: http://localhost:8080/articles
2: http://localhost:8080
```

每个URI都与一个可用于转到该URI的数字相关联：

```bash
http://localhost:8080:> history go 1
http://localhost:8080/articles:>
```

## 5. 总结

在本教程中，我们重点介绍了Spring生态系统中一个有趣且罕见的工具-命令行工具。