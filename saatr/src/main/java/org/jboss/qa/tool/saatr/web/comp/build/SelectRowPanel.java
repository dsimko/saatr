
package org.jboss.qa.tool.saatr.web.comp.build;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.build.Build;
import org.jboss.qa.tool.saatr.web.comp.build.BuildsTablePanel.RefreshSelectedEvent;

@SuppressWarnings("serial")
public class SelectRowPanel extends GenericPanel<Build> {

    public SelectRowPanel(String id, IModel<Build> model) {
        super(id, model);
        add(new AjaxCheckBox("checkbox", new Model<Boolean>() {

            @Override
            public Boolean getObject() {
                return BuildSelection.get().getIds().contains(model.getObject().getId());
            }

            @Override
            public void setObject(Boolean object) {
                if (getObject()) {
                    BuildSelection.get().getIds().remove(model.getObject().getId());
                } else {
                    BuildSelection.get().getIds().add(model.getObject().getId());
                }
            }
        }) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, new RefreshSelectedEvent(target));
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                attributes.setEventPropagation(EventPropagation.STOP);
            }
        });
    }
}
