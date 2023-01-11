## 1. 概述

在本教程中，我们将使用Spring Security创建一个登录页面，并通过Angular访问我们的后端服务：

## 2. Spring Security配置

首先，我们配置Rest API的安全规则和用户名密码：

```java

@Configuration
@EnableWebSecurity
public class BasicAuthConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("password")
                .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/login").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }
}
```

然后，我们新建一个Controller，它包含两个端点：一个用于登录，另一个用于获取用户数据：

```java

@RestController
@CrossOrigin
public class UserController {

    @RequestMapping("/login")
    public boolean login(@RequestBody User user) {
        return user.getUserName().equals("user") && user.getPassword().equals("password");
    }

    @RequestMapping("/user")
    public Principal user(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization").substring("Basic".length()).trim();
        return () -> new String(Base64.getDecoder().decode(authToken)).split(":")[0];
    }
}

@Setter
@Getter
public class User {
    private String userName;
    private String password;
}
```

## 3. Angular客户端

现在我们使用不同版本的Angular客户端设置登录页面。

我们在这里介绍的例子使用npm进行依赖管理，使用nodejs运行应用程序。

**Angular使用单页面架构，其中所有子组件(在我们的例子中是login和home组件)都被注入到一个公共的父DOM中**。

与使用JavaScript的AngularJS不同，Angular 2及更高版本使用TypeScript作为其主要语言。
因此，该应用程序还需要某些支持文件才能正常工作。

由于Angular的增量增强，所需的文件因版本而异。

+ systemjs.config.js – 系统配置(版本2)
+ package.json – node模块依赖项(从版本2开始)
+ tsconfig.json - 根级Typescript配置(版本2及以上)
+ tsconfig.app.json – 应用程序级别的Typescript配置(版本4及以上)
+ .angular-cli.json – Angular CLI配置(版本4和5)
+ angular.json – Angular CLI配置(从版本6开始)

## 4. 登录页面

### 4.1 使用AngularJS

让我们创建index.html文件并向其中添加相关依赖项：

```html
<!DOCTYPE html>
<html ng-app="app" lang="en">

<head>
    <title></title></head>
<body>
<div ng-view></div>
<script src="//code.jquery.com/jquery-3.1.1.min.js"></script>
<script src="//code.angularjs.org/1.6.0/angular.min.js"></script>
<script src="//code.angularjs.org/1.6.0/angular-route.min.js"></script>
<script src="app.js"></script>
<script src="home/home.controller.js"></script>
<script src="login/login.controller.js"></script>
</body>
</html>
```

由于这是一个单页应用程序，所有子组件将根据路由逻辑添加到具有ng-view属性的div元素中。

现在，让我们创建app.js，它定义了URL到组件的映射：

```javascript
(function () {
    'use strict';

    angular.module('app', ['ngRoute'])
            .config(config)
            .run(run);

    config.$inject = ['$routeProvider', '$locationProvider'];

    function config($routeProvider, $locationProvider) {
        $routeProvider
                .when('/', {
                    controller: 'HomeController',
                    templateUrl: 'home/home.view.html',
                    controllerAs: 'vm'
                })
                .when('/login', {
                    controller: 'LoginController',
                    templateUrl: 'login/login.view.html',
                    controllerAs: 'vm'
                })
                .otherwise({redirectTo: '/login'});
    }

    run.$inject = ['$rootScope', '$location', '$http', '$window'];

    function run($rootScope, $location, $http, $window) {
        let userData = $window.sessionStorage.getItem('userData');
        if (userData) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + JSON.parse(userData).authData;
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            let restrictedPage = $.inArray($location.path(), ['/login']) === -1;
            let loggedIn = $window.sessionStorage.getItem('userData');
            if (restrictedPage && !loggedIn) {
                $location.path('/login');
            }
        });
    }
})();
```

登录组件由两个文件组成，login.view.html和login.controller.js。

我们来看第一个：

```html

<div>
    <h2>Login</h2>
    <form name="form" ng-submit="vm.login()" role="form">
        <div>
            <label for="username">Username</label>
            <input type="text" name="username" id="username" ng-model="vm.username" required/>
            <span ng-show="form.username.$dirty && form.username.$error.required">Username is required</span>
        </div>
        <div>
            <label for="password">Password</label>
            <input type="password" name="password" id="password" ng-model="vm.password" required/>
            <span ng-show="form.password.$dirty && form.password.$error.required">Password is required</span>
        </div>
        <div class="form-actions">
            <button type="submit" ng-disabled="form.$invalid || vm.dataLoading">Login</button>
        </div>
    </form>
</div>
```

第二个：

```javascript
(function () {
    'use strict';

    angular
            .module('app')
            .controller('LoginController', LoginController);

    LoginController.$inject = ['$location', '$window', '$http'];

    function LoginController($location, $window, $http) {
        var vm = this;
        vm.login = login;

        (function initController() {
            $window.localStorage.setItem('token', '');
        })();

        function login() {
            $http({
                url: 'http://localhost:8082/login',
                method: "POST",
                data: {'userName': vm.username, 'password': vm.password}
            }).then(function (response) {
                if (response.data) {
                    var token = $window.btoa(vm.username + ':' + vm.password);
                    var userData = {
                        userName: vm.username,
                        authData: token
                    }
                    $window.sessionStorage.setItem('userData', JSON.stringify(userData));
                    $http.defaults.headers.common['Authorization'] = 'Basic ' + token;
                    $location.path('/');
                } else {
                    alert("Authentication failed.")
                }
            });
        }
    }
})();
```

