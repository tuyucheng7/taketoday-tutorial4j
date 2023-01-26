## 亚型映射[¶](https://www.instancio.org/user-guide/#subtype-mapping)

子类型映射允许将特定类型映射到它的子类型。这对于指定抽象类型的特定实现很有用。可以使用以下subtype方法指定映射：

| 1   | subtype(TargetSelector selector, Class<?> subtype) |
|-----|----------------------------------------------------|
|     |                                                    |

选择器表示的所有类型都必须是给定subtype参数的超类型。

| **示例：子类型映射** |                                                                                                                                                                                                                         |
|:-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5    | Person person = Instancio.of(Person.class)    .subtype(all(Pet.class), Cat.class)    .subtype(all(all(Collection.class), all(Set.class)), TreeSet.class)    .subtype(field("address"), AddressImpl.class)    .create(); |

2如果Pet是抽象类型，那么如果没有映射，所有Pet实例都将是null ，因为Instancio将无法解析实现类。
3只要子类型对所有类型都有效，就可以映射多个类型。
4假设Person有一个Address字段，Address超类在哪里AddressImpl。

## 使用模型[¶](https://www.instancio.org/user-guide/#using-models)

模型是用于创建对象的模板[。](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/Model.html)
它封装了使用构建器API指定的所有参数。一旦定义了模型，就可以使用它来创建对象而无需复制公共属性。

| **示例：从模型创建对象**                   |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|:---------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 10 11 12 13 14 | Model<Person> simpsonsModel = Instancio.of(Person.class)    .ignore(field(Person::getId))    .set(field(Person::getLastName), "Simpson")    .set(field(Address::getCity), "Springfield")    .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))    .toModel(); Person homer = Instancio.of(simpsonsModel)    .set(field(Person::getFirstName), "Homer")    .create(); Person marge = Instancio.of(simpsonsModel)    .set(field(Person::getFirstName), "Marge")    .create(); |

1该类Model本身不公开任何公共方法，并且其实例实际上是不可变的。

从模型创建的对象继承模型的所有属性，包括[设置](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/settings/Settings.html)、[模式](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/Mode.html)
和种子值。

模型也可以用作创建其他模型的模板。使用前面的示例，我们可以定义一个带有附加数据的新模型：

