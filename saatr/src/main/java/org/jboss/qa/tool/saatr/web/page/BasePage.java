package org.jboss.qa.tool.saatr.web.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.model.IModel;

/**
 * Base page for all web pages.
 * 
 * @author dsimko@redhat.com
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class BasePage<T> extends GenericWebPage<T> {

    public BasePage() {
    }

    public BasePage(IModel<T> model) {
        super(model);
    }

    protected void redirectToHomePage() {
        throw new RestartResponseException(getApplication().getHomePage());
    }

}
