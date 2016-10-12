
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.web.comp.build.filter.BuildFilter.PropertyDto;

@SuppressWarnings("serial")
public class CustomPropertiesFilterPanel extends AbsractPropertiesFilterPanel {

    public CustomPropertiesFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
    }

    @Override
    protected String getTitle() {
        return "Custom property name";
    }

    @Override
    protected List<PropertyDto> getProperties() {
        return getModelObject().getProperties();
    }

    @Override
    protected Iterable<String> getPropertyNames() {
        return buildRepository.findDistinctPropertiesNames();
    }

    @Override
    protected Iterable<String> getPropertyValues(String name) {
        return buildRepository.findDistinctPropertiesValues(name);
    }

}
