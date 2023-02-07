## 1. EC2元数据访问

AWS [EC2MetadataUtils](https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/util/EC2MetadataUtils.html)类提供静态方法来访问实例元数据，例如AMI Id和实例类型。**借助Spring Cloud AWS，我们可以使用@Value注解直接注入此元数据**。

这可以通过在任何配置类上添加@EnableContextInstanceData注解来启用：

```java
@Configuration
@EnableContextInstanceData
public class EC2EnableMetadata {
    // ...
}
```

**在Spring Boot环境中，默认启用实例元数据，这意味着不需要此配置**。

然后，我们可以注入值：

```java
@Value("${ami-id}")
private String amiId;

@Value("${hostname}")
private String hostname;

@Value("${instance-type}")
private String instanceType;

@Value("${services/domain}")
private String serviceDomain;
```

### 1.1 自定义标签

此外，Spring还支持注入用户定义的[标签](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/Using_Tags.html)。我们可以通过使用以下XML配置在context-instance-data中定义属性user-tags-map来启用此功能：

```xml
<beans...>
    <aws-context:context-instance-data user-tags-map="instanceData"/>
</beans>
```

现在，让我们借助Spring表达式语法注入用户定义的标签：

```java
@Value("#{instanceData.myTagKey}")
private String myTagValue;
```

## 2. EC2客户端

此外，如果为实例配置了用户标签，Spring将创建一个Amazon EC2客户端，我们可以使用@Autowired将其注入到我们的代码中：

```java
@Autowired
private AmazonEC2 amazonEc2;
```

请注意，只有当应用程序在EC2实例上运行时，这些功能才有效。

## 3. 总结

这是对使用Spring Cloud AWS访问EC2d数据的快速而切题的介绍。