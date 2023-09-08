package com.project.openrun.global.config;


import com.project.openrun.auth.filter.JwtAuthenticationFilter;
import com.project.openrun.auth.filter.JwtAuthorizationFilter;
import com.project.openrun.auth.jwt.JwtUtil;
import com.project.openrun.auth.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf((csrf) -> csrf.disable());

        httpSecurity.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        httpSecurity.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/health-check").permitAll()
                        .requestMatchers("/openrun/**").permitAll()
                        .requestMatchers("/api/members/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
        );

        httpSecurity.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
