
package org.jboss.qa.tool.saatr.web.comp.build.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;
import org.jboss.qa.tool.saatr.web.comp.build.filter.AbsractPropertiesFilterPanel.AddPropertyEvent;
import org.jboss.qa.tool.saatr.web.comp.build.filter.AbsractPropertiesFilterPanel.RemovePropertyEvent;

/**
 * @author dsimko@redhat.com
 */
@SuppressWarnings("serial")
abstract class BuildsPropertyFilterPanel extends GenericPanel<PropertyData> {

    public BuildsPropertyFilterPanel(String id, String title, IModel<PropertyData> model, List<String> variableNames, List<String> variableValues, boolean buttonsVisible) {
        super(id, new CompoundPropertyModel<>(model));
        add(new Label("title", title));
        add(new DropDownChoice<>("name", variableNames).setNullValid(true).add(new OnChangeAjaxBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                variableValues.clear();
                findDistinctValues(getModelObject().getName()).forEach(val -> variableValues.add(val));
            }

        }));
        AutoCompleteSettings settings = new AutoCompleteSettings();
        settings.setShowListOnEmptyInput(true);
        add(new AutoCompleteTextField<String>("value", settings) {

            @Override
            protected Iterator<String> getChoices(String input) {
                List<String> choices = new ArrayList<>(10);
                for (final String option : variableValues) {
                    if (option != null && option.toLowerCase().startsWith(input.toLowerCase())) {
                        choices.add(option);
                        if (choices.size() == 10) {
                            break;
                        }
                    }
                }
                return choices.iterator();
            }
        });
        add(new AjaxLink<PropertyData>("add", model) {

            @Override
            public boolean isVisible() {
                return buttonsVisible;
            }
            
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(this, Broadcast.BUBBLE, new AddPropertyEvent(target));
            }
        });
        add(new AjaxLink<PropertyData>("remove", model) {

            @Override
            public boolean isVisible() {
                return buttonsVisible;
            }
            
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(this, Broadcast.BUBBLE, new RemovePropertyEvent(target));
            }
        });

    }
    
    protected abstract Iterable<String> findDistinctValues(String name);

}
