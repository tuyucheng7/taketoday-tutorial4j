## 1. 概述

[Reladomo(以前称为 Mithra)](https://goldmansachs.github.io/reladomo/)是Java的对象关系映射 (ORM) 框架，由Goldman Sachs，目前作为开源项目发布。该框架提供了 ORM 通常需要的功能以及一些额外的功能。

让我们看看Reladomo的一些主要功能：

-   它可以生成Java类以及 DDL 脚本
-   它由写入 XML 文件的元数据驱动
-   生成的代码是可扩展的
-   查询语言是面向对象和强类型的
-   该框架提供了对分片的支持(相同的模式，不同的数据集)
-   还包括对测试的支持
-   它提供有用的功能，如高性能缓存和事务

在接下来的部分中，我们将看到设置和一些基本的使用示例。

## 2.Maven设置_

要开始使用 ORM，我们需要将[reladomo](https://search.maven.org/classic/#search|ga|1|a%3A"reladomo") 依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.goldmansachs.reladomo</groupId>
    <artifactId>reladomo</artifactId>
    <version>16.5.1</version>
</dependency>
```

我们将在示例中使用H2数据库，因此我们还要添加[h2](https://search.maven.org/classic/#search|ga|1|a%3A"h2" AND g%3A"com.h2database")依赖项：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
</dependency>
```

除此之外，我们需要设置将生成类和 SQL 文件的插件，并在执行期间加载它们。

对于文件生成，我们可以使用使用maven-antrun-plugin执行的任务。首先，让我们看看如何定义生成Java类的任务：

```xml
<plugin>
    <artifactId>maven-antrun-plugin</artifactId>
    <executions>
        <execution>
            <id>generateMithra</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>run</goal>
            </goals>
            <configuration>
                <tasks>
                    <property name="plugin_classpath" 
                      refid="maven.plugin.classpath"/>
                    <taskdef name="gen-reladomo" 
                      classpath="plugin_classpath"
                      classname="com.gs.fw.common.mithra.generator.MithraGenerator"/>
                    <gen-reladomo 
                      xml="${project.basedir}/src/main/resources/reladomo/ReladomoClassList.xml"
                      generateGscListMethod="true"
                      generatedDir="${project.build.directory}/generated-sources/reladomo"
                      nonGeneratedDir="${project.basedir}/src/main/java"/>
                </tasks>
            </configuration>
        </execution>
    </executions>
</plugin>    

```

gen-reladomo任务使用提供的MithraGenerator根据ReladomoClassList.xml文件中的配置创建 Java文件。我们将在后面的部分中仔细查看此文件包含的内容。

这些任务还有两个属性，用于定义生成文件的位置：

-   generatedDir – 包含不应修改或版本控制的类
-   nonGeneratedDir——生成的具体对象类，可以进一步定制和版本化

与Java对象对应的数据库表可以手动创建，也可以使用第二个Ant任务生成的 DDL 脚本自动创建：

```xml
<taskdef 
  name="gen-ddl"
  classname = "com.gs.fw.common.mithra.generator.dbgenerator.MithraDbDefinitionGenerator"
  loaderRef="reladomoGenerator">
    <classpath refid="maven.plugin.classpath"/>
</taskdef>
<gen-ddl 
  xml="${project.basedir}/src/main/resources/reladomo/ReladomoClassList.xml"
  generatedDir="${project.build.directory}/generated-db/sql"
  databaseType="postgres"/>
```

此任务使用基于前面提到的相同ReladomoClassList.xml文件的MithraDbDefinitionGenerator 。SQL 脚本将放在generated-db/sql目录中。

为了完成这个插件的定义，我们还必须添加两个用于创建的依赖项：

```xml
<plugin>
    <artifactId>maven-antrun-plugin</artifactId>
    <executions>
    //...               
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.goldmansachs.reladomo</groupId>
            <artifactId>reladomogen</artifactId>
            <version>16.5.1</version>
        </dependency>
        <dependency>
            <groupId>com.goldmansachs.reladomo</groupId>
            <artifactId>reladomo-gen-util</artifactId>
            <version>16.5.1</version>
        </dependency>
    </dependencies>
</plugin>
```

最后，使用build-helper-maven-plugin，我们可以将生成的文件添加到类路径中：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>add-source</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>${project.build.directory}/generated-sources/reladomo</source>
                </sources>
            </configuration>
        </execution>
        <execution>
            <id>add-resource</id>
            <phase>generate-resources</phase>
            <goals>
                <goal>add-resource</goal>
            </goals>
            <configuration>
                <resources>
                    <resource>
                        <directory>${project.build.directory}/generated-db/</directory>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

添加 DDL 脚本是可选的。在我们的示例中，我们将使用内存数据库，因此我们要执行脚本以创建表。

## 3.XML配置

Reladomo框架的元数据可以在几个 XML 文件中定义。

### 3.1. 对象 XML 文件

我们要创建的每个实体都需要在其 XML 文件中定义。

让我们创建一个包含两个实体的简单示例：部门和员工。这是我们领域模型的可视化表示：

[![桌子](https://www.baeldung.com/wp-content/uploads/2017/09/tables-300x119.png)](https://www.baeldung.com/wp-content/uploads/2017/09/tables.png)

让我们定义第一个Department.xml文件：

```xml
<MithraObject objectType="transactional">
    <PackageName>com.baeldung.reladomo</PackageName>
    <ClassName>Department</ClassName>
    <DefaultTable>departments</DefaultTable>

    <Attribute name="id" javaType="long" 
      columnName="department_id" primaryKey="true"/>
    <Attribute name="name" javaType="String" 
      columnName="name" maxLength="50" truncate="true"/>
    <Relationship name="employees" relatedObject="Employee" 
      cardinality="one-to-many" 
      reverseRelationshipName="department" 
      relatedIsDependent="true">
         Employee.departmentId = this.id
    </Relationship>
</MithraObject>
```

我们可以看到上面的实体是在一个名为MithraObject的根元素中定义的。然后，我们指定了对应的数据库表的包、类和名称。

类型的每个属性都使用Attribute元素定义，我们可以为其声明名称、Java 类型和列名。

我们可以使用Relationship标签来描述对象之间的关系。在我们的示例中，我们基于以下表达式定义了Department和Employee对象之间的一对多关系：

```xml
Employee.departmentId = this.id
```

reverseRelationshipName属性可用于使关系成为双向关系，而无需定义它两次。

relatedIsDependent属性允许我们级联操作。

接下来，让我们以类似的方式创建Employee.xml文件：

```xml
<MithraObject objectType="transactional">
    <PackageName>com.baeldung.reladomo</PackageName>
    <ClassName>Employee</ClassName>
    <DefaultTable>employees</DefaultTable>

    <Attribute name="id" javaType="long" 
      columnName="employee_id" primaryKey="true"/>
    <Attribute name="name" javaType="String" 
      columnName="name" maxLength="50" truncate="true"/>
    <Attribute name="departmentId" javaType="long" 
      columnName="department_id"/>
</MithraObject>
```

### 3.2. ReladomoClassList.xml文件

需要告知Reladomo它应该生成的对象。

在Maven部分，我们将ReladomoClassList.xml文件定义为生成任务的源，因此是时候创建该文件了：

```xml
<Mithra>
    <MithraObjectResource name="Department"/>
    <MithraObjectResource name="Employee"/>
</Mithra>
```

这是一个包含实体列表的简单文件，将根据 XML 配置为其生成类。

## 4.生成类

现在我们拥有了通过使用命令mvn clean install构建Maven应用程序来开始代码生成所需的所有元素。

具体类将在指定包的src/main/java文件夹中生成：

[![类](https://www.baeldung.com/wp-content/uploads/2017/08/classes.png)](https://www.baeldung.com/wp-content/uploads/2017/08/classes.png)

这些是我们可以在其中添加自定义代码的简单类。例如，Department类只包含一个不应删除的构造函数：

```java
public class Department extends DepartmentAbstract {
    public Department() {
        super();
        // You must not modify this constructor. Mithra calls this internally.
        // You can call this constructor. You can also add new constructors.
    }
}
```

如果我们想给这个类添加一个自定义的构造函数，它也需要调用父构造函数：

```java
public Department(long id, String name){
    super();
    this.setId(id);
    this.setName(name);
}
```

这些类基于generated-sources/reladomo文件夹中的抽象类和实用类：

[![基因类](https://www.baeldung.com/wp-content/uploads/2017/08/gen-classes-300x216.png)](https://www.baeldung.com/wp-content/uploads/2017/08/gen-classes.png)

此文件夹中的主要类类型是：

-   DepartmentAbstract和EmployeeAbstract类——包含用于处理定义的实体的方法
-   DepartmentListAbstract和EmployeeListAbstract——包含处理部门和员工列表的方法
-   DepartmentFinder和EmployeeFinder – 这些提供查询实体的方法
-   其他实用类

通过生成这些类，已经为我们创建了对我们的实体执行 CRUD 操作所需的大部分代码。

## 5.Reladomo应用

要对数据库执行操作，我们需要一个连接管理器类，它允许我们获取数据库连接。

### 5.1. 连接管理器

当使用单个数据库时，我们可以实现SourcelessConnectionManager接口：

```java
public class ReladomoConnectionManager implements SourcelessConnectionManager {

    private static ReladomoConnectionManager instance;
    private XAConnectionManager xaConnectionManager;

    public static synchronized ReladomoConnectionManager getInstance() {
        if (instance == null) {
            instance = new ReladomoConnectionManager();
        }
        return instance;
    }

    private ReladomoConnectionManager() {
        this.createConnectionManager();
    }
    //...
}
```

我们的ReladomoConnectionManager类实现了单例模式，并且基于XAConnectionManager，后者是事务连接管理器的实用程序类。

让我们仔细看看createConnectionManager()方法：

```java
private XAConnectionManager createConnectionManager() {
    xaConnectionManager = new XAConnectionManager();
    xaConnectionManager.setDriverClassName("org.h2.Driver");
    xaConnectionManager.setJdbcConnectionString("jdbc:h2:mem:myDb");
    xaConnectionManager.setJdbcUser("sa");
    xaConnectionManager.setJdbcPassword("");
    xaConnectionManager.setPoolName("My Connection Pool");
    xaConnectionManager.setInitialSize(1);
    xaConnectionManager.setPoolSize(10);
    xaConnectionManager.initialisePool();
    return xaConnectionManager;
}
```

在此方法中，我们设置了创建与H2内存数据库的连接所需的属性。

此外，我们需要从SourcelessConnectionManager接口实现几个方法：

```java
@Override
public Connection getConnection() {
    return xaConnectionManager.getConnection();
}
 
@Override
public DatabaseType getDatabaseType() {
    return H2DatabaseType.getInstance();
}
 
@Override
public TimeZone getDatabaseTimeZone() {
    return TimeZone.getDefault();
}
 
@Override
public String getDatabaseIdentifier() {
    return "myDb";
}
 
@Override 
public BulkLoader createBulkLoader() throws BulkLoaderException { 
    return null; 
}
```

最后，让我们添加一个自定义方法来执行生成的创建数据库表的 DDL 脚本：

```java
public void createTables() throws Exception {
    Path ddlPath = Paths.get(ClassLoader.getSystemResource("sql").toURI());
    try (
      Connection conn = xaConnectionManager.getConnection();
      Stream<Path> list = Files.list(ddlPath)) {
 
        list.forEach(path -> {
            try {
                RunScript.execute(conn, Files.newBufferedReader(path));
            } 
            catch (SQLException | IOException exc){
                exc.printStackTrace();
            }
        });
    }
}
```

当然，对于生产应用程序来说，这不是必需的，因为在生产应用程序中，的表不会在每次执行时都重新创建。

### 5.2. 初始化Reladomo

Reladomo初始化过程使用一个配置文件来指定连接管理器类和使用的对象类型。让我们定义一个ReladomoRuntimeConfig.xml文件：

```xml
<MithraRuntime>
    <ConnectionManager 
      className="com.baeldung.reladomo.ReladomoConnectionManager ">
    <MithraObjectConfiguration 
      className="com.baeldung.reladomo.Department" cacheType="partial"/>
    <MithraObjectConfiguration 
      className="com.baeldung.reladomo.Employee " cacheType="partial"/>
    </ConnectionManager>
</MithraRuntime>
```

接下来，我们可以创建一个主类，我们首先调用createTables()方法，然后使用MithraManager类加载配置并初始化Reladomo：

```java
public class ReladomoApplication {
    public static void main(String[] args) {
        try {
            ReladomoConnectionManager.getInstance().createTables();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        MithraManager mithraManager = MithraManagerProvider.getMithraManager();
        mithraManager.setTransactionTimeout(120);

        try (InputStream is = ReladomoApplication.class.getClassLoader()
          .getResourceAsStream("ReladomoRuntimeConfig.xml")) {
            MithraManagerProvider.getMithraManager()
              .readConfiguration(is);

            //execute operations
        }
        catch (IOException exc){
            exc.printStackTrace();
        }     
    }
}
```

### 5.3. 执行 CRUD 操作

现在让我们使用Reladomo生成的类对我们的实体执行一些操作。

首先，让我们创建两个Department和Employee对象，然后使用cascadeInsert()方法保存它们：

```java
Department department = new Department(1, "IT");
Employee employee = new Employee(1, "John");
department.getEmployees().add(employee);
department.cascadeInsert();
```

每个对象也可以通过调用insert()方法单独保存。在我们的示例中，可以使用cascadeInsert()，因为我们已将relatedIsDependent=true属性添加到我们的关系定义中。

要查询对象，我们可以使用生成的Finder类：

```java
Department depFound = DepartmentFinder
  .findByPrimaryKey(1);
Employee empFound = EmployeeFinder
  .findOne(EmployeeFinder.name().eq("John"));
```

以这种方式获得的对象是“实时”对象，这意味着使用 setter 对它们进行的任何更改都会立即反映在数据库中：

```java
empFound.setName("Steven");
```

为了避免这种行为，我们可以获得分离的对象：

```java
Department depDetached = DepartmentFinder
  .findByPrimaryKey(1).getDetachedCopy();
```

要删除对象，我们可以使用delete()方法：

```java
empFound.delete();
```

### 5.4. 交易管理

如果我们希望一组操作作为一个单元执行或不执行，我们可以将它们包装在一个事务中：

```java
mithraManager.executeTransactionalCommand(tx -> {
    Department dep = new Department(2, "HR");
    Employee emp = new Employee(2, "Jim");
    dep.getEmployees().add(emp);
    dep.cascadeInsert();
    return null;
});
```

## 6.测试支持报告

在上面的部分中，我们在Java主类中编写了示例。

如果我们想为我们的应用程序编写测试，一种方法是简单地在测试类中编写相同的代码。

然而，为了更好的测试支持，Reladomo 还提供了MithraTestResource类。这允许我们仅针对测试使用不同的配置和内存数据库。

首先，我们需要添加额外的[reladomo-test-util](https://search.maven.org/classic/#search|ga|1|a%3A"reladomo-test-util")依赖项，以及[junit](https://search.maven.org/classic/#search|ga|1|a%3A"junit" AND g%3A"junit")依赖项：

```xml
<dependency>
    <groupId>com.goldmansachs.reladomo</groupId>
    <artifactId>reladomo-test-util</artifactId>
    <version>16.5.1</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```

接下来，我们必须创建一个使用ConnectionManagerForTests类的ReladomoTestConfig.xml文件：

```xml
<MithraRuntime>
    <ConnectionManager 
      className="com.gs.fw.common.mithra.test.ConnectionManagerForTests">
        <Property name="resourceName" value="testDb"/>
        <MithraObjectConfiguration 
          className="com.baeldung.reladomo.Department" cacheType="partial"/>
        <MithraObjectConfiguration 
          className="com.baeldung.reladomo.Employee " cacheType="partial"/>
    </ConnectionManager>
 </MithraRuntime>
```

此连接管理器配置一个仅用于测试的内存中H2数据库。

MithraTestResource类的一个方便的特性是我们可以提供具有以下格式的测试数据的文本文件：

```plaintext
class com.baeldung.reladomo.Department
id, name
1, "Marketing"

class com.baeldung.reladomo.Employee
id, name
1, "Paul"
```

让我们创建一个JUnit测试类并在@Before方法中设置我们的MithraTestResource实例：

```java
public class ReladomoTest {
    private MithraTestResource mithraTestResource;

    @Before
    public void setUp() throws Exception {
        this.mithraTestResource 
          = new MithraTestResource("reladomo/ReladomoTestConfig.xml");

        ConnectionManagerForTests connectionManager
          = ConnectionManagerForTests.getInstanceForDbName("testDb");
        this.mithraTestResource.createSingleDatabase(connectionManager);
        mithraTestResource.addTestDataToDatabase("reladomo/test-data.txt", 
          connectionManager);

        this.mithraTestResource.setUp();
    }
}
```

然后我们可以编写一个简单的@Test方法来验证我们的测试数据是否已加载：

```java
@Test
public void whenGetTestData_thenOk() {
    Employee employee = EmployeeFinder.findByPrimaryKey(1);
    assertEquals(employee.getName(), "Paul");
}
```

测试运行后，需要清除测试数据库：

```java
@After
public void tearDown() throws Exception {
    this.mithraTestResource.tearDown();
}
```

## 七. 总结

在本文中，我们介绍了Reladomo ORM 框架的主要特性，以及设置和常用示例。