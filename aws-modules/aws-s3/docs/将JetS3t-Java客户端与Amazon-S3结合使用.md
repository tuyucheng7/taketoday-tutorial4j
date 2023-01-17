## 1. 概述

在本教程中，我们将[JetS3t](http://www.jets3t.org/) 库与 [Amazon S3 一起使用。](https://aws.amazon.com/s3/)

简单地说，我们将创建桶，将数据写入其中，读回数据，，然后列出和删除它们。

## 2. JetS3t 设置

### 2.1. Maven 依赖

首先，我们需要将 NATS 库和[Apache HttpClient](https://hc.apache.org/)添加 到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.lucee</groupId>
    <artifactId>jets3t</artifactId>
    <version>0.9.4.0006L</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.5</version>
</dependency>

```

Maven Central 拥有[最新版本的 JetS3t 库](https://search.maven.org/classic/#search|ga|1|jets3t) 和[最新版本的 HttpClient](https://search.maven.org/classic/#artifactdetails|org.apache.httpcomponents|httpclient|4.5.5|jar)。可以在 [此处](https://sourceforge.net/p/jets3t/wiki/Home/)找到 JetS3t 的源代码。

我们将使用[Apache Commons Codec](https://commons.apache.org/proper/commons-codec/) 进行其中一项测试，因此我们也将其添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>org.lucee</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.10.L001</version>
</dependency>

```

Maven Central 在[这里](https://search.maven.org/classic/#artifactdetails|org.lucee|commons-codec|1.10.L001|bundle)有最新版本。

### 2.2. 亚马逊 AWS 密钥

我们需要 AWS 访问密钥来连接到 S3 存储服务。 可以在 [这里创建一个免费帐户。](https://aws.amazon.com/free/?sc_ichannel=ha&sc_icampaign=signin_prospects&sc_isegment=en&sc_iplace=sign-in&sc_icontent=freetier&sc_segment=-1)

拥有帐户后，我们需要创建一组[安全密钥。](https://docs.aws.amazon.com/general/latest/gr/managing-aws-access-keys.html)这里有关于用户和访问密钥的文档 [。](https://docs.aws.amazon.com/general/latest/gr/aws-sec-cred-types.html)

JetS3t 使用 Apache Commons 日志记录，因此当我们想要打印有关我们正在做的事情的信息时，我们也会使用它。

## 3.连接到一个简单的存储

现在我们有了 AWS 访问密钥和秘密密钥，我们可以连接到 S3 存储。

### 3.1. 连接到 AWS

首先，我们创建 AWS 凭证，然后使用它们连接到服务：

```java
AWSCredentials awsCredentials 
  = new AWSCredentials("access key", "secret key");
s3Service = new RestS3Service(awsCredentials);

```

RestS3Service是我们与 Amazon S3 的连接。它使用 HttpClient通过 REST 与 S3 通信。

### 3.2. 验证连接

我们可以通过列出存储桶来验证我们是否已成功连接到该服务：

```java
S3Bucket[] myBuckets = s3Service.listAllBuckets();

```

根据我们之前是否创建过桶，数组可能是空的，但如果操作没有抛出异常，我们就有了一个有效的连接。

## 4.桶管理

通过连接到 Amazon S3，我们可以创建存储桶来保存我们的数据。S3 是一个对象存储系统。数据作为对象上传并存储在桶中。

由于所有 S3 存储桶共享相同的全局命名空间，因此每个存储桶都必须具有唯一的名称。

### 4.1. 创建桶

让我们尝试创建一个名为“ mybucket ”的桶：

```java
S3Bucket bucket = s3Service.createBucket("mybucket");

```

这失败了一个例外：

```plaintext
org.jets3t.service.S3ServiceException: Service Error Message.
  -- ResponseCode: 409, ResponseStatus: Conflict, XML Error Message:
  <!--?xml version="1.0" encoding="UTF-8"?-->
  <code>BucketAlreadyExists</code> The requested bucket name is not available. 
  The bucket namespace is shared by all users of the system.
  Please select a different name and try again.
  mybucket 07BE34FF3113ECCF 
at org.jets3t.service.S3Service.createBucket(S3Service.java:1586)
```

不出所料，“ mybucket ”这个名字已经被使用了。对于本教程的其余部分，我们将组成我们的名字。

让我们用不同的名字再试一次：

```java
S3Bucket bucket = s3Service.createBucket("myuniquename");
log.info(bucket);

```

使用唯一名称，调用成功，我们会看到有关我们的存储桶的信息：

```plaintext
[INFO] JetS3tClient - S3Bucket
[name=myuniquename,location=US,creationDate=Sat Mar 31 16:47:47 EDT 2018,owner=null]

```

### 4.2. 删除桶

删除存储桶与创建存储桶一样简单，除了一件事；桶必须是空的才能被移除！

```java
s3Service.deleteBucket("myuniquename");

```

这将为非空的桶抛出异常。

### 4.3. 指定桶区域

桶可以在特定的数据中心创建。对于 JetS3t，默认为美国北弗吉尼亚州或“us-east-1”。

我们可以通过指定不同的区域来覆盖它：

```java
S3Bucket euBucket 
  = s3Service.createBucket("eu-bucket", S3Bucket.LOCATION_EUROPE);
S3Bucket usWestBucket = s3Service
  .createBucket("us-west-bucket", S3Bucket.LOCATION_US_WEST);
S3Bucket asiaPacificBucket = s3Service
  .createBucket("asia-pacific-bucket", S3Bucket.LOCATION_ASIA_PACIFIC);

```

JetS3t 有大量定义为常量的区域。

## 5. 上传、下载和删除数据

一旦我们有了一个桶，我们就可以向它添加对象。存储桶旨在持久耐用，并且对存储桶可以包含的对象的大小或数量没有硬性限制。

通过创建S3Objects将数据上传到 S3 。我们可以从InputStream 上传数据，但 JetS3t 也为Strings和Files提供了方便的方法。

### 5.1. 字符串数据

我们先来看看字符串：

```java
S3Object stringObject = new S3Object("object name", "string object");
s3Service.putObject("myuniquebucket", stringObject);

```

与桶类似，对象也有名称，但是对象名称只存在于它们的桶中，所以我们不必担心它们是全局唯一的。

我们通过将名称和数据传递给构造函数来创建对象。然后我们用putObject 存储它。

当我们使用此方法通过 JetS3t存储字符串时，它会为我们设置正确的内容类型。

让我们向 S3 查询有关我们对象的信息并查看内容类型：

```java
StorageObject objectDetailsOnly 
  = s3Service.getObjectDetails("myuniquebucket", "my string");
log.info("Content type: " + objectDetailsOnly.getContentType() + " length: " 
  + objectDetailsOnly.getContentLength());

```

ObjectDetailsOnly()检索对象元数据而不下载它。当我们记录内容类型时，我们看到：

```plaintext
[INFO] JetS3tClient - Content type: text/plain; charset=utf-8 length: 9

```

JetS3t 将数据识别为文本并为我们设置长度。

让我们下载数据并将其与我们上传的数据进行比较：

```java
S3Object downloadObject = 
  s3Service.getObject("myuniquebucket, "string object");
String downloadString = new BufferedReader(new InputStreamReader(
  object.getDataInputStream())).lines().collect(Collectors.joining("n"));
 
assertTrue("string object".equals(downloadString));
```

数据在我们用于上传它的同一个S3Object中检索，字节数在DataInputStream 中可用。

### 5.2. 文件数据

上传文件的过程类似于Strings：

```java
File file = new File("src/test/resources/test.jpg");
S3Object fileObject = new S3Object(file);
s3Service.putObject("myuniquebucket", fileObject);

```

当向 S3Objects传递一个文件时，它们从它们包含的文件的基本名称中派生出它们的名称：

```plaintext
[INFO] JetS3tClient - File object name is test.jpg
```

JetS3t 获取文件并为我们上传。它将尝试从类路径加载[mime.types 文件](https://en.wikipedia.org/wiki/Media_type#mime.types)，并使用它来识别文件类型和适当发送的内容类型。

如果我们检索文件上传的对象信息并获取我们看到的内容类型：

```plaintext
[INFO] JetS3tClient - Content type:application/octet-stream
```

让我们将文件下载到一个新文件并比较内容：

```java
String getFileMD5(String filename) throws IOException {
    try (FileInputStream fis = new FileInputStream(new File(filename))) {
        return DigestUtils.md5Hex(fis);
    }
}

S3Object fileObject = s3Service.getObject("myuniquebucket", "test.jpg"); 
File newFile = new File("/tmp/newtest.jpg"); 
Files.copy(fileObject.getDataInputStream(), newFile.toPath(), 
  StandardCopyOption.REPLACE_EXISTING);
String origMD5 = getFileMD5("src/test/resources/test.jpg");
String newMD5 = getFileMD5("src/test/resources/newtest.jpg");
assertTrue(origMD5.equals(newMD5));
```

与字符串类似，我们下载了对象并使用DataInputStream 创建了一个新文件。然后我们计算了两个文件的 MD5 哈希值并进行了比较。

### 5.3. 流数据

当我们上传字符串或文件以外的对象时，我们需要做更多的工作：

```java
ArrayList<Integer> numbers = new ArrayList<>();
// adding elements to the ArrayList

ByteArrayOutputStream bytes = new ByteArrayOutputStream();
ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytes);
objectOutputStream.writeObject(numbers);

ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.toByteArray());

S3Object streamObject = new S3Object("stream");
streamObject.setDataInputStream(byteArrayInputStream);
streamObject.setContentLength(byteArrayInputStream.available());
streamObject.setContentType("binary/octet-stream");

s3Service.putObject(BucketName, streamObject);

```

我们需要在上传前设置我们的内容类型和长度。

检索此流意味着反转过程：

```java
S3Object newStreamObject = s3Service.getObject(BucketName, "stream");

ObjectInputStream objectInputStream = new ObjectInputStream(
  newStreamObject.getDataInputStream());
ArrayList<Integer> newNumbers = (ArrayList<Integer>) objectInputStream
  .readObject();

assertEquals(2, (int) newNumbers.get(0));
assertEquals(3, (int) newNumbers.get(1));
assertEquals(5, (int) newNumbers.get(2));
assertEquals(7, (int) newNumbers.get(3));

```

对于不同的数据类型，可以使用内容类型属性来选择不同的解码方法。

## 6. 、移动和重命名数据

### 6.1. 对象

可以在 S3 中对象，而无需检索它们。

让我们从 5.2 节中我们的测试文件，并验证结果：

```java
S3Object targetObject = new S3Object("testcopy.jpg");
s3Service.copyObject(
  BucketName, "test.jpg", 
  "myuniquebucket", targetObject, false);
S3Object newFileObject = s3Service.getObject(
  "myuniquebucket", "testcopy.jpg");

File newFile = new File("src/test/resources/testcopy.jpg");
Files.copy(
  newFileObject.getDataInputStream(), 
  newFile.toPath(), 
  REPLACE_EXISTING);
String origMD5 = getFileMD5("src/test/resources/test.jpg");
String newMD5 = getFileMD5("src/test/resources/testcopy.jpg");
 
assertTrue(origMD5.equals(newMD5));

```

我们可以在同一个桶内或两个不同的桶之间对象。

如果最后一个参数为真，的对象将接收新的元数据。否则，它将保留源对象的元数据。

如果我们想修改元数据，我们可以将标志设置为 true：

```java
targetObject = new S3Object("testcopy.jpg");
targetObject.addMetadata("My_Custom_Field", "Hello, World!");
s3Service.copyObject(
  "myuniquebucket", "test.jpg", 
  "myuniquebucket", targetObject, true);

```

### 6.2. 移动物体

可以将对象移动到同一区域中的另一个 S3 存储桶。移动操作先是，然后是删除操作。

如果操作失败，则不会删除源对象。如果删除操作失败，该对象仍将存在于源位置和目标位置。

移动对象看起来类似于它：

```java
s3Service.moveObject(
  "myuniquebucket",
  "test.jpg",
  "myotheruniquebucket",
  new S3Object("spidey.jpg"),
  false);

```

### 6.3. 重命名对象

JetS3t 有一个重命名对象的便捷方法。 要更改对象名称，我们只需使用新的S3Object调用它：

```java
s3Service.renameObject(
  "myuniquebucket", "test.jpg", new S3Object("spidey.jpg"));

```

## 七. 总结

在本教程中，我们使用 JetS3t 连接到 Amazon S3。我们创建和删除了存储桶。然后我们将不同类型的数据添加到桶中并检索数据. 总结一下，我们并移动了我们的数据。