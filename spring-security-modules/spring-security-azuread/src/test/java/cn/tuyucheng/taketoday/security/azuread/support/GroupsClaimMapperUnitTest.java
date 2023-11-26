package cn.tuyucheng.taketoday.security.azuread.support;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.ClaimAccessor;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupsClaimMapperUnitTest {

   private Map<String, List<String>> g2a = new HashMap<>();

   @Test
   void testWhenNoGroupClaimsPresent_thenNoAuthoritiesAdded() {
      ClaimAccessor source = mock(ClaimAccessor.class);
      GroupsClaimMapper mapper = new GroupsClaimMapper("ROLE", "group", g2a);

      Collection<? extends GrantedAuthority> authorities = mapper.mapAuthorities(source);
      assertThat(authorities)
            .isNotNull()
            .isEmpty();
   }

   @Test
   void testWhenEmptyGroupClaimsPresent_thenNoAuthoritiesAdded() {
      ClaimAccessor source = mock(ClaimAccessor.class);
      when(source.getClaimAsStringList("group"))
            .thenReturn(Collections.emptyList());

      GroupsClaimMapper mapper = new GroupsClaimMapper("ROLE", "group", g2a);

      Collection<? extends GrantedAuthority> authorities = mapper.mapAuthorities(source);
      assertThat(authorities)
            .isNotNull()
            .isEmpty();
   }
}