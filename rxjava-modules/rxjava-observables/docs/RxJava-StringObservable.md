## 1. StringObservable简介

在RxJava中处理字符串序列可能具有挑战性；幸运的是[RxJavaString](https://github.com/ReactiveX/RxJavaString)为我们提供了所有必要的工具。

在本文中，我们将介绍包含一些有用的字符串运算符的StringObservable。因此，在开始之前，建议先看一下[RxJava简介](https://www.baeldung.com/rx-java)。

## 2. Maven设置

首先，让我们在依赖项中包含RxJavaString：

```xml
<dependency>
  	<groupId>io.reactivex</groupId>
  	<artifactId>rxjava-string</artifactId>
	<version>1.1.1</version>
</dependency>
```

最新版本的rxjava-string在[Maven Central](https://search.maven.org/search?q=a:rxjava-string)上可用。

## 3. StringObservable

**StringObservable是一个方便的运算符，用于表示可能无限的编码字符串序列**。

运算符from读取一个输入流，创建一个Observable，它发出字节数组的字符边界序列：

<img src="../assets/img.png">

由[Reactivex.io](http://reactivex.io/)提供，在[CC-BY](https://creativecommons.org/licenses/by/3.0/)下使用

我们可以使用from运算符直接从InputStream创建一个Observable：

```java
TestSubscriber testSubscriber = new TestSubscriber();
ByteArrayInputStream is = new ByteArrayInputStream("Lorem ipsum loream, Lorem ipsum lore".getBytes());
Observable<byte[]> observableByteStream = StringObservable.from(is);

// emits 8 byte array items
observableByteStream.subscribe(testSubscriber);
```

## 4. 将字节转换为字符串

可以使用decode和encode运算符对来自不同字符集的无限序列进行编码/解码。

顾名思义，这些将简单地创建一个Observable，它发出编码或解码的字节数组或字符串序列，因此，**如果我们需要处理不同字符集中的字符串，我们可以使用它**：

解码字节数组Observable：

```java
TestSubscriber testSubscriber = new TestSubscriber();
ByteArrayInputStream is = new ByteArrayInputStream("Lorem ipsum loream, Lorem ipsum lore".getBytes());
Observable<byte[]> byteArrayObservable = StringObservable.from(is);
Observable<String> stringObservable = StringObservable
    .decode(byteArrayObservable, StandardCharsets.UTF_8);

// emits UTF-8 decoded strings,"Lorem ipsum loream, Lorem ipsum lore"
stringObservable.subscribe(testSubscriber);
```

## 5. 拆分字符串

StringObservable也有一些方便的运算符用于拆分字符串序列：split和byLine，它们都创建一个新的Observable，它按照模式对输入数据输出元素进行分块：

<img src="../assets/img_1.png">

来自[Reactivex.io](http://reactivex.io/)

```java
TestSubscriber testSubscriber = new TestSubscriber();
Observable<String> sourceObservable = Observable.just("Lorem ipsum loream,Lorem ipsum ", "lore");
Observable<String> splittedObservable = StringObservable.split(sourceObservable, ",");

// emits 2 strings "Lorem ipsum loream", "Lorem ipsum lore"
splittedObservable.subscribe(testSubscriber);
```

## 6. 连接字符串

与上一节的运算符互补的是join和stringConcat，它们连接来自String Observable的元素，在给定分隔符的情况下发出单个字符串。

另请注意，这些将在发出输出之前消耗所有元素。

<img src="../assets/img_2.png">

来自[Reactivex.io](http://reactivex.io/)

```java
TestSubscriber testSubscriber = new TestSubscriber();
Observable<String> sourceObservable = Observable.just("Lorem ipsum loream", "Lorem ipsum lore");
Observable<String> joinedObservable = StringObservable.join(sourceObservable, ",");

// emits single string "Lorem ipsum loream,Lorem ipsum lore"
joinedObservable.subscribe(testSubscriber);
```

## 7. 总结

对StringObservable的简要介绍展示了一些使用RxJavaString进行字符串操作的用例。