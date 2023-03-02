## 1. 简介

在这篇快速文章中，我们将构建一个使用实例配置文件凭证连接到S3存储桶的Spring Cloud应用程序。

## 2. 配置我们的云环境

实例配置文件是一项AWS功能，允许EC2实例使用临时凭证连接到其他AWS资源。这些凭据是短暂的，由AWS自动轮换。

用户只能从EC2实例中请求临时凭证。但是，我们可以在任何地方使用这些凭据，直到它们过期为止。

要获得更多关于[实例配置文件配置](https://docs.aws.amazon.com/codedeploy/latest/userguide/getting-started-create-iam-instance-profile.html)的帮助，请查看AWS的文档。

### 2.1 部署

首先，我们需要一个具有适当设置的AWS环境。

对于下面的代码示例，我们需要建立一个EC2实例、一个S3存储桶和适当的IAM角色。为此，我们可以使用代码示例中的[CloudFormation模板](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-aws/src/main/resources)，也可以自行搭建这些资源。

### 2.2 确认

接下来，我们应该确保我们的EC2实例可以检索实例配置文件凭证。将<InstanceProfileRoleName\>替换为实际的实例配置文件角色名称：

```shell
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/<InstanceProfileRoleName>
```

如果一切设置正确，则JSON响应将包含AccessKeyId、SecretAccessKey、Token和Expiration属性。

## 3. 配置Spring Cloud

现在，对于我们的示例应用程序。我们需要配置Spring Boot以使用instance profile，我们可以在Spring Boot配置文件中执行此操作：

```properties
cloud.aws.credentials.instanceProfile=true
```

而且，就是这样！如果此Spring Boot应用程序部署在EC2实例中，则每个客户端将自动尝试使用instance profile凭证连接到AWS资源。

这是因为Spring Cloud使用AWS SDK中的[EC2ContainerCredentialsProviderWrapper](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/EC2ContainerCredentialsProviderWrapper.html)。这将按优先顺序查找凭据，**如果在系统中找不到任何其他凭据，则自动以instance profile凭据结束**。

如果我们需要指定Spring Cloud只使用instance profile，那么我们可以实例化我们自己的AmazonS3实例。

我们可以使用InstanceProfileCredentialsProvider对其进行配置并将其发布为bean：

```java
@Bean
public AmazonS3 amazonS3() {
    InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider(true);
    return AmazonS3ClientBuilder.standard()
        .withCredentials(provider)
        .build();
}
```

**这将替换Spring Cloud提供的默认AmazonS3实例**。

## 4. 连接到我们的S3存储桶

现在，我们可以像往常一样使用Spring Cloud连接到我们的S3存储桶，但无需配置永久凭证：

```java
@Component
public class SpringCloudS3Service {

    // other declarations

    @Autowired
    AmazonS3 amazonS3;

    public void createBucket(String bucketName) {
        // log statement
        amazonS3.createBucket(bucketName);
    }
}
```

请记住，因为实例配置文件仅发布给EC2实例，**所以此代码仅在EC2实例上运行时有效**。

当然，我们可以对我们的EC2实例连接到的任何AWS服务重复该过程，包括EC2、SQS和SNS。

## 5. 总结

在本教程中，我们了解了如何在Spring Cloud中使用实例配置文件凭证。此外，我们还创建了一个连接到S3存储桶的简单应用程序。