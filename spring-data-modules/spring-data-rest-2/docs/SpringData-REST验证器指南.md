## 1. 概述

本文是对Spring Data REST验证器的基本介绍。如果你需要先了解Spring Data REST的基础知识，请务必阅读[本文]()。

简单地说，使用Spring Data REST，我们可以简单地通过REST API向数据库中添加一条新记录，但是我们当然也需要确保数据在实际持久化之前是有效的。

本文基于之前的[文章](../../spring-data-rest-1/docs/SpringData-REST简介.md)，重用之前构建过的项目。

## 2. 使用验证器

从Spring 3开始，该框架具有Validator接口，它可用于验证对象。

### 2.1 动机

在之前的文章中，我们定义了具有两个属性name和email的实体WebsiteUser。

因此，要创建一个新资源，我们只需要运行：

```shell
curl -i -X POST -H "Content-Type:application/json" -d '{"name": "Test", "email": "test@test.com"}' http://localhost:8080/users
```

这个POST请求会将提供的JSON对象保存到我们的数据库中，返回的内容为：

```json
{
	"name": "Test",
	"email": "test@test.com",
	"_links": {
		"self": {
			"href": "http://localhost:8080/users/1"
		},
		"websiteUser": {
			"href": "http://localhost:8080/users/1"
		}
	}
}
```

因为我们提供了有效的数据，所有预计会得到正确的结果。但是，如果我们删除属性name，或者只是将name的值设置为空字符串会发生什么？

为了测试第一个场景，我们修改之前运行过的命令，将空字符串设置为属性name的值：

```shell
curl -i -X POST -H "Content-Type:application/json" -d '{"name": "", "email": "Baggins"}' http://localhost:8080/users
```

执行以上cURL命令，我们将得到以下响应：

```json
{
	"name": "",
	"email": "Baggins",
	"_links": {
		"self": {
			"href": "http://localhost:8080/users/1"
		},
		"websiteUser": {
			"href": "http://localhost:8080/users/1"
		}
	}
}
```

对于第二种情况，我们从请求中删除属性name：

```shell
curl -i -X POST -H "Content-Type:application/json" -d '{"email": "Baggins"}' http://localhost:8080/users
```

此时，得到的响应为：

```json
{
	"name": null,
	"email": "Baggins",
	"_links": {
		"self": {
			"href": "http://localhost:8080/users/2"
		},
		"websiteUser": {
			"href": "http://localhost:8080/users/2"
		}
	}
}
```

正如我们所看到的，这两个请求都是正常的，我们可以通过201状态代码和到我们对象的API链接来确认这一点。

但是这种行为是不可接受的，因为我们希望避免将部分数据插入数据库。

### 2.2 Spring Data REST事件

在每次调用Spring Data REST API时，Spring Data REST导出器都会生成各种事件，如下所示：

-   BeforeCreateEvent
-   AfterCreateEvent
-   BeforeSaveEvent
-   AfterSaveEvent
-   BeforeLinkSaveEvent
-   AfterLinkSaveEvent
-   BeforeDeleteEvent
-   AfterDeleteEvent

由于所有事件都是以类似的方式处理，因此我们只演示如何处理在将新对象保存到数据库之前生成的beforeCreateEvent。

### 2.3 定义验证器

为了创建我们自己的验证器，我们需要实现org.springframework.validation.Validator接口，并实现supports和validate方法。support方法检查验证器是否支持提供的请求，而validate方法验证请求中提供的数据。

下面我们定义一个WebsiteUserValidator类：

```java
public class WebsiteUserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return WebsiteUser.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        WebsiteUser user = (WebsiteUser) obj;
        if (checkInputString(user.getName())) {
            errors.rejectValue("name", "name.empty");
        }
   
        if (checkInputString(user.getEmail())) {
            errors.rejectValue("email", "email.empty");
        }
    }

    private boolean checkInputString(String input) {
        return (input == null || input.trim().length() == 0);
    }
}
```

Errors对象是一个特殊的类，旨在包含validate方法中提供的所有错误。在本文的后面，我们会演示如何使用包含在Errors对象中的提供的消息。要添加新的错误消息，我们必须调用errors.rejectValue(nameOfField, errorMessage)。

在定义了验证器之后，我们需要将其映射到请求被接受后生成的特定事件。

