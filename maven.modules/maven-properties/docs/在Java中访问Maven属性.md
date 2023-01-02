## 1. 概述

在这个简短的教程中，我们将了解如何在Java应用程序中使用Maven的pom.xml中定义的变量。

## 2. 插件配置

在整个示例中，我们将使用[Maven Properties插件](https://www.mojohaus.org/properties-maven-plugin/)。

**该插件将绑定到generate-resources阶段，并在编译期间创建一个包含我们的pom.xml中定义的变量的文件，然后我们可以在运行时读取该文件以获取值**。

让我们首先将插件包含在我们的项目中：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>properties-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <phase>generate-resources</phase>
            <goals>
                <goal>write-project-properties</goal>
            </goals>
            <configuration>
                <outputFile>${project.build.outputDirectory}/properties-from-pom.properties</outputFile>
            </configuration>
        </execution>
    </executions>
</plugin>
```

接下来，我们将继续为我们的变量提供一个值。**此外，由于我们在pom.xml中定义它们，因此我们也可以使用**[Maven占位符](https://github.com/cko/predefined_maven_properties/blob/master/README.md)：

```xml
<properties> 
    <name>${project.name}</name> 
    <my.awesome.property>property-from-pom</my.awesome.property> 
</properties>
```

## 3. 读取属性

现在是时候从配置中访问我们的属性了，让我们创建一个简单的工具类来从类路径上的文件中读取属性：

```java
public class PropertiesReader {
    private Properties properties;

    public PropertiesReader(String propertyFileName) throws IOException {
        InputStream is = getClass().getClassLoader()
              .getResourceAsStream(propertyFileName);
        this.properties = new Properties();
        this.properties.load(is);
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
}
```

接下来，我们简单地编写一个读取我们的值的测试用例：

```java
PropertiesReader reader = new PropertiesReader("properties-from-pom.properties"); 
String property = reader.getProperty("my.awesome.property");
Assert.assertEquals("property-from-pom", property);
```

## 4. 总结

在本文中，我们完成了使用Maven Properties插件读取pom.xml中定义的值的过程。