备忘单的名称看起来像是不允许或可能被滥用的东西。在软件参考中，备忘单是用于快速参考或用于了解过于具体但没有任何细节(主要是代码、语法或公式)的东西。因此，本教程旨在让你快速参考一些 Postman 命令、代码和语法。请注意，此处使用的所有语法和代码都是 Postman 中编写代码/测试的最新风格。

有关更多信息，你可以访问 [https://learning.getpostman.com/docs/postman/scripts/test_examples/](https://learning.getpostman.com/docs/postman/scripts/test_examples/)

## 变量

使用环境和变量的代码

### 设置变量

处理变量的代码

#### 全局变量

```java
pm.globals.set('variable name', "value");
```

#### 局部变量

```java
var variable_name = value;
```

#### 环境变量

```java
pm.environment.set('variable_name' , 'value');
```

### 获取变量

#### 全局变量

```java
pm.globals.get('variable_name');
```

#### 环境变量

```java
pm.environment.get('varibable_name');
```

#### 数据变量

```java
pm.iterationData.get('variable_name');
```

#### 局部变量

```java
variable_name;
```

#### 清除变量

#### 全局变量(只有一个)

```java
pm.globals.unset('variable_name');
```

#### 全局变量(全部)

```java
pm.globals.clear();
```

#### 环境变量(只有一个)

```java
pm.environment.unset('variable_name');
```

#### 环境变量(全部)

```java
pm.environment.clear();
```

## 断言

### 回复

与响应相关的不同代码。

#### 响应包含一个字符串

```java
pm.test("String found", function(){

pm.expect(pm.response.text()).to.include("string you want to search");

});
```

#### 响应体等于一个字符串

```java
pm.test("Body is equal to string", function(){

pm.response.to.have.body("string you want to check");
 
});
```

### 状态码

Postman中状态码相关的代码

#### 单一状态代码

```java
pm.response.to.have.status(status_code);
```

#### 多重状态码

```java
pm.expect(pm.response.code).to.be.oneOf([status_code, status_code]);
```

### 响应时间

```java
pm.expect(pm.response.responseTime.to.be.below(time));
```

### 检查 JSON 值

```java
pm.test("Your_Test_Name", function(){

var jsonData = pm.response.json();

pm.expect(jsonData.value).to.eql(value);

});
```

### 内容类型呈现

```java
pm.test("Your_Test_Name", function(){

pm.response.to.have.header("Content-Type");

});
```

### 标头存在

```java
pm.response.to.have.header('X-Cache');
```

### Cookie 存在

```java
pm.expect(pm.cookies.has('sessionID')).to.be.true;
```

### 身体

body 字段相关的代码。

#### 完全符合

```java
pm.response.to.have.body("OK");
pm.response.to.have.body('{"success"=true}');
```

#### 部分匹配

```java
pm.expect(pm.response.text()).to.include('ToolsQA');
```

### 发送异步请求

```java
pm.sendRequest("https://postman-echo.com/get", function(err, response){

console.log(response.json());

});
```

## JSON 响应

### 解析体

```java
var jsonData = pm.response.json();
```

### 检查响应中的数组计数

```java
pm.test("ISBN Count", function () {
pm.expect(2).to.eql(pm.response.json().arrayName.length);
});
```

### 检查数组中的特定值

此示例检查响应中收到的所有书籍中的特定 ISBN 号，如果找到则返回 true。

```java
pm.test("Test Name", function () {
var result;
for (var loop = 0; loop < pm.response.json().arrayName.length; loop++)
{
if (pm.response.json().arrayName[loop].arrayElement=== pm.variables.get("arrayElementValue")){
result=true;
break;
}
}
pm.expect(true).to.eql(result);
});
```

上面的两个例子都使用了 Javascript 进行编码，因为 Postman Sandbox 在 javascript 中工作。这段代码与 Postman 没有具体的关系。请参考 Javascript。

### 检查值

```java
pm.expect(jsonData.age).to.eql(value);

pm.expect(jsonData.name).to.eql("string");
```

### 将 XML 正文转换为 JSON 对象

```java
var jsonObject = xml2Json(responseBody);
```

## 工作流程

### 设置下一个请求

```java
postman.setNextRequest("Request Name");
```

### 停止执行请求

```java
postman.setNextRequest(null);
```

## 柴断言图书馆

### 查找数组中存在的数字

```java
pm.test(“Number included”, function(){
pm.expect([1,2,3]).to.include(3);
});
```

### 检查数组是否为空

```java
pm.test(“Empty Array”, function(){
pm.expect([2]).to.be.an(‘array’).that.is.empty;
});
```

我希望这将成为你邮递员旅程中的一个快速参考点。