## 1. 概述

Kotlin 是由 JetBrains 开发的下一代编程语言。它作为 Java 的替代品在 Android 开发社区中越来越受欢迎。

Kotlin 的另一个令人兴奋的特性是对服务器端和客户端 JavaScript 的支持。在本文中，我们将讨论如何使用 Kotlin 编写服务器端 JavaScript 应用程序。

Kotlin 可以转译(用一种语言编写的源代码并转换为另一种语言)到 JavaScript。它让用户可以选择使用相同的语言来定位 JVM 和 JavaScript。

在接下来的部分中，我们将使用 Kotlin 开发一个 node.js 应用程序。

## 2.节点.js

Node.js 是一个精简、快速、跨平台的 JavaScript 运行时环境。它对服务器和桌面应用程序都很有用。

让我们从设置 node.js 环境和项目开始。

### 2.1. 安装 node.js

Node.js 可以从 [Node 网站](https://nodejs.org/en/download/)下载。它带有 npm 包管理器。安装后我们需要设置项目。

在空目录中，让我们运行：

```bash
npm init
```

它会询问一些关于包名、版本描述和入口点的问题。提供“kotlin-node”作为名称，“Kotlin Node Example”作为 描述 ，“crypto.js”作为 入口点。对于其余值，我们将保留默认值。

这个过程会生成一个包。json 文件。

在此之后，我们需要安装一些依赖包：

```bash
npm install
npm install kotlin --save
npm install express --save
```

这将在当前项目目录中安装我们的示例应用程序所需的模块。

## 3.使用 Kotlin创建node.js应用程序

在本节中，我们将在 Kotlin 中使用 node.js 创建一个加密 API 服务器。 API 将获取一些自生成的加密货币汇率。

### 3.1. 设置 Kotlin 项目

现在让我们设置 Kotlin 项目。我们将在这里使用Gradle，这是推荐且易于使用的方法。可以按照 [Gradle站点](https://gradle.org/install/)上提供的说明安装 Gradle 。

在此之后，使用[init task](https://docs.gradle.org/current/samples/sample_building_kotlin_applications.html)生成一个初始 Gradle 项目。选择项目的“基本”类型，Kotlin 作为实现语言，Groovy 作为构建脚本 DSL，然后输入“kotlin-node”作为项目名称，“balding”作为包名称。

这将创建一个build.Gradle文件。将此文件的内容替换为：

```groovy
buildscript {
    ext.kotlin_version = '1.4.10'
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

group 'com.baeldung'
version '1.0-SNAPSHOT'
apply plugin: 'kotlin2js'

repositories {
    mavenCentral()
}

dependencies {
    compile "org.jetbrains.kotlin:kotlin-stdlib-js:$kotlin_version"
    testCompile "org.jetbrains.kotlin:kotlin-test-js:$kotlin_version"
}

compileKotlin2Js.kotlinOptions {
    moduleKind = "commonjs"
    outputFile = "node/crypto.js"
}
```

build.gradle文件中有两个要点需要强调。首先，我们应用kotlin2js插件进行转译。

然后，在 KotlinOptions中，我们将moduleKind 设置为“ commonjs” 以与 node.js 一起工作。使用 outputFile 选项， 我们可以控制转译代码的生成位置。

注意：确保build.gradle文件中的ext.kotlin_version与package.json文件中的匹配。

### 3.2. 创建 API

让我们通过创建源文件夹 src/main/kotlin和包结构com/baeldung/kotlinjs 来开始实现我们的应用程序。

在这个包中，我们创建文件CryptoRate.kt 。

在这个文件中，我们首先需要导入 require函数，然后创建main方法：

```javascript
external fun require(module: String): dynamic

fun main(args: Array<String>) {
    
}
```

接下来，我们导入所需的模块并创建一个监听端口 3000 的服务器：

```javascript
val express = require("express")

val app = express()
app.listen(3000, {
    println("Listening on port 3000")
})

```

最后，我们添加 API 端点“/crypto”。它将生成并返回数据：

```javascript
app.get("/crypto", { _, res ->
    res.send(generateCrypoRates())
})

data class CryptoCurrency(var name: String, var price: Float)

fun generateCryptoRates(): Array<CryptoCurrency> {
    return arrayOf<CryptoCurrency>(
      CryptoCurrency("Bitcoin", 90000F),
      CryptoCurrency("ETH",1000F),
      CryptoCurrency("TRX",10F)
    );
}
```

我们使用了 node.js express 模块来创建 API 端点。 

## 4. 运行应用程序

运行该应用程序将分为两部分。在使用 Node 启动我们的应用程序之前，我们需要将 Kotlin 代码转换为 JavaScript。

要创建 JavaScript 代码，我们使用 Gradle 构建任务：

```bash
./gradlew build

```

这将在节点目录中生成源代码 。

接下来，我们使用 Node.js执行生成的代码文件crypto.js ：

```bash
node node/crypto.js

```

这将启动在端口 3000 运行的服务器。 在浏览器中，让我们通过调用http://localhost:3000/crypto 来访问 API 以获取此 JSON 结果：

```javascript
[
  {
    "name": "Bitcoin",
    "price": 90000
  },
  {
    "name": "ETH",
    "price": 1000
  },
  {
    "name": "TRX",
    "price": 10
  }
]
```

或者，我们可以使用Postman或SoapUI 等工具来使用 API。

## 5.总结

在本文中，我们学习了如何使用 Kotlin 编写 node.js 应用程序。

我们在几分钟内构建了一个小型服务，没有使用任何样板代码。