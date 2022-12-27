## 1. 概述

在本文中，我们将编写一个API规范，允许为相同的响应代码返回两个不同的对象，我们将演示如何使用该规范生成Java代码和Swagger文档。

## 2. 问题的提出

让我们定义两个对象，Car有一个owner和一个plate作为属性，两者都是字符串类型。另一方面，Bike类有owner和speed两个字段，speed是一个Integer类型。

使用OpenAPI，这些定义对应于以下描述：

```yaml
Car:
    type: object
    properties:
        owner:
            type: string
        plate:
            type: string
Bike:
    type: object
    properties:
        owner:
            type: string
        speed:
            type: integer
```

**我们想要描述一个端点/vehicle，它将接收GET请求并能够返回Car或Bike**。也就是说，我们要完成如下的描述符：

```yaml
paths:
    /vehicle:
        get:
            responses:
                '200':
                # return Car or Bike
```

我们将针对OpenAPI 2和3规范讨论该主题。

## 3. 对OpenAPI 3有两种不同的响应

OpenAPI版本3引入了oneOf，这正是我们所需要的。

### 3.1 构建描述符文件

**在OpenAPI 3规范中，oneOf需要一个对象数组，并指示提供的值应与给定对象之一完全匹配**：

```yaml
schema:
    oneOf:
        -   $ref: '#/components/schemas/Car'
        -   $ref: '#/components/schemas/Bike'
```

此外，OpenAPI 3引入了展示各种响应示例的可能性。为了清楚起见，我们绝对希望至少提供一个示例响应，其中包含Car和另一个包含Bike的响应：

```yaml
examples:
    car:
        summary: an example of car
        value:
            owner: tuyucheng
            plate: AEX305
    bike:
        summary: an example of bike
        value:
            owner: john doe
            speed: 25
```

最后，让我们看一下整个描述符文件：

```yaml
openapi: 3.0.0
info:
    title: Demo api
    description: Demo api for the article 'specify two responses with same code based on optional parameter'
    version: 1.0.0
paths:
    /vehicle:
        get:
            responses:
                '200':
                    description: Get a vehicle
                    content:
                        application/json:
                            schema:
                                oneOf:
                                    -   $ref: '#/components/schemas/Car'
                                    -   $ref: '#/components/schemas/Bike'
                            examples:
                                car:
                                    summary: an example of car
                                    value:
                                        owner: tuyucheng
                                        plate: AEX305
                                bike:
                                    summary: an example of bike
                                    value:
                                        owner: john doe
                                        speed: 25
components:
    schemas:
        Car:
            type: object
            properties:
                owner:
                    type: string
                plate:
                    type: string
        Bike:
            type: object
            properties:
                owner:
                    type: string
                speed:
                    type: integer
```

### 3.2 生成Java类

