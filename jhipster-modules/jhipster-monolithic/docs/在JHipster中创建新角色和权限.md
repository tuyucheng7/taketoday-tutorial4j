## 1. 概述

[JHipster](https://www.baeldung.com/jhipster)带有两个默认角色——USER 和 ADMIN——但有时我们需要添加自己的角色。

在本教程中，我们将创建一个名为 MANAGER 的新角色，我们可以使用它来为用户提供额外的权限。

请注意，JHipster 使用的术语 authorities与roles在某种程度上可以互换。无论哪种方式，我们本质上都是同一件事。

## 2. 代码变更

创建新角色的第一步是更新类AuthoritiesConstants。这个文件是在我们创建一个新的 JHipster 应用程序时自动生成的，它包含应用程序中所有角色和权限的常量。

要创建我们的新 MANAGER 角色，我们只需在该文件中添加一个新常量：

```java
public static final String MANAGER = "ROLE_MANAGER";
```

## 3. 架构更改

下一步是在我们的数据存储中定义新角色。

JHipster 支持各种持久性数据存储，并创建一个初始设置任务，用用户和权限填充数据存储。

要将新角色添加到数据库设置中，我们必须编辑InitialSetupMigration.java文件。它已经有一个名为addAuthorities的方法，我们只需将我们的新角色添加到现有代码中：

```java
public void addAuthorities(MongoTemplate mongoTemplate) {
    // Add these lines after the existing, auto-generated code
    Authority managerAuthority = new Authority();
    managerAuthority.setName(AuthoritiesConstants.MANAGER);
    mongoTemplate.save(managerAuthority);
}
```

此示例使用 MongoDB，但步骤与 JHipster 支持的其他持久存储非常相似。

请注意，某些数据存储(例如 H2)仅依赖名为authorities.csv 的文件， 因此没有任何需要更新的生成代码。

## 4. 使用我们的新角色

现在我们已经定义了一个新角色，让我们看看如何在我们的代码中使用它。

### 4.1. Java代码

在后端，有两种主要方法可以检查用户是否有权执行操作。

首先，如果我们想限制对特定 API 的访问，我们可以修改SecurityConfiguration ：

```java
public void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
            .antMatchers("/management/").hasAuthority(AuthoritiesConstants.MANAGER);
}
```

其次，我们可以在应用程序的任何地方使用SecurityUtils来检查用户是否在某个角色中：

```java
if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.MANAGER)) {
    // perform some logic that is applicable to manager role
}
```

### 4.2. 前端

JHipster 提供了两种方式来检查前端的角色。请注意，这些示例使用的是 Angular，但 React 也存在类似的结构。

首先，模板中的任何元素都可以使用jhiHasAnyAuthority指令。它接受单个字符串或字符串数组：

```html
<div jhiHasAnyAuthority="'ROLE_MANAGER'">
    <!-- manager related code here -->
</div>
```

其次，Principal类可以检查用户是否具有特定角色：

```javascript
isManager() {
    return this.principal.identity()
      .then(account => this.principal.hasAnyAuthority(['ROLE_MANAGER']));
}
```

## 5.总结

在本文中，我们看到了在 JHipster 中创建新角色和权限是多么简单。虽然默认的 USER 和 ADMIN 角色是大多数应用程序的良好起点，但其他角色提供了更大的灵活性。

通过附加角色，我们可以更好地控制哪些用户可以访问 API 以及他们可以在前端看到哪些数据。
