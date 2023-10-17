---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[在Docker](https://www.baeldung.com/ops/docker-guide)中保护密码是保护[容器化](https://www.baeldung.com/tag/docker-container)应用程序安全性的一个重要方面。外部服务和数据库通常需要密码验证。缺乏适当的保护可能会危及数据安全。

在本教程中，我们将解释在Docker中保护密码的不同方法。

## 2.密码安全的重要性

[密码](https://www.baeldung.com/cs/security-credential-stuffing-password-spraying)是验证外部服务或数据库的常用方法。云服务、数据库和其他第三方服务不断需要身份验证。但是，如果这些密码没有得到妥善保护，它们可能会被未经授权的用户泄露。因此，敏感数据可能会暴露给未经授权的个人。

容器化环境通常持续很短的时间并且是短暂的，这使得保护密码变得更加重要。

## 3.使用环境变量

将密码传递给容器的最简单方法是使用ENV变量。运行容器时，可以使用-e标志在运行时设置[环境变量。](https://www.baeldung.com/ops/docker-container-environment-variables)为了说明，我们可以使用以下命令将密码作为ENV变量传递：

```shell
$ docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql
```

在此mysql镜像中，密码是从环境变量MYSQL_ROOT_PASSWORD中检索的。为了设置root帐户的密码，我们将env变量传递给[docker run](https://www.baeldung.com/ops/docker-run-multiple-commands)命令。 

环境变量不存储在镜像本身中，因此它们不会意外提交到源代码仓库。我们不需要修改镜像来更改ENV。环境变量在多租户环境中是不可靠的，其中多个容器共享一个主机和环境。容器内运行的任何[进程都可以访问它。](https://www.baeldung.com/linux/linux-processes-guide)所以，我们不应该将它用于敏感数据存储。

## 4. 使用秘密管理系统

[我们可以使用像Vault](https://www.baeldung.com/vault)这样的秘密管理系统来安全地管理和存储密码。这些系统通常提供用于存储和检索机密的安全 API。此外，我们可以将它与我们的容器化环境集成，以在运行时向容器提供这些秘密。

### 4.1. 使用保险库

我们可以将密码作为秘密存储在保险库中，然后在容器启动时使用保险库的 API 检索密码。在我们的应用程序中，我们可以使用 vault 的命令行客户端或客户端库。

让我们在Docker环境中运行 vault 来存储密码并在另一个Docker容器中检索它：

```shell
$ docker run -itd --cap-add=IPC_LOCK -e 'VAULT_DEV_ROOT_TOKEN_ID=myroot' -e 'VAULT_DEV_LISTEN_ADDRESS=0.0.0.0:8200'
  -p 8200:8200 --name vault vault
```

在上面的命令中，VAULT_DEV_ROOT_TOKEN_ID设置为myroot，VAULT_DEV_LISTEN_ADDRESS设置为0.0.0.0:8200 ，因此所有可用的网络接口都将在端口8200上侦听。docker run命令的-p选项在主机的端口8200上向外部公开保管库服务器的端口8200。让我们来看看如何在保险库中创建秘密密码：

```shell
$ export VAULT_ADDR='http://127.0.0.1:8200'
```

上面的命令告诉 vault 命令行工具与运行在127.0.0.1端口8200上的服务器进行通信。让我们登录到保险库服务器并创建一个密码：

```shell
$ vault login myroot
$ vault kv put secret/test password=mysecretpasswordtest

```

[Vault login尝试使用令牌](https://developer.hashicorp.com/vault/docs/commands/login)myroot登录到服务器。此令牌处理用户的身份验证和授权。vault [kv put](https://developer.hashicorp.com/vault/docs/commands/kv/put)命令使用键值对在路径secret/test中创建密钥。这允许我们在服务器上存储敏感信息并在以后检索它。

### 4.2. 找回秘密

让我们运行另一个容器，从上面的保管库服务器检索密码：

```shell
$ docker run --rm --network=host -e 'VAULT_ADDR=http://127.0.0.1:8200' -e 'VAULT_TOKEN=myroot'
  vault sh -c 'apk add --update curl jq && vault login -method=token token=${VAULT_TOKEN}
  && PASSWORD=$(vault kv get -field=password secret/test) && echo ${PASSWORD}'
```

通过运行上述命令，将创建一个带有库镜像的新容器，并设置环境变量VAULT_ADDR和VAULT_TOKEN 。此外，它还连接到在端口8200上运行的本地主机的保管库服务器。之后，它向保险库服务器进行身份验证，从secret/test获取密码密钥值，然后将其打印出来。授权方只能访问提供额外安全层的秘密。但是秘密管理系统的设置和管理起来很复杂。此外，它还增加了我们应用程序的成本。

## 5. 使用DockerSecrets

[Docker secrets是](https://www.baeldung.com/ops/docker-secrets)[Docker Swarm](https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/)模式中的一项功能，它允许我们在Docker环境中安全地管理敏感信息，例如密码。使用Docker机密，我们可以将配置文件、敏感信息和命令行参数保留在镜像之外。这总体上降低了暴露的风险。

### 5.1. 创建DockerSecret

首先，我们需要初始化Dockerswarm 以使用Dockersecrets：

```shell
$ docker swarm init
```

下一步，我们将使用 echo 将密码通过管道传输到 docker secret create 中：

```shell
$ echo "testpassword" | docker secret create mysql_external_secret -
```

上面的命令创建了一个名为“mysql_external_secret”的唯一秘密，其值为“testpassword”。 使用docker-compose.yml，我们可以在Docker服务中安装MySQL服务器，环境变量MYSQL_ROOT_PASSWORD_FILE通过Docker秘密传递：

```shell
version: '3.1'
services:
  mysql:
    image: mysql
    secrets:                    # secrets block only for 'mysql' service
     - mysql_external_secret 
    environment:
      - MYSQL_ROOT_PASSWORD_FILE=/run/secrets/mysql_external_secret
secrets:                        # top level secrets block
  mysql_external_secret:
    external: true
```

secrets 块表示mysql_external_secret是我们主机上已经存在的外部机密。

### 5.2. 部署堆栈

Docker 机密在主机上创建机密并在运行时将它们传递给容器。这种机制最终降低了暴露的风险。要运行该服务，让我们部署堆栈：

```shell
$ docker stack deploy --compose-file=docker-compose.yml mysql_secret_test
```

上面的命令部署在docker-compose.yml文件中定义的堆栈并创建服务mysql_secret_test。使用这种方法，我们可以在我们的Docker环境中安全地管理敏感信息，例如密码。

## 6. 最佳实践

Docker 中的密码安全是一项至关重要的任务，需要全面规划和实施。在Docker中保护密码需要一种多层方法，包括使用环境变量、[加密](https://www.baeldung.com/linux/encrypt-decrypt-files)、Docker 机密和定期更新密码。

### 6.1. 加密敏感数据

加密是保护Docker容器中密码的一个重要方面。我们应该在将数据存储或传递给容器之前对其进行加密。这提供了额外的安全层，使未经授权的访问更加困难。AES、RSA 和 Blowfish 是可用的加密算法。算法的选择取决于资源可用性和安全要求。

### 6.2. 敏感信息访问受限

将对敏感信息(例如密码)的访问权限限制为只有需要的人才能访问，这一点很重要。使用 RBAC，我们可以授予某些用户访问权限。我们可以只向需要它的用户授予访问权限，并在他们不再需要时撤销它。因此，未经授权的用户无法访问敏感数据，从而降低了数据泄露的可能性。

### 6.3. 定期更新密码

定期更新密码是维护容器安全的一个非常简单和有用的步骤。我们应该至少每三到六个月更新一次密码。这确保即使密码被泄露，敏感信息的风险也受到限制。此外，我们应该使用难以猜测的强密码。

## 七、总结

在本文中，我们讨论了在Docker中保护密码的几种方法，包括使用环境变量、秘密管理系统和密钥管理系统。在Docker中保护密码的最佳方法取决于我们的特定用例和要求。

环境变量可用于轻松保护少量密码。但是，如果我们需要存储许多密码，秘密或密钥管理系统可能是更好的选择。
