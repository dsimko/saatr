
package org.jboss.qa.tool.saatr.web.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTabbedPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildExpansion;
import org.jboss.qa.tool.saatr.web.comp.build.BuildJsonPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildsPanel;
import org.jboss.qa.tool.saatr.web.comp.build.compare.CompareBuildFilterPanel;
import org.jboss.qa.tool.saatr.web.comp.build.compare.CompareBuildPanel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildPage extends BasePage<Build> {

    private static final String BUILD_PARAM_NAME = "build";

    private static final String ANCHOR_PARAM_NAME = "anchor";

    private boolean anchorExists;

    private Panel listPanel;

    private Panel detailPanel;

    @SpringBean
    private BuildRepository buildRepository;

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
            anchorExists = parameters.get(ANCHOR_PARAM_NAME).toBoolean(false);
        }
        add(listPanel = new BuildsPanel("buildsPanel", getModel()));
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
        add(detailPanel = new BootstrapTabbedPanel<>("tabs", tabs));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        String buildId = getPage().getPageParameters().get(BUILD_PARAM_NAME).toString(null);
        if (buildId != null) {
            Build build = buildRepository.findOne(new ObjectId(buildId));
            if (build != null) {
                setModelObject(build);
            }
        }
    }

    @Override
    public void onEvent(IEvent<?> event) {
        Object payload = event.getPayload();
        if (payload instanceof CompareBuildsEvent) {
            CompareBuildsEvent eventPayload = (CompareBuildsEvent) payload;
            replaceDetailPanel(eventPayload.getBuildIds());
        } else if (payload instanceof CompareBuildFiltersEvent) {
            CompareBuildFiltersEvent eventPayload = (CompareBuildFiltersEvent) payload;
            replaceListPanel(eventPayload.getBuildFilterIds());
            replaceDetailPanel(Collections.emptySet());
        }
    }

    private void replaceDetailPanel(Set<ObjectId> buildIds) {
        Panel comparePanel = new CompareBuildPanel(detailPanel.getId(), buildIds);
        detailPanel.replaceWith(comparePanel);
        detailPanel = comparePanel;
    }

    private void replaceListPanel(Set<ObjectId> buildFilterIds) {
        Panel comparePanel = new CompareBuildFilterPanel(listPanel.getId(), new ArrayList<>(buildFilterIds));
        listPanel.replaceWith(comparePanel);
        listPanel = comparePanel;
    }

    public static PageParameters createBuildDetailPageParameters(ObjectId buildId, PageParameters pageParameters) {
        PageParameters parameters = new PageParameters(pageParameters);
        parameters.set(BuildPage.BUILD_PARAM_NAME, buildId);
        parameters.set(BuildPage.ANCHOR_PARAM_NAME, true);
        return parameters;
    }

    @Data
    @AllArgsConstructor
    public static class CompareBuildsEvent implements Serializable {

        private final Set<ObjectId> buildIds;
    }

    @Data
    @AllArgsConstructor
    public static class CompareBuildFiltersEvent implements Serializable {

        private final Set<ObjectId> buildFilterIds;
    }

}
