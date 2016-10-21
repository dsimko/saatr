
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsSimpleTablePanel extends GenericPanel<Build> {

    public BuildsSimpleTablePanel(String id, IModel<Build> model, IModel<BuildFilter> filterModel) {
        super(id, model);
        List<IColumn<Build, String>> columns = new ArrayList<IColumn<Build, String>>();
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Job Name"), "fullName"));
        columns.add(new PropertyColumn<Build, String>(new Model<String>("Build Number"), "buildNumber"));
        columns.add(new StatusColumn());
        BootstrapTable<Build, String> dataTable = new BootstrapTable<Build, String>("table", columns, new BuildProvider(filterModel), 10,
                getModel()) {

            @Override
            protected void selectRow(Build build) {
                setModelObject(build);
            }
        };
        add(dataTable);
    }

    private static class BuildProvider extends SortableDataProvider<Build, String> {

        @Inject
        private BuildRepository buildRepository;

        private final IModel<BuildFilter> filter;

        public BuildProvider(IModel<BuildFilter> filter) {
            this.filter = filter;
            Injector.get().inject(this);
        }

        @Override
        public Iterator<Build> iterator(long first, long count) {
            return buildRepository.query(first, count, filter.getObject());
        }

        @Override
        public long size() {
            return buildRepository.count(filter.getObject());
        }

        @Override
        public IModel<Build> model(Build build) {
            return new DocumentModel<Build>(build);
        }
    }

}
