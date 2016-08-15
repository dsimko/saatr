package org.jboss.qa.tool.saatr.web.page;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.service.BuildService;
import org.jboss.qa.tool.saatr.web.component.build.BuildDetailPanel;
import org.jboss.qa.tool.saatr.web.component.build.BuildProvider;
import org.jboss.qa.tool.saatr.web.component.build.BuildProvider.BuildFilter;
import org.jboss.qa.tool.saatr.web.component.common.bootstrap.BootstrapButton;
import org.jboss.qa.tool.saatr.web.component.common.bootstrap.BootstrapTable;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildPage extends BasePage<Build> {

    private IModel<BuildFilter> filter = Model.of(new BuildFilter());

    @Inject
    private BuildService buildServicel;

    public BuildPage() {
        super(new Model<>());
        Form<BuildFilter> form = new Form<>("form", new CompoundPropertyModel<BuildFilter>(filter));
        form.add(new TextField<>("buildNumber"));
        form.add(new Link<Void>("clear") {
            @Override
            public void onClick() {
                filter.setObject(new BuildFilter());
            }
        });
        form.add(new BootstrapButton<Build>("dropDB", getModel()) {
            @Override
            public void onClick() {
                buildServicel.deleteAll();
                setModelObject(null);
            }
        });
        add(form);
        List<IColumn<Build, String>> columns = new ArrayList<IColumn<Build, String>>();
        columns.add(new PropertyColumn<Build, String>(new Model<String>("id"), "id", "id"));
        columns.add(new PropertyColumn<Build, String>(new Model<String>("jobName"), "jobName", "jobName"));
        columns.add(new PropertyColumn<Build, String>(new Model<String>("buildNumber"), "buildNumber", "buildNumber"));

        BootstrapTable<Build, String> dataTable = new BootstrapTable<Build, String>("table", columns, new BuildProvider(filter), 10,
                getModel()) {

            @Override
            protected void selectRow(Build user) {
                setModelObject(user);
            }
        };
        add(dataTable);
        add(new BuildDetailPanel("detail", getModel()));
    }
}
