## 1. 概述

在本教程中，我们将学习如何在Spring Boot中调用OpenAI ChatGPT API。我们将创建一个Spring Boot应用程序，该应用程序将通过调用OpenAI ChatGPT API生成对提示的响应。

## 2. OpenAI ChatGPT API

在开始本教程之前，让我们探索一下我们将在本教程中使用的OpenAI ChatGPT API。我们将调用[创建聊天完成API](https://platform.openai.com/docs/api-reference/chat/create)来生成对提示的响应。

### 2.1 API参数和身份验证

我们来看看API的强制请求参数：

-   **model**：这是我们**将向其发送请求的模型的版本**。该模型[有几个版本可用](https://platform.openai.com/docs/models/model-endpoint-compatibility)有。我们将使用gpt-3.5-turbo模型，这是公开可用的模型的最新版本
-   **messages**：消息是**对模型的提示**。每条消息都需要两个字段：role和content。role字段指定消息的发送者。它将在请求中为“user”，在响应中为“assistant”。content字段是实际消息

为了使用API进行身份验证，我们将生成一个[OpenAI API密钥](https://platform.openai.com/account/api-keys)。我们将**在调用API时在Authorization标头中设置此密钥**。

cURL格式的示例请求如下所示：

```shell
$ curl https://api.openai.com/v1/chat/completions \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $OPENAI_API_KEY" \
    -d '{
        "model": "gpt-3.5-turbo",
        "messages": [{"role": "user", "content": "Hello!"}]
    }'
```

此外，API接受许多可选参数来修改响应。

在接下来的部分中，我们将重点介绍一个简单的请求，但让我们看看一些有助于调整响应的可选参数：

-   n：如果我们想增加**生成的响应数量**，可以指定。默认值为1。
-   temperature：控制**响应的随机性**。默认值为1(最随机)。
-   max_tokens：用于**限制响应中的最大令牌数**。默认值为无穷大，这意味着响应将与模型可以生成的时间一样长。通常，最好将此值设置为一个合理的数字，以避免生成非常长的响应并产生高成本。

### 2.2 API响应

API响应将是一个带有一些元数据和一个choices字段的JSON对象。choices字段将是一个对象数组。每个对象都有一个text字段，其中包含对提示的响应。

choices数组中的对象数将等于请求中可选的n参数。如果未指定n参数，则choices数组将包含单个对象。

这是一个示例响应：

```json
{
    "id": "chatcmpl-123",
    "object": "chat.completion",
    "created": 1677652288,
    "choices": [
        {
            "index": 0,
            "message": {
                "role": "assistant",
                "content": "\n\nHello there, how may I assist you today?"
            },
            "finish_reason": "stop"
        }
    ],
    "usage": {
        "prompt_tokens": 9,
        "completion_tokens": 12,
        "total_tokens": 21
    }
}
```

响应中的usage字段将包含提示和响应中使用的令牌数。这用于计算API调用的成本。

## 3. 代码示例

我们将创建一个将使用OpenAI ChatGPT API的Spring Boot应用程序。为此，我们将创建一个Spring Boot Rest API，它接收提示作为请求参数，将其传递给OpenAI ChatGPT API，并将响应作为响应主体返回。

### 3.1 依赖关系

首先，让我们创建一个Spring Boot项目。对于这个项目，我们需要[spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 3.2 DTO

接下来，我们创建一个与OpenAI ChatGPT API的请求参数对应的[DTO](https://www.baeldung.com/java-dto-pattern)：

```java
public class ChatRequest {

    private String model;
    private List<Message> messages;
    private int n;
    private double temperature;

    public ChatRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }

    // getters and setters
}
```

我们还定义Message类：

```java
public class Message {

    private String role;
    private String content;

    // constructor, getters and setters
}
```

同样，让我们为响应创建一个DTO：

```java
public class ChatResponse {

    private List<Choice> choices;

    // constructors, getters and setters

    public static class Choice {
        private int index;
        private Message message;

        // constructors, getters and setters
    }
}
```

### 3.3 控制器

接下来，让我们创建一个控制器，该控制器将接收提示作为请求参数并将响应作为响应主体返回：

```java
@RestController
public class ChatController {

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        // create a request
        ChatRequest request = new ChatRequest(model, prompt);

        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }
}
```

让我们看一下代码的一些重要部分：

-   我们使用@Qualifier注解来注入我们将在下一节中创建的RestTemplate bean
-   使用RestTemplate bean，我们使用postForObject()方法调用了OpenAI ChatGPT API。postForObject()方法将URL、请求对象和响应类作为参数
-   最后，我们读取响应的choices列表并返回第一个回复

### 3.4 RestTemplate

接下来，让我们定义一个自定义的RestTemplate bean，它将使用OpenAI API密钥进行身份验证：

```java
@Configuration
public class OpenAIRestTemplateConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    @Qualifier("openaiRestTemplate")
    public RestTemplate openaiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
```

在这里，我们向基础RestTemplate添加了一个拦截器并添加了Authorization标头。

### 3.5 属性

最后，让我们在application.properties文件中提供API的属性：

```properties
openai.model=gpt-3.5-turbo
openai.api.url=https://api.openai.com/v1/chat/completions
openai.api.key=your-api-key
```

## 4. 运行应用程序

现在，我们可以运行应用程序并在浏览器中对其进行测试：

<img src="../assets/img_4.png">

如我们所见，应用程序生成了对提示的响应。请注意，响应可能会有所不同，因为它是由模型生成的。

## 5. 总结

在本教程中，我们创建了一个Spring Boot应用程序，它调用OpenAI ChatGPT API来生成对提示的响应。