package org.jboss.qa.tool.saatr.web.page;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
import org.jboss.qa.tool.saatr.entity.ConfigData;
import org.jboss.qa.tool.saatr.entity.jaxb.config.Config;
import org.jboss.qa.tool.saatr.service.ConfigService;
import org.jboss.qa.tool.saatr.web.comp.EntityModel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapFeedbackPanel;
import org.jboss.qa.tool.saatr.web.comp.bootstrap.BootstrapTable;
import org.jboss.qa.tool.saatr.web.comp.config.ConfigProvider;
import org.jboss.qa.tool.saatr.web.comp.config.ConfigProvider.ConfigFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Form page for basic configuration.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class ConfigPage extends BasePage<ConfigData> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigPage.class);

    private IModel<ConfigFilter> filter = Model.of(new ConfigFilter());

    @Inject
    private ConfigService configService;

    public ConfigPage() {
        super(new EntityModel<ConfigData>(ConfigData.class, null));
        setOutputMarkupId(true);
        List<IColumn<ConfigData, String>> columns = new ArrayList<IColumn<ConfigData, String>>();
        columns.add(new PropertyColumn<ConfigData, String>(new Model<String>("Name"), "name"));
        BootstrapTable<ConfigData, String> dataTable = new BootstrapTable<ConfigData, String>("table", columns, new ConfigProvider(filter),
                10, getModel()) {

            @Override
            protected void selectRow(ConfigData configData) {
                setModelObject(configData);
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
                            config = configService.unmarshal(inputStream);
                        }
                        ConfigData configData = ConfigData.create(config, upload.getClientFileName());
                        configService.save(configData);
                    }
                } catch (Exception e) {
                    LOG.warn(e.getMessage(), e);
                    error(e.getMessage());
                }
            }
        });
        add(form);
        add(new Label("content", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return configService.marshal(Config.create(getModelObject()));
            }
        }) {
            @Override
            public boolean isVisible() {
                return getModelObject() != null;
            }
        });
    }
}
