## 1. 概述

当我们要用Swagger生成验证时，我们通常使用[基本规范](https://swagger.io/docs/specification/describing-parameters/)。但是，我们可能需要添加Spring[自定义验证注解](https://www.baeldung.com/spring-mvc-custom-validator)。

本教程将阐述如何使用这些验证生成模型和REST API，同时重点关注OpenAPI服务器生成器而不是约束验证器。

## 2. 设置

对于设置，我们将使用以前的教程[从OpenAPI 3.0.0定义生成器服务器](https://www.baeldung.com/java-openapi-generator-server)。接下来，我们将添加一些[自定义验证注解](https://www.baeldung.com/spring-mvc-custom-validator)以及所有需要的依赖项。

## 3. PetStore API OpenAPI定义

假设我们有PetStore API OpenAPI定义，我们需要为REST API和描述的模型Pet添加自定义验证。

### 3.1 API模型的自定义验证

要创建宠物，我们需要让Swagger使用我们自定义的验证注解来测试宠物的名字是否大写。因此，为了本教程，我们将其称为Capitalized。

因此，请注意以下示例中的x约束规范。这足以让Swagger知道我们需要生成另一种类型的注解，而不是已知的注解：

```yaml
openapi: 3.0.1
info:
    version: "1.0"
    title: PetStore
paths:
    /pets:
        post:
        #.. post described here
components:
    schemas:
        Pet:
            type: object
            required:
                - id
                - name
            properties:
                id:
                    type: integer
                    format: int64
                name:
                    type: string
                    x-constraints: "Capitalized(required = true)"
                tag:
                    type: string
```

### 3.2 REST API端点的自定义验证

如上所述，我们将描述一个端点，以相同的方式按名称查找所有宠物。为了演示我们的目的，假设我们的系统区分大小写，这样我们将为name输入参数再次添加相同的x约束验证：

```yaml
/pets:
    # post defined here
    get:
        tags:
            - pet
        summary: Finds Pets by name
        description: 'Find pets by name'
        operationId: findPetsByTags
        parameters:
            -   name: name
                in: query
                schema:
                    type: string
                description: Tags to filter by
                required: true
                x-constraints: "Capitalized(required = true)"
        responses:
            '200':
                description: default response
                content:
                    application/json:
                        schema:
                            type: array
                            items:
                                $ref: '#/components/schemas/Pet'
            '400':
                description: Invalid tag value
```

## 4. 创建@Capitalized注解

要强制执行自定义验证，我们需要创建一个注解来确保功能。

首先，我们创建注解@Capitalized：

```java
@Documented
@Constraint(validatedBy = {Capitalized.class})
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Capitalized{
    String message() default "Name should be capitalized.";
    boolean required() default true;
    // default annotation methods
}
```

请注意，我们出于演示目的创建了所需的方法-稍后我们将对此进行解释。

接下来，我们添加CapitalizedValidator，引用了上面创建的@Constraint：

```java
public class CapitalizedValidator implements ConstraintValidator<Capitalized, String> {

    @Override
    public boolean isValid(String nameField, ConstraintValidatorContext context) {
        // validation code here
    }
}
```

## 5. 生成验证注解

### 5.1 指定Mustache模板目录

要使用@Capitalized验证注解生成模型，我们需要特定的mustache模板告诉Swagger在模型中生成它。

因此，在OpenAPI生成器插件中，在<configuration\>\[..]</configuration\>标签内，我们需要添加一个模板目录：

```xml
<plugin>  
    //... 
    <executions>
        <execution>
            <configuration>
                //...
                <templateDirectory>
                    ${project.basedir}/src/main/resources/openapi/templates
                </templateDirectory>
                //...
            </configuration>
        </execution>
    </executions>        
    //...
</plugin> 
```

### 5.2 添加Mustache Bean验证配置

在本章中，我们将配置Mustache模板以生成验证规范。为了添加更多详细信息，我们将修改[beanValidationCore.mustache](https://raw.githubusercontent.com/swagger-api/swagger-codegen/master/modules/swagger-codegen/src/main/resources/Java/beanValidationCore.mustache)、[model.mustache](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen/src/main/resources/Java/model.mustache)和[api.muctache](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen/src/main/resources/Java/api.mustache)文件以成功生成代码。

首先，需要通过添加供应商扩展规范来修改swagger-codegen模块中的[beanValidationCore.mustache](https://raw.githubusercontent.com/swagger-api/swagger-codegen/master/modules/swagger-codegen/src/main/resources/Java/beanValidationCore.mustache)：

```java
{{{ vendorExtensions.x-constraints }}}
```

其次，如果我们有一个带有内部属性的注解，如@Capitalized(required = "true")，那么需要在[beanValidationCore.mustache](https://raw.githubusercontent.com/swagger-api/swagger-codegen/master/modules/swagger-codegen/src/main/resources/Java/beanValidationCore.mustache)文件的第二行指定一个特定的模式：

```java
{{#required}}@Capitalized(required="{{{pattern}}}") {{/required}}
```

第三，我们需要更改[model.mustache](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen/src/main/resources/Java/model.mustache)规范以包含必要的导入。例如，我们将导入@Capitalized注解和Capitalized。应该在[model.mustache](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen/src/main/resources/Java/model.mustache)的package标签之后插入导入：

```java
{{#imports}}import {{import}}; {{/imports}} import
cn.tuyucheng.taketoday.openapi.petstore.validator.CapitalizedValidator;
import cn.tuyucheng.taketoday.openapi.petstore.validator.Capitalized;
```

最后，要在API中生成注解，我们需要在[api.mustache](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen/src/main/resources/Java/api.mustache)文件中添加@Capitalized注解的导入。

```java
{{#imports}}import {{import}}; {{/imports}} import
cn.tuyucheng.taketoday.openapi.petstore.validator.Capitalized;
```

此外，[api.mustache](https://github.com/swagger-api/swagger-codegen/blob/master/modules/swagger-codegen/src/main/resources/Java/api.mustache)依赖于[cookieParams.mustache](https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator/src/main/resources/JavaSpring/cookieParams.mustache)文件。因此，我们需要将其添加到openapi/templates目录中。

## 6. 生成资源

最后，我们可以使用生成的代码。我们至少需要运行mvn generate-sources。这将生成模型：

```java
public class Pet {
    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;
    // other parameters
    
    @Schema(required = true, description = "")
    @Capitalized 
    public String getName() {
        return name; 
    } 
    
    // default getters and setter
}
```

它还会生成一个API：

```java
default ResponseEntity<List<Pet>> findPetsByTags(
    @Capitalized(required = true)
    @ApiParam(value = "Tags to filter by") 
    @Valid @RequestParam(value = "name", required = false) String name) {
    
    // default generated code here 
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
}
```

## 7. 使用curl进行测试

启动应用程序后，我们将运行一些curl命令来测试它。

此外，请注意，违反约束确实会抛出ConstraintViolationException。需要通过[@ControllerAdvice](https://www.baeldung.com/exception-handling-for-rest-with-spring)适当处理异常以返回400 Bad Request状态。

### 7.1 测试Pet模型验证

这个Pet模型有一个小写的name。因此，应用程序应返回400 Bad Request：

```bash
curl -X 'POST' \
  'http://localhost:8080/pet' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "id": 1,
  "name": "rockie"
}'
```

### 7.2 测试查找宠物API

和上面一样，由于name是小写的，应用程序也应该返回一个400 Bad Request：

```bash
curl -I http://localhost:8080/pets/name="rockie"
```

## 8. 总结

在本教程中，我们了解了如何在实现REST API服务器时使用Spring生成自定义约束验证器。