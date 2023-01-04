## 1. 概述

验证从来都不像我们预期的那么简单。当然，验证用户在应用程序中输入的值对于保持我们数据的完整性非常重要。

在Web应用程序的上下文中，数据输入通常使用HTML表单完成，并且需要客户端和服务器端验证。

在本教程中，我们将了解使用AngularJS实现表单输入的客户端验证和使用Spring MVC框架实现服务器端验证。

本文重点介绍Spring MVC。我们的文章[Spring Boot中的验证](https://www.baeldung.com/spring-boot-bean-validation) 描述了如何在Spring Boot中进行验证。

## 2. Maven依赖

首先，让我们添加以下依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.3.7.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.4.0.Final</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

可以从Maven Central下载最新版本的[spring-webmvc](https://search.maven.org/classic/#search|ga|1|a%3A"spring-webmvc")、[hibernate-validator](https://search.maven.org/classic/#search|ga|1|a%3A"hibernate-validator")和[jackson-databind](https://search.maven.org/classic/#search|ga|1|a%3A"jackson-databind")。

## 3. 使用Spring MVC进行验证

应用程序不应该仅仅依赖于客户端验证，因为这很容易被规避。为防止保存不正确或恶意的值或导致应用程序逻辑执行不当，在服务器端验证输入值也很重要。

Spring MVC通过使用JSR 349 Bean验证规范注解提供对服务器端验证的支持。对于这个例子，我们将使用规范的参考实现，即hibernate-validator。

### 3.1 数据模型

让我们创建一个User类，它的属性用适当的验证注解进行注解：

```java
public class User {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 4, max = 15)
    private String password;

    @NotBlank
    private String name;

    @Min(18)
    @Digits(integer = 2, fraction = 0)
    private int age;

    // standard constructor, getters, setters
}
```

上面使用的注解属于JSR 349规范，但@Email和@NotBlank除外，它们特定于hibernate-validator库。

### 3.2 Spring MVC控制器

让我们创建一个定义/user端点的控制器类，它将用于将新的User对象保存到List。

为了能够对通过请求参数接收到的用户对象进行验证，声明之前必须带有@Valid注解，并且验证错误将保存在BindingResult实例中。

要确定对象是否包含无效值，我们可以使用BindingResult的hasErrors()方法。

如果hasErrors()返回true，我们可以返回一个JSON数组，其中包含与未通过验证相关的错误消息。否则，我们会将对象添加到列表中：

```java
@PostMapping(value = "/user")
@ResponseBody
public ResponseEntity<Object> saveUser(@Valid User user, BindingResult result, Model model) {
    if (result.hasErrors()) {
        List<String> errors = result.getAllErrors().stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.OK);
    } else {
        if (users.stream().anyMatch(it -> user.getEmail().equals(it.getEmail()))) {
            return new ResponseEntity<>(
              Collections.singletonList("Email already exists!"), 
              HttpStatus.CONFLICT);
        } else {
            users.add(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
}
```

如你所见，服务器端验证增加了能够执行客户端无法执行的额外检查的优势。

在我们的例子中，我们可以验证是否已经存在具有相同电子邮件地址的用户，如果存在，则返回409 CONFLICT状态。

我们还需要定义我们的用户列表并使用一些值对其进行初始化：

```java
private List<User> users = Arrays.asList(
  new User("ana@yahoo.com", "pass", "Ana", 20),
  new User("bob@yahoo.com", "pass", "Bob", 30),
  new User("john@yahoo.com", "pass", "John", 40),
  new User("mary@yahoo.com", "pass", "Mary", 30));
```

我们还添加一个映射，用于将用户列表检索为JSON对象：

```java
@GetMapping(value = "/users")
@ResponseBody
public List<User> getUsers() {
    return users;
}
```

我们在Spring MVC控制器中需要的最后一项是返回应用程序主页的映射：

```java
@GetMapping("/userPage")
public String getUserProfilePage() {
    return "user";
}
```

我们将在AngularJS部分更详细地查看user.html页面。

### 3.3 Spring MVC配置

让我们向我们的应用程序添加一个基本的MVC配置：

```java
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "cn.tuyucheng.taketoday.springmvcforms")
class ApplicationConfiguration implements WebMvcConfigurer {

    @Override
    public void configureDefaultServletHandling(
      DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public InternalResourceViewResolver htmlViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/html/");
        bean.setSuffix(".html");
        return bean;
    }
}
```

### 3.4 初始化应用程序

让我们创建一个实现WebApplicationInitializer接口的类来运行我们的应用程序：

```java
public class WebInitializer implements WebApplicationInitializer {

    public void onStartup(ServletContext container) throws ServletException {

        AnnotationConfigWebApplicationContext ctx
          = new AnnotationConfigWebApplicationContext();
        ctx.register(ApplicationConfiguration.class);
        ctx.setServletContext(container);
        container.addListener(new ContextLoaderListener(ctx));

        ServletRegistration.Dynamic servlet 
          = container.addServlet("dispatcher", new DispatcherServlet(ctx));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/");
    }
}
```

### 3.5 使用Curl测试Spring Mvc验证

在我们实现AngularJS客户端部分之前，我们可以使用cURL和命令测试我们 API：

```bash
curl -i -X POST -H "Accept:application/json" 
  "localhost:8080/spring-mvc-forms/user?email=aaa&password=12&age=12"
```

响应是一个包含默认错误消息的数组：

```bash
[
    "not a well-formed email address",
    "size must be between 4 and 15",
    "may not be empty",
    "must be greater than or equal to 18"
]
```

## 4. AngularJS验证

客户端验证有助于创建更好的用户体验，因为它为用户提供有关如何成功提交有效数据的信息，并使他们能够继续与应用程序交互。

AngularJS库非常支持在表单字段上添加验证要求、处理错误消息以及设置有效和无效表单的样式。

首先，让我们创建一个注入ngMessages模块的AngularJS模块，该模块用于验证消息：

```javascript
var app = angular.module('app', ['ngMessages']);
```

接下来，让我们创建一个AngularJS服务和控制器，它们将使用在上一节中构建的API。

### 4.1 AngularJS服务

我们的服务将有两种调用MVC控制器方法的方法，一种用于保存用户，一种用于检索用户列表：

```javascript
app.service('UserService',['$http', function ($http) {

    this.saveUser = function saveUser(user){
        return $http({
            method: 'POST',
            url: 'user',
            params: {email:user.email, password:user.password,
                name:user.name, age:user.age},
            headers: 'Accept:application/json'
        });
    }

    this.getUsers = function getUsers(){
        return $http({
            method: 'GET',
            url: 'users',
            headers:'Accept:application/json'
        }).then( function(response){
            return response.data;
        } );
    }
}]);
```

### 4.2.AngularJS控制器

UserCtrl控制器注入UserService，调用服务方法并处理响应和错误消息：

```javascript
app.controller('UserCtrl', ['$scope','UserService', function ($scope,UserService) {

    $scope.submitted = false;

    $scope.getUsers = function() {
        UserService.getUsers().then(function(data) {
            $scope.users = data;
        });
    }

    $scope.saveUser = function() {
        $scope.submitted = true;
        if ($scope.userForm.$valid) {
            UserService.saveUser($scope.user)
                .then (function success(response) {
                        $scope.message = 'User added!';
                        $scope.errorMessage = '';
                        $scope.getUsers();
                        $scope.user = null;
                        $scope.submitted = false;
                    },
                    function error(response) {
                        if (response.status == 409) {
                            $scope.errorMessage = response.data.message;
                        }
                        else {
                            $scope.errorMessage = 'Error adding user!';
                        }
                        $scope.message = '';
                    });
        }
    }

    $scope.getUsers();
}]);
```

我们可以在上面的例子中看到，只有当userForm的$valid属性为真时，服务方法才会被调用。尽管如此，在这种情况下，还有对重复电子邮件的额外检查，这只能在服务器上完成，并在error()函数中单独处理。

另外，请注意定义了一个submitted变量，它会告诉我们表单是否已提交。

最初，此变量将为false，在调用saveUser()方法时，它变为true。如果我们不希望在用户提交表单之前显示验证消息，我们可以使用submitted变量来防止这种情况发生。

### 4.3 使用AngularJS验证的表单

为了使用AngularJS库和我们的AngularJS模块，我们需要将脚本添加到我们的user.html页面：

```html
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular.min.js">
</script>
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.0/angular-messages.js">
</script>
<script src="js/app.js"></script>
```

然后我们可以通过设置ng-app和ng-controller属性来使用我们的模块和控制器：

```html
<body ng-app="app" ng-controller="UserCtrl">
```

让我们创建我们的HTML表单：

```html
<form name="userForm" method="POST" novalidate 
  ng-class="{'form-error':submitted}" ng-submit="saveUser()" >
...
</form>
```

请注意，我们必须在表单上设置novalidate属性以防止默认的HTML5验证并将其替换为我们自己的。

如果提交的变量的值为true，则ng-class属性将表单错误CSS类动态添加到表单。

ng-submit属性定义了提交表单时将调用的AngularJS控制器函数。使用ng-submit而不是ng-click的优点是它还响应使用ENTER键提交表单。

现在让我们为用户属性添加四个输入字段：

```html
<label class="form-label">Email:</label>
<input type="email" name="email" required ng-model="user.email" class="form-input"/>

<label class="form-label">Password:</label>
<input type="password" name="password" required ng-model="user.password"
       ng-minlength="4" ng-maxlength="15" class="form-input"/>

<label class="form-label">Name:</label>
<input type="text" name="name" ng-model="user.name" ng-trim="true"
       required class="form-input"/>

<label class="form-label">Age:</label>
<input type="number" name="age" ng-model="user.age" ng-min="18"
       class="form-input" required/>
```

每个输入字段都通过ng-model属性绑定到用户变量的属性。

为了设置验证规则，我们使用HTML5必需属性和几个AngularJS特定的属性：ng-minglength、ng-maxlength、ng-min和ng-trim。

对于电子邮件字段，我们还使用值为email的类型属性进行客户端电子邮件验证。

为了添加与每个字段对应的错误消息，AngularJS提供了ng-messages指令，它循环遍历输入的$errors对象并根据每个验证规则显示消息。

让我们在输入定义之后为电子邮件字段添加指令：

```html
<div ng-messages="userForm.email.$error"
     ng-show="submitted && userForm.email.$invalid" class="error-messages">
    <p ng-message="email">Invalid email!</p>
    <p ng-message="required">Email is required!</p>
</div>
```

可以为其他输入字段添加类似的错误消息。

我们可以使用带有布尔表达式的ng-show属性来控制何时为电子邮件字段显示指令。在我们的示例中，我们在字段具有无效值时显示指令，这意味着$invalid属性为true，并且提交的变量也是true。

对于一个字段，一次只会显示一条错误消息。

我们还可以在输入字段之后添加一个复选标记(由十六进制代码字符✓表示)以防该字段有效，具体取决于$valid属性：

```html
<div class="check" ng-show="userForm.email.$valid">✓</div>
```

AngularJS验证还支持使用CSS类(例如ng-valid和ng-invalid或更具体的类，例如ng-invalid-required和ng-invalid-minlength)进行样式设置。

让我们为表单的表单错误类中的无效输入添加CSS属性border-color:red：

```css
.form-error input.ng-invalid {
    border-color:red;
}
```

我们还可以使用CSS类以红色显示错误消息：

```css
.error-messages {
    color:red;
}
```

将所有内容放在一起后，让我们看一个示例，说明当填写了有效值和无效值的混合时，我们的客户端表单验证将如何显示：

<img src="../assets/img_3.png">

## 5. 总结

在本教程中，我们展示了如何使用AngularJS和Spring MVC结合客户端和服务器端验证。

与往常一样，可以在[GitHub上](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-mvc-forms-jsp)找到示例的完整源代码。

要查看应用程序，请在运行后访问/userPage URL。