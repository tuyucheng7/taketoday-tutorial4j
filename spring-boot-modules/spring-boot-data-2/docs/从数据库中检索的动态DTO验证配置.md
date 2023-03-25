## 1. 概述

在本教程中，我们将了解**如何创建自定义校验注解，该注解使用从数据库中检索的正则表达式来匹配字段值**。

我们将使用Hibernate Validator作为基本实现。

## 2. Maven依赖

对于开发，我们需要以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.4.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.4.0</version>
</dependency>
```

可以从Maven Central下载最新版本的[spring-boot-starter-thymeleaf](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf)和[spring-boot-starter-data-jpa](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)。

## 3. 自定义验证注解

对于我们的示例，我们将创建一个名为@ContactInfo的自定义注解，该注解将根据从数据库中检索的正则表达式验证值。然后，我们将在名为Customer的POJO类的contactInfo字段上应用此注解。

为了从数据库中检索正则表达式，我们将它们建模为ContactInfoExpression实体类。

### 3.1 数据模型和Repository

让我们创建带有id和contactInfo字段的Customer类：

```java
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String contactInfo;

    // standard constructor, getters, setters
}
```

接下来，让我们看一下ContactInfoExpression类-它将正则表达式值保存在一个名为pattern的属性中：

```java
@Entity
public class ContactInfoExpression {

    @Id
    @Column(name="expression_type")
    private String type;

    private String pattern;

    //standard constructor, getters, setters
}
```

接下来，让我们添加一个基于Spring Data的Repository接口来操作ContactInfoExpression实体：

```java
public interface ContactInfoExpressionRepository extends Repository<ContactInfoExpression, String> {
    Optional<ContactInfoExpression> findById(String id);
}
```

### 3.2 数据库设置

为了存储正则表达式，我们将使用具有以下配置的H2内存数据库：

```java
@EnableJpaRepositories("cn.tuyucheng.taketoday.dynamicvalidation.dao")
@EntityScan("cn.tuyucheng.taketoday.dynamicvalidation.model")
@Configuration
public class PersistenceConfig {

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2)
              .addScript("schema-expressions.sql")
              .addScript("data-expressions.sql")
              .build();
    }
}
```

上面代码中使用到的两个sql脚本用于创建表并将数据插入到contact_info_expression表中：

```sql
CREATE TABLE contact_info_expression
(
    expression_type varchar(50)  not null,
    pattern         varchar(500) not null,
    PRIMARY KEY (expression_type)
);
```

data-expressions.sql脚本将添加三条记录来表示email、phone和website。这些表示用于验证该值是有效电子邮件地址、有效电话号码或有效URL的正则表达式：

```sql
insert into contact_info_expression
values ('email',
        '[a-z0-9!#$%&*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?')
insert into contact_info_expression
values ('phone',
        '^([0-9]( |-)?)?(\(?[0-9]{3}\)?|[0-9]{3})( |-)?([0-9]{3}( |-)?[0-9]{4}|[a-zA-Z0-9]{7})$')
insert into contact_info_expression
values ('website',
        '^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$')
```

### 3.3 创建自定义验证器

让我们创建包含实际验证逻辑的ContactInfoValidator类。遵循Java Validation规范指南，该类**实现ConstraintValidator接口并重写isValid()方法**。

此类将获取当前使用的联系人信息类型(电子邮件、电话或网站)的值，该值设置在名为contactInfoType的属性中，然后使用它从数据库中检索正则表达式的值：

```java
public class ContactInfoValidator implements ConstraintValidator<ContactInfo, String> {

    private static final Logger LOG = LogManager.getLogger(ContactInfoValidator.class);

    @Autowired
    private ContactInfoExpressionRepository expressionRepository;

    @Value("${contactInfoType}")
    String expressionType;

    private String pattern;

    @Override
    public void initialize(final ContactInfo contactInfo) {
        if (StringUtils.isEmptyOrWhitespace(expressionType))
            LOG.error("Contact info type missing!");
        else
            pattern = expressionRepository.findById(expressionType).map(ContactInfoExpression::getPattern).orElse("");
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (!StringUtils.isEmptyOrWhitespace(pattern))
            return Pattern.matches(pattern, value);

        LOG.error("Contact info pattern missing!");
        return false;
    }
}
```

可以在application.properties文件中将contactInfoType属性设置为email、phone或website值之一：

```properties
contactInfoType=email
```

### 3.4 创建自定义约束注解

现在，让我们为自定义约束创建注解：

```java
@Constraint(validatedBy = {ContactInfoValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ContactInfo {
    String message() default "Invalid value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

### 3.5 应用自定义约束

最后，让我们将验证注解添加到Customer类的contactInfo字段中：

```java
public class Customer {

    // ...
    @ContactInfo
    @NotNull
    private String contactInfo;

    // ...
}
```

## 4. Spring控制器和HTML表单

为了测试我们的验证注解，我们将创建一个Spring MVC请求映射，它使用@Valid注解来触发对Customer对象的验证：

```java
@Controller
public class CustomerController {

    @PostMapping("/customer")
    public String validateCustomer(@Valid final Customer customer, final BindingResult result, final Model model) {
        if (result.hasErrors())
            model.addAttribute("message", "The information is invalid!");
        else
            model.addAttribute("message", "The information is valid!");
        return "customer";
    }
}
```

Customer对象从HTML表单发送到控制器：

```html
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Customer Page</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.0/jquery.min.js"></script>
</head>
<body>
<br/>
<form action="customer" method="POST">
    Contact Info: <input type="text" name="contactInfo"/> <br/>
    <input type="submit" value="Submit"/>
</form>
<br/><br/>
<span th:text="${message}"></span><br/>
</body>
</html>
```

现在可以将我们的应用程序作为Spring Boot应用程序运行：

```java
@SpringBootApplication
public class DynamicValidationApplication {

    @RolesAllowed("*")
    public static void main(String[] args) {
        SpringApplication.run(DynamicValidationApplication.class, args);
    }
}
```

## 5. 总结

在此示例中，我们演示了如何创建自定义验证注解，该注解从数据库中动态检索正则表达式并使用它来校验带有注解的字段。