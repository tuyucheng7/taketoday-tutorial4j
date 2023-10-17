## 1. 概述

))本教程提供了有关如何使用Gradle构建基于Java的项目的实用指南))。

我们将解释手动创建项目结构、执行初始配置以及添加Java插件和JUnit依赖项的步骤。然后，我们将构建并运行应用程序。

最后，在最后一节中，我们将举例说明如何使用Gradle Build Init插件执行此操作。一些基本的介绍也可以在[Gradle简介](https://www.baeldung.com/gradle)一文中找到。

## 2. Java项目结构

))在我们手动创建Java项目并准备构建之前，我们需要[安装Gradle](https://docs.gradle.org/current/userguide/installation.html)))。

然后我们使用powershell命令行创建一个项目文件夹“gradle-employee-app”：

```powershell
> mkdir gradle-employee-app
```

然后，我们进入到项目文件夹并创建子文件夹：

```powershell
> mkdir src/main/java/employee
```

结果输出如下所示：

```powershell
目录: D:workspaceintellijfullstack-roadmapsgradle.modulesgradlegradle-employee-appsrcmainjava

Mode                 LastWriteTime         Length Name
----                 -------------         ------ ----
d-----         2022/11/4     16:43                employee
```

在上面的项目结构中，让我们创建两个类。一个是简单的Employee类，其中包含name、emailAddress和yearOfBirth字段：

```java
public class Employee {
    String name;
    String emailAddress;
    int yearOfBirth;
}
```

第二个是打印Employee数据的主程序类：

```java
public class EmployeeApp {

    public static void main(String[] args) {
        Employee employee = new Employee();

        employee.name = "tuyucheng";
        employee.emailAddress = "tuyucheng@gmail.com";
        employee.yearOfBirth = 2000;

        System.out.println("Name: " + employee.name);
        System.out.println("Email Address: " + employee.emailAddress);
        System.out.println("Year Of Birth: " + employee.yearOfBirth);
    }
}
```

## 3. 构建Java项目

))接下来，为了构建我们的Java项目，我们在项目根文件夹中创建一个build.gradle配置文件))。

以下是PowerShell命令行中的内容：

```powershell
Echo > build.gradle
```

我们跳过与输入参数相关的下一步：

```powershell
cmdlet Write-Output at command pipeline position 1
Supply values for the following parameters:
InputObject[0]:
```

为了构建成功，我们需要添加[application插件](https://docs.gradle.org/current/userguide/application_plugin.html)：

```groovy
plugins {
    id 'application'
}
```

然后，我们应用application插件并))添加主类的全限定名称))：

```groovy
apply plugin: 'application'
mainClassName = 'employee.EmployeeApp'
```

每个项目都由任务组成。任务代表构建执行的一项工作，例如编译源代码。

例如，我们可以在配置文件中添加一个任务，打印关于已完成项目配置的消息：

```groovy
println 'This is executed during configuration phase'
task configured {
    println 'The project is configured'
}
```

))通常，gradle build是主要任务，也是最常用的任务。此任务编译、测试并将代码组装成JAR文件))。通过输入以下内容开始构建：

```powershell
> gradle build
```

执行上面的命令输出：

```powershell
> Configure project :
This is executed during configuration phase
The project is configured
BUILD SUCCESSFUL in 1s
2 actionable tasks: 2 up-to-date
```

))要查看构建结果，让我们查看包含子文件夹的build文件夹：classes、distributions、libs和reports))。输入Tree / F可以列出build文件夹的结构：

```powershell
├───build
│   ├───classes
│   │   └───java
│   │       ├───main
│   │       │   └───employee
│   │       │           Employee.class
│   │       │           EmployeeApp.class
│   │       │
│   │       └───test
│   │           └───employee
│   │                   EmployeeAppTest.class
│   │
│   ├───distributions
│   │       gradle-employee-app.tar
│   │       gradle-employee-app.zip
       
│   ├───libs
│   │       gradle-employee-app.jar
│   │
│   ├───reports
│   │   └───tests
│   │       └───test
│   │           │   index.html
│   │           │
│   │           ├───classes
│   │           │       employee.EmployeeAppTest.html
```

如你所见，classes子文件夹包含我们之前创建的两个已编译的.class文件，distributions子文件夹包含应用程序jar包的存档版本。而libs保存了我们应用程序的jar文件。

通常，在报告中，运行JUnit测试时会生成一些文件。

现在，我们可以通过输入gradle run来运行Java项目。退出时执行应用程序的结果：

```powershell
> Configure project :
This is executed during configuration phase
The project is configured

> Task :run
Name: tuyucheng
Email Address: tuyucheng@gmail.com
Year Of Birth: 2000

BUILD SUCCESSFUL in 867ms
2 actionable tasks: 2 executed
```

### 3.1 使用Gradle Wrapper构建

))Gradle Wrapper是一个调用已声明版本的Gradle的脚本))。