| 1 2 3 4 5 | Model<Person> simpsonsModelWithPets = Instancio.of(simpsonsModel)    .supply(field(Person::getPets), () -> List.of(                new Pet(PetType.CAT, "Snowball"),                new Pet(PetType.DOG, "Santa's Little Helper"))    .toModel(); |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|           |                                                                                                                                                                                                                                                   |

拥有一个通用模型允许测试方法通过选择器覆盖模型的属性(包括嵌套对象的属性)来创建自定义对象。这也适用于不可变对象，例如Java记录。

| 1 2 3 | Person withNewAddress = Instancio.of(simpsonsModel)    .set(field(Address:getCity), "Springograd")    .create(); |
|-------|------------------------------------------------------------------------------------------------------------------|
|       |                                                                                                                  |

这种方法减少了重复并简化了数据设置，尤其是对于具有许多字段和关系的复杂类。[使用模型创建对象模板](https://www.instancio.org/articles/creating-object-templates-using-models/)
一文中提供了有关使用模型的好处的更多详细信息，包括示例项目 。

## 自定义生成器[¶](https://www.instancio.org/user-guide/#custom-generators)

Instancio生成的每种类型的对象都是通过Generator接口的实现实现的。许多开箱即用的内部生成器用于创建字符串、数字类型、日期、集合等。还可以定义自定义生成器以满足某些用例：

- 用于生成不支持开箱即用的类型，例如来自第三方库(如 Guava)的类型。
- 用于创建预初始化的域对象。某些域对象需要在特定状态下构造才能有效。为了避免在不同的测试中重复构建逻辑，可以将其封装在一个自定义生成器中，该生成器可以在整个项目中重复使用。
- 用于将生成器作为可以跨项目共享的库进行分发。

Generator是具有单个抽象方法的功能接口generate(Random)：

| 1 2 3 4 5 6 7 8 9 | @FunctionalInterface interface Generator<T> {     T generate(Random random);     default Hints hints() {        return null;    } } |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------|
|                   |                                                                                                                                     |

如果生成器生成随机数据，它必须使用提供的[Random](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/Random.html)
实例来保证可以为给定的种子值复制创建的对象。该hints()
方法用于将附加指令传递给引擎。最重要的提示是[AfterGenerate](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/generator/AfterGenerate.html)
操作，它确定引擎是否应该：

- 填充未初始化的字段
- 通过应用匹配的选择器(如果有的话)修改对象

| **指定提示的示例** |                                                                                                  |
|:------------|--------------------------------------------------------------------------------------------------|
| 1 2 3 4     | @Override public Hints hints() {    return Hints.afterGenerate(AfterGenerate.APPLY_SELECTORS); } |

AfterGenerate枚举定义了以下值：

- **DO_NOT_MODIFY**

  指示不应修改由生成器创建的对象。引擎会将对象视为只读，并将其按原样分配给目标字段。不会应用匹配的选择器。

- **APPLY_SELECTORS**

  指示对象只能通过使用 、 和 方法的匹配选择器set()进行supply()修改generate()。

- **POPULATE_NULLS**

  指示null应填充对象声明的字段。此外，该对象可以使用上面描述的选择器进行修改APPLY_SELECTORS。

- **POPULATE_NULLS_AND_DEFAULT_PRIMITIVES** **(默认行为)**

  指示应填充具有对象声明的默认值的原始字段。此外，描述的行为POPULATE_NULLS 也适用。默认原语定义为：

    - 0对于所有数字类型
    - false为了boolean
    - '\u0000'为了char

- **POPULATE_ALL**

  指示应填充所有字段，无论其初始值如何。此操作将导致所有值被随机数据覆盖。这是使用内部发电机时引擎运行的默认模式。

总之，生成器可以实例化一个对象，并generate()使用提示指示引擎在方法返回后应该对对象做什么AfterGenerate。

### GeneratorProvider使用SPI注册生成器[¶](https://www.instancio.org/user-guide/#registering-generators-using-generatorprovider-spi)

Instancio提供[GeneratorProvider](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/spi/GeneratorProvider.html)
ServiceLoader服务提供者接口，用于使用该机制注册自定义生成器(或覆盖内置生成器) 。提供者接口定义为：

| 1 2 3 | interface GeneratorProvider {    Map<Class<?>, Generator<?>> getGenerators(); } |
|-------|---------------------------------------------------------------------------------|
|       |                                                                                 |

可以通过创建一个名为org.instancio.spi.GeneratorProvider under的文件来注册服务提供者，该文件/META-INF/services/包含提供者实现的完全限定名称：

**/META-INF/services/org.instancio.spi.GeneratorProvider**

```
org.example.CustomGeneratorProvider
```

### 修改overwrite.existing.values设置[¶](https://www.instancio.org/user-guide/#modifying-overwriteexistingvalues-setting)

Instancio配置有一个属性overwrite.existing.values，默认设置为true. 这会导致以下行为。

| **示例 1：创建对象时覆盖字段** |                                                                                                                                                                                                                 |
|:-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7      | class Address {    private String country = "USA"; // default    // snip... } Person person = Instancio.create(Person.class); assertThat(person.getAddress().getCountry()).isNotEqualTo("USA"); // overwritten! |

7该国家/地区不再是“美国”，因为它已被随机值覆盖。

| **示例 2：通过应用选择器覆盖的字段**         |                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|:------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 10 11 12 13 | class AddressGenerator implements Generator<Address> {    @Override    public Address generate(Random random) {        return Address.builder().country("USA").build();    } } Person person = Instancio.of(Person.class)    .supply(all(Address.class), new AddressGenerator())    .set(field(Address.class, "country"), "Canada")    .create(); assertThat(person.getAddress().getCountry()).isEqualTo("Canada"); // overwritten! |

13该国家/地区已通过应用的选择器覆盖。

要禁止覆盖初始化的字段，overwrite.existing.values可以将设置设置为false. 用最后一个例子来说明：

| **示例 2：通过应用选择器覆盖的字段** |                                                                                                                                                                                                                                                                                                                                      |
|:----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7         | Person person = Instancio.of(Person.class)    .supply(all(Address.class), new AddressGenerator())    .set(field(Address.class, "country"), "Canada")    .withSettings(Settings.create().set(Keys.OVERWRITE_EXISTING_VALUES, false))    .create(); assertThat(person.getAddress().getCountry()).isEqualTo("USA"); // not overwritten! |

## 分配设置[¶](https://www.instancio.org/user-guide/#assignment-settings)

分配设置控制值是直接分配给字段(默认行为)
还是通过设置方法分配。有一些带有枚举值的[设置键可以控制行为。](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/settings/Settings.html)

| Keys不变                  | 枚举类                 |
|:------------------------|:--------------------|
| ASSIGNMENT_TYPE         | AssignmentType      |
| SETTER_STYLE            | SetterStyle         |
| ON_SET_FIELD_ERROR      | OnSetFieldError     |
| ON_SET_METHOD_ERROR     | OnSetMethodError    |
| ON_SET_METHOD_NOT_FOUND | OnSetMethodNotFound |

要通过方法启用分配，Keys.ASSIGNMENT_TYPE可以设置为AssignmentType.METHOD.
启用后，Instancio将尝试使用SETTER_STYLE设置从字段名称中解析设置器名称。此键的值是SetterStyle支持三种命名约定的枚举：

- SET- 标准二传手前缀，例如setFoo("value")
- WITH- 例如withFoo("value")
- PROPERTY- 没有前缀，例如foo("value")

其余ON_SET_*键用于控制错误处理行为：

| 钥匙                      | 可能的原因                           |
|:------------------------|:--------------------------------|
| ON_SET_FIELD_ERROR      | 类型不匹配、字段不可修改、访问异常               |
| ON_SET_METHOD_ERROR     | 类型不匹配，setter 抛出的异常(例如由于验证)      |
| ON_SET_METHOD_NOT_FOUND | 方法不存在，或者名称不符合定义的命名约定SetterStyle |

以上所有都可以设置为忽略错误或通过引发异常快速失败。此外，这两个ON_SET_METHOD_*设置都可以配置为在出现错误时回退到字段分配。

下面的代码片段说明了如何创建一个通过 setter 填充的对象。在此示例中，SetterStyle.PROPERTY使用 是因为Phone该类具有不带*set*
前缀的 setter：

| **通过设置器填充**                                           |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
|:------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 | class Phone {    private String areaCode;    private String number;     void areaCode(String areaCode) {        this.areaCode = areaCode;    }     void number(String number) {        this.number = number;    } } Settings settings = Settings.create()        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)        .set(Keys.SETTER_STYLE, SetterStyle.PROPERTY)        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.IGNORE); Phone phone = Instancio.of(Phone.class)        .withSettings(settings)        .create(); |

可以按对象指定[设置，如上所示，或使用属性文件全局指定：](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/settings/Settings.html)

```
assignment.type=METHOD
setter.style=PROPERTY
on.set.method.error=IGNORE
```

有关详细信息，请参阅[配置](https://www.instancio.org/user-guide/#configuration)。

## 种子[¶](https://www.instancio.org/user-guide/#seed)

在创建对象之前，Instancio会初始化一个随机种子值。该种子值由伪随机数生成器内部使用，即java.util.Random。Instancio确保在整个对象创建过程中使用相同的随机数生成器实例，从开始到结束。这意味着Instancio可以使用相同的种子再次复制相同的对象。此功能允许重现失败的测试(
请参阅有关[使用 JUnit 重现测试](https://www.instancio.org/user-guide/#reproducing-failed-tests)的部分)。

此外，Instancio负责为UUID和等类生成值LocalDateTime，其中值的微小差异可能导致对象相等性检查失败。这些类的生成方式是，对于给定的种子值，生成的值将相同。为了举例说明，我们将使用以下SamplePojo类。

| **示例 POJO**                               |                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
|:------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 | class SamplePojo {    private UUID uuid;    private LocalDateTime localDateTime;     @Override    public boolean equals(Object o) {        if (this == o) return true;        if (!(o instanceof SamplePojo)) return false;        SamplePojo p = (SamplePojo) o;        return uuid.equals(p.uuid) && localDateTime.equals(p.localDateTime);    }     @Override    public int hashCode() {        return Objects.hash(uuid, localDateTime);    } } |

通过提供相同的种子值，生成相同的对象：

| **使用相同的种子生成两个 SamplePojo 实例** |                                                                                                                                                                                                                                  |
|:------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 10 11       | final long seed = 123; SamplePojo pojo1 = Instancio.of(SamplePojo.class)    .withSeed(seed)    .create(); SamplePojo pojo2 = Instancio.of(SamplePojo.class)    .withSeed(seed)    .create(); assertThat(pojo1).isEqualTo(pojo2); |

如果打印对象，则两者都会产生相同的输出：

```
SamplePojo(
  uuid=3bf992ad-1121-36a2-826d-94112bf1d82b,
  localDateTime=2069-10-15T10:28:31.940
)
```

虽然生成的值相同，但不建议使用硬编码值编写断言。

### 指定种子值[¶](https://www.instancio.org/user-guide/#specifying-seed-value)

默认情况下，如果未指定自定义种子，Instancio会生成一个随机种子值。因此，每次执行都会产生不同的输出。可以通过使用以下任何选项指定自定义种子来覆盖此行为。这些优先级从低到高排列：

- instancio.properties文件
- @Seed和@WithSettings注释([InstancioExtension](https://www.instancio.org/user-guide/#junit-jupiter-integration)用于
  JUnit Jupiter 时)
- [Settings](https://www.instancio.org/user-guide/#overriding-settings-programmatically)班级
-
构建器API的[withSeed(int seed)方法](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/InstancioApi.html#withSeed(int))

例如，如果在属性文件中指定了一个种子值，那么Instancio将使用这个种子来生成数据，并且每次执行都会生成相同的数据。如果使用withSeed()
方法指定了另一个种子，那么它将优先于属性文件中的种子。

| **示例：instancio.properties** |            |
|:----------------------------|------------|
| 1                           | seed = 123 |

| **种子优先级** |                                                                                                                                          |
|:----------|------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 | SamplePojo pojo1 = Instancio.create(SamplePojo.class); SamplePojo pojo2 = Instancio.of(SamplePojo.class)    .withSeed(456)    .create(); |

1 pojo1``123使用中指定的种子生成instancio.properties。
4 pojo2使用种子生成，456因为withSeed()具有更高的优先级。

### 获得种子价值[¶](https://www.instancio.org/user-guide/#getting-seed-value)

有时需要获取用于生成数据的种子值。一个这样的例子是重现失败的测试。InstancioExtension如果您使用的是 JUnit 5，则使用(
请参阅[JUnit Jupiter 集成](https://www.instancio.org/user-guide/#junit-jupiter-integration))自动报告种子值。如果您独立使用
JUnit 4、TestNG 或 Instancio，则可以通过调用asResult()构建器API的方法来获取种子值。这将返回一个Result包含创建的对象和用于填充其值的种子值。

| **使用 asResult() 的例子** |                                                                                                                                                                                             |
|:----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4               | Result<Person> result = Instancio.of(Person.class).asResult(); Person person = result.get(); long seed = result.getSeed(); // seed value that was used for populating the person // snip... |

# 元模型[¶](https://www.instancio.org/user-guide/#metamodel)

本部分扩展了[选择器](https://www.instancio.org/user-guide/#selectors)
部分，该部分描述了如何定位字段。Instancio在字段级别使用反射来填充对象。使用字段而不是设置器的主要原因是类型擦除。无法在运行时确定方法参数的泛型类型。但是，通用类型信息在字段级别可用：

| 1 2 3 4 5 6 7 | class Example {    private List<String> values;     void setList(List<String> values) {        this.values = values;    } } |
|---------------|-----------------------------------------------------------------------------------------------------------------------------|
|               |                                                                                                                             |

2在运行时，这将是一个List<String>.
4这成为一个List.

在不知道列表的通用类型的情况下，Instancio将无法填充列表。因此，它在现场级别运行。但是，使用字段有一个缺点：它们需要使用字段名称。为了避免这个问题，Instancio包括一个可以生成元模型类的注释处理器。

以下示例显示了city字段的两个选择器Address，一个按名称引用字段，另一个使用生成的元模型类：

| **元模型示例**         |                                                                                                                                                                                                                                                                                                                                                                             |
|:------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 | // Targeting Address "city" field without the metamodel Person person = Instancio.of(Person.class)    .generate(field(Address.class, "city"), gen -> gen.oneOf("Paris", "London"))    .create(); // Targeting 'Address_.city' using the metamodel Person person = Instancio.of(Person.class)    .generate(Address_.city, gen -> gen.oneOf("Paris", "London"))    .create(); |

8默认情况下，_用作元模型类后缀，但这可以使用-Ainstancio.suffix参数进行自定义。

## 配置注解处理器[¶](https://www.instancio.org/user-guide/#configuring-the-annotation-processor)

### 行家[¶](https://www.instancio.org/user-guide/#maven)

要使用 Maven 配置注释处理器，请将<annotationProcessorPaths>元素添加到您的构建插件部分，pom.xml如下所示。

笔记

您仍然需要在您的(请参阅[入门](https://www.instancio.org/getting-started/)instancio-core)
中拥有Instancio库，或者或。instancio-junit``<dependencies>

| **行家**                                                |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
|:------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 | <build>    <plugins>        <plugin>            <groupId>org.apache.maven.plugins</groupId>            <artifactId>maven-compiler-plugin</artifactId>            <version>3.8.1</version>            <configuration>                <source>${your.java.version}</source>                <target>${your.java.version}</target>                <annotationProcessorPaths>                    <path>                        <groupId>org.instancio</groupId>                        <artifactId>instancio-processor</artifactId>                        <version>2.5.0</version>                    </path>                    <!-- include other processors, if any -->                </annotationProcessorPaths>            </configuration>        </plugin>    </plugins> </build> |

### 摇篮[¶](https://www.instancio.org/user-guide/#gradle)

以下内容可用于 Gradle 4.6 或更高版本，将以下内容添加到您的build.gradle文件中：

| **摇篮** |                                                                                       |
|:-------|---------------------------------------------------------------------------------------|
| 1 2 3  | dependencies {    testAnnotationProcessor "org.instancio:instancio-processor:2.5.0" } |

## 生成元模型[¶](https://www.instancio.org/user-guide/#generating-metamodels)

有了注释处理器构建配置，就可以使用[@InstancioMetamodel](https://javadoc.io/doc/org.instancio/instancio-core/latest/org/instancio/InstancioMetamodel.html)
注释生成元模型。注释可以放在任何类型上，包括如下所示的接口。

| **使用@InstancioMetamodel** |                                                                                                                 |
|:--------------------------|-----------------------------------------------------------------------------------------------------------------|
| 1 2 3 4                   | @InstancioMetamodel(classes = {Address.class, Person.class}) interface SampleConfig {    // can be left blank } |

不建议多次声明@InstancioMetamodel相同的注解。classes

这样做也会导致不止一次生成元模型。出于这个原因，最好有一个包含@InstancioMetamodel注释的专用类。

注释中指定的类的元模型将在构建期间自动生成。通常，元模型放置在生成的源目录下，例如generated/sources或generated-sources.
如果您的 IDE 没有选择生成的类，那么将生成的源目录添加到构建路径(或简单地重新加载项目)应该可以解决这个问题。