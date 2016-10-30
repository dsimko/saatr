
package org.jboss.qa.tool.saatr.web.page;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.jaxb.config.Config;
import org.jboss.qa.tool.saatr.repo.config.ConfigRepository;
import org.jboss.qa.tool.saatr.util.IOUtils;
import org.jboss.qa.tool.saatr.web.comp.DocumentModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapButton;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;
import org.jboss.qa.tool.saatr.web.comp.config.ConfigProvider;
import org.jboss.qa.tool.saatr.web.comp.config.ConfigProvider.ConfigFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Form page for basic configuration.
 * 
 * @author dsimko@redhat.com
 *
 */
@Slf4j
@SuppressWarnings("serial")
public class ConfigPage extends BasePage<ConfigDocument> {

    private IModel<ConfigFilter> filter = Model.of(new ConfigFilter());

    @Inject
    private ConfigRepository configRepository;

    public ConfigPage() {
        super(new DocumentModel<ConfigDocument>(ConfigDocument.class, null));
        setOutputMarkupId(true);
        List<IColumn<ConfigDocument, String>> columns = new ArrayList<IColumn<ConfigDocument, String>>();
        columns.add(new PropertyColumn<ConfigDocument, String>(new Model<String>("Name"), "name"));
        BootstrapTable<ConfigDocument, String> dataTable = new BootstrapTable<ConfigDocument, String>("table", columns, new ConfigProvider(filter), 10,
                getModel()) {

            @Override
            protected void onRowClicked(AjaxRequestTarget target, ConfigDocument config) {
                setModelObject(config);
                setResponsePage(getPage());
            }

        };
        add(dataTable);
        Form<Void> form = new StatelessForm<Void>("form");
        form.setMultiPart(true);
        final FileUploadField fileUploadField = new FileUploadField("file");
        form.add(fileUploadField.setRequired(true));
        form.add(new BootstrapFeedbackPanel("feedback"));
        form.add(new Button("submit") {

            @Override
            public void onSubmit() {
                try {
                    FileUpload upload = fileUploadField.getFileUpload();
                    if (upload != null) {
                        Config config;
                        try (InputStream inputStream = upload.getInputStream()) {
                            config = IOUtils.unmarshal(inputStream, Config.class);
                        }
                        ConfigDocument configData = ConfigDocument.create(config, upload.getClientFileName());
                        configRepository.save(configData);
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                    error(e.getMessage());
                }
            }
        });
        add(form);
        add(new Label("content", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return IOUtils.marshal(Config.create(getModelObject()), Config.class);
            }
        }) {

            @Override
            public boolean isVisible() {
                return getModelObject() != null;
            }
        });
        add(new BootstrapButton<ConfigDocument>("delete", getModel()) {

            @Override
            public boolean isEnabled() {
                return getModelObject() != null;
            }

            @Override
            public void onClick() {
                configRepository.delete(getModelObject());
            }
        });
    }
}
