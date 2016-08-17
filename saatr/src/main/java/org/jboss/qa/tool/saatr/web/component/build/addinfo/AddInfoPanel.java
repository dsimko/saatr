package org.jboss.qa.tool.saatr.web.component.build.addinfo;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.WithProperties;

import lombok.Data;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class AddInfoPanel<T extends WithProperties> extends GenericPanel<T> {

    private Panel formPanel;
    private final WebMarkupContainer wmc;
    private boolean addInfoButtonVisible = true;

    public AddInfoPanel(String id, final IModel<T> model) {
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
                replaceFormPanel(new AddInfoFormPanel<>(formPanel.getId(), model));
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
        if (payload instanceof ResetPanelEvent) {
            ResetPanelEvent eventPayload = (ResetPanelEvent) payload;
            replaceFormPanel(new EmptyPanel(formPanel.getId()));
            addInfoButtonVisible = true;
            if (eventPayload.getTarget() != null) {
                eventPayload.getTarget().add(wmc);
            }
        }
    }

    private void replaceFormPanel(Panel with) {
        formPanel.replaceWith(with);
        formPanel = with;
    }

    @Data
    static class ResetPanelEvent implements Serializable {

        private final AjaxRequestTarget target;
    }

}