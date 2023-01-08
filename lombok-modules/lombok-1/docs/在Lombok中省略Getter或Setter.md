## 1. 概述

有时，我们想隐藏在对象中获取或设置字段值的能力。但是 Lombok 会自动生成默认的 getter/setter。在本快速教程中，我们将展示如何省略由 Lombok 生成的 getter 和 setter。[Project Lombok简介](https://www.baeldung.com/intro-to-project-lombok)中也提供了对 Project Lombok 库的详细了解。

在继续之前，我们应该[在我们的 IDE 中安装 Lombok 插件](https://www.baeldung.com/lombok-ide)。

## 2.依赖关系

首先，我们需要将[Lombok](https://search.maven.org/search?q=g:org.projectlombok AND a:lombok)添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.22</version>
    <scope>provided</scope>
</dependency>
```

## 3. 默认行为

在我们详细了解如何省略 getter 和 setter 的生成之前，让我们回顾一下负责生成它们的注解的默认行为。

### 3.1. @Getter和@Setter注解_

Lombok 提供了两个访问器注解，@Getter和@Setter。我们可以注解每个字段或简单地用它们标记整个类。生成的方法默认是公开的。但是，我们可以将访问级别更改为protected、 package 或private。让我们看一个例子：

```java
@Setter
@Getter
public class User {
    private long id;
    private String login;
    private int age;
}
```

我们可以在我们的 IDE 中使用插件中的delombok选项，并查看 Lombok 生成的代码：

```java
public class User {
    private long id;
    private String login;
    private int age;

    public long getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public int getAge() {
        return this.age;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
```

正如我们所观察到的，所有的 getter 和 setter 都被创建了。所有字段的 getter 和 setter 都是公共的，因为我们没有明确指定任何字段的访问级别。

### 3.2. @数据 注解

@Data结合了其他一些注解的特性，包括@Getter和@Setter。因此，在这种情况下，默认访问器方法也将生成为public：

```java
@Data
public class Employee {
    private String name;
    private String workplace;
    private int workLength;
}
```

## 4. 使用AccessLevel.NONE 省略 Getter 或 Setter

要在特定字段上禁用默认的 getter/setter 生成，我们应该使用特定的访问级别：

```java
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
```

这个访问级别让我们可以覆盖类上的@Getter、@Setter或@Data注解的行为。要覆盖访问级别，请使用显式@Setter或@Getter注解对字段或类进行注解。

### 4.1. 覆盖@Getter和@Setter注解

让我们在age字段的 getter 和id字段的 setter上将AccessLevel更改为NONE：

```java
@Getter
@Setter
public class User {
    @Setter(AccessLevel.NONE)
    private  long id;
    
    private String login;
    
    @Getter(AccessLevel.NONE)
    private int age;
}
```

让我们delombok这段代码：

```java
public class User {
    private  long id;

    private String login;

    private int age;

    public long getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
```

正如我们所见，id 字段没有 setter， age字段没有go getter 。

### 4.2. 覆盖@Data注解

让我们看另一个示例，其中我们使用@Data注解将类上的AccessLevel更改为NONE：

```java
@Data
public class Employee {

    @Setter(AccessLevel.NONE)
    private String name;

    private String workplace;
    
    @Getter(AccessLevel.NONE)
    private int workLength;
}
```

我们向 workLength 字段添加了显式@Getter注解，向name字段添加了显式@Setter注解。两个访问器的AccessLevel都设置为NONE。让我们看看delombok代码：

```java
public class Employee {

    private String name;

    private String workplace;

    private int workLength;
    
    public String getName() {
        return this.name;
    }

    public String getWorkplace() {
        return this.workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public void setWorkLength(int workLength) {
        this.workLength = workLength;
    }
}
```

正如我们所料，我们对@Getter和@Setter的显式设置覆盖了@Data注解生成的getter 和setter。没有为name字段生成 setter，也没有为workLength字段生成 getter。

## 5.总结

在本文中，我们探讨了如何通过 Lombok 为对象中的特定字段省略 getter 和 setter 生成。此外，我们还看到了@Getter、@Setter和@Data注解的示例。接下来，我们看到了 Lombok 为我们的注解设置生成的代码。