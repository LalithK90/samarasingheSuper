package lk.samarasingher_super.configuration;


import lk.samarasingher_super.asset.user_management.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final String[] ALL_PERMIT_URL = {"/favicon.ico", "/img/**", "/css/**", "/js/**", "/webjars/**",
      "/login", "/select/**", "/", "/index"};
private final String[] ADMIN = { "/role/**", "/user/**","/employee/**"};
 private final String[] MANAGER = {"/report/manager/**","/customer/**","/employee/**","/invoice/**","/goodReceivedNote/**","/employee/**","/item/**","/ledger/**","/payment/**","/purchaseOrder/**","/role/**","/supplier/**","/user/**"};
  private final String[] PROCUREMENT_MANAGER = {"/category/**", "/goodReceivedNote/**", " /item/**",
      "/ledger/**", "/purchaseOrder/**",
      "/supplier/**", "/supplierItem/**","/discountRatio/**","/goodReceivedNote","/goodReceivedNote/{id}","/invoice/search","/invoice/remove/{id}","/invoice/{id}","/item","/item/add","item/delete/{id}","/item/edit/{id}","/item/{id}","/ledger","/ledger/expiredDate","/ledger/reorderPoint","/ledger/{id}","/purchaseOrder/**","/supplier",};
  private final String[] ACCOUNT_MANAGER = {"/payment/**", "/invoice/**"};
  private final String[] HR_MANAGER = {"/employee/**","/purchaseOrder/**","/user/**"};
  private final String[] CASHIER = {"/category/getCategory/**", "/invoice/add", "/ledger","/customer","/customer/add","/customer/{id}","/invoice/add","/invoice/search","/item","/item/{id}","/ledger","/ledger/reorderPoint","/ledger/expiredDate","/ledger/{id}"};

  @Bean
  public UserDetailsServiceImpl userDetailsService() {
    return new UserDetailsServiceImpl();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /*Session management - bean start*/
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }
  /*Session management - bean end*/

  @Bean
  public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
    return new CustomAuthenticationSuccessHandler();
  }

  @Bean
  public LogoutSuccessHandler customLogoutSuccessHandler() {
    return new CustomLogoutSuccessHandler();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
/*   http.csrf().disable();
    http.authorizeRequests().antMatchers("/").permitAll();*/
    // For developing easy to give permission all lin


    http.authorizeRequests(
        authorizeRequests ->
            authorizeRequests
                //Anytime users can access without login
                //to see actuator details
                .antMatchers(ALL_PERMIT_URL).permitAll()
                //this is used the normal admin to give access every url mapping
                .antMatchers(ADMIN).hasAnyRole("ADMIN")
                //Need to login for access those are
                .antMatchers(MANAGER).hasAnyRole("MANAGER")
                .antMatchers(PROCUREMENT_MANAGER).hasAnyRole("PROCUMENT_MANAGER")
                .antMatchers(ACCOUNT_MANAGER).hasAnyRole("ACCOUNT_MANAGER")
                .antMatchers(HR_MANAGER).hasAnyRole("HR_MANAGER")
                .antMatchers(CASHIER).hasAnyRole("CASHIER")
                .anyRequest()
                .authenticated())
        // Login form
        .formLogin(
            formLogin ->
                formLogin
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    //Username and password for validation
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler(customAuthenticationSuccessHandler())
                    .failureUrl("/login?error")
                  )
        //Logout controlling
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessHandler(customLogoutSuccessHandler())
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true))
        //session management
        .sessionManagement(
            sessionManagement ->
                sessionManagement
                    .sessionFixation().migrateSession()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/login")
                    .maximumSessions(6)
                    .expiredUrl("/logout")
                    .sessionRegistry(sessionRegistry()))
        .rememberMe().key("uniqueAndSecret").tokenValiditySeconds(86400)
        .and()
        //Cross site disable
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling();

  }
}

