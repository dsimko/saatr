package org.jboss.qa.tool.saatr.web;

import java.net.URL;
import java.util.Properties;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.util.PropertiesUtils;
import org.jboss.qa.tool.saatr.web.component.URLConverter;
import org.jboss.qa.tool.saatr.web.page.ConfigPage;
import org.jboss.qa.tool.saatr.web.page.DocumentPage;
import org.jboss.qa.tool.saatr.web.page.InfoPage;
import org.jboss.qa.tool.saatr.web.page.ListPage;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

/**
 * Application object for the web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @author dsimko@redhat.com
 *
 */
public class WicketApplication extends WebApplication {

    private String configFolderPath;
    private MongoClient mongoClient;
    private Datastore datastore;

    @Override
    public Class<? extends Page> getHomePage() {
        return ListPage.class;
    }

    @Override
    protected void init() {
        super.init();

        Properties properties = PropertiesUtils.loadFromClassPath("application.properties");
        configFolderPath = properties.getProperty("config.folder.path");
        mongoClient = new MongoClient(properties.getProperty("mongo.host"), Integer.parseInt(properties.getProperty("mongo.port")));
        final Morphia morphia = new Morphia();
        morphia.mapPackage(Build.class.getPackage().getName());
        datastore = morphia.createDatastore(mongoClient, properties.getProperty("mongo.database.name"));
        datastore.ensureIndexes();

        mountPage("config", ConfigPage.class);
        mountPage("doc", DocumentPage.class);
        mountPage("info", InfoPage.class);
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator defaultLocator = new ConverterLocator();
        defaultLocator.set(URL.class, new URLConverter());
        return defaultLocator;
    }

    @Override
    protected void onDestroy() {
        mongoClient.close();
    }

    public static WicketApplication get() {
        return (WicketApplication) WebApplication.get();
    }

    public String getConfigFolderPath() {
        return configFolderPath;
    }

    public void setConfigFolderPath(String configFolderPath) {
        this.configFolderPath = configFolderPath;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
