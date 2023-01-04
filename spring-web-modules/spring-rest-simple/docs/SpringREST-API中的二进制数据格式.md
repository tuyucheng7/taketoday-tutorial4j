## 1. 概述

虽然JSON和XML是广泛流行的REST API数据传输格式，但它们并不是唯一可用的选项。

存在许多具有不同程度的序列化速度和序列化数据大小的其他格式。

在本文中，我们探讨了如何配置Spring REST机制以使用二进制数据格式—我们使用Kryo进行了说明。

此外，我们展示了如何通过添加对Google Protocol buffers的支持来支持多种数据格式。

## 2. HttpMessageConverter

HttpMessageConverter接口基本上是Spring用于转换REST数据格式的公共API。

有不同的方法来指定所需的转换器。在这里，我们实现了WebMvcConfigurer并明确提供了我们要在重写的configureMessageConverters方法中使用的转换器：

```java
@Configuration
@EnableWebMvc
@ComponentScan({ "cn.tuyucheng.taketoday.web" })
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        //...
    }
}
```

## 3. Kryo

### 3.1 Cryo概述和Maven

Kryo是一种二进制编码格式，与基于文本的格式相比，它提供了良好的序列化和反序列化速度以及更小的传输数据大小。

虽然理论上它可以用于在不同类型的系统之间传输数据，但它主要是为与Java组件一起工作而设计的。

我们添加具有以下Maven依赖项的必要Kryo库：

```xml
<dependency>
    <groupId>com.esotericsoftware</groupId>
    <artifactId>kryo</artifactId>
    <version>4.0.0</version>
</dependency>
```

