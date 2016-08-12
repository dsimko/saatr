package org.jboss.qa.tool.saatr.web.component;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.entity.Build;
import org.jboss.qa.tool.saatr.util.MongoDBUtils;

import lombok.Data;

@SuppressWarnings("serial")
public class BuildProvider extends SortableDataProvider<Build, String> {

    private final IModel<BuildFilter> filter;

    public BuildProvider(IModel<BuildFilter> filter) {
        this.filter = filter;
    }

    @Override
    public Iterator<Build> iterator(long first, long count) {
        return MongoDBUtils.query(first, count, filter.getObject());
    }

    @Override
    public long size() {
        return MongoDBUtils.count(filter.getObject());
    }

    @Override
    public IModel<Build> model(Build build) {
        return new Model<Build>(build);
    }

    @Data
    public static class BuildFilter implements Serializable {
        private Long buildNumber;
    }
}