首先，我们在build.gradle文件中定义一个wrapper任务：

```groovy
task wrapper(type: Wrapper) {
    gradleVersion = '7.4'
}
```

然后在PowerShell中的使用gradle wrapper运行此任务：

```powershell
> Configure project :
This is executed during configuration phase
The project is configured

BUILD SUCCESSFUL in 1s
1 actionable task: 1 executed
```

执行任务后，项目文件夹下会创建几个文件，包括gradle/wrapper位置下的文件：

```powershell
│   gradlew
│   gradlew.bat
│   
├───gradle
│   └───wrapper
│           gradle-wrapper.jar
│           gradle-wrapper.properties
```

- gradlew：用于在Linux上创建Gradle任务的shell脚本
- gradlew.bat：Windows用户创建Gradle任务的.bat脚本
- gradle-wrapper.jar：我们应用程序的包装器可执行jar包
- gradle-wrapper.properties：用于配置包装器的属性文件

## 4. 添加Java依赖并运行一个简单的测试

首先，在我们的配置文件中，我们需要设置一个远程仓库，从中下载依赖jar。大多数情况下，这些仓库是mavenCentral()：

```groovy
repositories {
    mavenCentral()
}
```

配置仓库后，我们可以指定要下载的依赖项。在此示例中，我们添加了Apache Commons和JUnit库。))要注意，请在依赖项配置中添加testImplementation和implementation部分))。

它建立在一个额外的test块上：

```groovy
dependencies {
    implementation group: 'org.apache.commons', name: 'commons-lang3', version: '3.12.0'
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testImplementation 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}

test {
    useJUnitPlatform()
}
```

然后，我们编写一个简单的JUnit测试类。导航到src文件夹并为测试创建子文件夹：

```powershell
src> mkdir test/java/employee
```

在最后一个子文件夹中，我们创建EmployeeAppTest.java：

```java
class EmployeeAppTest {

    @Test
    void testData() {
        Employee testEmp = this.getEmployeeTest();
        assertEquals(testEmp.name, "tuyucheng");
        assertEquals(testEmp.emailAddress, "tuyucheng@gmail.com");
        assertEquals(testEmp.yearOfBirth, 2000);
    }

    private Employee getEmployeeTest() {
        Employee employee = new Employee();
        employee.name = "tuyucheng";
        employee.emailAddress = "tuyucheng@gmail.com";
        employee.yearOfBirth = 2000;
        
        return employee;
    }
}
```

与之前类似，我们从命令行运行gradle clean test，测试应该没有问题地通过。

## 5. 使用Gradle初始化Java项目

在本节中，我们解释创建和构建到目前为止我们已经完成的Java应用程序的步骤。不同的是，这一次我们使用Gradle Build Init插件来完成。

创建一个新的项目文件夹并将其命名为gradle-java-example。然后，切换到那个空的项目文件夹并运行init脚本：

```powershell
> gradle init
```

Gradle会问我们几个问题，并提供创建项目的选项。第一个问题是我们要生成什么类型的项目：

```powershell
Select type of project to generate:
  1: basic
  2: cpp-application
  3: cpp-library
  4: groovy-application
  5: groovy-library
  6: java-application
  7: java-library
  8: kotlin-application
  9: kotlin-library
  10: scala-library
Select build script DSL:
  1: groovy
  2: kotlin
Enter selection [1..10] 6
```

))为项目类型选择选项6，然后为构建脚本选择第一个选项(groovy)))。

接下来，出现一个问题列表：

```powershell
Select test framework:
  1: junit
  2: testng
  3: spock
Enter selection (default: junit) [1..3] 1

Project name (default: gradle-java-example):
Source package (default: gradle.java.example): employee

BUILD SUCCESSFUL in 57m 45s
2 actionable tasks: 2 executed
```

在这里，我们为测试框架选择第一个选项junit。为我们的项目选择默认名称并输入“employee”作为源包的名称。

要查看/src项目文件夹中的完整目录结构，我们在Power Shell中输入Tree /F ：

```powershell
├───main
│   ├───java
│   │   └───employee
│   │           App.java
│   │
│   └───resources
└───test
    ├───java
    │   └───employee
    │           AppTest.java
    │
    └───resources
```

最后，如果我们使用gradle run构建项目，我们会在退出时看到“Hello World”输出：

```powershell
> Task :run
Hello world.

BUILD SUCCESSFUL in 1s
2 actionable tasks: 1 executed, 1 up-to-date
```

## 6. 总结

在本文中，我们介绍了两种使用Gradle创建和构建Java应用程序的方法。首先我们手动完成了这些，从命令行开始编译和构建应用程序。在这种情况下，如果应用程序使用多个库，我们应该注意一些需要的包和类的导入。

另一方面，))Gradle init脚本具有生成项目框架的功能，以及与Gradle相关的一些配置文件))。