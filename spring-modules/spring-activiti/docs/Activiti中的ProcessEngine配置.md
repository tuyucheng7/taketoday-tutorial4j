## 1. 概述

在我们之前的[Activiti with Java](https://www.baeldung.com/java-activiti)介绍文章中，我们看到了ProcessEngine的重要性，并通过框架提供的默认静态 API 创建了一个。

除了默认设置之外，还有其他创建ProcessEngine的方法——我们将在此处探讨。

## 2、获取ProcessEngine实例

有两种获取ProcessEngine实例的方法：

1.  使用ProcessEngines类
2.  以编程方式，通过ProcessEngineConfiguration

让我们仔细看看这两种方法的示例。

## 3.使用ProcessEngines类获取ProcessEngine

通常，ProcessEngine使用名为activiti.cfg.xml 的 XML 文件进行配置，默认创建过程也将使用该文件。

这是此配置的外观的快速示例：

```xml
<beans xmlns="...">
    <bean id="processEngineConfiguration" class=
      "org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
        <property name="jdbcUrl"
          vasentence you have mentioned and also changed thelue="jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000" />
        <property name="jdbcDriver" value="org.h2.Driver" />
        <property name="jdbcUsername" value="root" />
        <property name="jdbcPassword" value="" />
        <property name="databaseSchemaUpdate" value="true" />
    </bean>
</beans>
```

请注意引擎的持久性方面是如何在此处配置的。

现在，我们可以获得ProcessEngine：

```java
ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
```

## 4.使用ProcessEngineConfiguration获取ProcessEngine

超越获取引擎的默认路径——有两种创建ProcessEngineConfiguration的方法：

1.  使用 XML 配置
2.  使用Java配置

让我们从 XML 配置开始。

如第 2.1 节所述。– 我们可以以编程方式定义ProcessEngineConfiguration ，并使用该实例构建ProcessEngine ：

```java
@Test 
public void givenXMLConfig_whenCreateDefaultConfiguration_thenGotProcessEngine() {
    ProcessEngineConfiguration processEngineConfiguration 
      = ProcessEngineConfiguration
        .createProcessEngineConfigurationFromResourceDefault();
    ProcessEngine processEngine 
      = processEngineConfiguration.buildProcessEngine();
    
    assertNotNull(processEngine);
    assertEquals("root", processEngine.getProcessEngineConfiguration()
      .getJdbcUsername());
}
```

方法createProcessEngineConfigurationFromResourceDefault()也会查找activiti.cfg.xml文件，现在我们只需要调用buildProcessEngine() API。

在这种情况下，它查找的默认 bean 名称是processEngineConfiguration。如果我们想要更改配置文件名或 bean 名称，我们可以使用其他可用的方法来创建ProcessEngineConfiguration。

让我们看几个例子。

首先，我们将更改配置文件名并要求 API 使用我们的自定义文件：

```java
@Test 
public void givenDifferentNameXMLConfig_whenGetProcessEngineConfig_thenGotResult() {
    ProcessEngineConfiguration processEngineConfiguration 
      = ProcessEngineConfiguration
        .createProcessEngineConfigurationFromResource(
          "my.activiti.cfg.xml");
    ProcessEngine processEngine = processEngineConfiguration
      .buildProcessEngine();
    
    assertNotNull(processEngine);
    assertEquals("baeldung", processEngine.getProcessEngineConfiguration()
      .getJdbcUsername());
}
```

现在，让我们也更改 bean 名称：

```java
@Test 
public void givenDifferentBeanNameInXMLConfig_whenGetProcessEngineConfig_thenGotResult() {
    ProcessEngineConfiguration processEngineConfiguration 
      = ProcessEngineConfiguration
        .createProcessEngineConfigurationFromResource(
          "my.activiti.cfg.xml", 
          "myProcessEngineConfiguration");
    ProcessEngine processEngine = processEngineConfiguration
      .buildProcessEngine();
    
    assertNotNull(processEngine);
    assertEquals("baeldung", processEngine.getProcessEngineConfiguration()
      .getJdbcUsername());
}
```

当然，既然配置需要不同的名称，我们需要更改文件名(和 bean 名称)以匹配 - 在运行测试之前。

创建引擎的其他可用选项是createProcessEngineConfigurationFromInputStream(InputStream inputStream),
createProcessEngineConfigurationFromInputStream(InputStream inputStream, String beanName)。

如果我们不想使用 XML 配置，我们也可以仅使用Java配置进行设置。

我们将与四个不同的班级一起工作；每一个都代表一个不同的环境：

1.  org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration – ProcessEngine以独立方式使用，由数据库支持
2.  org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration –默认情况下，使用 H2 内存数据库。数据库在引擎启动和关闭时创建和删除——因此，这种配置样式可用于测试
3.  org.activiti.spring.SpringProcessEngineConfiguration –在 Spring 环境中使用
4.  org.activiti.engine.impl.cfg.JtaProcessEngineConfiguration –引擎以独立模式运行，带有 JTA 事务

让我们看几个例子。

这是一个用于创建独立流程引擎配置的 JUnit 测试：

```java
@Test 
public void givenNoXMLConfig_whenCreateProcessEngineConfig_thenCreated() {
    ProcessEngineConfiguration processEngineConfiguration 
      = ProcessEngineConfiguration
        .createStandaloneProcessEngineConfiguration();
    ProcessEngine processEngine = processEngineConfiguration
      .setDatabaseSchemaUpdate(ProcessEngineConfiguration
        .DB_SCHEMA_UPDATE_TRUE)
      .setJdbcUrl("jdbc:h2:mem:my-own-db;DB_CLOSE_DELAY=1000")
      .buildProcessEngine();
    
    assertNotNull(processEngine);
    assertEquals("sa", processEngine.getProcessEngineConfiguration()
      .getJdbcUsername());
}
```

同样，我们将编写一个 JUnit 测试用例，以使用内存数据库创建独立的流程引擎配置：

```java
@Test 
public void givenNoXMLConfig_whenCreateInMemProcessEngineConfig_thenCreated() {
    ProcessEngineConfiguration processEngineConfiguration 
      = ProcessEngineConfiguration
      .createStandaloneInMemProcessEngineConfiguration();
    ProcessEngine processEngine = processEngineConfiguration
      .buildProcessEngine();
    
    assertNotNull(processEngine);
    assertEquals("sa", processEngine.getProcessEngineConfiguration()
      .getJdbcUsername());
}
```

## 5.数据库设置

默认情况下，Activiti API 将使用 H2 内存数据库，数据库名称为“activiti”，用户名为“sa”。

如果我们需要使用任何其他数据库，我们必须明确地进行设置——使用两个主要属性。

databaseType – 有效值为h2、mysql、oracle、postgres、mssql、db2。这也可以从数据库配置中找出，但如果自动检测失败，这将很有用。

databaseSchemaUpdate——这个属性允许我们定义当引擎启动或关闭时数据库会发生什么。它可以具有以下三个值：

1.  false(默认)——此选项根据库验证数据库模式的版本。如果它们不匹配，引擎将抛出异常
2.  true – 构建流程引擎配置时，将对数据库执行检查。数据库将相应地创建/更新 create-drop
3.  “ - ” - 这将在创建流程引擎时创建数据库模式，并在流程引擎关闭时删除它。

我们可以将数据库配置定义为 JDBC 属性：

```xml
<property name="jdbcUrl" value="jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000" />
<property name="jdbcDriver" value="org.h2.Driver" />
<property name="jdbcUsername" value="sa" />
<property name="jdbcPassword" value="" />
<property name="databaseType" value="mysql" />

```

或者，如果我们使用DataSource：

```xml
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" >
    <property name="driverClassName" value="com.mysql.jdbc.Driver" />
    <property name="url" value="jdbc:mysql://localhost:3306/activiti" />
    <property name="username" value="activiti" />
    <property name="password" value="activiti" />
    <property name="defaultAutoCommit" value="false" />
    <property name="databaseType" value="mysql" />
</bean>

```

## 六. 总结

在这个快速教程中，我们重点介绍了在 Activiti中创建ProcessEngine的几种不同方法。

我们还看到了处理数据库配置的不同属性和方法。