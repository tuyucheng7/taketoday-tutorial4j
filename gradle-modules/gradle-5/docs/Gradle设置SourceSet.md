## 1. 概述

源集为我们提供了一种在[Gradle](https://www.baeldung.com/gradle)项目中构建源代码的强大方法。

在本快速教程中，我们将了解如何使用它们。

## 2. 默认源集

在进入默认设置之前，让我们首先解释一下什么是源集。顾名思义，))源集代表源文件的逻辑分组))。

我们将介绍Java项目的配置，但这些概念也适用于其他 Gradle 项目类型。

### 2.1. 默认项目布局

让我们从一个简单的项目结构开始：

```markdown
source-sets 
  ├── src 
  │    ├── main 
  │    │    └── java 
  │    │        ├── SourceSetsMain.java
  │    │        └── SourceSetsObject.java
  │    └── test 
  │         └── java 
  │             └── SourceSetsTest.java
  └── build.gradle 
```

现在让我们看一下)build.gradle)：

```groovy
apply plugin : "java"
description = "Source Sets example"
test {
    testLogging {
        events "passed", "skipped", "failed"
    }
}
dependencies {   
    implementation('org.apache.httpcomponents:httpclient:4.5.12')
    testImplementation('junit:junit:4.12')
}
```

Java 插件假定)))src/main/java)和)src/test/java)作为默认源目录)))。) 

让我们制作一个简单的实用程序任务：

```groovy
task printSourceSetInformation(){
    doLast{
        sourceSets.each { srcSet ->
            println "["+srcSet.name+"]"
            print "-->Source directories: "+srcSet.allJava.srcDirs+"n"
            print "-->Output directories: "+srcSet.output.classesDirs.files+"n"
            println ""
        }
    }
}
```

我们在这里只打印一些源集属性。我们可以随时查看完整的[JavaDoc](https://docs.gradle.org/current/javadoc/org/gradle/api/tasks/SourceSet.html)以获取更多信息。

让我们运行它，看看我们得到了什么：

```bash
$ ./gradlew printSourceSetInformation

> Task :source-sets:printSourceSetInformation
[main]
-->Source directories: [.../source-sets/src/main/java]
-->Output directories: [.../source-sets/build/classes/java/main]

[test]
-->Source directories: [.../source-sets/src/test/java]
-->Output directories: [.../source-sets/build/classes/java/test]

```

请注意))，我们有两个默认源集：)main)和)test)))。

### 2.2. 默认配置

))Java 插件还会自动为我们创建一些默认的 Gradle[配置](https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.Configuration.html)))。

它们遵循特殊的命名约定：)<sourceSetName><configurationName>)。

我们使用它们在)build.gradle)中声明依赖关系：

```groovy
dependencies { 
    implementation('org.apache.httpcomponents:httpclient:4.5.12') 
    testImplementation('junit:junit:4.12') 
}
```

请注意，我们指定了)implementation)而不是)mainImplementation)。这是命名约定的一个例外。

))默认情况下，)testImplementation)配置扩展)实现)并继承其所有依赖项和输出))。

让我们改进我们的助手任务，看看这是关于什么的：

```groovy
task printSourceSetInformation(){

    doLast{
        sourceSets.each { srcSet ->
            println "["+srcSet.name+"]"
            print "-->Source directories: "+srcSet.allJava.srcDirs+"n"
            print "-->Output directories: "+srcSet.output.classesDirs.files+"n"
            print "-->Compile classpath:n"
            srcSet.compileClasspath.files.each { 
                print "  "+it.path+"n"
            }
            println ""
        }
    }
}
```

让我们看一下输出：

```groovy
[main]
// same output as before
-->Compile classpath:
  .../httpclient-4.5.12.jar
  .../httpcore-4.4.13.jar
  .../commons-logging-1.2.jar
  .../commons-codec-1.11.jar

[test]
// same output as before
-->Compile classpath:
  .../source-sets/build/classes/java/main
  .../source-sets/build/resources/main
  .../httpclient-4.5.12.jar
  .../junit-4.12.jar
  .../httpcore-4.4.13.jar
  .../commons-logging-1.2.jar
  .../commons-codec-1.11.jar
  .../hamcrest-core-1.3.jar
```

)测试)源集在其编译类路径中包含)main)的输出，还包括其依赖项。

接下来，让我们创建单元测试：

```java
public class SourceSetsTest {

    @Test
    public void whenRun_ThenSuccess() {
        
        SourceSetsObject underTest = new SourceSetsObject("lorem","ipsum");
        
        assertThat(underTest.getUser(), is("lorem"));
        assertThat(underTest.getPassword(), is("ipsum"));
    }
}
```

这里我们测试一个存储两个值的简单 POJO。我们可以直接使用它，因为))主要)输出)在我们的)测试)类路径))中。

接下来，让我们从 Gradle 运行它：

```groovy
./gradlew clean test

> Task :source-sets:test

com.baeldung.test.SourceSetsTest > whenRunThenSuccess PASSED

```

## 3.自定义源集

到目前为止，我们已经看到了一些合理的默认值。然而，在实践中，我们经常需要自定义源集，尤其是对于集成测试。

那是因为我们可能希望仅在集成测试类路径上有特定的测试库。我们也可能希望独立于单元测试执行它们。

### 3.1. 定义自定义源集

让我们为集成测试创建一个单独的源目录：

```markdown
source-sets 
  ├── src 
  │    └── main 
  │         ├── java 
  │         │    ├── SourceSetsMain.java
  │         │    └── SourceSetsObject.java
  │         ├── test 
  │         │    └── SourceSetsTest.java
  │         └── itest 
  │              └── SourceSetsITest.java
  └── build.gradle 
```

