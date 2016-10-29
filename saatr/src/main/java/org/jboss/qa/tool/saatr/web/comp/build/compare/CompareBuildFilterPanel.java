
package org.jboss.qa.tool.saatr.web.comp.build.compare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.Build.HtmlRenderer;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildFilterRepository;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.build.filter.FilterColumn;
import org.jboss.qa.tool.saatr.web.page.BuildPage;

import lombok.Data;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class CompareBuildFilterPanel extends Panel {

    private String previousName = "";

    @SpringBean
    private BuildRepository buildRepository;

    @SpringBean
    private BuildFilterRepository buildFilterRepository;

    public CompareBuildFilterPanel(String id, final List<ObjectId> buildFilterIds) {
        super(id);
        List<BuildNameDto> allNames = new ArrayList<>();
        Map<ObjectId, List<Build>> allBuilds = new HashMap<>();
        for (ObjectId filterId : buildFilterIds) {
            BuildFilter filter = buildFilterRepository.findOne(filterId);
            allNames.addAll(buildRepository.getDistinctJobNames(filter));
            allBuilds.put(filterId, buildRepository.query(0, Long.MAX_VALUE, filter, new SortParam<>("id", false)));
        }
        Collections.sort(allNames);
        add(new ListView<ObjectId>("filters", buildFilterIds) {

            @Override
            protected void populateItem(ListItem<ObjectId> item) {
                item.add(new Label("filter", FilterColumn.geData(buildFilterRepository.findOne(item.getModelObject()))));
            }
        });
        add(new RefreshingView<BuildNameDto>("rows") {

            @Override
            protected Iterator<IModel<BuildNameDto>> getItemModels() {
                return allNames.stream().map(name -> (IModel<BuildNameDto>) new Model<>(name)).collect(Collectors.toList()).iterator();
            }

            @Override
            protected void populateItem(Item<BuildNameDto> item) {
                BuildNameDto buildName = item.getModelObject();
                if (!previousName.equals(buildName.getName())) {
                    previousName = buildName.getName();
                    item.add(new Label("name", buildName.getName()));
                } else {
                    item.add(new Label("name", ""));
                }
                item.add(new Label("config", buildName.getConfiguration()));
                item.add(new RefreshingView<ObjectId>("filters") {

                    @Override
                    protected Iterator<IModel<ObjectId>> getItemModels() {
                        return buildFilterIds.stream().map(id -> (IModel<ObjectId>) new Model<>(id)).collect(Collectors.toList()).iterator();

                    }

                    @Override
                    protected void populateItem(Item<ObjectId> item2) {
                        List<Build> builds = allBuilds.get(item2.getModelObject()).stream().filter(
                                b -> buildName.getName().equals(b.getName()) && buildName.getConfiguration().equals(b.getConfiguration())).collect(
                                        Collectors.toList());
                        item2.add(new ListView<Build>("builds", builds) {

                            @Override
                            protected void populateItem(ListItem<Build> item3) {
                                Build build = item3.getModelObject();
                                WebMarkupContainer wmc = new WebMarkupContainer("wmc");
                                wmc.add(new AjaxEventBehavior("click") {

                                    @Override
                                    protected void onEvent(AjaxRequestTarget target) {
                                        PageParameters parameters = BuildPage.createBuildDetailPageParameters(build.getId(), null);
                                        setResponsePage(BuildPage.class, parameters);
                                    }

                                });
                                wmc.add(new Label("testsuiteStats", HtmlRenderer.getTestsuiteStatisticsHtml(build)).setEscapeModelStrings(false));
                                wmc.add(new Label("testcaseStats", HtmlRenderer.getTestcaseStatisticsHtml(build)).setEscapeModelStrings(false));
                                wmc.add(new Label("status", HtmlRenderer.getStatusHtml(build)).setEscapeModelStrings(false));
                                item3.add(wmc);
                            }
                        });

                    }
                });
            }
        });

    }

    @Data
    public static class BuildNameDto implements Serializable, Comparable<BuildNameDto> {

        private String name;

        private String configuration;

        @Override
        public int compareTo(BuildNameDto o) {
            int ret = name.compareTo(o.getName());
            if (ret == 0) {
                return configuration.compareTo(o.getConfiguration());
            }
            return ret;
        }
    }
}