**现在，我们将使用我们的YAML文件来生成我们的API接口**，可以使用两个[Maven](https://www.baeldung.com/maven)插件[swagger-codegen和openapi-generator](https://www.baeldung.com/spring-boot-rest-client-swagger-codegen)从api.yaml文件生成Java代码。从6.0.1版本开始，openapi-generator不处理oneOf，因此我们将在本文中坚持使用swagger-codegen。

我们将为swagger-codegen插件使用以下配置：

```xml
<plugin>
    <groupId>io.swagger.codegen.v3</groupId>
    <artifactId>swagger-codegen-maven-plugin</artifactId>
    <version>3.0.34</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/static/api.yaml</inputSpec>
                <language>spring</language>
                <configOptions>
                    <java8>true</java8>
                    <interfaceOnly>true</interfaceOnly>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

请注意，我们决定切换选项以仅生成接口，以便避免生成大量对我们来说不是很感兴趣的文件。

现在让我们执行插件：

```shell
mvn clean compile
```

我们现在可以查看生成的文件：

-   生成Car和Bike对象
-   由于使用了[@JsonSubTypes](https://www.baeldung.com/jackson-annotations)注解，生成了OneOfinlineResponse200接口来表示可以是Car或Bike的对象
-   InlineResponse200是OneOfinlineResponse200的基础实现
-   VehicleApi定义端点：对此端点的获取请求返回InlineResponse200

<img src="../assets/img.png">

### 3.3 生成Swagger UI文档

要从我们的YAML描述符文件生成Swagger UI文档，我们将使用[springdoc-openapi](https://www.baeldung.com/spring-rest-openapi-documentation)。让我们将springdoc-openapi-ui的依赖添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.10</version>
</dependency>
```

**springdoc-openapi-ui的1.6.10版本依赖swagger-ui版本4.13.2，它可以正确处理oneOf和各种响应示例**。

要从YAML文件生成Swagger UI文档，我们需要声明一个[SpringBootApplication](https://www.baeldung.com/spring-boot-start)并添加以下三个[bean](https://www.baeldung.com/spring-bean)：

```java
@Bean
SpringDocConfiguration springDocConfiguration() {
    return new SpringDocConfiguration();
}

@Bean
SpringDocConfigProperties springDocConfigProperties() {
    return new SpringDocConfigProperties();
}

@Bean
ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties springDocConfigProperties) {
    return new ObjectMapperProvider(springDocConfigProperties);
}
```

最后但同样重要的是，我们需要确保我们的YAML描述符位于resources/static目录中，并更新application.properties以指定我们不想从[控制器](https://www.baeldung.com/spring-controllers)生成Swagger UI，而是从YAML文件生成：

```properties
springdoc.api-docs.enabled=false
springdoc.swagger-ui.url=/api.yaml
```

现在可以启动我们的应用程序了：

```shell
mvn spring-boot:run
```

Swagger UI可通过地址http://localhost:8080/swagger-ui/index.html访问。

我们可以看到有一个下拉菜单可以在Car和Bike示例之间导航：

<img src="../assets/img_1.png">

响应模式也被正确呈现：

<img src="../assets/img_2.png">

## 4. 对OpenAPI 2有两种不同的响应

在OpenAPI 2中，oneOf不存在。因此，让我们找到一个替代方案。

### 4.1 构建描述符文件

**我们能做的最好的事情就是定义一个包装对象，它将具有Car和Bike的所有属性**。公共属性将是必需的，并且仅属于其中一个的属性将保持可选：

```yaml
CarOrBike:
    description: a car will have an owner and a plate, whereas a bike has an owner and a speed
    type: object
    required:
        - owner
    properties:
        owner:
            type: string
        plate:
            type: string
        speed:
            type: integer
```

我们的API响应将是一个CarOrBike对象，我们将在描述中添加更多见解。不幸的是，我们无法添加各种示例，因此我们决定只给出一个Car示例。

让我们看一下生成的api.yaml：

```yaml
swagger: 2.0.0
info:
    title: Demo api
    description: Demo api for the article 'specify two responses with same code based on optional parameter'
    version: 1.0.0
paths:
    /vehicle:
        get:
            responses:
                '200':
                    description: Get a vehicle. Can contain either a Car or a Bike
                    schema:
                        $ref: '#/definitions/CarOrBike'
                    examples:
                        application/json:
                            owner: tuyucheng
                            plate: AEX305
                            speed:
definitions:
    Car:
        type: object
        properties:
            owner:
                type: string
            plate:
                type: string
    Bike:
        type: object
        properties:
            owner:
                type: string
            speed:
                type: integer
    CarOrBike:
        description: a car will have an owner and a plate, whereas a bike has an owner and a speed
        type: object
        required:
            - owner
        properties:
            owner:
                type: string
            plate:
                type: string
            speed:
                type: integer
```

### 4.2 生成Java类

让我们调整我们的swagger-codegen插件配置来解析OpenAPI 2文件。**为此，我们需要使用插件的2.x版本**，它也位于另一个包中：

```xml
<plugin>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-codegen-maven-plugin</artifactId>
    <version>2.4.27</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/static/api.yaml</inputSpec>
                <language>spring</language>
                <configOptions>
                    <java8>true</java8>
                    <interfaceOnly>true</interfaceOnly>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

现在让我们看一下生成的文件：

-   CarOrBike对象包含预期的字段，具有[@NotNull](https://www.baeldung.com/javax-validation)所有者
-   VehicleApi定义端点，对此端点的获取请求返回CarOrBike

### 4.3 生成Swagger UI文档

**我们可以像在3.3小节中那样生成文档**。

然后可以看到我们的描述显示：

<img src="../assets/img_3.png">

我们的CarOrBike模型也如预期的那样被描述：

<img src="../assets/img_4.png">

## 5. 总结

在本教程中，我们了解了如何为可以返回一个或另一个对象的端点编写OpenAPI规范，通过swagger-codegen，我们使用YAML描述符生成Java代码，并使用springdoc-openapi-ui生成Swagger UI文档。