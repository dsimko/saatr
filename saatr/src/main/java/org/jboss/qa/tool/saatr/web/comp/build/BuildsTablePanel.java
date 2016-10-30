
package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;
import org.jboss.qa.tool.saatr.web.page.BuildPage;
import org.jboss.qa.tool.saatr.web.page.BuildPage.CompareBuildsEvent;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsTablePanel extends GenericPanel<Build> {

    private static final int ROWS_PER_PAGE = 100;

    private Label selectedCount;

    public BuildsTablePanel(String id, IModel<Build> model, IModel<BuildFilter> filterModel) {
        super(id, model);
        selectedCount = new Label("selectedCount", new PropertyModel<>(this, "selectedIds.size"));
        add(selectedCount.setOutputMarkupId(true));
        List<IColumn<Build, String>> columns = new ArrayList<IColumn<Build, String>>();
        columns.add(new SelectRowColumn<>(getSelectedIds()));
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Job Name"), "name", "name"));
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Config"), "configuration", "configuration") {

            @Override
            public String getCssClass() {
                return "small";
            }
        });
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Build Number"), "buildNumber", "buildNumber"));
        columns.add(new TestcaseStatsColumn());
        columns.add(new TestsuiteStatsColumn());
        columns.add(new StatusColumn());
        BootstrapTable<Build, String> table = new BootstrapTable<Build, String>("table", columns, new BuildProvider(filterModel), ROWS_PER_PAGE, getModel()) {

            @Override
            protected void onRowClicked(AjaxRequestTarget target, Build build) {
                setResponsePage(BuildPage.class, BuildPage.createBuildDetailPageParameters(build.getId(), getPage().getPageParameters()));
            }

        };
        add(table.setOutputMarkupId(true));
        add(new AjaxLink<Void>("selectAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                table.getDataProvider().iterator(table.getCurrentPage() * table.getItemsPerPage(), table.getItemsPerPage()).forEachRemaining(
                        b -> getSelectedIds().add(b.getId()));
                target.add(selectedCount);
                target.add(table);
            }
        });
        add(new AjaxLink<Void>("deselectAll") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                getSelectedIds().clear();
                target.add(selectedCount);
                target.add(table);
            }
        });
        add(new Link<Void>("compare") {

            @Override
            public void onClick() {
                getPage().send(getPage(), Broadcast.EXACT, new CompareBuildsEvent(getSelectedIds()));
            }
        });

    }

    public Set<ObjectId> getSelectedIds() {
        return BuildSelection.get().getIds();
    }

    @Override
    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof RefreshSelectedEvent) {
            RefreshSelectedEvent refreshEvent = (RefreshSelectedEvent) event.getPayload();
            refreshEvent.getTarget().add(selectedCount);
        }
    }

    @Data
    @AllArgsConstructor
    public static class RefreshSelectedEvent implements Serializable {

        private final AjaxRequestTarget target;
    }

    private static class BuildProvider extends SortableDataProvider<Build, String> {

        @Inject
        private BuildRepository buildRepository;

        private final IModel<BuildFilter> filter;

        public BuildProvider(IModel<BuildFilter> filter) {
            this.filter = filter;
            Injector.get().inject(this);
            setSort("id", SortOrder.DESCENDING);
        }

        @Override
        public Iterator<Build> iterator(long first, long count) {
            return buildRepository.query(first, count, filter.getObject(), getSort()).iterator();
        }

        @Override
        public long size() {
            return buildRepository.count(filter.getObject());
        }

        @Override
        public IModel<Build> model(Build build) {
            return new Model<Build>(build);
        }
    }

}
