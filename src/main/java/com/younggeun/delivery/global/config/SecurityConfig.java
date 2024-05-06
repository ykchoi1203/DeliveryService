package com.younggeun.delivery.global.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.younggeun.delivery.global.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity  // 시큐리티 활성화 -> 기본 스프링 필터 체인에 등록
public class SecurityConfig {
  private final JwtAuthenticationFilter authenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter authenticationFilter) {
    this.authenticationFilter = authenticationFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .cors(AbstractHttpConfigurer::disable)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeHttpRequests(authorizeRequest
            -> authorizeRequest
            .requestMatchers(
                antMatcher("/swagger-ui/**"),
                antMatcher("/swagger-ui.html"),
                antMatcher("/v3/api-docs/**"),
                antMatcher("/swagger-resources/**")
            ).permitAll()
            .requestMatchers(
                antMatcher("/**/signin")
            ).permitAll()
            .requestMatchers(
                antMatcher("/**/signup")
            ).permitAll()
            .requestMatchers(
                antMatcher("/users/**")
            ).hasAuthority("ROLE_USER")
            .requestMatchers(
                antMatcher("/partners/**")
            ).hasAuthority("ROLE_PARTNER")
            .requestMatchers(
                antMatcher("/admin/**")
            ).hasAuthority("ROLE_ADMIN")
            .anyRequest().permitAll()
            .and()
            .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class));

    return http.build();
  }

}
