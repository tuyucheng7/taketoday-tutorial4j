## Core Java JVM Cookbooks and Examples

本模块包含有关使用Java Virtual Machine(JVM)的文章。

## 相关文章

+ [Java Instrumentation指南](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-instrumentation.html)
+ [Java中的Runtime.getRuntime().halt()与System.exit()](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-runtime-halt-vs-system-exit.html)
+ [Java中的类加载器](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-classloaders.html)
+ [JVM中的方法内联](http://tu-yucheng.github.io/java-jvm/2023/06/12/jvm-method-inlining.html)
+ [JVM日志锻造](http://tu-yucheng.github.io/java-jvm/2023/06/12/jvm-log-forging.html)
+ [System.exit()指南](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-system-exit.html)
+ [System.gc()指南](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-system-gc.html)
+ [在Java中查看class文件的字节码](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-class-view-bytecode.html)
+ [如何在Java中获取对象的大小](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-size-of-object.html)
+ [是什么原因导致java.lang.OutOfMemoryError: unable to create new native thread](http://tu-yucheng.github.io/java-jvm/2023/06/12/java-outofmemoryerror-unable-to-create-new-native-thread.html)

- 更多文章： [[next -->]](../java-jvm-2/README.md)

运行Instrumentation的代码: http://tu-yucheng.github.io/java-jvm/2023/06/12/java-instrumentation.html 文章:
1- 构建模块
2- 运行模块3次以构建3个jar：
    mvn install -PbuildAgentLoader
    mvn install -PbuildApplication
    mvn install -PbuildAgent
3- 使用target文件夹中生成的JAR的确切名称更新文章中的命令
4- 使用系统上代理的路径更新代理加载器类中的路径