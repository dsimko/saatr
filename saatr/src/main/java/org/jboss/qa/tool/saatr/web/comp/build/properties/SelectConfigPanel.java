package org.jboss.qa.tool.saatr.web.comp.build.properties;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.repo.config.ConfigRepository;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class SelectConfigPanel<T extends DocumentWithProperties<?>> extends GenericPanel<T> {

    @Inject
    private ConfigRepository configRepository;

    private Panel propertiesFormPanel;
    private boolean dropDownChoiceVisible = true;

    public SelectConfigPanel(String id, final IModel<T> model) {
        super(id, model);
        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.add(new BootstrapFeedbackPanel("feedback"));
        wmc.setOutputMarkupId(true);
        final IModel<ConfigDocument> configModel = new Model<>();
        wmc.add(new DropDownChoice<ConfigDocument>("config", configModel, configRepository.findAll(), new ChoiceRenderer<>("name")) {
            @Override
            public boolean isVisible() {
                return dropDownChoiceVisible;
            }
        }.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                configRepository.prefillValues(configModel.getObject(), getModelObject());
                propertiesFormPanel.replaceWith(new PropertiesFormPanel(propertiesFormPanel.getId(), configModel));
                dropDownChoiceVisible = false;
                target.add(wmc);
            }
        }));
        wmc.add(propertiesFormPanel = new EmptyPanel("propertiesFormPanel"));
        add(wmc);
    }

}