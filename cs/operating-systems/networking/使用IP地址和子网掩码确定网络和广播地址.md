## 1. 概述

IP 地址是主机地址和网络地址的组合。除了 IP 地址，我们还需要子网掩码来获取特定网络的确切网络地址和不同的逻辑地址。

[子网划分是有类 IP 寻址](https://en.wikipedia.org/wiki/Classful_network)使用的最重要的方法之一，它可以避免大量 IP 地址被浪费。

在本教程中，我们将详细讨论 IP 地址、广播地址、网络地址和子网掩码。我们还将通过数字示例展示并解释如何使用 IP 地址和子网掩码来确定网络和广播地址。

## 2.IP地址

术语 IP 地址指的是 Internet 协议地址。为了将数据从一台计算机传输到另一台计算机，我们需要两个设备的地址或标识。然而，有不同的通信方式，但通常情况下，彼此相距较远的计算机使用互联网和 IP 地址进行通信。

此外，它是唯一分配给连接到网络的每个设备的地址。因此，我们使用 IP 地址来标识网络中的特定设备以进行通信。此外，它也称为逻辑地址。IP 地址分为两类：全局 IP 地址和本地 IP 地址。

连接到互联网的每台设备都可以访问全球 IP 地址。相反，无法从该网络访问本地 IP 地址。此外，网络路由器可以手动或动态地将其分配给主机设备。

现在，根据寻址方案，我们可以将 IP 地址分为两类：[IPv4 和 IPv6](https://www.baeldung.com/cs/ipv4-vs-ipv6)。IPv4 的![32](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8304649ce156ac9f7daee9a539530a52_l3.svg)地址有点长并且有![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)八位字节。此外，IP 地址的范围可以从![mathsf{0.0.0.0}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06797b480c3ef830ced1cf0144ce992e_l3.svg)到![mathsf{255.255.255.255}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2bf7fa0cb77c9d0e09eac3e33e50ce81_l3.svg)：

![ipv4_结构](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/ipv4_structure.drawio.png)

在 IPv4 中，有两种寻址方式：有类和无类。有类 IP 寻址进一步分为![数学{5}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1aa18814edfae60c751c8687b458ffc7_l3.svg)类：A、B、C、D 和 E。此外，我们可以使用二进制形式表示法的前几位来感知 IP 地址的类。

此外，我们可以根据需要将 IP 地址分为几类。具体来说，A 类是为大型组织设计的。此外，我们通常将 B、C、D、E 类用于中型组织、小型组织、[多播](https://www.baeldung.com/cs/multicast-vs-broadcast-anycast-unicast)和实验用途：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66cab54d0895af2f76a02c23c74e6925_l3.svg)

此外，随着我们将更多设备连接到互联网或网络，可能会出现我们没有任何免费的唯一 IP 地址可分配给新设备的情况。因此，为了克服这种情况，引入了 IPv6。

IPv6 是 Internet 协议的最新版本。而且，我们用[十六进制格式](https://en.wikipedia.org/wiki/Hexadecimal)表示。这个最新版本由 Internet 工程任务组于 1994 年开发，以满足 Internet 用户和设备的爆炸性需求。

此外，IPv6 使用 128 位寻址方案并提供许多 IPv4 中不存在的功能。接下来，让我们看一下 IPv6 地址：![FDEC:BS68:7654:3210:ADFF:QBGF:2522:FIIF](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7c9b32ac52a3b42df59dc34f541a442d_l3.svg)

IPv6 中的一些特性包括内置安全性、范围地址、自动配置、[服务质量 (QoS)](https://en.wikipedia.org/wiki/Quality_of_service)、新报头格式和更大的地址空间。最重要的是，自 2000 年以来发布的大多数操作系统都直接或间接支持 IPv6。

## 3. 网络地址

我们知道 IP 地址结合了网络地址和广播地址。因此，IP地址类别决定了网络ID，通常称为网络地址。

一般情况下，我们对![数学{8}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1d6c83feb1d557e7981d6c9dc2b7bb3a_l3.svg)A类的网络地址使用IP地址的首位。同样，我们对B类和C类的网络地址指定首位![16](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c33a5122bad511e3ec324cd866a0a4dc_l3.svg)和![32](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8304649ce156ac9f7daee9a539530a52_l3.svg)位。

此外，由于 A 类使用![8](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e4888e98f77eb93ff65bfecac28d3c5e_l3.svg)IP 地址位作为网络地址，因此可以有最大![127(2^7)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e6a669380249a524c3b8f49305ef1ebc_l3.svg)可能的网络地址。同样，B 类使用![16](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c33a5122bad511e3ec324cd866a0a4dc_l3.svg)位，但第一个![2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8c267d62c3d7048247917e13baec69a5_l3.svg)位始终是![10](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f2dd7a07a97336ce3d17ca56d2618366_l3.svg)。因此，我们使用剩余的位来分配网络地址。剩下的![14](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6ebb06c89a650afa3e44d6610e6f94e8_l3.svg)位可以是![2^{14}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-393dea28e4c9392ad63ced3a3ffed3db_l3.svg)网络地址。

同样，C 类使用![24](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ea21a2786d7192fcae8d7000ad272902_l3.svg)位，但![3](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ce2009a45822333037922ccca0872a55_l3.svg)位始终是![110](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9e3e6912bc09e8cdabed23e91cc6c9d5_l3.svg). 因此，我们使用剩余的![21](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e464e87734fae9baedeadbf3f61e70bf_l3.svg)位来分配网络地址，这意味着可以有![2^{21}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-867f0b5942f987320c030d91682b46e3_l3.svg)网络地址：

![网络地址](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/Network-address.drawio.png)

## 4.广播地址

在任何网络中，当我们需要向连接到网络的主机设备发送数据时，路由器都会使用广播地址。此外，每个网络都有一个唯一的广播地址。

向连接到网络的每个设备或节点发送数据的过程称为[广播](https://www.baeldung.com/cs/multicast-vs-broadcast-anycast-unicast)。而且，广播只有一个源，其他主机设备是目的地。

接下来，我们可以简单地将网络地址的最后几位替换为![数学{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-277511c02b56b209330acf2e78fd3290_l3.svg)，这将变成一个广播地址。因此，我们可以使用子网掩码和该网络的 IP 地址来确定任何网络的广播地址。

让我们举个例子。让我们以 C 类 IP 地址为例：![192.168.1.0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-709f792035ff48063ad51ef41f394b86_l3.svg). 这是一个网络地址。因此，给定网络地址的广播地址将是![192.168.1.255](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1edbc6474d8027f1949d87217e4ce21d_l3.svg)。

## 5. 子网掩码

子网掩码用于称为[子网划分](https://www.baeldung.com/cs/ipv4-subnets)的过程，在该过程中，大型网络被划分为较小的网络。更具体地说，这就像将一个网络划分为几个连续的网络组，每个组称为一个子网。此外，实施此过程是为了减少因分类寻址而造成的 IP 地址浪费。

子网掩码用于确定网络地址和主机地址：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9a677c456dd182ae15c4fbf466c9c6d6_l3.svg)

## 6. 确定网络和广播地址

让我们举一个使用子网掩码计算网络和广播地址的例子。现在，为了计算网络地址和广播地址，我们需要两条信息：设备的 IP 地址和网络的子网掩码。

假设 IP 地址为![192.168.2.4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-85db8a961bdeefdd40db61c97b258404_l3.svg)，子网掩码为![255.255.255.240](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b4a66350674880959e2cdd733cfba4a3_l3.svg)。

第一步是以二进制形式记下 IP 地址和子网掩码：

 ![[IP hspace{0.1cm} 地址 = 192.168.2.4 = 1100 0000. 1010 1000. 0000 0010. 0000 0100]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6bd4025fa01cdcd09aef8719f08c479f_l3.svg)

 ![[子网 hspace{0.1cm} 掩码 = 255.255.255.240 = 1111 1111 。 1111 1111. 1111 1111. 1111 0000]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e49584728a8a5fa3b6f92d5159637aef_l3.svg)

子网掩码中的位![matfsf{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2c2f8737c7cbf08d030fa7591bc8cea3_l3.svg)表示网络地址。同样，![matfsf{0}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9f4764a6f6a6601be02b196ae849fdaa_l3.svg)将用于广播地址的位：

![子网掩码和 ip 地址示例](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/Subnetmask_and_ip_addr_example.drawio.png)

 

在此示例中，我们可以看到![4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d4d95642629f734574671d47307d46c3_l3.svg)子网掩码的最后一位是![matfsf{0}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9f4764a6f6a6601be02b196ae849fdaa_l3.svg). 因此，这意味着我们可以连接![14](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6ebb06c89a650afa3e44d6610e6f94e8_l3.svg)设备![(192.168.2.1 - 192.168.2.14)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-7c5af2562aad09e863508be55680cde3_l3.svg)并且广播 IP 地址将为![192.168.2.15](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-264c7743a43c6d6d8fb67007427fb159_l3.svg). 因此，网络地址将是![192.168.2.0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-13b326419b32bb0261d419ce20fdd70e_l3.svg)：

![网络主机 ID 子网掩码](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/Network_host_ids_subnet_mask.drawio.png)

因此，我们也可以改写上面的IP地址：![192.168.2.4/28](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ac939f4b0a0a5de3bc009fde256aabfd_l3.svg)。这里，![28](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-39897b0c77cde4b6ccf9718b9f45f186_l3.svg)指的是子网掩码的28位，即![matfsf{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2c2f8737c7cbf08d030fa7591bc8cea3_l3.svg).

关于我们如何从子网掩码中获取网络地址可能存在一些混淆。从上面的示例中可以清楚地看出，![matfsf{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2c2f8737c7cbf08d030fa7591bc8cea3_l3.svg)子网中的位代表网络地址。

而且IP地址![192.168.2.4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-85db8a961bdeefdd40db61c97b258404_l3.svg)属于C类。因此，我们知道在C类![24](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ea21a2786d7192fcae8d7000ad272902_l3.svg)中，IP地址的前几位用于网络地址，其余的用于主机地址。相反，在子网掩码中存在网络地址可以超过![24](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ea21a2786d7192fcae8d7000ad272902_l3.svg)位的情况。

一般来说，![24](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ea21a2786d7192fcae8d7000ad272902_l3.svg)网络地址和主机地址的第一位的划分是C类IP地址的默认配置。因此，我们可以通过更改![matfsf{1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-2c2f8737c7cbf08d030fa7591bc8cea3_l3.svg)子网掩码中的数字来更改它。此外，我们知道 C 类的默认子网掩码是![255.255.255.0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b2b9268e56b577459922db39c65d6a25_l3.svg).

因此，例如，我们可以更改子网掩码的某些位：

 ![[IP hspace{0.1cm} 地址 = 192.168.2.4 = 1100 0000. 1010 1000. 0000 0010. 0000 0100]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6bd4025fa01cdcd09aef8719f08c479f_l3.svg)

 ![[子网 hspace{0.1cm} 掩码 = 255.255.255.240 = 1111 1111 。 1111 1111。1111 1111。 1111 0000]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-0d3f2b4f8f108fd9e2e4fa6adb047d13_l3.svg)

让我们稍微改变一下子网掩码：

 ![[子网 hspace{0.1cm} 掩码 = 255.255.255.224 = 1111 1111.1111 1111.1111 1111.111 mathbf{0} 0000]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ffcd80883a3bd16ace260a9c55b172cc_l3.svg)

因此，具有此子网掩码的 IP 地址将位于不同的网络中。这就是我们可以使用子网掩码划分网络的方式。因此，现在我们可以将 IP 地址重写为![192.168.2.4/27](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1eac431de554da8a03a95c0b3e17870d_l3.svg).

## 七、总结

在本教程中，我们彻底讨论了 IP 地址、广播地址、网络地址和子网掩码。我们还提供了数值示例，解释如何使用 IP 地址和子网掩码确定网络和广播地址。