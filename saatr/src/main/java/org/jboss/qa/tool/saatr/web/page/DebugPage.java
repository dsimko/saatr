package org.jboss.qa.tool.saatr.web.page;

import javax.inject.Inject;

import org.apache.wicket.markup.html.link.Link;
import org.jboss.qa.tool.saatr.service.BuildService;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class DebugPage extends BasePage<Void> {

    @Inject
    private BuildService buildServicel;

    public DebugPage() {
        add(new Link<Void>("dropDb") {
            @Override
            public void onClick() {
                buildServicel.deleteAll();
            }
        });
    }
}
