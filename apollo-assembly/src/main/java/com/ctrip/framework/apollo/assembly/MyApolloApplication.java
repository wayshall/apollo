package com.ctrip.framework.apollo.assembly;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;

import com.ctrip.framework.apollo.adminservice.AdminServiceApplication;
import com.ctrip.framework.apollo.configservice.ConfigServiceApplication;
import com.ctrip.framework.apollo.portal.PortalApplication;
import com.google.common.collect.ImmutableSortedMap;

/***
 * program arg: --all
 * 
 * vm arg:
 * -Dapollo_profile=dev
	-Ddev_meta=http://localhost:8080/ #for portal
 * @author way
 *
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class})
public class MyApolloApplication {

  private static final Logger logger = LoggerFactory.getLogger(MyApolloApplication.class);

  public static void main(String[] args) throws Exception {
    /**
     * Common
     */
    ConfigurableApplicationContext commonContext =
        new SpringApplicationBuilder(MyApolloApplication.class).web(false).run(args);
    commonContext.addApplicationListener(new ApplicationPidFileWriter());
    logger.info(commonContext.getId() + " isActive: " + commonContext.isActive());
    
    Map<String, SpringApplicationBuilder> applicationMap = ImmutableSortedMap.of("configservice", new SpringApplicationBuilder(ConfigServiceApplication.class),
																			"adminservice", new SpringApplicationBuilder(AdminServiceApplication.class), 
																			"portal", new SpringApplicationBuilder(PortalApplication.class));

    /**
     * ConfigService
     */
    if (commonContext.getEnvironment().containsProperty("configservice")) {
      ConfigurableApplicationContext configContext = applicationMap.get("configservice")
													    		  .parent(commonContext)
													              .sources(RefreshScope.class).run(args);
      logger.info(configContext.getId() + " isActive: " + configContext.isActive());
    }

    /**
     * AdminService
     */
    if (commonContext.getEnvironment().containsProperty("adminservice")) {
      ConfigurableApplicationContext adminContext = applicationMap.get("adminservice")
													    		  .parent(commonContext)
													              .sources(RefreshScope.class).run(args);
      logger.info(adminContext.getId() + " isActive: " + adminContext.isActive());
    }

    /**
     * Portal
     */
    if (commonContext.getEnvironment().containsProperty("portal")) {
      ConfigurableApplicationContext portalContext = applicationMap.get("portal")
													    		  .parent(commonContext)
													              .sources(RefreshScope.class).run(args);
      logger.info(portalContext.getId() + " isActive: " + portalContext.isActive());
    }
    
    if(commonContext.getEnvironment().containsProperty("all")){
    	applicationMap.values().forEach(app->{
    		ConfigurableApplicationContext ctx = app.parent(commonContext).sources(RefreshScope.class).run(args);
    	    logger.info(ctx.getId() + " isActive: " + ctx.isActive());
    	});
    }


  }

}
