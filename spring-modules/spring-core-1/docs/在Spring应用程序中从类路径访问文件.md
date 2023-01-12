## 1. 概述

在本教程中，我们将演示使用Spring访问和加载位于类路径中的文件内容的各种方法。

## 2. 使用Resource

Resource接口抽象了对低级资源的访问，事实上，它支持以统一的方式处理各种文件资源。

让我们先看看获取Resource实例的各种方法。

### 2.1 手动获取

为了从classpath(类路径)访问资源，我们可以简单地使用ClassPathResource：

```java
class SpringResourceIntegrationTest {

    private Resource loadEmployeesWithClassPathResource() {
        return new ClassPathResource("data/employees.dat");
    }
}
```

默认情况下，ClassPathResource通过在线程的上下文类加载器和默认系统类加载器之间进行选择来删除一些样板代码。

但是，我们也可以指定类加载器：

```text
return new ClassPathResource("data/employees.dat", this.getClass().getClassLoader());
```

或间接通过指定的类：

```text
return new ClassPathResource("data/employees.dat", Employee.class.getClassLoader());
```

请注意，从Resource中，我们可以很容易地获取到像InputStream或File这样的标准Java API对象。

这里需要注意的另一点是，上述方法仅适用于绝对路径。如果要指定相对路径，可以传递第二个Class参数。路径将相对于该类：

```text
new ClassPathResource("../../../../data/employees.dat", Example.class).getFile();
```

上面的文件路径是相对于Example类的。

### 2.2 使用@Value

我们还可以使用@Value注解注入Resource：

```java
class SpringResourceIntegrationTest {
    @Value("classpath:data/employees.dat")
    private Resource resourceFile;
}
```

@Value还支持其他前缀，如file:和url:。

### 2.3 使用ResourceLoader

如果我们想延迟加载Resource，可以使用ResourceLoader：

```java
class SpringResourceIntegrationTest {
    @Autowired
    private ResourceLoader resourceLoader;
}
```

然后使用getResource()方法获取Resource：

```java
class SpringResourceIntegrationTest {
    private Resource loadEmployeesWithResourceLoader() {
        return resourceLoader.getResource("classpath:data/employees.dat");
    }
}
```

还要注意，所有具体的ApplicationContext都实现了ResourceLoader，这意味着如果它更适合我们的情况，我们也可以简单地依赖ApplicationContext：

```java
class SpringResourceIntegrationTest {
    @Autowired
    private ApplicationContext appContext;

    private Resource loadEmployeesWithApplicationContext() {
        return appContext.getResource("classpath:data/employees.dat");
    }
}
```

### 2.4 使用ResourceUtils

在Spring中还有另一种获取Resource的方法，但ResourceUtils的官方API文档清楚地表明，该类主要用于框架内部使用。

如果在我们的代码中使用ResourceUtils，如下所示：

```java
class SpringResourceIntegrationTest {
    private File loadEmployeesWithSpringInternalClass() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:data/employees.dat");
    }
}
```

我们应该仔细考虑其基本原理，**因为使用上述标准方法之一可能更好**。

## 3. 读取Resource数据

一旦我们有了一个Resource，就可以读取资源的内容。正如我们已经讨论过的，我们可以很容易地从Resource中获取File或InputStream对象。

假设我们在类路径下有如下data/employees.dat文件：

```text
Joe Employee,Jan Employee,James T. Employee
```

### 3.1 作为File读取

现在我们可以通过调用getFile()来读取它的内容：

```java
class SpringResourceIntegrationTest {

    @Test
    void whenResourceAsFile_thenReadSuccessful() throws IOException {
        final File resource = loadEmployeesWithClassPathResource().getFile();
        final String employees = new String(Files.readAllBytes(resource.toPath()));
        assertEquals(EMPLOYEES_EXPECTED, employees);
    }
}
```

不过，应该注意的是，**这种方法需要资源存在文件系统中，而不是jar文件中**。

### 3.2 作为InputStream读取

假设我们的资源在一个jar包里。

我们可以改为将Resource作为InputStream读取：

```java
class SpringResourceIntegrationTest {

    @Test
    void whenResourceAsStream_thenReadSuccessful() throws IOException {
        final InputStream resource = loadEmployeesWithClassPathResource().getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            final String employees = reader.lines().collect(Collectors.joining("\n"));
            assertEquals(EMPLOYEES_EXPECTED, employees);
        }
    }
}
```

## 4. 总结

在这篇简短的文章中，我们研究了几种使用Spring从类路径中访问和读取资源的方法。这包括急切和延迟加载，以及在文件系统或jar包中。