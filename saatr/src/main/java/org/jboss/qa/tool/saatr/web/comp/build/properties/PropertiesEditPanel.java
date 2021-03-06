
package org.jboss.qa.tool.saatr.web.comp.build.properties;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.build.BuildProperty;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesPanel.RefreshPropertiesPanelEvent;

import lombok.Data;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class PropertiesEditPanel<T extends DocumentWithProperties<?>> extends GenericPanel<T> {

    @Inject
    private BuildRepository buildRepository;

    private Panel formPanel;

    private final WebMarkupContainer wmc;

    private boolean addInfoButtonVisible = true;

    public PropertiesEditPanel(String id, final IModel<T> model) {
        super(id, model);
        wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        wmc.add(new AjaxLink<T>("addInfo") {

            @Override
            public boolean isVisible() {
                return addInfoButtonVisible;
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                replaceFormPanel(new SelectConfigPanel<>(formPanel.getId(), model));
                addInfoButtonVisible = false;
                target.add(wmc);
            }
        });
        wmc.add(formPanel = new EmptyPanel("formPanel"));
        add(wmc);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getModelObject() != null);
    }

    @Override
    public void onEvent(IEvent<?> event) {
        Object payload = event.getPayload();
        if (payload instanceof AddInfoSubmitEvent) {
            AddInfoSubmitEvent eventPayload = (AddInfoSubmitEvent) payload;
            Set<BuildProperty> properties = eventPayload.getConfig().getProperties().stream().map(c -> new BuildProperty(c.getName(), c.getValue())).collect(
                    Collectors.toSet());
            buildRepository.addOrUpdateProperties(getModelObject(), properties);
            resetPanel();
            send(this, Broadcast.BUBBLE, new RefreshPropertiesPanelEvent(eventPayload.getTarget()));
        } else if (payload instanceof ResetPanelEvent) {
            ResetPanelEvent eventPayload = (ResetPanelEvent) payload;
            resetPanel();
            eventPayload.getTarget().add(wmc);
        }
    }

    private void resetPanel() {
        replaceFormPanel(new EmptyPanel(formPanel.getId()));
        addInfoButtonVisible = true;
    }

    private void replaceFormPanel(Panel with) {
        formPanel.replaceWith(with);
        formPanel = with;
    }

    @Data
    static class AddInfoSubmitEvent implements Serializable {

        private final ConfigDocument config;

        private final AjaxRequestTarget target;
    }

    @Data
    static class ResetPanelEvent implements Serializable {

        private final AjaxRequestTarget target;
    }

}