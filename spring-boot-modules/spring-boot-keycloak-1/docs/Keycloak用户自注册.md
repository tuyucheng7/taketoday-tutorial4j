## 1. 概述

我们可以使用[Keycloak](https://www.keycloak.org/)作为第三方授权服务器来管理我们的Web或移动应用程序的用户。

虽然管理员可以添加用户，但Keycloak也可以让用户自行注册。此外，除了名字、姓氏和电子邮件等默认属性外，我们还可以根据应用程序的需要添加额外的用户属性。

在本教程中，我们将了解**如何在Keycloak上启用自助注册并在用户注册页面上添加自定义字段**。

我们在[自定义登录页面]()的基础上进行构建，因此在初始设置时先完成它会很有帮助。

## 2. 独立服务器

首先，我们将看到[独立]()Keycloak服务器的用户自助注册。

### 2.1 启用用户注册

**最初，我们需要启用Keycloak以允许用户注册**。为此，我们首先需要通过从Keycloak发行版的bin文件夹中运行此命令来启动服务器：

```bash
./standalone.sh -Djboss.socket.binding.port-offset=100
```

然后我们需要转到[管理控制台](http://localhost:8080/admin)并输入initial1/zaq1!QAZ凭据。

接下来，在Realm Settings页面的Login选项卡中，我们将切换User registration按钮：

<img src="../assets/img_36.png">

就这样！我们只需单击“Save”即可启用自助注册。

所以现在我们将在[登录页面](http://localhost:8080/realms/SpringBootKeycloak/protocol/openid-connect/auth?response_type=code&client_id=login-app&scope=openid&redirect_uri=http://localhost:8081/)上获得一个名为Register的链接：

<img src="../assets/img_37.png">

再次回想一下，该页面看起来与Keycloak的默认登录页面不同，因为我们正在扩展[我们之前所做的自定义]()。

注册链接将我们带到注册页面：

<img src="../assets/img_38.png">

我们可以看到，**默认页面包含了Keycloak用户的基本属性**。

在下一节中，我们将看到如何为我们的选择添加额外的属性。

### 2.2 添加自定义用户属性

继续我们的[自定义主题]()，让我们将现有模板base/login/register.ftl复制到我们的custom/login文件夹中。

我们现在将尝试为Date of birth添加一个新的字段dob，为此，我们需要修改上面的register.ftl并添加以下内容：

```html
<div class="form-group">
    <div class="${properties.kcLabelWrapperClass!}">
        <label for="user.attributes.dob" class="${properties.kcLabelClass!}">
            Date of birth</label>
    </div>

    <div class="${properties.kcInputWrapperClass!}">
        <input type="date" class="${properties.kcInputClass!}"
               id="user.attributes.dob" name="user.attributes.dob"
               value="${(register.formData['user.attributes.dob']!'')}"/>
    </div>
</div>
```

现在，**当我们在此页面上注册新用户时，我们也可以输入其出生日期**：

<img src="../assets/img_39.png">

为了进行验证，让我们打开管理控制台上的Users页面并查找Jane：

<img src="../assets/img_40.png">

接下来，让我们转到Jane的属性并查看DOB：

<img src="../assets/img_41.png">

很明显，这里显示的出生日期与我们在自助注册表上输入的日期相同。

## 3. 嵌入式服务器

现在让我们看看如何为[嵌入]()在Spring Boot应用程序中的Keycloak服务器添加自定义属性以进行自注册。

与独立服务器的第一步相同，我们需要在开始时启用用户注册。

我们可以通过在我们的Realm定义文件[tuyucheng-realm.json]()中将registrationAllowed设置为true来做到这一点：

```json
"registrationAllowed" : true,
```

之后，**我们需要将出生日期添加到register.ftl中，方法与之前完全相同**。

接下来，让我们将此文件复制到我们的src/main/resources/themes/custom/login目录。

现在启动服务器，我们的[登录页面](http://localhost:8083/realms/baeldung/protocol/openid-connect/auth?response_type=code&client_id=jwtClient&scope=openid&redirect_uri=http://localhost:8084/)带有注册链接，这是带有我们的自定义字段Date of birth的自助注册页面：

<img src="../assets/img_42.png">

请务必记住，**通过嵌入式服务器的自注册页面添加的用户是暂时的**。

由于我们没有将此用户添加到预配置文件中，因此它在服务器重启时将不可用。然而，这在开发阶段会派上用场，此时我们只检查设计和功能。

为了进行测试，在重新启动服务器之前，我们可以验证用户是否从[管理控制台](http://localhost:8083/admin/master/console/)添加了DOB作为自定义属性，我们也可以尝试使用新用户的凭据登录。

## 4. 总结

在本教程中，我们学习了如何在Keycloak中启用用户自助注册，并看到了如何在注册为新用户时添加自定义属性。

与往常一样，源代码可在GitHub上获得，对于独立服务器，它在[教程GitHub上]()，对于嵌入式实例，它在[OAuth GitHub上]()。