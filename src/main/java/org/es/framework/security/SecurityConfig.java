package org.es.framework.security;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private UserDetailsService userDetailsService;

	// @Resource
	private MenuAuthenticationFilter menuAuthenticationFilter;

	@Resource
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Resource
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Resource
	private MyPasswordEncoder myPasswordEncoder;

	@Value("${essecurity.failureLoginUrl}")
	private String failureLoginUrl;

	@Value("${essecurity.loginPage}")
	private String loginPage;

	@Value("${essecurity.loginProcessingUrl}")
	private String loginProcessingUrl;

	@Value("${essecurity.logoutSuccessUrl}")
	private String logoutSuccessUrl;

	@Value("${essecurity.logoutUrl}")
	private String logoutUrl;

	@Value("${essecurity.failureRedirectUrl}")
	private String failureRedirectUrl;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter(loginProcessingUrl);
		loginAuthenticationFilter.setAuthenticationManager(super.authenticationManager());
		loginAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
		loginAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		loginAuthenticationFilter.setUserDetailsService(userDetailsService);
		loginAuthenticationFilter.afterPropertiesSet();
		loginAuthenticationFilter.setMyPasswordEncoder(myPasswordEncoder);
		LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(
				loginPage);
		http.csrf().disable();
		http.headers().frameOptions().disable();
		http.addFilterBefore(loginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).authorizeRequests()
				.antMatchers("/pages/login.html", "/pages/accessDeny.html", "/pages/css/**", "/pages/codebase/**",
						"/pages/js/**", "/pages/imgs/**","/rest/**","/test/**","/hcwsystem/**")
				.permitAll().anyRequest().fullyAuthenticated().and().formLogin().loginProcessingUrl(loginProcessingUrl)
				.loginPage(loginPage).failureUrl(failureLoginUrl).permitAll().and().logout().logoutUrl(logoutUrl)
				.deleteCookies("remember-me").logoutSuccessUrl(logoutSuccessUrl).permitAll().and().rememberMe().and()
				.exceptionHandling().accessDeniedPage(failureRedirectUrl)
				.authenticationEntryPoint(loginUrlAuthenticationEntryPoint);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

}