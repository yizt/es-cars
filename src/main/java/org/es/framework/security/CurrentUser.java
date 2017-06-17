package org.es.framework.security;

import org.es.framework.domain.Users;
import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -2554682469236910319L;

	private Users user;
    
    private String [] roles;

    public CurrentUser(Users user, String... roles) {
        super(user.getUserName(), user.getPassword(), AuthorityUtils.createAuthorityList(roles));
        this.user = user;
        this.roles = roles;
    }

    public Users getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public String[] getRole() {
        return roles;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "user=" + user +
                "} " + super.toString();
    }
}
