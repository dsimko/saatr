
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

@SuppressWarnings("serial")
public class SystemParamsFilterPanel extends BuildsPropertiesFilterPanel {

    @SpringBean
    private BuildRepository buildRepository;

    public SystemParamsFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
    }

    @Override
    protected String getTitle() {
        return "System param name";
    }
    
    @Override
    protected List<PropertyData> getProperties() {
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
