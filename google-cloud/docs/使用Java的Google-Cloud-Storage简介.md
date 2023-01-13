## 1. 概述

[Google Cloud Storage](https://cloud.google.com/storage/)提供根据位置、访问频率和成本为单个应用程序的需求量身定制的在线存储。与 Amazon Web Services 不同，Google Cloud Storage 使用单一 API 进行高、中、低频访问。

与大多数云平台一样，谷歌提供免费访问层；定价细节在[这里。](https://cloud.google.com/storage/pricing)

在本教程中，我们将连接到存储、创建存储桶、写入、读取和更新数据。在使用 API 读取和写入数据时，我们还将使用[gsutil](https://cloud.google.com/storage/docs/gsutil)云存储实用程序。

## 2.谷歌云存储设置

### 2.1. Maven 依赖

我们需要向我们的pom.xml添加一个依赖项：

```xml
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-storage</artifactId>
    <version>1.17.0</version>
</dependency>
```

Maven Central 拥有[最新版本的库](https://search.maven.org/classic/#search|ga|1|a%3A"google-cloud-storage")。

### 2.2. 创建身份验证密钥

在我们连接到谷歌云之前，我们需要配置[身份验证](https://cloud.google.com/docs/authentication/)。谷歌云平台 (GCP) 应用程序从 JSON 配置文件加载私钥和配置信息。我们通过 GCP 控制台生成此文件。访问控制台需要有效的 Google Cloud Platform 帐户。

我们通过以下方式创建配置：

1.  前往[谷歌云平台控制台](https://console.cloud.google.com/apis/credentials/serviceaccountkey)
2.  如果我们还没有定义 GCP 项目，我们点击创建按钮并输入项目名称，例如“ baeldung-cloud-tutorial ”
3.  从下拉列表中选择“新服务帐户”
4.  在帐户名称字段中添加一个名称，例如“ baeldung-cloud-storage ”。
5.  在“角色”下选择项目，然后在子菜单中选择所有者。
6.  选择创建，控制台下载私钥文件。

步骤#6 中的角色授权帐户访问项目资源。为了简单起见，我们授予此帐户对所有项目资源的完全访问权限。

对于生产环境，我们将定义一个对应于应用程序所需访问权限的角色。

### 2.3. 安装身份验证密钥

接下来，我们将从 GCP 控制台下载的文件到一个方便的位置，并将GOOGLE_APPLICATION_CREDENTIALS环境变量指向它。这是加载凭据的最简单方法，尽管我们将在下面查看另一种可能性。

对于 Linux 或 Mac：

```shell
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/file"
```

对于 Windows：

```shell
set GOOGLE_APPLICATION_CREDENTIALS="C:pathtofile"
```

### 2.4. 安装云工具

Google 提供了多种工具来管理他们的云平台。在本教程中，我们将使用gsutil与 API 一起读取和写入数据。

我们可以通过两个简单的步骤来做到这一点：

1.  [按照此处](https://cloud.google.com/sdk/docs/)针对我们平台的说明安装 Cloud SDK 。
2.  在此处按照我们平台的快速入门[。](https://cloud.google.com/sdk/docs/quickstarts)在Initialize the SDK的第 4 步中，我们选择上面 2.2 节第 4 步中的项目名称(“ baeldung-cloud-storage ”或使用的任何名称)。

gsutil现已安装并配置为从我们的云项目中读取数据。

## 3. 连接存储并创建桶

### 3.1. 连接到存储

在我们使用谷歌云存储之前，我们必须创建一个服务对象。如果我们已经设置了GOOGLE_APPLICATION_CREDENTIALS环境变量，我们可以使用默认实例：

```java
Storage storage = StorageOptions.getDefaultInstance().getService();

```

如果我们不想使用环境变量，我们必须创建一个Credentials实例并使用项目名称将其传递给Storage ：

```java
Credentials credentials = GoogleCredentials
  .fromStream(new FileInputStream("path/to/file"));
Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
  .setProjectId("baeldung-cloud-tutorial").build().getService();

```

### 3.2. 创建桶

现在我们已经连接并通过身份验证，我们可以创建一个存储桶。桶是容纳对象的容器。它们可用于组织和控制数据访问。

桶中的对象数量没有限制。GCP[限制](https://cloud.google.com/storage/quotas)了对桶的操作数量，并鼓励应用程序设计人员强调对对象而不是对桶的操作。

创建桶需要BucketInfo：

```java
Bucket bucket = storage.create(BucketInfo.of("baeldung-bucket"));

```

对于这个简单的示例，我们使用存储桶名称并接受默认属性。桶名必须是 全局唯一的。如果我们选择一个已经被使用过的名称，create()将失败。

### 3.3. 使用gsutil 检查存储桶

由于我们现在有了一个存储桶，我们可以使用gsutil 对其进行检查。

让我们打开命令提示符看看：

```shell
$ gsutil ls -L -b gs://baeldung-1-bucket/
gs://baeldung-1-bucket/ :
	Storage class:			STANDARD
	Location constraint:		US
	Versioning enabled:		None
	Logging configuration:		None
	Website configuration:		None
	CORS configuration: 		None
	Lifecycle configuration:	None
	Requester Pays enabled:		None
	Labels:				None
	Time created:			Sun, 11 Feb 2018 21:09:15 GMT
	Time updated:			Sun, 11 Feb 2018 21:09:15 GMT
	Metageneration:			1
	ACL:
	  [
	    {
	      "entity": "project-owners-385323156907",
	      "projectTeam": {
	        "projectNumber": "385323156907",
	        "team": "owners"
	      },
	      "role": "OWNER"
	    },
	    ...
	  ]
	Default ACL:
	  [
	    {
	      "entity": "project-owners-385323156907",
	      "projectTeam": {
	        "projectNumber": "385323156907",
	        "team": "owners"
	      },
	      "role": "OWNER"
	    },
            ...
	  ]
```

gsutil看起来很像 shell 命令，熟悉 Unix 命令行的人在这里应该会感到很舒服。请注意，我们将路径作为 URL 传递给我们的存储桶：gs://baeldung-1-bucket/，以及一些其他选项。

ls选项生成一个列表或对象或桶，-L选项表示我们想要一个详细的列表——因此我们收到了有关桶的详细信息，包括创建时间和访问控制。

让我们向存储桶中添加一些数据！

## 4. 读取、写入和更新数据

在 Google Cloud Storage 中，对象存储在Blob中；Blob名称可以包含任何 Unicode 字符，限制为 1024 个字符。

### 4.1. 写入数据

让我们将一个字符串保存到我们的桶中：

```java
String value = "Hello, World!";
byte[] bytes = value.getBytes(UTF_8);
Blob blob = bucket.create("my-first-blob", bytes);

```

如所见，对象只是存储桶中的字节数组，因此我们通过简单地对其原始字节进行操作来存储字符串。

### 4.2. 使用gsutil读取数据

现在我们有了一个包含对象的桶，让我们来看看gsutil。

让我们首先列出我们的桶的内容：

```shell
$ gsutil ls gs://baeldung-1-bucket/
gs://baeldung-1-bucket/my-first-blob
```

我们再次将ls选项传递给gsutil ，但省略了-b和-L，因此我们要求提供一个简短的对象列表。我们收到每个对象的 URI 列表，在我们的例子中就是一个。

让我们检查一下对象：

```shell
$ gsutil cat gs://baeldung-1-bucket/my-first-blob
Hello World!
```

Cat将对象的内容连接到标准输出。我们看到我们写入Blob的字符串。

### 4.3. 读取数据

Blob 在创建时会分配一个BlobId。

检索 Blob 的最简单方法是使用BlobId：

```java
Blob blob = storage.get(blobId);
String value = new String(blob.getContent());

```

我们将 id 传递给Storage并返回Blob，然后getContent()返回字节。

如果我们没有BlobId，我们可以按名称搜索 Bucket：

```java
Page<Blob> blobs = bucket.list();
for (Blob blob: blobs.getValues()) {
    if (name.equals(blob.getName())) {
        return new String(blob.getContent());
    }
}
```

### 4.4. 更新数据

我们可以通过检索它然后访问它的WriteableByteChannel来更新Blob：

```java
String newString = "Bye now!";
Blob blob = storage.get(blobId);
WritableByteChannel channel = blob.writer();
channel.write(ByteBuffer.wrap(newString.getBytes(UTF_8)));
channel.close();
```

让我们检查更新的对象：

```shell
$ gsutil cat gs://baeldung-1-bucket/my-first-blob
Bye now!
```

### 4.5. 将对象保存到文件，然后删除

让我们将更新后的对象保存到一个文件中：

```shell
$ gsutil copy gs://baeldung-1-bucket/my-first-blob my-first-blob
Copying gs://baeldung-1-bucket/my-first-blob...
/ [1 files][    9.0 B/    9.0 B]
Operation completed over 1 objects/9.0 B.
Grovers-Mill:~ egoebelbecker$ cat my-first-blob
Bye now!
```

正如预期的那样，选项将对象到命令行中指定的文件名。

gsutil可以将任何对象从 Google Cloud Storage 到本地文件系统，前提是有足够的空间来存储它。

我们将通过清理来完成：

```shell
$ gsutil rm gs://baeldung-1-bucket/my-first-blob
Removing gs://baeldung-1-bucket/my-first-blob...
/ [1 objects]
Operation completed over 1 objects.
$ gsutil ls gs://baeldung-1-bucket/
$
```

rm(del也适用)删除指定的对象

## 5.总结

在这个简短的教程中，我们为 Google Cloud Storage 创建了凭据并连接到基础架构。我们创建了一个桶，写入了数据，然后读取并修改了它。在使用 API 时，我们还使用gsutil在创建和读取数据时检查云存储。

我们还讨论了如何使用存储桶以及如何高效地写入和修改数据。