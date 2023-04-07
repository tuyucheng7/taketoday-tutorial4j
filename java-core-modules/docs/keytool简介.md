## **一、概述**

在这个简短的教程中，我们将介绍*[keytool](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)*命令。我们将学习如何使用*keytool*创建新证书并检查该证书的信息。

## **2.什么是\*keytool？\***

Java 在其发行版中包含*keytool*实用程序。我们用它来**管理** **密钥和证书**并将它们存储在密钥库中。keytool命令允许我们创建自签名证书并显示有关密钥库的信息*。*

在以下部分中，我们将介绍此实用程序的不同功能。

## **3. 创建自签名证书**

首先，让我们创建一个自签名证书，例如，可用于在我们的开发环境中的项目之间建立安全通信。

为了**生成证书**，我们将打开命令行提示符并使用带有*-genkeypair选项的**keytool*命令：

```shell
keytool -genkeypair -alias <alias> -keypass <keypass> -validity <validity> -storepass <storepass>复制
```

让我们详细了解这些参数中的每一个：

-   *别名*——我们证书的名称
-   *keypass——*证书的密码。我们需要此密码才能访问我们证书的私钥
-   *validity* – 我们证书的有效期时间（以天为单位）
-   *storepass* – 密钥库的密码。如果商店不存在，这将是密钥库的密码

例如，让我们生成一个名为*“cert1”*的证书，其私钥为*“pass123”*，有效期为一年。我们还将指定*“stpass123”*作为密钥库密码：

```shell
keytool -genkeypair -alias cert1 -keypass pass123 -validity 365 -storepass stpass123复制
```

执行命令后，它会询问我们需要提供的一些信息：

```shell
What is your first and last name?
  [Unknown]:  Name
What is the name of your organizational unit?
  [Unknown]:  Unit
What is the name of your organization?
  [Unknown]:  Company
What is the name of your City or Locality?
  [Unknown]:  City
What is the name of your State or Province?
  [Unknown]:  State
What is the two-letter country code for this unit?
  [Unknown]:  US
Is CN=Name, OU=Unit, O=Company, L=City, ST=State, C=US correct?
  [no]:  yes复制
```

如前所述，如果我们之前没有创建密钥库，创建此证书将自动创建它。

我们还可以执行不带参数的*-genkeypair*选项。如果我们不在命令行中提供它们并且它们是强制性的，我们将被提示输入它们。

**请注意，通常建议不要在生产环境的命令行中提供密码（ \*-keypass\*或\*-storepass\*** ） 。

## **4. 在密钥库中列出证书**

接下来，我们将学习如何查看存储在我们的密钥库中的**证书。**为此，我们将使用*-list*选项：

```shell
keytool -list -storepass <storepass> 复制
```

执行命令的输出将显示我们创建的证书：

```shell
Keystore type: JKS
Keystore provider: SUN

Your keystore contains 1 entry

cert1, 02-ago-2020, PrivateKeyEntry, 
Certificate fingerprint (SHA1): 0B:3F:98:2E:A4:F7:33:6E:C4:2E:29:72:A7:17:E0:F5:22:45:08:2F复制
```

如果我们想要获取**具体证书的信息**，我们只需要在我们的命令中包含 *-alias选项。*为了获得默认提供的更多信息，我们还将添加*-v*（详细）选项：

```shell
keytool -list -v -alias <alias> -storepass <storepass> 复制
```

这将为我们提供与请求的证书相关的所有信息：

```shell
Alias name: cert1
Creation date: 02-ago-2020
Entry type: PrivateKeyEntry
Certificate chain length: 1
Certificate[1]:
Owner: CN=Name, OU=Unit, O=Company, L=City, ST=State, C=US
Issuer: CN=Name, OU=Unit, O=Company, L=City, ST=State, C=US
Serial number: 11d34890
Valid from: Sun Aug 02 20:25:14 CEST 2020 until: Mon Aug 02 20:25:14 CEST 2021
Certificate fingerprints:
	 MD5:  16:F8:9B:DF:2C:2F:31:F0:85:9C:70:C3:56:66:59:46
	 SHA1: 0B:3F:98:2E:A4:F7:33:6E:C4:2E:29:72:A7:17:E0:F5:22:45:08:2F
	 SHA256: 8C:B0:39:9F:A4:43:E2:D1:57:4A:6A:97:E9:B1:51:38:82:0F:07:F6:9E:CE:A9:AB:2E:92:52:7A:7E:98:2D:CA
Signature algorithm name: SHA256withDSA
Subject Public Key Algorithm: 2048-bit DSA key
Version: 3

Extensions: 

#1: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: A1 3E DD 9A FB C0 9F 5D   B5 BE 2E EC E2 87 CD 45  .>.....].......E
0010: FE 0B D7 55                                        ...U
]
]复制
```

## **五、其他特点**

除了我们已经看到的功能之外，此工具还有许多[其他功能可用。](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)

例如，我们可以从密钥库中**删除我们创建的证书：**

```shell
keytool -delete -alias <alias> -storepass <storepass>复制
```

另一个例子是我们甚至可以**更改证书的别名**：

```shell
keytool -changealias -alias <alias> -destalias <new_alias> -keypass <keypass> -storepass <storepass>复制
```

最后，要获取更多关于该工具的信息，我们可以通过命令行**寻求帮助：**

```shell
keytool -help复制
```

## **六，结论**

在本快速教程中，我们了解了一些有关*keytool*实用程序的知识。我们还学习了使用此工具中包含的一些基本功能。