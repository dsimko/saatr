
package org.jboss.qa.tool.saatr.web.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTabbedPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildExpansion;
import org.jboss.qa.tool.saatr.web.comp.build.BuildJsonPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildTreePanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildsPanel;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildPage extends BasePage<Build> {

    private boolean anchorExists;

    public BuildPage() {
        super(new DocumentModel<Build>(Build.class, null));
        initPage(null);
    }

    public BuildPage(PageParameters parameters) {
        super(new DocumentModel<Build>(Build.class, null));
        initPage(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        if (anchorExists) {
            response.render(new OnLoadHeaderItem("location.href='#anchor';"));
        }
    }

    private void initPage(PageParameters parameters) {
        if (parameters == null || parameters.isEmpty()) {
            BuildExpansion.get().collapseAll();
        } else {
            anchorExists = parameters.get(BuildTreePanel.ANCHOR_PARAM_NAME).toBoolean(false);
        }
        add(new BuildsPanel("buildsPanel", getModel()));
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(new AbstractTab(new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "<span class=\"glyphicon glyphicon-th-list\"></span> Tables";
            }
        }) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new BuildPanel(panelId, getModel());
            }
        });

        tabs.add(new AbstractTab(new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return "<span class=\"glyphicon glyphicon-paste\"></span> JSON";
            }
        }) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                return new BuildJsonPanel(panelId, getModel());
            }
        });
        add(new BootstrapTabbedPanel<>("tabs", tabs));
    }

}
