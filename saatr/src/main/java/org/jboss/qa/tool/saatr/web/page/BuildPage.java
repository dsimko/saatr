package org.jboss.qa.tool.saatr.web.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Build.Status;
import org.jboss.qa.tool.saatr.web.comp.EntityModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTabbedPanel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;
import org.jboss.qa.tool.saatr.web.comp.build.BuildJsonPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildPanel;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider.BuildFilter;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildPage extends BasePage<Build> {

    private IModel<BuildFilter> filter = Model.of(new BuildFilter());

    public BuildPage() {
        super(new EntityModel<Build>(Build.class, null));
        Form<BuildFilter> form = new Form<>("form", new CompoundPropertyModel<BuildFilter>(filter));
        form.add(new TextField<>("jobName"));
        form.add(new TextField<>("buildNumber"));
        form.add(new DropDownChoice<>("status", Arrays.asList(Status.values())).setNullValid(true));
        form.add(new Link<Void>("clear") {
            @Override
            public void onClick() {
                filter.setObject(new BuildFilter());
            }
        });
        add(form);
        List<IColumn<Build, String>> columns = new ArrayList<IColumn<Build, String>>();
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Job Name"), "jobName"));
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Build Number"), "buildNumber"));
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Status"), "status"));

        BootstrapTable<Build, String> dataTable = new BootstrapTable<Build, String>("table", columns, new BuildProvider(filter), 10,
                getModel()) {

            @Override
            protected void selectRow(Build build) {
                setModelObject(build);
            }
        };
        add(dataTable);
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
