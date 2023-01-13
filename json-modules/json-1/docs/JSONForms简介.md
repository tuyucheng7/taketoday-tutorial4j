## 1. 概述

在本系列的[第一篇文章](https://www.baeldung.com/introduction-to-json-schema-in-java)中，我们介绍了JSON Schema的概念以及如何使用它来验证JSON 对象的格式和结构。

在本文中，我们将了解如何利用JSON和JSON Schema 的功能构建基于表单的 Web UI。

为了实现我们的目标，我们将使用一个名为[JSON Forms](https://github.com/eclipsesource/jsonforms)的框架。它消除了手动编写 HTML 模板和 Javascript 进行数据绑定以创建可定制表单的需要。

然后使用 UI 框架呈现表单，目前基于 AngularJS。

## 2. JSON 表单的组件

要创建我们的表单，我们需要定义两个主要组件。

第一个组件是数据模式，定义要在 UI 中显示的基础数据(对象类型及其属性)。

在这种情况下，我们可以使用我们在[上一篇文章中使用的](https://www.baeldung.com/introduction-to-json-schema-in-java)JSON Schema来描述数据对象“Product”：

```java
{
    "$schema": "http://json-schema.org/draft-04/schema#",
    "title": "Product",
    "description": "A product from the catalog",
    "type": "object",
    "properties": {
        "id": {
            "description": "The unique identifier for a product",
            "type": "integer"
        },
        "name": {
            "description": "Name of the product",
            "type": "string"
        },
        "price": {
            "type": "number",
            "minimum": 0,
            "exclusiveMinimum": true
        }
    },
    "required": ["id", "name", "price"]
}
```

正如我们所见，JSON 对象显示了三个名为id、name和price的属性。它们中的每一个都将是一个以其名称标记的表单字段。

每个属性也有一些属性。type属性将由框架转换为输入字段的类型。

price属性的minimum和exclusiveMinimum属性告诉框架，在验证表单时，该输入字段的值必须大于 0。

最后，包含先前定义的所有属性的required属性指定需要填写所有表单字段。

第二个组件是UI 架构，它描述了表单的布局以及数据架构的哪些属性将呈现为控件：

```java
{
    "type": "HorizontalLayout",
    "elements": [
        {
            "type": "Control",
            "scope": { "$ref": "#/properties/id" }
        },
        {
            "type": "Control",
            "scope": { "$ref": "#/properties/name" }
        },
        {
            "type": "Control",
            "scope": { "$ref": "#/properties/price" }
        },
    ]
}

```

type属性定义为表单字段将在表单中排序。在这种情况下，我们选择了水平方式。

此外，UI 架构定义了数据架构的哪个属性将显示为表单字段。这是通过在elements数组中定义一个元素Control来获得的。

最后，控件通过范围属性直接引用数据模式，因此不必数据属性的规范，例如它们的数据类型。

## 3. 在 AngularJS 中使用 JSON 表单

创建的数据模式和UI 模式在运行时进行解释，即当包含表单的网页显示在浏览器上时，并转换为基于 AngularJS 的 UI，该 UI 已经具有完整的功能，包括数据绑定、验证等。

现在，让我们看看如何将 JSON 表单嵌入到基于 AngularJS 的 Web 应用程序中。

## 3.1. 设置项目

作为设置我们项目的先决条件，我们需要在我们的机器上安装node.js。如果你之前没有安装它，可以按照[官方网站上](https://nodejs.org/)的说明进行操作。

node.js还带有npm，它是用于安装 JSON Forms 库和其他所需依赖项的包管理器。

安装node.js并从[GitHub](https://github.com/eugenp/tutorials/tree/master/json-modules/json)克隆示例后，打开 shell 并 cd 进入webapp文件夹。此文件夹包含package.json文件等。它显示了有关该项目的一些信息，并且主要告诉npm它必须下载哪些依赖项。package,json文件将如下所示：

```java
{
    "name": "jsonforms-intro",
    "description": "Introduction to JSONForms",
    "version": "0.0.1",
    "license": "MIT",
    "dependencies": {
         "typings": "0.6.5",
         "jsonforms": "0.0.19",
         "bootstrap": "3.3.6"
     }
}
```

现在，我们可以输入npm install命令。这将开始下载所有需要的库。下载后，我们可以在node_modules文件夹中找到这些库。

有关更多详细信息，你可以参考[jsonforms npm 的页面](https://www.npmjs.com/package/jsonforms)。

## 4.定义视图

现在我们有了所有需要的库和依赖项，让我们定义一个显示表单的 html 页面。

在我们的页面中，我们需要导入jsonforms.js库并使用专用的 AngularJS 指令jsonforms嵌入表单：

```html
<!DOCTYPE html>
<html ng-app="jsonforms-intro">
<head>
    <title>Introduction to JSONForms</title>
    <script src="node_modules/jsonforms/dist/jsonforms.js" 
      type="text/javascript"></script>
    <script src="js/app.js" type="text/javascript"></script>
    <script src="js/schema.js" type="text/javascript"></script>
    <script src="js/ui-schema.js" type="text/javascript"></script>
</head>
<body>
    <div class="container" ng-controller="MyController">
        <div class="row" id="demo">
            <div class="col-sm-12">
                <div class="panel-primary panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title"><strong>Introduction 
                          to JSONForms</strong></h3>
                    </div>
                    <div class="panel-body jsf">
                        Bound data: {{data}}
                        <jsonforms schema="schema" 
                          ui-schema="uiSchema" data="data"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>

```

作为该指令的参数，我们需要指向上面定义的数据模式和UI 模式，以及将包含要显示的数据的JSON 对象。

## 5. AngularJS 控制器

在 AngularJS 应用程序中，指令所需的值通常由控制器提供：

```javascript
app.controller('MyController', ['$scope', 'Schema', 'UISchema', 
  function($scope, Schema, UISchema) {

    $scope.schema = Schema;
    $scope.uiSchema = UISchema;
    $scope.data = {
        "id": 1,
        "name": "Lampshade",
        "price": 1.85
    };
}]);
```

## 6. 应用模块

接下来我们需要在我们的应用程序模块中注入jsonforms ：

```javascript
var app = angular.module('jsonforms-intro', ['jsonforms']);
```

## 7.显示表格

如果我们用浏览器打开上面定义的 html 页面，我们可以看到我们的第一个 JSONForm：

[![JSON表单](https://www.baeldung.com/wp-content/uploads/2016/08/JSONForms-1024x159.png)](https://www.baeldung.com/wp-content/uploads/2016/08/JSONForms-1024x159.png)

## 八. 总结

在本文中，我们了解了如何使用JSONForms库构建 UI 表单。耦合数据模式和UI 模式，消除了手动编写 HTML 模板和 Javascript 进行数据绑定的需要。