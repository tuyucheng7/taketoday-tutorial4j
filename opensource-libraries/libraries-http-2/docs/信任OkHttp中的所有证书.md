## 1. 概述

在本教程中，我们将了解如何创建和配置OkHttpClient以信任所有证书。

查看我们[关于 OkHttp](https://www.baeldung.com/tag/okhttp/)的文章，了解有关该库的更多细节。

## 2.Maven依赖

让我们从将[OkHttp](https://search.maven.org/classic/#search|gav|1|g%3A"com.squareup.okhttp3" AND a%3A"okhttp")依赖项添加到我们的pom.xml文件开始：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.9.2</version>
</dependency>
```

## 3.使用普通的OkHttpClient

首先，让我们使用一个标准的OkHttpClient对象并调用一个带有过期证书的网页：

```java
OkHttpClient client = new OkHttpClient.Builder().build();
client.newCall(new Request.Builder().url("https://expired.badssl.com/").build()).execute();
```

堆栈跟踪输出将如下所示：

```plaintext
sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed
```

现在，让我们看看当我们使用自签名证书尝试另一个网站时收到的错误：

```plaintext
sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

让我们尝试一个主机证书错误的网站：

```plaintext
Hostname wrong.host.badssl.com not verified
```

如我们所见，默认情况下，OkHttpClient将在调用具有错误证书的站点时抛出错误。所以接下来，我们将看到如何创建和配置OkHttpClient以信任所有证书。

## 4. 设置OkHttpClient以信任所有证书

让我们创建包含单个X509TrustManager的TrustManager数组，该 X509TrustManager通过覆盖它们的方法来禁用默认证书验证：

```java
TrustManager[] trustAllCerts = new TrustManager[]{
    new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }
};
```

我们将使用这个TrustManager数组 来创建一个SSLContext：

```java
SSLContext sslContext = SSLContext.getInstance("SSL");
sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
```

然后，我们将使用这个SSLContext来设置OkHttpClient构建器的SSLSocketFactory：

```java
OkHttpClient.Builder newBuilder = new OkHttpClient.Builder();
newBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
newBuilder.hostnameVerifier((hostname, session) -> true);
```

我们还将新生成器的HostnameVerifier设置为一个新的HostnameVerifier对象，其验证方法始终返回true。

最后，我们可以获得一个新的OkHttpClient对象并再次调用证书错误的站点，不会出现任何错误：

```java
OkHttpClient newClient = newBuilder.build();
newClient.newCall(new Request.Builder().url("https://expired.badssl.com/").build()).execute();
```

## 5.总结

在这篇简短的文章中，我们了解了如何创建和配置OkHttpClient以信任所有证书。当然，不建议信任所有证书。但是，在某些情况下我们可能需要它。