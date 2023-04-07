## **一、概述**

在本快速教程中，**我们将重点介绍 Java 中 JAR 和 WAR 打包之间的区别。**

首先，我们将分别定义每个打包选项。之后，我们将总结它们的不同之处。

## **2.JAR打包**

简而言之，JAR（或 Java Archive）是一种包文件格式。JAR 文件具有*.jar*扩展名，可能包含**库、资源和元数据文件。**

*本质上，它是一个压缩文件，其中包含.class*文件的压缩版本以及已编译的 Java 库和应用程序的资源。

例如，这是一个简单的 JAR 文件结构：

```plaintext
META-INF/
    MANIFEST.MF
com/
    baeldung/
        MyApplication.class
复制
```

META [*-INF/MANIFEST.MF*文件](https://www.baeldung.com/java-jar-executable-manifest-main-class)可能包含有关存档中存储的文件的其他元数据。

我们可以使用*jar命令或*[Maven](https://www.baeldung.com/executable-jar-with-maven)等工具[创建 JAR](https://www.baeldung.com/java-create-jar)文件。

## **3. WAR包装**

WAR 代表 Web 应用程序存档或 Web 应用程序资源。这些存档文件具有*.war*扩展名，**用于打包**我们可以部署在任何 Servlet/JSP 容器上的 Web 应用程序。

下面是典型 WAR 文件结构的示例布局：

```plaintext
META-INF/
    MANIFEST.MF
WEB-INF/
    web.xml
    jsp/
        helloWorld.jsp
    classes/
        static/
        templates/
        application.properties
    lib/
        // *.jar files as libs复制
```

在内部，它有一个*META-INF*目录，其中包含*MANIFEST.MF*中有关 Web 存档的有用信息。META *-INF*目录是私有的，不能从外部访问。

另一方面，它还包含*WEB-INF*公共目录，其中包含所有静态 Web 资源，包括 HTML 页面、图像和 JS 文件。此外，它还包含*web.xml*文件、servlet 类和库。

我们可以使用用于构建 JAR 的相同工具和命令来构建*.war*存档。

## **4. 主要区别**

那么，这两种存档类型之间的主要区别是什么？

第一个也是最明显的区别是**文件扩展名**。JAR 的扩展名为*.jar*，而 WAR 文件的扩展名为*.war*。

第二个主要区别是**它们的目的和运作方式**。JAR 文件允许我们打包多个文件，以便将其用作库、插件或任何类型的应用程序。另一方面，WAR 文件仅用于 Web 应用程序。

**档案的结构也不同。**我们可以创建具有任何所需结构的 JAR。*相比之下，WAR 具有包含WEB-INF*和*META-INF*目录的预定义结构。

最后，如果我们将 JAR 构建为[可执行 JAR](https://www.baeldung.com/executable-jar-with-maven)而无需使用其他软件，我们就可以**从命令行运行它。**或者，我们可以将其用作库。相反，我们**需要一个服务器来执行 WAR**。

## **5.结论**

在这篇快速文章中，我们比较了*.jar*和*.war* Java 打包类型。这样做时，我们还注意到虽然两者使用相同的 ZIP 文件格式，但存在几个重要的区别。