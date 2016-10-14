
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter.PropertyDto;

@SuppressWarnings("serial")
public class SystemParamsFilterPanel extends AbsractPropertiesFilterPanel {

    public SystemParamsFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
    }

    @Override
    protected String getTitle() {
        return "System param name";
    }
    
    @Override
    protected List<PropertyDto> getProperties() {
        return getModelObject().getSystemParams();
    }

    @Override
    protected Iterable<String> getPropertyNames() {
        return buildRepository.findDistinctSystemPropertiesNames();
    }

    @Override
    protected Iterable<String> getPropertyValues(String name) {
        return buildRepository.findDistinctSystemPropertiesValues(name);
    }

}
