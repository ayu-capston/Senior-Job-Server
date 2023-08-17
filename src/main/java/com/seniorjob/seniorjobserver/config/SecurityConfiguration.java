package com.seniorjob.seniorjobserver.config;

import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/users/join", "/api/users/all", "/api/users/detail").permitAll()
                .antMatchers("/api/users/login", "/api/users/update").authenticated()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .csrf().disable()
                .logout()
                .logoutUrl("/api/users/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> {
                    try {
                        // 로그인 중인 회원이 없는 경우
                        if (authentication == null || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal().toString())) {
                            response.setCharacterEncoding("UTF-8");
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_OK);
                            PrintWriter out = response.getWriter();
                            out.println("{\"message\":\"로그인 중인 회원이 없습니다.\"}");
                            return;
                        }

                        request.getSession().invalidate();
                        String phoneNumber = ((UserDetails) authentication.getPrincipal()).getUsername();

                        UserEntity user = userRepository.findByPhoneNumber(phoneNumber)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + phoneNumber));

                        String message = user.getName() + " 회원님 로그아웃에 성공하였습니다.";

                        response.setCharacterEncoding("UTF-8");
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_OK);
                        PrintWriter out = response.getWriter();
                        out.println("{\"message\":\"" + message + "\"}");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
