
package org.jboss.qa.tool.saatr.web.comp.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.Status;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
@Slf4j
public class BuildsFilterPanel extends GenericPanel<BuildFilter> {

    private List<String> variableNames = new ArrayList<>();

    private List<String> variableValues = new ArrayList<>();

    @Inject
    private BuildRepository buildRepository;

    public BuildsFilterPanel(String id, IModel<BuildFilter> model) {
        super(id, model);
        initVariables();
        Form<BuildFilter> form = new Form<>("form", new CompoundPropertyModel<BuildFilter>(model));
        form.add(new TextField<>("jobName"));
        form.add(new TextField<>("buildNumber"));
        form.add(new DropDownChoice<>("status", Arrays.asList(Status.values())).setNullValid(true));
        form.add(new DropDownChoice<>("variableName", variableNames).setNullValid(true));
        form.add(new DateTimeField("createdFrom"));
        form.add(new DateTimeField("createdTo"));
        AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setShowListOnEmptyInput(true);
        form.add(new AutoCompleteTextField<String>("variableValue", settings) {

            @Override
            protected Iterator<String> getChoices(String input) {
                List<String> choices = new ArrayList<>(10);
                for (final String option : variableValues) {
                    if (option.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(option);
                        if (choices.size() == 10) {
                            break;
                        }
                    }
                }
                return choices.iterator();
            }
        });

        form.add(new Link<Void>("clear") {

            @Override
            public void onClick() {
                BuildsFilterPanel.this.setModelObject(new BuildFilter());
            }
        });
        add(form);
    }

    private void initVariables() {
        long start = System.currentTimeMillis();
        buildRepository.findDistinctVariableNames().forEach(name -> variableNames.add(name));
        buildRepository.findDistinctVariableValues().forEach(val -> variableValues.add(val));
        log.debug("Loading variables filter took {} ms.", System.currentTimeMillis() - start);
    }
}
