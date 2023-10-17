## 一、简介

在本教程中，我们将探索从 Kubernetes 上运行的应用程序访问存储在 Hashicorp Vault 中的机密的不同方法。

## 2. 快速回顾

我们已经在[之前的](https://www.baeldung.com/spring-vault) [教程](https://www.baeldung.com/spring-cloud-vault)中介绍了 Hashicorp 的 Vault ，其中我们展示了如何安装它并为其填充机密。简而言之，Vault 为应用程序秘密提供安全存储服务，该服务可以是静态的，也可以是动态生成的。

要访问 Vault 服务，应用程序必须使用一种可用机制对自身进行身份验证。**当应用程序在 Kubernetes 环境中运行时，Vault 可以根据其关联的服务帐户对其进行身份验证，从而无需单独的凭据**。

在此场景中，Kubernetes 服务帐户绑定到 Vault 角色，该角色定义关联的访问策略。该策略定义了应用程序可以访问哪些秘密。

## 3. 向应用程序提供秘密

在 Kubernetes 环境中，开发人员有多种选择来获取 Vault 管理的机密，这些机密可以分为或多或少的侵入性。在这种情况下，“侵入性”与应用程序对秘密来源的了解程度有关。

以下是我们将介绍的方法的摘要：

- 使用 Vault 的 API 显式检索
- 使用 Spring Boot 的 Vault 支持进行半显式检索
- 使用 Vault Sidecar 的透明支持
- 使用 Vault Secret CSI 提供程序的透明支持
- 使用 Vault Secret Operator 提供透明支持

## 4. 认证设置

在所有这些方法中，测试应用程序将使用 Kubernetes 身份验证来访问 Vault 的 API。当在 Kubernetes 中运行时，这会自动提供。**但是，要从集群外部使用这种身份验证，我们需要一个与服务帐户关联的有效令牌。**

实现此目的的一种方法是创建服务帐户令牌密钥。机密和服务帐户是命名空间范围内的资源，因此让我们首先创建一个命名空间来保存它们：

```bash
$ kubectl create namespace baeldung复制
```

接下来，我们创建服务帐户：

```bash
$ kubectl create serviceaccount --namespace baeldung vault-test-sa复制
```

最后，让我们生成一个24小时有效的令牌并将其保存到文件中：

```bash
$ kubectl create token --namespace baeldung vault-test-sa --duration 24h > sa-token.txt复制
```

现在，我们需要将 Kubernetes 服务帐户与 Vault 角色绑定：

```bash
$ vault write auth/kubernetes/role/baeldung-test-role \
  bound_service_account_names=vault-test-sa \
  bound_service_account_namespaces=baeldung \
  policies=default,baeldung-test-policy \
  ttl=1h复制
```

## 5. 显式检索

在这种情况下，应用程序直接使用 Vault 的 REST API 或更可能使用可用的库之一获取所需的机密。[*对于 Java，我们将使用spring-vault*项目](https://docs.spring.io/spring-vault/docs/current/reference/html/)中的库，该项目利用 Spring 框架进行低级 REST 操作：

```xml
<dependency>
    <groupId>org.springframework.vault</groupId>
    <artifactId>spring-vault-core</artifactId>
    <version>2.3.4</version>
</dependency>复制
```

此依赖项的最新版本可在[Maven Central](https://mvnrepository.com/artifact/org.springframework.vault/spring-vault-core)上找到。

**请确保选择与 Spring Framework 主版本兼容的版本**：*spring-vault-core* 3.x 需要 Spring 6.x，而*spring-vault-core* 2.x 需要 Spring 5.3.x。

访问 Vault API 的主要入口点是*VaultTemplate*类。该库提供了*EnvironmentVaultConfiguration*帮助程序类，该类简化了使用所需的访问和身份验证详细信息配置*VaultTemplate*实例的过程。*要使用它，推荐的方法是从我们应用程序的@Configuration*类之一导入它：

```java
@Configuration
@PropertySource("vault-config-k8s.properties")
@Import(EnvironmentVaultConfiguration.class)
public class VaultConfig {
    // No code!
}
复制
```

在本例中，我们还添加了*Vault-config-k8s* [属性source](https://www.baeldung.com/spring-yaml-propertysource)，我们将在其中添加所需的连接详细信息。至少，我们需要告知 Vault 的端点 URI 和要使用的身份验证机制。**由于我们将在开发过程中在集群外部运行应用程序，因此我们还需要提供保存服务帐户令牌的文件的位置**：

```properties
vault.uri=http://localhost:8200
vault.authentication=KUBERNETES
vault.kubernetes.role=baeldung-test-role
vault.kubernetes.service-account-token-file=sa-token.txt复制
```

现在，我们可以在需要访问 Vault API 的任何地方注入*VaultTemplate 。*作为一个简单的示例，让我们创建一个[*CommandLineRunner* ](https://www.baeldung.com/running-setup-logic-on-startup-in-spring#6-spring-boot-commandlinerunner)*@Bean*来列出所有机密的内容：

```java
@Bean
CommandLineRunner listSecrets(VaultTemplate vault) {

    return args -> {
        VaultKeyValueOperations ops = vault.opsForKeyValue("secrets", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        List<String> secrets = ops.list("");
        if (secrets == null) {
            System.out.println("No secrets found");
            return;
        }

        secrets.forEach(s -> {
            System.out.println("secret=" + s);
            var response = ops.get(s);
            var data = response.getRequiredData();

            data.entrySet()
              .forEach(e -> {
                  System.out.println("- key=" + e.getKey() + " => " + e.getValue());
              });
        });
    };
}
复制
```

*在我们的例子中，Vault 在/secrets*路径上安装了一个 KV 版本 2 机密引擎，因此我们使用*opsForKeyValue*方法来获取*VaultKeyValueOperations*对象，我们将使用该对象列出所有机密。其他秘密引擎也有专用的操作对象，提供定制的方法来访问它们。

对于没有专用*VaultXYZOperations*外观的秘密引擎，我们可以使用通用方法来访问任何路径：

- *read(path)*：从指定路径读取数据
- *write(path, data)：* 在指定路径写入数据
- *list(path)：* 返回指定路径下的条目列表
- *delete* ( *path* )：删除指定路径下的secret

## 6. 半显式检索

**在前面的方法中，我们直接访问 Vault 的 API 引入了强耦合，这可能会带来一些障碍**。例如，这意味着开发人员在开发期间和运行 CI 管道时将需要 Vault 实例或创建模拟。

**或者，我们可以在项目中使用[Spring Cloud Vault](https://www.baeldung.com/spring-cloud-vault)**库，使 Vault 的秘密查找对应用程序的代码透明。*该库通过向 Spring 公开自定义PropertySource*来实现这一点，该自定义 PropertySource 将在应用程序引导期间被拾取和配置。

我们将此方法称为“半显式”，因为虽然应用程序的代码确实不知道 Vault 的使用情况，但我们仍然必须向项目添加所需的依赖项。实现此目标的最简单方法是使用可用的入门库：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
    <version>3.1.3</version>
</dependency>
复制
```

此依赖项的最新版本可在[Maven Central](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-vault-config)上找到。

**和以前一样，我们必须选择一个与我们项目使用的 Spring Boot 主版本兼容的版本**。Spring Boot 2.7.x 需要版本 3.1.x，而 Spring Boot 3.x 需要版本 4.x。

要启用 Vault 作为属性源，我们必须添加一些配置属性。常见的做法是为此使用专用的 Spring 配置文件，这使我们能够快速从基于 Vault 的机密切换到任何其他来源。

对于 Kubernetes，典型的配置属性文件如下所示：

```properties
spring.config.import=vault://
spring.cloud.vault.uri=http://vault-internal.vault.svc.cluster.local:8200
spring.cloud.vault.authentication=KUBERNETES
spring.cloud.vault.kv.backend=secrets
spring.cloud.vault.kv.application-name=baeldung-test复制
```

此配置使 Vault 的 KV 后端能够安装在服务器上的*机密路径上。*该库将使用配置的应用程序名称作为该后端下的路径，从中选择机密。

还需要 spring.config.import 属性来启用 Vault 作为属性源*。***请注意，此属性是在 Spring Boot 2.4 中引入的，同时弃用了引导上下文初始化。**在迁移基于旧版本Spring Boot的应用程序时，这是需要特别注意的。

可用配置属性的完整列表可在 Spring Cloud Vault 的[文档](https://cloud.spring.io/spring-cloud-vault/reference/html/)中找到。

现在，让我们通过一个简单的示例来展示如何使用它，该示例从 Spring 的环境中获取配置值：

```java
@Bean
CommandLineRunner listSecrets(Environment env) {
    return args -> {
        var foo = env.getProperty("foo");
        Assert.notNull(foo, "foo must have a value");
        System.out.println("foo=" + foo);
    };
}
复制
```

当我们运行此应用程序时，我们可以在输出中看到密钥的值，从而确认集成正在运行。

## 7. 使用 Vault Sidecar 的透明支持

如果我们不想或无法更改现有应用程序的代码以从 Vault 获取其机密，那么使用[Vault 的 sidecar](https://developer.hashicorp.com/vault/docs/platform/k8s/injector)方法是一个合适的替代方案。**唯一的要求是应用程序已经能够从环境变量和配置文件中选取值。**

sidecar[模式](https://www.baeldung.com/linux/kubernetes-pods-sidecar-containers)是 Kubernetes 环境中的常见做法，其中应用程序将某些特定功能委托给在同一[pod](https://kubernetes.io/docs/concepts/workloads/pods/)上运行的另一个容器。这种模式的一个流行应用是[Istio 服务网格](https://istio.io/)，用于向现有应用程序添加流量控制策略和服务发现以及其他功能。

我们可以将此方法用于任何 Kubernetes 工作负载类型，例如*Deployment*、*Statefulset*或*Job*。**此外，我们可以使用\*Mutating Webhook\*在创建 pod 时自动注入 sidecar，从而使用户无需手动将其添加到工作负载规范中。**

Vault sidecar 使用工作负载 pod 模板的元数据部分中存在的注释来指示 sidecar 从 Vault 中提取哪些机密。然后，这些秘密将被放入一个文件中，该文件存储在 sidecar 与同一 Pod 中的任何其他容器之间的共享卷中。如果这些秘密中的任何一个是动态的，则 sidecar 还会负责跟踪其更新，并在需要时重新呈现文件。

### 7.1. Sidecar注入器部署

**在使用这种方法之前，首先我们需要部署Vault的Sidecar Injector组件。**最简单的方法是使用 Hashicorp 提供的[Helm Chart](https://github.com/hashicorp/vault-helm)，默认情况下，它已经将注入器添加为 Kubernetes 上常规 Vault 部署的一部分。

如果不是这种情况，我们必须使用*injector.enabled*属性的新值升级现有的helm版本：

```bash
$ helm upgrade vault hashicorp/vault -n vault --set injector.enabled=true复制
```

为了验证注入器是否正确安装，让我们查询可用的*WebHookConfiguration*对象：

```bash
$ kubectl get mutatingwebhookconfiguration
NAME                       WEBHOOKS   AGE
vault-agent-injector-cfg   1          16d复制
```

### 7.2. 注释部署

**秘密注入是“选择加入”的，这意味着除非注入器发现特定注释作为工作负载元数据的一部分，否则不会发生任何更改**。这是使用最少的所需注释集的部署清单示例：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  namespace: baeldung
spec:
  selector:
    matchLabels:
      app: nginx
  replicas: 1 
  template:
    metadata:
      labels:
        app: nginx
      annotations:
        vault.hashicorp.com/agent-inject: "true"
        vault.hashicorp.com/agent-inject-secret-baeldung.properties: "secrets/baeldung-test"
        vault.hashicorp.com/role: "baeldung-test-role"
        vault.hashicorp.com/agent-inject-template-baeldung.properties: |
          {{- with secret "secrets/baeldung-test" -}}
          {{- range $k, $v := .Data.data }}
          {{$k}}={{$v}}
          {{- end -}}
          {{ end }}
    spec:
      serviceAccountName: vault-test-sa
      automountServiceAccountToken: true
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
        - containerPort: 80
复制
```

当我们将此清单部署到集群时，注入器将对其进行修补并注入配置如下的 Vault 代理 sidecar 容器：

- *使用baeldung-test-role*自动登录
- 位于*Secrets/baeldung-test*路径下的机密将渲染到默认机密目录 ( */vault/secrets* )下名为*baeldung.properties的文件*
- 使用提供的模板生成的文件内容

还有更多可用的注释，我们可以使用它们来自定义用于呈现秘密的位置和模板。支持的注释的完整列表可在[Vault 文档中找到。](https://developer.hashicorp.com/vault/docs/platform/k8s/injector/annotations)

## 8. 使用 Vault Secret CSI 提供程序的透明支持

CSI（容器存储接口）提供商允许供应商扩展 Kubernetes 集群支持的卷类型。[Vault CSI 提供程序](https://developer.hashicorp.com/vault/docs/platform/k8s/csi)是使用 sidecar 的替代方案，允许 Vault 机密作为常规卷公开给 pod。

**这里的主要优点是我们没有为每个 pod 连接一个 sidecar，因此我们需要更少的资源（CPU/内存）来运行我们的工作负载**。虽然资源消耗不是很大，但 Sidecar 的成本会随着活动 Pod 的数量而增加。相比之下，CSI 使用 DaemonSet *，*这意味着集群中的每个节点都有一个 pod。

### 8.1. 启用 Vault CSI 提供程序

在安装此提供程序之前，我们必须检查目标集群中是否已存在[CSI Secret Store Driver ：](https://secrets-store-csi-driver.sigs.k8s.io/)

```bash
$ kubectl get csidrivers复制
```

结果应包含 Secrets-store.csi.k8s.io 驱动程序：

```bash
NAME                       ATTACHREQUIRED   PODINFOONMOUNT  ... 
secrets-store.csi.k8s.io   false            true             ...复制
```

如果不是这种情况，只需应用适当的舵图即可：

```bash
$ helm repo add secrets-store-csi-driver https://kubernetes-sigs.github.io/secrets-store-csi-driver/charts
$ helm install csi-secrets-store secrets-store-csi-driver/secrets-store-csi-driver \
     --namespace kube-system\
     --set syncSecret.enabled=true
复制
```

该项目的文档还描述了其他安装方法，但除非有一些特定要求，否则 helm 方法是首选方法。

现在，让我们继续安装 Vault CSI Provider。我们将再次使用官方 Vault 头盔图表。**CSI 提供程序默认未启用，因此我们需要使用\*csi.enabled\*属性对其进行升级：**

```bash
$ helm upgrade vault hashicorp/vault -n vault –-set csi.enabled=true复制
```

为了验证驱动程序是否正确安装，我们将检查其*DaemonSet*是否运行正常：

```bash
$ kubectl get daemonsets –n vault
NAME DESIRED CURRENT READY UP-TO-DATE AVAILABLE NODE SELECTOR AGE
vault-csi-provider 1 1 1 1 1 <none> 15d 复制
```

### 8.2. Vault CSI 提供商的使用

使用 Vault CSI 提供程序使用 Vault 机密配置工作负载需要两个步骤。首先，我们定义一个*SecretProviderClass*资源，它指定要检索的秘密和密钥：

```yaml
apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: baeldung-csi-secrets
  namespace: baeldung
spec:
  provider: vault
  parameters:
    roleName: 'baeldung-test-role'
    objects: |
      - objectName: 'baeldung.properties'
        secretPath: "secrets/data/baeldung-test"
复制
```

请注意*spec.provider*属性，必须将其设置为*Vault*。这是必需的，以便 CSI 驱动程序知道要使用哪个可用的提供程序。参数部分包含提供者用来定位所请求秘密的信息：

- *roleName*：登录期间使用的 Vault 角色，定义应用程序将有权访问的机密
- *对象*：该值是一个 YAML 格式的字符串（因此是“|”），其中包含要检索的秘密数组

*对象*数组中的每个条目都是一个具有三个属性的对象：

- *SecretPath*：Vault 的秘密路径
- *objectName*：将包含机密的文件的名称
- *objectKey*：Vault 秘密中的密钥，提供要放入文件中的内容。如果省略，该文件将包含一个包含所有值的 JSON 对象

现在，让我们在示例部署工作负载中使用此资源：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-csi
  namespace: baeldung
spec:
  selector:
    matchLabels:
      app: nginx-csi
  replicas: 1
  template:
    metadata:
      labels:
        app: nginx-csi
    spec:
      serviceAccountName: vault-test-sa
      automountServiceAccountToken: true
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
        - containerPort: 80
        volumeMounts:
        - name: vault-secrets
          mountPath: /vault/secrets
          readOnly: true
      volumes:
      - name: vault-secrets
        csi:
          driver: 'secrets-store.csi.k8s.io'
          readOnly: true
          volumeAttributes:
            secretProviderClass: baeldung-csi-secrets
复制
```

在*卷*部分，请注意我们如何使用指向之前定义的*SecretStorageClass 的 CSI 定义。*

为了验证此部署，我们可以在主容器中打开一个 shell，并检查指定安装路径下是否存在密钥：

```bash
$ kubectl get pods -n baeldung -l app=nginx-csi
NAME                        READY   STATUS    RESTARTS   AGE
nginx-csi-b7866bc69-njzff   1/1     Running   0          19m
$ kubectl exec -it -n baeldung nginx-csi-b7866bc69-njzff -- /bin/sh
# cat /vault/secrets/baeldung.properties
{"request_id":"eb417a64-b1c4-087d-a5f4-30229f27aba1","lease_id":"","lease_duration":0,
   "renewable":false,
   "data":{
      "data":{"foo":"bar"},
  ... more data omitted  复制
```

## 9. 使用 Vault Secrets Operator 的透明支持

**Vault Secrets Operator 将自定义资源定义 (CRD) 添加到 Kubernetes 集群，我们可以使用从 Vault 实例提取的值来填充常规密钥。**

与 CSI 方法相比，该操作员的主要优点是我们不需要对现有工作负载进行任何更改即可从标准机密转移到 Vault 支持的机密。

### 9.1. Vault Secrets Operator 部署

Operator 有自己的图表，可将所有必需的工件部署到集群中：

```bash
$ helm install --create-namespace --namespace vault-secrets-operator \
    vault-secrets-operator hashicorp/vault-secrets-operator \
    --version 0.1.0复制
```

现在，让我们检查新的 CRD：

```bash
$ kubectl get customresourcedefinitions | grep vault
vaultauths.secrets.hashicorp.com                            2023-09-13T01:08:11Z
vaultconnections.secrets.hashicorp.com                      2023-09-13T01:08:11Z
vaultdynamicsecrets.secrets.hashicorp.com                   2023-09-13T01:08:11Z
vaultpkisecrets.secrets.hashicorp.com                       2023-09-13T01:08:11Z
vaultstaticsecrets.secrets.hashicorp.com                    2023-09-13T01:08:11Z复制
```

截至撰写本文时，操作员定义了这些 CRD：

- *VaultConnection*：定义 Vault 连接详细信息，例如其地址、TLS 证书等
- *VaultAuth*：特定 VaultConnection 使用的身份验证详细信息
- *Vault<type>Secret*：定义 Kubernetes 和 Vault 密钥之间的映射，其中*<type>*可以是*Static*、*Dynamic*或*PKI*，并且对应于密钥类型。

### 9.2. Vault Secret 操作员使用情况

让我们通过一个简单的示例来展示如何使用此运算符。首先，我们需要创建一个指向我们的 Vault 实例的*VaultConnection资源：*

```yaml
apiVersion: secrets.hashicorp.com/v1beta1
kind: VaultConnection
metadata:
  namespace: baeldung
  name: vault-local
spec:
  address: http://vault.vault.svc.cluster.local:8200
复制
```

接下来，我们需要一个*VaultAuth*资源，其中包含我们将用于访问机密的身份验证详细信息：

```yaml
apiVersion: secrets.hashicorp.com/v1beta1
kind: VaultAuth
metadata:
  namespace: baeldung
  name: baeldung-test
spec:
  vaultConnectionRef: vault-local
  method: kubernetes
  mount: kubernetes
  Kubernetes:
    role: baeldung-test-role
    serviceAccount: vault-test-sa
复制
```

这些是我们必须填写的关键属性：

- *spec.vaultConnectionRef*：我们刚刚创建的*VaultConnection资源的名称*
- *spec.method*：设置为*kubernetes*，因为我们将使用此身份验证方法
- *spec.kubernetes.role*：身份验证时使用的 Vault 角色
- *spec.kubernetes.serviceAccount*：身份验证时使用的服务帐户

现在，让我们定义一个*VaultStaticSecret将 Vault 上的**Secrets/baeldung-test*中的机密映射到名为*baeldung-test*的机密：

```yaml
apiVersion: secrets.hashicorp.com/v1beta1
kind: VaultStaticSecret
metadata:
  namespace: baeldung
  name: baeldung-test
spec:
  vaultAuthRef: baeldung-test
  mount: secrets
  type: kv-v2
  path: baeldung-test
  refreshAfter: 60s
  hmacSecretData: true
  destination:
    create: true
    name: baeldung-test
复制
```

最后，我们可以使用*kubectl*来确认密钥是否已正确创建：

```bash
$ kubectl get secret -n baeldung baeldung-test
NAME            TYPE     DATA   AGE
baeldung-test   Opaque   3      24h复制
```

## 10. 方法比较

正如我们所看到的，基于 Kubernetes 的应用程序不乏从 Vault 访问机密的替代方案。为了帮助选择最适合给定用例的方法，我们对每种方法的功能/特性进行了简短比较：

| 特点/特性          | 显式的   | 半显式         | 注射器                            | CSI                  | 操作员             |
| ------------------ | -------- | -------------- | --------------------------------- | -------------------- | ------------------ |
| 需要更改代码       | 是的     | 否（仅限部门） | 不                                | 不                   | 不                 |
| 访问 Vault 的 API  | 完全控制 | 只读           | 部分（例如，无管理 API 访问权限） | 有限的               | 有限的             |
| 需要额外资源       | 不       | 不             | 是的，每个 Pod 额外一个容器       | 是的，每个节点一个   | 是的，每个集群一个 |
| 对现有应用程序透明 | 不       | 不             | 部分（需要额外注释）              | 部分（需要额外的卷） | 没有任何           |
| 需要集群更改       | 不       | 不             | 是的                              | 是的                 | 是的               |

## 11. 结论

在本教程中，我们探索了从基于 Kubernetes 的应用程序访问存储在 Vault 实例中的机密的不同方法。