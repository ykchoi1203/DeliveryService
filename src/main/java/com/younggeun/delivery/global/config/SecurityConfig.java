package com.younggeun.delivery.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity  // 시큐리티 활성화 -> 기본 스프링 필터 체인에 등록
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(Customizer.withDefaults())
        .authorizeHttpRequests(authorizeRequest
            -> authorizeRequest.
            requestMatchers(
                AntPathRequestMatcher.antMatcher("/**/signin")
            ).permitAll()
            .requestMatchers(
                AntPathRequestMatcher.antMatcher("/**/signup")
            ).permitAll()
            .requestMatchers(
                AntPathRequestMatcher.antMatcher("/user/**")
            ).hasAuthority("USER")
            .requestMatchers(
                AntPathRequestMatcher.antMatcher("/partner/**")
            ).hasAuthority("PARTNER")
            .requestMatchers(
                AntPathRequestMatcher.antMatcher("/admin/**")
            ).hasAuthority("ADMIN")
            .anyRequest().permitAll());

    return http.build();
  }
}
