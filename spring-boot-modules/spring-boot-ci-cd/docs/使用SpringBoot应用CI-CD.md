## 1. 概述

在本教程中，我们介绍持续集成/持续部署(CI/CD)流程并实现其重要部分。

我们将创建一个简单的Spring Boot应用程序，然后将其推送到共享的Git仓库。之后，我们将使用构建集成服务构建它，创建Docker镜像，并将其推送到Docker仓库。

最后，我们会自动将我们的应用程序部署到PaaS服务(Heroku)。

## 2. 版本控制

CI/CD的关键部分是管理我们代码的版本控制系统。此外，我们需要一个仓库托管服务，我们的构建和部署步骤将绑定到该服务中。

我们选择[Git](https://git-scm.com/)作为VCS和[GitHub](https://github.com/)作为我们的仓库提供者，因为它们是目前最流行的并且可以免费使用。

首先，我们必须在GitHub上[创建一个帐户](https://github.com/join)。

此外，我们应该[创建一个Git仓库](https://github.com/new)，我们将其命名为tuyucheng-ci-cd-process。另外，让我们选择一个公共仓库，因为它将允许我们免费访问其他服务。最后，让我们使用README.md初始化我们的仓库。

现在我们的仓库已经创建，我们应该在本地克隆我们的项目。为此，让我们在本地计算机上执行此命令：

```bash
git clone https://github.com/$USERNAME/tuyucheng-ci-cd-process.git
```

这将在我们执行命令的目录中初始化我们的项目，目前，它应该只包含README.md文件。

## 3. 创建应用程序

在本节中，我们将创建一个将参与该过程的简单Spring Boot应用程序，这里使用Maven作为我们的构建工具。

首先，我们在克隆版本控制仓库的目录中初始化我们的项目。例如，我们可以使用[Spring Initializer](https://start.spring.io/)来做到这一点，添加web和actuator模块。

### 3.1 手动创建应用程序

或者，我们可以手动添加[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)和[spring-boot-starter-actuator](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-actuator)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

第一个是引入REST端点，第二个是健康检查端点。

此外，我们可以添加允许我们运行应用程序的插件：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
</plugin>
```

最后，让我们添加一个Spring Boot主类：

```java
@SpringBootApplication
public class CiCdApplication {

    public static void main(String[] args) {
        SpringApplication.run(CiCdApplication.class, args);
    }
}
```

### 3.2 推送

无论是使用Spring Initializr还是手动创建项目，我们现在都可以提交更改并将其推送到仓库。

我们使用以下命令来执行此操作：

```bash
git add .
git commit -m 'Initialize application'
git push
```

现在，我们可以检查我们的更改是否存在于仓库中。

## 4. 构建自动化

CI/CD过程的另一部分是将构建和测试我们推送的代码的服务。

我们将在这里使用[Travis CI](https://travis-ci.com/)，但任何构建服务也可以使用。

### 4.1 Maven包装器

首先向我们的应用程序添加一个[Maven Wrapper](https://github.com/takari/maven-wrapper)，如果我们使用了Spring Initializr，我们可以跳过这部分，因为它是默认包含的。

在应用程序目录中，执行以下命令：

```bash
mvn -N io.takari:maven:0.7.7:wrapper
```

这将添加Maven包装器文件，包括可以用来代替Maven的mvnw和mvnw.cmd文件。

虽然Travis CI有自己的Maven，但其他构建服务可能没有。这个Maven Wrapper将帮助我们为这两种情况做好准备。此外，开发人员不必在他们的机器上安装Maven。

### 4.2 构建服务

之后，通过使用我们的GitHub帐户[登录来在Travis CI上创建一个帐户](https://travis-ci.com/signin)。展望未来，我们应该允许访问我们在GitHub中的项目。

接下来，我们应该创建一个.travis.yml文件，该文件将描述Travis CI中的构建过程。大多数构建服务都允许我们创建这样一个文件，它位于我们仓库的根目录下。

在我们的例子中，我们告诉Travis使用Java 11和Maven Wrapper来构建我们的应用程序：

```yaml
language: java
jdk:
    - openjdk11
script:
    - ./mvnw clean install
```

language属性表示我们要使用Java。

jdk属性表示要从DockerHub下载哪个Docker镜像，在本例中为openjdk11。

script属性说明要运行什么命令-我们想使用我们的Maven包装器。

最后，我们应该将更改推送到仓库，Travis CI应该会自动触发构建。

## 5. 容器化

在本节中，我们将使用我们的应用程序构建一个Docker镜像，并将其作为CD过程的一部分托管在[DockerHub](https://hub.docker.com/)上，它将使我们能够轻松地在任何机器上运行它。

### 5.1 Docker镜像仓库

首先，我们应该为我们的镜像创建一个Docker仓库。

让我们在[DockerHub](https://hub.docker.com/signup)上创建一个帐户。另外，我们通过填写适当的字段来为我们的项目[创建仓库](https://docs.docker.com/docker-hub/repos/)：

-   name：tuyucheng-ci-cd-process
-   visibility：Public
-   Build setting：GitHub

### 5.2 Docker镜像

现在，我们已准备好创建一个Docker镜像并将其推送到DockerHub。

首先，我们添加[jib-maven-plugin](https://search.maven.org/search?q=g:com.google.cloud.tools AND a:jib-maven-plugin)，它将创建我们的镜像并将其与应用程序一起推送到Docker仓库(将[DockerHubUsername](https://search.maven.org/search?q=g:com.google.cloud.tools AND a:jib-maven-plugin)替换为正确的用户名)：

```xml
<profile>
    <id>deploy-docker</id>
    <properties>
        <maven.deploy.skip>true</maven.deploy.skip>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>com.google.cloud.tools</groupId>
                <artifactId>jib-maven-plugin</artifactId>
                <version>2.2.0</version>
                <configuration>
                    <to>
                        <image>${DockerHubUsername}/tuyucheng-ci-cd-process</image>
                        <tags>
                            <tag>${project.version}</tag>
                            <tag>latest</tag>
                        </tags>
                    </to>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

我们已将其添加为Maven Profile的一部分，以便不使用默认构建触发它。

此外，我们还为镜像指定了两个标签。要了解有关该插件的更多信息，请访问我们[关于Jib的文章]()。

接下来，调整我们的构建文件(.travis.yml)：

```yaml
before_install:
    - echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
    - docker pull openjdk:11-jre-slim-sid

script:
    - ./mvnw clean install
    - ./mvnw deploy jib:build -P deploy-docker
```

通过这些更改，构建服务将在构建应用程序之前登录到DockerHub。此外，它将使用我们的配置文件执行部署阶段，在此阶段，我们的应用程序将作为镜像推送到Docker仓库。

最后，我们应该在构建服务中定义DOCKER_PASSWORD和DOCKER_USERNAME变量，在Travis CI中，这些变量可以定义为[构建设置](https://docs.travis-ci.com/user/environment-variables/)的一部分。

现在，让我们将更改推送到VCS，构建服务应该会根据我们的更改自动触发构建。

我们可以通过在本地运行来检查Docker镜像是否已经被推送到仓库中：

```bash
docker run -p 8080:8080 -t $DOCKER_USERNAME/tuyucheng-ci-cd-process
```

现在，我们应该能够通过访问http://localhost:8080/actuator/health来访问我们的健康检查。

## 6. 代码分析

我们将在CI/CD过程中包括的下一件事是静态代码分析，该过程的主要目标是确保最高的代码质量。例如，它可以检测到我们没有足够的测试用例或者我们有一些安全问题。

让我们与[CodeCov](https://codecov.io/)集成，它将告知我们关于测试覆盖率的信息。

首先，我们应该使用我们的GitHub个人资料登录CodeCov以建立集成。

其次，我们应该对代码进行更改，首先我们添加[jacoco插件](https://search.maven.org/search?q=g:org.jacoco AND a:jacoco-maven-plugin)：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.5</version>
    <executions>
        <execution>
            <id>default-prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

该插件负责生成将由CodeCov使用的测试报告。

接下来，我们应该调整构建服务文件(.travis.yml )中的脚本部分：

```yaml
script:
    - ./mvnw clean org.jacoco:jacoco-maven-plugin:prepare-agent install
    - ./mvnw deploy jib:build -P deploy-docker

after_success:
    - bash <(curl -s https://codecov.io/bash)
```

我们指示jacoco插件在clean install阶段触发。此外，我们还包含了after_success部分，它将在构建成功后将报告发送到CodeCov。

接下来，我们应该向我们的应用程序添加一个测试类。例如，它可以是对主类的测试：

```java
@SpringBootTest
class CiCdApplicationIntegrationTest {

    @Test
    void contextLoads() {

    }
}
```

最后，我们应该推送我们的改变，然后应该触发构建，并且应该在我们与仓库相关的CodeCov配置文件中生成报告。

## 7. 部署应用

作为流程的最后一部分，我们将部署我们的应用程序。有了可供使用的Docker镜像，我们就可以将它部署到任何服务上。例如，我们可以将其部署在基于云的PaaS或IaaS上。

让我们将我们的应用程序部署到Heroku，这是一个需要最少设置的PaaS。

首先，我们应该[创建一个帐户](https://signup.heroku.com/)，然后[登录](https://id.heroku.com/login)。

接下来，让我们在Heroku中创建[应用程序空间](https://dashboard.heroku.com/new-app)并将其命名为tuyucheng-ci-cd-process，应用程序的名称必须是唯一的，因此我们可能需要使用另一个名称。

我们通过[将Heroku与GitHub集成](https://devcenter.heroku.com/articles/github-integration)来部署它，因为这是最简单的解决方案。但是，我们也可以编写使用我们的Docker镜像的管道。

展望未来，我们应该在 pom 中包含[heroku插件：](https://search.maven.org/search?q=g:com.heroku.sdk AND a:heroku-maven-plugin)

```xml
<profile>
    <id>deploy-heroku</id>
    <properties>
        <maven.deploy.skip>true</maven.deploy.skip>
    </properties>
    <build>
        <plugins>
            <plugin>
                <groupId>com.heroku.sdk</groupId>
                <artifactId>heroku-maven-plugin</artifactId>
                <version>3.0.2</version>
                <configuration>
                    <appName>spring-boot-ci-cd</appName>
                    <processTypes>
                        <web>java $JAVA_OPTS -jar -Dserver.port=$PORT target/${project.build.finalName}.jar</web>
                    </processTypes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

与Docker一样，我们已将其添加为Maven Profile的一部分。此外，我们还在<web\>标签中包含了一个启动命令。

接下来，调整我们的构建服务文件(.travis.yml)以将应用程序部署到Heroku：

```yaml
script:
    - ./mvnw clean install
    - ./mvnw heroku:deploy jib:build -P deploy-heroku,deploy-docker
```

此外，让我们在构建服务的HEROKU_API_KEY变量中添加Heroku API-KEY。

最后，提交我们的更改。构建完成后，应将应用程序部署到Heroku。

我们可以通过访问[https://tuyucheng-ci-cd-process.herokuapp.com/actuator/health](https://baeldung-ci-cd-process.herokuapp.com/actuator/health)来检查它。

## 8. 总结

在本文中，我们了解了CI/CD过程的基本部分是什么以及如何准备它们。

首先，我们在GitHub中准备了一个Git仓库，并将我们的应用程序推送到那里。然后，我们使用Travis CI作为构建工具，从该仓库构建我们的应用程序。之后，我们创建了一个Docker镜像并将其推送到DockerHub。接下来，我们添加了一个负责静态代码分析的服务。最后，我们将应用程序部署到PaaS并访问它。