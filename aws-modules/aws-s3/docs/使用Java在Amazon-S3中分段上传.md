## 1. 概述

在本教程中，我们将了解如何使用 AWSJavaSDK 在 Amazon S3 中处理分段上传。

简单地说，在分段上传中，我们将内容分成更小的部分并单独上传每个部分。所有零件在收到时都重新组装。

分段上传具有以下优点：

-   更高的吞吐量——我们可以并行上传部分
-   更容易的错误恢复——我们只需要重新上传失败的部分
-   暂停和恢复上传——我们可以随时上传零件。整个过程可以暂停，剩下的部分可以稍后上传

请注意，在使用Amazon S3 进行分段上传时，除最后一部分之外的每个部分的大小都必须至少为 5 MB。

## 2.Maven依赖

在我们开始之前，我们需要在我们的项目中添加 AWS SDK 依赖：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk</artifactId>
    <version>1.11.290</version>
</dependency>
```

要查看最新版本，请查看[Maven Central](https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk)。

## 3. 执行分段上传

### 3.1. 创建 Amazon S3 客户端

首先，我们需要创建一个客户端来访问 Amazon S3。为此，我们将使用AmazonS3ClientBuilder：

```java
AmazonS3 amazonS3 = AmazonS3ClientBuilder
  .standard()
  .withCredentials(new DefaultAWSCredentialsProviderChain())
  .withRegion(Regions.DEFAULT_REGION)
  .build();
```

这会使用默认凭证提供程序链创建一个客户端来访问 AWS 凭证。

有关默认凭证提供程序链如何工作的更多详细信息，请参阅[文档](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html#credentials-default)。如果你使用的不是默认区域 ( US West-2 )，请确保将Regions.DEFAULT_REGION替换为该自定义区域。

### 3.2. 创建 TransferManager 以管理上传

我们将使用TransferManagerBuilder创建一个TransferManager实例。

此类提供简单的 API 来管理 Amazon S3 的上传和下载，并管理所有相关任务：

```java
TransferManager tm = TransferManagerBuilder.standard()
  .withS3Client(amazonS3)
  .withMultipartUploadThreshold((long) (5  1024  1025))
  .build();
```

分段上传阈值指定大小(以字节为单位)，超过该阈值上传应作为分段上传执行。

Amazon S3 规定最小部分大小为 5 MB(对于最后一部分以外的部分)，因此我们使用 5 MB 作为分段上传阈值。

### 3.3. 上传对象

要使用TransferManager上传对象，我们只需要调用它的upload()函数。这会并行上传各部分：

```java
String bucketName = "baeldung-bucket";
String keyName = "my-picture.jpg";
String file = new File("documents/my-picture.jpg");
Upload upload = tm.upload(bucketName, keyName, file);
```

TransferManager.upload()返回一个上传对象。这可用于检查上传状态和管理上传。我们将在下一节中这样做。

### 3.4. 等待上传完成

TransferManager.upload()是一个非阻塞函数；当上传在后台运行时，它会立即返回。

我们可以使用返回的Upload对象等待上传完成后再退出程序：

```java
try {
    upload.waitForCompletion();
} catch (AmazonClientException e) {
    // ...
}
```

### 3.5. 跟踪上传进度

跟踪上传进度是一个很常见的需求；我们可以在ProgressListener实例的帮助下做到这一点：

```java
ProgressListener progressListener = progressEvent -> System.out.println(
  "Transferred bytes: " + progressEvent.getBytesTransferred());
PutObjectRequest request = new PutObjectRequest(
  bucketName, keyName, file);
request.setGeneralProgressListener(progressListener);
Upload upload = tm.upload(request);
```

我们创建的ProgressListener将继续打印传输的字节数，直到上传完成。

### 3.6. 控制上传并行度

默认情况下，TransferManager使用最多十个线程来执行分段上传。

但是，我们可以通过在构建TransferManager时指定ExecutorService来控制它：

```java
int maxUploadThreads = 5;
TransferManager tm = TransferManagerBuilder.standard()
  .withS3Client(amazonS3)
  .withMultipartUploadThreshold((long) (5  1024  1025))
  .withExecutorFactory(() -> Executors.newFixedThreadPool(maxUploadThreads))
  .build();
```

在这里，我们使用 lambda 来创建ExecutorFactory的包装器实现，并将其传递给withExecutorFactory()函数。

## 4. 总结

在这篇简短的文章中，我们学习了如何使用适用于Java的 AWS 开发工具包执行分段上传，并且了解了如何控制上传的某些方面并跟踪其进度。