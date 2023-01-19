## 1. 概述

[Gradle](https://gradle.org/)是一个基于Groovy的构建管理系统，专为构建基于Java的项目而设计。安装说明可以在[这里](https://gradle.org/install/)找到。

## 2. 构建块 - Projects和Tasks

))在Gradle中，构建由一个或多个项目组成，每个项目由一个或多个任务组成))。

Gradle中的项目可以组装成一个jar、war包，甚至是一个zip文件。))任务是一项工作))。包括编译类，或创建和发布Jar/War包。

一个简单的任务可以定义为：

```groovy
task hello {
    doLast {
        println 'Tuyucheng'
    }
}
```

如果我们使用gradle -q hello命令从build.gradle所在的同一位置执行上述任务，我们应该可以在控制台中看到输出。

### 2.1 任务

Gradle的构建脚本使用的是Groovy：

```groovy
task toLower {
    doLast {
        String someString = 'HELLO FROM TUYUCHENG'
        println "Original: " + someString
        println "Lower case: " + someString.toLowerCase()
    }
}
```

我们可以定义依赖于其他任务的任务。任务依赖可以通过在任务定义中传递dependsOn: taskName参数来定义：

```groovy
task helloGradle {
    doLast {
        println 'Hello Gradle!'
    }
}

task fromGradle(dependsOn: helloGradle) {
    doLast {
        println "I'm from Tuyucheng"
    }
}
```

### 2.2 向任务添加行为

我们可以定义一个任务并通过一些额外的行为来增强它：

```groovy
task helloTuyucheng {
    doLast {
        println 'I will be executed second'
    }
}

helloTuyucheng.doFirst {
    println 'I will be executed first'
}

helloTuyucheng.doLast {
    println 'I will be executed third'
}

helloTuyucheng {
    doLast {
        println 'I will be executed fourth'
    }
}
```

doFirst和doLast分别在action列表的头部和尾部添加action，))并且可以在单个任务中多次定义))。

### 2.3 添加任务属性

我们还可以定义属性：

```groovy
task ourTask {
    ext.theProperty = "theValue"
}
```

在这里，我们将“theValue”设置为ourTask任务的属性。

## 3. 管理插件

Gradle中有两种类型的插件：脚本插件和二进制插件。要从插件提供的附加功能中受益，每个插件都需要经历两个阶段：解析和应用。

))解析意味着找到正确版本的插件jar包，并将其添加到项目的类路径中))。))应用插件是在项目上执行Plugin.apply(T)))。

### 3.1 应用脚本插件

在aplugin.gradle中，我们可以定义一个任务：

```groovy
task fromPlugin {
    doLast {
        println "I'm from plugin"
    }
}
```

如果我们想将这个插件应用到我们的项目build.gradle文件中，我们需要做的就是将下面的代码添加到build.gradle中：

```groovy
apply from: 'aplugin.gradle'
```

现在，执行gradle tasks命令应该会在任务列表中显示fromPlugin任务。

### 3.2 使用Plugins DSL应用二进制插件

在添加核心二进制插件的情况下，我们可以添加短名称或插件ID：

```groovy
plugins {
    id 'application'
}
```

现在，application插件中的run任务应该可以在项目中使用以执行任何可运行的jar。要应用社区插件，我们必须指定一个完全限定的插件id：

```groovy
plugins {
    id "org.shipkit.bintray" version "2.3.5"
}
```

现在，shipkit任务应该在gradle任务列表中可用。

plugins DSL的局限性是：

+ 它不支持plugins块内的Groovy代码。
+ plugins块需要是项目构建脚本中的顶级语句(在它之前只允许buildscript{}块)。
+ plugins DSL不能写在脚本插件、settings.gradle文件或init脚本中。

plugins DSL仍在孵化中，DSL和其他配置可能会在以后的Gradle版本中发生变化。

### 3.3 应用插件的传统方法

我们也可以使用“apply plugin”来应用插件：

```groovy
apply plugin: 'war'
```

如果我们需要添加社区插件，我们必须使用buildscript{}块将外部jar添加到构建类路径中。

))然后，我们可以在buildscript中应用插件，但只能在任何现有的plugins{}块之后))：

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "org.shipkit:shipkit:2.3.5"
    }
}

