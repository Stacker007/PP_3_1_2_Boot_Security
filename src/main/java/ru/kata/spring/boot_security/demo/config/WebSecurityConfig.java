package ru.kata.spring.boot_security.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(auth -> auth.antMatchers("/", "/index", "/css/**", "/js/**"
//                        ,"/admin/**"
        ).permitAll().antMatchers("/admin/**").hasRole("ADMIN").antMatchers("/user/**").hasAnyRole("USER", "ADMIN").anyRequest().authenticated()).formLogin(form -> form.loginPage("/login").successHandler(successUserHandler).permitAll()).logout().permitAll();
    }

}