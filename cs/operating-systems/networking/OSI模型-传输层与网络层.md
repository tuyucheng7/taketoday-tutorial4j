## 1. 概述

传输层和网络层是 OSI 模型中最重要的两个部分。

在本教程中，我们将探索这两层提供的基础知识和服务。

最后，我们将强调它们之间的核心区别。

## 2. 传输层介绍

传输层是[OSI 模型的第四层：](https://www.baeldung.com/cs/osi-model)

![图层轴](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/osi_layer.jpg)

它是一个端到端或进程到进程的通信层，负责传递整个消息。

进程是在主机系统上运行的应用程序。主机上可能运行着多个进程。传输层确保主机进程与另一主机进程之间的通信。

对于处理通信的进程，传输层使用唯一分配给每个进程的[端口号。](https://www.baeldung.com/cs/port-vs-socket)

让我们举一个明信片递送的真实例子。有两栋房子，Home-A 和 Home-B。Jack 属于 Home-A，Jones 属于 Home-B。杰克想给琼斯寄一张明信片。因此，为了将明信片投递到正确的地址，他需要在明信片上写明地址、收件人姓名和明信片类型等详细信息。

在此示例中，Home-A 和 Home-B 是两个不同的网络。Jack 和 Jones 是两台主机的 IP 地址。明信片的类型代表不同类型的流程：

![例子](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/example.jpg)

重要的是要注意端口的使用在传输层中至关重要。当然，在特定时间可以在单个主机上运行多种类型的进程。通常，单个 IP 地址分配给主机。发送方主机使用端口号将数据发送到接收方主机。此外，接收主机可以识别端口并将数据路由到特定进程。

## 3.传输层服务

传输层提供了很多服务：

![1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/1.jpg)

该层负责传递消息，不仅从一个主机到另一个主机，而且从一个主机的特定进程到另一个主机的特定进程。

此外，它还通过提供按顺序传送要处理的数据包、不丢失数据和控制数据等服务来提供可靠性。

另外，传输层负责数据传输的流量控制。此处，接收方主机控制要从发送方主机发送的数据量。这用于防止接收主机上的数据开销，因为接收主机可能无法以相同的速率处理数据。[传输层使用Selective Repeat](https://en.wikipedia.org/wiki/Selective_Repeat_ARQ)提供流量控制机制：

![FC](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/FC.jpg)

[该层使用校验和位](https://en.wikipedia.org/wiki/Checksum)提供错误控制。发送方主机使用某种算法生成校验和。此外，接收主机解码该校验和，以便它可以检测损坏的数据包。在传输的时候，如果有任何噪音，它可以改变数据。因此，传输层使用[自动重复请求](https://en.wikipedia.org/wiki/Automatic_repeat_request)方法重新传输丢失或错误的数据包：

![差错控制](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/Error_control.jpg)

它还通过使用[多路复用](https://en.wikipedia.org/wiki/Multiplexing)促进了网络的有效使用。可能有这样一种情况，来自发送方主机的多个进程需要发送数据包，但一次只有一个传输层。在这一层中，协议接受来自不同进程的数据，根据分配的端口进行区分，并将它们添加到报头中。

在接收方的主机上，为了处理传入的数据，我们需要执行多[路分解](https://en.wikipedia.org/wiki/Multiplexing)。这里，传输层将所有的数据进行区分，根据端口地址发送给各自的进程：

![复用解复用](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/Multiplexing_demultiplexing.jpg)

## 4. 网络层介绍

网络层是 OSI 模型的第三层。它负责跨多个网络从源到目的地或主机到主机的数据包传送。

该层从传输层获取数据，添加其报头，并将其转发到[数据链路层](https://www.baeldung.com/cs/osi-model)。该层确保每个数据包从其起点到达最终目的地。 [这一层使用交换机和路由器](https://www.baeldung.com/cs/routers-vs-switches-vs-access-points)。网络层在网络设备上实现。

[拥塞](https://en.wikipedia.org/wiki/TCP_congestion_control)和[错误控制](https://en.wikipedia.org/wiki/Error_detection_and_correction)也由网络层负责。它向数据包添加校验和以进行错误控制。

它使用目标主机和源主机的逻辑地址(IP 地址)来发送数据。IP 地址是网络地址和主机地址的组合。

## 5. 网络层服务

与传输层一样，网络层也提供一些重要的服务：

![2-1](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/2-1.jpg)

网络层使用逻辑地址在不同网络中的主机之间进行通信。此外，它在每个数据包中添加发送方和接收方的IP地址，以便每个发送的数据包都能到达其指定的目的地。

网络层还提供路由和交换服务。当连接多个独立网络以创建一个更大的网络时，连接设备(路由器和交换机)将数据包从源路由和交换到目的地。

可以有不止一条路由将数据包从源发送到目的地。因此，网络层负责使用[路由协议](https://en.wikipedia.org/wiki/Routing_protocol)选择最佳路由或路径来发送数据包。

网络层创建了一个路由表来维护从主机到目的地的最短路径的细节。我们在切换过程中也使用了这个表。交换是一个使用路由表并将数据包引导到不同网络设备的过程：

![路由切换](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/Routing_switching.jpg)

在这个例子中，我们要从主机 A 向主机 B 发送一个数据包。有两条路径：P1 和 P2。网络层会选择最优路径P1。

网络层提供的另一个重要服务是[分片](https://en.wikipedia.org/wiki/Fragmentation_(computing))。它将数据包分成片段以减少路由上的过载。数据大小可能大于路由器的处理能力。

该层将数据分成小片段并将它们发送到更远的地方。此外，它还负责在接收主机上累积分片数据。而分片数据的堆积过程只发生在目的节点上：

![碎片化](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/Fragmentation.jpg)

## 6. 传输层和网络层的流程

现在让我们通过一个实际的例子来探讨传输层和网络层是如何工作的：

![网络传输层](https://www.baeldung.com/wp-content/uploads/sites/4/2021/07/NetransLayer.jpg)

图中有两个主机设备Host-A、Host-B，分别运行在Network-A和Network-B中。此外，三个进程在每个终端设备上运行。Host-A 的每个进程都想将数据发送到 Host-B 的某些进程。

将数据从一个进程发送到另一个进程是由传输层完成的。为了将数据从一个进程发送到另一个进程，首先我们需要将数据发送到目标主机。为了实现这一点，我们使用网络层，因为两个进程都在不同的网络中。

现在，传输层添加其报头，为每个进程分配一个唯一的端口号，并将其传递给网络层。此外，网络层使用源主机和目标主机的逻辑地址。它将数据路由到其目标主机。这就是网络层被称为源到目的地交付层的原因。它将为从源到目的地的最短路径创建一个路由表。此外，它还切换来自不同网络设备的数据包。

在这个例子中，有两条路径从 Network-A 出去。网络层将决定选择哪条路径发送数据包。

当数据包到达它们的目标主机时，传输层将使用它们的端口号将数据包路由到它们各自的进程。

进程与进程之间的通信称为端到端交付。然而，从主机到主机的通信被称为源到目的地传送。此外，两个网络设备之间的通信被称为跳到跳传送。

## 7. 传输层和网络层的区别

让我们看看传输层和网络层的根本区别：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1f49e45973fe29e528015d1b5fe8fcb2_l3.svg)

## 八、总结

在本教程中，我们讨论了 OSI 模型中的两个重要层：传输层和网络层。我们还解释了这两层提供的服务以及它们之间的核心区别。