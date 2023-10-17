## 1. 概述

在本教程中，我们介绍如何在Gradle项目中配置条件依赖项。

## 2. 项目构建

我们构建一个多模块项目，添加两个提供者模块provider1和provider2，以及两个消费者模块consumer1和consumer2：

<img src="../assets/img.png">

## 3. 配置条件依赖

假设，基于项目属性，我们希望包含两个提供者模块中的其中一个。对于我们的consumer1模块，如果指定了属性isLocal，我们希望包含provider1模块；否则，应该包含provider2模块。

为此，让我们在consumer1模块的gradle.settings.kts文件中添加以下内容：

```kotlin
plugins {
	id("java")
}

group = "cn.tuyucheng.taketoday"
version = "1.0.0"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

	if (project.hasProperty("isLocal")) {
		implementation("cn.tuyucheng.taketoday:provider1")
	} else {
		implementation("cn.tuyucheng.taketoday:provider2")
	}
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}
```

现在，我们进入到consumer1模块，并运行dependencies任务，来查看选择的是哪个提供者模块：

```shell
gradle -PisLocal dependencies --configuration implementation
```

<img src="../assets/img_1.png">

如我们所见，如果我们在命令中传递了isLocal属性，则包含的是provider1模块。现在让我们在没有指定任何属性的情况下运行dependencies任务：

```shell
gradle dependencies --configuration implementation
```

<img src="../assets/img_2.png">

可以看到，此时包含的是provider2模块。

## 4. 通过模块替换配置条件依赖

让我们看看另一种通过依赖替换有条件地配置依赖的方法。对于我们的consumer2模块，如果指定了isLocal属性，我们希望包含provider2模块。否则，应使用模块provider1。

我们将以下配置添加到consumer2模块中以实现此目标：

```kotlin
plugins {
	id("java")
}

group = "cn.tuyucheng.taketoday"
version = "1.0.0"

repositories {
	mavenCentral()
}

configurations.all {
	resolutionStrategy.dependencySubstitution {
		if (project.hasProperty("isLocal"))
			substitute(project(":provider1"))
				.using(project(":provider2"))
				.because("Project property override(isLocal).")
	}
}

dependencies {
	implementation(project(":provider1"))

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}
```

如果我们再次运行相同的命令，我们应该会得到类似的结果。首先我们使用指定的isLocal属性运行：

```shell
gradle -PisLocal dependencies --configuration compilePath
```

<img src="../assets/img_3.png">

果然，我们看到provider1项目被provider2项目取代。现在让我们在没有指定属性的情况下运行命令：

```shell
gradle dependencies --configuration compilePath
```

<img src="../assets/img_4.png">

正如预期的那样，这次没有进行替换，并且包括了provider1。

## 5. 两种方法的区别

这两种方法都帮助我们实现了有条件地配置依赖项的目标。首先，**与第二种方法相比，直接编写条件逻辑看起来更简单，配置更少**。其次，**虽然第二种方法涉及更多的配置，但似乎更惯用**。在第二种方法中，我们利用了Gradle本身提供的替换机制，它还允许我们指定替换的原因。此外，在日志中，我们可以注意到发生了替换，在第一种方法中则没有此类信息：

```text
compileClasspath - Compile classpath for source set 'main'.
\--- project :provider1 -> project :provider2
```

我们还要注意，在第一种方法中，不需要依赖解析。我们可以通过以下方式获得结果：

```shell
gradle -PisLocal dependencies --configuration implementation
```

而在第二种方法中，如果我们要检查implementation配置，我们将看不到预期的结果。原因是它仅在依赖解析发生时才有效。因此，它可以用compilePath配置：

```shell
gradle -PisLocal dependencies --configuration compilePath
```

## 6. 总结

在本文中，我们介绍了两种在Gradle中有条件地配置依赖项的方法，并分析了两者的区别；Gradle提供的依赖替换配置则是更惯用的方法。