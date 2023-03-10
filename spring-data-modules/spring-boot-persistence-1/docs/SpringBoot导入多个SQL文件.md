## 1. 概述

Spring Boot允许我们将示例数据导入我们的数据库，主要用于为集成测试准备数据。有两种开箱即用的方法，**我们可以使用import.sql(Hibernate支持)或data.sql(Spring JDBC支持)文件来加载数据**。

然而，有时我们希望将一个大的SQL文件拆分成几个较小的文件，例如，为了更好的可读性或在模块之间共享一些带有初始化数据的文件。

在本教程中，我们介绍如何同时使用Hibernate和Spring JDBC实现这一点。

## 2. Hibernate支持

**我们可以使用spring.jpa.properties.hibernate.hbm2ddl.import_files属性定义包含要加载的示例数据的文件**，它可以在测试资源文件夹内的application.properties文件中设置。

在这种情况下，我们只想为JUnit测试加载示例数据，该值必须是要导入的文件的逗号分隔列表：

```properties
spring.jpa.properties.hibernate.hbm2ddl.import_files=import_active_users.sql,import_inactive_users.sql
```

此配置将从两个文件加载示例数据：import_active_users.sql和import_inactive_users.sql。这里要提到的重要一点是，**我们必须使用前缀spring.jpa.properties将值(JPA配置)传递给EntityManagerFactory**。

接下来，我们演示如何使用Spring JDBC支持来做到这一点。

## 3. Spring JDBC支持

初始数据和**Spring JDBC支持的配置与Hibernate非常相似，我们必须使用spring.sql.init.data-locations属性**：

```properties
spring.sql.init.data-locations=import_active_users.sql,import_inactive_users.sql
```

按上述方式设置值会得到与Hibernate支持中相同的结果；但是，**此解决方案的一个显著优势是可以使用Ant样式模式定义值**：

```properties
spring.sql.init.data-locations=import_*_users.sql
```

上面的配置告诉Spring搜索名称与import_*_users.sql模式匹配的所有文件并导入其中的数据。

该属性在Spring Boot 2.5.0中引入；**在早期版本的Spring Boot中，我们需要使用spring.datasource.data属性**。

## 4. 总结

在这篇简短的文章中，我们展示了如何配置Spring Boot应用程序以从自定义SQL文件加载初始数据。

最后，我们展示了两种可能性：Hibernate和Spring JDBC。它们都工作得很好，选择哪一个取决于开发人员。