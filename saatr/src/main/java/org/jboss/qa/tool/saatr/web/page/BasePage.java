package org.jboss.qa.tool.saatr.web.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.web.component.common.bootstrap.BootstrapNavbarLink;

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
        initNavbar();
    }

    public BasePage(IModel<T> model) {
        super(model);
        initNavbar();
    }

    protected void redirectToHomePage() {
        throw new RestartResponseException(getApplication().getHomePage());
    }

    private void initNavbar() {
        add(new BootstrapNavbarLink("builds", BuildPage.class, Model.of("Builds")));
        add(new BootstrapNavbarLink("config", ConfigPage.class, Model.of("Config")));
    }
}
