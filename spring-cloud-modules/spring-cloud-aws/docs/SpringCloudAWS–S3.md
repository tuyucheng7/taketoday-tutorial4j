## 1. 简单的S3下载

让我们从轻松访问存储在S3上的文件开始：

```java
@Autowired
ResourceLoader resourceLoader;

public void downloadS3Object(String s3Url) throws IOException {
    Resource resource = resourceLoader.getResource(s3Url);
    File downloadedS3Object = new File(resource.getFilename());
 
    try (InputStream inputStream = resource.getInputStream()) {
        Files.copy(inputStream, downloadedS3Object.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
```

## 2. 简单的S3上传

我们还可以上传文件：

```java
public void uploadFileToS3(File file, String s3Url) throws IOException {
    WritableResource resource = (WritableResource) resourceLoader
        .getResource(s3Url);
 
    try (OutputStream outputStream = resource.getOutputStream()) {
        Files.copy(file.toPath(), outputStream);
    }
}
```

## 3. S3 URL结构

s3 Url使用以下格式表示：

```shell
s3://<bucket>/<object>
```

例如，如果文件bar.zip位于my-s3-bucket存储桶上的文件夹foo中，则URL将为：

```shell
s3://my-s3-bucket/foo/bar.zip
```

而且，我们还可以使用ResourcePatternResolver和Ant风格的模式匹配一次下载多个对象：

```java
private ResourcePatternResolver resourcePatternResolver;
 
@Autowired
public void setupResolver(ApplicationContext applicationContext, AmazonS3 amazonS3) {
    this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3, applicationContext);
 }

public void downloadMultipleS3Objects(String s3Url) throws IOException {
    Resource[] allFileMatchingPatten = this.resourcePatternResolver
        .getResources(s3Url);
        // ...
    }
}
```

**URL可以包含通配符而不是确切名称**。

例如s3://my-s3-bucket/\*\*/a\*.txt URL将在my-s3-bucket的任何文件夹中递归查找名称以“a”开头的所有文本文件。

请注意，bean ResourceLoader和ResourcePatternResolver是在应用程序启动时使用Spring Boot的自动配置功能创建的。

## 4. 总结

我们已经完成了-这是对使用Spring Cloud AWS访问S3的快速而切题的介绍。