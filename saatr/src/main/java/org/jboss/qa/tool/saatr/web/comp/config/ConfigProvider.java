
package org.jboss.qa.tool.saatr.web.comp.config;

import java.io.Serializable;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.repo.config.ConfigRepository;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;

import lombok.Data;

@SuppressWarnings("serial")
public class ConfigProvider extends SortableDataProvider<ConfigDocument, String> {

    @Inject
    private ConfigRepository configRepository;

    private final IModel<ConfigFilter> filter;

    public ConfigProvider(IModel<ConfigFilter> filter) {
        this.filter = filter;
        Injector.get().inject(this);
    }

    @Override
    public Iterator<ConfigDocument> iterator(long first, long count) {
        return configRepository.query(first, count, filter.getObject());
    }

    @Override
    public long size() {
        return configRepository.count(filter.getObject());
    }

    @Override
    public IModel<ConfigDocument> model(ConfigDocument configData) {
        return new DocumentModel<ConfigDocument>(configData);
    }

    @Data
    public static class ConfigFilter implements Serializable {

        private String name;
    }
}
