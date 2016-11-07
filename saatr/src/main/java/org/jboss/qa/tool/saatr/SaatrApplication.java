
package org.jboss.qa.tool.saatr;

import java.net.URL;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.CharEncoding;
import org.jboss.qa.tool.saatr.domain.User;
import org.jboss.qa.tool.saatr.domain.User.Role;
import org.jboss.qa.tool.saatr.repo.UserRepository;
import org.jboss.qa.tool.saatr.repo.build.ConsoleTextRepository;
import org.jboss.qa.tool.saatr.web.AuthenticationSession;
import org.jboss.qa.tool.saatr.web.comp.URLConverter;
import org.jboss.qa.tool.saatr.web.comp.build.ConsoleTextResource;
import org.jboss.qa.tool.saatr.web.page.AdminPage;
import org.jboss.qa.tool.saatr.web.page.AggregationPage;
import org.jboss.qa.tool.saatr.web.page.BuildPage;
import org.jboss.qa.tool.saatr.web.page.ConfigPage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mongodb.MongoClient;

/**
 * Application configuration to connect to a MongoDB and using a {@link MongoClient}. Also
 * enables Spring Data repositories for MongoDB.
 * 
 * @author dsimko@redhat.com
 */
@SpringBootApplication
@EnableScheduling
public class SaatrApplication extends AuthenticatedWebApplication {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConsoleTextRepository consoleTextrepository;

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;

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

        MetaDataRoleAuthorizationStrategy.authorize(AdminPage.class, Role.Admin.name());
        MetaDataRoleAuthorizationStrategy.authorize(AggregationPage.class, Role.Admin.name());

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
    InitializingBean createAdminUser(UserRepository repository) {
        return () -> {
            User user = repository.findByUsername(username);
            if (user == null) {
                repository.createUser(username, password, Role.User, Role.Admin);
            }
        };
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return AuthenticationSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return BuildPage.class;
    }

}
