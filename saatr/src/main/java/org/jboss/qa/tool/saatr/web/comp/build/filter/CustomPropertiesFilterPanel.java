
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter.PropertyDto;

import lombok.AllArgsConstructor;
import lombok.Data;

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

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof RefreshCustomPropertiesFilterEvent) {
            initVariables();
            RefreshCustomPropertiesFilterEvent refreshEvent = (RefreshCustomPropertiesFilterEvent) event.getPayload();
            refreshEvent.getTarget().add(this);
        }
    }

    @Data
    @AllArgsConstructor
    public static class RefreshCustomPropertiesFilterEvent implements Serializable {

        AjaxRequestTarget target;
    }

}
