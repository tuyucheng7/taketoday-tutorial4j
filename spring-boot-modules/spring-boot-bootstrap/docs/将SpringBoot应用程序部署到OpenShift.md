## 一、概述

在本教程中，我们将展示如何 使用 Spring Boot 教程将应用程序从[Bootstrap a Simple Application部署到](https://www.baeldung.com/spring-boot-start)[Openshift](https://www.openshift.com/)。

作为其中的一部分，我们将：

-   安装和配置 Openshift 开发工具。
-   创建 Openshift 项目和 MySQL 部署。
-   [为Spring Cloud Kubernetes](https://github.com/spring-cloud/spring-cloud-kubernetes)配置应用程序。
-   [使用Fabric8 Maven 插件](https://github.com/fabric8io/fabric8-maven-plugin)在容器中创建和部署应用程序，并测试和扩展应用程序。

## 2. 开班配置

首先，我们需要[安装 Minishift](https://docs.okd.io/latest/minishift/getting-started/installing.html)、本地单节点 Openshift 集群和[Openshift 客户端](https://docs.okd.io/latest/cli_reference/openshift_cli/getting-started-cli.html#installing-the-cli)。

在使用 Minishift 之前我们需要为开发者用户配置权限：

```bash
minishift addons install --defaults
minishift addons enable admin-user
minishift start
oc adm policy --as system:admin add-cluster-role-to-user cluster-admin developer
```

现在我们要使用 Openshift 控制台创建一个 MySQL 服务。我们可以使用以下方式启动浏览器 URL：

```bash
minishift console
```

如果你没有自动登录，请使用developer/developer。

创建一个名为baeldung-demo的项目，然后从目录中创建一个 MySQL 数据库服务。 为数据库服务提供 baeldung -db ，为 MySQL 数据库名称提供 baeldung_db，并将其他值保留为默认值。

我们现在有一个服务和访问数据库的秘密。记下数据库连接url： mysql://baeldung-db:3306/baeldung_db

我们还需要允许应用程序读取配置，例如 Kubernetes Secrets 和 ConfigMaps：

```bash
oc create rolebinding default-view --clusterrole=view 
  --serviceaccount=baeldung-demo:default --namespace=baeldung-demo
```

## 3. Spring Cloud Kubernetes 依赖

我们将使用[Spring Cloud Kubernetes](https://github.com/spring-cloud/spring-cloud-kubernetes)项目为支持 Openshift 的 Kubernetes 启用云原生 API：

```xml
<profile>
  <id>openshift</id>
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-kubernetes-dependencies</artifactId>
        <version>0.3.0.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
      <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-dependencies</artifactId>
        <version>Greenwich.RELEASE</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  
  <dependencies>
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-kubernetes-config</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
  </dependencies>
</profile>
```

我们还将使用[Fabric8 Maven 插件](https://github.com/fabric8io/fabric8-maven-plugin) 来构建和部署容器：

```xml
<plugin>
    <groupId>io.fabric8</groupId>
    <artifactId>fabric8-maven-plugin</artifactId>
    <version>3.5.37</version>
    <executions>
      <execution>
        <id>fmp</id>
        <goals>
          <goal>resource</goal>
          <goal>build</goal>
        </goals>
      </execution>
    </executions>
</plugin>

```

## 4. 应用配置

现在我们需要提供配置以确保将正确的 Spring Profiles 和 Kubernetes Secrets 作为环境变量注入。

让我们在src/main/fabric8中创建一个 YAML 片段，以便 Fabric8 Maven 插件在创建部署配置时使用它。

我们还需要为 Spring Boot 执行器添加一个部分，因为 Fabric8 中的默认值仍然尝试访问/health而不是/actuator/health：

```plaintext
spec:
  template:
    spec:
      containers:
      - env:
        - name: SPRING_PROFILES_ACTIVE
          value: mysql
        - name: SPRING_DATASOURCE_USER
          valueFrom:
            secretKeyRef:
              name: baeldung-db
              key: database-user
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: baeldung-db
              key: database-password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
            scheme: HTTP
          initialDelaySeconds: 180
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
            scheme: HTTP
          initialDelaySeconds: 30
```

接下来，我们将在openshift/configmap.yml中保存一个ConfigMap，其中包含 带有 MySQL URL的application.properties的数据：

```plaintext
apiVersion: v1
kind: ConfigMap
metadata:
  name: spring-boot-bootstrap
data:
  application.properties: |-
    spring.datasource.url=jdbc:mysql://baeldung-db:3306/baeldung_db
```

在使用命令行客户端与 Openshift 交互之前，我们需要先登录。在 Web 控制台的右上角是一个用户图标，我们可以从中选择标有“登录命令”的下拉菜单。然后在shell中使用：

```shell
oc login https://192.168.42.122:8443 --token=<some-token>
```

让我们确保我们使用的是正确的项目：

```bash
oc project baeldung-demo
```

然后我们上传ConfigMap：

```bash
oc create -f openshift/configmap.yml
```

## 5.部署

在部署期间，Fabric8 Maven 插件会尝试确定配置的端口。我们的示例应用程序中现有的application.properties文件使用表达式来定义插件无法解析的端口。因此，我们必须注解该行：

```bash
#server.port=${port:8080}
```

来自当前的application.properties。

我们现在可以部署了：

```bash
mvn clean fabric8:deploy -P openshift

```

我们可以观察部署进度，直到我们看到我们的应用程序正在运行：

```bash
oc get pods -w
```

应提供清单：

```bash
NAME                            READY     STATUS    RESTARTS   AGE
baeldung-db-1-9m2cr             1/1       Running   1           1h
spring-boot-bootstrap-1-x6wj5   1/1       Running   0          46s

```

在我们测试应用程序之前，我们需要确定路由：

```bash
oc get routes
```

将打印当前项目中的路由：

```bash
NAME                    HOST/PORT                                                   PATH      SERVICES                PORT      TERMINATION   WILDCARD
spring-boot-bootstrap   spring-boot-bootstrap-baeldung-demo.192.168.42.122.nip.io             spring-boot-bootstrap   8080                    None

```

现在，让我们通过添加一本书来验证我们的应用程序是否正常工作：

```bash
http POST http://spring-boot-bootstrap-baeldung-demo.192.168.42.122.nip.io/api/books 
  title="The Player of Games" author="Iain M. Banks"


```

期待以下输出：

```bash
HTTP/1.1 201 
{
    "author": "Iain M. Banks",
    "id": 1,
    "title": "The Player of Games"
}
```

## 6. 扩展应用程序

让我们扩展部署以运行 2 个实例：

```bash
oc scale --replicas=2 dc spring-boot-bootstrap
```

然后，我们可以使用与之前相同的步骤来观察它的部署、获取路由和测试端点。

Openshift 提供了广泛的选项来[管理性能和扩展，](https://docs.openshift.com/container-platform/3.11/scaling_performance/index.html)超出了本文的范围。

## 七、总结

在本教程中，我们：

-   安装并配置了 Openshift 开发工具和本地环境
-   部署了一个MySQL服务
-   创建了 ConfigMap 和 Deployment 配置以提供数据库连接属性
-   为我们配置的 Spring Boot 应用程序构建并部署了一个容器，以及
-   测试并扩展了应用程序。

有关更多详细信息，请查看[详细的 Openshift 文档](http://docs.openshift.com/container-platform/3.11/welcome/index.html)。