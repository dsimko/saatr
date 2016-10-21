
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.Build.Status;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildFilterRepository;
import org.jboss.qa.tool.saatr.web.comp.build.BuildExpansion;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsFilterPanel extends GenericPanel<BuildFilter> {

    private static final String FILTER_PARAM_NAME = "filter";

    @SpringBean
    private BuildFilterRepository buildFilterRepository;

    public BuildsFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
        Form<BuildFilter> form = new Form<BuildFilter>("form", new CompoundPropertyModel<BuildFilter>(model)) {

            @Override
            protected void onSubmit() {
                BuildFilter buildFilter = getModelObject();
                if (buildFilter.getId() == null) {
                    buildFilter = buildFilterRepository.save(buildFilter);
                } else {
                    BuildFilter original = buildFilterRepository.findOne(buildFilter.getId());
                    if (!buildFilter.equals(original)) {
                        buildFilter.setId(null);
                        buildFilter.setCreated(new Date());
                        buildFilter.setLastUsed(new Date());
                        buildFilter = buildFilterRepository.save(buildFilter);
                    }
                }
                changeFilter(buildFilter);
            }

        };
        form.add(new TextField<>("jobName"));
        form.add(new TextField<>("buildNumber"));
        form.add(new DropDownChoice<>("status", Arrays.asList(Status.values())).setNullValid(true));
        form.add(new DateTimeField("createdFrom"));
        form.add(new DateTimeField("createdTo"));
        form.add(new JobParamsFilterPanel("jobParams", model));
        form.add(new SystemParamsFilterPanel("systemParams", model));
        form.add(new CustomPropertiesFilterPanel("customProperties", model));
        form.add(new Link<Void>("clear") {

            @Override
            public void onClick() {
                BuildExpansion.get().collapseAll();
                setResponsePage(getPage().getClass());
            }
        });
        add(form);
        List<IColumn<BuildFilter, String>> columns = new ArrayList<IColumn<BuildFilter, String>>();
        columns.add(new FilterColumn());
        columns.add(new AbstractColumn<BuildFilter, String>(Model.of("")) {

            @Override
            public void populateItem(Item<ICellPopulator<BuildFilter>> cellItem, String componentId, IModel<BuildFilter> rowModel) {
                cellItem.add(new DeleteColumnPanel(componentId, rowModel));
            }
        });
        DataTable<BuildFilter, String> dataTable = new DataTable<BuildFilter, String>("table", columns, new BuildFilterProvider(), 5) {

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
        add(dataTable.setOutputMarkupId(true));

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

    @Data
    @AllArgsConstructor
    static class RefreshTableEvent implements Serializable {

        AjaxRequestTarget target;
    }
}
