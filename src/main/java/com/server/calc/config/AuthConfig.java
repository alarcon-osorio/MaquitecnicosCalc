package com.server.calc.config;

import com.server.calc.controller.auth0.LogoutHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class AuthConfig extends WebSecurityConfigurerAdapter {

    private final LogoutHandler logoutHandler;

    public AuthConfig(LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and().oauth2Login()
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .addLogoutHandler(logoutHandler);
    }
}
