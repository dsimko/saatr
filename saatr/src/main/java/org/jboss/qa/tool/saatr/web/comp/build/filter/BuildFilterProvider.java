
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildFilterRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.springframework.security.core.context.SecurityContextHolder;

@SuppressWarnings("serial")
public class BuildFilterProvider extends SortableDataProvider<BuildFilter, String> {

    private final String creatorUsername;
    
    @Inject
    private BuildFilterRepository buildFilterRepository;

    public BuildFilterProvider() {
        Injector.get().inject(this);
        this.creatorUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public Iterator<BuildFilter> iterator(long first, long count) {
        return buildFilterRepository.query(first, count, creatorUsername);
    }

    @Override
    public long size() {
        return buildFilterRepository.count(creatorUsername);
    }

    @Override
    public IModel<BuildFilter> model(BuildFilter buildFilter) {
        return new DocumentModel<BuildFilter>(buildFilter);
    }
}
