package org.jboss.qa.tool.saatr.web.page;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.file.Folder.FileFilter;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.jboss.qa.tool.saatr.web.WicketApplication;
import org.jboss.qa.tool.saatr.web.component.common.bootstrap.BootstrapFeedbackPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Form page for basic configuration.
 * 
 * @author dsimko@redhat.com
 *
 */
@SuppressWarnings("serial")
public class ConfigPage extends BasePage<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigPage.class);

    private boolean changeEnabled;
    private File selectedFile;

    public ConfigPage() {
        setOutputMarkupId(true);
        Form<Void> form = new StatelessForm<Void>("form");
        form.setMultiPart(true);
        final FileUploadField fileUploadField = new FileUploadField("file");
        form.add(fileUploadField);
        form.add(new BootstrapFeedbackPanel("feedback"));
        form.add(new TextField<>("path", new PropertyModel<>(this, "application.configFolderPath")).setRequired(true)
                .add(new IValidator<Object>() {
                    @Override
                    public void validate(IValidatable<Object> validatable) {
                        String path = validatable.getValue().toString();
                        if (!(new Folder(path)).exists()) {
                            validatable.error(new ValidationError("Directory doesn't exists."));
                        }
                    }
                }));
        form.add(new AjaxLink<Void>("change") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                changeEnabled = true;
                target.add(getPage());
            }
        });
        form.add(new Button("submit") {
            @Override
            public void onSubmit() {
                if (changeEnabled) {
                    changeEnabled = false;
                } else {
                    try {
                        FileUpload upload = fileUploadField.getFileUpload();
                        if (upload != null) {
                            upload.writeTo(new java.io.File(getConfigFolderPath() + File.separator + upload.getClientFileName()));
                        }
                    } catch (Exception e) {
                        LOG.warn(e.getMessage(), e);
                        error(e.getMessage());
                    }
                }
            }
        });
        add(form);
        add(new RefreshingView<File>("files") {
            @Override
            protected Iterator<IModel<File>> getItemModels() {
                List<IModel<File>> models = new ArrayList<>();
                for (File file : (new Folder(getConfigFolderPath())).getFiles(XmlFileFilter.INSTANCE)) {
                    models.add(Model.of(file));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<File> item) {
                Link<File> link = new Link<File>("select", item.getModel()) {

                    @Override
                    public void onClick() {
                        selectedFile = getModelObject();
                    }
                };
                link.add(new Label("name", item.getModelObject().getName()));
                item.add(link);
                item.add(new Link<File>("delete", item.getModel()) {
                    @Override
                    public void onClick() {
                        selectedFile = null;
                        getModelObject().delete();
                    }
                });
            }
        });
        add(new Label("content", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                try {
                    if (selectedFile != null) {
                        return new String(Files.readAllBytes(Paths.get(selectedFile.toURI())));
                    }
                    return null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        get("form:path").setEnabled(changeEnabled);
        get("form:file").setEnabled(!changeEnabled);
        get("content").setVisible(selectedFile != null);
    }

    private String getConfigFolderPath() {
        return ((WicketApplication) getApplication()).getConfigFolderPath();
    }

    public static class XmlFileFilter implements FileFilter {

        public static FileFilter INSTANCE = new XmlFileFilter();

        private XmlFileFilter() {
        }

        @Override
        public boolean accept(File file) {
            return "xml".equals(file.getExtension());
        }

    }
}
