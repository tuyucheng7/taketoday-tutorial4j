## 1. 概述

在本教程中，我们将介绍如何使用YAML文件中定义的属性来配置POJO类中的Map值。

## 2. POJO和YAML

POJO类是[普通的旧Java对象]()，YAML是一种人类可读的结构化数据格式，它使用缩进来表示嵌套。

### 2.1 简单Map示例

假设我们正在经营一家在线商店，并且我们正在创建一项翻译服装尺码的服务。起初，我们只在英国销售服装，我们想知道标签“S”、“M”、“L”等指的是什么英国尺码，下面创建我们的POJO配置类：

```java
@ConfigurationProperties(prefix = "t-shirt-size")
public class TshirtSizeConfig {

	private Map<String, Integer> simpleMapping;

	public TshirtSizeConfig(Map<String, Integer> simpleMapping) {
		this.simpleMapping = simpleMapping;
	}

	// getters and setters..
}
```

注意带有prefix值的[@ConfigurationProperties]()注解，我们将在YAML文件中的相同根值下定义我们的Map，正如我们在下一节中看到的那样。

我们还需要记住在我们的Spring Boot应用程序主类上使用以下注解启用配置属性：

```java
@EnableConfigurationProperties(TshirtSizeConfig.class)
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```

### 2.2 YAML配置

现在我们添加`t-shirt-size`到我们的YAML配置中。

我们可以在application.yml文件中使用以下结构：

```yaml
t-shirt-size:
    simple-mapping:
        XS: 6
        S: 8
        M: 10
        L: 12
        XL: 14
```

注意缩进和空格，YAML使用缩进来指示嵌套，推荐的语法是每个嵌套级别使用两个空格。

请注意我们是如何将`simple-mapping`与破折号一起使用的，但我们类中的属性名称称为`simpleMapping`，带有破折号的YAML属性将自动转换为代码中的驼峰式大小写等效项。

### 2.3 更复杂的Map示例

在我们成功的英国商店之后，我们现在需要考虑将尺寸转换为其他国家的尺寸。例如，我们现在想知道在法国和美国，标签“S”的尺寸是多少，我们需要在我们的配置中添加另一层数据。

我们可以用更复杂的映射来更改我们的application.yml ：

```yaml
t-shirt-size:
    complex-mapping:
        XS:
            uk: 6
            fr: 34
            us: 2
        S:
            uk: 8
            fr: 36
            us: 4
        M:
            uk: 10
            fr: 38
            us: 6
        L:
            uk: 12
            fr: 40
            us: 8
        XL:
            uk: 14
            fr: 42
            us: 10
```

我们的POJO中的相应字段将是一个Map的Map：

```java
private Map<String, Map<String, Integer>> complexMapping;
```

## 3. 总结

在本文中，我们了解了如何在YAML配置文件中为简单的POJO定义简单和更复杂的嵌套Map。