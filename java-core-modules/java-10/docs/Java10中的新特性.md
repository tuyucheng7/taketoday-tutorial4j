## 1. 简介

[JDK 10](https://openjdk.java.net/projects/jdk/10)是Java SE 10的实现，于2018年3月20日发布。

在本文中，我们介绍和探讨JDK 10中引入的新特性和更改。

## 2. 局部变量类型推断

请点击链接以获取有关此功能的深入文章：[Java 10局部变量类型推断](Java10局部变量类型推断.md)

## 3. 不可修改的集合

Java 10中有一些与不可修改集合相关的更改。

### 3.1 copyOf()

java.util.List、java.util.Map和java.util.Set都有一个新的静态方法copyOf(Collection)，它返回给定集合的不可修改的副本：

```java
@Test
void whenModifyCopyOfList_thenThrowsException() {
	List<Integer> copyList = List.copyOf(someIntList);
    
	assertThrows(UnsupportedOperationException.class, () -> copyList.add(4));
}
```

任何修改此类集合的尝试都将导致java.lang.UnsupportedOperationException运行时异常。

### 3.2 toUnmodifiable*()

java.util.stream.Collectors添加了额外的方法来将Stream收集到不可修改的List、Map或Set中：

```java
@Test
void whenModifyToUnmodifiableList_thenThrowsException() {
	List<Integer> evenList = someIntList.stream()
	    .filter(i -> i % 2 == 0)
	    .collect(Collectors.toUnmodifiableList());
    
	assertThrows(UnsupportedOperationException.class, () -> evenList.add(4));
}
```

任何修改此类集合的尝试都将导致java.lang.UnsupportedOperationException运行时异常。

## 4. Optional*.orElseThrow()

java.util.Optional、java.util.OptionalDouble、java.util.OptionalInt和java.util.OptionalLong都添加了一个新方法orElseThrow()，它不接收任何参数，并且在不存在值时抛出NoSuchElementException：

```java
@Test
void whenListContainsInteger_OrElseThrowReturnsInteger() {
	Integer firstEven = someIntList.stream()
	    .filter(i -> i % 2 == 0)
	    .findFirst()
	    .orElseThrow();
	is(firstEven).equals(2);
}
```

它与现有的get()方法同义，现在是它的首选替代方法。

## 5. 性能改进

请点击链接以获取有关此功能的深入文章：[Java 10性能改进](https://www.baeldung.com/java-10-performance-improvements)

## 6. 容器意识

**JVM现在可以意识到自己是否在Docker容器中运行**，并将提取特定于容器的配置，而不是查询操作系统本身，它适用于分配给容器的CPU数量和总内存等数据。

但是，此支持仅适用于基于Linux的平台。这种新的支持在默认情况下是启用的，并且可以在命令行中使用JVM选项禁用：

```bash
-XX:-UseContainerSupport
```

此外，此更改还添加了一个JVM选项，可以指定JVM将使用的CPU数量：

```bash
-XX:ActiveProcessorCount=count
```

此外，还添加了三个新的JVM选项，以允许Docker容器用户对将用于Java堆的系统内存量进行更细粒度的控制：

```bash
-XX:InitialRAMPercentage
-XX:MaxRAMPercentage
-XX:MinRAMPercentage
```

## 7. 根证书

到目前为止，cacerts密钥库最初为空，旨在包含一组根证书，这些根证书可用于在各种安全协议使用的证书链中建立信任。

因此，在OpenJDK构建下，TLS等关键安全组件默认不起作用。

**在Java 10中，Oracle开源了Oracle Java SE Root CA程序中的根证书**，以使OpenJDK构建对开发人员更具吸引力，并减少这些构建与Oracle JDK构建之间的差异。

## 8. 弃用和删除

### 8.1 命令行选项和工具

javah工具已从Java 10中删除，该工具用于生成实现本机方法所需的C头文件和源文件；现在，可以使用javac -h代替。

policytool是用于创建和管理策略文件的基于UI的工具，现在已将其删除。用户可以使用简单的文本编辑器来执行此操作。

删除了java -Xprof选项，该选项用于分析正在运行的程序并将分析数据发送到标准输出。用户现在应该改用jmap工具。

### 8.2 API

已弃用的java.security.acl包已被标记为forRemoval=true，并且可能会在Java SE的未来版本中被删除。它已被java.security.Policy和相关类所取代。

同样，java.security.{Certificate,Identity,IdentityScope,Signer} API被标记为forRemoval=true。

## 9. 基于时间的发布版本控制

从Java 10开始，Oracle已经转向基于时间的Java版本。这具有以下含义：

1.  **每六个月发布一个新的Java版本**。2018年3月的版本是JDK 10，2018年9月的版本是JDK 11，依此类推。这些被称为功能版本，预计至少包含一两个重要功能
2.  **对功能版本的支持仅持续六个月**，即直到下一个功能版本发布
3.  **长期支持版本将被标记为LTS，对此类版本的支持将持续三年**
4.  Java 11将是LTS版本

**java -version命令现在将包含GA日期**，从而更容易识别版本的发布时间：

```bash
$ java -version
java version "17.0.5" 2022-10-18 LTS
Java(TM) SE Runtime Environment (build 17.0.5+9-LTS-191)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.5+9-LTS-191, mixed mode, sharing)
```

## 10. 总结

在本文中，我们介绍了Java 10带来的新特性和变化。