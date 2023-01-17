## 1. 简介

在本教程中，我们将学习如何通过Java以编程方式与 Amazon S3(简单存储服务)存储系统进行交互。

请记住，S3 的结构非常简单；每个桶可以存储任意数量的对象，可以使用 SOAP 接口或 REST 风格的 API 访问这些对象。

展望未来，我们将使用适用于Java的 AWS 开发工具包来创建、列出和删除 S3 存储桶。我们还将上传、列出、下载、、移动、重命名和删除这些存储桶中的对象。

## 2.Maven依赖

在我们开始之前，我们需要在我们的项目中声明 AWS SDK 依赖：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk</artifactId>
    <version>1.11.163</version>
</dependency>
```

要查看最新版本，我们可以查看[Maven Central](https://search.maven.org/classic/#search|ga|1|a%3A"aws-java-sdk")。

## 3.先决条件

要使用 AWS SDK，我们需要一些东西：

1.  AWS 账户：我们需要一个 Amazon Web Services 账户。如果我们没有，我们可以继续[创建一个帐户](https://portal.aws.amazon.com/gp/aws/developer/registration/index.html)。
2.  AWS 安全凭证：这些是我们的访问密钥，允许我们以编程方式调用 AWS API 操作。我们可以通过两种方式获取这些凭证，一种是使用[Security Credentials页面访问密钥部分中的 AWS 根账户凭证，另一种是使用](https://console.aws.amazon.com/iam/home?#security_credential)[IAM 控制台](https://console.aws.amazon.com/iam/home?#)中的 IAM 用户凭证。
3.  选择 AWS 区域：我们还必须选择要存储 Amazon S3 数据的 AWS 区域。请记住，S3 存储价格因地区而异。有关更多详细信息，请访问[官方文档](https://aws.amazon.com/s3/pricing/)。在本教程中，我们将使用美国东部(俄亥俄州，地区us-east-2)。

## 4. 创建客户端连接

首先，我们需要创建一个客户端连接来访问 Amazon S3 Web 服务。为此，我们将使用AmazonS3接口：

```java
AWSCredentials credentials = new BasicAWSCredentials(
  "<AWS accesskey>", 
  "<AWS secretkey>"
);

```

然后我们将配置客户端：

```java
AmazonS3 s3client = AmazonS3ClientBuilder
  .standard()
  .withCredentials(new AWSStaticCredentialsProvider(credentials))
  .withRegion(Regions.US_EAST_2)
  .build();
```

## 5. Amazon S3 桶操作

### 5.1. 创建桶

请务必注意，存储桶命名空间由系统的所有用户共享。因此，我们的存储桶名称在 Amazon S3 中的所有现有存储桶名称中必须是唯一的(稍后我们将了解如何检查这一点)。

另外，[官方文档](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/AmazonS3.html#createBucket-java.lang.String-)中规定，Bucket名称必须符合以下要求：

-   名称不应包含下划线
-   名称长度应介于 3 到 63 个字符之间
-   名字不应该以破折号结尾
-   名称不能包含相邻的句点
-   名称不能在句点旁边包含破折号(例如，“my-.bucket.com”和“my.-bucket”无效)
-   名称不能包含大写字符

现在让我们创建一个桶：

```java
String bucketName = "baeldung-bucket";

if(s3client.doesBucketExist(bucketName)) {
    LOG.info("Bucket name is not available."
      + " Try again with a different Bucket name.");
    return;
}

