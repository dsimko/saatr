package org.jboss.qa.tool.saatr.web.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.qa.tool.saatr.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAuthenticationFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(BasicAuthenticationFilter.class);
    private String username;
    private String password;
    private boolean authDisabled;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Properties properties = IOUtils.loadFromClassPath("application.properties");
        username = properties.getProperty("web.auth.username");
        password = properties.getProperty("web.auth.password");
        authDisabled = Boolean.valueOf(properties.getProperty("web.auth.disabled"));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (authDisabled) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null) {
                StringTokenizer st = new StringTokenizer(authHeader);
                if (st.hasMoreTokens()) {
                    String basic = st.nextToken();
                    if (basic.equalsIgnoreCase("Basic")) {
                        try {
                            String credentials = new String(Base64.getDecoder().decode(st.nextToken()), "UTF-8");
                            LOG.trace("Credentials: " + credentials);
                            int p = credentials.indexOf(":");
                            if (p != -1) {
                                String _username = credentials.substring(0, p).trim();
                                String _password = credentials.substring(p + 1).trim();
                                if (!username.equals(_username) || !password.equals(_password)) {
                                    unauthorized(response, "Bad credentials");
                                } else {
                                    filterChain.doFilter(servletRequest, servletResponse);
                                }
                            } else {
                                unauthorized(response, "Invalid authentication token");
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new Error("Couldn't retrieve authentication", e);
                        }
                    }
                }
            } else {
                unauthorized(response);
            }
        }
    }

    @Override
    public void destroy() {
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"Protected\"");
        response.sendError(401, message);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        unauthorized(response, "Unauthorized");
    }

}