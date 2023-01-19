## 1. 概述

在本教程中，我们介绍如何在Spring Rest Controller中访问HTTP请求头。

首先，我们使用@RequestHeader来单独读取请求头以及读取一组请求头。

之后，我们更深入地介绍@RequestHeader中的属性。

## 2. 访问HTTP头

### 2.1 访问单个

**如果需要访问特定的http请求头，我们可以使用@RequestHeader注解，并指定请求头的名字**：

```java
@RestController
public class ReadHeaderRestController {

    @GetMapping("/greeting")
    public ResponseEntity<String> greeting(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language) {
        // code that uses the language variable
        return new ResponseEntity<String>(greeting, HttpStatus.OK);
    }
}
```

然后我们可以使用传递给方法的变量来访问该值，
如果在请求中未找到名为”accept-language“的请求头，该方法将返回“400 Bad Request”错误。

指定的请求头不必是字符串，如果我们知道请求头是一个数字，我们可以将变量声明为int类型：

```java
public class ReadHeaderRestController {

    @GetMapping("/double")
    public ResponseEntity<String> doubleNumber(@RequestHeader("my-number") int myNumber) {
        return new ResponseEntity<>(String.format("%d * 2 = %d", myNumber, (myNumber * 2)), HttpStatus.OK);
    }
}
```

### 2.2 访问全部

如果我们不确定包含哪些请求头，或者我们需要访问很多的请求头，我们可以使用@RequestHeader注解，而不需要指定名称。

对于变量类型，我们有几个选择：Map、MultiValueMap或HttpHeaders对象。

首先，我们以Map的形式获取请求头：

```java
public class ReadHeaderRestController {

    @GetMapping("/listHeaders")
    public ResponseEntity<String> listAllHeaders(@RequestHeader Map<String, String> headers) {
        headers.forEach((key, value) -> LOG.info(String.format("Header '%s' = %s", key, value)));

        return new ResponseEntity<>(String.format("Listed %d headers", headers.size()), HttpStatus.OK);
    }
}
```

**如果我们使用Map并且其中一个请求头有多个值，那么我们只能得到第一个值**。
这相当于在MultiValueMap上调用getFirst方法。

**如果请求头可能有多个值，我们可以将它们作为MultiValueMap获取**：

```java
public class ReadHeaderRestController {

    @GetMapping("/multiValue")
    public ResponseEntity<String> multiValue(@RequestHeader MultiValueMap<String, String> headers) {
        headers.forEach((key, value) -> LOG.info(String.format("Header '%s' = %s", key, String.join("|", value))));

        return new ResponseEntity<>(String.format("Listed %d headers", headers.size()), HttpStatus.OK);
    }
}
```

我们还可以将请求头作为HttpHeaders对象获取：

```java
public class ReadHeaderRestController {

    @GetMapping("/getBaseUrl")
    public ResponseEntity<String> getBaseUrl(@RequestHeader HttpHeaders headers) {
        InetSocketAddress host = headers.getHost();
        String url = "http://" + host.getHostName() + ":" + host.getPort();
        return new ResponseEntity<>(String.format("Base URL = %s", url), HttpStatus.OK);
    }
}
```

HttpHeaders对象具有通用应用程序请求头的访问器。

**当我们从Map、MultiValueMap或HttpHeaders对象中按名称访问请求头时，如果它不存在，我们将得到一个null值**。

## 3. @RequestHeader属性

当我们指定了请求头的名字时，已经隐式地使用了name或value属性：

```java
/*@formatter:off*/
public ResponseEntity<String> greeting(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) String language) {
}
/*@formatter:on*/
```

我们可以通过明确指定name属性来实现相同的效果：

```java
/*@formatter:off*/
public ResponseEntity<String> greeting(@RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE) String language) {
}
/*@formatter:on*/
```

对于value属性也一样：

```java
/*@formatter:off*/
public ResponseEntity<String> greeting(@RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE) String language) {
}
/*@formatter:on*/
```

**当我们指定了请求头的名字时，默认情况下请求头是必需的**。如果在请求中找不到该请求头，则控制器返回400错误。

通过使用required属性，我们可以指定请求头不是必需存在请求中的：

```java
public class ReadHeaderRestController {

    @GetMapping("/nonRequiredHeader")
    public ResponseEntity<String> evaluateNonRequiredHeader(@RequestHeader(value = "optional-header", required = false) String optionalHeader) {

        return new ResponseEntity<>(
                String.format("Was the optional header present? %s!", (optionalHeader == null ? "No" : "Yes")),
                HttpStatus.OK);
    }
}
```

**由于如果请求中不存在请求头，optionalHeader变量将为null，因此我们需要确保进行适当的空检查**。

通过使用defaultValue属性，我们可以为请求头提供默认值：

```java
public class ReadHeaderRestController {

    @GetMapping("/default")
    public ResponseEntity<String> evaluateDefaultHeaderValue(@RequestHeader(value = "optional-header", defaultValue = "3600") int optionalHeader) {

        return new ResponseEntity<>(String.format("Optional Header is %d", optionalHeader), HttpStatus.OK);
    }
}
```

## 4. 总结

在这个教程中，我们介绍了如何访问Spring REST控制器中的请求头。

首先，我们使用@RequestHeader注解来指定我们需要获取的请求头，然后我们详细介绍了@RequestHeader注解包含的属性。