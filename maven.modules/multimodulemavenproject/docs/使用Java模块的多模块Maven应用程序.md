## 1. 概述

[Java平台模块系统(JPMS)](https://www.baeldung.com/java-9-modularity)为Java应用程序增加了更高的可靠性、更好的关注点分离和更强大的封装。但是，**它不是构建工具，因此它缺乏自动管理项目依赖项的能力**。

当然，我们可能想知道是否可以在模块化应用程序中使用成熟的构建工具，如[Maven](https://www.baeldung.com/maven)或[Gradle](https://www.baeldung.com/gradle)。

其实，我们可以！在本教程中，**我们将学习如何使用Java模块创建多模块Maven应用程序**。

## 2. 在Java模块中封装Maven模块

由于**模块化和依赖管理在Java中并不是相互排斥的概念**，我们可以将[JPMS](https://www.baeldung.com/new-java-9)与Maven等无缝集成，从而利用两全其美的优势。

在一个标准的多模块Maven项目中，我们添加一个或多个子Maven模块，方法是将它们放在项目的根文件夹下，并在父POM的<modules\>部分中声明它们。

反过来，我们编辑每个子模块的POM并通过标准<groupId\>、<artifactId\>和<version\>坐标指定其依赖项。

Maven中的反应器机制负责处理多模块项目，负责以正确的顺序构建整个项目。

在这种情况下，我们将基本上使用相同的设计方法，但有一个微妙但基本的变体：**我们将通过向其添加模块描述符文件module-info.java将每个Maven模块包装到一个Java模块中**。

## 3. 父Maven模块

为了演示模块化和依赖管理如何很好地协同工作，我们将构建一个基本的Demo多模块Maven项目，**其功能将缩小到仅从持久层获取一些域对象**。

为了保持代码简单，我们将使用普通Map作为存储域对象的底层数据结构。当然，我们可以轻松地进一步切换到成熟的关系型数据库。

让我们从定义父Maven模块开始，为此，让我们创建一个名为multimodulemavenproject的根项目目录(但它可以是其他任何名称)，并向其中添加父pom.xml文件：

```xml
<groupId>cn.tuyucheng.taketoday.multimodulemavenproject</groupId>
<artifactId>multimodulemavenproject</artifactId>
<version>1.0.0</version>
<packaging>pom</packaging>
<name>multimodulemavenproject</name>
 
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
 
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

在父POM的定义中有一些细节值得注意。

首先，**由于我们使用的是Java 11，因此我们的系统上至少需要Maven 3.5.0，因为Maven从该版本开始支持Java 9及更高版本**。

而且，我们还需要至少3.8.0版本的[Maven编译器插件](https://www.baeldung.com/maven-compiler-plugin)。因此，让我们确保在[Maven Central](https://search.maven.org/artifact/org.apache.maven.plugins/maven-compiler-plugin)上检查该插件的最新版本。

## 4. 子Maven模块

请注意，到目前为止，**父POM没有声明任何子模块**。

由于我们的Demo项目将从持久层获取一些域对象，因此我们将创建四个子Maven模块：

1.  entitymodule：将包含一个简单的域类
2.  daomodule：将持有访问持久层所需的接口(一个基本的[DAO](https://www.baeldung.com/java-dao-pattern)合约)
3.  userdaomodule：将包含daomodule接口的实现
4.  mainappmodule：项目的入口点

### 4.1 entitymodule Maven模块

现在，让我们添加第一个子Maven模块，它只包含一个基本的域类。

在项目根目录下，我们创建entitymodule/src/main/java/cn/tuyucheng/taketoday/entity目录结构，并添加一个User类：

```java
public class User {

    private final String name;

    // standard constructor / getter / toString
}
```

接下来，让我们包含模块的pom.xml文件：

```xml
<parent>
    <groupId>cn.tuyucheng.taketoday.multimodulemavenproject</groupId>
    <artifactId>multimodulemavenproject</artifactId>
    <version>1.0</version>
</parent>
 
<groupId>cn.tuyucheng.taketoday.entitymodule</groupId>
<artifactId>entitymodule</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
<name>entitymodule</name>
```

正如我们所见，实体模块与其他模块没有任何依赖关系，也不需要额外的Maven工件，因为它只包含User类。

现在，我们需要**将Maven模块封装成Java模块**。为此，我们只需将以下模块描述符文件(module-info.java )放在entitymodule/src/main/java目录下：

```java
module cn.tuyucheng.taketoday.entitymodule {
    exports cn.tuyucheng.taketoday.entitymodule;
}
```

最后，让我们将子Maven模块添加到父POM中：

```xml
<modules>
    <module>entitymodule</module>
</modules>
```

### 4.2 daomodule Maven模块

让我们创建一个新的Maven模块，它将包含一个简单的接口，这对于定义从持久层获取泛型类型的抽象契约很方便。

事实上，有一个非常有说服力的理由将这个接口放在一个单独的Java模块中，**通过这样做，我们有一个抽象的、高度解耦的契约，它很容易在不同的上下文中重用**。从本质上讲，这是[依赖倒置原则](https://www.baeldung.com/java-dependency-inversion-principle)的另一种实现，它产生了更灵活的设计。

因此，让我们在项目地根目录下创建daomodule/src/main/java/cn/tuyucheng/taketoday/dao目录结构，并在其中添加Dao<T>接口：

```java
public interface Dao<T> {

    Optional<T> findById(int id);

    List<T> findAll();
}
```

现在，让我们定义模块的pom.xml文件：

```xml
<parent>
    // parent coordinates
</parent>
 
<groupId>cn.tuyucheng.taketoday.daomodule</groupId>
<artifactId>daomodule</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
<name>daomodule</name>
```

新模块也不需要其他模块或工件，因此我们只需将其包装到Java模块中即可，让我们在daomodule/src/main/java目录下创建模块描述符：

```java
module cn.tuyucheng.taketoday.daomodule {
    exports cn.tuyucheng.taketoday.daomodule;
}
```

最后，让我们将模块添加到父POM中：

```xml
<modules>
    <module>entitymodule</module>
    <module>daomodule</module>
</modules>
```

### 4.3 userdaomodule Maven模块

接下来，让我们定义包含Dao接口实现的Maven模块。

在项目的根目录下，我们创建userdaomodule/src/main/java/cn/tuyucheng/taketoday/userdao目录结构，并添加如下UserDao类：

```java
public class UserDao implements Dao<User> {

    private final Map<Integer, User> users;

    // standard constructor

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
```

**简单地说，UserDao类提供了一个基本的API，允许我们从持久层获取User对象**。

为了简单起见，我们使用Map作为支持数据结构来持久化域对象。当然，可以提供更彻底的实现，例如使用[Hibernate的实体管理器](https://www.baeldung.com/hibernate-entitymanager)。

现在，让我们定义Maven模块的POM：

```xml
<parent>
    // parent coordinates
</parent>
 
<groupId>cn.tuyucheng.taketoday.userdaomodule</groupId>
<artifactId>userdaomodule</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
<name>userdaomodule</name>
 
<dependencies>
    <dependency>
        <groupId>cn.tuyucheng.taketoday.entitymodule</groupId>
        <artifactId>entitymodule</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>cn.tuyucheng.taketoday.daomodule</groupId>
        <artifactId>daomodule</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

在这种情况下，情况略有不同，因为userdaomodule模块需要entitymodule和daomodule模块，这就是我们将它们作为依赖项添加到pom.xml文件中的原因。

我们还需要将这个Maven模块封装成一个Java模块，因此，让我们在userdaomodule/src/main/java目录下添加以下模块描述符：

```java
module cn.tuyucheng.taketoday.userdaomodule {
    requires cn.tuyucheng.taketoday.entitymodule;
    requires cn.tuyucheng.taketoday.daomodule;
    provides cn.tuyucheng.taketoday.daomodule.Dao with cn.tuyucheng.taketoday.userdaomodule.UserDao;
    exports cn.tuyucheng.taketoday.userdaomodule;
}
```

最后，我们需要将这个新模块添加到父POM中：

```xml
<modules>
    <module>entitymodule</module>
    <module>daomodule</module>
    <module>userdaomodule</module>
</modules>
```

从高层次的角度来看，很容易看出**pom.xml文件和模块描述符扮演着不同的角色**。尽管如此，它们还是可以很好地互补。

假设我们需要更新entitymodule和daomodule Maven工件的版本，我们可以很容易地做到这一点，而不必更改模块描述符中的依赖关系，Maven会负责为我们包含正确的工件。

同样，我们可以通过修改模块描述符中的“provides..with”指令来更改模块提供的服务实现。

**当我们一起使用Maven和Java模块时，我们会收获很多。前者带来了自动、集中依赖管理的功能，而后者则提供了模块化的内在优势**。

### 4.4 mainappmodule Maven模块

此外，我们需要定义包含项目主类的Maven模块。

正如我们之前所做的，我们在根目录下创建mainappmodule/src/main/java/mainapp目录结构，并在其中添加如下Application类：

```java
public class Application {

    public static void main(String[] args) {
        Map<Integer, User> users = new HashMap<>();
        users.put(1, new User("Julie"));
        users.put(2, new User("David"));
        Dao userDao = new UserDao(users);
        userDao.findAll().forEach(System.out::println);
    }
}
```

Application类的main()方法非常简单。首先，它用几个User对象填充一个HashMap；接下来，它使用UserDao实例从Map中获取它们，然后将它们显示到控制台。

另外，我们还需要定义模块的pom.xml文件：

```xml
<parent>
    // parent coordinates
</parent>
 
<groupId>cn.tuyucheng.taketoday.mainappmodule</groupId>
<artifactId>mainappmodule</artifactId>
<version>1.0.0</version>
<packaging>jar</packaging>
<name>mainappmodule</name>
 
<dependencies>
    <dependency>
        <groupId>cn.tuyucheng.taketoday.entitymodule</groupId>
         <artifactId>entitymodule</artifactId>
         <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>cn.tuyucheng.taketoday.daomodule</groupId>
        <artifactId>daomodule</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>cn.tuyucheng.taketoday.userdaomodule</groupId>
        <artifactId>userdaomodule</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

该模块的依赖关系是不言自明的，所以，我们只需要将模块放在Java模块中即可。因此，在mainappmodule/src/main/java目录结构下，让我们包含模块描述符：

```java
module cn.tuyucheng.taketoday.mainappmodule {
    requires cn.tuyucheng.taketoday.entitypmodule;
    requires cn.tuyucheng.taketoday.userdaopmodule;
    requires cn.tuyucheng.taketoday.daopmodule;
    uses cn.tuyucheng.taketoday.daopmodule.Dao;
}
```

最后，让我们将这个模块添加到父POM中：

```xml
<modules>
    <module>entitymodule</module>
    <module>daomodule</module>
    <module>userdaomodule</module>
    <module>mainappmodule</module>
</modules>
```

所有的子Maven模块都已经就位，并整齐地封装在Java模块中，项目的结构如下所示：

```powershell
multimodulemavenproject (the root directory)
pom.xml
|-- entitymodule
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn
                |-- tuyucheng
                    |-- taketoday
                        |-- entity
                            |-- User.java
    pom.xml
|-- daomodule
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn
                |-- tuyucheng
                    |-- taketoday
                        |-- dao
                            |-- Dao.java
    pom.xml
|-- userdaomodule
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn
                |-- tuyucheng
                    |-- taketoday
                        |-- userdao
                            |-- UserDao.java
    pom.xml
|-- mainappmodule
    |-- src
        |-- main
            | -- java
            module-info.java
            |-- cn
                |-- tuyucheng
                    |-- taketoday
                        |-- mainapp
                            |-- Application.java
    pom.xml
```

## 5. 运行应用程序

最后，让我们从IDE或控制台运行应用程序。

正如我们所料，当应用程序启动时，我们应该会看到一些User对象打印到控制台：

```bash
User{name=Julie}
User{name=David}
```

## 6. 总结

在本教程中，我们以实用的方式学习了如何在使用Java模块的基本多模块Maven项目的开发中让Maven和JPMS并行工作。