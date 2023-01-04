## 1. 概述

Thymeleaf是与Spring Boot捆绑在一起的流行模板引擎。我们已经发表了几篇关于它的文章，我们强烈建议你阅读[Thymeleaf系列](https://www.baeldung.com/tag/thymeleaf/)。

在本教程中，我们将学习如何在Thymeleaf中使用选择和选项标签。

## 2. HTML基础知识

在HTML中，我们可以构建一个包含多个值的下拉列表：

```html
<select>
    <option value="apple">Apple</option>
    <option value="banana">Banana</option>
    <option value="orange">Orange</option>
    <option value="pear">Pear</option>
</select>
```

每个列表都包含选择和嵌套的选项标签。默认情况下，Web浏览器将呈现一个列表，其中第一个选项是预选的。

我们可以通过使用selected属性来控制选择哪个值：

```html
<option value="orange" selected>Orange</option>
```

此外，我们可以使用disabled属性指定一个选项不可选择：

```html
<option disabled>Please select...</option>
```

## 3. Thymeleaf

在Thymeleaf中，我们可以使用th:field属性将视图与模型绑定：

```html
<select th:field="*{gender}">
    <option th:value="'M'" th:text="Male"></option>
    <option th:value="'F'" th:text="Female"></option>
</select>
```

虽然上面的示例不需要使用模板引擎，但在后面更高级的示例中，我们将看到Thymeleaf的强大功能。

### 3.1 没有选择的选项

在有更多选项可供选择的情况下，显示所有选项的一种简洁明了的方法是将th:each属性与th:value和th:text一起使用：

```html
<select th:field="*{percentage}">
    <option th:each="i : ${#numbers.sequence(0, 100)}" th:value="${i}" th:text="${i}">
    </option>
</select>
```

在上面的示例中，我们使用了从0到100的数字序列。我们将每个数字i的值分配给选项标签的value属性，并且我们使用与显示值相同的数字。

Thymeleaf代码将在浏览器中呈现为：

```html
<select id="percentage" name="percentage">
    <option value="0">0</option>
    <option value="1">1</option>
    <option value="2">2</option>
    ...
    <option value="100">100</option>
</select>
```

让我们将此示例视为创建；所以我们从一个新的表格开始，百分比值不需要预先选择。

### 3.2 选定的选项

假设我们想用更新功能扩展我们的表单。为了返回到先前创建的记录并使用现有数据填充表单，需要选择该选项。

我们可以通过添加th:selected属性以及一些条件来实现这一点：

```html
<select th:field="*{percentage}">
    <option th:each="i : ${#numbers.sequence(0, 100)}" th:value="${i}" th:text="${i}"
            th:selected="${i==75}"></option>
</select>
```

在上面的例子中，我们想通过检查i是否等于75来预选75的值。

但是，此代码将不起作用，呈现的HTML将是：

```html
<select id="percentage" name="percentage">
    <option value="0">0</option>
    ...
    <option value="74">74</option>
    <option value="75">75</option>
    <option value="76">76</option>
    ...
    <option value="100">100</option>
</select>
```

要修复它，我们需要删除th:field并将其替换为name和id属性：

```html
<select id="percentage" name="percentage">
```

最后，我们会得到：

```xml
<select id="percentage" name="percentage">
    <option value="0">0</option>
    ...
    <option value="74">74</option>
    <option value="75" selected="selected">75</option>
    <option value="76">76</option>
    ...
    <option value="100">100</option>
</select>
```

## 4. 用列表填充下拉列表 

现在让我们看看如何在Thymeleaf中使用列表填充下拉列表。为此，我们将在控制器中创建一个字符串列表，并将其显示在视图中。

首先，我们将创建一个控制器，其中包含一个初始化字符串列表的方法。然后我们将使用模型属性来保存我们的列表以在视图内呈现：

```java
@RequestMapping(value = "/populateDropDownList", method = RequestMethod.GET) 
public String populateList(Model model) {
    List<String> options = new ArrayList<String>();
    options.add("option 1");
    options.add("option 2");
    options.add("option 3");
    model.addAttribute("options", options);
    return "dropDownList/dropDownList.html";
}
```

最后，我们可以引用我们的列表模型属性并对其进行循环，以将每个列表元素显示为下拉列表的一个选项：

```xml
<select class="form-control" id="dropDownList">
    <option value="0">select option</option>
    <option th:each="option : ${options}" th:value="${option}" th:text="${option}"></option>
</select>
```

## 5. 总结

在这篇简短的文章中，我们演示了如何在Thymeleaf中使用下拉/列表选择器。我们还讨论了预选值的一个常见陷阱，并为它制定了解决方案。