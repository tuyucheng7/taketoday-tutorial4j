package cn.tuyucheng.taketoday.springsecuredsockets.transfer.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private cn.tuyucheng.taketoday.springsecuredsockets.domain.User user;

    public CustomUserDetails(cn.tuyucheng.taketoday.springsecuredsockets.domain.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), authorities);
        this.user = user;
    }

    public CustomUserDetails(cn.tuyucheng.taketoday.springsecuredsockets.domain.User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = user;
    }

    public cn.tuyucheng.taketoday.springsecuredsockets.domain.User getUser() {
        return user;
    }
}