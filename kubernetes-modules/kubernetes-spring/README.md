## 使用Spring Boot的Kong入口控制器

此项目是从Spring Initializr网站(https://start.spring.io/)生成的：

Maven版本Java latest：

```shell
curl https://start.spring.io/starter.tgz -d dependencies=webflux,actuator -d type=maven-project | tar -xzvf -
```

Maven版本Java 11：

```shell
curl https://start.spring.io/starter.tgz -d dependencies=webflux,actuator -d type=maven-project -d javaVersion=11 | tar -xzvf -
```

## 运行Demo的步骤：

1. 安装minikube：[使用Minikube运行Spring Boot应用程序](docs/使用Minikube运行SpringBoot应用程序.md)

2. 安装Kong入口控制器
   minikube： https://docs.konghq.com/kubernetes-ingress-controller/latest/deployment/minikube/

- 测试回显服务器
- 创建环境变量：export PROXY_IP=$(minikube service -n kong kong-proxy --url | head -1)

3. 构建Spring Boot服务：

- 运行./mvnw install
- 运行jar：java -jar target/*.jar

4. 创建Docker镜像：
   如果使用minikube并且不想将镜像推送到仓库，请将本地Docker客户端指向Minikube的实现：eval $(minikube -p minikube
   docker-env) --- 使用相同的shell。

- 运行：./mvnw spring-boot:build-image

5. 部署应用程序，创建服务和入口规则：

```shell
kubectl apply -f serviceDeployment.yaml
kubectl apply -f clusterIp.yaml
kubectl apply -f ingress-rule.yaml
```

6. 使用代理IP测试访问权限：

```shell
curl -i $PROXY_IP/actuator/health
```

## 在API上设置速率限制器

7. 创建一个插件：

kubectl apply -f rate-limiter.yaml

8. 现在测试资源，每分钟尝试5次以上：

```shell
curl -i $PROXY_IP/actuator/health
```

## 相关文章

+ [使用Spring Boot的Kong入口控制器](docs/使用SpringBoot的Kong入口控制器.md)

- 更多文章： [[<-- prev]](../k8s-intro/README.md)