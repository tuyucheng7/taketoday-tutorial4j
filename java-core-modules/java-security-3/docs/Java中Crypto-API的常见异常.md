## 一、简介

[*Cipher*](https://www.baeldung.com/java-cipher-class)对象是一个重要的Java 类，它帮助我们提供加密和解密功能。

在本文中，我们将了解在使用它加密和解密文本时可能发生的一些常见异常。

## 2. *NoSuchAlgorithmException*：找不到任何支持 X 的提供程序

如果我们运行以下代码以使用编造的算法获取*Cipher的实例：*

```java
Cipher.getInstance("ABC");复制
```

我们将看到堆栈跟踪开始：

```java
java.security.NoSuchAlgorithmException: Cannot find any provider supporting ABC
    at javax.crypto.Cipher.getInstance(Cipher.java:543)复制
```

这里到底发生了什么？

那么，要使用*Cipher.getInstance*，**我们需要将算法转换作为\*字符串传递，\*并且这必须是[文档](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher)中列出的允许值。**如果不是，我们将得到一个*NoSuchAlgorithmException*。

**如果我们检查了文档并且仍然看到这个，我们最好确保检查转换是否有错误。**

我们还可以在转换中指定算法模式和填充。

让我们确保这些字段的值也与给定的文档匹配。否则，我们会看到一个异常：

```java
Cipher.getInstance("AES/ABC"); // invalid, causes exception

Cipher.getInstance("AES/CBC/ABC"); // invalid, causes exception

Cipher.getInstance("AES/CBC/PKCS5Padding"); // valid, no exception复制
```

请记住，如果我们不指定这些额外字段，则会使用默认值。

算法模式的默认值为[ECB](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Electronic_codebook_(ECB))，填充的默认值为*“NoPadding”。*

由于 ECB 被认为很弱，我们将要指定一种模式以确保我们不会最终使用它。

总而言之，在解决*NoSuchAlgorithmException*时，我们将要检查我们选择的转换的每个部分是否存在于文档的允许列表中，注意检查拼写中的任何拼写错误。

## 3. *IllegalBlockSizeException*：输入长度不是 X 字节的倍数

我们可能会看到此异常的原因有几个。

首先，让我们看看在尝试解密时抛出的异常，然后在尝试加密时查看它。

### 3.1. 解密期间*出现 IllegalBlockSizeException*

下面写一个很简单的解密方法：

```java
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
cipher.init(Cipher.DECRYPT_MODE, key);
		
return cipher.doFinal(cipherTextBytes);复制
```

此代码的行为将根据传递给我们的方法的密文而改变，这可能是我们无法控制的。

有时，我们可能会看到*IllegalBlockSizeException：*

```java
javax.crypto.IllegalBlockSizeException: Input length not multiple of 16 bytes
    at com.sun.crypto.provider.CipherCore.finalNoPadding(CipherCore.java:1109)
    at com.sun.crypto.provider.CipherCore.fillOutputBuffer(CipherCore.java:1053)
    at com.sun.crypto.provider.CipherCore.doFinal(CipherCore.java:853)
    at com.sun.crypto.provider.AESCipher.engineDoFinal(AESCipher.java:446)
    at javax.crypto.Cipher.doFinal(Cipher.java:2168)复制
```

那么“块大小”到底是什么意思，又是什么让它“非法”呢？

要理解这一点，让我们记住[AES是](https://www.baeldung.com/java-aes-encryption-decryption)[Block Cipher](https://en.wikipedia.org/wiki/Block_cipher)的一个例子。

块密码通过采用称为块的固定长度的位组来工作。

要找出我们算法的一个块中有多少字节，我们可以使用：

```java
Cipher.getInstance("AES/ECB/PKCS5Padding").getBlockSize();复制
```

由此可见，**AES 使用的是 16 字节的块。**

这意味着它将使用一个 16 字节的块，执行相关的算法步骤，然后移动到下一个 16 字节的块。

简单地说，**非法块是不包含正确字节数的块。**

通常，当文本长度不是 16 字节的倍数时，这会发生在最后一个块上。

**这通常意味着要解密的文本一开始就没有正确加密，因此无法解密。**

请记住，我们不控制提供给我们的代码以进行解密的输入，因此我们必须准备好处理此异常。

因此，像*cipher.doFinal*这样的方法会抛出*IllegalBlockSizeException*来强制我们处理这种情况，要么[抛出](https://www.baeldung.com/java-exceptions#1throws)它，要么在*[try-catch](https://www.baeldung.com/java-exceptions#2-try-catch)* 语句中*。否则*，代码将无法编译。

但是，请记住，大约每 16 次一次，一些错误的密文恰好是正确的长度，以避免 AES 的这种异常。

在这种情况下，我们很可能会遇到本文中提到的其他异常之一。

### 3.2. 加密期间*出现 IllegalBlockSizeException*

现在让我们在尝试加密文本“ *https://www.baeldung.com/* ”时看一下这个异常：

```java
String plainText = "https://www.baeldung.com/";
byte[] plainTextBytes = plainText.getBytes();
		
Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
cipher.init(Cipher.ENCRYPT_MODE, key);
		
return cipher.doFinal(plainTextBytes);复制
```

正如我们在上面看到的，要使 AES 算法起作用，字节数必须是 16 的倍数，而我们的文本不是。因此，运行此代码会出现与上述相同的异常。

那么我们是否只能使用 AES 来加密具有 16、32、48……字节的文本？

如果我们想加密一些没有正确字节数的东西怎么办？

嗯，这是**我们需要填充数据的地方。**

**填充数据只是意味着我们要在文本的开头、中间或结尾添加额外的字节，**从而确保数据现在具有正确的字节数。

与算法名称和模式一样，我们可以使用一组允许的填充操作列表。

幸运的是，Java 为我们处理了这件事，所以我们不打算在这里详细介绍它是如何工作的。

我们要做的就是**在我们的*****Cipher\*****实例上设置一个填充操作，比如[PKCS #5](https://www.rfc-editor.org/rfc/rfc8018) ，而不是指定“NoPadding”：**

```java
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");复制
```

当然，解密文本的代码也必须使用相同的填充操作。

### 3.3. 其他故障排除技巧

如果我们开始遇到*NoSuchAlgorithmException* 或*NoSuchPaddingException，* 我们将需要检查[Java 文档](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher)以确保我们使用的是有效的填充 - 并且我们的拼写没有拼写错误。

如果我们已经这样做了，那么可能值得检查我们正在查看的文档是否与我们正在使用的 Java 版本相匹配，因为允许的填充操作可以在版本之间更改。本文中提供的链接适用于 Java 8。

## 4. *BadPaddingException*：给定的最终块未正确填充

如果在处理填充时出现问题，代码将抛出*BadPaddingException*，表明这是我们使用的填充的问题。

但是，实际上可能有几个不同的问题导致我们看到此异常。

### 4.1. 错误填充导致*的 BadPaddingException* 

假设我们的文本“ *https://www.baeldung.com/* ”是使用[ISO 10126](https://www.iso.org/standard/18114.html)填充加密的：

```java
Cipher cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");
cipher.init(Cipher.ENCRYPT_MODE, key);
byte[] cipherTextBytes = cipher.doFinal(plainTextBytes);复制
```

然后，如果我们尝试使用不同的填充对其进行解密，比如 PKCS #5：

```java
cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
cipher.init(Cipher.DECRYPT_MODE, encryptionKey);

return cipher.doFinal(cipherTextBytes);复制
```

我们的代码会抛出异常：

```java
javax.crypto.BadPaddingException: Given final block not properly padded. Such issues can arise if a bad key is used during decryption.
  at com.sun.crypto.provider.CipherCore.unpad(CipherCore.java:975)
  at com.sun.crypto.provider.CipherCore.fillOutputBuffer(CipherCore.java:1056)
  at com.sun.crypto.provider.CipherCore.doFinal(CipherCore.java:853)
  at com.sun.crypto.provider.AESCipher.engineDoFinal(AESCipher.java:446)
  at javax.crypto.Cipher.doFinal(Cipher.java:2168)复制
```

然而，当我们看到这个异常时，填充往往不是根本原因。

上面的异常中的一行暗示了这一点，“如果在解密过程中使用了错误的密钥，就会出现此类问题。”

那么让我们看看还有什么可以导致*BadPaddingException。*

### 4.2. 密钥错误导致*的 BadPaddingException* 

如堆栈跟踪所示，当我们未使用正确的加密密钥进行解密时，我们可能会看到此异常：

```java
SecretKey encryptionKey = CryptoUtils.getKeyForText("BaeldungIsASuperCoolSite");
SecretKey differentKey = CryptoUtils.getKeyForText("ThisGivesUsAnAlternative");

Cipher cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");

cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
byte[] cipherTextBytes = cipher.doFinal(plainTextBytes);

cipher.init(Cipher.DECRYPT_MODE, differentKey);

return cipher.doFinal(cipherTextBytes);复制
```

上面的代码抛出*BadPaddingException*而不是*InvalidKeyException*，因为这是代码遇到问题并且无法继续进行的地方。

这可能是导致此异常的最常见原因。

**如果我们看到这个异常，那么我们必须确保我们使用的是正确的密钥。**

这意味着我们必须使用相同的密钥进行加密和解密。

### 4.3. 算法错误导致*的BadPaddingException* 

鉴于以上情况，下一个应该是显而易见的，但始终值得检查。

如果我们尝试使用与数据加密方式不同的算法或算法模式进行解密，我们可能会看到类似的症状：

```java
Cipher cipher = Cipher.getInstance("AES/ECB/ISO10126Padding");
cipher.init(Cipher.ENCRYPT_MODE, key);
byte[] cipherTextBytes = cipher.doFinal(plainTextBytes);

cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
cipher.init(Cipher.DECRYPT_MODE, key);

return cipher.doFinal(cipherTextBytes);复制
```

在上面的示例中，数据使用[CBC](https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation#Cipher_block_chaining_(CBC))模式加密， 但使用 ECB 模式解密，这是行不通的（在大多数情况下）。

通常，我们解决此异常的方法是**验证我们的解密机制的每个组件是否与数据的加密方式相匹配。**

## 5.*无效密钥异常*

**InvalidKeyException通常表示我们错误地设置了\*Cipher\**对象\*。**

让我们来看看最常见的原因。

### 5.1. *InvalidKeyException*：缺少参数

我们使用的一些算法需要[初始化向量](https://en.wikipedia.org/wiki/Initialization_vector)(IV)。

IV 防止重复加密文本，因此某些加密模式（如 CBC）需要它。

让我们尝试初始化一个没有 IV 集的*Cipher实例：*

```java
Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
cipher.doFinal(cipherTextBytes);复制
```

如果我们运行上面的代码，那么我们将看到以下堆栈跟踪：

```java
java.security.InvalidKeyException: Parameters missing
  at com.sun.crypto.provider.CipherCore.init(CipherCore.java:469)
  at com.sun.crypto.provider.AESCipher.engineInit(AESCipher.java:313)
  at javax.crypto.Cipher.implInit(Cipher.java:805)
  at javax.crypto.Cipher.chooseProvider(Cipher.java:867)
  at javax.crypto.Cipher.init(Cipher.java:1252)
  at javax.crypto.Cipher.init(Cipher.java:1189)复制
```

幸运的是，这个很容易修复，因为**我们只需要用IV初始化我们的\*Cipher ：\***

```java
byte[] ivBytes = new byte[]{'B', 'a', 'e', 'l', 'd', 'u', 'n', 'g', 'I', 's', 'G', 'r', 'e', 'a', 't', '!'};
IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
cipher.init(Cipher.DECRYPT_MODE, encryptionKey, ivParameterSpec);
byte[] decryptedBytes = cipher.doFinal(cipherTextBytes);复制
```

请注意，**给定的 IV 必须与用于加密文本的 IV 相同。**

关于我们的 IV 的最后一个注意事项是它必须有一定的长度。

**如果我们使用 CBC，我们的 IV 必须恰好 16 个字节长。**

如果我们尝试使用不同的字节数，我们将得到一个非常清晰的*InvalidAlgorithmParameterException：*

```java
java.security.InvalidAlgorithmParameterException: Wrong IV length: must be 16 bytes long
  at com.sun.crypto.provider.CipherCore.init(CipherCore.java:525)
  at com.sun.crypto.provider.AESCipher.engineInit(AESCipher.java:346)
  at javax.crypto.Cipher.implInit(Cipher.java:809)
  at javax.crypto.Cipher.chooseProvider(Cipher.java:867)复制
```

修复只是为了确保我们的 IV 是 16 字节长。

### 5.2. *InvalidKeyException*：无效的 AES 密钥长度：X 字节

我们将很快介绍这个，因为它与上述情况非常相似。

如果我们尝试使用长度不正确的密钥，那么我们将看到一个简单的异常：

```java
java.security.InvalidKeyException: Invalid AES key length: X bytes
  at com.sun.crypto.provider.AESCrypt.init(AESCrypt.java:87)
  at com.sun.crypto.provider.CipherBlockChaining.init(CipherBlockChaining.java:93)
  at com.sun.crypto.provider.CipherCore.init(CipherCore.java:591)复制
```

我们的密钥也必须是 16 个字节。

这是因为 Java 默认仅支持 128 位（16 字节）加密。

## 六，结论

在本文中，我们看到了在加密和解密文本时可能发生的各种异常。

特别是，我们看到异常可能提到一件事的情况，例如填充，但根本原因实际上是另一件事，例如无效的密钥。