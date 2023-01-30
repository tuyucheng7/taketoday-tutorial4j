## 1. 概述

在数据科学中，当我们谈论整数时，我们指的是表示某种数学整数范围的数据类型。我们知道数学整数没有上限，但这可能是内存大小方面的问题，迫使几乎所有计算机架构都限制这种数据类型。

所以，这就是我们今天要问的问题：“我们可以在某台机器上表示的最大整数值是多少？”

## 二、问题说明

好吧， 这个问题没有唯一的答案，因为影响答案的因素有很多。主要有：

-   平台位
-   使用的语言
-   签名或未签名版本

在计算机科学中，整数最常见的表示是一组二进制数字(位)，存储为二进制数字系统。内存字节的顺序各不相同——![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)我们可以用位来编码![2^n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0288856e580589b0aa07b6eb5048e37e_l3.svg)整数。

我们可以在这个简单的示例中看到差异：使用![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)位，我们可以将整数编码为从![0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8354ade9c79ec6a7ac658f2c3032c9df_l3.svg)到![2^n - 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-cffcb6c5ce8a64103759bbf1900c2bb8_l3.svg)或从![- 2^{n-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3831aeec131a0504989abe77bca34c53_l3.svg)到![2^{n-1} - 1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5891d7bddb907cf176f913dea01f59ed_l3.svg)。

通常，整数大小![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)由机器体系结构定义，但某些计算机语言也独立定义整数大小。不同的 CPU 支持不同的数据类型，基于它们自己的平台。通常，它们都支持有符号和无符号类型，但只支持一组有限且固定的宽度，通常为 8、16、32 和 64 位。

一些语言定义了两种或多种整数数据类型，一种较小的以节省内存和占用较少的存储空间，另一种较大的以扩大支持范围。

要记住的另一件事是平台的字长(也称为字长)。

在计算机科学中，词是特定处理器设计使用的数据单元，由处理器的硬件作为一个单元处理的固定大小的数据。当我们谈论字长时，我们指的是一个字中的位数，这是任何特定计算机体系结构和处理器设计的关键特征。

当前的体系结构通常具有 64 位字长，但也有一些具有 32 位字长。

## 3. 答案时间

在本节中，我们将从老朋友 C 开始回答我们的问题，再到相对较新的 Python，再经过常青树 Java。开始吧。

### 3.1. C

C语言是1972年设计的，目的是在不同的机器类型上以相同的方式工作。因此，它不会直接确定整数数据类型的范围，因为这取决于机器体系结构。

但是，C 有两种整数；又短又长。

一个短整数至少是 16 位。所以，在 16 位机器上，它恰好符合长整型格式。对于有符号版本，短整数格式范围为 -32,767 到 32,767，对于无符号版本，范围为 0 到 65,535。好吧，这很奇怪，但对于签名版本，我们似乎漏掉了一个数字。这很容易解释：因为我们需要一位来表示符号！

如果我们在64位系统上运行，我们很容易计算出long格式可以达到一个![2^{64-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f166f2aab3571e8eb2a399e728090456_l3.svg)值，对应unsigned数据类型的是18,446,744,073,709,551,615，signed数据类型的范围是-9,223,372,036,854,775,807到9,223,372,036,854,775,807。

为了完整起见，我们将对 long long integer 数据类型进行一些探讨。long long 格式在 C 上不可用，但仅在 C99 版本上可用。它的内存容量是long数据类型的两倍，但显然它不受需要以前 C 标准的编译器的支持。使用long long![2^{64-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f166f2aab3571e8eb2a399e728090456_l3.svg)数据类型，如果我们在 32 位或更高版本的机器上运行它，我们可以达到巨大。

### 3.2. 爪哇

谈到 Java，我们必须记住它是通过虚拟机工作的。这使我们免于为 C 语言解释的所有可变性。

Java 仅支持整数的签名版本。他们是：

-   字节(8 位)
-   短(16 位)
-   整数(32 位)
-   长(64 位)

因此，使用长整数格式，我们可以 ![2^{64-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f166f2aab3571e8eb2a399e728090456_l3.svg) 在 64 位机器上使用 C 来实现，但这次是在所有机器架构上。

然而，由于char格式，通过一些位操作，我们可以获得未签名的版本。那是16位格式，所以无符号整数格式可以达到65535。

但是对于 Java，我们可以更进一步，使用一些小技巧，这样我们就可以通过BigInteger类库表示非常大的整数。这个库结合了较小变量的数组来构建巨大的数字。唯一的限制是物理内存，所以我们可以表示一个巨大但仍然有限的整数范围。

例如，使用 1 KB 的内存，我们可以达到长达 2,466 位的整数！

### 3.3. Python

Python 直接支持任意精度整数，也称为无限精度整数或 bignums，作为顶级构造。

这意味着，与 Java BigInteger一样，我们使用任意大整数所需的内存。因此，就编程语言而言，这个问题根本不适用于 Python，因为纯整数类型在理论上是无限的。

不无界的是当前解释器的字长，大部分情况下和机器的字长一样。该信息在 Python 中以sys.maxsize的形式提供，它是最大可能列表或内存序列的大小，对应于带符号字可表示的最大值。

在 64 位机器上，它对应于![2^{64-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f166f2aab3571e8eb2a399e728090456_l3.svg)= 9,223,372,036,854,775,807。

## 4.流程图

让我们看一个流程图来总结我们到目前为止所看到的内容：

![大整数3](https://www.baeldung.com/wp-content/uploads/sites/4/2020/07/BigInteger3.png)

## 5. 每种语言的真实代码

这里有一些真实的代码可以直接检查我们到目前为止所讨论的内容。该代码将演示每种语言中整数的最大值和最小值。

由于 Python 中不存在这些值，因此代码显示了如何显示当前解释器的字长。

### 5.1. C代码

```c
#include<bits/stdc++.h>
int main() { 
    printf("%dn", INT_MAX); 
    printf("%d", INT_MIN); 
    return 0; 
}
```

### 5.2. Java代码

```java
public class Test {
    public static void main(String[] args) {
       System.out.println(Integer.MIN_VALUE);
       System.out.println(Integer.MAX_VALUE);
    }
}
```

### 5.3. Python代码

```python
import platform
platform.architecture()
import sys
sys.maxsize
```

## 六，总结

在本文中，我们介绍了这三种顶级语言在最大可能整数方面的差异。我们还展示了这个问题在某些情况下如何不适用。

但是，应该始终根据自己的情况使用最合适的，以防止内存消耗和系统延迟。