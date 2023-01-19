## 一、简介

在本教程中，我们将探讨将 Groovy 集成到 Java 应用程序中的最新技术。

## 2. 关于 Groovy 的几句话

Groovy 编程语言是一种功能强大的、可选类型的动态语言。它得到 Apache 软件基金会和 Groovy 社区的支持，并得到了 200 多名开发人员的贡献。

它可用于构建整个应用程序，创建与我们的 Java 代码交互的模块或附加库，或者运行即时评估和编译的脚本。

有关更多信息，请阅读[Groovy 语言简介](https://www.baeldung.com/groovy-language)或转到[官方文档](http://groovy-lang.org/)。

## 3.Maven依赖

在撰写本文时，最新的稳定版本是 2.5.7，而 Groovy 2.6 和 3.0(均于 17 年秋季开始)仍处于 alpha 阶段。

与 Spring Boot 类似，我们只需要包含[groovy-all](https://search.maven.org/search?q=g:org.codehaus.groovy a:groovy-all) pom 来添加我们可能需要的所有依赖项，而不用担心它们的版本：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-all</artifactId>
    <version>${groovy.version}</version>
    <type>pom</type>
</dependency>
```

## 4. 联合编译

在详细介绍如何配置 Maven 之前，我们需要了解我们要处理的是什么。

我们的代码将包含 Java 和 Groovy 文件。Groovy 查找 Java 类完全没有问题，但是如果我们希望 Java 查找 Groovy 类和方法怎么办？

联合编译来拯救！

联合编译是一个旨在通过单个 Maven 命令编译同一项目中的 Java 和 Groovy 文件的过程。

通过联合编译，Groovy 编译器将：

-   解析源文件
-   根据实现，创建与 Java 编译器兼容的存根
-   调用 Java 编译器来编译存根和 Java 源代码——这样 Java 类就可以找到 Groovy 依赖项
-   编译 Groovy 源代码——现在我们的 Groovy 源代码可以找到它们的 Java 依赖项

根据实现它的插件，我们可能需要将文件分成特定的文件夹或告诉编译器在哪里可以找到它们。

如果没有联合编译，Java 源文件将被编译为就好像它们是 Groovy 源一样。有时这可能会起作用，因为大多数 Java 1.7 语法都与 Groovy 兼容，但语义会有所不同。

## 5. Maven 编译器插件

有一些可用的编译器插件支持联合编译，每个都有其优点和缺点。

Maven 中最常用的两个是 Groovy-Eclipse Maven 和 GMaven+。

### 5.1. Groovy-Eclipse Maven 插件

[Groovy-Eclipse Maven 插件](https://github.com/groovy/groovy-eclipse/wiki/Groovy-Eclipse-Maven-plugin#why-another-groovy-compiler-for-maven-what-about-gmaven) 通过避免生成存根来简化联合编译，这仍然是其他编译器(如 GMaven + )的强制性步骤，但它存在一些配置问题。

为了能够检索最新的编译器工件，我们必须添加 Maven Bintray 存储库：

```xml
<pluginRepositories>
    <pluginRepository>
        <id>bintray</id>
        <name>Groovy Bintray</name>
        <url>https://dl.bintray.com/groovy/maven</url>
        <releases>
            <!-- avoid automatic updates -->
            <updatePolicy>never</updatePolicy>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </pluginRepository>
</pluginRepositories>
```

然后，在插件部分，我们告诉 Maven 编译器它必须使用哪个 Groovy 编译器版本。

事实上，我们将使用的插件[——Maven 编译器插件](https://www.baeldung.com/maven-compiler-plugin)——实际上并不编译，而是将工作委托给[groovy -eclipse-batch工件](https://search.maven.org/search?q=g:org.codehaus.groovy a:groovy-eclipse-batch)：

```xml
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <compilerId>groovy-eclipse-compiler</compilerId>
        <source>${java.version}</source>
        <target>${java.version}</target>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.codehaus.groovy</groupId>
            <artifactId>groovy-eclipse-compiler</artifactId>
            <version>3.3.0-01</version>
        </dependency>
        <dependency>
            <groupId>org.codehaus.groovy</groupId>
            <artifactId>groovy-eclipse-batch</artifactId>
            <version>${groovy.version}-01</version>
        </dependency>
    </dependencies>
</plugin>
```

groovy-all依赖版本应该与编译器版本相匹配。

最后，我们需要配置源自动发现：默认情况下，编译器会查找src/main/java和src/main/groovy 等文件夹，但如果我们的 java 文件夹为空，编译器将不会查找我们的 groovy消息来源。

相同的机制对我们的测试有效。

要强制文件发现，我们可以在src/main/java和src/test/java中添加任何文件，或者简单地添加[groovy-eclipse-compiler插件](https://search.maven.org/search?q=g:org.codehaus.groovy a:groovy-eclipse-compiler)：

```xml
<plugin>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-eclipse-compiler</artifactId>
    <version>3.3.0-01</version>
    <extensions>true</extensions>
</plugin>
```

<extension>部分是强制性的，让插件添加额外的构建阶段和目标，包含两个 Groovy 源文件夹。

### 5.2. GMavenPlus 插件

[GMavenPlus 插件](https://github.com/groovy/GMavenPlus/wiki/Choosing-Your-Build-Tool)的名称可能类似于旧的 GMaven 插件，但作者并没有创建一个补丁，而是努力简化编译器并将其与特定的 Groovy 版本分离。

为此，该插件将自身与编译器插件的标准指南分开。

GMavenPlus 编译器添加了对当时其他编译器中尚不存在的功能的支持，例如[invokedynamic](http://groovy-lang.org/indy.html)、交互式 shell 控制台和 Android。

另一方面，它带来了一些并发症：

-   它修改 Maven 的源目录以包含 Java 和 Groovy 源，但不包含 Java 存根
-   如果我们不以适当的目标删除存根，则需要我们管理存根

要配置我们的项目，我们需要添加[gmavenplus-plugin](https://search.maven.org/search?q=g:org.codehaus.gmavenplus a:gmavenplus-plugin)：

```xml
<plugin>
    <groupId>org.codehaus.gmavenplus</groupId>
    <artifactId>gmavenplus-plugin</artifactId>
    <version>1.7.0</version>
    <executions>
        <execution>
            <goals>
                <goal>execute</goal>
                <goal>addSources</goal>
                <goal>addTestSources</goal>
                <goal>generateStubs</goal>
                <goal>compile</goal>
                <goal>generateTestStubs</goal>
                <goal>compileTests</goal>
                <goal>removeStubs</goal>
                <goal>removeTestStubs</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.codehaus.groovy</groupId>
            <artifactId>groovy-all</artifactId>
            <!-- any version of Groovy >= 1.5.0 should work here -->
            <version>2.5.6</version>
            <scope>runtime</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</plugin>
```

为了允许测试此插件，我们在示例中创建了第二个名为gmavenplus-pom.xml 的 pom 文件。

### 5.3. 使用 Eclipse-Maven 插件编译

现在一切都配置好了，我们终于可以构建我们的类了。

在我们提供的示例中，我们在源文件夹src/main/java中创建了一个简单的 Java 应用程序，在src/main/groovy中创建了一些 Groovy 脚本 ，我们可以在其中创建 Groovy 类和脚本。

让我们使用 Eclipse-Maven 插件构建一切：

```bash
$ mvn clean compile
...
[INFO] --- maven-compiler-plugin:3.8.0:compile (default-compile) @ core-groovy-2 ---
[INFO] Changes detected - recompiling the module!
[INFO] Using Groovy-Eclipse compiler to compile both Java and Groovy files
...
```

在这里我们看到Groovy 正在编译一切。

### 5.4. 使用 GMavenPlus 编译

GMavenPlus 显示了一些差异：

```bash
$ mvn -f gmavenplus-pom.xml clean compile
...
[INFO] --- gmavenplus-plugin:1.7.0:generateStubs (default) @ core-groovy-2 ---
[INFO] Using Groovy 2.5.7 to perform generateStubs.
[INFO] Generated 2 stubs.
[INFO]
...
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ core-groovy-2 ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 3 source files to XXXBaeldungTutorialsRepocore-groovy-2targetclasses
[INFO]
...
[INFO] --- gmavenplus-plugin:1.7.0:compile (default) @ core-groovy-2 ---
[INFO] Using Groovy 2.5.7 to perform compile.
[INFO] Compiled 2 files.
[INFO]
...
[INFO] --- gmavenplus-plugin:1.7.0:removeStubs (default) @ core-groovy-2 ---
[INFO]
...
```

我们立即注意到 GMavenPlus 经历了以下额外步骤：

1.  生成存根，每个 groovy 文件一个
2.  编译 Java 文件——类似存根和 Java 代码
3.  编译 Groovy 文件

通过生成存根，GMavenPlus 继承了过去几年在使用联合编译时让开发人员头疼的一个弱点。

在理想情况下，一切都会很好，但是引入更多步骤我们也会有更多失败点：例如，构建可能会在能够清理存根之前失败。

如果发生这种情况，遗留的旧存根可能会混淆我们的 IDE，然后它会显示编译错误，而我们知道一切都应该是正确的。

只有干净的构建才能避免痛苦而漫长的政治迫害。

### 5.5. 在 Jar 文件中打包依赖

为了从命令行[将程序作为 jar 运行](https://www.baeldung.com/executable-jar-with-maven)，我们添加[了maven-assembly-plugin](https://search.maven.org/search?q=g:org.apache.maven.plugins a:maven-assembly-plugin)，它将所有 Groovy 依赖项包含在一个“fat jar”中，该“fat jar”以属性descriptorRef 中定义的后缀命名：

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <!-- get all project dependencies -->
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
        <!-- MainClass in mainfest make a executable jar -->
        <archive>
            <manifest>
                <mainClass>cn.tuyucheng.taketoday.MyJointCompilationAppcn.tuyucheng.taketoday.MyJointCompilationApp</mainClass>
            </manifest>
        </archive>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <!-- bind to the packaging phase -->
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

编译完成后，我们可以使用以下命令运行我们的代码：

```shell
$ java -jar target/core-groovy-2-1.0-SNAPSHOT-jar-with-dependencies.jar cn.tuyucheng.taketoday.MyJointCompilationApp
```

## 6. 动态加载 Groovy 代码

Maven 编译让我们在项目中包含 Groovy 文件并从 Java 引用它们的类和方法。

虽然，如果我们想在运行时更改逻辑，这还不够：编译在运行时阶段之外运行，所以我们仍然必须重新启动应用程序才能看到我们的更改。

要利用 Groovy 的动态能力(和风险)，我们需要探索可用于在应用程序已运行时加载文件的技术。

### 6.1. Groovy类加载器

为此，我们需要GroovyClassLoader，它可以解析文本或文件格式的源代码并生成生成的类对象。

当源是一个文件时，编译结果也被缓存，以避免当我们向加载器请求同一个类的多个实例时的开销。

相反，直接来自String对象的脚本不会被缓存，因此多次调用同一个脚本仍可能导致内存泄漏。

GroovyClassLoader是构建其他集成系统的基础。

实现比较简单：

```java
private final GroovyClassLoader loader;

private Double addWithGroovyClassLoader(int x, int y) 
  throws IllegalAccessException, InstantiationException, IOException {
    Class calcClass = loader.parseClass(
      new File("src/main/groovy/com/baeldung/", "CalcMath.groovy"));
    GroovyObject calc = (GroovyObject) calcClass.newInstance();
    return (Double) calc.invokeMethod("calcSum", new Object[] { x, y });
}

public MyJointCompilationApp() {
    loader = new GroovyClassLoader(this.getClass().getClassLoader());
    // ...
}

```

### 6.2. GroovyShell

Shell Script Loader parse()方法接受文本或文件格式的源并生成Script类的实例。

该实例继承了Script的run()方法，从上到下执行整个文件并返回最后一行执行给出的结果。

如果我们愿意，我们也可以在我们的代码中扩展Script，并覆盖默认实现以直接调用我们的内部逻辑。

调用Script.run()的实现如下所示：

```groovy
private Double addWithGroovyShellRun(int x, int y) throws IOException {
    Script script = shell.parse(new File("src/main/groovy/com/baeldung/", "CalcScript.groovy"));
    return (Double) script.run();
}

public MyJointCompilationApp() {
    // ...
    shell = new GroovyShell(loader, new Binding());
    // ...
}

```

请注意run()不接受参数，因此我们需要将一些全局变量添加到我们的文件中，通过Binding对象对它们进行初始化。

由于此对象是在GroovyShell初始化中传递的，因此变量与所有脚本实例共享。

如果我们更喜欢更细粒度的控制，我们可以使用invokeMethod()，它可以通过反射访问我们自己的方法并直接传递参数。

让我们看看这个实现：

```java
private final GroovyShell shell;

private Double addWithGroovyShell(int x, int y) throws IOException {
    Script script = shell.parse(new File("src/main/groovy/com/baeldung/", "CalcScript.groovy"));
    return (Double) script.invokeMethod("calcSum", new Object[] { x, y });
}

public MyJointCompilationApp() {
    // ...
    shell = new GroovyShell(loader, new Binding());
    // ...
}

```

在幕后，GroovyShell依赖于GroovyClassLoader来编译和缓存生成的类，因此前面解释的相同规则以相同的方式应用。

### 6.3. GroovyScript引擎

GroovyScriptEngine类特别适用于那些依赖重新加载脚本及其依赖项的应用程序。

虽然我们有这些额外的特性，但实现只有几个小的区别：

```java
private final GroovyScriptEngine engine;

private void addWithGroovyScriptEngine(int x, int y) throws IllegalAccessException,
  InstantiationException, ResourceException, ScriptException {
    Class<GroovyObject> calcClass = engine.loadScriptByName("CalcMath.groovy");
    GroovyObject calc = calcClass.newInstance();
    Object result = calc.invokeMethod("calcSum", new Object[] { x, y });
    LOG.info("Result of CalcMath.calcSum() method is {}", result);
}

public MyJointCompilationApp() {
    ...
    URL url = null;
    try {
        url = new File("src/main/groovy/com/baeldung/").toURI().toURL();
    } catch (MalformedURLException e) {
        LOG.error("Exception while creating url", e);
    }
    engine = new GroovyScriptEngine(new URL[] {url}, this.getClass().getClassLoader());
    engineFromFactory = new GroovyScriptEngineFactory().getScriptEngine(); 
}
```

这次我们必须配置源根，我们只用它的名字来引用脚本，这样更干净一些。

查看 loadScriptByName方法内部，我们可以立即看到检查 isSourceNewer引擎检查当前缓存中的源是否仍然有效。

每次我们的文件更改时，GroovyScriptEngine都会自动重新加载该特定文件以及依赖它的所有类。

虽然这是一个方便且强大的功能，但它可能会导致非常危险的副作用：多次重新加载大量文件会导致 CPU 开销而不会发出警告。

如果发生这种情况，我们可能需要实现自己的缓存机制来处理这个问题。

### 6.4. GroovyScriptEngineFactory (JSR-223)

[JSR-223](https://jcp.org/aboutJava/communityprocess/final/jsr223/index.html)提供了一个标准的 API，用于调用自 Java 6 以来的脚本框架。

实现看起来很相似，尽管我们返回到通过完整文件路径加载：

```groovy
private final ScriptEngine engineFromFactory;

private void addWithEngineFactory(int x, int y) throws IllegalAccessException, 
  InstantiationException, javax.script.ScriptException, FileNotFoundException {
    Class calcClas = (Class) engineFromFactory.eval(
      new FileReader(new File("src/main/groovy/com/baeldung/", "CalcMath.groovy")));
    GroovyObject calc = (GroovyObject) calcClas.newInstance();
    Object result = calc.invokeMethod("calcSum", new Object[] { x, y });
    LOG.info("Result of CalcMath.calcSum() method is {}", result);
}

public MyJointCompilationApp() {
    // ...
    engineFromFactory = new GroovyScriptEngineFactory().getScriptEngine();
}
```

如果我们将我们的应用程序与多种脚本语言集成，那就太好了，但它的功能集会受到更多限制。例如，它不支持类重新加载。因此，如果我们只与 Groovy 集成，那么最好坚持使用早期的方法。

## 7. 动态编译的陷阱

使用上述任何一种方法，我们都可以创建一个应用程序，从我们的 jar 文件外部的特定文件夹中读取脚本或类。

这将使我们能够在系统运行时灵活地添加新功能(除非我们需要在 Java 部分添加新代码)，从而实现某种持续交付开发。

但要小心这把双刃剑：我们现在需要非常小心地保护自己免受编译时和运行时可能发生的故障的影响，事实上确保我们的代码安全地失败。

## 8. 在 Java 项目中运行 Groovy 的陷阱

### 8.1. 表现

我们都知道，当一个系统需要非常高性能时，有一些黄金法则可以遵循。

可能对我们的项目影响更大的两个是：

-   避免反射
-   最小化字节码指令的数量

由于检查类、字段、方法、方法参数等的过程，反射尤其是一个代价高昂的操作。

如果我们分析从 Java 到 Groovy 的方法调用，例如，当运行示例addWithCompiledClasses时， .calcSum和实际 Groovy 方法的第一行之间的操作堆栈如下所示：

```java
calcSum:4, CalcScript (com.baeldung)
addWithCompiledClasses:43, MyJointCompilationApp (com.baeldung)
addWithStaticCompiledClasses:95, MyJointCompilationApp (com.baeldung)
main:117, App (com.baeldung)
```

这与Java一致。当我们转换加载器返回的对象并调用它的方法时，也会发生同样的情况。

但是，这是invokeMethod调用的作用：

```java
calcSum:4, CalcScript (com.baeldung)
invoke0:-1, NativeMethodAccessorImpl (sun.reflect)
invoke:62, NativeMethodAccessorImpl (sun.reflect)
invoke:43, DelegatingMethodAccessorImpl (sun.reflect)
invoke:498, Method (java.lang.reflect)
invoke:101, CachedMethod (org.codehaus.groovy.reflection)
doMethodInvoke:323, MetaMethod (groovy.lang)
invokeMethod:1217, MetaClassImpl (groovy.lang)
invokeMethod:1041, MetaClassImpl (groovy.lang)
invokeMethod:821, MetaClassImpl (groovy.lang)
invokeMethod:44, GroovyObjectSupport (groovy.lang)
invokeMethod:77, Script (groovy.lang)
addWithGroovyShell:52, MyJointCompilationApp (com.baeldung)
addWithDynamicCompiledClasses:99, MyJointCompilationApp (com.baeldung)
main:118, MyJointCompilationApp (com.baeldung)
```

在这种情况下，我们可以体会到 Groovy 强大功能背后的真正原因：MetaClass。

MetaClass定义任何给定的 Groovy 或 Java 类的行为，因此只要有要执行的动态操作，Groovy 就会查看它 以找到目标方法或字段。一旦找到，标准反射流就会执行它。

一种调用方法打破了两条黄金法则！

如果我们需要处理数百个动态 Groovy 文件，我们如何调用我们的方法将对我们的系统产生巨大的性能差异。

### 8.2. 未找到方法或属性

如前所述，如果我们想在 CD 生命周期中部署新版本的 Groovy 文件，我们需要将它们视为独立于我们的核心系统的 API。

这意味着要实施多重故障安全检查和代码设计限制，这样我们新加入的开发人员就不会因错误的推送而炸毁生产系统。

每个例子是：有一个 CI 管道和使用方法弃用而不是删除。

如果我们不这样做会怎样？由于缺少方法以及错误的参数计数和类型，我们会遇到可怕的异常。

如果我们认为编译可以拯救我们，让我们看看我们的 Groovy 脚本的方法calcSum2() ：

```java
// this method will fail in runtime
def calcSum2(x, y) {
    // DANGER! The variable "log" may be undefined
    log.info "Executing $x + $y"
    // DANGER! This method doesn't exist!
    calcSum3()
    // DANGER! The logged variable "z" is undefined!
    log.info("Logging an undefined variable: $z")
}
```

通过查看整个文件，我们立即发现两个问题：方法calcSum3()和变量 z没有在任何地方定义。

即便如此，脚本还是成功编译了，甚至没有一个警告，无论是在 Maven 中静态编译还是在 GroovyClassLoader 中动态编译。

只有当我们尝试调用它时它才会失败。

仅当我们的 Java 代码直接引用 calcSum3()时，Maven 的静态编译才会显示错误，就像我们在addWithCompiledClasses()方法中那样转换GroovyObject之后，但如果我们改用反射，它仍然无效。

## 9.总结

在本文中，我们探讨了如何将 Groovy 集成到我们的 Java 应用程序中，研究了不同的集成方法以及我们在使用混合语言时可能会遇到的一些问题。