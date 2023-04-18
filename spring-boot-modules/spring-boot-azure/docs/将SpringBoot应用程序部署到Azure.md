## 1. 简介

Microsoft Azure现在具有非常可靠的Java支持。

在本教程中，我们将逐步演示如何使我们的Spring Boot应用程序在Azure平台上运行。

## 2. Maven依赖及配置

首先，**我们需要Azure订阅才能使用那里的云服务**；目前，我们可以在[这里](https://azure.microsoft.com/en-us/free/)注册一个免费帐户。

接下来，登录平台并使用[Azure CLI](https://docs.microsoft.com/en-us/cli/azure/index?view=azure-cli-latest)创建服务主体：

```shell
> az login
To sign in, use a web browser to open the page \
https://microsoft.com/devicelogin and enter the code XXXXXXXX to authenticate.
```

```shell
> az ad sp create-for-rbac --name "app-name" --password "password"
{
    "appId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
    "displayName": "app-name",
    "name": "http://app-name",
    "password": "password",
    "tenant": "tttttttt-tttt-tttt-tttt-tttttttttttt"
}
```

现在，**我们在Maven [settings.xml](https://maven.apache.org/settings.html)中配置Azure服务主体身份验证设置**，借助以下部分的<servers\>：

```xml
<server>
    <id>azure-auth</id>
    <configuration>
        <client>aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa</client>
        <tenant>tttttttt-tttt-tttt-tttt-tttttttttttt</tenant>
        <key>password</key>
        <environment>AZURE</environment>
    </configuration>
</server>
```

**在使用azure-webapp-maven-plugin将我们的Spring Boot应用程序上传到Microsoft平台时，我们将依赖上面的身份验证配置**。

让我们将以下Maven插件添加到pom.xml中：

```xml
<plugin>
    <groupId>com.microsoft.azure</groupId>
    <artifactId>azure-webapp-maven-plugin</artifactId>
    <version>1.1.0</version>
    <configuration>
        <!-- ... -->
    </configuration>
</plugin>
```

我们可以在[这里](https://central.sonatype.com/artifact/com.microsoft.azure/azure-webapp-maven-plugin/2.10.0)查看最新的发布版本。

这个插件有许多可配置的属性，将在下面的小节中介绍。

## 3. 将Spring Boot应用程序部署到Azure

现在我们已经设置了环境，让我们尝试将Spring Boot应用程序部署到Azure。

当我们访问“/hello”时，我们的应用程序回复“hello azure!”：

```java
@GetMapping("/hello")
public String hello() {
    return "hello azure!";
}
```

该平台现在允许为Tomcat和Jetty部署Java Web App。使用azure-webapp-maven-plugin，我们可以将我们的应用程序作为默认(ROOT)应用程序直接部署到支持的Web容器，或通过FTP部署。

请注意，当我们要将应用程序部署到Web容器时，我们应该将其打包为WAR存档。作为快速提醒，我们有一篇文章介绍了[如何将Spring Boot WAR部署到Tomcat中](https://www.baeldung.com/spring-boot-war-tomcat-deploy)。

### 3.1 Web容器部署

如果我们打算部署到Windows实例上的Tomcat，我们将为azure-webapp-maven-plugin使用以下配置：

```xml
<configuration>
    <javaVersion>1.8</javaVersion>
    <javaWebContainer>tomcat 8.5</javaWebContainer>
    <!-- ... -->
</configuration>
```

对于Linux实例，请尝试以下配置：

```xml
<configuration>
    <linuxRuntime>tomcat 8.5-jre8</linuxRuntime>
    <!-- ... -->
</configuration>
```

不要忘记Azure身份验证：

```xml
<configuration>
    <authentication>
        <serverId>azure-auth</serverId>
    </authentication>
    <appName>spring-azure</appName>
    <resourceGroup>tuyucheng</resourceGroup>
    <!-- ... -->
</configuration>
```

将应用程序部署到Azure时，我们会看到它显示为App Service。所以这里我们指定属性<appName\>来命名应用服务。另外，App Service作为一种资源，需要由一个[资源组容器](https://docs.microsoft.com/en-us/azure/azure-resource-manager/resource-group-overview)来承载，因此<resourceGroup\>也是必需的。

**现在我们准备好使用azure-webapp:deploy Maven目标来触发**，我们将看到输出：

```shell
> mvn clean package azure-webapp:deploy
...
[INFO] Start deploying to Web App spring-tuyucheng...
[INFO] Authenticate with ServerId: azure-auth
[INFO] [Correlation ID: cccccccc-cccc-cccc-cccc-cccccccccccc] \
Instance discovery was successful
[INFO] Target Web App doesn't exist. Creating a new one...
[INFO] Creating App Service Plan 'ServicePlanssssssss-bbbb-0000'...
[INFO] Successfully created App Service Plan.
[INFO] Successfully created Web App.
[INFO] Starting to deploy the war file...
[INFO] Successfully deployed Web App at \
https://spring-tuyucheng.azurewebsites.net
...
```

现在我们可以**访问[https://spring-tuyucheng.azurewebsites.net/hello](https://spring-tuyucheng.azurewebsites.net/hello)并查看响应**：'hello azure!'。

在部署过程中，Azure自动为我们创建了一个App Service Plan。查看[官方文档](https://docs.microsoft.com/en-us/azure/app-service/azure-web-sites-web-hosting-plans-in-depth-overview)以了解有关Azure应用服务计划的详细信息。如果我们已经有应用服务计划，我们可以设置属性<appServicePlanName\>以避免创建新计划：

```xml
<configuration>
    <!-- ... -->
    <appServicePlanName>ServicePlanssssssss-bbbb-0000</appServicePlanName>
</configuration>
```

### 3.2 FTP部署

要通过FTP部署，我们可以使用以下配置：

```xml
<configuration>
    <authentication>
        <serverId>azure-auth</serverId>
    </authentication>
    <appName>spring-tuyucheng</appName>
    <resourceGroup>tuyucheng</resourceGroup>
    <javaVersion>1.8</javaVersion>

    <deploymentType>ftp</deploymentType>
    <resources>
        <resource>
            <directory>${project.basedir}/target</directory>
            <targetPath>webapps</targetPath>
            <includes>
                <include>*.war</include>
            </includes>
        </resource>
    </resources>
</configuration>
```

在上面的配置中，我们让插件在目录${project.basedir}/target中寻找WAR文件，并将其部署到Tomcat容器的webapps目录中。

假设我们的最终工件名为azure-1.0.0.war，一旦开始部署，我们将看到如下输出：

```shell
> mvn clean package azure-webapp:deploy
...
[INFO] Start deploying to Web App spring-tuyucheng...
[INFO] Authenticate with ServerId: azure-auth
[INFO] [Correlation ID: cccccccc-cccc-cccc-cccc-cccccccccccc] \
Instance discovery was successful
[INFO] Target Web App doesn't exist. Creating a new one...
[INFO] Creating App Service Plan 'ServicePlanxxxxxxxx-xxxx-xxxx'...
[INFO] Successfully created App Service Plan.
[INFO] Successfully created Web App.
...
[INFO] Finished uploading directory: \
/xxx/.../target/azure-webapps/spring-tuyucheng --> /site/wwwroot
[INFO] Successfully uploaded files to FTP server: \
xxxx-xxxx-xxx-xxx.ftp.azurewebsites.windows.net
[INFO] Successfully deployed Web App at \
https://spring-tuyucheng.azurewebsites.net
```

请注意，这里我们没有将我们的应用程序部署为Tomcat的默认Web应用程序，因此我们只能通过[https://spring-baeldung.azurewebsites.net/azure-1.0.0/hello](https://spring-baeldung.azurewebsites.net/azure-1.0.0/hello)访问它。服务器将按预期响应“hello azure!”。

## 4. 使用自定义应用程序设置进行部署

大多数时候，我们的Spring Boot应用程序需要访问数据才能提供服务。Azure现在支持SQL Server、MySQL和PostgreSQL等数据库。

为了简单起见，我们将使用它的In-App MySQL作为我们的数据源，因为它的配置与其他Azure数据库服务非常相似。

### 4.1 在Azure上启用应用内MySQL

由于没有单行代码来创建启用了In-App MySQL的Web应用程序，因此我们必须首先使用CLI创建Web应用程序：

```shell
az group create --location japanwest --name tuyucheng-group
az appservice plan create --name tuyucheng-plan --resource-group tuyucheng-group --sku B1
az webapp create --name tuyucheng-webapp --resource-group tuyucheng-group \
  --plan tuyucheng-plan --runtime java|1.8|Tomcat|8.5
```

然后在门户中的App中启用MySQL：

<img src="../assets/img.png">

启用In-App MySQL后，我们可以在文件系统/home/data/mysql目录下一个名为MYSQLCONNSTR_xxx.txt的文件中找到默认数据库、数据源URL和默认账户信息。

### 4.2 使用Azure In-App MySQL的Spring Boot应用程序

在这里，为了演示需要，我们创建了一个User实体和两个用于注册和列出用户的端点：

```java
@PostMapping("/user")
public String register(@RequestParam String name) {
    userRepository.save(userNamed(name));
    return "registered";
}

@GetMapping("/user")
public Iterable<User> userlist() {
    return userRepository.findAll();
}
```

我们将在本地环境中使用H2数据库，并将其切换到Azure上的MySQL。一般我们在application.properties文件中配置数据源属性：

```properties
spring.datasource.url=jdbc:h2:file:~/test
spring.datasource.username=sa
spring.datasource.password=
```

而对于Azure部署，**我们需要在azure-webapp-maven-plugin中配置<appSettings\>**：

```xml
<configuration>
    <authentication>
        <serverId>azure-auth</serverId>
    </authentication>
    <javaVersion>1.8</javaVersion>
    <resourceGroup>tuyucheng-group</resourceGroup>
    <appName>tuyucheng-webapp</appName>
    <appServicePlanName>tuyucheng-plan</appServicePlanName>
    <appSettings>
        <property>
            <name>spring.datasource.url</name>
            <value>jdbc:mysql://127.0.0.1:55738/localdb</value>
        </property>
        <property>
            <name>spring.datasource.username</name>
            <value>uuuuuu</value>
        </property>
        <property>
            <name>spring.datasource.password</name>
            <value>pppppp</value>
        </property>
    </appSettings>
</configuration>
```

现在我们可以开始部署了：

```shell
> mvn clean package azure-webapp:deploy
...
[INFO] Start deploying to Web App custom-webapp...
[INFO] Authenticate with ServerId: azure-auth
[INFO] [Correlation ID: cccccccc-cccc-cccc-cccc-cccccccccccc] \
Instance discovery was successful
[INFO] Updating target Web App...
[INFO] Successfully updated Web App.
[INFO] Starting to deploy the war file...
[INFO] Successfully deployed Web App at \
https://tuyucheng-webapp.azurewebsites.net
```

从日志中我们可以看出部署已经完成。

让我们测试我们的新端点：

```shell
> curl -d "" -X POST https://tuyucheng-webapp.azurewebsites.net/user\?name\=tuyucheng
registered

> curl https://tuyucheng-webapp.azurewebsites.net/user
[{"id":1,"name":"tuyucheng"}]
```

## 5. 将容器化的Spring Boot应用程序部署到Azure

在前面的部分中，我们展示了如何将应用程序部署到Servlet容器(本例中为Tomcat)。如何部署为独立的可运行jar？

现在，我们可能需要容器化我们的Spring Boot应用程序。具体来说，我们可以将其docker化，并将镜像上传到Azure。

我们已经有一篇关于[如何对Spring Boot App进行docker化](https://www.baeldung.com/dockerizing-spring-boot-application)的文章，但在这里**我们将使用另一个Maven插件docker-maven-plugin来为我们自动进行docker化**：

```xml
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>1.1.0</version>
    <configuration>
        <!-- ... -->
    </configuration>
</plugin>
```

最新版本可以在[这里](https://central.sonatype.com/artifact/com.spotify/docker-maven-plugin/1.2.2)找到。

### 5.1 Azure容器注册表

首先，**我们需要Azure上的Container Registry来上传我们的docker镜像**。

因此，让我们创建一个：

```shell
az acr create --admin-enabled --resource-group tuyucheng-group \
  --location japanwest --name tuyuchengadr --sku Basic
```

我们还需要Container Registry的身份验证信息，可以使用以下方式查询：

```shell
> az acr credential show --name tuyuchengadr --query passwords[0]
{
    "additionalProperties": {},
    "name": "password",
    "value": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
```

然后在Maven的settings.xml中添加如下服务器认证配置：

```xml
<server>
    <id>tuyuchengadr</id>
    <username>tuyuchengadr</username>
    <password>xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</password>
</server>
```

### 5.2 Maven插件配置

让我们将以下Maven插件配置添加到pom.xml中：

```xml
<properties>
    <!-- ... -->
    <azure.containerRegistry>tuyuchengadr</azure.containerRegistry>
    <docker.image.prefix>${azure.containerRegistry}.azurecr.io</docker.image.prefix>
</properties>

<build>
<plugins>
    <plugin>
        <groupId>com.spotify</groupId>
        <artifactId>docker-maven-plugin</artifactId>
        <version>1.0.0</version>
        <configuration>
            <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
            <registryUrl>https://${docker.image.prefix}</registryUrl>
            <serverId>${azure.containerRegistry}</serverId>
            <dockerDirectory>docker</dockerDirectory>
            <resources>
                <resource>
                    <targetPath>/</targetPath>
                    <directory>${project.build.directory}</directory>
                    <include>${project.build.finalName}.jar</include>
                </resource>
            </resources>
        </configuration>
    </plugin>
    <!-- ... -->
</plugins>
</build>
```

在上面的配置中，我们指定了docker镜像名称、注册表URL和一些类似于FTP部署的属性。

请注意，该插件将使用<dockerDirectory\>中的值来定位Dockerfile。我们把Dockerfile放在docker目录下，它的内容是：

```dockerfile
FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD azure-1.0.0.jar app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
```

### 5.3 在Docker实例中运行Spring Boot应用程序

现在我们可以构建一个Docker镜像并将其推送到Azure注册表：

```shell
> mvn docker:build -DpushImage
...
[INFO] Building image tuyuchengadr.azurecr.io/azure-0.1
...
Successfully built aaaaaaaaaaaa
Successfully tagged tuyuchengadr.azurecr.io/azure-0.1:latest
[INFO] Built tuyuchengadr.azurecr.io/azure-0.1
[INFO] Pushing tuyuchengadr.azurecr.io/azure-0.1
The push refers to repository [tuyuchengadr.azurecr.io/azure-0.1]
...
latest: digest: sha256:0f0f... size: 1375
```

上传完成后，让我们检查一下tuyuchengadr注册表。我们将在存储库列表中看到该镜像：

<img src="../assets/img_1.png">

现在我们准备运行镜像的实例：

<img src="../assets/img_2.png">

启动实例后，我们可以通过其公共IP地址访问应用程序提供的服务：

```shell
> curl http://a.x.y.z:8080/hello
hello azure!
```

### 5.4 Docker容器部署

假设我们有一个容器注册表，无论它来自Azure、DockerHub还是我们的私有注册表。

借助以下azure-webapp-maven-plugin配置，我们还可以将Spring Boot Web应用程序部署到容器中：

```xml
<configuration>
    <containerSettings>
        <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
        <registryUrl>https://${docker.image.prefix}</registryUrl>
        <serverId>${azure.containerRegistry}</serverId>
    </containerSettings>
    <!-- ... -->
</configuration>
```

一旦我们运行mvn azure-webapp:deploy，该插件将帮助将我们的Web应用程序存档部署到指定镜像的实例。

然后我们可以通过实例的IP地址或Azure App Service的URL访问Web服务。

## 6. 总结

在本文中，我们介绍了如何将Spring Boot应用程序部署到Azure，作为容器中的可部署WAR或可运行JAR。虽然我们已经介绍了azure-webapp-maven-plugin的大部分功能，但还有一些丰富的功能有待探索。请在[此处](https://github.com/Microsoft/azure-maven-plugins/tree/master/azure-webapp-maven-plugin)查看更多详细信息。