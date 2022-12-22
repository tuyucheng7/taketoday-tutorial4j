## 1. 概述

**[Groovy]()具有许多我们可能希望在Spring Web应用程序中使用的功能**。

因此，在本教程中，我们将使用Spring Boot和Groovy构建一个简单的Todo应用程序。此外，我们将探讨它们的集成点。

## 2. Todo应用程序

我们的应用程序将具有以下功能：

-   创建任务
-   编辑任务
-   删除任务
-   查看具体任务
-   查看所有任务

它将是一个**基于REST的应用程序**，我们将**使用Maven作为我们的构建工具**。

### 2.1 Maven依赖项

首先我们在pom.xml文件中包含所需的所有依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.5.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.5.1</version>
</dependency>
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy</artifactId>
    <version>3.0.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>2.5.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.200</version>
    <scope>runtime</scope>
</dependency>
```

在这里，**我们包括[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-web)来构建REST端点，并导入[groovy](https://search.maven.org/search?q=g:org.codehaus.groovy AND a:groovy)依赖项以为我们的项目提供Groovy支持**。

**对于持久层，我们使用[spring-boot-starter-data-jpa](https://search.maven.org/search?q=g:org.springframework.boot AND a:spring-boot-starter-data-jpa)，而[h2](https://search.maven.org/search?q=g:com.h2database AND a:h2)是嵌入式数据库**。

此外，我们必须在pom.xml中包含具有所有[目标](https://github.com/groovy/GMavenPlus/wiki/Usage#why-do-i-need-so-many-goals)的[gmavenplus-plugin](https://search.maven.org/search?q=gmavenplus-plugin)：

```xml
<build>
    <plugins>
        //...
        <plugin>
            <groupId>org.codehaus.gmavenplus</groupId>
            <artifactId>gmavenplus-plugin</artifactId>
            <version>1.9.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>addSources</goal>
                        <goal>addTestSources</goal>
                        <goal>generateStubs</goal>
                        <goal>compile</goal>
                        <goal>generateTestStubs</goal>
                        <goal>compileTests</goal>
                        <goal>removeStubs</goal>
                        <goal>removeTestStubs</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 2.2 JPA实体类

让我们编写一个简单的Todo Groovy类，其中包含3个字段-id、task和isCompleted：

```groovy
@Entity
@Table(name = 'todo')
class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id

    @Column
    String task

    @Column
    Boolean isCompleted
}
```

这里的id字段是任务的唯一标识，task包含任务的详细信息，isCompleted显示任务是否已完成。

请注意，**当我们不为字段提供访问修饰符时，Groovy编译器会将该字段设为私有字段，并为其生成[getter和setter]()方法**。

### 2.3 持久层

让我们创建一个Groovy接口-继承[JpaRepository]()的TodoRepository，它将处理我们应用程序中的所有CRUD操作：

```groovy
@Repository
interface TodoRepository extends JpaRepository<Todo, Integer> {}
```

### 2.4 服务层

**TodoService接口包含我们的CRUD操作所需的所有抽象方法**：

```groovy
interface TodoService {

    List<Todo> findAll()

    Todo findById(Integer todoId)

    Todo saveTodo(Todo todo)

    Todo updateTodo(Todo todo)

    Todo deleteTodo(Integer todoId)
}
```

TodoServiceImpl是一个实现TodoService所有方法的实现类：

```groovy
@Service
class TodoServiceImpl implements TodoService {

    //...

    @Override
    List<Todo> findAll() {
        todoRepository.findAll()
    }

    @Override
    Todo findById(Integer todoId) {
        todoRepository.findById todoId get()
    }

    @Override
    Todo saveTodo(Todo todo){
        todoRepository.save todo
    }

    @Override
    Todo updateTodo(Todo todo){
        todoRepository.save todo
    }

    @Override
    Todo deleteTodo(Integer todoId){
        todoRepository.deleteById todoId
    }
}
```

### 2.5 控制器层

现在，让我们在TodoController中定义所有REST API，这是我们的[@RestController]()：

```groovy
@RestController
@RequestMapping('todo')
class TodoController {

    @Autowired
    TodoService todoService

    @GetMapping
    List<Todo> getAllTodoList(){
        todoService.findAll()
    }

    @PostMapping
    Todo saveTodo(@RequestBody Todo todo){
        todoService.saveTodo todo
    }

    @PutMapping
    Todo updateTodo(@RequestBody Todo todo){
        todoService.updateTodo todo
    }

    @DeleteMapping('/{todoId}')
    deleteTodo(@PathVariable Integer todoId){
        todoService.deleteTodo todoId
    }

    @GetMapping('/{todoId}')
    Todo getTodoById(@PathVariable Integer todoId){
        todoService.findById todoId
    }
}
```

在这里，我们定义了五个端点，用户可以调用它们来执行CRUD操作。

### 2.6 引导Spring Boot应用程序

现在，让我们编写一个包含用于启动应用程序的main方法的类：

