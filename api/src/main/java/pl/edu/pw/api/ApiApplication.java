package pl.edu.pw.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@SpringBootApplication
//@EnableWebSecurity(debug = true)
//@EnableMethodSecurity(securedEnabled = true)
public class ApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}


//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		return http.csrf().disable()
//				.authorizeHttpRequests()
//				.requestMatchers("/login").permitAll()
//				.and()
//				.authorizeHttpRequests().requestMatchers("/users")
//				.authenticated().and()
//				.formLogin()
//				.and().build();
//	}
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(
						customizer -> customizer
								.requestMatchers("/auth/**").permitAll()
								.anyRequest().authenticated()
				)
				.exceptionHandling(
						customizer -> customizer
								.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				)
				// TODO: add persistence mechanism (tokens?)
				.build();
		return http.build();
	}
}
