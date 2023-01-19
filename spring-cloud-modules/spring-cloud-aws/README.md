## Spring Cloud AWS

## 相关文章

+ []()

## 运行集成测试

要运行实时测试，我们需要拥有一个AWS账户并生成用于编程访问的API密钥。编辑`application.properties`文件以添加以下属性：

```properties
cloud.aws.credentials.accessKey=YourAccessKey
cloud.aws.credentials.secretKey=YourSecretKey
cloud.aws.region.static=us-east-1
```

要测试从RDS实例自动创建数据源，我们还需要在AWS账户中创建RDS实例。假设RDS实例称为`spring-cloud-test-db`
，主密码为`se3retpass`，那么我们需要在`application.properties`中编写以下内容：

```properties
cloud.aws.rds.spring-cloud-test-db.password=se3retpass
```

此项目下有多个应用程序类可用。要启动InstanceProfileAwsApplication应用程序，请将`pom.xml`下的`start-class`替换为：

```shell
<start-class>cn.tuyucheng.taketoday.spring.cloud.aws.InstanceProfileAwsApplication</start-class>
```