```groovy
@SpringBootApplication
class SpringBootGroovyApplication {
    
    static void main(String[] args) {
        SpringApplication.run SpringBootGroovyApplication, args
    }
}
```

请注意，**在Groovy中，当通过传递参数调用方法时，[括号的使用是可选](https://groovy-lang.org/style-guide.html#_omitting_parentheses)的**，这就是我们在上面的示例中所做的。

此外，**Groovy中的任何类都不需要[后缀.class](https://groovy-lang.org/style-guide.html#_classes_as_first_class_citizens)**，这就是我们直接使用SpringBootGroovyApplication的原因。

现在，让我们在pom.xml中将这个类定义为[start-class]()：

```xml
<properties>
    <start-class>cn.tuyucheng.taketoday.app.SpringBootGroovyApplication</start-class>
</properties>
```

## 3. 运行应用程序

最后，我们的应用程序已准备好运行，我们应该简单地将SpringBootGroovyApplication类作为Java应用程序运行或运行Maven构建：

```shell
spring-boot:run
```

这应该在[http://localhost:8080](http://localhost:8080/)上启动应用程序，我们应该能够访问它的端点。

## 4. 测试应用

我们的应用程序已准备好进行测试，让我们创建一个Groovy类TodoAppTest来测试我们的应用程序。

### 4.1 初始设置

让我们在类中定义三个静态变量-API_ROOT、readingTodoId和writingTodoId：

```groovy
static API_ROOT = "http://localhost:8080/todo"
static readingTodoId
static writingTodoId
```

在这里，API_ROOT包含我们应用程序的根URL，readingTodoId和writingTodoId是我们测试数据的主键，稍后我们将使用它们来执行测试。

现在，让我们创建另一个方法populateDummyData()，使用注解[@BeforeAll]()来填充测试数据：

```groovy
@BeforeAll
static void populateDummyData() {
	Todo readingTodo = new Todo(task: 'Reading', isCompleted: false)
	Todo writingTodo = new Todo(task: 'Writing', isCompleted: false)

	final Response readingResponse =
		RestAssured.given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(readingTodo).post(API_ROOT)

	Todo cookingTodoResponse = readingResponse.as Todo.class
	readingTodoId = cookingTodoResponse.getId()

	final Response writingResponse =
		RestAssured.given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(writingTodo).post(API_ROOT)

	Todo writingTodoResponse = writingResponse.as Todo.class
	writingTodoId = writingTodoResponse.getId()
}
```

我们还将在同一方法中填充变量readingTodoId和writingTodoId以存储我们正在保存的记录的主键。

请注意，**在Groovy中，我们还可以使用[命名参数和默认构造函数](https://groovy-lang.org/style-guide.html#_initializing_beans_with_named_parameters_and_the_default_constructor)来初始化bean**，就像我们在上面的代码片段中对readingTodo和writingTodo这样的bean所做的那样。

### 4.2 测试CRUD操作

接下来，我们从Todo列表中找出所有任务：

```groovy
@Test
void whenGetAllTodoList_thenOk(){
    final Response response = RestAssured.get(API_ROOT)
    
    assertEquals HttpStatus.OK.value(),response.getStatusCode()
    assertTrue response.as(List.class).size() > 0
}
```

然后，通过传递我们之前填充的readingTodoId来查找特定任务：

```groovy
@Test
void whenGetTodoById_thenOk(){
    final Response response = RestAssured.get("$API_ROOT/$readingTodoId")
    
    assertEquals HttpStatus.OK.value(),response.getStatusCode()
    Todo todoResponse = response.as Todo.class
    assertEquals readingTodoId,todoResponse.getId()
}
```

在这里，我们使用[插值](https://groovy-lang.org/style-guide.html#_gstrings_interpolation_multiline)来连接URL字符串。

此外，我们使用readingTodoId更新Todo列表中的任务：

```groovy
@Test
void whenUpdateTodoById_thenOk(){
    Todo todo = new Todo(id:readingTodoId, isCompleted: true)
    final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(todo)
            .put(API_ROOT)
          
    assertEquals HttpStatus.OK.value(),response.getStatusCode()
    Todo todoResponse = response.as Todo.class
    assertTrue todoResponse.getIsCompleted()
}
```

然后使用writingTodoId删除Todo列表中的任务：

```groovy
@Test
void whenDeleteTodoById_thenOk(){
    final Response response = RestAssured.given()
            .delete("$API_ROOT/$writingTodoId")
    
    assertEquals HttpStatus.OK.value(),response.getStatusCode()
}
```

最后，我们可以保存一个新任务：

```groovy
@Test
void whenSaveTodo_thenOk(){
    Todo todo = new Todo(task: 'Blogging', isCompleted: false)
    final Response response = RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(todo)
            .post(API_ROOT)
    
    assertEquals HttpStatus.OK.value(),response.getStatusCode()
}
```

## 5. 总结

在本文中，我们使用Groovy和Spring Boot构建了一个简单的应用程序，并看到了如何将它们集成在一起。