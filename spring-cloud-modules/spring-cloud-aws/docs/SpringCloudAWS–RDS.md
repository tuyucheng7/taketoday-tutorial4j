## 1.RDS支持

### 1.1 简单配置

**Spring Cloud AWS只需指定RDS数据库标识符和主密码即可自动创建DataSource**。用户名、JDBC驱动程序和完整的URL都由Spring解析。

如果AWS帐户有一个RDS实例，其数据库实例标识符为spring-cloud-test-db，主密码为se3retpass，那么创建DataSource所需的全部是application.properties中的以下行：

```properties
cloud.aws.rds.spring-cloud-test-db.password=se3retpass
```

如果你希望使用RDS默认值以外的值，可以添加其他三个属性：

```properties
cloud.aws.rds.spring-cloud-test-db.username=testuser
cloud.aws.rds.spring-cloud-test-db.readReplicaSupport=true
cloud.aws.rds.spring-cloud-test-db.databaseName=test
```

### 1.2 自定义数据源

在没有Spring Boot的应用程序或需要自定义配置的情况下，**我们还可以使用基于Java的配置创建数据源**：

```java
@Configuration
@EnableRdsInstance(
      dbInstanceIdentifier = "spring-cloud-test-db",
      password = "se3retpass")
public class SpringRDSSupport {

    @Bean
    public RdsInstanceConfigurer instanceConfigurer() {
        return () -> {
            TomcatJdbcDataSourceFactory dataSourceFactory
                  = new TomcatJdbcDataSourceFactory();
            dataSourceFactory.setInitialSize(10);
            dataSourceFactory.setValidationQuery("SELECT 1");
            return dataSourceFactory;
        };
    }
}
```

另请注意，我们需要添加正确的JDBC驱动程序依赖项。

## 2. 总结

在本文中，我们了解了访问AWS RDS服务的各种方式；在本系列的[下一篇](https://www.baeldung.com/spring-cloud-aws-messaging)也是最后一篇文章中，我们将了解AWS消息传递支持。