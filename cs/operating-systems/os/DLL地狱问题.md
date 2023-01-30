## 1. 概述

在本教程中，我们将介绍 DLL 地狱问题及其可能的解决方案。

## 2. DLL地狱问题

DLL(动态链接库)地狱问题是[动态链接](https://www.baeldung.com/cs/static-dynamic-linking-differences)的主要弱点之一。

当[操作系统](https://www.baeldung.com/cs/os-basic-services)加载的 DLL与我们的应用程序期望的版本不同时，就会出现此问题。结果，我们得到[unresolved symbols](https://www.baeldung.com/cs/how-compilers-work)。例如，如果某些函数在 DLL 的较新版本中具有不同的签名，就会发生这种情况。

假设我们的应用程序![应用_{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c2eb6bcc07f0abd6ad7b32d464e376c5_l3.svg)使用版本 1.o 0f 的 DLL 库![库_{DLL}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3d8f9e7af797b3188a3428e2340f0541_l3.svg)：

![DLL 地狱工作](https://www.baeldung.com/wp-content/uploads/sites/4/2022/11/DLL-HELL-WORKING.jpg)

现在，假设我们将此 DLL 更新为 1.1 版。所以，在新版本中，一些功能发生了变化，但我们的应用程序![APP_{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27947ec7cb57b5c44be7a30db247a266_l3.svg)仍然需要旧的 API。结果，![APP_{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27947ec7cb57b5c44be7a30db247a266_l3.svg)将在运行时崩溃：

![DLL 地狱崩溃](https://www.baeldung.com/wp-content/uploads/sites/4/2022/11/DLL-HELL-CRASH.jpg)

原因是我们没有内置机制来检查在使用 DLL 时的向后[兼容性。](https://www.baeldung.com/cs/apis-vs-abis)因此，即使对 DLL 进行微小的更改也可能导致问题。

解决这个问题最明显的方法是转向静态链接。这样，我们消除了 DLL，但失去了我们从不同应用程序之间的运行时库共享中获得的所有[资源优化。](https://www.baeldung.com/cs/optimization-terminology)

### 2.1. Windows 中的 DLL 地狱问题

DLL 地狱问题更具体地针对基于 Windows 的软件系统，因为它们通常具有不兼容的支持库。

在 Windows 系统中，应用程序通常会全局安装流行的 DLL。但是，每个可能需要同一 DLL 的不同版本。

如今，此问题仅限于 .NET 系统并且很少发生。在 .NET 系统中，加载程序足够智能，可以通过解码版本号并从 Windows 注册表中进行简单的表查找来选择库的正确程序集版本。

### 2.2. Linux 中的 DLL 地狱问题

我们在大多数基于 Linux 或 Unix 的系统中都没有发现 DLL Hell 问题。

大多数 Linux 发行版都有一个包管理器(例如 Debian 的apt)。包管理器安装新软件所需的所有依赖项。因此，我们不必为要使用的应用程序手动安装每个依赖项。 

此外，大多数 Unix 应用程序使用共享库。每个共享库都是一个单独的包，具有明确定义的版本。由于 Linux 应用程序通常使用显式共享库名称和版本来声明其依赖性，因此系统知道要使用哪个包。例如，软件X可以有诸如“ X依赖共享库Y(版本 >=6.0)”之类的依赖语句。这会自动选择正确版本的Y。

## 3.解决方案

我们将展示解决此问题的两种方法。

### 3.1. .NET 中的并行版本控制

假设两个应用程序![APP_{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27947ec7cb57b5c44be7a30db247a266_l3.svg)和![APP_{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fea5a5ef5bb8be12e8b494178531be88_l3.svg)共享![dll_{共享}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5172e3b878f686ef7a86db144179483e_l3.svg)其版本为 1.0.0.0。现在，我们更新![dll_{共享}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5172e3b878f686ef7a86db144179483e_l3.svg)到一个新的版本2.0.0.0，相应的变化在![APP_{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27947ec7cb57b5c44be7a30db247a266_l3.svg)但不是在![APP_{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fea5a5ef5bb8be12e8b494178531be88_l3.svg). 所以，![APP_{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fea5a5ef5bb8be12e8b494178531be88_l3.svg)会崩溃。

我们使用并行版本控制解决了 .NET 系统中的问题。在这里，每个库都遵循标准版本控制系统![WXYZ](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f153164cc99dbb31d2596cd319f00cb1_l3.svg)：

-   ![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-183777ab9133546b80b6f342c6ec9919_l3.svg)表示主数
-   ![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-996ff7036e644e89f8ac379fa58d0cf7_l3.svg)表示小数
-   ![是](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-42ae22abcaa05c2d6c2fdc3746446019_l3.svg)表示修改后的数字
-   ![从](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0be116875001706f29a24434bd0d91c9_l3.svg)表示新的版本号

操作系统将每个共享库的程序集放在我们称为全局程序集缓存 (GAC) 的中央位置。此外，GAC 中的每个程序集条目都有四个字段：

1.  [汇编](https://www.baeldung.com/cs/assembly-language)代码的名称
2.  版本号
3.  文化(例如，语言代码，如“en”或“de”)
4.  [公钥令牌](https://www.baeldung.com/cs/tokens-vs-sessions)(用于解密图书馆的某些功能)

现在，对于上面的示例，我们将![dll_{共享}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5172e3b878f686ef7a86db144179483e_l3.svg)在 GAC 中有 2 个版本。该应用程序![APP_{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-27947ec7cb57b5c44be7a30db247a266_l3.svg)将![dll_{共享}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5172e3b878f686ef7a86db144179483e_l3.svg)与版本 1.0.0.0 一起![APP_{2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fea5a5ef5bb8be12e8b494178531be88_l3.svg)使用，并将![dll_{共享}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5172e3b878f686ef7a86db144179483e_l3.svg)与版本 2.0.0.0 一起使用。因此，我们在运行时从 GAC 加载不同版本的共享库程序集。

但是，只有当 DLL 没有共享依赖项时，才能使用并行版本控制。

### 3.2. 使应用程序可移植

我们还可以使我们的应用程序可移植。这样，我们的程序将拥有它所需的任何 DLL 的私有副本。这些私有 DLL 不共享任何组件，因此我们可以将它们作为独立的软件加载。此外，由于操作系统在任何系统内存位置之前搜索应用程序的可执行目录，因此这种方法工作顺利。

但是，它使我们的应用程序容易受到病毒注入的攻击。黑客可以轻松破坏我们的库副本并导致我们的程序崩溃或利用敏感数据。因此，如果我们不使用安全补丁使我们的私有 DLL 保持最新，那么这种增加的灵活性是以安全为代价的。

## 4。总结

在本文中，我们探讨了 DLL 地狱问题及其原因。我们也列举了一些解决方案。我们可以通过采用并行版本控制最有效地解决 DLL 地狱问题。在那里，我们将不同版本的库存储在一个中央位置，以便每个应用程序都可以准确地获取它需要的版本。