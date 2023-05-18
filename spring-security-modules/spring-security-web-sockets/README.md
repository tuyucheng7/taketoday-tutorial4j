## Spring Security Web Sockets

本模块包含有关WebSockets与Spring Security的文章

## 相关文章

+ [REST与WebSockets](http://tu-yucheng.github.io/springsecurity/2023/05/17/rest-vs-websockets.html)
+ [Security和WebSocket简介](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-security-websockets.html)
+ [Spring WebSockets向特定用户发送消息](http://tu-yucheng.github.io/springsecurity/2023/05/17/spring-websockets-send-message-to-user.html)

## 运行项目

要构建项目，请运行命令：`mvn clean install`，这将在target文件夹中构建一个WAR文件，你可以部署在像Tomcat这样的服务器上。

或者，从IDE运行项目，目标为`org.codehaus.cargo：cargo-maven2-plugin：run`

要登录，请使用src/main/resource中的data.sql文件中的凭据，例如：user/password。