package cn.tuyucheng.taketoday.openid.oidc.jwtauthorities.config;

import cn.tuyucheng.taketoday.openid.oidc.jwtauthorities.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomJwtAuthenticationConverterUnitTest {

    @Test
    void testGivenCustomJwtAuthenticationConverter_whenConvert_thenReturnAccountToken() {

        AccountService accountService = new AccountService();
        MappingJwtGrantedAuthoritiesConverter authoritiesConverter = new MappingJwtGrantedAuthoritiesConverter(new HashMap<>());

        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter(
              accountService, authoritiesConverter, null);

        Jwt jwt = Jwt.withTokenValue("NOTUSED")
              .header("typ", "JWT")
              .subject("user")
              .claim("scp", "openid email profile")
              .build();

        Object auth = converter.convert(jwt);
        assertTrue(auth instanceof AccountToken, "Authentication must be instance of AccountToken");
        AccountToken token = AccountToken.class.cast(auth);

        assertEquals("user", token.getName());
    }

    @Test
    void testGivenCustomPrincipalClaimName_whenConvert_thenReturnAccountToken() {

        AccountService accountService = new AccountService();
        MappingJwtGrantedAuthoritiesConverter authoritiesConverter = new MappingJwtGrantedAuthoritiesConverter(new HashMap<>());

        CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter(
              accountService, authoritiesConverter, "preferred_username");

        Jwt jwt = Jwt.withTokenValue("NOTUSED")
              .header("typ", "JWT")
              .claim("preferred_username", "user")
              .claim("scp", "openid email profile")
              .build();

        Object auth = converter.convert(jwt);
        assertTrue(auth instanceof AccountToken, "Authentication must be instance of AccountToken");
        AccountToken token = AccountToken.class.cast(auth);

        assertEquals("user", token.getName());
    }

}
