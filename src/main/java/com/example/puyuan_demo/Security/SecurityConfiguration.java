package com.example.puyuan_demo.Security;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                //CSRF不啟用
                .csrf().disable()
                // 定義哪些url需要被保護
                .authorizeHttpRequests()
                //除了這個位置的api外，其他都須得到身分驗證才可以進入
                //此類型的API都是可以訪問的    permitAll()任何人都可以訪問
                .requestMatchers(
                    "/api/v1/auth/**",
                    //顯示錯誤訊息
                    "/error/**")
                .permitAll()
                // 其他尚未匹配到的url都需要身份驗證
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authenticationProvider)
                // 添加過濾器，針對其他請求進行JWT的驗證
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}
