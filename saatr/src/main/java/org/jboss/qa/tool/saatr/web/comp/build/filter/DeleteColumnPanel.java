
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildFilterRepository;
import org.jboss.qa.tool.saatr.web.comp.build.filter.BuildsFilterPanel.RefreshTableEvent;

@SuppressWarnings("serial")
public class DeleteColumnPanel extends GenericPanel<BuildFilter> {

    @SpringBean
    private BuildFilterRepository repository;
    
    public DeleteColumnPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
        add(new AjaxLink<BuildFilter>("delete", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                repository.delete(getModelObject());
                send(this, Broadcast.BUBBLE, new RefreshTableEvent(target));
            }
            
            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.setEventPropagation(EventPropagation.STOP);
            }

        });
    }

}
