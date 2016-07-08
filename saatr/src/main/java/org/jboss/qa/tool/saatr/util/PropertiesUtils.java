package org.jboss.qa.tool.saatr.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Utility class for loading configuration.
 * 
 * @author dsimko@redhat.com
 *
 */
public class PropertiesUtils {

    /**
     * Loads properties from a classpath resource
     *
     * @param resource
     * @return loaded properties
     */
    public static Properties loadFromClassPath(String resource) {
        URL url = PropertiesUtils.class.getClassLoader().getResource(resource);
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
