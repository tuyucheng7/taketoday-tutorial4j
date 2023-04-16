## 1. 概述

WildFly 为服务器管理提供了不同的方法。最熟悉的方法是使用其 Web 界面，但我们可以使用 CLI 或 XML 脚本。

在本教程中，我们将专注于访问管理 Web 界面。

我们假设读者已经了解标准的 [WildFly 设置](https://www.baeldung.com/wildfly-server-setup)过程。 

## 2.远程访问

Web 界面或控制台是一个 GWT 应用程序，它使用WildFly 的 HTTP 管理 API 来配置独立服务器或域管理服务器。此 API 服务于两种不同的上下文：

-   网页界面：[http://:9990/console](http://:9990/management)
-   管理操作：http://%3Chost%3E:9990/management

默认情况下，Web 控制台只能从 localhost 访问。也就是说，我们的配置文件只包含与 Web 控制台交互的本地接口。

用 WildFly 的术语来说，接口由具有选择标准的网络接口组成。在大多数情况下，选择标准是绑定到接口的 IP 地址。本地接口声明如下：

```xml
<interface name="management">
    <inet-address value="${jboss.bind.address.management:127.0.0.1}"/>
</interface>
<!--127.0.0.1 is the localhost IP address. -->复制
```

结果，这个管理本地连接到 socket listener management-http从端口9000接收 web 控制台的连接：

```xml
<socket-binding-group name="standard-sockets" default-interface="public" 
  port-offset="${jboss.socket.binding.port-offset:0}">
    <socket-binding name="ajp" port="${jboss.ajp.port:8009}"/>
    <socket-binding name="http" port="${jboss.http.port:8080}"/>
    <socket-binding name="https" port="${jboss.https.port:8443}"/>
    <socket-binding name="management-http" interface="management" 
      port="${jboss.management.http.port:9990}"/>
    <socket-binding name="management-https" interface="management" 
      port="${jboss.management.https.port:9993}"/>
    <socket-binding name="txn-recovery-environment" port="4712"/>
    <socket-binding name="txn-status-manager" port="4713"/>
    <outbound-socket-binding name="mail-smtp">
       <remote-destination host="localhost" port="25"/>
    </outbound-socket-binding>
</socket-binding-group>复制
```

要允许从远程机器访问，我们首先需要在适当的配置文件中创建远程管理界面。如果我们正在配置独立服务器，我们将更改standalone/configuration/standalone.xml，对于域管理，我们将更改 domain/configuration/host.xml：

```xml
<interface name="remoteManagement">
    <inet-address value="${jboss.bind.address.management:REMOTE_HOST_IP}"/> 
</interface> 
<!--REMOTE_HOST_IP is the remote host IP address. (e.g 192.168.1.2) -->复制
```

我们还必须修改management-http 的套接字绑定，删除之前的本地接口并添加新的：

```xml
<socket-binding-group name="standard-sockets" default-interface="public" 
  port-offset="${jboss.socket.binding.port-offset:0}">
    <!-- same as before -->
    <socket-binding name="management-http" interface="remoteManagement" 
      port="${jboss.management.http.port:9990}"/>
    <socket-binding name="management-https" interface="remoteManagement" 
      port="${jboss.management.https.port:9993}"/>
    <!-- same as before -->
</socket-binding-group>复制
```

在上面的配置中，我们将新的remoteManagement 接口绑定到我们的 HTTP (9990) 和 HTTPS (9993) 端口。它将允许远程主机 IP 通过 HTTP/HTTPS 端口连接到 Web 界面。

## 3.认证

WildFly 默认保护所有远程连接。默认的安全机制是通过 HTTP 摘要认证的用户名/密码。

但是，如果我们在将用户添加到服务器之前尝试连接到管理控制台， 我们将不会收到登录弹出窗口的提示。

然后，为了创建用户，WildFly 提供了一个交互式add-user.sh(在 Windows 平台上为add-user.bat )脚本，其中包含几个步骤：

1.  用户类型： 管理或应用程序用户
2.  Realm :配置时使用的域名，默认为ManagementRealm 
3.  用户名：新用户的用户名
4.  密码：新用户的密码
5.  从域控制器：一个标志，指示用户是否将控制分布式域体系结构中的从域进程；它默认为否

也可以通过使用相同的脚本并将输入指定为参数，以非交互方式添加用户：

```bash
$ ./add-user.sh -u 'adminuser1' -p 'password1!'复制
```

添加管理用户“adminuser1 ”，密码为“password1!” 到默认领域。

## 4。总结

在这个简短的教程中，我们探讨了如何设置 WildFly 以允许远程访问服务器的管理 Web 控制台。此外，我们还了解了如何使用 WildFly 提供的脚本创建用户。