
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.build.filter.BuildFilter.PropertyDto;

@SuppressWarnings("serial")
public class JobParamsFilterPanel extends AbsractPropertiesFilterPanel {

    @SpringBean
    private BuildRepository buildRepository;

    public JobParamsFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
    }

    @Override
    protected String getTitle() {
        return "Job param name";
    }
    
    @Override
    protected List<PropertyDto> getProperties() {
        return getModelObject().getVariables();
    }

    @Override
    protected Iterable<String> getPropertyNames() {
        return buildRepository.findDistinctVariableNames();
    }

    @Override
    protected Iterable<String> getPropertyValues(String name) {
        return buildRepository.findDistinctVariableValues(name);
    }

}
