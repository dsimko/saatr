package org.jboss.qa.tool.saatr.web;

import java.net.URL;
import java.util.Properties;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.bson.Document;
import org.jboss.qa.tool.saatr.util.PropertiesUtils;
import org.jboss.qa.tool.saatr.web.component.URLConverter;
import org.jboss.qa.tool.saatr.web.page.ConfigPage;
import org.jboss.qa.tool.saatr.web.page.DocumentPage;
import org.jboss.qa.tool.saatr.web.page.InfoPage;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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
    private MongoCollection<Document> mongoCollection;

    @Override
    public Class<? extends Page> getHomePage() {
        return ConfigPage.class;
    }

    @Override
    protected void init() {
        super.init();

        Properties properties = PropertiesUtils.loadFromClassPath("application.properties");
        configFolderPath = properties.getProperty("config.folder.path");
        mongoClient = new MongoClient(properties.getProperty("mongo.host"), Integer.parseInt(properties.getProperty("mongo.port")));
        MongoDatabase database = mongoClient.getDatabase(properties.getProperty("mongo.database.name"));
        mongoCollection = database.getCollection(properties.getProperty("mongo.collection.name"));

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

    public MongoCollection<Document> getMongoCollection() {
        return mongoCollection;
    }
}
