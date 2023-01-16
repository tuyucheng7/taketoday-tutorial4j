## 1. 简介

[JDBC 是一组规范，定义了Java 数据库连接](https://www.baeldung.com/java-jdbc)契约的 API 和 SPI 部分。该标准将 JDBC 驱动程序抽象定义为与数据库交互的主要入口点。

在本教程中，我们将了解加载 JDBC 驱动程序所需的一些基本步骤。

## 2. JDBC 驱动程序

要连接到数据库，我们必须获得 JDBC 驱动程序的实例。

我们可以通过指定 JDBC URL 连接字符串通过DriverManager获取它。这样的 URL 包含数据库引擎的类型、数据库名称、主机名和端口，以及特定于数据库供应商的其他连接参数。

使用连接字符串，我们可以获得一个数据库连接对象，它是 JDBC 中与数据库通信的基本单元：

```java
Connection con = DriverManager.getConnection(
   "jdbc:postgresql://localhost:21500/test?user=fred&password=secret&ssl=true");

```

如果唯一的指示是指定的 URL，驱动程序管理器如何知道要使用哪个驱动程序？

类路径上可能有很多 JDBC 驱动程序，因此必须有一种方法来唯一区分每个驱动程序。

## 3. 遗留方法

在 JDBC 版本 4 和JavaSE 1.6 之前，JVM 中没有通用机制可以自动发现和注册服务。因此，需要一个手动步骤来[按名称加载 JDBC 驱动程序类](https://www.baeldung.com/java-reflection)：

```java
Class.forName("oracle.jdbc.driver.OracleDriver");
```

类加载过程触发一个静态初始化例程，该例程将驱动程序实例注册到DriverManager并将此类与数据库引擎标识符相关联，例如oracle或 postgres。

注册完成后，我们可以在 JDBC URL 中使用这个标识符作为jdbc:oracle。

典型的驱动程序注册例程将实例化驱动程序实例并将其传递给DriverManager.registerDriver 方法：

```java
public static void register() throws SQLException {
    if (isRegistered()) {
        throw new IllegalStateException("Driver is already registered. It can only be registered once.");
    } else {
        Driver registeredDriver = new Driver();
        DriverManager.registerDriver(registeredDriver);
        Driver.registeredDriver = registeredDriver;
    }
}
```

上面的示例显示了使用DriverManager注册 Postgres JDBC 驱动程序。它由 JVM 作为静态初始化程序的一部分触发。

通过设置jdbc.drivers系统属性，即使使用传统方法也可以部分自动化此步骤：

```bash
java -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
```

指定此属性时，驱动程序管理器将自动尝试加载指定的 JDBC 驱动程序。

## 4.JDBC 4 方法

Java 1.6 和[服务提供者机制](https://www.baeldung.com/java-spi)解决了自动服务发现的问题。它使服务提供者能够通过将服务放在包含服务的 JAR 文件内的META-INF/services下来声明他们的服务。

此机制会自动注册驱动程序，因此不再需要手动加载类的步骤。但是，即使有服务提供者，手动类加载也不会导致失败。使用最新的 JVM 和 JDBC 4 驱动程序显式调用驱动程序加载是完全合法的。

服务提供者规范简单地用声明性方法取代了手动类加载。例如，PostgreSQL JDBC 驱动程序在META-INF/services/下有一个文件。文件名为java.sql.Driver(这是 JDBC 驱动程序的公认约定)。它包含 JDBC 驱动程序的完全限定类名，在本例中为org.postgresql.Driver。

## 5.总结

在本文中，我们回顾了 JDBC 的基本概念，以及加载 JDBC 驱动程序的各种方法，并对每种方法进行了解释。