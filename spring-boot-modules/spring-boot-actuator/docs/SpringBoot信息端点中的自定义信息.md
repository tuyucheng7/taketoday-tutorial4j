## 1. 概述

在这篇快速文章中，我们将了解如何自定义Spring Boot Actuators的/info端点。

请参阅[本文]()以了解有关Boot中的Actuator以及如何配置它们的更多信息。

## 2. /info中的静态属性

如果我们有一些静态信息，例如应用程序的名称或长时间不变的版本，那么最好将这些详细信息添加到我们的application.properties文件中：

```properties
## Configuring info endpoint
info.app.name=Spring Sample Application
info.app.description=This is my first Spring Boot application
info.app.version=1.0.0
```

这就是我们在/info端点上提供此数据所需做的全部工作，Spring会自动将所有以info为前缀的属性添加到/info端点：

```json
{
    "app": {
        "description": "This is my first Spring Boot application",
        "version": "1.0.0",
        "name": "Spring Sample Application"
    }
}
```

## 3. /info中的环境变量

现在让我们在/info端点中公开一个环境变量：

```properties
info.java-vendor=${java.specification.vendor}
```

这会将Java供应商公开给我们的/info端点：

```json
{
    "app": {
        "description": "This is my first Spring Boot application",
        "version": "1.0.0",
        "name": "Spring Sample Application"
    },
    "java-vendor": "Oracle Corporation"
}
```

请注意，所有环境变量都已在/env端点上可用。但是，同样可以在/info端点上快速公开相同的内容。

## 4. 来自持久层的自定义数据

现在让我们更进一步，从持久性存储中公开一些有用的数据。

为此，我们需要实现InfoContributor接口并重写contribute()方法：

```java
@Component
public class TotalUsersInfoContributor implements InfoContributor {

    @Autowired
    UserRepository userRepository;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Integer> userDetails = new HashMap<>();
        userDetails.put("active", userRepository.countByStatus(1));
        userDetails.put("inactive", userRepository.countByStatus(0));

        builder.withDetail("users", userDetails);
    }
}
```

第一件事是我们需要将实现类标记为@Component，然后将所需的详细信息添加到提供给contribute()方法的Info.Builder实例。

这种方法为我们提供了很多关于我们可以向/info端点公开的内容的灵活性：

```json
{
    ...other /info data...,
    ...
    "users": {
        "inactive": 2,
        "active": 3
    }
}
```

## 5. 总结

在本教程中，我们介绍了将自定义数据添加到/info端点的各种方法。

请注意，在另一篇文章中我们还讨论了如何将[git信息]()添加到/info端点。