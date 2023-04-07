##1.概述

在我们上一篇SpringCloud文章中，我们将[Zipkin](https://www.baeldung.com/tracing-services-with-zipkin)支持添加到我们的应用程序中。在本文中，我们将向堆栈中添加一个前端应用程序。

到目前为止，我们一直在后端完全工作以构建我们的云应用程序。但是，如果没有UI，Web应用程序有什么用呢？在本文中，我们将通过将单页应用程序集成到我们的项目中来解决该问题。

我们将使用Angular和Bootstrap编写这个应用程序。Angular4代码的风格感觉很像编写Spring应用程序，这是Spring开发人员的自然交叉！虽然前端代码将使用Angular，但可以轻松地将本文的内容扩展到任何前端框架，而无需付出太多努力。

在本文中，我们将构建一个Angular4应用程序并将其连接到我们的云服务。我们将演示如何在SPA和SpringSecurity之间集成登录。我们还将展示如何使用Angular对HTTP通信的支持来访问我们应用程序的数据。

##2.网关变化

前端到位后，我们将切换到基于表单的登录，并将UI的安全部分切换到特权用户。这需要更改我们的网关安全配置。

###2.1.更新HttpSecurity

首先，让我们更新网关SecurityConfig.java类中的configure(HttpSecurityhttp)方法：

```java
@Override
protected void configure(HttpSecurity http) {
    http
      .formLogin()
      .defaultSuccessUrl("/home/index.html", true)
      .and()
    .authorizeRequests()
      .antMatchers("/book-service/", "/rating-service/", "/login", "/")
      .permitAll()
      .antMatchers("/eureka/").hasRole("ADMIN")
      .anyRequest().authenticated()
      .and()
    .logout()
      .and()
    .csrf().disable();
}
```

首先，我们添加一个默认的成功URL以指向/home/index.html，因为这将是我们的Angular应用程序所在的位置。接下来，我们将蚂蚁匹配器配置为允许通过网关的任何请求，但Eureka资源除外。这会将所有安全检查委托给后端服务。

接下来，我们删除了注销成功URL，因为默认重定向回登录页面可以正常工作。

###2.2.添加主要端点

接下来，让我们添加一个端点以返回经过身份验证的用户。这将在我们的Angular应用程序中用于登录和识别我们的用户拥有的角色。这将帮助我们控制他们可以在我们的网站上执行的操作。

在网关项目中，添加一个AuthenticationController类：

```java
@RestController
public class AuthenticationController {
 
    @GetMapping("/me")
    public Principal getMyUser(Principal principal) {
        return principal;
    }
}
```

控制器将当前登录的用户对象返回给调用者。这为我们提供了控制Angular应用程序所需的所有信息。

###2.3.添加登陆页面

让我们添加一个非常简单的登陆页面，以便用户在进入我们应用程序的根目录时看到一些东西。

在src/main/resources/static中，让我们添加一个带有登录页面链接的index.html文件：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Book Rater Landing</title>
</head>
<body>
    <h1>Book Rater</h1>
    <p>So many great things about the books</p>
    <a href="/login">Login</a>
</body>
</html>
```

##3.AngularCLI和入门项目

在开始一个新的Angular项目之前，请确保安装最新版本的[Node.js和npm](https://nodejs.org/en/download/)。

###3.1.安装角度CLI

首先，我们需要使用npm下载并安装Angular命令行界面。打开终端并运行：

```bash
npm install -g @angular/cli
```

这将下载并全局安装CLI。

###3.2.安装新项目

仍在终端中时，导航到网关项目并进入gateway/src/main文件夹。创建一个名为“angular”的目录并导航到它。从这里运行：

```bash
ng new ui
```

要有耐心;CLI正在设置一个全新的项目并使用npm下载所有JavaScript依赖项。此过程需要很多分钟的情况并不少见。

ng命令是AngularCLI的快捷方式，新参数指示CLI创建一个新项目，ui命令为我们的项目命名。

###3.3.运行项目

一旦新命令完成。导航到创建并运行的ui文件夹：

```bash
ng serve
```

一旦项目构建导航到http://localhost:4200。我们应该在浏览器中看到这个：

[![angular2开始1](https://www.baeldung.com/wp-content/uploads/2017/05/angular2-start-1-300x192.png)](https://www.baeldung.com/wp-content/uploads/2017/05/angular2-start-1.png)

恭喜！我们刚刚构建了一个Angular应用程序！

###3.4.安装引导程序

让我们使用npm安装bootstrap。从ui目录运行此命令：

```bash
npm install bootstrap@4.0.0-alpha.6 --save
```

这会将引导程序下载到node_modules文件夹中。

在ui目录中，打开.angular-cli.json文件。这是配置有关我们项目的一些属性的文件。找到apps>styles属性并添加我们的BootstrapCSS类的文件位置：

```javascript
"styles": [
    "styles.css",
    "../node_modules/bootstrap/dist/css/bootstrap.min.css"
],
```

这将指示Angular在与项目一起构建的已编译CSS文件中包含Bootstrap。

###3.5.设置构建输出目录

接下来，我们需要告诉Angular将构建文件放在哪里，以便我们的springboot应用程序可以为它们提供服务。SpringBoot可以从资源文件夹中的两个位置提供文件：

-源代码/主要/资源/静态
-源代码/主要/资源/公共

由于我们已经在使用静态文件夹为Eureka提供一些资源，并且每次运行构建时Angular都会删除此文件夹，所以让我们将Angular应用程序构建到公共文件夹中。

再次打开.angular-cli.json文件并找到apps>outDir属性。更新该字符串：

```javascript
"outDir": "../../resources/static/home",
```

如果Angular项目位于src/main/angular/ui，那么它将构建到src/main/resources/public文件夹。如果应用程序位于另一个文件夹中，则需要修改此字符串以正确设置位置。

###3.6.使用Maven自动化构建

最后，我们将设置一个自动构建以在编译代码时运行。只要运行“mvncompile”，这个ant任务就会运行AngularCLI构建任务。将此步骤添加到网关的POM.xml中，以确保每次编译时我们都获得最新的ui更改：

```xml
<plugin>
    <artifactId>maven-antrun-plugin</artifactId>
    <executions>
        <execution>
            <phase>generate-resources</phase>
            <configuration>
                <tasks>
                    <exec executable="cmd" osfamily="windows"
                      dir="${project.basedir}/src/main/angular/ui">
                        <arg value="/c"/>
                        <arg value="ng"/>
                        <arg value="build"/>
                    </exec>
                    <exec executable="/bin/sh" osfamily="mac"
                      dir="${project.basedir}/src/main/angular/ui">
                        <arg value="-c"/>
                        <arg value="ng build"/>
                    </exec>
                </tasks>
            </configuration>
            <goals>
                <goal>run</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

我们应该注意，此设置确实需要AngularCLI在类路径上可用。将此脚本推送到没有该依赖项的环境将导致构建失败。

现在让我们开始构建我们的Angular应用程序！

##4.角度

在本教程的这一部分中，我们在页面中构建了一个身份验证机制。我们使用基本身份验证并遵循一个简单的流程来使其工作。

用户有一个登录表单，他们可以在其中输入用户名和密码。

接下来，我们使用他们的凭据创建一个base64身份验证令牌并请求“/me”端点。端点返回一个包含该用户角色的Principal对象。

最后，我们会将凭据和委托人存储在客户端上，以供后续请求使用。

让我们看看这是如何完成的！

###4.1.模板

在网关项目中，导航到src/main/angular/ui/src/app并打开app.component.html文件。这是Angular加载的第一个模板，将是我们的用户登录后登陆的地方。

在这里，我们将添加一些代码来显示带有登录表单的导航栏：

```html
<nav class="navbar navbar-toggleable-md navbar-inverse fixed-top bg-inverse">
    <button class="navbar-toggler navbar-toggler-right" type="button" 
      data-toggle="collapse" data-target="#navbarCollapse" 
      aria-controls="navbarCollapse" aria-expanded="false" 
      aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
    </button>
    <a class="navbar-brand" href="#">Book Rater 
        <span ngIf="principal.isAdmin()">Admin</span></a>
    <div class="collapse navbar-collapse" id="navbarCollapse">
    <ul class="navbar-nav mr-auto">
    </ul>
    <button ngIf="principal.authenticated" type="button" 
      class="btn btn-link" (click)="onLogout()">Logout</button>
    </div>
</nav>

<div class="jumbotron">
    <div class="container">
        <h1>Book Rater App</h1>
        <p ngIf="!principal.authenticated" class="lead">
        Anyone can view the books.
        </p>
        <p ngIf="principal.authenticated && !principal.isAdmin()" class="lead">
        Users can view and create ratings</p>
        <p ngIf="principal.isAdmin()"  class="lead">Admins can do anything!</p>
    </div>
</div>
```

此代码使用Bootstrap类设置导航栏。嵌入在栏中的是一个内联登录表单。Angular使用此标记与JavaScript动态交互以呈现页面的各个部分并控制诸如表单提交之类的事情。

像(ngSubmit)=”onLogin(f)”这样的语句只是表示当提交表单时调用方法“onLogin(f)”并将表单传递给该函数。在jumbotrondiv中，我们有段落标签，这些标签将根据我们的主要对象的状态动态显示。

接下来，让我们编写支持此模板的Typescript文件。

###4.2.打字稿

从同一目录打开app.component.ts文件。在这个文件中，我们将添加制作模板功能所需的所有打字稿属性和方法：

```javascript
import {Component} from "@angular/core";
import {Principal} from "./principal";
import {Response} from "@angular/http";
import {Book} from "./book";
import {HttpService} from "./http.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    selectedBook: Book = null;
    principal: Principal = new Principal(false, []);
    loginFailed: boolean = false;

    constructor(private httpService: HttpService){}

    ngOnInit(): void {
        this.httpService.me()
          .subscribe((response: Response) => {
              let principalJson = response.json();
              this.principal = new Principal(principalJson.authenticated,
              principalJson.authorities);
          }, (error) => {
              console.log(error);
        });
    }

    onLogout() {
        this.httpService.logout()
          .subscribe((response: Response) => {
              if (response.status === 200) {
                  this.loginFailed = false;
                  this.principal = new Principal(false, []);
                  window.location.replace(response.url);
              }
           }, (error) => {
               console.log(error);
       });
    }
}
```

此类与Angular生命周期方法ngOnInit()挂钩。在这个方法中，我们调用/me端点来获取用户的当前角色和状态。这决定了用户在主页上看到的内容。只要创建此组件，就会触发此方法，这是在我们的应用程序中检查用户属性以获取权限的好时机。

我们还有一个onLogout()方法，用于注销我们的用户并将该页面的状态恢复到其原始设置。

不过这里有一些神奇的事情发生。在构造函数中声明的httpService属性。Angular在运行时将这个属性注入到我们的类中。Angular管理服务类的单例实例，并使用构造函数注入来注入它们，就像Spring一样！

接下来，我们需要定义HttpService类。

###4.3.HTTP服务

在同一目录中创建一个名为“http.service.ts”的文件。在此文件中添加此代码以支持登录和注销方法：

```javascript
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Response, Http, Headers, RequestOptions} from "@angular/http";
import {Book} from "./book";
import {Rating} from "./rating";

@Injectable()
export class HttpService {

    constructor(private http: Http) { }

    me(): Observable<Response> {
        return this.http.get("/me", this.makeOptions())
    }

    logout(): Observable<Response> {
        return this.http.post("/logout", '', this.makeOptions())
    }

    private makeOptions(): RequestOptions {
        let headers = new Headers({'Content-Type': 'application/json'});
        return new RequestOptions({headers: headers});
    }
}
```

在这个类中，我们使用Angular的DI构造注入另一个依赖项。这次是Http类。此类处理所有HTTP通信并由框架提供给我们。

这些方法各自使用Angular 的 HTTP 库执行 HTTP 请求。每个请求还在标头中指定一种内容类型。

现在我们还需要做一件事来让HttpService在依赖注入系统中注册。打开app.module.ts文件并找到 providers 属性。将HttpService添加到该数组。结果应如下所示：

```javascript
providers: [HttpService],
```

### 4.4. 添加主体

接下来，让我们在 Typescript 代码中添加我们的 Principal DTO 对象。在同一目录中添加一个名为“principal.ts”的文件并添加以下代码：

```javascript
export class Principal {
    public authenticated: boolean;
    public authorities: Authority[] = [];
    public credentials: any;

    constructor(authenticated: boolean, authorities: any[], credentials: any) {
        this.authenticated = authenticated;
        authorities.map(
          auth => this.authorities.push(new Authority(auth.authority)))
        this.credentials = credentials;
  }

    isAdmin() {
        return this.authorities.some(
          (auth: Authority) => auth.authority.indexOf('ADMIN') > -1)
    }
}

export class Authority {
    public authority: String;

    constructor(authority: String) {
        this.authority = authority;
    }
}
```

我们添加了Principal类和一个Authority类。这是两个 DTO 类，很像 Spring 应用程序中的 POJO。因此，我们不需要在 angular 中向 DI 系统注册这些类。

接下来，让我们配置一个重定向规则，将未知请求重定向到我们应用程序的根目录。

### 4.5. 404处理

让我们返回到网关服务的Java代码。在GatewayApplication类所在的位置添加一个名为ErrorPageConfig的新类：

```java
@Component
public class ErrorPageConfig implements ErrorPageRegistrar {
 
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,
          "/home/index.html"));
    }

}
```

此类将识别任何 404 响应并将用户重定向到“/home/index.html”。在单页应用程序中，这就是我们处理所有不流向专用资源的流量的方式，因为客户端应该处理所有可导航的路由。

现在我们准备启动这个应用程序，看看我们构建了什么！

### 4.6. 构建和查看

现在从网关文件夹运行“ mvn compile ”。这将编译我们的 java 源代码并将 Angular 应用程序构建到 public 文件夹。让我们启动其他云应用程序：config、discovery和zipkin。然后运行网关项目。当服务启动时，导航到http://localhost:8080以查看我们的应用程序。我们应该看到这样的东西：

[![书评人](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-landing-1-300x124.png)](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-landing-1.png)

接下来，让我们点击登录页面的链接：

[![ng2登录1](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-login-1-300x127.png)](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-login-1.png)

使用用户/密码凭据登录。单击“登录”，我们应该被重定向到加载单页应用程序的 /home/index.html。

[![图书评级应用程序](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-user-1-3-300x45.png)](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-user-1-3.png)

看起来我们的超大屏幕显示我们已经以用户身份登录！现在通过单击右上角的链接注销，这次使用admin/admin凭据登录。

[![预订率应用程序](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-admin-1-1-300x49.png)](https://www.baeldung.com/wp-content/uploads/2017/05/ng2-admin-1-1.png)

看起来不错！现在我们以管理员身份登录。

## 5.总结

在本文中，我们看到了将单页应用程序集成到我们的云系统中是多么容易。我们采用了现代框架并将有效的安全配置集成到我们的应用程序中。

使用这些示例，尝试编写一些代码来调用book-service或rating-service。由于我们现在有进行 HTTP 调用和将数据连接到模板的示例，这应该相对容易。