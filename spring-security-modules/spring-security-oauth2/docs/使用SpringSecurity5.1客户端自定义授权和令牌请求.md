## 1. 概述

有时OAuth2 API可能与标准略有不同，在这种情况下，我们需要对标准OAuth2请求进行一些自定义。

**Spring Security 5.1支持自定义OAuth2授权和令牌请求**。

在本教程中，我们将了解如何自定义请求参数和响应处理。

## 2. 自定义授权请求

首先，我们将自定义OAuth2授权请求。我们可以根据需要修改标准参数并在授权请求中添加额外的参数。

为此，**我们需要实现自己的OAuth2AuthorizationRequestResolver**：

```java
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo, String authorizationRequestBaseUri) {
        defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, authorizationRequestBaseUri);
    }

    // ...
}
```

请注意，我们使用DefaultOAuth2AuthorizationRequestResolver来提供基本功能。

我们还将覆盖resolve()方法以添加我们的自定义逻辑：

```java
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    // ...

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        if(req != null) {
            req = customizeAuthorizationRequest(req);
        }
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        if(req != null) {
            req = customizeAuthorizationRequest(req);
        }
        return req;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest req) {
        // ...
    }
}
```

稍后我们将使用我们的方法customizeAuthorizationRequest()方法添加自定义项，我们将在下一节中讨论。


