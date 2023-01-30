## 1. 概述

在本教程中，我们将学习 SSL 证书验证。简而言之，我们通过检查其真实性来验证公钥证书。这里的真实性是指证书中包含的所有信息都是有效的。

我们将首先解释使用非对称密码术进行加密和签名。然后，我们将证明公钥证书的必要性。这将最终引导我们了解 SSL 证书的作用和验证它们的过程。

## 2. 非对称密码学

在[非对称密码学](https://www.baeldung.com/cs/symmetric-vs-asymmetric-cryptography)中，网络用户有两个密钥：一个私钥和一个公钥。因此，用户![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)有一个公钥![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-434acee571cf5d14e8ca7196841ee184_l3.svg)，所有其他用户都知道。此外，还有一个只有 . 知道![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)的私钥。这种设置的一种可能用途是[加密和解密](https://www.baeldung.com/cs/introduction-to-cryptography)。![K_A^{-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-865d32fcfa56cee67239b3b19aca5a01_l3.svg)![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)

### 2.1. 非对称加解密

假设我们的朋友爱丽丝(也称为![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg))想![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)向她的朋友鲍勃 ( ![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)) 发送一条秘密消息。如果她通过通信通道发送消息，则监听该通道的入侵者 ( ![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)) 将能够读取它。因此，爱丽丝![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)使用![乙](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c74288aabc0e2ca280d25d92bf1a1ec2_l3.svg)自己的公钥进行加密，得到密文![c = e_{K_B}(m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e5b8d04580691eaad15a8a1a7df3e8c3_l3.svg)。现在，入侵者![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)无法读取![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)，即使他拦截了![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276a76eafbebc4494deafceec7cc4ddd_l3.svg)。因此，要解密![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-276a76eafbebc4494deafceec7cc4ddd_l3.svg)和揭示![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)，Bob 必须使用他的私钥。

以这种方式，非对称加密使任何人都可以向 Bob 发送加密消息，因为这![K_B](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e609b4c4f7bc9e7189550c1170003f66_l3.svg)是众所周知的。但是，只有 Bob 可以解密发送的消息，因为他是唯一知道的人![K_B^{-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-9f222ff8e3167817539a801f17bc6acd_l3.svg)。此外，我们可以使用公钥/私钥对进行数字签名。

### 2.2. 签名和验证

在当前情况下，我们的朋友爱丽丝想买一辆汽车。她去找汽车经销商鲍勃。爱丽丝选择了一辆车，从而写下并签署了一张支票来支付鲍勃。因此，Bob 会将支票出示给银行兑现。当然，银行需要分析支票的真伪。为此，银行员工将执行两个验证步骤：

1.  他们需要确保实际上是爱丽丝签署了支票
2.  他们需要验证支票在 Alice 签名后没有以任何方式修改

换句话说，他们需要评估支票的来源和完整性。

我们现在谈论的是纸质支票。但是，如果我们想将这个过程数字化怎么办？让我们来谈谈数字签名吧！

### 2.3. 数字签名

与手写签名的目的类似，[数字签名](https://www.baeldung.com/java-digital-signature)旨在证明数字文档的来源和完整性。数字签名方案考虑两种不同的算法：签名算法和签名验证算法。在这种情况下，爱丽丝使用签名算法对消息进行签名![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)。然后，她向![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)银行发送指示，将一定金额转入汽车经销商的账户。

除了![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)，签名算法还需要 Alice 的私钥![K_A^{-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-865d32fcfa56cee67239b3b19aca5a01_l3.svg)作为输入。有了这些输入，算法就可以生成爱丽丝的签名![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)：![text{sgn}_A(m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52e910c9e0d47cb74df2c28349aad33f_l3.svg)。因此，由于使用了私钥，我们确信没有其他人可以在 上生成相同的签名![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)。

然后爱丽丝将消息发送![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)给![text{sgn}_A(m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52e910c9e0d47cb74df2c28349aad33f_l3.svg)银行。当然，银行需要检查消息![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)是否真实。换句话说，它需要确认签名确实是爱丽丝制作的![text{sgn}_A(m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52e910c9e0d47cb74df2c28349aad33f_l3.svg)，并且没有人修改过![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)。这个过程就是签名验证算法的作用。

### 2.4. 数字签名验证

银行的验证算法收到爱丽丝的公钥![钾](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-434acee571cf5d14e8ca7196841ee184_l3.svg)、消息和消息![m^质数](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-227f44b2c93caa68c2274b71ed0711c6_l3.svg)签名。然后它给出是或否的答案：![text{sgn}_X(m)](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e661bec79a04b660e0b90a8961a48c51_l3.svg)![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg)

 ![[ text{verify}(K_A, m^prime, text{sgn}_X(m)) = left{ begin{array}{ll} text{Yes} & text{if}~ X = A ~text{and}~ m = m^prime text{No} & text{otherwise} end{array} right.  ]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3bed355663a850a69d3e7a6f7fb958e2_l3.svg)

让我们在这里澄清一些要点。首先，当银行收到消息时，它假定入侵者可能在传输过程中修改了消息。这种情况证明了为什么我们将消息写为![m^质数](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-227f44b2c93caa68c2274b71ed0711c6_l3.svg)而不是![米](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-fdc40b8ad1cdad0aab9d632215459d28_l3.svg). 其次，银行不假设谁制作了签名。这就是我们![X](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-996ff7036e644e89f8ac379fa58d0cf7_l3.svg)为签名者写作的原因。

因此，银行需要获得爱丽丝的公钥才能运行验证算法。但是银行如何找到这把钥匙呢？让我们在下一节中看到。

## 3.公钥证书

在非对称密码学中，我们需要发布公钥。我们还需要知道每个公钥的所有者。 证书将公钥的值与其所有者的身份联系起来。这样，我们就可以使用这个密钥来加密发送给密钥所有者的消息。我们还可以使用密钥来验证其所有者的签名。

### 3.1. 生成密钥

用户使用某种算法生成公钥/私钥对，例如[RSA 算法](https://www.baeldung.com/java-rsa)。 

用户将自己的私钥保密，绝不会与任何人共享。另一方面，用户需要与他们的身份一起共享他的公钥。 

例如，Alice 需要![角度A，K_A 角度](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd7297bbb419c2b90ff6a87f0f3056ce_l3.svg)在某个目录服务器上发布信息，例如使用[轻量级目录访问协议 (LDAP)](https://www.baeldung.com/java-test-ldap-connections)的服务器。

然后，当其他用户想找到爱丽丝的密钥的值时，他们可以使用爱丽丝的身份搜索这个目录![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)。因此，目录服务返回数据结构![角度A，K_A 角度](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dd7297bbb419c2b90ff6a87f0f3056ce_l3.svg)。

但是，这个系统存在一些固有的风险：如果某个入侵者![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)可以攻击该目录并用他自己的 ( ) 替换 Alice 的密钥![K_I](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ae2ea7fea5ab0e3953e25aa097fa2f39_l3.svg)怎么办？如果是这样，该目录将包含![A, K_I](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c3d649fd64f323460a5bd40db8827a0e_l3.svg).

现在，用户将向 Alice 发送由 加密的消息![K_I](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ae2ea7fea5ab0e3953e25aa097fa2f39_l3.svg)，并且![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)能够解密它们。此外，现在可以假装是爱丽丝来![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)签署文件。![K_I^{-1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5405dc180c2269741e46520f5907f569_l3.svg)

这样，任何收到这些签名的用户都将使用 验证它们(成功)![K_I](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ae2ea7fea5ab0e3953e25aa097fa2f39_l3.svg)，因此![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)将能够欺骗其他用户。

公钥证书正是在这种情况下发挥作用的！

### 3.2. 颁发公钥证书

证书颁发机构 ( ![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)) 颁发公钥证书以防止上一小节中描述的攻击。

用户![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)的证书包含信息：![langle A, K_A, text{sgn}_{text{CA}}(A, K_A)rangle](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-44839940b3b42cd7c869ce36c6b15d6d_l3.svg)。在这种情况下，搜索![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)的公钥并收到证书的用户可以验证它：

 ![[ text{verify}(K_{text{CA}}, (A, K_A), text{sgn}_{text{CA}}(A, K_A)) ]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b5ba30bffc7e10f934967e4e66faded6_l3.svg)

只有在没有人修改证书信息的情况下，验证才能成功。要理解这一点，请考虑之前的攻击。试图冒充的入侵者![一种](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-816b613a4f79d4bf9cb51396a9654120_l3.svg)可能能够将证书信息更改为![langle A, K_I, text{sgn}_{text{CA}}(A, K_A)rangle](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5de83c7201bb68d4700701f740cc304d_l3.svg).

我们注意到入侵者无法![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)在伪造的信息上生成 的签名。这是因为![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)不知道![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)的私钥。因此，验证将失败，任何收到证书的用户都会拒绝它。

现在让我们更深入地了解证书验证过程。

### 3.3. 验证公钥证书

用户通过验证证书中![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)包含的签名来验证公钥证书。然而，为了做到这一点，用户需要![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)的公钥。此密钥包含在具有以下内容的证书中：

 ![[ langle text{CA}, K_{text{CA}}, text{sgn}_{text{CA}}(text{CA}, K_{text{CA}})rangle ]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-17c0365f18daff4c1bb00f81248d5fd3_l3.svg)

换句话说，这是一个![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)为自己颁发并签署其内容的证书。因此，它被称为自签名证书或根证书。

自签名证书很容易被伪造。这是因为验证证书所需的密钥包含在证书本身中。因此，让我们假设用户通过公共渠道发送根证书。入侵者可以将其替换为：

 ![[ langle text{CA}, K_{I}, text{sgn}_{I}(text{CA}, K_{I})rangle ]](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-84f1fdf96931bd7527cb452962796cab_l3.svg)

在这种情况下，![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)替换![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)的签名。验证将成功，因为![我](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-14b16a74c9ddcc6f9be3e94b9c8d8f08_l3.svg)还用![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg)自己的密钥替换了公钥![K_I](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ae2ea7fea5ab0e3953e25aa097fa2f39_l3.svg)。这就是为什么我们从不通过公共渠道发送根证书。相反，它们预装在操作系统和网络浏览器等软件中。

### 3.4. 实际考虑

当我们为公钥证书开发系统时，有一些实际的考虑因素：

-   关于证书所有者(也称为主题，例如姓名、电子邮件、域名等)应包含哪些信息？
-   哪个实体应该扮演认证机构的角色？
-   向用户颁发证书的具体步骤是什么，包括确保他们提供的信息是正确的？
-   证书的确切格式是什么？

当我们构建证书系统(也称为公钥基础设施 - PKI)时，我们必须解决这些问题。

## 4.SSL证书

安全套接字层 (SSL) 协议使用证书在客户端和服务器之间创建安全链接。例如，Web 服务器可以使用 SSL 来设置密钥以加密其自身与客户端之间的流量。

### 4.1. SSL协议

SSL 协议以握手开始，客户端和服务器在握手过程中建立加密密钥。对称加密算法使用此密钥来加密未来的流量。在握手期间，服务器将其公钥证书发送给客户端。

反过来，服务器证书包含域名和服务器公钥等信息，并由![text{CA}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-1798ec4bedabf0bad9979e8a8eb9a1c7_l3.svg). 公钥用于向客户端验证服务器并建立对称加密密钥。

### 4.2. 验证 SSL 证书

当客户端收到服务器的证书时，它会读取颁发此证书的 CA 的名称。然后，客户端在其存储的根证书中搜索此 CA 的公钥证书。最后，客户端使用这个公钥来验证服务器的证书。

下图显示了 Firefox 浏览器中存储的一些根证书(CA 的证书)：

![img](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/CAs--300x233.png)![img](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/CAs-jpeg.jpeg)

## 5.总结

在本文中，我们解释了 SSL 证书的作用以及它们的颁发和验证方式。事实上，互联网协议广泛使用它们来保护与服务器的连接。SSL 证书使我们能够使用交换敏感数据的服务，例如在网上银行或电子商务环境中。当然，如果没有适当的证书验证，这些应用程序是不可能的。