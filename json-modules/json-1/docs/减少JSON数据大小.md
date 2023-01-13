## 1. 简介

Java 应用程序通常使用 JSON 作为发送和接收数据的通用格式。此外，它还用作存储数据的序列化协议。使用更小的 JSON 数据大小，我们的应用程序变得更便宜、更快。

在本教程中，我们将研究减少Java 应用程序中[JSON](https://www.baeldung.com/java-org-json)大小的各种方法。

## 2.领域模型和测试数据

让我们用一些联系数据为客户创建一个域模型：

```java
public class Customer {
    private long id;
    private String firstName;
    private String lastName;
    private String street;
    private String postalCode;
    private String city;
    private String state;
    private String phoneNumber;
    private String email;
```

请注意，所有字段都是必填的，除了phoneNumber和email。

要正确测试 JSON 数据大小差异，我们至少需要几百个Customer实例。他们必须有不同的数据才能使我们的测试更逼真。数据生成网站[mockaroo](https://www.mockaroo.com/)在这里帮助我们。我们可以在那里以我们自己的格式免费创建 1,000 条 JSON 数据记录，并使用真实的测试数据。

让我们为我们的域模型配置mockaroo ：

[![JSON生成](https://www.baeldung.com/wp-content/uploads/2020/09/JSON-generation.png)](https://www.baeldung.com/wp-content/uploads/2020/09/JSON-generation.png)

在这里，要记住一些事项：

-   这是我们指定字段名称的地方
-   这里我们选择了我们字段的数据类型
-   模拟数据中 50% 的电话号码为空
-   30% 的电子邮件地址也是空的

下面的所有代码示例都使用来自mockaroo的 1,000 名客户的相同数据。我们使用工厂方法Customer.fromMockFile()来读取该文件并将其转换为Customer对象。

我们将使用[Jackson](https://www.baeldung.com/jackson)作为我们的 JSON 处理库。

## 3. Jackson 默认选项的 JSON 数据大小

让我们使用默认的 Jackson 选项将Java对象写入 JSON：

```java
Customer[] customers = Customer.fromMockFile();
ObjectMapper mapper = new ObjectMapper();
byte[] feedback = mapper.writeValueAsBytes(customers); 
```

让我们看看第一个Customer的模拟数据：

```json
{
  "id" : 1, 
  "firstName" : "Horatius", 
  "lastName" : "Strognell", 
  "street" : "4848 New Castle Point", 
  "postalCode" : "33432", 
  "city" : "Boca Raton", 
  "state" : "FL", 
  "phoneNumber" : "561-824-9105", 
  "email" : "hstrognell0@dailymail.co.uk"
}
```

使用默认的 Jackon 选项时，包含所有 1,000 个客户的 JSON 数据字节数组的大小为 181.0 KB。

## 4.用gzip压缩

作为文本数据，JSON 数据压缩得很好。这就是为什么gzip是我们减少 JSON 数据大小的首选。此外，它可以自动应用于 HTTP 这种用于发送和接收 JSON 的通用协议。

让我们使用默认的 Jackson 选项生成的 JSON 并使用gzip压缩它。结果为 45.9 KB，仅为原始大小的 25.3%。因此，如果我们可以通过配置启用gzip压缩，我们将在不更改Java代码的情况下将 JSON 数据大小减少 75%！

如果我们的Spring Boot应用程序将 JSON 数据传送到其他服务或前端，那么我们将在Spring Boot配置中启用gzip压缩。让我们看看 YAML 语法中的典型压缩配置：

```yaml
server:
  compression:
    enabled: true
    mime-types: text/html,text/plain,text/css,application/javascript,application/json
    min-response-size: 1024
```

首先，我们通常通过将enabled设置为 true 来启用压缩。然后，我们通过将application/json添加到mime-types列表来专门启用 JSON 数据压缩。最后，请注意我们将min-response-size设置为 1,024 字节长。这是因为如果我们压缩少量数据，我们可能会产生比原始数据更大的数据。

通常，诸如[NGINX 之](https://www.nginx.com/)类的代理 或诸如[Apache HTTP 服务器](https://httpd.apache.org/)之类的 Web 服务器 将 JSON 数据传送到其他服务或前端。在这些工具中配置 JSON 数据压缩超出了本教程的范围。

[之前关于gzip](https://www.baeldung.com/linux/gzip-and-gunzip)[的教程](https://www.baeldung.com/linux/gzip-and-gunzip)告诉我们gzip有不同的压缩级别。我们的代码示例使用 具有默认Java压缩级别的gzip 。对于相同的 JSON 数据，Spring Boot、代理或 Web 服务器可能会得到不同的压缩结果。

如果我们使用 JSON 作为序列化协议来存储数据，我们将需要自己对数据进行压缩和解压缩。

## 5. JSON 中较短的字段名称

最佳做法是使用既不太短也不太长的字段名称。为了演示起见，让我们忽略它：我们将在 JSON 中使用单字符字段名称，但我们不会更改Java字段名称。这减少了 JSON 数据大小，但降低了 JSON 的可读性。由于它还需要更新所有服务和前端，我们可能只会在存储数据时使用这些短字段名称：

```json
{
  "i" : 1,
  "f" : "Horatius",
  "l" : "Strognell",
  "s" : "4848 New Castle Point",
  "p" : "33432",
  "c" : "Boca Raton",
  "a" : "FL",
  "o" : "561-824-9105",
  "e" : "hstrognell0@dailymail.co.uk"
}
```

使用 Jackson 更改 JSON 字段名称很容易，同时保持Java字段名称不变。我们将使用@JsonProperty注解：

```java
@JsonProperty("p")
private String postalCode;
```

使用单字符字段名称会导致数据大小为原始大小的 72.5%。此外，使用gzip 会将其压缩到 23.8%。这并不比我们使用gzip简单压缩原始数据得到的 25.3% 小很多。我们总是需要寻找合适的成本收益关系。在大多数情况下，不建议为了尺寸的小幅增加而失去可读性。

## 6.序列化为数组

让我们看看如何通过完全省略字段名称来进一步减少 JSON 数据大小。我们可以通过在我们的 JSON 中存储一个客户数组来实现这一点。请注意，我们还将降低可读性。我们还需要更新所有使用我们的 JSON 数据的服务和前端：

```json
[ 1, "Horatius", "Strognell", "4848 New Castle Point", "33432", "Boca Raton", "FL", "561-824-9105", "hstrognell0@dailymail.co.uk" ]
```

将Customer存储为数组导致输出为原始大小的 53.1%，使用gzip压缩后为 22.0%。这是迄今为止我们最好的成绩。尽管如此，22% 并没有比我们仅使用gzip压缩原始数据得到的 25.3% 小很多。

为了将客户序列化为数组，我们需要完全控制 JSON 序列化。再次参阅我们的[Jackson](https://www.baeldung.com/jackson-object-mapper-tutorial)教程以获取更多示例。

## 7.排除空值

Jackson 和其他 JSON 处理库在读取或写入 JSON 时可能无法正确处理 JSON空值。比如Jackson在遇到Java空值时默认写入一个JSON空值。这就是为什么最好删除 JSON 数据中的空字段。这将空值的初始化留给每个 JSON 处理库并减少 JSON 数据大小。

在我们的模拟数据中，我们将 50% 的电话号码和 30% 的电子邮件地址设置为空。省略这些空值会将我们的 JSON 数据大小减少到 166.8kB 或原始数据大小的 92.1%。然后，gzip压缩会将其降至 24.9%。

现在，如果我们将忽略空值与上一节中较短的字段名称相结合，那么我们将获得更显着的节省：原始大小的 68.3% 和gzip的 23.4% 。

[我们可以在 Jackson 中为每个类](https://www.baeldung.com/jackson-ignore-null-fields#on-class)或[全局为所有类](https://www.baeldung.com/jackson-ignore-null-fields#globally)配置省略空值字段。

## 8. 新领域类

通过将其序列化为数组，我们实现了迄今为止最小的 JSON 数据大小。进一步减少的一种方法是使用更少字段的新域模型。但我们为什么要那样做？

让我们想象一下我们的 JSON 数据的前端，它将所有客户显示为一个包含两列的表：姓名和街道地址。我们专门为这个前端写JSON数据：

```json
{
  "id" : 1,
  "name" : "Horatius Strognell",
  "address" : "4848 New Castle Point, Boca Raton FL 33432"
}
```

请注意我们是如何将名称字段连接到name并将地址字段连接到address的。此外，我们遗漏了email和phoneNumber。

这应该会产生更小的 JSON 数据。它还使前端免于连接客户字段。但不利的一面是，这将我们的后端与前端紧密耦合。

让我们为这个前端创建一个新的域类CustomerSlim ：

```java
public class CustomerSlim {
    private long id;
    private String name;
    private String address;
```

如果我们将测试数据转换为这个新的CustomerSlim域类，我们会将其减小到原始大小的 46.1%。这将使用默认的 Jackson 设置。如果我们使用gzip，它会下降到 15.1%。最后一个结果已经比之前的 22.0% 的最佳结果有了显着的提高。

接下来，如果我们也使用一个字符的字段名称，这会使我们减少到原始大小的 40.7%，而gzip进一步将其减少到 14.7%。这个结果只是我们使用 Jackson 默认设置达到的超过 15.1% 的小增益。

CustomerSlim中没有字段是可选的，因此省略空值对 JSON 数据大小没有影响。

我们最后的优化是数组的序列化。通过将CustomerSlim序列化为数组，我们获得了最佳结果：原始大小的 34.2% 和gzip的 14.2% 。因此，即使不进行压缩，我们也会删除近三分之二的原始数据。压缩将我们的 JSON 数据缩小到原始大小的七分之一！

## 9.总结

在本文中，我们首先了解了为什么需要减小 JSON 数据大小。接下来，我们学习了各种减少此 JSON 数据大小的方法。最后，我们学习了如何使用为一个前端定制的域模型进一步减少 JSON 数据大小。