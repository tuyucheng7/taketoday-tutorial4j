## 1. 概述

在本教程中，我们将讨论如何使用Java查找[IP 地址](https://www.baeldung.com/cs/ipv4-vs-ipv6)是否在给定范围内。对于这个问题，我们将在整篇文章中考虑所有给定的 IP 地址都是有效的 IPv4(Internet 协议版本 4)和 IPv6(Internet 协议版本 6)地址。

## 二、问题简介

给定一个输入 IP 地址以及其他两个 IP 地址作为范围(从和到)。我们应该能够确定输入的[IP 地址是否在给定的范围内](https://www.baeldung.com/cs/get-ip-range-from-subnet-mask)。

例如：

-   输入 = 192.220.3.0，范围在 192.210.0.0 和 192.255.0.0 之间
    输出 = true
-   输入 = 192.200.0.0，范围在 192.210.0.0 和 192.255.0.0 之间
    输出 = false

现在，让我们看看使用各种Java库检查给定 IP 地址是否在范围内的不同方法。

## 3.IP地址库

IPAddress库由 Sean C Foley 编写，支持[为](https://seancfoley.github.io/IPAddress/)广泛的[用例](https://github.com/seancfoley/IPAddress/wiki/Code-Examples)处理 IPv4 和 IPv6 地址。请务必注意，此库至少需要Java8 才能运行。

设置这个库很简单。我们需要将[ipaddress](https://mvnrepository.com/artifact/com.github.seancfoley/ipaddress)依赖项添加到我们的 pom.xml 中：

```xml
<dependency>
    <groupId>com.github.seancfoley</groupId>
    <artifactId>ipaddress</artifactId>
    <version>5.3.3</version>
</dependency>
```

它提供了解决我们的问题所需的以下Java类：

-   IPAddress，将 IP 地址保存为Java实例
-   IPAddressString，从给定的 IP 构造 IPAddress 实例作为字符串
-   IPAddressSeqRange，表示任意范围的 IP 地址

现在，让我们看一下使用上述类查找 IP 地址是否在给定范围内的代码：

```java
public static boolean checkIPIsInGivenRange (String inputIP, String rangeStartIP, String rangeEndIP) 
  throws AddressStringException {
    IPAddress startIPAddress = new IPAddressString(rangeStartIP).getAddress();
    IPAddress endIPAddress = new IPAddressString(rangeEndIP).getAddress();
    IPAddressSeqRange ipRange = startIPAddress.toSequentialRange(endIPAddress);
    IPAddress inputIPAddress = new IPAddressString(inputIP).toAddress();

    return ipRange.contains(inputIPAddress);
}
```

上面的代码适用于 IPv4 和 IPv6 地址。IPAddressString参数化构造函数将 IP 作为字符串来构造IPAddress实例。IPAddressString实例可以使用以下两种方法之一转换为IPAddress ：

-   讲话()
-   获取地址()

getAddress () 方法假定给定的 IP 有效，但toAddress()方法验证输入一次，如果无效则抛出AddressStringException 。IPAddress 类提供了一个toSequentialRange 方法，该方法使用开始和结束 IP 范围构造IPAddressSeqRange实例。

让我们考虑几个使用 IPv4 和 IPv6 地址调用checkIPIsInGivenRange 的单元案例：

```java
@Test
void givenIPv4Addresses_whenIsInRange_thenReturnsTrue() throws Exception {
    assertTrue(IPWithGivenRangeCheck.checkIPIsInGivenRange("192.220.3.0", "192.210.0.0", "192.255.0.0"));
}

@Test
void givenIPv4Addresses_whenIsNotInRange_thenReturnsFalse() throws Exception {
    assertFalse(IPWithGivenRangeCheck.checkIPIsInGivenRange("192.200.0.0", "192.210.0.0", "192.255.0.0"));
}

@Test
void givenIPv6Addresses_whenIsInRange_thenReturnsTrue() throws Exception {
    assertTrue(IPWithGivenRangeCheck.checkIPIsInGivenRange(
      "2001:db8:85a3::8a03:a:b", "2001:db8:85a3::8a00:ff:ffff", "2001:db8:85a3::8a2e:370:7334"));
}

@Test
void givenIPv6Addresses_whenIsNotInRange_thenReturnsFalse() throws Exception {
    assertFalse(IPWithGivenRangeCheck.checkIPIsInGivenRange(
      "2002:db8:85a3::8a03:a:b", "2001:db8:85a3::8a00:ff:ffff", "2001:db8:85a3::8a2e:370:7334"));
}
```

## 4. 公共知识产权数学

[Commons IP Math](https://github.com/jgonian/commons-ip-math)库提供了用于表示 IPv4 和 IPv6 地址和范围的类。它提供了用于处理最常见操作的 API，此外，它还提供了用于处理 IP 范围的比较器和其他实用程序。

我们需要将[commons-ip-math](https://mvnrepository.com/artifact/com.github.jgonian/commons-ip-math)依赖项添加到我们的 pom.xml 中：

```xml
<dependency>
    <groupId>com.github.jgonian</groupId>
    <artifactId>commons-ip-math</artifactId>
    <version>1.32</version>
</dependency>

```

### 4.1. 对于 IPv4

该库提供Ipv4和Ipv4Range 类，分别用于保存单个 IP 地址和地址范围作为实例。现在，让我们看一下使用上述类的代码示例：

```java
public static boolean checkIPv4IsInRange (String inputIP, String rangeStartIP, String rangeEndIP) {
    Ipv4 startIPAddress = Ipv4.of(rangeStartIP);
    Ipv4 endIPAddress = Ipv4.of(rangeEndIP);
    Ipv4Range ipRange = Ipv4Range.from(startIPAddress).to(endIPAddress);
    Ipv4 inputIPAddress = Ipv4.of(inputIP);
    return ipRange.contains(inputIPAddress);
}
```

Ipv4类提供了一个静态方法of() ，它使用 IP 字符串来构造一个Ipv4实例。Ipv4Range 类使用[构建器设计模式](https://www.baeldung.com/creational-design-patterns#builder)通过使用from()和to()方法指定范围来创建其实例。此外，它还提供了包含 () 的函数来检查指定范围内是否存在 IP 地址。

现在让我们对我们的函数运行一些测试：

```java
@Test
void givenIPv4Addresses_whenIsInRange_thenReturnsTrue() throws Exception {
    assertTrue(IPWithGivenRangeCheck.checkIPv4IsInRange("192.220.3.0", "192.210.0.0", "192.255.0.0"));
}

@Test
void givenIPv4Addresses_whenIsNotInRange_thenReturnsFalse() throws Exception {
    assertFalse(IPWithGivenRangeCheck.checkIPv4IsInRange("192.200.0.0", "192.210.0.0", "192.255.0.0"));
}
```

### 4.2. 对于 IPv6

对于 IP 版本 6，该库提供相同的类和函数，但版本号从 4 → 6 有所变化。版本 6 的类是Ipv6和Ipv6Range。 

让我们通过使用上述类来查看 IP 版本 6 的代码示例：

```java
public static boolean checkIPv6IsInRange (String inputIP, String rangeStartIP, String rangeEndIP) {
    Ipv6 startIPAddress = Ipv6.of(rangeStartIP);
    Ipv6 endIPAddress = Ipv6.of(rangeEndIP);
    Ipv6Range ipRange = Ipv6Range.from(startIPAddress).to(endIPAddress);
    Ipv6 inputIPAddress = Ipv6.of(inputIP);
    return ipRange.contains(inputIPAddress);
}
```

现在让我们运行单元测试来检查我们的代码：

```java
@Test
void givenIPv6Addresses_whenIsInRange_thenReturnsTrue() throws Exception {
    assertTrue(IPWithGivenRangeCheck.checkIPv6IsInRange(
      "2001:db8:85a3::8a03:a:b", "2001:db8:85a3::8a00:ff:ffff", "2001:db8:85a3::8a2e:370:7334"));
}

@Test
void givenIPv6Addresses_whenIsNotInRange_thenReturnsFalse() throws Exception {
    assertFalse(IPWithGivenRangeCheck.checkIPv6IsInRange(
      "2002:db8:85a3::8a03:a:b", "2001:db8:85a3::8a00:ff:ffff", "2001:db8:85a3::8a2e:370:7334"));
}
```

## 5.为 IPv4使用Java的InetAddress类

IPv4 地址是四个 1 字节值的序列。因此，它可以转换为 32 位整数。我们可以检查它是否在给定范围内。

Java 的InetAddress 类表示 IP 地址并提供获取任何给定主机名的 IP 的方法。InetAddress 的实例表示具有相应主机名的 IP 地址。

下面是将 IPv4 地址转换为长整数的Java代码：

```java
long ipToLongInt (InetAddress ipAddress) {
    long resultIP = 0;
    byte[] ipAddressOctets = ipAddress.getAddress();

    for (byte octet : ipAddressOctets) {
        resultIP <<= 8;
        resultIP |= octet & 0xFF;
    }
    return resultIP;
}
```

通过使用上述方法，让我们检查 IP 是否在范围内：

```java
public static boolean checkIPv4IsInRangeByConvertingToInt (String inputIP, String rangeStartIP, String rangeEndIP) 
  throws UnknownHostException {
    long startIPAddress = ipToLongInt(InetAddress.getByName(rangeStartIP));
    long endIPAddress = ipToLongInt(InetAddress.getByName(rangeEndIP));
    long inputIPAddress = ipToLongInt(InetAddress.getByName(inputIP));

    return (inputIPAddress >= startIPAddress && inputIPAddress <= endIPAddress);
}
```

InetAddress类中的getByName ()方法将域名或 IP 地址作为输入，如果无效则抛出UnknownHostException 。让我们通过运行单元测试来检查我们的代码：

```java
@Test
void givenIPv4Addresses_whenIsInRange_thenReturnsTrue() throws Exception {
    assertTrue(IPWithGivenRangeCheck.checkIPv4IsInRangeByConvertingToInt("192.220.3.0", "192.210.0.0", "192.255.0.0"));
}

@Test
void givenIPv4Addresses_whenIsNotInRange_thenReturnsFalse() throws Exception {
    assertFalse(IPWithGivenRangeCheck.checkIPv4IsInRangeByConvertingToInt("192.200.0.0", "192.210.0.0", "192.255.0.0"));
}
```

上述将IP地址转换为整数的逻辑同样适用于IPv6，但它是一个128位的整数。Java 语言在原始数据类型中最多支持 64 位(长整型)。如果我们必须对版本 6 应用上述逻辑，我们需要使用两个长整数或[BigInteger](https://www.baeldung.com/java-bigdecimal-biginteger)类进行计算。但这将是一个繁琐的过程，并且还涉及复杂的计算。

## 6.JavaIPv6 库

[Java IPv6 库](https://github.com/janvanbesien/java-ipv6/)是专门为Java中的 IPv6 支持和对其执行相关操作而编写的。该库在内部使用两个长整数来存储 IPv6 地址。它至少需要Java6 才能工作。

我们需要将[java-ipv6](https://mvnrepository.com/artifact/com.googlecode.java-ipv6/java-ipv6)依赖项添加到我们的 pom.xml 中：

```xml
<dependency>
    <groupId>com.googlecode.java-ipv6</groupId>
    <artifactId>java-ipv6</artifactId>
    <version>0.17</version>
</dependency>

```

该库提供各种类来处理 IPv6 地址。以下是帮助我们解决问题的两个：

-   IPv6Address，用于将 IPv6 表示为Java实例
-   IPv6AddressRange，用于表示连续的 IPv6 地址范围

让我们看一下使用上述类检查 IP 是否在给定范围内的代码片段：

```java
public static boolean checkIPv6IsInRangeByIPv6library (String inputIP, String rangeStartIP, String rangeEndIP) {
    IPv6Address startIPAddress = IPv6Address.fromString(rangeStartIP);
    IPv6Address endIPAddress = IPv6Address.fromString(rangeEndIP);
    IPv6AddressRange ipRange = IPv6AddressRange.fromFirstAndLast(startIPAddress, endIPAddress);
    IPv6Address inputIPAddress = IPv6Address.fromString(inputIP);
    return ipRange.contains(inputIPAddress);
}
```

IPv6Address类为我们提供了各种静态函数来构造其实例：

-   来自字符串
-   来自互联网地址
-   来自大整数
-   来自字节数组
-   来自Longs

以上所有方法都是不言自明的，这有助于我们创建一个IPv6Address实例。IPv6AddressRange 有一个名为fromFirstAndLast()的方法，它接受两个 IP 地址作为输入。此外，它还提供了一个contains()方法，该方法将IPv6Address作为参数并确定它是否存在于指定范围内。

通过调用我们定义的上述方法，让我们在测试中传递一些样本输入：

```java
@Test
void givenIPv6Addresses_whenIsInRange_thenReturnsTrue() throws Exception {
    assertTrue(IPWithGivenRangeCheck.checkIPv6IsInRangeByIPv6library(
      "fe80::226:2dff:fefa:dcba",
      "fe80::226:2dff:fefa:cd1f",
      "fe80::226:2dff:fefa:ffff"
    ));
}

@Test
void givenIPv6Addresses_whenIsNotInRange_thenReturnsFalse() throws Exception {
    assertFalse(IPWithGivenRangeCheck.checkIPv6IsInRangeByIPv6library(
      "2002:db8:85a3::8a03:a:b",
      "2001:db8:85a3::8a00:ff:ffff",
      "2001:db8:85a3::8a2e:370:7334"
    ));
}
```

## 七、总结

在本文中，我们研究了如何确定给定的 IP 地址(v4 和 v6)是否在指定范围内。在各种库的帮助下，我们分析了检查 IP 地址是否存在，而无需任何复杂的逻辑和计算。