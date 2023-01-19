## 1. 概述

[大多数Java库都以JAR 文件的](https://www.baeldung.com/java-create-jar)形式提供。在本教程中，我们将介绍如何从命令行和Java程序中获取给定 JAR 文件中类的名称。

然后，我们将查看在运行时从给定 JAR 文件加载类的Java程序示例。

## 2. 示例 JAR 文件

在本教程中，我们将以[stripe-0.0.1-SNAPSHOT.jar](https://github.com/eugenp/tutorials/tree/master/stripe)文件为例，介绍如何获取 JAR 文件中的类名：

[![罐子示例 1](https://www.baeldung.com/wp-content/uploads/2020/10/a_jar_example-1.png)](https://www.baeldung.com/wp-content/uploads/2020/10/a_jar_example-1.png)

## 3.使用 jar命令

JDK 附带了一个 [jar](https://docs.oracle.com/en/java/javase/11/tools/jar.html)命令。我们可以使用带有t和f选项的此命令来列出 JAR 文件的内容：

```bash
$ jar tf stripe-0.0.1-SNAPSHOT.jar 
META-INF/
META-INF/MANIFEST.MF
...
templates/result.html
templates/checkout.html
application.properties
com/baeldung/stripe/StripeApplication.class
com/baeldung/stripe/ChargeRequest.class
com/baeldung/stripe/StripeService.class
com/baeldung/stripe/ChargeRequest$Currency.class
...
```

由于我们只对存档中的.class文件感兴趣，我们可以使用[grep](https://www.baeldung.com/linux/common-text-search)命令过滤输出：

```bash
$ jar tf stripe-0.0.1-SNAPSHOT.jar | grep '.class$'
com/baeldung/stripe/StripeApplication.class
com/baeldung/stripe/ChargeRequest.class
com/baeldung/stripe/StripeService.class
com/baeldung/stripe/ChargeRequest$Currency.class
com/baeldung/stripe/ChargeController.class
com/baeldung/stripe/CheckoutController.class
```

这为我们提供了 JAR 文件中的类文件列表。

## 4. 在Java中获取 JAR 文件的类名

使用 jar命令打印 JAR 文件中的类名非常简单。但是，有时我们希望在我们的Java程序中从 JAR 文件中加载一些类。在这种情况下，命令行输出是不够的。

为了实现我们的目标，我们需要从Java程序扫描 JAR 文件并获取类名。

让我们看看如何使用[JarFile](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/jar/JarFile.html) 和[JarEntry](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/jar/JarEntry.html)类从示例 JAR 文件中提取类名：

```java
public static Set<String> getClassNamesFromJarFile(File givenFile) throws IOException {
    Set<String> classNames = new HashSet<>();
    try (JarFile jarFile = new JarFile(givenFile)) {
        Enumeration<JarEntry> e = jarFile.entries();
        while (e.hasMoreElements()) {
            JarEntry jarEntry = e.nextElement();
            if (jarEntry.getName().endsWith(".class")) {
                String className = jarEntry.getName()
                  .replace("/", ".")
                  .replace(".class", "");
                classNames.add(className);
            }
        }
        return classNames;
    }
}

```

现在，让我们仔细看看上面方法中的代码，了解它是如何工作的：

-   try (JarFile jarFile = new JarFile(givenFile)) – 在这里，我们使用[try-with-resources 语句](https://www.baeldung.com/java-try-with-resources)从给定的File对象中获取jarFile
-   if (jarEntry.getName().endsWith(“.class”)){…} – 我们获取每个类jarEntry，并将类文件的路径更改为合格的类名，例如更改“package1/package2/SomeType.类”变成“package1.package2.SomeType”

让我们通过单元测试方法验证该方法是否可以从我们的示例 JAR 文件中提取类名：

```java
private static final String JAR_PATH = "example-jar/stripe-0.0.1-SNAPSHOT.jar";
private static final Set<String> EXPECTED_CLASS_NAMES = Sets.newHashSet(
  "com.baeldung.stripe.StripeApplication",
  "com.baeldung.stripe.ChargeRequest",
  "com.baeldung.stripe.StripeService",
  "com.baeldung.stripe.ChargeRequest$Currency",
  "com.baeldung.stripe.ChargeController",
  "com.baeldung.stripe.CheckoutController");

@Test
public void givenJarFilePath_whenLoadClassNames_thenGetClassNames() throws IOException, URISyntaxException {
    File jarFile = new File(
      Objects.requireNonNull(getClass().getClassLoader().getResource(JAR_PATH)).toURI());

    Set<String> classNames = GetClassNamesFromJar.getClassNamesFromJarFile(jarFile);

    Assert.assertEquals(EXPECTED_CLASS_NAMES, classNames);
}

```

## 5. 在Java中从 JAR 文件中获取类

我们已经了解了如何从 JAR 文件中获取类名。有时，我们希望在运行时动态地从 JAR 文件中加载一些类。

在这种情况下，我们可以首先使用我们的getClassNamesFromJarFile方法从给定的 JAR 文件中获取类名。

接下来，我们可以创建一个ClassLoader来按名称加载所需的类：

```java
public static Set<Class> getClassesFromJarFile(File jarFile) throws IOException, ClassNotFoundException {
    Set<String> classNames = getClassNamesFromJarFile(jarFile);
    Set<Class> classes = new HashSet<>(classNames.size());
    try (URLClassLoader cl = URLClassLoader.newInstance(
           new URL[] { new URL("jar:file:" + jarFile + "!/") })) {
        for (String name : classNames) {
            Class clazz = cl.loadClass(name); // Load the class by its name
            classes.add(clazz);
        }
    }
    return classes;
}
```

在上面的方法中，我们创建了一个[URLClassLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/URLClassLoader.html)对象来加载类。实现非常简单。

但是，可能值得稍微解释一下 JAR URL 的语法。一个有效的 JAR URL 包含三个部分：“ jar: + [JAR 文件的位置] + !/”。 

结尾的“ !/ ”表示 JAR URL 指的是整个 JAR 文件。 让我们看几个 JAR URL 示例：

```bash
jar:http://www.example.com/some_jar_file.jar!/
jar:file:/local/path/to/some_jar_file.jar!/
jar:file:/C:/windows/path/to/some_jar_file.jar!/
```

在我们的getClassesFromJarFile方法中，JAR 文件位于本地文件系统中，因此，URL 的前缀是“ file: ”。

现在，让我们编写一个测试方法来验证我们的方法是否可以获得所有预期的Class对象：

```java
@Test
public void givenJarFilePath_whenLoadClass_thenGetClassObjects()
  throws IOException, ClassNotFoundException, URISyntaxException {
    File jarFile
      = new File(Objects.requireNonNull(getClass().getClassLoader().getResource(JAR_PATH)).toURI());
    Set<Class> classes = GetClassNamesFromJar.getClassesFromJarFile(jarFile);
    Set<String> names = classes.stream().map(Class::getName).collect(Collectors.toSet());
    Assert.assertEquals(EXPECTED_CLASS_NAMES, names);
}

```

一旦我们有了所需的Class对象，我们就可以使用[Java 反射](https://www.baeldung.com/java-reflection)来创建类的实例并调用方法。

## 六，总结

在本文中，我们学习了两种不同的方法来从给定的 JAR 文件中获取类名。

jar命令 可以打印类名。如果我们需要检查 JAR 文件是否包含给定的类，它会非常方便。但是，如果我们需要从正在运行的Java程序中获取类名，JarFile和 JarEntry可以帮助我们实现这一点。

最后，我们还看到了一个在运行时从 JAR 文件加载类的Java程序示例。