s3client.createBucket(bucketName);
```

在这里，我们使用在上一步中创建的s3client。在创建存储桶之前，我们必须使用doesBucketExist()方法检查我们的存储桶名称是否可用。如果名称可用，那么我们将使用createBucket()方法。

### 5.2. 列出桶

现在我们已经创建了几个存储桶，让我们使用listBuckets()方法打印 S3 环境中所有可用存储桶的列表。此方法将返回所有 Bucket 的列表：

```java
List<Bucket> buckets = s3client.listBuckets();
for(Bucket bucket : buckets) {
    System.out.println(bucket.getName());
}
```

这将列出我们的 S3 环境中存在的所有存储桶：

```plaintext
baeldung-bucket
baeldung-bucket-test2
elasticbeanstalk-us-east-2
```

### 5.3. 删除桶

在我们删除它之前确保我们的桶是空的是很重要的。否则，将抛出异常。另请注意，无论其权限如何(访问控制策略)，只有存储桶的所有者才能删除它：

```java
try {
    s3client.deleteBucket("baeldung-bucket-test2");
} catch (AmazonServiceException e) {
    System.err.println("e.getErrorMessage());
    return;
}
```

## 6. Amazon S3 对象操作

Amazon S3 存储桶中的文件或数据集合称为对象。我们可以对对象执行上传、列出、下载、、移动、重命名和删除等多种操作。

### 6.1. 上传对象

上传对象是一个非常简单的过程。我们将使用putObject()方法，它接受三个参数：

1.  bucketName : 我们要上传对象的桶名
2.  key : 这是文件的完整路径
3.  file：包含要上传的数据的实际文件

```java
s3client.putObject(
  bucketName, 
  "Document/hello.txt", 
  new File("/Users/user/Document/hello.txt")
);
```

### 6.2. 列出对象

我们将使用listObjects()方法列出 S3 存储桶中的所有可用对象：

```java
ObjectListing objectListing = s3client.listObjects(bucketName);
for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
    LOG.info(os.getKey());
}
```

调用s3client对象的listObjects()方法会产生ObjectListing对象，该对象可用于获取指定桶中所有对象摘要的列表。我们只是在这里打印密钥，但还有一些其他选项可用，例如大小、所有者、上次修改时间、存储类等。

现在，这将打印我们存储桶中所有对象的列表：

```plaintext
Document/hello.txt
```

### 6.3. 下载对象

要下载一个对象，我们将首先使用s3client上的getObject()方法，它将返回一个S3Object对象。一旦我们得到它，我们将对其调用getObjectContent()以获取S3ObjectInputStream对象，它的行为类似于传统的JavaInputStream：

```java
S3Object s3object = s3client.getObject(bucketName, "picture/pic.png");
S3ObjectInputStream inputStream = s3object.getObjectContent();
FileUtils.copyInputStreamToFile(inputStream, new File("/Users/user/Desktop/hello.txt"));
```

这里我们使用 Apache Commons 的FileUtils.copyInputStreamToFile()方法。我们还可以访问[这篇 Baeldung 文章](https://www.baeldung.com/convert-input-stream-to-a-file)，探索将InputStream转换为文件的其他方法。

### 6.4. 、重命名和移动对象

我们可以通过在我们的 s3client 上调用copyObject()方法来一个对象，它接受四个参数：

1.  源桶名
2.  源存储桶中的对象键
3.  目标桶名称(可以与源相同)
4.  目标存储桶中的对象键

```java
s3client.copyObject(
  "baeldung-bucket", 
  "picture/pic.png", 
  "baeldung-bucket2", 
  "document/picture.png"
);
```

注意：我们可以结合使用copyObject()方法和deleteObject( ) 方法来执行移动和重命名任务。这将涉及首先对象，然后将其从旧位置删除。

### 6.5. 删除对象

要删除对象，我们将在 s3client 上调用deleteObject ()方法并传递存储桶名称和对象键：

```java
s3client.deleteObject("baeldung-bucket","picture/pic.png");
```

### 6.6. 删除多个对象

要一次删除多个对象，我们将首先创建DeleteObjectsRequest对象并将存储桶名称传递给其构造函数。然后我们将传递一个数组，其中包含我们要删除的所有对象键。

一旦我们有了这个DeleteObjectsRequest对象，我们就可以将它作为参数传递给 s3client 的deleteObjects ()方法。如果成功，它将删除我们提供的所有对象：

```java
String objkeyArr[] = {
  "document/hello.txt", 
  "document/pic.png"
};

DeleteObjectsRequest delObjReq = new DeleteObjectsRequest("baeldung-bucket")
  .withKeys(objkeyArr);
s3client.deleteObjects(delObjReq);
```

## 七. 总结

在本文中，我们重点介绍了在存储桶和对象级别与 Amazon S3 Web 服务交互的基础知识。