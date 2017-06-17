package org.es.framework.security;

import java.util.List;

import javax.annotation.Resource;

import org.es.framework.domain.Roles;
import org.es.framework.domain.Users;
import org.es.framework.mvc.exception.EsRuntimeException;
import org.es.framework.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserDetailsService.class);
   
    @Resource
    private UsersService userService;

    @Override
    public CurrentUser loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.debug("Authenticating user with userName={}", userName);
        Users user = userService.findByUserName(userName);
        if(user == null){
        	throw new AuthenticationServiceException(String.format("%s用户不存在", userName));
        }
        List<Roles> roles = userService.findRolesByUserId(user.getId());
        String [] roleStrings = new String[roles.size()];
        for (int i = 0; i < roles.size(); i++) {
        	roleStrings[i] = roles.get(i).getCode();
		}
        return new CurrentUser(user,roleStrings);
    }

}
