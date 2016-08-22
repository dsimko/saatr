package org.jboss.qa.tool.saatr.web.comp.build.properties;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.entity.ConfigData;
import org.jboss.qa.tool.saatr.entity.ConfigData.ConfigProperty;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesEditPanel.AddInfoSubmitEvent;
import org.jboss.qa.tool.saatr.web.comp.build.properties.PropertiesEditPanel.ResetPanelEvent;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class PropertiesFormPanel extends GenericPanel<ConfigData> {

    public PropertiesFormPanel(String id, final IModel<ConfigData> model) {
        super(id, model);
        Form<Config> form = new StatelessForm<Config>("form");
        form.add(new BootstrapFeedbackPanel("feedback"));
        RepeatingView view = new RepeatingView("props");
        for (ConfigProperty prop : getModelObject().getProperties()) {
            view.add(new PropertyPanel(view.newChildId(), new CompoundPropertyModel<ConfigProperty>(prop)));
        }
        form.add(view);
        form.add(new AjaxSubmitLink("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                send(this, Broadcast.BUBBLE, new AddInfoSubmitEvent(getModelObject(), target));
            }
        });
        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                send(this, Broadcast.BUBBLE, new ResetPanelEvent(target));
            }
        });
        add(form);
    }
}