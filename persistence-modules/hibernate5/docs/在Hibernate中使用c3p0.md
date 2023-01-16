## 1. 概述

建立数据库连接是相当昂贵的。数据库[连接池](https://www.baeldung.com/java-connection-pooling)是降低这种支出的行之有效的方法。

在本教程中，我们将讨论如何将[c3p0](https://www.mchange.com/projects/c3p0/)与 Hibernate 结合使用来建立连接池。

## 2.什么是c3p0？

c3p0 是 一个Java库，它提供了一种管理数据库连接的便捷方法。

简而言之，它通过创建连接池来实现这一点。它还有效地处理了 Statement和 ResultSet在使用后的清理。此清理是必要的，以确保优化资源使用并且不会发生可避免的死锁。

该库与各种传统 JDBC 驱动程序无缝集成。此外，它还提供了一个层，用于使基于 DriverManager 的 JDBC 驱动程序适应更新的javax.sql.DataSource 方案。

而且，因为 Hibernate 支持通过 JDBC 连接到数据库，所以一起使用 Hibernate 和 c3p0 很简单。

## 3. 使用 Hibernate 配置 c3p0

现在让我们看看如何配置现有的 Hibernate 应用程序以使用 c3p0 作为其数据库连接管理器。

### 3.1. Maven 依赖项

首先，我们首先需要添加[hibernate-c3p0](https://mvnrepository.com/artifact/org.hibernate/hibernate-c3p0) maven 依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-c3p0</artifactId>
    <version>5.3.6.Final</version>
</dependency>
```

使用 Hibernate 5，只需添加上述依赖项就足以启用 c3p0。只要没有指定其他 JDBC 连接池管理器，就是如此。

因此，在我们添加依赖后，我们可以运行我们的应用程序并检查日志：

```plaintext
Initializing c3p0-0.9.5.2 [built 08-December-2015 22:06:04 -0800; debug? true; trace: 10]
Initializing c3p0 pool... com.mchange.v2.c3p0.PoolBackedDataSource@34d0bdb9 [ ... default settings ... ]
```

如果正在使用另一个 JDBC 连接池管理器，我们可以强制我们的应用程序使用 c3p0。我们只需要在属性文件中将provider_class设置为 C3P0ConnectionProvider ：

```plaintext
hibernate.connection.provider_class=org.hibernate.connection.C3P0ConnectionProvider
```

### 3.2. 连接池属性

最终，我们需要覆盖默认配置。我们可以将自定义属性添加到hibernate.cfg.xml 文件中：

```xml
<property name="hibernate.c3p0.min_size">5</property>
<property name="hibernate.c3p0.max_size">20</property>
<property name="hibernate.c3p0.acquire_increment">5</property>
<property name="hibernate.c3p0.timeout">1800</property>
```

同样， hibernate.properties文件可以包含相同的设置：

```plaintext
hibernate.c3p0.min_size=5
hibernate.c3p0.max_size=20
hibernate.c3p0.acquire_increment=5
hibernate.c3p0.timeout=1800
```

min_size属性 指定它在任何给定时间应保持的最小连接数。默认情况下，它会保持至少三个连接。此设置还定义池的初始大小。

max_size属性指定它在任何给定时间可以维护的最大连接数。默认情况下，它将最多保留 15 个连接。

acquire_increment 属性指定如果池用完可用连接它应该尝试获取多少连接。默认情况下，它将尝试获取三个新连接。

timeout属性 指定未使用的连接在被丢弃之前将保留的秒数。默认情况下，池中的连接永远不会过期。

我们可以通过再次检查日志来验证新的池设置：

```plaintext
Initializing c3p0-0.9.5.2 [built 08-December-2015 22:06:04 -0800; debug? true; trace: 10]
Initializing c3p0 pool... com.mchange.v2.c3p0.PoolBackedDataSource@b0ad7778 [ ... new settings ... ]
```

这些是基本的连接池属性。另外，其他配置属性可以参考[官方指南](https://www.mchange.com/projects/c3p0/#configuration_properties)。

## 5.总结

在本文中，我们讨论了如何将 c3p0 与 Hibernate 结合使用。我们查看了一些常见的配置属性并将 c3p0 添加到测试应用程序中。

对于大多数环境，我们建议使用连接池管理器(如 c3p0 或[HikariCP)](https://www.baeldung.com/hikaricp)而不是传统的 JDBC 驱动程序。