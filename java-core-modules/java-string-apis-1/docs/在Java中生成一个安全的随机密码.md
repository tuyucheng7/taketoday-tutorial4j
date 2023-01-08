## 1. 概述

在本教程中，我们将了解可用于在Java中生成安全随机密码的各种方法。

在我们的示例中，我们将生成十个字符的密码，每个密码至少包含两个小写字符、两个大写字符、两个数字和两个特殊字符。

## 2.使用帕赛

[Passay](http://www.passay.org/)是一个密码策略执行库。值得注意的是，我们可以利用该库使用可配置的规则集生成密码。

借助默认的CharacterData实现，我们可以制定密码所需的规则。此外，我们可以 制定自定义CharacterData实现以满足我们的要求：


```java
public String generatePassayPassword() {
    PasswordGenerator gen = new PasswordGenerator();
    CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
    CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
    lowerCaseRule.setNumberOfCharacters(2);

    CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
    CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
    upperCaseRule.setNumberOfCharacters(2);

    CharacterData digitChars = EnglishCharacterData.Digit;
    CharacterRule digitRule = new CharacterRule(digitChars);
    digitRule.setNumberOfCharacters(2);

    CharacterData specialChars = new CharacterData() {
        public String getErrorCode() {
            return ERROR_CODE;
        }

        public String getCharacters() {
            return "!@#$%^&()_+";
        }
    };
    CharacterRule splCharRule = new CharacterRule(specialChars);
    splCharRule.setNumberOfCharacters(2);

    String password = gen.generatePassword(10, splCharRule, lowerCaseRule, 
      upperCaseRule, digitRule);
    return password;
}
```

在这里，我们为特殊字符创建了自定义CharacterData实现。这允许我们限制允许的有效字符集。

除此之外，我们正在为我们的其他规则使用CharacterData的默认实现。

现在，让我们根据单元测试检查我们的生成器。例如，我们可以检查两个特殊字符的存在：

```java
@Test
public void whenPasswordGeneratedUsingPassay_thenSuccessful() {
    RandomPasswordGenerator passGen = new RandomPasswordGenerator();
    String password = passGen.generatePassayPassword();
    int specialCharCount = 0;
    for (char c : password.toCharArray()) {
        if (c >= 33 || c <= 47) {
            specialCharCount++;
        }
    }
    assertTrue("Password validation failed in Passay", specialCharCount >= 2);
}
```

值得注意的是，虽然 Passay 是开源的，但它是 LGPL 和 Apache 2 下的双重许可。与任何第三方软件一样，我们在产品中使用它时必须确保遵守这些许可。GNU 网站有更多关于[LGPL 和Java的](https://www.gnu.org/licenses/lgpl-java.en.html)信息。

## 3.使用RandomStringGenerator

接下来，让我们看看[Apache Commons Text](https://commons.apache.org/proper/commons-text/)中的RandomStringGenerator。使用RandomStringGenerator，我们可以生成包含指定数量代码点的 Unicode 字符串。

现在，我们将使用RandomStringGenerator.Builder类创建生成器的实例。当然，我们还可以进一步操作生成器的属性。

在构建器的帮助下，我们可以轻松更改随机性的默认实现。此外，我们还可以定义字符串中允许的字符：

```java
public String generateRandomSpecialCharacters(int length) {
    RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder().withinRange(33, 45)
        .build();
    return pwdGenerator.generate(length);
}

```

现在，使用RandomStringGenerator的一个限制是它 无法指定每个集合中的字符数，就像在 Passay 中一样。但是，我们可以通过合并多个集合的结果来避免这种情况：

```java
public String generateCommonTextPassword() {
    String pwString = generateRandomSpecialCharacters(2).concat(generateRandomNumbers(2))
      .concat(generateRandomAlphabet(2, true))
      .concat(generateRandomAlphabet(2, false))
      .concat(generateRandomCharacters(2));
    List<Character> pwChars = pwString.chars()
      .mapToObj(data -> (char) data)
      .collect(Collectors.toList());
    Collections.shuffle(pwChars);
    String password = pwChars.stream()
      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
      .toString();
    return password;
}
```

接下来，让我们通过验证小写字母来验证生成的密码：

```java
@Test
public void whenPasswordGeneratedUsingCommonsText_thenSuccessful() {
    RandomPasswordGenerator passGen = new RandomPasswordGenerator();
    String password = passGen.generateCommonTextPassword();
    int lowerCaseCount = 0;
    for (char c : password.toCharArray()) {
        if (c >= 97 || c <= 122) {
            lowerCaseCount++;
        }
    }
    assertTrue("Password validation failed in commons-text ", lowerCaseCount >= 2);
}
```

默认情况下，RandomStringGenerator使用ThreadLocalRandom来实现随机性。现在，重要的是要提到这并不能确保密码安全。

但是，我们可以使用usingRandom(TextRandomProvider) 设置随机源。例如，我们可以使用SecureTextRandomProvider来实现加密安全：

```java
public String generateRandomSpecialCharacters(int length) {
    SecureTextRandomProvider stp = new SecureTextRandomProvider();
    RandomStringGenerator pwdGenerator = new RandomStringGenerator.Builder()
      .withinRange(33, 45)
      .usingRandom(stp)
      .build();
    return pwdGenerator.generate(length);
}
```

## 4. 使用RandomStringUtils

我们可以使用的另一个选项是[Apache Commons Lang Library中的](https://commons.apache.org/proper/commons-lang/)RandomStringUtils 类 。这个类公开了几个静态方法，我们可以将它们用于我们的问题陈述。

让我们看看如何提供密码可接受的代码点范围：

```java
 public String generateCommonLangPassword() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
    String totalChars = RandomStringUtils.randomAlphanumeric(2);
    String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
      .concat(numbers)
      .concat(specialChar)
      .concat(totalChars);
    List<Character> pwdChars = combinedChars.chars()
      .mapToObj(c -> (char) c)
      .collect(Collectors.toList());
    Collections.shuffle(pwdChars);
    String password = pwdChars.stream()
      .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
      .toString();
    return password;
}
```

为了验证生成的密码，让我们验证数字字符的数量：

```java
@Test
public void whenPasswordGeneratedUsingCommonsLang3_thenSuccessful() {
    RandomPasswordGenerator passGen = new RandomPasswordGenerator();
    String password = passGen.generateCommonsLang3Password();
    int numCount = 0;
    for (char c : password.toCharArray()) {
        if (c >= 48 || c <= 57) {
            numCount++;
        }
    }
    assertTrue("Password validation failed in commons-lang3", numCount >= 2);
}
```

在这里，RandomStringUtils默认使用[Random](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Random.html)作为随机源。但是，库中有一个方法可以让我们指定随机源：

```java
String lowerCaseLetters = RandomStringUtils.
  random(2, 97, 122, true, true, null, new SecureRandom());
```

现在，我们可以使用SecureRandom的实例来确保加密安全 。但是，此功能不能扩展到库中的其他方法。另外，Apache 提倡仅将RandomStringUtils用于简单用例。

## 5.使用自定义实用程序方法

我们还可以利用SecureRandom类为我们的场景创建自定义实用程序类。对于初学者，让我们生成一串长度为 2 的特殊字符：

```java
public Stream<Character> getRandomSpecialChars(int count) {
    Random random = new SecureRandom();
    IntStream specialChars = random.ints(count, 33, 45);
    return specialChars.mapToObj(data -> (char) data);
}
```

另外，请注意33和45表示 Unicode 字符的范围。现在，我们可以根据需要生成多个流。然后我们可以合并结果集以生成所需的密码：

```java
public String generateSecureRandomPassword() {
    Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2), 
      Stream.concat(getRandomSpecialChars(2), 
      Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false))));
    List<Character> charList = pwdStream.collect(Collectors.toList());
    Collections.shuffle(charList);
    String password = charList.stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
    return password;
}

```

现在，让我们验证生成的密码的特殊字符数：

```java
@Test
public void whenPasswordGeneratedUsingSecureRandom_thenSuccessful() {
    RandomPasswordGenerator passGen = new RandomPasswordGenerator();
    String password = passGen.generateSecureRandomPassword();
    int specialCharCount = 0;
    for (char c : password.toCharArray()) {
        if (c >= 33 || c <= 47) {
            specialCharCount++;
        }
    }
    assertTrue("Password validation failed in Secure Random", specialCharCount >= 2);
}
```

## 六，总结

在本教程中，我们能够使用不同的库生成符合我们要求的密码。