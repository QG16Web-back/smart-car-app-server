package com.qg.smart.car.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**Spring Security.
 * @author 小排骨
 * Date 2017/9/5
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll();
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth)
            throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("mins").password("Mins1205").roles("USER")
                .and()
                .withUser("livid").password("123456").roles("USER")
                .and()
                .withUser("hunger").password("123456").roles("USER");
    }
}
