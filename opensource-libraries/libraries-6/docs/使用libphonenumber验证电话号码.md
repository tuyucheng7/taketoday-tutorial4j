## 1. 概述

在本快速教程中，我们将了解如何使用Google 的开源库[libphonenumber](https://github.com/google/libphonenumber)在Java中验证电话号码。

## 2.Maven依赖

首先，我们需要在我们的pom.xml中添加这个库的依赖：

```xml
<dependency>
    <groupId>com.googlecode.libphonenumber</groupId>
    <artifactId>libphonenumber</artifactId>
    <version>8.12.10</version>
</dependency>
```

最新的版本信息可以在[Maven Central](https://search.maven.org/artifact/com.googlecode.libphonenumber/libphonenumber)上找到。

现在，我们已经准备好使用这个库提供的所有功能。

## 3.电话号码实用程序 

该库提供了一个实用程序类[PhoneNumberUtil](https://www.javadoc.io/doc/com.googlecode.libphonenumber/libphonenumber/8.12.9/com/google/i18n/phonenumbers/PhoneNumberUtil.html)，它提供了多种处理电话号码的方法。

让我们看几个示例，了解如何使用其各种 API 进行验证。

重要的是，在所有示例中，我们将使用此类的单例对象来进行方法调用：

```java
PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
```

### 3.1. 是可能的数字

使用P honeNumberUtil#isPossibleNumber，我们可以检查给定的号码是否适用于特定的[国家代码](https://countrycode.org/)或地区。

例如，让我们以国家代码为 1 的美国为例。我们可以用这种方式检查给定的电话号码是否可能是美国号码：

```java
@Test
public void givenPhoneNumber_whenPossible_thenValid() {
    PhoneNumber number = new PhoneNumber();
    number.setCountryCode(1).setNationalNumber(123000L);
    assertFalse(phoneNumberUtil.isPossibleNumber(number));
    assertFalse(phoneNumberUtil.isPossibleNumber("+1 343 253 00000", "US"));
    assertFalse(phoneNumberUtil.isPossibleNumber("(343) 253-00000", "US"));
    assertFalse(phoneNumberUtil.isPossibleNumber("dial p for pizza", "US"));
    assertFalse(phoneNumberUtil.isPossibleNumber("123-000", "US"));
}
```

在这里，我们还使用了此函数的另一种变体，方法是将我们希望拨出号码的区域作为String传入。

### 3.2. isPossibleNumberForType

该库可识别不同类型的电话号码，例如固定电话、移动电话、免费电话、语音邮件、VoIP、寻呼机[等等](https://www.javadoc.io/doc/com.googlecode.libphonenumber/libphonenumber/8.12.9/com/google/i18n/phonenumbers/PhoneNumberUtil.PhoneNumberType.html)。

它的实用方法isPossibleNumberForType检查给定的数字对于特定区域中的给定类型是否可能。

例如，让我们以阿根廷为例，因为它允许不同类型的数字可能具有不同的长度。

因此，我们可以使用它来演示此 API 的功能：

```java
@Test
public void givenPhoneNumber_whenPossibleForType_thenValid() {
    PhoneNumber number = new PhoneNumber();
    number.setCountryCode(54);

    number.setNationalNumber(123456);
    assertTrue(phoneNumberUtil.isPossibleNumberForType(number, PhoneNumberType.FIXED_LINE));
    assertFalse(phoneNumberUtil.isPossibleNumberForType(number, PhoneNumberType.TOLL_FREE));

    number.setNationalNumber(12345678901L);
    assertFalse(phoneNumberUtil.isPossibleNumberForType(number, PhoneNumberType.FIXED_LINE));
    assertTrue(phoneNumberUtil.isPossibleNumberForType(number, PhoneNumberType.MOBILE));
    assertFalse(phoneNumberUtil.isPossibleNumberForType(number, PhoneNumberType.TOLL_FREE));
}
```

如我们所见，上述代码验证了阿根廷允许使用 6 位固定电话号码和 11 位手机号码。

### 3.3. isAlphaNumber

此方法用于验证给定的电话号码是否为有效的字母数字号码，例如325-CARS：

```java
@Test
public void givenPhoneNumber_whenAlphaNumber_thenValid() {
    assertTrue(phoneNumberUtil.isAlphaNumber("325-CARS"));
    assertTrue(phoneNumberUtil.isAlphaNumber("0800 REPAIR"));
    assertTrue(phoneNumberUtil.isAlphaNumber("1-800-MY-APPLE"));
    assertTrue(phoneNumberUtil.isAlphaNumber("1-800-MY-APPLE.."));
    assertFalse(phoneNumberUtil.isAlphaNumber("+876 1234-1234"));
}
```

需要说明的是，有效的字母数字至少包含三个数字开头，后跟三个或更多字母。上面的实用方法首先去除给定输入的任何格式，然后检查这种情况。

### 3.4. 是有效号码

我们之前讨论的 API 仅根据电话号码的长度快速检查电话号码。另一方面，isValidNumber使用前缀和长度信息进行完整验证：

```java
@Test
public void givenPhoneNumber_whenValid_thenOK() throws Exception {

    PhoneNumber phone = phoneNumberUtil.parse("+911234567890", 
      CountryCodeSource.UNSPECIFIED.name());

    assertTrue(phoneNumberUtil.isValidNumber(phone));
    assertTrue(phoneNumberUtil.isValidNumberForRegion(phone, "IN"));
    assertFalse(phoneNumberUtil.isValidNumberForRegion(phone, "US"));
    assertTrue(phoneNumberUtil.isValidNumber(phoneNumberUtil.getExampleNumber("IN")));
}
```

在这里，当我们没有指定区域时以及指定区域时都会验证数字。

### 3.5. isNumberGeographical

此方法检查给定数字是否具有与其关联的地理或区域：

```java
@Test
public void givenPhoneNumber_whenNumberGeographical_thenValid() throws NumberParseException {
    
    PhoneNumber phone = phoneNumberUtil.parse("+911234567890", "IN");
    assertTrue(phoneNumberUtil.isNumberGeographical(phone));

    phone = new PhoneNumber().setCountryCode(1).setNationalNumber(2530000L);
    assertFalse(phoneNumberUtil.isNumberGeographical(phone));

    phone = new PhoneNumber().setCountryCode(800).setNationalNumber(12345678L);
    assertFalse(phoneNumberUtil.isNumberGeographical(phone));
}
```

在这里，在上面的第一个断言中，我们以国际格式提供了带有地区代码的电话号码，并且该方法返回了 true。第二个断言使用来自美国的本地号码，第三个断言使用免费电话号码。因此 API 为这两个返回了 false。

## 4. 总结

在本教程中，我们看到了libphonenumber提供的一些功能，用于使用代码示例格式化和验证电话号码。

这是一个丰富的库，提供了更多的实用功能，并满足了我们对格式化、解析和验证电话号码的大部分应用程序需求。