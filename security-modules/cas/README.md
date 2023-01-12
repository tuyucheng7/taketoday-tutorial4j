## CAS

本模块包含有关中央身份验证服务(CAS)的文章。

该模块由2个子模块组成：

1. `cas-server` - 它需要JDK 11并使用Gradle War叠加样式来简化设置和部署。要启动服务器，只需运行：

`./gradlew run
-Dorg.gradle.java.home=$JAVA11_HOME
-Pargs="-Dcas.standalone.configurationDirectory=/cas-server/src/main/resources/etc/cas/config"`

服务器从https://localhost:8443启动，`casuser`/`Mellon`是用于登录的用户名和密码.

2. `cas-secured-app` - 基于Maven的Springboot应用程序