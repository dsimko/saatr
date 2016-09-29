
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTabbedPanel;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsPanel extends GenericPanel<BuildDocument> {

    protected IModel<BuildFilter> filter = Model.of(new BuildFilter());

    public BuildsPanel(String id, IModel<BuildDocument> model) {
        super(id, model);
        add(new BuildsFilterPanel("filter", filter));
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(new AbstractTab(new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "<span class=\"glyphicon glyphicon-tree-conifer\"></span> Tree";
            }
        }) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new BuildsTreeTablePanel(panelId, model, filter);
            }
        });
        tabs.add(new AbstractTab(new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "<span class=\"glyphicon glyphicon-th-list\"></span> Table";
            }
        }) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new BuildsSimpleTablePanel(panelId, model, filter);
            }
        });
        add(new BootstrapTabbedPanel<>("tabs", tabs));
    }
}
