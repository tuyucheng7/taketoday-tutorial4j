## 1. 概述

在本文中，我们将讨论[JDBC](https://www.baeldung.com/java-jdbc)连接状态的某些方面。首先，我们将了解连接丢失的最常见原因。然后，我们将学习如何确定连接状态。

我们还将学习如何在运行 SQL 语句之前验证连接。

## 2.JDBC连接

Connection类负责与数据源通信。连接可能因各种原因而丢失：

-   数据库服务器已关闭
-   网络连接
-   重用关闭的连接

在连接丢失时运行任何数据库操作都将导致SQLException。此外，我们可以检查异常以了解有关问题的详细信息。

## 3. 检查连接

有不同的方法来检查连接。我们将查看这些方法来决定何时使用它们中的每一个。

### 3.1. 连接状态

我们可以使用isClosed()方法检查连接状态。使用此方法，无法授予 SQL 操作。但是，检查连接是否打开会很有帮助。

让我们在运行 SQL 语句之前创建一个状态条件：

```java
public static void runIfOpened(Connection connection) throws SQLException
{
    if (connection != null && !connection.isClosed()) {
        // run sql statements
    } else {
        // handle closed connection path
    }
}
```

### 3.2. 连接验证

即使打开了连接，也可能会因为上一节中描述的原因而丢失。因此，可能需要在运行任何 SQL 语句之前验证连接。

从 version 1.6 开始，Connection类提供了验证方法 。 首先，它向数据库提交验证查询。其次，它使用超时参数作为操作的阈值。最后，如果操作在timeout内成功，则连接被标记为有效。

让我们看看如何在运行任何语句之前验证连接：

```java
public static void runIfValid(Connection connection)
        throws SQLException
{
    if (connection.isValid(5)) {
        // run sql statements
    }
    else {
        // handle invalid connection
    }
}
```

在这种情况下，超时为 5 秒。零值表示超时不适用于验证。另一方面，小于零的值将抛出SQLException。

### 3.3. 自定义验证

创建自定义验证方法是有充分理由的。例如，我们可以使用没有验证方法的遗留 JDBC。同样，我们的项目可能需要在所有语句之前运行自定义验证查询。

让我们创建一个方法来运行预定义的验证查询：

```java
public static boolean isConnectionValid(Connection connection)
{
    try {
        if (connection != null && !connection.isClosed()) {
            // Running a simple validation query
            connection.prepareStatement("SELECT 1");
            return true;
        }
    }
    catch (SQLException e) {
        // log some useful data here
    }
    return false;
}
```

首先，该方法检查连接状态。其次，它尝试运行验证查询，在成功时返回true 。最后，如果验证查询没有运行或失败，它会返回false 。

现在我们可以在运行任何语句之前使用自定义验证：

```java
public static void runIfConnectionValid(Connection connection)
{
    if (isConnectionValid(connection)) {
        // run sql statements
    }
    else {
        // handle invalid connection
    }
}
```

当然，运行一个简单的查询是验证数据库连接性的不错选择。但是，根据目标驱动程序和数据库，还有其他有用的方法：

-   自动提交——使用 connection.getAutocommit()和 connection.setAutocommit()
-   元数据——使用connection.getMetaData()

## 4.连接池

数据库连接在资源方面是昂贵的。连接池是管理和配置这些连接的好策略。简而言之，它们可以降低连接生命周期的成本。

所有[Java 连接池](https://www.baeldung.com/java-connection-pooling)框架都有自己的连接验证实现。此外，它们中的大多数都使用可参数化的验证查询。

以下是一些最流行的框架：

-   Apache Commons DBCP – validationQuery、validationQueryTimeout
-   Hikari CP – connectionTestQuery, validationTimeout
-   C3P0——首选测试查询

## 5. 总结

在本文中，我们了解了 JDBC 连接状态的基础知识。我们回顾了Connection类的一些有用方法。之后，我们描述了一些在运行 SQL 语句之前验证连接的替代方法。