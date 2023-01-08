## Core Java JVM Cookbooks and Examples

本模块包含有关使用Java Virtual Machine(JVM)的文章。

## 相关文章

+ [Java Instrumentation指南](docs/Java-Instrumentation指南.md)
+ [Java中的Runtime.getRuntime().halt()与System.exit()](docs/Java中的Runtime.getRuntime().halt()与System.exit().md)
+ [Java中的类加载器](docs/Java中的类加载器.md)
+ [JVM中的方法内联](docs/JVM中的方法内联.md)
+ [JVM日志锻造](docs/JVM日志锻造.md)
+ [System.exit()指南](docs/System.exit()指南.md)
+ [System.gc()指南](docs/System.gc()指南.md)
+ [在Java中查看类文件的字节码](docs/在Java中查看类文件的字节码.md)
+ [如何在Java中获取对象的大小](docs/如何在Java中获取对象的大小.md)
+ [是什么原因导致java.lang.OutOfMemoryError: unable to create new native thread](docs/是什么原因导致java.lang.OutOfMemoryError无法创建新的本地线程.md)

- 更多文章： [[next -->]](../java-jvm-2/README.md)

运行Instrumentation的代码: https://www.baeldung.com/java-instrumentation 文章:
1- 构建模块
2- 运行模块3次以构建3个jar：
    mvn install -PbuildAgentLoader
    mvn install -PbuildApplication
    mvn install -PbuildAgent
3- 使用target文件夹中生成的JAR的确切名称更新文章中的命令
4- 使用系统上代理的路径更新代理加载器类中的路径