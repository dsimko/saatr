
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.build.Build.Status;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.UserRepository;
import org.jboss.qa.tool.saatr.repo.build.BuildFilterRepository;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapDateTimeField;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildExpansion;
import org.jboss.qa.tool.saatr.web.comp.build.BuildsTablePanel.RefreshSelectedEvent;
import org.jboss.qa.tool.saatr.web.comp.build.SelectRowColumn;
import org.jboss.qa.tool.saatr.web.page.BuildPage.CompareBuildFiltersEvent;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsFilterPanel extends GenericPanel<BuildFilter> {

    public static final String FILTER_PARAM_NAME = "filter";
    private static final int ROWS_PER_PAGE = 10;

    private Label selectedCount;

    @SpringBean
    private BuildFilterRepository buildFilterRepository;

    @SpringBean
    private UserRepository userRepository;

    public BuildsFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
        selectedCount = new Label("selectedCount", new PropertyModel<>(this, "selectedIds.size"));
        add(selectedCount.setOutputMarkupId(true));
        Form<BuildFilter> form = new Form<BuildFilter>("form", new CompoundPropertyModel<BuildFilter>(model)) {

            @Override
            protected void onSubmit() {
                BuildFilter buildFilter = getModelObject();
                buildFilter.setCreatorUsername(userRepository.getCurrentUserName());
                buildFilterRepository.saveIfNewOrChanged(buildFilter);
                changeFilter(buildFilter);
            }

        };
        form.add(new BootstrapFeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this)));
        form.add(new TextField<>("jobName"));
        form.add(new TextField<>("buildNumber"));
        form.add(new DropDownChoice<>("status", Arrays.asList(Status.values())).setNullValid(true));
        form.add(new BootstrapDateTimeField("createdFrom"));
        form.add(new BootstrapDateTimeField("createdTo"));
        form.add(new JobParamsFilterPanel("jobParams", model));
        form.add(new SystemParamsFilterPanel("systemParams", model));
        form.add(new CustomPropertiesFilterPanel("customProperties", model));
        form.add(new TextField<>("errorMessage"));
        form.add(new TextField<>("failureMessage"));
        form.add(new Link<Void>("clear") {

            @Override
            public void onClick() {
                BuildExpansion.get().collapseAll();
                setResponsePage(getPage().getClass());
            }
        });
        add(form);
        List<IColumn<BuildFilter, String>> columns = new ArrayList<IColumn<BuildFilter, String>>();
        columns.add(new SelectRowColumn<>(BuildFilterSelection.get().getIds()));
        columns.add(new FilterColumn());
        columns.add(new AbstractColumn<BuildFilter, String>(Model.of("")) {

            @Override
            public void populateItem(Item<ICellPopulator<BuildFilter>> cellItem, String componentId, IModel<BuildFilter> rowModel) {
                cellItem.add(new DeleteColumnPanel(componentId, rowModel));
            }
        });
        DataTable<BuildFilter, String> table = new DataTable<BuildFilter, String>("table", columns, new BuildFilterProvider(), ROWS_PER_PAGE) {

            @Override
            protected Item<BuildFilter> newRowItem(String id, int index, final IModel<BuildFilter> model) {
                Item<BuildFilter> row = new OddEvenItem<BuildFilter>(id, index, model);
                if (model.getObject().equals(BuildsFilterPanel.this.getModelObject())) {
                    row.add(new AttributeAppender("class", Model.of("active"), " "));
                }
                row.add(new AttributeAppender("class", Model.of("clicableTableRow"), " "));
                row.add(new AjaxEventBehavior("click") {

                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        changeFilter(model.getObject());
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);
                        // because of links on actions panel
                        attributes.setPreventDefault(true);
                    }
                });

                return row;
            }

            @Override
            public void onEvent(IEvent<?> event) {
                if (event.getPayload() instanceof RefreshTableEvent) {
                    ((RefreshTableEvent) event.getPayload()).getTarget().add(this);
                }
            }
        };
        add(table.setOutputMarkupId(true));
        add(new AjaxLink<Void>("deselect") {

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
                getPage().send(getPage(), Broadcast.EXACT, new CompareBuildFiltersEvent(getSelectedIds()));
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        String filterId = getPage().getPageParameters().get(FILTER_PARAM_NAME).toString(null);
        if (filterId != null) {
            BuildFilter buildFilter = buildFilterRepository.findAndUpdateLastUsed(filterId);
            if (buildFilter != null) {
                setModelObject(buildFilter);
            }
        }
    }

    private void changeFilter(BuildFilter buildFilter) {
        PageParameters params = getPage().getPageParameters();
        params.clearNamed();
        params.set(FILTER_PARAM_NAME, buildFilter.getId());
        setResponsePage(getPage().getClass(), params);
    }

    public Set<ObjectId> getSelectedIds() {
        return BuildFilterSelection.get().getIds();
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
    static class RefreshTableEvent implements Serializable {

        AjaxRequestTarget target;
    }
}
