package org.jboss.qa.tool.saatr.jenkins.miner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static String JENKINS_PATH_REGEX = ".*/job/(.*)/(\\d+)/?";
    private static Pattern JENKINS_PATH_PATTERN = Pattern.compile(JENKINS_PATH_REGEX);

    public static String getJobName(URL jenkinsBuildUrl) {
        return getMatched(jenkinsBuildUrl, 1);
    }

    public static Long getBuildNumber(URL jenkinsBuildUrl) {
        return Long.parseLong(getMatched(jenkinsBuildUrl, 2));
    }

    public static Integer getPort(URL jenkinsBuildUrl) {
        int port = jenkinsBuildUrl.getPort();
        if (port > 0) {
            return port;
        }
        return jenkinsBuildUrl.getProtocol().equals("https") ? 443 : 80;
    }

    private static String getMatched(URL jenkinsBuildUrl, int group) {
        String path = jenkinsBuildUrl.getPath();
        Matcher matcher = JENKINS_PATH_PATTERN.matcher(path);
        if (matcher.matches()) {
            return matcher.group(group);
        }
        throw new IllegalArgumentException("Build URL " + jenkinsBuildUrl + " doesnt match " + JENKINS_PATH_REGEX);
    }

    /**
     * Loads properties from a classpath resource
     *
     * @param resource
     * @return loaded properties
     */
    public static Properties loadFromClassPath(String resource) {
        URL url = Utils.class.getClassLoader().getResource(resource);
        if (url == null) {
            throw new IllegalStateException("Could not find classpath properties resource: " + resource);
        }
        try {
            Properties props = new Properties();
            InputStream is = url.openStream();
            try {
                props.load(url.openStream());
            } finally {
                is.close();
            }
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Could not read properties at classpath resource: " + resource, e);
        }
    }

}