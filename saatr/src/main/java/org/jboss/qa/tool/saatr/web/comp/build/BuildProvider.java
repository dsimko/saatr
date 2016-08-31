package org.jboss.qa.tool.saatr.web.comp.build;

import java.io.Serializable;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.entity.Build.Status;
import org.jboss.qa.tool.saatr.service.BuildService;
import org.jboss.qa.tool.saatr.web.comp.EntityModel;

import lombok.Data;

@SuppressWarnings("serial")
public class BuildProvider extends SortableDataProvider<Build, String> {

    @Inject
    private BuildService buildService;

    private final IModel<BuildFilter> filter;

    public BuildProvider(IModel<BuildFilter> filter) {
        this.filter = filter;
        Injector.get().inject(this);
    }

    @Override
    public Iterator<Build> iterator(long first, long count) {
        return buildService.query(first, count, filter.getObject());
    }

    @Override
    public long size() {
        return buildService.count(filter.getObject());
    }

    @Override
    public IModel<Build> model(Build build) {
        return new EntityModel<Build>(build);
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
