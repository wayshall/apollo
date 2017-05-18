package com.ctrip.framework.apollo.portal.component;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@Order(99)
@Profile({"!ctrip"})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SystemUserConfigurer extends WebSecurityConfigurerAdapter {

  public static final String USER_ROLE = "user";

//  @Autowired
//  private PortalConfig portalConfig;
  @Autowired
  private DataSource datasource;
//
//  private AuthenticationManagerBuilder auth;


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.csrf().disable();
    http.headers().frameOptions().sameOrigin();
    http.authorizeRequests().anyRequest().hasAnyRole(USER_ROLE);
    http.formLogin();
    http.logout().invalidateHttpSession(true).clearAuthentication(true);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth, JdbcUserDetailsManager userDetailsService)
      throws Exception {
//    this.auth = auth;

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    auth.jdbcAuthentication().dataSource(datasource).usersByUsernameQuery(
        "select username,password, enabled from users where username=?");

//    initUsers(userDetailsService);
  }



  //init at application start. So if user information changed should restart application.
//  public void initUsers(JdbcUserDetailsManager userDetailsService) throws Exception {
//    if (auth == null) {
//      return;
//    }
//
//    Map<String, ApolloUser> users = portalConfig.systemUsers();
//
//    List<GrantedAuthority> authorities = new ArrayList<>();
//    authorities.add(new SimpleGrantedAuthority("ROLE_" + USER_ROLE));
//    PasswordEncoder encoder = new BCryptPasswordEncoder();
//
//    for (Map.Entry<String, ApolloUser> user : users.entrySet()) {
//      String userName = user.getKey();
//      if (!userDetailsService.userExists(userName)) {
//
//        User userDetails = new User(userName, encoder.encode(user.getValue().getPassword()), authorities);
//
//        userDetailsService.createUser(userDetails);
//      }
//    }
//  }

}
