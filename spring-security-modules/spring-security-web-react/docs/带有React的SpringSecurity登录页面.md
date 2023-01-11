## 1. 概述

[React](https://reactjs.org/) 是 Facebook 构建的基于组件的 JavaScript 库。使用 React，我们可以轻松构建复杂的 Web 应用程序。在本文中，我们将使 Spring Security 与 React 登录页面一起工作。

我们将利用前面示例的现有 Spring Security 配置。因此，我们将在上一篇关于[使用 Spring Security 创建表单登录](https://www.baeldung.com/spring-security-login)的文章的基础上构建。

## 2.设置反应

首先，让我们使用命令行工具 [create-react-app](https://github.com/facebook/create-react-app)通过执行命令“ create-react-app react”来创建一个应用程序。

我们将在 react/package.json中有如下配置：

```javascript
{
    "name": "react",
    "version": "0.1.0",
    "private": true,
    "dependencies": {
        "react": "^16.4.1",
        "react-dom": "^16.4.1",
        "react-scripts": "1.1.4"
    },
    "scripts": {
        "start": "react-scripts start",
        "build": "react-scripts build",
        "test": "react-scripts test --env=jsdom",
        "eject": "react-scripts eject"
    }
}
```

然后，我们将使用 [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin)来帮助使用 Maven 构建我们的 React 项目：

```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <version>1.6</version>
    <configuration>
        <nodeVersion>v8.11.3</nodeVersion>
        <npmVersion>6.1.0</npmVersion>
        <workingDirectory>src/main/webapp/WEB-INF/view/react</workingDirectory>
    </configuration>
    <executions>
        <execution>
            <id>install node and npm</id>
            <goals>
                <goal>install-node-and-npm</goal>
            </goals>
        </execution>
        <execution>
            <id>npm install</id>
            <goals>
                <goal>npm</goal>
            </goals>
        </execution>
        <execution>
            <id>npm run build</id>
            <goals>
                <goal>npm</goal>
            </goals>
            <configuration>
                <arguments>run build</arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

最新版本的插件可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"com.github.eirslett" AND a%3A"frontend-maven-plugin")找到。

当我们运行 mvn compile时，这个插件将下载node和npm，安装所有 node 模块依赖项并为我们构建 react 项目。

我们需要在这里解释几个配置属性。我们指定了node和npm的版本，这样插件就会知道要下载哪个版本。

我们的 React 登录页面将作为 Spring 中的静态页面，因此我们使用“ src/main/ webapp /WEB-INF/view/react ”作为npm的工作目录。

## 3.Spring安全配置

在深入研究 React 组件之前，我们更新 Spring 配置以提供 React 应用程序的静态资源：

```java
@EnableWebMvc
@Configuration
public class MvcConfig extends WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
      ResourceHandlerRegistry registry) {
 
        registry.addResourceHandler("/static/")
          .addResourceLocations("/WEB-INF/view/react/build/static/");
        registry.addResourceHandler("/.js")
          .addResourceLocations("/WEB-INF/view/react/build/");
        registry.addResourceHandler("/.json")
          .addResourceLocations("/WEB-INF/view/react/build/");
        registry.addResourceHandler("/.ico")
          .addResourceLocations("/WEB-INF/view/react/build/");
        registry.addResourceHandler("/index.html")
          .addResourceLocations("/WEB-INF/view/react/build/index.html");
    }
}
```

请注意，我们将登录页面“index.html”添加为静态资源 ，而不是动态提供的 JSP。

接下来，我们更新 Spring Security 配置以允许访问这些静态资源。

不像我们在[之前的表单登录](http://wwwbaeldung.com/spring-security-login)文章中那样使用“login.jsp”，这里我们使用“index.html”作为我们的登录页面：

```java
@Configuration
@EnableWebSecurity
@Profile("!https")
public class SecSecurityConfig {

    //...

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) 
      throws Exception {
        http.csrf().disable().authorizeRequests()
          //...
          .antMatchers(
            HttpMethod.GET,
            "/index", "/static/", "/.js", "/.json", "/.ico")
            .permitAll()
          .anyRequest().authenticated()
          .and()
          .formLogin().loginPage("/index.html")
          .loginProcessingUrl("/perform_login")
          .defaultSuccessUrl("/homepage.html",true)
          .failureUrl("/index.html?error=true")
          //...
    }
}
```

正如我们从上面的代码片段中看到的那样，当我们将表单数据发布到“ /perform_login ”时，如果凭据成功匹配，Spring 会将我们重定向到“ /homepage.html ”，否则重定向到“ /index.html?error=true ”。

## 4.反应组件

现在让我们开始接触 React。我们将使用组件构建和管理表单登录。

请注意，我们将使用 ES6 (ECMAScript 2015) 语法来构建我们的应用程序。

### 4.1. 输入

让我们从一个Input组件开始，它支持 react/src/Input.js中登录表单的<input />元素 ：

```javascript
import React, { Component } from 'react'
import PropTypes from 'prop-types'

class Input extends Component {
    constructor(props){
        super(props)
        this.state = {
            value: props.value? props.value : '',
            className: props.className? props.className : '',
            error: false
        }
    }

    //...

    render () {
        const {handleError, ...opts} = this.props
        this.handleError = handleError
        return (
          <input {...opts} value={this.state.value}
            onChange={this.inputChange} className={this.state.className} /> 
        )
    }
}

Input.propTypes = {
  name: PropTypes.string,
  placeholder: PropTypes.string,
  type: PropTypes.string,
  className: PropTypes.string,
  value: PropTypes.string,
  handleError: PropTypes.func
}

