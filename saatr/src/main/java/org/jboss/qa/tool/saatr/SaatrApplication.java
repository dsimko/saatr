
package org.jboss.qa.tool.saatr;

import java.net.URL;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.CharEncoding;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.User.Role;
import org.jboss.qa.tool.saatr.repo.UserRepository;
import org.jboss.qa.tool.saatr.repo.build.ConsoleTextRepository;
import org.jboss.qa.tool.saatr.security.BasicAuthInterceptor;
import org.jboss.qa.tool.saatr.web.comp.URLConverter;
import org.jboss.qa.tool.saatr.web.comp.build.ConsoleTextResource;
import org.jboss.qa.tool.saatr.web.page.AdminPage;
import org.jboss.qa.tool.saatr.web.page.AggregationPage;
import org.jboss.qa.tool.saatr.web.page.BuildPage;
import org.jboss.qa.tool.saatr.web.page.ConfigPage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mongodb.MongoClient;

/**
 * Application configuration to connect to a MongoDB and using a {@link MongoClient}. Also
 * enables Spring Data repositories for MongoDB.
 * 
 * @author dsimko@redhat.com
 */
@SpringBootApplication
@EnableScheduling
public class SaatrApplication extends WebApplication {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConsoleTextRepository consoleTextrepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SaatrApplication.class, args);
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return BuildPage.class;
    }

    @Override
    protected void init() {
        super.init();

        mountPage("config", ConfigPage.class);
        mountPage("admin", AdminPage.class);
        mountPage("aggregation", AggregationPage.class);
        mountResource(ConsoleTextResource.PATH + "${" + ConsoleTextResource.ID + "}", new ConsoleTextResource(consoleTextrepository));

        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setDefaultMarkupEncoding(CharEncoding.UTF_8);
        getRequestCycleSettings().setGatherExtendedBrowserInfo(true);

        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator defaultLocator = new ConverterLocator();
        defaultLocator.set(URL.class, new URLConverter());
        return defaultLocator;
    }

    public static SaatrApplication get() {
        return (SaatrApplication) WebApplication.get();
    }

    @Bean
    @ConfigurationProperties(prefix = "security")
    public BasicAuthInterceptor interceptor() {
        return new BasicAuthInterceptor();
    }

    @Bean
    InitializingBean populateTestUsers(UserRepository repository) {
        return () -> {
            if (repository.count() == 0L) {
                interceptor().getUsers().entrySet().stream().forEach(e -> {
                    User user = new User();
                    user.setUsername(e.getKey());
                    user.setPassword(bCryptPasswordEncoder.encode(e.getValue()));
                    user.getRoles().add(Role.User);
                    repository.save(user);
                });
            }
        };
    }

}
