## 1. 概述

在本教程中，我们介绍org.springframework.web.bind.annotation包中的Spring Web注解。

## 2. @RequestMapping

简单地说，**@RequestMapping标记@Controller类内部的请求处理程序方法**；可以使用以下方式进行配置：

+ path或其别名name和value：方法映射到哪个URL。
+ method：兼容的HTTP方法。
+ params：根据HTTP参数的存在、不存在或值过滤请求。
+ headers：根据HTTP头的存在、不存在或值过滤请求。
+ consumes：该方法可以在HTTP请求正文中使用哪些媒体类型。
+ produces：该方法可以在HTTP响应正文中生成哪些媒体类型。

下面是一个简单的示例：

```java
@Controller
class VehicleController {

    @RequestMapping(value = "/vehicles/home", method = RequestMethod.GET)
    String home() {
        return "home";
    }
}
```

**如果我们在类级别使用此注解，相当于为@Controller类中的所有处理程序方法提供默认设置**。**唯一的例外是，Spring不会用方法级设置覆盖该URL，而是合并两个路径部分**。

例如，下面的配置和上面的效果是一样的：

```java
@Controller
@RequestMapping(value = "/vehicles", method = RequestMethod.GET)
class VehicleController {

    @RequestMapping("/home")
    String home() {
        return "home";
    }
}
```

此外，@GetMapping、@PostMapping、@PutMapping、@DeleteMapping和@PatchMapping是@RequestMapping的不同变体，
其HTTP方法已分别设置为GET、POST、PUT、DELETE和PATCH。

这些从Spring 4.3版本开始可用。

## 3. @RequestBody

**@RequestBody将HTTP请求体映射到一个对象**：

```java
class VehicleController {

    @PostMapping("/save")
    void saveVehicle(@RequestBody Vehicle vehicle) {
        // ...
    }
}
```

反序列化是自动的，并且取决于请求的内容类型。

## 4. @PathVariable

**此注解指示方法参数绑定到URI路径变量**。我们可以使用@RequestMapping注解指定URI路径，并使用@PathVariable将方法参数绑定到路径部分之一。

我们可以通过name或其别名value参数来实现这一点：

```java
class VehicleController {

    @RequestMapping("/{id}")
    Vehicle getVehicle(@PathVariable("id") long id) {
        // ...
    }
}
```

如果路径变量的名称与方法参数的名称匹配，我们不必在注解中指定：

```java
class VehicleController {

    @RequestMapping("/{id}")
    Vehicle getVehicle(@PathVariable long id) {
        // ...
    }
}
```

此外，我们可以通过将require的参数设置为false来将路径变量标记为可选：

```java
class VehicleController {

    @RequestMapping("/{id}")
    Vehicle getVehicle(@PathVariable(required = false) long id) {
        // ...
    }
}
```

## 5. @RequestParam

我们**使用@RequestParam来访问HTTP请求参数**：

```java
class VehicleController {

    @RequestMapping
    Vehicle getVehicleByParam(@RequestParam("id") long id) {
        // ...
    }
}
```

它包含与@PathVariable注解相同的配置参数。

除了这些设置之外，当Spring在请求中发现没有值或值为空时，我们可以使用@RequestParam指定一个要注入的值。为此，我们必须设置defaultValue参数。

提供默认值隐式地将required属性设置为false：

```java
class VehicleController {

    @RequestMapping("/buy")
    Car buyCar(@RequestParam(defaultValue = "5") int seatCount) {
        // ...
    }
}
```

除了参数之外，我们还可以访问其他HTTP请求部分，例如cookie和header。**我们可以分别使用注解@CookieValue和@RequestHeader实现相同的效果**。

并且可以像@RequestParam一样配置它们。

## 6. 响应处理注解

在接下来的部分中，我们介绍在Spring MVC中操作HTTP响应的最常见注解。

### 6.1 @ResponseBody

如果我们**用@ResponseBody标记请求处理程序方法，Spring将方法的结果视为响应体本身**：

```java
class VehicleController {

    @ResponseBody
    @RequestMapping("/hello")
    String hello() {
        return "Hello World!";
    }
}
```

如果我们用这个注解来标注一个@Controller类，所有的请求处理方法都会应用该注解。

### 6.2 @ExceptionHandler

**使用这个注解，我们可以声明一个自定义的异常处理方法，当请求处理程序方法抛出任何指定的异常时，Spring调用此方法**。

捕获的异常可以作为参数传递给该异常处理方法：

```java
class VehicleController {

    @ExceptionHandler(IllegalArgumentException.class)
    void onIllegalArgumentException(IllegalArgumentException exception) {
        // ...
    }
}
```

### 6.3 @ResponseStatus

**如果我们使用此注解标注请求处理程序方法，我们可以指定响应的HTTP状态码**。我们可以使用code参数或它的别名value参数来声明状态码。

此外，我们可以使用reason参数提供原因。

我们也可以将它与@ExceptionHandler一起使用：

```java
class VehicleController {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void onIllegalArgumentException(IllegalArgumentException exception) {
        // ...
    }
}
```

有关HTTP响应状态码的更多信息，请阅读[本文]()。

## 7. 其他Web注解

有些注解不直接管理HTTP请求或响应，在接下来的部分中，我们只介绍最常用的。

### 7.1 @Controller

我们可以使用@Controller定义一个Spring MVC控制器。有关更多信息，请阅读[关于Spring Bean注解]()。

### 7.2 @RestController

**@RestController结合了@Controller和@ResponseBody**。

因此，以下声明是等价的：

```java
@Controller
@ResponseBody
class VehicleRestController {
    // ...
}

@RestController
class VehicleRestController {
    // ...
}
```

### 7.3 @ModelAttribute

**使用这个注解，我们可以通过提供model的key来访问已经存在于MVC @Controller模型中的元素**：

```java
public class VehicleController {

    @PostMapping("/assemble")
    void assembleVehicle(@ModelAttribute("vehicle") Vehicle vehicleInModel) {
        // ...
    }
}
```

与@PathVariable和@RequestParam一样，如果参数名称相同，我们不必指定model的key：

```java
class VehicleController {

    @PostMapping("/assemble")
    void assembleVehicle(@ModelAttribute Vehicle vehicle) {
        // ...
    }
}
```

此外，@ModelAttribute还有另一个用途：**如果我们用它标注一个方法，Spring会自动将该方法的返回值添加到Model中**：

```java
class VehicleController {

    @ModelAttribute("vehicle")
    Vehicle getVehicle() {
        // ...
    }
}
```

和之前一样，我们不必指定model的key，Spring默认使用方法的名称：

```java
class VehicleRestController {

    @ModelAttribute
    Vehicle vehicle() {
        // ...
    }
}
```

在Spring调用请求处理程序方法之前，它会调用类中所有带有@ModelAttribute注解的方法。

有关@ModelAttribute的更多信息可以在[本文]()中找到。

### 7.4 @CrossOrigin

**@CrossOrigin为带有该注解的请求处理程序方法启用跨域通信**：

```java
class VehicleController {

    @CrossOrigin
    @RequestMapping("/hello")
    String hello() {
        return "Hello World!";
    }
}
```

如果我们用它标记一个类，它作用于类中的所有请求处理程序方法。

我们可以使用此注解的参数调整CORS行为。

有关更多详细信息，请阅读[本文]()。

## 8. 总结

在本文中，我们了解了如何使用Spring MVC处理HTTP请求和响应。