接下来，让我们))使用)))))sourceSets)))))构造))))在我们的)build.gradle)))中配置它：

```groovy
sourceSets {
    itest {
        java {
        }
    }
}
dependencies {
    implementation('org.apache.httpcomponents:httpclient:4.5.12')
    testImplementation('junit:junit:4.12')
}
// other declarations omitted

```

请注意，我们没有指定任何自定义目录。那是因为我们的文件夹与新源集的名称 ( )itest) ) 相匹配。

我们可以))自定义包含在)srcDirs)属性))中的目录：

```groovy
sourceSets{
    itest {
        java {
            srcDirs("src/itest")
        }
    }
}
```

还记得我们一开始的辅助任务吗？让我们重新运行它，看看它打印了什么：

```bash
$ ./gradlew printSourceSetInformation

> Task :source-sets:printSourceSetInformation
[itest]
-->Source directories: [.../source-sets/src/itest/java]
-->Output directories: [.../source-sets/build/classes/java/itest]
-->Compile classpath:
  .../source-sets/build/classes/java/main
  .../source-sets/build/resources/main

[main]
 // same output as before

[test]
 // same output as before
```

### 3.2. 分配源集特定的依赖关系

还记得默认配置吗？我们现在也获得了)itest)源集的一些配置。

))让我们使用)itestImplementation)来分配一个新的依赖))：))
))

```groovy
dependencies {
    implementation('org.apache.httpcomponents:httpclient:4.5.12')
    testImplementation('junit:junit:4.12')
    itestImplementation('com.google.guava:guava:29.0-jre')
}
```

这只适用于集成测试。

让我们修改之前的测试并将其添加为集成测试：

```java
public class SourceSetsItest {

    @Test
    public void givenImmutableList_whenRun_ThenSuccess() {

        SourceSetsObject underTest = new SourceSetsObject("lorem", "ipsum");
        List someStrings = ImmutableList.of("Baeldung", "is", "cool");

        assertThat(underTest.getUser(), is("lorem"));
        assertThat(underTest.getPassword(), is("ipsum"));
        assertThat(someStrings.size(), is(3));
    }
}
```

为了能够))运行它))，我们需要))定义一个使用编译输出的自定义测试任务))：

```groovy
// source sets declarations

// dependencies declarations 

task itest(type: Test) {
    description = "Run integration tests"
    group = "verification"
    testClassesDirs = sourceSets.itest.output.classesDirs
    classpath = sourceSets.itest.runtimeClasspath
}
```

))这些声明在[配置阶段](https://docs.gradle.org/current/userguide/build_lifecycle.html)))进行评估。因此，))他们的顺序很重要))。

例如，在声明this之前，我们不能引用任务主体中的)itest)源集。

让我们看看如果我们运行测试会发生什么：

```bash
$ ./gradlew clean itest

// some compilation issues

FAILURE: Build failed with an exception.

) What went wrong:
Execution failed for task ':source-sets:compileItestJava'.
> Compilation failed; see the compiler error output for details.
```

与上一次运行不同，这次我们遇到编译错误。所以发生了什么事？

这个新的源集创建了一个独立的配置。

换句话说，)))itestImplementation)不继承)JUnit)依赖项，也不获取)main)))的输出。

让我们在 Gradle 配置中解决这个问题：

```groovy
sourceSets{
    itest {
        compileClasspath += sourceSets.main.output
        runtimeClasspath += sourceSets.main.output
        java {
        }
    }
}

// dependencies declaration
configurations {
    itestImplementation.extendsFrom(testImplementation)
    itestRuntimeOnly.extendsFrom(testRuntimeOnly)
}
```

现在让我们重新运行我们的集成测试：

```bash
$ ./gradlew clean itest

> Task :source-sets:itest

com.baeldung.itest.SourceSetsItest > givenImmutableList_whenRun_ThenSuccess PASSED
```

测试通过。

### 3.3. Eclipse IDE 处理

到目前为止，我们已经了解了如何使用 Gradle 直接使用源集。然而，大多数时候，我们会使用 IDE(例如 Eclipse)。

当我们导入项目时，我们遇到了一些编译问题：

[![编译问题日食](https://www.baeldung.com/wp-content/uploads/2020/08/compilation-issue-eclipse.png)](https://www.baeldung.com/wp-content/uploads/2020/08/compilation-issue-eclipse.png)

但是，如果我们从 Gradle 运行集成测试，我们不会收到任何错误：

```bash
$ ./gradlew clean itest

> Task :source-sets:itest

com.baeldung.itest.SourceSetsItest > givenImmutableList_whenRun_ThenSuccess PASSED
```

所以发生了什么事？在这种情况下，)番石榴)依赖项属于 )itestImplementation)。

不幸))的是，Eclipse Buildship Gradle 插件不能很好地处理这些自定义配置))。

让我们在)))build.gradle)))))中解决这个))问题：

```groovy
apply plugin: "eclipse"

// previous declarations

eclipse {
    classpath {
        plusConfigurations+=[configurations.itestCompileClasspath] 
    } 
}

```

让我们解释一下我们在这里做了什么。我们将配置附加到 Eclipse 类路径。

如果我们刷新项目，编译问题就消失了。

但是，))这种方法有一个缺点))：IDE 不区分配置。

这意味着我们可以轻松地))在我们的)) )))测试))) ))源中))))导入)番石榴))) (我们特别想避免)。

## 4. 总结

在本教程中，我们介绍了 Gradle 源集的基础知识。

然后我们解释了自定义源集的工作原理以及如何在 Eclipse 中使用它们。