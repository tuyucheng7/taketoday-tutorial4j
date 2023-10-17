## 1. 简介

在本教程中，我们将了解如何在 Gradle 项目中使用多个仓库。当我们需要使用 Maven Central 上不可用的 JAR 文件时，这很有用。我们还将了解如何使用 GitHub 发布 Java 包并在不同项目之间共享它们。

## 2. 在 Gradle 中使用多个仓库

在[使用 Gradle 作为构建工具](https://www.baeldung.com/gradle-building-a-java-app)时，我们经常在build.gradle的仓库部分遇到mavenCentral() 。如果我们想添加其他仓库，我们可以将它们添加到同一部分以指示我们的库的来源：

```scss
repositories {
    mavenLocal()
    mavenCentral()
}
```

在这里，mavenLocal()用于在[Maven 的本地缓存](https://www.baeldung.com/maven-local-repository)中查找所有依赖项。任何未在此缓存中找到的仓库将从 Maven Central 下载。

## 3.使用经过身份验证的仓库

我们还可以通过提供有效的身份验证来使用不公开可用的仓库。例如，GitHub 和其他一些平台提供了一种功能，可以将我们的包发布到它的注册中心，然后在其他项目中使用它。

### 3.1. 将包发布到 GitHub 包注册表

我们会将以下类发布到 GitHub 注册表，稍后在另一个项目中使用它：

```java
public class User {
    private Integer id;
    private String name;
    private Date dob;
    
   // standard constructors, getters and setters
}
```

要发布我们的代码，我们需要来自 GitHub 的个人访问令牌。我们可以按照[GitHub 文档](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)中提供的说明创建一个。然后，我们使用我们的用户名和此令牌将发布任务添加到我们的build.gradle文件中：

```groovy
publishing {
    publications {
        register("jar", MavenPublication) {
            from(components["java"])
            pom {
                url.set("https://github.com/eugenp/tutorials.git")
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = "https://maven.pkg.github.com/eugenp/tutorials"
            credentials {
                username = project.USERNAME
                password = project.GITHUB_TOKEN
            }
        }
    }
}
```

在上面的代码片段中，用户名和密码是在执行 Gradle 的发布任务时提供的项目级变量。

### 3.2. 使用已发布的包作为库

成功发布我们的包后，我们可以将它安装为来自经过身份验证的仓库的库。让我们在build.gradle 中添加以下代码以在新项目中使用已发布的包：

```groovy
repositories {
    // other repositories
    maven {
        name = "GitHubPackages"
        url = "https://maven.pkg.github.com/eugenp/tutorials"
        credentials {
            username = project.USERNAME
            password = project.GITHUB_TOKEN
        }
    }
}
dependencies {
    implementation('com.baeldung.gradle:publish-package:1.0.0-SNAPSHOT')
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    // other dependencies
}
```

这将从 GitHub Package Registry 安装库，并允许我们在我们的项目中扩展类：

```java
public class Student extends User {
    private String studentCode;
    private String lastInstitution;
    // standard constructors, getters and setters
}
```

让我们使用一个简单的测试方法来测试我们的代码：

```java
@Test
public void testPublishedPackage(){
    Student student = new Student();
    student.setId(1);
    student.setStudentCode("CD-875");
    student.setName("John Doe");
    student.setLastInstitution("Institute of Technology");

    assertEquals("John Doe", student.getName());
}
```

## 4。结论

在本文中，我们了解了在处理 Gradle 项目时如何使用来自多个仓库的库。我们还学习了如何将 GitHub Package Registry 用于经过身份验证的仓库。