控制器将通过传递用户名和密码来调用REST服务。
身份验证成功后，它将对用户名和密码进行编码，并将编码后的令牌存储在session存储中以供将来使用。

与登录组件类似，home组件也包含两个文件，即home.view.html：

```html
<h1>Hi {{vm.user}}!</h1>
<p>You're logged in!!</p>
<p><a href="#!/login" class="btn btn-primary" ng-click="logout()">Logout</a></p>
```

和home.controller.js：

```javascript
(function () {
    'use strict';

    angular.module('app')
            .controller('HomeController', HomeController);

    HomeController.$inject = ['$window', '$http', '$scope'];

    function HomeController($window, $http, $scope) {
        var vm = this;

        vm.user = null;

        initController();

        function initController() {

            $http({
                url: 'http://localhost:8082/user',
                method: "GET"
            }).then(function (response) {
                vm.user = response.data.name;
            }, function (error) {
                console.log(error);
            });
        }

        $scope.logout = function () {
            $window.sessionStorage.setItem('userData', '');
            $http.defaults.headers.common['Authorization'] = 'Basic';
        }
    }
})();
```

HomeController通过传递Authorization头来请求用户数据。
仅当token有效时，我们的REST服务才会返回用户数据。

现在让我们安装http-server来运行Angular应用程序：

```shell
npm install http-server --save
```

安装完成后，我们可以进入到项目根文件夹，并执行命令：

```shell
http-server -o
```

### 4.2 Angular 2，4，5版本

版本2中的index.html与AngularJS版本略有不同：

```html
<!DOCTYPE html>
<html>
<head>
    <base href="/"/>
    <script src="node_modules/core-js/client/shim.min.js"></script>
    <script src="node_modules/zone.js/dist/zone.js"></script>
    <script src="node_modules/systemjs/dist/system.src.js"></script>
    <script src="systemjs.config.js"></script>
    <script>
        System.import('app').catch(function (err) {
            console.error(err);
        });
    </script>
</head>
<body>
<app>Loading...</app>
</body>
</html>
```

main.ts是应用程序的主要入口点。它引导应用程序模块，因此浏览器加载登录页面：

```ts
platformBrowserDynamic().bootstrapModule(AppModule);
```

app.routing.ts负责应用程序路由：

```typescript
const appRoutes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'login', component: LoginComponent},
    {path: '**', redirectTo: ''}
];

export const routing = RouterModule.forRoot(appRoutes);
```

app.module.ts声明组件并导入相关模块：

```typescript
@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        routing
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        LoginComponent
    ],
    bootstrap: [AppComponent]
})

export class AppModule {
}
```

由于我们创建的是一个单页应用程序，让我们创建一个根组件，将所有子组件添加到其中：

```typescript
@Component({
    selector: 'app',
    templateUrl: './app/app.component.html'
})

export class AppComponent {
}
```

app.component.html将只有一个<router-outlet\>标签，Angular使用这个标签作为它的位置路由机制。

现在让我们在login.component.ts中创建登录组件及其对应的模板：

```typescript
@Component({
    selector: 'login',
    templateUrl: './app/login/login.component.html'
})

export class LoginComponent implements OnInit {
    model: any = {};

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private http: Http) {
    }

    ngOnInit() {
        sessionStorage.setItem('token', '');
    }

    login() {
        let url = 'http://localhost:8082/login';
        let result = this.http.post(url, {
            userName: this.model.username,
            password: this.model.password
        }).map(res => res.json()).subscribe(isValid => {
            if (isValid) {
                sessionStorage.setItem('token', btoa(this.model.username + ':' + this.model.password));
                this.router.navigate(['']);
            } else {
                alert("Authentication failed.");
            }
        });
    }
}
```

最后，让我们看看login.component.html：

```html

<div class="col-md-6 col-md-offset-3">
    <h2>Login</h2>
    <form name="form" (ngSubmit)="f.form.valid && login()" #f="ngForm" novalidate>
        <div class="form-group" [ngClass]="{ 'has-error': f.submitted && !username.valid }">
            <label for="username">Username</label>
            <input type="text" class="form-control" name="username" [(ngModel)]="model.username" #username="ngModel"
                   required/>
            <div *ngIf="f.submitted && !username.valid" class="help-block">Username is required</div>
        </div>
        <div class="form-group" [ngClass]="{ 'has-error': f.submitted && !password.valid }">
            <label for="password">Password</label>
            <input type="password" class="form-control" name="password" [(ngModel)]="model.password" #password="ngModel"
                   required/>
            <div *ngIf="f.submitted && !password.valid" class="help-block">Password is required</div>
        </div>
        <div class="form-group">
            <button [disabled]="loading" class="btn btn-primary">Login</button>
        </div>
    </form>
</div>
```

### 4.3 Angular 6

Angular团队在版本6中进行了一些增强，由于这些更改，我们的示例与其他版本相比也会有些不同。
我们在示例中对版本6的唯一更改是服务调用部分。

**版本6从@angulal/common/http导入HttpClientModule，而不是HttpModule**。

服务调用部分也将与旧版本略有不同：

```typescript
this.http.post<Observable<boolean>>(url, {
    userName: this.model.username,
    password: this.model.password
}).subscribe(isValid => {
    if (isValid) {
        sessionStorage.setItem(
            'token',
            btoa(this.model.username + ':' + this.model.password)
        );
        this.router.navigate(['']);
    } else {
        alert("Authentication failed.")
    }
});
```

## 5. 总结

我们具体演示了如何使用Angular实现Spring Security登录页面。
从版本4开始，我们可以使用Angular CLI项目来轻松开发和测试。