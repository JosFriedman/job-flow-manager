package gov.nyc.doitt.jobstatemanager.security;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;

/**
 * Customization of Spring security resources
 */
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "SPRING_REST_API";

	@Autowired
	private AuthParamsExtractor authParamsExtractor;

	@Autowired
	private JobAuthenticationManager jobAuthenticationManager;

	@Autowired
	private OAuth2WebSecurityExpressionHandler expressionHandler;

	@Autowired
	private JobAuthenticationExceptionHandler jobAuthenticationExceptionHandler;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID).stateless(false);
		resources.tokenExtractor(authParamsExtractor);
		resources.authenticationManager(jobAuthenticationManager);
		resources.expressionHandler(expressionHandler);
		resources.authenticationEntryPoint(jobAuthenticationExceptionHandler);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/**").access("@jobAuthorizer.checkRequest(request)");
		http.exceptionHandling().accessDeniedHandler(jobAuthenticationExceptionHandler);
	}

	@Bean
	public JobAuthorizer jobAuthorizer() {
		return new JobAuthorizer();
	}

	@Bean
	public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
		OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;
	}

	@Bean
	public OAuth2ExceptionRenderer OAuth2ExceptionRenderer() {
		return new DefaultOAuth2ExceptionRenderer();
	}

	@Bean
	public StandardPBEStringEncryptor standardPBEStringEncryptor() {

		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword("jasypt"); // set jasypt password
		return encryptor;
	}

}