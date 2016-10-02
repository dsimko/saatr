
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.Arrays;

import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.web.comp.build.BuildExpansion;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsFilterPanel extends GenericPanel<BuildFilter> {

    public BuildsFilterPanel(String id, IModel<BuildFilter> model, final IModel<BuildDocument> pageModel) {
        super(id, model);
        Form<BuildFilter> form = new Form<BuildFilter>("form", new CompoundPropertyModel<BuildFilter>(model)) {

            @Override
            protected void onSubmit() {
                onFilterChanged(pageModel);
            }
        };
        form.add(new TextField<>("jobName"));
        form.add(new TextField<>("buildNumber"));
        form.add(new DropDownChoice<>("status", Arrays.asList(Status.values())).setNullValid(true));
        form.add(new DateTimeField("createdFrom"));
        form.add(new DateTimeField("createdTo"));
        form.add(new JobParamsFilterPanel("jobParams", model));
        form.add(new SystemParamsFilterPanel("systemParams", model));
        form.add(new CustomPropertiesFilterPanel("customProperties", model));
        form.add(new Link<Void>("clear") {

            @Override
            public void onClick() {
                BuildsFilterPanel.this.setModelObject(new BuildFilter());
                onFilterChanged(pageModel);
            }
        });
        add(form);
    }

    private void onFilterChanged(IModel<BuildDocument> pageModel) {
        pageModel.setObject(null);
        BuildExpansion.get().collapseAll();
    }
}
