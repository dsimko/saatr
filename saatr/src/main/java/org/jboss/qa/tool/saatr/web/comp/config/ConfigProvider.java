package org.jboss.qa.tool.saatr.web.comp.config;

import java.io.Serializable;
import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.ConfigData;
import org.jboss.qa.tool.saatr.service.ConfigService;
import org.jboss.qa.tool.saatr.web.comp.EntityModel;

import lombok.Data;

@SuppressWarnings("serial")
public class ConfigProvider extends SortableDataProvider<ConfigData, String> {

    @Inject
    private ConfigService configService;

    private final IModel<ConfigFilter> filter;

    public ConfigProvider(IModel<ConfigFilter> filter) {
        this.filter = filter;
        Injector.get().inject(this);
    }

    @Override
    public Iterator<ConfigData> iterator(long first, long count) {
        return configService.query(first, count, filter.getObject());
    }

    @Override
    public long size() {
        return configService.count(filter.getObject());
    }

    @Override
    public IModel<ConfigData> model(ConfigData configData) {
        return new EntityModel<ConfigData>(configData);
    }

    @Data
    public static class ConfigFilter implements Serializable {
        private String name;
    }
}
