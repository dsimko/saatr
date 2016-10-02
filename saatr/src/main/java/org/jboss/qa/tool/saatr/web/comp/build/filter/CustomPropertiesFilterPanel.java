
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

@SuppressWarnings("serial")
public class CustomPropertiesFilterPanel extends AbsractPropertiesFilterPanel {

    @SpringBean
    private BuildRepository buildRepository;

    public CustomPropertiesFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
    }

    @Override
    protected String getTitle() {
        return "Custom property name";
    }
    
    @Override
    protected List<PropertyData> getProperties() {
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
