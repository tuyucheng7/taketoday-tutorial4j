##  一、概述

在本教程中，我们将学习如何将包含 Java 源代码的*String*转换为已编译的类并执行它。在运行时编译代码有许多潜在的应用：

-   生成的代码——来自运行前不可用或经常更改的信息的动态代码
-   热插拔——无需循环我们的应用程序即可替换代码
-   代码存储/注入——将应用程序逻辑存储在数据库中，以便临时检索和执行。小心，自定义类可以在不使用时卸载。

尽管编译类的方法有多种，但今天，我们将重点关注 JavaCompiler API。

## 2. 工具与策略

*javax.tools包包含编译**String*所需的大部分抽象。让我们看看其中的一些，以及我们将遵循的一般流程：

[![Java编译器图](https://www.baeldung.com/wp-content/uploads/2023/01/javacompilerdiagram-1024x560.png)](https://www.baeldung.com/wp-content/uploads/2023/01/javacompilerdiagram.png)

1.  首先，我们将代码传递给 JavaCompiler API。
2.  接下来，*FileManager*提取*JavaCompiler*的源代码。
3.  然后。*JavaCompiler*编译它并返回字节码。
4.  最后，自定义的*ClassLoader*将类加载到内存中。

*我们究竟如何以String* 格式生成源代码是另一个指南的主题。今天，我们将使用一个简单的硬编码文字值：

```java
final static String sourceCode =
  "package com.baeldung.inmemorycompilation;\n" 
    + "public class TestClass {\n" 
    + "@Override\n" 
    + "    public void runCode() {\n" 
    + "        System.out.println(\"code is running...\");\n" 
    + "    }\n" 
    + "}\n";复制
```

## 3. 代表我们的代码（来源和编译）

我们清单上的第一项是以***FileManager\*****熟悉****的格式表示我们的代码**。

Java 源文件和类文件的顶级抽象是*FileObject*。虽然没有提供适合我们需要的完整实现，但我们可以利用部分实现*SimpleJavaFileObject* 并仅覆盖我们关心的方法。

### 3.1. 源代码

对于我们的源代码，**我们必须定义\*FileManager\*应该如何读取它**。这意味着覆盖*getCharContent()*。此方法需要一个*CharSequence*。由于我们的代码已经包含在*String*中，我们可以简单地按原样返回它：

```java
public class JavaSourceFromString extends SimpleJavaFileObject {

    private String sourceCode;

    public JavaSourceFromString(String name, String sourceCode) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
            Kind.SOURCE);
        this.sourceCode = requireNonNull(sourceCode, "sourceCode must not be null");
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }
}复制
```

### 3.2. 编译代码

对于我们的编译代码，我们做完全相反的事情。**我们需要定义\*FileManager\*应该如何写入我们的对象**。这仅仅意味着覆盖*openOutputStream()*并提供[*OutputStream*](https://www.baeldung.com/java-outputstream)的实现。

我们将我们的代码存储在*ByteArrayOutputStream*中，并创建一个方便的方法用于稍后在类加载期间提取字节：

```java
public class JavaClassAsBytes extends SimpleJavaFileObject {

    protected ByteArrayOutputStream bos =
        new ByteArrayOutputStream();

    public JavaClassAsBytes(String name, Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/')
            + kind.extension), kind);
    }

    public byte[] getBytes() {
        return bos.toByteArray();
    }

    @Override
    public OutputStream openOutputStream() {
        return bos;
    }
}复制
```

### 3.3. 顶层接口

虽然不是绝对必要的，但在使用内存编译为我们编译的类创建顶级接口时，它会很有帮助。这个额外步骤有两个主要好处：

1.  我们知道从*ClassLoader*期待什么类型的对象，所以我们可以更安全/更容易地转换。
2.  我们可以在类加载器之间保持对象相等。**如果从不同类加载器加载的类中创建，则完全相同的对象可能会出现相等性问题**。由同一个*ClassLoader*加载的共享接口弥补了这一差距。

许多预定义的[功能接口](https://www.baeldung.com/java-8-functional-interfaces)适合这种编码模式，例如*Function*、*Runnable*和*Callable*。但是，对于本指南，我们将创建自己的：

```java
public interface InMemoryClass {
    void runCode();
}复制
```

现在，我们只需要返回并稍微调整我们的源代码来实现我们的新接口：

```java
static String sourceCode =
  "package com.baeldung.inmemorycompilation;\n" 
    + "public class TestClass implements InMemoryClass {\n" 
    + "@Override\n" 
    + "    public void runCode() {\n" 
    + "        System.out.println(\"code is running...\");\n" 
    + "    }\n" 
    + "}\n";复制
```

## 4. 管理我们的内存代码

现在我们已经为 JavaCompiler API 获得了正确格式的代码，我们需要一个可以对其进行操作的*FileManager* 。标准的*FileManager* 不能满足我们的目的，并且与 JavaCompiler API 中的大多数其他抽象一样，没有可供我们使用的默认实现。

幸运的是，*工具*包确实包含*ForwardingJavaFileManager*，它只是将所有方法调用转发给底层的*FileManager*。***我们可以通过扩展ForwardingJavaFileManager\*****并仅覆盖我们想要自己处理的行为****来利用此行为**，类似于我们对*SimpleJavaFileObject*所做的。

首先，我们需要覆盖*getJavaFileForOutput()*。*JavaCompiler将在我们的**FileManager*上 调用此方法以获得已编译字节码的*JavaFileObject*。我们需要为它提供我们新的自定义类*JavaClassAsBytes*的实例：

```java
public class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    // standard constructor

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
      FileObject sibling) {
        return new JavaClassAsBytes(className, kind);
    }
}复制
```

我们还需要在某个地方存储编译后的类，以便稍后可以通过我们的自定义*ClassLoader 检索它们。*我们将把这些类插入到一个*Map*中 ，并提供一个方便的方法来访问它：

```java
public class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private Map<String, JavaClassAsBytes> compiledClasses;

    public InMemoryFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
        this.compiledClasses = new Hashtable<>();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location,
        String className, Kind kind, FileObject sibling) {

        JavaClassAsBytes classAsBytes = new JavaClassAsBytes(className, kind);
        compiledClasses.put(className, classAsBytes);

        return classAsBytes;
    }

    public Map<String, JavaClassAsBytes> getBytesMap() {
        return compiledClasses;
    }
}复制
```

## 5.加载我们的内存代码

最后一步是创建一些东西来加载我们的类，一旦它们被编译。我们将为我们的*InMemoryFileManager构建一个互补的**ClassLoader*。

[类加载](https://www.baeldung.com/java-classloaders)本身就是一个相当深入的主题，它超出了本文的范围。简而言之，**我们要将自定义\*ClassLoader\*挂接到现有委托层次结构的底部，并使用它直接从\*FileManager\***加载类：

[![内存编译类加载器图](https://www.baeldung.com/wp-content/uploads/2023/01/inmemoryclassloaderdiagram2.png)](https://www.baeldung.com/wp-content/uploads/2023/01/inmemoryclassloaderdiagram2.png)

 

首先，我们需要创建一个扩展*ClassLoader*的自定义类。我们将稍微修改构造函数以接受我们的*InMemoryFileManager*作为参数。这将允许我们的*ClassLoader*稍后在管理器中进行查找：

```java
public class InMemoryClassLoader extends ClassLoader {

    private InMemoryFileManager manager;

    public InMemoryClassLoader(ClassLoader parent, InMemoryFileManager manager) {
        super(parent);
        this.manager = requireNonNull(manager, "manager must not be null");
    }
}复制
```

接下来，我们需要覆盖*ClassLoader的**findClass()*方法来定义在哪里寻找我们编译的类。对我们来说幸运的是，这只是检查存储在*InMemoryFileManager*中的地图：

```java
@Override
protected Class<?> findClass(String name) throws ClassNotFoundException {

    Map<String, JavaClassAsBytes> compiledClasses = manager.getBytesMap();

    if (compiledClasses.containsKey(name)) {
        byte[] bytes = compiledClasses.get(name).getBytes();
        return defineClass(name, bytes, 0, bytes.length);
    } else {
        throw new ClassNotFoundException();
    }
}复制
```

我们应该注意，**如果找不到该类，****我们将抛出\*ClassNotFoundException\***。由于我们处于层次结构的底部，如果现在还没有找到它，就不会在任何地方找到它。

现在我们已经完成了*InMemoryClassLoader*，我们需要返回并对我们的*InMemoryFileManager*进行一些小修改以完成它们的双向关系。首先，我们将创建一个*ClassLoader*成员变量并修改构造函数以接受我们的新*InMemoryClassLoader*：

```java
private ClassLoader loader; 

public InMemoryFileManager(StandardJavaFileManager standardManager) {
    super(standardManager);
    this.compiledClasses = new Hashtable<>();
    this.loader = new InMemoryClassLoader(this.getClass().getClassLoader(), this);
}复制
```

接下来，我们需要覆盖*getClassLoader()*以返回我们新的*InMemoryClassLoader*实例：

```java
@Override
public ClassLoader getClassLoader(Location location) {
    return loader;
}复制
```

**现在，如果我们愿意，我们可以将相同的\*FileManager\*和\*ClassLoader一起重新用于多个内存中编译。\***

## 6. 把它们放在一起

剩下要做的唯一一件事就是将我们所有不同的部分放在一起。让我们看看我们如何通过一个简单的单元测试来做到这一点：

```java
@Test
public void whenStringIsCompiled_ThenCodeShouldExecute() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    InMemoryFileManager manager = new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));

    List<JavaFileObject> sourceFiles = Collections.singletonList(new JavaSourceFromString(qualifiedClassName, sourceCode));

    JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sourceFiles);

    boolean result = task.call();

    if (result) {
        diagnostics.getDiagnostics()
          .forEach(d -> LOGGER.error(String.valueOf(d)));
    } else {
        ClassLoader classLoader = manager.getClassLoader(null);
        Class<?> clazz = classLoader.loadClass(qualifiedClassName);
        InMemoryClass instanceOfClass = (InMemoryClass) clazz.newInstance();

        Assertions.assertInstanceOf(InMemoryClass.class, instanceOfClass);

        instanceOfClass.runCode();
    }
}复制
```

当我们执行测试时，我们观察控制台输出：

```java
code is running...复制
```

**可以看到我们\*String\*源码中的方法已经执行成功了！**

## 七、结论

在本文中，我们学习了如何将包含 Java 源代码的*String*转换为已编译的类，然后执行它。

作为一般警告，我们应该注意在使用类加载器时要格外小心。*Class*和*ClassLoader*之间的双向关系使得自定义类加载容易出现[内存泄漏](https://www.baeldung.com/java-memory-leaks)。在使用第三方库时尤其如此，它们可能会在幕后保留类引用。