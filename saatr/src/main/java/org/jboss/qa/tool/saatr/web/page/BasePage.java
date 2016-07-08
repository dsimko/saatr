package org.jboss.qa.tool.saatr.web.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.GenericWebPage;

/**
 * Base page for all web pages.
 * 
 * @author dsimko@redhat.com
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class BasePage<T> extends GenericWebPage<T> {

    protected void redirectToHomePage() {
        throw new RestartResponseException(getApplication().getHomePage());
    }

}
