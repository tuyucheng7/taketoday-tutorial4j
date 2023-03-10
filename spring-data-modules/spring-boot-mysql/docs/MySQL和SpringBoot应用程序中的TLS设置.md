## 1. 概述

MySQL服务器和客户端之间的未加密连接可以暴露通过网络传输的数据。对于生产就绪的应用程序，我们应该通过TLS(传输层安全)协议将所有通信转移到安全连接。

在本教程中，我们将学习如何在MySQL服务器上启用安全连接。此外，我们将配置Spring Boot应用程序以使用此安全连接。

## 2. 为什么要在MySQL上使用TLS？

首先，让我们了解一些TLS的基础知识。

[TLS](https://www.baeldung.com/spring-tls-setup)协议使用加密算法来确保通过网络接收的数据是可信的，不会被篡改或检查。它具有检测数据更改、丢失或重放攻击的机制。**TLS还包含使用X.509标准提供身份验证的算法**。

加密连接增加了一层安全性，并使数据无法通过网络流量读取。

在MySQL服务器和客户端之间配置安全连接可以实现更好的身份验证、数据完整性和可信度。此外，MySQL服务器可以对客户端的身份执行额外的检查。

但是，由于加密，这种安全连接会带来性能损失。性能成本的严重程度取决于各种因素，如查询大小、数据负载、服务器硬件、网络带宽和其他因素。

## 3. 在MySQL服务器上配置TLS连接

MySQL服务器在每个连接的基础上执行加密，对于给定用户，这可以是强制性的或可选的。MySQL通过安装的[OpenSSL](https://www.baeldung.com/openssl-self-signed-cert)库在运行时支持SSL加密相关操作。

**我们可以使用JDBC Driver [Connector/J](https://github.com/mysql/mysql-connector-j)在初始握手后加密客户端和服务器之间的数据**。

MySQL服务器v8.0.28或更高版本仅支持TLSv1.2和TLSv1.3。它不再支持早期版本的TLS(v1和v1.1)。

可以**使用由受信任的根证书颁发机构签名的证书或自签名证书**来启用服务器身份验证。此外，**为MySQL构建我们自己的根CA文件是常见的做法**，即使在生产环境中也是如此。

此外，服务器可以验证和验证客户端的SSL证书，并对客户端的身份执行额外的检查。

### 3.1 使用TLS证书配置MySQL服务器

我们将使用属性require_secure_transport和默认生成的证书在MySQL服务器上启用安全传输。

让我们通过在[docker-compose.yml](https://www.baeldung.com/ops/docker-compose)中实现设置来快速引导MySQL服务器：

```yaml
version: '3.8'

services:
    mysql-service:
        image: "mysql/mysql-server:8.0.30"
        container_name: mysql-db
        command: [ "mysqld",
                   "--require_secure_transport=ON",
                   "--default_authentication_plugin=mysql_native_password",
                   "--general_log=ON" ]
        ports:
            - "3306:3306"
        volumes:
            - type: bind
              source: ./data
              target: /var/lib/mysql
        restart: always
        environment:
            MYSQL_ROOT_HOST: "%"
            MYSQL_ROOT_PASSWORD: "Password2022"
            MYSQL_DATABASE: test_db
```

我们应该注意到，**上面的MySQL服务器使用位于路径/var/lib/mysql中的默认证书**。

或者，我们可以通过在docker-compose.yml中包含一些mysqld配置来覆盖默认证书：

```shell
command: [ "mysqld",
  "--require_secure_transport=ON",
  "--ssl-ca=/etc/certs/ca.pem",
  "--ssl-cert=/etc/certs/server-cert.pem",
  "--ssl-key=/etc/certs/server-key.pem",
  ....]
```

现在，让我们使用docker-compose命令启动mysql-service：

```shell
$ docker-compose -p mysql-server up
```

### 3.2 使用X509创建用户

或者，我们可以使用X.509标准为MySQL服务器配置客户端标识。使用X509，需要有效的客户端证书。这会启用双向TLS或mTLS。

让我们使用X509创建一个用户并授予对test_db数据库的权限：

```shell
mysql> CREATE USER 'test_user'@'%' IDENTIFIED BY 'Password2022' require X509;
mysql> GRANT ALL PRIVILEGES ON test_db.* TO 'test_user'@'%';
```

我们可以在没有任何用户证书标识的情况下建立TLS连接：

```shell
mysql> CREATE USER 'test_user'@'%' IDENTIFIED BY 'Password2022' require SSL;
```

我们应该注意，**如果使用SSL，则客户端需要提供信任库**。

## 4. 在Spring Boot应用程序上配置TLS

Spring Boot应用程序可以通过使用一些属性设置JDBC URL来配置JDBC连接上的TLS。

有多种方法可以配置Spring Boot应用程序以将TLS与[MySQL](https://www.baeldung.com/java-connect-mysql)一起使用。

在此之前，我们需要将信任库和客户端证书转换为JKS格式。

### 4.1 将PEM文件转换为JKS格式

让我们将MySQL服务器生成的ca.pem和client-cert.pem文件转换为[JKS](https://www.baeldung.com/convert-pem-to-jks)格式：

```shell
keytool -importcert -alias MySQLCACert.jks -file ./data/ca.pem \
    -keystore ./certs/truststore.jks -storepass mypassword
openssl pkcs12 -export -in ./data/client-cert.pem -inkey ./data/client-key.pem \
    -out ./certs/certificate.p12 -name "certificate"
keytool -importkeystore -srckeystore ./certs/certificate.p12 -srcstoretype pkcs12 -destkeystore ./certs/client-cert.jks
```

我们应该注意到，**从Java 9开始，默认的密钥库格式是PKCS12**。

### 4.2 使用application.yml配置

可以通过将sslMode设置为PREFERRED、REQUIRED、VERIFY_CA或VERIFY_IDENTITY来启用TLS。

PREFERRED模式要么使用安全连接(如果服务器支持)，要么退回到未加密的连接。

在REQUIRED模式下，客户端只能使用加密连接。与REQUIRED一样，VERIFY_CA模式使用安全连接，但另外根据配置的证书颁发机构(CA)证书验证服务器证书。

VERIFY_IDENTITY模式对主机名进行一项额外的检查以及证书验证。

此外，还需要将一些Connector/J属性添加到JDBC URL，例如trustCertufucateKeyStoreUrl、trustCertificateKeyStorePassword、clientCertificateKeyStoreUrl和clientCertificateKeyStorePassword。

让我们在application.yml中配置JDBC URL，并将sslMode设置为VERIFY_CA：

```yaml
spring:
    profiles: "dev2"
    datasource:
        url: >-
            jdbc:mysql://localhost:3306/test_db?
            sslMode=VERIFY_CA&
            trustCertificateKeyStoreUrl=file:/<project-path>/mysql-server/certs/truststore.jks&
            trustCertificateKeyStorePassword=mypassword&
            clientCertificateKeyStoreUrl=file:/<project-path>/mysql-server/certs/client-cert.jks&
            clientCertificateKeyStorePassword=mypassword
        username: test_user
        password: Password2022
```

我们应该注意，**与VERIFY_CA等效的已弃用属性是useSSL=true和verifyServerCertificate=true的组合**。

如果未提供信任证书文件，我们将收到一个错误消息：

```shell
Caused by: java.security.cert.CertPathValidatorException: Path does not chain with any of the trust anchors
	at java.base/sun.security.provider.certpath.PKIXCertPathValidator.validate(PKIXCertPathValidator.java:157) ~[na:na]
	at java.base/sun.security.provider.certpath.PKIXCertPathValidator.engineValidate(PKIXCertPathValidator.java:83) ~[na:na]
	at java.base/java.security.cert.CertPathValidator.validate(CertPathValidator.java:309) ~[na:na]
	at com.mysql.cj.protocol.ExportControlled$X509TrustManagerWrapper.checkServerTrusted(ExportControlled.java:402) ~[mysql-connector-java-8.0.29.jar:8.0.29]
```

如果缺少客户端证书，我们会得到一个不同的错误：

```shell
Caused by: java.sql.SQLException: Access denied for user 'test_user'@'172.20.0.1'
```

### 4.3 使用环境变量配置TLS

或者，我们可以将上述配置设置为环境变量，并将与SSL相关的配置作为JVM参数。

让我们将TLS和Spring相关的配置添加为环境变量：

```shell
export TRUSTSTORE=./mysql-server/certs/truststore.jks
export TRUSTSTORE_PASSWORD=mypassword
export KEYSTORE=./mysql-server/certs/client-cert.jks
export KEYSTORE_PASSWORD=mypassword
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/test_db?sslMode=VERIFY_CA
export SPRING_DATASOURCE_USERNAME=test_user
export SPRING_DATASOURCE_PASSWORD=Password2022
```

然后，让我们使用上述SSL配置运行应用程序：

```shell
$java -Djavax.net.ssl.keyStore=$KEYSTORE \
 -Djavax.net.ssl.keyStorePassword=$KEYSTORE_PASSWORD \
 -Djavax.net.ssl.trustStore=$TRUSTSTORE \
 -Djavax.net.ssl.trustStorePassword=$TRUSTSTORE_PASSWORD \
 -jar ./target/spring-boot-mysql-1.0.0.jar
```

## 5. 验证TLS连接

现在让我们使用上述任何方法运行应用程序并验证TLS连接。

可以使用MySQL服务器通用日志或通过查询进程和系统管理表来验证TLS连接。

让我们使用默认路径/var/lib/mysql/中的日志文件验证连接：

```shell
$ cat /var/lib/mysql/7f44397082d7.log
2022-09-17T13:58:25.887830Z        19 Connect   test_user@172.22.0.1 on test_db using SSL/TLS
```

或者，让我们验证test_user使用的连接：

```shell
mysql> SELECT process.thd_id,user,db,ssl_version,ssl_cipher FROM sys.processlist process, sys.session_ssl_status session 
where process.user='test_user@172.20.0.1'and process.thd_id=session.thread_id;

+--------+----------------------+---------+-------------+------------------------+
| thd_id | user                 | db      | ssl_version | ssl_cipher             |
+--------+----------------------+---------+-------------+------------------------+
|    167 | test_user@172.20.0.1 | test_db | TLSv1.3     | TLS_AES_256_GCM_SHA384 |
|    168 | test_user@172.20.0.1 | test_db | TLSv1.3     | TLS_AES_256_GCM_SHA384 |
|    169 | test_user@172.20.0.1 | test_db | TLSv1.3     | TLS_AES_256_GCM_SHA384 |
```

## 6. 总结

在本文中，我们了解了与MySQL的TLS连接如何确保网络上的数据安全。此外，我们还了解了如何在Spring Boot应用程序中配置MySQL服务器上的TLS连接。