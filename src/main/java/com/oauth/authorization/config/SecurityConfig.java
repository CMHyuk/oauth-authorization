package com.oauth.authorization.config;

import com.oauth.authorization.domain.user.repository.UserInfoQueryRepository;
import com.oauth.authorization.domain.user.repository.UserInfoRepository;
import com.oauth.authorization.domain.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserInfoQueryRepository userInfoQueryRepository;
    private final UserInfoRepository userInfoRepository;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request ->
                        request.requestMatchers("/oauth/sign-in").permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
        http.addFilterBefore(new CustomAbstractAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoService(userInfoQueryRepository, userInfoRepository);
    }
}