apply plugin: "org.shipkit.bintray-release"
```

## 4. 依赖管理

Gradle提供非常灵活的依赖管理系统，它与各种可用的方法兼容。

))Gradle中依赖管理的最佳实践是版本控制、动态版本控制、解决版本冲突和管理可传递依赖))。

### 4.1 依赖项配置

依赖项被分组到不同的配置中，))配置有一个名称，它们可以相互扩展))。

如果我们应用Java插件，我们将有implementation、testImplementation、runtimeOnly配置可用于对我们的依赖项进行分组。))默认配置扩展了“runtimeOnly”))。

### 4.2 声明依赖项

让我们看一个使用几种不同方式添加一些依赖项(Spring和Hibernate)的示例：

```groovy
dependencies {
    implementation group: 'org.springframework', name: 'spring-core', version: '5.3.13'
    implementation 'org.springframework:spring-core:5.3.13',
            'org.springframework:spring-aop:5.3.13'
    
    implementation(
            [group: 'org.springframework', name: 'spring-core', version: '5.3.13'],
            [group: 'org.springframework', name: 'spring-aop', version: '5.3.13']
    )
    
    testImplementation('org.hibernate:hibernate-core:5.6.1.Final') {
        transitive = true
    }
    runtimeOnly(group: 'org.hibernate', name: 'hibernate-core', version: '5.6.1.Final') {
        transitive = false
    }
}
```

我们在各种配置中声明依赖关系：各种格式的implementation、testImplementation和runtimeOnly。

有时我们需要具有多个工件的依赖项。在这种情况下，我们可以添加一个artifact-only符号@extensionName(或扩展形式的ext)来下载所需的工件：

```groovy
runtimeOnly "org.codehaus.groovy:groovy-all:2.4.11@jar"
runtimeOnly group: 'org.codehaus.groovy', name: 'groovy-all', version: '2.4.11', ext: 'jar'
```

在这里，我们添加了@jar符号，以便只下载没有依赖项的jar工件。

要将依赖项添加到任何本地文件，我们可以使用以下方法：

```groovy
implementation files('libs/joda-time-2.2.jar', 'libs/junit-4.13.2.jar')
implementation fileTree(dir: 'libs', include: ').jar')
```

))当我们想要避免传递依赖时，我们可以在配置级别或依赖级别上这样做))：

```groovy
configurations {
    testImplementation.exclude module: 'junit'
}

testImplementation("org.springframework.batch:spring-batch-test:3.0.7.RELEASE") {
    exclude module: 'junit'
}
```

## 5. 多项目构建

### 5.1 构建生命周期

))在初始化阶段，Gradle会确定哪些项目将参与多项目构建))。这通常在位于项目根目录的settings.gradle文件中可以看出，Gradle还会创建参与项目的实例。

))在配置阶段，所有创建的项目实例都是基于Gradle功能配置按需配置的))。在此功能中，仅为特定任务执行配置所需的项目。这样，对于大型多项目构建，配置时间就大大减少了。这一特性仍在孵化中。

最后，))在执行阶段，执行创建和配置的任务子集))。我们可以在settings.gradle和build.gradle文件中添加一些代码来理解这三个阶段的执行。

在settings.gradle中：

```groovy
println 'At initialization phase.'
```

在build.gradle：

```groovy
println 'At configuration phase.'

task configured { println 'Also at the configuration phase.' }

task execFirstTest { doLast { println 'During the execution phase.' } }

task execSecondTest {
    doFirst { println 'At first during the execution phase.' }
    doLast { println 'At last during the execution phase.' }
    println 'At configuration phase.'
}
```

### 5.2 创建多项目构建

我们可以在根目录中执行gradle init命令来为settings.gradle和build.gradle文件创建一个骨架。

所有通用配置都将保存在根构建脚本中：

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    version = '1.0'
}
```

settings.gradle需要包含根项目名称和子项目名称：

```groovy
rootProject.name = 'multi-project-builds'
include 'greeting-library', 'greeter'
```

现在，我们需要有几个名为greeting-library和greeter的子项目文件夹来演示多项目构建。每个子项目都需要有一个单独的build.gradle来配置其单独的依赖项和其他必要的配置。

如果我们想让我们的greeter项目依赖于greeting-library，我们需要在greeter的构建脚本中包含依赖项：

```groovy
dependencies {
    implementation project(':greeting-library')
}
```

## 6. Gradle Wrapper的使用

如果一个Gradle项目有Linux的gradlew文件和Windows的gradlew.bat文件，我们可以不需要安装Gradle就可以构建项目。

如果我们在Windows中执行gradlew build或者在Linux中执行./gradlew build，则会自动下载gradlew文件中指定的Gradle发行版。

如果我们想将Gradle Wrapper添加到我们的项目中：

```shell
gradle wrapper --gradle-version 7.4
```

该命令需要从项目的根目录执行；这将创建所有必要的文件和文件夹，以将Gradle Wrapper绑定到项目。另一种方法是将wrapper任务添加到构建脚本中：

```groovy
wrapper {
    gradleVersion = '7.4'
}
```

现在我们需要执行wrapper任务，该任务会将我们的项目绑定到Wrapper。除了gradlew文件之外，还会在gradle文件夹中生成一个wrapper文件夹，其中包含一个jar包和一个gradle-wrapper属性文件。

如果我们想切换到新版本的Gradle，我们只需要更改gradle-wrapper.properties中的属性distributionUrl。

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https://services.gradle.org/distributions/gradle-7.4-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

## 7. 总结

在本文中，我们介绍了Gradle；相比之下，在解决版本冲突和管理传递依赖项方面比其他现有构建工具有更大的灵活性。