
package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;

import lombok.Data;

@SuppressWarnings("serial")
public class BuildProvider extends SortableDataProvider<BuildDocument, String> {

    @Inject
    private BuildRepository buildRepository;

    private final IModel<BuildFilter> filter;

    public BuildProvider(IModel<BuildFilter> filter) {
        this.filter = filter;
        Injector.get().inject(this);
    }

    @Override
    public Iterator<BuildDocument> iterator(long first, long count) {
        return buildRepository.query(first, count, filter.getObject());
    }

    @Override
    public long size() {
        return buildRepository.count(filter.getObject());
    }

    @Override
    public IModel<BuildDocument> model(BuildDocument build) {
        return new DocumentModel<BuildDocument>(build);
    }

    @Data
    public static class BuildFilter implements Serializable {

        private Long buildNumber;

        private String jobName;

        private Status status;

        private String variableName;

        private String variableValue;
    }
}
