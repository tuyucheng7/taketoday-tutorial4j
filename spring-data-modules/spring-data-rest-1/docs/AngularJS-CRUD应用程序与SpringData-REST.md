## 1. 概述

在本教程中，我们创建一个简单的CRUD应用程序示例，使用AngularJS作为前端，使用Spring Data REST作为后端。

## 2. 创建REST数据服务

为了创建对持久性的支持，我们将使用Spring Data REST规范，使我们能够对数据模型执行CRUD操作。

你可以在[Spring Data REST简介](SpringData-REST简介.md)中找到有关如何设置REST端点的所有必要信息。在本文中，我们重用在前文中构建的项目。对于持久层，我们使用内存数据库H2。

作为数据模型，上一篇文章定义了一个WebsiteUser类，具有id、name和email属性以及一个名为UserRepository的Repository接口。

定义此接口指示Spring创建对公开REST集合资源和元素资源的支持。现在让我们仔细看看我们可用的端点，稍后我们将从AngularJS调用这些端点。

### 2.1 集合资源

我们可以通过端点/users获得所有用户的集合，可以使用GET方法调用此URL，并将返回以下形式的JSON对象：

```json
{
    "_embedded": {
        "users": [
            {
                "name": "Bryan",
                "age": 20,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/users/1"
                    },
                    "User": {
                        "href": "http://localhost:8080/users/1"
                    }
                }
            },
            ...
        ]
    }
}
```

### 2.2 元素资源

可以通过使用不同的HTTP方法和请求负载访问/users/{userID}形式的URL来操纵单个WebsiteUser对象。

为了检索WebsiteUser对象，我们可以使用GET方法访问/users/{userID}，这将返回以下形式的JSON对象：

```json
{
    "name": "Bryan",
    "email": "bryan@yahoo.com",
    "_links": {
        "self": {
            "href": "http://localhost:8080/users/1"
        },
        "User": {
            "href": "http://localhost:8080/users/1"
        }
    }
}
```

要添加新的WebsiteUser，我们需要使用POST方法调用/users。新WebsiteUser记录的属性将作为JSON对象添加到请求正文中：

```java
{name: "Bryan", email: "bryan@yahoo.com"}
```

如果没有出现错误，此URL将返回状态码“201 CREATED”。

如果我们想要更新WebsiteUser记录的属性，我们需要使用PATCH方法和包含新值的请求主体调用URL “/users/{UserID}”：

```java
{name: "Bryan", email: "bryan@gmail.com"}
```

要删除WebsiteUser记录，我们可以使用DELETE方法调用URL “/users/{UserID}”。如果没有错误，则返回状态码“204(NO CONTENT)”。

### 2.3 MVC配置

我们还添加一个基本的MVC配置以在我们的应用程序中显示html文件：

```java
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    
    public MvcConfig(){
        super();
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> enableDefaultServlet() {
        return (factory) -> factory.setRegisterDefaultServlet(true);
    }
}
```

### 2.4 允许跨源请求

如果我们想单独部署AngularJS前端应用程序而不是REST API，那么我们需要启用跨源请求。

Spring Data REST从1.5.0.RELEASE版本开始添加了对此的支持。要允许来自不同域的请求，你所要做的就是将@CrossOrigin注解添加到Repository：

```java
@CrossOrigin
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends CrudRepository<WebsiteUser, Long> {
}
```

因此，在来自REST端点的每个响应中，都会添加一个Access-Control-Allow-Origin标头。

## 3. 创建AngularJS客户端

为了创建我们的CRUD应用程序的前端，我们将使用AngularJS，它可以简化前端应用程序的创建。为了使用AngularJS，我们首先需要在我们的html页面中包含angular.min.js文件：

```html
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular.min.js">
</script>
```

接下来，我们需要创建一个Angular模块、控制器和服务，它将调用REST端点并显示返回的数据。

这些将被放置在一个名为app.js的 JavaScript 文件中，该文件也需要包含在users.html页面中：

```html
<script src="view/app.js"></script>
```

### 3.1 Angular服务

首先，我们创建一个名为UserCRUDService的Angular服务，它将使用注入的AngularJS $http服务来调用服务器，每个调用都放置在一个单独的方法中。

下面是使用/users/{userID}端点定义通过id检索用户的方法：

```javascript
app.service('UserCRUDService', ['$http', function ($http) {

    this.getUser = function getUser(userId) {
        return $http({
            method: 'GET',
            url: 'users/' + userId
        });
    }
}])
```

接下来，我们定义addUser方法，它向/users URL发出POST请求并在data属性中发送用户值：

```javascript
this.addUser = function addUser(name, email) {
    return $http({
        method : 'POST',
        url : 'users',
        data : {
            name : name,
            email: email
        }
    });
}
```

updateUser方法类似于上面的方法，除了另外的id参数和使用的请求方式为PATCH：

```javascript
this.updateUser = function updateUser(id, name, email) {
    return $http({
        method : 'PATCH',
        url : 'users/' + id,
        data : {
            name : name,
            email: email
        }
    });
}
```

删除WebsiteUser记录的方法将发出DELETE请求：

