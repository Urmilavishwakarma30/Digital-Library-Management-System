package com.library.digitallibrary.config;

import com.library.digitallibrary.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
              
            .csrf(csrf -> csrf.disable())
        
            .authenticationProvider(authenticationProvider())

            .authorizeHttpRequests(auth -> auth

            	    .requestMatchers(
            	        "/register",
            	        "/login",
            	        "/css/**",
            	        "/js/**",
            	        "/images/**"
            	    ).permitAll()

            	    // Book management - ADMIN only
            	    .requestMatchers(
            	        "/books/add",
            	        "/books/save",
            	        "/books/edit/**",
            	        "/books/delete/**"
            	    ).hasRole("ADMIN")

            	    // Admin pages - ADMIN only
            	    .requestMatchers("/admin/**").hasRole("ADMIN")

            	    // Other authenticated features
            	    .anyRequest().authenticated()
            	)

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/books", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}