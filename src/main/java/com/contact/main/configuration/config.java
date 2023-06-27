package com.contact.main.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class config extends WebSecurityConfiguration{
	
	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImple();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
		
		dap.setUserDetailsService(this.getUserDetailService());
		dap.setPasswordEncoder(passwordEncoder());
		
		return dap;
	}
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     
        http.authorizeHttpRequests().requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
        .requestMatchers("/user/**").hasAuthority("User").requestMatchers("/**").permitAll().and().formLogin()
        .loginPage("/signin").loginProcessingUrl("/dologin")
        .defaultSuccessUrl("/user/index").failureUrl("/error").and().csrf().disable(); 
        http.headers().frameOptions().sameOrigin();
 
        return http.build();
    }
}
