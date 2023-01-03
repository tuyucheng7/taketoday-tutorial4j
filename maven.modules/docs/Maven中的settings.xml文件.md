## 1. 概述

在使用Maven时，我们将大部分特定于项目的配置保留在pom.xml中。

Maven提供了一个设置文件settings.xml，它允许我们指定它将使用哪些本地和远程仓库。我们还可以使用它来存储我们在源代码中不需要的设置，例如凭据。

在本教程中，我们将学习如何使用settings.xml。我们将查看代理、镜像和profiles，我们还将讨论如何确定适用于我们项目的当前设置。

## 2. 配置

settings.xml文件配置[Maven](https://www.baeldung.com/maven)安装，它类似于pom.xml文件，但全局定义或按用户定义。

让我们探索一下可以在settings.xml文件中配置的元素，settings.xml文件的主要设置元素可以包含九个可能的预定义子元素：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository/>
    <interactiveMode/>
    <offline/>
    <pluginGroups/>
    <servers/>
    <mirrors/>
    <proxies/>
    <profiles/>
    <activeProfiles/>
</settings>
```

### 2.1 简单值

一些顶级配置元素包含简单的值：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository>${user.home}/.m2/repository</localRepository>
    <interactiveMode>true</interactiveMode>
    <offline>false</offline>
</settings>
```

localRepository元素指向系统本地仓库的路径，[本地仓库](https://www.baeldung.com/maven-local-repository)**是缓存我们项目的所有依赖项的地方**，默认设置是使用用户的主目录。但是，我们可以更改它以允许所有登录用户从公共本地仓库进行构建。

interactiveMode标签定义了我们是否允许Maven与请求输入的用户进行交互，此标签默认为true。

offline标签确定构建系统是否可以在离线模式下运行，这默认为false；但是，在构建服务器无法连接到远程仓库的情况下，我们可以将其切换为true 。

### 2.2 pluginGroups

pluginGroups元素包含指定groupId的子元素列表，groupId是创建特定Maven工件的组织的唯一标识符：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <pluginGroups>
        <pluginGroup>org.apache.tomcat.maven</pluginGroup>
    </pluginGroups>
</settings>
```

**当在命令行中没有提供groupId的情况下使用插件时，Maven会搜索pluginGroups列表**。默认情况下，该列表包含org.apache.maven.plugins和org.codehaus.mojo组。

上面定义的settings.xml文件允许我们执行截断的Tomcat插件命令：

```bash
mvn tomcat7:help
mvn tomcat7:deploy
mvn tomcat7:run
```

### 2.3 proxies

我们可以为Maven的部分或全部HTTP请求配置代理，proxies元素允许子proxy元素列表，但**一次只能有一个proxy处于激活状态**：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <proxies>
        <proxy>
            <id>tuyucheng-proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>tuyucheng.proxy.com</host>
            <port>8080</port>
            <username>demo-user</username>
            <password>dummy-password</password>
            <nonProxyHosts>*.tuyucheng.com|*.apache.org</nonProxyHosts>
        </proxy>
    </proxies>
</settings>
```

我们通过active标签定义当前激活的代理，然后使用nonProxyHosts元素，我们指定哪些主机不被代理。使用的分隔符取决于具体的代理服务器，最常见的分隔符是竖线和逗号。

### 2.4 mirrors

仓库可以在项目pom.xml中声明，这意味着共享项目代码的开发人员开箱即用地获得正确的仓库设置。

**在我们想要为特定仓库定义替代镜像的情况下，我们可以使用mirrors，这会覆盖pom.xml中的内容**。

例如，我们可以通过镜像所有仓库请求来强制Maven使用单个仓库：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>internal-tuyucheng-repository</id>
            <name>Tuyucheng Internal Repo</name>
            <url>https://tuyucheng.com/repo/maven2/</url>
            <mirrorOf>*</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```

我们可能只为给定的仓库定义一个镜像，Maven将选择第一个匹配项。通常，**我们应该使用通过CDN在全球范围内分发的官方仓库**。

### 2.5 servers

在项目pom.xml中定义仓库是一个很好的做法，但是，我们不应该使用pom.xml将安全设置(例如凭据)放入我们的源代码仓库中。相反，**我们在settings.xml文件中定义此安全信息**：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>internal-tuyucheng-repository</id>
            <username>demo-user</username>
            <password>dummy-password</password>
            <privateKey>${user.home}/.ssh/tuyucheng_key</privateKey>
            <passphrase>dummy-passphrase</passphrase>
            <filePermissions>664</filePermissions>
            <directoryPermissions>775</directoryPermissions>
            <configuration></configuration>
        </server>
    </servers>
</settings>
```

我们应该注意的是settings.xml中的服务器ID需要与pom.xml中提到的仓库的ID元素相匹配，XML还允许我们使用占位符从环境变量中获取凭据。

## 3. profiles

profiles元素使我们能够创建多个[profile](https://www.baeldung.com/maven-profiles)子元素，这些元素由其ID子元素区分。settings.xml中的profile元素是pom.xml中相同元素的截断版本。

它只能包含四个子元素：activation、repositories、pluginRepositories和properties。这些元素将构建系统配置为一个整体，而不是任何特定项目。

请务必注意，**来自settings.xml中激活的profile的值将覆盖pom.xml或profiles.xml文件中的任何等效profile值**，Profiles由ID匹配。

### 3.1 activation

我们只能在给定的情况下使用profiles来修改某些值，我们可以使用activation元素指定这些情况。因此，**当满足所有指定条件时，profile激活发生**：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>tuyucheng-test</id>
            <activation>
                <activeByDefault>false</activeByDefault>
                <jdk>1.8</jdk>
                <os>
                    <name>Windows 10</name>
                    <family>Windows</family>
                    <arch>amd64</arch>
                    <version>10.0</version>
                </os>
                <property>
                    <name>mavenVersion</name>
                    <value>3.0.7</value>
                </property>
                <file>
                    <exists>${basedir}/activation-file.properties</exists>
                    <missing>${basedir}/deactivation-file.properties</missing>
                </file>
            </activation>
        </profile>
    </profiles>
</settings>
```

有四种可能的激活器，并非所有激活器都需要指定：

-   jdk：根据指定的JDK版本激活(支持范围)
-   os：根据操作系统属性激活
-   property：如果Maven检测到特定的属性值，则激活profile
-   file：如果给定的文件名存在或缺失则激活profile

为了检查哪个profile将激活某个构建，我们可以使用Maven help插件：

```bash
mvn help:active-profiles
```

输出将显示给定项目的当前激活的profiles：

```bash
[INFO] --- maven-help-plugin:3.2.0:active-profiles (default-cli) @ core-java-streams-3 ---
[INFO]
Active Profiles for Project 'cn.tuyucheng.taketoday.java-core-modules:java-streams-3:jar:1.0.0':
The following profiles are active:
 - tuyucheng-test (source: cn.tuyucheng.taketoday.java-core-modules:java-streams-3:1.0.0)
```

### 3.2 properties

Maven属性可以被认为是某个值的命名占位符，**可以使用${property_name}表示法在pom.xml文件中访问这些值**：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>tuyucheng-test</id>
            <properties>
                <user.project.folder>${user.home}/tuyucheng-tutorials</user.project.folder>
            </properties>
        </profile>
    </profiles>
</settings>
```

pom.xml文件中提供了四种不同类型的属性：

-   使用env前缀的属性返回一个环境变量值，例如${env.PATH}。
-   使用project前缀的属性返回在pom.xml的project 素中设置的属性值，例如${project.version}。
-   使用settings前缀的属性从settings.xml返回相应元素的值，例如${settings.localRepository}。
-   我们可以直接通过Java中的System.getProperties方法引用所有可用的属性，例如${java.home}。
-   我们可以在没有前缀的属性元素中使用属性集，例如${junit.version}。

### 3.3 repositories

远程仓库包含Maven用来填充本地仓库的工件集合，特定工件可能需要不同的远程仓库，**Maven搜索在激活的profile下启用的仓库**：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>adobe-public</id>
            <repositories>
                <repository>
                    <id>adobe-public-releases</id>
                    <name>Adobe Public Repository</name>
                    <url>https://repo.adobe.com/nexus/content/groups/public</url>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
</settings>
```

我们可以使用repository元素来仅启用来自特定仓库的工件的发布或快照版本。

### 3.4 pluginRepositories

有两种标准类型的Maven工件，依赖项和插件。由于Maven插件是一种特殊类型的工件，**我们可以将插件仓库与其他仓库分开**：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>adobe-public</id>
            <pluginRepositories>
                <pluginRepository>
                    <id>adobe-public-releases</id>
                    <name>Adobe Public Repository</name>
                    <url>https://repo.adobe.com/nexus/content/groups/public</url>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>
    </profiles>
</settings>
```

值得注意的是，pluginRepositories元素的结构与repositories元素非常相似。

### 3.5 activeProfiles

activeProfiles元素包含引用特定profile ID的子元素，**Maven会自动激活此处引用的任何profile**：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <activeProfiles>
        <activeProfile>tuyucheng-test</activeProfile>
        <activeProfile>adobe-public</activeProfile>
    </activeProfiles>
</settings>
```

在这个例子中，每次调用mvn都像我们在命令行中添加了-P tuyucheng-test,adobe-public一样运行。

## 4. 设置级别

settings.xml文件通常位于以下几个位置：

-  Maven主目录中的全局设置：${maven.home}/conf/settings.xml
-  用户家目录中的用户设置：${user.home}/.m2/settings.xml

如果两个文件都存在，则合并它们的内容，**用户设置中的配置优先**。

### 4.1 确定文件位置

为了确定全局和用户设置的位置，我们可以使用debug标志运行Maven并在输出中搜索“settings”：

```bash
$ mvn -X clean | grep "settings"
[DEBUG]   Imported: org.apache.maven.settings < plexus.core
[DEBUG] Reading global settings from D:\develop-tools\apache-maven-3.8.4\conf\settings.xml
[DEBUG] Reading user settings from C:\Users\tuyuc\.m2\settings.xml
```

### 4.2 确定有效设置

我们可以**使用Maven help插件找出合并的全局和用户设置的内容**：

```bash
mvn help:effective-settings
```

这描述了XML格式的设置：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <localRepository>D:\develop-tools\apache-maven-3.8.4\repository</localRepository>
    <pluginGroups>
        <pluginGroup>org.apache.tomcat.maven</pluginGroup>
        <pluginGroup>org.apache.maven.plugins</pluginGroup>
        <pluginGroup>org.codehaus.mojo</pluginGroup>
    </pluginGroups>
</settings>
```

### 4.3 覆盖默认位置

Maven还允许我们通过命令行覆盖全局和用户设置的位置：

```bash
$ mvn clean --settings c:\user\user-settings.xml --global-settings c:\user\global-settings.xml
```

我们还可以使用同一命令的较短的–s版本：

```bash
$ mvn clean --s c:\user\user-settings.xml --gs c:\user\global-settings.xml
```

## 5. 总结

在本文中，我们**探讨了Maven的settings.xml文件中可用的配置**。

我们学习了如何配置代理、仓库和Profile。接下来，我们查看了全局设置文件和用户设置文件之间的区别，以及如何确定正在使用的文件。

最后，我们着眼于确定使用的有效设置，并覆盖默认文件位置。