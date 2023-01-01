## 1. 概述

在本教程中，我们将演示**如何使用Spring Boot构建**[REST服务](https://www.baeldung.com/rest-with-spring-series)**以消费和生成JSON内容**。

我们还将了解如何轻松使用RESTful HTTP语义。

为简单起见，我们不会包含[持久层](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)，但[Spring Data](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)也可以使添加变得很容易。

## 2. REST服务

在Spring Boot中编写JSON REST服务很简单，因为这是[Jackson](https://www.baeldung.com/jackson)在类路径上时的默认意见：

```java
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService service;

    @GetMapping("/{id}")
    public Student read(@PathVariable String id) {
        return service.find(id);
    }

    // ...
}
```

通过使用[@RestController](https://www.baeldung.com/spring-controller-vs-restcontroller)标注我们的StudentController，**我们告诉Spring Boot将read方法的返回类型写入响应正文**。由于我们**在类级别也有一个@RequestMapping**，因此对于我们添加的任何更多公共方法来说都是一样的。

虽然简单，**但这种方法缺乏HTTP语义**。例如，如果我们找不到请求的学生会怎样？我们可能希望返回404，而不是返回200或500状态码。

让我们看看如何获得对HTTP响应本身的更多控制权，然后将一些典型的RESTful行为添加到我们的控制器中。

## 3. Create

当我们需要控制响应主体以外的其他方面时，例如状态码，我们可以改为返回一个ResponseEntity：

```java
@PostMapping("/")
public ResponseEntity<Student> create(@RequestBody Student student) 
    throws URISyntaxException {
    Student createdStudent = service.create(student);
    if (createdStudent == null) {
        return ResponseEntity.notFound().build();
    } else {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
          .path("/{id}")
          .buildAndExpand(createdStudent.getId())
          .toUri();

        return ResponseEntity.created(uri)
          .body(createdStudent);
    }
}
```

在这里，我们所做的不仅仅是在响应中返回创建的Student，**我们还以语义清晰的HTTP状态进行响应，如果创建成功，则返回新资源的URI**。

## 4. Read

如前所述，如果我们想读取单个学生，如果找不到该学生，则返回404在语义上更清楚：

```java
@GetMapping("/{id}")
public ResponseEntity<Student> read(@PathVariable("id") Long id) {
    Student foundStudent = service.read(id);
    if (foundStudent == null) {
        return ResponseEntity.notFound().build();
    } else {
        return ResponseEntity.ok(foundStudent);
    }
}
```

在这里，我们可以清楚地看到与我们最初的read()实现的区别。

**这样，Student对象将被正确映射到响应正文并同时返回正确的状态**。

## 5. Update

更新与创建非常相似，只是它映射到PUT而不是POST，并且URI包含我们正在更新的资源的id：

```java
@PutMapping("/{id}")
public ResponseEntity<Student> update(@RequestBody Student student, @PathVariable Long id) {
    Student updatedStudent = service.update(id, student);
    if (updatedStudent == null) {
        return ResponseEntity.notFound().build();
    } else {
        return ResponseEntity.ok(updatedStudent);
    }
}
```

## 6. Delete

删除操作映射到DELETE方法，URI还包含资源的id：

```java
@DeleteMapping("/{id}")
public ResponseEntity<Object> deleteStudent(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
}
```

我们没有实现特定的[错误处理](https://www.baeldung.com/exception-handling-for-rest-with-spring)，因为delete()方法实际上会因抛出异常而失败。

## 7. 总结

在本文中，我们学习了如何在使用Spring Boot开发的典型CRUD REST服务中消费和生成JSON内容。此外，我们演示了如何实现正确的响应状态控制和错误处理。

为了简单起见，这次我们没有进入持久化，但是[Spring Data REST](https://www.baeldung.com/spring-data-rest-intro)提供了一种快速有效的方式来构建RESTful数据服务。