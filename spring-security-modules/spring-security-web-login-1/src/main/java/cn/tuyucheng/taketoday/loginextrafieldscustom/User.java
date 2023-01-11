package cn.tuyucheng.taketoday.loginextrafieldscustom;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class User extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private String domain;

    public User(String username, String domain, String password, boolean enabled,
                boolean accountNonExpired, boolean credentialsNonExpired,
                boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }
}
