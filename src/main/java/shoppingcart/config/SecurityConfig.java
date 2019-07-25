package shoppingcart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import shoppingcart.services.UserDetailsServiceImpl;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Configuration
    @Order(1)
    public class WebSecurityConfigRest extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();

            http.antMatcher("/api/**")
                    .exceptionHandling()
                    .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/users/{username}/roles/**", "/api/users/{username}/trust/")
                    .access("hasRole('ROLE_ADMIN') or @userSecurity.hasUsername(authentication, #username)")
//                    .and()
//                    .authorizeRequests()
//                    .antMatchers("/api/users/{username}/")
//                    .access("hasRole('ROLE_ADMIN') or @userSecurity.hasUsername(authentication, #username)")
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/users/{username}/ratings/")
                    .permitAll()
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.PUT, "/api/users/{username}/ratings/")
                    .access("hasRole('ROLE_BUYER')")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/users/{username}/ratings/{ratingId}/")
                    .access("@userSecurity.ratingGivenByUser(authentication, #username, #ratingId)")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/trusted-users/")
                    .access("hasRole('ROLE_SELLER')")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/cart/**", "/api/checkout/")
                    .access("hasRole('ROLE_BUYER')");

        }
    }


    @Configuration
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Autowired
        private DataSource dataSource;

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            // Setting Service to find User in the database.
            // And Setting PassswordEncoder
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();

            http.authorizeRequests()
                    .antMatchers("/", "/login", "/logout")
                    .permitAll()
                    .and()
                    .authorizeRequests()
                    .antMatchers("/userInfo")
                    .access("isAuthenticated()")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/admin")
                    .access("hasRole('ROLE_ADMIN')")
                    .and()
                    .authorizeRequests()
                    .and()
                    .formLogin()
                    // Submit URL of login page.
                    .loginProcessingUrl("/j_spring_security_check") // Submit URL
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .failureUrl("/login?error=true")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    // Config for Logout Page
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/logoutSuccessful")
                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/403");

            // Config Remember Me.
            http.authorizeRequests().and()
                    .rememberMe()
                    .tokenRepository(this.persistentTokenRepository())
                    .tokenValiditySeconds(24 * 60 * 60);

        }

        @Bean
        public PersistentTokenRepository persistentTokenRepository() {
            JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
            db.setDataSource(this.dataSource);
            return db;
        }
    }

}
