## 1. 概述

Quarkus是一个由核心和一组扩展组成的框架。核心基于上下文和依赖注入(CDI)，扩展通常旨在通过将其主要组件公开为CDI bean来集成第三方框架。

在本教程中，我们将重点介绍如何在对[Quarkus](https://www.baeldung.com/quarkus-io)有基本了解的情况下编写Quarkus扩展。

## 2. 什么是Quarkus扩展

Quarkus扩展只是一个可以在Quarkus应用程序之上运行的模块。Quarkus应用程序本身是具有一组其他扩展的核心模块。

这种扩展最常见的用例是让第三方框架在Quarkus应用程序之上运行。

## 3. 在普通Java应用程序中运行Liquibase

让我们尝试实现一个集成[Liquibase](https://www.liquibase.org/)的扩展，Liquibase是一种数据库变更管理工具。

但在我们深入研究之前，我们首先需要展示如何从Java main方法运行Liquibase迁移。这将极大地促进扩展的实施。

Liquibase框架的入口点是Liquibase API。要使用它，我们需要一个变更日志文件、一个用于访问该文件的类加载器以及一个到底层数据库的连接：

```java
Connection c = DriverManager.getConnection("jdbc:h2:mem:testdb", "user", "password");
ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
String changLogFile = "db/liquibase-changelog-master.xml";
Liquibase liquibase = new Liquibase(changLogFile, resourceAccessor, new JdbcConnection(c));
```

有了这个实例，我们只需调用update()方法来更新数据库以匹配变更日志文件。

```java
liquibase.update(new Contexts());
```

目标是将Liquibase作为Quarkus扩展公开。也就是说，通过Quarkus Configuration提供数据库配置和变更日志文件，然后将Liquibase API作为CDI bean生成。这提供了一种记录迁移调用以供以后执行的方法。

## 4. 如何编写Quarkus扩展

从技术上讲，Quarkus扩展是由两个模块组成的Maven多模块项目。第一个是我们实现需求的运行时模块。第二个是用于处理配置和生成运行时代码的部署模块。

因此，让我们首先创建一个名为quarkus-liquibase-parent的Maven多模块项目，它包含两个子模块，runtime和deployment：

```xml
<modules>
    <module>runtime</module>
    <module>deployment</module>
</modules>
```

## 5. 运行时模块的实现

在运行时模块中，我们将实现：

-   用于捕获Liquibase变更日志文件的配置类
-   用于公开Liquibase API的CDI生产者
-   以及充当记录调用调用代理的记录器

### 5.1 Maven依赖项和插件

运行时模块将依赖于[quarkus-core](https://search.maven.org/search?q=a:quarkus-core)模块，并最终依赖于所需扩展的运行时模块。在这里，我们需要[quarkus-agroal](https://search.maven.org/artifact/io.quarkus/quarkus-agroal)依赖项，因为我们的扩展需要一个数据源。我们还将在此处包含[Liquibase库](https://search.maven.org/search?q=a:liquibase-core)：

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-core</artifactId>
    <version>${quarkus.version}</version>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-agroal</artifactId>
    <version>${quarkus.version}</version>
</dependency>
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
    <version>3.8.1</version>
</dependency>
```

此外，我们可能需要添加[quarkus-bootstrap-maven-plugin](https://search.maven.org/artifact/io.quarkus/quarkus-bootstrap-maven-plugin)。该插件通过调用extension-descriptor目标自动生成Quarkus扩展描述符。

或者，我们可以省略这个插件并手动生成描述符。

无论哪种方式，我们都可以找到位于META-INF/quarkus-extension.properties下的扩展描述符：

```shell
deployment-artifact=cn.tuyucheng.taketoday.quarkus.liquibase:deployment:1.0.0
```

### 5.2 公开配置

为了提供变更日志文件，我们需要实现一个配置类：

```java
@ConfigRoot(name = "liquibase", phase = ConfigPhase.BUILD_AND_RUN_TIME_FIXED)
public final class LiquibaseConfig {
    @ConfigItem
    public String changeLog;
}
```

我们用@ConfigRoot标注类，用@ConfigItem标注属性。因此，changeLog字段(change-log的驼峰式形式)将通过位于Quarkus应用程序类路径中的application.properties文件中的quarkus.liquibase.change-log属性提供：

```properties
quarkus.liquibase.change-log=db/liquibase-changelog-master.xml
```

我们还可以注意到@ConfigRoot.phase的值，它指示何时解析更改日志键。在这种情况下，BUILD_AND_RUN_TIME_FIXED，键在部署时读取并在运行时可供应用程序使用。

### 5.3 将Liquibase API公开为CDI Bean

我们在上面看到了如何从main方法运行Liquibase迁移。

现在，我们将重现相同的代码，但作为CDI bean，我们将为此目的使用CDI生产者：

```java
@Produces
public Liquibase produceLiquibase() throws Exception {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(classLoader);
    DatabaseConnection jdbcConnection = new JdbcConnection(dataSource.getConnection());
    Liquibase liquibase = new Liquibase(liquibaseConfig.changeLog, resourceAccessor, jdbcConnection);
    return liquibase;
}
```

### 5.4 记录字节码

在这一步中，我们将编写一个记录器类，作为记录字节码和设置运行时逻辑的代理：

```java
@Recorder
public class LiquibaseRecorder {

    public BeanContainerListener setLiquibaseConfig(LiquibaseConfig liquibaseConfig) {
        return beanContainer -> {
            LiquibaseProducer producer = beanContainer.instance(LiquibaseProducer.class);
            producer.setLiquibaseConfig(liquibaseConfig);
        };
    }

    public void migrate(BeanContainer container) throws LiquibaseException {
        Liquibase liquibase = container.instance(Liquibase.class);
        liquibase.update(new Contexts());
    }
}
```

在这里，我们必须记录两次调用。setLiquibaseConfig用于设置配置，migrate用于执行迁移。接下来，我们将了解我们将在部署模块中实现的部署构建步骤处理器如何调用这些记录器方法。

请注意，当我们在构建时调用这些记录器方法时，指令不会被执行，而是被记录下来以供稍后在启动时执行。

## 6. 实现部署模块

Quarkus扩展中的核心组件是构建步骤处理器。它们是通过记录器生成字节码的标注为@BuildStep的方法，它们在构建期间通过在Quarkus应用程序中配置的quarkus-maven-plugin的构建目标执行。

由于BuildItems，@BuildSteps被记录。它们使用由早期构建步骤生成的构建项，并为后续构建步骤生成构建项。

在应用程序部署模块中找到的所有有序构建步骤生成的代码实际上是运行时代码。

### 6.1 Maven依赖项

部署模块应该依赖于相应的运行时模块，并最终依赖于所需扩展的部署模块：

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-core-deployment</artifactId>
    <version>${quarkus.version}</version>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-arc-deployment</artifactId>
    <version>${quarkus.version}</version>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-agroal-deployment</artifactId>
    <version>${quarkus.version}</version>
</dependency>

<dependency>
    <groupId>com.baeldung.quarkus.liquibase</groupId>
    <artifactId>runtime</artifactId>
    <version>${project.version}</version>
</dependency>
```

最新稳定版的Quarkus扩展与运行时模块相同。

### 6.2 实施构建步骤处理器

现在，让我们实现两个用于记录字节码的构建步骤处理器。第一个构建步骤处理器是build()方法，它将记录字节码以在静态init方法中执行。我们通过STATIC_INIT值配置它：

```java
@Record(ExecutionTime.STATIC_INIT)
@BuildStep
void build(BuildProducer<AdditionalBeanBuildItem> additionalBeanProducer,
    BuildProducer<FeatureBuildItem> featureProducer,
    LiquibaseRecorder recorder,
    BuildProducer<BeanContainerListenerBuildItem> containerListenerProducer,
    DataSourceInitializedBuildItem dataSourceInitializedBuildItem) {

    featureProducer.produce(new FeatureBuildItem("liquibase"));

    AdditionalBeanBuildItem beanBuilItem = AdditionalBeanBuildItem.unremovableOf(LiquibaseProducer.class);
    additionalBeanProducer.produce(beanBuilItem);

    containerListenerProducer.produce(new BeanContainerListenerBuildItem(recorder.setLiquibaseConfig(liquibaseConfig)));
}
```

首先，我们创建一个FeatureBuildItem来标记扩展的类型或名称。然后，我们创建一个AdditionalBeanBuildItem，以便LiquibaseProducer bean可用于Quarkus容器。

最后，我们创建一个BeanContainerListenerBuildItem以便在QuarkusBeanContainer启动后触发BeanContainerListener。在这里，在监听器中，我们将配置传递给Liquibase bean。

反过来，processMigration()将记录调用以在main方法中执行，因为它是使用RUNTIME_INIT参数进行配置以进行记录的。

```java
@Record(ExecutionTime.RUNTIME_INIT)
@BuildStep
void processMigration(LiquibaseRecorder recorder,BeanContainerBuildItem beanContainer) throws LiquibaseException {
    recorder.migrate(beanContainer.getValue());
}
```

在这里，在这个处理器中，我们只是调用了migrate()记录器方法，它又记录了update()Liquibase方法以供以后执行。

## 7. 测试Liquibase扩展

为了测试我们的扩展，我们首先使用quarkus-maven-plugin创建一个Quarkus应用程序：

```bash
mvn io.quarkus:quarkus-maven-plugin:1.0.0.CR1:create\
-DprojectGroupId=cn.tuyucheng.taketoday.quarkus.app\
-DprojectArtifactId=quarkus-app
```

接下来，除了与我们的底层数据库相对应的[Quarkus JDBC扩展](https://search.maven.org/artifact/io.quarkus/quarkus-jdbc-h2)之外，我们还将添加我们的扩展作为依赖项：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday.quarkus.liquibase</groupId>
    <artifactId>runtime</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-h2</artifactId>
    <version>1.0.0.CR1</version>
</dependency>
```

接下来，我们需要在我们的pom文件中包含[quarkus-maven-plugin](https://search.maven.org/artifact/io.quarkus/quarkus-maven-plugin)：

```xml
<plugin>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-maven-plugin</artifactId>
    <version>${quarkus.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>build</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

这对于使用开发目标运行应用程序或使用构建目标构建可执行文件特别有用。

接下来，我们将通过位于src/main/resources中的application.properties文件提供数据源配置：

```properties
quarkus.datasource.url=jdbc:h2:mem:testdb
quarkus.datasource.driver=org.h2.Driver
quarkus.datasource.username=user
quarkus.datasource.password=password
```

接下来，我们将为我们的变更日志文件提供变更日志配置：

```properties
quarkus.liquibase.change-log=db/liquibase-changelog-master.xml
```

最后，我们可以在开发模式下启动应用程序：

```shell
mvn compile quarkus:dev
```

或者在生产模式下：

```shell
mvn clean package
java -jar target/quarkus-app-1.0.0-runner.jar
```

## 8. 总结

在本文中，我们实现了一个Quarkus扩展。例如，我们展示了如何让Liquibase在Quarkus应用程序之上运行。