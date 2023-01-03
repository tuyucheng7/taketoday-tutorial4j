## 1. 概述

在本教程中，我们将讨论在创建Web应用程序时遇到的常见Eclipse错误“**web.xml is missing and <failOnMissingWebXml\> is set to true**”。

## 2. Eclipse错误

在Java Web应用程序中，[web.xml](https://docs.oracle.com/cd/E13222_01/wls/docs70/webapp/webappdeployment.html)是部署描述符的标准名称。

我们可以使用Maven创建一个Web应用程序或使用Eclipse创建动态Web项目，Eclipse不会在WEB-INF/目录下创建默认部署描述符web.xml 。

**Java EE 6+规范试图不再强调部署描述符，因为它们可以被注解取代。但是，较低版本仍然需要它**。

failOnMissingWebXml属性是[Apache Maven](https://www.baeldung.com/maven) war插件org.apache.maven.plugins:maven-war-plugin的属性之一，此插件的默认值对于版本<3.1.0为true，对于更高版本为false。

这意味着如果我们使用早于3.1.0版本的maven-war-plugin，并且web.xml文件不存在，那么将其打包为war文件的目标将失败。

## 3. 使用web.xml

对于我们仍然需要web.xml部署描述符的所有情况，我们都可以在Eclipse中轻松**生成web.xml**：

-   右键单击web项目
-   将鼠标悬停在菜单上的**Java EE Tools**
-   从子菜单中选择**Generate Deployment Descriptor**

<img src="../assets/img.png">

瞧！在WEB-INF/目录下生成了web.xml文件。

## 4. 没有web.xml

在大多数情况下，我们可能根本不需要web.xml文件。我们可以简单地完全跳过创建它，而不是在我们的项目中保留一个空白的web.xml文件。幸运的是，有两种简单的方法，具体取决于我们使用的maven-war-plugin版本。

### 4.1 在3.1.0之前使用maven-war-plugin

我们可以在pom.xml的<plugins\>部分配置Maven项目的所有插件，正如我们之前所说，在插件版本3.1.0之前，failOnMissingWebXml的默认值为true。

让我们在pom.xml中声明maven-war-plugin并**将属性failOnMissingWebXml显式设置为false**：

```xml
<plugin>
    <artifactId>maven-war-plugin</artifactId>
    <version>2.4</version>
    <configuration>
        <failOnMissingWebXml>false</failOnMissingWebXml>    
    </configuration>
</plugin>
```

### 4.2 使用maven-war-plugin 3.1.0及更高版本

我们还可以通过升级maven-war-plugin的版本来避免显式设置属性，对于maven-war-plugin版本3.1.0及更高版本，属性failOnMissingWebXml的默认值为false：

```xml
<plugin>
    <artifactId>maven-war-plugin</artifactId>
    <version>3.1.0</version>
</plugin>
```

## 5. 总结

在本文中，我们了解了缺少web.xml错误背后的原因以及修复它的多种方法。