## 1. 概述

当我们处理多线程项目时，我们知道**如果多个线程共享对象，而这些对象的实现并没有考虑到[线程安全](https://www.baeldung.com/java-thread-safety)，则线程可能会出现意外行为**。

我们中的许多人可能都遇到过线程安全问题。因此，经常想到“这个类是线程安全的吗？”这个问题。

Java应用程序通过JDBC访问关系数据库并使用多线程是很常见的。在这个快速教程中，我们将讨论java.sql.Connection是否是线程安全的。

## 2. java.sql.Connection接口

当我们通过JDBC从我们的应用程序访问数据库时，我们将直接或间接地使用[java.sql.Connection](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Connection.html)对象。我们依靠这些连接对象来执行数据库操作。因此，java.sql.Connection是JDBC中一个非常重要的类型。

这也是多个线程需要同时与数据库对话的常见场景。因此，我们经常听到这样的问题：“java.sql.Connection是线程安全的吗？”

在接下来的几节中，我们将仔细研究这个问题。此外，我们将讨论在多个线程中使用java.sql.Connection对象的正确方法，以便多个线程可以同时访问数据库。

## 3. 线程安全和java.sql.Connection

首先，让我们快速谈谈[线程安全](https://www.baeldung.com/java-thread-safety)。**线程安全是一种编程方法。也就是说，它是一个与实现相关的概念**。因此，我们可以使用不同的技术使实现线程安全-例如，无状态实现、不可变实现等等。

现在，让我们看一下java.sql.Connection。首先，它是一个接口-它不包含任何实现。因此，**如果我们笼统地问：“java.sql.Connection是线程安全的吗？”没有多大意义**。我们必须检查实现这个接口的类来决定一个实现是否是线程安全的。

那么，马上就会想到几个问题：哪些类实现了这个接口？它们是线程安全的吗？

通常，我们不会在应用程序代码中实现java.sql.Connection接口。**JDBC驱动程序将实现此接口**，以便我们可以获得与特定数据库的连接，例如SQL Server或Oracle。

因此，Connection实现的线程安全性完全依赖于JDBC驱动程序。

接下来，我们将探索几个数据库JDBC驱动程序作为示例。

## 4. java.sql.Connection实现实例

Microsoft SQL Server和Oracle Database是两种广泛使用的关系型数据库产品。

在本节中，我们将介绍这两个数据库的JDBC驱动程序，并讨论它们对java.sql.Connection接口的实现是否是线程安全的。

### 4.1 Microsoft SQL Server

根据其Javadoc，Microsoft SQL Server驱动程序类[SQLServerConnection](https://www.javadoc.io/doc/com.microsoft.sqlserver/mssql-jdbc/latest/com.microsoft.sqlserver.jdbc/com/microsoft/sqlserver/jdbc/SQLServerConnection.html)实现了java.sql.Connection接口并且不是线程安全的：

>   SQLServerConnection不是线程安全的，但是从单个连接创建的多个语句可以在并发线程中同时处理。

因此，**这意味着我们不应该在线程之间共享SQLServerConnection对象，但我们可以共享从同一个SQLServerConnection对象创建的语句**。

接下来，让我们来看看另一个知名的数据库产品-Oracle Database。

### 4.2 Oracle Database

官方的Oracle JDBC驱动程序以线程安全的方式实现了java.sql.Connection接口。

Oracle在其[官方文档](https://docs.oracle.com/cd/B19306_01/java.102/b14355/apxtips.htm#i1005436)中声明了其Connection实现的线程安全性：

>   Oracle JDBC驱动程序为使用Java多线程的应用程序提供全面支持并对其进行了高度优化...
>
>   但是，Oracle强烈建议不要在多个线程之间共享数据库连接。避免允许多个线程同时访问一个连接...

那么，根据上面的描述，我们可以说Oracle的连接实现是线程安全的。但是，**“强烈建议不要”在多个线程之间共享一个连接对象**。

因此，从SQL Server和Oracle示例中，我们知道我们不能假设java.sql.Connection实现是线程安全的。那么，我们可能会问，如果我们想要多个线程并发访问一个数据库，那么正确的做法是什么？让我们在下一节中弄清楚。

## 5. 使用连接池

当我们从应用程序访问数据库时，我们首先需要建立与数据库的连接。这被认为是一项昂贵的操作。为了提高性能，通常我们会使用[连接池](https://www.baeldung.com/java-connection-pooling)。

让我们快速了解连接池在多线程场景中的工作原理。

连接池包含多个连接对象。我们可以配置池的大小。

当多个线程需要并发访问一个数据库时，它们会从连接池中请求连接对象。

如果池中仍有空闲连接，则线程将获取一个连接对象并开始其数据库操作。线程完成其工作后，它将连接返回到池中。

如果池中没有空闲连接，线程将等待另一个线程将连接对象返回到池中。

因此，**连接池允许多个线程使用不同的连接对象并发访问数据库，而不是共享同一个**。

此外，通过这种方式，我们就不用关心Connection接口的实现是否线程安全了。

## 6. 总结

在本文中，我们讨论了一个常见问题：java.sql.Connection是线程安全的吗？

由于java.sql.Connection是一个接口，因此很难预测这些实现是否是线程安全的。

此外，我们已经指出，如果多个线程需要同时访问数据库，连接池是处理连接的正确方法。