export default Input
```

如上所示，我们将 <input />元素包装到 React 控制的组件中，以便能够管理其状态并执行字段验证。

React 提供了一种使用[PropTypes](https://reactjs.org/docs/typechecking-with-proptypes.html)验证类型的方法。具体来说，我们使用Input.propTypes = {...}来验证用户传入的属性类型。

请注意，PropType验证仅适用于开发。 PropType验证是为了检查我们对组件所做的所有假设是否得到满足。

最好拥有它，而不是对生产中的随机问题感到惊讶。

### 4.2. 形式

接下来，我们将在文件Form.js中构建一个通用表单组件，该组件组合了我们的输入组件 的多个实例，我们可以将其作为登录表单的基础。

在Form组件中，我们获取 HTML <input/>元素的属性并从中创建Input组件。

然后将输入组件和验证错误消息插入到表单中：

```javascript
import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Input from './Input'

class Form extends Component {

    //...

    render() {
        const inputs = this.props.inputs.map(
          ({name, placeholder, type, value, className}, index) => (
            <Input key={index} name={name} placeholder={placeholder} type={type} value={value}
              className={type==='submit'? className : ''} handleError={this.handleError} />
          )
        )
        const errors = this.renderError()
        return (
            <form {...this.props} onSubmit={this.handleSubmit} ref={fm => {this.form=fm}} >
              {inputs}
              {errors}
            </form>
        )
    }
}

Form.propTypes = {
  name: PropTypes.string,
  action: PropTypes.string,
  method: PropTypes.string,
  inputs: PropTypes.array,
  error: PropTypes.string
}

export default Form
```

现在让我们看看我们如何管理字段验证错误和登录错误：

```javascript
class Form extends Component {

    constructor(props) {
        super(props)
        if(props.error) {
            this.state = {
              failure: 'wrong username or password!',
              errcount: 0
            }
        } else {
            this.state = { errcount: 0 }
        }
    }

    handleError = (field, errmsg) => {
        if(!field) return

        if(errmsg) {
            this.setState((prevState) => ({
                failure: '',
                errcount: prevState.errcount + 1, 
                errmsgs: {...prevState.errmsgs, [field]: errmsg}
            }))
        } else {
            this.setState((prevState) => ({
                failure: '',
                errcount: prevState.errcount===1? 0 : prevState.errcount-1,
                errmsgs: {...prevState.errmsgs, [field]: ''}
            }))
        }
    }

    renderError = () => {
        if(this.state.errcount || this.state.failure) {
            const errmsg = this.state.failure 
              || Object.values(this.state.errmsgs).find(v=>v)
            return <div className="error">{errmsg}</div>
        }
    }

    //...

}
```

在这段代码中，我们定义了 handleError函数来管理表单的错误状态。回想一下，我们也将它用于输入字段验证。实际上，handleError()作为render()函数中的回调传递给输入组件。

我们使用renderError()来构造错误消息元素。请注意，Form 的构造函数使用了一个错误属性。此属性指示登录操作是否失败。

然后是表单提交处理程序：

```javascript
class Form extends Component {

    //...

    handleSubmit = (event) => {
        event.preventDefault()
        if(!this.state.errcount) {
            const data = new FormData(this.form)
            fetch(this.form.action, {
              method: this.form.method,
              body: new URLSearchParams(data)
            })
            .then(v => {
                if(v.redirected) window.location = v.url
            })
            .catch(e => console.warn(e))
        }
    }
}
```

我们将所有表单字段包装到FormData中，并使用[fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)将其发送到服务器 。

别忘了我们的登录表单带有successUrl和failureUrl，这意味着无论请求是否成功，响应都需要重定向。

这就是为什么我们需要在响应回调中处理重定向。

### 4.3. 表单渲染

现在我们已经设置了所有需要的组件，我们可以继续将它们放入 DOM 中。基本的 HTML 结构如下(在react/public/index.html下找到)：

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <!-- ... -->
  </head>
  <body>

    <div id="root">
      <div id="container"></div>
    </div>

  </body>
</html>
```

最后，我们将在 react/src/index.js中将表单渲染到ID 为“ container”的<div/>中：

```javascript
import React from 'react'
import ReactDOM from 'react-dom'
import './index.css'
import Form from './Form'

const inputs = [{
  name: "username",
  placeholder: "username",
  type: "text"
},{
  name: "password",
  placeholder: "password",
  type: "password"
},{
  type: "submit",
  value: "Submit",
  className: "btn" 
}]

const props = {
  name: 'loginForm',
  method: 'POST',
  action: '/perform_login',
  inputs: inputs
}

const params = new URLSearchParams(window.location.search)

ReactDOM.render(
  <Form {...props} error={params.get('error')} />,
  document.getElementById('container'))
```

所以我们的表单现在包含两个输入字段：用户名 和密码，以及一个提交按钮。

这里我们向Form组件传递了一个额外的错误属性，因为我们想在重定向到失败 URL 后处理登录错误：/index.html?error=true。

![表单登录错误](https://www.baeldung.com/wp-content/uploads/2018/08/form-login-error-300x221.png)

现在我们已经完成了使用 React 构建 Spring Security 登录应用程序。我们需要做的最后一件事是运行mvn compile。

在此过程中，Maven 插件将帮助构建我们的 React 应用程序，并将构建结果收集到src/main/webapp/WEB-INF/view/react/build中。

## 5.总结

在本文中，我们介绍了如何构建 React 登录应用程序并让它与 Spring Security 后端交互。更复杂的应用程序将涉及使用[React Router](https://reacttraining.com/react-router/)或[Redux](https://redux.js.org/)的状态转换和路由，但这超出了本文的范围。

与往常一样，可以[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/spring-security-modules/spring-security-web-react)找到完整的实现。要在本地运行它，请在项目根文件夹中执行mvn jetty:run ，然后我们可以在http://localhost:8080访问 React 登录页面。