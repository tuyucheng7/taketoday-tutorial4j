## 1. 概述

在本文中，我们将主要关注在Spring REST API和简单的 AngularJS 前端中实现服务器端分页。

我们还将探索 Angular 中一个常用的表格网格，名为[UI Grid](http://ui-grid.info/)。

## 2.依赖关系

这里我们详细介绍本文需要的各种依赖。

### 2.1. JavaScript

为了让 Angular UI Grid 正常工作，我们需要在 HTML 中导入以下脚本。

-   [角度JS(1.5.8)](https://ajax.googleapis.com/ajax/libs/angularjs/1.5.8/angular.min.js)

-   [角度 UI 网格](https://cdn.rawgit.com/angular-ui/bower-ui-grid/master/ui-grid.min.js)

### 2.2. 行家

对于我们的后端，我们将使用Spring Boot，因此我们需要以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

注意：此处未指定其他依赖项，有关完整列表，请查看[GitHub](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-rest-angular)项目中的完整pom.xml 。

## 三、关于申请

该应用程序是一个简单的学生目录应用程序，允许用户在分页表格网格中查看学生详细信息。

该应用程序使用Spring Boot并在具有嵌入式数据库的嵌入式 Tomcat 服务器中运行。

最后，在 API 方面，有几种方法可以进行分页，在[此处的 Spring 中的 REST 分页一文中](https://www.baeldung.com/rest-api-pagination-in-spring)有所描述——强烈建议与本文一起阅读。

我们这里的解决方案很简单——在 URI 查询中包含分页信息，如下所示：/student/get?page=1&size=2。

## 4.客户端

首先，我们需要创建客户端逻辑。

### 4.1. 用户界面网格

我们的index.html将包含我们需要的导入和表格网格的简单实现：

```html
<!DOCTYPE html>
<html lang="en" ng-app="app">
    <head>
        <link rel="stylesheet" href="https://cdn.rawgit.com/angular-ui/
          bower-ui-grid/master/ui-grid.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/
          1.5.6/angular.min.js"></script>
        <script src="https://cdn.rawgit.com/angular-ui/bower-ui-grid/
          master/ui-grid.min.js"></script>
        <script src="view/app.js"></script>
    </head>
    <body>
        <div ng-controller="StudentCtrl as vm">
            <div ui-grid="gridOptions" class="grid" ui-grid-pagination>
            </div>
        </div>
    </body>
</html>
```

让我们仔细看看代码：

-   ng-app – 是加载模块app的 Angular 指令。这些下的所有元素都将成为应用程序模块的一部分
-   ng-controller – 是 Angular 指令，它使用别名vm加载控制器StudentCtrl 。这些下的所有元素都将成为StudentCtrl控制器的一部分
-   ui-grid – 是属于 Angular ui-grid的 Angular 指令，使用gridOptions作为其默认设置，gridOptions在app.js中的$scope下声明

### 4.2. AngularJS 模块

让我们首先在app.js中定义模块：

```javascript
var app = angular.module('app', ['ui.grid','ui.grid.pagination']);
```

我们声明了应用程序模块并注入了 ui.grid以启用 UI-Grid 功能；我们还注入了 ui.grid.pagination以启用分页支持。

接下来，我们将定义控制器：

```javascript
app.controller('StudentCtrl', ['$scope','StudentService', 
    function ($scope, StudentService) {
        var paginationOptions = {
            pageNumber: 1,
            pageSize: 5,
        sort: null
        };

    StudentService.getStudents(
      paginationOptions.pageNumber,
      paginationOptions.pageSize).success(function(data){
        $scope.gridOptions.data = data.content;
        $scope.gridOptions.totalItems = data.totalElements;
      });

    $scope.gridOptions = {
        paginationPageSizes: [5, 10, 20],
        paginationPageSize: paginationOptions.pageSize,
        enableColumnMenus:false,
    useExternalPagination: true,
        columnDefs: [
           { name: 'id' },
           { name: 'name' },
           { name: 'gender' },
           { name: 'age' }
        ],
        onRegisterApi: function(gridApi) {
           $scope.gridApi = gridApi;
           gridApi.pagination.on.paginationChanged(
             $scope, 
             function (newPage, pageSize) {
               paginationOptions.pageNumber = newPage;
               paginationOptions.pageSize = pageSize;
               StudentService.getStudents(newPage,pageSize)
                 .success(function(data){
                   $scope.gridOptions.data = data.content;
                   $scope.gridOptions.totalItems = data.totalElements;
                 });
            });
        }
    };
}]);

```

现在让我们看一下$scope.gridOptions中的自定义分页设置：

-   paginationPageSizes – 定义可用的页面大小选项
-   paginationPageSize – 定义默认页面大小
-   enableColumnMenus – 用于启用/禁用列上的菜单
-   useExternalPagination – 如果你在服务器端分页则需要
-   columnDefs – 将自动映射到从服务器返回的 JSON 对象的列名称。从服务器返回的 JSON 对象中的字段名和定义的列名应该匹配。
-   onRegisterApi – 在网格内注册公共方法事件的能力。在这里，我们注册了gridApi.pagination.on.paginationChanged以告诉 UI-Grid 在页面更改时触发此函数。

并将请求发送到 API：

```javascript
app.service('StudentService',['$http', function ($http) {

    function getStudents(pageNumber,size) {
        pageNumber = pageNumber > 0?pageNumber - 1:0;
        return $http({
          method: 'GET',
            url: 'student/get?page='+pageNumber+'&size='+size
        });
    }
    return {
        getStudents: getStudents
    };
}]);
```

## 5.后端和API

### 5.1. RESTful 服务

下面是支持分页的简单 RESTful API 实现：

```java
@RestController
public class StudentDirectoryRestController {

    @Autowired
    private StudentService service;

    @RequestMapping(
      value = "/student/get", 
      params = { "page", "size" }, 
      method = RequestMethod.GET
    )
    public Page<Student> findPaginated(
      @RequestParam("page") int page, @RequestParam("size") int size) {

        Page<Student> resultPage = service.findPaginated(page, size);
        if (page > resultPage.getTotalPages()) {
            throw new MyResourceNotFoundException();
        }

        return resultPage;
    }
}
```

@RestController是在 Spring 4.0中引入的，作为一种方便的注解，它隐式声明了@Controller和@ResponseBody。

对于我们的 API，我们声明它接受两个参数，即页面和大小，这两个参数也将决定返回给客户端的记录数。

我们还添加了一个简单的验证，如果页码高于总页数，该验证将抛出MyResourceNotFoundException 。

最后，我们将返回Page作为 Response——这是 S pring Data的一个超级有用的组件，它保存了分页数据。

### 5.2. 服务实施

我们的服务将简单地根据控制器提供的页面和大小返回记录：

```java
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository dao;

    @Override
    public Page<Student> findPaginated(int page, int size) {
        return dao.findAll(new PageRequest(page, size));
    }
}

```

### 5.3. 存储库实现

对于我们的持久层，我们使用嵌入式数据库和 Spring Data JPA。

首先，我们需要设置持久性配置：

```java
@EnableJpaRepositories("com.baeldung.web.dao")
@ComponentScan(basePackages = { "com.baeldung.web" })
@EntityScan("com.baeldung.web.entity") 
@Configuration
public class PersistenceConfig {

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder
          .setType(EmbeddedDatabaseType.HSQL)
          .addScript("db/sql/data.sql")
          .build();
        return db;
    }
}

```

持久性配置很简单——我们有@EnableJpaRepositories来扫描指定的包并找到我们的 Spring Data JPA 存储库接口。

我们这里有@ComponentScan来自动扫描所有 bean，我们有 @EntityScan(来自 Spring Boot)来扫描实体类。

我们还声明了我们的简单数据源——使用将运行启动时提供的 SQL 脚本的嵌入式数据库。

现在是我们创建数据存储库的时候了：

```java
public interface StudentRepository extends JpaRepository<Student, Long> {}

```

这基本上就是我们在这里需要做的所有事情；如果你想更深入地了解如何设置和使用功能强大的 Spring Data JPA，请务必[阅读此处的指南](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)。

## 6. 分页请求和响应

调用 API – http://localhost:8080/student/get?page=1&size=5时，JSON 响应将如下所示：

```javascript
{
    "content":[
        {"studentId":"1","name":"Bryan","gender":"Male","age":20},
        {"studentId":"2","name":"Ben","gender":"Male","age":22},
        {"studentId":"3","name":"Lisa","gender":"Female","age":24},
        {"studentId":"4","name":"Sarah","gender":"Female","age":26},
        {"studentId":"5","name":"Jay","gender":"Male","age":20}
    ],
    "last":false,
    "totalElements":20,
    "totalPages":4,
    "size":5,
    "number":0,
    "sort":null,
    "first":true,
    "numberOfElements":5
}

```

这里要注意的一件事是服务器返回一个org.springframework.data.domain.Page DTO，包装我们的学生资源。

Page对象将具有以下字段：

-   last – 如果是最后一页则设置为true否则为 false
-   first – 如果是第一页则设置为true否则为 false
-   totalElements – 行/记录的总数。在我们的示例中，我们将其传递给ui-grid选项$scope.gridOptions.totalItems以确定有多少页面可用
-   totalPages – 从 ( totalElements / size )派生的页面总数
-   大小——每页的记录数，这是通过参数大小从客户端传递的
-   number – 客户端发送的页码，在我们的响应中这个数字是 0 因为在我们的后端我们使用一个Student的数组，它是一个从零开始的索引，所以在我们的后端，我们将页码减 1
-   sort – 页面的排序参数
-   numberOfElements – 页面返回的行数/记录数

## 7. 测试分页

[现在让我们使用RestAssured](https://github.com/rest-assured/rest-assured)为我们的分页逻辑设置一个测试；要了解有关RestAssured的更多信息，你可以查看本[教程](https://www.baeldung.com/rest-assured-tutorial)。

### 7.1. 准备测试

为了便于开发我们的测试类，我们将添加静态导入：

```java
io.restassured.RestAssured.
io.restassured.matcher.RestAssuredMatchers.
org.hamcrest.Matchers.
```

接下来，我们将设置启用 Spring 的测试：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:8888")

```

@SpringApplicationConfiguration帮助 Spring 知道如何加载ApplicationContext ，在这种情况下，我们使用Application.java来配置我们的ApplicationContext。

@WebAppConfiguration被定义为告诉 Spring 要加载的ApplicationContext应该是一个WebApplicationContext。

并且@IntegrationTest被定义为在运行测试时触发应用程序启动，这使得我们的 REST 服务可用于测试。

### 7.2. 测试

这是我们的第一个测试用例：

```java
@Test
public void givenRequestForStudents_whenPageIsOne_expectContainsNames() {
    given().params("page", "0", "size", "2").get(ENDPOINT)
      .then()
      .assertThat().body("content.name", hasItems("Bryan", "Ben"));
}

```

上面的这个测试用例是为了测试当页面 1 和大小 2 被传递给 REST 服务时，从服务器返回的 JSON 内容应该有名字Bryan和Ben。

让我们剖析测试用例：

-   given – RestAssured的一部分，用于开始构建请求，你也可以使用with()
-   get – RestAssured的一部分，如果使用会触发 get 请求，请使用 post() 进行 post 请求
-   hasItems——检查值是否匹配的 hamcrest 部分

我们再添加几个测试用例：

```java
@Test
public void givenRequestForStudents_whenResourcesAreRetrievedPaged_thenExpect200() {
    given().params("page", "0", "size", "2").get(ENDPOINT)
      .then()
      .statusCode(200);
}
```

此测试断言，当实际调用该点时，会收到 OK 响应：

```java
@Test
public void givenRequestForStudents_whenSizeIsTwo_expectNumberOfElementsTwo() {
    given().params("page", "0", "size", "2").get(ENDPOINT)
      .then()
      .assertThat().body("numberOfElements", equalTo(2));
}
```

此测试断言，当请求页面大小为二时，返回的页面大小实际上为二：

```java
@Test
public void givenResourcesExist_whenFirstPageIsRetrieved_thenPageContainsResources() {
    given().params("page", "0", "size", "2").get(ENDPOINT)
      .then()
      .assertThat().body("first", equalTo(true));
}

```

该测试断言，当第一次调用资源时，第一个页面名称的值为真。

存储库中还有更多测试，所以一定要看看[GitHub](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-rest-angular)项目。

## 八. 总结

本文阐述了如何在AngularJS中使用UI-Grid实现数据表格网格，以及如何实现所需的服务器端分页。

这些示例和测试的实现可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-rest-angular)中找到。这是一个 Maven 项目，因此它应该很容易导入并按原样运行。

要运行 Spring boot 项目，你只需执行mvn spring-boot:run并在http://localhost:8080/上本地访问它。