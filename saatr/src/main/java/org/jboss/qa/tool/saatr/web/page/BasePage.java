
package org.jboss.qa.tool.saatr.web.page;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapNavbarLink;

import lombok.extern.slf4j.Slf4j;

/**
 * Base page for all web pages.
 * 
 * @author dsimko@redhat.com
 *
 * @param <T>
 */
@SuppressWarnings("serial")
@Slf4j
public abstract class BasePage<T> extends GenericWebPage<T> {

    private static final Duration SESSION_REFRESH_INTERVAL = Duration.minutes(5);

    public BasePage() {
        initBasePage();
    }

    public BasePage(IModel<T> model) {
        super(model);
        initBasePage();
    }

    protected void redirectToHomePage() {
        throw new RestartResponseException(getApplication().getHomePage());
    }

    private void initBasePage() {
        add(new DebugBar("debug").setVisible(getApplication().getDebugSettings().isDevelopmentUtilitiesEnabled()));
        add(new BootstrapNavbarLink("builds", BuildPage.class, Model.of("Builds"), "glyphicon glyphicon-th-list"));
        add(new BootstrapNavbarLink("config", ConfigPage.class, Model.of("Config"), "glyphicon glyphicon-wrench"));
        add(new BootstrapNavbarLink("aggregation", AggregationPage.class, Model.of("Aggregation"), "glyphicon glyphicon-search"));
        add(new BootstrapNavbarLink("admin", AdminPage.class, Model.of("Admin"), "glyphicon glyphicon-cog"));
        add(new AbstractAjaxTimerBehavior(SESSION_REFRESH_INTERVAL) {

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                log.trace("Received AJAX request in order to keep http session alive.");
            }
        });
    }
}
