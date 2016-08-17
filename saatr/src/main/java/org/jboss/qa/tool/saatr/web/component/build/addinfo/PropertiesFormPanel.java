package org.jboss.qa.tool.saatr.web.component.build.addinfo;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.WithProperties;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config.Property;
import org.jboss.qa.tool.saatr.web.component.build.BuildDetailPanel.AddInfoSubmitEvent;
import org.jboss.qa.tool.saatr.web.component.build.addinfo.AddInfoPanel.ResetPanelEvent;
import org.jboss.qa.tool.saatr.web.component.common.bootstrap.BootstrapFeedbackPanel;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class PropertiesFormPanel extends GenericPanel<Config> {

    public PropertiesFormPanel(String id, final IModel<Config> model, final IModel<? extends WithProperties> model2) {
        super(id, model);
        Form<Config> form = new StatelessForm<Config>("form");
        form.add(new BootstrapFeedbackPanel("feedback"));
        RepeatingView view = new RepeatingView("props");
        for (Property prop : getModelObject().getProperties()) {
            view.add(new PropertyPanel(view.newChildId(), new CompoundPropertyModel<Property>(prop)));
        }
        form.add(view);
        form.add(new SubmitLink("submit") {

            @Override
            public void onSubmit() {
                send(getPage(), Broadcast.BREADTH, new AddInfoSubmitEvent(getModelObject(), model2.getObject()));
                send(getPage(), Broadcast.BREADTH, new ResetPanelEvent(null));
            }
        });
        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, new ResetPanelEvent(target));
            }
        });
        add(form);
    }
}