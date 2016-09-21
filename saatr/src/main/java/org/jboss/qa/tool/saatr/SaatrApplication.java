
package org.jboss.qa.tool.saatr;

import java.net.URL;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.CharEncoding;
import org.jboss.qa.tool.saatr.web.comp.URLConverter;
import org.jboss.qa.tool.saatr.web.page.AggregationPage;
import org.jboss.qa.tool.saatr.web.page.BuildPage;
import org.jboss.qa.tool.saatr.web.page.ConfigPage;
import org.jboss.qa.tool.saatr.web.page.AdminPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.mongodb.MongoClient;

/**
 * Application configuration to connect to a MongoDB and using a {@link MongoClient}. Also
 * enables Spring Data repositories for MongoDB.
 * 
 * @author dsimko@redhat.com
 */
@SpringBootApplication
public class SaatrApplication extends WebApplication {

    @Autowired
    private ApplicationContext applicationContext;

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

        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setDefaultMarkupEncoding(CharEncoding.UTF_8);

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

}
