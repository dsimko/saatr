
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.domain.build.BuildFilter;
import org.jboss.qa.tool.saatr.repo.build.BuildFilterRepository;
import org.jboss.qa.tool.saatr.web.comp.build.BuildExpansion;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
public class BuildsFilterPanel extends GenericPanel<BuildFilter> {

    private static final String FILTER_PARAM_NAME = "filter";

    @SpringBean
    private BuildFilterRepository buildFilterRepository;

    public BuildsFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
        Form<BuildFilter> form = new Form<BuildFilter>("form", new CompoundPropertyModel<BuildFilter>(model)) {

            @Override
            protected void onSubmit() {
                PageParameters params = getPage().getPageParameters();
                BuildFilter buildFilter = buildFilterRepository.save(getModelObject());
                params.set(FILTER_PARAM_NAME, buildFilter.getId());
                setResponsePage(getPage().getClass(), params);
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
                BuildExpansion.get().collapseAll();
                setResponsePage(getPage().getClass());
            }
        });
        add(form);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        String filterId = getPage().getPageParameters().get(FILTER_PARAM_NAME).toString(null);
        if (filterId != null) {
            BuildFilter buildFilter = buildFilterRepository.findAndUpdateLastUsed(filterId);
            if (buildFilter != null) {
                setModelObject(buildFilter);
            }
        }
    }
}
