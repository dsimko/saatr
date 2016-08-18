package org.jboss.qa.tool.saatr.web.comp.build.properties;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.file.Folder;
import org.jboss.qa.tool.saatr.entity.PersistableWithProperties;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.jboss.qa.tool.saatr.service.ConfigService;
import org.jboss.qa.tool.saatr.web.WicketApplication;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;
import org.jboss.qa.tool.saatr.web.page.ConfigPage.XmlFileFilter;

/**
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
class SelectConfigPanel<T extends PersistableWithProperties> extends GenericPanel<T> {

    @Inject
    private ConfigService configService;

    private Panel propertiesFormPanel;
    private boolean dropDownChoiceVisible = true;

    public SelectConfigPanel(String id, final IModel<T> model) {
        super(id, model);
        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.add(new BootstrapFeedbackPanel("feedback"));
        wmc.setOutputMarkupId(true);
        final IModel<File> configModel = new Model<>();
        wmc.add(new DropDownChoice<File>("config", configModel, new AbstractReadOnlyModel<List<File>>() {
            @Override
            public List<File> getObject() {
                return Arrays.asList((new Folder(WicketApplication.get().getConfigFolderPath())).getFiles(XmlFileFilter.INSTANCE));
            }
        }) {
            @Override
            public boolean isVisible() {
                return dropDownChoiceVisible;
            }
        }.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                try {
                    Config config = configService.unmarshal(configModel.getObject());
                    configService.prefillValues(config, getModelObject());
                    propertiesFormPanel.replaceWith(new PropertiesFormPanel(propertiesFormPanel.getId(), Model.of(config)));
                    dropDownChoiceVisible = false;
                } catch (JAXBException e) {
                    error(e.getMessage());
                }
                target.add(wmc);
            }
        }));
        wmc.add(propertiesFormPanel = new EmptyPanel("propertiesFormPanel"));
        add(wmc);
    }

}