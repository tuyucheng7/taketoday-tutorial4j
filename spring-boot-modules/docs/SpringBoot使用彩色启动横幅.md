## 1. 概述

[Spring Boot](https://www.baeldung.com/spring-boot)的一项令人喜爱的功能是它的启动[横幅](https://www.baeldung.com/spring-boot-custom-banners)。多年来，Spring Boot 已经发展到支持各种类型的横幅广告。例如，在[Spring Boot 1.3](https://github.com/spring-projects/spring-boot/wiki/spring-boot-1.3-release-notes#ansi-color-bannertxt-files)中为横幅添加了文本和背景颜色支持。

在本快速教程中，我们将了解Spring Boot的彩色横幅支持以及如何使用它。

## 2. 改变背景颜色

要为Spring Boot横幅添加背景颜色，我们只需使用AnsiBackground类在banner.txt 行前面加上所需的颜色代码。

例如，让我们创建一个banner.txt文件，使整个背景为红色：

```plaintext
${AnsiBackground.RED}
  ___         _   _      _ 
 / __|  ___  | | (_)  __| |
 \__ \ / _ \ | | | | / _` |
 |___/ \___/ |_| |_| \__,_|
${AnsiBackground.DEFAULT}
```

[![春季靴子彩色横幅纯色背景](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-banner-solid-background.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-banner-solid-background.jpg)

事实上，我们可以在一个横幅中使用任意多的背景颜色。

例如，我们可以将每一行设置为它自己的背景颜色。我们只需在每一行前面加上所需的颜色：

```plaintext
${AnsiBackground.RED}    ____             _             __
${AnsiBackground.BLUE}   / __ \  ____ _   (_)   ____    / /_   ____  _      __
${AnsiBackground.YELLOW}  / /_/ / / __ `/  / /   / __ \  / __ \ / __ \| | /| / /
${AnsiBackground.GREEN} / _, _/ / /_/ /  / /   / / / / / /_/ // /_/ /| |/ |/ /
${AnsiBackground.MAGENTA}/_/ |_|  \__,_/  /_/   /_/ /_/ /_.___/ \____/ |__/|__/
${AnsiBackground.DEFAULT}

```

[![春季靴颜色横幅彩虹背景](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-banner-rainbow-background.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-banner-rainbow-background.jpg)

请务必记住，我们所有的应用程序日志记录都将使用banner.txt中最后指定的背景颜色。因此，最佳做法是始终以默认颜色结束banner.txt文件。

## 3.改变文字颜色

要更改文本的颜色，我们可以使用AnsiColor类。就像AnsiBackground类一样，它具有可供我们选择的预定义颜色常量。

我们只需在每组字符前加上所需的颜色即可：

```plaintext
${AnsiColor.RED}.------.${AnsiColor.BLACK}.------.
${AnsiColor.RED}|A.--. |${AnsiColor.BLACK}|K.--. |
${AnsiColor.RED}| (\/) |${AnsiColor.BLACK}| (\/) |
${AnsiColor.RED}| :\/: |${AnsiColor.BLACK}| :\/: |
${AnsiColor.RED}| '--'A|${AnsiColor.BLACK}| '--'K|
${AnsiColor.RED}`------'${AnsiColor.BLACK}`------'
${AnsiColor.DEFAULT}
```

[![春季靴颜色文本](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-text.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-text.jpg)

与背景颜色一样，重要的是横幅的最后一行始终将颜色重置为默认值。

## 4. ANSI 8 位颜色

Spring Boot 2.2 的新特性之一是支持[ANSI 8 位颜色](https://en.wikipedia.org/wiki/ANSI_escape_code#8-bit)。我们可以使用 256 种颜色的全部范围来指定文本和背景颜色，而不是局限于少数几种预定义颜色。

为了使用新颜色，AnsiColor和AnsiBackground属性现在都接受数值而不是颜色名称：

```plaintext
${AnsiColor.1}${AnsiBackground.233}  ______  __________ .___ ___________
${AnsiBackground.235} /  __  \ \______   \|   |\__    ___/
${AnsiBackground.237} >      <  |    |  _/|   |  |    |
${AnsiBackground.239}/   --   \ |    |   \|   |  |    |
${AnsiBackground.241}\______  / |______  /|___|  |____|
${AnsiBackground.243}       \/         \/
${AnsiBackground.DEFAULT}${AnsiColor.DEFAULT}
```

[![spring boot 颜色横幅 8 位 ansi](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-banner-8-bit-ansi.jpg)](https://www.baeldung.com/wp-content/uploads/2019/12/spring-boot-color-banner-8-bit-ansi.jpg)

请注意，我们可以根据需要混合文本和背景属性。我们甚至可以在同一个横幅中混合使用新的 8 位颜色代码和旧的颜色常量。

## 5.总结

在本文中，我们了解了如何更改Spring Boot横幅的文本和背景颜色。

我们还看到了较新版本的Spring Boot如何支持 ANSI 8 位颜色代码。