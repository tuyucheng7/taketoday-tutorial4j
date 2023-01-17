## 1. 概述

在本文中，我们将学习使用JavaSDK 控制 EC2 资源。如果你不[熟悉 EC2(弹性云计算)](https://aws.amazon.com/ec2/) ——这是一个在亚马逊云中提供计算能力的平台。

## 2.先决条件

使用适用于 EC2 的 Amazon AWS SDK 所需的 Maven 依赖项、AWS 帐户设置和客户端连接与[此处本文中的相同。](https://www.baeldung.com/aws-s3-java)

假设我们已经创建了AWSCredentials的实例，如上一篇文章所述，我们可以继续创建我们的 EC2 客户端：

```java
AmazonEC2 ec2Client = AmazonEC2ClientBuilder
  .standard()
  .withCredentials(new AWSStaticCredentialsProvider(credentials))
  .withRegion(Regions.US_EAST_1)
  .build();
```

## 3.创建EC2实例

使用 SDK，我们可以快速设置启动第一个 EC2 实例所需的内容。

### 3.1. 创建安全组

安全组控制着我们 EC2 实例的网络流量。我们能够为多个 EC2 实例使用一个安全组。

让我们创建一个安全组：

```java
CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest()
  .withGroupName("BaeldungSecurityGroup")
  .withDescription("Baeldung Security Group");
CreateSecurityGroupResult createSecurityGroupResult = ec2Client.createSecurityGroup(
  createSecurityGroupRequest);
```

由于默认情况下安全组不允许任何网络流量，因此我们必须将安全组配置为允许流量。

让我们允许来自任何 IP 地址的 HTTP 流量：

```java
IpRange ipRange = new IpRange().withCidrIp("0.0.0.0/0");
IpPermission ipPermission = new IpPermission()
  .withIpv4Ranges(Arrays.asList(new IpRange[] { ipRange }))
  .withIpProtocol("tcp")
  .withFromPort(80)
  .withToPort(80);
```

最后，我们必须将ipRange实例附加到AuthorizeSecurityGroupIngressRequest并使用我们的 EC2 客户端发出请求：

```java
AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest 
  = new AuthorizeSecurityGroupIngressRequest()
  .withGroupName("BaeldungSecurityGroup")
  .withIpPermissions(ipPermission);
ec2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);
```

### 3.2. 创建密钥对

在启动 EC2 实例时，我们需要指定一个[密钥对。](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html) 我们可以使用 SDK 创建密钥对：

```java
CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest()
  .withKeyName("baeldung-key-pair");
CreateKeyPairResult createKeyPairResult = ec2Client.createKeyPair(createKeyPairRequest);
```

让我们获取私钥：

```java
createKeyPairResult.getKeyPair().getKeyMaterial();

```

我们必须确保将此密钥保存在安全可靠的地方。如果我们丢失了它，我们将无法找回它(亚马逊不会保留它)。这是我们连接到 EC2 实例的唯一方式。

### 3.3. 创建 EC2 实例

要创建 EC2，我们将使用RunInstancesRequest：

```java
RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
  .withImageId("ami-97785bed")
  .withInstanceType("t2.micro") 
  .withKeyName("baeldung-key-pair") 
  .withMinCount(1)
  .withMaxCount(1)
  .withSecurityGroups("BaeldungSecurityGroup");

```

Image Id 是此实例将使用的[AMI 映像。](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AMIs.html)

[实例类型](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html)定义了实例的规格。

键名是可选的；如果未指定，则我们无法连接到我们的实例。如果我们确信我们已经正确设置了我们的实例并且不需要连接，这很好。

最小和最大计数给出了将创建多少个实例的界限。这取决于可用性区域：如果 AWS 不能在该区域中创建至少最小数量的实例，它就不会创建任何实例。

相反，如果 AWS 不能创建最大数量的实例，它会尝试创建更少的实例，前提是这个数量高于我们指定的最小实例数量。

现在我们可以使用 runInstances() 方法执行请求并检索创建的实例的 ID：

```java
String yourInstanceId = ec2Client.runInstances(runInstancesRequest)
  .getReservation().getInstances().get(0).getInstanceId();
```

## 4. 管理 EC2 实例

使用 SDK，我们可以启动、停止、重启、描述和配置对我们的 EC2 实例的监控。

### 4.1. 启动、停止和重启 EC2 实例

启动、停止和重启实例相对简单。

启动一个实例：

```java
StartInstancesRequest startInstancesRequest = new StartInstancesRequest()
  .withInstanceIds(yourInstanceId);

ec2Client.startInstances(request);

```

停止实例：

```java
StopInstancesRequest stopInstancesRequest = new StopInstancesRequest()
  .withInstanceIds(yourInstanceId);
        
ec2Client.stopInstances(request);

```

重启实例：

```java
RebootInstancesRequest request = new RebootInstancesRequest()
  .withInstanceIds(yourInstanceId);
        
RebootInstancesResult rebootInstancesRequest = ec2Client.rebootInstances(request);

```

从这些请求中的每一个，都可以查询实例的先前状态：

```java
ec2Client.stopInstances(stopInstancesRequest)
  .getStoppingInstances()
  .get(0)
  .getPreviousState()
  .getName()
```

### 4.2. 监控 EC2 实例

让我们看看如何开始和停止监控我们的 EC2 实例：

```java
MonitorInstancesRequest monitorInstancesRequest = new MonitorInstancesRequest()
  .withInstanceIds(yourInstanceId);
        
ec2Client.monitorInstances(monitorInstancesRequest);
         
UnmonitorInstancesRequest unmonitorInstancesRequest = new UnmonitorInstancesRequest()
  .withInstanceIds(yourInstanceId);

ec2Client.unmonitorInstances(unmonitorInstancesRequest);

```

### 4.3. 描述 EC2 实例

最后，我们可以描述我们的 EC2 实例：

```java
DescribeInstancesRequest describeInstancesRequest
 = new DescribeInstancesRequest();
DescribeInstancesResult response = ec2Client
  .describeInstances(describeInstancesRequest);

```

EC2 实例被分组为预留。预留是用于创建一个或多个 EC2 实例的StartInstancesRequest调用：

```java
response.getReservations()
```

从这里我们可以得到实际的实例。让我们在第一个预订中获取第一个实例：

```java
response.getReservations().get(0).getInstances().get(0)
```

现在，我们可以描述我们的实例：

```java
// ...
.getImageId()
.getSubnetId()
.getInstanceId()
.getImageId()
.getInstanceType()
.getState().getName()
.getMonitoring().getState()
.getKernelId()
.getKeyName()

```

## 5.总结

在本快速教程中，我们展示了如何使用JavaSDK 管理 Amazon EC2 实例。