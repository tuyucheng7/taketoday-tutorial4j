## 1. 概述

在本快速教程中，我们将展示如何使用[RxJava](https://github.com/ReactiveX/RxJava)编写自定义运算符。

我们将讨论如何构建这个简单的运算符以及一个转换器-既可以作为一个类，也可以作为一个简单的函数。

## 2. Maven配置

首先，我们需要确保我们在pom.xml中有rxjava依赖：

```xml
<dependency>
    <groupId>io.reactivex</groupId>
    <artifactId>rxjava</artifactId>
    <version>1.3.0</version>
</dependency>
```

我们可以在[Maven Central](https://search.maven.org/search?q=rxjava)查看最新版本的rxjava。

## 3. 自定义运算符

**我们可以通过实现[Operator](http://reactivex.io/RxJava/1.x/javadoc/rx/Observable.Operator.html)接口来创建自定义运算符**，在下面的示例中，我们实现了一个简单的运算符，用于从String中删除非字母数字字符：

```java
public class ToCleanString implements Operator<String, String> {

    public static ToCleanString toCleanString() {
        return new ToCleanString();
    }

    private ToCleanString() {
        super();
    }

    @Override
    public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
        return new Subscriber<String>(subscriber) {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(t);
                }
            }

            @Override
            public void onNext(String item) {
                if (!subscriber.isUnsubscribed()) {
                    final String result = item.replaceAll("[^A-Za-z0-9]", "");
                    subscriber.onNext(result);
                }
            }
        };
    }
}
```

在上面的示例中，我们需要在应用我们的操作并将项目发送给它之前检查订阅者是否已订阅，因为这是不必要的。

**我们还将实例创建限制为静态工厂方法，以便在链接方法和使用静态导入时实现更加用户友好的可读性**。

现在，我们可以使用[lift](http://reactivex.io/RxJava/1.x/javadoc/rx/Observable.html#lift(rx.Observable.Operator))运算符轻松地将我们的自定义运算符与其他运算符链接起来：

```java
observable.lift(toCleanString())....
```

这是我们自定义运算符的简单测试：

```java
@Test
public void whenUseCleanStringOperator_thenSuccess() {
    List<String> list = Arrays.asList("john_1", "tom-3");
    List<String> results = new ArrayList<>();
    Observable<String> observable = Observable
        .from(list)
        .lift(toCleanString());
    observable.subscribe(results::add);

    assertThat(results, notNullValue());
    assertThat(results, hasSize(2));
    assertThat(results, hasItems("john1", "tom3"));
}
```

## 4. Transformer

我们还可以通过实现[Transformer](http://reactivex.io/RxJava/1.x/javadoc/rx/Observable.Transformer.html)接口来创建我们的运算符：

```java
public class ToLength implements Transformer<String, Integer> {

    public static ToLength toLength() {
        return new ToLength();
    }

    private ToLength() {
        super();
    }

    @Override
    public Observable<Integer> call(Observable<String> source) {
        return source.map(String::length);
    }
}
```

请注意，我们使用转换器toLength将我们的observable从String转换为它在Integer中的长度。

我们需要一个[compose](http://reactivex.io/RxJava/1.x/javadoc/rx/Observable.html#compose(rx.Observable.Transformer))运算符来使用我们的转换器：

```java
observable.compose(toLength())...
```

这是一个简单的测试：

```java
@Test
public void whenUseToLengthOperator_thenSuccess() {
    List<String> list = Arrays.asList("john", "tom");
    List<Integer> results = new ArrayList<>();
    Observable<Integer> observable = Observable
        .from(list)
        .compose(toLength());
    observable.subscribe(results::add);

    assertThat(results, notNullValue());
    assertThat(results, hasSize(2));
    assertThat(results, hasItems(4, 3));
}
```

lift(Operator)对observable的订阅者进行操作，而compose(Transformer)对observable本身进行操作。

当我们创建我们的自定义运算符时，如果我们想对可观察对象作为一个整体进行操作，我们应该选择Transformer，如果我们想对可观察对象发出的项目进行操作，则选择Operator。

## 5. 自定义运算符作为函数

我们可以将自定义运算符实现为函数而不是公共类：

```java
Operator<String, String> cleanStringFn = subscriber -> {
    return new Subscriber<String>(subscriber) {
        @Override
        public void onCompleted() {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }

        @Override
        public void onError(Throwable t) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onError(t);
            }
        }

        @Override
        public void onNext(String str) {
            if (!subscriber.isUnsubscribed()) {
                String result = str.replaceAll("[^A-Za-z0-9]", "");
                subscriber.onNext(result);
            }
        }
    };
};
```

这是简单的测试：

```java
List<String> results = new ArrayList<>();
Observable.from(Arrays.asList("ap_p-l@e", "or-an?ge"))
    .lift(cleanStringFn)
    .subscribe(results::add);

assertThat(results, notNullValue());
assertThat(results, hasSize(2));
assertThat(results, hasItems("apple", "orange"));
```

同样对于Transformer示例：

```java
@Test
public void whenUseFunctionTransformer_thenSuccess() {
    Transformer<String, Integer> toLengthFn = s -> s.map(String::length);

    List<Integer> results = new ArrayList<>();
    Observable.from(Arrays.asList("apple", "orange"))
        .compose(toLengthFn)
        .subscribe(results::add);

    assertThat(results, notNullValue());
    assertThat(results, hasSize(2));
    assertThat(results, hasItems(5, 6));
}
```

## 6. 总结

在本文中，我们展示了如何编写RxJava运算符。