package org.jboss.qa.tool.saatr.jenkins.miner;

import java.net.URL;
import java.util.Properties;

import org.jboss.qa.tool.saatr.jenkins.IJenkinsMiner;

import com.github.danielpacak.jenkins.ci.core.client.JenkinsClient;

public abstract class AbstractJenkinsMiner implements IJenkinsMiner {

    protected JenkinsClient createClient(URL buildUrl) {
        JenkinsClient client = new JenkinsClient(buildUrl.getProtocol(), buildUrl.getHost(), Utils.getPort(buildUrl), null);
        Properties prop = Utils.loadFromClassPath("auth.properties");
        client.setCredentials(prop.getProperty("user"), prop.getProperty("password"));
        return client;
    }
}