```javascript
this.deleteUser = function deleteUser(id) {
    return $http({
        method : 'DELETE',
        url : 'users/' + id
    })
}
```

最后，下面是检索整个用户集合的方法：

```javascript
this.getAllUsers = function getAllUsers() {
    return $http({
        method : 'GET',
        url : 'users'
    });
}
```

所有这些Service方法都将由AngularJS控制器调用。

### 3.2 Angular控制器

我们将创建一个UserCRUDCtrl AngularJS控制器，它注入一个UserCRUDService并使用Service方法从服务器获取响应，处理成功和错误情况，并设置包含响应数据的$scope变量以在HTML页面中显示它.

让我们来看看getUser()函数，它调用getUser(userId) Service函数，并在成功和错误时定义两个回调方法。如果服务器请求成功，则将响应保存在user变量中；否则，将处理错误消息：

```javascript
app.controller('UserCRUDCtrl', ['$scope', 'UserCRUDService', function ($scope, UserCRUDService) {

    $scope.getUser = function () {
        var id = $scope.user.id;
        UserCRUDService.getUser($scope.user.id)
            .then(function success(response) {
                    $scope.user = response.data;
                    $scope.user.id = id;
                    $scope.message = '';
                    $scope.errorMessage = '';
                },
                function error(response) {
                    $scope.message = '';
                    if (response.status === 404) {
                        $scope.errorMessage = 'User not found!';
                    } else {
                        $scope.errorMessage = "Error getting user!";
                    }
                });
    }
}]);
```

addUser()函数将调用相应的Service函数并处理响应：

```javascript
$scope.addUser = function () {
    if ($scope.user != null && $scope.user.name) {
        UserCRUDService.addUser($scope.user.name, $scope.user.email)
            .then(function success(response) {
                    $scope.message = 'User added!';
                    $scope.errorMessage = '';
                },
                function error(response) {
                    $scope.errorMessage = 'Error adding user!';
                    $scope.message = '';
                });
    } else {
        $scope.errorMessage = 'Please enter a name!';
        $scope.message = '';
    }
}
```

updateUser()和deleteUser()函数与上面的类似：

```javascript
$scope.updateUser = function () {
    UserCRUDService.updateUser($scope.user.id, $scope.user.name, $scope.user.email)
        .then(function success(response) {
                $scope.message = 'User data updated!';
                $scope.errorMessage = '';
            },
            function error(response) {
                $scope.errorMessage = 'Error updating user!';
                $scope.message = '';
            });
}

$scope.deleteUser = function () {
    UserCRUDService.deleteUser($scope.user.id)
        .then(function success(response) {
                $scope.message = 'User deleted!';
                $scope.user = null;
                $scope.errorMessage = '';
            },
            function error(response) {
                $scope.errorMessage = 'Error deleting user!';
                $scope.message = '';
            })
}
```

最后，下面是检索用户集合的函数，并将其存储在users变量中：

```javascript
$scope.getAllUsers = function () {
    UserCRUDService.getAllUsers()
        .then(function success(response) {
                $scope.users = response.data._embedded.users;
                $scope.message = '';
                $scope.errorMessage = '';
            },
            function error(response) {
                $scope.message = '';
                $scope.errorMessage = 'Error getting users!';
            });
}
```

### 3.3 HTML页面

users.html页面将使用上一节中定义的控制器函数和存储的变量。

首先，为了使用Angular模块，我们需要设置ng-app属性：

```html
<html ng-app="app">
```

然后，为了避免每次使用控制器的函数时都调用UserCRUDCtrl.getUser()，我们可以将HTML元素包装在一个带有ng-controller属性集的div中：

```html
<div ng-controller="UserCRUDCtrl">
```

我们创建一个表单，该表单将输入和显示要操纵的WebiteUser对象的值。其中每一个都有一个ng-model属性集，该属性集将其绑定到属性的值：

```html
<table>
    <tr>
        <td width="100">ID:</td>
        <td><input type="text" id="id" ng-model="user.id"/></td>
    </tr>
    <tr>
        <td width="100">Name:</td>
        <td><input type="text" id="name" ng-model="user.name"/></td>
    </tr>
    <tr>
        <td width="100">Age:</td>
        <td><input type="text" id="age" ng-model="user.email"/></td>
    </tr>
</table>
```

例如，将id输入绑定到user.id变量意味着每当输入的值更改时，都会在user.id变量中设置该值，反之亦然。

接下来，我们使用ng-click属性来定义将触发调用定义的每个CRUD控制器函数的链接：

```html
<a ng-click="getUser(user.id)">Get User</a>
<a ng-click="updateUser(user.id,user.name,user.email)">Update User</a>
<a ng-click="addUser(user.name,user.email)">Add User</a>
<a ng-click="deleteUser(user.id)">Delete User</a>
```

最后，我们按名称完整显示用户列表：

```html
<a ng-click="getAllUsers()">Get all Users</a><br/><br/>
<div ng-repeat="usr in users">
{{usr.name}} {{usr.email}}
```

## 4. 总结

在本教程中，我们演示了如何使用AngularJS和Spring Data REST规范创建CRUD应用程序。要运行该应用程序，你可以使用命令mvn spring-boot:run并访问URL /users.html。