实现自定义OAuth2AuthorizationRequestResolver之后，我们需要将它添加到我们的安全配置中：

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2Login()
              .authorizationEndpoint()
              .authorizationRequestResolver(
                    new CustomAuthorizationRequestResolver(clientRegistrationRepository(), "/oauth2/authorize-client"))
        //...
    }
}
```

**这里我们使用oauth2Login().authorizationEndpoint().authorizationRequestResolver()来注入我们自定义的OAuth2AuthorizationRequestResolver**。

## 3. 自定义授权请求标准参数

现在，让我们讨论实际的自定义。我们可以根据需要修改OAuth2AuthorizationRequest。

对于初学者，**我们可以为每个授权请求修改一个标准参数**。

例如，我们可以生成自己的“state”参数：

```java
private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest req) {
    return OAuth2AuthorizationRequest
        .from(req).state("xyz").build();
}
```

## 4. 授权请求额外参数

我们还可以使用OAuth2AuthorizationRequest的additionalParameters()方法**向OAuth2AuthorizationRequest添加额外的参数并传入一个Map**：

```java
private OAuth2AuthorizationRequest customizeAuthorizationRequest(OAuth2AuthorizationRequest req) {
    Map<String,Object> extraParams = new HashMap<String,Object>();
    extraParams.putAll(req.getAdditionalParameters()); 
    extraParams.put("test", "extra");
    
    return OAuth2AuthorizationRequest
        .from(req)
        .additionalParameters(extraParams)
        .build();
}
```

我们还必须确保在添加新参数之前包含旧的附加参数。

让我们通过自定义与Okta授权服务器一起使用的授权请求来查看一个更实际的示例。

### 4.1 自定义Okta授权请求

[Okta](https://developer.okta.com/docs/api/resources/oidc#authorize)为授权请求提供了额外的可选参数，为用户提供更多功能。例如，指示身份提供者的idp。

默认情况下，身份提供者是Okta，但我们可以使用idp参数对其进行自定义：

```java
private OAuth2AuthorizationRequest customizeOktaReq(OAuth2AuthorizationRequest req) {
    Map<String,Object> extraParams = new HashMap<String,Object>();
    extraParams.putAll(req.getAdditionalParameters()); 
    extraParams.put("idp", "https://idprovider.com");
    return OAuth2AuthorizationRequest
        .from(req)
        .additionalParameters(extraParams)
        .build();
}
```

## 5. 自定义令牌请求

现在，我们将了解如何自定义OAuth2令牌请求。

**我们可以通过自定义OAuth2AccessTokenResponseClient来自定义令牌请求**。

OAuth2AccessTokenResponseClient的默认实现是DefaultAuthorizationCodeTokenResponseClient。

我们可以通过提供自定义RequestEntityConverter来自定义令牌请求本身，甚至可以通过自定义DefaultAuthorizationCodeTokenResponseClient RestOperations来自定义令牌响应处理：

```java
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.tokenEndpoint()
              .accessTokenResponseClient(accessTokenResponseClient())
        //...
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient(){
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(new CustomRequestEntityConverter());

        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setTokenResponseConverter(new CustomTokenResponseConverter());
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        accessTokenResponseClient.setRestOperations(restTemplate);
        return accessTokenResponseClient;
    }
}
```

**我们可以使用tokenEndpoint().accessTokenResponseClient()注入我们自己的OAuth2AccessTokenResponseClient**。

要自定义令牌请求参数，我们将实现CustomRequestEntityConverter。同样，要自定义处理令牌响应，我们将实现CustomTokenResponseConverter。

我们将在以下部分中讨论CustomRequestEntityConverter和CustomTokenResponseConverter。

## 6. 令牌请求额外参数

现在，我们将了解如何通过构建自定义Converter为我们的令牌请求添加额外的参数：

```java
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private OAuth2AuthorizationCodeGrantRequestEntityConverter defaultConverter;

    public CustomRequestEntityConverter() {
        defaultConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
    }

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest req) {
        RequestEntity<?> entity = defaultConverter.convert(req);
        MultiValueMap<String, String> params = (MultiValueMap<String,String>) entity.getBody();
        params.add("test2", "extra2");
        return new RequestEntity<>(params, entity.getHeaders(), entity.getMethod(), entity.getUrl());
    }
}
```

**我们的转换器将OAuth2AuthorizationCodeGrantRequest转换为RequestEntity**。 

我们使用默认转换器OAuth2AuthorizationCodeGrantRequestEntityConverter来提供基本功能，并向RequestEntity主体添加额外的参数。

## 7. 自定义令牌响应处理

现在，我们将自定义处理令牌响应。

我们可以使用默认的令牌响应转换器[OAuth2AccessTokenResponseHttpMessageConverter](https://github.com/spring-projects/spring-security/blob/master/oauth2/oauth2-core/src/main/java/org/springframework/security/oauth2/core/http/converter/OAuth2AccessTokenResponseHttpMessageConverter.java#L134)作为起点。

**我们将实现CustomTokenResponseConverter以不同方式处理“scope”参数**：

```java
public class CustomTokenResponseConverter implements Converter<Map<String, String>, OAuth2AccessTokenResponse> {
    private static final Set<String> TOKEN_RESPONSE_PARAMETER_NAMES = Stream.of(
          OAuth2ParameterNames.ACCESS_TOKEN,
          OAuth2ParameterNames.TOKEN_TYPE,
          OAuth2ParameterNames.EXPIRES_IN,
          OAuth2ParameterNames.REFRESH_TOKEN,
          OAuth2ParameterNames.SCOPE).collect(Collectors.toSet());

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, String> tokenResponseParameters) {
        String accessToken = tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);

        Set<String> scopes = Collections.emptySet();
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
            String scope = tokenResponseParameters.get(OAuth2ParameterNames.SCOPE);
            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, ","))
                  .collect(Collectors.toSet());
        }

        //...
        return OAuth2AccessTokenResponse.withToken(accessToken)
              .tokenType(accessTokenType)
              .expiresIn(expiresIn)
              .scopes(scopes)
              .refreshToken(refreshToken)
              .additionalParameters(additionalParameters)
              .build();
    }
}
```

令牌响应转换器将Map转换为OAuth2AccessTokenResponse。

在此示例中，我们将“scope”参数解析为逗号分隔而不是空格分隔的字符串。

让我们通过使用LinkedIn作为授权服务器自定义令牌响应来演示另一个实际示例。

### 7.1 LinkedIn令牌响应处理

最后，让我们看看如何处理[LinkedIn](https://docs.microsoft.com/en-us/linkedin/shared/authentication/token-introspection?tabs=http)令牌响应。这仅包含access_token和expires_in，但我们还需要token_type。

我们可以简单地实现自己的令牌响应转换器并手动设置token_type：

```java
public class LinkedinTokenResponseConverter implements Converter<Map<String, String>, OAuth2AccessTokenResponse> {

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, String> tokenResponseParameters) {
        String accessToken = tokenResponseParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
        long expiresIn = Long.valueOf(tokenResponseParameters.get(OAuth2ParameterNames.EXPIRES_IN));

        OAuth2AccessToken.TokenType accessTokenType = OAuth2AccessToken.TokenType.BEARER;

        return OAuth2AccessTokenResponse.withToken(accessToken)
              .tokenType(accessTokenType)
              .expiresIn(expiresIn)
              .build();
    }
}
```

## 8. 总结

在本文中，我们学习了如何通过添加或修改请求参数来自定义OAuth2授权和令牌请求。