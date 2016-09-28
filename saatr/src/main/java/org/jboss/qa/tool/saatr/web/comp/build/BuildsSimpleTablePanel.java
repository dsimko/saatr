
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;
import org.jboss.qa.tool.saatr.web.comp.build.BuildProvider.BuildFilter;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsSimpleTablePanel extends GenericPanel<BuildDocument> {

    public BuildsSimpleTablePanel(String id, IModel<BuildDocument> model, IModel<BuildFilter> filterModel) {
        super(id, model);
        List<IColumn<BuildDocument, String>> columns = new ArrayList<IColumn<BuildDocument, String>>();
        columns.add(new PropertyColumn<BuildDocument, String>(new Model<String>("Job Name"), "jobName"));
        columns.add(new PropertyColumn<BuildDocument, String>(new Model<String>("Build Number"), "buildNumber"));
        columns.add(new StatusColumn());
        BootstrapTable<BuildDocument, String> dataTable = new BootstrapTable<BuildDocument, String>("table", columns, new BuildProvider(filterModel), 10,
                getModel()) {

            @Override
            protected void selectRow(BuildDocument build) {
                setModelObject(build);
            }
        };
        add(dataTable);
    }
}