例如，在我们的例子中，生成beforeCreateEvent是因为我们想将一个新对象插入到我们的数据库中。但由于我们想要验证请求中的对象，我们需要首先定义验证器。

这可以通过三种方式实现：

-   添加name属性为“beforeCreateWebsiteUserValidator”的组件注解。Spring Boot会识别前缀beforeCreate来确定我们要捕获的事件，并且它还会从组件名称中识别WebsiteUser类。

    ```java
    @Component("beforeCreateWebsiteUserValidator")
    public class WebsiteUserValidator implements Validator {
        // ...
    }
    ```

-   使用@Bean注解在应用程序上下文中创建Bean ：

    ```java
    @Bean
    public WebsiteUserValidator beforeCreateWebsiteUserValidator() {
        return new WebsiteUserValidator();
    }
    ```

-   手动注册：

    ```java
    @SpringBootApplication
    public class SpringDataRestApplication implements RepositoryRestConfigurer {
        public static void main(String[] args) {
            SpringApplication.run(SpringDataRestApplication.class, args);
        }
    
        @Override
        public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener v) {
            v.addValidator("beforeCreate", new WebsiteUserValidator());
        }
    }
    ```

    -   对于这种情况，你不需要在WebsiteUserValidator类上添加任何注解。

### 2.4 事件发现错误

目前，[Spring Data REST中存在一个错误](https://jira.spring.io/browse/DATAREST-524)，这会影响事件发现。

如果我们调用生成beforeCreate事件的POST请求，我们的应用程序将不会调用验证器，因为由于这个错误，该事件将不会被发现。

解决此问题的一个简单方法是将所有事件插入Spring Data REST ValidatingRepositoryEventListener类：

```java
@Configuration
public class ValidatorEventRegister implements InitializingBean {

	@Autowired
	ValidatingRepositoryEventListener validatingRepositoryEventListener;

	@Autowired
	private Map<String, Validator> validators;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<String> events = Arrays.asList("beforeCreate");

		for (Map.Entry<String, Validator> entry : validators.entrySet()) {
			events.stream()
					.filter(p -> entry.getKey().startsWith(p))
					.findFirst()
					.ifPresent(p -> validatingRepositoryEventListener.addValidator(p, entry.getValue()));
		}
	}
}
```

## 3. 测试

在第2.1节中，我们表明，在没有验证器的情况下，我们可以将没有name属性的对象添加到我们的数据库中，而这不是我们想要的行为，因为这不会检查数据的完整性。

如果我们想添加没有name属性相同对象，并且使用我们提供的验证器，则会得到以下错误：

```shell
curl -i -X POST -H "Content-Type:application/json" -d '{"email": "test@test.com"}' http://localhost:8080/users
```

```json
{
	"timestamp": 1472510818701,
	"status": 406,
	"error": "Not Acceptable",
	"exception": "org.springframework.data.rest.core.RepositoryConstraintViolationException",
	"message": "Validation failed",
	"path": "/users"
}
```

如我们所见，检测到请求中缺少数据，并且没有将对象保存到数据库中。我们的请求返回了500 HTTP码和内部错误消息。

错误消息没有说明我们请求中的问题。如果我们想让它更具信息性，我们将不得不修改响应对象。

在[Spring中的异常处理]()一文中，我们演示了如何处理框架生成的异常，因此在这一点上，这绝对是一篇不错的文章。

由于我们的应用程序生成了一个RepositoryConstraintViolationException异常，我们将为该特定异常创建一个处理程序，它将修改响应消息。

下面是我们的RestResponseEntityExceptionHandle类：

```java
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ RepositoryConstraintViolationException.class })
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
          RepositoryConstraintViolationException nevEx = (RepositoryConstraintViolationException) ex;

          String errors = nevEx.getErrors().getAllErrors().stream().map(p -> p.toString()).collect(Collectors.joining("\n"));
          return new ResponseEntity<Object>(errors, new HttpHeaders(), HttpStatus.PARTIAL_CONTENT);
    }
}
```

使用此自定义处理程序，我们的返回对象将包含有关所有检测到的错误的信息。

## 4. 总结

在本文中，我们介绍了验证器对于每个Spring Data REST API都是必不可少的，它为数据插入提供了额外的安全层。