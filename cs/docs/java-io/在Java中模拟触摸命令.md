## 1. 概述

[Linux 中的touch](https://www.baeldung.com/linux/touch-command)命令是更改文件或目录的访问时间和修改时间的简便方法。它还可以用于快速创建一个空文件。

在这个简短的教程中，我们将了解如何在Java中模拟此命令。

## 2.使用纯Java

### 2.1. 创建我们的触摸方法

让我们用 Java创建我们的触摸方法。如果文件不存在，此方法将创建一个空文件。它可以更改文件的访问时间或修改时间，或两者都更改。

此外，它还可以使用从输入传入的自定义时间：

```java
public static void touch(String path, String... args) throws IOException, ParseException {
    File file = new File(path);
    if (!file.exists()) {
        file.createNewFile();
        if (args.length == 0) {
            return;
        }
    }
    long timeMillis = args.length < 2 ? System.currentTimeMillis() : new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(args[1]).getTime();
    if (args.length > 0) {
        // change access time only
        if ("a".equals(args[0])) {
            FileTime accessFileTime = FileTime.fromMillis(timeMillis);
            Files.setAttribute(file.toPath(), "lastAccessTime", accessFileTime);
            return;
        }
        // change modification time only
        if ("m".equals(args[0])) {
            file.setLastModified(timeMillis);
            return;
        }
    }
    // other inputs will change both
    FileTime accessFileTime = FileTime.fromMillis(timeMillis);
    Files.setAttribute(file.toPath(), "lastAccessTime", accessFileTime);
    file.setLastModified(timeMillis);
}
```

如上所示，我们的方法使用[可变参数](https://www.baeldung.com/java-varargs)来避免重载，我们可以以“dd-MM-yyyy hh:mm:ss”格式将自定义时间传递给该方法。

### 2.2. 使用我们的触摸方法

让我们用我们的方法创建一个空文件：

```java
touch("test.txt");
```

并在Linux中使用stat命令查看文件信息：

```bash
stat test.txt
```

我们可以在stat输出中看到文件的访问和修改时间：

```plaintext
Access: 2021-12-07 10:42:16.474007513 +0700
Modify: 2021-12-07 10:42:16.474007513 +0700
```

现在，让我们用我们的方法改变它的访问时间：

```java
touch("test.txt", "a", "16-09-2020 08:00:00");
```

然后我们将再次使用stat命令获取此文件信息：

```plaintext
Access: 2020-09-16 08:00:00.000000000 +0700
Modify: 2021-12-07 10:42:16.474007000 +0700

```

## 3. 使用 Apache Commons Lang

我们还可以使用[Apache Commons Lang](https://www.baeldung.com/java-commons-lang-3)库中的FileUtils类。这个类有一个易于使用的touch()方法，如果文件不存在，它也会创建一个空文件：

```java
FileUtils.touch(new File("/home/baeldung/test.txt"));
```

注意如果文件已经存在，这个方法只会更新文件的修改时间，不会更新访问时间。

## 4。总结

在本文中，我们了解了如何在Java中模拟 Linux touch命令。