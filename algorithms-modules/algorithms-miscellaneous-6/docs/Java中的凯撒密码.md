## 1. 概述

在本教程中，我们将探索凯撒密码，这是一种加密方法，可以将消息的字母移位以产生另一个可读性较差的字母。

首先，我们将通过加密方法，看看如何在Java中实现它。

然后，我们将了解如何破译加密消息，前提是我们知道用于加密它的偏移量。

最后，我们将学习如何破解这样的密码，从而在不知道使用的偏移量的情况下从加密的消息中检索原始消息。

## 2.凯撒密码

### 2.1. 解释

首先，让我们定义什么是密码。[密码是一种加密消息的方法](http://www.cs.trincoll.edu/~crypto/historical/substitution.html)，旨在降低消息的可读性。至于凯撒密码，它是一种替换密码，通过将消息的字母移动给定的偏移量来转换消息。

假设我们要将字母表移动 3，然后字母A将转换为字母D，B转换为E，C转换为F，等等。

这是偏移量为 3 的原始字母和转换字母之间的完整匹配：

```plaintext
A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
D E F G H I J K L M N O P Q R S T U V W X Y Z A B C
```

正如我们所看到的，一旦转换超出了字母Z，我们就会回到字母表的开头，这样X、Y和Z就分别转换为A、B和C。

因此，如果我们选择一个大于或等于 26 的偏移量，我们将在整个字母表上循环至少一次。假设我们将一条消息移位 28，这实际上意味着我们将其移位 2。确实，在移位 26 之后，所有字母都与自身相匹配。

实际上，我们可以通过对它执行模 26 操作将任何偏移量转换为更简单的偏移量：

```java
offset = offset % 26
```

### 2.2. Java中的算法

现在，让我们看看如何在Java中实现凯撒密码。

首先，让我们创建一个类 CaesarCipher ，它将包含一个 以消息和偏移量作为参数的cipher()方法：

```java
public class CaesarCipher {
    String cipher(String message, int offset) {}
}
```

该方法将使用凯撒密码加密消息。

我们在这里假设偏移量是正的并且消息只包含小写字母和空格。然后，我们想要的是将所有字母字符移动给定的偏移量：

```java
StringBuilder result = new StringBuilder();
for (char character : message.toCharArray()) {
    if (character != ' ') {
        int originalAlphabetPosition = character - 'a';
        int newAlphabetPosition = (originalAlphabetPosition + offset) % 26;
        char newCharacter = (char) ('a' + newAlphabetPosition);
        result.append(newCharacter);
    } else {
        result.append(character);
    }
}
return result;
```

正如我们所看到的，我们依靠字母表字母的 ASCII 码来实现我们的目标。

首先，我们计算当前字母在字母表中的位置，为此，我们获取其 ASCII 码并从中减去字母a的 ASCII 码。然后我们将偏移量应用到这个位置，小心地使用模数以保持在字母表范围内。最后，我们通过将新位置添加到字母a的 ASCII 码中来检索新字符。

现在，让我们在偏移量为 3 的消息“他告诉我我永远无法教美洲驼开车”上尝试此实现：

```java
CaesarCipher cipher = new CaesarCipher();

String cipheredMessage = cipher.cipher("he told me i could never teach a llama to drive", 3);

assertThat(cipheredMessage)
  .isEqualTo("kh wrog ph l frxog qhyhu whdfk d oodpd wr gulyh");
```

正如我们所见，加密后的消息遵循之前为偏移量 3 定义的匹配。

现在，这个特定示例具有在转换期间不超过字母z的特殊性，因此不必返回到字母表的开头。因此，让我们用 10 的偏移量再试一次，这样一些字母将被映射到字母表开头的字母，比如t将被映射到d：

```java
String cipheredMessage = cipher.cipher("he told me i could never teach a llama to drive", 10);

assertThat(cipheredMessage)
  .isEqualTo("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo");
```

由于模运算，它按预期工作。该操作还处理较大的偏移量。假设我们要使用 36 作为偏移量，相当于 10，模运算确保转换将给出相同的结果。

## 3.破译

### 3.1. 解释

现在，让我们看看当我们知道用于加密它的偏移量时如何破译这样的消息。

事实上，解密用凯撒密码加密的消息可以看作是用负偏移量加密它，或者也用互补偏移量来加密它。

因此，假设我们有一条使用偏移量 3 加密的消息。然后，我们可以使用偏移量 -3 对其进行加密或使用偏移量 23 对其进行加密。无论哪种方式，我们都会检索到原始消息。

不幸的是，我们的算法无法立即处理负偏移量。我们在将循环字母转换回字母表末尾时会遇到问题(例如，将字母a转换为偏移量为 -1的字母z )。但是，我们可以计算正的互补偏移量，然后使用我们的算法。

那么，如何获得这个互补偏移量呢？这样做的天真方法是从 26 中减去原始偏移量。当然，这适用于 0 到 26 之间的偏移量，但否则会产生负面结果。

这就是我们将在执行减法之前直接在原始偏移量上再次使用模运算符的地方。这样，我们确保始终返回正偏移量。

### 3.2. Java中的算法

现在让我们用Java实现它。首先，我们将向我们的类添加一个decipher()方法：

```java
String decipher(String message, int offset) {}
```

然后，让我们用计算出的互补偏移调用cipher()方法：

```java
return cipher(message, 26 - (offset % 26));
```

就是这样，我们的解密算法就设置好了。让我们在偏移量为 36 的示例中尝试一下：

```java
String decipheredSentence = cipher.decipher("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo", 36);
assertThat(decipheredSentence)
  .isEqualTo("he told me i could never teach a llama to drive");
```

如我们所见，我们检索了原始消息。

## 4.破解凯撒密码

### 4.1. 解释

现在我们已经介绍了使用凯撒密码加密和解密消息，我们可以深入研究如何破解它。也就是说，首先在不知道使用的偏移量的情况下解密加密消息。

为此，我们将利用[概率在文本中查找英文字母](http://www.cs.trincoll.edu/~crypto/historical/caesar.html)。这个想法是使用偏移量 0 到 25 来破译消息，并检查哪个移位呈现类似于英文文本的字母分布。

为了确定两个分布的相似性，我们将使用[卡方统计量](http://practicalcryptography.com/cryptanalysis/text-characterisation/chi-squared-statistic/)。

卡方统计量将提供一个数字，告诉我们两个分布是否相似。数字越小，它们越相似。

因此，我们将为每个偏移量计算卡方，然后返回具有最小卡方的那个。这应该给我们用于加密消息的偏移量。

但是，我们必须记住，这种技术不是万无一失的，如果消息太短或不幸地使用不能代表标准英语文本的单词，它可能会返回错误的偏移量。

### 4.2. 定义基本字母分布

现在让我们看看如何在Java中实现中断算法。

首先，让 我们在CaesarCipher类中创建一个breakCipher()方法 ，它将返回用于加密消息的偏移量：

```java
int breakCipher(String message) {}
```

然后，让我们定义一个数组，其中包含在英文文本中找到特定字母的概率：

```java
double[] englishLettersProbabilities = {0.073, 0.009, 0.030, 0.044, 0.130, 0.028, 0.016, 0.035, 0.074,
  0.002, 0.003, 0.035, 0.025, 0.078, 0.074, 0.027, 0.003,
  0.077, 0.063, 0.093, 0.027, 0.013, 0.016, 0.005, 0.019, 0.001};
```

从这个数组中，我们可以通过将概率乘以消息的长度来计算给定消息中字母的预期频率：

```java
double[] expectedLettersFrequencies = Arrays.stream(englishLettersProbabilities)
  .map(probability -> probability  message.getLength())
  .toArray();
```

例如，在一条长度为 100 的消息中，我们应该期望字母a出现 7.3 次，字母e出现 13 次。

### 4.3. 计算卡方

现在，我们要计算解密消息字母分布和标准英文字母分布的卡方。

为此，我们需要导入[Apache Commons Math3 库](https://search.maven.org/search?q=g:org.apache.commons a:commons-math3)，其中包含用于计算卡方的实用程序类：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

我们现在需要做的是创建一个数组，其中包含为 0 到 25 之间的每个偏移量计算的卡方。

因此，我们将使用每个偏移量破译加密消息，然后计算该消息中的字母数。

最后，我们将使用 ChiSquareTest#chiSquare方法计算预期和观察到的字母分布之间的卡方：

```java
double[] chiSquares = new double[26];

for (int offset = 0; offset < chiSquares.length; offset++) {
    String decipheredMessage = decipher(message, offset);
    long[] lettersFrequencies = observedLettersFrequencies(decipheredMessage);
    double chiSquare = new ChiSquareTest().chiSquare(expectedLettersFrequencies, lettersFrequencies);
    chiSquares[offset] = chiSquare;
}

return chiSquares;
```

observedLettersFrequencies()方法简单地实现了传递消息中字母a到z的 计数：

```java
long[] observedLettersFrequencies(String message) {
    return IntStream.rangeClosed('a', 'z')
      .mapToLong(letter -> countLetter((char) letter, message))
      .toArray();
}

long countLetter(char letter, String message) {
    return message.chars()
      .filter(character -> character == letter)
      .count();
}
```

### 4.4. 找到最可能的偏移量

计算完所有卡方后，我们可以返回与最小卡方匹配的偏移量：

```java
int probableOffset = 0;
for (int offset = 0; offset < chiSquares.length; offset++) {
    <span class="x x-first">log</span><span class="pl-k x">.</span><span class="x x-last">debug</span>(String.format("Chi-Square for offset %d: %.2f", offset, chiSquares[offset]));
    if (chiSquares[offset] < chiSquares[probableOffset]) {
        probableOffset = offset;
    }
}

return probableOffset;
```

虽然没有必要以偏移量 0 进入循环，因为我们认为它是开始循环之前的最小值，但我们这样做是为了打印其卡方值。

让我们在使用偏移量 10 加密的消息上尝试此算法：

```java
int offset = algorithm.breakCipher("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo");
assertThat(offset).isEqualTo(10);

assertThat(algorithm.decipher("ro dyvn wo s myevn xofob dokmr k vvkwk dy nbsfo", offset))
  .isEqualTo("he told me i could never teach a llama to drive");
```

正如我们所看到的，该方法检索了正确的偏移量，然后可以使用它来解密消息并检索原始消息。

以下是针对此特定中断计算的不同卡方：

```plaintext
Chi-Square for offset 0: 210.69
Chi-Square for offset 1: 327.65
Chi-Square for offset 2: 255.22
Chi-Square for offset 3: 187.12
Chi-Square for offset 4: 734.16
Chi-Square for offset 5: 673.68
Chi-Square for offset 6: 223.35
Chi-Square for offset 7: 111.13
Chi-Square for offset 8: 270.11
Chi-Square for offset 9: 153.26
Chi-Square for offset 10: 23.74
Chi-Square for offset 11: 643.14
Chi-Square for offset 12: 328.83
Chi-Square for offset 13: 434.19
Chi-Square for offset 14: 384.80
Chi-Square for offset 15: 1206.47
Chi-Square for offset 16: 138.08
Chi-Square for offset 17: 262.66
Chi-Square for offset 18: 253.28
Chi-Square for offset 19: 280.83
Chi-Square for offset 20: 365.77
Chi-Square for offset 21: 107.08
Chi-Square for offset 22: 548.81
Chi-Square for offset 23: 255.12
Chi-Square for offset 24: 458.72
Chi-Square for offset 25: 325.45
```

正如我们所见，偏移量 10 的明显小于其他的。

## 5.总结

在本文中，我们介绍了凯撒密码。我们学习了如何通过将消息的字母移动给定的偏移量来加密和破译消息。我们还学习了如何破解密码。我们看到了所有允许我们这样做的Java实现。