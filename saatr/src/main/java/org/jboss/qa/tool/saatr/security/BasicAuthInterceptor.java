
package org.jboss.qa.tool.saatr.security;

import java.util.HashMap;
import java.util.Map;

public class BasicAuthInterceptor {

    private Map<String, String> users = new HashMap<String, String>();

    public Map<String, String> getUsers() {
        return users;
    }
}
