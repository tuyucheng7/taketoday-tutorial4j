## 1. 概述

Ktor 是一个框架，用于使用强大的 Kotlin 编程语言在连接的系统中构建异步服务器和客户端。它有助于开发带有嵌入式服务器的独立应用程序。

在本教程中，我们将探讨如何使用 Ktor 创建独立的服务器应用程序。

## 2. 设置 Ktor 应用

让我们从设置 Ktor 项目开始。我们将使用Gradle ，这是推荐且易于使用的方法。可以按照 [Gradle站点](https://gradle.org/install/)上提供的说明安装 Gradle 。

创建build.gradle文件：

```groovy
group 'com.baeldung.kotlin'
version '1.0-SNAPSHOT'

buildscript {
    ext.kotlin_version = '1.2.40'
    ext.ktor_version = '0.9.2'

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

apply plugin: 'java'
apply plugin: 'kotlin'
apply plugin: 'application'

mainClassName = 'APIServer.kt'

sourceCompatibility = 1.8
compileKotlin { kotlinOptions.jvmTarget = "1.8" }
compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }

kotlin { experimental { coroutines "enable" } }

repositories {
    mavenCentral()
    jcenter()
    maven { url "https://dl.bintray.com/kotlin/ktor" }
}

dependencies {
    compile "io.ktor:ktor-server-netty:$ktor_version"
    compile "ch.qos.logback:logback-classic:1.2.1"
    testCompile group: 'junit', name: 'junit', version: '4.12'
}
```

我们已经导入了 Ktor 和 Ktor netty 服务器包。Netty 是我们将在本示例中使用的嵌入式服务器。

## 3.搭建服务器

我们通过将代码添加到源文件夹 src/main/kotlin来创建我们的应用程序。

在这里，我们使用 main 方法创建文件APIServer.kt ：

```javascript
fun main(args: Array<String>) {

}

```

接下来，我们创建并启动嵌入式 Netty 服务器：

```javascript
embeddedServer(Netty, 8080) {
    
}.start(wait = true)

```

它将在端口8080创建并启动服务器。我们在 start()方法中设置了wait=true 来监听连接。

## 4. 构建 API

让我们添加 API。为了处理 HTTP 请求，Ktor 提供了路由 特性。

 我们使用安装块激活 路由功能，我们可以在其中为特定路径和 HTTP 方法定义路由：

```javascript
val jsonResponse = """{
    "id": 1,
    "task": "Pay waterbill",
    "description": "Pay water bill today",
}"""

embeddedServer(Netty, 8080) {
  install(Routing) {
      get("/todo") {
          call.respondText(jsonResponse, ContentType.Application.Json)
      }
  }
}.start(wait = true)
```

在此示例中，服务器将处理 路径/todo的GET请求，并使用todo JSON 对象进行回复。 我们将在安装功能部分了解更多关于安装功能的信息。

## 5. 运行服务器

要运行服务器，我们需要在 Gradle 中运行一个任务：

```groovy
task runServer(type: JavaExec) {
    main = 'APIServer'
    classpath = sourceSets.main.runtimeClasspath
}

```

要启动服务器，我们将此任务称为：

```bash
./gradlew runServer

```

然后可以通过http://localhost:8080/todo 访问 Out API。

## 6. 安装功能

一个 Ktor 应用程序通常由一系列功能组成。我们可以将特性视为注入请求和响应管道的功能。

使用 DefaultHeaders功能，我们可以为每个传出响应添加标头。路由是另一个特性，它允许我们定义路由来处理请求等。

我们还可以开发我们的功能并安装它们。

让我们通过安装DefaultHeaders功能为每个请求添加一个自定义标头来了解一下：

```groovy
install(DefaultHeaders) {
    header("X-Developer", "Baeldung")
}
```

同样，我们可以覆盖 Ktor 框架本身设置的默认标头：

```groovy
install(DefaultHeaders) {
    header(HttpHeaders.Server, "My Server")
}
```

可以在类io.ktor.features.DefaultHeaders 中找到可用的默认标头列表。 

## 7. 提供 JSON

手动构建字符串化的 JSON 并不容易。Ktor 提供了使用 Gson 将数据对象作为 JSON 提供服务的功能。

让我们在build.gradle中添加 Gson 依赖：

```groovy
compile "io.ktor:ktor-gson:$ktor_version"
```

例如，我们使用名称为 Author 的数据对象：

```javascript
data class Author(val name: String, val website: String)
```

接下来，我们安装gson功能：

```groovy
install(ContentNegotiation) {
   gson {
       setPrettyPrinting()
   }
}
```

最后，让我们添加一个到服务器的路由，以 JSON 形式提供作者对象：

```javascript
get("/author") {
    val author = Author("baeldung", "baeldung.com")
    call.respond(author)
}
```

作者 API 将作者数据对象作为JSON提供。

## 8.添加控制器

要了解如何处理多个 HTTP 操作请求，让我们创建一个允许用户添加、删除、查看和列出 TODO 项的 TODO 应用程序。

我们将从添加 Todo数据类开始：

```javascript
data class ToDo(var id: Int, val name: String, val description: String, val completed: Boolean)

```

然后我们创建一个 ArrayList来保存多个Todo项：

```javascript
val toDoList = ArrayList<ToDo>();

```

接下来，我们添加控制器来处理 POST、DELETE 和 GET 请求：

```javascript
routing() {
    route("/todo") {
        post {
            var toDo = call.receive<ToDo>();
            toDo.id = toDoList.size;
            toDoList.add(toDo);
            call.respond("Added")

        }
        delete("/{id}") {
            call.respond(toDoList.removeAt(call.parameters["id"]!!.toInt()));
        }
        get("/{id}") {
            call.respond(toDoList[call.parameters["id"]!!.toInt()]);
        }
        get {
            call.respond(toDoList);
        }
    }
}
```

我们添加了一个待办事项 路由，然后将不同的 HTTP 动词请求映射到该端点。

## 9.总结

在本文中，我们学习了如何使用 Ktor 框架创建 Kotlin 服务器应用程序。

我们在几分钟内构建了一个小型服务器应用程序，而没有使用任何样板代码。