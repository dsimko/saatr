
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.repo.build.BuildRepository;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
class BuildsPropertyFilterPanel extends GenericPanel<PropertyData> {

    @Inject
    private BuildRepository buildRepository;

    public BuildsPropertyFilterPanel(String id, IModel<PropertyData> model, List<String> variableNames, List<String> variableValues) {
        super(id, new CompoundPropertyModel<>(model));
        add(new DropDownChoice<>("name", variableNames).setNullValid(true).add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                variableValues.clear();
                buildRepository.findDistinctVariableValues(getModelObject().getName()).forEach(val -> variableValues.add(val));
            }

        }));
        AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setShowListOnEmptyInput(true);
        add(new AutoCompleteTextField<String>("value", settings) {

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
    }
}
