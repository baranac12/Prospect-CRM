package com.prospect.crm.config;

import com.prospect.crm.repository.UserRepository;
import com.prospect.crm.security.CustomUserDetailsService;
import com.prospect.crm.security.JwtAuthenticationFilter;
import com.prospect.crm.service.JwtService;
import com.prospect.crm.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   CustomUserDetailsService userDetailsService,
                                                   JwtConfig jwtConfig,
                                                   SubscriptionService subscriptionService,
                                                   UserRepository userRepository) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtService, userDetailsService, jwtConfig, subscriptionService, userRepository
        );
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/auth/**").permitAll()
                .requestMatchers("/v1/users/register").permitAll()
                .requestMatchers("/v1/oauth/login").permitAll()
                .requestMatchers("/v1/oauth/callback/login").permitAll()
                .requestMatchers("/v1/oauth/callback/**").permitAll()
                .requestMatchers("/v1/payments/success").permitAll()
                .requestMatchers("/v1/payments/cancel").permitAll()
                .requestMatchers("/v1/payments/webhook").permitAll()
                .requestMatchers("/v1/health/**").permitAll()
                .requestMatchers("/v1/test/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                .requestMatchers("/v1/logs/**").hasRole("ADMIN")
                .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/v1/system/**").hasRole("ADMIN")
                
                .requestMatchers("/v1/users/**").authenticated()
                .requestMatchers("/v1/leads/**").hasRole("USER")
                .requestMatchers("/v1/subscriptions/**").hasRole("USER")
                .requestMatchers("/v1/payments/**").hasRole("USER")
                .requestMatchers("/v1/emails/**").hasRole("USER")
                .requestMatchers("/v1/robots/**").hasRole("USER")
                
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

} 