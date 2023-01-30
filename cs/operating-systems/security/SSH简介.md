## 1. 概述

[Secure Shell (SSH)](https://www.ssh.com/ssh/)是一种流行的网络协议，它允许我们通过不安全的网络(如 Internet)访问远程计算机。

在本教程中，我们将深入研究它并探索它的各个方面。

## 2. 什么是安全外壳？

Secure Shell 或 Secure Socket Shell 是一种网络协议。它是[开放系统互连](https://www.geeksforgeeks.org/layers-of-osi-model/)(OSI)网络模型的第 7 层以后的应用层协议。它还指的是实现 SSH 协议的实用程序套件。

Secure Shell 还支持基于密码和密钥的身份验证。基于密码的身份验证让用户提供用户名和密码以向远程服务器进行身份验证。基于密钥的[身份验证](https://www.digitalocean.com/community/tutorials/how-to-configure-ssh-key-based-authentication-on-a-linux-server)允许用户通过密钥对进行身份验证。密钥对是两个加密安全密钥，用于向 Secure Shell 服务器验证客户端。

此外，Secure Shell 协议还对两台计算机之间的数据通信进行加密。它广泛用于通过 Internet 与远程计算机进行通信。

## 3. 安全外壳架构

![SSH 1](https://www.baeldung.com/wp-content/uploads/sites/4/2020/08/SSH-1.png)

Secure Shell 具有客户端-服务器体系结构。通常，服务器管理员会安装接受或拒绝传入连接的服务器程序。此外，用户在他们的系统上运行一个客户端程序来请求服务器。默认情况下，服务器侦听 HTTP 端口 22。

## 4.我们应该在哪里使用Secure Shell？

在大多数情况下，安全 Shell 应用程序在默认情况下可用于所有操作系统服务器。SSH 连接用于多种用途。例如，以下是一些用法：

-   本地和远程机器之间的通信
-   远程访问服务器资源
-   在远程主机中执行命令
-   执行大量系统管理任务

此外，基于密钥的身份验证提供了跨远程主机的便捷单点登录 (SSO) 访问。这使用户无需密码即可在其帐户之间移动。

## 5. 安全 Shell 命令

Secure Shell 提供了几个具有附加功能的可执行命令：

-   ssh – 用于登录到远程机器并在远程机器上执行命令
-   sshd——它是一个 SSH 服务器守护进程，等待来自 SSH 客户端的传入 SSH 连接请求，并使授权系统能够连接到本地主机
-   ssh-keygen – SSH 的新身份验证密钥对，用于自动登录以实现单点登录和验证主机
-   ssh-copy-id – 允许在服务器上、安装和配置 SSH 密钥
-   scp – RCP 协议的 SSH 安全版本，让我们可以将文件从一台机器到另一台机器
-   sftp – FTP 协议的 SSH 安全版本，用于在 Internet 上共享文件

## 6. 我们如何使用 Secure Shell？

使用 Secure Shell 最常见的方法是使用ssh命令登录到远程计算机：

```
ssh admin@server.example.com
```

在上面的命令中，我们使用ssh可执行文件以管理员用户连接到server.example.com服务器。命令格式为user@servername。user是服务器用户，servername是服务器的名称。此外，我们还可以使用 IP 地址代替[DNS](https://www.cloudflare.com/learning/dns/what-is-dns/)或服务器名称。例如，命令root@10.1.1.2让用户root登录到服务器10.1.1.2。

### 6.1. 创建密钥对

我们还可以使用 SSH 在你的机器中生成私钥和公钥对：

```
ssh-keygen -t rsa
```

我们使用ssh-keygen命令来创建私钥-公钥对。公钥与远程计算机共享，私钥为安全保密。

### 6.2. 文件

我们还可以使用 Secure Shell 协议使用 SCP 命令将文件从一台机器到另一台机器：

```
scp <em>fileName</em> <em>user@remotehost:destinationPath</em>
```

在上面的命令中，fileName是要到宿主机当前目录下的文件。命令的其余部分表示用户和服务器详细信息以及远程计算机上的目标路径。

## 7. 安全 Shell 隧道

安全 Shell 隧道是一种使用户能够在本地和远程主机之间打开安全隧道的技术。

其主要目的是将网络流量重定向到特定端口或 IP 地址。这允许本地计算机上的应用程序直接访问远程主机。目的地可能在远程 SSH 服务器上，或者该服务器可能被配置为转发到另一个远程主机。

## 八、总结

在本教程中，我们提供了安全 Shell 协议的概述。首先，我们讨论了它是什么以及架构。然后我们讨论了如何使用它以及它最适合的领域。

最后，我们讨论了常用命令并讨论了一个有用的功能——安全 Shell 隧道。