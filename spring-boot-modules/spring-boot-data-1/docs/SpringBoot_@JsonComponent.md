## 1. 概述

这篇文章重点介绍如何在Spring Boot中使用@JsonComponent注解。

该注解允许我们将带注解的类公开为Jackson 序列化器和/或反序列化器，而无需手动将其添加到ObjectMapper。

这是核心Spring Boot模块的一部分，因此在普通的Spring Boot应用程序中不需要额外的依赖项。

## 2. 序列化

