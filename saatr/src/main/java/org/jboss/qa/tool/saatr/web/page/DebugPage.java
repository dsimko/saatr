package org.jboss.qa.tool.saatr.web.page;

import javax.inject.Inject;

import org.apache.wicket.markup.html.link.Link;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.repo.config.ConfigRepository;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class DebugPage extends BasePage<Void> {

    @Inject
    private BuildRepository buildRepository;
    @Inject
    private ConfigRepository configRepository;

    public DebugPage() {
        add(new Link<Void>("deleteAllBuilds") {
            @Override
            public void onClick() {
                buildRepository.deleteAll();
            }
        });
        add(new Link<Void>("deleteAllConfigs") {
            @Override
            public void onClick() {
                configRepository.deleteAll();
            }
        });

    }
}
