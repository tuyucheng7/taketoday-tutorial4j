## 1. 概述

本教程介绍了[Aegis](https://cxf.apache.org/docs/aegis-21.html)数据绑定，这是一个可以在Java对象和 XML 模式描述的 XML 文档之间进行映射的子系统。Aegis 允许对映射过程进行详细控制，同时将编程工作量降至最低。

Aegis 是[Apache CXF](https://cxf.apache.org/)的一部分，但不限于只能在此框架内使用。相反，这种数据绑定机制可以在任何地方使用，因此在本教程中我们将重点放在它作为独立子系统的使用上。

## 2.Maven依赖

激活 Aegis 数据绑定所需的唯一依赖项是：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-databinding-aegis</artifactId>
    <version>3.1.8</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.cxf" AND a%3A"cxf-rt-databinding-aegis")找到此工件的最新版本。

## 3.类型定义

本节介绍用于说明 Aegis 的三种类型的定义。

### 3.1. 课程

这是我们示例中最简单的类，定义为：

```java
public class Course {
    private int id;
    private String name;
    private String instructor;
    private Date enrolmentDate;

    // standard getters and setters
}
```

### 3.2. 课程回购

CourseRepo是我们模型中的顶级类型。我们将其定义为一个接口而不是一个类，以演示编组Java接口是多么容易，如果没有自定义适配器，这在 JAXB 中是不可能的：

```java
public interface CourseRepo {
    String getGreeting();
    void setGreeting(String greeting);
    Map<Integer, Course> getCourses();
    void setCourses(Map<Integer, Course> courses);
    void addCourse(Course course);  
}
```

请注意，我们使用返回类型Map声明getCourses方法。这是为了表达 Aegis 相对于 JAXB 的另一个优势。后者不能在没有自定义适配器的情况下编组映射，而前者可以。

### 3.3. 课程回购实施

此类提供CourseRepo接口的实现：

```java
public class CourseRepoImpl implements CourseRepo {
    private String greeting;
    private Map<Integer, Course> courses = new HashMap<>();

    // standard getters and setters

    @Override
    public void addCourse(Course course) {
        courses.put(course.getId(), course);
    }
}
```

## 4.自定义数据绑定

为了使自定义生效，类路径中必须存在 XML 映射文件。要求将这些文件放在一个目录中，该目录的结构对应于相关Java类型的包层次结构。

例如，如果一个完全限定名称的类名为package.ClassName，则其关联的映射文件必须位于类路径上的package/ClassName子目录中。映射文件的名称必须等于关联的Java类型并附加一个.aegis.xml后缀。

### 4.1. 课程回购映射

CourseRepo接口属于cn.tuyucheng.taketoday.cxf.aegis包，所以它对应的映射文件名为CourseRepo.aegis.xml，放在classpath的com /baeldung/cxf/aegis目录下。

在CourseRepo映射文件中，我们更改了与CourseRepo接口关联的 XML 元素的名称和命名空间，以及其问候语属性的样式：

```xml
<mappings xmlns:ns="http://courserepo.baeldung.com">
    <mapping name="ns:Baeldung">
        <property name="greeting" style="attribute"/>
    </mapping>
</mappings>
```

### 4.2. 课程映射

与CourseRepo类型类似，类Course的映射文件名为Course.aegis.xml，也位于com/baeldung/cxf/aegis目录中。

在此映射文件中，我们指示 Aegis 在编组时忽略Course类的instructor属性，因此它的值在从输出 XML 文档重新创建的对象中不可用：

```xml
<mappings>
    <mapping>
        <property name="instructor" ignore="true"/>
    </mapping>
</mappings>
```

[Aegis 的主页](https://cxf.apache.org/docs/aegis-21.html)是我们可以找到更多自定义选项的地方。

## 5. 测试

本部分是设置和执行测试用例的分步指南，该用例说明了 Aegis 数据绑定的用法。

为了方便测试过程，我们在测试类中声明了两个字段：

```java
public class BaeldungTest {
    private AegisContext context;
    private String fileName = "baeldung.xml";

    // other methods
}
```

这些字段已在此处定义，以供此类的其他方法使用。

### 5.1. AegisContext初始化

首先，必须创建一个AegisContext对象：

```java
context = new AegisContext();
```

然后配置和初始化该AegisContext实例。以下是我们如何为上下文设置根类：

```java
Set<Type> rootClasses = new HashSet<Type>();
rootClasses.add(CourseRepo.class);
context.setRootClasses(rootClasses);
```

Aegis 为Set<Type>对象中的每个类型创建一个 XML 映射元素。在本教程中，我们仅将 CourseRepo设置为根类型。

现在，让我们为上下文设置实现映射以指定CourseRepo接口的代理类：

```java
Map<Class<?>, String> beanImplementationMap = new HashMap<>();
beanImplementationMap.put(CourseRepoImpl.class, "CourseRepo");
context.setBeanImplementationMap(beanImplementationMap);
```

Aegis 上下文的最后一个配置是告诉它在相应的 XML 文档中设置xsi:type属性。除非被映射文件覆盖，否则此属性携带关联Java对象的实际类型名称：

```java
context.setWriteXsiTypes(true);
```

我们的AegisContext实例现在可以初始化了：

```java
context.initialize();
```

为了保持代码整洁，我们将本小节中的所有代码片段收集到一个辅助方法中：

```java
private void initializeContext() {
    // ...
}
```

### 5.2. 简单的数据设置

由于本教程的简单性，我们直接在内存中生成示例数据，而不是依赖于持久性解决方案。让我们使用下面的设置逻辑填充课程回购：

```java
private CourseRepoImpl initCourseRepo() {
    Course restCourse = new Course();
    restCourse.setId(1);
    restCourse.setName("REST with Spring");
    restCourse.setInstructor("Eugen");
    restCourse.setEnrolmentDate(new Date(1234567890000L));
    
    Course securityCourse = new Course();
    securityCourse.setId(2);
    securityCourse.setName("Learn Spring Security");
    securityCourse.setInstructor("Eugen");
    securityCourse.setEnrolmentDate(new Date(1456789000000L));
    
    CourseRepoImpl courseRepo = new CourseRepoImpl();
    courseRepo.setGreeting("Welcome to Beldung!");
    courseRepo.addCourse(restCourse);
    courseRepo.addCourse(securityCourse);
    return courseRepo;
}
```

### 5.3. 绑定Java对象和 XML 元素

将Java对象编组为 XML 元素需要采取的步骤通过以下辅助方法进行说明：

```java
private void marshalCourseRepo(CourseRepo courseRepo) throws Exception {
    AegisWriter<XMLStreamWriter> writer = context.createXMLStreamWriter();
    AegisType aegisType = context.getTypeMapping().getType(CourseRepo.class);
    XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance()
      .createXMLStreamWriter(new FileOutputStream(fileName));
    
    writer.write(courseRepo, 
      new QName("http://aegis.cxf.baeldung.com", "baeldung"), false, xmlWriter, aegisType);
    
    xmlWriter.close();
}
```

如我们所见，必须从AegisContext实例创建AegisWriter和AegisType对象。然后，AegisWriter对象将给定的Java实例编组到指定的输出。

在本例中，这是一个XMLStreamWriter对象，它与文件系统中以fileName类级别字段的值命名的文件相关联。

以下方法将 XML 文档解组为给定类型的Java对象：

```java
private CourseRepo unmarshalCourseRepo() throws Exception {       
    AegisReader<XMLStreamReader> reader = context.createXMLStreamReader();
    XMLStreamReader xmlReader = XMLInputFactory.newInstance()
      .createXMLStreamReader(new FileInputStream(fileName));
    
    CourseRepo courseRepo = (CourseRepo) reader.read(
      xmlReader, context.getTypeMapping().getType(CourseRepo.class));
    
    xmlReader.close();
    return courseRepo;
}
```

在这里，AegisReader对象是从AegisContext实例生成的。然后，AegisReader对象根据提供的输入创建一个Java对象。在此示例中，该输入是一个XMLStreamReader对象，该对象由我们在上面描述的marshalCourseRepo方法中生成的文件支持。

### 5.4. 断言

现在，是时候将前面小节中定义的所有辅助方法组合到一个测试方法中了：

```java
@Test
public void whenMarshalingAndUnmarshalingCourseRepo_thenCorrect()
  throws Exception {
    initializeContext();
    CourseRepo inputRepo = initCourseRepo();
    marshalCourseRepo(inputRepo);
    CourseRepo outputRepo = unmarshalCourseRepo();
    Course restCourse = outputRepo.getCourses().get(1);
    Course securityCourse = outputRepo.getCourses().get(2);

    // JUnit assertions
}
```

我们首先创建一个CourseRepo实例，然后将其编组为 XML 文档，最后解编该文档以重新创建原始对象。让我们验证重新创建的对象是否是我们所期望的：

```java
assertEquals("Welcome to Beldung!", outputRepo.getGreeting());
assertEquals("REST with Spring", restCourse.getName());
assertEquals(new Date(1234567890000L), restCourse.getEnrolmentDate());
assertNull(restCourse.getInstructor());
assertEquals("Learn Spring Security", securityCourse.getName());
assertEquals(new Date(1456789000000L), securityCourse.getEnrolmentDate());
assertNull(securityCourse.getInstructor());
```

很明显，除了instructor属性之外，所有其他属性都恢复了它们的值，包括具有Date类型值的enrolmentDate属性。这正是我们所期望的，因为我们已指示 Aegis在编组Course对象时忽略instructor属性。

### 5.5. 输出 XML 文档

为了使Aegis 映射文件的效果更加明确，我们在下面展示了未经定制的XML 文件：

```xml
<ns1:baeldung xmlns:ns1="http://aegis.cxf.baeldung.com"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:type="ns1:CourseRepo">
    <ns1:courses>
        <ns2:entry xmlns:ns2="urn:org.apache.cxf.aegis.types">
            <ns2:key>1</ns2:key>
            <ns2:value xsi:type="ns1:Course">
                <ns1:enrolmentDate>2009-02-14T06:31:30+07:00
                </ns1:enrolmentDate>
                <ns1:id>1</ns1:id>
                <ns1:instructor>Eugen</ns1:instructor>
                <ns1:name>REST with Spring</ns1:name>
            </ns2:value>
        </ns2:entry>
        <ns2:entry xmlns:ns2="urn:org.apache.cxf.aegis.types">
            <ns2:key>2</ns2:key>
            <ns2:value xsi:type="ns1:Course">
                <ns1:enrolmentDate>2016-03-01T06:36:40+07:00
                </ns1:enrolmentDate>
                <ns1:id>2</ns1:id>
                <ns1:instructor>Eugen</ns1:instructor>
                <ns1:name>Learn Spring Security</ns1:name>
            </ns2:value>
        </ns2:entry>
    </ns1:courses>
    <ns1:greeting>Welcome to Beldung!</ns1:greeting>
</ns1:baeldung>
```

将此与使用 Aegis 自定义映射时的情况进行比较：

```xml
<ns1:baeldung xmlns:ns1="http://aegis.cxf.baeldung.com"
    xmlns:ns="http://courserepo.baeldung.com"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:type="ns:Baeldung" greeting="Welcome to Beldung!">
    <ns:courses>
        <ns2:entry xmlns:ns2="urn:org.apache.cxf.aegis.types">
            <ns2:key>1</ns2:key>
            <ns2:value xsi:type="ns1:Course">
                <ns1:enrolmentDate>2009-02-14T06:31:30+07:00
                </ns1:enrolmentDate>
                <ns1:id>1</ns1:id>
                <ns1:name>REST with Spring</ns1:name>
            </ns2:value>
        </ns2:entry>
        <ns2:entry xmlns:ns2="urn:org.apache.cxf.aegis.types">
            <ns2:key>2</ns2:key>
            <ns2:value xsi:type="ns1:Course">
                <ns1:enrolmentDate>2016-03-01T06:36:40+07:00
                </ns1:enrolmentDate>
                <ns1:id>2</ns1:id>
                <ns1:name>Learn Spring Security</ns1:name>
            </ns2:value>
        </ns2:entry>
    </ns:courses>
</ns1:baeldung>
```

在运行本节中定义的测试后，你可能会在项目主目录中的baeldung.xml中找到此 XML 结构。

你将看到对应于CourseRepo对象的 XML 元素的类型属性和命名空间根据我们在CourseRepo.aegis.xml文件中设置的内容而变化。greeting属性也转换为属性，Course对象的instructor属性也如期消失。

值得注意的是，默认情况下，Aegis 将基本Java类型转换为最匹配的模式类型，例如，从Date对象转换为xsd:dateTime元素，如本教程所示。但是，我们可以通过在相应映射文件中设置配置来更改该特定绑定。

如果你想了解更多信息，请导航至[Aegis 主页。](https://cxf.apache.org/docs/aegis-21.html)

## 六. 总结

本教程说明如何将 Apache CXF Aegis 数据绑定用作独立子系统。它演示了如何使用 Aegis 将Java对象映射到 XML 元素，反之亦然。

本教程还重点介绍如何自定义数据绑定行为。