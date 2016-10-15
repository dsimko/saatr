
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildFilterRepository;

@SuppressWarnings("serial")
public class BuildFilterProvider extends SortableDataProvider<BuildFilter, String> {

    @Inject
    private BuildFilterRepository buildFilterRepository;

    public BuildFilterProvider() {
        Injector.get().inject(this);
    }

    @Override
    public Iterator<BuildFilter> iterator(long first, long count) {
        return buildFilterRepository.query(first, count);
    }

    @Override
    public long size() {
        return buildFilterRepository.count();
    }

    @Override
    public IModel<BuildFilter> model(BuildFilter buildFilter) {
        return new Model<BuildFilter>(buildFilter);
    }
}
