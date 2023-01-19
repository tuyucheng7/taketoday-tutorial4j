## 1. 概述

在这篇简短的文章中，我们将回顾以编程方式创建 jar 文件的过程。在编写软件时，最终我们需要将其部署到生产状态。在某些情况下，可以使用带有单独文件的类路径。通常，处理单个文件更方便。对于 Java，执行此操作的标准方法是使用 JAR、WAR 或 EAR 文件。

基本过程是编写清单、打开 jar、添加内容，最后关闭 jar。

## 2. Jar 文件剖析

jar 文件是 ZIP 文件格式的扩展，包含清单文件。清单文件是特定于 JAR 文件的特殊文件，可能包含各种设置。其中一些是主类、可选数据(即作者、版本等)和代码签名信息。

我们可能会使用 zip 兼容工具(例如[WinRar](https://www.baeldung.com/winrar.com))来查看和提取部分或全部档案。我们还可以包含一个 jars 或 libs 子目录来包含依赖 jar。由于 jar 是 zip 文件的扩展，我们可以包含任何文件或目录。

## 3. 创建JarTool类

为了简化创建 JAR 文件的过程，我们创建了一个单独的普通旧Java对象 (POJO) 类来封装我们的操作。我们可能包括将条目放入清单文件、创建 JAR 文件、添加文件或目录。

我们还可以创建方法来执行从 JAR 中删除或什至将条目附加到现有 JAR，尽管这些操作需要完全读取和重写 JAR。

### 3.1. JAR清单

为了创建 JAR 文件，我们必须首先开始清单：

```java
public class JarTool {    
    private Manifest manifest = new Manifest();

    public void startManifest() {
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    }
}
```

如果我们希望 jar 是可执行的，我们必须设置主类：

```java
public void setMainClass(String mainFQCN) {
    if (mainFQCN != null && !mainFQCN.equals("")) {
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainFQCN);
    }
}
```

另外，如果我们想指定额外的属性，我们可以将它们添加到清单中，例如：

```java
addToManifest("Can-Redefine-Classes", "true");
```

这是该方法：

```java
public void addToManifest(String key, String value) {
     manifest.getMainAttributes().put(new Attributes.Name(key), value);
}
```

### 3.2. 开瓶写作

完成清单后，我们现在可以将条目写入 JAR 文件。为此，我们必须首先打开罐子：

```java
public JarOutputStream openJar(String jarFile) throws IOException {        
    return new JarOutputStream(new FileOutputStream(jarFile), manifest);
}

```

### 3.3. 将文件添加到 Jar

将文件添加到 JAR 时，Java 使用 Solaris 样式的文件名，并使用正斜杠作为分隔符 (/)。请注意，我们可以添加任何类型的任何文件，包括其他 JAR 文件或空目录。这对于包含依赖项非常方便。

此外，由于 JAR 文件是类路径的一种形式，我们必须指定我们希望在 JAR 中使用绝对路径的哪一部分。出于我们的目的，根路径将是我们项目的类路径。

理解了这一点，我们现在可以用这个方法完成我们的JarTool类：

```java
public void addFile(JarOutputStream target, String rootPath, String source) 
  throws FileNotFoundException, IOException {
    String remaining = "";
    if (rootPath.endsWith(File.separator)) {
        remaining = source.substring(rootPath.length());
    } else {
        remaining = source.substring(rootPath.length() + 1);
    }
    String name = remaining.replace("","/");
    JarEntry entry = new JarEntry(name);
    entry.setTime(new File(source).lastModified());
    target.putNextEntry(entry);
    
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
    byte[] buffer = new byte[1024];
    while (true) {
        int count = in.read(buffer);
        if (count == -1) {
            break;
        }
        target.write(buffer, 0, count);
    }
    target.closeEntry();
    in.close();
}
```

## 4. 一个工作示例

为了演示可执行 jar 的最低要求，我们将编写一个应用程序类，然后看看它是如何工作的：

```java
public class Driver {
    public static void main(String[] args) throws IOException {
        JarTool tool = new JarTool();
        tool.startManifest();
        tool.addToManifest("Main-Class", "createjar.cn.tuyucheng.taketoday.HelloWorld");

        JarOutputStream target = tool.openJar("HelloWorld.jar");
        
        tool.addFile(target, System.getProperty("user.dir") + "srcmainjava",
          System.getProperty("user.dir") + "srcmainjavacombaeldungcreatejarHelloWorld.class");
        target.close();
    }
}
```

HelloWorld 类是一个非常简单的类，只有一个用于打印文本的 main() 方法：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

```

为了证明它有效，我们有这个例子：

```shell
$ javac -cp src/main/java src/main/java/com/baeldung/createjar/HelloWorld.java
$ javac -cp src/main/java src/main/java/com/baeldung/createjar/JarTool.java
$ javac -cp src/main/java src/main/java/com/baeldung/createjar/Driver.java
$ java -cp src/main/java com/baeldung/createjar/Driver
$ java -jar HelloWorld.jar
Hello World!

```

在这里，我们编译了每个类，然后执行了Driver类，这将创建HelloWorld jar。最后，我们执行了 jar，结果打印了“Hello World”消息。

上面的命令应该从项目位置执行。

## 5.总结

在本教程中，我们了解了如何以编程方式创建 jar 文件、向其中添加文件以及最后执行它。