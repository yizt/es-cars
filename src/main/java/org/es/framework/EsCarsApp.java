
package org.es.framework;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.es.framework.config.DefaultProfileUtil;
import org.es.framework.config.FrameworkConstants;
import org.es.framework.mvc.ExtMessageSource;
import org.es.framework.mvc.ExtResourceBundleMessageSource;
import org.es.framework.mvc.exception.AjaxRequestExceptionResolver;
import org.es.framework.mvc.interceptor.CrossDomainHeaderInterceptor;
import org.es.framework.mvc.interceptor.HeaderCommonFilter;
import org.es.framework.mvc.interceptor.LocaleChangeInterceptor;
import org.es.framework.mvc.validation.ValidatorUtil;
import org.es.framework.util.json.PojoMapper;
import org.es.framework.utils.ConstantFunction;
import org.es.framework.utils.EsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import nz.net.ultraq.thymeleaf.LayoutDialect;

@ComponentScan(basePackages = { "org.es.framework", "org.es.framework.security", "org.es.cars"})
@EnableAutoConfiguration
@EntityScan(basePackages = { "org.es.framework.domain", "org.es.cars.domain" })
@EnableJpaRepositories(value = { "org.es.framework.repository", "org.es.cars.repository" })
@ImportResource("classpath*:base-config.xml")
@Configuration
public class EsCarsApp {
	
	private static final Logger log = LoggerFactory.getLogger(EsCarsApp.class);

	@Resource
	private Environment env;
	
	@PostConstruct
	public void initApplication() {
		log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
		Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
		if (activeProfiles.contains(FrameworkConstants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(FrameworkConstants.SPRING_PROFILE_TEST)
				&& activeProfiles.contains(FrameworkConstants.SPRING_PROFILE_PRODUCTION)) {
			log.error("You have misconfigured your application! It should not run "
					+ "with both the 'dev' and 'prod' profiles at the same time.");
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		SpringApplication app = new SpringApplication(EsCarsApp.class);
		DefaultProfileUtil.addDefaultProfile(app);
		Environment env = app.run(args).getEnvironment();
		log.info(
				"\n----------------------------------------------------------\n\t"
						+ "Application '{}' is running! Access URLs:\n\t" + "Local: \t\thttp://localhost:{}\n\t"
						+ "External: \thttp://{}:{}\n----------------------------------------------------------",
				env.getProperty("spring.application.name"), env.getProperty("server.port"),
				InetAddress.getLocalHost().getHostAddress(), env.getProperty("server.port"));
	}

	@Bean
	public WebMvcConfigurerAdapter adapter() {

		return new WebMvcConfigurerAdapter() {

			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new CrossDomainHeaderInterceptor());
				registry.addInterceptor(new LocaleChangeInterceptor());
				super.addInterceptors(registry);
			}

			@Override
			public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
				exceptionResolvers.add(new AjaxRequestExceptionResolver());
				super.configureHandlerExceptionResolvers(exceptionResolvers);
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry.addResourceHandler("/pages/**").addResourceLocations("/pages/");
			}

		};
	}
	
	@Bean
	public FilterRegistrationBean someFilterRegistration() {
	    FilterRegistrationBean registration = new FilterRegistrationBean();
	    registration.setFilter(new HeaderCommonFilter());
	    registration.addUrlPatterns("/*");
	    registration.setName("HeaderCommonFilter");
	    registration.setOrder(1);
	    return registration;
	} 

	@Bean
	public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = PojoMapper.getObjectMapper();
		jsonConverter.setObjectMapper(objectMapper);
		return jsonConverter;
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
		sessionLocaleResolver.setDefaultLocale(new Locale("zh", "CN"));
		return sessionLocaleResolver;
	}

	@Bean
	public ExtMessageSource extMessageSource() {
		return new ExtMessageSource();
	}

	@Bean(name = { "messageSource" })
	public ResourceBundleMessageSource extResourceBundleMessageSource() {
		ExtResourceBundleMessageSource extResourceBundleMessageSource = new ExtResourceBundleMessageSource();
		extResourceBundleMessageSource.setBasenames("messages/messages", "messages/ValidationMessages");
		return extResourceBundleMessageSource;
	}

	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}

	@Bean
	public ValidatorUtil validatorUtil() {
		return new ValidatorUtil();
	}

	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		return localValidatorFactoryBean;
	}

	@Bean
	public EsConstants constants() {
		return new EsConstants();
	}

	@Bean
	public ConstantFunction constantFunction() {
		return new ConstantFunction();
	}

}
