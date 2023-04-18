## 1. 简介

在本教程中，我们将学习如何构建能够创建、检索、更新和删除(CRUD)客户端数据的应用程序。该应用程序将包含一个简单的[Spring Boot RESTful API](https://www.baeldung.com/rest-with-spring-series)和一个使用[React](https://reactjs.org/)JavaScript库实现的用户界面(UI)。

## 2. Spring Boot

### 2.1 Maven 依赖项

让我们首先向pom.xml文件添加一些依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.4.4</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <version>2.4.4</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <version>2.4.4</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.200</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

在这里，我们添加了Web、测试和JPA持久层启动器，以及H2依赖项，因为应用程序将有一个H2内存数据库。

### 2.2 创建模型

接下来，让我们创建具有name和email属性的Client实体类来表示我们的数据模型：

```java
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;

    // getter, setters, contructors
}
```

### 2.3 创建Repository

然后我们将**创建从JpaRepository扩展的ClientRepository类以提供JPA CRUD功能**：

```java
public interface ClientRepository extends JpaRepository<Client, Long> {
}
```

### 2.4 创建REST控制器

最后，让我们通过创建一个控制器来与ClientRepository交互来公开一个REST API：

```java
@RestController
@RequestMapping("/clients")
public class ClientsController {

    private final ClientRepository clientRepository;

    public ClientsController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable Long id) {
        return clientRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping
    public ResponseEntity createClient(@RequestBody Client client) throws URISyntaxException {
        Client savedClient = clientRepository.save(client);
        return ResponseEntity.created(new URI("/clients/" + savedClient.getId())).body(savedClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateClient(@PathVariable Long id, @RequestBody Client client) {
        Client currentClient = clientRepository.findById(id).orElseThrow(RuntimeException::new);
        currentClient.setName(client.getName());
        currentClient.setEmail(client.getEmail());
        currentClient = clientRepository.save(client);

        return ResponseEntity.ok(currentClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteClient(@PathVariable Long id) {
        clientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
```

### 2.5 启动我们的API

完成后，我们现在准备启动我们的Spring Boot API。我们可以使用spring-boot-maven-plugin来做到这一点：

```shell
mvn spring-boot:run
```

然后我们将能够通过访问[http://localhost:8080/clients](http://localhost:8080/clients)来获取我们的客户端列表。

### 2.6 创建客户端

此外，我们可以使用[Postman](https://www.baeldung.com/postman-testing-collections)创建一些客户端：

```shell
curl -X POST http://localhost:8080/clients -d '{"name": "John Doe", "email": "john.doe@tuyucheng.com"}'
```

## 3. React

React是一个用于创建用户界面的JavaScript库。使用React需要安装[Node.js](https://nodejs.org/)，我们可以在[Node.js下载页面](https://nodejs.org/en/download)找到安装说明。

### 3.1 创建React UI

**[Create React App](https://reactjs.org/docs/create-a-new-react-app.html)是一个命令实用程序，可以为我们生成React项目**。让我们通过运行以下命令在我们的Spring Boot应用程序基目录中创建我们的前端应用程序：

```shell
npx create-react-app frontend
```

应用程序创建过程完成后，我们将在frontend目录中安装[Bootstrap](https://getbootstrap.com/)、[React Router](https://reactrouter.com/)和[reactstrap](https://reactstrap.github.io/)：

```shell
npm install --save bootstrap@5.1 react-cookie@4.1.1 react-router-dom@5.3.0 reactstrap@8.10.0
```

我们将使用Bootstrap的CSS和Reactstrap的组件来创建更好看的UI，并使用React Router组件来处理应用程序的可导航性。

让我们将Bootstrap的CSS文件作为导入添加到app/src/index.js中：

```javascript
import 'bootstrap/dist/css/bootstrap.min.css';
```

### 3.2 启动我们的React UI

现在准备启动我们的前端应用程序：

```shell
npm start
```

在我们的浏览器中访问[http://localhost:3000](http://localhost:3000/)时，我们应该会看到React示例页面：

<img src="../assets/img.png">

### 3.3 调用我们的Spring Boot API

调用我们的Spring Boot API需要设置我们的React应用程序的package.json文件，以便在调用API时配置代理。

为此，我们将在package.json中包含API的URL：

```json
...
"proxy": "http://localhost:8080",
...
```

接下来，让我们编辑frontend/src/App.js以便它调用我们的API来显示具有name和email属性的客户端列表：

```javascript
class App extends Component {
    state = {
        clients: []
    };

    async componentDidMount() {
        const response = await fetch('/clients');
        const body = await response.json();
        this.setState({clients: body});
    }

    render() {
        const {clients} = this.state;
        return (
            <div className="App">
                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo" />
                    <div className="App-intro">
                        <h2>Clients</h2>
                        {clients.map(client =>
                            <div key={client.id}>
                                {client.name} ({client.email})
                            </div>
                        )}
                    </div>
                </header>
            </div>
        );
    }
}
export default App;
```

在componentDidMount函数中，**我们获取客户端API**并在clients变量中设置响应主体。在我们的render函数中，我们返回带有在API中找到的客户端列表的HTML。

我们将看到客户端的页面，它看起来像这样：

<img src="../assets/img_1.png">

> **注意：确保Spring Boot应用程序正在运行，以便UI能够调用API**。

### 3.4 创建一个ClientList组件

我们现在可以**改进我们的UI以显示更复杂的组件来使用我们的API列出、编辑、删除和创建客户端**。稍后，我们将看到如何使用此组件并从App组件中删除客户端列表。

让我们在frontend/src/ClientList.js中创建一个文件：

```javascript
import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class ClientList extends Component {
    constructor(props) {
        super(props);
        this.state = {clients: []};
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        fetch('/clients')
            .then(response => response.json())
            .then(data => this.setState({clients: data}));
    }
}
export default ClientList;
```

与在App.js中一样，componentDidMount函数调用我们的API来加载我们的客户端列表。

当我们想要删除客户端时，我们还将包括remove函数来处理对API的DELETE调用。此外，我们将创建render函数，该函数将使用Edit、Delete和Add Client操作渲染HTML：

```javascript
async remove(id) {
    await fetch(`/clients/${id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(() => {
        let updatedClients = [...this.state.clients].filter(i => i.id !== id);
        this.setState({clients: updatedClients});
    });
}

render() {
    const {clients, isLoading} = this.state;

    if (isLoading) {
        return <p>Loading...</p>;
    }

    const clientList = clients.map(client => {
        return <tr key={client.id}>
            <td style={{whiteSpace: 'nowrap'}}>{client.name}</td>
            <td>{client.email}</td>
            <td>
                <ButtonGroup>
                    <Button size="sm" color="primary" tag={Link} to={"/clients/" + client.id}>Edit</Button>
                    <Button size="sm" color="danger" onClick={() => this.remove(client.id)}>Delete</Button>
                </ButtonGroup>
            </td>
        </tr>
    });

    return (
        <div>
            <AppNavbar/>
            <Container fluid>
                <div className="float-right">
                    <Button color="success" tag={Link} to="/clients/new">Add Client</Button>
                </div>
                <h3>Clients</h3>
                <Table className="mt-4">
                    <thead>
                        <tr>
                            <th width="30%">Name</th>
                            <th width="30%">Email</th>
                            <th width="40%">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {clientList}
                    </tbody>
                </Table>
            </Container>
        </div>
    );
}
```

### 3.5 创建一个ClientEdit组件

ClientEdit组件将负责**创建和编辑我们的客户端**。

让我们在frontend/src/ClientEdit.js中创建一个文件：

```javascript
import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import AppNavbar from './AppNavbar';

class ClientEdit extends Component {
    emptyItem = {
        name: '',
        email: ''
    };

    constructor(props) {
        super(props);
        this.state = {
            item: this.emptyItem
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }
}
export default withRouter(ClientEdit);
```

让我们添加componentDidMount函数来检查我们是在处理创建还是编辑功能；在编辑的情况下，它将从API获取我们的客户端：

```javascript
async componentDidMount() {
    if (this.props.match.params.id !== 'new') {
        const client = await (await fetch(`/clients/${this.props.match.params.id}`)).json();
        this.setState({item: client});
    }
}
```

然后在handleChange函数中，我们将更新提交表单时将使用的组件状态项属性：

```javascript
handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let item = {...this.state.item};
    item[name] = value;
    this.setState({item});
}
```

在handeSubmit中，我们将调用我们的API，根据我们调用的功能将请求发送到PUT或POST方法。为此，我们可以检查id属性是否已填充：

```javascript
async handleSubmit(event) {
    event.preventDefault();
    const {item} = this.state;

    await fetch('/clients' + (item.id ? '/' + item.id : ''), {
        method: (item.id) ? 'PUT' : 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(item),
    });
    this.props.history.push('/clients');
}
```

最后但同样重要的是，我们的render函数将处理我们的表单：

```javascript
render() {
    const {item} = this.state;
    const title = <h2>{item.id ? 'Edit Client' : 'Add Client'}</h2>;

    return <div>
        <AppNavbar/>
        <Container>
            {title}
            <Form onSubmit={this.handleSubmit}>
                <FormGroup>
                    <Label for="name">Name</Label>
                    <Input type="text" name="name" id="name" value={item.name || ''}
                           onChange={this.handleChange} autoComplete="name"/>
                </FormGroup>
                <FormGroup>
                    <Label for="email">Email</Label>
                    <Input type="text" name="email" id="email" value={item.email || ''}
                           onChange={this.handleChange} autoComplete="email"/>
                </FormGroup>
                <FormGroup>
                    <Button color="primary" type="submit">Save</Button>{' '}
                    <Button color="secondary" tag={Link} to="/clients">Cancel</Button>
                </FormGroup>
            </Form>
        </Container>
    </div>
}
```

>   **注意：我们还有一个Link，其路由配置为在单击“Cancel”按钮时返回到/clients**。

### 3.6 创建AppNavbar组件

为了给我们的应用程序提供更好的可导航性，让我们在frontend/src/AppNavbar.js中创建一个文件：

```javascript
import React, {Component} from 'react';
import {Navbar, NavbarBrand} from 'reactstrap';
import {Link} from 'react-router-dom';

export default class AppNavbar extends Component {
    constructor(props) {
        super(props);
        this.state = {isOpen: false};
        this.toggle = this.toggle.bind(this);
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    render() {
        return <Navbar color="dark" dark expand="md">
            <NavbarBrand tag={Link} to="/">Home</NavbarBrand>
        </Navbar>;
    }
}
```

在render函数中，**我们将使用react-router-dom功能创建一个Link以路由到我们的应用程序主页**。

###  3.7 创建我们的主页组件

该组件将成为我们的应用程序主页，并且将有一个指向我们之前创建的ClientList组件的按钮。

让我们在frontend/src/Home.js中创建一个文件：

```javascript
import React, { Component } from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';

class Home extends Component {
    render() {
        return (
            <div>
                <AppNavbar/>
                <Container fluid>
                    <Button color="link"><Link to="/clients">Clients</Link></Button>
                </Container>
            </div>
        );
    }
}
export default Home;
```

>   **注意：在这个组件中，我们还有一个来自react-router-dom的链接，将我们引向/clients。该路由将在下一步中配置**。

### 3.8 使用React Router

现在我们将使用[React Router](https://reactrouter.com/)在我们的组件之间导航。

让我们更改App.js：

```javascript
import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import ClientList from './ClientList';
import ClientEdit from "./ClientEdit";

class App extends Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={Home}/>
                    <Route path='/clients' exact={true} component={ClientList}/>
                    <Route path='/clients/:id' component={ClientEdit}/>
                </Switch>
            </Router>
        )
    }
}

export default App;
```

如我们所见，我们为我们创建的每个组件定义了应用程序路由。

当访问[localhost:3000](http://localhost:3000/)时，我们现在的主Home页面带有Clients链接：

<img src="../assets/img_2.png">

单击Clients链接，我们现在有了列出客户端，以及Edit、Remove和Add Client功能：

<img src="../assets/img_3.png">

## 4. 构建和打包

要使用Maven构建和打包我们的React应用程序，我们将使用[frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin)。

该插件将负责将我们的前端应用程序打包并复制到我们的Spring Boot API构建文件夹中：

```xml
<properties>
    ...
    <frontend-maven-plugin.version>1.6</frontend-maven-plugin.version>
    <node.version>v14.8.0</node.version>
    <yarn.version>v1.12.1</yarn.version>
    ...
</properties>
...
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-resources-plugin</artifactId>
            <version>3.1.0</version>
            <executions>
                ...
            </executions>
        </plugin>
        <plugin>
            <groupId>com.github.eirslett</groupId>
            <artifactId>frontend-maven-plugin</artifactId>
            <version>${frontend-maven-plugin.version}</version>
            <configuration>
                ...
            </configuration>
            <executions>
                ...
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```

让我们仔细看看我们的[maven-resources-plugin](https://maven.apache.org/plugins/maven-resources-plugin/)，它负责将我们的frontend源复制到应用程序target文件夹：

```xml
...
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>copy-resources</id>
            <phase>process-classes</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <outputDirectory>${basedir}/target/classes/static</outputDirectory>
                <resources>
                    <resource>
                        <directory>frontend/build</directory>
                    </resource>
                </resources>
            </configuration>
        </execution>
    </executions>
</plugin>
...
```

front-end-maven-plugin将负责安装Node.js和[Yarn](https://yarnpkg.com/)，然后构建和测试我们的前端应用程序：

```xml
...
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <version>${frontend-maven-plugin.version}</version>
    <configuration>
        <workingDirectory>frontend</workingDirectory>
    </configuration>
    <executions>
        <execution>
            <id>install node</id>
            <goals>
                <goal>install-node-and-yarn</goal>
            </goals>
            <configuration>
                <nodeVersion>${node.version}</nodeVersion>
                <yarnVersion>${yarn.version}</yarnVersion>
            </configuration>
        </execution>
        <execution>
            <id>yarn install</id>
            <goals>
                <goal>yarn</goal>
            </goals>
            <phase>generate-resources</phase>
        </execution>
        <execution>
            <id>yarn test</id>
            <goals>
                <goal>yarn</goal>
            </goals>
            <phase>test</phase>
            <configuration>
                <arguments>test</arguments>
                <environmentVariables>
                    <CI>true</CI>
                </environmentVariables>
            </configuration>
        </execution>
        <execution>
            <id>yarn build</id>
            <goals>
                <goal>yarn</goal>
            </goals>
            <phase>compile</phase>
            <configuration>
                <arguments>build</arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
...
```

> **注意：要指定不同的Node.js版本，我们可以简单地编辑pom.xml中的node.version属性**。

## 5. 运行我们的Spring Boot React CRUD应用程序

最后，通过添加插件，我们可以通过运行以下命令访问我们的应用程序：

```shell
mvn spring-boot:run
```

我们的React应用程序将通过[http://localhost:8080/](http://localhost:8080/) URL完全集成到我们的API中。

## 6. 总结

在本文中，我们研究了如何使用Spring Boot和React创建CRUD应用程序。为此，我们首先创建了一些REST API端点来与我们的数据库进行交互。然后我们创建了一些React组件来使用我们的API获取和写入数据。我们还学习了如何将带有React UI的Spring Boot应用程序打包到一个应用程序包中。