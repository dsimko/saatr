package org.jboss.qa.tool.saatr.web.page;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.UrlTextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.file.Folder.FileFilter;
import org.apache.wicket.util.string.Strings;
import org.jboss.qa.tool.saatr.domain.Document;
import org.jboss.qa.tool.saatr.util.DocumentUtils;
import org.jboss.qa.tool.saatr.web.WicketApplication;
import org.jboss.qa.tool.saatr.web.component.BootstrapFeedbackPanel;
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
    private File config;
    private URL jenkinsBuild;

    public ConfigPage() {
        setOutputMarkupId(true);
        Form<Void> form = new StatelessForm<Void>("form");
        form.add(new BootstrapFeedbackPanel("feedback"));
        form.add(new TextField<>("path", new PropertyModel<>(this, "application.configFolderPath")));
        form.add(new UrlTextField("build", new PropertyModel<>(this, "jenkinsBuild")).setRequired(true));
        form.add(new DropDownChoice<>("config", new PropertyModel<>(this, "config"), new AbstractReadOnlyModel<List<File>>() {
            @Override
            public List<File> getObject() {
                WicketApplication application = WicketApplication.get();
                if (!Strings.isEmpty(application.getConfigFolderPath())) {
                    Folder folder = new Folder(application.getConfigFolderPath());
                    if (folder.exists()) {
                        return Arrays.asList(folder.getFiles(new FileFilter() {

                            @Override
                            public boolean accept(File file) {
                                return "xml".equals(file.getExtension());
                            }
                        }));
                    }
                }
                return Collections.emptyList();
            }
        }).setRequired(true));
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
                        Document document = DocumentUtils.unmarshal(config);
                        DocumentUtils.populateFromJenkinsMiner(document, jenkinsBuild);
                        setResponsePage(new DocumentPage(document));
                    } catch (Exception e) {
                        LOG.warn(e.getMessage(), e);
                        error(e.getMessage());
                    }
                }
            }
        });
        add(form);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        get("form:path").setEnabled(changeEnabled);
        get("form:build").setEnabled(!changeEnabled);
        get("form:config").setEnabled(!changeEnabled);
    }
}
