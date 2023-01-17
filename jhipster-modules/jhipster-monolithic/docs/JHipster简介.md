## 1. 简介

本文将为你简要介绍 JHipster，向你展示如何使用命令行工具创建简单的单体应用程序和自定义实体。

我们还将在每个步骤中检查生成的代码，并涵盖构建命令和自动化测试。

## 2. 什么是 Jhipster

[简而言之， JHipster](https://jhipster.github.io/)是一个高级代码生成器，它建立在广泛的尖端开发工具和平台之上。

该工具的主要组成部分是：

-   [Yeoman](http://yeoman.io/)，一个前端脚手架工具
-   好旧的[Spring Boot](https://spring.io/projects/spring-boot)
-   [AngularJS](https://angularjs.org/)，著名的 Javascript 框架。JHipster 也适用于[AngularJS 2](https://angular.io/)

JHipster 仅使用几个 shell 命令创建了一个成熟的Javaweb 项目，具有友好、响应迅速的前端、文档化的 REST API、全面的测试覆盖率、基本的安全性和数据库集成！生成的代码有很好的注解并遵循行业最佳实践。

它利用的其他关键技术是：

-   [Swagger](http://swagger.io/)，用于 API 文档
-   [Maven](https://maven.apache.org/)、[Npm](https://www.npmjs.com/)、[Yarn](https://yarnpkg.com/en/)、[Gulp](http://gulpjs.com/)和[Bower](https://bower.io/)作为依赖管理器和构建工具
-   [Jasmine](https://jasmine.github.io/)、[Protractor](https://www.protractortest.org/#/)、[Cucumber](https://cucumber.io/)和[Gatling](http://gatling.io/)作为测试框架
-   用于数据库版本控制的[Liquibase](https://www.liquibase.org/)

我们不需要在生成的应用程序中使用所有这些项目。可选项目是在项目创建期间选择的。

[![JHipster应用程序](https://www.baeldung.com/wp-content/uploads/2017/03/jhipster-app-1.png)](https://www.baeldung.com/wp-content/uploads/2017/03/jhipster-app-1.png)

一个漂亮的 JHipster 生成的应用程序。这是我们将在整篇文章中进行的工作的结果。

## 三、安装

要安装 JHipster，我们首先需要安装它的所有依赖项：

-  Java–推荐[版本 8](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
-   [Git——](https://git-scm.com/)版本控制系统
-   [节点JS](https://nodejs.org/en/download/)
-   [约曼](http://yeoman.io/learning/index.html)
-   [纱](https://yarnpkg.com/en/docs/install)

如果你决定使用 AngularJS 2，那么依赖项就足够了。但是，如果你更喜欢使用 AngularJS 1，则还需要安装[Bower](https://bower.io/)和[Gulp](https://github.com/gulpjs/gulp/blob/master/docs/getting-started.md)。

现在，为了完成，我们只需要安装 JHipster 本身。这是最简单的部分。由于 JHipster 是一个[Yeoman 生成器](http://yeoman.io/generators/)，它又是一个 Javascript 包，安装就像运行一个简单的 shell 命令一样简单：

```shell
yarn global add generator-jhipster
```

而已！我们使用 Yarn 包管理器来安装 JHipster 生成器。

## 4.创建项目

创建一个 JHipster 项目本质上就是构建一个 Yeoman 项目。一切都以yo命令开始：

```shell
mkdir baeldung-app && cd baeldung-app
yo jhipster
```

这将创建我们的项目文件夹，名为baeldung-app，并启动 Yeoman 的命令行界面，它将引导我们完成项目的创建。

该过程涉及 15 个步骤。我鼓励你探索每个步骤的可用选项。在本文的范围内，我们将创建一个简单的整体式应用程序，而不会过多偏离默认选项。

以下是与本文最相关的步骤：

-   应用类型——选择Monolithic application(推荐用于简单项目)
-   从 JHipster Marketplace 安装其他生成器——Type N。在这一步中，我们可能想要安装很酷的附加组件。一些流行的是启用数据跟踪的实体审计；bootstrap-material-design，使用流行的 Material Design 组件和角度数据表
-   Maven 或 Gradle——选择Maven
-   其他技术——不要选择任何选项，只需按Enter 键即可进入下一步。在这里我们可以选择插件Social login with Google, Facebook, Twitter，这是一个很不错的功能。
-   客户端框架——选择[BETA] Angular 2.x。我们也可以使用 AngularJS 1
-   启用国际化——键入Y，然后选择英语作为母语。我们可以选择任意多的语言作为第二语言
-   测试框架——选择Gatling和Protractor

[![jhipster项目创建](https://www.baeldung.com/wp-content/uploads/2017/03/jhipster-project-creation.png)](https://www.baeldung.com/wp-content/uploads/2017/03/jhipster-project-creation.png)

JHipster 将创建项目文件，然后开始安装依赖项。输出中将显示以下消息：

```plaintext
I'm all done. Running npm install for you to install the required 
   dependencies. If this fails, try running the command yourself.
```

依赖项安装可能需要一些时间。完成后会显示：

```shell
Server application generated successfully.

Run yourSpring Bootapplication:
 ./mvnw

Client application generated successfully.

Start your Webpack development server with:
npm start
```

我们的项目现已创建。我们可以在我们的项目根文件夹上运行主要命令：

```shell
./mvnw #starts Spring Boot, on port 8080
./mvnw clean test #runs the application's tests
yarn test #runs the client tests
```

JHipster 生成一个 README 文件，就放在我们项目的根文件夹中。该文件包含运行与我们的项目相关的许多其他有用命令的说明。

## 5. 生成代码概述

查看自动生成的文件。你会注意到该项目看起来很像标准的 Java/Spring 项目，但有很多额外功能。

由于 JHipster 也负责创建前端代码，因此你会发现一个package.json文件、一个webpack文件夹和一些其他与 Web 相关的东西。

让我们快速浏览一些关键文件。

### 5.1. 后端文件

-   正如预期的那样，Java 代码包含在src/main/java文件夹中
-   src/main/resources文件夹中有一些Java代码使用的静态内容。在这里我们将找到国际化文件(在i18n文件夹中)、电子邮件模板和一些配置文件
-   单元和集成测试位于src/test/java文件夹中
-   性能(Gatling)测试在src/test/gatling中。但是，此时，该文件夹中不会有太多内容。一旦我们创建了一些实体，这些对象的性能测试将位于此处

### 5.2. 前端

-   根前端文件夹是src/main/webapp
-   app文件夹包含大部分 AngularJS模块
-   i18n包含前端部分的国际化文件
-   单元测试 (Karma) 在src/test/javascript/spec文件夹中
-   端到端测试(量角器)在src/test/javascript/e2e文件夹中

## 6. 创建自定义实体

实体是我们 JHipster 应用程序的构建块。它们代表业务对象，如User、Task、Post、Comment等。

使用 JHipster 创建实体是一个轻松的过程。我们可以使用命令行工具创建一个对象，类似于我们创建项目本身的方式，或者通过[JDL-Studio](https://jhipster.github.io/jdl-studio/)，一个生成实体的 JSON 表示的在线工具，稍后可以将其导入到我们的项目中。

在本文中，让我们使用命令行工具创建两个实体：Post和Comment。

一个帖子应该有一个标题、一个文本内容和一个创建日期。它还应该与用户相关，该用户是Post的创建者。一个用户可以有许多与之关联的帖子。

一个Post也可以有零个或多个Comments。每个评论都有一个文本和创建日期。

要启动我们的Post实体的创建过程，请转到我们项目的根文件夹并键入：

```shell
yo jhipster:entity post
```

现在按照界面显示的步骤进行操作。

-   添加一个名为title类型的字段，并向该字段添加一些验证规则(必填、最小长度和最大长度)
-   添加另一个名为content of type String的字段，并将其设置为Required
-   添加名为creationDate的第三个字段，类型为LocalDate
-   现在让我们添加与User的关系。请注意，实体User已经存在。它是在项目构思期间创建的。对方实体名称为user，关系名称为creator，类型为many-to-one，显示字段为name ，最好将关系设为必填
-   不要选择使用 DTO，而是使用Direct 实体
-   选择将存储库直接注入服务类。请注意，在实际应用程序中，将 REST 控制器与服务类分开可能更合理
-   最后，选择无限滚动作为分页类型
-   如果需要，授予 JHipster 覆盖现有文件的权限

重复上述过程以创建一个名为comment的实体，它有两个字段，text 类型，String类型和creationDate类型LocalDate。Comment还应该与Post具有必需的多对一关系。

而已！该过程有很多步骤，但你会发现完成这些步骤并不需要那么多时间。

你会注意到 JHipster 创建了一堆新文件，并修改了一些其他文件，作为创建实体过程的一部分：

-   一个 。jhipster文件夹已创建，其中包含每个对象的JSON文件。这些文件描述了实体的结构
-   实际的@Entity注解类在域包中
-   存储库是在存储库包中创建的
-   REST 控制器进入web.rest包
-   每个表创建的 Liquibase 变更日志都在resources/config/liquibase/changelog文件夹中
-   在前端部分，在实体目录中为每个实体创建一个文件夹
-   国际化文件设置在i18n文件夹中(如果需要，可以随意修改)
-   src/test文件夹下创建了几个测试，前端，后端

这是相当多的代码！

随意运行测试并仔细检查所有测试是否通过。现在我们还可以使用 Gatling 运行性能测试，使用命令(应用程序必须运行才能通过这些测试)：

```shell
mvnw gatling:execute
```

如果你想检查前端的运行情况，请使用 启动应用程序。/mvnw，导航到http://localhost:8080并以管理员用户身份登录(密码为admin)。

在实体菜单项下的顶部菜单中选择发布。你将看到一个空列表，稍后将包含所有帖子。单击“创建新帖子”按钮以显示包含表单：

[![潮人实体形式](https://www.baeldung.com/wp-content/uploads/2017/03/jhipster-entity-form.png)](https://www.baeldung.com/wp-content/uploads/2017/03/jhipster-entity-form.png)

请注意 JHipster 在表单组件和验证消息上的谨慎程度。当然，我们可以随心所欲地修改前端，但表单构建得非常好。

## 7. 持续集成支持

JHipster 可以为最常用的持续集成工具自动创建配置文件。只需运行此命令：

```shell
yo jhipster:ci-cd
```

并回答问题。在这里我们可以选择要为哪些 CI 工具创建配置文件，是否要使用 Docker、Sonar 甚至部署到 Heroku 作为构建过程的一部分。

ci-cd命令可以为以下CI 工具创建配置文件：

-   Jenkins：该文件是JenkinsFile
-   Travis CI：文件是.travis.yml
-   Circle CI：文件为circle.yml
-   GitLab：文件是.gitlab-ci.yml

## 八. 总结

本文对 JHipster 的功能进行了一些介绍。当然，我们在这里无法涵盖更多内容，所以一定要继续探索[JHipster 官方网站](https://jhipster.github.io/)。
