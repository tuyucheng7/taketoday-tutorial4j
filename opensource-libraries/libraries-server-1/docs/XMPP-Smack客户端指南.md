## 1. 简介

[XMPP](https://xmpp.org/rfcs/rfc3921.html)是一个丰富而复杂的即时消息协议。

在本教程中，我们不会从头开始编写我们自己的客户端，而是看一下 Smack，这是一个用Java编写的模块化和可移植的开源 XMPP 客户端 ，它为我们做了很多繁重的工作。

## 2.依赖关系

Smack 被组织为几个模块以提供更多的灵活性，因此我们可以轻松地包含我们需要的功能。

其中一些包括：

-   XMPP over TCP 模块
-   支持 XMPP 标准基金会定义的许多扩展的模块
-   遗留扩展支持
-   要调试的模块

我们可以在 [XMPP 的文档](https://download.igniterealtime.org/smack/docs/latest/javadoc/)中找到所有支持的模块。

然而，在本教程中，我们将只使用tcp、im、 extensions和java7模块：

```xml
<dependency>
    <groupId>org.igniterealtime.smack</groupId>
    <artifactId>smack-tcp</artifactId>
</dependency>
<dependency>
    <groupId>org.igniterealtime.smack</groupId>
    <artifactId>smack-im</artifactId>
</dependency>
<dependency>
    <groupId>org.igniterealtime.smack</groupId>
    <artifactId>smack-extensions</artifactId>
</dependency>
<dependency>
    <groupId>org.igniterealtime.smack</groupId>
    <artifactId>smack-java7</artifactId>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/search?q=g:org.igniterealtime.smack)找到。

## 3.设置

为了测试客户端，我们需要一个 XMPP 服务器。为此，我们将在[jabber.hot-chilli.net](https://jabber.hot-chilli.net/)上创建一个帐户，这是一项面向所有人的免费 Jabber/XMPP 服务。

之后，我们可以使用 XMPPTCPConnectionConfiguration 类配置 Smack，该类提供构建器来设置连接参数：

```java
XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
  .setUsernameAndPassword("baeldung","baeldung")
  .setXmppDomain("jabb3r.org")
  .setHost("jabb3r.org")
  .build();
```

构建器允许我们设置执行连接所需的基本信息。如果需要，我们还可以设置其他参数，例如端口、SSL 协议和超时。

## 4.连接

使用 XMPPTCPConnection类可以简单地建立连接：

```java
AbstractXMPPConnection connection = new XMPPTCPConnection(config);
connection.connect(); //Establishes a connection to the server
connection.login(); //Logs in

```

该类包含一个构造函数，该构造函数接受先前构建的配置。它还提供了连接到服务器和登录的方法。

建立连接后，我们就可以使用 Smack 的功能，例如聊天，我们将在下一节中进行介绍。

如果连接突然中断，默认情况下，Smack 会尝试重新连接。

[ReconnectionManager](https://download.igniterealtime.org/smack/docs/latest/javadoc/org/jivesoftware/smack/ReconnectionManager.html)将尝试立即重新连接到服务器并增加尝试之间的延迟，因为连续的重新连接不断失败。

## 5.聊天

该库的主要功能之一是聊天支持。

使用 Chat类可以在两个用户之间创建新的消息线程：

```java
ChatManager chatManager = ChatManager.getInstanceFor(connection);
EntityBareJid jid = JidCreate.entityBareFrom("baeldung2@jabb3r.org");
Chat chat = chatManager.chatWith(jid);
```

请注意，为了构建聊天，我们使用了 ChatManager并且显然指定了与谁聊天。我们通过使用 EntityBareJid对象实现了后者，该对象包装了一个由本地部分 ( baeldung2 ) 和域部分 ( jabb3r.org ) 组成的 XMPP 地址(又名 JID )。 

之后，我们可以使用 send()方法发送消息：

```java
chat.send("Hello!");
```

并通过设置监听器接收消息：

```java
chatManager.addIncomingListener(new IncomingChatMessageListener() {
  @Override
  public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
      System.out.println("New message from " + from + ": " + message.getBody());
  }
});
```

### 5.1. 房间

除了端到端的用户聊天，Smack 还通过使用房间来支持群聊。

有两种类型的房间，即时房间和预订房间。

即时房间可供立即访问，并根据一些默认配置自动创建。另一方面，保留的房间是在允许任何人进入之前由房间所有者手动配置的。

让我们看看如何使用MultiUserChatManager创建即时聊天室：

```java
MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
MultiUserChat muc = manager.getMultiUserChat(jid);
Resourcepart room = Resourcepart.from("baeldung_room");
muc.create(room).makeInstant();
```

以类似的方式，我们可以创建一个保留房间：

```java
Set<Jid> owners = JidUtil.jidSetFrom(
  new String[] { "baeldung@jabb3r.org", "baeldung2@jabb3r.org" });

muc.create(room)
  .getConfigFormManger()
  .setRoomOwners(owners)
  .submitConfigurationForm();
```

## 6.花名册

Smack 提供的另一个功能是可以跟踪其他用户的存在。

通过 Roster.getInstanceFor()， 我们可以获得一个Roster实例：

```java
Roster roster = Roster.getInstanceFor(connection);
```

[Roster](https://github.com/igniterealtime/Smack/blob/master/documentation/roster.md)是一个联系人列表，将用户表示为RosterEntry对象，并允许我们将用户组织成组。

我们可以 使用 getEntries()方法打印名册中的所有条目：

```java
Collection<RosterEntry> entries = roster.getEntries();
for (RosterEntry entry : entries) {
    System.out.println(entry);
}
```

此外，它允许我们使用 RosterListener 监听其条目和状态数据的变化：

```java
roster.addRosterListener(new RosterListener() {
    public void entriesAdded(Collection<String> addresses) { // handle new entries }
    public void entriesDeleted(Collection<String> addresses) { // handle deleted entries }
    public void entriesUpdated(Collection<String> addresses) { // handle updated entries }
    public void presenceChanged(Presence presence) { // handle presence change }
});
```

它还提供了一种通过确保只有批准的用户才能订阅名册来保护用户隐私的方法。为此，Smack 实施了一个基于权限的模型。

使用Roster.setSubscriptionMode()方法可以通过三种方式处理状态订阅请求 ：

-   Roster.SubscriptionMode.accept_all – 接受所有订阅请求
-   Roster.SubscriptionMode.reject_all – 拒绝所有订阅请求
-   Roster.SubscriptionMode.manual – 手动处理状态订阅请求

如果我们选择手动处理订阅请求，我们将需要注册一个StanzaListener(在下一节中描述)并处理具有 Presence.Type.subscribe类型的数据包。

## 7.节

除了聊天之外，Smack 还提供了一个灵活的框架来发送一个节并监听传入的节。

澄清一下，节是 XMPP 中意义的离散语义单元。它是通过 XML 流从一个实体发送到另一个实体的结构化信息。

我们可以使用 send()方法通过连接 传输节：

```java
Stanza presence = new Presence(Presence.Type.subscribe);
connection.sendStanza(presence);
```

在上面的例子中，我们发送了一个Presence 节来订阅一个花名册。

另一方面，为了处理传入的节，库提供了两个结构：

-   节收集器 
-   节听者

特别是，StanzaCollector 让我们同步等待新节：

```java
StanzaCollector collector
  = connection.createStanzaCollector(StanzaTypeFilter.MESSAGE);
Stanza stanza = collector.nextResult();
```

虽然 StanzaListener是一个用于异步通知我们传入节的接口：

```java
connection.addAsyncStanzaListener(new StanzaListener() {
    public void processStanza(Stanza stanza) 
      throws SmackException.NotConnectedException,InterruptedException, 
        SmackException.NotLoggedInException {
            // handle stanza
        }
}, StanzaTypeFilter.MESSAGE);
```

### 7.1. 过滤器

此外，该库提供了一组内置的过滤器来处理传入的节。

我们可以使用 StanzaTypeFilter 按类型过滤节，或使用 StanzaIdFilter按 ID 过滤节：

```java
StanzaFilter messageFilter = StanzaTypeFilter.MESSAGE;
StanzaFilter idFilter = new StanzaIdFilter("123456");
```

或者，通过特定地址辨别：

```java
StanzaFilter fromFilter
  = FromMatchesFilter.create(JidCreate.from("baeldung@jabb3r.org"));
StanzaFilter toFilter
  = ToMatchesFilter.create(JidCreate.from("baeldung2@jabb3r.org"));
```

我们可以使用逻辑过滤器运算符(AndFilter、OrFilter、NotFilter)来创建复杂的过滤器：

```java
StanzaFilter filter
  = new AndFilter(StanzaTypeFilter.Message, FromMatchesFilter.create("baeldung@jabb3r.org"));
```

## 八. 总结

在本文中，我们介绍了 Smack 提供的现成的最有用的类。

我们学习了如何配置库以发送和接收 XMPP 节。

随后，我们学习了如何使用ChatManager和Roster功能处理群聊。