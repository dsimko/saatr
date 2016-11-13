
package org.jboss.qa.tool.saatr.web.page;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.jboss.qa.tool.saatr.domain.User.Role;
import org.jboss.qa.tool.saatr.repo.UserRepository;
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

    protected String pageTitle = "SAATR - Simple tool for storing and analyzing tests results";
    
    @SpringBean
    private UserRepository userRepository;
    
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
        // for reason of this please see RequestCycleSettings#setGatherExtendedBrowserInfo
        getSession().getClientInfo();
        add(new Label("pageTitle", new PropertyModel<>(this, "pageTitle")));
        add(new Link<Void>("logout") {
            @Override
            public void onClick() {
                getSession().invalidateNow();
                throw new RedirectToUrlException("/logout");
            }
        }.add(new Label("username", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return userRepository.getCurrentUserName();
            }
        })));
        add(new BootstrapNavbarLink("builds", BuildPage.class, Model.of("Builds"), "glyphicon glyphicon-th-list"));
        add(new BootstrapNavbarLink("config", ConfigPage.class, Model.of("Config"), "glyphicon glyphicon-wrench"));
        Component adminLink = new BootstrapNavbarLink("admin", AdminPage.class, Model.of("Admin"), "glyphicon glyphicon-cog");
        Component aggregationLink = new BootstrapNavbarLink("aggregation", AggregationPage.class, Model.of("Aggregation"), "glyphicon glyphicon-search");
        add(adminLink);
        add(aggregationLink);
        MetaDataRoleAuthorizationStrategy.authorize(adminLink, RENDER, Role.Admin.name());
        MetaDataRoleAuthorizationStrategy.authorize(aggregationLink, RENDER, Role.Admin.name());
        add(new AbstractAjaxTimerBehavior(SESSION_REFRESH_INTERVAL) {

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                log.trace("Received AJAX request in order to keep http session alive.");
            }
        });
    }
}
