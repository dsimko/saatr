
package org.jboss.qa.tool.saatr.web.comp.build.properties;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;

import lombok.Data;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class PropertiesPanel<T extends DocumentWithProperties<?>> extends GenericPanel<T> {

    private WebMarkupContainer panel;

    public PropertiesPanel(String id, final IModel<T> model) {
        super(id, model);
        panel = new WebMarkupContainer("panel") {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                if (!getModelObject().getProperties().isEmpty()) {
                    tag.append("class", "panel panel-info panel-properties", " ");
                }
            }
        };
        panel.setOutputMarkupId(true);
        WebMarkupContainer panelBody = new WebMarkupContainer("panel-body") {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                if (!getModelObject().getProperties().isEmpty()) {
                    tag.append("class", "panel-body", " ");
                }
            }
        };
        panelBody.add(new PropertiesViewPanel<>("view", model));
        panelBody.add(new PropertiesEditPanel<>("edit", model));
        add(panel.add(panelBody));
    }

    @Override
    public void onEvent(IEvent<?> event) {
        Object payload = event.getPayload();
        if (payload instanceof RefreshPropertiesPanelEvent) {
            RefreshPropertiesPanelEvent eventPayload = (RefreshPropertiesPanelEvent) payload;
            eventPayload.getTarget().add(panel);
        }
    }

    @Data
    static class RefreshPropertiesPanelEvent implements Serializable {

        private final AjaxRequestTarget target;
    }

}