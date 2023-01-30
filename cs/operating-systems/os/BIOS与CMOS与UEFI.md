## 1. 概述

[在本教程中，我们将解释BIOS](https://en.wikipedia.org/wiki/BIOS)、 [CMOS](https://en.wikipedia.org/wiki/CMOS)和[UEFI](https://en.wikipedia.org/wiki/Unified_Extensible_Firmware_Interface)之间 的 区别 。

BIOS(基本输入/输出系统)和 CMOS(互补金属氧化物半导体)之间存在显着差异。通常，术语 BIOS 和 CMOS 可以互换使用。[主板中的](https://en.wikipedia.org/wiki/Motherboard)BIOS 芯片 包含启动计算机的程序([固件](https://en.wikipedia.org/wiki/Firmware) 通常称为 BIOS)。CMOS 芯片存储日期和时间、风扇速度、启动顺序等设置。 

[在Windows 操作系统](https://en.wikipedia.org/wiki/Microsoft_Windows)的早期 ， [DOS](https://en.wikipedia.org/wiki/DOS)命令 WIN 是启动 Windows 所必需的。UEFI(统一可扩展固件接口) 中采用新接口形式的类似概念，以扩展更好的用户体验并提供新的安全 BIOS。

## 2. 差异

UEFI 相对于 BIOS 有几个优点，如下表所示：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a7d2711ce9a17030235f9f81e122d769_l3.svg)

下图是BIOS和UEFI在开机过程中的区别：

![BIOS 与 UEFI](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/BIOS-vs-UEFI-768x771-1.png)

[引导加载程序是硬件和操作系统](https://www.tutorialspoint.com/operating_system/os_overview.htm#:~:text=An Operating System (OS) is,as disk drives and printers.)(OS)之间的接口。UEFI 的优势之一是使用安全引导加载程序的安全引导过程。此功能可防止恶意访问，允许在启动过程中进行远程监视和控制。

## 3.BIOS _

当我们第一次打开计算机时，BIOS 会运行开机自检 ( [POST )：](https://whatis.techtarget.com/definition/POST-Power-On-Self-Test#:~:text=When power is turned on,other hardware are working correctly.)

![Windows 启动屏幕 bios](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/windows-boot-screen-bios.jpg)

它测试计算机硬件是否正常工作。POST运行成功，硬件准备就绪后，调用存储在硬盘等存储设备中的代码，最后运行[Bootloader](https://www.ionos.com/digitalguide/server/configuration/what-is-a-bootloader/#:~:text=A bootloader%2C also known as,DVD or a USB stick.) 启动操作系统：

![BIOS引导步骤1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/BIOS-Booting-steps1-1024x362.png)

一旦固件指示引导加载程序启动，它首先加载主内存，然后加载操作系统的 [内核](https://www.baeldung.com/cs/os-kernel)。计算机内核是计算机系统的主要元素，它管理所有功能，例如存储、权限和驱动程序。成功完成分配的作业后，引导加载程序将系统控制权返回给内核。

 [BIOS 芯片](https://www.informit.com/articles/article.aspx?p=332850&seqNum=3)中的程序 是非易失性的。非易失性意味着即使计算机关闭，程序也不会被删除：

![芯片2](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/Bios-chip-2.png)

通常BIOS有三种模式：

-   引导——启动操作系统
-   设置——配置固件
-   更新——更新固件版本

## 4、CMOS

计算机需要很少的设置，例如日期、时间、启动顺序以及设备和外围设备的自定义设置。这些设置保留在[CMOS 芯片](https://www.easytechjunkie.com/what-is-cmos.htm)中并且是易失的：

![CMOS设置](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/CMOS-setup.png)

CMOS芯片也被称为RTC([实时时钟](https://whatis.techtarget.com/definition/real-time-clock-RTC))、NVRAM([非易失性RAM](https://www.techopedia.com/definition/2794/non-volatile-random-access-memory-nvram#:~:text=Non-Volatile Random Access Memory (NVRAM) is a category,CMOS battery on the motherboard.))或[CMOS RAM](https://sourcedaddy.com/aplus/cmos-ram.html)：

![CMOS芯片](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/cmos-chip-768x425-1.jpg)

[CMOS 芯片需要来自CMOS 电池](https://www.hp.com/us-en/shop/tech-takes/what-is-cmos-battery-how-to-remove-and-replace)的电源 来保存自定义设置。如果从主板上拔下电池，CMOS 会将设置重置为 BIOS 芯片中存储的默认设置：

![CMOS电池](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/CMOS-battery-768x773-1.png)

大多数 [主板](https://www.easytechjunkie.com/what-is-a-motherboard.htm)手册都提供了可用 CMOS 选项的完整列表。这些根据制造商的 BIOS 设计规范而有所不同。

以下是电池 CMOS 供电下降的一些常见症状： 

-   显示旧日期而不是当前日期和时间的日期和时间
-   鼠标和键盘等外围设备行为异常
-   司机不见了
-   没有网络连接

## 5.UEFI

英特尔 [宣布](https://fossbytes.com/intel-end-legacy-bios-support-2020-uefi/#:~:text=Intel has announced that it's,legacy support of CSM compatibility.)从 2020 年起不再支持传统 BIOS，这导致主板制造商更快地采用 UEFI。UEFI 提供轻量级操作系统并连接计算机硬件。UEFI 是运行固件程序的虚拟平台：

![安全启动](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/secure-boot-1024x555-1.jpg)

在引导过程中，UEFI 执行以下操作：

-   充当引导代理
-   提供预操作系统命令行界面([EFI Shell](https://superuser.com/questions/592854/what-is-the-efi-shell))

![UEFI基本流程](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/UEFI-basic-process.png)

大多数新主板都内置了 UEFI，带有用户友好的 [图形用户界面](https://us.informatiweb.net/tutorials/it/bios/change-the-hard-disk-controller-mode.html)，支持不同的颜色甚至动画：

![在 UEFI 中](https://www.baeldung.com/wp-content/uploads/sites/4/2021/05/asus-UEFI.jpg)

UEFI 在平台固件中具有丰富的新功能，一组增强的策略和规范。

固件提供一组初始命令，以便在计算机开启时运行。固件是连接到主板的非易失性存储器。有时，视频卡和存储控制器等外围设备可以使用内置固件。与 BIOS 相比，UEFI 多了一种称为“用户模式”的模式。在用户模式下，EFI shell 运行固件命令和其他实用程序。它还从 64 位 UEFI 平台启动 64 位 Windows。 

 在引导过程中， UEFI 固件 仅在 [实模式下初始化平台。](https://wiki.osdev.org/Real_Mode)平台初始化后，UEFI 使用基本操作系统为后续启动启用 64 位[保护模式](https://www.techopedia.com/definition/1381/protected-mode#:~:text=Protected mode is an operational,as protected virtual address mode.) 。

安全启动过程的所有者(平台供应商/OEM/企业)拥有[数字签名的密钥对](https://www.baeldung.com/cs/authentication-vs-authorization)。私钥对所有者是秘密的。公钥用于公开分发以进行安全访问。所有者负责在位于 UEFI 主板中的第一阶段引导过程中对固件文件(图像)进行数字签名。

对于 UEFI 支持的 Windows 安全启动，以下是限制：

-   启用安全启动的 UEFI v2.3.1
-   仅 64 位 Windows 8 或更高版本支持 UEFI

安全引导允许数字签名的驱动程序加载并阻止任何数字未签名的驱动程序加载。此外，UEFI 可以运行预操作系统实用程序，如网络和运行用于硬件测试诊断的驱动程序。

## 六，总结

本文概述了 BIOS、CMOS、UEFI 以及它们之间的区别。

BIOS 允许运行多个操作系统，而无需更改构成安全威胁的设置。在基于 UEFI 的系统中，固件实用程序作为系统维护工具的一部分预先安装。此工具提供安全引导以防止加载未数字签名的驱动程序。 

但是，UEFI 还为基于文本的用户界面提供了传统模式。此功能对于习惯基于文本的用户界面的用户非常有用，也可以满足安装[Linux 发行版](https://en.wikipedia.org/wiki/Linux_distribution)等特殊需求。