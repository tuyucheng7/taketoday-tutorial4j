## 1. 简介

**YAML是配置文件中使用的一种人性化的表示法**，为什么我们更喜欢这种数据序列化而不是[Spring Boot]()中的属性文件？除了可读性和减少重复之外，YAML还是为部署编写配置即代码的完美语言。

同样，将YAML用于Spring [DevOps]()有助于在环境中存储配置变量，正如[12因素身份验证器](https://12factor.net/config)所建议的那样。

在本教程中，我们将比较Spring YAML与properties文件，以检查使用其中一个的主要优势。但请记住，选择YAML而不是属性文件配置有时取决于个人喜好。

## 2. YAML符号

YAML代表“**YAML Ain't Markup Language**”的递归首字母缩写词，它提供了以下特性：

-   更清晰和人性化
-   非常适合分层配置数据
-   它支持Map、List和标量类型等增强功能

**这些功能使YAML成为Spring配置文件的完美伴侣**，对于那些刚开始使用YAML的人，请注意：由于其[缩进](https://yaml.org/spec/1.2/spec.html#id2777534)规则，一开始编写它可能有点乏味。

让我们看看它是如何工作的！

## 3. Spring YAML配置

如前几节所述，YAML是一种特殊的配置文件数据格式，它的可读性要高得多，并且它提供了比属性文件更强大的功能。因此，在属性文件配置上推荐这种表示法是有意义的。**此外，从1.2版本开始，YAML是JSON的超集**。

此外，在Spring中，[放置在工件外部的配置文件覆盖了打包jar内部的配置文件]()。Spring配置的另一个有趣的特性是可以在运行时分配环境变量，这对于DevOps部署极为重要。

[Spring Profile]()允许分离环境并对它们应用不同的属性，YAML增加了在同一个文件中包含多个Profile的可能性。

注意：Spring Boot 2.4.0的属性文件也支持此功能。

在我们的例子中，为了部署目的，我们将有三个Profile：test、dev和prod：

```yaml
spring:
    profiles:
        active:
            - test

---

spring:
    config:
        activate:
            on-profile: test
name: test-YAML
environment: testing
servers:
    - www.abc.test.com
    - www.xyz.test.com

---

spring:
    config:
        activate:
            on-profile: prod
name: prod-YAML
environment: production
servers:
    - www.abc.com
    - www.xyz.com

---

spring:
    config:
        activate:
            on-profile: dev
name: ${DEV_NAME:dev-YAML}
environment: development
servers:
    - www.abc.dev.com
    - www.xyz.dev.com
```

注意：如果我们使用2.4.0之前的Spring Boot版本，我们应该使用spring.profiles属性而不是我们在这里使用的spring.config.activate.on-profile。

现在让我们检查一下默认分配test环境的spring.profiles.active属性，我们可以使用不同的Profile重新部署工件，而无需重新构建源代码。

Spring中另一个有趣的功能是你可以[通过环境变量启用Profile]()：

```bash
export SPRING_PROFILES_ACTIVE=dev
```

我们将在测试部分看到这个环境变量的相关性。最后，我们可以配置YAML属性，直接从环境中分配值：

```yaml
name: ${DEV_NAME:dev-YAML}
```

我们可以看到，如果没有配置环境变量，则使用默认值dev-YAML。

## 4. 减少重复和可读性

**YAML的层次结构提供了减少配置属性文件上层的方法**，让我们看一个例子的区别：

```yaml
component:
    idm:
        url: myurl
        user: user
        password: password
        description: >
            this should be a long 
            description
    service:
        url: myurlservice
        token: token
        description: >
            this should be another long 
            description
```

如果使用属性文件，相同的配置将变得多余：

```properties
component.idm.url=myurl
component.idm.user=user
component.idm.password=password
component.idm.description=this should be a long description
component.service.url=myurlservice
component.service.token=token
component.service.description=this should be another long description
```

**YAML的分层特性极大地增强了易读性**，这不仅是避免重复的问题，而且使用得当的缩进也完美地描述了配置的内容和用途。使用YAML，就像带有反斜杠\的属性文件一样，可以使用>字符将内容分成多行。

## 5. List和Map

**我们可以使用**[YAML]()**和**[属性文件]()**配置List和Map**。

有两种方法可以分配值并将它们存储在List中：

```yaml
servers:
    - www.abc.test.com
    - www.xyz.test.com

external: [ www.abc.test.com, www.xyz.test.com ]
```

这两个示例提供相同的结果，使用属性文件的等效配置将更难阅读：

```properties
servers[0]=www.abc.test.com
servers[1]=www.xyz.test.com

external=www.abc.test.com, www.xyz.test.com
```

同样，YAML版本更易于阅读和清晰。

以同样的方式，我们可以配置Map：

```yaml
map:
    firstkey: key1
    secondkey: key2
```

## 6. 测试

现在，让我们检查一切是否按预期工作，如果我们检查应用程序的日志记录，我们可以看到默认选择的环境是test：

```bash
16:13:02.033 [main] INFO  [c.t.taketoday.yaml.MyApplication] >>> Starting MyApplication using Java 17.0.5 on tuyucheng with PID 21988
...
using environment:testing
name:test-YAML
enabled:false
servers:[www.abc.test.com, www.xyz.test.com]
external:[www.abc.test.com, www.xyz.test.com]
map:{firstkey=key1, secondkey=key2}
Idm:
   Url: myurl
   User: user
   Password: password
   Description: this should be a long description

Service:
   Url: myurlservice
   Token: token
   Description: this should be another long description
```

我们可以通过在环境中配置DEV_NAME来覆盖名称：

```bash
export DEV_NAME=new-dev-YAML
```

我们可以看到环境的名称发生了变化，并使用dev Profile执行应用程序：

```bash
16:13:03.659 [main] INFO  [c.t.taketoday.yaml.MyApplication] >>> Starting MyApplication using Java 17.0.5 on tuyucheng with PID 26543
...
using environment:development
name:new-dev-YAML
servers:[www.abc.dev.com, www.xyz.dev.com]
```

让我们使用SPRING_PROFILES_ACTIVE=prod运行生产环境：

```bash
export SPRING_PROFILES_ACTIVE=prod

16:13:05.347 [main] INFO  [c.t.taketoday.yaml.MyApplication] >>> Starting MyApplication using Java 17.0.5 on tuyucheng with PID 34436
...
using environment:production
name:prod-YAML
servers:[www.abc.com, www.xyz.com]
```

## 7. 总结

在本教程中，我们描述了与属性文件相比使用YAML配置的复杂性，并演示了YAML提供的人类友好的能力，它减少了重复并且比使用属性文件更简洁。