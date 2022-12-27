## 1. 概述

在本教程中，我们将介绍一个示例应用程序，该应用程序使用Vue.js前端呈现单个页面，同时使用Spring Boot作为后端。

我们还将利用Thymeleaf将信息传递给模板。

## 2. Spring Boot设置

应用程序pom.xml使用[spring-boot-starter-thymeleaf](https://search.maven.org/search?q=spring-boot-starter-thymeleaf)依赖项进行模板渲染以及通常的[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web)：

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId> 
    <version>2.4.0</version>
</dependency> 
<dependency> 
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId> 
    <version>2.4.0</version>
</dependency>
```

默认情况下，Thymeleaf在templates/查找视图模板，我们将添加一个空的index.html文件到src/main/resources/templates/目录中，我们将在下一节中更新其内容。

最后，我们的Spring Boot控制器将位于src/main/java中：

```java
@Controller
public class MainController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("eventName", "FIFA 2018");
        return "index";
    }
}
```

该控制器使用model.addAttribute将数据通过Spring Web的Model对象传递给视图来呈现单个模板。

让我们使用以下命令运行应用程序：

```bash
mvn spring-boot:run
```

在浏览器访问http://localhost:8080以查看index页面，当然，此时它将是空的。

我们的目标是让页面打印出类似这样的内容：

```text
Name of Event: FIFA 2018

Lionel Messi
Argentina's superstar

Christiano Ronaldo
Portugal top-ranked player
```

## 3. 使用Vue.Js组件渲染数据

### 3.1 模板的基本设置

在模板中，让我们加载Vue.js和Bootstrap(可选)来渲染用户界面：

```html
// in head tag

<!-- Include Bootstrap -->

//  other markup

// at end of body tag
<script src="https://cdn.jsdelivr.net/npm/vue@2.5.16/dist/vue.js">
</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/babel-standalone/6.21.1/babel.min.js">
</script>
```

在这里，我们从CDN加载Vue.js，但如果愿意，你也可以托管它。

我们在浏览器中加载Babel，这样我们就可以在页面中编写一些符合ES6的代码，而无需运行转换步骤。

在实际应用程序中，你可能会使用Webpack和Babel转译器等工具的构建过程，而不是使用浏览器内的Babel。

现在让我们保存页面并使用mvn spring-boot:run命令重新启动应用程序，我们刷新浏览器以查看我们的更新；还没有什么有趣的。

接下来，让我们设置一个空的div元素，我们将在其上附加用户界面：

```html
<div id="contents"></div>
```

接下来我们在页面上搭建一个Vue应用程序：

```javascript
<script type="text/babel">
    var app = new Vue({
        el: '#contents'
    });
</script>
```

刚才发生了什么？这段代码在页面上创建一个Vue应用程序，我们使用CSS选择器#contents将它附加到元素上。

这是指页面上的空div元素，该应用程序现已设置为使用Vue.js！

### 3.2 在模板中显示数据

接下来，让我们创建一个标头，显示我们从Spring控制器传递的“eventName”属性，并使用Thymeleaf的功能渲染它：

```html
<div class="lead">
    <strong>Name of Event:</strong>
    <span th:text="${eventName}"></span>
</div>
```

现在让我们将“data”属性附加到Vue应用程序以保存我们的players数据数组，这是一个简单的JSON数组。

我们的Vue应用现在看起来像这样：

```html
<script type="text/babel">
    var app = new Vue({
        el: '#contents',
        data: {
            players: [
                { id: "1", 
                  name: "Lionel Messi", 
                  description: "Argentina's superstar" },
                { id: "2", 
                  name: "Christiano Ronaldo", 
                  description: "World #1-ranked player from Portugal" }
            ]
        }
    });
</script>
```

现在Vue.js知道一个名为players的数据属性。

### 3.3 使用Vue.js组件渲染数据

接下来，让我们创建一个名为player-card的Vue.js组件，它只呈现一个player，**请记住在创建Vue应用程序之前注册此组件**。

否则，Vue将找不到它：

```javascript
Vue.component('player-card', {
    props: ['player'],
    template: `<div class="card">
        <div class="card-body">
            <h6 class="card-title">
                {{ player.name }}
            </h6>
            <p class="card-text">
                <div>
                    {{ player.description }}
                </div>
            </p>
        </div>
        </div>`
});
```

最后，让我们遍历app对象中的players，并为每个player渲染一个player-card组件：

```javascript
<ul>
    <li style="list-style-type:none" v-for="player in players">
        <player-card
          v-bind:player="player" 
          v-bind:key="player.id">
        </player-card>
    </li>
</ul>
```

这里的逻辑是名为v-for的Vue指令，它将遍历players数据属性中的每个player，并为<li\>元素内的每个player条目呈现一个player-card。

v-bind:player意味着player-card组件将被赋予一个名为player的属性，其值将是当前正在使用的player循环变量，v-bind:key是使每个<li\>元素唯一的必要条件。

通常，player.id是一个不错的选择，因为它已经是唯一的。

现在，如果你重新加载此页面，请观察devtools中生成的HTML标签，它看起来类似于：

```html
<ul>
    <li style="list-style-type: none;">
        <div class="card">
            // contents
        </div>
    </li>
    <li style="list-style-type: none;">
        <div class="card">
            // contents
        </div>
    </li>
</ul>
```

工作流程改进说明：每次更改代码时都必须重新启动应用程序并刷新浏览器，这很快就会变得很麻烦。

因此，为了方便起见，请参考[这篇](https://www.baeldung.com/spring-boot-devtools)关于如何使用Spring Boot devtools和自动重启的文章。

## 4. 总结

在这篇简短的文章中，我们介绍了如何使用Spring Boot作为后端并使用Vue.js作为前端来设置Web应用程序，这个秘诀可以构成更强大和可扩展应用程序的基础，而这只是大多数此类应用程序的起点。