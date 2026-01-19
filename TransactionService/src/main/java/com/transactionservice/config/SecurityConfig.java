package com.transactionservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	
	
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//    	System.out.println("Doing Authentication in security Filter Chain");
//    	return  httpSecurity.csrf(csrf->csrf.disable())
//                .authorizeHttpRequests(request-> request.anyRequest().authenticated())
//                .httpBasic(Customizer.withDefaults()).build();
//    }
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	    return http
	        .csrf(csrf -> csrf.disable())

	        // 🔥 THIS ENABLES CORS AT SECURITY LEVEL
	        .cors(Customizer.withDefaults())

	        .authorizeHttpRequests(auth -> auth
	            // 🔥 VERY IMPORTANT: allow preflight
	            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

	            // your actual API
	            .requestMatchers("/transaction-service/create/txn").authenticated()

	            .anyRequest().authenticated()
	        )
	        .httpBasic(Customizer.withDefaults())
	        .build();
	}
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

	    CorsConfiguration config = new CorsConfiguration();

	    config.setAllowedOrigins(List.of("http://localhost:8081"));
	    config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));

	    // 🔥 THIS IS THE REAL FIX
	    config.setAllowedHeaders(List.of(
	            "Authorization",
	            "Content-Type",
	            "Accept"
	    ));

	    config.setAllowCredentials(false);

	    UrlBasedCorsConfigurationSource source =
	            new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return source;
	}


    @Bean
    UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}