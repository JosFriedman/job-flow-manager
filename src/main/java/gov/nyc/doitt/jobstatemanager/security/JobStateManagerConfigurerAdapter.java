package gov.nyc.doitt.jobstatemanager.security;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class JobStateManagerConfigurerAdapter extends WebSecurityConfigurerAdapter {
}

/**
 * Customization of Spring security resources
 */
//@Configuration
//@EnableWebSecurity
//public class JobStateManagerConfigurerAdapter extends WebSecurityConfigurerAdapter {
//
//	@Bean
//	public JobStateManagerAuthorizer jobStateManagerAuthorizer() {
//		return new JobStateManagerAuthorizer();
//	}
//
//	@Autowired
//	private UserAccessDeniedHandler userAccessDeniedHandler;
//
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER").and().withUser("admin").password("admin")
//				.roles("USER", "ADMIN");
//		auth.inMemoryAuthentication().
//	}
//
////	@Autowired
////	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//////		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
////		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER").and().withUser("admin").password("admin")
////				.roles("USER", "ADMIN");
////	}
//
//	@Override
//	public void configure(HttpSecurity http) throws Exception {
//		http.authorizeRequests().antMatchers("/**").access("@jobStateManagerAuthorizer.checkRequest(request)");
////		http.authorizeRequests().antMatchers("/**").permitAll();
//		http.authorizeRequests().and().csrf().disable();
//		http.exceptionHandling().accessDeniedHandler(userAccessDeniedHandler);
//
//	}
//
//}