要查看最新版本的kryo，你可以在[此处](https://search.maven.org/search?q=a:kryo)查看。

### 3.2 Spring REST中的Kryo

为了利用Kryo作为数据传输格式，我们创建了一个自定义的HttpMessageConverter并实现了必要的序列化和反序列化逻辑。此外，我们为Kryo定义了自定义HTTP标头：application/x-kryo。这是一个完整的简化工作示例，我们将其用于演示目的：

```java
public class KryoHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final MediaType KRYO = new MediaType("application", "x-kryo");

    private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register(Foo.class, 1);
            return kryo;
        }
    };

    public KryoHttpMessageConverter() {
        super(KRYO);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Object.class.isAssignableFrom(clazz);
    }

    @Override
    protected Object readInternal(
          Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException {
        Input input = new Input(inputMessage.getBody());
        return kryoThreadLocal.get().readClassAndObject(input);
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException {
        Output output = new Output(outputMessage.getBody());
        kryoThreadLocal.get().writeClassAndObject(output, object);
        output.flush();
    }

    @Override
    protected MediaType getDefaultContentType(Object object) {
        return KRYO;
    }
}
```

请注意，我们在这里使用ThreadLocal只是因为创建Kryo实例可能会变得昂贵，我们希望尽可能多地重新利用这些实例。

控制器方法很简单(注意不需要任何自定义协议特定的数据类型，我们使用普通的Foo DTO)：

```java
@RequestMapping(method = RequestMethod.GET, value = "/foos/{id}")
@ResponseBody
public Foo findById(@PathVariable long id) {
    return fooRepository.findById(id);
}
```

并进行快速测试以证明我们已将所有内容正确连接在一起：

```java
RestTemplate restTemplate = new RestTemplate();
restTemplate.setMessageConverters(Arrays.asList(new KryoHttpMessageConverter()));

HttpHeaders headers = new HttpHeaders();
headers.setAccept(Arrays.asList(KryoHttpMessageConverter.KRYO));
HttpEntity<String> entity = new HttpEntity<String>(headers);

ResponseEntity<Foo> response = restTemplate.exchange("http://localhost:8080/spring-rest/foos/{id}", HttpMethod.GET, entity, Foo.class, "1");
Foo resource = response.getBody();

assertThat(resource, notNullValue());
```

## 4. 支持多种数据格式

通常你会希望为同一服务提供对多种数据格式的支持。客户端在Accept HTTP标头中指定所需的数据格式，并调用相应的消息转换器来序列化数据。

通常，你只需注册另一个转换器即可开箱即用。Spring根据Accept标头中的值和转换器中声明的受支持媒体类型自动选择合适的转换器。

例如，要添加对JSON和Kryo的支持，请同时注册KryoHttpMessageConverter和MappingJackson2HttpMessageConverter：

```java
@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    messageConverters.add(new MappingJackson2HttpMessageConverter());
    messageConverters.add(new KryoHttpMessageConverter());
    super.configureMessageConverters(messageConverters);
}
```

现在，假设我们也想将Google Protocol Buffer添加到列表中。对于这个例子，我们假设有一个类FooProtos.Foo由基于以下proto文件的protoc编译器生成：

```protobuf
package tuyucheng;
option java_package = "cn.tuyucheng.taketoday.web.dto";
option java_outer_classname = "FooProtos";
message Foo {
    required int64 id = 1;
    required string name = 2;
}
```

Spring带有一些对Protocol Buffer的内置支持，我们需要做的就是将ProtobufHttpMessageConverter包含在支持的转换器列表中：

```java
@Override
public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
    messageConverters.add(new MappingJackson2HttpMessageConverter());
    messageConverters.add(new KryoHttpMessageConverter());
    messageConverters.add(new ProtobufHttpMessageConverter());
}
```

但是，我们必须定义一个单独的控制器方法来返回FooProtos.Foo实例(JSON和Kryo都处理Foos，因此控制器中不需要更改来区分两者)。

有两种方法可以解决关于调用哪个方法的歧义。第一种方法是对protobuf和其他格式使用不同的 URL。例如，对于protobuf：

```java
@RequestMapping(method = RequestMethod.GET, value = "/fooprotos/{id}")
@ResponseBody
public FooProtos.Foo findProtoById(@PathVariable long id) { ... }
```

对于其他人：

```java
@RequestMapping(method = RequestMethod.GET, value = "/foos/{id}")
@ResponseBody
public Foo findById(@PathVariable long id) { ... }
```

请注意，对于protobuf，我们使用value = "/fooprotos/{id}"，对于其他格式，我们使用value = "/foos/{id}"。

第二种更好的方法是使用相同的URL，但在protobuf的请求映射中明确指定生成的数据格式：

```java
@RequestMapping(
    method = RequestMethod.GET, 
    value = "/foos/{id}", 
    produces = { "application/x-protobuf" })
@ResponseBody
public FooProtos.Foo findProtoById(@PathVariable long id) { ... }
```

请注意，通过在produces注解属性中指定媒体类型，我们根据客户端提供的Accept标头中的值向底层Spring机制提示需要使用哪个映射，因此对于哪个方法需要使用没有歧义为“foos/{id}” URL调用。

第二种方法使我们能够为所有数据格式的客户端提供统一且一致的REST API。

最后，如果你有兴趣深入了解将协议缓冲区与Spring REST API结合使用，请查看[参考文章](https://www.baeldung.com/spring-rest-api-with-protocol-buffers)。

## 5. 注册额外的消息转换器

请务必注意，当你覆盖configureMessageConverters方法时，你将丢失所有默认消息转换器。只有你提供的才会被使用。

虽然有时这正是你想要的，但在许多情况下，你只想添加新的转换器，同时仍然保留已经处理标准数据格式(如JSON)的默认转换器。为此，重写extendMessageConverters方法：

```java
@Configuration
@EnableWebMvc
@ComponentScan({ "cn.tuyucheng.taketoday.web" })
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(new ProtobufHttpMessageConverter());
        messageConverters.add(new KryoHttpMessageConverter());
    }
}
```

## 6. 总结

在本教程中，我们了解了在Spring MVC中使用任何数据传输格式是多么容易，我们以Kryo为例对此进行了检查。

我们还展示了如何添加对多种格式的支持，以便不同的客户端能够使用不同的格式。