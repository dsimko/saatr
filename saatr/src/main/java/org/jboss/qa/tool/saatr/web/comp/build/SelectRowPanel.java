
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.domain.DocumentWithID;
import org.jboss.qa.tool.saatr.web.comp.build.BuildsTablePanel.RefreshSelectedEvent;

@SuppressWarnings("serial")
public class SelectRowPanel<T extends DocumentWithID<ObjectId>> extends GenericPanel<T> {

    public SelectRowPanel(String id, IModel<T> model, final Set<ObjectId> selected) {
        super(id, model);
        add(new AjaxCheckBox("checkbox", new Model<Boolean>() {

            @Override
            public Boolean getObject() {
                return selected.contains(model.getObject().getId());
            }

            @Override
            public void setObject(Boolean object) {
                if (getObject()) {
                    selected.remove(model.getObject().getId());
                } else {
                    selected.add(model.getObject().getId());
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
