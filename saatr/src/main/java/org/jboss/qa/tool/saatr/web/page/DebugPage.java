package org.jboss.qa.tool.saatr.web.page;

import javax.inject.Inject;

import org.apache.wicket.markup.html.link.Link;
import org.jboss.qa.tool.saatr.service.BuildService;
import org.jboss.qa.tool.saatr.service.ConfigService;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class DebugPage extends BasePage<Void> {

    @Inject
    private BuildService buildService;
    @Inject
    private ConfigService configService;

    public DebugPage() {
        add(new Link<Void>("deleteAllBuilds") {
            @Override
            public void onClick() {
                buildService.deleteAll();
            }
        });
        add(new Link<Void>("deleteAllConfigs") {
            @Override
            public void onClick() {
                configService.deleteAll();
            }
        });

    }
}
